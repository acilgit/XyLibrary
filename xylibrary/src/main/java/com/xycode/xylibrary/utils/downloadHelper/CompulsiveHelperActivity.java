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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xycode.xylibrary.R;
import com.xycode.xylibrary.okHttp.Param;
import com.xycode.xylibrary.utils.TS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
    TextView mTitle;
    TextView mIllustration;

    TextView mUpdateProgress;

    TextView mDownFileLength;

    TextView mFileLength;

    TextView mConfirm;

    TextView mCancel;

    ProgressBar mProgress;

    public interface CancelCallBack {

        void onCancel(boolean must);

        void onFinish(boolean must);

        void onFailed(boolean must);

        void onDowdLoad(int downLength, int fileLength);

    }

    int fileLength = 0;
    int downedFileLength = 0;
    private String downloadFileUrl = "";
    private String tempDownloadFileName = "tempDownloadFileName.apk";
    private InputStream inputStream;
    static CancelCallBack mCancelCallBack;
    private static int defaultDownloadFileSize = 1024 * 1024 * 20;
    private OutputStream outputStream;
    private boolean cancelDownload;
    private boolean noFileLength = false;


    private Handler downloadHandler;


    public static void update(Context context, CancelCallBack cancelCallback, Param builder) {
        Intent intent = new Intent(context, CompulsiveHelperActivity.class);
        mCancelCallBack = cancelCallback;
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
        setContentView(R.layout.dialog_compulsive_update);
        initView();
        init();
    }

    private void initView() {
        mTitle = (TextView) findViewById(R.id.tv_title);
        mIllustration = (TextView) findViewById(R.id.tv_illustration);
        mUpdateProgress = (TextView) findViewById(R.id.tv_update_progress);
        mDownFileLength = (TextView) findViewById(R.id.tv_downFileLength);
        mFileLength = (TextView) findViewById(R.id.tv_fileLength);
        mConfirm = (TextView) findViewById(R.id.confirm);
        mCancel = (TextView) findViewById(R.id.cancel);
        mProgress = (ProgressBar) findViewById(R.id.pb_update_progress);
    }

    private void init() {
        Param builder = (Param) getIntent().getSerializableExtra(Params);
        if (builder != null) {
            title = builder.getKey(Title);
            illustration = builder.getKey(Illustration);
            cancel = builder.getKey(Cancel);
            confirm = builder.getKey(Confirm);
            isMust = builder.getKey(IsMust).isEmpty() ? isMust : builder.getKey(IsMust);
            downloadFileUrl = builder.getKey(URL);
            errorTips = builder.getKey(ErrorTips);
            contacts_way = builder.getKey(ContactsWay);
        }
        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
        if (!TextUtils.isEmpty(illustration)) {
            mIllustration.setText(illustration);
        }
        if (!TextUtils.isEmpty(cancel)) {
            mCancel.setText(cancel);
        }
        if (!TextUtils.isEmpty(confirm)) {
            mConfirm.setText(confirm);
        }


        //if is not must ,show the cancel button
        if (!isMust()) {
            mCancel.setVisibility(View.VISIBLE);
        }
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
                            mFileLength.setText(String.valueOf(fileLength / 1024));
                            break;
                        case 1: // fileDownloadLength
                            mConfirm.setSelected(true);
                            mConfirm.setText(R.string.text_cancel_update);
                            if (noFileLength && msg.arg1 * 1.05f >= fileLength) {
                                fileLength = (int) (fileLength * 1.2f);
                                mFileLength.setText(String.valueOf(fileLength / 1024));
                            }
                            mCancel.setVisibility(View.GONE);
                            mDownFileLength.setText(String.valueOf(msg.arg1 / 1024));
                            mProgress.setProgress(msg.arg1 * 100 / fileLength);
                            mUpdateProgress.setText(String.format(getString(R.string.percent), String.valueOf(msg.arg1 * 100 / fileLength)));
                            if (mCancelCallBack != null) {
                                mCancelCallBack.onDowdLoad(msg.arg1, fileLength);
                            }
                            break;
                        case 2: // finish
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(getDownloadTempFileName())),
                                    "application/vnd.android.package-archive");
                            startActivity(intent);
                            if (mCancelCallBack != null) {
                                mCancelCallBack.onFinish(isMust());
                            }
                            break;
                        default: // failed
                            if (cancelDownload) {
                                if (mCancelCallBack != null) {
                                    mCancelCallBack.onCancel(isMust());
                                }
                                finish();
                            } else {
                                mIllustration.setText(TextUtils.isEmpty(errorTips) ?
                                        getString(R.string.update_error_tips) : errorTips + "\n" + msg.obj + "\n" +
                                        contacts_way);
                                mIllustration.setVisibility(View.VISIBLE);
                                mConfirm.setSelected(false);
                                mConfirm.setText(TextUtils.isEmpty(confirm) ? getString(R.string.update_now) : confirm);
                                if (!isMust()) {
                                    mCancel.setVisibility(View.VISIBLE);
                                }
                            }
                            break;
                    }
                }
            };
        }
        //when update click
        mConfirm.setOnClickListener((v) -> {
            //is updating
            if (mConfirm.isSelected() && Integer.valueOf(mDownFileLength.getText().toString().trim()) > 0) {
                mConfirm.setSelected(false);
                cancelDownload = true;
            } else {
                mIllustration.setVisibility(View.GONE);
                mCancel.setVisibility(View.GONE);
                new Thread(CompulsiveHelperActivity.this::downFile).start();
                mConfirm.setText(R.string.update_connecting);
                mConfirm.setSelected(true);
            }
        });

        //can only visible on update not must
        mCancel.setOnClickListener(v -> finish());
    }

    @Override
    public void onBackPressed() {
        finish();
        cancelDownload = true;
        if (mCancelCallBack != null) {
            mCancelCallBack.onCancel(isMust());
        }
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

    @Override
    public void finish() {
        mCancelCallBack = null;
        super.finish();
    }
}
