package com.xycode.xylibrary.unit;

/**
 * Created by XY on 2017-04-10.
 * 定位位置
 */

public class Gps {

    private double lat;
    private double lon;

    public Gps(double lat, double lon) {
        setLat(lat);
        setLon(lon);
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return lat + "," + lon;
    }
}