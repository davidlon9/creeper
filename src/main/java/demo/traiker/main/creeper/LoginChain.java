package demo.traiker.main.creeper;

import com.alibaba.fastjson.JSONObject;
import com.dlong.creeper.HttpUtil;
import com.dlong.creeper.annotation.*;
import com.dlong.creeper.annotation.http.Get;
import com.dlong.creeper.annotation.http.Post;
import com.dlong.creeper.annotation.seq.RequestChain;
import com.dlong.creeper.annotation.seq.SeqRequest;
import com.dlong.creeper.annotation.result.JsonResultCookie;
import com.dlong.creeper.annotation.result.JsonResultCookies;
import com.dlong.creeper.control.MoveAction;
import com.dlong.creeper.control.MoveActions;
import com.dlong.creeper.execution.RequestChainExecutor;
import com.dlong.creeper.execution.context.*;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.RequestChainEntity;
import com.dlong.creeper.resolver.ChainsMappingResolver;
import com.dlong.creeper.util.ResultUtil;
import demo.traiker.Env;
import demo.traiker.model.StationDesc;
import demo.traiker.resovler.CallbackParam;
import demo.traiker.resovler.CaptchaImage;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

@Host(value="kyfw.12306.cn",scheme="https")
@RequestChain(index =1,name="LoginChain",description="登陆请求链")
public class LoginChain {
    private static Logger logger=Logger.getLogger(LoginChain.class);

    @SeqRequest(index =1,name="deviceCookie",description="获取登陆必需Cookie")
    @Get(Env.deivceCookieUrl)
    @Parameters({
            @Parameter(name = "timestamp",value = "${time.now()}"),
    })
    @JsonResultCookies({
            @JsonResultCookie(jsonKey ="dfp",name = "RAIL_DEVICEID",domain = ".12306.cn",cache = true),
            @JsonResultCookie(jsonKey ="exp",name = "RAIL_EXPIRATION",domain = ".12306.cn",cache = true)
    })
    public Object deivceCookie(JSONObject result,HttpResponse httpResponse, FormParamStore paramStore,ContextParamStore contextParamStore, CookieStore cookieStore) throws IOException {
        paramStore.addParams(new CallbackParam().getExtraParams());
        return true;
    }

    @SeqRequest(index =2,name="captchaImage",description="获取验证码图片")
    @Get("/passport/captcha/captcha-image64?login_site=E&module=login&rand=sjrand&${time.now()}")
    //login_site=E&module=login&rand=sjrand&1582180282215&callback=jQuery19106534370631095612_1582180275236&_=1582180275237
    @Parameters({
            @Parameter(name="callback"),
            @Parameter(name="_")})
    public boolean captchaImage(HttpResponse response, FormParamStore paramStore,CookieStore cookieStore) throws IOException {
        String s = EntityUtils.toString(response.getEntity());
        if(!"".equals(s) && HttpUtil.ContentType.isJson(response)){
            s=s.substring(s.indexOf("(")+1,s.lastIndexOf(")"));
            JSONObject jsonObject = JSONObject.parseObject(s);
            String image = jsonObject.getString("image");
            CaptchaImage.createImage(image, "C:\\Users\\74494\\Desktop\\a.png");
        }else{
            logger.error("返回异常\n"+s);
            return false;
        }
        logger.info("请输入正确图片编号");
        Scanner scanner = new Scanner(System.in);
        String imageIdxs = scanner.nextLine();
        String answer = CaptchaImage.getAnswer(imageIdxs);

        paramStore.addParam("answer",answer);
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
    public MoveAction captchaCheck(HttpResponse response, FormParamStore paramStore, CookieStore cookieStore){
        JSONObject result = ResultUtil.getResult(response);
        JSONObject body = (JSONObject) ResultUtil.getBody(result);
        String result_code = body.getString("result_code");
        if("4".equals(result_code)){
            return MoveActions.FORWARD();
        }else{
            logger.error("验证码错误，请重试");
        }
        return MoveActions.BACK(0);
    }

    @SeqRequest(index = 4,name = "login",description = "登陆")
    @Post("/passport/web/login")
    @Parameters({
            @Parameter(name="appid",value="otn"),
            @Parameter(name="username",value="qq744941378"),
            @Parameter(name="password",value="long123481"),
            @Parameter(name="answer")})
    public MoveAction login(HttpResponse response, JSONObject result) throws IOException {
        String statusCode = ResultUtil.getStatusCode(result);
        JSONObject body = (JSONObject) ResultUtil.getBody(result);
        String result_code = body.getString("result_code");
        if("0".equals(result_code)){
            logger.info(body.getString("result_message"));
            return MoveActions.FORWARD();
        }else{
            logger.error(body.getString("result_message")+"\n登陆失败，请重试");
            return MoveActions.JUMP("captchaImage");
        }
    }


    @SeqRequest(index =5,name="uamtk",description="获取token")
    @Post("/passport/web/auth/uamtk")
    @Parameters({
            @Parameter(name="callback"),
            @Parameter(name="appid",value="otn")
    })
    @RequestHeaders({
            @RequestHeader(name="Referer",value="https://kyfw.12306.cn/otn/passport?redirect=/otn/login/userLogin"),
            @RequestHeader(name="Origin",value="https://kyfw.12306.cn")
    })
    public boolean uamtk(FormParamStore paramStore, BasicCookieStore cookieStore, HttpResponse response, JSONObject result){
        if(HttpUtil.ContentType.isJson(response)){
            JSONObject body = (JSONObject) ResultUtil.getBody(result);
            paramStore.addParam("tk",body.getString("newapptk"));
            List<Cookie> cookies = cookieStore.getCookies();
            Cookie remove=null;
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("uamtk")) {
                    remove=cookie;
                    break;
                }
            }
            cookies.remove(remove);
            cookieStore.clear();
            cookieStore.addCookies(cookies.toArray(new Cookie[]{}));
        }else{
            logger.error("返回异常");
        }
//        var c = b.newapptk || b.apptk;
        return true;
    }

    @SeqRequest(index =6,name="uamauthclient",description="获取token")
    @Post("/otn/uamauthclient")
    @Parameters({
            @Parameter(name="tk"),
            @Parameter(name="_json_att",value = "")
    })
    public boolean uamauthclient(CookieStore cookieStore,HttpResponse httpResponse){
        return true;
    }

    @SeqRequest(index =7,name="userinfo",description="获取用户信息")
    @Post("/otn/modifyUser/initQueryUserInfoApi")
    @Parameters({@Parameter(name="appid",value="otn")})
    public boolean userinfo(HttpResponse response){
        return true;
    }

    public static ExecutionResult<RequestChainEntity> execute() {
        Map<String, String> stationCodeMap = StationDesc.stationCodeMap;
        RequestChainEntity requestChainEntity = new ChainsMappingResolver().resolve(LoginChain.class);
        ChainContext chainContext = new ChainContext(requestChainEntity);
        RequestChainExecutor requestChainExecutor = new RequestChainExecutor(chainContext);
        ExecutionResult<RequestChainEntity> result = requestChainExecutor.execute();
        return result;
    }

    public static void main(String[] args) {
        ExecutionResult<RequestChainEntity> execute = execute();
    }

}
