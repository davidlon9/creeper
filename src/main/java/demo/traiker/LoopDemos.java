package demo.traiker;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dlong.creeper.annotation.*;
import com.dlong.creeper.annotation.control.FailedRetry;
import com.dlong.creeper.annotation.control.looper.ForEach;
import com.dlong.creeper.annotation.control.looper.ForIndex;
import com.dlong.creeper.annotation.control.looper.While;
import com.dlong.creeper.annotation.control.looper.scheduler.Scheduler;
import com.dlong.creeper.annotation.control.looper.scheduler.Trigger;
import com.dlong.creeper.annotation.control.recorder.FileRecordsIgnore;
import com.dlong.creeper.annotation.handler.AfterMethod;
import com.dlong.creeper.annotation.handler.BeforeMethod;
import com.dlong.creeper.annotation.http.Get;
import com.dlong.creeper.annotation.seq.RequestChain;
import com.dlong.creeper.annotation.seq.SeqRequest;
import com.dlong.creeper.control.ContinueAction;
import com.dlong.creeper.control.ForwardAction;
import com.dlong.creeper.control.MoveAction;
import com.dlong.creeper.control.MoveActions;
import com.dlong.creeper.execution.context.ContextParamStore;
import com.dlong.creeper.util.ResultUtil;
import demo.pdf.serivce.DZSWService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LoopDemos {
    //While Demo
    @While(conditionExpr = "${#loopNum < 10}")//循环直至loopNum>=10
    @SeqRequest(index =1,name="leftTicket",description="查询余票")
    @Get("/otn/leftTicket/query")
    @Parameters({
            @Parameter(name="leftTicketDTO.train_date",desc = "日期"),//dc
            @Parameter(name="leftTicketDTO.from_station",desc = "出发站"),//武汉,WHN
            @Parameter(name="leftTicketDTO.to_station",desc = "到达站"),//杭州,WHN
            @Parameter(name="purpose_codes",value = "ADULT")})
    public MoveAction leftTicket(String result, ContextParamStore contextParamStore){
        Integer loopNum = (Integer) contextParamStore.getValue("loopNum");
        loopNum+=1;
        contextParamStore.addParam("loopNum",loopNum);
        if(loopNum==5){//模拟抢到票
            return new ForwardAction();//跳出循环并执行下一请求
        }
        return new ContinueAction(1000);//继续循环
    }
    //ForEach Demo
    @BeforeMethod("handlePDFBookDetial")
    public boolean beforeHandlePDFBookDetial(ContextParamStore contextParamStore){
        Collection<String> urls = new HashSet<>();
        urls.add("http://detail1");
        urls.add("http://detail2");
        contextParamStore.addParam("pagePDFDetailUrls",urls);
        return true;
    }

    //itemsContextKey是ContextParamStore中的Collection接口的任意对象的key
    //itemName是当前遍历对象在ContextParamStore中的key
    @ForEach(itemsContextKey = "pagePDFDetailUrls", itemName = "detailUrl")
    @SeqRequest(index = 1, description = "处理详情页面")
    @Get(value = "${#detailUrl}", urlInheritable = false)
    //${#detailUrl}在每次循环中将会解析出不同的url，第一次是http://detail1，第二次是http://detail2
    @FileRecordsIgnore(filePath = "D:\\repository\\traiker\\records\\demo.txt")
    public MoveAction handlePDFBookDetial(String result, ContextParamStore contextParamStore) throws IOException {
        Object detailUrl = contextParamStore.getValue("detailUrl");//获取当前遍历的对象
        Document rootPage = Jsoup.parse(result);
        DZSWService.handlePDFDetail(rootPage, contextParamStore);//处理详情页面
        return new ContinueAction(100);
    }

    //ForIndex demo
    @RequestChain
    @Host(value = "www.xgv5.com", scheme = "https")
    public class PageHandleChain {
        @ForIndex(start = "1", end = "10",indexName = "index")//默认indexName为index，可以省略掉indexName = "index"
        @SeqRequest(index = 1, description = "处理列表页面")
        @Get("/category-30${#index==1?'':'_'+#index}.html")
        //index=1时的url[http://www.xgv5.com/category-30]
        //index=2时的url[http://www.xgv5.com/category-30_2]
        //index=3时的url[http://www.xgv5.com/category-30_3]
        //...
        public MoveAction handlePDFListBook(String result, ContextParamStore contextParamStore) {
            Document rootPage = Jsoup.parse(result);
            DZSWService.handlePDFListBook(rootPage, contextParamStore);
            return MoveActions.FORWARD();
        }
    }

    //Scheduler demo
    @Scheduler(
        trigger = @Trigger(
            startTimeExpr = "${time.now()}",//开始时间
            endTimeExpr = "${endTime}",//结束时间
            timeInterval = 1000,//每次执行间隔
            repeatCount = 10,//执行次数
            delay = 5000//延迟5秒执行
        )
    )
    @Host(value="kyfw.12306.cn",scheme="https")
    @RequestChain(name="LoginChain",description="登陆请求链")
    public class LoginChain {
        //省略序列请求
    }

    //Chain demo
}
