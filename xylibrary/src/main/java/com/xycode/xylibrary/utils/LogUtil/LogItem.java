package com.xycode.xylibrary.utils.LogUtil;

/**
 * Created by XY on 2017-06-09.
 */
public class LogItem {

    public static final int LOG_TYPE_E = 0;
    public static final int LOG_TYPE_CRASH = 1;
    public static final int LOG_TYPE_I = 2;
    public static final int LOG_TYPE_D = 3;

    private String dateTime;
    private String title;
    private String content;
    /**
     * 0: e         white
     * 1: crash     red
     * 2: i         gray
     * 3: d         blue
     */
    private int type = 0;

    public LogItem() {
    }

    public LogItem(String dateTime, String content) {
        this.dateTime = dateTime;
        this.content = content;
    }

    public LogItem(String dateTime, String content, int type) {
        this.dateTime = dateTime;
        this.content = content;
        this.type = type;
    }

    public LogItem(String dateTime, String title, String content) {
        this.dateTime = dateTime;
        this.title = title;
        this.content = content;
    }

    public LogItem(String dateTime, String title, String content, int type) {
        this.dateTime = dateTime;
        this.title = title;
        this.content = content;
        this.type = type;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
