package com.test.baserefreshview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.test.baserefreshview.ListBean.ContentBean;
import com.xycode.xylibrary.adapter.CustomHolder;
import com.xycode.xylibrary.adapter.XAdapter;
import com.xycode.xylibrary.animation.SlideInBottomAnimation;
import com.xycode.xylibrary.annotation.SaveState;
import com.xycode.xylibrary.base.PhotoSelectBaseActivity;
import com.xycode.xylibrary.instance.FrescoLoader;
import com.xycode.xylibrary.okHttp.Header;
import com.xycode.xylibrary.okHttp.OkResponseListener;
import com.xycode.xylibrary.okHttp.Param;
import com.xycode.xylibrary.uiKit.recyclerview.FloatingBarItemDecoration;
import com.xycode.xylibrary.uiKit.views.MultiImageView;
import com.xycode.xylibrary.uiKit.views.loopview.AdLoopView;
import com.xycode.xylibrary.uiKit.views.nicespinner.NiceSpinner;
import com.xycode.xylibrary.unit.MsgEvent;
import com.xycode.xylibrary.unit.StringData;
import com.xycode.xylibrary.unit.UrlData;
import com.xycode.xylibrary.unit.ViewTypeUnit;
import com.xycode.xylibrary.unit.WH;
import com.xycode.xylibrary.utils.ImageUtils;
import com.xycode.xylibrary.utils.LogUtil.L;
import com.xycode.xylibrary.utils.TS;
import com.xycode.xylibrary.utils.Tools;
import com.xycode.xylibrary.utils.serverApiHelper.ServerControllerActivity;
import com.xycode.xylibrary.xRefresher.RefreshRequest;
import com.xycode.xylibrary.xRefresher.XRefresher;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.Call;
import okhttp3.Response;

import static com.test.baserefreshview.api.Api.api;


/**
 * new
 */
public class MainActivity extends ABaseActivity {

    private XRefresher xRefresher;
    TagLayout tags;
    private SimpleDraweeView siv;
    @SaveState
    private int iii;
    NiceSpinner spinner;
    @SaveState(SaveState.JSON_OBJECT)
    private ListBean bean;
    @SaveState
    List<String> list;

    String content = "<p>\r\n\t<img src=\"http://ww4.sinaimg.cn/bmiddle/483b2741jw1fawazpne7yj20qo0zkgtx.jpg\" /><img src=\"http://ww3.sinaimg.cn/bmiddle/483b2741jw1fawazxtqglj20qo0zkwlx.jpg\" />\r\n</p>\r\n<p>\r\n\thjfgsdiohfksdhcsjdhcfiduhfjkhiewhfjsda.n\r\n</p>";
    private XAdapter<ContentBean> adapter;

    @Override
    protected int setActivityLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initOnCreate() {
        setWindowMode(WindowMode.INPUT_ADJUST);
//        ButterKnife.bind(this);
//        start(TestA.class);
        xRefresher = (XRefresher) findViewById(R.id.xRefresher);
        siv = (SimpleDraweeView) findViewById(R.id.siv);
        tags = (TagLayout) findViewById(R.id.tags);
        spinner = (NiceSpinner) findViewById(R.id.nice_spinner);
//        mBean.setMessage("ddddddd");
//        mBean.setResultCode(1);
//        spinner.attachDataSource(Arrays.asList(R.array.test_array));
        /*
        spinner.attachDataSource(new ArrayList<StringData>());
        spinner.getStringData().getObject()
        */

        Intent intent = getIntent();

        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (uri != null) {
                String host = uri.getHost();
                String dataString = intent.getDataString();
                String id = uri.getQueryParameter("id");
                String path = uri.getPath();
                String path1 = uri.getEncodedPath();
                String queryString = uri.getQuery();
                L.d("host:"+host);
                L.d("dataString:" + dataString);
                L.d("id:" + id);
                L.d("path:" + path);
                L.d("path1:" + path1);
                L.d("queryString:" + queryString);
            }
        }

        findViewById(R.id.xtv).setOnClickListener(v -> {
            L.e("----------------- start");
            Flowable.interval(2, 1, TimeUnit.SECONDS)
//                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Long>() {
                        Subscription subscription;
                        @Override
                        public void onSubscribe(Subscription s) {
                            L.e("----------------- onSubscribe");
                            subscription = s;
                            s.request(4);
                        }

                        @Override
                        public void onNext(Long aLong) {
                            L.e("----------------- next: "+ aLong);
                            if (aLong == 3) {
                                subscription.cancel();
                            }

                        }

                        @Override
                        public void onError(Throwable t) {
                            t.printStackTrace();
                            L.e("----------------- error:" );
                        }

                        @Override
                        public void onComplete() {
                            L.e("----------------- onComplete");
                        }
                    });
            ServerControllerActivity.startThis(getThis(), api());
        });

//        DrawerLayout drawerLayout = (DrawerLayout) getLayoutInflater().inflate(com.xycode.xylibrary.R.layout.layout_base_console_view, null);
//        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.dl);
//        ((TextView) drawerLayout.findViewById(R.id.tvLogs)).setText("哈哈哈");
        View v = findViewById(R.id.v);


      /*  mToggle = new ActionBarDrawerToggle(HomeActivity.this,
                mDrawerLayout,
                toolbar,
                R.string.open,
                R.string.close);
        mToggle.syncState();
        mDrawerLayout.addDrawerListener(mToggle);*/

        findViewById(R.id.li).setOnClickListener(null);
      /*  ListItem item = new ListItem(getThis());
        item.setOnViewSenseListener(new BaseItemView.OnViewSenseListener<String>() {

            @Override
            public void sense(View view, String obj) {

            }
        });*/

        List<String> list = new ArrayList<>();
//        list.add("或在在要要在");
//        list.add("在在要要在");
        list.add("在在要要在");
        list.add("要要在");
        tags.setDataList(list);
        tags.setTagCheckedMode(TagLayout.FLOW_TAG_CHECKED_SINGLE);
        tags.setOnTagSelectListener((childViewList, dataList, selectedStateList, clickPos) -> {
//            adapter.notifyDataSetChanged();
//            adapter.setDataList(new ArrayList<>());
            PhotoActivity.startThis(getThis(), "http://mxycsku.qiniucdn.com/group5/M00/5B/0C/wKgBfVXdYkqAEzl0AAL6ZFMAdKk401.jpg");

            newCall().url(api().getSomeAddress)
                    .body(new Param("p", "PPP"))
                    .addDefaultHeader(true)
                    .addDefaultParams(true)
                    .header(new Header("a", "callA"))
                    .call(new OkResponseListener() {
                        @Override
                        public void handleJsonSuccess(Call call, Response response, JSONObject json) throws Exception {
                            ListBean listBean = JSON.parseObject(null, ListBean.class);
                            TS.show("OK :) Ha ha :" + listBean.getContent().size());
//                            start(New1Activity.class);
                        }

                        @Override
                        public void handleJsonError(Call call, Response response, JSONObject json) throws Exception {

                        }
                    });
        });
        Uri uri = Uri.parse("http://mxycsku.qiniucdn.com/group5/M00/5B/0C/wKgBfVXdYkqAEzl0AAL6ZFMAdKk401.jpg");
//        siv.setImageURI();
        FrescoLoader.setImageUrl(siv, "http://47.52.25.198/files//1/image/2017_08_14/7D87ADF7E151063A.gif");

//        ImageUtils.setFrescoViewUri(siv, uri, null);

        //                        .setImageUrl(R.id.siv, item.getCoverPicture(), new ResizeOptions(getResources().getDimensionPixelSize(R.dimen.imageSelectorFolderCoverSize)))
//                            ImageUtils.setFrescoViewUri(holder.getView(R.id.siv), null, nu);
/*+"!"+ (int)(60*ratio)+ "!60"*///                        mvItem.setList(list);
//                            drawable.addLevel(1, 1, d);
/**
 * 适配图片大小 <br/>
 * 默认大小：bitmap.getWidth(), bitmap.getHeight()<br/>
 * 适配屏幕：getDrawableAdapter
 */
        FloatingBarItemDecoration floatingBarItemDecoration = new FloatingBarItemDecoration(getThis(), null,
                new FloatingBarItemDecoration.Options(R.dimen.margin32)
                        .setBackgroundColor(R.color.gray).setTextPaint(R.color.white, R.dimen.text14),
                obj -> ((ContentBean) obj).getPosterTitle());
        adapter = new XAdapter<ContentBean>(this, () -> bean.getContent()) {

            @Override
            protected void beforeSetDataList(List<ContentBean> dataList) {
                floatingBarItemDecoration.setList(dataList, adapter.getHeaderCount());
                super.beforeSetDataList(dataList);
            }

            @Override
            protected ViewTypeUnit getViewTypeUnitForLayout(ContentBean item) {
                switch (item.getId()) {
                    case "1":
                        break;
                    default:
                        break;
                }

                return new ViewTypeUnit(item.getId(), R.layout.item_house);
//                if (item.getTitle().length()==8) {
//                    return new ViewTypeUnit(2, R.layout.list_item_text).setFullSpan(true);
//
//                }
//                return new ViewTypeUnit(1, R.layout.list_item_text);
            }

            @Override
            public void creatingHolder(final CustomHolder holder, ViewTypeUnit viewType) {
                switch (viewType.getLayoutId()) {
                    case R.layout.item_house:
                        holder.setExpandViewId(R.id.tvText);
                        holder.setClick(R.id.llItem);
                        holder.setClick(R.id.tvName);
                        MultiImageView mvItem = holder.getView(R.id.mvItem);

                        mvItem.setOverlayDrawableListener(position -> {
                            if (position == 8) {
                                return getResources().getDrawable(R.drawable.more_images);
                            }
                            return null;
                        });
                        mvItem.setOnItemClickListener((view, position, urlData) -> {
                            WH wh = Tools.getWidthHeightFromFilename(getShowingList().get(holder.getAdapterPosition()).getPosterImage(), "_wh", "x");
                            TS.show(getThis(), "wh:" + wh.width + " h:" + wh.height + " r:" + wh.getAspectRatio(), null);
                        });
                        break;
                    case R.layout.list_item_text:
                        holder.setClick(R.id.item);
                        break;
                    default:
                        break;
                }
            }

            @Override
            protected void handleItemViewClick(CustomHolder holder, ContentBean item, int viewId, ViewTypeUnit viewTypeUnit) {
                switch (viewId) {
                    case R.id.tvName:
                        String s1 = String.format("%.0f", 0.0001d);
                        String s2 = String.format("%s", 0.0000100d);
                        TS.show(" YES tvName s1:" + s1 + "   s2:"+s2);
//                        TS.show(" no tvNameas " + viewId);
                        item.setExpanded(!item.isExpanded());
                        holder.setExpand(item.isExpanded(), true, obj -> notifyDataSetChanged());
                        break;
                    case R.id.item:
//                        String s1 = String.format("%.0f", 0.0001d);
//                        String s2 = String.format("%s", 0.0000100d);
//                        TS.show(" YES tvName s1:" + s1 + "   s2:"+s2);
//                        adapter.setDataList(new ArrayList<>());

                        break;
                    case R.id.tvText:
//                        String s1 = String.format("%.0f", 0.0001d);
//                        String s2 = String.format("%s", 0.0000100d);
//                        TS.show(" YES tvName s1:" + s1 + "   s2:"+s2);
//                        TS.show(" YES text " + viewId);
                        break;
                    default:
//                        TS.show(item.getAddress() + " YES " + viewId);
                        break;
                }
            }

            @Override
            public void bindingHolder(CustomHolder holder, final List<ContentBean> dataList, final int pos) {
                ContentBean item = dataList.get(pos);
                switch (getLayoutId(item.getId())) {
                    case R.layout.item_house:
                        ((CheckBox) holder.getView(R.id.cb)).setChecked(item.isExpanded());
                        MultiImageView mvItem = holder.getView(R.id.mvItem);
//                            ImageUtils.setFrescoViewUri(holder.getView(R.id.siv), null, nu);

                        final List<UrlData> list = new ArrayList<>();
                        for (int i = 0; i <= pos; i++) {
                            if (i == 3) {
//                                list.add(new UrlData("abd" + item.getCoverPicture() /*+"!"+ (int)(60*ratio)+ "!60"*/));
                            } else {
//                                list.add(new UrlData(item.getCoverPicture() /*+"!"+ (int)(60*ratio)+ "!60"*/));
                            }
                        }
                        mvItem.setList(list);

                        StringData stringData = null;
                        holder.setExpand(item.isExpanded(), false);
                        holder
                                .setText(R.id.tvText, pos + "")
//                        .setImageUrl(R.id.siv, item.getCoverPicture(), new ResizeOptions(getResources().getDimensionPixelSize(R.dimen.imageSelectorFolderCoverSize)))
                                .setText(R.id.tvName, item.getPosterTitle() )
                                .setText(R.id.tvText, stringData.getString())
                        ;
                       /* TextView tv = holder.getView(R.id.tvText);

                        Html.fromHtml(content, source -> {
                            File file = Tools.checkFile(Tools.getFileDir(getThis()).getAbsolutePath(), source);
                            if (!file.exists()) {
                                ImageUtils.loadBitmapFromFresco(getThis(), Uri.parse(source), bitmap -> {
                                        ImageUtils.saveBitmapToFile(getThis(), file, bitmap);
                                        getThis().runOnUiThread(() -> {
                                            setHtmlText(tv);
                                            adapter.notifyDataSetChanged();
                                        });
                                });
                            }
                            return null;
                        }, null);
                        setHtmlText(tv);*/
                        break;
                    case R.layout.list_item_text:
                        holder.setText(R.id.tvName, item.getPosterTitle());
                        break;
                    default:
                        break;
                }
            }

            @Override
            protected void creatingHeader(final CustomHolder holder, int headerKey) {
                switch (headerKey) {
                    case 2:
                        AdLoopView bannerView = holder.getView(R.id.banner);
                        setBanner(bannerView);
//                        ImageUtils.loadBitmapFromFresco(Uri.parse("http://mxycsku.qiniucdn.com/group5/M00/5B/0C/wKgBfVXdYkqAEzl0AAL6ZFMAdKk401.jpg"), bitmap1 -> {
                        ImageUtils.loadBitmapFromFresco(Uri.parse("https://members.mytaoheung.com//files////afd98eadd2394913bb40d3ade0100c3e//image//2017_10_12//D585B1DD9C8A705F.jpg"), bitmap1 -> {
                            Bitmap bmp = ImageUtils.doGaussianBlur(bitmap1, 30, false, 100);
                            holder.setImageBitmap(R.id.iv, bmp);
                        });
                        holder.setClick(R.id.iv, v1 -> {
                            Integer.parseInt("abc");
                        });
                        holder.setClick(R.id.iv2, v1 -> {
                            start(New1Activity.class);
                        });

                        holder.getRootView().setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        break;
                    case 3:
                        break;
                    case 4:
                        RecyclerView rv = holder.getView(R.id.rv);
                        rv.setLayoutManager(new LinearLayoutManager(getThis()));
                        break;
                    default:
                        break;
                }
            }

            @Override
            protected void bindingHeader(CustomHolder holder, int headerKey) {
                switch (headerKey) {
                    case 3:
                        break;
                    case 4:
                        List<String> listStr = new ArrayList<>();
                        listStr.add("kasdjfa;sjfallajsdfa1");
                        listStr.add("kasdjfa;sjfallajsdfa2");
                        listStr.add("kasdjfa;sjfallajsdfa3");
                        listStr.add("kasdjfa;地苛标准苛颉在在村苛另。工基本原则栽栽载村落枯塔顶，载栽甄别朝代喉咙暴露口在历史上2日3啥时3虽然3呢日呢是2呢是2国家是");
                        XAdapter<String> xAdapter = new XAdapter<String>(getThis(), () -> listStr) {
                            @Override
                            public void bindingHolder(CustomHolder holder, List<String> dataList, int pos) {
                                holder.setText(R.id.tv, dataList.get(pos));
                            }

                            @Override
                            protected ViewTypeUnit getViewTypeUnitForLayout(String item) {
                                return new ViewTypeUnit(1, R.layout.list_item);
                            }
                        };
                        RecyclerView rv = holder.getView(R.id.rv);
                        rv.setAdapter(xAdapter);
                        break;
                }
            }

            @Override
            protected void bindingFooter(CustomHolder holder) {
                if (getShowingList().size() == 0) {
                    holder.setText(R.id.tv, "OK");
                } else {
                    holder.setText(R.id.tv, "点击我一下")
                            .setClick(R.id.tv, v -> TS.show("haha"));
                }
            }

        /*    @Override
            protected void bindingFooterLoader(CustomHolder holder, int viewType) {
                if (viewType == XAdapter.LAYOUT_FOOTER_RETRY) {
                    holder.setText(com.xycode.xylibrary.R.id.tv, "哇哈哈！");
                }
            }*/
        };
        L.d("http://mxycsku.qiniucdn.com/group6/M00/98/E9/wKgBjVXdGPiAUmMHAALfY_C7_7U637.jpg");
        L.i("http://mxycsku.qiniucdn.com/group6/M00/98/E9/wKgBjVXdGPiAUmMHAALfY_C7_7U637.jpg");
        L.v("http://mxycsku.qiniucdn.com/group6/M00/98/E9/wKgBjVXdGPiAUmMHAALfY_C7_7U637.jpg,http://mxycsku.qiniucdn.com/group6/M00/98/E9/wKgBjVXdGPiAUmMHAALfY_C7_7U637.jpg,http://mxycsku.qiniucdn.com/group6/M00/98/E9/wKgBjVXdGPiAUmMHAALfY_C7_7U637.jpg");

        adapter.openLoadAnimation(new SlideInBottomAnimation());
        adapter.setShowNoDataFooter(true);

//        adapter.setFooter(R.layout.layout_no_datas);
//        adapter.addHeader(3, R.layout.layout_recyclerview);
        adapter.addHeader(2, R.layout.layout_banner);
//        adapter.addHeader(3, R.layout.layout_hotfix);
//        adapter.addHeader(4, R.layout.layout_recyclerview);
//        adapter.setFooter(R.layout.footer);

        xRefresher.setup(this, adapter).setLoadMore()
                .setOnSwipeListener(() -> {
           /* postForm("https://www.taichi-tiger.com:8080/append/app_poster/selectAllPosters", new Param(), false, new OkHttp.OkResponseListener() {
                @Override
                public void handleJsonSuccess(Call call, Response response, JSONObject json) throws Exception {
                    TS.show("OK");
                }

                @Override
                public void handleJsonError(Call call, Response response, JSONObject json) throws Exception {
                    TS.show("NO");
                }
            });*/

                }).setRefreshRequest(new RefreshRequest<ContentBean>() {
            @Override
            public String setRequestParamsReturnUrl(Param params) {
                params.add("a", "asfafasfasdfasfasfasfasfasfasdfasfdasdfadsfasdfsadfas");
                params.add("b", "asfafasfasdfasfasfasfasfasfasdfasfdasdfadsfasdfsadfas");
                params.add("c", "asfafasfasdfasfasfasfasfasfasdfasfdasdfadsfasdfsadfas");
                L.e(JSON.toJSONString(params));
//                return "http://zhijia51.com/append/store_recommend/sell_house_page";
//                return "http://www.zhijia51.com/append/store_recommend/sell_house_page";
                return api().getSomeAddress;
            }


            @Override
            public List<ContentBean> setListData(JSONObject json) {
                bean = JSON.parseObject(json.toString(), ListBean.class);
                int size = bean.getContent().size();
                for (int i = 0; i < size - new Random().nextInt(3); i++) {
//                    bean.getContent().remove(0);
                }
                return bean.getContent();
            }

           /* @Override
            protected boolean ignoreSameItem(ContentBean newItem, ContentBean listItem) {
                return newItem.getId().equals(listItem.getId());
            }*/

        })
//                .setRefreshPageSize(6).setStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//        }).setRefreshPageSize(6).setGridLayoutManager(2, GridLayoutManager.VERTICAL, false)
                .setRecyclerViewDividerWithGap(R.color.transparent, R.dimen.dividerHeight, R.dimen.dividerHeight);
        xRefresher.getRecyclerView().addItemDecoration(floatingBarItemDecoration);
//        new FloatingBarItemDecoration(getThis(), )
//        xRefresher.getRecyclerView().addItemDecoration();
//        xRefresher.setRecyclerViewDivider(android.R.color.holo_orange_light, R.dimen.margin32, R.dimen.sideMargin, R.dimen.sideMargin);
//        xRefresher.refreshList();
       /* CompulsiveHelperActivity.update(getThis(), new CompulsiveHelperActivity.CancelCallBack() {
            @Override
            public void onCancel(boolean must) {
                if (must) ;
            }

            @Override
            public void onFinish(boolean must) {
                if (must) ;
            }

            @Override
            public void onFailed(boolean must) {
                if (must) ;
            }

            @Override
            public void onDownLoad(int downLength, int fileLength) {

            }
        }, new Param().add(CompulsiveHelperActivity.Api, "down_url")
                .add(CompulsiveHelperActivity.IsMust, String.valueOf(1)).add(CompulsiveHelperActivity.Illustration, "关系说明"));*/

    }

    private void setHtmlText(TextView tv) {
        Spanned spanned = Html.fromHtml(content, s -> {
            File f = Tools.checkFile(Tools.getFileDir().getAbsolutePath(), s);
            if (!f.exists()) {
                return null;
            }
            Bitmap bmp = ImageUtils.getBitmapFromFile(f);
            BitmapDrawable d = new BitmapDrawable(bmp);
//                            drawable.addLevel(1, 1, d);
            if (bmp != null) {
                long newHeight = 0;// 未知数
                int newWidth = Tools.getScreenSize().x;// 默认屏幕宽
                newHeight = (newWidth * bmp.getHeight()) / bmp.getWidth();
                d.setBounds(0, 0, newWidth, (int) newHeight);
                d.setLevel(1);
            }
            return d;
        }, null);
        tv.setText(spanned);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    protected AlertDialog setLoadingDialog() {
        return null;
    }

    public static final String NAME = "Main" + "Reset";

    private void setBanner(AdLoopView bannerView) {
        List<UrlData> bannerList = new ArrayList<>();

        bannerList.add(new UrlData("res:///" + R.mipmap.chuzu));
        bannerList.add(new UrlData("http://mxycsku.qiniucdn.com/group6/M00/98/E9/wKgBjVXdGPiAUmMHAALfY_C7_7U637.jpg"));
        bannerList.add(new UrlData("http://mxycsku.qiniucdn.com/group6/M00/96/F7/wKgBjVXbxnCABW_iAAKLH0qKKXo870.jpg", new WH(1, 2)));
        bannerList.add(new UrlData("http://mxycsku.qiniucdn.com/group6/M00/96/F7/wKgBjVXbxnCABW_iAAKLH0qKKXo870.jpg"));

        bannerView.setOnImageClickListener((parent, view, position, realPosition, urlData) -> {

            postEvent("anEventName", "ABC", obj -> {
                L.e((String) obj);
                return "AA";
            });

            File externalCacheDir = getThis().getExternalCacheDir();
            L.e("externalCacheDir  " + externalCacheDir + " " + getThis().getFilesDir());
            List<String> list = new ArrayList<>();
//                list.add("或在在要要在");
//                list.add("在在要要在");
//                list.add("或要要在");
//                list.add("或要要在");
//                list.add("或要要在");
//                list.add("或在在要在");
//                list.add("要");
//                tags.setDataList(list);
            PhotoSelectActivity.startForResult(getThis(), PhotoSelectActivity.class, new PhotoSelectBaseActivity.CropParam());
//            TS.show("count " + xRefresher.getAdapter().getItemCount());

            RelativeLayout rl = new RelativeLayout(getThis());
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(600, 600);

            rl.setLayoutParams(param);
//            rl.setBackgroundColor(R.color.colorPrimary);


            SimpleDraweeView siv = new SimpleDraweeView(getThis());
            siv.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
            siv.setAspectRatio(1);
            int side = Tools.dp2px(16);
            RelativeLayout.LayoutParams ivParam = new RelativeLayout.LayoutParams(side, side);
            ivParam.addRule(RelativeLayout.CENTER_IN_PARENT);
            siv.setLayoutParams(ivParam);
            siv.setImageURI(ImageUtils.getResUri(com.xycode.xylibrary.R.mipmap.loading));

            rl.addView(siv);
            ((ViewGroup) findViewById(R.id.ll)).addView(rl);

//            Animation animation = AnimationUtils.loadAnimation(getThis(), com.xycode.xylibrary.R.animator.rotate_loading);
//            LinearInterpolator lin = new LinearInterpolator();
//            animation.setInterpolator(lin);

//            siv.setAnimation(animation);
//            animation.start();


//                DownloadHelper.getInstance().update(getThis(), "http://m.bg114.cn/scene/api/public/down_apk/1/driver1.0.20.apk", "有新版本了啊！！");
//                Uri destination = Uri.fromFile(getTempHead());  // 保存地址
//                Crop.of(uri, destination).withSize(150, 150).crop(getThis(), BaseActivity.REQUEST_CODE_GOT_RESULT);
//                TS.show(getThis(), "Hi + " + position + " real:" + realPosition);
        });
        bannerView.initData(bannerList);
    }

    @Override
    protected void onPhotoSelectResult(int resultCode, Uri uri) {
        super.onPhotoSelectResult(resultCode, uri);
        if (resultCode == RESULT_OK) {
            if (iii == 1) {
            }
            siv.setImageURI(null);
            siv.setImageURI(uri);
            CustomHolder holder = xRefresher.getHeader(2);
//            holder.setImageUrl(R.id.iv, "");
            holder.setImageUrl(R.id.iv, String.valueOf(uri));
        } else {

        }
    }

    @Override
    public void onEvent(MsgEvent event) {
        super.onEvent(event);
        if (event.getEventName().equals("anEventName")) {
            TS.show(getThis(), event.getString(), null);
            Object o = event.getFeedBack().go("Event " + event.getString());
            L.e("object: " + o);
            xRefresher.refresh();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
