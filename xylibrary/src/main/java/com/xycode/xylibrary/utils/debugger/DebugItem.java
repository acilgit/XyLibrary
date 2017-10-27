package com.xycode.xylibrary.utils.debugger;

import com.xycode.xylibrary.interfaces.Interfaces;
import com.xycode.xylibrary.okHttp.Param;

import java.util.UUID;

/**
 * Created by xiuye on 2017/10/26.
 * @author xiuye
 */

public class DebugItem {

    private String url;
    private String json;
    private String jsonModify;
    private String key;

    private Param param;

    /**
     * 请求开始
     */
    private boolean postBegun = false;
    /**
     * 请求完成
     */
    private boolean postFinished = false;

    /**
     * result
     */
    private Interfaces.CB<String> cb;

    public DebugItem(String url) {
        this.url = url;
    }

   /* public DebugItem(String url, String json) {
        this.url = url;
        this.json = json;
    }*/

    public String getKey() {
        if (key == null) {
            key = UUID.randomUUID().toString();
        }
        return key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getJsonModify() {
        return jsonModify;
    }

    public void setJsonModify(String jsonModify) {
        this.jsonModify = jsonModify;
    }

    public boolean isPostFinished() {
        return postFinished;
    }

    public void setPostFinished(boolean postFinished) {
        this.postFinished = postFinished;
    }

    public Interfaces.CB getCb() {
        return cb;
    }

    public void setCb(Interfaces.CB cb) {
        this.cb = cb;
    }

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }
}
