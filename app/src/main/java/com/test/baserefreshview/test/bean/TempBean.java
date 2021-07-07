package com.test.baserefreshview.test.bean;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author：thisfeng
 * @time 2019-08-26 10:31
 */
public class TempBean implements Parcelable {

    private Point point;

    private String tableNum;

    private int id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TempBean( Point point, String tableNum) {
        this.point = point;
        this.tableNum = tableNum;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }



    public String getTableNum() {
        return tableNum;
    }

    public void setTableNum(String tableNum) {
        this.tableNum = tableNum;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.point, flags);
        dest.writeString(this.tableNum);
        dest.writeInt(this.id);
    }

    protected TempBean(Parcel in) {
        this.point = in.readParcelable(Point.class.getClassLoader());
        this.tableNum = in.readString();
        this.id = in.readInt();
    }

    public static final Creator<TempBean> CREATOR = new Creator<TempBean>() {
        @Override
        public TempBean createFromParcel(Parcel source) {
            return new TempBean(source);
        }

        @Override
        public TempBean[] newArray(int size) {
            return new TempBean[size];
        }
    };
}
