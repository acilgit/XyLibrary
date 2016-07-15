package com.xycode.xylibrary.utils;

import android.util.Log;

/**
 * Log
 * 
 * @author way
 * 
 */
public class L {
	private static boolean isDebug = true;
	private static String TAG = " Debug ";

    public static void setDebugMode(boolean isDebug){
        L.isDebug = isDebug ;
    }

	public static boolean isDebug() {
		return isDebug;
	}

	public static void setTag(String tag){
        TAG = tag;
    }

	public static void i(String msg) {
		if (isDebug())
			Log.i(TAG, msg);
	}

	public static void i(int msg) {
		if (isDebug())
			Log.i(TAG, msg+"");
	}

	public static void d(String msg) {
		if (isDebug())
			Log.d(TAG, msg);
	}

	public static void e(String msg) {
		if (isDebug())
			Log.e(TAG, msg);
	}

	public static void v(String msg) {
		if (isDebug())
			Log.v(TAG, msg);
	}

	public static void i(String tag, String msg) {
		if (isDebug())
			Log.i(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (isDebug())
			Log.i(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (isDebug())
			Log.i(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (isDebug())
			Log.i(tag, msg);
	}
}
