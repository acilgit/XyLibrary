package com.xycode.xylibrary.xRefresher;

import com.alibaba.fastjson.JSONObject;

import okhttp3.Call;

/**
 * 设置Refresher的默认选项
 */
public interface InitRefresher {

    /**
     * 默认的结果出错操作，例如toast
     *
     * @param call
     * @param json
     */
    void handleError(Call call, JSONObject json);

    /**
     * 默认的所有出错的操作，例如 关闭LoadingDialog显示
     *
     * @param call
     * @param resultCode 结果值
     */
    void handleAllFailureSituation(Call call, int resultCode);

    /**
     * 设置默认的Header
     *
     * @return
     */
    boolean addDefaultHeader();

    /**
     * 设置默认的参数
     *
     * @return
     */
    boolean addDefaultParam();
}
