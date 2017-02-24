package com.test.baserefreshview;

import android.app.Activity;
import android.app.Application;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.xycode.xylibrary.adapter.XAdapter;
import com.xycode.xylibrary.instance.FrescoLoader;
import com.xycode.xylibrary.okHttp.Header;
import com.xycode.xylibrary.okHttp.OkHttp;
import com.xycode.xylibrary.okHttp.Param;
import com.xycode.xylibrary.okHttp.XSSSocketLFactory;
import com.xycode.xylibrary.unit.WH;
import com.xycode.xylibrary.utils.L;
import com.xycode.xylibrary.utils.TS;
import com.xycode.xylibrary.utils.Tools;
import com.xycode.xylibrary.utils.downloadHelper.DownloadHelper;
import com.xycode.xylibrary.xRefresher.XRefresher;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by XY on 2016/7/9.
 */
public class App extends Application {

    private static App instance;

    public static App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null) {
            instance = this;
        } else {
            return;
        }

      /*  TS.init(this, new TS.IToastLayoutSetter() {
            @Override
            public void onToastLayout(View root, Toast toast) {

            }
        });*/

        TS.init(this);
//        Fresco.initialize(this);


        OkHttp.OkOptions okOptions = new OkHttp.OkOptions(1,1,1){
            @Override
            public void setOkHttpBuilder(OkHttpClient.Builder builder) {
                super.setOkHttpBuilder(builder);
                XSSSocketLFactory.SSLParams sslParams = XSSSocketLFactory.getSSLSocketFactory(getInstance().getResources().openRawResource(R.raw.tiger));
                builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
            }
        };
        OkHttp.init(this, new OkHttp.IOkInit() {
           @Override
            public int judgeResultWhenFirstReceivedResponse(Call call, Response response, JSONObject json) {
                String resultCode = json.getString("resultCode");
                if ("1".equals(resultCode)) {
                    return OkHttp.RESULT_SUCCESS;
                } else if ("0".equals(resultCode)) {
                    return OkHttp.RESULT_ERROR;
                } else if ("-1".equals(resultCode)) {
                    return OkHttp.RESULT_VERIFY_ERROR;
                }
                return OkHttp.RESULT_OTHER;
            }

            @Override
            public void networkError(Call call, boolean isCanceled) {

            }

            @Override
            public void receivedNetworkErrorCode(Call call, Response response) {
                TS.show(R.string.ts_no_network);
            }

            @Override
            public boolean resultSuccessByJudge(Call call, Response response, JSONObject json, int resultCode) {
                switch (resultCode) {
                    case OkHttp.RESULT_VERIFY_ERROR:
                        return true;
                }
                return false;
            }

            @Override
            public void judgeResultParseResponseFailed(Call call, String response, Exception e) {
                    L.e(e.getMessage());
            }

            @Override
            public Param setDefaultParams(Param defaultParams) {
                return defaultParams;
            }

            @Override
            public Header setDefaultHeader(Header defaultHeader) {
                defaultHeader.add("sectionId", "");
                return defaultHeader;
            }

        }, okOptions);
        OkHttp.setMaxTransFileCount(2);

        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, OkHttp.getClient())
                .build();
        Fresco.initialize(this, config);

        /*DownloadHelper.init("现在更新", "暂不更新", "正在下载中", "取消", new DownloadHelper.OnShowDownloadDialog() {
            @Override
            public AlertDialog.Builder getConfirmDialogBuilder(Activity activity, String updateMessage) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("哈哈哈哈").setMessage(updateMessage);
                return builder;
            }

            @Override
            public AlertDialog.Builder getProgressDialogBuilder(Activity activity) {
                return null;
            }
        });
        DownloadHelper.getInstance().setOnProgressListener(new DownloadHelper.OnProgressListener() {
            @Override
            public void onFileLength(long length) {

            }

            @Override
            public void onStep(long downloadedLength) {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onFailure() {
                TS.show("下载失败");
            }
        });*/

        XRefresher.setCustomerLoadMoreView(R.layout.layout_load_more);
//        XRefresher.setDefaultNoDataText("暂无数据", 1);

//        FrescoLoader.init(url ->  null);
        FrescoLoader.init(getInstance(), new FrescoLoader.OnFrescoListener() {
                    @Override
                    public String getPreviewUri(String url) {
                        WH wh = Tools.getWidthHeightFromFilename(url, "_wh", "_");
                        return url + "!" + (wh.getAspectRatio() * 40) + "!40";
                    }

                    @Override
                    public ResizeOptions getMaxResizeOptions(String url) {
                        WH wh = Tools.getWidthHeightFromFilename(url, "_wh", "_");
                        Point screenSize = Tools.getScreenSize(getInstance());
                        int x, y;
                        if (wh.isAvailable() && wh.width> screenSize.x) {
                            x = screenSize.x;
                            y = (int) ((1.0 * x) / wh.getAspectRatio());
                            return new ResizeOptions(x, y);
                        }
                        return null;
                    }
                });

  /*      XRefresher.setCustomerFooterView(R.layout.layout_load_more, new XAdapter.ICustomerLoadMore() {
            @Override
            public void bindFooter(XAdapter.CustomHolder holder, int footerState) {
                switch (footerState) {
                    case XAdapter.LOADER_LOADING:
                        holder.setText(R.id.tvLoading, R.string.app_name);
                        holder.getView(R.id.pbLoadMore).setVisibility();
                        break;
                    default:
                        break;
                }
            }
        });*/
    }

}
