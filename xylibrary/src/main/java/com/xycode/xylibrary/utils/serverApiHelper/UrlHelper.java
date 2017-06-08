package com.xycode.xylibrary.utils.serverApiHelper;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.xycode.xylibrary.utils.ShareStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XY on 2017-06-07.
 * invoke Xy.init() first init Application
 */

public  class UrlHelper {

    private static ShareStorage storage;
    private static List<String> serverList = new ArrayList<>();
    private static final String server= "server";
    private static final String serverSP = "serverSP";
//    private static final String SERVER_LIST = "serverList";

    private static ShareStorage getUrlStorage() {
        if (storage == null) {
            storage = new ShareStorage(serverSP);
        }
        return storage;
    }

    public static String getServer() {
        if (!TextUtils.isEmpty(getUrlStorage().getString(server))) {
            return getUrlStorage().getString(server);
        }
        return serverList.isEmpty() ? "" :serverList.get(0);
    }

}
