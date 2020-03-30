package com.test.baserefreshview.test;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.baserefreshview.R;
import com.test.baserefreshview.test.bean.TempBean;

import java.util.ArrayList;

/**
 * @author thisfeng
 * @date 2018/2/6-下午4:29
 * 拖拽测试，点击长按图标 拖拽至目标区域
 */

public class DragTestSaveActivity extends BaseActivity {


    private TextView textview1;

    FrameLayout llRoot;

    int changeCount = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_test3);
        llRoot = findViewById(R.id.llRoot);


        Intent intent = getIntent();

        ArrayList<TempBean> tempBeanList = intent.getParcelableArrayListExtra("list");


        initView(tempBeanList);


      /*  findViewById(R.id.btnRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                for (TempBean tempBean : tempBeanList) {

                    tempBean.setTableNum(tempBean.getTableNum() + "-new" + changeCount++);

                }

                initView(tempBeanList);


            }
        });*/


  /*      findViewById(R.id.btnGet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TS.show("当前View数量" + llRoot.getChildCount());


            }
        });
*/

        ViewTreeObserver viewTreeObserver = llRoot.getViewTreeObserver();

        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int height = llRoot.getHeight();
                int width = llRoot.getWidth();

//                llRoot.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });



    }

    private void initView(ArrayList<TempBean> tempBeanList) {
        //需要移除
        llRoot.removeAllViews();

        for (TempBean tempBean : tempBeanList) {

            TextView textView = new TextView(this);

            textView.setText(tempBean.getTableNum());

            textView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
//            textView.setBackgroundColor(ContextCompat.getColor(getThis(), R.color.logTextGolden));
            textView.setBackground(ContextCompat.getDrawable(getThis(),R.drawable.selector_btn_flag));
            textView.setTextSize(20);
            textView.setPadding(10, 10, 10, 10);

            llRoot.addView(textView);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(textView.getLayoutParams());

            Point point = tempBean.getPoint();

            layoutParams.leftMargin = point.x;
            layoutParams.topMargin = point.y;
            textView.setLayoutParams(layoutParams);

        }
    }

    private int dip2px(Context context, float dipValue) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }


}
