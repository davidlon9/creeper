package demo.traiker.main.creeper;

import com.alibaba.fastjson.JSONObject;
import com.dlong.creeper.HttpUtil;
import com.dlong.creeper.annotation.*;
import com.dlong.creeper.annotation.http.Get;
import com.dlong.creeper.annotation.http.Post;
import com.dlong.creeper.annotation.result.JsonResultCookie;
import com.dlong.creeper.annotation.result.JsonResultCookies;
import com.dlong.creeper.annotation.seq.RequestChain;
import com.dlong.creeper.annotation.seq.SeqRequest;
import com.dlong.creeper.control.MoveAction;
import com.dlong.creeper.control.MoveActions;
import com.dlong.creeper.execution.context.ContextParamStore;
import com.dlong.creeper.execution.context.FormParamStore;
import com.dlong.creeper.util.ResultUtil;
import demo.traiker.Env;
import demo.traiker.resovler.CallbackParam;
import demo.traiker.resovler.CaptchaImage;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

//@RequestChain注解将当前类视为一个请求链配置类，该类中的所有序列请求，将会按顺序依次执行，并处理
@RequestChain(description="登陆请求链")
@Host(value="kyfw.12306.cn",scheme="https")
public class LoginChainSimple {

    //@SeqRequest注解将当前方法视为一个序列请求，方法体视为请求执行后的处理方法
    //当前序列请求的执行顺序为1
    @SeqRequest(index =1,description="获取登陆必需Cookie")
    @Get(Env.deivceCookieUrl)
    @Parameter(name = "timestamp",value = "${time.now()}")
    //使用下面注解，在请求执行过后，若返回的是Json类型的响应结果，将把Json结果中的指定值添加至CookieStore中
    @JsonResultCookies({
            //把Json结果中key为dfp的值，视为一个名为RAIL_DEVICEID的Cookie，并添加至CookieStore
            @JsonResultCookie(jsonKey ="dfp",name = "RAIL_DEVICEID",domain = ".12306.cn",cache = true),
            //把Json结果中key为exp的值，视为一个名为RAIL_EXPIRATION的Cookie，并添加至CookieStore
            @JsonResultCookie(jsonKey ="exp",name = "RAIL_EXPIRATION",domain = ".12306.cn",cache = true)
    })
    //FormParamStore中负责存储当前请求链执行过程中的请求参数，其中的参数会被自动注入到请求中
    public Object deivceCookie(FormParamStore paramStore) {
        //请求执行过后的处理
        //paramStore参数为当前请求链中的FormParamStore
        CallbackParam callbackParam = new CallbackParam();
        String callback = callbackParam.getCallback();
        String ajaxNonce = callbackParam.getAjaxNonce();
        //由于下一次请求中callback参数与_参数未赋值，因此需要在前一个请求处理方法中添加参数（即当前方法），否则将默认为空值
        paramStore.addParam("callback",callback);//添加下一请求中需要的callback参数至FormParamStore中
        paramStore.addParam("_",ajaxNonce);//添加下一请求中需要的_参数至FormParamStore中
        return true;//返回true表示执行成功，继续执行下一请求
    }

    //当前序列请求的执行顺序为2
    @SeqRequest(index =2,description="获取验证码图片")
    @Get("/passport/captcha/captcha-image64?login_site=E&module=login&rand=sjrand&${time.now()}")
    @Parameters({
            @Parameter(name="callback"),//自动从FormParamStore中读取callback参数的值
            @Parameter(name="_")})//自动从FormParamStore中读取_参数的值
    public boolean captchaImage(String result, FormParamStore paramStore) throws IOException {
        //请求执行过后的处理
        //result参数是当前请求执行后返回的String类型的响应体
        //paramStore参数为当前请求链中的FormParamStore
        if("".equals(result) || !HttpUtil.ContentType.isJson(result)){
            System.out.println("返回异常\n"+result);
            return false;//返回false表示执行失败，终止执行
        }
        String answer = getCaptchaImageAnswer(result);
        //由于下一次请求中answer参数未赋值，因此需要在前一个请求处理方法中添加参数（即当前方法），否则将默认为空值
        paramStore.addParam("answer",answer);//添加下一请求中需要的answer参数至FormParamStore中
        return true;//返回true表示执行成功，继续执行下一请求
    }

    //获取验证码图片，输入验证码正确答案，返回输入的答案
    private String getCaptchaImageAnswer(String s) {
        s=s.substring(s.indexOf("(")+1,s.lastIndexOf(")"));
        JSONObject jsonObject = JSONObject.parseObject(s);
        String image = jsonObject.getString("image");
        CaptchaImage.createImage(image, "C:\\Users\\74494\\Desktop\\a.png");
        System.out.println("请输入正确图片编号");
        Scanner scanner = new Scanner(System.in);
        String imageIdxs = scanner.nextLine();
        return CaptchaImage.getAnswer(imageIdxs);
    }

    //当前序列请求的执行顺序为3
    @SeqRequest(index =3,description="检测验证码答案")
    @Get("/passport/captcha/captcha-check")
    @Parameters({
            @Parameter(name="login_site",value="E"),
            @Parameter(name="rand",value="sjrand"),
            @Parameter(name="answer"),//自动从FormParamStore中读取answer参数的值
            @Parameter(name="callback"),//自动从FormParamStore中读取callback参数的值
            @Parameter(name="_")})//自动从FormParamStore中读取_参数的值
    public MoveAction captchaCheck(String result){
        //请求执行过后的处理
        //result参数是当前请求执行后返回的String类型的响应体
        JSONObject body = JSONObject.parseObject(result);
        String result_code = body.getString("result_code");
        if("4".equals(result_code)){//4是12306验证码答案api的成功码，因此继续执行下一请求
            return MoveActions.FORWARD();//MoveActions是MoveAction的工厂，MoveActions.FORWARD()返回一个ForwardAction对象，表示继续执行下一请求，等价于返回true
        }
        return MoveActions.BACK(0);//MoveActions.BACK(0)返回一个BackAction对象，表示执行上一请求，间隔0毫秒
    }

    //当前序列请求的执行顺序为4
    @SeqRequest(index = 4,description = "登陆")
    @Post("/passport/web/login")
    @Parameters({
            @Parameter(name="appid",value="otn"),
            @Parameter(name="username",value="zhangsan"),
            @Parameter(name="password",value="123456"),
            @Parameter(name="answer")})//自动从FormParamStore中读取answer参数的值
    public MoveAction login(String result) throws IOException {
        //请求执行过后的处理
        //result参数是当前请求执行后返回的String类型的响应体
        JSONObject body = JSONObject.parseObject(result);
        String result_code = body.getString("result_code");
        if("0".equals(result_code)){//0是12306登陆api的成功码，因此继续执行下一请求
            return MoveActions.FORWARD();//返回一个ForwardAction对象，表示继续执行下一请求，等价于返回true
        }else{
            //登陆失败重新跳转至captchaImage序列请求
            System.out.println(body.getString("result_message")+"\n登陆失败，请重试");
            return MoveActions.JUMP("captchaImage");//跳转并执行captchaImage序列请求
        }
    }

    //当前序列请求的执行顺序为5
    @SeqRequest(index =5,description="获取token")
    @Post("/passport/web/auth/uamtk")
    @Parameters({
            @Parameter(name="callback"),//自动从FormParamStore中读取callback参数的值
            @Parameter(name="appid",value="otn")
    })
    @RequestHeaders({
            @RequestHeader(name="Referer",value="https://kyfw.12306.cn/otn/passport?redirect=/otn/login/userLogin"),
            @RequestHeader(name="Origin",value="https://kyfw.12306.cn")
    })
    public boolean uamtk(String result,FormParamStore paramStore){
        //请求执行过后的处理
        //paramStore参数为当前请求链中的FormParamStore
        //result参数是当前请求执行后返回的String类型的响应体
        if(HttpUtil.ContentType.isJson(result)){
            JSONObject body = JSONObject.parseObject(result);
            paramStore.addParam("tk",body.getString("newapptk"));//添加下一请求中需要的tk参数至FormParamStore中
        }else{
            System.out.println("返回异常");
            return false;//返回false表示执行失败，终止执行
        }
        return true;//返回true表示执行成功，继续执行下一请求
    }

    //当前序列请求的执行顺序为6
    @SeqRequest(index =6,description="获取token")
    @Post("/otn/uamauthclient")
    @Parameters({
            @Parameter(name="tk"),//自动从FormParamStore中读取tk参数的值
            @Parameter(name="_json_att",value = "")
    })
    public boolean uamauthclient(){
        return true;//返回true表示执行成功，继续执行下一请求
    }

    //当前序列请求的执行顺序为7
    @SeqRequest(index =7,description="获取用户信息")
    @Post("/otn/modifyUser/initQueryUserInfoApi")
    @Parameters({@Parameter(name="appid",value="otn")})
    public boolean userinfo(){
        return true;//返回true表示执行成功，由于该序列请求为最后一个请求，因此表示当前请求链执行成功
    }
}
