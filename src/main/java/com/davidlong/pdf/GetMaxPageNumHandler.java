package com.davidlong.pdf;

import com.davidlong.http.control.MoveAction;
import com.davidlong.http.control.MoveActions;
import com.davidlong.http.execution.context.ContextParamStore;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.execution.handler.entity.ExecutionHandler;
import com.davidlong.pdf.serivce.DZSWService;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class GetMaxPageNumHandler extends ExecutionHandler {
    @Override
    public Boolean beforeHandle(Request request, ExecutionContext context) throws IOException {
        return true;
    }

    @Override
    public MoveAction afterHandle(HttpResponse response, ExecutionContext context) throws IOException {
        String html = EntityUtils.toString(response.getEntity());
        Document rootPage = Jsoup.parse(html);
        ContextParamStore contextStore = context.getContextStore();
        int maxPageNum = DZSWService.getMaxPageNum(rootPage, contextStore);
        contextStore.addParam("maxPage",maxPageNum);
        contextStore.addParam("isHandlePage",true);
        return MoveActions.FORWARD();
    }
}
