package com.xycode.xylibrary.utils;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;

/**
 * Log
 *
 * @author way
 */
public class L {
    private static boolean isDebug = true;
    private static String TAG = " Debug ";
    private static boolean isLong = true;
    private static File outputFile = null;

    public static void setDebugMode(boolean isDebug) {
        L.isDebug = isDebug;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static void setShowLongErrorMode(boolean isLong) {
        L.isLong = isLong;
    }

    public static void setTag(String tag) {
        TAG = tag;
    }

    public static void i(String msg) {
        if (isDebug())
            Log.i(TAG, msg);
    }

    public static void i(int msg) {
        if (isDebug())
            Log.i(TAG, msg + "");
    }

    public static void d(String msg) {
        if (isDebug())
            Log.d(TAG, msg);
    }

    public static void e(String msg) {
        e(TAG, msg);
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
            if (isLong) {
                eLong(tag, msg);
            } else {
                Log.i(tag, msg);
            }
    }

    public static void v(String tag, String msg) {
        if (isDebug())
            Log.i(tag, msg);
    }

    private static void eLong(String tag, String longString) {
        if (isDebug()) {
            int maxLogSize = 1000;
            if (longString.length() > maxLogSize) {
                for (int i = 0; i <= longString.length() / maxLogSize; i++) {
                    int start = i * maxLogSize;
                    int end = (i + 1) * maxLogSize;
                    end = end > longString.length() ? longString.length() : end;
                    Log.e(tag, longString.substring(start, end));
                }
            } else {
                Log.e(tag, longString);
            }
            if(outputFile!=null) writeLogToOutputFile(longString);
        }
    }

    public static void setLogOutputFile(File file) {
        String fileName = file.getName();
    }

    private static void writeLogToOutputFile(String content) {
        if (!outputFile.exists()) {
            return;
        }
        try {
            RandomAccessFile accessFile = new RandomAccessFile(outputFile, "rw");
            String s = DateUtils.formatDateTime("yyyy-MM-dd HH:mm:ss:zzz ->\n", System.currentTimeMillis()) + content + "\r\n\r\n";
            accessFile.seek(0);
            accessFile.writeChars(s);
            accessFile.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        /*try {
            OutputStream outputStream = new FileOutputStream(outputFile);
            OutputStreamWriter out = new OutputStreamWriter(outputStream);
            out.write(DateUtils.formatDateTime("yyyy-MM-dd HH:mm:ss:zzz ->\n", System.currentTimeMillis()) + content + "\r\n\r\n");
            out.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }*/
    }


}
