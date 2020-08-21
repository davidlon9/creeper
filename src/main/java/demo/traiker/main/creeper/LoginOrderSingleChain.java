package demo.traiker.main.creeper;

import com.alibaba.fastjson.JSONObject;
import com.davidlong.creeper.annotation.control.looper.ForIndex;
import com.davidlong.creeper.annotation.control.looper.While;
import com.davidlong.creeper.annotation.seq.RequestChain;
import com.davidlong.creeper.annotation.seq.SeqRequest;
import com.davidlong.creeper.control.*;
import com.davidlong.creeper.execution.RequestChainExecutor;
import com.davidlong.creeper.execution.context.ContextParamStore;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.execution.context.FormParamStore;
import com.davidlong.creeper.model.*;
import com.davidlong.creeper.model.result.ExecutionResult;
import com.davidlong.creeper.model.seq.RequestChainEntity;
import com.davidlong.creeper.resolver.ChainsMappingResolver;
import com.davidlong.creeper.annotation.*;
import com.davidlong.creeper.annotation.control.*;
import com.davidlong.creeper.annotation.http.Get;
import com.davidlong.creeper.annotation.http.Post;
import com.davidlong.creeper.annotation.result.JsonResultCookie;
import com.davidlong.creeper.annotation.result.JsonResultCookies;
import com.davidlong.creeper.util.ResultUtil;
import demo.traiker.resovler.CallbackParam;
import demo.traiker.model.StationDesc;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;

import java.io.IOException;
import java.util.*;

@Host(value="kyfw.12306.cn",scheme="https")
@RequestChain(index =1,name="AppChain",description="App")
public class LoginOrderSingleChain {

//    @RequestChain(jsonKey=1,name="LoginChain",description="登陆请求链")
//    public LoginChain loginChain;

    @RequestChain(index =1,name="LoginChain",description="登陆请求链")
    public class LoginChain {
        CallbackParam callbackParam=new CallbackParam();

        @ForIndex(start = "${#start}", end = "${#end}", executionMode = ExecutionMode.SEQUENTIAL)
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
        public Object deivceCookie(JSONObject result,HttpResponse httpResponse, FormParamStore paramStore,ContextParamStore contextParamStore, CookieStore cookieStore){
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
//            String s = EntityUtils.toString(response.getEntity());
//            if(!"".equals(s)){
//                s=s.substring(s.indexOf("(")+1,s.lastIndexOf(")"));
//                JSONObject jsonObject = JSONObject.parseObject(s);
//                String image = jsonObject.getString("image");
//                CaptchaImage.createImage(image, "C:\\Users\\74494\\Desktop\\a.png");
//            }
//            System.out.println("请输入正确图片编号");
////            Scanner scanner = new Scanner(System.in);
////            String imageIdxs = scanner.nextLine();
////            String answer = CaptchaImage.getAnswer(imageIdxs);
////
////            paramStore.addParam("answer",answer);
//            paramStore.addParams(callbackParam.getExtraParams());
            return true;
        }

        @SeqRequest(index =3,name="captchaCheck",description="检测验证码答案")
        @Get("/passport/captcha/captcha-check")
        @Parameters({
                @Parameter(name="login_site",value="E"),
                @Parameter(name="rand",value="sjrand"),
                @Parameter(name="answer"),
                @Parameter(name="callback"),
                @Parameter(name="_")})
        @FailedBack
        public boolean captchaCheck(HttpResponse response,FormParamStore paramStore,CookieStore cookieStore){
//            JSONObject result = ResultUtil.getResult(response);
//            JSONObject body = (JSONObject) ResultUtil.getBody(result);
//            String result_code = body.getString("result_code");
//            if("4".equals(result_code)){
//                return true;
//            }
            return false;
        }

        @SeqRequest(index =4,name="loginChain",description="登陆")
        @Post("/passport/web/loginChain")
        @Parameters({
                @Parameter(name="appid",value="otn"),
                @Parameter(name="usernameVal",value="qq744941378"),
                @Parameter(name="passwordVal",value="long123481"),
                @Parameter(name="answer")})
        public MoveAction login(JSONObject result){
            String statusCode = ResultUtil.getStatusCode(result);
            JSONObject body = (JSONObject) ResultUtil.getBody(result);
            System.out.println(body.getString("result_message"));

            String result_code = body.getString("result_code");
            if("0".equals(result_code)){
                return MoveActions.FORWARD();
            }
            return MoveActions.FORWARD();
        }


        @SeqRequest(index =5,name="uamtk",description="获取token")
        @Post("/passport/web/auth/uamtk")
        @Parameters({@Parameter(name="appid",value="otn")})
        @JsonResultCookie(jsonKey ="newapptk",name="tk",domain = "kyfw.12306.cn",path = "/otn")
        public void uamtk(HttpResponse response){
        }

        @SeqRequest(index =6,name="userinfo",description="获取用户信息")
        @Post("/otn/modifyUser/initQueryUserInfoApi")
        @Parameters({@Parameter(name="appid",value="otn")})
        public void userinfo(HttpResponse response){
        }
    }

//    @MultiRequestChain(jsonKey=2,name="OrderChain",description="订单请求链",threadSize = 5)
    @RequestChain(index =2,name="OrderChain",description="订单请求链")
    public class Order{
        //        dc: "单程",
        @While(coniditionExpression = "${#loopNum < 10}",executionMode = ExecutionMode.SEQUENTIAL)
        @SeqRequest(index =1,name="leftTicket",description="查询余票")
        @Get("/otn/leftTicket/query")
        @Parameters({
                @Parameter(name="leftTicketDTO.train_date",desc = "日期"),//dc
                @Parameter(name="leftTicketDTO.from_station",desc = "出发站"),//武汉,WHN
                @Parameter(name="leftTicketDTO.to_station",desc = "到达站"),//杭州,WHN
                @Parameter(name="purpose_codes",value = "ADULT")
        })
        @FailedRetry(interval = 1000)
        public MoveAction leftTicket(JSONObject result, ContextParamStore contextParamStore){
            JSONObject body = ResultUtil.getJsonBody(result);
            Integer loopNum = (Integer) contextParamStore.getValue("loopNum");
            loopNum+=1;
            System.out.println("loop to "+loopNum);
            contextParamStore.addParam("loopNum",loopNum);
            if(loopNum==5){
                return new ForwardAction();
            }
            //TODO nullpoint
//            Map<String, String> leftTicketByType = LeftTicketResovler.getAcceptLeftTicket(body);
//            if(leftTicketByType!=null){
//                System.out.println(leftTicketByType);
//            }
            return new RetryAction(1000);
        }
        //        wc: "往返",
//        fc: "返程",
//        gc: "改签",
//        lc: "连续换乘",
//        lc1: "连续换乘1",
//        lc2: "连续换乘2"
        //{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"flag":true},"messages":[],"validateMessages":{}}
        @SeqRequest(index =2,name="checkUser",description="提交订单前验证用户")
        @Get("/otn/loginChain/checkUser")
        @FailedRetry
        public boolean checkUser(JSONObject result){
            return false;
        }

        //{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":"N","messages":[],"validateMessages":{}}
        @SeqRequest(index =3,name="submitOrderRequest",description="填写订单乘车人信息")
        @Post("/otn/leftTicket/submitOrderRequest")
        @Parameters({
                @Parameter(name="undefined",value = ""),
                @Parameter(name="purpose_codes",desc = "成人票/学生票"),
                @Parameter(name="secretStr"),
                @Parameter(name="train_date", desc = "出发日"),
                @Parameter(name="back_train_date", desc = "返程日"),
                @Parameter(name="tour_flag", globalKey = "tourFlag" ,desc = "单程dc/往返wc"),
                @Parameter(name="query_from_station_name", desc = "出发城市"),
                @Parameter(name="query_to_station_name", desc = "到达城市")
        })
        public boolean submitOrderRequest(){
            return false;
        }

        //{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"notify_for_gat":"","isExist":true,"exMsg":"","two_isOpenClick":["93","95","97","99"],"other_isOpenClick":["91","93","98","99","95","97"],"normal_passengers":[{"code":"1","passenger_name":"龙英杰","sex_code":"M","sex_name":"男","born_date":"1997-07-14 00:00:00","country_code":"CN","passenger_id_type_code":"1","passenger_id_type_name":"中国居民身份证","passenger_id_no":"420606199707142054","passenger_type":"1","passenger_flag":"0","passenger_type_name":"成人","mobile_no":"18458152573","phone_no":"","email":"744941378@qq.com","address":"","postalcode":"","first_letter":"LYJ","recordCount":"3","total_times":"99","index_id":"0","gat_born_date":"","gat_valid_date_start":"","gat_valid_date_end":"","gat_version":""},{"code":"2","passenger_name":"潘杭琳","sex_code":"M","sex_name":"男","born_date":"1900-01-01 00:00:00","country_code":"CN","passenger_id_type_code":"1","passenger_id_type_name":"中国居民身份证","passenger_id_no":"330726197401100011","passenger_type":"1","passenger_flag":"0","passenger_type_name":"成人","mobile_no":"","phone_no":"","email":"","address":"","postalcode":"","first_letter":"PHL","recordCount":"3","total_times":"99","index_id":"1","gat_born_date":"","gat_valid_date_start":"","gat_valid_date_end":"","gat_version":""},{"code":"3","passenger_name":"宋文婷","sex_code":"F","sex_name":"女","born_date":"1900-01-01 00:00:00","country_code":"CN","passenger_id_type_code":"1","passenger_id_type_name":"中国居民身份证","passenger_id_no":"420606199612102041","passenger_type":"1","passenger_flag":"0","passenger_type_name":"成人","mobile_no":"15071406778","phone_no":"","email":"","address":"","postalcode":"","first_letter":"SW-","recordCount":"3","total_times":"99","index_id":"2","gat_born_date":"","gat_valid_date_start":"","gat_valid_date_end":"","gat_version":""}],"dj_passengers":[]},"messages":[],"validateMessages":{}}
        @SeqRequest(index =4,name="getPassengerDTOs",description="获取乘车人信息")
        @Post("/otn/confirmPassenger/getPassengerDTOs")
        @Parameters({
                @Parameter(name="_json_att",value = ""),
                @Parameter(name="REPEAT_SUBMIT_TOKEN"),
        })
        public boolean getPassengerDtos(){
            return false;
        }

        //{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"ifShowPassCode":"N","canChooseBeds":"N","canChooseSeats":"N","choose_Seats":"MOP9","isCanChooseMid":"N","ifShowPassCodeTime":"1","submitStatus":true,"smokeStr":""},"messages":[],"validateMessages":{}}
        @SeqRequest(index =5,name="checkOrderInfo",description="提交并检查订单信息")
        @Post("/otn/confirmPassenger/checkOrderInfo")
        @Parameters({
                @Parameter(name="_json_att"),
                @Parameter(name="cancel_flag",value = "2"),
                @Parameter(name="bed_level_order_num",value = "000000000000000000000000000000"),
                @Parameter(name="randCode",value = ""),
                @Parameter(name="whatsSelect",value = "1"),
                @Parameter(name="passengerTicketStr"),//3,0,1,龙英杰,1,420606199707142054,18458152573,N    seat_type,0,ticket_type,name,id_type,id_no,phone_no,save_status
                @Parameter(name="oldPassengerStr"),//龙英杰,1,420606199707142054,1_  name , id_type , id_no , ticket_type _
                @Parameter(name="tour_flag"),
                @Parameter(name="REPEAT_SUBMIT_TOKEN")
        })
        public boolean checkOrderInfo(){
            return false;
        }

        //{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"count":"48","ticket":"19","op_2":"false","countT":"0","op_1":"true"},"messages":[],"validateMessages":{}}
        @SeqRequest(index =6,name="getQueueCount",description="获取队列数量")
        @Post("/otn/confirmPassenger/getQueueCount")
        @Parameters({
                @Parameter(name="_json_att"),
                @Parameter(name="train_date"),//Date类型 Sat Apr 27 2019 00:00:00 GMT+0800 (中国标准时间)
                @Parameter(name="train_no",desc="火车ID"),//56000D222260
                @Parameter(name="stationTrainCode",desc = "火车代码"),//D2222
                @Parameter(name="seatType",desc="座位类型"),//M
                @Parameter(name="fromStationTelecode",desc = "起始站代码"),//HGH
                @Parameter(name="toStationTelecode",desc = "到达站代码"),//HKN
                @Parameter(name="leftTicket"),
                @Parameter(name="purpose_codes"),
                @Parameter(name="train_location",desc = "火车位置"),//LocationCode
                @Parameter(name="REPEAT_SUBMIT_TOKEN")
        })
        public boolean getQueueCount(){
            return false;
        }
//        train_date: new Date(orderRequestDTO.train_date.time).toString(),
//        train_no: orderRequestDTO.train_no,
//        stationTrainCode: orderRequestDTO.station_train_code,
//        seatType: limit_tickets[0].seat_type,
//        fromStationTelecode: orderRequestDTO.from_station_telecode,
//        toStationTelecode: orderRequestDTO.to_station_telecode,
//        leftTicket: ticketInfoForPassengerForm.queryLeftTicketRequestDTO.ypInfoDetail,
//        purpose_codes: ticketInfoForPassengerForm.purpose_codes,
//        train_location: ticketInfoForPassengerForm.train_location,


        //{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"count":"48","ticket":"19","op_2":"false","countT":"0","op_1":"true"},"messages":[],"validateMessages":{}}
        @SeqRequest(index =7,name="confirmSingleForQueue",description="队列中确认单人订单")
        @Post("/otn/confirmPassenger/confirmSingleForQueue")
        @Parameters({
                @Parameter(name="dwAll",value = "N"),
                @Parameter(name="choose_seats",value="false"),
                @Parameter(name="roomType",value = "00"),
                @Parameter(name="_json_att"),
                @Parameter(name="randCode"),
                @Parameter(name="whatsSelect"),
                @Parameter(name="passengerTicketStr"),
                @Parameter(name="oldPassengerStr"),
                @Parameter(name="key_check_isChange"),//413100DBFF5A1994BBDEEAC2C152E3C32A5CC4E8ACF7D3E6E56366C5
                @Parameter(name="train_location"),
                @Parameter(name="seatDetailType"),
                @Parameter(name="leftTicketStr", globalKey = "leftTicket"),
                @Parameter(name="purpose_codes"),
                @Parameter(name="REPEAT_SUBMIT_TOKEN")
        })
        public boolean confirmSingleForQueue(){
            return false;
        }
//        passengerTicketStr: getpassengerTickets(),
//        oldPassengerStr: getOldPassengers(),
//        randCode: $("#randCode").val(),
//        purpose_codes: ticketInfoForPassengerForm.purpose_codes,
//        key_check_isChange: ticketInfoForPassengerForm.key_check_isChange,
//        leftTicketStr: ticketInfoForPassengerForm.leftTicketStr,
//        train_location: ticketInfoForPassengerForm.train_location,
//        choose_seats: j(),
//        seatDetailType: h(),
//        whatsSelect: $.whatsSelect(true) ? "1" : "0"


//{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"queryOrderWaitTimeStatus":true,"count":0,"waitTime":10,"requestId":6526815027482755960,"waitCount":0,"tourFlag":"dc","orderId":null},"messages":[],"validateMessages":{}}
//{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"queryOrderWaitTimeStatus":true,"count":0,"waitTime":-1,"requestId":6526815027482755960,"waitCount":0,"tourFlag":"dc","orderId":"E538737298"},"messages":[],"validateMessages":{}}
//     购买冲突异常  {"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"queryOrderWaitTimeStatus":true,"count":0,"waitTime":-2,"requestId":6527086556263121784,"waitCount":0,"tourFlag":"dc","errorcode":"0","msg":"龙英杰(二代身份证-420606199707142054)存在与本次购票行程冲突的车票!!","orderId":null},"messages":[],"validateMessages":{}}
        @SeqRequest(index =8,name="queryOrderWaitTime",description="等待请求订单")
        @Get("/otn/confirmPassenger/queryOrderWaitTime")
        @Parameters({
                @Parameter(name="_json_att"),
                @Parameter(name="randCode"),
                @Parameter(name="random"),//毫秒时间戳
                @Parameter(name="tourFlag"),
                @Parameter(name="REPEAT_SUBMIT_TOKEN")
        })
        public boolean queryOrderWaitTime(){
            return false;
        }


        @SeqRequest(index =9,name="resultOrderForDCQueue",description="请求订单的结果")
        @Post("/otn/confirmPassenger/resultOrderForDcQueue")
        @Parameters({
                @Parameter(name="_json_att"),
                @Parameter(name="randCode"),
                @Parameter(name="orderSequence_no"),
                @Parameter(name="REPEAT_SUBMIT_TOKEN")
        })
        public boolean resultOrderForDCQueue(){
            return false;
        }

    }

    public static void main(String[] args) {
        Map<String, String> stationCodeMap = StationDesc.stationCodeMap;
        List<Param> startParam=new ArrayList<>();
        RequestChainEntity requestChainEntity = new ChainsMappingResolver(LoginOrderSingleChain.class).resolve();
//        RequestChainEntity loginChain = requestChainMap.get("LoginChain");
//        startParam.add(new Param("timestamp",new Date().getTime()));
//        BaseChainExecutor loginExecutor = new BaseChainExecutor(loginChain, startParam);
//        loginExecutor.loop();

        String fromStation="武汉";
        String toStation="杭州东";
        String fromCode=stationCodeMap.get(fromStation);
        String toCode=stationCodeMap.get(toStation);
        String date="2019-05-06";
        startParam.add(new Param("leftTicketDTO.train_date",date));
        startParam.add(new Param("leftTicketDTO.from_station",fromCode));
        startParam.add(new Param("leftTicketDTO.to_station",toCode));
        ExecutionContext executionContext = new ExecutionContext(requestChainEntity, startParam);
        ContextParamStore contextStore = executionContext.getContextStore();
        contextStore.addParam("start",1);
        contextStore.addParam("end",10);
        contextStore.addParam("loopNum",0);

        RequestChainExecutor requestChainExecutor = new RequestChainExecutor(executionContext);
        ExecutionResult<RequestChainEntity> execute = requestChainExecutor.execute();
        System.out.println();
//        RequestChainExecutor orderExecutor = new RequestChainExecutor(requestChainEntity, startParam);
//        ExecutionContext executionContext = orderExecutor.getExecutionContext();
//
//        orderExecutor.setSeqLimit(1);
//        orderExecutor.executeRequest();
    }

}
