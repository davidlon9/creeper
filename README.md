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
上述例子对问题的暴露可能不够明显，可以参考我写的使用HttpClient-Fluent写的[12306爬虫程序](https://github.com/davidlon9/creeper/blob/master/src/main/java/demo/traiker/main/oldway/LoginByCode.java)


