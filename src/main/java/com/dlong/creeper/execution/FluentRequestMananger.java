package com.dlong.creeper.execution;

import com.dlong.creeper.execution.context.ContextParamStore;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.execution.context.FormParamStore;
import org.apache.http.client.CookieStore;
import org.apache.http.client.fluent.Executor;
import org.apache.log4j.Logger;

import java.lang.reflect.Proxy;

public class FluentRequestMananger {
    private Logger logger = Logger.getLogger(FluentRequestMananger.class);

    private ExecutionContext context;

    public FluentRequestMananger(Executor executor, ExecutionContext context) {
        if(context!=null){
            this.context = context;
        }else{
            this.context = new ExecutionContext();
        }
        if(executor!=null){
            this.context.setExecutor(executor);
        }
    }

    public FluentRequestMananger(ExecutionContext context) {
        this(null,context);
    }

    public FluentRequestMananger(Executor executor) {
        this(executor,null);
    }

    public FluentRequestMananger() {
        this(null,null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getClassProxy(Class<T> mappingClass) {
        isInterface(mappingClass);
        return (T) Proxy.newProxyInstance(
                mappingClass.getClassLoader(),
                new Class[] { mappingClass },
                new FluentRequestInvocationHandler(mappingClass, this.context));
    }

    private <T> void isInterface(Class<T> mappingClass) {
        if (!mappingClass.isInterface()) {
            throw new IllegalArgumentException("fluent mapping class must be interface");
        }
    }

    public ContextParamStore getContextStore() {
        return context.getContextStore();
    }

    public FormParamStore getParamStore() {
        return context.getParamStore();
    }

    public CookieStore getCookieStore() {
        return context.getCookieStore();
    }

    public Executor getExecutor() {
        return context.getExecutor();
    }

//    public static void main(String[] args) throws IOException {
//        FluentRequestMananger mananger = new FluentRequestMananger();
//        ContextParamStore context = mananger.getContextStore();
//        SimpleDemo simpleDemo = mananger.getClassProxy(SimpleDemo.class);
//        context.addParam("index",1);
//        Integer index = (Integer) context.getValue("index");
//        while (index <= 4){
//            System.out.println(index);
//            Request maxPageNum = simpleDemo.handlePDFListBook();
//            context.addParam("index",++index);
//        }
//        System.out.println();
//    }
}
