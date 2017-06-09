package com.xycode.xylibrary.utils.serverApiHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.xycode.xylibrary.R;
import com.xycode.xylibrary.Xy;
import com.xycode.xylibrary.interfaces.Interfaces;
import com.xycode.xylibrary.utils.LogUtil.L;

/**
 * 服务器地址摄制页面
 */
public class ServerControllerActivity extends AppCompatActivity implements View.OnClickListener{

    private static ServerControllerActivity instance;
    private static ApiHelper api;
    private TextView tvServer;
    private Switch swLog;

    /**
     * 启动登陆界面，启动时清空当前栈里的activity
     *
     * @param api
     */
    public static void startThis(Activity activity, ApiHelper api) {
        if (!Xy.isRelease() && instance == null && api != null) {
            ServerControllerActivity.api = api;
//            Intent intent = new Intent();
//            intent.setClass(activity, ServerControllerActivity.class);
//        if (!(context instanceof Activity))
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(new Intent(activity, ServerControllerActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Xy.isRelease()) {
            finish();
            return;
        }
        setContentView(R.layout.activity_server_controller);
        instance = this;
        initViews();
    }

    private void initViews() {
        tvServer = (TextView) findViewById(R.id.tvServer);
        swLog = (Switch) findViewById(R.id.swLog);
        if (api.getServer().equals(api.getReleaseUrl())) {
            tvServer.setText("正式服务器：" + api.getServer());
        } else  if (api.getServer().equals(api.getDebugUrl())) {
            tvServer.setText("测试服务器："+ api.getServer());
        } else {
            tvServer.setText("当前服务器："+ api.getServer());
        }

         findViewById(R.id.btnReleaseServer).setOnClickListener(this);
         findViewById(R.id.btnDebugServer).setOnClickListener(this);
         findViewById(R.id.btnOptionServer).setOnClickListener(this);
         findViewById(R.id.tvClose).setOnClickListener(this);

        swLog.setChecked(Xy.getStorage().getBoolean(L.SHOW_LOG, false));
        swLog.setOnCheckedChangeListener((buttonView, isChecked) -> Xy.getStorage().put(L.SHOW_LOG, isChecked));
    }



    public static ApiHelper getApi() {
        return api;
    }
    /**
     * 点击监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnReleaseServer) {
            api.setServerUrl(api.getReleaseUrl());
            finish();
        } else if (i == R.id.btnDebugServer) {
            api.setServerUrl(api.getDebugUrl());
            finish();
        } else if (i == R.id.btnOptionServer) {
            new ServerSelectDialog(ServerControllerActivity.this).show();
        } else if (i == R.id.tvClose) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        instance = null;
        api = null;
        super.onDestroy();
    }
}
