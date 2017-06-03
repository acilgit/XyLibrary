package com.xycode.xylibrary.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.xycode.xylibrary.R;
import com.xycode.xylibrary.unit.MsgEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Log
 *
 * @author way
 */
public class L {
    public static String EVENT_LOG = "EVENT_LOG";

    private static boolean isDebug = true;
    private static String TAG = " Debug ";
    private static boolean isLong = true;
    private static File outputFile = null;


    private static String LOG_DIR;
    private static final String LOG_NAME = "CrashLog.txt";

    // Log的输出List
    private static List<LogItem> logList;

    public static List<LogItem> getLogList() {
        if(logList == null) {
            logList = new ArrayList<>();
        }
        return logList;
    }

    public static void addLogItem(String msg) {
        getLogList().add(new LogItem(DateUtils.formatDateTime("yyyy-M-d HH:mm:ss:SSS",DateUtils.getNow()), msg));
        EventBus.getDefault().post(new MsgEvent(EVENT_LOG, null, null));
    }

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
        if (isDebug()) {
            addLogItem(msg);
            Log.i(TAG, msg);
        }
    }

    public static void i(int msg) {
        if (isDebug()) {
            addLogItem(msg+"");
            Log.i(TAG, msg + "");
        }
    }

    public static void d(String msg) {
        if (isDebug()) {
            addLogItem(msg);
            Log.d(TAG, msg);
        }
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void v(String msg) {
        if (isDebug()) {
            addLogItem(msg);
            Log.v(TAG, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isDebug()) {
            addLogItem(msg);
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug()) {
            addLogItem(msg);
            Log.i(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug())
            if (isLong) {
                eLong(tag, msg);
            } else {
                addLogItem(msg);
                Log.i(tag, msg);
            }
    }

    public static void v(String tag, String msg) {
        if (isDebug()) {
            Log.i(tag, msg);
            addLogItem(msg);
        }
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
            addLogItem(longString);
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

    private static ShareStorage storage;
    private static final String crashSP = "crashSP";
    private static final String CRASH_LOG = "crashLog";
    private static final String CRASH_TIME = "crashTime";

    private static ShareStorage getStorage(Context context) {
        if (storage == null) {
            storage = new ShareStorage(context, crashSP);
        }
        return storage;
    }

    public static void setCrashLog(Context context) {
        if (!getStorage(context).getString(CRASH_TIME).isEmpty()) {
            getLogList().add(new LogItem(getStorage(context).getString(CRASH_TIME), getStorage(context).getString(CRASH_LOG), 1));
        }
        Thread.setDefaultUncaughtExceptionHandler((t, ex) -> {
            ByteArrayOutputStream baos = null;
            PrintStream printStream = null;
            try {
                String info = null;
                baos = new ByteArrayOutputStream();
                printStream = new PrintStream(baos);
                ex.printStackTrace(printStream);
                byte[] data = baos.toByteArray();
                info = new String(data);
                data = null;
                getStorage(context).put(CRASH_LOG, info);
                getStorage(context).put(CRASH_TIME, DateUtils.formatDateTime("yyyy-M-d HH:mm:ss:SSS",DateUtils.getNow()));
                getLogList().add(new LogItem(getStorage(context).getString(CRASH_TIME), getStorage(context).getString(CRASH_LOG), 1));
                EventBus.getDefault().post(new MsgEvent(EVENT_LOG, null, null));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                throw ex;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            // 杀死该应用进程
            try {
                Thread.sleep(300);
                android.os.Process.killProcess(android.os.Process.myPid());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private static void writeLog(Context context, Throwable ex) {
        LOG_DIR = Tools.getCacheDir(context) + "/log/";
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


    /**
     * layout: item_log.xml
     */

    public static enum LogType{
        Error(0, android.R.color.holo_red_light),
        Info(1, R.color.white),
        Debug(2, android.R.color.holo_blue_light);

        LogType(int i, int color) {

        }
    }
    public static class LogItem {
        private String dateTime;
        private String content;
        private int type = 0;

        public LogItem(String dateTime, String content) {
            this.dateTime = dateTime;
            this.content = content;
        }

        public LogItem(String dateTime, String content, int type) {
            this.dateTime = dateTime;
            this.content = content;
            this.type = type;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }


}
