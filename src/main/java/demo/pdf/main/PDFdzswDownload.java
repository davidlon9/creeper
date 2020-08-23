package demo.pdf.main;

import com.dlong.creeper.annotation.Host;
import com.dlong.creeper.annotation.http.Get;
import com.dlong.creeper.annotation.seq.RequestChain;
import com.dlong.creeper.annotation.seq.SeqRequest;
import com.dlong.creeper.control.MoveAction;
import com.dlong.creeper.control.MoveActions;
import com.dlong.creeper.execution.context.ContextParamStore;
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
