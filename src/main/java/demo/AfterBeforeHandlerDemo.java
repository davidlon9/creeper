package demo;

import com.dlong.creeper.annotation.Host;
import com.dlong.creeper.annotation.Parameter;
import com.dlong.creeper.annotation.handler.AfterMethod;
import com.dlong.creeper.annotation.handler.BeforeMethod;
import com.dlong.creeper.annotation.http.Post;
import com.dlong.creeper.annotation.seq.ChainReference;
import com.dlong.creeper.annotation.seq.RequestChain;
import com.dlong.creeper.annotation.seq.RequestReference;
import com.dlong.creeper.annotation.seq.SeqRequest;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.execution.handler.entity.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.lang.reflect.Method;

public class AfterBeforeHandlerDemo {
    //* SeqRequest类型注解后处理器
    @SeqRequest(index = 7, name = "userinfo", description = "获取用户信息")
    @Post("/otn/modifyUser/initQueryUserInfoApi")
    @Parameter(name = "appid", value = "otn")
    public boolean userinfo(HttpResponse response) {
        return true;
    }

    //前处理器
    @SeqRequest(index = 7, name = "userinfo", description = "获取用户信息")
    @Post("/otn/modifyUser/initQueryUserInfoApi")
    @Parameter(name = "appid", value = "otn")
    @BeforeMethod
    public boolean userinfo2(HttpResponse response) {
        return true;
    }


    //* 序列请求同时拥有前后处理器
    @SeqRequest(index = 7, name = "userinfo", description = "获取用户信息")//name默认为方法名（RequestChain的name默认为类名）
    @Post("/otn/modifyUser/initQueryUserInfoApi")
    @Parameter(name = "appid", value = "otn")
    //@AfterMethod 可以省略掉该注解，因为方法注解了@SeqRequest
    public boolean userinfo3(HttpResponse response) {
        //处理响应结果，若下一请求没有BeforeHandler，则必需在此时添加下一请求的动态参数。
        return true;//继续执行下一请求
    }

    @BeforeMethod("userinfo")//指定SeqRequest或RequestChain的name
    public boolean checkUserInfo(Request request, ExecutionContext context) {
        //检查添加各种参数
        return true;//不跳过执行
    }


    //* RequestChain中的前后处理器
    @RequestChain(name = "ChainName", description = "演示")//name默认为类名ChainBeforeAfterHandlerDemo
    public class ChainBeforeAfterHandlerDemo {
        @BeforeMethod("ChainName")//指定RequestChain的name
        public boolean chainBeforeHandle(ExecutionContext context) {
            //检查添加各种参数
            return true;//不跳过执行
        }

        @AfterMethod("ChainName")//指定RequestChain的name
        public boolean chainAfterHandle(ExecutionContext context) {
            //执行结束的处理
            return true;//继续执行下一请求
        }
    }


    //* 引用请求与请求链
    @Host(value = "kyfw.12306.cn", scheme = "https")
    @RequestChain(description = "订单请求链")
    public class OrderChain {
        @ChainReference(index = 1)
        LoginChain login12306;//12306登陆请求链

        @RequestReference(index = 2,chainClass = LoginChain.class, requestName = "deviceCookie")
        Method deviceCookie;//12306登陆请求链中获取必要Cookie的请求
    }

    //* Handler接口
    @SeqRequest(index = 7, name = "userinfo", description = "获取用户信息")//name默认为方法名（RequestChain的name默认为类名）
    @Post("/otn/modifyUser/initQueryUserInfoApi")
    @Parameter(name = "appid", value = "otn")
    BeforeHandler checkUserinfo = new BeforeHandler() {
        @Override
        public Object beforeHandle(Request request, ExecutionContext context) throws IOException {
            return true;
        }
    };

    @AfterMethod("userinfo")//指定SeqRequest的name
    public boolean userinfo4(Request request, ExecutionContext context) {
        //检查添加各种参数
        return true;//不跳过执行
    }

    //ExecutionHandler
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

    //ChainExecutionHandler
//    @Host(value="kyfw.12306.cn",scheme="https")
//    @RequestChain(name="LoginChain",description="登陆请求链")
//    public class LoginChain {
//        ChainExecutionHandler chainHandler=new ChainExecutionHandler() {
//            @Override
//            public Object beforeHandle(ExecutionContext context) throws IOException {
//                return true;
//            }
//
//            @Override
//            public Object afterHandle(ExecutionContext context) throws IOException {
//                return true;
//            }
//        };
//    }

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


}
