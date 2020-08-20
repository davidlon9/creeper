package com.davidlong.traiker.model;

public class AcceptHourTime {
    private int minStartHour;
    private int maxStartHour;
    private ArriveHour minArriveHour;
    private ArriveHour maxArriveHour;

    public AcceptHourTime() {
    }

    public AcceptHourTime(int minStartHour, int maxStartHour, ArriveHour minArriveHour, ArriveHour maxArriveHour) {
        this.minStartHour = minStartHour;
        this.maxStartHour = maxStartHour;
        this.minArriveHour = minArriveHour;
        this.maxArriveHour = maxArriveHour;
    }

    public AcceptHourTime(int minStartHour, int maxStartHour,int minArriveHour,int maxArriveHour,boolean nextDay) {
        this.minStartHour = minStartHour;
        this.maxStartHour = maxStartHour;
        this.minArriveHour = new ArriveHour(minArriveHour,nextDay);
        this.maxArriveHour = new ArriveHour(maxArriveHour,nextDay);
    }

    public AcceptHourTime(int minStartHour, int maxStartHour,int minArriveHour,int maxArriveHour) {
        this.minStartHour = minStartHour;
        this.maxStartHour = maxStartHour;
        this.minArriveHour = new ArriveHour(minArriveHour);
        this.maxArriveHour = new ArriveHour(maxArriveHour);
    }

    public void setMinStartHour(int minStartHour) {
        this.minStartHour = minStartHour;
    }

    public void setMaxStartHour(int maxStartHour) {
        this.maxStartHour = maxStartHour;
    }

    public void setMinArriveHour(ArriveHour minArriveHour) {
        this.minArriveHour = minArriveHour;
    }

    public void setMaxArriveHour(ArriveHour maxArriveHour) {
        this.maxArriveHour = maxArriveHour;
    }

    public int getMaxStartHour() {
        return maxStartHour;
    }

    public int getMinStartHour() {
        return minStartHour;
    }

    public int getMinArriveHour() {
        return minArriveHour.isNextDay()?minArriveHour.getHour()+24:minArriveHour.getHour();
    }

    public int getMaxArriveHour() {
        return maxArriveHour.isNextDay()?maxArriveHour.getHour()+24:maxArriveHour.getHour();
    }

    public static class ArriveHour {
        private boolean nextDay;
        private int hour;

        public ArriveHour(int hour,boolean nextDay) {
            this.nextDay = nextDay;
            this.hour = hour;
        }

        public ArriveHour(int hour) {
            this.hour = hour;
            this.nextDay = false;
        }

        public boolean isNextDay() {
            return nextDay;
        }

        public int getHour() {
            return hour;
        }
    }
}
