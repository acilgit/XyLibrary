package com.xycode.xylibrary.uiKit.imageSelector.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
 */
public class FolderAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;

    private List<FolderBean> folderBeen = new ArrayList<>();

    int imageSize;

    int lastSelected = 0;

    public FolderAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageSize = this.context.getResources().getDimensionPixelOffset(options().folderCoverSize);
    }

    /**
     *
     * @param folderBeen
     */
    public void setData(List<FolderBean> folderBeen) {
        if (folderBeen != null && folderBeen.size() > 0) {
            this.folderBeen = folderBeen;
        } else {
            this.folderBeen.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return folderBeen.size() + 1;
    }

    @Override
    public FolderBean getItem(int i) {
        if (i == 0) return null;
        return folderBeen.get(i - 1);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_image_selector_folder, viewGroup, false);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (holder != null) {
            if (i == 0) {
                holder.name.setText(R.string.text_folder_all);
                holder.path.setText("/sdcard");
                holder.size.setText(String.format("%d%s",
                        getTotalImageSize(), context.getResources().getString(R.string.text_photo_unit)));
                if (folderBeen.size() > 0) {
                    FolderBean f = folderBeen.get(0);
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
        if (folderBeen != null && folderBeen.size() > 0) {
            for (FolderBean f : folderBeen) {
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
                size.setText(String.format("%d%s", data.imageBeen.size(), context.getResources().getString(R.string.text_photo_unit)));
            } else {
                size.setText("*" + context.getResources().getString(R.string.text_photo_unit));
            }
            if (data.cover != null) {
                cover.setImageURI(Uri.fromFile(new File(data.cover.path)));
            } else {
                cover.setImageURI(null);
            }
        }
    }

}
