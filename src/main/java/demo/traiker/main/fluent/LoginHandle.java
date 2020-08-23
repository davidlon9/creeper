package demo.traiker.main.fluent;

import com.alibaba.fastjson.JSONObject;
import com.davidlong.creeper.resolver.FluentRequestMappingMananger;
import demo.traiker.resovler.CallbackParam;
import demo.traiker.resovler.CaptchaImage;
import org.apache.http.client.CookieStore;
import org.apache.http.client.fluent.Executor;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.util.Scanner;

public class LoginHandle {
    public static void main(String[] args) {
        //创建一个Executor
        Executor executor = Executor.newInstance();
        //创建一个CookieStore
        CookieStore cookieStore = new BasicCookieStore();
        executor.use(cookieStore);
        LoginMapping loginMapping = new FluentRequestMappingMananger(executor).getClassProxy(LoginMapping.class);
        //第一步 deviceCookie中提取两个必备cookie
        String deivceCookie = loginMapping.deivceCookie();
        handleDeivceCookie(deivceCookie,cookieStore);

        //第二步 从captchaImageData中提取图片
        CallbackParam callbackParam = new CallbackParam();
        String callback = callbackParam.getCallback();
        String ajaxNonce = callbackParam.getAjaxNonce();
        String captchaImageData = loginMapping.captchaImage(callback, ajaxNonce);
        createCaptchaImage(captchaImageData);

        //第三步 根据图片，判断正确的有哪些，并输入对应坐标
        String answer = inputAnswer();
        String captchaCheckResult = loginMapping.captchaCheck(answer);
        //获取验证码答案是否正确，如果错误重新调用captchaCheck
        //此处省略验证失败再次请求captchaCheck的代码

        //第四步 如果验证码输入正确，就可以登录了,输入用户名密码
        String loginResult = loginMapping.login("zhangsan","123");
        //此处省略登录失败后重新请求login的代码

        //第五步 获取token
        String tokenData = loginMapping.uamtk();
        String token = JSONObject.parseObject(tokenData).getString("newapptk");

        //第六步 用户客户端认证，传入token
        loginMapping.uamauthclient(token);

        //最后，访问用户信息页面，测试是否登录成功
        String userinfo = loginMapping.userinfo();
    }

    private static String inputAnswer() {
        System.out.println("请输入正确图片编号");
        Scanner scanner = new Scanner(System.in);
        String imageIdxs = scanner.nextLine();
        return CaptchaImage.getAnswer(imageIdxs);
    }

    private static void createCaptchaImage(String captchaImageData) {
        if(!"".equals(captchaImageData)){
            captchaImageData=captchaImageData.substring(captchaImageData.indexOf("(")+1,captchaImageData.lastIndexOf(")"));
            JSONObject jsonObject = JSONObject.parseObject(captchaImageData);
            String image = jsonObject.getString("image");
            //在桌面生成一个验证码图片
            CaptchaImage.createImage(image, "C:\\Users\\admin\\Desktop\\a.png");
        }
    }

    private static void handleDeivceCookie(String deivceCookie,CookieStore cookieStore) {
        JSONObject json = JSONObject.parseObject(deivceCookie);
        BasicClientCookie basicClientCookie = new BasicClientCookie("RAIL_DEVICEID", json.getString("dfp"));
        basicClientCookie.setDomain(".12306.cn");
        basicClientCookie.setAttribute("domain",".12306.cn");
        basicClientCookie.setAttribute("path","/");
        cookieStore.addCookie(basicClientCookie);

        BasicClientCookie basicClientCookie1 = new BasicClientCookie("RAIL_EXPIRATION", json.getString("exp"));
        basicClientCookie1.setDomain(".12306.cn");
        basicClientCookie1.setAttribute("domain",".12306.cn");
        basicClientCookie1.setAttribute("path","/");
        cookieStore.addCookie(basicClientCookie1);
    }
}
