package demo.traiker.resovle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import demo.traiker.StringConsts;
import demo.traiker.UserConfig;
import demo.traiker.model.AcceptHourTime;
import demo.traiker.model.LeftTicketDesc;
import demo.traiker.model.SeatType;
import demo.traiker.model.TicketInfo;

import java.util.*;

public class LeftTicketResovler {
    public static final Map<Integer,String> trainDesc = LeftTicketDesc.INDEX_MAPPING;
    //TODO 如果选择硬卧，但是不想要二等卧，未指定列车类型
    public static final List<Integer> accept_ticket_indexs = Arrays.asList(LeftTicketDesc.EdzNum.index,LeftTicketDesc.YwedwNum.index);

    public static AcceptHourTime acceptHourTime = UserConfig.acceptHourTime;

    public static TicketInfo getSpecificTrainIfHasTicket(List list,String trainCode,String... seatTypes){
        TicketInfo ticketInfo=new TicketInfo();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            String str = (String) list.get(i);
            String[] split = str.split(StringConsts.SPLITER);
            for (int j = 0; j < seatTypes.length; j++) {
                String canBuy = split[LeftTicketDesc.CanWebBuy.index];
                if(canBuy.equals(StringConsts.Y)){
                    Integer seatIdx = SeatType.SEAT_INDEX_MAPPING.get(seatTypes[j]);

                    String leftCount = split[seatIdx];
                    if(StringConsts.BLANK.equals(leftCount) || StringConsts.WU.equals(leftCount)){
                        continue;
                    }
                    String tc = split[LeftTicketDesc.StationTrainCode.index];
                    if(trainCode.equals(tc)){
                        ticketInfo=new TicketInfo();
                        setTicketInfo(ticketInfo,
                                tc,
                                split[LeftTicketDesc.SecretStr.index],
                                SeatType.CODE_MAPPING.get(seatTypes[j]),
                                split[LeftTicketDesc.TrainNo.index],
                                split[LeftTicketDesc.LocationCode.index],
                                split[LeftTicketDesc.YpInfo.index],
                                split[LeftTicketDesc.StartTrainDate.index]);
                        return ticketInfo;
                    }
                }
            }
        }
        return ticketInfo;
    }

    public static TicketInfo getBestSeatLeftTrain(List list, String... seatTypes){
        TicketInfo ticketInfo=new TicketInfo();
        int size = list.size();
        int max=0;
        AcceptHourTime acceptHourTime = UserConfig.acceptHourTime;
        String bestTrainSecretStr;
        for (int i = 0; i < size; i++) {
            String str = (String) list.get(i);
            String[] split = str.split(StringConsts.SPLITER);
            for (int j = 0; j < seatTypes.length; j++) {
                String canBuy = split[LeftTicketDesc.CanWebBuy.index];
                if(canBuy.equals(StringConsts.Y)){
                    Integer seatIdx = SeatType.SEAT_INDEX_MAPPING.get(seatTypes[j]);

                    String leftCount = split[seatIdx];
                    if(StringConsts.BLANK.equals(leftCount) || StringConsts.WU.equals(leftCount)){
                        continue;
                    }
                    String st = split[LeftTicketDesc.StartTime.index];
                    String lishi = split[LeftTicketDesc.Lishi.index];
                    String[] sts = st.split(StringConsts.TIME_SPLITER);
                    String[] lishis = lishi.split(StringConsts.TIME_SPLITER);

                    int shour = Integer.parseInt(sts[0]);
                    int arriveHour = Integer.parseInt(lishis[0])+shour+1;

                    if(StringConsts.YOU.equals(leftCount)){
                        if(isAcceptTime(shour, arriveHour, acceptHourTime)){
                            System.out.println("catch train:"+split[LeftTicketDesc.StationTrainCode.index]);

                            setTicketInfo(ticketInfo,
                                    split[LeftTicketDesc.StationTrainCode.index],
                                    split[LeftTicketDesc.SecretStr.index],
                                    SeatType.CODE_MAPPING.get(seatTypes[j]),
                                    split[LeftTicketDesc.TrainNo.index],
                                    split[LeftTicketDesc.LocationCode.index],
                                    split[LeftTicketDesc.YpInfo.index],
                                    split[LeftTicketDesc.StartTrainDate.index]);
                            return ticketInfo;
                        }
                    }else if(isAcceptTime(shour, arriveHour, acceptHourTime)){
                        int count = Integer.parseInt(leftCount);
                        if(max<count){
                            max=count;
                            System.out.println("catch train:"+split[LeftTicketDesc.StationTrainCode.index]);
                            bestTrainSecretStr=split[LeftTicketDesc.SecretStr.index];

                            setTicketInfo(ticketInfo,
                                    split[LeftTicketDesc.StationTrainCode.index],
                                    bestTrainSecretStr,
                                    SeatType.CODE_MAPPING.get(seatTypes[j]),
                                    split[LeftTicketDesc.TrainNo.index],
                                    split[LeftTicketDesc.LocationCode.index],
                                    split[LeftTicketDesc.YpInfo.index],
                                    split[LeftTicketDesc.StartTrainDate.index]);
                        }
                    }
                }
            }
        }
        return ticketInfo;
    }

    private static void  setTicketInfo(TicketInfo ticketInfo, String trainCode, String secretStr, String seatType, String trainNo, String trainLocation, String ypInfo, String startTrainDate) {
        ticketInfo.setSeatType(seatType);
        ticketInfo.setTrainCode(trainCode);
        ticketInfo.setTrainNo(trainNo);
        ticketInfo.setTrainLocation(trainLocation);
        ticketInfo.setYpInfo(ypInfo);
        ticketInfo.setSecretStr(secretStr);
        ticketInfo.setTrainDate(startTrainDate);
    }


    private static boolean isAcceptTime(int shour, int arriveHour, AcceptHourTime acceptHourTime) {
        return shour>=acceptHourTime.getMinStartHour() && shour<=acceptHourTime.getMaxStartHour()  && arriveHour<=acceptHourTime.getMaxArriveHour() && arriveHour>=acceptHourTime.getMinArriveHour();
    }
    
    public static Map<String,String> getAcceptLeftTicket(JSONObject body){
        JSONArray result = body.getJSONObject("data").getJSONArray("result");
        for (int i = 0; i < result.size(); i++) {
            String line = result.getString(i);
            String[] attrs = line.split("\\|");
            for (int j = 0; j < attrs.length; j++) {
                String attr = attrs[j];
                if(accept_ticket_indexs.contains(j) && !attr.equals("无") && !attr.equals("") && (attr.equals("有") || Integer.parseInt(attr)>0)){
                    String start_time = attrs[8];
                    String[] split = start_time.split(":");
                    int time = Integer.parseInt(split[0]);
                    if(!(time>=acceptHourTime.getMinStartHour() && time<=acceptHourTime.getMaxArriveHour())){
                        System.out.println("catch but hour is not accept which is "+time);
                        continue;
                    }
                    Map<String, String> res = new LinkedHashMap<>();
                    for (int k = 0; k < attrs.length; k++) {
                        res.put(trainDesc.get(k),attrs[k]);
                    }
                    return res;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
    }
}
