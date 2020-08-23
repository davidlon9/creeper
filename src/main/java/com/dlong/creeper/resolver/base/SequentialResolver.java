package com.dlong.creeper.resolver.base;

import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.model.seq.RequestChainEntity;
import com.dlong.creeper.model.seq.SequentialEntity;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class SequentialResolver extends AnnotationResolver{
    private static final String DEFUALT_NAME="";

    public SequentialResolver(AnnotatedElement target, Class<? extends Annotation> annoClass) {
        super(target,annoClass);
    }

    @Override
    public Annotation resolveAnnotation() throws AnnotationNotFoundException {
        Annotation annotation = super.resolveAnnotation();
        if (annotation == null) {
            throw new AnnotationNotFoundException(getTarget().toString()+" must be annotated with "+getAnnoClass().getSimpleName());
        }
        return annotation;
    }

    public void resolveSequential(SequentialEntity sequentialEntity) throws AnnotationNotFoundException {
        this.resolveAnnotation();
        Map<String, Object> attrs = AnnotationUtils.getAnnotationAttributes(getAnnotation());
        Integer index = (Integer) attrs.get("index");
        String name = (String) attrs.get("name");
        String description = (String) attrs.get("description");

        sequentialEntity.setIndex(index);
        if (DEFUALT_NAME.equals(name)) {
            name = getDefaultName();
        }
        sequentialEntity.setName(name);
        sequentialEntity.setDescription(description);
    }

    private String findTargetName(Object target) {
        if(target instanceof Class){
            return ((Class) target).getSimpleName();
        }else if(target instanceof Method){
            return ((Method) target).getName();
        }else{
            return "";
        }
    }

    public String getDefaultName(){
        return getTarget().toString();
    }

    private static void assignSequenceIds(SequentialEntity target,RequestChainEntity parent){
        String parentId;
        String parentFullName=null;
        if(parent==null){
            parentId=String.valueOf(target.hashCode());
        }else{
            parentId = parent.getSequenceId();
            parentFullName = parent.getFullName();
        }

        String name = target.getName();
        if(parentFullName!=null){
            target.setFullName(parentFullName+"-"+name);
        }else{
            target.setFullName(name);
        }

        if(target instanceof RequestChainEntity){
            RequestChainEntity chain=(RequestChainEntity)target;

            target.setSequenceId(parentId+"-"+target.getIndex());
            List<SequentialEntity> sequentialList = chain.getSequentialList();
            for (SequentialEntity seq : sequentialList) {
                assignSequenceIds(seq,chain);
            }
        }else{
            target.setSequenceId(parentId+"-"+target.getIndex());
        }
    }

    public static void assignSequenceIds(SequentialEntity target){
       assignSequenceIds(target,null);
    }
}
