package com.davidlong.demo.traiker.resovle;

import com.alibaba.fastjson.JSONObject;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class DomResolve {
    public static JSONObject getKeyCheckIsChange(Elements scripts){
        List<Element> preserveScript=new ArrayList<>();
        for (Element element : scripts) {
            String attr = element.attr("xml:space");
            Attributes attributes = element.attributes();
            if (attributes.size()==1 && "preserve".equals(attr)) {
                preserveScript.add(element);
            }
        }
        if(preserveScript.size()>0){
            Element target = preserveScript.get(preserveScript.size() - 1);
            String[] data = target.data().split("var ");
            for (String var : data) {
                if(var.contains("ticketInfoForPassengerForm=")){
                    String[]  split = var.split("=");
                    String trim = split[1].trim();
                    String str = trim.substring(0, trim.length()-1);
                    JSONObject json = JSONObject.parseObject(str);
                    return json;
                }
            }
        }
        return null;
    }

    public static String getRepeatSubmitToken(Elements scripts) {
        Element element = scripts.get(0);
        String[] data = element.data().split("var");
        for (String var : data) {
            if(var.contains("globalRepeatSubmitToken")){
                String[]  split = var.split("=");
                String trim = split[1].trim();
                String token = trim.substring(1, trim.length()-2);
                return token;
            }
        }
        return null;
    }
}
