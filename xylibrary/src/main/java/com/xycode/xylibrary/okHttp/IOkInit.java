package com.xycode.xylibrary.okHttp;

import com.alibaba.fastjson.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by xiuye on 2017/8/17.
 */
public interface IOkInit {
    /**
     * the first time the response got from internet
     * 0：RESULT_ERROR ;
     * 1：RESULT_SUCCESS ;
     * -1：RESULT_VERIFY_ERROR;
     * 2: RESULT_OTHER ;
     * at IOkResponse interface callback
     *
     * @param call
     * @param response
     * @param json
     * @return
     */
    int judgeResultWhenFirstReceivedResponse(Call call, Response response, JSONObject json);

    /**
     * no network or  or call back cancel
     *
     * @param call
     * @param isCanceled
     */
    void networkError(Call call, boolean isCanceled);

    /**
     * after judgeResultWhenFirstReceivedResponse
     * result code not in  [200...300)
     *
     * @param call
     * @param response
     */
    void receivedNetworkErrorCode(Call call, Response response);

    /**
     * after judgeResultWhenFirstReceivedResponse
     * result is SUCCESS
     * returns ---
     * false: go on callbacks
     * true：interrupt callbacks
     * 可在此方法保存资料到SQLite
     *
     * @param call
     * @param response
     * @param json
     * @param resultCode
     * @return
     */
    boolean resultSuccessByJudge(Call call, Response response, JSONObject json, int resultCode);

    /**
     * after judgeResultWhenFirstReceivedResponse
     * when parse JSON failed
     *
     * @param call
     * @param parseErrorResult
     */
    void judgeResultParseResponseFailed(Call call, String parseErrorResult, Exception e);

    /**
     * add defaultParams in param
     * when setFormBody requestBody
     *
     * @param defaultParams
     * @return
     */
    Param setDefaultParams(Param defaultParams);

    /**
     * add defaultHeader in header
     * when new a request
     *
     * @param defaultHeader
     * @return
     */
    Header setDefaultHeader(Header defaultHeader);

}
