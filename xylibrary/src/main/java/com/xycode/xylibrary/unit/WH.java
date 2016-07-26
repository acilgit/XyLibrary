package com.xycode.xylibrary.unit;

/**
 * Created by XY on 2016-07-26.
 */
public class WH {
    public int width;
    public int height;

    public WH(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public float getAspectRatio() {
        return width * 1.0f / height;
    }


}
