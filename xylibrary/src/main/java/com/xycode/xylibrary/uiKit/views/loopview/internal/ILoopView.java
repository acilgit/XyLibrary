package com.xycode.xylibrary.uiKit.views.loopview.internal;

import java.util.List;

/**
 *
 * ILoopView
 *
 */
public interface ILoopView {

    /**
     * scroll speed
     * @param duration ms
     */
    void setScrollDuration(long duration);

    /**
     * Set the custom layout to be inflated for the loop views.
     *
     * @param layoutResId Layout id to be inflated
     */
    void setLoopLayout(int layoutResId);

    /**
     * 设置页面切换时间间隔
     */
    void setInterval(long interval);

    /**
     * @param rotateData
     */
    void initData(List<String> rotateData);

    /**
     * @param loopData
     */
    void refreshData(List<String> loopData);

    /**
     * @return
     */
    List<String> getLoopData();

    /**
     */
    void startAutoLoop();

    /**
     *
     * @param delayTimeInMills
     */
    void startAutoLoop(long delayTimeInMills);

    /**
     */
    void stopAutoLoop();

}