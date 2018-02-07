package com.test.baserefreshview.test.Utils;

import android.content.ClipData;
import android.content.ClipDescription;
import android.text.TextUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Toast;

import com.xycode.xylibrary.utils.TS;

/**
 * @author thisfeng
 * @date 2018/2/6-下午6:04
 */

public class DragUtils {

    //用这一接口方便 两个view之间所在的调用
    public interface DragStatus {
        void complete(String mLocalState, String data);
    }

    public static void bindDragInZone(View view, final DragStatus impl) {
        //存储坐标
        final int[] dragPoint = new int[2];
        //是否拖进区域
        final boolean[] isIn = {false};
        //可通过getLocalState() 取出数据
        final String[] localState = {""};
        view.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                final int action = event.getAction();

                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED://拖拽开始
                        localState[0] = (String) event.getLocalState();
                        Log.i("rex", "localState-----" + localState[0]);
                        Log.i("拖拽事件", "拖拽开始");
                        if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
//                            shadowView.setBackgroundResource(R.drawable.xml_rect_line_bg);
                            return true;
                        }

                        return false;
                    case DragEvent.ACTION_DRAG_ENTERED://拖拽进入目标区域
                        Log.i("拖拽事件", "被拖拽View进入目标区域");
                        TS.show("拖拽进入目标区域");
                        isIn[0] = true;


                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION://拖拽位置
                        Log.i("拖拽事件", "被拖拽View在目标区域移动___X：" + event.getX() + "___Y：" + event.getY());

                        return true;

                    case DragEvent.ACTION_DRAG_EXITED://拖拽到目标区域外
                        Log.i("拖拽事件", "被拖拽View离开目标区域");

                        isIn[0] = false;
                        TS.show("拖拽到目标区域外");

                        return true;
                    case DragEvent.ACTION_DROP: //拖拽完成之后松开手指
                        Log.i("拖拽事件", "放开被拖拽View");
                        TS.show("拖拽完成之后松开手指" + event.getX() + "----" + event.getY());

                        dragPoint[0] = (int) event.getX();
                        dragPoint[1] = (int) event.getY();

                        if (isIn[0] && !TextUtils.isEmpty(localState[0]) && impl != null) {

                            // 释放拖放阴影，并获取剪贴板中数据，Tips：只能在DragEvent.ACTION_DROP做此操作获取
                            ClipData.Item item1 = event.getClipData().getItemAt(0);
                            String content1 = item1.getText().toString();

                            impl.complete(localState[0], content1);
                        }

                        return true;
                    case DragEvent.ACTION_DRAG_ENDED://拖拽完成
                        Log.i("拖拽事件", "拖拽完成");
                        if (isIn[0] && !TextUtils.isEmpty(localState[0]) && impl != null) {
                            // TODO: 2018/2/7 这里不能去通过 event.getClipData() ，只能回调 localState[0]的值

                        }
                        isIn[0] = false;

                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
    }
}
