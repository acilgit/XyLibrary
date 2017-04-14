package com.xycode.xylibrary.utils.downloadHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xycode.xylibrary.R;
import com.xycode.xylibrary.interfaces.Interfaces;
import com.xycode.xylibrary.okHttp.Param;
import com.xycode.xylibrary.utils.TS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Administrator on 2016/10/22 0022.
 */

public class CompulsiveHelperActivity extends Activity {
    public static final String Title = "title";
    public static final String Illustration = "illustration";
    public static final String Cancel = "cancel";
    public static final String IsMust = "isMust";
    public static final String Confirm = "confirm";
    public static final String Must = "0";
    public static final String NotMust = "1";
    public static final String URL = "URL";
    public static final String Params = "Params";
    public static final String ErrorTips = "ErrorTips";
    public static final String ContactsWay = "ContactsWay";
    String title = "";
    String illustration = "";
    String cancel = "";
    String isMust = "1";
    String confirm = "";
    String errorTips = "";
    String contacts_way = "";
    private TextView tvTitle;
    private TextView tvIllustration;
    private TextView tvUpdateProgress;
    private TextView tvDownFileLength;
    private TextView tvFileLength;
    private TextView tvConfirm;
    private TextView tvCancel;
    private ProgressBar progressBar;
    private TextView tvIgnore;

    public interface CancelCallBack {

        void onCancel(boolean must);

        void onFinish(boolean must);

        void onFailed(boolean must);

        void onDownLoad(int downLength, int fileLength);

        void onAbortUpdate();

    }

    int fileLength = 0;
    int downedFileLength = 0;
    private String downloadFileUrl = "";
    private String tempDownloadFileName = "tempDownloadFileName.apk";
    private InputStream inputStream;
    static CancelCallBack cancelCallBack;
    static Interfaces.CB ignoreCallback;
    private static int defaultDownloadFileSize = 1024 * 1024 * 20;
    private OutputStream outputStream;
    private boolean cancelDownload;
    private boolean noFileLength = false;

//    private Options options;

    private Handler downloadHandler;

    /**
     * @param context
     * @param cancelCallback
     * @param builder        must contain url
     */
    @Deprecated
    public static void update(Context context, CancelCallBack cancelCallback, Param builder) {
        Intent intent = new Intent(context, CompulsiveHelperActivity.class);
        cancelCallBack = cancelCallback;
        if (builder.getKey(URL) == null) {
            TS.show(context.getString(R.string.tips_get_dowload_url_fail));
            return;
        }
        intent.putExtra(Params, builder);
        context.startActivity(intent);
    }

    public static void update(Context context, CancelCallBack cancelCallback, Options options) {
        Intent intent = new Intent(context, CompulsiveHelperActivity.class);
        cancelCallBack = cancelCallback;
        ignoreCallback = options.getIgnoreCallback();

        Param builder = new Param()
                .add(Title, options.title)
                .add(Illustration, options.illustration)
                .add(Cancel, options.cancel)
                .add(Confirm, options.confirm)
                .add(IsMust, options.isMust ? Must : NotMust)
                .add(URL, options.downloadFileUrl)
                .add(ErrorTips, options.errorTips)
                .add(ContactsWay, options.contacts_way);

        if (builder.getKey(URL) == null) {
            TS.show(context.getString(R.string.tips_get_dowload_url_fail));
            return;
        }
        intent.putExtra(Params, builder);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compulsive_update);
        initView();
        init();
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvIllustration = (TextView) findViewById(R.id.tv_illustration);
        tvUpdateProgress = (TextView) findViewById(R.id.tv_update_progress);
        tvDownFileLength = (TextView) findViewById(R.id.tv_downFileLength);
        tvFileLength = (TextView) findViewById(R.id.tv_fileLength);
        tvConfirm = (TextView) findViewById(R.id.confirm);
        tvCancel = (TextView) findViewById(R.id.cancel);
        tvIgnore = (TextView) findViewById(R.id.tvIgnore);
        progressBar = (ProgressBar) findViewById(R.id.pb_update_progress);
    }

    private void init() {
        HashMap<String, String> builder = (HashMap) getIntent().getSerializableExtra(Params);
        if (builder != null) {
            title = builder.get(Title);
            illustration = builder.get(Illustration);
            cancel = builder.get(Cancel);
            confirm = builder.get(Confirm);
            isMust = TextUtils.isEmpty(builder.get(IsMust)) ? isMust : builder.get(IsMust);
            downloadFileUrl = builder.get(URL);
            errorTips = builder.get(ErrorTips);
            contacts_way = builder.get(ContactsWay);
        }
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(illustration)) {
            tvIllustration.setText(illustration);
        }
        if (!TextUtils.isEmpty(cancel)) {
            tvCancel.setText(cancel);
        }
        if (!TextUtils.isEmpty(confirm)) {
            tvConfirm.setText(confirm);
        }


        //if is not must ,show the cancel button
        tvCancel.setVisibility(isMust() ? View.GONE : View.VISIBLE);
        tvIgnore.setVisibility(ignoreCallback == null ? View.GONE : View.VISIBLE);

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
                            tvFileLength.setText(String.valueOf(fileLength / 1024));
                            break;
                        case 1: // fileDownloadLength
                            tvConfirm.setSelected(true);
                            tvConfirm.setText(R.string.text_cancel_update);
                            if (noFileLength && msg.arg1 * 1.05f >= fileLength) {
                                fileLength = (int) (fileLength * 1.2f);
                                tvFileLength.setText(String.valueOf(fileLength / 1024));
                            }
                            tvCancel.setVisibility(View.GONE);
                            tvIgnore.setVisibility(View.GONE);
                            tvDownFileLength.setText(String.valueOf(msg.arg1 / 1024));
                            int progress = (int) ((100.0d * msg.arg1) / fileLength);
                            progressBar.setProgress(progress);
                            tvUpdateProgress.setText(String.format(getString(R.string.percent), String.valueOf(progress)));
                            if (cancelCallBack != null) {
                                cancelCallBack.onDownLoad(msg.arg1, fileLength);
                            }
                            break;
                        case 2: // finish
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(getDownloadTempFileName())),
                                    "application/vnd.android.package-archive");
                            startActivity(intent);
                            if (cancelCallBack != null) {
                                cancelCallBack.onFinish(isMust());
                            }
                            break;
                        default: // failed
                            if (cancelDownload) {
                                if (cancelCallBack != null) {
                                    cancelCallBack.onCancel(isMust());
                                }
                                finish();
                            } else {
                                tvIllustration.setText(TextUtils.isEmpty(errorTips) ?
                                        getString(R.string.update_error_tips) : errorTips + "\n" + msg.obj + "\n" +
                                        contacts_way);
                                tvIllustration.setVisibility(View.VISIBLE);
                                tvConfirm.setSelected(false);
                                tvConfirm.setText(TextUtils.isEmpty(confirm) ? getString(R.string.update_now) : confirm);
                                if (!isMust()) {
                                    tvCancel.setVisibility(View.VISIBLE);
                                }
                            }
                            break;
                    }
                }
            };
        }
        //when update click
        tvConfirm.setOnClickListener((v) -> {
            //is updating
            if (tvConfirm.isSelected() && Integer.valueOf(tvDownFileLength.getText().toString().trim()) > 0) {
                tvConfirm.setSelected(false);
                cancelDownload = true;
            } else {
                tvIllustration.setVisibility(View.GONE);
                tvCancel.setVisibility(View.GONE);
                tvIgnore.setVisibility(View.GONE);
                new Thread(CompulsiveHelperActivity.this::downFile).start();
                tvConfirm.setText(R.string.update_connecting);
                tvConfirm.setSelected(true);
            }
        });

        //can only visible on update not must
        tvCancel.setOnClickListener(v -> {
            cancelCallBack.onAbortUpdate();
            finish();
        });
        tvIgnore.setOnClickListener(v -> {
            if(ignoreCallback != null) ignoreCallback.go(null);
            cancelCallBack.onAbortUpdate();
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        cancelDownload = true;
        if (cancelCallBack != null) {
            cancelCallBack.onCancel(isMust());
        }
        finish();
    }

    public boolean isMust() {
        return Must.equals(isMust);
    }

    public String getDownloadTempFileName() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/tempdownload/" + tempDownloadFileName;
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
            if (downloadFileUrl.startsWith("https")) {
                HttpsURLConnection conn = getHttpsConnection(downloadFileUrl);
                fileLength = conn.getContentLength();
                inputStream = conn.getInputStream();
                if (conn.getResponseCode() != 200) {
                    inputStream.close();
                    throw new IOException("ImageBean request failed with response code " + conn.getResponseCode());
                }
            } else {
                HttpURLConnection conn = getHttpConnection(downloadFileUrl);
                fileLength = conn.getContentLength();
                inputStream = conn.getInputStream();
                if (conn.getResponseCode() != 200) {
                    inputStream.close();
                    throw new IOException("ImageBean request failed with response code " + conn.getResponseCode());
                }
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
                message3.obj = e.getMessage();
                downloadHandler.sendMessage(message3);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message message3 = new Message();
            message3.what = 3;
            message3.obj = e.getMessage();
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

    public static HttpsURLConnection getHttpsConnection(String url) {
        try {
            SslUtils.ignoreSsl();
            HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
            conn.setRequestProperty("Accept-Encoding", "identity");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            return conn;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void finish() {
        cancelCallBack = null;
        super.finish();
    }

    public static class Options {
        private String title;
        private String illustration;
        private String cancel;
        private String confirm;
        private boolean isMust;
        private String downloadFileUrl;
        private String errorTips;
        private String contacts_way;
        private Interfaces.CB ignoreCallback = null;

        public Options(String downloadFileUrl) {
            this.downloadFileUrl = downloadFileUrl;
        }

        public Interfaces.CB getIgnoreCallback() {
            return ignoreCallback;
        }

        public Options setIgnoreCallback(Interfaces.CB ignoreCallback) {
            this.ignoreCallback = ignoreCallback;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public Options setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getIllustration() {
            return illustration;
        }

        public Options setIllustration(String illustration) {
            this.illustration = illustration;
            return this;
        }

        public String getCancel() {
            return cancel;
        }

        public Options setCancel(String cancel) {
            this.cancel = cancel;
            return this;
        }

        public String getConfirm() {
            return confirm;
        }

        public Options setConfirm(String confirm) {
            this.confirm = confirm;
            return this;
        }

        public boolean isMust() {
            return isMust;
        }

        public Options setMust(boolean must) {
            isMust = must;
            return this;
        }

        public String getDownloadFileUrl() {
            return downloadFileUrl;
        }

        public Options setDownloadFileUrl(String downloadFileUrl) {
            this.downloadFileUrl = downloadFileUrl;
            return this;
        }

        public String getErrorTips() {
            return errorTips;
        }

        public Options setErrorTips(String errorTips) {
            this.errorTips = errorTips;
            return this;
        }

        public String getContacts_way() {
            return contacts_way;
        }

        public Options setContacts_way(String contacts_way) {
            this.contacts_way = contacts_way;
            return this;
        }
    }
}
