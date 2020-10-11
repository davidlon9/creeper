package demo;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;

public class HttpClientFluentDemo {
    public static void main(String[] args) throws IOException {
        //构建一个Request请求对象
        Request get = Request.Get("http://www.giftcat.com")
                .bodyForm(
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
    }
}
