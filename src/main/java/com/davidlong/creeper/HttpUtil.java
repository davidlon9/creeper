package com.davidlong.creeper;


import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HttpUtil {
    private static Logger logger= Logger.getLogger(HttpUtil.class);

    public static class ContentType{
        public static boolean isJson(String strType){
            return strType.contains(HttpConst.CONTENT_TYPE_JSON);
        }

        public static boolean isHTML(String strType){
            return strType.contains(HttpConst.CONTENT_TYPE_TEXT_HTML);
        }

        public static boolean isJson(HttpResponse response){
            return isJson(response.getEntity().getContentType().getValue());
        }

        public static boolean isHTML(HttpResponse response){
            return isHTML(response.getEntity().getContentType().getValue());
        }
    }

    public static void main(String[] args) {
        SimpleDateFormat normalFormatter = new SimpleDateFormat("yyyyMMdd");
        try {
            Date parse = normalFormatter.parse("20200224");
            SimpleDateFormat cstFormatter = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT+0800' (中国标准时间)", Locale.ENGLISH);
            String cstDate = cstFormatter.format(parse);
            System.out.println(cstDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
