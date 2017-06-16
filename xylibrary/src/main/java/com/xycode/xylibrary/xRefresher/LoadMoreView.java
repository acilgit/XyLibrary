package com.xycode.xylibrary.xRefresher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.xycode.xylibrary.R;

/**
 * Created by XY on 2016-08-12.
 */
public class LoadMoreView extends RelativeLayout {

    private static int layoutId = R.layout.layout_blank;

    private static final int animating = 0;
    private static final int showing = 1;
    private static final int hiding = 2;
    private static final int hidden = 3;

    private Animation animationShow;
    private Animation animationHide;

    private int state = hidden;

    public LoadMoreView(Context context) {
        super(context, null);
    }

    public LoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(layoutId, this, true);
        setVisibility(GONE);

        animationShow = AnimationUtils.loadAnimation(getContext(), R.animator.move_up);
        animationHide = AnimationUtils.loadAnimation(getContext(), R.animator.move_down);
        animationShow.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (state == hidden) {
//                    clearAnimation();
                    startAnimation(animationHide);
                } else {
                    state = showing;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationHide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                state = hiding;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(GONE);
                state = hidden;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void show() {
        if (state == hidden) {
            state = animating;
            clearAnimation();
            startAnimation(animationShow);
        }
    }

    public void hide() {
        if (state == showing) {
            clearAnimation();
            startAnimation(animationHide);
        } else if(state != hiding){
            state = hidden;
        }
    }

    public static void setLayoutId(int layoutId) {
        LoadMoreView.layoutId = layoutId;
    }
}
