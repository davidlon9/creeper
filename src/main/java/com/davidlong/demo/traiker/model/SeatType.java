package com.davidlong.traiker.model;

import com.davidlong.traiker.StringConsts;

import java.util.LinkedHashMap;
import java.util.Map;

public class SeatType {
    public static final Map<String, String> CODE_MAPPING = new LinkedHashMap<String, String>();
    public static final Map<String, Integer> SEAT_INDEX_MAPPING = new LinkedHashMap<String, Integer>();

    /** 特等座 **/
    public static final String TDZ="P";
    /** 商务座 **/
    public static final String SWZ="9";
    /** 软卧 **/
    public static final String RW="4";
    /** 一等卧 **/
    public static final String YDW="4";
    /** 硬卧 **/
    public static final String YW="3";
    /** 二等卧 **/
    public static final String EDW="3";
    /** 软座 **/
    public static final String RZ="2";
    /** 硬座 **/
    public static final String YZ="1";
    /** 硬座无座 **/
    public static final String YZ_WZ="1";
    /** 二等座 **/
    public static final String EDZ="O";
    /** 二等座无座 **/
    public static final String EDZ_WZ="O";
    /** 一等座 **/
    public static final String YDZ="M";

    static{
        CODE_MAPPING.put("特等座", "P");
        CODE_MAPPING.put("商务座", "9");
        CODE_MAPPING.put("软卧一等卧", "4");
        CODE_MAPPING.put("硬卧二等卧", "3");
        CODE_MAPPING.put("软座", "2");
        CODE_MAPPING.put("硬座", "1");
        CODE_MAPPING.put("硬座或无座", "1");
        CODE_MAPPING.put("二等座", "O");
        CODE_MAPPING.put("二等座或无座", "O");
        CODE_MAPPING.put("一等座", "M");

        SEAT_INDEX_MAPPING.put(StringConsts.GJRW,21);
        SEAT_INDEX_MAPPING.put(StringConsts.RW,23);
        SEAT_INDEX_MAPPING.put(StringConsts.YDW,23);
        SEAT_INDEX_MAPPING.put(StringConsts.RZ,24);
        SEAT_INDEX_MAPPING.put(StringConsts.TDZ,25);
        SEAT_INDEX_MAPPING.put(StringConsts.WZ,26);
        SEAT_INDEX_MAPPING.put(StringConsts.YW,28);
        SEAT_INDEX_MAPPING.put(StringConsts.EDW,28);

        SEAT_INDEX_MAPPING.put(StringConsts.YZ,29);
        SEAT_INDEX_MAPPING.put(StringConsts.EDZ,30);
        SEAT_INDEX_MAPPING.put(StringConsts.YDZ,31);
        SEAT_INDEX_MAPPING.put(StringConsts.SWZ,32);
        SEAT_INDEX_MAPPING.put(StringConsts.DW,33);
    }
}