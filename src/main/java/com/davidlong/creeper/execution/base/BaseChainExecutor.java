package com.davidlong.creeper.execution.base;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.exception.RuntimeExecuteException;
import com.davidlong.creeper.execution.ExecutorFactory;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.execution.registry.DefaultChainResultHandlerRegistry;
import com.davidlong.creeper.execution.registry.base.ChainExecutionResultHandlerRegistry;
import com.davidlong.creeper.execution.registry.base.ExecutionResultResolverRegistry;
import com.davidlong.creeper.model.ChainExecutionResult;
import com.davidlong.creeper.model.ExecutionResult;
import com.davidlong.creeper.model.seq.RequestChainEntity;
import com.davidlong.creeper.model.seq.RequestEntity;
import com.davidlong.creeper.model.seq.SequentialEntity;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class BaseChainExecutor<T extends RequestChainEntity> extends AbstractLoopableExecutor<T> implements ChainExecutor<T> {
    private static Logger logger = Logger.getLogger(BaseChainExecutor.class);

    private ChainExecutionResultHandlerRegistry chainExecutionResultHandlerRegistry;
    private ExecutionResultResolverRegistry executionResultResolverRegistry;

    public BaseChainExecutor(ExecutionContext context) {
        this(context,false);
    }

    public BaseChainExecutor(ExecutionContext context, boolean isMultiThread) {
        super(context,isMultiThread);
        chainExecutionResultHandlerRegistry = new DefaultChainResultHandlerRegistry();
        executionResultResolverRegistry = new ExecutionResultResolverRegistry();
    }

    @Override
    public ChainExecutionResult<T> execute() {
        return this.execute((T) getRootChain());
    }

    public ChainExecutionResult<T> execute(T chainEntity){
        try {
            if (isInRoot(chainEntity)) {
                return (ChainExecutionResult<T>) super.execute(chainEntity);
            }else{
                throw new RuntimeExecuteException("no chain found in context, executor can't execute "+chainEntity+" in current execution context");
            }
        } catch (IOException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isInRoot(RequestChainEntity chainEntity) {
        RequestChainEntity rootChain = getRootChain();
        if (chainEntity.equals(rootChain)) {
            return true;
        }else{
            List<SequentialEntity> sequentialList = rootChain.getSequentialList();
            for (SequentialEntity sequentialEntity : sequentialList) {
                if(sequentialEntity.equals(chainEntity)){
                    return true;
                }else if(sequentialEntity instanceof RequestChainEntity){
                    return isInRoot((RequestChainEntity) sequentialEntity);
                }
            }
        }
        return false;
    }

    public RequestChainEntity getRootChain() {
        return getContext().getRootChain();
    }

    /**
     * 实际执行Chain的方法
     * 从第一个Sequential开始执行
     * @param chainEntity
     * @return
     * @throws IOException
     * @throws ExecutionException
     */
    public ChainExecutionResult<T> doExecute(T chainEntity) throws IOException, ExecutionException {
        ChainExecutionResult<T> executionResult = new ChainExecutionResult<>(chainEntity);
        ExecutionContext context = super.getContext();
        executionResult.setContext(context);

        SequentialEntity firstSeq = context.getSequntialFinder().findFirstSeqByFixedIndex(chainEntity);
        logger.info("Begin Chain "+chainEntity);

        getChainExecutionResultHandlerRegistry().invokeBeforeExecutionHandler(executionResult, context);

        getExecutionResultResolverRegistry().beforeExecuteResolve(executionResult, context);

        //在Chain中从第一个Sequential开始递归执行
        executeSeqEntity(executionResult,firstSeq);

        ExecutionResult finalResult = executionResult.getFinalResult();
        if(finalResult.isFailed()){
            executionResult.setFailed(true);
        }
        //调用handlerMethod——>多线程处理
        getChainExecutionResultHandlerRegistry().invokeAfterExecutionHandler(executionResult, context);

        getExecutionResultResolverRegistry().afterExecuteResolve(executionResult, context);

        if(executionResult.isFailed()){
            logger.error("Chain "+chainEntity+" execute failed!");
        }
        executionResult.setExecuted(true);
        logger.info("End Chain "+chainEntity+"\n");
        return executionResult;
    }

    @SuppressWarnings("unchecked")
    public ExecutionResult executeSeqEntity(ChainExecutionResult<T> executionResult, SequentialEntity sequentialEntity) throws IOException, ExecutionException {
        ExecutionResult innerResult = null;
        if (sequentialEntity instanceof RequestChainEntity) {
            innerResult = executeChain((RequestChainEntity) sequentialEntity);
        } else if (sequentialEntity instanceof RequestEntity) {
            innerResult = executeRequest((RequestEntity) sequentialEntity);
        }

        //添加至ChainResult
        if(innerResult==null){
            innerResult = new ExecutionResult(sequentialEntity);
        }
        executionResult.addChainResult(innerResult);

        SequentialEntity nextSeq = innerResult.getNextSeq();
        if (nextSeq != null) {
            //执行下一个SeqentialEntity
            executeSeqEntity(executionResult,nextSeq);
        }else{
            executionResult.setFinalResult(innerResult);
            //TODO 执行失败的话inner entity是未执行的，需要更改
            logger.info("all inner entity of Chain "+executionResult.getOrginalSeq()+" has been executed");
        }
        return executionResult;
    }

    /**
     * 创建一个ChainExecutor来执行Chain
     * @param chainEntity
     * @return
     * @throws IOException
     * @throws ExecutionException
     */
    public ExecutionResult executeChain(RequestChainEntity chainEntity) throws IOException, ExecutionException {
        ExecutionResult result;
        ChainExecutor chainExecutor = ExecutorFactory.createChainExecutor(chainEntity.getClass(), super.getContext());
        result = chainExecutor.execute(chainEntity);
        return result;
    }

    /**
     * 创建一个RequestExecutor来执行Request
     * @param requestEntity
     * @return
     * @throws IOException
     */
    public ExecutionResult<RequestEntity> executeRequest(RequestEntity requestEntity) throws IOException {
        ExecutionResult<RequestEntity> result;
        try {
            RequestExecutor<RequestEntity> requestExecutor = ExecutorFactory.createRequestExecutor(requestEntity.getClass(), super.getContext());
            result = requestExecutor.execute(requestEntity);
        } catch (ExecutionException e) {
            e.printStackTrace();
            logger.warn("execution throw a execption then skiped this request "+requestEntity);
            result = new ExecutionResult<>(requestEntity);
            int index = requestEntity.getIndex();
            SequentialEntity next = getContext().getSequntialFinder().findSeqByFixedIndex(++index,requestEntity.getParent());
            result.setNextSeq(next);
        }
        return result;
    }

    public ChainExecutionResultHandlerRegistry getChainExecutionResultHandlerRegistry() {
        return chainExecutionResultHandlerRegistry;
    }

    public ExecutionResultResolverRegistry getExecutionResultResolverRegistry() {
        return executionResultResolverRegistry;
    }
}
