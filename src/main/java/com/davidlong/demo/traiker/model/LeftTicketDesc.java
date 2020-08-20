package com.davidlong.demo.traiker.model;

import com.davidlong.demo.traiker.StringConsts;
import com.davidlong.demo.traiker.UserConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public enum LeftTicketDesc {
    SecretStr(0,"SecretStr"),
    ButtonTextInfo(1,"备注"),
    TrainNo(2,"车次ID"),
    StationTrainCode(3,"车次"),
    StartStationTelecode(4,"起始站"),
    EndStationTelecode(5,"终点站"),
    FromStationTelecode(6,"出发站"),
    ToStationTelecode(7,"到达站"),
    StartTime(8,"出发时间"),
    ArriveTime(9,"到达时间"),
    Lishi(10,"历时"),
    CanWebBuy(11,"是否还有座"),
    YpInfo(12,"YpInfo"),
    StartTrainDate(13,"日期"),
    TrainSeatFeature(14,"座位类型"),
    LocationCode(15,"线路编号"),
    FromStationNo(16,"起始站台编号"),
    ToStationNo(17,"终点站台编号"),
    IsSupportCard(18,"是否支持身份证直接进站"),
    ControlledTrainFlag(19,"是否是被控制的车次"),

    GgNum(20,"gg"),//???
    GjrwNum(21,"高级软卧"),
    QtNum(22,"qt"),//???
    RwydwNum(23,"软卧一等卧"),
    RzNum(24,"软座"),
    TdzNum(25,"特等座"),
    WzNum(26,"无座"),
    YbNum(27,"硬?"),//???
    YwedwNum(28,"硬卧二等卧"),
    YzNum(29,"硬座"),

    EdzNum(30,"二等座"),
    YdzNum(31,"一等座"),
    SwzNum(32,"商务座"),
    DwNum(33,"动卧"),
    SupportTypes(34,"支持座位类型"),
    SeatTypes(35,"车辆座位类型"),
    ExchangeTrainFlag(36,"是否支持兑换"),
    HoubuTrainFlag(37,"是否后补车次"),
    HoubuSeatLimit(38,"后补座位上限");

    public int index;
    public String description;

    public static int getIdxByDesc(String desc){
        LeftTicketDesc[] values = LeftTicketDesc.values();
        for (LeftTicketDesc value : values) {
            if(value.description.contains(desc)){
                return value.index;
            }
        }
        return -1;
    }

    public static final Map<Integer,String> INDEX_MAPPING = new LinkedHashMap<>();

    static{
        INDEX_MAPPING.put(0,"SecretStr");
        INDEX_MAPPING.put(12,"YpInfo");

        INDEX_MAPPING.put(1,"备注");
        INDEX_MAPPING.put(2,"车次ID");
        INDEX_MAPPING.put(3,"车次");
        INDEX_MAPPING.put(4,"出发站");
        INDEX_MAPPING.put(5,"终点站");
        INDEX_MAPPING.put(6,"出发站");
        INDEX_MAPPING.put(7,"到达站");
        INDEX_MAPPING.put(8,"出发时间");
        INDEX_MAPPING.put(9,"到达时间");
        INDEX_MAPPING.put(10,"历时");
        INDEX_MAPPING.put(11,"是否还有座");
        INDEX_MAPPING.put(13,"日期");
        INDEX_MAPPING.put(14,"座位类型");
        INDEX_MAPPING.put(15,"线路编号");
        INDEX_MAPPING.put(16,"起始站台编号");
        INDEX_MAPPING.put(17,"终点站台编号");
        INDEX_MAPPING.put(18,"是否支持身份证直接进站");
        INDEX_MAPPING.put(19,"是否是被控制的车次");

        INDEX_MAPPING.put(20,"gg");//???
        INDEX_MAPPING.put(21,"高级软卧");
        INDEX_MAPPING.put(22,"qt");//???
        INDEX_MAPPING.put(23,"软卧一等卧");
        INDEX_MAPPING.put(24,"软座");
        INDEX_MAPPING.put(25,"特等座");
        INDEX_MAPPING.put(26,"无座");
        INDEX_MAPPING.put(27,"硬?");//???
        INDEX_MAPPING.put(28,"硬卧二等卧");
        INDEX_MAPPING.put(29,"硬座");

        INDEX_MAPPING.put(30,"二等座");
        INDEX_MAPPING.put(31,"一等座");
        INDEX_MAPPING.put(32,"商务座");
        INDEX_MAPPING.put(33,"动卧");
        INDEX_MAPPING.put(34,"支持座位类型");
        INDEX_MAPPING.put(35,"车辆座位类型");
        INDEX_MAPPING.put(36,"是否支持兑换");
        INDEX_MAPPING.put(37,"是否后补车次");
        INDEX_MAPPING.put(38,"后补座位上限");
    }

    LeftTicketDesc(int index, String description) {
        this.index=index;
        this.description=description;
    }

    public static List<String> parseStrDesc(String str){
        String[] split = str.split("\\|");
        for (String s : split) {
            
        }
        return null;
    }

    private static SimpleDateFormat normalFormatter = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat cstFormatter = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT+0800' (中国标准时间)", Locale.ENGLISH);

    public static String getTrainDate(String startTrainDate) {
        try {
            Date parse = normalFormatter.parse(startTrainDate);
            return cstFormatter.format(parse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startTrainDate;
    }
}
