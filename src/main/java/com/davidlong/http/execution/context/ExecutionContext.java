package com.davidlong.http.execution.context;

import com.davidlong.http.exception.RuntimeExecuteException;
import com.davidlong.http.expression.ContextExpressionParser;
import com.davidlong.http.model.Param;
import com.davidlong.http.model.seq.RequestChainEntity;
import com.davidlong.http.model.seq.RequestEntity;
import com.davidlong.http.model.seq.SequentialEntity;
import com.davidlong.http.util.SSLUtil;
import org.apache.http.client.CookieStore;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.log4j.Logger;

import java.util.List;

public class ExecutionContext implements Cloneable{
    private Executor executor;
    private FormParamStore paramStore;
    private CookieStore cookieStore;
    private ContextParamStore contextStore;

    private RequestChainEntity rootChain;

    private ChainIndexSequntialFinder sequntialFinder;

    private static Logger logger = Logger.getLogger(ExecutionContext.class);

    public ExecutionContext(RequestChainEntity rootChain) {
        this(rootChain,null);
    }

    public ExecutionContext(RequestChainEntity rootChain, List<Param> startParams) {
        this.rootChain = rootChain;
        this.cookieStore = new BasicCookieStore();
        this.contextStore = new ContextParamStore();
        this.paramStore = new FormParamStore();
        init();
        initStartParams(startParams);
    }

    public ExecutionContext(RequestChainEntity rootChain, List<Param> startParams, ExecutionContext context) {
        this.rootChain = rootChain;
        this.cookieStore = context.getCookieStore();
        this.contextStore = context.getContextStore();
        this.paramStore = context.getParamStore();
        init();
        initStartParams(startParams);
    }

    private void init() {
        try {
////            this.executor=Executor.newInstance(SSLUtil.getIgnoreVerifySSLHttpClient());
//
//            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            keyStore.load(new FileInputStream(new File("D:\\_EnvSofts\\Java\\jdk1.8.0_144\\jre\\lib\\security\\12306cn.keystore")), "changeit".toCharArray());
//            SSLContext sslcontext = SSLContexts.custom()
//                    //忽略掉对服务器端证书的校验
//                    .loadTrustMaterial(new TrustStrategy() {
//                        @Override
//                        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//                            return true;
//                        }
//                    })
//
//                    //加载服务端提供的truststore(如果服务器提供truststore的话就不用忽略对服务器端证书的校验了)
//                    //.loadTrustMaterial(new File("D:\\truststore.jks"), "123456".toCharArray(),
//                    //        new TrustSelfSignedStrategy())
//                    .loadKeyMaterial(keyStore, "changeit".toCharArray())
//                    .build();
//            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
//                    sslcontext,
//                    new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"},
//                    null,
//                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//            CloseableHttpClient httpclient = HttpClients.custom()
//                    .setSSLSocketFactory(sslConnectionSocketFactory)
//                    .build();
            this.executor = createExecutor();
        } catch (Exception e) {
            throw new RuntimeExecuteException(e);
        }
        this.executor.use(cookieStore);
        //初始化Chain实体的索引信息
        this.sequntialFinder = new ChainIndexSequntialFinder(this.rootChain);
    }

    private Executor createExecutor() {
        return Executor.newInstance(SSLUtil.getIgnoreVerifySSLHttpClient());
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public RequestChainEntity getRootChain() {
        return rootChain;
    }

    public FormParamStore getParamStore() {
        return paramStore;
    }

    public void setParamStore(FormParamStore paramStore) {
        this.paramStore = paramStore;
    }

    public ContextParamStore getContextStore() {
        return this.contextStore;
    }

    public void setContextStore(ContextParamStore contextStore) {
        this.contextStore = contextStore;
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(CookieStore cookieStore) {
        if(this.executor == null){
            this.executor = createExecutor();
        }
        this.cookieStore = cookieStore;
        this.executor.use(this.cookieStore);
    }

    private void initStartParams(List<Param> startParams) {
        //初始化有默认值的Param
        initDefaultValueParams();

        //添加启动参数
        if(startParams!=null){
            for (Param param : startParams) {
                paramStore.addIfNull(param);
            }
        }
    }

    private void initDefaultValueParams() {
        List<SequentialEntity> sequentialList = rootChain.getSequentialList();
        for (SequentialEntity seq : sequentialList) {
            if (seq instanceof RequestEntity){
                List<Param> params = ((RequestEntity) seq).getRequestInfo().getParams();
                for (Param param : params) {
                    paramStore.addIfNull(param);
                }
            }
        }
    }

    @Override
    public ExecutionContext clone() throws CloneNotSupportedException {
        ExecutionContext clone = (ExecutionContext) super.clone();
        clone.setContextStore((ContextParamStore) this.contextStore.clone());
        return clone;
    }

    public RequestEntity getRequestEntity(String name){
        SequentialEntity seq = getSequntialFinder().findSeqByName(name);
        if(seq instanceof RequestEntity){
            RequestEntity requestEntity = (RequestEntity) seq;
            requestEntity.buildRequest(this);
            return requestEntity;
        }
        return null;
    }

    public Request getRequest(String name){
        RequestEntity requestEntity = getRequestEntity(name);
        if (requestEntity != null) {
            return requestEntity.getRequest();
        }
        return null;
    }

    public ChainIndexSequntialFinder getSequntialFinder() {
        return sequntialFinder;
    }

    public ContextExpressionParser getExpressionParser() {
        return this.contextStore.getExpressionParser();
    }
}
