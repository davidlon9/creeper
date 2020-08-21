# Creeper
java annotation oriented crawler framework base on http client, to focus on http response data resolve

## 简介
基于HttpClient的面向Java注解编程的爬虫框架，将关注点放在Http请求响应的数据解析上，使爬虫开发更迅速、简洁、优雅，且拥有更高的维护性。    

可以使用注解注明一个Http请求的链接、参数、请求头、Cookie等信息，支持SpringEl解析。  
将注解标记在方法上，将方法视为一个Http请求，方法内容用于处理请求后的Http响应，通过包装的执行器来执行请求。  

Creeper也可以将一个类中的请求按顺序组成一个请求链，然后按顺序执行，并可以通过注解再度包装请求或请求链，使单个请求或请求链以循环执行、多线程执行等。
