package demo.cocodiy.main;

import com.dlong.creeper.annotation.Host;
import com.dlong.creeper.annotation.seq.ChainReference;
import com.dlong.creeper.annotation.seq.RequestChain;
import com.dlong.creeper.annotation.seq.SeqRequest;
import com.dlong.creeper.control.MoveAction;
import com.dlong.creeper.control.MoveActions;
import com.dlong.creeper.execution.ChainContextExecutor;
import com.dlong.creeper.execution.context.ContextParamStore;
import com.dlong.creeper.model.seq.RequestChainEntity;
import demo.cocodiy.model.Cat;
import demo.cocodiy.service.CatService;

import java.io.IOException;
import java.util.List;

@RequestChain
@Host(value = "www.cocodiy.com",scheme = "https")
public class CocodiyChain {

    @SeqRequest(index=1,description = "获取所有子类别")
    public MoveAction getPathCats(String result, ContextParamStore contextParamStore) throws IOException {
        List<Cat> leafCats = new CatService().resolveLeafCats(result);
        contextParamStore.addParam("leafCats",leafCats);
        return MoveActions.FORWARD();
    }

    @ChainReference(index = 2,description = "遍历所有子类")
    LeafCatChain leafCatChain;

    public static void main(String[] args) throws IOException {
        ChainContextExecutor contextExecutor = new ChainContextExecutor(CocodiyChain.class);
        RequestChainEntity rootChain = contextExecutor.getChainContext().getRootChain();
        contextExecutor.exeucteRootChain();
    }
}
