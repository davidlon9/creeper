package com.davidlong.demo.traiker.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StaticFormContext {
    private static final Map<String,String> params = new ConcurrentHashMap<>();
    private static final Map<String,String> sameValueParamsKeyMap = new HashMap<>();

    static {
        params.put("undefined","");
        params.put("_json_att","");
        params.put("whatsSelect","1");
        params.put("REPEAT_SUBMIT_TOKEN","?");
        params.put("back_train_date","?");
        params.put("bed_level_order_num","?");
        params.put("cancel_flag","?");
        params.put("choose_seats","?");
        params.put("dwAll","?");
        params.put("key_check_isChange","?");
        params.put("oldPassengerStr","?");
        params.put("passengerTicketStr","?");
        params.put("orderSequence_no","?");
        params.put("purpose_codes","?");
        params.put("query_from_station_name","?");
        params.put("query_to_station_name","?");
        params.put("randCode","?");
        params.put("random","?");
        params.put("roomType","?");
        params.put("seatDetailType","?");
        params.put("seatType","?");
        params.put("secretStr","?");
        params.put("stationTrainCode","?");
        params.put("fromStationTelecode","?");
        params.put("toStationTelecode","?");
        params.put("train_date","?");
        params.put("train_location","?");
        params.put("train_no","?");

        params.put("tourFlag,tour_flag","?");
        params.put("leftTicket,leftTicketStr","?");

        for (String key : params.keySet()) {
            if(key.contains(",")){
                String[] split = key.split(",");
                for (String s : split) {
                    sameValueParamsKeyMap.put(s,key);
                }
            }
        }
    }

    public static String getValue(String key){
        String v = params.get(getContextKey(key));
        return isNullValue(v) ? null : v;
    }

    public static String putValue(String key,String value){
        return params.put(getContextKey(key),value == null ? "?" : value);
    }

    public static boolean containsKey(String key){
        return params.containsKey(getContextKey(key));
    }

    public static String getContextKey(String key) {
        if(sameValueParamsKeyMap.containsKey(key)){
            return sameValueParamsKeyMap.get(key);
        }
        return key;
    }

    private static boolean isNullValue(String value){
        return "?".equals(value);
    }

    public static void main(String[] args) {
        System.out.println(getValue("whatsSelect"));
    }
}
