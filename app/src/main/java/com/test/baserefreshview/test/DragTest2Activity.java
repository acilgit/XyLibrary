package com.test.baserefreshview.test;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.baserefreshview.R;
import com.test.baserefreshview.test.bean.TempBean;
import com.test.baserefreshview.test.weiget.DragLayout;

import java.util.ArrayList;

/**
 * @author thisfeng
 * @date 2018/2/6-下午4:29
 * 拖拽测试，点击长按图标 拖拽至目标区域
 */

public class DragTest2Activity extends BaseActivity {


    private DragLayout dragLayout;

    private TextView tvChange;

    int count = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_test2);
        dragLayout = findViewById(R.id.dragLayout);
        tvChange = findViewById(R.id.tvChange);


        findViewById(R.id.button).setOnClickListener(onClickListener);


        //去新页面 展示
        findViewById(R.id.btnSave).setOnClickListener(onClickListener);

        findViewById(R.id.btnLeft).setOnClickListener(onClickListener);
        findViewById(R.id.btnRight).setOnClickListener(onClickListener);
        findViewById(R.id.btnBottom).setOnClickListener(onClickListener);
        findViewById(R.id.btnTop).setOnClickListener(onClickListener);


//        dragLayout.addView(new MyCanvas(getThis()));


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button:
                    TextView textView = new TextView(DragTest2Activity.this);

                    textView.setText(String.valueOf(count++));
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);

                    textView.setLayoutParams(layoutParams);
//                    textView.setBackgroundColor(ContextCompat.getColor(getThis(), R.color.logTextGolden));
                    textView.setBackground(ContextCompat.getDrawable(getThis(),R.drawable.selector_btn_flag));
                    textView.setTextSize(20);
                    textView.setPadding(10, 10, 10, 10);
                    dragLayout.addView(textView);
                    break;
                case R.id.btnSave:

                    ArrayList<TempBean> allLocationViewList = dragLayout.getAllLocationViewList();

                    start(DragTestSaveActivity.class, new BaseIntent() {
                        @Override
                        public void setIntent(Intent intent) {

                            intent.putParcelableArrayListExtra("list", allLocationViewList);


                        }
                    });
                    break;
                case R.id.btnLeft:


                    dragLayout.scrollLeft();

//                dragLayout.getCurrentChangedView().scrollTo(    dragLayout.getCurrentChangedView().getTop()-10,    dragLayout.getCurrentChangedView().getLeft()-10);


                    break;
                case R.id.btnRight:


                    dragLayout.scrollRight();

//                dragLayout.getCurrentChangedView().scrollTo(   x ,  y  );

                    break;
                case R.id.btnTop:
                    dragLayout.scrollTop();

                    break;
                case R.id.btnBottom:
                    dragLayout.scrollBottom();


                    break;
                default:
            }
        }
    };




}
