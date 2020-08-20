package com.davidlong.traiker.util;


import java.net.URLEncoder;

public class AppUtil {
    public static String encodeFromToStation(String str){
        String[] split = str.split(",");
        String s1 = Unicoder.cnToUnicode(split[0]).replaceAll("\\\\", "%");
        String s2 = URLEncoder.encode(","+split[1]);
        return s1+s2;
    }

    public static void main(String[] args) {
        String str1="https://kyfw.12306.cn/otn/leftTicket/query?leftTicketDTO.train_date=2019-05-05&leftTicketDTO.from_station=XFN&leftTicketDTO.to_station=HZH&purpose_codes=ADULT";
        String str2="https://kyfw.12306.cn/otn/leftTicket/query?&leftTicketDTO.train_date=2019-05-05&leftTicketDTO.from_station=WHN&leftTicketDTO.to_station=HGH&purpose_codes=ADULT";

    }
}
