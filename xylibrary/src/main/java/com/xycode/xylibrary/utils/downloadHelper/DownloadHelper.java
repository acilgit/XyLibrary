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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

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
@Deprecated
public class DownloadHelper {

    private static DownloadHelper helper = null;
    private DownloadOptions options;
    private Activity activity;
    private Handler downloadHandler;
    private DownloadDialog downloadDialog;
    private AlertDialog customerDownloadDialog;
    private OnShowDownloadDialog onShowDownloadDialog;
    private OnProgressListener onProgressListener;

    private static int defaultDownloadFileSize = 1024*1024*20;

//    private String downloadingTitle = "";
//    private String downloadingCancelButtonName = "";
//    private String updateButtonName = "";
    private String updateMessage;
//    private String cancelButtonName = null;
    private String downloadFileUrl = "";
    private String tempDownloadFileName = "tempDownloadFileName.apk";

    private InputStream inputStream;
    private OutputStream outputStream;

    int fileLength = 0;
    int downedFileLength = 0;

    private CancelListener cancelListener;

    private boolean cancelDownload;
    private boolean noFileLength = false;

    public static void init(DownloadOptions options, @NonNull OnShowDownloadDialog onShowDownloadDialog) {
        if (helper != null) return;
        helper = new DownloadHelper(onShowDownloadDialog);
        helper.options = options;
    }

    public DownloadHelper(@NonNull OnShowDownloadDialog onShowDownloadDialog) {
        this.onShowDownloadDialog = onShowDownloadDialog;
    }

    public static DownloadHelper getInstance() {
        return helper;
    }

    public String getDownloadTempFileName() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()+"/tempdownload/" + tempDownloadFileName;
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public void cancelDownload() {
        this.cancelDownload = true;
    }

    /**
     * check update
     * @param activity
     * @param cancelListener
     * @param downloadFileUrl
     * @param updateMessage
     */
    public void update(final Activity activity, final CancelListener cancelListener, final String downloadFileUrl, String updateMessage) {
        this.activity = activity;
        getInstance().downloadFileUrl = downloadFileUrl;
        getInstance().updateMessage = updateMessage;
        getInstance().cancelListener = cancelListener;
        if (downloadHandler == null) {
            downloadHandler = new Handler() {
                int fileLength = 0;

                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 0: // fileLength
                            fileLength = msg.arg1;
                            noFileLength = fileLength <= 0;
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
                            if (noFileLength && msg.arg1*1.05f>=fileLength) {
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
                            /**
                             * when download being canceled, callback
                             */
                            if (cancelDownload && cancelListener != null) {
                                cancelListener.onCancel();
                            }
                            break;
                    }
                }
            };
        }

        final AlertDialog.Builder builder = onShowDownloadDialog.getConfirmDialogBuilder(activity, updateMessage);
        builder.setCancelable(false)
                .setPositiveButton(options.updateButtonName, (dialog, which) -> {

                    AlertDialog.Builder customerBuilder = onShowDownloadDialog.getProgressDialogBuilder(activity);
                    if (customerBuilder != null) customerDownloadDialog = builder.create();

                    if (customerDownloadDialog != null) {
                        customerDownloadDialog.show();
                    } else {
                        downloadDialog = new DownloadDialog(activity, options.downloadingTitle);
                        downloadDialog.showDialog();
                    }

                });
        if (options.cancelButtonName != null) {
            builder.setNegativeButton(options.cancelButtonName, null);
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
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, options.downloadingCancelButtonName, (dialog1, which) -> {
                helper.cancelDownload();
            });
        }

        public ProgressDialog getDialog() {
            return dialog;
        }

        public void showDialog() {
            dialog.show();
            new Thread(DownloadHelper.this::downFile).start();
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
                throw new IOException("ImageBean request failed with response code " + conn.getResponseCode());
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

    public static class DownloadOptions {
       private String updateButtonName;
       private String cancelButtonName;
       private String downloadingTitle;
       private String downloadingCancelButtonName;

        public DownloadOptions(String updateButtonName, String cancelButtonName, String downloadingTitle, String downloadingCancelButtonName) {
            this.updateButtonName = updateButtonName;
            this.cancelButtonName = cancelButtonName;
            this.downloadingTitle = downloadingTitle;
            this.downloadingCancelButtonName = downloadingCancelButtonName;
        }

        public String getUpdateButtonName() {
            return updateButtonName == null ? "" : updateButtonName;
        }

        public void setUpdateButtonName(String updateButtonName) {
            this.updateButtonName = updateButtonName;
        }

        public String getCancelButtonName() {
            return cancelButtonName == null ? "" : cancelButtonName;
        }

        public void setCancelButtonName(String cancelButtonName) {
            this.cancelButtonName = cancelButtonName;
        }

        public String getDownloadingTitle() {
            return downloadingTitle == null ? "" : downloadingTitle;
        }

        public void setDownloadingTitle(String downloadingTitle) {
            this.downloadingTitle = downloadingTitle;
        }

        public String getDownloadingCancelButtonName() {
            return downloadingCancelButtonName == null ? "" : downloadingCancelButtonName;
        }

        public void setDownloadingCancelButtonName(String downloadingCancelButtonName) {
            this.downloadingCancelButtonName = downloadingCancelButtonName;
        }
    }

    public interface CancelListener{
        void onCancel();
    }

}
