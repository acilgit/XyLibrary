package com.xycode.xylibrary.utils.downloadHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

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
    private Activity activity;
    private Handler downloadHandler;
    private DownloadDialog downloadDialog;
    private AlertDialog customerDownloadDialog;
    private OnShowDownloadDialog onShowDownloadDialog;
    private OnProgressListener onProgressListener;

    public static int defaultDownloadFileSize = 1024*1024*20;

    public static String downloadingTitle = "";
    public static String downloadingCancelButtonName = "";
    public static String updateButtonName = "update";
    public static String updateMessage;
    public static String cancelButtonName = null;
    public static String downloadFileUrl = "";
    public static String tempDownloadFileName = "tempDownloadFileName.apk";

    private InputStream inputStream;
    private OutputStream outputStream;

    int fileLength = 0;
    int downedFileLength = 0;

    private boolean cancelDownload;

    public static void init(String updateButtonName, String cancelButtonName, String downloadingTitle, String downloadingCancelButtonName, @NonNull OnShowDownloadDialog onShowDownloadDialog) {
        if (helper != null) return;
        helper = new DownloadHelper(onShowDownloadDialog);
        DownloadHelper.updateButtonName = updateButtonName;
        DownloadHelper.cancelButtonName = cancelButtonName;
        DownloadHelper.downloadingTitle = downloadingTitle;
        DownloadHelper.downloadingCancelButtonName = downloadingCancelButtonName;
    }

    public DownloadHelper(@NonNull OnShowDownloadDialog onShowDownloadDialog) {
        this.onShowDownloadDialog = onShowDownloadDialog;
    }

    public static DownloadHelper getInstance() {
        return helper;
    }

    public static String getDownloadTempFileName() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()+"/tempdownload/" + tempDownloadFileName;
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public void cancelDownload() {
        this.cancelDownload = true;
    }

    public void update(final Activity activity, final String downloadFileUrl, String updateMessage) {
        this.activity = activity;
        DownloadHelper.downloadFileUrl = downloadFileUrl;
        DownloadHelper.updateMessage = updateMessage;
        if (downloadHandler == null) {
            downloadHandler = new Handler() {
                int fileLength = 0;

                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 0: // fileLength
                            fileLength = msg.arg1;
                            if (fileLength <= 0) {
                                fileLength = defaultDownloadFileSize;
                            }
                            if (onProgressListener != null) {
                                onProgressListener.onFileLength(msg.arg1);
                            } else {
                            }
                            if (downloadDialog != null) downloadDialog.getDialog().setMax(fileLength/1024);
                            break;
                        case 1: // fileDownloadLength
                            if (msg.arg1*1.05f>=fileLength) {
                                fileLength = (int) (fileLength*1.2f);
                                if (downloadDialog != null) downloadDialog.getDialog().setMax(fileLength/1024);
                            }
                            if (onProgressListener != null) {
                                onProgressListener.onStep(msg.arg1);
                            }
                            if (downloadDialog != null) {
                                int x = msg.arg1 * 100 / fileLength;
                                downloadDialog.getDialog().setProgress(msg.arg1/1024);
                            }
                            break;
                        case 2: // finish
                            if (onProgressListener != null) {
                                onProgressListener.onFinish();
                            }
                            if (downloadDialog != null) downloadDialog.getDialog().cancel();
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(getDownloadTempFileName())),
                                    "application/vnd.android.package-archive");
                            activity.startActivity(intent);
                            break;
                        default: // failed
                            if (onProgressListener != null) {
                                onProgressListener.onFailure();
                            }
                            if (downloadDialog != null) downloadDialog.getDialog().cancel();
                            break;
                    }
                }
            };
        }

        final AlertDialog.Builder builder = onShowDownloadDialog.getConfirmDialogBuilder(activity, updateMessage);
        builder.setCancelable(false)
                .setPositiveButton(updateButtonName, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AlertDialog.Builder customerBuilder = onShowDownloadDialog.getProgressDialogBuilder(activity);
                        if (customerBuilder != null) customerDownloadDialog = builder.create();

                        if (customerDownloadDialog != null) {
                            customerDownloadDialog.show();
                        } else {
                            downloadDialog = new DownloadDialog(activity, downloadingTitle);
                            downloadDialog.showDialog();
                        }

                    }
                });
        if (cancelButtonName != null) {
            builder.setNegativeButton(cancelButtonName, null);
        }
        builder.create().show();
    }

    public class DownloadDialog {
        private ProgressDialog dialog;

        public DownloadDialog(Context context, String title) {

            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setTitle(title);
            String message = null;
            if (message != null && !message.isEmpty()) dialog.setMessage(message);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setProgress(100);
            dialog.setIndeterminate(false);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, downloadingCancelButtonName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    helper.cancelDownload();
                }
            });
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

    }

    public void downFile() {
        fileLength = 0;
        int downedFileLength = 0;
        cancelDownload = false;

        String savePathString = getDownloadTempFileName();

        File file = new File(savePathString);
        File dir = new File(file.getParent());

        try {
            if (!dir.exists()) {
                dir.mkdir();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
            fileLength = conn.getContentLength();
            inputStream = conn.getInputStream();
            if (conn.getResponseCode() != 200) {
                inputStream.close();
                throw new IOException("Image request failed with response code " + conn.getResponseCode());
            }
            outputStream = new FileOutputStream(file);

            message.arg1 = fileLength;
            message.what = 0;
            downloadHandler.sendMessage(message);

            try {
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[10240];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    if (cancelDownload) {
                        throw new Exception("cancel download");
                    }
                    fos.write(buffer, 0, length);
                    downedFileLength = downedFileLength + length;
                    Message message1 = new Message();
                    message1.arg1 = downedFileLength;
                    message1.what = 1;
                    downloadHandler.sendMessage(message1);
                }
                fos.flush();
                fos.close();
                inputStream.close();
                Message message2 = new Message();
                message2.what = 2;
                downloadHandler.sendMessage(message2);
            } catch (Exception e) {
                e.printStackTrace();
                Message message3 = new Message();
                message3.what = 3;
                downloadHandler.sendMessage(message3);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message message3 = new Message();
            message3.what = 3;
            downloadHandler.sendMessage(message3);
        }
    }

    public static HttpURLConnection getHttpConnection(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestProperty("Accept-Encoding", "identity");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            return conn;
        } catch (Exception e) {
            return null;
        }
    }

    public interface OnShowDownloadDialog {

        AlertDialog.Builder getConfirmDialogBuilder(Activity activity, String updateMessage);

        /**
         * return null to use stander ProgressDialog
         *
         * @return
         */
        AlertDialog.Builder getProgressDialogBuilder(Activity activity);
    }

    public interface OnProgressListener {
        void onFileLength(long length);

        void onStep(long downloadedLength);

        void onFinish();

        void onFailure();

    }

}
