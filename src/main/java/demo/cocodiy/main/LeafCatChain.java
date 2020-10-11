package demo.cocodiy.main;

import com.dlong.creeper.annotation.control.looper.ForEach;
import com.dlong.creeper.annotation.control.recorder.FileRecordsIgnore;
import com.dlong.creeper.annotation.http.Get;
import com.dlong.creeper.annotation.seq.ChainReference;
import com.dlong.creeper.annotation.seq.RequestChain;
import com.dlong.creeper.annotation.seq.SeqRequest;
import com.dlong.creeper.control.MoveAction;
import com.dlong.creeper.control.MoveActions;
import com.dlong.creeper.execution.context.ContextParamStore;
import demo.cocodiy.Config;
import demo.cocodiy.model.Cat;
import demo.cocodiy.service.CatService;

import java.io.IOException;
import java.util.Set;

@ForEach(itemsContextKey = "leafCats",itemName = "cat")
@RequestChain(description = "遍历所有子类")
public class LeafCatChain{
    CatService catService=new CatService();
    @SeqRequest(index=1,description = "获取子类别中所有的礼物链接")
    @Get("${#cat.path}")
    public MoveAction getLeafCatGifts(String result, ContextParamStore contextParamStore) throws IOException {
        Cat cat = (Cat) contextParamStore.getValue("cat");
        System.out.println("retriving cat gift details "+cat.getTitle());
        Set<String> paths = catService.resvoleLeafCatGiftPaths(result);
        contextParamStore.addParam("giftDetailPaths",paths);
        return MoveActions.FORWARD(Config.DEFAULT_INTERVAL);
    }

    @ChainReference(index = 2,description = "遍历所有子类")
    LeafCatGiftChain leafCatGiftChain;

}

