package com.xycode.xylibrary.utils.downloadHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by XY on 2016-08-06.
 */
public class DownloadHelper {

    private static DownloadHelper helper = null;
    private Context context;
    private Handler downloadHandler;
    private DownloadDialog downloadDialog;
    private OnDownloadListener onDownloadListener;

    public static String updateButtonName = "update";
    public static String cancelButtonName = null;
    public static String downloadTitle;
    public static String downloadFileUrl = "";
    public static String tempDownloadFileName = "tempDownloadFileName.apk";

    public static void init(Context context, String updateButtonName, String cancelButtonName, String downloadTitle,
                            @NonNull OnDownloadListener onDownloadListener) {
        if (helper != null) return;
        helper = new DownloadHelper(context, onDownloadListener);
        DownloadHelper.updateButtonName = updateButtonName;
        DownloadHelper.cancelButtonName = cancelButtonName;
        DownloadHelper.downloadTitle = downloadTitle;
    }

    public DownloadHelper(Context context, @NonNull OnDownloadListener onDownloadListener) {
        this.context = context;
        this.onDownloadListener = onDownloadListener;
    }

    public static DownloadHelper getInstance() {
        return helper;
    }

    public static String getDownloadTempFileName(Context context) {
        return context.getFilesDir().getAbsolutePath() + tempDownloadFileName;
    }

    public void update(final Activity activity, final String downloadFileUrl, String title, String content ) {
        context = activity;
        DownloadHelper.downloadFileUrl = downloadFileUrl;
        if (downloadHandler == null) {
            downloadHandler = new Handler() {
                int downedFileLength = 0;
                int fileLength = 0;

                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 0:
                            fileLength = msg.arg1;
                            downloadDialog.getDialog().setMax(msg.arg1);
                            break;
                        case 1:
                            downloadDialog.getDialog().setProgress(msg.arg1);
                            int x = msg.arg1 * 100 / fileLength;
                            break;
                        case 2:
                            downloadDialog.getDialog().cancel();
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(getDownloadTempFileName(context))), "application/vnd.android.package-archive");
                            context.startActivity(intent);
                            break;
                        default: // failed
                            downloadDialog.getDialog().cancel();

                            break;
                    }
                }
            };
        }

        AlertDialog.Builder builder = onDownloadListener.getConfirmDialogBuilder(title, content);
        builder.setCancelable(false)
                .setPositiveButton(updateButtonName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (downloadDialog == null)
                            downloadDialog = new DownloadDialog(activity, downloadHandler, downloadFileUrl, downloadTitle);
                        downloadDialog.showDialog();
                    }
                });
        if (cancelButtonName != null) {
            builder.setNegativeButton(cancelButtonName, null);
        }
        builder.create().show();
    }

    public class DownloadDialog {
        private Context context;
        private ProgressDialog dialog;
        private View view;
        private Handler handler;
        int fileLength = 0;
        int downedFileLength = 0;
        private InputStream inputStream;
        private OutputStream outputStream;
        private String downloadFileUrl;

        public DownloadDialog(Context context, Handler handler, String downloadFileUrl, String title) {
            this.context = context;
            this.handler = handler;
            this.downloadFileUrl = downloadFileUrl;

            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setTitle(title);
            String message = null;
            if (message != null && !message.isEmpty()) dialog.setMessage(message);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setProgress(100);
            dialog.setIndeterminate(false);
        }

        public ProgressDialog getDialog() {
            return dialog;
        }

        public void showDialog() {
            dialog.show();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    downFile();
                }
            };
            new Thread(runnable).start();
        }

        private void downFile() {

            String savePathString = getDownloadTempFileName(context);
            File file = new File(savePathString);
            try {
                if (!file.exists()) {
                    file.createNewFile();
                } else {
                    file.delete();
                    file.createNewFile();
                }
            } catch (IOException e) {
            // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Message message = new Message();

            try {
                HttpURLConnection conn = getHttpConnection(downloadFileUrl);
                inputStream = conn.getInputStream();
                if (conn.getResponseCode() != 200) {
                    inputStream.close();
                    throw new IOException("Image request failed with response code " + conn.getResponseCode());
                }
                fileLength = conn.getContentLength();
                outputStream = new FileOutputStream(file);
                message.arg1 = fileLength;
                message.what = 0;
                handler.sendMessage(message);

                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[10240];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                        downedFileLength = downedFileLength + length;
                        Message message1 = new Message();
                        message1.arg1 = downedFileLength;
                        message1.what = 1;
                        handler.sendMessage(message1);
                    }
                    fos.flush();
                    fos.close();
                    inputStream.close();
                    Message message2 = new Message();
                    message2.what = 2;
                    handler.sendMessage(message2);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message3 = new Message();
                    message3.what = 3;
                    handler.sendMessage(message3);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Message message3 = new Message();
                message3.what = 3;
                handler.sendMessage(message3);
            }
        }
    }

    public static HttpURLConnection getHttpConnection(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            return conn;
        } catch (Exception e) {
            return null;
        }
    }

    public interface OnDownloadListener {

        AlertDialog.Builder getConfirmDialogBuilder(String title, String content);

        void onDownloadFailure();
    }


}
