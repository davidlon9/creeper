package demo.traiker;

import com.davidlong.creeper.model.Param;
import com.davidlong.creeper.util.FastURI;
import demo.traiker.model.StaticFormContext;
import org.apache.http.NameValuePair;

import java.net.URI;
import java.util.*;

public class AppUrl {
    public static final URI HOST=new FastURI("kyfw.12306.cn").HTTPS().build();



    public static class Login{

        //@Path("/passport/captcha/captcha-image64")
        //@Parameters()
        //@Post
        //@JsonResultCookies
        public static final FastURI DEIVCE_COOKIE=new FastURI(HOST,"/otn/HttpZF/logdevice?algID=LV8VaeMQEV&hashCode=7ajVAXlEPHxJyTqvGceqr7AB3y1_l5XKpLo1JxnVHhw&FMQw=0&q4f3=zh-CN&VySQ=FGHxRx9RbokXS7ActSqH8dwSbIPTC0s8&VPIf=1&custID=133&VEek=unknown&dzuS=0&yD16=0&EOQP=4902a61a235fbb59700072139347967d&lEnu=3318022501&jp76=52d67b2a5aa5e031084733d5006cc664&hAqN=Win32&platform=WEB&ks0Q=d22ca0b81584fbea62237b14bd04c866&TeRS=824x1536&tOHY=24xx864x1536&Fvje=i1l1o1s1&q5aJ=-8&wNLf=99115dfb07133750ba677d055874de87&0aew=Mozilla/5.0%20(Windows%20NT%2010.0;%20Win64;%20x64)%20AppleWebKit/537.36%20(KHTML,%20like%20Gecko)%20Chrome/73.0.3683.103%20Safari/537.36&E3gR=0a91931ab794bb4ed8037488e7c471bc&timestamp=?");

        //获取验证码预定义URI
        public static final FastURI CAPTCHA_IMAGE=new FastURI(HOST,"/passport/captcha/captcha-image64").params(new Param("login_site","E"),new Param("module","login"),new Param("rand","sjrand"),
                new Param("callback","?"),
                new Param("_","?"));

        //获取验证码预定义URI
        public static final FastURI CAPTCHA_CHECK=new FastURI(HOST,"/passport/captcha/captcha-check").params(new Param("login_site","E"),new Param("rand","sjrand"),
                new Param("answer","?"),
                new Param("callback","?"),
                new Param("_","?"));

        //登陆预定义URI
        public static final FastURI LOGIN=new FastURI(HOST,"/passport/web/login").POST().params(new Param("appid","otn"),
                new Param("usernameVal","?"),
                new Param("passwordVal","?"),
                new Param("answer","?"));

        //获取token
        public static final FastURI UAMTK=new FastURI(HOST,"/passport/web/auth/uamtk").POST().param("appid","otn");

        //获取个人信息
        public static final FastURI USERINFO=new FastURI(HOST,"/otn/modifyUser/initQueryUserInfoApi").POST();
    }

    public static class Order{
//        dc: "单程",
//        wc: "往返",
//        fc: "返程",
//        gc: "改签",
//        lc: "连续换乘",
//        lc1: "连续换乘1",
//        lc2: "连续换乘2"
        //提交订单前验证用户
        //{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"flag":true},"messages":[],"validateMessages":{}}
        public static final FastURI CHECK_USER_1=new FastURI(HOST,"/otn/login/checkUser");

        //填写订单乘车人信息
        //{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":"N","messages":[],"validateMessages":{}}
        public static final FastURI SUBMIT_ORDER_REQUEST_2=new FastURI(HOST,"/otn/leftTicket/submitOrderRequest").POST().params(new Param("undefined",""),
                new Param("secretStr","?"),//
                new Param("train_date","?"),//出发日
                new Param("back_train_date","?"),//返程日
                new Param("tour_flag","?"),//单程 dc  往返 wc
                new Param("purpose_codes","ADULT"),//成人票 ADULT  学生票 0X00
                new Param("query_from_station_name","?"),//出发城市
                new Param("query_to_station_name","?"));//到达城市

        //获取乘车人信息
        //{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"notify_for_gat":"","isExist":true,"exMsg":"","two_isOpenClick":["93","95","97","99"],"other_isOpenClick":["91","93","98","99","95","97"],"normal_passengers":[{"code":"1","passenger_name":"龙英杰","sex_code":"M","sex_name":"男","born_date":"1997-07-14 00:00:00","country_code":"CN","passenger_id_type_code":"1","passenger_id_type_name":"中国居民身份证","passenger_id_no":"420606199707142054","passenger_type":"1","passenger_flag":"0","passenger_type_name":"成人","mobile_no":"18458152573","phone_no":"","email":"744941378@qq.com","address":"","postalcode":"","first_letter":"LYJ","recordCount":"3","total_times":"99","index_id":"0","gat_born_date":"","gat_valid_date_start":"","gat_valid_date_end":"","gat_version":""},{"code":"2","passenger_name":"潘杭琳","sex_code":"M","sex_name":"男","born_date":"1900-01-01 00:00:00","country_code":"CN","passenger_id_type_code":"1","passenger_id_type_name":"中国居民身份证","passenger_id_no":"330726197401100011","passenger_type":"1","passenger_flag":"0","passenger_type_name":"成人","mobile_no":"","phone_no":"","email":"","address":"","postalcode":"","first_letter":"PHL","recordCount":"3","total_times":"99","index_id":"1","gat_born_date":"","gat_valid_date_start":"","gat_valid_date_end":"","gat_version":""},{"code":"3","passenger_name":"宋文婷","sex_code":"F","sex_name":"女","born_date":"1900-01-01 00:00:00","country_code":"CN","passenger_id_type_code":"1","passenger_id_type_name":"中国居民身份证","passenger_id_no":"420606199612102041","passenger_type":"1","passenger_flag":"0","passenger_type_name":"成人","mobile_no":"15071406778","phone_no":"","email":"","address":"","postalcode":"","first_letter":"SW-","recordCount":"3","total_times":"99","index_id":"2","gat_born_date":"","gat_valid_date_start":"","gat_valid_date_end":"","gat_version":""}],"dj_passengers":[]},"messages":[],"validateMessages":{}}
        public static final FastURI GET_PASSENGER_DTOS_3=new FastURI(HOST,"/otn/confirmPassenger/getPassengerDTOs").POST().params(new Param("_json_att",""),
                new Param("REPEAT_SUBMIT_TOKEN","?"));

        //提交乘车人信息
        //{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"ifShowPassCode":"N","canChooseBeds":"N","canChooseSeats":"N","choose_Seats":"MOP9","isCanChooseMid":"N","ifShowPassCodeTime":"1","submitStatus":true,"smokeStr":""},"messages":[],"validateMessages":{}}
        public static final FastURI CHECK_ORDER_INFO_4=new FastURI(HOST,"/otn/confirmPassenger/checkOrderInfo").POST().params(new Param("cancel_flag","2"), new Param("bed_level_order_num","000000000000000000000000000000"),new Param("randCode",""),new Param("_json_att",""),new Param("whatsSelect","1"),
                new Param("passengerTicketStr","?"),//3,0,1,龙英杰,1,420606199707142054,18458152573,N    seat_type,0,ticket_type,name,id_type,id_no,phone_no,save_status
                new Param("oldPassengerStr","?"),//龙英杰,1,420606199707142054,1_  name , id_type , id_no , ticket_type _
                new Param("tour_flag","?"),
                new Param("REPEAT_SUBMIT_TOKEN","?"));//与上一步token相同

        //获取队列数量
        //{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"count":"48","ticket":"19","op_2":"false","countT":"0","op_1":"true"},"messages":[],"validateMessages":{}}
        public static final FastURI GET_QUEUE_COUNT_5=new FastURI(HOST,"/otn/confirmPassenger/getQueueCount").POST().params(new Param("_json_att",""),
                new Param("train_date","?"),//Date类型 Sat Apr 27 2019 00:00:00 GMT+0800 (中国标准时间)
                new Param("train_no","?"),//56000D222260火车号
                new Param("stationTrainCode","?"),//D2222火车代码
                new Param("seatType","?"),//座位类型M
                new Param("fromStationTelecode","?"),//HGH 起始站代码
                new Param("toStationTelecode","?"),//HKN 到达站代码
                new Param("leftTicket","?"),//leftTicketStr
                new Param("purpose_codes","?"),
                new Param("train_location","?"),//火车位置 LocationCode
                new Param("REPEAT_SUBMIT_TOKEN","?"));//与上一步token相同
//        train_date: new Date(orderRequestDTO.train_date.time).toString(),
//        train_no: orderRequestDTO.train_no,
//        stationTrainCode: orderRequestDTO.station_train_code,
//        seatType: limit_tickets[0].seat_type,
//        fromStationTelecode: orderRequestDTO.from_station_telecode,
//        toStationTelecode: orderRequestDTO.to_station_telecode,
//        leftTicket: ticketInfoForPassengerForm.queryLeftTicketRequestDTO.ypInfoDetail,
//        purpose_codes: ticketInfoForPassengerForm.purpose_codes,
//        train_location: ticketInfoForPassengerForm.train_location,


        //队列中确认单人
        public static final FastURI CONFIRM_SINGLE_FOR_QUEUE_6=new FastURI(HOST,"/otn/confirmPassenger/confirmSingleForQueue").POST().params(new Param("_json_att",""),new Param("randCode",""),new Param("whatsSelect","1"),new Param("choose_seats","false"),
                new Param("passengerTicketStr","?"),//与上一步相同
                new Param("oldPassengerStr","?"),//与上一步相同
                new Param("key_check_isChange","?"),//413100DBFF5A1994BBDEEAC2C152E3C32A5CC4E8ACF7D3E6E56366C5
                new Param("train_location","?"),//与上一步相同
                new Param("seatDetailType","?"),
                new Param("leftTicketStr","?"),//与上一步相同
                new Param("roomType","00"),
                new Param("purpose_codes","?"),//与上一步相同
                new Param("dwAll","N"),
                new Param("REPEAT_SUBMIT_TOKEN","?"));//与上一步token相同
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

        //等待请求订单
        //{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"queryOrderWaitTimeStatus":true,"count":0,"waitTime":10,"requestId":6526815027482755960,"waitCount":0,"tourFlag":"dc","orderId":null},"messages":[],"validateMessages":{}}
        //{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"queryOrderWaitTimeStatus":true,"count":0,"waitTime":-1,"requestId":6526815027482755960,"waitCount":0,"tourFlag":"dc","orderId":"E538737298"},"messages":[],"validateMessages":{}}
//     购买冲突异常  {"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"queryOrderWaitTimeStatus":true,"count":0,"waitTime":-2,"requestId":6527086556263121784,"waitCount":0,"tourFlag":"dc","errorcode":"0","msg":"龙英杰(二代身份证-420606199707142054)存在与本次购票行程冲突的车票!!","orderId":null},"messages":[],"validateMessages":{}}
        public static final FastURI QUERY_ORDER_WAIT_TIME_7=new FastURI(HOST,"/otn/confirmPassenger/queryOrderWaitTime").params(new Param("_json_att",""),new Param("randCode",""),
                new Param("random","?"),//毫秒时间戳
                new Param("tourFlag","?"),//与之前的相同
                new Param("REPEAT_SUBMIT_TOKEN","?"));//与上一步token相同

        //请求订单的结果
        //{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"submitStatus":true},"messages":[],"validateMessages":{}}
        public static final FastURI RESULT_ORDER_FOR_DC_QUEUE_8=new FastURI(HOST,"/otn/confirmPassenger/resultOrderForDcQueue").POST().params(new Param("_json_att",""),new Param("randCode",""),
                new Param("orderSequence_no","?"),//上一步返回的订单号
                new Param("REPEAT_SUBMIT_TOKEN","?"));//与上一步token相同

    }

    public static void main(String[] args) {
        URI long123481 = Login.LOGIN.paramValues("744941378", "long123481","12,121,515").build();
        URI build = Login.CAPTCHA_IMAGE.paramValues(System.currentTimeMillis(), System.currentTimeMillis()).build();
        List<FastURI> list=new ArrayList<>();
        
        list.add(Order.CHECK_USER_1);
        list.add(Order.SUBMIT_ORDER_REQUEST_2);
        list.add(Order.GET_PASSENGER_DTOS_3);
        list.add(Order.CHECK_ORDER_INFO_4);
        list.add(Order.GET_QUEUE_COUNT_5);
        list.add(Order.CONFIRM_SINGLE_FOR_QUEUE_6);
        list.add(Order.QUERY_ORDER_WAIT_TIME_7);
        list.add(Order.RESULT_ORDER_FOR_DC_QUEUE_8);

        Set<String> set=new TreeSet<>();
        for (FastURI fastURI : list) {
            List<NameValuePair> paramStore = fastURI.getParamStore();
            for (NameValuePair nameValuePair : paramStore) {
                set.add(nameValuePair.getName());
            }
        }
        set.stream().forEach(System.out::println);
        System.out.println();

        System.out.println("value:"+ StaticFormContext.getValue("_json_att"));
    }
}
