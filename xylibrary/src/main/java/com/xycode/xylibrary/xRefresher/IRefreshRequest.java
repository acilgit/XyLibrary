package com.xycode.xylibrary.xRefresher;

import com.alibaba.fastjson.JSONObject;
import com.xycode.xylibrary.okHttp.Param;

import java.util.List;

/**
 * Created by XY on 2017-06-09.
 */
interface IRefreshRequest<T> {
    /**
     * return the url you need to post, and set the params in the method;
     *
     * @param params
     * @return
     */
    String setRequestParamsReturnUrl(Param params);

    /**
     * handle the JSON and get the List from the json, then return it.
     *
     * @param json
     * @return
     */
    List<T> setListData(JSONObject json);
}
