package com.xycode.xylibrary.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.xycode.xylibrary.Xy;

/**
 * Created by Administrator on 2016/3/14 0014.
 * invoke Xy.init() first init Application
 */
public class VersionUtils {
    public static String getVersionName(){
        String versionName = "";
        PackageManager pm = Xy.getContext().getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(Xy.getContext().getPackageName(), 0);
            versionName = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
    public static int getVersionCode(){
        int versionCode = 0 ;
        PackageManager pm = Xy.getContext().getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(Xy.getContext().getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }
}
