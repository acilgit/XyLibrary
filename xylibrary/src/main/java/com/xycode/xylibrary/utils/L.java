package com.xycode.xylibrary.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Log统一管理类
 * 
 * @author way
 * 
 */
public class L {
//	public static boolean isDebug = App.isDebug;// 是否需要输出Log，可以在application的onCreate函数里面初始化
	private static final String TAG = " Debug "; // App.TAG;

    public static boolean isDebug(){
        return true;
    }

    public static String getTag(){
        return TAG;
    };

    // 下面四个是默认tag的函数
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

	// 下面是传入自定义tag的函数
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


	private static final String LOG_PATH = Environment.getExternalStorageDirectory()+"/MiQin/";
	private static final String LOG_FILE ="logcat.txt";
	/**
	 * 写入Exception到logcat文件
	 * @param e  Catch的错误
	 */
	public static void c(Exception e) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < e.getStackTrace().length; i++) {
			StackTraceElement s = e.getStackTrace()[i];
			stringBuffer.append("\n").append(s.toString());
		}
		SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
		final String time = format.format(new Date(System.currentTimeMillis()));
		final String err = time + " ------>\n" + e.getMessage() + "\n--------" + stringBuffer.toString() + "\n";
		//生成文件夹之后，再生成文件，不然会出错
		File dir, file;
		try {
			dir = new File(LOG_PATH);
			if (!dir.exists()) {
				dir.mkdir();
			}
			file = new File(LOG_PATH + LOG_FILE);
			if (!file.exists()) {
				file.createNewFile();
			}
			RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
			accessFile.seek(0);
			accessFile.write(err.getBytes());
			accessFile.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
