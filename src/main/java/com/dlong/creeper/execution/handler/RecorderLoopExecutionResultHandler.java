package com.dlong.creeper.execution.handler;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.LoopExecutionResult;
import com.dlong.creeper.model.seq.LoopableEntity;
import com.dlong.creeper.model.seq.RequestEntity;
import com.dlong.creeper.model.seq.control.Looper;
import com.dlong.creeper.model.seq.control.PredictableUrlLooper;
import com.dlong.creeper.model.seq.recorder.UrlRecorder;
import org.apache.log4j.Logger;

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
        Looper looper = loopableEntity.getLooper();
        if(looper instanceof PredictableUrlLooper){
            PredictableUrlLooper predictableUrlLooper = (PredictableUrlLooper) looper;
            Map<String, Object> predictItemUrlMap = predictableUrlLooper.getPredictUrlItemMap();
            if(predictItemUrlMap.size()==0){
                return;
            }
            if (loopableEntity instanceof RequestEntity) {
                RequestEntity requestEntity = (RequestEntity) loopableEntity;
                UrlRecorder recorder = requestEntity.getRecorder();
                if (recorder != null) {
                    try {
                        logger.info("reading recorded urls");
                        Set<String> urls = recorder.readUrlRecords(context);
                        logger.info("readed "+urls.size()+" recorded urls");
                        for (String url : urls) {
                            Object obj = predictItemUrlMap.get(url);
                            if(obj == null){
                                continue;
                            }
                            predictItemUrlMap.remove(url);
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
//        LoopableEntity loopableEntity = executionResult.getOrginalSeq();
//        Looper looper = loopableEntity.getLooper();
//        if(looper!=null){
//            int loopNum = executionResult.getLoopNum();
//            int totalLoopNum = executionResult.getTotalNum();
//            int i = totalLoopNum - loopNum;
//            if(loopNum){
//
//            }
//        }
    }
}
