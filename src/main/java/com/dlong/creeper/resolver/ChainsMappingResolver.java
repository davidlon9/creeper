package com.dlong.creeper.resolver;

import com.dlong.creeper.annotation.RequestLog;
import com.dlong.creeper.annotation.ResponseLog;
import com.dlong.creeper.annotation.seq.ChainReference;
import com.dlong.creeper.annotation.seq.RequestReference;
import com.dlong.creeper.exception.ResolveException;
import com.dlong.creeper.exception.RuntimeResolveException;
import com.dlong.creeper.model.log.RequestLogInfo;
import com.dlong.creeper.model.log.ResponseLogInfo;
import com.dlong.creeper.model.seq.*;
import com.dlong.creeper.resolver.base.SequentialResolver;
import com.dlong.creeper.resolver.util.LogNames;
import com.dlong.creeper.resolver.util.ResolveUtil;
import com.dlong.creeper.util.ClassUtil;
import org.apache.log4j.Logger;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ChainsMappingResolver {
    private Class<?> rootClass;

    private Map<String,RequestChainEntity> allRequestChainNameMap = new HashMap<>();

    private Map<AnnotatedElement, RequestInfo> rootMethodInfoMap;

    private boolean fixIndex = true;

    private Logger logger=Logger.getLogger(ChainsMappingResolver.class);

    public ChainsMappingResolver(Class<?> rootClass) {
        this(rootClass,true);
    }

    public ChainsMappingResolver(Class<?> rootClass,boolean fixIndex) {
        this.rootClass = rootClass;
        this.fixIndex = fixIndex;
        this.rootMethodInfoMap = new DefaultRequestInfoResolver().resolve(rootClass);
    }

    public RequestChainEntity resolve() {
        RequestChainEntity requestChainEntity = null;
        try {
            requestChainEntity = resolveChainSequentials(rootClass);
            setChainRecorderToRequest(requestChainEntity);
            SequentialResolver.assignSequenceIds(requestChainEntity);
            logger.info("resolve Chain "+ LogNames.chain(requestChainEntity)+" successfully!\n");

        } catch (ResolveException e) {
            e.printStackTrace();
            System.out.println("resolve Chain "+ LogNames.chain(requestChainEntity)+" failed!");
        }
        return requestChainEntity;
    }

    private RequestChainEntity resolveChainSequentials(Class clz) throws ResolveException {
        logger.info("Start resolving Chain class "+ LogNames.chain(clz));
        ChainResolver chainResolver = ResolverFactory.getChainResolver(clz);

        if(chainResolver == null){
            throw new ResolveException("chain class "+ LogNames.chain(clz)+" must annotate with RequestChain type annotation");
        }

        RequestChainEntity requestChain = chainResolver.resolveChain();

        List<SequentialEntity> chainSequentialList =new ArrayList<>();
        requestChain.setSequentialList(chainSequentialList);

        //TODO request字段的parentChain未设置
        //解析chain中的所有Request请求
        List<RequestEntity> requests = resolveRequestsForChain(requestChain);
        chainSequentialList.addAll(requests);
        //解析chain中的所有SubClass类型的请求Chain
        List<RequestChainEntity> subClassChains = resolveSubClassForChain(requestChain);
        chainSequentialList.addAll(subClassChains);
        for (RequestChainEntity subClassChain : subClassChains) {
            logger.info("add sub class Chain "+ LogNames.chain(subClassChain)+" to "+ LogNames.chain(requestChain));
        }
        //解析chain中的所有fieldRef类型的Seq
        List<SequentialEntity> fieldRefSeqs = resolveFieldRefsForChain(requestChain);
        chainSequentialList.addAll(fieldRefSeqs);

        //SequentialList按index排序
        fixAndSortSequentialIndex(chainSequentialList);

        if(allRequestChainNameMap.containsKey(requestChain.getName())){
            throw new ResolveException("found name duplicated RequestChain "+ LogNames.chain(requestChain));
        }
        allRequestChainNameMap.put(requestChain.getName(), requestChain);
        logger.info("Finish resolve Chain class "+ LogNames.chain(clz));
        return requestChain;
    }

    private void fixAndSortSequentialIndex(List<SequentialEntity> chainSequentialList) throws ResolveException {
        chainSequentialList.sort(SequentialEntity.INDEX_COMPARATOR);
        Set<Integer> idxs=new HashSet<>();
        int offset = 0;
        SequentialEntity prev=null;
        for (SequentialEntity sequentialEntity : chainSequentialList) {
            int index = sequentialEntity.getIndex() + offset;
            if(idxs.contains(index)){
                offset++;
                index = sequentialEntity.getIndex() + offset;
            }
            idxs.add(index);
            if(offset>0){
                if(fixIndex){
                    sequentialEntity.setIndex(index);
                    logger.info("Sequential "+sequentialEntity.getName()+" index "+sequentialEntity.getIndex()+" duplicated with "+prev.getName()+", greater than "+index+" index sequentials and "+sequentialEntity.getName()+" indexes will be add 1 offset");
                }else{
                    throw new ResolveException("Sequential "+sequentialEntity.getName()+" index "+sequentialEntity.getIndex()+" duplicated with "+prev.getName());
                }
            }
            prev=sequentialEntity;
        }
    }

    private List<RequestChainEntity> resolveSubClassForChain(RequestChainEntity requestChain) throws ResolveException {
        List<RequestChainEntity> chainSequentialList = new ArrayList<>();
        Class[] childClasses = requestChain.getChainClass().getDeclaredClasses();
        if(childClasses.length>0){
            for (Class child : childClasses) {
                RequestChainEntity childRequestChain = resolveChainSequentials(child);
                if(childRequestChain !=null){
                    childRequestChain.setParent(requestChain);
                    chainSequentialList.add(childRequestChain);
                }
            }
        }
        return chainSequentialList;
    }

    private List<SequentialEntity> resolveFieldRefsForChain(RequestChainEntity requestChain) throws ResolveException {
        List<SequentialEntity> sequentialList = new ArrayList<>();
        Class clz = requestChain.getChainClass();
        Field[] fields = clz.getDeclaredFields();
        if(fields.length>0){
            for (Field field : fields) {
                Class<?> type = field.getType();
                if(type.equals(clz)) throw new ResolveException("recursive reference chain "+type);
                if(field.isAnnotationPresent(ChainReference.class)){
                    sequentialList.add(resolveChainRefField(field, requestChain));
                }else if(field.isAnnotationPresent(RequestReference.class)){
                    sequentialList.add(resolveRequestRefField(field,requestChain));
                }
            }
        }
        return sequentialList;
    }

    private RequestEntity resolveRequestRefField(Field field,RequestChainEntity refParent) throws ResolveException {
        Class<?> type = field.getType();
        if(!type.equals(Method.class)) throw new ResolveException("RequestReference field "+field.getName()+" must annotate on entityTarget type");
        RequestReference requestReference = field.getAnnotation(RequestReference.class);
        Class chainClass = requestReference.chainClass();
        if(!ResolveUtil.isChainClass(chainClass)) throw new ResolveException("RequestReference field chainClass  "+chainClass+" must annotate with RequestChain type annotation");
        String requestMethodName = requestReference.requestMethod();
        if ("".equals(requestMethodName)) requestMethodName = field.getName();
        Method chainMethod = null;
        Method[] declaredMethods = chainClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if(method.getName().equals(requestMethodName)){
                chainMethod = method;
            }
        }
        if(chainMethod == null) throw new ResolveException("no such request entityTarget in chainClass "+chainClass);
        Map<AnnotatedElement, RequestInfo> methodInfoMap;
        //如果Request的Chain类是子类，且最外层父类是当前RootClass则使用RootClass解析的RequestInfoMap
        if(ClassUtil.getOutterClass(chainClass).equals(rootClass)){
            methodInfoMap = rootMethodInfoMap;
        }else{
            //否则使用Request的ChainClass
            methodInfoMap = new DefaultRequestInfoResolver().resolve(chainClass);
        }
        RequestInfo requestInfo = methodInfoMap.get(chainMethod);
        Assert.notNull(requestInfo);

        RequestEntity requestEntity = resolveRequest(refParent,chainMethod);

        requestEntity.setIndex(requestReference.index());
        String description = requestReference.description();
        if("".equals(description)) requestEntity.setDescription(description);
        setLogInfo(requestEntity);

        ChainResolver chainResolver = ResolverFactory.getChainResolver(chainClass);
        RequestChainEntity parent = chainResolver.resolveChain();
        requestEntity.setParent(parent);
        parent.setSequentialList(Collections.singletonList(requestEntity));

        requestEntity.setRefParent(refParent);
        return requestEntity;
    }

    private RequestChainEntity resolveChainRefField(Field field, RequestChainEntity parentChain) throws ResolveException {
        Class<?> type = field.getType();
        if (!ResolveUtil.isChainClass(type)) throw new ResolveException("reference chain "+type+" must annotate with RequestChain type annotation");
        RequestChainEntity childRequestChain = new ChainsMappingResolver(type).resolve();
        ChainReference chainReference = field.getAnnotation(ChainReference.class);
        childRequestChain.setIndex(chainReference.index());
        String name = chainReference.name();
        String description = chainReference.description();
        if(!"".equals(name)) childRequestChain.setName(name);
        if(!"".equals(description)) childRequestChain.setDescription(description);
        childRequestChain.setParent(parentChain);
        childRequestChain.setRefParent(parentChain);
        return childRequestChain;
    }

    private void setChainRecorderToRequest(RequestChainEntity requestChain) {
        List<SequentialEntity> sequentialList = requestChain.getSequentialList();
        for (SequentialEntity sequentialEntity : sequentialList) {
            if(sequentialEntity instanceof LoopableEntity){
                LoopableEntity loopableEntity= (LoopableEntity) sequentialEntity;
                if(requestChain.getRecorder()!=null && loopableEntity.getRecorder() == null){
                    loopableEntity.setRecorder(requestChain.getRecorder());
                }
                if (loopableEntity instanceof RequestChainEntity) {
                    setChainRecorderToRequest((RequestChainEntity)sequentialEntity);
                }
            }
        }
    }

    private List<RequestEntity> resolveRequests(RequestChainEntity chainEntity) {
        Class chainClass = chainEntity.getChainClass();
        //解析method类型的RequestEntity
        ReflectionUtils.doWithMethods(chainClass, method -> {
            resolveRequest(chainEntity, method);
        }, ResolveUtil::isRequestMethod);

        //解析field类型的RequestEntity
        ReflectionUtils.doWithFields(chainClass, field -> {
            resolveRequest(chainEntity, field);
        }, ResolveUtil::isRequestField);

        Map<String, RequestEntity> requestNameMap = chainEntity.getRequestNameMap();
        List<RequestEntity> requests = new ArrayList<>(requestNameMap.values());
        requests.sort((o1, o2) -> {
            if (o1.getIndex()==o2.getIndex()) {
                return 0;
            }
            return o1.getIndex()>o2.getIndex()?1:-1;
        });
        return requests;
    }

    private RequestEntity resolveRequest(RequestChainEntity chainEntity, AnnotatedElement target) {
        Class chainClass = chainEntity.getChainClass();
        RequestInfo requestInfo = rootMethodInfoMap.get(target);
        Assert.notNull(requestInfo);
        RequestResolver requestResolver = ResolverFactory.getRequestResolver(chainClass, target);
        if(requestResolver==null){
            throw new RuntimeResolveException("not supported seq annotation type");
        }
        RequestEntity requestEntity = requestResolver.resolve(requestInfo);
        logger.info("add Request "+ LogNames.request(requestEntity) +" to Chain "+ LogNames.chain(chainEntity)+"\n");
        setLogInfo(requestEntity);

        Map<String, RequestEntity> requestNameMap = chainEntity.getRequestNameMap();
        if (requestNameMap.containsKey(requestEntity.getName())) {
            throw new RuntimeResolveException("found name duplicated RequestEntity "+ LogNames.request(requestEntity));
        }
        requestNameMap.put(requestEntity.getName(),requestEntity);
        return requestEntity;
    }

    private List<RequestEntity> resolveRequestsForChain(RequestChainEntity requestChain) {
        Class clz = requestChain.getChainClass();
        //解析请求
        List<RequestEntity> requests = resolveRequests(requestChain);

        if(requests.size()==0){
            logger.warn("does't find any SeqRequest for "+clz);
        }
        //为请求设置Parent
        for (RequestEntity request : requests) {
            request.setParent(requestChain);
        }
        return requests;
    }

    public Class<?> getRootClass() {
        return rootClass;
    }

    private void setLogInfo(RequestEntity request) {
        RequestLogInfo requestLogInfo=new RequestLogInfo();
        ResponseLogInfo responseLogInfo=new ResponseLogInfo();

        AnnotatedElement target = request.getEntityElement();
        RequestLog requestLog = AnnotationUtils.getAnnotation(target, RequestLog.class);
        ResponseLog responseLog = AnnotationUtils.getAnnotation(target, ResponseLog.class);

        if(requestLog!=null){
            setRequestLogInfo(requestLogInfo, requestLog);
        }else{
            requestLog=findAnnoFromParent(request,RequestLog.class);
            if(requestLog!=null){
                setRequestLogInfo(requestLogInfo, requestLog);
                request.getParent().setRequestLogInfo(requestLogInfo);
            }
        }

        if(responseLog!=null){
            setResponseLogInfo(responseLogInfo, responseLog);
        }else{
            responseLog=findAnnoFromParent(request,ResponseLog.class);
            if(responseLog!=null){
                setResponseLogInfo(responseLogInfo, responseLog);
                request.getParent().setResponseLogInfo(responseLogInfo);
            }
        }

        request.setRequestLogInfo(requestLogInfo);
        request.setResponseLogInfo(responseLogInfo);
    }

    private void setResponseLogInfo(ResponseLogInfo responseLogInfo, ResponseLog responseLog) {
        responseLogInfo.setShowResult(responseLog.showResult());
        responseLogInfo.setShowSetCookies(responseLog.showSetCookies());
        responseLogInfo.setShowStatus(responseLog.showStatus());
    }

    private void setRequestLogInfo(RequestLogInfo requestLogInfo, RequestLog requestLog) {
        requestLogInfo.setShowUrl(requestLog.showUrl());
        requestLogInfo.setShowFilledHeaders(requestLog.showFilledHeaders());
        requestLogInfo.setShowFilledParams(requestLog.showFilledParams());
    }

    private  <A extends Annotation> A findAnnoFromParent(SequentialEntity request,Class<A> annotationType) {
        RequestChainEntity parent = request.getParent();
        if(parent!=null){
            Class chainClass = parent.getChainClass();
            A annotation = AnnotationUtils.findAnnotation(chainClass, annotationType);
            if(annotation==null){
                return findAnnoFromParent(parent,annotationType);
            }else{
                return annotation;
            }
        }
        return null;
    }

    public void setFixIndex(boolean fixIndex) {
        this.fixIndex = fixIndex;
    }
}