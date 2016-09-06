package com.xycode.xylibrary.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xycode.xylibrary.interfaces.Interfaces;
import com.xycode.xylibrary.uiKit.recyclerview.HorizontalDividerItemDecoration;
import com.xycode.xylibrary.unit.StringData;
import com.xycode.xylibrary.unit.UrlData;
import com.xycode.xylibrary.unit.WH;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Utils
 * Created by Administrator on 2014/7/27.
 */
public class Tools {

//    public static final String CACHE_DIR = Environment.getExternalStorageDirectory()+"";

    private static File currentPhotoFile;

    private static Tools instance = null;

    private static int ScreenWidth = 0;
    private static Point screenSize = null;

    private static AtomicInteger atomicCounter = new AtomicInteger(0);


    /**
     * no "/" at the end
     *
     * @param context
     * @return
     */
    public static File getCacheDir(Context context) {
        return context.getCacheDir();
    }

    public static File getFileDir(Context context) {
        return context.getFilesDir();
    }

    /**
     * check application can only be invoked once
     *
     * @param context
     * @return
     */
    public static boolean isProcessRunning(Context context) {
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(context, pid);
        if (processAppName == null || !processAppName.equalsIgnoreCase(context.getPackageName())) {
            return false;
        }
        return true;
    }

    private static String getAppName(Context context, int pid) {
        String processName = null;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List list = activityManager.getRunningAppProcesses();
        Iterator i = list.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pid) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static Tools getInstance() {
        if (instance == null) {
            instance = new Tools();
        }
        return instance;
    }


    public static int randomInt(int min, int max) {
        return (int) (Math.random() * max) + min;
    }

    public static boolean isMobileNO(String mobiles) {

        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

        Matcher m = p.matcher(mobiles);

        return m.matches();

    }

    public static boolean isEmail(String email) {

        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";

        Pattern p = Pattern.compile(str);

        Matcher m = p.matcher(email);

        return m.matches();

    }

    public static boolean isNumeric(String str) {

        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);

        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static List<String> getNumbers(String content) {
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+");
//        Pattern pattern = Pattern.compile("[0]");
        Matcher matcher = pattern.matcher(content);
//        if (matcher.find()) {
        /*if (matcher.matches()) {
            L.e("matcher.find count"+"("+ matcher.groupCount()+")");
        for (int i = 0; i < matcher.groupCount(); i++) {
            L.e("matcher.find"+"("+ matcher.group(i)+")");
        }

        }*/
        while (matcher.find()) {
            if (matcher.group().length() >= 4) {
                list.add(matcher.group());
            }
//            L.e("matcher.find" + "(" + matcher.group() + ")");
        }
        return list;
    }

    /**
     * goto see Paint.UNDERLINE_TEXT_FLAG...
     *
     * @param textView
     * @param paintEffect
     */
    public static void textEffect(TextView textView, int paintEffect) {
        textView.getPaint().setFlags(paintEffect); // Paint.UNDERLINE_TEXT_FLAG
        textView.getPaint().setAntiAlias(true);
    }

    /**
     * get file name without ext
     */
    public static String getFileNameNoExt(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public static WH getWidthHeightFromFilename(String filename, String mark, String splitter) {
        int h = 500, w = 500;
        try {
            String name = getFileNameNoExt(filename);
            int pos = name.lastIndexOf(mark);
            if ((pos > -1) && (pos < (name.length()))) {
                String wAndH = name.substring(pos + mark.length(), name.length());
                String[] strings = wAndH.split(splitter);
                if (strings.length == 2) {
                    w = Integer.parseInt(strings[0]);
                    h = Integer.parseInt(strings[1]);
                }
            }
        } catch (Exception e) {
            w = 500;
            h = 500;
        }
        WH wh = new WH(w, h);
        return wh;
    }

    /**
     * copy to Clipboard
     * add by wangqianzhou
     *
     * @param content
     */
    public static void copyToClipboard(String content, Context context) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content);
    }

    public static long getLongID() {
        if (atomicCounter.get() > 9999) {
            atomicCounter.set(1);
        }
        return System.currentTimeMillis() * 10000 + atomicCounter.incrementAndGet();
    }

    public static CharSequence getHtmlDrawableText(final Context context, int drawableId, String text, final int drawableHeightDP) {
        final String html = "<img src='" + drawableId + "'/>&nbsp;" + text;
        CharSequence charSequence = Html.fromHtml(html, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                Drawable drawable = context.getResources().getDrawable(Integer.parseInt(source));
                int dh = Tools.dp2px(context, (float) drawableHeightDP);
                drawable.setBounds(0, 0, dh * drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight(), dh);
                return drawable;
            }
        }, null);
        return charSequence;
    }

    public static CharSequence getHtmlDrawableText(final Context context, int drawableId, String text, int textColerRes, final int drawableHeightDP) {

        StringBuffer tc = new StringBuffer("#" + Integer.toHexString(textColerRes));
        if (tc.length() == 9) tc.delete(1, 2);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<font color='");
        stringBuffer.append(tc);
        stringBuffer.append("'>");
        stringBuffer.append(text);
        stringBuffer.append("</font>");
        final String html = "<img src='" + drawableId + "'/>" + stringBuffer.toString(); //&nbsp;
        CharSequence charSequence = Html.fromHtml(html, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                Drawable drawable = context.getResources().getDrawable(Integer.parseInt(source));
                int dh = Tools.dp2px(context, (float) drawableHeightDP);
                drawable.setBounds(0, 0, dh * drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight(), dh);
                return drawable;
            }
        }, null);
        return charSequence;
    }

    public static String returnString(StringBuffer stringBuffer) {
        if (stringBuffer == null) {
            stringBuffer = new StringBuffer();
        }
        return stringBuffer.toString();
    }

    public static Uri getFileUriFromUri(Activity activity, Uri contentUri) {
        if (contentUri.getScheme().equals("content")) {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
            int actual_image_column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String img_path = cursor.getString(actual_image_column_index);
            if (img_path == null) {
                return contentUri;
            }
            File file = new File(img_path);
            return Uri.parse("file://" + file.getPath());
        }
        return contentUri;
    }

    public static Uri getContentUriFromActivityResult(Activity activity, Intent data) {
        Uri uri = data.getData();
        String type = data.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = activity.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.ImageColumns._ID},
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri.parse("content://media/external/imageBeen/media/" + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }

    @SuppressLint("NewApi")
    public static Uri getFilePathByUri(final Context context, final Uri uri) {
        String uriPath = null;
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    uriPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                uriPath = getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                uriPath = getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore
            // (and
            // general)
            uriPath = getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            uriPath = uri.getPath();
        }
        if (uriPath == null || uriPath.isEmpty()) {
            return null;
        } else {
            if (!uriPath.contains("file:")) {
                uriPath = "file://" + uriPath;
            }
        }
        return Uri.parse(uriPath);
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String bytesToBase64(byte[] bytes) {
        String result = null;
        result = Base64.encodeToString(bytes, Base64.DEFAULT);
        return result;
    }


    public static class MD5 {
        public static String getMD5(String value) throws NoSuchAlgorithmException {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(value.getBytes());
            byte[] m = md5.digest();  // 加密
            return getString(m);
        }

        private static String getString(byte[] b) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                sb.append(b[i]);
            }
            return sb.toString();
        }

        /**
         * translate to MD5
         *
         * @param string
         * @return string
         */
        public static String stringToMD5(String string) {
            byte[] hash;
            try {
                hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10)
                    hex.append("0");
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        }
    }

    /**
     * judge network
     *
     * @return true，false
     */
    public static boolean isAvailableNetWork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo network : info) {
                    if (network.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * weather App is on Foreground
     *
     * @return
     */
    public static boolean isAppOnForeground(Context context) {
        // Returns a list of application processes that are running on the device
        List<ActivityManager.RunningAppProcessInfo> appProcesses = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
        if (appProcesses == null) return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.

            if (appProcess.processName.equals(context.getPackageName()) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    public static String getTopActivityClassName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(context.getPackageName()) && info.baseActivity.getPackageName().equals(context.getPackageName())) {
                return info.topActivity.getClassName();
//                L.e("baseActivity.getPackageName()"+"("+info.baseActivity.getPackageName()+")"+"baseActivity.getClassName()"+"("+info.baseActivity.getClassName()+")");
//                break;
            }
        }
//        L.e("isAppRunning"+"("+isAppRunning+")");
        return "";
    }

    public static List<String> getActivityClassNames(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        List<String> stringList = new ArrayList<>();
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.baseActivity.getPackageName().equals(context.getPackageName())) {
                stringList.add(info.baseActivity.getClassName());
            }
        }
        return stringList;
    }


    public static File checkFile(String path, String aFileNameWithAnyPath) {
        final String fileName = new File(aFileNameWithAnyPath).getName();
        File file = new File(path, fileName);
        return file;
    }

    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldFile = new File(oldPath);
            if (oldFile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("copy single file error");
            e.printStackTrace();
        }
    }

    public static File saveFileFromInputStream(InputStream inputStream, File file) {

        File dir = new File(file.getParent());
        if (!dir.exists())
            dir.mkdirs();
//        if (file.exists()) {
//            return file;
//        }
        try {
            FileOutputStream fos = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
            fos.close();
            inputStream.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * dp to px(pixel)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px(pixel) to dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * scroll to the view in a scrollview
     *
     * @param scrollView
     * @param view
     */
    public static void scrollToView(final ScrollView scrollView, final View view) {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                int offset = location[1] - scrollView.getMeasuredHeight();
                scrollView.smoothScrollTo(0, offset);
            }
        });
    }

    public static int[] getViewLocation(View v) {
        int[] loc = new int[4];
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        loc[0] = location[0];
        loc[1] = location[1];
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);

        loc[2] = v.getMeasuredWidth();
        loc[3] = v.getMeasuredHeight();

        return loc;
    }

    public static Point getScreenSize(Context context) {
        if (screenSize == null) {
            screenSize = new Point();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(screenSize);
        }
        return screenSize;
    }


    public static void dialNumber(Context context, String phoneNo) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNo));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static class Cal {
        public static int getPosInArray(int[] arr, int value) {
            for (int i : arr) {
                if (i == value) {
                    return i;
                }
            }
            return -1;
        }
    }

    public static HorizontalDividerItemDecoration getHorizontlDivider(Context context, @ColorRes int dividerColor, @DimenRes int dividerHeight, @DimenRes int marginLeft, @DimenRes int marginRight) {
        HorizontalDividerItemDecoration.Builder builder = new HorizontalDividerItemDecoration.Builder(context)
                .colorResId(dividerColor).sizeResId(dividerHeight)
                .marginResId(marginLeft, marginRight);
        return builder.build();
    }

    public static class Permission {

        public static boolean isGrantExternalRW(Activity activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 1);
                return false;
            }

            return true;
        }
    }

    public static List<StringData> getStringDataList(List list, Interfaces.OnStringData onStringData) {
        ArrayList<StringData> dataList = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                dataList.add(new StringData(onStringData.getDataString(list.get(i)), list.get(i)));
            }
        }
        return dataList;
    }

    public static List<UrlData> getUrlDataList(List list, Interfaces.OnStringData onStringData) {
        ArrayList<UrlData> dataList = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                dataList.add(new UrlData(onStringData.getDataString(list.get(i)), list.get(i)));
            }
        }
        return dataList;
    }


}
