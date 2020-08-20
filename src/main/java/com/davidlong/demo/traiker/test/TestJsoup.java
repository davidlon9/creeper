package com.davidlong.traiker.test;

import com.alibaba.fastjson.JSONObject;
import com.davidlong.creeper.util.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestJsoup {
    public static void main(String[] args) {
        String html = null;
        try {
            html = FileUtil.read("C:\\Users\\74494\\Desktop\\initDc.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document parse = Jsoup.parse(html);
        Elements script = parse.getElementsByTag("script");
        List<Element> res=new ArrayList<>();
        for (Element element : script) {
            String attr = element.attr("xml:space");
            Attributes attributes = element.attributes();
            if (attributes.size()==1 && "preserve".equals(attr)) {
                res.add(element);
            }
        }
        if(res.size()>0){
            Element target = res.get(res.size() - 1);
            String[] data = target.data().split("var ");
            for (String var : data) {
                if(var.contains("ticketInfoForPassengerForm=")){
                    String[]  split = var.split("=");
                    String trim = split[1].trim();
                    String str = trim.substring(0, trim.length()-1);
                    JSONObject json = JSONObject.parseObject(str);
                    String key_check_isChange = json.getString("key_check_isChange");
                    System.out.println(str);
                }
            }
        }

    }
}
