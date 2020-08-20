package com.davidlong.creeper.util;


import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    public long now(){
        return System.currentTimeMillis();
    }

    public long timestamp(){
        return System.currentTimeMillis();
    }

    public long format(String format,String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = dateFormat.parse(date,pos);
        return strtodate.getTime();
    }

    public long yyyyMMddHHmmss(int year,int month,int day,int hour,int minute,int second){
        return date(year,month,day,hour,minute,second);
    }

    public long yyyyMMdd(int year,int month,int day){
        return date(year,month,day);
    }

    public long date(int year,int month,int day,int hour,int minute,int second){
        Calendar instance = Calendar.getInstance();
        month++;
        instance.set(year,month,day,hour,minute,second);
        return instance.getTimeInMillis();
    }

    public long date(int year,int month,int day){
        Calendar instance = Calendar.getInstance();
        month++;
        instance.set(year,month,day);
        return instance.getTimeInMillis();
    }

    public long HHmmss(int hour,int minute,int second){
        return time(hour,minute,second);
    }

    public long time(int hour,int minute,int second){
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR_OF_DAY,hour);
        instance.set(Calendar.MINUTE,minute);
        instance.set(Calendar.SECOND,second);
        return instance.getTimeInMillis();
    }

}
