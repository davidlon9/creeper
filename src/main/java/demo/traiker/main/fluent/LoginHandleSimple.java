package demo.traiker.main.fluent;

import com.davidlong.creeper.resolver.FluentRequestMappingMananger;

public class LoginHandle {
    public static void main(String[] args) {
        LoginMapping loginMapping = new FluentRequestMappingMananger().getClassProxy(LoginMapping.class);
        //第一步 deviceCookie中提取两个必备cookie
        String deivceCookie = loginMapping.deivceCookie();
        //此处处理代码省略

        //第二步 从captchaImageData中提取图片
        String callback = "";
        String ajaxNonce = "";
        //此处省略参数获取代码
        String captchaImageData = loginMapping.captchaImage(callback, ajaxNonce);
        //此处处理代码省略

        //第三步 根据图片，判断正确的有哪些，并输入对应坐标
        String captchaCheckResult = loginMapping.captchaCheck("11,22,33,44");
        //获取验证码答案是否正确，如果错误重新调用captchaCheck
        //此处省略了再次调用captchaCheck的代码

        //第四步 如果验证码输入正确，就可以登录了,输入用户名密码
        String loginResult = loginMapping.login("zhangsan","123");
        //此处省略登录成功与否的验证，与失败后重新调用的代码

        //第五步 获取token
        String tokenData = loginMapping.uamtk();
        String token = "";//此处省略对tokenData解析token的代码

        //第六步 用户客户端认证，传入token
        loginMapping.uamauthclient(token);

        //最后，访问用户信息页面，测试是否登录成功
        String userinfo = loginMapping.userinfo();
    }
}
