package demo.traiker;

import demo.traiker.model.AcceptHourTime;

import java.util.Arrays;
import java.util.List;

public class UserConfig {
    public static final List<String> passengerNames= Arrays.asList("xxx");

    public static final String[] acceptTicketType={StringConsts.EDZ,StringConsts.YW};

    public static final AcceptHourTime acceptHourTime = new AcceptHourTime(0,24,8,24);
}
