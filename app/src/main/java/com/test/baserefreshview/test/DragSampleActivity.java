package com.test.baserefreshview.test;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.baserefreshview.R;
import com.test.baserefreshview.test.Utils.DragUtils;
import com.test.baserefreshview.test.bean.DragDataBean;
import com.xycode.xylibrary.adapter.CustomHolder;
import com.xycode.xylibrary.adapter.OnInitList;
import com.xycode.xylibrary.adapter.XAdapter;
import com.xycode.xylibrary.unit.ViewTypeUnit;
import com.xycode.xylibrary.utils.toast.TS;
import com.xycode.xylibrary.xRefresher.OnSwipeListener;
import com.xycode.xylibrary.xRefresher.XRefresher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author thisfeng
 * @date 2018/2/6-下午5:06
 * 拖拽案例
 * 两个列表 实现一个列表拖拽至另一个列表目标区域。
 */

public class DragSampleActivity extends BaseActivity {
    XRefresher xRefresher1;
    XRefresher xRefresher2;
    RelativeLayout rlContent;
    XAdapter adapter2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_sample);
        xRefresher1 = findViewById(R.id.xRefresher1);
        xRefresher2 = findViewById(R.id.xRefresher2);
        rlContent = findViewById(R.id.rlContent);


        /**
         * datas
         */
        List<DragDataBean> list1 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list1.add(new DragDataBean("第" + i, i));
        }

        List<DragDataBean> list2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list2.add(new DragDataBean("Target" + i, i));

        }


        /**
         * 可拖拽列表区域
         */
        XAdapter adapter1 = new XAdapter<DragDataBean>(this, new OnInitList() {
            @Override
            public List getList() throws Exception {
                return list1;
            }
        }) {

            @Override
            protected ViewTypeUnit getViewTypeUnitForLayout(DragDataBean item) {
                return new ViewTypeUnit(0, R.layout.item_drag);

            }

            @Override
            public void creatingHolder(CustomHolder holder, ViewTypeUnit viewTypeUnit ) throws Exception {
                super.creatingHolder(holder, viewTypeUnit );
                holder.setClick(R.id.tvContent);
                holder.setLongClick(R.id.ivLogo);
            }

            @Override
            public void bindingHolder(CustomHolder holder, List<DragDataBean> dataList, int pos) throws Exception {
                super.bindingHolder(holder, dataList, pos);
                DragDataBean dataBean = dataList.get(pos);
                holder.setText(R.id.tvContent, dataBean.getName());

            }

            @Override
            protected void handleItemViewClick(CustomHolder holder, DragDataBean item, int viewId, ViewTypeUnit viewTypeUnit) {
                super.handleItemViewClick(holder, item, viewId, viewTypeUnit);
                switch (viewId) {
                    case R.id.tvContent:
                        TS.show(item.getName());
                        break;
                }
            }

            @Override
            protected boolean handleItemViewLongClick(CustomHolder holder, DragDataBean data, int viewId, ViewTypeUnit viewTypeUnit) {
                switch (viewId) {
                    case R.id.ivLogo:
                        //给所有的icon 设置可拖拽
                        ClipData.Item citem = new ClipData.Item("我来了->" + data.getName());//也可将数据设置在剪贴板内
                        ClipData clipData = new ClipData(null, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, citem);
                        //可被拖拽的View
                        ImageView icon = holder.getView(R.id.ivLogo);
                        LinearLayout llItem = holder.getView(R.id.llItem);
                        //启动拖拽的View将会有影子呈现，data可自由传递拖拽携带过去的值 Object
                        llItem.startDrag(clipData, new View.DragShadowBuilder(llItem), data.getName(), 0);


                        break;

                }
                return false;
            }

        };

        xRefresher1.setup(this, adapter1).setRecyclerViewDivider(R.color.bgDivider, R.dimen.dividerLineHeight);
        xRefresher1.refresh();


        /**
         * 拖拽至目标列表区域
         */
        adapter2 = new XAdapter<DragDataBean>(this, new OnInitList() {
            @Override
            public List getList() throws Exception {
                return list2;
            }
        }) {

            @Override
            protected ViewTypeUnit getViewTypeUnitForLayout(DragDataBean item) {
                return new ViewTypeUnit(0, R.layout.item_drag);

            }

            @Override
            public void creatingHolder(CustomHolder holder, ViewTypeUnit viewTypeUnit ) throws Exception {
                holder.setClick(R.id.tvContent);
                /**
                 * 给每一个Item的某个View设置监听拖拽事件
                 */
                TextView tvContent = holder.getView(R.id.tvContent);
                DragUtils.bindDragInZone(tvContent, new DragUtils.DragStatus() {
                    @Override
                    public void complete(String mLocalState, String data) {

                        Log.e("complete()", "回调:" + mLocalState + data);
                        TS.show(mLocalState + "-" + data + "------Target" + holder.getLayoutPosition());
//                        holder.setText(R.id.tvContent, data);
//                        adapter2.notifyDataSetChanged();
                        DragDataBean dataBean = list2.get(holder.getLayoutPosition());
                        dataBean.setName(mLocalState);
                        adapter2.notifyDataSetChanged();

                    }
                });

            }

            @Override
            public void bindingHolder(CustomHolder holder, List<DragDataBean> dataList, int pos) throws Exception {
                super.bindingHolder(holder, dataList, pos);
                DragDataBean dataBean = dataList.get(pos);
                holder.setText(R.id.tvContent, dataBean.getName());
//                holder.setTextColor(R.id.tvContent, getResources().getColor(R.color.colorAccent));


            }

            @Override
            protected void handleItemViewClick(CustomHolder holder, DragDataBean item, int viewId, ViewTypeUnit viewTypeUnit) {
                super.handleItemViewClick(holder, item, viewId, viewTypeUnit);
                switch (viewId) {
                    case R.id.tvContent:
                        TS.show(item.getName());
                        break;
                }
            }
        };

        xRefresher2.setup(this, adapter2).setOnSwipeListener(new OnSwipeListener() {
            @Override
            public void onRefresh() {
                xRefresher2.setRefreshing(false);
                adapter2.notifyDataSetChanged();
            }
        }).setRecyclerViewDivider(R.color.bgDivider, R.dimen.dividerLineHeight);
        xRefresher2.refresh();

    }

}
