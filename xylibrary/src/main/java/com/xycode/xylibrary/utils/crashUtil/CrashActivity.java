package com.xycode.xylibrary.utils.crashUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xycode.xylibrary.R;
import com.xycode.xylibrary.base.BaseActivity;
import com.xycode.xylibrary.interfaces.Interfaces;
import com.xycode.xylibrary.utils.LogUtil.LogLayout;
import com.xycode.xylibrary.utils.DateUtils;
import com.xycode.xylibrary.utils.LogUtil.L;
import com.xycode.xylibrary.utils.ShareStorage;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public class CrashActivity extends Activity {

    public static ICrash iCrash;
    public static Interfaces.CB<CrashItem> cb;
    public static final String MSG = "msg";
    private static ShareStorage storage;
    private static final String crashSP = "crashSP";
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
        String json = getCrashStorage(this).getString(CRASH_LOG);
        List<L.LogItem> logItems = JSON.parseArray(json, L.LogItem.class);
        L.setLogList(logItems);
        errorMsg = getIntent().getStringExtra(MSG);

        initViews();
    }

    private void initViews() {
        if (iCrash != null) {
            setContentView(iCrash.getLayoutId());
            iCrash.setViews(this);
        } else {
            setContentView(R.layout.activity_crash);

            TextView tv = (TextView) findViewById(R.id.tv);
            tv.setText(errorMsg);
            Button btn = (Button) findViewById(R.id.btn);
            btn.setOnClickListener(
                    v -> finish()
            );
        }
        try {
            crashItem = getCrashItem();
            if (cb != null) {
                cb.go(crashItem);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (L.isDebug() && logLayout == null) {
            logLayout = new LogLayout(this);
            ((ViewGroup) getWindow().getDecorView().getRootView()).addView(logLayout.getView());
        }
    }

    private static ShareStorage getCrashStorage(Context context) {
        if (storage == null) {
            storage = new ShareStorage(context, crashSP);
        }
        return storage;
    }

    public static void setCrashOperation(Context context, Interfaces.CB<CrashItem> catchErrorCallback) {
        setCrashOperation(context, catchErrorCallback, null);
    }
    /**
     * @param context
     * @param catchErrorCallback 返回true则执行相关操作，否则直接关闭程序
     */
    public static void setCrashOperation(Context context, Interfaces.CB<CrashItem> catchErrorCallback, ICrash iCrash) {
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
                L.getLogList().add(new L.LogItem(DateUtils.formatDateTime("yyyy-M-d HH:mm:ss:SSS", DateUtils.getNow()), info, L.LOG_TYPE_CRASH));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                throw ex;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            String jsonString = JSON.toJSONString(L.getLogList());
            getCrashStorage(context).put(CRASH_LOG, jsonString);
            Intent intent = new Intent(context, CrashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(CrashActivity.MSG, info);
            context.startActivity(intent);

            // 杀死该应用进程
            BaseActivity.finishAllActivity();
            try {
                Thread.sleep(300);
                android.os.Process.killProcess(android.os.Process.myPid());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    protected CrashItem getCrashItem() throws PackageManager.NameNotFoundException {
        //应用的版本名称和版本号
        PackageManager pm = this.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
        CrashItem crashItem = new CrashItem();
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
        return crashItem;
    }

}
