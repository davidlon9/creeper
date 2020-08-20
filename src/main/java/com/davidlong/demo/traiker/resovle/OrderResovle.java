package com.davidlong.demo.traiker.resovle;

import com.alibaba.fastjson.JSONObject;
import com.davidlong.demo.traiker.AppConsts;
import com.davidlong.creeper.util.JsoupUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OrderResovle {

    public static String getRepeatSubmitToken(String html){
        Document parse = Jsoup.parse(html);
        Element head = parse.head();
        Elements script = head.getElementsByTag("script");
        Element element = script.get(0);
        if(element!=null){
            html = element.html();
            return JsoupUtil.getVarValue(html, AppConsts.VARNAME_REPEATSUBMITTOKEN);
        }
        return null;
    }

    public static String getLeftTicketStr(String html){
        Document parse = Jsoup.parse(html);
        Elements preserves = parse.getElementsByAttributeValue("xml:space", "preserve");
        Element element = preserves.get(4);
        String ticketInfoForPassengerForm = JsoupUtil.getVarValue(element.html(), "ticketInfoForPassengerForm");
        if(ticketInfoForPassengerForm!=null){
            return JSONObject.parseObject(ticketInfoForPassengerForm).getString("leftTicketStr");
        }
        return null;
    }
}
