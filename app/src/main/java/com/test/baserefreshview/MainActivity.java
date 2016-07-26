package com.test.baserefreshview;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.test.baserefreshview.ListBean.Content.ContentBean;
import com.xycode.xylibrary.adapter.XAdapter;
import com.xycode.xylibrary.base.BaseActivity;
import com.xycode.xylibrary.okHttp.Param;
import com.xycode.xylibrary.uiKit.views.MultiImageView;
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
                final List<String> list = new ArrayList<>();
                for (int i = 0; i <= pos; i++) {
                    list.add(item.getCoverPicture());
                }
                MultiImageView mvItem = holder.getView(R.id.mvItem);
                mvItem.setList(list);
                if (list.size()==1) {
                    mvItem.setSingleImageRatio(Tools.getWidthHeightFromFilename(list.get(0), "_wh", "x").getAspectRatio());
                }
                mvItem.setLoadImageListener(new MultiImageView.OnImageLoadListener() {
                    @Override
                    public Uri setPreviewUri(int position) {
                        WH wh = Tools.getWidthHeightFromFilename(list.get(position), "_wh", "x");
                        return Uri.parse(list.get(position)+"!"+(wh.getAspectRatio()*50)+"!50");
                    }
                });
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
                        WH wh = Tools.getWidthHeightFromFilename(dataList.get(pos).getCoverPicture(), "_wh", "x");
                        TS.show(getThis(), "wh:"+ wh.width + " h:" + wh.height+ " r:"+wh.getAspectRatio());

                    }
                });
            }

            @Override
            protected void bindingHeader(CustomHolder holder, int pos) {
                switch (getItemViewType(pos)) {
                    case 1:
                        holder.setText(R.id.tvName, "我是Header1");
                        break;
                    case 2:
                        holder.setText(R.id.tvLoading, "我是Header2");
                        break;
                }
            }
        };

//        adapter.addHeader(1, R.layout.item_house);
//        adapter.addHeader(2, R.layout.layout_load_more);

        xRefresher.setup(this, adapter, true, new XRefresher.RefreshRequest<ContentBean>() {
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

        }, 4);
//        xRefresher.refreshList();
    }


}
