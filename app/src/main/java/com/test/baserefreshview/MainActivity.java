package com.test.baserefreshview;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.test.baserefreshview.ListBean.Content.ContentBean;
import com.xycode.xylibrary.adapter.XAdapter;
import com.xycode.xylibrary.annotation.SaveState;
import com.xycode.xylibrary.base.BaseActivity;
import com.xycode.xylibrary.base.BaseFlowTagLayout;
import com.xycode.xylibrary.base.PhotoSelectBaseActivity;
import com.xycode.xylibrary.okHttp.Param;
import com.xycode.xylibrary.uiKit.views.MultiImageView;
import com.xycode.xylibrary.uiKit.views.loopview.AdLoopView;
import com.xycode.xylibrary.uiKit.views.loopview.internal.BaseLoopAdapter;
import com.xycode.xylibrary.unit.UrlData;
import com.xycode.xylibrary.unit.ViewTypeUnit;
import com.xycode.xylibrary.unit.WH;
import com.xycode.xylibrary.utils.ImageUtils;
import com.xycode.xylibrary.utils.L;
import com.xycode.xylibrary.utils.TS;
import com.xycode.xylibrary.utils.Tools;
import com.xycode.xylibrary.xRefresher.XRefresher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * new
 */
public class MainActivity extends BaseActivity {

    private XRefresher xRefresher;
    TagLayout tags;
    private SimpleDraweeView siv;
    @SaveState
    private int iii;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowMode(WindowMode.INPUT_ADJUST);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        xRefresher = (XRefresher) findViewById(R.id.xRefresher);
        siv = (SimpleDraweeView) findViewById(R.id.siv);
        tags = (TagLayout) findViewById(R.id.tags);

        findViewById(R.id.li).setOnClickListener(null);

        List<String> list = new ArrayList<>();
        list.add("或在在要要在");
        list.add("在在要要在");
        list.add("在在要要在");
        list.add("要要在");
        tags.setDataList(list);
        tags.setTagCheckedMode(TagLayout.FLOW_TAG_CHECKED_SINGLE);
        tags.setOnTagSelectListener(new BaseFlowTagLayout.OnTagSelectListener() {
            @Override
            public void onItemSelected(SparseArray<View> childViewList, List dataList, SparseBooleanArray selectedStateList, int clickPos) {

            }

//            @Override
//            public void onItemSelected(View childView, List dataList, int pos, boolean selected) {
//                TagLayout.setTextColor(childView, R.id.tv, getResources().getColor(selected ? R.color.white : R.color.black));
//            }
        });
        Uri uri = Uri.parse("http://mxycsku.qiniucdn.com/group5/M00/5B/0C/wKgBfVXdYkqAEzl0AAL6ZFMAdKk401.jpg");
//        siv.setImageURI();

        ImageUtils.setFrescoViewUri(siv, uri, null);

        XAdapter<ContentBean> adapter = new XAdapter<ContentBean>(this, new ArrayList<ContentBean>()) {
            @Override
            protected ViewTypeUnit getViewTypeUnitForLayout(ContentBean item) {
                switch (item.getId()) {
                    case "1":

                        break;
                    default:
                        break;
                }

                return new ViewTypeUnit(item.getId(), R.layout.item_house);
            }

            @Override
            public void creatingHolder(final CustomHolder holder, final List<ContentBean> dataList, ViewTypeUnit viewType) {
                switch (viewType.getLayoutId()) {
                    case R.layout.item_house:
                        holder.setClick(R.id.llItem);
                        holder.setClick(R.id.tvName);
                        MultiImageView mvItem = holder.getView(R.id.mvItem);
            /*    mvItem.setLoadImageListener(new MultiImageView.OnImageLoadListener() {
                    @Override
                    public Uri setPreviewUri(int position) {
                        WH wh = Tools.getWidthHeightFromFilename(list.get(position), "_wh", "x");
                        return Uri.parse(list.get(position)+"!"+(wh.getAspectRatio()*20)+"!20");
                    }
                });*/

                        mvItem.setOverlayDrawableListener(new MultiImageView.OnImageOverlayListener() {
                            @Override
                            public Drawable setOverlayDrawable(int position) {
                                if (position == 8) {
                                    return getResources().getDrawable(R.drawable.more_images);
                                }
                                return null;
                            }
                        });
                        mvItem.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position, UrlData urlData) {
                                WH wh = Tools.getWidthHeightFromFilename(dataList.get(holder.getAdapterPosition()).getCoverPicture(), "_wh", "x");
                                TS.show(getThis(), "wh:" + wh.width + " h:" + wh.height + " r:" + wh.getAspectRatio());
                            }
                        });
                        break;
                    default:
                        break;
                }
            }

            @Override
            protected void handleItemViewClick(CustomHolder holder, ContentBean item, int viewId, ViewTypeUnit viewTypeUnit) {

                switch (viewId) {
                    case R.id.tvName:
                        TS.show(" YES tvName " + viewId);
                        break;
                    case R.id.tvText:
                        TS.show(" YES text " + viewId);
                        break;
                    default:
                        TS.show(item.getAddress() + " YES " + viewId);
                        break;
                }
            }

            @Override
            public void bindingHolder(CustomHolder holder, final List<ContentBean> dataList, final int pos) {
                ContentBean item = dataList.get(pos);
                switch (getLayoutId(item.getId())) {
                    case R.layout.item_house:
                        holder.setText(R.id.tvName, item.getTitle())
//                        .setImageUrl(R.id.sdvItem, item.getCoverPicture())
                                .setText(R.id.tvText, pos + "");
                        MultiImageView mvItem = holder.getView(R.id.mvItem);

                        final List<UrlData> list = new ArrayList<>();
                        for (int i = 0; i <= pos; i++) {
                            list.add(new UrlData(item.getCoverPicture() /*+"!"+ (int)(60*ratio)+ "!60"*/));
                        }
                        mvItem.setList(list);
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
                        ImageUtils.loadBitmapFromFresco(getThis(), Uri.parse("http://mxycsku.qiniucdn.com/group5/M00/5B/0C/wKgBfVXdYkqAEzl0AAL6ZFMAdKk401.jpg"), new ImageUtils.IGetFrescoBitmap() {
                            @Override
                            public void afterGotBitmap(Bitmap bitmap) {
                                Bitmap bmp = ImageUtils.doGaussianBlur(bitmap, 30, false);
                                holder.setImageBitmap(R.id.iv, bmp);
                            }
                        });
                        break;
                    case 3:
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
                    case 4:
                        List<String> listStr = new ArrayList<>();
                        listStr.add("kasdjfa;sjfallajsdfa1");
                        listStr.add("kasdjfa;sjfallajsdfa2");
                        listStr.add("kasdjfa;sjfallajsdfa3");
                        listStr.add("kasdjfa;地苛标准苛颉在在村苛另。工基本原则栽栽载村落枯塔顶，载栽甄别朝代喉咙暴露口在历史上2日3啥时3虽然3呢日呢是2呢是2国家是");
                        XAdapter<String> xAdapter = new XAdapter<String>(getThis(), listStr) {
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
        };

        adapter.addHeader(3, R.layout.layout_recyclerview);
        adapter.addHeader(2, R.layout.layout_banner);
        adapter.addHeader(4, R.layout.layout_recyclerview);
//        adapter.setFooter(R.layout.footer);

        xRefresher.setup(this, adapter, true, new XRefresher.RefreshRequest<ContentBean>() {
            @Override
            public String setRequestParamsReturnUrl(Param params) {
//                params.add("a", "b");
//                return "http://zhijia51.com/append/store_recommend/sell_house_page";
                return "http://zhijia51.com/append/store_recommend/sell_house_page";
            }

            @Override
            public List<ContentBean> setListData(JSONObject json) {
                return JSON.parseObject(json.toString(), ListBean.class).getContent().getContent();
            }

            @Override
            protected boolean ignoreSameItem(ContentBean newItem, ContentBean listItem) {
                return newItem.getId().equals(listItem.getId());
            }
        });
        xRefresher.setRecyclerViewDivider(android.R.color.holo_orange_light, R.dimen.margin32, R.dimen.sideMargin, R.dimen.sideMargin);
//        xRefresher.refreshList();
    }

    @Override
    protected AlertDialog setLoadingDialog() {
        return null;
    }

    private void setBanner(AdLoopView bannerView) {
        List<UrlData> bannerList = new ArrayList<>();

        bannerList.add(new UrlData("res:///" + R.mipmap.chuzu));
        bannerList.add(new UrlData("http://mxycsku.qiniucdn.com/group6/M00/98/E9/wKgBjVXdGPiAUmMHAALfY_C7_7U637.jpg"));
        bannerList.add(new UrlData("http://mxycsku.qiniucdn.com/group6/M00/96/F7/wKgBjVXbxnCABW_iAAKLH0qKKXo870.jpg", new WH(1, 2)));
        bannerList.add(new UrlData("http://mxycsku.qiniucdn.com/group6/M00/96/F7/wKgBjVXbxnCABW_iAAKLH0qKKXo870.jpg"));

        bannerView.setOnImageClickListener(new BaseLoopAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PagerAdapter parent, View view, int position, int realPosition, UrlData urlData) {

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
                TS.show("count " + xRefresher.getAdapter().getItemCount());
//                DownloadHelper.getInstance().update(getThis(), "http://m.bg114.cn/scene/api/public/down_apk/1/driver1.0.20.apk", "有新版本了啊！！");
//                Uri destination = Uri.fromFile(getTempHead());  // 保存地址
//                Crop.of(uri, destination).withSize(150, 150).crop(getThis(), BaseActivity.REQUEST_CODE_GOT_RESULT);
//                TS.show(getThis(), "Hi + " + position + " real:" + realPosition);
            }
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
            XAdapter.CustomHolder holder = xRefresher.getHeader(2);
            holder.setImageUrl(R.id.iv, "");
            holder.setImageURI(R.id.iv, uri);
        } else {

        }
    }
}
