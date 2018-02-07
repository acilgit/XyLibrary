package com.test.baserefreshview.test.bean;

/**
 * @author thisfeng
 * @date 2018/2/7-下午12:04
 */

public class DragDataBean {

    private String Name;
    private int color;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public DragDataBean(String name, int color) {
        Name = name;
        this.color = color;
    }
}
