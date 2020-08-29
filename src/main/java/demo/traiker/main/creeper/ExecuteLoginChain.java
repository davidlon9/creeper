package demo.traiker.main.creeper;

import com.dlong.creeper.execution.ChainContextExecutor;
import com.dlong.creeper.execution.base.ContextExecutor;

public class ExecuteLoginChain {
    public static void main(String[] args) {
        //创建一个ChainContextExecutor，并传入Chain配置类
        ContextExecutor executor = new ChainContextExecutor(LoginChainSimple.class);
        //执行Chain
        executor.exeucteRootChain();
    }
}
