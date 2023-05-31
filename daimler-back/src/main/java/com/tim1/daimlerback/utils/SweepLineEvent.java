package com.tim1.daimlerback.utils;

public class SweepLineEvent {
    private long timeStamp;
    private int add;

    public SweepLineEvent(long timeStamp, int add) {
        this.timeStamp = timeStamp;
        this.add = add;
    }

    public int compareTo(SweepLineEvent other) {
        return Long.compare(timeStamp, other.getTimeStamp());
    }
    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getAdd() {
        return add;
    }

    public void setAdd(int add) {
        this.add = add;
    }
}
