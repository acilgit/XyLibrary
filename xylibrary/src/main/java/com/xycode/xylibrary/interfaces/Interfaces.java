package com.xycode.xylibrary.interfaces;

/**
 * Created by XY on 2016-09-02.
 */
public class Interfaces {

    public interface OnStringData<T> {
        String getDataString(T data);
    }

    public interface OnDialogClickListener<T> {
        void onCommit(T obj);

        void onCancel(T obj);
    }

}
