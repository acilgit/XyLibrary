package com.xycode.xylibrary.utils.debugger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xycode.xylibrary.R;
import com.xycode.xylibrary.Xy;
import com.xycode.xylibrary.base.BaseActivity;
import com.xycode.xylibrary.interfaces.Interfaces;
import com.xycode.xylibrary.okHttp.Param;
import com.xycode.xylibrary.utils.TS;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理请求的Debug页面
 *
 * @author xiuye
 */
public class DebugActivity extends BaseActivity {

    public static final String DEBUG_KEY = "DEBUG_KEY";
    public static final String PARAMS_JSON = "PARAMS_JSON";
    public static final String POST_IS_FINISH = "POST_IS_FINISH";
    /**
     *
     */
    public static final String POST_URL = "POST_URL";
    private static DebugActivity instance;

    public static DebugActivity getInstance() {
        return instance;
    }

    private Param param;

    private DebugItem debugItem;

    /*public static Param getParam() {
        return param;
    }

    public static void setParam(Param param) {
        DebugActivity.param = param;
    }*/

    private static Map<String, DebugItem> debugItems = new ConcurrentHashMap<>();

    public static DebugItem addDebugItem(String url) {
        DebugItem debugItem = new DebugItem(url);
        debugItems.put(debugItem.getKey(), debugItem);
        return debugItem;
    }

    public static DebugItem getDebugItem(String key) {
        return debugItems.get(key);
    }

    /**
     * 暂不使用修改参数
     *
     */
   /* public static void startThis(Param param) {
        Intent intent = new Intent(Xy.getContext(), DebugActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(DebugActivity.PARAMS_JSON, JSON.toJSONString(param))
                .putExtra();
        Xy.getContext().startActivity(intent);
    }*/
    public static void startThis(String debugKey, Interfaces.CB<String> cb) {
        Intent intent = new Intent(Xy.getContext(), DebugActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(DebugActivity.DEBUG_KEY, debugKey)
                .putExtra(DebugActivity.POST_IS_FINISH, true);
        getDebugItem(debugKey).setCb(cb);
        Xy.getContext().startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DebugActivity.instance = this;
//        param  = JSON.parseObject(getIntent().getStringExtra(PARAMS_JSON), Param.class);
        String key = getIntent().getStringExtra(DEBUG_KEY);
        debugItem = getDebugItem(key);

        /* 删除已经完成的结果 */
      /*  for (Map.Entry<String, DebugItem> itemEntry : debugItems.entrySet()) {
            if (itemEntry.getValue().isPostFinished()) {
                debugItems.remove(itemEntry.getKey());
            }
        }*/

        if (debugItem == null) {
            TS.show(getThis(), "Error: No DebugItem [" + key + "]", null);
            finish();
            return;
        }
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.activity_debug);

        ((EditText) rootHolder().getView(R.id.et)).setText(debugItem.getJson());
        rootHolder().setText(R.id.tvTitle, "[Debug] " + debugItem.getUrl());
        rootHolder().setClick(R.id.tvCancel, v -> {
            debugItem.setPostFinished(true);
            finish();
        })
                .setClick(R.id.tvCommit, v -> {
                    if (debugItem.getCb() != null) {
                        debugItem.setJsonModify(rootHolder().getText(R.id.et));
                        debugItem.getCb().go(rootHolder().getText(R.id.et));
                    }
                    debugItem.setPostFinished(true);
                    finish();
                });
    }

    @Override
    public void onBackPressed() {
        if(!debugItem.isPostFinished())
            debugItem.setPostFinished(true);
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected AlertDialog setLoadingDialog() {
        return null;
    }
}
