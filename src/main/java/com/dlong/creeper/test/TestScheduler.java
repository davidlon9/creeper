package com.dlong.creeper.test;

import com.alibaba.fastjson.JSONObject;
import com.dlong.creeper.annotation.Host;
import com.dlong.creeper.annotation.Parameter;
import com.dlong.creeper.annotation.Parameters;
import com.dlong.creeper.annotation.control.ExecutionMode;
import com.dlong.creeper.annotation.control.FailedRetry;
import com.dlong.creeper.annotation.control.FailedTerminate;
import com.dlong.creeper.annotation.control.looper.While;
import com.dlong.creeper.annotation.handler.AfterMethod;
import com.dlong.creeper.annotation.handler.BeforeMethod;
import com.dlong.creeper.annotation.http.Get;
import com.dlong.creeper.annotation.seq.RequestChain;
import com.dlong.creeper.annotation.seq.SeqRequest;
import com.dlong.creeper.annotation.seq.multi.MultiRequest;
import com.dlong.creeper.annotation.seq.multi.MultiRequestChain;
import com.dlong.creeper.annotation.result.JsonResultCookie;
import com.dlong.creeper.annotation.result.JsonResultCookies;
import com.dlong.creeper.control.BreakAction;
import com.dlong.creeper.control.ContinueAction;
import com.dlong.creeper.control.MoveAction;
import com.dlong.creeper.execution.RequestChainExecutor;
import com.dlong.creeper.execution.context.ContextParamStore;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.execution.context.FormParamStore;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.RequestChainEntity;
import com.dlong.creeper.resolver.ChainsMappingResolver;
import com.dlong.creeper.util.ResultUtil;
import demo.traiker.model.StationDesc;
import demo.traiker.resovler.CallbackParam;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

@Host(value="kyfw.12306.cn",scheme="https")
@RequestChain(index =1,name="MainChain")
public class TestScheduler {
    private static int winCount=0;
    @While(executionMode = ExecutionMode.PARALLEL)
    @MultiRequestChain(index =1,name="OrderChain",description="订单请求链",threadSize=2)
    public class OrderChain {
        @BeforeMethod("OrderChain")
        public boolean chainBeforeHandler(ContextParamStore paramStore){
            System.out.println("执行了chainBeforeHandler");
            Map<String, String> stationCodeMap = StationDesc.stationCodeMap;

            String fromStation="武汉";
            String toStation="杭州东";
            String fromCode=stationCodeMap.get(fromStation);
            String toCode=stationCodeMap.get(toStation);
            String date="2019-05-06";
            paramStore.addParam("leftTicketDTO.train_date",date);
            paramStore.addParam("leftTicketDTO.from_station",fromCode);
            paramStore.addParam("leftTicketDTO.to_station",toCode);
            return true;
        }

        @AfterMethod("OrderChain")
        public MoveAction chainAfterHandler(){
            System.out.println("执行了chainAfterHandler");
            if(winCount>=3){
                System.out.println("赢了三次了，可以结束循环");
                return new BreakAction();
            }
            return new ContinueAction(100);
        }

        @MultiRequest(index =1,name="leftTicket",description="查询余票",threadSize=5)
        @While()
        @Get("/otn/leftTicket/query")
        @Parameters({
                @Parameter(name="leftTicketDTO.train_date",desc = "日期"),//dc
                @Parameter(name="leftTicketDTO.from_station",desc = "出发站"),//武汉,WHN
                @Parameter(name="leftTicketDTO.to_station",desc = "到达站"),//杭州,WHN
                @Parameter(name="purpose_codes",value = "ADULT")
        })
        @FailedRetry(interval = 1000)
        public boolean leftTicket(JSONObject result){
            JSONObject body = ResultUtil.getJsonBody(result);
            //TODO nullpoint
//            Map<String, String> leftTicketByType = LeftTicketResovler.getAcceptLeftTicket(body);
//            if(leftTicketByType!=null){
//                System.out.println(leftTicketByType);
//            }
            int random = new Random().nextInt(100);
            System.out.println(Thread.currentThread()+":"+random);
            if(random==1){
                winCount++;
                System.out.println(Thread.currentThread()+"win "+winCount+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                return true;
            }
            return false;
        }

        @SeqRequest(index =2,name="checkUser",description="提交订单前验证用户")
        @Get("/otn/loginChain/checkUser")
        @FailedRetry
        public boolean checkUser(JSONObject result){
            return true;
        }
    }

    @RequestChain(index =2,name="LoginChain",description="登陆请求链")
    public class LoginChain {
        CallbackParam callbackParam=new CallbackParam();

        @SeqRequest(index =1,name="deivceCookie",description="获取登陆必需Cookie")
        @Get("/otn/HttpZF/logdevice?algID=LV8VaeMQEV&hashCode=7ajVAXlEPHxJyTqvGceqr7AB3y1_l5XKpLo1JxnVHhw&FMQw=0&q4f3=zh-CN&VySQ=FGHxRx9RbokXS7ActSqH8dwSbIPTC0s8&VPIf=1&custID=133&VEek=unknown&dzuS=0&yD16=0&EOQP=4902a61a235fbb59700072139347967d&lEnu=3318022501&jp76=52d67b2a5aa5e031084733d5006cc664&hAqN=Win32&platform=WEB&ks0Q=d22ca0b81584fbea62237b14bd04c866&TeRS=824x1536&tOHY=24xx864x1536&Fvje=i1l1o1s1&q5aJ=-8&wNLf=99115dfb07133750ba677d055874de87&0aew=Mozilla/5.0%20(Windows%20NT%2010.0;%20Win64;%20x64)%20AppleWebKit/537.36%20(KHTML,%20like%20Gecko)%20Chrome/73.0.3683.103%20Safari/537.36&E3gR=0a91931ab794bb4ed8037488e7c471bc")
        @Parameters({
                @Parameter(name = "timestamp",value = "${time.now()}"),
                @Parameter(name = "index",value = "${#index}")
        })
        @JsonResultCookies({
                @JsonResultCookie(jsonKey ="dfp",name = "RAIL_DEVICEID",domain = ".12306.cn",cache = true),
                @JsonResultCookie(jsonKey ="exp",name = "RAIL_EXPIRATION",domain = ".12306.cn",cache = true)
        })
        @FailedTerminate
        public Object deivceCookie(JSONObject result, HttpResponse httpResponse, FormParamStore paramStore, ContextParamStore contextParamStore, CookieStore cookieStore){
            paramStore.addParams(callbackParam.getExtraParams());
            return true;
        }

        @SeqRequest(index =2,name="captchaImage",description="获取验证码图片")
        @Get("/passport/captcha/captcha-image64")
        @Parameters({
                @Parameter(name="login_site",value="E"),
                @Parameter(name="module",value="login"),
                @Parameter(name="rand",value="sjrand"),
                @Parameter(name="callback"),
                @Parameter(name="_")})
        public boolean captchaImage(HttpResponse response, FormParamStore paramStore,CookieStore cookieStore) throws IOException {
            return true;
        }
    }

    public static void main(String[] args) {

        RequestChainEntity requestChainEntity = new ChainsMappingResolver(TestScheduler.class).resolve();
//        RequestChainEntity loginChain = requestChainMap.get("LoginChain");
//        startParam.add(new Param("timestamp",new Date().getTime()));
//        BaseChainExecutor loginExecutor = new BaseChainExecutor(loginChain, startParam);
//        loginExecutor.loop();

//        Map<String, String> stationCodeMap = StationDesc.stationCodeMap;
//        List<Param> startParam=new ArrayList<>();
//        String fromStation="武汉";
//        String toStation="杭州东";
//        String fromCode=stationCodeMap.get(fromStation);
//        String toCode=stationCodeMap.get(toStation);
//        String date="2019-05-06";
//        startParam.add(new Param("leftTicketDTO.train_date",date));
//        startParam.add(new Param("leftTicketDTO.from_station",fromCode));
//        startParam.add(new Param("leftTicketDTO.to_station",toCode));
//        MultiChainExecutor chainExecutor = new MultiChainExecutor(new ExecutionContext(multiChainEntity));
//        ExecutionResult<MultiRequestChainEntity> execute = chainExecutor.execute(multiChainEntity);
        RequestChainExecutor executor = new RequestChainExecutor(new ExecutionContext(requestChainEntity));
        ExecutionResult<RequestChainEntity> excute = executor.execute();
        System.out.println();
//
//        orderExecutor.setSeqLimit(1);
//        orderExecutor.executeRequest();
    }

}
