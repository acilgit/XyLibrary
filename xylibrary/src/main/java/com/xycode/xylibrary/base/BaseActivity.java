package com.xycode.xylibrary.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.xycode.xylibrary.annotation.annotationHelper.StateBinder;
import com.xycode.xylibrary.interfaces.Interfaces;
import com.xycode.xylibrary.okHttp.CallItem;
import com.xycode.xylibrary.okHttp.Header;
import com.xycode.xylibrary.okHttp.OkHttp;
import com.xycode.xylibrary.okHttp.Param;
import com.xycode.xylibrary.utils.LogUtil.LogLayout;
import com.xycode.xylibrary.unit.MsgEvent;
import com.xycode.xylibrary.utils.LogUtil.L;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.RequestBody;

/**
 * Created by Administrator on 2016/1/11 0011.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_GOT_RESULT = 100;
    public static final int REQUEST_CODE_PHOTO_SELECT = 202;
    public static final int REQUEST_CODE_MULTI_PHOTO_SELECT = 203;
    public static final int REQUEST_CODE_GOT_PHONE_NUMBER = 301;

    private static List<Activity> activities = new LinkedList<>();

    private static AlertDialog loadingDialog;

    //    private static AlertDialog loadingDialog;
    private static boolean loadingDialogShowManual = false;

    protected List<okhttp3.Call> requestList = new ArrayList<>();

    private BroadcastReceiver finishReceiver;
    private BaseActivity thisActivity;

    public static final String ACTION_FINISH_ACTIVITY = "FinishBaseActivity";
    private LogLayout logLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = this;
        addActivity(this);
        if (useEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        if (useEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        for (okhttp3.Call call : requestList) {
            if (call != null) {
                call.cancel();
            }
        }
        dismissLoadingDialog();
        removeActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (L.showLog() && logLayout == null) {
            logLayout = LogLayout.attachLogLayoutToActivity(getThis());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected BaseActivity getThis() {
        return this;
    }

    public void start(Class<? extends Activity> activityClass) {
        Intent intent = new Intent();
        intent.setClass(this, activityClass);
        this.startActivity(intent);
    }

    public void start(Class<? extends Activity> activityClass, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, activityClass);
        this.startActivityForResult(intent, requestCode);
    }

    public void start(Class<? extends Activity> activityClass, BaseIntent baseIntent) {
        Intent intent = new Intent();
        intent.setClass(this, activityClass);
        baseIntent.setIntent(intent);
        this.startActivity(intent);
    }

    public void start(Class<? extends Activity> activityClass, BaseIntent baseIntent, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, activityClass);
        baseIntent.setIntent(intent);
        this.startActivityForResult(intent, requestCode);
    }

    protected static List<Activity> getActivities() {
        return activities;
    }

    public boolean isLoadingDialogShowing() {
        return loadingDialog != null && loadingDialog.isShowing();
    }

    public void showLoadingDialog() {
        showDialog(false);
    }

    public void showLoadingDialogManualDismiss() {
        showDialog(true);
    }

    private void showDialog(boolean manualDismiss) {
        loadingDialogShowManual = manualDismiss;
        loadingDialog = setLoadingDialog();
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    public static void dismissLoadingDialog() {
        loadingDialogShowManual = false;
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public static void dismissLoadingDialogByManualState() {
        if (!loadingDialogShowManual && loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        StateBinder.saveState(this, outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        StateBinder.bindState(this, savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

  /*  public void showLoadingDialog(CharSequence text) {
        showLoadingDialog(text, false);
    }

    public void showLoadingDialog(CharSequence text, boolean cancelable) {
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(this);
        } else {
            loadingDialog.dismiss();
        }

        loadingDialog.setCanceledOnTouchOutside(cancelable);
        if (cancelable) {
            loadingDialog.setOnCancelListener(null);
        } else {
            loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
        }

        loadingDialog.setMessage(text);
        loadingDialog.show();
    }*/

    public void hideSoftInput() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    protected void registerFinishReceiver() {
        finishReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_FINISH_ACTIVITY);
        LocalBroadcastManager.getInstance(this).registerReceiver(finishReceiver, filter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface BaseIntent {
        void setIntent(Intent intent);
    }


    /**
     * static methods
     */


    public static void addActivity(Activity activity) {
        if (!activities.contains(activity)) {
            activities.add(activity);
        }
        StringBuilder sb = new StringBuilder("addActivity: [");
        for (int i = 0; i < activities.size(); i++) {
            sb.append(activities.get(i).getClass().getSimpleName()).append(i < activities.size() - 1 ? ", " : "]");
        }
        L.i(sb.toString());
    }

    public static void removeActivity(Activity activity) {
        if (activities.contains(activity)) {
            activities.remove(activity);
        }
        StringBuilder sb = new StringBuilder("removeActivity: [");
        for (int i = 0; i < activities.size(); i++) {
            sb.append(activities.get(i).getClass().getSimpleName()).append(i < activities.size() - 1 ? ", " : "]");
        }
        L.i(sb.toString());
    }

    public static void finishAllActivity() {
        for (final Activity activity : activities) {
            if (activity != null) {
                activity.runOnUiThread(() -> activity.finish());
            } else {
                activities.remove(activity);
            }
        }
    }

    public static void exitApplication() {
        finishAllActivity();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * judge a activity is on foreground
     *
     * @param activity
     */
    public static boolean isForeground(Activity activity) {
        if (TextUtils.isEmpty(activity.getClass().getName())) {
            return false;
        }
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (activity.getClass().getName().equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static Activity getForegroundActivity(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            Log.e(" getForegroundActivity ", cpn.getClassName());
            for (Activity activity : activities) {
                if (activity.getClass().getName().equals(cpn.getClassName())) {
                    Log.e(" getActivity ", activity.getClass().getName());
                    return activity;
                }
            }
        }
        return null;
    }

    public static Activity getActivityByClassName(String className) {
        for (Activity activity : activities) {
            if (activity.getClass().getName().contains(className)) {
                Log.e("ActivityClassName ", activity.getClass().getName());
                return activity;
            }
        }
        return null;
    }

    /**
     * EventBus
     */
    protected boolean useEventBus() {
        return logLayout != null;
    }

    public void postEvent(String eventName) {
        postEvent(eventName, null, null);
    }

    public void postEvent(String eventName, Interfaces.FeedBack feedBack) {
        postEvent(eventName, null, feedBack);
    }

    public void postEvent(String eventName, Object object) {
        postEvent(eventName, object, null);
    }

    public void postEvent(String eventName, String object) {
        postEvent(eventName, object, null);
    }

    public void postEvent(String eventName, Object object, Interfaces.FeedBack feedBack) {
        EventBus.getDefault().post(new MsgEvent(eventName, object, feedBack));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MsgEvent event) {
        if (logLayout != null && event.getEventName().equals(L.EVENT_LOG)) {
            logLayout.refreshData();
        }
    }

    @Subscribe
    public void onEventBackground(MsgEvent event) {

    }

    /**
     * okHttp request
     */
    public CallItem newCall() {
        return OkHttp.newCall(getThis());
    }

    /**
     * {@link #newCall}
     */
    @Deprecated
    public okhttp3.Call postForm(String url, Param param, boolean addDefaultHeader, OkHttp.OkResponseListener okResponseListener) {
        return OkHttp.postForm(getThis(), url, setFormBody(param), null, addDefaultHeader, okResponseListener);
    }

    /**
     * {@link #newCall}
     */
    @Deprecated
    private okhttp3.Call postForm(String url, RequestBody body, Header header, boolean addDefaultHeader, OkHttp.OkResponseListener okResponseListener) {
        okhttp3.Call call = OkHttp.postForm(getThis(), url, body, header, addDefaultHeader, okResponseListener);
        requestList.add(call);
        return call;
    }

    public RequestBody setFormBody(Param params) {
        return OkHttp.setFormBody(params, false);
    }

    /**
     * Results return
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = null;
        if (data != null) {
            uri = data.getData();
        }
        onPhotoSelectResult(resultCode, uri);
    }

    protected void onPhotoSelectResult(int resultCode, Uri uri) {

    }

    protected abstract AlertDialog setLoadingDialog();

    public AlertDialog getLoadingDialog() {
        return loadingDialog;
    }

    protected void setWindowMode(int windowMode) {
        getWindow().setSoftInputMode(windowMode);
    }

    protected static class WindowMode {
        public static int INPUT_ADJUST = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
    }


}
