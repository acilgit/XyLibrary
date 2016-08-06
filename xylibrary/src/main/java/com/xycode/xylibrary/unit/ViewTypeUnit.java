package com.xycode.xylibrary.unit;

import android.support.annotation.LayoutRes;

/**
 * Created by XY on 2016-08-05.
 */
public class ViewTypeUnit {

    private String mark;
    private  @LayoutRes int layoutId = 0;

    public ViewTypeUnit(String mark, int layoutId) {
        this.mark = mark;
        this.layoutId = layoutId;
    }

    public ViewTypeUnit(int mark, int layoutId) {
        this.mark = mark+"";
        this.layoutId = layoutId;
    }

    public ViewTypeUnit(long mark, int layoutId) {
        this.mark = mark+"";
        this.layoutId = layoutId;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public void setMark(int mark) {
        this.mark = mark+"";
    }

    public void setMark(long mark) {
        this.mark = mark+"";
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
    }
}
