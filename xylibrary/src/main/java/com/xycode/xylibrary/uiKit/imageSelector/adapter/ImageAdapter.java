package com.xycode.xylibrary.uiKit.imageSelector.adapter;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xycode.xylibrary.R;
import com.xycode.xylibrary.uiKit.imageSelector.MultiImageSelectorActivity;
import com.xycode.xylibrary.uiKit.imageSelector.MultiImageSelectorFragment;
import com.xycode.xylibrary.uiKit.imageSelector.bean.ImageBean;
import com.xycode.xylibrary.uiKit.imageSelector.utils.ImageSelectorUtils;
import com.xycode.xylibrary.utils.LogUtil.L;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.xycode.xylibrary.uiKit.imageSelector.ImageSelectorOptions.options;


/**
 * Created by Administrator on 2016/5/10.
 */
public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_CAMERA = 1;
    private static final int TYPE_NORMAL = 0;
    private Context context;

    private LayoutInflater inflater;
    private boolean showCamera = true;
    private boolean showSelectIndicator = true;
    private List<ImageBean> imageBeen = new ArrayList<>();
    private List<ImageBean> selectedImageBeen = new ArrayList<>();
    final int gridWidth;
    MultiImageSelectorFragment fragment;
    public View.OnClickListener showCameraActionListener;
    public View.OnClickListener clickListener;

    {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment != null && v.getTag(R.id.image) != null) {
                    fragment.selectImageFromGrid((ImageBean) v.getTag(R.id.image), showSelectIndicator == true ? MultiImageSelectorActivity.MODE_MULTI : MultiImageSelectorActivity.MODE_SINGLE);
                }
            }
        };
        showCameraActionListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.e("qqq", "showCameraAction:" + "fragment" + (fragment == null));
                if (fragment != null) {
                    fragment.showCameraAction();
                }
            }
        };
    }

    /**
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
     *
     * @param b
     */
    public void showSelectIndicator(boolean b) {
        showSelectIndicator = b;
    }

    /**
     *
     * @param b
     */
    public void setShowCamera(boolean b) {
        if (showCamera == b) return;

        showCamera = b;
        notifyDataSetChanged();
    }

    /**
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
     *
     * @param resultList
     */
    public void setDefaultSelected(List<String> resultList) {
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


    public ImageAdapter(Context context, boolean showCamera, int column, MultiImageSelectorFragment fragment) {
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
            if (showSelectIndicator) {
                indicator.setVisibility(View.VISIBLE);
                if (selectedImageBeen.contains(data)) {
                    indicator.setImageResource(options().indicatorImageChecked);
                    mask.setVisibility(View.VISIBLE);
                } else {
                    indicator.setImageResource(options().indicatorImageUnchecked);
                    mask.setVisibility(View.GONE);
                }
            } else {
                indicator.setVisibility(View.GONE);
            }
            File imageFile = new File(data.path);
            if (imageFile.exists()) {
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
