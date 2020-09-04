package com.dlong.creeper.execution.context;

import com.dlong.creeper.expression.ContextExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContextParamStore implements ParamStore<String,Object>, Cloneable{
    private final Map<String,Object> params = new ConcurrentHashMap<>();
    private StandardEvaluationContext evaluationContext;
    private ContextExpressionParser  expressionParser;
    private Object rootObject;

    public ContextParamStore(Object rootObject) {
        initEvaluationContext(rootObject);
    }

    private void initEvaluationContext(Object rootObject){
        if (rootObject == null) {
            rootObject=new ContextRootObject(this);
        }
        this.rootObject = rootObject;
        this.evaluationContext = new StandardEvaluationContext(rootObject);
        this.expressionParser = new ContextExpressionParser(new SpelExpressionParser(),evaluationContext);
    }

    public ContextParamStore() {
        this(null);
    }

    public Object getValue(String name){
        return params.get(name);
    }

    public synchronized void addParam(String name, Object value){
        params.put(name,value);
        this.evaluationContext.setVariable(name, value);
    }

    public synchronized void addParams(Map<String, Object> params) {
        this.params.putAll(params);
        this.evaluationContext.setVariables(params);
    }

    public boolean containsName(String name){
        return params.containsKey(name);
    }

    public Map<String,Object> getParamMap() {
        return params;
    }

    public StandardEvaluationContext getEvaluationContext() {
        return this.evaluationContext;
    }

    public void setRootObject(Object rootObject) {
        this.rootObject = rootObject;
    }

    public Object getRootObject() {
        return rootObject;
    }

    public ContextExpressionParser getExpressionParser() {
        return expressionParser;
    }

    @Override
    protected ContextParamStore clone() throws CloneNotSupportedException {
        return (ContextParamStore) super.clone();
    }
}
