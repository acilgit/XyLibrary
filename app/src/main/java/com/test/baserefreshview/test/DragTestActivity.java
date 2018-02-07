package com.test.baserefreshview.test;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test.baserefreshview.R;

/**
 * @author thisfeng
 * @date 2018/2/6-下午4:29
 * 拖拽测试，点击长按图标 拖拽至目标区域
 */

public class DragTestActivity extends AppCompatActivity implements View.OnDragListener {
    RelativeLayout rlContent;
    TextView tvContent;
    ImageView ico;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);
        tvContent = (TextView) findViewById(R.id.tvContent);
        rlContent = (RelativeLayout) findViewById(R.id.rlContent);
        ico = (ImageView) findViewById(R.id.icon);

        rlContent.setOnDragListener(this);
        ico.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item("我来了");
                ClipData data = new ClipData(null, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                v.startDrag(data, new View.DragShadowBuilder(v), null, 0);

                return false;
            }
        });
    }



    @Override
    public boolean onDrag(View v, DragEvent event) {

        final int action = event.getAction();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED: // 拖拽开始
                Log.i("拖拽事件", "拖拽开始");
                return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

            case DragEvent.ACTION_DRAG_ENTERED: // 被拖拽View进入目标区域
                Log.i("拖拽事件", "被拖拽View进入目标区域");
                return true;

            case DragEvent.ACTION_DRAG_LOCATION: // 被拖拽View在目标区域移动
                Log.i("拖拽事件", "被拖拽View在目标区域移动___X：" + event.getX() + "___Y：" + event.getY());
                tvContent.setText("X：" + event.getX() + "   Y：" + event.getY());
                return true;

            case DragEvent.ACTION_DRAG_EXITED: // 被拖拽View离开目标区域
                Log.i("拖拽事件", "被拖拽View离开目标区域");
                return true;

            case DragEvent.ACTION_DROP: // 放开被拖拽View
                Log.i("拖拽事件", "放开被拖拽View");
                // 释放拖放阴影，并获取移动数据
                ClipData.Item item = event.getClipData().getItemAt(0);
                String content = item.getText().toString();
                Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
                return true;

            case DragEvent.ACTION_DRAG_ENDED: // 拖拽完成
                Log.i("拖拽事件", "拖拽完成");
                return true;

            default:
                break;
        }


        return false;
    }
}
