package com.xycode.xylibrary.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.Toast;

/**
 * Toast
 *
 * @author way
 */
public class TS {

    private static Handler uiHandler = new Handler(Looper.getMainLooper());
    private static Context context;
    private static Toast toast;
    private static IToastLayoutSetter toastSetter;

    private static int toastLayoutId = -1;

    public static void init(@LayoutRes int toastLayoutId, Context defaultContext, IToastLayoutSetter toastSetter) {
        TS.context = defaultContext;
        TS.toastLayoutId = toastLayoutId;
        TS.toastSetter = toastSetter;
    }

    public static void init(Context defaultContext) {
        init(-1, defaultContext, null);
    }

    public static void show(int resText) {
        String text = TS.context.getString(resText);
        show(TS.context, text);
    }

    public static void show(String text) {
        show(TS.context, text);
    }

    public static void show(final Context context, final int resText) {
        show(context, TS.context.getString(resText));
    }

    public static void show(final Context context, final String text) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            doShow(context, text);
        } else {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    doShow(context, text);
                }
            });
        }
    }

    public static void cancel() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            doCancel();
        } else {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    doCancel();
                }
            });
        }
    }

    private static void doCancel() {
        if(toast != null)
        toast.cancel();
    }

    private static void doShow(Context context, String text) {
        //取消显示上一个Toast
        doCancel();

        if (TS.toastLayoutId != -1) {
            toast = new Toast(context);
            View view = View.inflate(context, TS.toastLayoutId, null);
//		toast.setGravity(Gravity.TOP, 0, context.getResources().getDimensionPixelOffset(R.dimen.toast_margin_top));
            toast.setView(view);
            toast.setDuration(Toast.LENGTH_SHORT);
            if (toastSetter != null) toastSetter.onToastLayout(view, toast);
        } else {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }

        toast.show();
    }

  /*  public static class ToastSetter {

        private IToastLayoutSetter toastLayoutSetter;

        public ToastSetter(boolean onlyShowOneToast, IToastLayoutSetter toastLayoutSetter) {
            this.toastLayoutSetter = toastLayoutSetter;
            TS.onlyShowOneToast = onlyShowOneToast;
        }
        protected void setToastOffsetAfterAnotherNewToast(Toast oldToast){
        }
    }*/

    public interface IToastLayoutSetter {
        void onToastLayout(View root, Toast toast);
    }

}
