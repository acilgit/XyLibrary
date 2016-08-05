package com.xycode.xylibrary.unit;

/**
 * Created by XY on 2016-08-05.
 */
public class ViewTypeUnit {

    public String mark;
    public int layoutId = 0;

    public ViewTypeUnit(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }
}
