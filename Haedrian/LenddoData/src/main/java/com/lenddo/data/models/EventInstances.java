package com.lenddo.data.models;

/**
 * Created by joseph on 6/3/14.
 */
public class EventInstances {
    private long id;
    private long begin;
    private long end;
    private long endDay;
    private long endMinute;
    private int startDay;
    private int startMinute;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public long getBegin() {
        return begin;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getEnd() {
        return end;
    }

    public void setEndDay(long endDay) {
        this.endDay = endDay;
    }

    public long getEndDay() {
        return endDay;
    }

    public void setEndMinute(long endMinute) {
        this.endMinute = endMinute;
    }

    public long getEndMinute() {
        return endMinute;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public int getStartDay() {
        return startDay;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getStartMinute() {
        return startMinute;
    }
}
