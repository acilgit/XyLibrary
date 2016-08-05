package com.test.baserefreshview;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.test.baserefreshview.ListBean.Content.ContentBean;
import com.xycode.xylibrary.adapter.XAdapter;
import com.xycode.xylibrary.base.BaseActivity;
import com.xycode.xylibrary.okHttp.Param;
import com.xycode.xylibrary.uiKit.views.MultiImageView;
import com.xycode.xylibrary.uiKit.views.loopview.AdLoopView;
import com.xycode.xylibrary.uiKit.views.loopview.internal.BaseLoopAdapter;
import com.xycode.xylibrary.unit.WH;
import com.xycode.xylibrary.utils.TS;
import com.xycode.xylibrary.utils.Tools;
import com.xycode.xylibrary.xRefresher.XRefresher;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private XRefresher xRefresher;
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xRefresher = (XRefresher) findViewById(R.id.xRefresher);

        XAdapter<ContentBean> adapter = new XAdapter<ContentBean>(this, new ArrayList<ContentBean>(), R.layout.item_house) {

            @Override
            public void bindingHolder(CustomHolder holder, final List<ContentBean> dataList, final int pos) {
                ContentBean item = dataList.get(pos);
                holder.setText(R.id.tvName, item.getTitle())
//                        .setImageUrl(R.id.sdvItem, item.getCoverPicture())
                        .setText(R.id.tvText, pos + "");
                MultiImageView mvItem = holder.getView(R.id.mvItem);
//                if (dataList.get(pos).getCoverPicture() != null) {
//                WH wh = Tools.getWidthHeightFromFilename(dataList.get(holder.getAdapterPosition()).getCoverPicture(), "_wh", "x");
//                float ratio = wh.getAspectRatio();
//                mvItem.setSingleImageRatio(ratio<1 ? 1 : ratio);
//                L.e("wh.getAspectRatio: "+ ratio);
//                }
                final List<String> list = new ArrayList<>();
                for (int i = 0; i <= pos; i++) {
                    list.add(item.getCoverPicture() /*+"!"+ (int)(60*ratio)+ "!60"*/);
                }
                mvItem.setList(list);

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
                    public void onItemClick(View view, int position) {
                        WH wh = Tools.getWidthHeightFromFilename(dataList.get(position).getCoverPicture(), "_wh", "x");
                        TS.show(getThis(), "wh:"+ wh.width + " h:" + wh.height+ " r:"+wh.getAspectRatio());
                    }
                });
            }

            @Override
            protected void creatingHeader(CustomHolder holder, int headerKey) {
                switch (headerKey) {
                    case 1:
                        AdLoopView bannerView = holder.getView(R.id.banner);
                        setBanner(bannerView);
                        break;
                    default:
                        break;
                }
            }

            @Override
            protected void bindingHeader(CustomHolder holder, int pos) {
                switch (getItemViewType(pos)) {
                    case 1:
                        break;
                    case 2:
                        holder.setText(R.id.tvLoading, "我是Header2");
                        break;
                }
            }

            @Override
            public void creatingHolder(final CustomHolder holder, final List<ContentBean> dataList, int viewType) {
                MultiImageView mvItem = holder.getView(R.id.mvItem);

             /*   mvItem.setLoadImageListener(new MultiImageView.OnImageLoadListener() {
                    @Override
                    public Uri setPreviewUri(int position) {
                        WH wh = Tools.getWidthHeightFromFilename(list.get(position), "_wh", "x");
                        return Uri.parse(list.get(position)+"!"+(wh.getAspectRatio()*20)+"!20");
                    }
                });*//*
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
                    public void onItemClick(View view, int position) {
                        WH wh = Tools.getWidthHeightFromFilename(dataList.get(holder.getAdapterPosition()).getCoverPicture(), "_wh", "x");
                        TS.show(getThis(), "wh:"+ wh.width + " h:" + wh.height+ " r:"+wh.getAspectRatio());
                    }
                });*/
            }

        /*    @Override
            protected int setViewLayoutWhenLayoutListNull(int viewType) {
                return R.layout.item_house;
            }

            @Override
            protected int getItemTypeForMultiLayout(ContentBean item) {
                for (int i = 0; i < multiLayoutList.size(); i++) {
                    if (multiLayoutList.get(i).equals(item.getId())) {
                        return i;
                    }
                }
                int key = multiLayoutList.size();
                multiLayoutList.add(item.getId());
                return key;
            }*/
        };

//        adapter.addHeader(1, R.layout.layout_banner);
//        adapter.addHeader(2, R.layout.layout_load_more);

        xRefresher.setup(this, adapter, true, new XRefresher.OnSwipeListener() {
            @Override
            public void onRefresh() {

            }
        }, new XRefresher.RefreshRequest<ContentBean>() {
            @Override
            public String setRequestParamsReturnUrl(Param params) {
//                params.add("a", "b");
                return "http://192.168.1.222:9000/append/store_recommend/sell_house_page";
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
//        xRefresher.refreshList();

        setBanner(((AdLoopView) findViewById(R.id.banner)));
    }

    private void setBanner(AdLoopView bannerView) {
        List<String> bannerList = new ArrayList<>();
        bannerList.add("http://mxycsku.qiniucdn.com/group5/M00/5B/0C/wKgBfVXdYkqAEzl0AAL6ZFMAdKk401.jpg");
        bannerList.add("http://mxycsku.qiniucdn.com/group6/M00/98/E9/wKgBjVXdGPiAUmMHAALfY_C7_7U637.jpg");
        bannerList.add("http://mxycsku.qiniucdn.com/group6/M00/96/F7/wKgBjVXbxnCABW_iAAKLH0qKKXo870.jpg");

        bannerView.setOnImageClickListener(new BaseLoopAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PagerAdapter parent, View view, int position, int realPosition) {
                TS.show(getThis(), "Hi + " + position + " real:" + realPosition);
            }
        });
        bannerView.initData(bannerList);
    }


}
