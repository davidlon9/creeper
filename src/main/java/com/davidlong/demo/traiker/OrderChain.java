package com.davidlong.demo.traiker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.davidlong.creeper.HttpUtil;
import com.davidlong.creeper.annotation.*;
import com.davidlong.creeper.annotation.control.looper.While;
import com.davidlong.creeper.annotation.handler.AfterMethod;
import com.davidlong.creeper.annotation.handler.BeforeMethod;
import com.davidlong.creeper.annotation.http.Get;
import com.davidlong.creeper.annotation.http.Post;
import com.davidlong.creeper.annotation.seq.RequestChain;
import com.davidlong.creeper.annotation.seq.SeqRequest;
import com.davidlong.creeper.control.*;
import com.davidlong.creeper.execution.RequestChainExecutor;
import com.davidlong.creeper.execution.context.ContextParamStore;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.execution.context.FormParamStore;
import com.davidlong.creeper.model.ChainExecutionResult;
import com.davidlong.creeper.model.ExecutionResult;
import com.davidlong.creeper.model.Param;
import com.davidlong.creeper.model.seq.RequestChainEntity;
import com.davidlong.creeper.resolver.ChainsMappingResolver;
import com.davidlong.creeper.util.ResultUtil;
import com.davidlong.demo.traiker.model.LeftTicketDesc;
import com.davidlong.demo.traiker.model.Passenger;
import com.davidlong.demo.traiker.model.StationDesc;
import com.davidlong.demo.traiker.model.TicketInfo;
import com.davidlong.demo.traiker.resovle.DomResolve;
import com.davidlong.demo.traiker.resovle.LeftTicketResovler;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Host(value="kyfw.12306.cn",scheme="https")
@RequestChain(index =1,name="OrderChain",description="订单请求链")
@ResponseLog(showResult = false,showSetCookies = false)
@RequestLog(showFilledParams = true,showFilledHeaders = true)
public class OrderChain {
    private static Logger logger=Logger.getLogger(OrderChain.class);
    private static Random random=new Random();

    @SeqRequest(index =1,name="loginConf",description="获取登陆必需Cookie")
    @Post("/otn/login/conf")
    public boolean loginConf(CookieStore cookieStore) throws IOException {
        return true;
    }

    //        dc: "单程",
    @While()
    @SeqRequest(index =2,name="leftTicket",description="查询余票")
    @Get("/otn/leftTicket/queryO")
    @Parameters({
            @Parameter(name="leftTicketDTO.train_date",desc = "日期"),//dc
            @Parameter(name="leftTicketDTO.from_station",desc = "出发站"),//武汉,WHN
            @Parameter(name="leftTicketDTO.to_station",desc = "到达站"),//杭州,WHN
            @Parameter(name="purpose_codes",value = "ADULT")
    })
    @RequestLog(showFilledParams = false,showUrl = false)
    @ResponseLog(showResult = true)
    public MoveAction leftTicket(JSONObject result, HttpResponse response, ContextParamStore contextParamStore, FormParamStore formParamStore) throws IOException {
        if(HttpUtil.ContentType.isJson(response)){
            JSONObject body = ResultUtil.getJsonBody(result);
            JSONArray leftArr = body.getJSONObject("data").getJSONArray("result");
            TicketInfo ticketInfo = LeftTicketResovler.getBestSeatLeftTrain(leftArr,UserConfig.acceptTicketType);
            if(ticketInfo.getSecretStr()!=null){
                logger.info("found train ["+ticketInfo.getTrainCode()+"]:"+ticketInfo.getSecretStr());
                formParamStore.addParam("secretStr", URLDecoder.decode(ticketInfo.getSecretStr()));
                contextParamStore.addParam("ticketInfo",ticketInfo);
                return new ForwardAction();
            }else{
                logger.info("no acceptable train found, retrying");
                return new RetryAction((random.nextInt(3)+1) * 1000);
            }
        }else{
            logger.error("页面错误");
        }
        return new TerminateAction(true);
    }

    @SeqRequest(index =3,name="checkUser",description="提交订单前验证用户")
    @Post("/otn/login/checkUser")
    @RequestHeaders({
            @RequestHeader(name="Cache-Control",value = "no-cache"),
            @RequestHeader(name="If-Modified-Since",value = "0")
    })
    public boolean checkUser(JSONObject result){
        return true;
    }

    @SeqRequest(index =4,name="submitOrderRequest",description="填写订单乘车人信息")
    @Post("/otn/leftTicket/submitOrderRequest")
    @Parameters({
            @Parameter(name="undefined",value = ""),
            @Parameter(name="purpose_codes",desc = "成人票/学生票"),
            @Parameter(name="secretStr"),
            @Parameter(name="train_date",globalKey = "leftTicketDTO.train_date",desc = "出发日"),
            @Parameter(name="back_train_date",value = "2020-02-21", desc = "返程日"),
            @Parameter(name="tour_flag",desc = "单程dc/往返wc"),
            @Parameter(name="query_from_station_name",value = "衢州",desc = "出发城市"),
            @Parameter(name="query_to_station_name",value = "杭州",desc = "到达城市")
    })
    @RequestHeaders({
            @RequestHeader(name="Content-Type",value = "application/x-www-form-urlencoded; charset=UTF-8"),
            @RequestHeader(name="X-Requested-With",value = "XMLHttpRequest"),
            @RequestHeader(name="Referer",value = "https://kyfw.12306.cn/otn/leftTicket/init"),
            @RequestHeader(name="Accept-Encoding",value = "gzip, deflate, br"),
            @RequestHeader(name="Host",value = "kyfw.12306.cn"),
    })
    public boolean submitOrderRequest(JSONObject result,HttpResponse response,FormParamStore formParamStore,CookieStore cookieStore) throws IOException {
        JSONObject body = ResultUtil.getJsonBody(result);
        Boolean status = body.getBoolean("status");
        if (!status) {
            logger.error(body.getString("messages"));
            return false;
        }
        return true;
    }

    //{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"notify_for_gat":"","isExist":true,"exMsg":"","two_isOpenClick":["93","95","97","99"],"other_isOpenClick":["91","93","98","99","95","97"],"normal_passengers":[{"code":"1","passenger_name":"龙英杰","sex_code":"M","sex_name":"男","born_date":"1997-07-14 00:00:00","country_code":"CN","passenger_id_type_code":"1","passenger_id_type_name":"中国居民身份证","passenger_id_no":"420606199707142054","passenger_type":"1","passenger_flag":"0","passenger_type_name":"成人","mobile_no":"18458152573","phone_no":"","email":"744941378@qq.com","address":"","postalcode":"","first_letter":"LYJ","recordCount":"3","total_times":"99","index_id":"0","gat_born_date":"","gat_valid_date_start":"","gat_valid_date_end":"","gat_version":""},{"code":"2","passenger_name":"潘杭琳","sex_code":"M","sex_name":"男","born_date":"1900-01-01 00:00:00","country_code":"CN","passenger_id_type_code":"1","passenger_id_type_name":"中国居民身份证","passenger_id_no":"330726197401100011","passenger_type":"1","passenger_flag":"0","passenger_type_name":"成人","mobile_no":"","phone_no":"","email":"","address":"","postalcode":"","first_letter":"PHL","recordCount":"3","total_times":"99","index_id":"1","gat_born_date":"","gat_valid_date_start":"","gat_valid_date_end":"","gat_version":""},{"code":"3","passenger_name":"宋文婷","sex_code":"F","sex_name":"女","born_date":"1900-01-01 00:00:00","country_code":"CN","passenger_id_type_code":"1","passenger_id_type_name":"中国居民身份证","passenger_id_no":"420606199612102041","passenger_type":"1","passenger_flag":"0","passenger_type_name":"成人","mobile_no":"15071406778","phone_no":"","email":"","address":"","postalcode":"","first_letter":"SW-","recordCount":"3","total_times":"99","index_id":"2","gat_born_date":"","gat_valid_date_start":"","gat_valid_date_end":"","gat_version":""}],"dj_passengers":[]},"messages":[],"validateMessages":{}}
    @SeqRequest(index =5,name="getRepeatSubmitToken",description="获取repeatToken")
    @Post("/otn/confirmPassenger/initDc")
    @Parameters({
            @Parameter(name="_json_att",value = "")
    })
    @RequestHeaders({
            @RequestHeader(name="Content-Type",value = "application/x-www-form-urlencoded; charset=UTF-8"),
            @RequestHeader(name="X-Requested-With",value = "XMLHttpRequest"),
            @RequestHeader(name="Host",value = "kyfw.12306.cn"),
    })
    @ResponseLog(showResult = false)
    public boolean getRepeatSubmitToken(HttpResponse response,FormParamStore formParamStore) throws IOException {
        String html = EntityUtils.toString(response.getEntity());
        Document parse = Jsoup.parse(html);
        Elements script = parse.getElementsByTag("script");

        String repeatSubmitToken = DomResolve.getRepeatSubmitToken(script);
        JSONObject formJson = DomResolve.getKeyCheckIsChange(script);
        if (repeatSubmitToken!=null && formJson!=null){
            formParamStore.addParam("REPEAT_SUBMIT_TOKEN",repeatSubmitToken);
            formParamStore.addParam("key_check_isChange",formJson.getString("key_check_isChange"));
//            formParamStore.addParam("leftTicket",formJson.getString("leftTicketStr"));

            logger.info("repeatSubmitToken:"+repeatSubmitToken);
            logger.info("keyCheckIsChange:"+formJson.getString("key_check_isChange"));
            return true;
        }
        logger.error("无法找到repeatSubmitToken与keyCheckIsChange");
        return false;
    }

    //{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"notify_for_gat":"","isExist":true,"exMsg":"","two_isOpenClick":["93","95","97","99"],"other_isOpenClick":["91","93","98","99","95","97"],"normal_passengers":[{"code":"1","passenger_name":"龙英杰","sex_code":"M","sex_name":"男","born_date":"1997-07-14 00:00:00","country_code":"CN","passenger_id_type_code":"1","passenger_id_type_name":"中国居民身份证","passenger_id_no":"420606199707142054","passenger_type":"1","passenger_flag":"0","passenger_type_name":"成人","mobile_no":"18458152573","phone_no":"","email":"744941378@qq.com","address":"","postalcode":"","first_letter":"LYJ","recordCount":"3","total_times":"99","index_id":"0","gat_born_date":"","gat_valid_date_start":"","gat_valid_date_end":"","gat_version":""},{"code":"2","passenger_name":"潘杭琳","sex_code":"M","sex_name":"男","born_date":"1900-01-01 00:00:00","country_code":"CN","passenger_id_type_code":"1","passenger_id_type_name":"中国居民身份证","passenger_id_no":"330726197401100011","passenger_type":"1","passenger_flag":"0","passenger_type_name":"成人","mobile_no":"","phone_no":"","email":"","address":"","postalcode":"","first_letter":"PHL","recordCount":"3","total_times":"99","index_id":"1","gat_born_date":"","gat_valid_date_start":"","gat_valid_date_end":"","gat_version":""},{"code":"3","passenger_name":"宋文婷","sex_code":"F","sex_name":"女","born_date":"1900-01-01 00:00:00","country_code":"CN","passenger_id_type_code":"1","passenger_id_type_name":"中国居民身份证","passenger_id_no":"420606199612102041","passenger_type":"1","passenger_flag":"0","passenger_type_name":"成人","mobile_no":"15071406778","phone_no":"","email":"","address":"","postalcode":"","first_letter":"SW-","recordCount":"3","total_times":"99","index_id":"2","gat_born_date":"","gat_valid_date_start":"","gat_valid_date_end":"","gat_version":""}],"dj_passengers":[]},"messages":[],"validateMessages":{}}
    @SeqRequest(index =6,name="getPassengerDTOs",description="获取乘车人信息")
    @Post("/otn/confirmPassenger/getPassengerDTOs")
    @Parameters({
            @Parameter(name="_json_att",value = ""),
            @Parameter(name="REPEAT_SUBMIT_TOKEN"),
    })
    @RequestHeaders({
            @RequestHeader(name="Content-Type",value = "application/x-www-form-urlencoded; charset=UTF-8"),
            @RequestHeader(name="X-Requested-With",value = "XMLHttpRequest"),
            @RequestHeader(name="Host",value = "kyfw.12306.cn"),
    })
    @ResponseLog(showResult = true)
    public boolean getPassengerDtos(JSONObject result,HttpResponse response,ContextParamStore contextParamStore,FormParamStore formParamStore){
        if(HttpUtil.ContentType.isJson(response)){
            JSONObject body = ResultUtil.getJsonBody(result);
            JSONObject data = body.getJSONObject("data");
            JSONArray passengers = data.getJSONArray("normal_passengers");
            if(data.size()>0){
                for (Object o : passengers) {
                    JSONObject passenger =( JSONObject) o;
                    String passenger_name = passenger.getString("passenger_name");
                    if(UserConfig.passengerNames.contains(passenger_name)){
                        Passenger pdto = new Passenger();
                        TicketInfo ticketInfo = (TicketInfo) contextParamStore.getValue("ticketInfo");
                        pdto.setSeatType(ticketInfo.getSeatType());
                        pdto.setPassengerName(passenger_name);
                        pdto.setMobileNo(passenger.getString("mobile_no"));
                        pdto.setPassengerIdNo(passenger.getString("passenger_id_no"));
                        pdto.setPassengerIdTypeCode(passenger.getString("passenger_id_type_code"));
                        pdto.setPassengerType(passenger.getString("passenger_type"));
                        pdto.setAllEncStr(passenger.getString("allEncStr"));
                        String passengerTicketStr = pdto.getPassengerTicketStr();
                        String oldPassengerStr = pdto.getOldPassengerStr();

                        formParamStore.addParam("passengerTicketStr",passengerTicketStr);
                        formParamStore.addParam("oldPassengerStr",oldPassengerStr);

                        logger.info("添加乘客:"+ passengerTicketStr);
                        return true;
                    }
                }
            }
        }else{
            logger.error("页面错误");
        }
        return false;
    }

    @SeqRequest(index =7,name="checkOrderInfo",description="提交并检查订单信息")
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
            @Parameter(name="scene",value = "nc_login"),
            @Parameter(name="REPEAT_SUBMIT_TOKEN")
    })
    @RequestHeaders({
            @RequestHeader(name="Content-Type",value = "application/x-www-form-urlencoded; charset=UTF-8"),
            @RequestHeader(name="Host",value = "kyfw.12306.cn"),
    })
    //TODO 可跳过
    public MoveAction checkOrderInfo(JSONObject result, HttpResponse response, ContextParamStore contextParamStore, FormParamStore formParamStore){
        if(HttpUtil.ContentType.isJson(response)){
            JSONObject body = ResultUtil.getJsonBody(result);
            JSONObject data = body.getJSONObject("data");
            if(data.getBoolean("submitStatus")){
                TicketInfo ticketInfo = (TicketInfo) contextParamStore.getValue("ticketInfo");
                formParamStore.addParam("train_no",ticketInfo.getTrainNo());
                formParamStore.addParam("stationTrainCode",ticketInfo.getTrainCode());
                formParamStore.addParam("train_location",ticketInfo.getTrainLocation());
                formParamStore.addParam("seatType",ticketInfo.getSeatType());
                formParamStore.addParam("leftTicket",ticketInfo.getYpInfo());
                return MoveActions.FORWARD();
            }else{
                String errMsg = data.getString("errMsg");
                logger.error(errMsg+"   重新尝试抢票");
                return MoveActions.JUMP("leftTicket");
            }
        }else{
            logger.error("页面错误");
            return MoveActions.TERMINATE(true);
        }
    }

    //{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"count":"48","ticket":"19","op_2":"false","countT":"0","op_1":"true"},"messages":[],"validateMessages":{}}
    @SeqRequest(index =8,name="getQueueCount",description="获取队列数量")
    @Post("/otn/confirmPassenger/getQueueCount")
    @Parameters({
            @Parameter(name="_json_att"),
            @Parameter(name="train_date"),//Date类型 Sat Apr 27 2019 00:00:00 GMT+0800 (中国标准时间)
            @Parameter(name="train_no",desc="火车ID"),//56000D222260
            @Parameter(name="stationTrainCode",desc = "火车代码"),//D2222
            @Parameter(name="seatType",desc="座位类型"),//M
            @Parameter(name="fromStationTelecode",globalKey = "leftTicketDTO.from_station",desc = "起始站代码"),//HGH
            @Parameter(name="toStationTelecode",globalKey = "leftTicketDTO.to_station",desc = "到达站代码"),//HKN
            @Parameter(name="leftTicket"),
            @Parameter(name="purpose_codes",value = "00"),
            @Parameter(name="train_location",desc = "火车位置"),//LocationCode
            @Parameter(name="REPEAT_SUBMIT_TOKEN")
    })
    @RequestHeaders({
            @RequestHeader(name="Content-Type",value = "application/x-www-form-urlencoded; charset=UTF-8"),
            @RequestHeader(name="X-Requested-With",value = "XMLHttpRequest"),
            @RequestHeader(name="Host",value = "kyfw.12306.cn"),
    })
    public MoveAction getQueueCount(JSONObject result, HttpResponse response, ContextParamStore contextParamStore, FormParamStore formParamStore){
        if(HttpUtil.ContentType.isJson(response)){
            JSONObject body = ResultUtil.getJsonBody(result);
            JSONObject data = body.getJSONObject("data");
            String[] split = data.getString("ticket").split(",");
            int leftTicket = Integer.parseInt(split[0]);
            if(leftTicket>0){
                logger.info("剩余"+leftTicket+"张票");
                String passengerTicketStr = formParamStore.getValue("passengerTicketStr");
                String oldPassengerStr = formParamStore.getValue("oldPassengerStr");
//                try {
//                    formParamStore.addParam("passengerTicketStr", URLEncoder.encode(passengerTicketStr,"utf-8"));
//                    formParamStore.addParam("oldPassengerStr", URLEncoder.encode(oldPassengerStr,"utf-8"));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                return MoveActions.FORWARD();
            }else{
                String errMsg = data.getString("errMsg");
                logger.error("余票为0，3秒后尝试重新抢票");
                return new JumpAction("leftTicket",3000);
            }
        }else{
            logger.error("页面错误");
            return MoveActions.TERMINATE(true);
        }
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
    @SeqRequest(index =9,name="confirmSingleForQueue",description="队列中确认单人订单")
    @Post("/otn/confirmPassenger/confirmSingleForQueue")
    @Parameters({
            @Parameter(name="passengerTicketStr"),
            @Parameter(name="oldPassengerStr"),
            @Parameter(name="randCode"),
            @Parameter(name="purpose_codes"),
            @Parameter(name="key_check_isChange"),
            @Parameter(name="leftTicketStr", globalKey = "leftTicket"),
            @Parameter(name="train_location"),
            @Parameter(name="choose_seats",value=""),
            @Parameter(name="seatDetailType",value="000"),
            @Parameter(name="whatsSelect"),
            @Parameter(name="roomType",value = "00"),
            @Parameter(name="dwAll",value = "N"),
            @Parameter(name="_json_att"),
            @Parameter(name="REPEAT_SUBMIT_TOKEN")
    })
    @RequestHeaders({
            @RequestHeader(name="Content-Type",value = "application/x-www-form-urlencoded; charset=UTF-8"),
//            @RequestHeader(name="Accept",value = "application/json, text/javascript, */*; q=0.01"),
//            @RequestHeader(name="Accept-Encoding",value = "gzip, deflate, br"),
//            @RequestHeader(name="Accept-Language",value = "zh-CN,zh;q=0.9"),
////            @RequestHeader(name="Referer",value = "https://kyfw.12306.cn/otn/confirmPassenger/initDc"),
//            @RequestHeader(name="Origin",value = "https://kyfw.12306.cn"),
//            @RequestHeader(name="Connection",value = "keep-alive"),
//            @RequestHeader(name="Host",value = "kyfw.12306.cn"),
////            @RequestHeader(name="Sec-Fetch-Dest",value = "empty"),
//            @RequestHeader(name="Sec-Fetch-Site",value = "same-origin"),
//            @RequestHeader(name="Sec-Fetch-Mode",value = "cors"),
            @RequestHeader(name="X-Requested-With",value = "XMLHttpRequest")
    })
    @AfterMethod("confirmSingleForQueue")
    public MoveAction confirmSingleForQueue(JSONObject result, HttpResponse response, ContextParamStore contextParamStore, FormParamStore formParamStore){
        JSONObject body = ResultUtil.getJsonBody(result);
        return body.getJSONObject("data").getBoolean("submitStatus")?new ForwardAction():new RetryAction(0);
    }

    @BeforeMethod("confirmSingleForQueue")
    public boolean beforeConfirm(Request request,CookieStore cookieStore){

        return true;
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

    @While()
    @SeqRequest(index =10,name="queryOrderWaitTime",description="等待请求订单")
//{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"queryOrderWaitTimeStatus":true,"count":0,"waitTime":10,"requestId":6526815027482755960,"waitCount":0,"tourFlag":"dc","orderId":null},"messages":[],"validateMessages":{}}
//{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"queryOrderWaitTimeStatus":true,"count":0,"waitTime":-1,"requestId":6526815027482755960,"waitCount":0,"tourFlag":"dc","orderId":"E538737298"},"messages":[],"validateMessages":{}}
//     购买冲突异常  {"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"queryOrderWaitTimeStatus":true,"count":0,"waitTime":-2,"requestId":6527086556263121784,"waitCount":0,"tourFlag":"dc","errorcode":"0","msg":"龙英杰(二代身份证-420606199707142054)存在与本次购票行程冲突的车票!!","orderId":null},"messages":[],"validateMessages":{}}
    @Get("/otn/confirmPassenger/queryOrderWaitTime")
    @Parameters({
            @Parameter(name="_json_att"),
            @Parameter(name="random",value="${time.now()}"),//毫秒时间戳
            @Parameter(name="tourFlag",globalKey = "tour_flag"),
            @Parameter(name="REPEAT_SUBMIT_TOKEN")
    })
    public MoveAction queryOrderWaitTime(JSONObject result, HttpResponse response){
        return new RetryAction(1000);
    }


    @SeqRequest(index =11,name="resultOrderForDCQueue",description="请求订单的结果")
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

    public static void main(String[] args) {

        Map<String, String> stationCodeMap = StationDesc.stationCodeMap;
        //登陆
        ChainExecutionResult<RequestChainEntity> loginResult = (ChainExecutionResult<RequestChainEntity>) LoginChain.execute();
        RequestChainEntity requestChainEntity = new ChainsMappingResolver(OrderChain.class).resolve();

        List<Param> startParam=new ArrayList<>();
        String fromStation="衢州";
        String toStation="杭州";
        String fromCode=stationCodeMap.get(fromStation);
        String toCode=stationCodeMap.get(toStation);
        String date="2020-02-28";
        startParam.add(new Param("leftTicketDTO.train_date",date));
        startParam.add(new Param("leftTicketDTO.from_station",fromCode));
        startParam.add(new Param("leftTicketDTO.to_station",toCode));
        startParam.add(new Param("tour_flag","dc"));
        startParam.add(new Param("train_date", LeftTicketDesc.getTrainDate(date)));

        ExecutionContext context = new ExecutionContext(requestChainEntity, startParam, loginResult.getContext());
        ContextParamStore contextStore = context.getContextStore();

        RequestChainExecutor requestChainExecutor = new RequestChainExecutor(context);
        ExecutionResult<RequestChainEntity> execute = requestChainExecutor.execute();
        System.out.println();
    }

}
