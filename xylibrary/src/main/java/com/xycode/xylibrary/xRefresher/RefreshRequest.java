package com.xycode.xylibrary.xRefresher;

import com.alibaba.fastjson.JSONObject;

import okhttp3.Call;

/**
 * Created by XY on 2017-06-09.
 */
public abstract class RefreshRequest<T> implements IRefreshRequest<T> {

    /**
     * ignore the same item in the list，use return newItem.getId().equals(listItem.getId());
     * if not,  don't override it;
     *
     * @param newItem
     * @param listItem
     * @return
     */
    protected boolean ignoreSameItem(T newItem, T listItem) {
        return false;
    }

    /**
     * reorder the list，returns:
     * -1 large to small
     * 1 small to large
     * 0 same
     * eg: long Long.compareTo(), item0:item1,default result is 1;
     * if no use, don't override if.
     *
     * @param item0
     * @param item1
     * @return
     */
    protected int compareTo(T item0, T item1) {
        return 0;
    }

    /**
     * 返回错误判断的时候会调用此方法，如果返回结果是true则不执行InitRefresher的handleError()方法
     *
     * @param call
     * @param json
     * @return if true to stop InitRefresher.handleError()
     */
    protected boolean handleError(Call call, JSONObject json) {
        return false;
    }

    /**
     * 返回错误判断的时候会调用此方法，如果返回结果是true则不执行InitRefresher的handleError()方法
     *
     * @param call
     * @param resultCode
     * @return if true to stop InitRefresher.handleAllFailureSituation()
     */
    protected boolean handleAllFailureSituation(Call call, int resultCode) {
        return false;
    }
}
