package com.davidlong.demo.traiker;

import com.davidlong.demo.traiker.model.AcceptHourTime;

import java.util.Arrays;
import java.util.List;

public class UserConfig {
    public static final List<String> passengerNames= Arrays.asList("龙英杰");

    public static final String[] acceptTicketType={StringConsts.EDZ,StringConsts.YW};

    public static final AcceptHourTime acceptHourTime = new AcceptHourTime(0,24,8,24);
}
