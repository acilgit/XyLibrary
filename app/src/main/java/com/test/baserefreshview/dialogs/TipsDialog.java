package com.test.baserefreshview.dialogs;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.test.baserefreshview.R;
import com.xycode.xylibrary.interfaces.Interfaces;

/**
 * 小提示dialog
 */
public class TipsDialog {

    public Activity context;
    public Interfaces.OnCommitListener buttonClickListener;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private String title;
    private String message;
    private String positiveButton;
    private String negativeButton;

    public TipsDialog(Activity context, String title, String message, String positiveButton, String negativeButton, Interfaces.OnCommitListener buttonClickListener) {
        this.context = context;
        this.title = title;
        this.message = message;
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
        this.buttonClickListener = buttonClickListener;
        init();
    }

    public TipsDialog(Activity context, String title, String message, Interfaces.OnCommitListener buttonClickListener) {
        this.context = context;
        this.title = title;
        this.message = message;
        this.buttonClickListener = buttonClickListener;
        init();
    }

    private void init() {
        if (builder == null) {
            builder = new AlertDialog.Builder(context);
            if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(message)) {
                builder.setTitle(title);
                builder.setMessage(message);
            }
            builder.setPositiveButton((positiveButton == null ? context.getString(R.string.confirm) : positiveButton), (dialog, which) ->
                    buttonClickListener.onCommit(null));


            builder.setNegativeButton((negativeButton == null ? context.getString(R.string.cancle) : negativeButton), (dialog12, which) ->
                    buttonClickListener.onCancel(null));
            dialog = builder.create();
        }
    }


    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    /**
     * @return 是否正在展示dialog, 可根据此判断 避免多次展示
     */
    public boolean isShowing() {
        return dialog.isShowing();
    }
}

