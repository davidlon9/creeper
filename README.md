## 简介
基于HttpClient-Fluent拓展的面向Java注解编程的爬虫框架，将关注点放在Http请求响应的数据解析上，使爬虫开发更迅速、简洁，且拥有更高的维护性。    

可以使用注解注明Http请求的链接、参数、请求头、Cookie等信息，支持SpringEl解析。  
将注解标记在方法上，将方法视为一个Http请求，可以直接在方法中处理Http响应。  

也可以将一个类中的请求按顺序组成一个请求链，然后按顺序执行，并可以通过注解再度包装请求或请求链，使单个请求或请求链以循环执行、多线程执行等。

也就是说，仅需要一个类，就能够迅速完成一个复杂的爬虫程序。

## 示例
首先熟悉一下HttpClient-Fluent的基本使用方式

```java
//构建一个Request请求对象
Request get = Request.Get("http://www.giftcat.com").bodyForm(
                new BasicNameValuePair("param1","value1"),
                new BasicNameValuePair("param2","value2"));
//创建Executor实例
Executor executor = Executor.newInstance();
//使用Executor执行Request
Response response = executor.execute(get);
//获取执行后的Response字符串
String stringResult = response.returnContent().asString();
//获取执行后apache封装的HttpResponse对象(可以获取一系列Response相关的信息)
HttpResponse httpResponse = response.returnResponse();
```
HttpClient-Fluent已经帮助我们节省了很多代码，但是对于请求创建的链接参数等信息仍然需要编写代码。而且当代码累积过多，就很难管理，缺乏统一的管理性
上述例子对问题的暴露可能不够明显，可以参考我写的使用HttpClient-Fluent写的[12306爬虫程序](https://github.com/davidlon9/creeper/blob/master/src/main/java/demo/traiker/main/fluent/LoginByCode.java)

#### 构建Request映射配置类
针对12306登陆编写的链接配置类LoginMapping
```java
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
```

#### 调用Request配置接口实例
生成12306登陆请求配置接口的代理对象，然后调用代理对象获取请求结果并做处理，代码省略了对结果的处理部分，完整代码请查看[12306映射配置登陆处理](https://github.com/davidlon9/creeper/blob/master/src/main/java/demo/traiker/main/fluent/LoginHandle.java)
```java
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
loginMapping.uamauthclient(token);

//最后，访问用户信息页面，测试是否登录成功
String userinfo = loginMapping.userinfo();
```
可以看到上述代码，将请求链接的配置与请求执行后的处理完全分离了，使开发者将重心放在请求结果的处理上
