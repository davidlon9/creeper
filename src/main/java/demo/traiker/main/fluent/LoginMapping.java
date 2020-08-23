package demo.traiker.main.fluent;

import com.davidlong.creeper.annotation.*;
import com.davidlong.creeper.annotation.http.Get;
import com.davidlong.creeper.annotation.http.Post;

//接口上注解Host，该接口下所有的请求链接都以该host为域名
@Host(value="kyfw.12306.cn",scheme="https")
public interface LoginMapping {
    //获取两个必要cookie
    @Get("/otn/HttpZF/logdevice?algID=ZGB0eNTCXV&hashCode=s-hLl13iA3-UAXc9O4cfNSsDk203zmJffFi5kG43fxE&FMQw=0&q4f3=zh-CN&VySQ=FGEEJev5tTvG6q3axISQE1DJ36r7gqiH&VPIf=1&custID=133&VEek=unknown&dzuS=0&yD16=0&EOQP=4902a61a235fbb59700072139347967d&jp76=52d67b2a5aa5e031084733d5006cc664&hAqN=Win32&platform=WEB&ks0Q=d22ca0b81584fbea62237b14bd04c866&TeRS=824x1536&tOHY=24xx864x1536&Fvje=i1l1o1s1&q5aJ=-8&wNLf=99115dfb07133750ba677d055874de87&0aew=Mozilla/5.0%20(Windows%20NT%2010.0;%20Win64;%20x64)%20AppleWebKit/537.36%20(KHTML,%20like%20Gecko)%20Chrome/80.0.3987.116%20Safari/537.36&E3gR=4230a15ab4eb447d31ce29cfff1c2961")
    @Parameter(name = "timestamp",value = "${time.now()}")//time.now() 获取当前时间戳
    String deivceCookie();

    //获取验证码图片，请求需要传入callback、_两个参数
    @Get("/passport/captcha/captcha-image64?login_site=E&module=login&rand=sjrand&${time.now()}")//time.now() 获取当前时间戳
    String captchaImage(@Parameter(name="callback") String callback, @Parameter(name="_") String p2);//传入的参数可以被后续调用的方法使用

    //验证图片答案，传入正确答案的坐标
    @Get("/passport/captcha/captcha-check")
    @Parameters({
        @Parameter(name="login_site",value="E"),
        @Parameter(name="rand",value="sjrand"),
        @Parameter(name="callback"),//使用上一步中的callback
        @Parameter(name="_")})//使用上一步中的_
    String captchaCheck(@Parameter(name="answer")String answer);//传入的参数可以被后续调用的方法使用

    //登陆，输入username、password
    @Post("/passport/web/login")
    @Parameters({
        @Parameter(name="appid",value="otn"),
        @Parameter(name="answer")//使用上一步中的answer
    })
    String login(@Parameter(name="username")String username, @Parameter(name="password")String password);

    //获取用户认证的token
    @Post("/passport/web/auth/uamtk")
    @Parameters({
        @Parameter(name="callback"),//使用之前的callback
        @Parameter(name="appid")//使用上一步中的appid
    })
    @RequestHeaders({
        @RequestHeader(name="Referer",value="https://kyfw.12306.cn/otn/passport?redirect=/otn/login/userLogin"),//必需的Header
        @RequestHeader(name="Origin",value="https://kyfw.12306.cn")//必需的Header
    })
    String uamtk();

    //用户认证，传入上一步骤中返回的token
    @Post("/otn/uamauthclient")
    @Parameter(name="_json_att")//之前未赋值的参数，解析为空字符，建议对空字符参数赋值，如 @Parameter(name="_json_att",value = "")
    String uamauthclient(@Parameter(name="tk") String tk);

    //用户个人页面
    @Post("/otn/modifyUser/initQueryUserInfoApi")
    @Parameter(name="appid")//使用之前的appid
    String userinfo();
}