package com.xycode.xylibrary.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/11 0011.
 */
public class BaseActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_GOT_RESULT = 100;

    private static List<Activity> activities = new LinkedList<>();

    ProgressDialog mProgressDialog;
    //    RequestQueue mVolleyRequestQueue;
    BroadcastReceiver mFinishReceiver;
    BaseActivity thisActivity;

    public static final String ACTION_FINISH_ACTIVITY = "FinishBaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = this;
//        if (!(thisActivity instanceof LoginActivity)) {
        registerFinishReceiver();
//        }
        addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
//        cancelVolleyRequestQueue();
        removeActivity(this);
    }

    protected BaseActivity getThis() {
        return this;
    }

   /* public void start(Activity activity) {
        if (thisActivity!=null) {
            Intent intent = new Intent();
            intent.setClass(thisActivity, activity.getClass());
            activity.startActivity(intent);
        }
    }*/

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

    public void showProgressDialog(CharSequence text) {
        showProgressDialog(text, false);
    }

    public void showProgressDialog(CharSequence text, boolean cancelable) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        } else {
            mProgressDialog.dismiss();
        }

        mProgressDialog.setCanceledOnTouchOutside(cancelable);
        if (cancelable) {
            mProgressDialog.setOnCancelListener(null);
        } else {
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
        }

        mProgressDialog.setMessage(text);
        mProgressDialog.show();
    }

    public void setProgressDialogMessage(int resId) {
        setProgressDialogMessage(getString(resId));
    }

    public void setProgressDialogMessage(CharSequence text) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setMessage(text);
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public void hideSoftInput() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    protected void registerFinishReceiver() {
        mFinishReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_FINISH_ACTIVITY);
        LocalBroadcastManager.getInstance(this).registerReceiver(mFinishReceiver, filter);
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
        for (Activity a : activities) {
            Log.e(" addActivity ", a.getClass().getName());
        }
    }

    public static void removeActivity(Activity activity) {
        if (activities.contains(activity)) {
            activities.remove(activity);
        }
        for (Activity a : activities) {
            Log.e(" removeActivity ", a.getClass().getName());
        }
    }

    public static void finishAllActivity() {
        for (final Activity activity : activities) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.finish();
                    }
                });
            } else {
                activities.remove(activity);
            }
        }
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
            if (activity.getClass().getName().equals(className)) {
                Log.e("ActivityClassName ", activity.getClass().getName());
                return activity;
            }
        }
        return null;
    }


}
