package demo.traiker.main.creeper;

import com.alibaba.fastjson.JSONObject;
import com.dlong.creeper.annotation.*;
import com.dlong.creeper.annotation.http.Get;
import com.dlong.creeper.annotation.seq.RequestChain;
import com.dlong.creeper.annotation.seq.SeqRequest;
import com.dlong.creeper.execution.context.ContextParamStore;
import com.dlong.creeper.execution.context.FormParamStore;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;

import java.io.IOException;

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
