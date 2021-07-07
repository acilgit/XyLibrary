package com.xycode.xylibrary.okHttp;

import com.alibaba.fastjson.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by xiuye on 2017/8/17.
 */
public abstract class OkResponseListener implements OkHttp.IOkResponseListener {

    // 验证错误
    protected void handleJsonVerifyError(Call call, Response response, JSONObject json) throws Exception {

    }

    // 其它不在返回结果列表中的代码
    protected void handleJsonOther(Call call, Response response, JSONObject json) throws Exception {

    }

    // 解析Json出错
    protected void handleParseError(Call call, String responseResult) throws Exception {

    }

    // 网络错误
    protected void handleNoServerNetwork(Call call, boolean isCanceled) throws Exception {

    }

    // 网络返回代码Code是错误的代码
    protected void handleResponseCodeError(Call call, Response response) throws Exception {

    }

    // 处理返回结果时，出现异常
    protected void handleResponseFailure(Call call, Response response, Exception e) throws Exception {

    }

    // 以上所有的出错时，统一再一次会执行此方法
    protected void handleAllFailureSituation(Call call, int resultCode) throws Exception {

    }

    /**
     * 成功时，进行线程中处理操作
     *
     * @param call
     * @param json
     * @throws Exception
     */
    protected void handleSuccessInBackground(Call call, JSONObject json) throws Exception {

    }


}
