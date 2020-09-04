# RequestChain使用文档
## 目录
  * [基本概念](#%E5%9F%BA%E6%9C%AC%E6%A6%82%E5%BF%B5)
    * [请求链](#%E8%AF%B7%E6%B1%82%E9%93%BE)
    * [序列请求](#%E5%BA%8F%E5%88%97%E8%AF%B7%E6%B1%82)
    * [序列对象](#%E5%BA%8F%E5%88%97%E5%AF%B9%E8%B1%A1)
    * [请求链执行流程图](#%E8%AF%B7%E6%B1%82%E9%93%BE%E6%89%A7%E8%A1%8C%E6%B5%81%E7%A8%8B%E5%9B%BE)
  * [声明请求链与请求](#%E5%A3%B0%E6%98%8E%E8%AF%B7%E6%B1%82%E9%93%BE%E4%B8%8E%E8%AF%B7%E6%B1%82)
    * [请求链](#%E8%AF%B7%E6%B1%82%E9%93%BE-1)
    * [序列请求](#%E5%BA%8F%E5%88%97%E8%AF%B7%E6%B1%82-1)
    * [引用请求与请求链](#%E5%BC%95%E7%94%A8%E8%AF%B7%E6%B1%82%E4%B8%8E%E8%AF%B7%E6%B1%82%E9%93%BE)
    * [Path类型注解](#path%E7%B1%BB%E5%9E%8B%E6%B3%A8%E8%A7%A3)
  * [前后处理器](#%E5%89%8D%E5%90%8E%E5%A4%84%E7%90%86%E5%99%A8)
    * [前处理器[BeforeHandler]](#%E5%89%8D%E5%A4%84%E7%90%86%E5%99%A8beforehandler)
    * [后处理器[AfterHandler]](#%E5%90%8E%E5%A4%84%E7%90%86%E5%99%A8afterhandler)
    * [示例](#%E7%A4%BA%E4%BE%8B)
      * [方法模式示例](#%E6%96%B9%E6%B3%95%E6%A8%A1%E5%BC%8F%E7%A4%BA%E4%BE%8B)
        * [SeqRequest类型注解后处理器](#seqrequest%E7%B1%BB%E5%9E%8B%E6%B3%A8%E8%A7%A3%E5%90%8E%E5%A4%84%E7%90%86%E5%99%A8)
        * [序列请求同时拥有前后处理器](#%E5%BA%8F%E5%88%97%E8%AF%B7%E6%B1%82%E5%90%8C%E6%97%B6%E6%8B%A5%E6%9C%89%E5%89%8D%E5%90%8E%E5%A4%84%E7%90%86%E5%99%A8)
        * [RequestChain中的前后处理器](#requestchain%E4%B8%AD%E7%9A%84%E5%89%8D%E5%90%8E%E5%A4%84%E7%90%86%E5%99%A8)
        * [前后处理器方法的可用参数类型](#%E5%89%8D%E5%90%8E%E5%A4%84%E7%90%86%E5%99%A8%E6%96%B9%E6%B3%95%E7%9A%84%E5%8F%AF%E7%94%A8%E5%8F%82%E6%95%B0%E7%B1%BB%E5%9E%8B)
        * [前后处理器方法的可用返回类型](#%E5%89%8D%E5%90%8E%E5%A4%84%E7%90%86%E5%99%A8%E6%96%B9%E6%B3%95%E7%9A%84%E5%8F%AF%E7%94%A8%E8%BF%94%E5%9B%9E%E7%B1%BB%E5%9E%8B)
      * [接口模式示例](#%E6%8E%A5%E5%8F%A3%E6%A8%A1%E5%BC%8F%E7%A4%BA%E4%BE%8B)
        * [ExecutionHandler接口](#executionhandler%E6%8E%A5%E5%8F%A3)
        * [AfterHandler接口](#afterhandler%E6%8E%A5%E5%8F%A3)
        * [BeforeHandler接口](#beforehandler%E6%8E%A5%E5%8F%A3)
        * [RequestChain的处理器接口](#requestchain%E7%9A%84%E5%A4%84%E7%90%86%E5%99%A8%E6%8E%A5%E5%8F%A3)
  * [ExecutionContext执行上下文](#executioncontext%E6%89%A7%E8%A1%8C%E4%B8%8A%E4%B8%8B%E6%96%87)
    * [FormParamStore](#formparamstore)
      * [参数Parameter](#%E5%8F%82%E6%95%B0parameter)
    * [ContextParamStore](#contextparamstore)
      * [支持SpringEl表达式的注解属性](#%E6%94%AF%E6%8C%81springel%E8%A1%A8%E8%BE%BE%E5%BC%8F%E7%9A%84%E6%B3%A8%E8%A7%A3%E5%B1%9E%E6%80%A7)
  * [控制执行顺序](#%E6%8E%A7%E5%88%B6%E6%89%A7%E8%A1%8C%E9%A1%BA%E5%BA%8F)
    * [MoveAction](#moveaction)
    * [Boolean](#boolean)
    * [空值](#%E7%A9%BA%E5%80%BC)
  * [循环执行](#%E5%BE%AA%E7%8E%AF%E6%89%A7%E8%A1%8C)
    * [可用的Loop注解](#%E5%8F%AF%E7%94%A8%E7%9A%84loop%E6%B3%A8%E8%A7%A3)
    * [跳出循环](#%E8%B7%B3%E5%87%BA%E5%BE%AA%E7%8E%AF)
    * [继续循环](#%E7%BB%A7%E7%BB%AD%E5%BE%AA%E7%8E%AF)
    * [示例](#%E7%A4%BA%E4%BE%8B-1)
      * [While](#while)
      * [ForEach](#foreach)
      * [ForIndex](#forindex)
      * [Scheduler](#scheduler)
  * [其他注解](#%E5%85%B6%E4%BB%96%E6%B3%A8%E8%A7%A3)
    * [@JsonResultCookie](#jsonresultcookie)
    * [@FileRecordsIgnore](#filerecordsignore)

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

## 声明请求链与请求

请求链一般是由序列请求构成，而序列请求基本是由[路径类注解](#Path类型注解)(Host、Path、Get、Post、Put、Delete)与请求参数类型注解Parameter构成，单独使用这些注解，也可以配置管理请求，请参考[Request映射配置](https://github.com/davidlon9/creeper#request%E6%98%A0%E5%B0%84%E9%85%8D%E7%BD%AE)

### 请求链
通常需要两个注解，@Host、@RequestChain。
@Host注解会使该类下的所有序列请求，都默认以其为服务器域名前缀
当然@Host也是非必要的，如果不使用@Host在请求链上，则必需在序列请求上注解@Host，或者链接自带服务器域名
```java
@Host(value="kyfw.12306.cn",scheme="https")
@RequestChain(name="LoginChain",description="登陆请求链")
public class LoginChain{
}
```
也可以在RequestChain上注解@Path，来制造一个基础路径，使类下的所有序列请求，都以其为前缀
```java
//基础路径"kyfw.12306.cn/path"
@Host(value="kyfw.12306.cn",scheme="https")
@Path("/path")
@RequestChain(name="LoginChain",description="登陆请求链")
public class LoginChain{
}
```

### 序列请求
通常注解@SeqRequest在方法上，来声明一个序列请求，将方法视为该序列请求的后处理器。也可以注解在处理器接口上，请看[接口模式示例](#接口模式示例)
除了@SeqRequest注解外，还需要声明该请求的方法与路径，因此需要[Path类型注解](#Path类型注解)，同时使用@Parameter指定该请求的参数
```java
@SeqRequest(index = 7, name = "userinfo", description = "获取用户信息")
@Post("/otn/modifyUser/initQueryUserInfoApi")
@Parameter(name = "appid", value = "otn")
public boolean userinfo(HttpResponse response) {
    return true;
}
```
如果没有声明Path类型注解，则以请求链的基础链接作为该请求的链接

### 引用请求与请求链
在请求链类中可以引用其他创建好的请求链类或请求，引用方式如下
```java
@Host(value = "kyfw.12306.cn", scheme = "https")
@RequestChain(description = "订单请求链")
public class OrderChain {
    @ChainReference(index = 1)
    LoginChain login12306;//12306登陆请求链

    @RequestReference(index = 2,chainClass = LoginChain.class, requestName = "deviceCookie")
    Method deviceCookie;//12306登陆请求链中获取必要Cookie的请求
}
```

### Path类型注解
@Path、@Get、@Post、@Put、@Delete都属于Path类型的注解，@Path注解需要指定一个路径与一个Http方法（默认是Get）
除了@Path注解，其他注解相当于注解的同时就指定了Http方法Get、Post、Put、Delete，同时也需要指定一个路径或链接

### 请求链的执行
使用ChainContextExecutor来执行请求链，需要传入一个请求链类
```java
//传入请求链类，构造方法内会根据请求链类创建一个ChainContext实例
ContextExecutor executor = new ChainContextExecutor(LoginChain.class);
//执行Chain
executor.exeucteRootChain();
```

## 前后处理器
### 前处理器[BeforeHandler]
前处理器的3个作用:
- 初始化<em>  当前序列请求或请求链  </em>需要的上下文参数[ContextParamStore](#ContextParamStore)、请求参数[FormParamStore](#FormParamStore)、Cookie存储[CookieStore]
- 手动处理HttpClient-Fluent中的Request（直接调用HttpClient原生Api处理Request），例如请求装配还缺少些参数、Cookie等信息
- 判断当前序列请求或请求链，是否要跳过执行。

使用@BeforeMethod，来将一个方法声明为序列请求或请求链的后处理器[BeforeHandler]，并在其执行前进行处理，可用参数请查看[前后处理器方法的可用参数类型
](#前后处理器方法的可用参数类型
)

### 后处理器[AfterHandler]
后处理器的3个作用:
- 初始化<em>  下一序列请求或请求链  </em>需要的上下文参数[ContextParamStore](#ContextParamStore)、请求参数[FormParamStore](#FormParamStore)、Cookie存储[CookieStore]
- 处理请求执行后的响应结果
- 处理并指定下一序列请求（使用返回值来指定下一序列请求）

使用@AfterMethod或SeqRequest类型注解，来将一个方法声明为序列请求或请求链的后处理器[AfterHandler]，并在其执行后进行处理，可用参数请查看[前后处理器方法的可用参数类型
](#前后处理器方法的可用参数类型)

### 示例
有两种模式来声明序列请求、绑定前后处理器  
[方法模式](#方法模式)  
[接口模式](#接口模式)  

#### 方法模式示例
##### SeqRequest类型注解后处理器
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

##### 序列请求同时拥有前后处理器
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

##### RequestChain中的前后处理器
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

##### 前后处理器方法的可用参数类型
如果使用了不支持的参数，则该参数为空，String类型的参数在后处理器中是响应体的字符串值。
| 参数类型                                         | 所属包                              | BeforeHandler是否可用  | AfterHandler是否可用  | 
| :----------------------------------------------- | :---------------------------------- | :-------------------: | :--------------------: |
| Request                                          | org.apache.http.client.fluent       | √ | × |
| HttpResponse                                     | org.apache.http                     | × | √ |
| String                                           | java.lang                           | × | √ |
| [FormParamStore](#FormParamStore)                | com.dlong.creeper.execution.context | √ | √ | 
| [ContextParamStore](#ContextParamStore)          | com.dlong.creeper.execution.context | √ | √ |
| CookieStore                                      | org.apache.http.client              | √ | √ |  
| [ExecutionContext](#执行上下文ExecutionContext)  | com.dlong.creeper.execution.context | √ | √ |  

##### 前后处理器方法的可用返回类型
| 返回值类型   | BeforeHandler返回值对应动作 | AfterHandler返回值对应动作 |
| :----------- | :------------------------- | -------------------------- |
| com.dlong.creeper.control.MoveAction | 仅支持ContinueAction，表示在循环执行跳过当前的执行，若使用其他MoveAction实现类则会抛出异常 | 不同的MoveAction实现类，对应不同的执行动作，详情参考[MoveActions](#MoveActions) | 
| Boolean/boolean | true表示可以执行，false表示跳过当前执行| true表示继续执行下一请求等价于ForwardAction，false表示执行失败终结执行等价于TerminateAction |
| Object | 仅可使用上面两种类型的值 | 仅可使用上面两种类型的值 |
| void   | 不跳过当前执行 | 继续执行下一请求 |

#### 接口模式示例
将SeqRequest类型注解使用在Handler接口上，也可以将该Handler绑定在序列请求上，并视为一个序列请求。
与声明在方法上不同，声明在接口实现上，只能使用固定的参数。
##### ExecutionHandler接口
使用前后处理器接口ExecutionHandler声明序列请求，ExecutionHandler接口中包含前处理方法与后处理方法，使用方式如下
```java
@SeqRequest(index = 7, name = "userinfo", description = "获取用户信息")//name默认为方法名（RequestChain的name默认为类名）
@Post("/otn/modifyUser/initQueryUserInfoApi")
@Parameter(name = "appid", value = "otn")
ExecutionHandler executionHandler=new ExecutionHandler() {
    @Override
    public Object afterHandle(HttpResponse response, ExecutionContext context) throws IOException {
        return true;
    }
    @Override
    public Object beforeHandle(Request request, ExecutionContext context) throws IOException {
        return true;
    }
};
```

##### AfterHandler接口
使用后处理器接口AfterHandler声明序列请求，如果用这种方式绑定后处理器到序列请求，将无法再使用BeforeHandler实例绑定到该序列请求，但是可以将@BeforeMethod注解使用在方法上，来绑定前处理器到序列请求
```java
@SeqRequest(index = 7, name = "userinfo", description = "获取用户信息")//name默认为方法名（RequestChain的name默认为类名）
@Post("/otn/modifyUser/initQueryUserInfoApi")
@Parameter(name = "appid", value = "otn")
AfterHandler userinfo4 = new AfterHandler() {
    @Override
    public Object afterHandle(HttpResponse response, ExecutionContext context) throws IOException {
        return true;
    }
};

@BeforeMethod("userinfo")//指定SeqRequest的name
public boolean checkUserinfo(Request request, ExecutionContext context) {
    //检查添加各种参数
    return true;//不跳过执行
}
```
若想同时创建前后处理器的接口实例，推荐使用使用ExecutionHandler

##### BeforeHandler接口
使用前处理器接口BeforeHandler声明序列请求，如果用这种方式绑定前处理器到序列请求，将无法再使用AfterHandler实例绑定到该序列请求，但是可以将@AfterMethod注解使用在方法上，来绑定后处理器到序列请求
```java
@SeqRequest(index = 7, name = "userinfo", description = "获取用户信息")//name默认为方法名（RequestChain的name默认为类名）
@Post("/otn/modifyUser/initQueryUserInfoApi")
@Parameter(name = "appid", value = "otn")
BeforeHandler checkUserinfo = new BeforeHandler() {
    @Override
    public Object beforeHandle(Request request, ExecutionContext context) throws IOException {
        return true;
    }
};
若想同时创建前后处理器的接口实例，推荐使用使用ExecutionHandler

@AfterMethod("userinfo")//指定SeqRequest的name
public boolean userinfo4(Request request, ExecutionContext context) {
    //检查添加各种参数
    return true;//不跳过执行
}
```
若想同时创建前后处理器的接口实例，推荐使用使用ExecutionHandler

##### RequestChain的处理器接口
直接让RequestChain类继承ChainExecutionHandler抽象类
@Host(value="kyfw.12306.cn",scheme="https")
@RequestChain(name="LoginChain",description="登陆请求链")
public class LoginChain extends ChainExecutionHandler {
    @Override
    public Object afterHandle(ExecutionContext context) throws IOException {
        return true;
    }

    @Override
    public Object beforeHandle(ExecutionContext context) throws IOException {
        return true;
    }
}
ChainExecutionHandler抽象类，实现了ChainBeforeHandler与ChainAfterHandler接口，可以根据需要为RequestChain实现前后处理器接口，如下例
```java
@Host(value="kyfw.12306.cn",scheme="https")
@RequestChain(name="LoginChain",description="登陆请求链")
public class LoginChain implements ChainBeforeHandler,ChainAfterHandler {
    @Override
    public Object afterHandle(ExecutionContext context) throws IOException {
        return true;
    }

    @Override
    public Object beforeHandle(ExecutionContext context) throws IOException {
        return true;
    }
}
```
或者在RequestChain内部创建一个ChainExecutionHandler实例，来为RequestChain绑定一个前后处理器，如下例
```java
@Host(value="kyfw.12306.cn",scheme="https")
@RequestChain(name="LoginChain",description="登陆请求链")
public class LoginChain {
    ChainExecutionHandler chainHandler=new ChainExecutionHandler() {
        @Override
        public Object beforeHandle(ExecutionContext context) throws IOException {
            return true;
        }

        @Override
        public Object afterHandle(ExecutionContext context) throws IOException {
            return true;
        }
    };
}
```
同样也可以单独根据需要创建ChainBeforeHandler与ChainAfterHandler的实例，来为RequestChain绑定前后处理器
```java
@Host(value="kyfw.12306.cn",scheme="https")
@RequestChain(name="LoginChain",description="登陆请求链")
public class LoginChain{
    ChainBeforeHandler beforeHandle = new ChainBeforeHandler() {
        @Override
        public Object beforeHandle(ExecutionContext context) throws IOException {
            return true;
        }
    };
    ChainAfterHandler afterHandle = new ChainAfterHandler() {
        @Override
        public Object afterHandle(ExecutionContext context) throws IOException {
            return true;
        }
    };
}
```

## 执行上下文
执行上下文ExecutionContext中存储了请求链中的所有参数，Cookie，以及SpringEl表达式中的参数，每个ExecutionContext实例中都会包含一个[FormParamStore](#FormParamStore)、[ContextParamStore](#ContextParamStore)、CookieStore、Executor

### 请求链上下文
请求链上下文ChainContext继承自ExecutionContext，是执行请求链的必需参数。拥有一个请求链的实体对象，比ExecutionContext多了一些对于请求链的操作。

### 表单参数库
表单参数库FormParamStore用于存储请求链中的所有的Http请求参数，每个请求链只拥有一个FormParamStore，可以作为前后处理器的参数，可以使用其来添加Http请求参数，并作用到整个请求链。
#### 自动从库中读值
注解在序列请求下的@Parameter，只指定了Http参数名，未指定值时，需要向FormParamStore添加一个相同Http参数名，并被赋值的参数，若FormParamStore中不存在，则为空值，如下例中的answer参数:
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
#### 参数不同名但值相同
当两个Http参数名不同，但是参数的值一致时，例如有个参数名为answer1，又有个参数为answer2，他们两个的值是相同的，可以使用@Parameter注解的globalKey，如下例:
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
#### 参数同名但值不相同
当两个Http参数名相同，但是参数的值却不同，例如有个Http参数名为answer，又有个参数也名为answer，但是他们两个的值是不相同的，可以使用@Parameter注解的uniqueKey，如下例:
```java
@SeqRequest(index = 3,description="检测验证码答案")
@Get("/passport/captcha/captcha-check")
public boolean captchaCheck(FormParamStore formParamStore){
    formParamStore.addParam("answer","result1");//添加登陆1中的answer值
    formParamStore.addParam("answer2","result2");//使用uniqueKey "answer2" 添加登陆2中的answer值
    return true;
}

@SeqRequest(index = 4,description = "登陆1")
@Post("/passport/web/login")
@Parameter(name="answer")//自动从FormParamStore中读取name为answer参数的值
public boolean login(String result) throws IOException {
    return true;
}

@SeqRequest(index = 5,description = "登陆2")
@Post("/passport/web/login")
@Parameter(name="answer",uniqueKey = "answer2")//自动从FormParamStore中读取name为answer2参数的值
public boolean login(String result) throws IOException {
    return true;
}
```

### 上下文参数库
上下文参数库ContextParamStore用于存储SpringEl表达式中的对象，SpringEl表达式一般用在链接，参数上，其他可用注解值请看下表
除了用在SpringEl上，ContextParamStore存储的对象，也可直接用在一些注解中，例如[@ForEach](#ForEach)注解。  
在Creeper中SpringEl表达式必需被${}包裹住，例如${time.now()}。  

#### SpringEl解析上下文
SpringEl解析也需要一个EvaluationContext上下文，用于解析字符串中的对象。  
EvaluationContext需要传入一个Context根对象，可在表达式中使用根对象中的属性与方法，ContextParamStore默认传入ContextRootObject作为根对象，可自定义传入任意对象作为根对象。  
ContextRootObject包含两个字段，TimeUtil中包含一些获取时间的工具类，context字段就是ContextParamStore的Map形式，
```java
private final TimeUtil time;
private final Map<String,Object> context;
```
在SpringEl表达式中可以直接调用TimeUtil中的方法，或者获取context中的值。如下  
${time.now()}  
${context.value1}  

#### 支持SpringEl表达式的注解属性
| 注解                      | 支持SpringEl的属性        |
| :------------------------ | :------------------------ |
| @Path/Get/Post/Put/Delete | url                       |
| @Parameter                | name/value                |
| @JsonResultCookie         | defaultValue              |
| @ForIndex                 | start/end                 |
| @While                    | conditionExpr             |
| @Trigger                  | startTimeExpr/endTimeExpr |
| @MultiRequestQueue        | stopConditionExpr         |  

## 控制执行顺序
通过后处理器的返回值，来控制一个请求链中请求的执行顺序，请求的后处理器返回值，决定了将要执行的下一请求。  
前处理器无法控制执行顺序，只能用来控制是否跳过当前请求的执行。
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

ContinueAction在前处理器中返回时，表示跳过当前循环执行，继续执行下一次循环。  
ContinueAction在后处理器中返回时，也表示继续执行下一循环，只不过当前循环已经执行了。  
循环的详细解释请看[循环执行](#循环执行)

### Boolean
后处理器可以返回Boolean类型的值，true表示继续执行下一请求等价于ForwardAction，false表示执行失败终结执行等价于TerminateAction
### 空值
后处理器的返回类型可以为void，也就是说返回值为null。当返回值为null时，表示继续执行下一请求等价于ForwardAction

## 循环执行
Loop注解不仅可以注解在序列请求上，也可以注解在请求链上，也就是说可以注解在任意序列对象上，将序列对象的执行包装一层循环
### 可用的Loop注解
| 注解                     | 解释                                                                                          |  
| :----------------------- | :-------------------------------------------------------------------------------------------- |
| [@While](#While)         | 传入一个SpringEl的boolean表达式，循环执行直至条件不匹配                                        |
| [@ForEach](#ForEach)     | 传入一个ContextParamStore中的集合对象的key，遍历该集合                                         |
| [@ForIndex](#ForIndex)   | 传入一个起始数字，一个结束数字，循环类似for(int i = start;i <= end; i++)，可使用SpringEl表达式  |
| [@Scheduler](#Scheduler) | 传入一个Trigger注解，将按照Trigger中的属性值来定期执行                                         |
### 跳出循环
- 返回BreakAction终止循环
- 返回任意其他可以移动的MoveAction来终止循环，包括:ForwardAction、BackAction、JumpAction、TerminateAction、RestartAction
- 返回false终止循环
### 继续循环
- 返回ContinueAction继续循环，在前处理器中返回时，会跳过当前循环的执行
- 后处理器中返回RetryAction继续循环，会重新执行当前的循环，循环变量不会发生改变，例如在ForEach中，将再次遍历同一个对象。
- 后处理器中返回true继续循环

### 示例
#### While
模拟不断请求12306余票页面
```java
@While(conditionExpr = "${#loopNum < 10}")//循环直至loopNum>=10
@SeqRequest(index =1,name="leftTicket",description="查询余票")
@Get("/otn/leftTicket/query")
@Parameters({
        @Parameter(name="leftTicketDTO.train_date",desc = "日期"),//dc
        @Parameter(name="leftTicketDTO.from_station",desc = "出发站"),//武汉,WHN
        @Parameter(name="leftTicketDTO.to_station",desc = "到达站"),//杭州,WHN
        @Parameter(name="purpose_codes",value = "ADULT")})
public MoveAction leftTicket(String result, ContextParamStore contextParamStore){
    Integer loopNum = (Integer) contextParamStore.getValue("loopNum");
    loopNum+=1;
    contextParamStore.addParam("loopNum",loopNum);//覆盖掉loopNum参数
    if(loopNum==5){//模拟抢到票
        return new ForwardAction();//跳出循环并执行下一请求
    }
    return new ContinueAction(1000);//继续循环
}
```

#### ForEach
某PDF网站的详情页遍历处理，完整代码请看[PDF电子书爬虫](#)
```java
@BeforeMethod("handlePDFBookDetial")
public boolean beforeHandlePDFBookDetial(ContextParamStore contextParamStore){
    Collection<String> urls = new HashSet<>();
    urls.add("http://detail1");
    urls.add("http://detail2");
    contextParamStore.addParam("pagePDFDetailUrls",urls);
    return true;
}

//itemsContextKey是ContextParamStore中的Collection接口的任意对象的key
//itemName是当前遍历对象在ContextParamStore中的key
@ForEach(itemsContextKey = "pagePDFDetailUrls", itemName = "detailUrl")
@SeqRequest(index = 1, description = "处理详情页面")
@Get(value = "${#detailUrl}", urlInheritable = false)
//${#detailUrl}在每次循环中将会解析出不同的url并执行，第一次是http://detail1，第二次是http://detail2
public MoveAction handlePDFBookDetial(String result, ContextParamStore contextParamStore) throws IOException {
    Object detailUrl = contextParamStore.getValue("detailUrl");//获取当前遍历的对象
    Document rootPage = Jsoup.parse(result);
    DZSWService.handlePDFDetail(rootPage, contextParamStore);//处理详情页面
    return new ContinueAction(100);
}
```

#### ForIndex
传入一个起始数字start，一个结束数字end，循环类似for(int i = start;i <= end; i++)，执行前会把i的值放在ContextParamStore中，indexName为i值的key，下例中index的值就是当前循环中i的值
```java
@RequestChain
@Host(value = "www.xgv5.com", scheme = "https")
public class PageHandleChain {
    @ForIndex(start = "1", end = "10",indexName = "index")//默认indexName为index，可以省略掉indexName = "index"
    @SeqRequest(index = 1, description = "处理列表页面")
    @Get("/category-30${#index==1?'':'_'+#index}.html")
    //index=1时的url[http://www.xgv5.com/category-30]
    //index=2时的url[http://www.xgv5.com/category-30_2]
    //index=3时的url[http://www.xgv5.com/category-30_3]
    //...
    public MoveAction handlePDFListBook(String result, ContextParamStore contextParamStore) {
        Document rootPage = Jsoup.parse(result);
        DZSWService.handlePDFListBook(rootPage, contextParamStore);
        return MoveActions.FORWARD();
    }
}
```
#### Scheduler
下例中的12306登陆请求链将重复执行10次，每次间隔1秒，延迟5秒后开始，直至时间到endTime时结束执行
```java
@Scheduler(
    trigger = @Trigger(
        startTimeExpr = "${time.now()}",//开始时间
        endTimeExpr = "${endTime}",//结束时间
        timeInterval = 1000,//每次执行间隔
        repeatCount = 10,//执行次数
        delay = 5000//延迟5秒执行
    )
)
@Host(value="kyfw.12306.cn",scheme="https")
@RequestChain(name="LoginChain",description="登陆请求链")
public class LoginChain {
    //省略序列请求
}
```
## 其他注解
### @JsonResultCookie
当序列请求注解了@JsonResultCookie，在执行过后，会自动获取Json类型的响应数据中的某个json键对应的json值，然后将此值生成一个Cookie添加到CookieStore中，如下例
```java
@SeqRequest
@Get("/otn/HttpZF/logdevice?algID=ZGB0eNTCXV&hashCode=s-hLl13iA3-UAXc9O4cfNSsDk203zmJffFi5kG43fxE&FMQw=0&q4f3=zh-CN&VySQ=FGEEJev5tTvG6q3axISQE1DJ36r7gqiH&VPIf=1&custID=133&VEek=unknown&dzuS=0&yD16=0&EOQP=4902a61a235fbb59700072139347967d&jp76=52d67b2a5aa5e031084733d5006cc664&hAqN=Win32&platform=WEB&ks0Q=d22ca0b81584fbea62237b14bd04c866&TeRS=824x1536&tOHY=24xx864x1536&Fvje=i1l1o1s1&q5aJ=-8&wNLf=99115dfb07133750ba677d055874de87&0aew=Mozilla/5.0%20(Windows%20NT%2010.0;%20Win64;%20x64)%20AppleWebKit/537.36%20(KHTML,%20like%20Gecko)%20Chrome/80.0.3987.116%20Safari/537.36&E3gR=4230a15ab4eb447d31ce29cfff1c2961")
@Parameter(name = "timestamp",value = "${time.now()}")
    //把Json结果中key为dfp的值，视为一个名为RAIL_DEVICEID的Cookie，并添加至CookieStore
    @JsonResultCookie(jsonKey ="dfp",name = "RAIL_DEVICEID",domain = ".12306.cn",cache = true),
    //把Json结果中key为exp的值，视为一个名为RAIL_EXPIRATION的Cookie，并添加至CookieStore
    @JsonResultCookie(jsonKey ="exp",name = "RAIL_EXPIRATION",domain = ".12306.cn",cache = true)})
public Object deivceCookie(FormParamStore paramStore) {
    return true;//返回true表示执行成功，继续执行下一请求
}

```
### @FileRecordsIgnore
当一个在循环中的序列请求注解了@FileRecordsIgnore，  
在序列请求的循环执行前，将读取存储在指定文件中历史链接，若当前链接是历史链接，则跳过当前执行。  
在序列请求的循环执行后，将把请求的链接存储在指定文件中。  
下例循环执行中，每个被执行过的请求链接，都将存储在demo.txt中，若程序终止，下次运行该程序时，将跳过已经执行过的请求。
```java
@ForEach(itemsContextKey = "pagePDFDetailUrls", itemName = "detailUrl")
@SeqRequest(index = 2, description = "处理详情页面")
@Get(value = "${#detailUrl}", urlInheritable = false)
@FileRecordsIgnore(filePath = "D:\\repository\\traiker\\records\\demo.txt")
public MoveAction handlePDFBookDetial(String result, ContextParamStore contextParamStore) throws IOException {
    Document rootPage = Jsoup.parse(result);
    DZSWService.handlePDFDetail(rootPage, contextParamStore);
    return new ContinueAction(100);
}
```

