package com.davidlong.creeper.execution;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.base.ChainExecutor;
import com.davidlong.creeper.execution.base.ContextExecutor;
import com.davidlong.creeper.execution.base.RequestExecutor;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.model.ChainExecutionResult;
import com.davidlong.creeper.model.result.ChainResult;
import com.davidlong.creeper.model.result.HttpResult;
import com.davidlong.creeper.model.seq.RequestChainEntity;
import com.davidlong.creeper.model.seq.RequestEntity;
import com.davidlong.creeper.model.seq.SequentialEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.util.Assert;

import java.io.IOException;

public class ChainContextExecutor implements ContextExecutor {
    private ExecutionContext executionContext;

    public ChainContextExecutor(ExecutionContext context) {
        Assert.isInstanceOf(ExecutionContext.class,context);
        this.executionContext = context;
    }

    public ChainContextExecutor(Class entityClass) {
        ExecutionContext context = ExecutionContextFactory.createContext(entityClass);
        Assert.isInstanceOf(ExecutionContext.class,context);
        this.executionContext = context;
    }

    @Override
    public HttpResult exeucteRequest(String name, boolean withHandle) {
        SequentialEntity seq = executionContext.getSequntialFinder().findSeqByName(name);
        HttpResult result=new HttpResult();
        if (seq instanceof RequestEntity) {
            RequestEntity requestEntity = (RequestEntity) seq;
            RequestExecutor<RequestEntity> requestExecutor = ExecutorFactory.createRequestExecutor(requestEntity.getClass(), executionContext);
            try {
                HttpResponse httpResponse;
                if(withHandle){
                    httpResponse = requestExecutor.execute(requestEntity).getHttpResponse();
                }else{
                    httpResponse = requestExecutor.exeucteRequest(requestEntity);
                }
                String content = EntityUtils.toString(httpResponse.getEntity());
                result.setHttpResponse(httpResponse);
                result.setContent(content);
            } catch (IOException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public HttpResult exeucteRequest(String name) {
        return exeucteRequest(name,true);
    }

    @Override
    public ChainResult exeucteChain(String name) {
        SequentialEntity seq = executionContext.getSequntialFinder().findSeqByName(name);
        ChainResult result=new ChainResult();
        if (seq instanceof RequestChainEntity) {
            RequestChainEntity chainEntity = (RequestChainEntity) seq;
            ChainExecutor chainExecutor = ExecutorFactory.createChainExecutor(chainEntity.getClass(), executionContext);
            try {
                ChainExecutionResult executionResult = chainExecutor.execute(chainEntity);
            } catch (IOException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public ChainResult exeucteRootChain() {
        ChainExecutor executor = ExecutorFactory.createChainExecutor(executionContext.getRootChain().getClass(),executionContext);
        ChainExecutionResult result = executor.execute();
        System.out.println();
        return null;
    }

    @Override
    public ExecutionContext getExecutionContext() {
        return executionContext;
    }


}
