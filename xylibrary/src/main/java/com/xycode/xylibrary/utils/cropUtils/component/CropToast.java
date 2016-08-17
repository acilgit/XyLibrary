package com.xycode.xylibrary.utils.cropUtils.component;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xycode.xylibrary.R;


public class CropToast {

    private static Toast mToast;

    public static void show(Context context, String text) {
        show(context, text, 0);
    }

    public static void show(Context context, String text, int yOffset) {
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = new Toast(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.toast, null);
        mToast.setView(view);

        mToast.setGravity(Gravity.TOP, 0, yOffset);
        mToast.setDuration(Toast.LENGTH_SHORT);

        TextView textView = (TextView) view.findViewById(R.id.textMessage);
        textView.setText(text);

        mToast.show();
    }

    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}