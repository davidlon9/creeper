package com.davidlong.traiker.test;

import com.alibaba.fastjson.JSONObject;
import com.davidlong.creeper.model.Param;
import com.davidlong.creeper.util.LogUtil;
import com.davidlong.traiker.AppUrl;
import com.davidlong.traiker.Env;
import com.davidlong.traiker.resovle.CallbackParam;
import com.davidlong.traiker.resovle.CaptchaImage;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import java.io.*;
import java.util.*;

public class TestLogin {
   static LogUtil log= new LogUtil();

    public static final CookieStore cookieStore = new BasicCookieStore();
    public static final Executor executor = Executor.newInstance().use(cookieStore);

    public static void main(String[] args) {

        CallbackParam callbackParam = new CallbackParam();
        try {
            String u1="https://kyfw.12306.cn"+ Env.deivceCookieUrl+"&timestamp="+new Date().getTime();
            HttpResponse response4 = executor.execute(Request.Get(u1)).returnResponse();
            log.info(response4);
            String data = EntityUtils.toString(response4.getEntity());
            data=data.substring(data.indexOf("'")+1,data.lastIndexOf("'"));
//        a.substring(a.indexOf('{'),a.lastIndexOf('}') + 1)

            JSONObject json = JSONObject.parseObject(data);
            BasicClientCookie basicClientCookie = new BasicClientCookie("RAIL_DEVICEID", json.getString("dfp"));
//            BasicClientCookie basicClientCookie = new BasicClientCookie("RAIL_DEVICEID","ZT_ZjKDsP476h8dfWg2eshaBjk-LoKFmQpjiqe6kZHGSwZiNhHS7ezGwm_KJCZSXFSC0t5OQYSrWH0dzEsgxpJlHiwAq3eLQA79hQGVv1Hn07hE1ghddlpIw6rGEKHSigpqyuy05JNjjxfxoMrh3_i017TnxXfgw");
            basicClientCookie.setDomain(".12306.cn");
            basicClientCookie.setAttribute("domain",".12306.cn");
            basicClientCookie.setAttribute("path","/");
            cookieStore.addCookie(basicClientCookie);

            BasicClientCookie basicClientCookie1 = new BasicClientCookie("RAIL_EXPIRATION", json.getString("exp"));
            basicClientCookie1.setDomain(".12306.cn");
            basicClientCookie1.setAttribute("domain",".12306.cn");
            basicClientCookie1.setAttribute("path","/");
            cookieStore.addCookie(basicClientCookie1);

            long st=System.currentTimeMillis();
            HttpResponse response1 = executor.execute(AppUrl.Login.CAPTCHA_IMAGE.paramValues(callbackParam.getExtraParamValues()).buildRequest()).returnResponse();
            String s = EntityUtils.toString(response1.getEntity());
            System.out.println("usetime:"+(System.currentTimeMillis()-st));
            if(!"".equals(s)){
                s=s.substring(s.indexOf("(")+1,s.lastIndexOf(")"));
                JSONObject jsonObject = JSONObject.parseObject(s);
                String image = jsonObject.getString("image");
                CaptchaImage.createImage(image, "C:\\Users\\74494\\Desktop\\a.png");
            }

            System.out.println(s);
            System.out.println("请输入正确图片编号");
            Scanner scanner = new Scanner(System.in);
            String imageIdxs = scanner.nextLine();
            String answer = CaptchaImage.getAnswer(imageIdxs);

            HttpResponse response2 = executor.execute(AppUrl.Login.CAPTCHA_CHECK.paramValues(answer, callbackParam.getExtraParamValues()).buildRequest()).returnResponse();
            log.info(response2);


            //登录
            Request loginRequest = AppUrl.Login.LOGIN.params(
                    new Param("appid", "otn"),
                    new Param("usernameVal", "qq744941378"),
                    new Param("passwordVal", "long123481"),
                    new Param("answer", answer)).buildRequest();

            System.out.println(loginRequest.toString());
            HttpResponse response3 = executor.execute(loginRequest).returnResponse();
            log.info(response3);


            Request request2 = AppUrl.Login.UAMTK.buildRequest();
            System.out.println(request2);
            //获取token
            HttpResponse response6 = executor.execute(request2).returnResponse();
            log.info(response6);
            String s1 = EntityUtils.toString(response6.getEntity());
            JSONObject tokenJson = JSONObject.parseObject(s1);

            BasicClientCookie token=new BasicClientCookie("tk",tokenJson.getString("newapptk"));
            token.setDomain("kyfw.12306.cn");
            token.setAttribute("path","/otn");
            cookieStore.addCookie(token);

            //获取用户信息
            HttpResponse response8 = executor.execute(AppUrl.Login.USERINFO.buildRequest()).returnResponse();
            log.info(response8);

            for (Cookie cookie : cookieStore.getCookies()) {
                System.out.println(cookie);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
