package demo.traiker.main.creeper;

import com.alibaba.fastjson.JSONObject;
import com.davidlong.creeper.HttpUtil;
import com.davidlong.creeper.annotation.*;
import com.davidlong.creeper.annotation.http.Get;
import com.davidlong.creeper.annotation.http.Post;
import com.davidlong.creeper.annotation.result.JsonResultCookie;
import com.davidlong.creeper.annotation.result.JsonResultCookies;
import com.davidlong.creeper.annotation.seq.RequestChain;
import com.davidlong.creeper.annotation.seq.SeqRequest;
import com.davidlong.creeper.control.MoveAction;
import com.davidlong.creeper.control.MoveActions;
import com.davidlong.creeper.execution.RequestChainExecutor;
import com.davidlong.creeper.execution.context.ContextParamStore;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.execution.context.FormParamStore;
import com.davidlong.creeper.model.result.ExecutionResult;
import com.davidlong.creeper.model.seq.RequestChainEntity;
import com.davidlong.creeper.resolver.ChainsMappingResolver;
import com.davidlong.creeper.util.ResultUtil;
import demo.traiker.Env;
import demo.traiker.model.StationDesc;
import demo.traiker.resovler.CallbackParam;
import demo.traiker.resovler.CaptchaImage;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Host(value="kyfw.12306.cn",scheme="https")
@RequestChain(index =1,name="CaseChain",description="登陆请求链")
public class CaseChain {

    @SeqRequest(index =1,name="captchaCheck",description="检测验证码答案")
    @Get("/passport/captcha/captcha-check")
    @Parameters({
            @Parameter(name="login_site",value="E"),
            @Parameter(name="rand",value="sjrand"),
            @Parameter(name="answer"),
            @Parameter(name="callback"),
            @Parameter(name="_")})
    public boolean deivceCookie(JSONObject result,HttpResponse httpResponse, FormParamStore paramStore,ContextParamStore contextParamStore, CookieStore cookieStore) throws IOException {
        return true;
    }
}
