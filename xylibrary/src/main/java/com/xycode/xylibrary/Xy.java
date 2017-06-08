package com.xycode.xylibrary;

import android.content.Context;

/**
 * Created by XY on 2017-06-08.
 * 使用xyLibrary时先init()把应用
 */

public class Xy {
    private static Context xyContext;

    public static void init(Context appContextForAllUtils) {
        xyContext = appContextForAllUtils;
    }

    public static Context getContext() {
        return xyContext;
    }
}
