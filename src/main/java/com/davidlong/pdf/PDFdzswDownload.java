package com.davidlong.pdf;

import com.davidlong.http.annotation.Host;
import com.davidlong.http.annotation.http.Get;
import com.davidlong.http.annotation.seq.RequestChain;
import com.davidlong.http.annotation.seq.SeqRequest;
import com.davidlong.http.control.MoveAction;
import com.davidlong.http.control.MoveActions;
import com.davidlong.http.execution.context.ContextParamStore;
import org.apache.http.HttpResponse;

import java.io.IOException;

@Host(value = "www.xgv5.com",scheme = "https")
@RequestChain(index = 1)
public class PDFdzswDownload {
    @SeqRequest(index = 1,description = "获取最大页数")
    @Get("/category-30.html")
    public MoveAction download(HttpResponse response, ContextParamStore contextParamStore) throws IOException {
        return MoveActions.FORWARD();
    }
}
