package demo;

import com.alibaba.fastjson.JSONObject;
import com.dlong.creeper.annotation.Parameter;
import com.dlong.creeper.annotation.Parameters;
import com.dlong.creeper.annotation.handler.AfterMethod;
import com.dlong.creeper.annotation.handler.BeforeMethod;
import com.dlong.creeper.annotation.http.Get;
import com.dlong.creeper.annotation.http.Post;
import com.dlong.creeper.annotation.seq.RequestChain;
import com.dlong.creeper.annotation.seq.SeqRequest;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.execution.context.FormParamStore;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;

public class HttpClientFluentDemo {
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

    //当前序列请求的执行顺序为3
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
