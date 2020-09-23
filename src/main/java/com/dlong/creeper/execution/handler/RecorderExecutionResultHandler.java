package com.dlong.creeper.execution.handler;

import com.dlong.creeper.control.*;
import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.execution.request.DefaultRequestBuilder;
import com.dlong.creeper.execution.resolver.method.MoveStrategyBeforeResultResolver;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.RequestEntity;
import com.dlong.creeper.model.seq.recorder.FileUrlRecorder;
import com.dlong.creeper.model.seq.recorder.UrlRecorder;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 此handler afterExecute()必需在 handlerMethod调用后执行
 */
public class RecorderExecutionResultHandler implements RequestExecutionResultHandler {
    private static Logger logger = Logger.getLogger(RecorderExecutionResultHandler.class);
    //TODO configable
    public static List<Class<? extends MoveAction>> needRecordActions = Arrays.asList(ForwardAction.class, JumpAction.class, BreakAction.class, ContinueAction.class);

    @Override
    public void beforeExecute(ExecutionResult<? extends RequestEntity> executionResult, ChainContext context) throws ExecutionException {
        RequestEntity requestEntity = executionResult.getOrginalSeq();
        UrlRecorder recorder = requestEntity.getRecorder();
        if (recorder == null) {
            return;
        }
        String url = new DefaultRequestBuilder(context).getParsedUrl(requestEntity.getRequestInfo());
        if (recorder.isUrlRecorded(url)) {
            logger.info("url " + url + " is already recorded!");
            //继续下一循环
            new MoveStrategyBeforeResultResolver().resolveResult(executionResult, context, new ContinueAction(0));
        }
    }

    @Override
    public void afterExecute(ExecutionResult<? extends RequestEntity> executionResult, ChainContext context) throws ExecutionException {
        RequestEntity requestEntity = executionResult.getOrginalSeq();
        UrlRecorder recorder = requestEntity.getRecorder();
        if (recorder == null) {
            return;
        }
        if (executionResult.isFailed()) {
            return;
        }
        MoveAction actionResult = executionResult.getActionResult();
        Assert.notNull(actionResult);
        if (!needRecordActions.contains(actionResult.getClass())) {
            return;
        }
        String url = new DefaultRequestBuilder(context).getParsedUrl(requestEntity.getRequestInfo());
        if (recorder instanceof FileUrlRecorder) {
            FileUrlRecorder fileUrlRecorder = (FileUrlRecorder) recorder;
            recorder.addUrlRecord(url);
            logger.info("recorded url " + url + " , current url size is " + fileUrlRecorder.getCurrentIterateCount());
            if (fileUrlRecorder.isIterateToCount()) {
                try {
                    logger.info("writing " + fileUrlRecorder.getPerIterateTimesUpdate() + " url to " + fileUrlRecorder.getRecordFile().getName() + " , current file url size is " + fileUrlRecorder.getCurrentIterateCount());
                    fileUrlRecorder.writeUrlRecords();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
