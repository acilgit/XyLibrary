package com.test.baserefreshview.test.weiget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * @author：thisfeng
 * @time 2019-08-26 17:32
 * 在HorizontalScrollView中进行双指滑动HorizontalScrollView跟随着手势滑动，单指滑动的事件自己不处理，留给其子view进行手势的处理
 */
public class MyHorizontalScrollView extends HorizontalScrollView {
    private boolean isDragCanvsMode;
    private boolean isDoubleDrag = false;
    private float mStartDownX;
    private float mStartDownY;
    private float mMoveDragCanvsX;
    private float mMoveDragCanvsY;

    public MyHorizontalScrollView(Context context) {
        super(context);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //多点拖动的操作
        switch (event.getAction()&MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:{
//                Log.i("everb","按下去："+mStartDownX);
            }
            break;
            case MotionEvent.ACTION_POINTER_DOWN:{
                isDoubleDrag = true;
            }
            break;
            case MotionEvent.ACTION_MOVE:{
//                Log.i("everb", "canvas scrollview onTouchEvent：" + event.getX() + " " + event.getY() + " event.getAction(): " + event.getAction()+ " isDoubleDrag:"+isDoubleDrag);
                mMoveDragCanvsX = event.getX();
                Log.i("everb","move的值："+mMoveDragCanvsX);
                float canvsMoveX = mMoveDragCanvsX - mStartDownX;
                Log.i("everb","移动的值："+canvsMoveX);
                //双指滑动的距离进行双指滑动HorizontalScrollView也进行随手势滑动
                this.scrollBy(-(int)canvsMoveX,0);
            }
            break;
            case MotionEvent.ACTION_CANCEL:{
                isDoubleDrag = false;
//                Log.i("everb", "canvas scrollview onTouchEvent：ACTION_CANCEL");
            }
            break;
        }
        mStartDownX=mMoveDragCanvsX;
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (event.getAction()&MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:{
                mStartDownX = event.getX();
                isDragCanvsMode=false;
            }
            break;
            case MotionEvent.ACTION_POINTER_DOWN:{
                //多指按下的识别
                isDragCanvsMode=true;
            }
            break;
        }
//        Log.i("everb","canvas scrollview onInterceptTouchEvent："+event.getX()+" "+event.getY() + " event.getAction()"+event.getAction());

        if(isDragCanvsMode == false){
            //如单指按下则事件不拦截，把事件传递到子view中
            return false;
        }
//        Log.i("everb","canvas scrollview onInterceptTouchEvent："+event.getX()+" "+event.getY() + " event.getAction()"+event.getAction());
        //如是双指按下则返回true，把事件拦截在自己的onTouchEvent进行处理
        return true;
    }
}
