# RequestChain使用文档

## 基本概念
### 请求链
请求链[RequestChain]是一系列按顺序排序的[序列请求](#序列请求)[SeqRequest]或请求链的集合，支持嵌套请求链。
### 序列请求
序列请求[SeqRequest]仅存在于请求链中，序列请求的基本构成要素，除了拥有请求的链接、参数、头等信息外，还多了一个后处理器，用于在请求自动执行过后，负责处理响应信息。当然也可以添加一个前处理器，用于在执行前处理，详情请查看[前后处理器](#前后处理器)
### 序列对象
序列对象是可以被按顺序执行的对象，序列对象有请求链[RequestChain]与序列请求[SeqRequest]两大类，@RequestChain与@SeqRequest注解仅是最基础的两个注解，除了这两个注解外还有很多可用的请求链与序列请求注解
| 请求链[RequestChain]类型注解            | 序列请求[SeqRequest]类型注解          | 
| :-------------------------------------- | :------------------------------------ |
| @RequestChain（请求链）                  | @SeqRequest （序列请求）              |  
| @MultiRequestChain （多线程执行的请求链）| @MultiRequest （多线程执行的请求）    |
| @MultiUserExecutor （多用户多线程执行）  | @MultiRequestQueue （多线程队列请求） | 

除了基础的@RequestChain与@SeqRequest，其他注解都是这两个序列对象的包装，可以自定义创建一个包装执行器，或者单纯通过代码来包装请求或请求链。
### 请求链执行流程图
<img src="https://raw.githubusercontent.com/davidlon9/creeper/master/doc/images/%E8%AF%B7%E6%B1%82%E9%93%BE%E6%89%A7%E8%A1%8C%E6%B5%81%E7%A8%8B%E5%9B%BE.png" width="80%">

### 示例
下例是使用@RequestChain来编写12306登陆中，检测验证码答案与登陆的部分，完整请看[请求链12306登陆](https://github.com/davidlon9/creeper#requestchain%E6%98%A0%E5%B0%84%E5%A4%84%E7%90%86%E7%B1%BB)
```java
@RequestChain(index =1,name="LoginChain",description="登陆请求链")
@Host(value="kyfw.12306.cn",scheme="https")
public class LoginChain {
    @SeqRequest(index =1,description="检测验证码答案")
    @Get("/passport/captcha/captcha-check")
    @Parameters({
            @Parameter(name="login_site",value="E"),
            @Parameter(name="rand",value="sjrand"),
            @Parameter(name="answer",value="11,22,33")})
    public MoveAction captchaCheck(String result){
        //请求执行过后的处理
        //result参数是当前请求执行后返回的String类型的响应体
        JSONObject body = JSONObject.parseObject(result);
        if("4".equals(body.getString("result_code"))){//4是12306验证码答案api的成功码，因此继续执行下一请求
            return MoveActions.FORWARD();//MoveActions是MoveAction的工厂，MoveActions.FORWARD()返回一个ForwardAction对象，表示继续执行下一请求，等价于返回true
        }
        return MoveActions.TERMINATE();//MoveActions.TERMINATE()返回一个TerminateAction对象，表示终止执行
    }

    @SeqRequest(index = 2,description = "登陆")
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
        if("0".equals(body.getString("result_code"))){//0是12306登陆api的成功码，因此继续执行下一请求
            return MoveActions.FORWARD();//返回一个ForwardAction对象，表示继续执行下一请求，等价于返回true
        }else{
            //登陆失败重新跳转至captchaImage序列请求
            return MoveActions.JUMP("captchaImage");//跳转并执行captchaImage序列请求
        }
    }
}
```
请求链一般是由序列请求构成，而序列请求是由请求信息注解构成，请求信息基本是由链接信息注解(Host、Path、Get、Post、Put、Delete)与请求参数类型注解Parameter构成，单独使用这些注解，也可以配置管理请求，请参考[Request映射配置](https://github.com/davidlon9/creeper#request%E6%98%A0%E5%B0%84%E9%85%8D%E7%BD%AE)

## 前后处理器
### 前处理器[BeforeHandler]
前处理器的3个作用:
- 初始化 <em>当前序列请求或请求链</em> 需要的上下文参数[ContextParamStore](#ContextParamStore)、请求参数[FormParamStore](#FormParamStore)、Cookie存储[CookieStore]
- 手动处理HttpClient-Fluent中的Request（直接调用HttpClient原生Api处理Request），例如请求装配还缺少些参数、Cookie等信息
- 判断当前序列请求或请求链，是否要跳过执行。

使用@BeforeMethod，来将一个方法声明为序列请求或请求链的后处理器[BeforeHandler]，并在其执行前进行处理，可用参数请查看[前后处理器方法的可用参数类型
](#前后处理器方法的可用参数类型
)
### 后处理器[AfterHandler]
后处理器的3个作用:
- 初始化 <em>下一序列请求或请求链</em> 需要的上下文参数[ContextParamStore](#ContextParamStore)、请求参数[FormParamStore](#FormParamStore)、Cookie存储[CookieStore]
- 处理请求执行后的响应结果
- 处理并指定下一序列请求（使用返回值来指定下一序列请求）

使用@AfterMethod或SeqRequest类型注解，来将一个方法声明为序列请求或请求链的后处理器[AfterHandler]，并在其执行后进行处理，可用参数请查看[前后处理器方法的可用参数类型
](#前后处理器方法的可用参数类型)
### 示例
#### SeqRequest类型注解后处理器
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

注意，此时的AfterHandler将为空，意味着不对该请求做处理，执行结束后直接执行下一请求

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
一般情况是不需要BeforeHandler，因为Request已经被组装好了，只需要AfterHandler处理请求执行结果，因此不用担心类中每个请求都要用两个方法来表示，导致阅读性变差，接下来会介绍更优雅的方式。
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

### 前后处理器方法的可用参数类型
如果使用了不支持的参数，则该参数为空
| 参数类型         | 所属包                               | BeforeHandler是否可用  | AfterHandler是否可用  | 
| :---------------- | :---------------------------------- | :-------------------: | :--------------------: |
| Request           | org.apache.http.client.fluent       | √ | × |
| HttpResponse      | org.apache.http                     | × | √ |
| String            | java.lang                           | × | √ |
| [FormParamStore](#FormParamStore)    | com.dlong.creeper.execution.context | √ | √ | 
| [ContextParamStore](#ContextParamStore) | com.dlong.creeper.execution.context | √ | √ |
| CookieStore       | org.apache.http.client              | √ | √ |  
| [ExecutionContext](#执行上下文ExecutionContext)  | com.dlong.creeper.execution.context | √ | √ |  

### 前后处理器方法的可用返回类型
| 返回值类型   | BeforeHandler返回值对应动作 | AfterHandler返回值对应动作 |
| :----------- | :------------------------- | -------------------------- |
| com.dlong.creeper.control.MoveAction | 仅支持ContinueAction，表示在循环执行跳过当前的执行，若使用其他MoveAction实现类则会抛出异常 | 不同的MoveAction实现类，对应不同的执行动作，详情参考[MoveActions](#MoveActions) | 
| Boolean/boolean | true表示可以执行，false表示跳过当前执行| true表示继续执行下一请求等价于ForwardAction，false表示执行失败终结执行等价于TerminateAction |
| Object | 仅可使用上面两种类型的值 | 仅可使用上面两种类型的值 |
| void   | 不跳过当前执行 | 继续执行下一请求 |

## 执行上下文ExecutionContext
ExecutionContext中存储了请求链中的所有参数，Cookie，以及SpringEl表达式中的参数，每个ExecutionContext实例中都会包含一个[FormParamStore](#FormParamStore)、[ContextParamStore](#ContextParamStore)、CookieStore、Executor
## FormParamStore
FormParamStore用于存储请求链中的所有参数，每个请求链只拥有一个FormParamStore，可以作为前后处理器的参数，可以使用其来添加参数，并作用到整个请求链。
### 参数Parameter
注解在序列请求下，未指定值时，需要向FormParamStore添加一个相同名称的Param对象，若FormParamStore中也没有，则为空值，如下例中的answer参数:
```java
@SeqRequest(index = 3,description="检测验证码答案")
@Get("/passport/captcha/captcha-check")
public boolean captchaCheck(String result,FormParamStore formParamStore){
    formParamStore.addParam("answer",result);//添加下一请求中未赋值的参数
    return true;
}

@SeqRequest(index = 4,description = "登陆")
@Post("/passport/web/login")
@Parameters({
        @Parameter(name="appid",value="otn"),
        @Parameter(name="username",value="zhangsan"),
        @Parameter(name="password",value="123456"),
        @Parameter(name="answer")})//自动从FormParamStore中读取answer参数的值
public boolean login(String result) throws IOException {
    return true;
}
```
当两个参数名不同，但是参数的值一致时，例如有个参数名为answer1，又有个参数为answer2，他们两个的值是相同的，可以使用@Parameter注解的globalKey，如下例:
@Parameter(name = "test",globalKey = "globalName")
```java
@SeqRequest(index = 3,description="检测验证码答案")
@Get("/passport/captcha/captcha-check")
public boolean captchaCheck(String result,FormParamStore formParamStore){
    formParamStore.addParam("answer1",result);//添加下一请求中未赋值的参数
    return true;
}

@SeqRequest(index = 4,description = "登陆")
@Post("/passport/web/login")
@Parameters({
        @Parameter(name="appid",value="otn"),
        @Parameter(name="username",value="zhangsan"),
        @Parameter(name="password",value="123456"),
        @Parameter(name="answer2",globalKey = "answer1")})//自动从FormParamStore中读取name为answer1参数的值
public boolean login(String result) throws IOException {
    return true;
}
```
## ContextParamStore
ContextParamStore用于存储SpringEl表达式中的对象，SpringEl表达式一般用在链接，参数上，其他可用注解值请看下表
### 支持SpringEl表达式的注解属性
| 注解                      | 支持SpringEl的属性        |
| :------------------------ | :------------------------ |
| @Path/Get/Post/Put/Delete | url                       |
| @Parameter                | name/value                |
| @JsonResultCookie         | defaultValue              |
| @ForIndex                 | start/end                 |
| @While                    | coniditionExpression      |
| @Trigger                  | startTimeExpr/endTimeExpr |
| @MultiRequestQueue        | stopConditionExpr         |  

## 控制执行顺序
通过后处理器的返回值，来控制一个请求链中请求的执行顺序，请求的后处理器返回值，决定了将要执行的下一请求。
### MoveAction
标准的后处理器返回类型，可以使用MoveActions工厂类来快捷创建实例，或直接用new创建对应MoveAction实例。
| MoveAction      | 表示的动作                                            | 
| :-------------- | :---------------------------------------------------- |
| ForwardAction   | 继续执行下一请求                                      |
| BackAction      | 回退并执行上一请求                                    |
| JumpAction      | 跳转至任意请求并执行                                  |
| RetryAction     | 重新执行当前请求                                      |
| TerminateAction | 强制结束当前请求链的执行                              |
| RestartAction   | 重新开始执行当前请求链                                |
| BreakAction     | 终止当前域下的[循环](#循环执行)                       |
| ContinueAction  | 跳过当前[循环](#循环执行)的执行，继续下一次循环的执行  |  

### Boolean
后处理器可以返回Boolean类型的值，true表示继续执行下一请求等价于ForwardAction，false表示执行失败终结执行等价于TerminateAction
### 空值
后处理器的返回类型可以为void，也就是说返回值为null。当返回值为null时，表示继续执行下一请求等价于ForwardAction

## 循环执行

## 其他注解



