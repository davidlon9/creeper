package com.dlong.creeper.execution.context;

import com.dlong.creeper.expression.ContextExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

public class ContextParamStore implements ParamStore<String,Object>,Cloneable{
    private final ThreadLocal<Map<String,Object>> localParams = new ThreadLocal<>();
    private StandardEvaluationContext evaluationContext;
    private ContextRootObject rootObject;
    private ContextExpressionParser  expressionParser;

    public ContextParamStore(ContextRootObject rootObject) {
      initEvaluationContext(rootObject);
    }

    private void initEvaluationContext(ContextRootObject rootObject){
        if (rootObject == null) {
            rootObject=new ContextRootObject(this);
        }
        this.rootObject=rootObject;
        this.evaluationContext = new StandardEvaluationContext(rootObject);
        this.expressionParser = new ContextExpressionParser(new SpelExpressionParser(),evaluationContext);
    }

    public ContextParamStore() {
        this(null);
    }

    public Object getValue(String name){
        return getParams().get(name);
    }

    public void addParam(String name, Object value){
        getParams().put(name,value);
        this.evaluationContext.setVariable(name, value);
    }

    public void addParams(Map<String, Object> params) {
        getParams().putAll(params);
        this.evaluationContext.setVariables(params);
    }

    public boolean containsName(String name){
        return getParams().containsKey(name);
    }

    public Map<String,Object> getParamMap() {
        return getParams();
    }

    public StandardEvaluationContext getEvaluationContext() {
        return this.evaluationContext;
    }

    public void setRootObject(ContextRootObject rootObject) {
        this.rootObject = rootObject;
    }

    public ContextRootObject getRootObject() {
        return rootObject;
    }

    private Map<String, Object> getParams() {
        Map<String, Object> map = localParams.get();
        if(map!=null){
            return map;
        }
        return initLocalMap();
    }

    private Map initLocalMap(){
        Map map=new HashMap<>();
        if(this.rootObject!=null){
            Map<String, Object> context = this.rootObject.getContext();
            map.putAll(context);
        }
        localParams.set(map);
        initEvaluationContext(null);
        return map;
    }

    public ContextExpressionParser getExpressionParser() {
        return expressionParser;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ContextParamStore clone = (ContextParamStore) super.clone();
        return clone;
    }
}
