package demo.traiker.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import demo.traiker.model.LeftTicketDesc;

import java.util.*;

public class Test {
//    SecretStr(0,"SecretStr"),
//    ButtonTextInfo(1,"备注"),
//    TrainNo(2,"车次ID"),
//    StationTrainCode(3,"车次"),
//    StartStationTelecode(4,"起始站"),
//    EndStationTelecode(5,"终点站"),
//    FromStationTelecode(6,"出发站"),
//    ToStationTelecode(7,"到达站"),
//    StartTime(8,"出发时间"),
//    ArriveTime(9,"到达时间"),
//    Lishi(10,"历时"),
//    CanWebBuy(11,"是否还有座"),
//    YpInfo(12,"YpInfo"),
//    StartTrainDate(13,"日期"),
//    TrainSeatFeature(14,"座位类型"),
//    LocationCode(15,"线路编号"),
//    FromStationNo(16,"起始站台编号"),
//    ToStationNo(17,"终点站台编号"),
//    IsSupportCard(18,"是否支持身份证直接进站"),
//    ControlledTrainFlag(19,"是否是被控制的车次"),
//
//    GgNum(20,"gg"),//???
//    GjrwNum(21,"高级软卧"),
//    QtNum(22,"qt"),//???
//    RwydwNum(23,"软卧一等卧"),
//    RzNum(24,"软座"),
//    TdzNum(25,"特等座"),
//    WzNum(26,"无座"),
//    YbNum(27,"硬?"),//???
//    YwedwNum(28,"硬卧二等卧"),
//    YzNum(29,"硬座"),
//
//    EdzNum(30,"二等座"),
//    YdzNum(31,"一等座"),
//    SwzNum(32,"商务座"),
//    DwNum(33,"动卧"),
//    SupportTypes(34,"支持座位类型"),
//    SeatTypes(35,"车辆座位类型"),
//    ExchangeTrainFlag(36,"是否支持兑换"),
//    HoubuTrainFlag(37,"是否后补车次"),
//    HoubuSeatLimit(38,"后补座位上限");

    public static final String SPACE_STR=" ";
    public static final Map<Integer,String> trainDesc= LeftTicketDesc.INDEX_MAPPING;
    public static final Map<String,String> ticketTypes=new LinkedHashMap<>();

    public static final List<Integer> checkIdxs=Arrays.asList(new Integer[]{11,14,17});
    public static final List<Integer> idxRange=Arrays.asList(new Integer[]{});
    public static final List<Integer> ignoreIdxs=Arrays.asList(new Integer[]{0});
    public static final List<Integer> afterPrintIdxs=Arrays.asList(new Integer[]{3});


    public static final List<Integer> unknownIdxs=new ArrayList<>();

    public static final Set<String> dupFilterSet15=new TreeSet<>();
    public static final Set<String> dupFilterSet16=new TreeSet<>();
    public static final Set<String> dupFilterSet17=new TreeSet<>();

    public static void main(String[] args) {
        StringBuilder sb=new StringBuilder("{\"httpstatus\":200,\"data\":{\"result\":[\"|列车停运|760000K5300O|K530|ICW|HZH|QEH|HZH|24:00|24:00|99:59|IS_TIME_NOT_BUY||20200228|null|W2|17|20|0|1|||||||||||||||||1|0|null||||||||\",\"|列车停运|710000K1500N|K150|ZJZ|SNH|QEH|HGH|24:00|24:00|99:59|IS_TIME_NOT_BUY||20200228|null|Z1|28|32|0|1|||||||||||||||||1|0|null||||||||\",\"wQViNfcediKPXmFQCyHTN9%2BHYrmjCXCQXPx21Id70aJ9X%2B71Ce%2FlCyHM1TXEVxyEPQcMqV6suBRK%0ANxW7VETlmhS79abwtlBHWdF34z9dBkPHht1u3gV7hs%2FSkbANnpDOqw5%2Bg1NxI9Sh1njs4WG9eezY%0A%2Bd%2BgeFlbOakXwDJSTgv9SWBLjt8U1xZvaNwGB8EWVjjLTIq8%2BUFbLaXVkoJ5eRylWE%2B%2FkId666lW%0A3wJsWccUQ6PKrqnqN7kdxC%2FtaeIWk0ljS7Vl%2Bre%2FJWisqlELTC1XeltqTaIrIbwPWqxArTo3zo78%0AtN%2BqLjSLQFU%3D|预订|57000K11870Q|K1186|JJG|SNH|QEH|HGH|01:05|04:23|03:18|N|bqnUXBDsNOtHnsIOxvi3SI9J%2FaQ5S%2FcDZ8RepUhS8xI1RSzr7CXFELY68ms%3D|20200229|3|G1|10|13|0|0||||无|||无||无|无|||||30401010|3411|1|1|||||||||\",\"|列车停运|42000K11280A|K1128|XWN|SNH|QEH|HZH|24:00|24:00|99:59|IS_TIME_NOT_BUY||20200229|null|N2|09|13|0|1|||||||||||||||||1|0|null||||||||\",\"Kix%2Btn%2BA2oTmY0dcIurG%2BpSZ5qeMNp%2FZtPo7GHW4Jj7aXifaAPq5SwkV49%2BhhjBzQ%2FuAfXmdiVYa%0AdG6FF9s4bHpUDKJGjGeYH5l%2B4yNVBMyxdNCdZQ4WuOAuyhDHFH2ep8oPReyKHBiiqRKaorw55qDy%0A%2F4DL%2FbV7VshfvGztBDkjx7ajPjbQtLVdGttwANdKt3E2GC2UgBIpGTTZrQ5qHMYK5MuEUMR7Ztb5%0AYZo3iEwXQ%2FDL9OHlltw6fVQkkAZGOvE9lBr1MgAGJR8Ucqm7uj20EFZY%2FkU2wEFem1%2BOIgd277Su%0AbKPP8sF0XJc%3D|预订|650000T2120J|T212|SZQ|SNH|QEH|HGH|02:43|05:44|03:01|Y|mioCEVOnqLC%2BrKRM9feM3XiQkgqyv%2BKf7muVezv4FEsiVKrkha9udcAw7zI%3D|20200229|3|Q7|09|12|0|0||||无|||无||无|1|||||40301010|4311|1|1|||||||||\",\"|列车停运|7100000T7830|T78|NNZ|SNH|QEH|HGH|24:00|24:00|99:59|IS_TIME_NOT_BUY||20200229|null|Z1|11|14|0|1|||||||||||||||||1|0|null||||||||\",\"QA2M9QHAm%2FXOm%2Ff9TX%2FlXhNofK4XXXC%2BpzS57t%2BW5CRdL%2FdR4lsOohyDSZXYP5Yyt508If4JRXF8%0A0pZVqr2xYjsAH%2Fu9PIXPvwXgag78tu%2Bw95%2FgcHiQFIzmoiXvffmIx6x2cHKNI9KaoVKmQF6aGsi6%0AaN19f1m6w6SZblFB1z%2FnFHFwSYLAI1c1Q92Ho2Q1mrLxgBSW04J3RvbsEe4DEGXzKCCpSfGKUK1%2B%0ABoainLM9UaxDJy1Kr1V4JD62y4kpLuaTIfx1vLN7Q%2FKCawuIFS2FCXTxTSFy3nxvxzCWVT9slPvh%0ARlEV6Q%3D%3D|预订|7700000K7309|K72|CUW|SNH|QEH|HGH|06:08|09:48|03:40|N|bqnUXBDsNOtHnsIOxvi3SI9J%2FaQ5S%2FcDZ8RepUhS8xI1RSzr7CXFELY68ms%3D|20200229|3|W2|15|19|0|0||||无|||无||无|无|||||30401010|3411|1|1|||||||||\",\"Zbd8H1AO8J5OKCKQx%2BVtvCKX3jQhMSOlwEgB1Bm4HDem8vEd%2BYiGYm7b%2FqBh5s%2B4T2KaHRNKP2%2FP%0AiETTnHSpVH6SMBz8iB%2FFuWlE0MGOe6aKrtuj%2F2C8QyhL1Q8aEuN3pP2lblhT9MB2XDmDFLebLS9a%0APovudEQet9N%2FA9v4SibJeOMD2kJHJKOJ201j6g8V833zB4XBKLR87ap8%2FHVQtIL0DAEz4DJzMx8w%0A%2FLcczpbHM7OvKsncVd4w8gepGm4kyyqwaCbL8QgxyOx5klBFpXUFJjgPJhxY2frncTKxQpUn35M8%0AnbtT9cgtjZE%3D|预订|56000G737216|G7372|JUH|ENH|QEH|HGH|06:40|08:00|01:20|Y|XnRaYctYYYkFu3wojLZOy9jywZbaPvUx7cWLdSCSNQF6BWQmWaa9ve2X9Y8%3D|20200301|3|H2|02|06|1|0|||||||无||||无|有|有||O0M090O0|OM9O|1|1|||||||||\",\"|列车停运|760000K3520N|K352|CDW|SNH|QEH|HGH|24:00|24:00|99:59|IS_TIME_NOT_BUY||20200228|null|W3|20|23|0|1|||||||||||||||||1|0|null||||||||\",\"UUkN5iXdjnQgEN8FLgiz30y1A8rS636ik52JTwCfWLTOnIUA1T9achHjq3d%2BdcDSmfSG%2BIkHpDoT%0AxHXL%2FWKj%2FXxXbitM%2BNe8q8Z648pX864FJNVEBQsfGYuPPKN0RqMtdkdUdON54aQPhSShL1jC7Wxj%0AbLQTNUPjAXrOX3Jssvu%2FG6uHsuOzh0RkUVhEA21ZBNSf4r5CAWXjoR9vT8YWDBJH%2FeP1Y3JtXhG3%0Ac2DYxI7YeAMqBiFrMBEoqvXkJyDiY93HvqJDntzwKJEibVLhjsF9R3gzxSGui6%2F5wV6y3WnA7IQ2%0Av%2BxfpI4qO0c%3D|预订|56000G738200|G7382|QEH|AOH|QEH|HGH|07:15|08:42|01:27|Y|VRBkcRSx2iKiU2MqzVJ8WhPz%2FDvJODw2PrHpZxwwKTZpRMOj6xYD6qt%2BA44%3D|20200301|3|H2|01|06|1|0|||||||无||||有|有|有||O0M090O0|OM9O|1|0|||||||||\",\"|列车停运|420000K2540G|K254|JMN|SNH|QEH|HGH|24:00|24:00|99:59|IS_TIME_NOT_BUY||20200229|null|N7|13|16|0|1|||||||||||||||||1|0|null||||||||\",\"lE2Xzr%2FZHyTpO6p%2Bapvs0ewvDlpHkVV20DvuiM5fTOXTJpgOhQw7McFB8%2FyUmX%2F0esaOioe40KJx%0AEd85%2FmqqZgE6uC6qh0qgO8kDJBIe6BylMsZcJGV9QsbY972QgIo0aNm%2Bl35ML4nRGjkqy6DQPq7x%0AdwdH5578AG2%2FwEvhJmf300qJ7mJ24w5oMnw0LDVlkU8mEHdyt7FeCmCywgDPPEPGxKUQGnqVXFgQ%0ACKOpw4139tvAuS%2FPIN1GZdo6f%2Blj4h8YKmm7kA%2B1wux9GQHvaW9f1N1TPXJw9pidqTegooxWB6bb%0ARmSQ0cuORes%3D|预订|780000K11208|K112|GIW|SNH|QEH|HGH|07:25|10:31|03:06|N|MQduDlOnYNOApwp560MfEWNbDmc6w5fNOHSvKX%2FeVlMq9zTam4HZjZc6Dr4%3D|20200229|3|W3|13|16|0|0||||无|||无||无|无|||||30104010|3141|0|1|||||||||\",\"w5ynLPLq6%2FMcMDb69DqihLE8AO%2Fr8q%2F3kcuXTIWLIoYiyq0EAOT3Kdw2Fp9ujg899QAt8AxdQWe2%0AT0Vhi8asEOQnWNDPLF3K1aVK32GUU77%2B8yhxj2aZ1eG4AbbJlSCFa7u2BT4cw0kvRb1e6fJsTPWi%0A0TKpLjquNoBrW%2BFqS8ToyM0mtN1NDgdLJ%2FrMIE0kxZTamMXhKfD2C6QsabU%2BoIhFL4Cd3ri1oGPr%0ALoSPbI6M4B17l0g3VP%2BB4z2PC3eerTWw7jVKMBEWuYKFUOzTaEoY7uf0fWxrhjEVaTADnzqQVdlN%0Ao0wXU%2BSwj3%2FH0N7I|预订|56000G7364E2|G7364|JUH|AOH|QEH|HGH|07:52|09:22|01:30|N|IU18aTzNeovFojf%2BC2BaQRMKCIC15NQew%2Bn0RhJkrPPBDdG%2FFMUdSbW3EBg%3D|20200301|3|H6|02|07|1|0|||||||无||||无|无|无||O0M090O0|OM9O|1|1|OM9||||||||\",\"us0Gt67D%2FWjTIvMeOM2H%2FeSLMmZ8dOw60Im6TrnxIInwrhxRDJrncatFE5tzToqVFzbU7i4r4ohb%0Aua6bmVjHwHC4gRCMVw5C01lLqqn%2FLfzhqreBwJsvSy9KB0uJ0oVglrNmH9fR%2FD5oTRhPlS8WJgJu%0A44I1%2Bh8%2BY%2BuTc1jzKAJohxhyNX9PIXXKfO7l%2Fm4Cgs%2F8FfMvGFFsPzCbD5s8MFkdnWeIlTQzwNL4%0AAhciKirK3BJ4iOpNMRuqg2yTydRb3aF1ao8EbTlEukjE379uaDery4jQoFG02eNIoHn4BCVoqLZn%0AmE6i4s9espgPet%2Bs|预订|56000G738670|G7386|QEH|ENH|QEH|HGH|08:11|09:31|01:20|N|IU18aTzNeovFojf%2BC2BaQRMKCIC15NQew%2Bn0RhJkrPPBDdG%2FFMUdSbW3EBg%3D|20200301|3|H6|01|05|1|0|||||||无||||无|无|无||O0M090O0|OM9O|1|1|OM9||||||||\",\"P10K4dnmiHCHUDiQHF1vwEKVUqmMMl%2FtzxIGmGYInN5TtwS%2B5PIy2sKNkNxtXwSxi%2F5eIXgo65Up%0Au86MoByTxjF%2B%2FGB5Ifi%2FCtJJE4NtoQv9Chpa2A8cyztiiuCZO%2Bmz%2BXhSKxaF6n8YvqAVi7A%2Bp7Md%0Aj8SjYyzof7l%2FXW%2BdGhbwZqr%2B%2FX6RXbghkwxQivrosdBfCUejzz3%2B55bdzDSdHhT%2FrlsMGihttELI%0AYxW%2FIcfvQrUqdZDDC55TIXV26oL27ymOUsv684YZcttqxsN3I2DTAInGuF4obqNkTG5mLMA%3D|预订|5600000G4650|G46|JUH|VNP|QEH|HGH|08:16|09:44|01:28|Y|mfxVFTR6sXbTj731L9NirD%2F3GzlTkkA8TZMCUZ79ogk17hed|20200301|3|H3|02|07|1|0|||||||||||有|有|14||O0M090|OM9|1|0|||||||||\",\"SVPzmL0d7dTA5wQwSGl1uB4sZcUneVtQoI6Rje%2FBO2S8%2FERqO9UM3RTfoEqmjNcag1E%2B%2BOxq%2Fdw3%0A0ypgs9eIrhk3nTLHE4kjZJt2r0jJQfzxDyq3FHSjQoP2pK5Vtxwhzg%2FfQ5jcPGsoev5qgHvpLNbY%0Ab%2F6CoaUElWKb765HzJ6xzEQ%2BELuyTIh9y%2Fhn399JKMBrudZMG1UvWtOBPTwrJ99klNR6rlk7A8mb%0Api%2FgwEv4WeLlsR0iVW%2FW%2BxrtldA%2BesLpbTYXc4uLC4x1nhYw0ollWH6Wbnu68%2F15Cw9LxYCu4yH8%0AIj3i6Z8oiz0%3D|预订|800000K7400P|K740|KMM|SNH|QEH|HGH|08:24|11:52|03:28|Y|fhLdfZuppQmbLFtxp4f%2FAoq8EEp4rxWLd4Rt7z5YEroWSSVzVgDTU9gI9gU%3D|20200228|3|M1|18|22|0|0||||无|||无||1|7|||||30104010|3141|1|1|||||||||\",\"hJ47ae4ZdcPzaxzdPOnh6eSv19AAQuT%2FT1%2FTA5%2FRMht79qmXrweKzcC9j28tfgAqAILVNroU%2B13A%0AA2YIrPg74w%2F3gRRhLG3xK3RQB5Si%2FByccfvWjO8DGzTzEKuA9CS5Y%2FcMP3oNhR1cK3pSmROlQrVv%0AEQCwddBKmASiDKEdlQbukpQkxhP3qfo620qVgGiglwrvArWd2bP2VrsPEJHef6Pzp3YjyM5uVVTV%0AsBRg429WqYQNOlZ2%2BZY%2Buo3SH13ibOQuRhpJALdL3CA%2BwoKCnZTq%2BYLPG7RsVyBFGHxJ8r%2FJioZp%0ASlDTsw%3D%3D|预订|7100000T820F|T82|NNZ|SNH|QEH|HGH|08:43|11:35|02:52|Y|DEtnPL3Etxa01Wez8kZpVHmr8ICezuYPoMoASN6EZhQXeZxl%2F5eu7cK8wy8%3D|20200229|3|Z1|20|24|0|0||||无|||无||1|1|||||30401010|3411|1|1|||||||||\",\"qDklrZhl1wVLaQhF1scfGzrf2J3y19EyEbZETVC8erwk2gi5dxYDj5XSK%2FfjmHijBPg8Cg0CUgCH%0A1Z7AHFFaci%2BZKlMpKSB26ZKZsIhoUGwgqjszPjObSCEsdsAU6LsqvE1R18XJ8Avrz5anmZUGOCXv%0AJuTTj58kuV2Ha7LXNoam2XIigEbRs6d7aFiFN8J5USbF%2FVqpt%2BWavQCT2DXH7oLwM%2BCKjNZUGjvP%0AisRWg87DYqnEqRcJqdjaUsDG2NnZaFsDnwfsPw7g2%2Bj4UqAybvk64ku0QPrxvGgy2e%2FP3BA%3D|预订|56000D548400|D5484|KHU|SNH|QEH|HGH|09:07|11:23|02:16|Y|oPgg0O1%2B0Tgzuo5KAR1einuaE5J3TI2mGJkIf%2BENb92MpAA%2B|20200301|3|H1|03|06|1|0|||||||无||||有|19|||O0M0O0|OMO|1|0|||||||||\",\"cEOLe4YjvHIXrTslg2fEhFEPMCxW30JhG6cJ7TiMki4gZeBdSMK81ldvYhR7YymEcJpXyu2eHQK6%0Abo4ldNoZvAsX3MotldINPpNzfHecKRc%2FVZqTP31YbtUTSxA3MIAaairkKDW%2BT6A42Dzlpu7doVq%2F%0A4LyLiP6jQJ6uP0clhjhh%2BCo5B9pRUw3VyVqY9%2FSmNB2oI86AxtGoA3dJDa2k1rB8rUOydJRA7jMk%0AxfQ8cBG9Si4WavM%2B27xL1xsx8XIcUR27AjCIqSiM6LUJf0pV4EPsUmSycsmV3C05J4p0FvE%3D|预订|57000G138202|G1382|NCG|AOH|QEH|HGH|09:13|10:27|01:14|Y|EDKmtafsIx3z6WOmRY99PccSdhGAqyZuApv4HZs82Qj9XCgQ|20200301|3|G2|05|08|1|0|||||||||||无|无|1||O0M090|OM9|1|1|||||||||\",\"6%2BttoYq1TkCqwW8uBOIO9jGMgb3hzSxZPTqHGA0Y0riEbw49O%2BqgUNhIuQXi30EyDLwuJvhCoyIt%0AEzkd0k1k1ZPPuOSvLOLhl1hPnfboE3v3Y9g05bYwBXdGiiC5Ge3AWl97OUicIqVqiLiBNKInB9Hs%0Abxe2Bw32tjpENGWez%2FmW6A87i4uvfP7e4u9HsPN%2FZSnBmDNBhd1e1MQp6lPiAR4g7n8r9q8owBtg%0Aw%2FuE%2BGi%2FLK%2Ffw71lofOFToJI96X1eKRMPZnwPUIotILmmd85ZrMQ0cQT8n02pBprOUgJMnKIGOw0%0AtSbu3eRdJeY%3D|预订|64000K13740C|K1374|HHQ|SNH|QEH|HGH|09:16|12:30|03:14|Y|lEUqO%2FUJCPV8soYxpagjqFv0n3PbwVQF7QonqIo2%2BbqgxRtohRuoxFKt8MQ%3D|20200229|3|Q9|14|17|0|0||||无|||无||6|9|||||40301010|4311|1|1|||||||||\",\"|列车停运|58000G16720A|G1672|FZS|NKH|QEH|HGH|24:00|24:00|99:59|IS_TIME_NOT_BUY||20200301|null|G2|06|09|0|1|||||||||||||||||1|0|null||||||||\",\"Y0Y4zUuiCNFGNklkrMA98baQGUSCLYurT%2Fd7cBqpZSnrITO0FPEkr7Xgis4qq6JjUfy9bfcCg4f%2B%0AaDnBG8kPGopMc2GWgy5WhWGbJootH2WmkK%2Bk5vtdEf7xyUd%2FJ32j%2BF7L0YAcMmxJkIg%2FPfkx4QSq%0Ad4XIBkfXnTIsh05I9o6CJ75EaKaMdvJafLWTaoUmei5aum7katLOgYHJ7MgXSSogHpaD8YjRIaXH%0AOL7coih5MVCz%2FCMH5xGhNo9uHgrOli69QGPBHHW7M20t%2FBExjOdFBjut%2BF%2FLy2ENRCEQhzNd6BVH%0AiPW7TA%3D%3D|预订|7100000T260A|T26|NNZ|SNH|QEH|HGH|09:40|12:43|03:03|Y|2TSTLljKhrYwi8mX6BnQLEQQhVM6hIaiaASAk%2FhxFjZYXNJzW0FU7N5764g%3D|20200229|3|Z1|14|17|0|0||||3|||无||2|无|||||40301010|4311|1|1|||||||||\",\"FXlOcIF8UvPE7Wkv%2FR8aekj2RCPN4yfu7MJqj2dxVrB3g8fNAdE6Y%2BhA8MKOLfXg3de7UxkNoo3m%0AE5LCpgapRP13o7kLLGXvcDU%2B9wp0HaR84tK2HZera75xeIvskXyQ6QmppQ74okVOwwM46ne9O8jk%0A8xoX6z2MPurNvBNQShwt32X0%2BBLCMuRGgw8qjr9Y23MlCg14SSJtdF8XrriK1%2BIgckDBUK1abx0I%0AmQkVdKtnuALOhn7Y9WT%2BUe34cY5MR2%2BcuYbgvLPT7OjOGUV1JSJ0h0hMup9IqZ5aiWUCF%2B8%3D|预订|6c000G13420C|G1342|CWQ|AOH|QEH|HGH|10:03|11:10|01:07|Y|2ph7ZTdE0Fyd8E9k9rQqQPbcwL00L1fBeVtdDPIWszDHdC7D|20200301|3|QX|04|06|1|0||||||无|||||无|3|||P0O0M0|POM|1|1|||||||||\",\"|列车停运|58000G16320C|G1632|FZS|AOH|QEH|HGH|24:00|24:00|99:59|IS_TIME_NOT_BUY||20200301|null|G2|07|10|0|1|||||||||||||||||1|0|null||||||||\",\"RhA0%2BLKFqb606iYwvC80Qk%2BN0ICQIy5Hw%2B3q2TEocgMZq%2B8gf12oRcPaXW8PTOL%2FOLLmpfX19p1h%0AXsYw468nLee4p3oULe3UArNzCfnZZ22u533xJF%2FhwsUkKVk7890DErgxQtAcU1dngKD1%2BoaOr7UL%0Ackj6J14fWzKV2OGKr1Icu%2BmB2zOZPXhpzgUE9yZi1fReM2ZLe3s5Zb%2B84YXNWDnUQwaLtMldXxaf%0AAWjXbr4XPridPgVrtOmfKip0aYqg10PBnXjPCp9pApa9UmC3StFrPa4rIGLMrquN2qU%2Bm1KLX2Sz%0AJSi%2F1w%3D%3D|预订|630000K2100I|K210|GZQ|NGH|QEH|HZH|10:30|13:42|03:12|Y|%2F6Qh08LHrfAz%2F1KkWOvdg2cU6p%2B3o%2Bbbw7KkjdHmqv93OWMNbB%2FhXiEeU2U%3D|20200229|3|Q6|10|14|0|0||||2|||无||3|无|||||40301010|4311|1|1|||||||||\",\"bNuLQ8sbjazpCe9wExM%2BiY%2B4pZSShvK5Hvmdxsgph1IbNWQpIp3%2F1sx%2BKYAucbMaAOAhRhb6esXR%0A9qYiggEmj8AlAGjUCXcmVVMJLInNmqWZ0zmOvPWfDW4vZFtw7pK240p1f1NKRD6T5ByDEJ0v1MSy%0AwO3M%2BlShcLtmEic7b37Y1SC0qpmYRZVK1M6EnY1KqSIZsu8PWz8qpR5UY8Uwm1Q6g8pf2fRVMeUl%0AjS1DOJecrS52RJ8DLi%2FxROhmRwMOtVVq6vP63xJ7xtrdZVMDkDhbpiUqEyWGM0Ol%2BICRD8c%3D|预订|57000G138600|G1386|GOG|AOH|QEH|HGH|10:32|12:10|01:38|N|mDMeNvt15DUr2945b7dn7GTceaRYxjeUX%2Fl2WP642ZFyvRgK|20200301|3|G1|06|11|1|0|||||||||||无|无|无||O0M090|OM9|1|1|||||||||0\",\"GKdRylmbrMI3D9jz0Y6x2M1f5EiKeOszMhBQX9OdPi0QTw6qfaEBH8tfLPNoT5L8Y9Gn%2BWISJJPa%0AbbSJ%2BGXuHkT2TUSGytKxWS1v9U4RZ69%2FhXiQjiqmIcIzIv1pG6zSQl%2FqOxeEa5Zm4F4%2FtVhgHfUg%0AHuKZ5VScZy%2BxFIErliF2NlbDjO5qYYdnowW7qiSerpmrBtX%2FKXIi5IOkCbD2RxWiRNK93waIFYMa%0AW8d5yd7kzymh%2FiAz9LJ0MJamwEPHUGs1qPI2vsvhEmrKZgNFLfvPTiGyy5PHBI7ykrdQb0Q%3D|预订|5u000G149604|G1496|NXG|NKH|QEH|HGH|10:37|12:00|01:23|Y|EDKmtafsIx3z6WOmRY99PccSdhGAqyZuApv4HZs82Qj9XCgQ|20200301|3|G1|04|07|1|0|||||||||||无|无|1||O0M090|OM9|1|1|||||||||\",\"OABKCaykhtc164XNyIl%2FSgje8chxq8IqjSgVaQ7Kwy6JDPRFvf3AevByrpSTOrFPpHKkpzf86pCR%0A6YwhagRz1ENsAOg23QspoWo2wwQC6vaXUVnNGm%2BYkAp4oOs3e7kQppxWnQBUI6DeLs1JsMjgSEcN%0AMlLIBBdpA0xoT4ajzv0c%2Fe1W5eTNKgcXwpQuSpcMcIOouI7kBzCsGEFTKIuLll4VAgIk6ma3oxDJ%0ALjZKfAkXtK0ebyG0OpH06SilbQ4SnflsyXDEWsBPCjPyUuV3JYhJBp79eOv8CVjTf3B6acU%3D|预订|6c000G134609|G1346|CWQ|AOH|QEH|HGH|11:10|12:17|01:07|N|l5fGE9BxGJmK1SBxptyMzjdC4NZF0GeaxQmHam%2F3QbVo%2BJU7|20200301|3|QY|07|09|1|0|||||||||||无|无|无||M090O0|M9O|1|1|||||||||\",\"T1D5qBYY7FTnn983jf%2FiTQedeyGLPhq8mjgwsA5RDpl14EiXBwOdPxXbasUcbTVaK6Qtnm4Qi7iY%0ASdf3MWi0bTHf8tUaMdI5%2FKhNVxFXD3f6Cc57an3r0LkRZXDxfFM7S1r2DiHqUP5Pu7S9YI2YzfR6%0AJNPwAJNfekRvdOXDg3OzGB3HX6iuOzD8lIF7mBotWiQVAVlCp%2FDJ%2F3BWvcGppQmn0t6KyXKeiClj%0AxIbNhzNww5FS8prxGWZh%2B7UR2B8VrdKJZqTqKTktBsV876nA%2FvcsoOCstWYaTk23jf88hFw%3D|预订|57000G15810B|G1584|JJG|ESH|QEH|HGH|11:58|13:04|01:06|N|mDMeNvt15DUr2945b7dn7GTceaRYxjeUX%2Fl2WP642ZFyvRgK|20200301|3|G2|08|10|1|0|||||||||||无|无|无||O0M090|OM9|1|1|||||||||\",\"Y3DqzUDvl1H%2B25z0uyU5OF%2BLmwfF2I2DKk9Oa2JaZZPvqWuXEfdO%2F95V9sWCdkT6EAh2zPWKUWfb%0AV9rmm7Uwqq6PqTbWlxVdGywlKQTUrdkgTRAvYj2Q7hXOBNte8V5E4Yf0sN5di7L1iCKrFAo7h7ti%0AHmOeNJPmU%2FPhEvyIB%2FGcpCZdno0eDSDLoRG%2FlR%2BYI%2Bd2sQYLZPIkNAqcWDspO%2FU5iiKkW%2FU67YhC%0A2%2Fe7Xl%2F3Vg7VmtlIHxUtAOUXKMhPs6F2xYiq32vvfc%2BdGbLvv6i37%2By39ZWKtQxzbVO%2B0v7HoHGX%0A|预订|56000G739070|G7390|QEH|CZH|QEH|HGH|12:16|13:32|01:16|N|dmzUUF12vDnJ%2FfL7LnmaO%2FEhlHZbxVOD3lSUqjEQ3pEyDu0s|20200301|3|H6|01|04|1|0|||||||无||||无|无|||O0M0O0|OMO|1|1|OM||||||||\",\"|列车停运|58000G16800O|G1680|XMS|HGH|QEH|HGH|24:00|24:00|99:59|IS_TIME_NOT_BUY||20200301|null|G2|14|16|0|1|||||||||||||||||1|0|null||||||||\",\"5uMQfTt7fXjS4ROMPacuB9ciM8KIO1%2B6F3DQ%2BYzEEeHRfG9P5%2FfVeGj92Gm2EAL6BE6cq%2BJ2YX%2FK%0AG2Co5i0Nrp9SKcRLJRiwQKioftnmJz4unio2Mo9vbHlJcmlELrrS90WNm1ZiFgJxaJAHbUoz0d93%0AyR2ffp%2BLT90dQ4gKAK1zYb8X5%2FO8id%2B%2FBXshd96tlNWIT8JA%2BJuxu9M%2FgN2mntE9s8%2BFmJpPrip1%0AWVzrqzHmAOyFj6SA6RwZmnH0PqE3SmgWzbrC9m%2BqTYDcrmyHe7maBH4t6ym02lrVu8VkmWc%3D|预订|56000G769242|G7692|JUH|ENH|QEH|HGH|13:32|14:55|01:23|N|dmzUUF12vDnJ%2FfL7LnmaO%2FEhlHZbxVOD3lSUqjEQ3pEyDu0s|20200301|3|H2|02|06|1|0|||||||无||||无|无|||O0M0O0|OMO|1|1|||||||||\",\"dPSJGYM%2BCTbAPslqcrK9vSc%2FExYGwzIEHuqryLE8FbTxhlWhejGrNfSoa%2BDR2O6zpZfGnSLY2YcB%0A3EnTdp5NSIP1eByqjx7Tw4rKGARe7T6vcO7veXtdXIja7fIJhzGAisjg4kTj9PJLe0mrUGJO%2FPdT%0Ay5uX5AD9ioPKI4shRvDQxRHzqrjTh8VLBJb2I4VrDuh2Kzno%2BBNZpkPicAf%2B4xwelWKocqVH%2FQXz%0AebLhrixSwg4ew6zw8L%2Ft40%2BLxrF80821HjTVYCBSA8ggaaLgxW9KA1a0u0dvEzOaYeLITwY%3D|预订|57000G146210|G1462|GOG|HGH|QEH|HGH|14:14|15:28|01:14|N|mDMeNvt15DUr2945b7dn7GTceaRYxjeUX%2Fl2WP642ZFyvRgK|20200301|3|G2|10|13|1|0|||||||||||无|无|无||O0M090|OM9|1|1|||||||||0\",\"C7m6aVDAlhVuQcw%2BD5LNIyWFZoKCdIRoBOdPiHAZtfAy8lk7zWqUFl%2B9N532PGD3wLCB%2FzDNSwom%0AtVr4wcULtjzX4lLttGQh4QJFXYiEp2wct32dhxtWs%2FcpHox79efIEsAWzrs%2FQx%2FdC8IpiKwiQeNW%0As%2B3ngmkD3bzvEDu9EhpmTgq3%2BOrMVfk2Roa9CSJlaRhDduC6Zg8jHsVM99QNtG4iLg05%2BpVCaWBm%0ApRmMtAuKwLx4S%2F2PLcX3%2B2oB%2Fl3s3hXKsUK06PkvMBxQiuPA5WgxIFqiUK5QYJvsv%2BHNW14%3D|预订|61000G137005|G1370|KAQ|AOH|QEH|HGH|14:19|15:32|01:13|N|ZfE1e8TLr%2F12VnjTEPo2hrjEWC5C7M3RMYFvx67Hg0OpLuYd|20200301|3|Q7|12|15|1|0||||||无|||||无|无|||P0O0M0|POM|1|1|||||||||\",\"VK29KlGhA1tDHwTD96FcNzP3xrkaKuwnCEblA1bbJBW9vPraeL7P%2F66H5k34cdWgEEA45wgQTK0f%0AWOOQQXAM%2BK7vn1eTrMIeDjU77KNnvhmRVcRsiXt59K1oZCA5d5QZMOksutQoR4nNmmc1xqR1Z%2BYL%0AQL63yJeqzxSe0J90QP4vE5DZp2zt53uXLTInXprHnsVAjpNgNRjxY%2FwsJlH2syO3DKAMmxPoIj8r%0A4W3IYq5hcN1BWEpNLbpZtlFqqG9Kcvz%2BxvR8AHoso%2BiM5uS7yoPhYusoy95BXu6nvoQq5BWbdXpO%0Avjcc00uA8d2JkUnl|预订|56000G737627|G7376|JUH|ENH|QEH|HGH|14:24|15:38|01:14|N|IU18aTzNeovFojf%2BC2BaQRMKCIC15NQew%2Bn0RhJkrPPBDdG%2FFMUdSbW3EBg%3D|20200301|3|H6|02|05|1|0|||||||无||||无|无|无||O0M090O0|OM9O|1|1|OM9||||||||\",\"6ZeXv85vROhIpZGDK7jiQ%2FofDrONdHdBDOxHSRns289oki%2BdRlxBUkm1rgz8bu6alClGJYO5l0Hc%0Avmp8FpPdT7gq3kKt2m44COpQH5KkKofAsVIj6Gepc7RoS2qaUr%2B7PgoiPpcfX0zOTf15EqowOCHL%0A54Q7%2BDTo25BPv6rOafJiaPa5g9deHAefrKkLmdiqab0SynSPE%2Bjc69r%2Fd%2F4Mt8ytH5TURsHwkjy9%0ABipZqxQ8KDzBqR7FhHVeKPzkumgdGd1PMB2dWwBO4bJIS%2BHUpOCmHyIvFo8NR5R0CWztIPo%3D|预订|78000G14840A|G1484|KQW|NKH|QEH|HGH|14:58|16:13|01:15|N|mDMeNvt15DUr2945b7dn7GTceaRYxjeUX%2Fl2WP642ZFyvRgK|20200301|3|W3|15|18|1|0|||||||||||无|无|无||O0M090|OM9|1|1|||||||||\",\"zTDaeenzzMmeW7fOAbwcuJtFfVKTzuke950o29nCv%2Bf0VMo7IEZxnd%2B%2F0e438Zhza5udpUusw36D%0A1tyEVOLjmqi%2FwqsqdQZWVCluKx9L2pgoYPk9GBhTMT%2B7oGvchwsGmDGiAlDlccMs%2FQWkvIPC5v8Q%0ADmHnktDpaoJcxIHh5MES9Snf9PxXo%2F60oM%2FmbLBU8KEbp7kqRQKvC7wVJ1KrUeaOjRYm54vbrqJ%2B%0AlC0yxQAP58bC%2F6eE13G5u8w%2Bo%2Bx0iz9VtVYNG8LRn12OdcBVSC%2Bw59d34Ft12Q%2FpHFFjIeI%3D|预订|58000G163808|G1638|FZS|AOH|QEH|HGH|15:10|16:17|01:07|N|mDMeNvt15DUr2945b7dn7GTceaRYxjeUX%2Fl2WP642ZFyvRgK|20200301|3|G1|05|07|1|0|||||||||||无|无|无||O0M090|OM9|0|1|||||||||\",\"54%2F4f6SdUcusK0UkqWzXPz0wmPXKR8DdrmXOio5U%2BI5IKOP7Y19Nd%2Bb3a3mu3enes%2FMdQpM6AM5d%0AcLcnLkaZQl8RvyDaZ2bfpdvINCpW6Wgf4C4OTD%2Bl2R%2FpnU%2Bv3Obeyto54cICwaBD09hcHMWanNlF%0Aordlss%2FpXpnIiKvNPuytGAMFd7%2B6zXZWMOXlIqZMpmlJMJpjqqVB%2FDK6LgWm5t10GOpRUzGWnqvB%0Anie5OrFNiONQlGVonK2zxc73tL4AyTNstJs1D0YSvfG3zUqwkpkkUGT8kkhVpQsmFy%2Fxd3VcBLqV%0AjDoj%2FG3tXRIXVPb2|预订|56000G769632|G7696|JUH|HRH|QEH|HGH|15:57|17:22|01:25|N|IU18aTzNeovFojf%2BC2BaQRMKCIC15NQew%2Bn0RhJkrPPBDdG%2FFMUdSbW3EBg%3D|20200301|3|H1|02|05|1|0|||||||无||||无|无|无||O0M090O0|OM9O|1|1|OM9||||||||\",\"|列车运行图调整,暂停发售|78000G132209|G1322|KQW|AOH|QEH|HGH|24:00|24:00|99:59|IS_TIME_NOT_BUY||20200301|null|W3|15|17|0|1|||||||||||||||||0|0|null||||||||\",\"D%2BjIaVx9tx6K8CXzxZFsqpFOvOo%2BI2%2Bwf%2Bg%2BJKhGek%2F2gAYIipY4iz7X2QgBwy%2BOdKWWr2DShWmo%0ATiFpQL13ND9Mck3A6psfN7COWKUZR4jAz8JsZCEnr04svU9wqFVtBMlL9U36CaneHKeSrh%2Bbhq93%0AgwZZ8SCzUsu%2F74QGmhtSTL3kNv0O8naqyR9GCYSAOI6yVKbRcAbpsjcjuIAzXB20UAzw1Pb8I256%0AoPbbLlZQbNfkPvD5Z19uQx6Ux5MXNIv%2FN7p3gVctwLAvRO%2BEKU525pX%2FGUe8v105Q%2FgRie0%3D|预订|78000G233407|G2334|KQW|NHH|QEH|HGH|16:10|17:43|01:33|N|mDMeNvt15DUr2945b7dn7GTceaRYxjeUX%2Fl2WP642ZFyvRgK|20200301|3|W3|17|22|1|0|||||||||||无|无|无||O0M090|OM9|1|1|||||||||\",\"4DBnpdJiAygJmXUIdy2vUQVtcAQoGygGYC8o7seoZ%2FIKFM%2FPkWuS8id%2FDDnX7bp6R%2BeecEkTG3Uv%0Ab5HIWs4EYzlivhCJHYZhXQHadCWhX6BF4I8os2pp3Wt0v4BGMu3hGhHZEjo2YmfQaLdmQEbS76lP%0AWsSzPApFvX3chSszBUwVLvWjVJK5HB5WmU08i4AeG7%2Ba3km%2FaojXzzNvtNGHw8BHlVsGh%2B99d4kE%0A3j%2FO5W1EnNTtLEeLv9kK5A33e3%2FaD0ptMibW4wIYhbei8B4k7lSOPuEn63Nga8yBUB2hLBE%3D|预订|5u00000G620I|G62|NXG|JGK|QEH|HGH|16:17|17:32|01:15|Y|%2F57ZQYj3r2RwpiLtzMXUGsAStIedpGq%2BVcvnBIJNFUhBgyRi|20200301|3|G2|05|08|1|0|||||||||||无|1|无||O0M090|OM9|0|1|||||||||\",\"3%2FViJQhlp1OkQUcSnYrwTzkoImmN6gI9P%2FHPnvVWAdaGKlUfHqvH837%2Byh0KjH6DzqjyjXlUUHYT%0AWLi6mAfE10g4GZZZSQyeAd%2BsBSJlyZC4DXa9O%2B%2FT2XMtJkuqff%2FCELvnfjL28T%2FqiwQoGkB%2BPZ3K%0AsxEe85tPWCe0tU7WQ4R6Y5RLHMFHZHgcUE6tZsn%2FRyXNrxfha53Hdmwd484034Wk5%2FTqZiEocmek%0ADcVk9UJRORqDEI71Rh6pIemuoG4u88Sfho1sLbxNH8OzVz%2BfVu4elCvS3mx0QtjkpoWsDwo%3D|预订|56000D548600|D5486|KHU|SNH|QEH|HGH|16:30|18:55|02:25|Y|dHEpe%2F5OVcH%2BLt17Yga3rlGHTopaTJyQCxanr8nbdYlb2rvc|20200301|3|H6|03|07|1|0|||||||无||||有|有|||O0M0O0|OMO|1|0|||||||||\",\"ZgmnoyJywVjqUDW%2FYzGW%2FLFaT0HL6EuKc5k9VY3OR3OYyP5f6cGb%2FLDgHXEK9%2B34zfGLHiQBpsjq%0AaY1h4KgPFL6zz8bd7BC%2B5VV%2Fg6MKeYwgi69b72Zyd3FJJNFLTCOxfNjBs7KxKaX1ASuEcv2pCj26%0AGP2aeq3ZI%2BSeWGsV9j1t3Z%2F84oeAvlD4GltV%2BgmBWQ0UcoYcvU2jBfQOlR%2FuACA6aFxKhzyXHiE1%0AwuzgBOH2%2FxvNVhF05PPA%2B6IM0zT1UfNU1HPO6uz%2B2fp0cULMs%2Fq7s7%2BYUYMmvpr%2FiQe%2Fq4s%3D|预订|6c000G13620D|G1362|CWQ|AOH|QEH|HGH|16:51|18:06|01:15|N|mDMeNvt15DUr2945b7dn7GTceaRYxjeUX%2Fl2WP642ZFyvRgK|20200301|3|Q6|09|12|1|0|||||||||||无|无|无||O0M090|OM9|0|1|||||||||\",\"|列车停运|58000G164205|G1642|FZS|HGH|QEH|HGH|24:00|24:00|99:59|IS_TIME_NOT_BUY||20200301|null|G2|05|08|0|1|||||||||||||||||0|0|null||||||||\",\"p0upwdDDvz2dF4TcnWEwrVgNE38GXekYJrLEMfGhV9FuRIs1dok9vumfMMOJkRMFvubHxOMU2iMl%0Ak81jb6xCKPtaV4dxphVAuKVGKplW5Lr89aqx70DWPfqDcydaPLmXEQKHvdkLC0JmuL%2BjpEp5wqDA%0ASrotjrPKmU3KRnmEW3yX6m3%2Fkyt3v%2FP7ZLW6jU4R2GvX37XIwWAlGEh1c3c%2BBg%2F6mEezfno4CsiT%0AnKgy8O17gHY0vvqJ08kHENx6NTPtkA631eRyPdEs%2BoiJeoOyrAdEw%2Fk5CUpQuNPCRU%2FuGuo%3D|预订|5u000G13880A|G1388|NXG|AOH|QEH|HGH|17:31|18:46|01:15|N|mDMeNvt15DUr2945b7dn7GTceaRYxjeUX%2Fl2WP642ZFyvRgK|20200301|3|G1|04|07|1|0|||||||||||无|无|无||O0M090|OM9|1|1|||||||||\",\"TNNyw08jvVDu19JnP%2F7UZGQcxapCZbLakYkCk4KO8yy8on9duyn72WMixQ3gGuuA7PH9quh4Xe%2FW%0A1KmMNR0xI5VcLDGZMoPSD%2F4MEz3RBYk2t5lDhNufpFrZI%2BRdmY1uITdwj%2B3R1b%2BYxAdvt7CIgMk0%0AcQh0bd3UheYIZPLrM1sWFTOZ8ycxbHf1auhpDs5hrCBfnl7qtpk7I9fUyIrEUQeVzGsHfvoe76%2BP%0A4BrKHK2VcaTYh2pPxkdxNuUaZFvxl%2BKGc3nDaF1gYj6F5XrGDXNwIhUe65zJpRoE%2BQLHSpY%3D|预订|5c000G16630G|G1662|LYS|BMH|QEH|HGH|17:37|18:55|01:18|N|mDMeNvt15DUr2945b7dn7GTceaRYxjeUX%2Fl2WP642ZFyvRgK|20200301|3|G2|13|16|1|0|||||||||||无|无|无||O0M090|OM9|1|1|||||||||\",\"|列车运行图调整,暂停发售|80000G138008|G1380|KOM|NKH|QEH|HGH|24:00|24:00|99:59|IS_TIME_NOT_BUY||20200301|null|M1|21|24|0|1|||||||||||||||||0|0|null||||||||\",\"eQQAGd53zJKRobCxAGgrEIq3%2Bel%2Bkrks2ZofWEHm7itXAofbXgMvJ8lXblAvVIXYtzGN22FILTAw%0ArL%2BuYsRGZaA99qEDbW%2BlF%2FW8ekv%2Bl1SWkYGzo7Y0sS5AAWPW4bjSHdgFFrfmwTfey2ahRsdGYrlf%0AFKWV%2FPv6i7gfNaJrIaYqwyvMSPqStRXbmvXIxB95qDOmySiTQjWvoybQh56nFDGHX3e3Y4nJIZ87%0AsL0LBrxf1N319Xv%2B17ujP5BUokKvG795R89Rz3p%2F479EFSYmiz%2BTmHtY9SSZGH0aNUxUkAc%3D|预订|78000G233607|G2336|KQW|NKH|QEH|HGH|18:03|19:21|01:18|N|mDMeNvt15DUr2945b7dn7GTceaRYxjeUX%2Fl2WP642ZFyvRgK|20200301|3|W2|16|19|1|0|||||||||||无|无|无||O0M090|OM9|1|1|||||||||\",\"FIw9gR0O9Al52WICXlBfksV86zwMeFlbydYK4Krm6OH3JsopMtwRnbe%2FX%2Fp1LQG%2BADG4T6Fy1cof%0AEqWkM4Kgx58cmfz28YZfgYVWNn3F6JT72Vs0CQa5rG2Py%2BjZ3E7aoaVb1MDgPNB6p%2FIqs6U3tV3c%0AV%2FfOzAFKVqHyeOGAH7%2FhnJL4BxXAYtzQqpp55VZCRG2KLSq8yH2Hgs0a6vSK7Ea3kmsYtAVxxOXt%0AOSIbPTmzo5SaveGvMWG4i3m6tytlDFdMcy48DVsfgCrOFO4PuhoTe4Yt8VkQWXcqrYLepxM%3D|预订|5n000G16540H|G1654|XKS|AOH|QEH|HGH|18:08|19:30|01:22|Y|rNDdyRmtRFDIPN9jKy07k5T%2FPU3B4Z%2BBA8CqkVbMhgahzHYf|20200301|3|G2|13|16|1|0|||||||||||无|无|3||O0M090|OM9|1|1|||||||||\",\"|列车停运|780000K83403|K834|GIW|SNH|QEH|HGH|24:00|24:00|99:59|IS_TIME_NOT_BUY||20200229|null|W2|17|20|0|1|||||||||||||||||0|0|null||||||||\",\"G9d37fiZ61RZNoLTy2XPBHhqW6JNqTmR0zJ8OejN7Ss7UH4mMQdq3NeHU32b7HzUVUlT5FoDrGfb%0AE8kdIOGF0HcWJIFPJgLJm3ic4TYubp%2FaF4gPYbDJ2cgEUo875rxkuH5TXlMm9dYcJuqhW%2FCTk24O%0AbISkQ9YKsdCEjxpGajeuU3VpvlOQCXTQWxCWwYKo9Dfd9yNFAevC6rF6Zi00o8qZt79I9msgk0Ja%0AJnZozDYyNpoI5XMulN4JvgNoxALoQjjw8OuKdMzUsfRwNW435eJDm6vYehVyBSpc7TQvesI%3D|预订|5n000G16560E|G1656|XKS|AOH|QEH|HGH|18:27|19:40|01:13|Y|px0Pj5NWXEyI03es5YgeTo%2FLVoAVV9MPRsgcAKcA9u0CxmXu|20200301|3|G1|11|14|1|0|||||||||||1|无|无||O0M090|OM9|1|1|||||||||\",\"|列车运行图调整,暂停发售|77000G133506|G1334|CXW|AOH|QEH|HGH|24:00|24:00|99:59|IS_TIME_NOT_BUY||20200301|null|W2|20|24|0|1|||||||||||||||||1|0|null||||||||\",\"|列车运行图调整,暂停发售|80000G139202|G1392|KOM|HGH|QEH|HGH|24:00|24:00|99:59|IS_TIME_NOT_BUY||20200301|null|M1|19|23|0|1|||||||||||||||||1|0|null||||||||\",\"rXpYmbB2QN9mnSD7O%2BjFnKOtKNTcoT%2FzI8gFZj2kfbL1rsG5N6VZbQRNAhg0fvaGhXkGoBOW%2B2hS%0A5Db61qygUXK4k9gHhXZPQmKX2yfxPcrkYO4i7%2Bi%2BCPW%2BCztWn8dDqrsI%2FgdrKcSPb44SrlbCCxMV%0ASrRA%2BpsleXS7Rh%2BHxSgReeH1qkBH9%2BGbbY%2F2e9TZfTnFcypWm0ZFhlVpRYrtBUjUw5YKUJsIMMEl%0AGwtiNRA7s1cbozBfoGhLjtBOJfsik9nfm%2BtUzqp6j0LU9USwFRG4iRP881JMYhoY8Owocx%2FHS%2BvW%0A|预订|6c000G141809|G1418|CWQ|NGH|QEH|HGH|18:54|20:01|01:07|N|mDMeNvt15DUr2945b7dn7GTceaRYxjeUX%2Fl2WP642ZFyvRgK|20200301|3|Q7|07|09|1|0|||||||||||无|无|无||O0M090|OM9|1|1|OM9||||||||\",\"AHZBv70GIo5jORQPJ329KY5iUG5ouljz7ODwH%2BkpwZoC2JX6m9Sw5Hi0BsRp5QpLkj1D5dJsr6M5%0A8nC%2FIEUnpmocfTRp6OVzGZDLSJD8ofcum9mRAGaB4S5UvyUWaaDb%2BmAxq68bEBCo%2B1qtpMGC7Ej0%0A%2F%2FhN2OgdhIa3Ih343fT8eXovTr6QNdtUJjbHyNvvd7NTQLOhKOcUCQQnF2f3h%2BOjEO5gxdPlqpKw%0AWppOiOj9x4zZMqGeohW%2BBO8awHc0BBruEzakur8gf0V7VqScbn%2BOQ0qI%2Fs2%2BZc56v7Q0z7WIx4RS%0AVjRzKdZ069Q%3D|预订|56000G7366B0|G7366|JUH|AOH|QEH|HGH|18:59|20:12|01:13|Y|SaMfTNGTAiCraFug3kLkwOIHx1l%2B18a6W9dWAPN61zxHlMgJJcmxTtGUcq0%3D|20200301|3|H2|02|05|1|0|||||||无||||15|7|4||O0M090O0|OM9O|1|0|||||||||\",\"6rgKPRUk64gJ%2Fqbjd2K%2B1aibNOQz0M9P%2BaTbtHiyUCPyLq08PUxVtQmEGSIG4eg1dVh6t15ZiZkd%0ACUNUoxibB9E9tnYMvqAC5FJlD%2Fk%2B715EsfumLKIOFefoR2yWyglChsKs0lhvB7%2Fkfzo2ksomfS09%0AIprp6seuY9EF0q5amOLOYuB3tDDtqgDwei5YFyt8dmuB9xLwv00bdGf8hvzuhR6rIoWoZV9HkFaF%0AnlCay6oU9gXR49AljPnCyk94A4AewRFTszYpmlmslzEP%2BdWn6pbefay2CLEvjHccXoky4uo%3D|预订|570000D77203|D772|JJG|SNH|QEH|HGH|19:05|21:28|02:23|Y|DMROAHJgpnsiWsVmqE7rHJmTV8YiZ%2BAliqOVLtmWsjkaLqx1|20200301|3|G2|06|09|1|0|||||||无||||2|无|||O0M0O0|OMO|1|1|||||||||\",\"4jq9Hk33lBoC6igU%2Bi9CzmC1GeKelAWf%2FUoLDjEqgN9oc5ePW94Itzi7ykUcVCkh2dmBukSbh83n%0AlW90fUYPowfQ3GNHbDjPkhW0fEU%2BNiVZRmbVLQbSMbrxGzSq0AXJFA%2FG0G81KGYZjOJ1zXU5vxMs%0AB4UfPufLTBgoO6qgZXuEGVW0W3B22QIv%2BUplqlw1kC2xYkNbCg4Fhvmyye7wvA9QAYSLTxW7Dwzh%0AT%2B9U66T8AxOX8AQjxVwsH4DTe3b9za6iX%2BcycRt4kwzDJnIg76lQBFJLL8MAMK6LzlE3ABQ%3D|预订|78000G132805|G1328|KQW|AOH|QEH|HGH|19:07|20:28|01:21|N|mDMeNvt15DUr2945b7dn7GTceaRYxjeUX%2Fl2WP642ZFyvRgK|20200301|3|W2|16|20|1|0|||||||||||无|无|无||O0M090|OM9|1|1|||||||||\",\"HMZyHMR%2BBXkkEMzEddblBFarfJS2D5Ai1ly1WcIPNocS8EOEm7XSFGSYKqy2dN5wmaNVWtWma4qq%0APky48xiz0GpY%2BvSR7AgFl7hf7RSs9t6CQx6rlw%2Fr89e%2FwmfQ61xrryOhvGopFzG9DnrL2jekGJ5c%0Aeiw5uHRj4lrl%2Fo52IeTfZz1T7fD2nB%2BX08Md4wfCVOA8cIw%2BVfXlQOUdSHCfeIm%2BBwh5P%2BH9jM%2Fc%0AH%2F79xkbYq3rnawIoESS5sB67gZmfjqlP5mVhamYttKIpd3myH%2BfE3Iy7NYT%2Flbj%2BeJ3GPYw%3D|预订|5n000G166807|G1668|XKS|NKH|QEH|HGH|19:49|21:04|01:15|Y|EDKmtafsIx3z6WOmRY99PccSdhGAqyZuApv4HZs82Qj9XCgQ|20200301|3|G1|10|13|1|0|||||||||||无|无|1||O0M090|OM9|1|1|||||||||\",\"|列车运行图调整,暂停发售|80000G13740H|G1374|KOM|AOH|QEH|HGH|24:00|24:00|99:59|IS_TIME_NOT_BUY||20200301|null|M1|21|25|0|1|||||||||||||||||1|0|null||||||||\",\"|列车运行图调整,暂停发售|76000G219101|G2190|ICW|AOH|QEH|HGH|24:00|24:00|99:59|IS_TIME_NOT_BUY||20200301|null|W3|22|25|0|1|||||||||||||||||1|0|null||||||||0\",\"sNPEFzmZJRDmvdFbj%2FuSJ193AmHH8jiUZYL0ta%2FCxM2vWzErEZ525kzpXhnGevBDks9ZE7JZ0Bx5%0AGa7ycgkZPaObmMhDYxQpxx2DXMOlP9ZXyMGkuPNnhwovOYuf70ZiiL8a4Nt8PBcHpg1ZOBYneUUv%0AmRF1F2%2FTck%2FishE4t4FsWlh0nsvNjtU%2FG0ssOZuou24SnacAHR6zx9oF4GsHfmwO1eaq15tKnWf2%0AR0g%2FfgcXVFE9KRHkUTpQUBaKDP9IE8qS6NHB7ZBUDMZJNZIMdj%2BX%2BQ3yU5nuv4Axs55x6kk%3D|预订|58000G164009|G1640|FZS|AOH|QEH|HGH|20:15|21:48|01:33|N|mDMeNvt15DUr2945b7dn7GTceaRYxjeUX%2Fl2WP642ZFyvRgK|20200301|3|G2|06|10|1|0|||||||||||无|无|无||O0M090|OM9|1|1|||||||||\",\"wJom14tUyEkTk0G%2Fann9LZUImxWlTYf7UD5rb0GklqDrdfUWiadhkI%2F3RJwkl4V16pxKVJWuF2Y%2B%0A5oqjqGnnjCneUAqPjWgU%2Faz03gHndBh91%2Fe7PeERWbqy8H8LR3ejl8BmeSM753Lvfk840fNHLeu%2B%0AwKkZ9h%2FCg3BrSKrsFWAIElGhErH3Mj1mi5kYmqENUzQVg%2FQgkJWVc1uXmhsMGDEqfZXaMsXbhx3X%0ALCXHTVY1i%2BViSgAzeAo8EejjVdKaJAvTDwzoB7Bc%2FOuH838%2FDqBKghufA5dPA3OoLGACeK4%3D|预订|5n000G16580F|G1658|XKS|AOH|QEH|HGH|20:31|21:52|01:21|N|mDMeNvt15DUr2945b7dn7GTceaRYxjeUX%2Fl2WP642ZFyvRgK|20200301|3|G1|11|15|1|0|||||||||||无|无|无||O0M090|OM9|1|1|||||||||\",\"91OBeohvNqAno4idwEbWwYJtYAaA36AEHvO93iR91%2BSqO%2FpwUTGO%2BeB37DISvvuGlByGXhJ8kD8m%0AsGqymy9U6nQBBIPQNtZf4vGiGuad9%2F7wcYMDEpd9Yds7SP6tyHnfRGIDuaXEPjtxOwtCoviz0huJ%0AEupOBENeAuaC0p3bgr8e1jdENr7Vwx7LiZHkrWT%2BtWxhYD%2BT90HxeRkbglTOIe3lZibNmYA13iWa%0A5qOnMmQhPBM7406N15gossWbaekuZZOgffhMeo406hhuNw2tEwjkg6AtTro2gFu8kiY%2BG242MsRt%0A|预订|62000G23660C|G2366|SYQ|AOH|QEH|HGH|20:42|21:57|01:15|N|mDMeNvt15DUr2945b7dn7GTceaRYxjeUX%2Fl2WP642ZFyvRgK|20200301|3|Q7|14|17|1|0|||||||||||无|无|无||O0M090|OM9|1|1|OM9||||||||\",\"0phSqEehbFdAneNId6xAMSSwWpMbA0RE7GyZMYwYJdOA9PJNWoaJF5cash5%2BQYzfswCdRbwBzksA%0AumwntoQIV9AN41q6zIPQfJmchQ3kr2mvhxGtFndG6xIoP9JQq9TnoZTcpYOJNJuFC8hDsvgyqZ8Y%0AkCXjaBWCoxDLjQSX45KCjebsBfznx%2FBUiGMUvvgEwLr0HEbUIALolGK6XnipWY6VXNjlzzFYr%2Fcv%0AaikDZ33fo%2BuF5Pikc7zwklxvT0mxOobDa7VJJac89Q5LtDQVkMHEutPN%2FwWGiDZlt4JN3yI%3D|预订|80000G13760B|G1376|KOM|AOH|QEH|HGH|20:47|22:02|01:15|Y|4uQ9%2BSMH16SfAc5VVyq9yAUUs8DCjACgMZDsWw3J9JC0WKpW|20200301|3|M1|20|23|1|0|||||||||||15|无|无||O0M090|OM9|1|1|||||||||\",\"tHw3W1VD1Xy6ZOonYnBrDgjjR9a4WAMYuLDlgDbaMB62pilhuPWdoKfm5zQzTME9JjsVeO%2BV0IM5%0ANTXcFF4meiyfhdLgxKuExmSPXJ32WrT4a9ljF1LhRqF8SDc0EETwUeIApbhZ3O7XDd3SySiFE8FO%0AnnIGn1crFxzbHUVl4CSCO42ai5mT0tuskhsV6Gu3b%2BgoFVTgXNl53MA%2FWwskJH%2Br6%2FWuohMHXdnE%0AZigksTNQXjRCbwHGzwqaJtdl4O8sNdMJPQu7Z4I6rjoQuSLF1XiAGhXnMNpp%2FRVoAx2lOzE%3D|预订|6c000G13060A|G1306|IZQ|AOH|QEH|HGH|21:03|22:17|01:14|Y|RLllVM7sGBTO7ky0yTq75UziNuT7lK2E8mlQ4YQp%2Bs4%2BqErw|20200301|3|QZ|07|10|1|0|||||||||||无|无|2||O0M090|OM9|1|1|||||||||\",\"f1DYo4mrXSaSwlyTe7f4NRjzyA1vB1JKF%2B3v%2FyhNkfEjWW2DqorIeJ9OXFHpvS2aJYBLKjNXfsic%0AXASq9ZYqwvJ3D5wuAfH6QiKReu7PfxDVpRE1oRr1axyt%2B8V5xK3F4tmUQuUY43UFnEd5YjbdpW80%0ArinzDPdYD9skYjSbHcpPs%2Fh%2B5iXkIbG9IdZSxD0Wb0l0ldDaGj0nNlX7dcB6bJmPbEOBH0%2FdMeUs%0A%2B92UO4NDdOhRtfMe2kmzGRaHzVLX02m4LbPNOiWvl5C37U8iqJqxiW4XPeUqIiMmfk0li2bVCTYF%0A|预订|6c000G140409|G1404|IZQ|HGH|QEH|HGH|22:12|23:25|01:13|N|mDMeNvt15DUr2945b7dn7GTceaRYxjeUX%2Fl2WP642ZFyvRgK|20200301|3|QX|15|18|1|0|||||||||||无|无|无||O0M090|OM9|0|1|OM9||||||||\",\"eWWIOX4nKXAwGXQr877NfEF1bUroxEP6xO5rcwaDZLsVneepj09SdKgj7B%2FCqbOe6zqcl4fOas8M%0AQeGIgo2I98fBHyBAreuSefoM4XzdRsbc%2BdurOe%2F%2Fq72t3EUQZswUH6wIc4gJwsaZjxWva9IMmMii%0Aqm6cWlyUwYXvgvqGCzjOi%2B%2B7MM797lxX2RSgTO2B7QJC9X6QdCEPxBPLqVfKd4yVzOzBv1c0Yy%2BB%0APdcGPDfc%2FpFuEvKdSTyvGxRDdhorxCeXq6yssGmlpaqUgzQlKcG%2FdYdrk0%2B7GvihFseRFrSdrQVF%0AK9ggXepVUvg%3D|预订|580000K5260H|K526|FZS|NJH|QEH|HGH|22:13|02:05|03:52|N|bqnUXBDsNOtHnsIOxvi3SI9J%2FaQ5S%2FcDZ8RepUhS8xI1RSzr7CXFELY68ms%3D|20200301|3|G1|08|12|0|0||||无|||无||无|无|||||30401010|3411|1|1|||||||||\",\"pzXhbtA9hquFCAqxH0sv6W%2FWTbpy5t29MT%2FNKQNtIhyUMSOJMXcl9qzTvby2%2FkFIlleD18ZdmmpE%0AkCTNtXz9Nu9nsjckxWOjFGCnPE3UD7MjQ00uLNvebxCGr%2FzQE0my6wVtQB92RTsreLiJQtrpBiZT%0ACv4hKFHtEfDeent8KlbBjnZy7qZqI8CoJ7CNwTEpU6Rl2GfyVZn6iWACYgIUfMP7PLJzwG5dBGR%2B%0AYlaGN7VUKwhuQ8fFoagwwbb8sS3eMpF74M%2BhL6h%2FDzEndJw%2FJP3EJbib3MHuzFt3EWhyAtB51qMI%0AqIrkaBJy%2BK8%3D|预订|630000K5280R|K528|GZQ|NJH|QEH|HGH|23:13|02:12|02:59|Y|qYNnXSkLyJskrbesYx9yKhmNKJBIcdLJbOShUHZ9RemnssQbdqU5k9fpOJ8%3D|20200301|3|QZ|16|19|0|0||||无|||无||无|1|||||30104010|3141|1|1|||||||||\",\"|列车停运|390000K7520I|K752|XUN|SNH|QEH|HGH|24:00|24:00|99:59|IS_TIME_NOT_BUY||20200301|null|N3|14|18|0|1|||||||||||||||||1|0|null||||||||\",\"KqDKAcIm7kJ25vYdjvo27mexCNl7COV2ENox4nmXJpDHVaixpFu1aGrfJmAsAhZnSk8m3rZ6v6KR%0AcgUv99N4We%2FwXn%2BhDmcqCytouuuFWRir8%2BdO1Ynau3CX1JicBnatrFizSxdzZkvv5LBVfZZQbqKk%0Ab1xD4Jjvn%2FDJ4Uh1f5%2BVZI8dDyL2Fb9aLY3ez5tOHkNKlXc8S5io7jhIYgLbllTMMSHiizyAUqf2%0APhStvC%2BhpkDc9GlW4pqFah1q1PZPDpycgfL3YKF9uvOLeIkXF1Rue5M0FNO64%2FzvEjH8MyBWZlUr%0AUSAyEw%3D%3D|预订|8000000K800M|K80|KMM|SNH|QEH|HGH|23:44|02:49|03:05|N|MQduDlOnYNOApwp560MfEWNbDmc6w5fNOHSvKX%2FeVlMq9zTam4HZjZc6Dr4%3D|20200229|3|M1|17|20|0|0||||无|||无||无|无|||||30104010|3141|1|1|||||||||\"],\"flag\":\"1\",\"map\":{\"QEH\":\"衢州\",\"HZH\":\"杭州\",\"HGH\":\"杭州东\"}},\"messages\":\"\",\"status\":true}");

        JSONObject json = JSONObject.parseObject(sb.toString());
        JSONArray result = json.getJSONObject("data").getJSONArray("result");

        for (int i = 0; i < result.size(); i++) {
            String s = (String) result.get(i);
            int i1 = s.indexOf("|");
            result.set(i,i+s.substring(i1));
        }

        System.out.println(JSONObject.toJSONString(result,true));

        List res=new ArrayList();

        printHeader();
        for (int i = 0; i < result.size(); i++) {
            String line= result.getString(i);
            String[] attrs = line.split("\\|");
            Map<Integer,String> attrMap=new LinkedHashMap<>();
            System.out.print((i<10?i+" ":i)+":"+SPACE_STR);
            for (int j = 0; j < attrs.length; j++) {
                if(check(j)){
                    print(j,attrs[j]);
                }
                printAfterIdxs(attrs, j);
                attrMap.put(j,attrs[j]);
            }
            System.out.println();
            res.add(attrMap);
        }
        System.out.println(res);
    }

    private static void printAfterIdxs(String[] attrs, int j) {
        if(j==attrs.length-1){
            for (Integer afterPrintIdx : afterPrintIdxs) {
                print(afterPrintIdx,attrs[afterPrintIdx]);
            }
        }
    }

    public static void print(int idx,Object value){
        if(idx==15){
            dupFilterSet15.add(String.valueOf(value));
        }else if(idx==16){
            dupFilterSet16.add(String.valueOf(value));
        }else if(idx==17){
            dupFilterSet17.add(String.valueOf(value));
        }

        if(idx==18){
            if(value.equals("1")){
                System.out.print("".equals(value)?"\\N":value);
            }
        }else{
            System.out.print("".equals(value)?"\\N":value);
        }
        System.out.print(SPACE_STR);
    }

    public static boolean check(int idx){
        return printCheckIdxs(idx)
                || printRange(idx)
                || printUnknow(idx)
                && !printClarify(idx)
                && defaultCheck(idx);
    }

    public static boolean defaultCheck(int idx){
        return !printIgnoreIdxs(idx) && !afterPrintIdxs.contains(idx);
    }

    public static void printHeader(){

        System.out.print("描述"+SPACE_STR);
        Set<Integer> idxs=new HashSet<>();

        for (int i = 0; i < 39; i++) {
            if(check(i)){
                idxs.add(i);
            }
        }

        List<Integer> res = new ArrayList<>(idxs);
        res.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if(o1==3){
                    return 1;
                }
                if(o1==o2){
                    return 0;
                }
                return o1<o2?-1:1;
            }
        });

        for (int i = 0; i < res.size(); i++) {
            Integer idx = res.get(i);
            if(trainDesc.containsKey(idx)){
                System.out.print(idx + trainDesc.get(idx) +SPACE_STR);
            }else{
                System.out.print(idx +SPACE_STR);
            }

            if(i==res.size()-1){
                for (Integer afterPrintIdx : afterPrintIdxs) {
                    System.out.print(afterPrintIdx + trainDesc.get(afterPrintIdx) +SPACE_STR);
                }
            }
        }
        System.out.println();
        System.out.print("L" +SPACE_STR);
        for (int i = 0; i < res.size(); i++) {
            System.out.print(res.get(i) +SPACE_STR);
            if(i==res.size()-1){
                for (Integer afterPrintIdx : afterPrintIdxs) {
                    System.out.print(afterPrintIdx +SPACE_STR);
                }
            }
        }
        System.out.println();
    }

    private static boolean printCheckIdxs(int idx) {
        return checkIdxs.contains(idx);
    }

    public static boolean printClarify(int idx){
        Set<Integer> clarifyIdxs = trainDesc.keySet();
        return clarifyIdxs.contains(idx);
    }

    public static boolean printRange(int idx){
        if(idxRange.size()==2){
            return idxRange.get(0) <=idx && idxRange.get(1) >=idx;
        }
        return false;
    }

    public static boolean printUnknow(int idx){
        if(idxRange.size()==2){
            return idxRange.get(0) <=idx && idxRange.get(1) >=idx && unknownIdxs.contains(idx);
        }
        return unknownIdxs.contains(idx);
    }

    private static boolean printIgnoreIdxs(int idx) {
        return ignoreIdxs.contains(idx);
    }
}
