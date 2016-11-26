package com.test.baserefreshview;


import android.content.Context;
import android.support.annotation.LayoutRes;

import java.util.List;

/**
 * Created by XY on 2016-11-01.
 */

public abstract class XAdapter<T> extends com.xycode.xylibrary.adapter.XAdapter<T> {

    /**
     * use single Layout
     *
     * @param context
     * @param dataList
     */
    public XAdapter(Context context, List<T> dataList) {
        super(context, dataList);
    }

}
