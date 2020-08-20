package com.davidlong.creeper.util;

import java.util.Calendar;

public class CalendarUtil{
    public static final String TYPE_YEAR="year";
    public static final String TYPE_MONTH="month";
    public static final String TYPE_DAY="day";
    public static final String TYPE_HOUR="hour";
    public static final String TYPE_MINUTE="minute";
    public static final String TYPE_SECOND="second";
    public static final String TYPE_MILLISECOND="millisecond";

    public static Calendar getInstance(){
        return Calendar.getInstance();
    }

    public static Calendar getInstance(int field,int value){
        Calendar instance = Calendar.getInstance();
        instance.set(field,value);
        return instance;
    }

    public static Calendar getInstance(int hour,int minute,int second){
        Calendar instance = Calendar.getInstance();
        set(instance,hour,minute,second);
        return instance;
    }
    public static Calendar getInstance(int year,int month,int day,int hour,int minute,int second){
        Calendar instance = Calendar.getInstance();
        set(instance,year,minute,day,hour,minute,second);
        return instance;
    }

    public static Calendar set(Calendar calendar,int hour,int minute,int second){
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,second);
        return calendar;
    }

    public static Calendar set(Calendar calendar,int year,int month,int day,int hour,int minute,int second){
        calendar.set(year,month,day,hour,minute,second);
        return calendar;
    }

    public static Calendar reset(Calendar calendar,String type,boolean recursion){
        if(TYPE_MONTH.equals(type)){
            calendar.set(Calendar.MONTH,0);
            return recursion?reset(calendar,TYPE_HOUR,recursion):calendar;
        }else if(TYPE_DAY.equals(type)){
            calendar.set(Calendar.DAY_OF_MONTH,1);
            return recursion?reset(calendar,TYPE_HOUR,recursion):calendar;
        }else if(TYPE_HOUR.equals(type)){
            calendar.set(Calendar.HOUR_OF_DAY,0);
            return recursion?reset(calendar,TYPE_MINUTE,recursion):calendar;
        }else if(TYPE_MINUTE.equals(type)){
            calendar.set(Calendar.MINUTE,0);
            return recursion?reset(calendar,TYPE_SECOND,recursion):calendar;
        }else if(TYPE_SECOND.equals(type)){
            calendar.set(Calendar.SECOND,0);
            return recursion?reset(calendar,TYPE_MILLISECOND,recursion):calendar;
        }else if(TYPE_MILLISECOND.equals(type)){
            calendar.set(Calendar.MILLISECOND,0);
            return calendar;
        }else{
            System.err.println("CalendarUtil warn:type "+type+" not support calendar dose't change");
            return calendar;
        }
    }

    public static Calendar reset(Calendar calendar,String type){
        return reset(calendar,type,true);
    }
}
