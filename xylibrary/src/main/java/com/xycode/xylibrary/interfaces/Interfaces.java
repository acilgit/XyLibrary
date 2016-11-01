package com.xycode.xylibrary.interfaces;

import java.io.Serializable;

/**
 * Created by XY on 2016-09-02.
 */
public class Interfaces {

    @FunctionalInterface
    public interface OnStringData<T> extends Serializable{
        String getDataString(T data);
    }

    public interface OnCommitListener<T> extends Serializable{
        void onCommit(T obj);

        void onCancel(T obj);
    }

    @FunctionalInterface
    public interface CB<T> extends Serializable{
        void go(T obj);
    }

    @FunctionalInterface
    public interface FeedBack<T> extends Serializable{
        Object go(T obj);
    }
}
