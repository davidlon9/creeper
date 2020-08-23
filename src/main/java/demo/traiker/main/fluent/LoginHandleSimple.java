package demo.traiker.main.fluent;

import com.alibaba.fastjson.JSONObject;
import com.dlong.creeper.resolver.FluentRequestMappingMananger;
import demo.traiker.resovler.CallbackParam;

public class LoginHandleSimple {
    public static void main(String[] args) {
        //创建一个请求管理器，在该管理器下获取一个LoginMapping代理对象
        LoginMapping loginMapping = new FluentRequestMappingMananger().getClassProxy(LoginMapping.class);
        //第一步 deviceCookie中提取两个必备cookie
        String deivceCookie = loginMapping.deivceCookie();
        //此处处理代码省略

        //第二步 从captchaImageData中获取图片
        CallbackParam callbackParam = new CallbackParam();
        String callback = callbackParam.getCallback();
        String ajaxNonce = callbackParam.getAjaxNonce();
        String captchaImageData = loginMapping.captchaImage(callback, ajaxNonce);
        //此处处理代码省略

        //第三步 根据图片，判断正确的有哪些，并输入对应坐标
        String captchaCheckResult = loginMapping.captchaCheck("11,22,33,44");
        //获取验证码答案是否正确

        //第四步 如果验证码输入正确，就可以登录了,输入用户名密码
        String loginResult = loginMapping.login("zhangsan","123");

        //第五步 获取token
        String tokenData = loginMapping.uamtk();
        String token = JSONObject.parseObject(tokenData).getString("newapptk");

        //第六步 用户客户端认证，传入token
        String uamauthclient = loginMapping.uamauthclient(token);

        //最后，访问用户信息页面，测试是否登录成功
        String userinfo = loginMapping.userinfo();
    }
}
