## 简介
基于HttpClient-Fluent拓展的面向Java注解编程的爬虫框架，将关注点放在Http请求响应的数据解析上，使爬虫开发更迅速、简洁，且拥有更高的维护性。    

使用注解管理Http请求的链接、参数、请求头、Cookie等信息，支持SpringEl解析。  
可将注解标记在方法上，可直接在该方法中处理Http响应。  

可将类中的请求按顺序组成一个请求链，然后按顺序执行，并可以通过注解再度包装请求或请求链，使单个请求或请求链以循环执行、多线程执行等。

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
HttpClient-Fluent已经帮助我们节省了很多代码，但是对于请求创建的链接参数等信息仍然需要编写代码。而且当代码累积过多，就很难管理，上述例子对问题的暴露可能不够明显，可以参考我使用HttpClient-Fluent写的[12306爬虫程序](https://github.com/davidlon9/creeper/blob/master/src/main/java/demo/traiker/main/fluent/LoginByCode.java)，接下来我将用Creeper来重构这个12306爬虫程序。

### Request映射配置
使用步骤：
- 第一步 ([构建Request映射配置类](#构建Request映射配置类))：用一个接口来配置HttpClient请求，在接口方法中使用注解来配置请求的链接、参数、请求头。
- 第二步 ([调用Request配置接口实例](#调用Request配置接口实例))：生成请求配置接口的代理对象，然后使用代理对象调用接口中的方法，返回一个请求或请求执行后的结果，接下来只需要对结果做处理即可。
#### 构建Request映射配置类
针对12306登陆编写一个链接配置类LoginMapping，以下例子中的方法均返回了String，表示该请求执行后的响应体字符串，更多可用返回值请查看[Request配置接口方法的可用返回类型](#Request配置接口方法的可用返回类型)
```java
//接口上注解Host，该接口下所有的请求链接都以该host为域名
@Host(value="kyfw.12306.cn",scheme="https")
public interface LoginMapping {
    //获取两个必要cookie
    @Get("/otn/HttpZF/logdevice?algID=ZGB0eNTCXV&hashCode=s-hLl13iA3-UAXc9O4cfNSsDk203zmJffFi5kG43fxE&FMQw=0&q4f3=zh-CN&VySQ=FGEEJev5tTvG6q3axISQE1DJ36r7gqiH&VPIf=1&custID=133&VEek=unknown&dzuS=0&yD16=0&EOQP=4902a61a235fbb59700072139347967d&jp76=52d67b2a5aa5e031084733d5006cc664&hAqN=Win32&platform=WEB&ks0Q=d22ca0b81584fbea62237b14bd04c866&TeRS=824x1536&tOHY=24xx864x1536&Fvje=i1l1o1s1&q5aJ=-8&wNLf=99115dfb07133750ba677d055874de87&0aew=Mozilla/5.0%20(Windows%20NT%2010.0;%20Win64;%20x64)%20AppleWebKit/537.36%20(KHTML,%20like%20Gecko)%20Chrome/80.0.3987.116%20Safari/537.36&E3gR=4230a15ab4eb447d31ce29cfff1c2961")
    @Parameter(name = "timestamp",value = "${time.now()}")//time.now() 获取当前时间戳
    String deviceCookie();

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
生成12306登陆请求配置接口的代理对象，然后调用代理对象获取请求结果并做处理，代码省略了对结果的处理部分，完整代码请查看  
[12306映射配置登陆处理](https://github.com/davidlon9/creeper/blob/master/src/main/java/demo/traiker/main/fluent/LoginHandle.java)
```java
//创建一个请求管理器，在该管理器下获取一个LoginMapping代理对象
LoginMapping loginMapping = new FluentRequestMappingMananger().getClassProxy(LoginMapping.class);
//第一步 deviceCookie中提取两个必备cookie
String deviceCookie = loginMapping.deviceCookie();
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
```
可以看到上述代码，将请求链接的配置与请求执行后的处理完全分离了，使开发者将重心放在请求结果的处理上。
#### Request配置接口方法的可用返回类型
上述示例中均返回了String类型，可以使用以下类型替换Sting类型，下面也列举出了HttpClient-Fluent中对应类型实例的获取方式

| 返回值类名   | 所属包                        |对应HttpClient-Fluent的获取方式                                               | 
| :----------- | :---------------------------- | :-------------------------------------------------------------------------- |
| Request      | org.apache.http.client.fluent | Request.Get(URL)                                                            |
| Response     | org.apache.http.client.fluent | Executor.newInstance().execute(Request.Get(URL));                           |
| Content      | org.apache.http.client.fluent | Executor.newInstance().execute(Request.Get(URL)).returnContent()            |
| HttpResponse | org.apache.http               | Executor.newInstance().execute(Request.Get(URL)).returnResponse()           |
| String       | java.lang                     | Executor.newInstance().execute(Request.Get(URL)).returnContent().asString() |
| InputStream  | java.io                       | Executor.newInstance().execute(Request.Get(URL)).returnContent().asStream() |
| byte[]       |                               | Executor.newInstance().execute(Request.Get(URL)).returnContent().asBytes()  |

### RequestChain映射处理类
与Request映射配置的方式类似，只不过RequestChain对于请求的执行又增加了顺序与处理，使用注解配置请求的同时，可以直接处理对应请求。  
使用步骤：
- 第一步（[创建RequestChain映射处理类](#创建RequestChain映射处理类)）：创建一个类，并在类上注解[RequestChain类型注解](#RequestChain类型注解)，将该类视为一个请求链
- 第二步（[创建RequestChain映射处理类](#创建RequestChain映射处理类)）：在类中创建方法，并在方法上注解[SeqRequest类型注解](#SeqRequest类型注解)，将该方法视为一个在该请求链中的序列请求
- 第三步（[执行RequestChain](#执行RequestChain)）：生成一个请求链执行器，将前两步中创建好的请求链类传入，然后执行该请求链。该请求链内的所有请求会依次执行，直至最后一个请求执行成功，视为该请求链执行成功。
#### 创建RequestChain映射处理类
针对12306登陆编写一个RequestChain映射处理类，请求的方法格式将与之前Request映射配置类不同。  
- 请求执行后的结果，将放在参数中，然后直接在方法体中处理结果，可用参数请参考[可用参数](#可用参数)。
- 而返回值将用来控制请求的执行顺序，具体请参考[控制请求执行顺序](#可用参数)。
- 请求的动态参数，将使用一个容器来一起存储，即FormParamStore，所有的参数统一存储在一起。
```java
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
```
#### 执行RequestChain
创建一个RequestChain执行器，传入上面已经编写好的12306登陆请求链类（LoginChainSimple.class），然后执行该请求链。  
执行器将会按顺序执行每个请求，然后在每个请求执行后，会调用请求处理方法，即上面例子中的每个被注解了@SeqRequest的方法。  
每个请求处理方法，除了对当前请求的执行结果处理外，还需要对下一请求中的动态参数进行赋值。  
```java
//创建一个ChainContextExecutor，并传入Chain配置类
ContextExecutor executor = new ChainContextExecutor(LoginChainSimple.class);
//执行Chain
executor.exeucteRootChain();
```
上述代码一运行，执行器就会依次请求链接，直至登陆成功访问用户中心。

## RequestChain的使用文档
### 介绍
#### 请求链
请求链[RequestChain]是一系列按顺序排序的[序列请求](#序列请求)的集合。
#### 序列请求
序列请求仅存在于请求链中，序列请求的基本构成要素，除了拥有请求的链接、参数、头等信息外，还多了一个后处理器，用于在请求自动执行过后，负责处理响应信息。当然也可以添加一个前处理，用于在执行前处理，详情请查看[前后处理器](#前后处理器)

### 请求链执行流程图
<img src="https://raw.githubusercontent.com/davidlon9/creeper/master/doc/images/%E8%AF%B7%E6%B1%82%E9%93%BE%E6%89%A7%E8%A1%8C%E6%B5%81%E7%A8%8B%E5%9B%BE.png" width="80%">
关于RequestChain映射处理类的详细文档请查看[RequestChain文档](#RequestChain文档)

### 前后处理器
使用@AfterMethod与@BeforeMethod，来将一个方法声明为序列请求或请求链的AfterHandler与BeforeHandler，并在执行前后进行处理，可用参数请查看[AfterHandler与BeforeHandler的参数](#AfterHandler与BeforeHandler的参数)

#### 注解了SeqRequest类型的方法
在一个RequestChain类中，若方法上被注解了@SeqRequest类型的注解，则可以省略掉@AfterMethod注解，并默认视为该方法为一个AfterHandler。
在请求执行后会调用该方法，返回true会继续执行下一请求，false表示执行失败终止执行。 
```java
@SeqRequest(index =7,name="userinfo",description="获取用户信息")
@Post("/otn/modifyUser/initQueryUserInfoApi")
@Parameters({@Parameter(name="appid",value="otn")})
//@AfterMethod 可以省略掉该注解，因为方法注解了@SeqRequest
public boolean userinfo(HttpResponse response){
    return true;
}
```
若需要可以修改上面的方法，添加注解@BeforeMethod，可将该方法视为一个BeforeHandler，在请求执行前会调用该方法，返回false会跳过请求的执行，如下例：
```java
@SeqRequest(index =7,name="userinfo",description="获取用户信息")
@Post("/otn/modifyUser/initQueryUserInfoApi")
@Parameters({@Parameter(name="appid",value="otn")})
@BeforeMethod
public boolean userinfo(HttpResponse response){
    return true;
}
```
注意，此时的AfterHandler将为空

#### 序列请求同时拥有前后处理器
当同时需要BeforeHandler与AfterHandler时，新增一个方法并注解@BeforeMethod("name")，name指定为SeqRequest的name，如下例：
```java
@SeqRequest(index =7,name="userinfo",description="获取用户信息")//name默认为方法名（RequestChain的name默认为类名）
@Post("/otn/modifyUser/initQueryUserInfoApi")
@Parameters({@Parameter(name="appid",value="otn")})
//@AfterMethod 可以省略掉该注解，因为方法注解了@SeqRequest
public boolean userinfo(HttpResponse response){
    //处理响应结果，若下一请求没有BeforeHandler，则必需在此时添加下一请求的动态参数。
    return true;//继续执行下一请求
}
@BeforeMethod("userinfo")//指定SeqRequest或RequestChain的name
public boolean checkUserInfo(Request request, ExecutionContext context){
    //检查添加各种参数
    return true;//不跳过执行
}
```
上述例子中，当然也可以在@SeqRequest注解的方法下注解@BeforeMethod，然后再新增一个方法注解@AfterMethod("")，并指定SeqRequest的name。

#### RequestChain中的前后处理器
BeforeHandler与AfterHandler也可以用在RequestChain中，用来控制RequestChain的执行前后的处理，如下例：
```java
@RequestChain(name="ChainName",description = "演示")//name默认为类名ChainBeforeAfterHandlerDemo
public class ChainBeforeAfterHandlerDemo{
    @BeforeMethod("ChainName")//指定RequestChain的name
    public boolean chainBeforeHandle(ExecutionContext context){
        //检查添加各种参数
        return true;//不跳过执行
    }
    @AfterMethod("ChainName")//指定RequestChain的name
    public boolean chainAfterHandle(ExecutionContext context){
        //执行结束的处理
        return true;//继续执行下一请求
    }
}
```
当然RequestChain的前后处理器都不是必须的，可以自己按需求来选择，甚至可以不要。
### ExecutionHandler
ExecutionHandler就是AfterHandler与BeforeHandler的集合，AfterHandler与BeforeHandler分别拥有一个抽象方法即afterHandle与beforeHandle，
```java
ExecutionHandler executionHandler = new ExecutionHandler() {
    @Override
    public Boolean beforeHandle(Request request, ExecutionContext context) throws IOException {
        return true;
    }
    @Override
    public MoveAction afterHandle(HttpResponse response, ExecutionContext context) throws IOException {
        return MoveActions.FORWARD();
    }
};
```
##### ExecutionHandler方法的可用返回类型

ReuqestChain配置类的方法，一般是Chain或Request的BeforeHandler、AfterHandler，分别在请求的执行前后进行处理，详细解释请看[ExecutionHandler](#ExecutionHandler)，以下是其可用返回值类型与解释
| 返回值类型   | BeforeHandler返回值对应动作 | AfterHandler返回值对应动作 |
| :----------- | :------------------------- | -------------------------- |
| com.dlong.creeper.control.MoveAction | 仅支持ContinueAction，表示在循环执行跳过当前的执行，若使用其他MoveAction实现类则会抛出异常 | 不同的MoveAction实现类，对应不同的执行动作，详情参考[MoveActions](#MoveActions) | 
| Boolean/boolean | true表示可以执行，false表示跳过当前执行| true表示继续执行下一请求等价于ForwardAction，false表示执行失败终结执行等价于TerminateAction |
| Object | 仅可使用上面两种类型的值 | 仅可使用上面两种类型的值 |

