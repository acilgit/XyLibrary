package com.xycode.xylibrary.annotation;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/18 0018.
 */

public class Sparsekey implements Parcelable {
    int key;

    public Sparsekey(int key, int ids) {
        this.key = key;
        this.ids = ids;
    }

    int ids;

    public Sparsekey(Parcel in) {
        this.key = in.readInt();
        this.ids = in.readInt();
    }


    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(key);
        dest.writeInt(ids);
    }

    public static final Parcelable.Creator<Sparsekey> CREATOR = new Parcelable.Creator<Sparsekey>() {
        public Sparsekey createFromParcel(Parcel in) {
            return new Sparsekey(in);
        }

        public Sparsekey[] newArray(int size) {
            return new Sparsekey[size];
        }
    };
}
