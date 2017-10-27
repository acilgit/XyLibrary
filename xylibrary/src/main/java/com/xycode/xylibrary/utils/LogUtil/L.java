package com.xycode.xylibrary.utils.LogUtil;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xycode.xylibrary.Xy;
import com.xycode.xylibrary.unit.MsgEvent;
import com.xycode.xylibrary.utils.DateUtils;
import com.xycode.xylibrary.utils.Tools;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Log
 * showLog模式下，才会输出到logcat
 * 任何模式下Log都会输出到logList
 * 可把logList在CrashActivity发送到服务器
 */
public class L {
    public static final String SHOW_LOG = "SHOW_LOG_FOR_XY";
    public static final String EVENT_LOG = "EVENT_LOG";
    /**
     * -1时不限数量
     */
    private static int MAX_LOG_LIST_SIZE_IN_RELEASE_MODE = 30;

    private static boolean showLog = true;
    private static String TAG = " Debug ";
    private static boolean isLong = true;
    private static File outputFile = null;

    private static String LOG_DIR;
    private static final String LOG_NAME = "CrashLog.txt";

    // Log的输出List
    private static List<LogItem> logList;

    public static List<LogItem> getLogList() {
        if (logList == null) {
            logList = new ArrayList<>();
        }
        return logList;
    }

    public static void setLogList(List<LogItem> logList) {
        L.logList = logList;
    }

    public static void addLogItem(String msg) {
        addLogItem(null, msg, LogItem.LOG_TYPE_E);
    }

    public static void addLogItem(String title, String msg) {
        addLogItem(title, msg, LogItem.LOG_TYPE_E);
    }

    public static void addLogItem(String title, String msg, int type) {
        String addTitle = "", addMsg = "";
        if(title != null) addTitle = new String(title);
        if(msg != null) addMsg = new String(msg);
        getLogList().add(new LogItem(DateUtils.formatDateTime("yyyy-M-d HH:mm:ss:SSS", DateUtils.getNow()), addTitle, addMsg, type));
        if (!showLog() && MAX_LOG_LIST_SIZE_IN_RELEASE_MODE != -1 && getLogList().size() > MAX_LOG_LIST_SIZE_IN_RELEASE_MODE) {
            getLogList().remove(0);
        }
        EventBus.getDefault().post(new MsgEvent(EVENT_LOG, null, null));
    }

    public static void setShowLog(boolean showLog) {
        L.showLog = showLog;
        Xy.getStorage(Xy.getContext()).getEditor().putBoolean(SHOW_LOG, showLog).commit();
    }

    public static boolean showLog() {
        return showLog;
    }

    public static void setShowLongErrorMode(boolean isLong) {
        L.isLong = isLong;
    }

    public static void setTag(String tag) {
        TAG = tag;
    }

    public static void i(String msg) {
        if (showLog()) {
            Log.i(TAG, msg);
        }
        addLogItem(null, msg, LogItem.LOG_TYPE_I);
    }

    public static void i(int msg) {
        i(msg + "");
    }

    public static void d(String msg) {
        if (showLog()) {
            Log.d(TAG, msg);
        }
        addLogItem(null, msg, LogItem.LOG_TYPE_D);
    }

    public static void v(String msg) {
        if (showLog()) {
            Log.v(TAG, msg);
        }
        addLogItem(null, msg, LogItem.LOG_TYPE_I);
    }

    public static void e(String msg) {
        e(null, msg);
    }


    public static void e(String title, String msg) {
        if (showLog()) {
            if (isLong) {
                eLong(title, msg);
            } else {
                Log.e(TAG, TextUtils.isEmpty(title) ? msg : title + "\n" + msg);
            }
        }
        addLogItem(title, msg);
    }

    private static void eLong(String title, String longString) {
        if (showLog()) {
            int maxLogSize = 1000;
            String content = TextUtils.isEmpty(title) ? longString : title + "\n" + longString;
            if (content.length() > maxLogSize) {
                for (int i = 0; i <= content.length() / maxLogSize; i++) {
                    int start = i * maxLogSize;
                    int end = (i + 1) * maxLogSize;
                    end = end > content.length() ? content.length() : end;
                    Log.e(TAG, content.substring(start, end));
                }
            } else {
                Log.e(TAG, content);
            }
            if (outputFile != null) writeLogToOutputFile(content);
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

    private static void writeLog(Context context, Throwable ex) {
        LOG_DIR = Tools.getCacheDir() + "/log/";
        String info = null;
        ByteArrayOutputStream baos = null;
        PrintStream printStream = null;
        try {
            baos = new ByteArrayOutputStream();
            printStream = new PrintStream(baos);
            ex.printStackTrace(printStream);
            byte[] data = baos.toByteArray();
            info = new String(data);
            data = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (printStream != null) {
                    printStream.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        e("崩溃信息\n" + info);
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, LOG_NAME);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(info.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void setMaxLogListSizeInReleaseMode(int maxLogListSizeInReleaseMode) {
        MAX_LOG_LIST_SIZE_IN_RELEASE_MODE = maxLogListSizeInReleaseMode;
    }

    /**
     * layout: item_log.xml
     */
}
