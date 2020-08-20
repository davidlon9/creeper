package com.davidlong.http.util;

import java.util.HashMap;
import java.util.Map;

public class Print {
    public static void threadName(String content){
        System.out.println(Thread.currentThread().getName()+":"+content);
    }

    public static void threadName(String nameSuffix, String content){
        System.out.println(Thread.currentThread().getName()+nameSuffix+":"+content);
    }

    public static void threadNameAndWrap(String content,String wrapMarks){
        System.out.println(wrapMarks+" "+Thread.currentThread().getName()+":"+content+" "+wrapMarks);
    }

    public static void wrap(String content,String wrapMarks){
        System.out.println(wrapMarks+" "+content+" "+wrapMarks);
    }

    public static void main(String[] args) {
        Map<Integer,String> map=new HashMap<>();
        map.put(-1,"a");
        System.out.println(map.get(-1));
    }
}
