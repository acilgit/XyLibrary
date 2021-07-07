package com.test.baserefreshview;

import android.app.Application;
import android.graphics.Point;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.xycode.xylibrary.Xy;
import com.xycode.xylibrary.instance.FrescoLoader;
import com.xycode.xylibrary.okHttp.Header;
import com.xycode.xylibrary.okHttp.IOkInit;
import com.xycode.xylibrary.okHttp.OkHttp;
import com.xycode.xylibrary.okHttp.OkResponseListener;
import com.xycode.xylibrary.okHttp.Param;
import com.xycode.xylibrary.okHttp.XSSSocketLFactory;
import com.xycode.xylibrary.unit.WH;
import com.xycode.xylibrary.utils.LogUtil.L;
import com.xycode.xylibrary.utils.Tools;
import com.xycode.xylibrary.utils.crashUtil.CrashActivity;
import com.xycode.xylibrary.utils.crashUtil.CrashItem;
import com.xycode.xylibrary.utils.crashUtil.ICrash;
import com.xycode.xylibrary.utils.toast.TS;
import com.xycode.xylibrary.xRefresher.InitRefresher;
import com.xycode.xylibrary.xRefresher.XRefresher;

import java.net.SocketTimeoutException;

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

        Xy.init(this, false);



        initCrash();
//        Fresco.initialize(this);

        OkHttp.OkOptions okOptions = new OkHttp.OkOptions(10, 10, 10) {
            @Override
            public void setOkHttpBuilder(OkHttpClient.Builder builder) {
                super.setOkHttpBuilder(builder);
                XSSSocketLFactory.SSLParams sslParams = XSSSocketLFactory.getSSLSocketFactory(getInstance().getResources().openRawResource(R.raw.tiger));
                builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
            }
        };
        OkHttp.init(new IOkInit() {
            @Override
            public int judgeResultWhenFirstReceivedResponse(Call call, Response response, JSONObject json) {
                String resultCode = json.getString("status");
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
            public void networkError(Call call, boolean isCanceled, Throwable throwable) {

                if (throwable instanceof SocketTimeoutException) {
                    //如果服务器超时 发送 日志记录给服务端

                    StringBuffer errorStr = new StringBuffer();
                    errorStr.append(call.request().url().url().toString());

                    errorStr.append(throwable.toString());

                }
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
//                    L.e(e.getMessage());
            }

            @Override
            public Param setParamsHeadersBeforeRequest(Param allParams, Header header) {
                header.add("aha", "heheh");
                return allParams;
            }

            @Override
            public Param setDefaultParams(Param defaultParams) {
                defaultParams.add("p", "defaultP");
                return defaultParams;
            }

            @Override
            public Header setDefaultHeader(Header defaultHeader) {
                defaultHeader.add("sectionId", "");
                defaultHeader.add("a", "defaultA");
                return defaultHeader;
            }

        }, okOptions);
        OkHttp.setMaxTransFileCount(2);

       /* ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, OkHttp.getClient())
                .build();
        Fresco.initialize(this, config);*/

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

//        XRefresher.setCustomerLoadMoreView(R.layout.layout_base_load_more);
        XRefresher.init(new InitRefresher() {
                            @Override
                            public void handleError(Call call, JSONObject json) {
                                TS.show(json.getString("msg"));
                            }

                            @Override
                            public void handleAllFailureSituation(Call call, int resultCode) {

                            }

                            @Override
                            public boolean addDefaultHeader() {
                                return false;
                            }

                            @Override
                            public boolean addDefaultParam() {
                                return false;
                            }
                        }, new XRefresher.Options()
                        .setLoadingRefreshingArrowColorRes(new int[]{android.R.color.holo_purple})

        );
//        XRefresher.setDefaultNoDataText("暂无数据", 1);

//        FrescoLoader.init(url ->  null);
        FrescoLoader.init(new FrescoLoader.OnFrescoListener() {
            @Override
            public ResizeOptions getMaxResizeOptions(Object urlObject) {
                WH wh = Tools.getWidthHeightFromFilename((String) urlObject, "_wh", "_");
                Point screenSize = Tools.getScreenSize();
                int x, y;
                if (wh.isAvailable() && wh.width > screenSize.x) {
                    x = screenSize.x;
                    y = (int) ((1.0 * x) / wh.getAspectRatio());
                    return new ResizeOptions(x, y);
                }
                return null;
            }

            @Override
            public String getUrlInObject(Object urlObject) {
                return null;
            }

            @Override
            public String getUrlPreviewInObject(Object urlObject) {
                if (urlObject instanceof String) {
                    String url = (String) urlObject;
                    WH wh = Tools.getWidthHeightFromFilename(url, "_wh", "_");
                    return url + "!" + (wh.getAspectRatio() * 40) + "!40";
                }
                return null;
            }
        });

  /*      XRefresher.setCustomerFooterView(R.layout.layout_base_load_more, new XAdapter.ICustomerLoadMore() {
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

    private void initCrash() {
        CrashActivity.setCrashOperation(crashItem -> {
            L.e(crashItem.toString());

            String jsonString = JSON.toJSONString(L.getLogList());

            OkHttp.newCall(CrashActivity.getInstance()).url("http://192.168.90.54:8080/a/api/app/log/submit").body(new Param()
                    .add("channel", "ANDROID").add("serial", "2FwGqS703X386W5").add("content", crashItem.toString())).call(new OkResponseListener() {
                @Override
                public void handleJsonSuccess(Call call, Response response, JSONObject json) throws Exception {
                    TS.show("错误信息已发送");
                }

                @Override
                public void handleJsonError(Call call, Response response, JSONObject json) throws Exception {

                }
            });
        }, new ICrash() {
            @Override
            public int getLayoutId() {
                return R.layout.activity_crash;
            }

            @Override
            public void setViews(CrashActivity activity, CrashItem crashItem) {

                Button btn = activity.findViewById(R.id.btn);
                btn.setOnClickListener(
                        v -> TS.show("okok......")
                );

                TextView tv = activity.findViewById(R.id.tv);
                //!Xy.isRelease() && L.showLog() ?
                tv.setText(crashItem.toString());
            }

            @Override
            public boolean getIsSaveCrashLogFile() {
                return true;
            }


        });
    }


}
