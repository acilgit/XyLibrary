package com.xycode.xylibrary.uiKit.imageSelector.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xycode.xylibrary.R;
import com.xycode.xylibrary.uiKit.imageSelector.bean.FolderBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.xycode.xylibrary.uiKit.imageSelector.ImageSelectorOptions.options;


/**
 * 文件夹Adapter
 * Created by Nereo on 2015/4/7.
 * Updated by nereo on 2016/1/19.
 */
public class FolderAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    private List<FolderBean> mFolderBeen = new ArrayList<>();

    int mImageSize;

    int lastSelected = 0;

    public FolderAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mImageSize = mContext.getResources().getDimensionPixelOffset(options().folderCoverSize);
    }

    /**
     * 设置数据集
     *
     * @param folderBeen
     */
    public void setData(List<FolderBean> folderBeen) {
        if (folderBeen != null && folderBeen.size() > 0) {
            mFolderBeen = folderBeen;
        } else {
            mFolderBeen.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFolderBeen.size() + 1;
    }

    @Override
    public FolderBean getItem(int i) {
        if (i == 0) return null;
        return mFolderBeen.get(i - 1);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_image_selector_folder, viewGroup, false);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (holder != null) {
            if (i == 0) {
                holder.name.setText(options().textAllFolder);
                holder.path.setText("/sdcard");
                holder.size.setText(String.format("%d%s",
                        getTotalImageSize(), mContext.getResources().getString(R.string.photo_unit)));
                if (mFolderBeen.size() > 0) {
                    FolderBean f = mFolderBeen.get(0);
                    if(f.cover==null){
                        holder.cover.setImageURI(Uri.parse(""));
                    }else {
                        holder.cover.setImageURI(Uri.fromFile(new File(f.cover.path)));
                    }

                }
            } else {
                holder.bindData(getItem(i));
            }
            if (lastSelected == i) {
                holder.rlItem.setBackgroundResource(R.color.transparentBlackLite);
            } else {
                holder.rlItem.setBackgroundResource(R.color.transparent);
            }
        }
        return view;
    }

    private int getTotalImageSize() {
        int result = 0;
        if (mFolderBeen != null && mFolderBeen.size() > 0) {
            for (FolderBean f : mFolderBeen) {
                result += f.imageBeen.size();
            }
        }
        return result;
    }

    public void setSelectIndex(int i) {
        if (lastSelected == i) return;

        lastSelected = i;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return lastSelected;
    }

    class ViewHolder {
        RelativeLayout rlItem;
        SimpleDraweeView cover;
        TextView name;
        TextView path;
        TextView size;
        ImageView indicator;

        ViewHolder(View view) {
            rlItem = (RelativeLayout) view.findViewById(R.id.rlItem);
            cover = (SimpleDraweeView) view.findViewById(R.id.cover);
            name = (TextView) view.findViewById(R.id.name);
            path = (TextView) view.findViewById(R.id.path);
            size = (TextView) view.findViewById(R.id.size);
            view.setTag(this);
        }

        void bindData(FolderBean data) {
            if (data == null) {
                return;
            }
            name.setText(data.name);
            path.setText(data.path);
            if (data.imageBeen != null) {
                size.setText(String.format("%d%s", data.imageBeen.size(), mContext.getResources().getString(R.string.photo_unit)));
            } else {
                size.setText("*" + mContext.getResources().getString(R.string.photo_unit));
            }
            // 显示图片
            if (data.cover != null) {
                cover.setImageURI(Uri.fromFile(new File(data.cover.path)));
            } else {
                cover.setImageURI(null);
            }
        }
    }

}
