package demo.cocodiy.main;

import com.dlong.creeper.annotation.control.looper.ForEach;
import com.dlong.creeper.annotation.control.recorder.FileRecordsIgnore;
import com.dlong.creeper.annotation.http.Get;
import com.dlong.creeper.annotation.seq.RequestChain;
import com.dlong.creeper.annotation.seq.SeqRequest;
import com.dlong.creeper.control.MoveAction;
import com.dlong.creeper.control.MoveActions;
import com.dlong.creeper.execution.context.ContextParamStore;
import demo.cocodiy.Config;
import demo.cocodiy.service.CoGiftService;

import java.io.IOException;

@ForEach(itemsContextKey = "giftDetailPaths",itemName = "detailPath")
@RequestChain(description = "遍历子类别中的所有礼物链接")
public class LeafCatGiftChain{
    CoGiftService coGiftService=new CoGiftService();

    @SeqRequest(index=1,description = "解析礼物详情页面")
    @Get("${#detailPath}")
    @FileRecordsIgnore(filePath = "D:\\repository\\gift-cat-server\\src\\main\\resources\\detail-his.txt")
    public MoveAction giftDetail(String result, ContextParamStore contextParamStore) throws IOException {
        coGiftService.resolveGiftDetail(result,contextParamStore);
        return MoveActions.FORWARD(Config.DEFAULT_INTERVAL);
    }
}