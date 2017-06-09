package com.xycode.xylibrary;

import android.content.Context;

import com.xycode.xylibrary.utils.LogUtil.L;
import com.xycode.xylibrary.utils.ShareStorage;

/**
 * Created by XY on 2017-06-08.
 * 使用xyLibrary时先在应用中init()
 */

public class Xy {
    private static Context xyContext;
    private static boolean isRelease;
    private static final String XY_PUBLIC_SP = "XY_PUBLIC_SP";
    private static ShareStorage storage;

    public static void init(Context appContextForAllUtils, boolean isRelease) {
        xyContext = appContextForAllUtils;
        Xy.isRelease = isRelease;

        L.setShowLog(!isRelease && getStorage().getBoolean(L.SHOW_LOG, false));
    }

    public static Context getContext() {
        return xyContext;
    }

    public static boolean isRelease() {
        return isRelease;
    }

    /**
     * ShareStorage
     * Xy类公共持久化类
     * @return
     */
    public static ShareStorage getStorage() {
        if (storage == null) {
            storage = new ShareStorage(XY_PUBLIC_SP);
        }
        return storage;
    }
}
