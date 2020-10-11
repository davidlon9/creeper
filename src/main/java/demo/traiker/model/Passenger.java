package demo.traiker.model;

public class Passenger {
//可选的ticketType
//成人票 1
//儿童票 2
//学生票 3
//残军票 4
    private String seatType;
    private int ticketType=1;
    private String passengerName;
    private String passengerIdTypeCode;
    private String passengerType;
    private String passengerIdNo;
    private String mobileNo;
    private String allEncStr;

    /**
     * seat_type,0,ticket_type,passenger_name,passenger_id_type_code,passenger_id_no,mobile_no,save_status,allEncStr
     * */
    public String getPassengerTicketStr(){
        return seatType+",0,"+ticketType+","+passengerName+","+passengerIdTypeCode+","+passengerIdNo+","+mobileNo+",N,"+allEncStr;
    }
//    passenger_name + "," + passenger_id_type_code + "," + passenger_id_no + "," + passenger_type;
    public String getOldPassengerStr(){
        return passengerName+","+passengerIdTypeCode+","+passengerIdNo+","+passengerType+"_";
    }

    public String getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public int getTicketType() {
        return ticketType;
    }

    public void setTicketType(int ticketType) {
        this.ticketType = ticketType;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerIdTypeCode() {
        return passengerIdTypeCode;
    }

    public void setPassengerIdTypeCode(String passengerIdTypeCode) {
        this.passengerIdTypeCode = passengerIdTypeCode;
    }

    public String getPassengerIdNo() {
        return passengerIdNo;
    }

    public void setPassengerIdNo(String passengerIdNo) {
        this.passengerIdNo = passengerIdNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAllEncStr() {
        return allEncStr;
    }

    public void setAllEncStr(String allEncStr) {
        this.allEncStr = allEncStr;
    }
}
