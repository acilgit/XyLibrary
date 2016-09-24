package com.xycode.xylibrary.interfaces;

import org.json.JSONObject;

/**
 * Created by XY on 2016-09-02.
 */
public class Interfaces {

    public interface OnStringData<T> {
        String getDataString(T data);
    }

    public interface OnCommitListener<T> {
        void onCommit(T obj);

        void onCancel(T obj);
    }

    @FunctionalInterface
    public interface CB<T> {
        void go(T obj);
    }
}
