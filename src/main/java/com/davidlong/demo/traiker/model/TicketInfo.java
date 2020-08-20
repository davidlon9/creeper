package com.davidlong.demo.traiker.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TicketInfo {
    private static SimpleDateFormat normalFormatter = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat cstFormatter = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT+0800' (中国标准时间)", Locale.ENGLISH);

    private String trainNo;
    private String trainCode;
    private String seatType;
    private String secretStr;
    private String ypInfo;
    private String trainLocation;
    private String trainDate;

    public TicketInfo() {
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    public String getSecretStr() {
        return secretStr;
    }

    public void setSecretStr(String secretStr) {
        this.secretStr = secretStr;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public String getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public String getYpInfo() {
        return ypInfo;
    }

    public void setYpInfo(String ypInfo) {
        this.ypInfo = ypInfo;
    }

    public String getTrainLocation() {
        return trainLocation;
    }

    public void setTrainLocation(String trainLocation) {
        this.trainLocation = trainLocation;
    }

    public String getTrainDate() {
        return trainDate;
    }

    public void setTrainDate(String startTrainDate) {
        try {
            Date parse = normalFormatter.parse(startTrainDate);
            this.trainDate = cstFormatter.format(parse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
