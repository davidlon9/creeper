package demo;

import com.alibaba.fastjson.JSONObject;
import com.dlong.creeper.annotation.*;
import com.dlong.creeper.annotation.handler.AfterMethod;
import com.dlong.creeper.annotation.handler.BeforeMethod;
import com.dlong.creeper.annotation.http.Get;
import com.dlong.creeper.annotation.http.Post;
import com.dlong.creeper.annotation.seq.RequestChain;
import com.dlong.creeper.annotation.seq.SeqRequest;
import com.dlong.creeper.control.MoveAction;
import com.dlong.creeper.control.MoveActions;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.execution.context.FormParamStore;

import java.io.IOException;

public class DocDemos {
    //LoginChain Demo
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

}
