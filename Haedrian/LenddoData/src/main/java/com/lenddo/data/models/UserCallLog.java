package com.lenddo.data.models;

import android.provider.CallLog;

/**
 * Created by joseph on 4/25/14.
 */
public class UserCallLog {

    public static final String CALL_TYPE_MISSED_CALL = "missed";
    public static final String CALL_TYPE_INCOMING_CALL = "incoming";
    public static final String CALL_TYPE_OUTGOING_CALL = "outgoing";
    private String id;

    public String getNumber() {
        return number;
    }

    private String number;

    public String getType() {
        return type;
    }

    private String type;
    private String date;
    private int duration;
    private String name;

    public void setNumber(String number) {
        this.number = number;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(int type) {
        switch(type) {
            case CallLog.Calls.MISSED_TYPE:
                this.type = UserCallLog.CALL_TYPE_MISSED_CALL;
                break;
            case CallLog.Calls.OUTGOING_TYPE:
                this.type = UserCallLog.CALL_TYPE_OUTGOING_CALL;
                break;
            case CallLog.Calls.INCOMING_TYPE:
                this.type = UserCallLog.CALL_TYPE_INCOMING_CALL;
                break;
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
