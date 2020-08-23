package com.dlong.creeper.resolver;

import com.dlong.creeper.execution.context.ContextParamStore;
import demo.simple.SimpleDemo;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Proxy;

public class FluentRequestMappingMananger {
    private Logger logger = Logger.getLogger(FluentRequestMappingMananger.class);

    private ContextParamStore context;

    private Executor executor;

    public FluentRequestMappingMananger(ContextParamStore context, Executor executor) {
        this.context = context;
        this.executor = executor;
    }

    public FluentRequestMappingMananger(Executor executor) {
        this(new ContextParamStore(),Executor.newInstance());
    }

    public FluentRequestMappingMananger(ContextParamStore context) {
        this(context,Executor.newInstance());
    }

    public FluentRequestMappingMananger() {
        this(new ContextParamStore(),Executor.newInstance());
    }

    @SuppressWarnings("unchecked")
    public <T> T getClassProxy(Class<T> mappingClass) {
        isInterface(mappingClass);
        return (T) Proxy.newProxyInstance(
                mappingClass.getClassLoader(),
                new Class[] { mappingClass },
                new FluentRequestInvocationHandler(mappingClass, this.context, this.executor));
    }

    private <T> void isInterface(Class<T> mappingClass) {
        if (!mappingClass.isInterface()) {
            throw new IllegalArgumentException("fluent mapping class must be interface");
        }
    }

    public ContextParamStore getContext() {
        return context;
    }

    public Executor getExecutor() {
        return executor;
    }

    public static void main(String[] args) throws IOException {
        FluentRequestMappingMananger mananger = new FluentRequestMappingMananger();
        ContextParamStore context = mananger.getContext();
        SimpleDemo simpleDemo = mananger.getClassProxy(SimpleDemo.class);
        context.addParam("index",1);
        Integer index = (Integer) context.getValue("index");
        while (index <= 4){
            System.out.println(index);
            Request maxPageNum = simpleDemo.handlePDFListBook();
            context.addParam("index",++index);
        }
        System.out.println();
    }
}
