package com.dlong.creeper.execution.handler;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.model.result.LoopExecutionResult;
import com.dlong.creeper.model.seq.LoopableEntity;
import com.dlong.creeper.model.seq.RequestEntity;
import com.dlong.creeper.model.seq.recorder.FileUrlRecorder;
import com.dlong.creeper.model.seq.recorder.UrlRecorder;
import org.apache.log4j.Logger;

import java.io.IOException;

public class RecorderLoopExecutionResultHandler implements LoopExecutionResultHandler {
    private static Logger logger = Logger.getLogger(RecorderLoopExecutionResultHandler.class);

    @Override
    public void beforeExecute(LoopExecutionResult<? extends LoopableEntity> executionResult, ExecutionContext context) throws ExecutionException {
        LoopableEntity loopableEntity = executionResult.getOrginalSeq();
        if(loopableEntity instanceof RequestEntity){
            RequestEntity requestEntity = (RequestEntity) loopableEntity;
            String url = requestEntity.getRequestInfo().getUrl();
            UrlRecorder recorder = requestEntity.getRecorder();
            if(recorder.isUrlRecorded(url)){
                logger.info("url "+url+" is already recorded!");
                executionResult.setSkipExecute(true);
            }
        }
    }

    @Override
    public void afterExecute(LoopExecutionResult<? extends LoopableEntity> executionResult, ExecutionContext context) throws ExecutionException {
        LoopableEntity loopableEntity = executionResult.getOrginalSeq();
        if(loopableEntity instanceof RequestEntity){
            RequestEntity requestEntity = (RequestEntity) loopableEntity;
            String url = requestEntity.getRequestInfo().getUrl();
            UrlRecorder recorder = requestEntity.getRecorder();
            logger.info("recording url "+url);
            recorder.addUrlRecord(url);
            if(recorder instanceof FileUrlRecorder){
                FileUrlRecorder fileUrlRecorder = (FileUrlRecorder) recorder;
                if(fileUrlRecorder.isIterateToCount()){
                    try {
                        fileUrlRecorder.writeUrlRecords();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
