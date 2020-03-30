package com.test.baserefreshview.test.weiget;

import android.content.Context;
import android.graphics.Point;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test.baserefreshview.test.bean.TempBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 可拖拽的根布局，作用所有此布局内的子View
 * 模拟场景自定义摆放， 定义一个摆放页面，
 * <p>
 * 分几种场景:1.需要在Android端设置餐桌的位置, 用viewDragHelper, 提供"+"给用户添加餐桌.然后记录餐桌的坐标.
 * <p>
 * 2.Android端显示: 根据坐标数组和台号, new TextView,创建好控件, 然后设置LayoutParameter的x,y, width, height 就可以了
 */
public class DragLayout extends FrameLayout {

    private static final String TAG = "DragLayout";

    private ViewDragHelper dragHelper;

    private TextView mDragView1;

    private View mDragView2;

    private View mDragView3;

    private Point point = new Point();
    int offsetX, offsetY;

    View currentChangedView;

    private String currentChangeTableNum;

    /**
     * 标记记录 桌台的坐标
     */
    Map<String, TempBean> locationMapList = new HashMap<>();
    private int lastX, lastY;

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        dragHelper = ViewDragHelper.create(this, callback);

        // 设置边界触摸回调被激活
        dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT | ViewDragHelper.EDGE_TOP | ViewDragHelper.EDGE_RIGHT | ViewDragHelper.EDGE_BOTTOM);
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         * 指定View是否可拖拽
         * @param child 用户捕获的view
         * @param pointerId
         * @return 捕获的view是否可拖动
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;//child == mDragView1 || child == mDragView3
        }

        /**
         * @param state 拖拽的状态
         *
         * @see # STATE_IDLE 拖拽结束
         * @see # STATE_DRAGGING 正在拖拽
         * @see # STATE_SETTLING 正在被放置，这个状态不会出现
         */
        @Override
        public void onViewDragStateChanged(int state) {
            if (state == ViewDragHelper.STATE_DRAGGING) {
                mDragView1.setText("正在拖拽");
            } else if (state == ViewDragHelper.STATE_IDLE) {
                mDragView1.setText("拖拽结束");

                Log.d(TAG, " View 拖拽结束 --------X:" + point.x + " Y:" + point.y);


                // TODO: 2019-08-26 sava 绝对坐标防止 添加新View时 全部归位  // 2 需去重复
                if (currentChangedView != null) {
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(currentChangedView.getLayoutParams());
                    layoutParams.leftMargin = point.x;
                    layoutParams.topMargin = point.y;
                    currentChangedView.setLayoutParams(layoutParams);
                }

                Point currentPoint = new Point();

                currentPoint.x = point.x;
                currentPoint.y = point.y;

                if (currentChangedView instanceof TextView) {
                    currentChangeTableNum = ((TextView) currentChangedView).getText().toString();
                }

                TempBean newTemp = new TempBean(currentPoint, currentChangeTableNum);

                locationMapList.put(currentChangeTableNum, newTemp);

            }
        }

        /**
         * 当View的位置发生改变是回调
         * @param changedView 当前发生位置改变的View
         * @param left view左上角新X坐标
         * @param top view左上角新Y坐标
         * @param dx 俩次回调X轴方向移动的距离
         * @param dy 俩次回调Y轴方向移动的距离
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            Log.d(TAG, "onViewPositionChanged left" + left);
            Log.d(TAG, "onViewPositionChanged top" + top);
            Log.d(TAG, "onViewPositionChanged dx" + dx);
            Log.d(TAG, "onViewPositionChanged dy" + dy);
            Log.d(TAG, "----------------------");

            currentChangedView = changedView;

            point.x = left;
            point.y = top;


        }

        /**
         * 当view被捕获时回调
         *
         * @param capturedChild 被捕获的view
         * @param activePointerId
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {

        }

        /**
         * 手指释放时候回调该方法
         * @param releasedChild 被释放的子view
         * @param xvel 手指的X轴速度，以像素每秒钟离开屏幕
         * @param yvel 手指的Y轴速度，以像素每秒钟离开屏幕
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            // 向下，向右为正，否则为负
            if (yvel > 0) {
                if (releasedChild == mDragView3) {
                    smoothToBottom(releasedChild);
                }
                Toast.makeText(getContext(), "释放时，速度向下大于0", Toast.LENGTH_SHORT).show();
            } else if (yvel < 0) {
                if (releasedChild == mDragView3) {
                    smoothToTop(releasedChild);
                }
                Toast.makeText(getContext(), "释放时，速度向上大于0", Toast.LENGTH_SHORT).show();
            } else {
            }

        }

        /**
         * 当触摸到父布局边界时回调（必须调用ViewDragHelper.setEdgeTrackingEnabled才会生效）
         *
         * @param edgeFlags
         * @param pointerId
         */
        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            if (edgeFlags == ViewDragHelper.EDGE_TOP) {
                Toast.makeText(getContext(), "到上边界了", Toast.LENGTH_SHORT).show();
            } else if (edgeFlags == ViewDragHelper.EDGE_BOTTOM) {
                Toast.makeText(getContext(), "到下边界了", Toast.LENGTH_SHORT).show();
            } else if (edgeFlags == ViewDragHelper.EDGE_LEFT) {
                Toast.makeText(getContext(), "到左边界了", Toast.LENGTH_SHORT).show();
            } else if (edgeFlags == ViewDragHelper.EDGE_RIGHT) {
                Toast.makeText(getContext(), "到右边界了", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public boolean onEdgeLock(int edgeFlags) {
            return false;
        }

        /**
         * 当没有捕获到子view时，通过触摸边界拖拽子view（场景：当view在屏幕外，无法捕获view，只能通过触摸屏幕边界先让他滚回来）
         * @param edgeFlags
         * @param pointerId
         */
        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
//            dragHelper.captureChildView(mDragView2, pointerId);
//            dragHelper.captureChildView(mDragView3, pointerId);
        }

        /**
         * 返回捕获的view Z轴的坐标（即当前view在布局的第几层）
         * @param index
         * @return
         */
        @Override
        public int getOrderedChildIndex(int index) {
            Log.d(TAG, "getOrderedChildIndex index" + index);
            return index;
        }

        /**
         * 该view水平方向拖动的范围(子控件消耗点击事件时候才回调（例如：按钮）)
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        /**
         * 该view垂直方向拖动的范围(子控件消耗点击事件时候才回调（例如：按钮）)
         * @param child
         * @return
         */
        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }

        /**
         * 垂直方向限制View的拖动范围
         * @param child 被拖动的子View
         * @param top 移动过程中Y轴的坐标
         * @param dy
         * @return view的新位置
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            // 限定view上下边界（以view左上角坐标点为准）
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - child.getHeight();
            final int newTop = Math.min(Math.max(top, topBound), bottomBound);
            return newTop;
        }

        /**
         * 水平方向限制View的拖动范围
         * @param child 被拖动的子View
         * @param left 移动过程中X轴的坐标
         * @param dx
         * @return view的新位置
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return dragHelper.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
/*
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                ((TextView) currentChangedView ).setSelected(true);

                break;
            case MotionEvent.ACTION_UP:

                break;
                default:
        }*/
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDragView1 = (TextView) getChildAt(0);
        mDragView2 = getChildAt(1);
        mDragView3 = getChildAt(2);
    }

    public void smoothToTop(View view) {
        // 动画平滑的移动到指定位置
        if (dragHelper.smoothSlideViewTo(view, getPaddingLeft(), getPaddingTop())) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void smoothToBottom(View view) {
        // 动画平滑的移动到指定位置
        if (dragHelper.smoothSlideViewTo(view, getPaddingLeft(), getHeight() - view.getHeight())) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }


    /**
     * @return 获取所有坐标集合
     */
    public ArrayList<TempBean> getAllLocationViewList() {
        ArrayList<TempBean> datas = new ArrayList<>(locationMapList.values());
        return datas;
    }


    public void scrollLeft() {

        if (currentChangedView != null) {
            currentChangedView.offsetLeftAndRight(-5);
        }

    }

    public void scrollRight() {

        if (currentChangedView != null) {
            currentChangedView.offsetLeftAndRight(5);
        }

    }

    public void scrollTop() {

        if (currentChangedView != null) {

            currentChangedView.offsetTopAndBottom(-5);
        }

    }

    public void scrollBottom() {

        if (currentChangedView != null) {

            currentChangedView.offsetTopAndBottom(5);
        }

    }


    public View getCurrentChangedView() {
        return currentChangedView;
    }
}