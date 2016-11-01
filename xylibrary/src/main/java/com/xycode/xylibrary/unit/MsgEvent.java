package com.xycode.xylibrary.unit;

import com.xycode.xylibrary.interfaces.Interfaces;

/**
 * Created by XY on 2016-11-01.
 */

public class MsgEvent {

    private String eventName;
    private Object object;
    private Interfaces.CB cb;
    private Interfaces.FeedBack feedBack;

    public MsgEvent(String eventName, Object object, Interfaces.FeedBack feedBack) {
        this.eventName = eventName;
        this.object = object;
        this.feedBack = feedBack;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getString() {
        return (String) object;
    }

    public void setString(Object object) {
        this.object = object;
    }

    public int getInt() {
        return (int) object;
    }

    public void setInt(int object) {
        this.object = object;
    }

    public double getDouble() {
        return (double) object;
    }

    public void setDouble(double object) {
        this.object = object;
    }


    public Interfaces.FeedBack getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(Interfaces.FeedBack feedBack) {
        this.feedBack = feedBack;
    }
}
