package com.dlong.creeper.execution.handler;

import com.dlong.creeper.Config;
import com.dlong.creeper.control.MoveAction;
import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.execution.request.DefaultRequestBuilder;
import com.dlong.creeper.model.result.LoopExecutionResult;
import com.dlong.creeper.model.seq.LoopableEntity;
import com.dlong.creeper.model.seq.RequestEntity;
import com.dlong.creeper.model.seq.control.Looper;
import com.dlong.creeper.model.seq.control.PredictableUrlLooper;
import com.dlong.creeper.model.seq.recorder.BaseRecorder;
import com.dlong.creeper.model.seq.recorder.UrlRecorder;
import com.dlong.creeper.model.seq.recorder.WriteStrategy;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * 此handler afterExecute()必需在 handlerMethod调用后执行
 */
public class RecorderLoopExecutionResultHandler implements LoopExecutionResultHandler {
    private static Logger logger = Logger.getLogger(RecorderLoopExecutionResultHandler.class);

    /**
     * 过滤掉可预测Url的Looper中已被记录的items
     * @param executionResult
     * @param context
     * @throws ExecutionException
     */
    @Override
    public void beforeExecute(LoopExecutionResult<? extends LoopableEntity> executionResult, ChainContext context) throws ExecutionException {
        LoopableEntity loopableEntity = executionResult.getOrginalSeq();
        if (loopableEntity instanceof RequestEntity) {
            Looper looper = loopableEntity.getLooper();
            if(looper instanceof PredictableUrlLooper){
                PredictableUrlLooper predictableUrlLooper = (PredictableUrlLooper) looper;
                Map<String, Object> predictUrlItemMap = predictableUrlLooper.getPredictUrlItemMap();
                if(predictUrlItemMap.size()==0){
                    return;
                }
                RequestEntity requestEntity = (RequestEntity) loopableEntity;
                UrlRecorder urlRecorder = requestEntity.getRecorder();
                if (urlRecorder != null) {
                    try {
                        logger.info("reading recorded urls");
                        Set<String> urls = urlRecorder.readUrlRecords(context);
                        logger.info("readed "+urls.size()+" recorded urls");
                        for (String url : urls) {
                            Object obj = predictUrlItemMap.get(url);
                            if(obj == null){
                                continue;
                            }
                            predictUrlItemMap.remove(url);
                            logger.warn("remove history item "+obj.toString()+" of recorded url "+url);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void afterExecute(LoopExecutionResult<? extends LoopableEntity> executionResult, ChainContext context) throws ExecutionException {
        LoopableEntity loopableEntity = executionResult.getOrginalSeq();
        if (!(loopableEntity instanceof RequestEntity)) {
            return;
        }
        RequestEntity requestEntity = (RequestEntity) loopableEntity;
        UrlRecorder urlRecorder = requestEntity.getRecorder();
        if (urlRecorder == null) {
            return;
        }
        if (executionResult.isFailed()) {
            return;
        }
        MoveAction actionResult = executionResult.getActionResult();
        Assert.notNull(actionResult);
        if (!Config.needRecordActions.contains(actionResult.getClass())) {
            return;
        }
        String url = new DefaultRequestBuilder(context).buildUrl(requestEntity.getRequestInfo());
        if (urlRecorder instanceof BaseRecorder) {
            WriteStrategy writeStrategy = ((BaseRecorder) urlRecorder).getWriteStrategy();
            if (writeStrategy.equals(WriteStrategy.LoopEnd)) {
                urlRecorder.addUrlRecord(url);
                try {
                    urlRecorder.writeUrlRecords(context);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
