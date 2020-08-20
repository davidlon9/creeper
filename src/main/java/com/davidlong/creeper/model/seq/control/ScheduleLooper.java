package com.davidlong.creeper.model.seq.control;

public class ScheduleLooper extends Looper {
    private Trigger trigger;

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    public static class Trigger {
        private String startTimeExpr;
        private String endTimeExpr;
        private int timeInterval;
        private String intervalUnit;
        private int repeatCount;
        private boolean repeatForever;
        private int delay;

        public int getDelay() {
            return delay;
        }

        public void setDelay(int delay) {
            this.delay = delay;
        }

        public String getStartTimeExpr() {
            return startTimeExpr;
        }

        public void setStartTimeExpr(String startTimeExpr) {
            this.startTimeExpr = startTimeExpr;
        }

        public String getEndTimeExpr() {
            return endTimeExpr;
        }

        public void setEndTimeExpr(String endTimeExpr) {
            this.endTimeExpr = endTimeExpr;
        }

        public int getTimeInterval() {
            return timeInterval;
        }

        public void setTimeInterval(int timeInterval) {
            this.timeInterval = timeInterval;
        }

        public String getIntervalUnit() {
            return intervalUnit;
        }

        public void setIntervalUnit(String intervalUnit) {
            this.intervalUnit = intervalUnit;
        }

        public int getRepeatCount() {
            return repeatCount;
        }

        public void setRepeatCount(int repeatCount) {
            this.repeatCount = repeatCount;
        }

        public boolean isRepeatForever() {
            return repeatForever;
        }

        public void setRepeatForever(boolean repeatForever) {
            this.repeatForever = repeatForever;
        }
    }

}
