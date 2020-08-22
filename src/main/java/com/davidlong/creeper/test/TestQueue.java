package com.davidlong.creeper.test;

import com.davidlong.creeper.execution.base.ContextSeqExecutor;
import com.davidlong.creeper.execution.context.ContextParamStore;
import com.davidlong.creeper.execution.context.ContextRootObject;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.model.seq.RequestChainEntity;
import com.davidlong.creeper.resolver.ChainsMappingResolver;
import demo.pdf.main.PDFdzswChain;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class TestQueue {
    public static void main(String[] args) throws CloneNotSupportedException, InterruptedException {
        RequestChainEntity chainEntity = new ChainsMappingResolver(PDFdzswChain.class).resolve();

        ExecutionContext executionContext =new ExecutionContext(chainEntity);
        ContextSeqExecutor contextSeqExecutor = new ContextSeqExecutor(executionContext, true);
        final ExecutionContext context2 = contextSeqExecutor.getContext();
        int size=5;
        ExecutorService threadPool = Executors.newFixedThreadPool(size);
        ContextParamStore rootContexStore = context2.getContextStore();
        Object parse = context2.getExpressionParser().parse("${#abc}");
        System.out.println(parse);

        final BlockingQueue<String> queue=new LinkedBlockingDeque<>();
        final int[] queueCount = {10};
        for (int i = 0; i < queueCount[0]; i++) {
            queue.add("a"+i);
        }
        rootContexStore.addParam("queue",queue);
        System.out.println(Thread.currentThread()+":"+rootContexStore.getValue("queue"));
        for (int i = 0; i < size; i++) {
            threadPool.execute(() -> {
                ExecutionContext context = contextSeqExecutor.getContext();
                System.out.println();
                ContextParamStore contextStore = context.getContextStore();
                System.out.println(Thread.currentThread().getName() + " contextStore:" + contextStore);
                Map<String, Object> main = ((ContextRootObject) rootContexStore.getRootObject()).getContext();

                while (true) {
                    if (queue.size() == 0) {
                        System.out.println(Thread.currentThread().getName() + "结束了");
                        break;
                    }
                    System.out.println("main poll:"+main.get("poll"));
                    System.out.println("main queue:"+main.get("queue"));
                    System.out.println("tnread queue:"+contextStore.getValue("queue"));

                    String poll = queue.poll();
                    contextStore.addParam("poll", poll);
                    System.out.println(Thread.currentThread().getName() + " put poll:" + contextStore.getValue("poll"));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + " get :" + contextStore.getValue("poll"));
                    Object poll2 = contextStore.getRootObject().getContext().get("poll");

                    System.out.println(Thread.currentThread().getName()+poll2);
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

    }
}