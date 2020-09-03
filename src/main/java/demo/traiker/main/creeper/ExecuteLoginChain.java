package demo.traiker.main.creeper;

import com.dlong.creeper.execution.ChainContextExecutor;
import com.dlong.creeper.execution.base.ContextExecutor;
import com.dlong.creeper.execution.context.ChainContext;

public class ExecuteLoginChain {
    public static void main(String[] args) {
//        //创建一个ChainContextExecutor，并传入Chain配置类
//        ContextExecutor executor = new ChainContextExecutor(LoginChainSimple.class);
//        //执行Chain
//        executor.exeucteRootChain();

        //第一个
        ContextExecutor executor = new ChainContextExecutor(LoginChainSimple.class);
        executor.exeucteRootChain();

        //获取LoginChainSimple的请求
        ChainContext chainContext = executor.getChainContext();
        ContextExecutor executor2 = new ChainContextExecutor(chainContext);
        //使用LoginChainSimple的上下文执行Chain
        executor2.exeucteRootChain();
    }
}
