package com.xycode.xylibrary.uiKit.views.loopview.internal;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 *
 * LoopHandler
 *
 */
public class LoopHandler extends Handler {

    private final WeakReference<Activity> activity;
    private final WeakReference<BaseLoopView> loopView;

    public LoopHandler(BaseLoopView loopView, Activity activity) {
        this.loopView = new WeakReference<>(loopView);
        this.activity = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        Activity activity = this.activity.get();
        BaseLoopView loopView = this.loopView.get();
        if (activity != null && loopView != null) {
            if (!loopView.isAutoScroll()) return;

            switch (msg.what) {
                case 0: //
                    int change = (loopView.getDirection() == BaseLoopView.LEFT) ? -1 : 1;
                    loopView.getViewPager().setCurrentItem(loopView.getViewPager().getCurrentItem() + change, true);
                    loopView.sendScrollMessage(loopView.getInterval());
                    break;
                case 1: //
                    loopView.getViewPager().setCurrentItem(loopView.getViewPager().getCurrentItem() - 1, false);
                    loopView.getViewPager().setCurrentItem(loopView.getViewPager().getCurrentItem() + 1, false);
                    loopView.sendScrollMessage(loopView.getInterval());
                    break;
            }

        } else {
            removeMessages(0);
            removeMessages(1);
        }
    }

}