package com.xycode.xylibrary.uiKit.imageSelector.adapter;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xycode.xylibrary.R;
import com.xycode.xylibrary.uiKit.imageSelector.MultiImageSelectorFragment;
import com.xycode.xylibrary.uiKit.imageSelector.bean.ImageBean;
import com.xycode.xylibrary.uiKit.imageSelector.utils.ImageSelectorUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.xycode.xylibrary.uiKit.imageSelector.ImageSelectorOptions.options;


/**
 * Created by Administrator on 2016/5/10.
 */
public class GridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //是否有相机
    private static final int TYPE_CAMERA = 1;
    private static final int TYPE_NORMAL = 0;
    private Context context;

    private LayoutInflater inflater;
    //是否有相机
    private boolean showCamera = true;
    //是否是多选(是则显示指示器，否则不显示)
    private boolean showSelectIndicator = true;
    //展示的图片数据
    private List<ImageBean> imageBeen = new ArrayList<>();
    //被选的图片数据
    private List<ImageBean> selectedImageBeen = new ArrayList<>();
    //每个视图的宽度
    final int gridWidth;
    //用于点击item时的回调
    MultiImageSelectorFragment fragment;
    public View.OnClickListener showCameraActionListener;
    public View.OnClickListener clickListener;

    {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment != null && v.getTag(R.id.image) != null) {
                    fragment.selectImageFromGrid((ImageBean) v.getTag(R.id.image), showSelectIndicator == true ? MultiImageSelectorFragment.MODE_MULTI : MultiImageSelectorFragment.MODE_SINGLE);
                }
            }
        };
        showCameraActionListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("qqq", "showCameraAction:" + "fragment" + (fragment == null));
                if (fragment != null) {
                    fragment.showCameraAction();
                }
            }
        };
    }

    /**
     * 设置当前显示的图片的地址的列表
     *
     * @param imageBeen
     */
    public void setData(List<ImageBean> imageBeen) {
        selectedImageBeen.clear();
        if (imageBeen != null && imageBeen.size() > 0) {
            this.imageBeen = imageBeen;
        } else {
            this.imageBeen.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 设置是否展示多选指示器
     *
     * @param b
     */
    public void showSelectIndicator(boolean b) {
        showSelectIndicator = b;
    }

    /**
     * 设置是否显示相机按钮
     *
     * @param b
     */
    public void setShowCamera(boolean b) {
        if (showCamera == b) return;

        showCamera = b;
        notifyDataSetChanged();
    }

    /**
     * 更新被选择的图片的地址列表
     *
     * @param imageBean
     */
    public void select(ImageBean imageBean) {
        if (selectedImageBeen.contains(imageBean)) {
            selectedImageBeen.remove(imageBean);
        } else {
            selectedImageBeen.add(imageBean);
        }
        notifyDataSetChanged();
    }

    /**
     * 设置默认展示的目录
     *
     * @param resultList
     */
    public void setDefaultSelected(ArrayList<String> resultList) {
        for (String path : resultList) {
            ImageBean imageBean = getImageByPath(path);
            if (imageBean != null) {
                selectedImageBeen.add(imageBean);
            }
        }
        if (selectedImageBeen.size() > 0) {
            notifyDataSetChanged();
        }
    }

    private ImageBean getImageByPath(String path) {
        if (imageBeen != null && imageBeen.size() > 0) {
            for (ImageBean imageBean : imageBeen) {
                if (imageBean.path.equalsIgnoreCase(path)) {
                    return imageBean;
                }
            }
        }
        return null;
    }


    public GridAdapter(Context context, boolean showCamera, int column, MultiImageSelectorFragment fragment) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.showCamera = showCamera;
        this.fragment = fragment;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
        } else {
            width = wm.getDefaultDisplay().getWidth();
        }
        gridWidth = width / column;
        //将图片缩小column倍
    }

    @Override
    public int getItemViewType(int position) {
        int type = TYPE_NORMAL;
        if (position == 0) {
            type = TYPE_CAMERA;
        }
        return type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case TYPE_CAMERA:
                root = inflateTargetView(R.layout.item_image_selector_camera, parent);
                holder = new CameraHolder(root);
                break;
            case TYPE_NORMAL:
                root = inflateTargetView(R.layout.item_image_selector_image, parent);
                holder = new ViewHolder(root);
                break;
        }
        return holder;
    }

    public View inflateTargetView(int id, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View temp;
        try {
            temp = inflater.inflate(id, parent, false);
        } catch (Exception e) {
//            Fresco.initialize(context, FrescoFactory.getImagePipelineConfig(context));
            return null;
        }
        return temp;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_CAMERA:
                break;
            case TYPE_NORMAL:
                ((ViewHolder) holder).bindData(imageBeen.get(position - 1));
                ((ViewHolder) holder).image.setTag(R.id.image, imageBeen.get(position - 1));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return showCamera ? imageBeen.size() + 1 : imageBeen.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView image;
        ImageView indicator;
        View mask;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (SimpleDraweeView) itemView.findViewById(R.id.image);
            image.setOnClickListener(clickListener);
 /*           ViewGroup.LayoutParams params = image.getLayoutParams();
            params.height = gridWidth * 2;
            params.width = gridWidth * 2;
            image.setLayoutParams(params);*/
            indicator = (ImageView) itemView.findViewById(R.id.checkMark);
            indicator.setImageResource(options().indicatorImageUnchecked);
            mask = itemView.findViewById(R.id.mask);
        }

        void bindData(final ImageBean data) {
            if (data == null) return;
            // 处理单选和多选状态
            if (showSelectIndicator) {
                indicator.setVisibility(View.VISIBLE);
                if (selectedImageBeen.contains(data)) {
                    // 设置选中状态
                    indicator.setImageResource(options().indicatorImageChecked);
                    mask.setVisibility(View.VISIBLE);
                } else {
                    // 未选择
                    indicator.setImageResource(options().indicatorImageUnchecked);
                    mask.setVisibility(View.GONE);
                }
            } else {
                indicator.setVisibility(View.GONE);
            }
            File imageFile = new File(data.path);
            if (imageFile.exists()) {
                // 显示图片
                ImageSelectorUtils.display(imageFile, image, context, gridWidth, gridWidth);
            }
        }
    }

    class CameraHolder extends RecyclerView.ViewHolder {
        TextView camera;

        public CameraHolder(View itemView) {
            super(itemView);
            camera = (TextView) itemView.findViewById(R.id.tv_camera);
            itemView.setOnClickListener(showCameraActionListener);
            camera.setOnClickListener(showCameraActionListener);
        }
    }
}
