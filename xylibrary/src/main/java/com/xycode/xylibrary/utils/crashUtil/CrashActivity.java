package com.xycode.xylibrary.utils.crashUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xycode.xylibrary.R;
import com.xycode.xylibrary.Xy;
import com.xycode.xylibrary.base.BaseActivity;
import com.xycode.xylibrary.interfaces.Interfaces;
import com.xycode.xylibrary.utils.LogUtil.LogItem;
import com.xycode.xylibrary.utils.LogUtil.LogLayout;
import com.xycode.xylibrary.utils.DateUtils;
import com.xycode.xylibrary.utils.LogUtil.L;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public class CrashActivity extends AppCompatActivity {

    public static ICrash iCrash;
    public static Interfaces.CB<CrashItem> cb;
    public static final String MSG = "msg";
    public static final String CRASH_LOG = "crashLog";

    private LogLayout logLayout;
    private CrashItem crashItem;
    protected String errorMsg;

    private static CrashActivity instance;

    public static CrashActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashActivity.instance = this;
        String json = Xy.getStorage(Xy.getContext()).getString(CRASH_LOG);
        List<LogItem> logItems = JSON.parseArray(json, LogItem.class);
        L.setLogList(logItems);
        errorMsg = getIntent().getStringExtra(MSG);

        initViews();
    }

    private void initViews() {
        crashItem = getCrashItem(errorMsg);
        if (cb != null) {
            cb.go(crashItem);
        }
        if (iCrash != null && iCrash.getLayoutId() != 0) {
            setContentView(iCrash.getLayoutId());
            iCrash.setViews(this, crashItem);
        } else {
            setContentView(R.layout.activity_base_crash);

            TextView tv = (TextView) findViewById(R.id.tv);
            tv.setText(crashItem != null ? crashItem.toString() : errorMsg);
            Button btn = (Button) findViewById(R.id.btn);
            btn.setOnClickListener(
                    v -> finish()
            );
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (L.showLog() && logLayout == null) {
            logLayout = LogLayout.attachLogLayoutToActivity(this);
        }
    }

    public static void setCrashOperation(Interfaces.CB<CrashItem> catchErrorCallback) {
        setCrashOperation( catchErrorCallback, null);
    }

    /**
     * @param catchErrorCallback 返回true则执行相关操作，否则直接关闭程序
     */
    public static void setCrashOperation(Interfaces.CB<CrashItem> catchErrorCallback, ICrash iCrash) {
        CrashActivity.cb = catchErrorCallback;
        CrashActivity.iCrash = iCrash;
        Thread.setDefaultUncaughtExceptionHandler((t, ex) -> {
            ByteArrayOutputStream baos = null;
            PrintStream printStream = null;
            String info = null;
            try {
                baos = new ByteArrayOutputStream();
                printStream = new PrintStream(baos);
                ex.printStackTrace(printStream);
                byte[] data = baos.toByteArray();
                info = new String(data);
                data = null;
                L.getLogList().add(new LogItem(DateUtils.formatDateTime("yyyy-M-d HH:mm:ss:SSS", DateUtils.getNow()), info, LogItem.LOG_TYPE_CRASH));
            } catch (Exception e) {
                e.printStackTrace();
            }
            ex.printStackTrace();
            String jsonString = JSON.toJSONString(L.getLogList());
            if (Xy.getStorage(Xy.getContext()).getEditor().putString(CRASH_LOG, jsonString).commit()) {
                Intent intent = new Intent(Xy.getContext(), CrashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(CrashActivity.MSG, info);
                Xy.getContext().startActivity(intent);
                // 杀死该应用进程
                BaseActivity.exitApplication();
            }

        });
    }

    public CrashItem getCrashItem(String errorMsg) {
        CrashItem crashItem = null;
        try {
            //应用的版本名称和版本号
            PackageManager pm = this.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
            crashItem = new CrashItem();
            crashItem.setVersionName(pi.versionName);
            crashItem.setVersionCode(pi.versionCode);
            //android版本号
            crashItem.setRelease(Build.VERSION.RELEASE);
            crashItem.setSdk(Build.VERSION.SDK_INT);
            //手机制造商
            crashItem.setManufacturer(Build.MANUFACTURER);
            //手机型号
            crashItem.setModel(Build.MODEL);
            crashItem.setErrorMsg(errorMsg);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return crashItem;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.getLogList().clear();
    }
}
