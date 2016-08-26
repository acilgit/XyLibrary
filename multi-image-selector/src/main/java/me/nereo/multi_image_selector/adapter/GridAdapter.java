package me.nereo.multi_image_selector.adapter;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorFragment;
import me.nereo.multi_image_selector.R;
import me.nereo.multi_image_selector.bean.Image;
import me.nereo.multi_image_selector.utils.FrescoFactory;
import me.nereo.multi_image_selector.utils.ImageUtils;

/**
 * Created by Administrator on 2016/5/10.
 */
public class GridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //是否有相机
    private static final int TYPE_CAMERA = 1;
    private static final int TYPE_NORMAL = 0;
    private Context mContext;

    private LayoutInflater mInflater;
    //是否有相机
    private boolean showCamera = true;
    //是否是多选(是则显示指示器，否则不显示)
    private boolean showSelectIndicator = true;
    //展示的图片数据
    private List<Image> mImages = new ArrayList<>();
    //被选的图片数据
    private List<Image> mSelectedImages = new ArrayList<>();
    //每个视图的宽度
    final int mGridWidth;
    //用于点击item时的回调
    MultiImageSelectorFragment mFragment;
    public View.OnClickListener mShowCameraActionListener;
    public View.OnClickListener mClickListener;

    {
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFragment != null && v.getTag(R.id.image) != null) {
                    mFragment.selectImageFromGrid((Image) v.getTag(R.id.image), showSelectIndicator==true?MultiImageSelectorFragment.MODE_MULTI:MultiImageSelectorFragment.MODE_SINGLE);
                }
            }
        };
        mShowCameraActionListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("qqq", "showCameraAction:" + "mFragment" + (mFragment == null));
                if (mFragment != null) {
                    mFragment.showCameraAction();
                }
            }
        };
    }

    /**
     * 设置当前显示的图片的地址的列表
     *
     * @param images
     */
    public void setData(List<Image> images) {
        mSelectedImages.clear();
        if (images != null && images.size() > 0) {
            mImages = images;
        } else {
            mImages.clear();
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
     * @param image
     */
    public void select(Image image) {
        if (mSelectedImages.contains(image)) {
            mSelectedImages.remove(image);
        } else {
            mSelectedImages.add(image);
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
            Image image = getImageByPath(path);
            if (image != null) {
                mSelectedImages.add(image);
            }
        }
        if (mSelectedImages.size() > 0) {
            notifyDataSetChanged();
        }
    }

    private Image getImageByPath(String path) {
        if (mImages != null && mImages.size() > 0) {
            for (Image image : mImages) {
                if (image.path.equalsIgnoreCase(path)) {
                    return image;
                }
            }
        }
        return null;
    }


    public GridAdapter(Context context, boolean showCamera, int column, MultiImageSelectorFragment fragment) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.showCamera = showCamera;
        this.mFragment = fragment;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
        } else {
            width = wm.getDefaultDisplay().getWidth();
        }
        mGridWidth = width / column;
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
                root = inflateTargetView(R.layout.list_item_camera, parent);
                if (root == null) {
                    root = inflateTargetView(R.layout.list_item_camera, parent);
                }
                holder = new CameraHolder(root);
                break;
            case TYPE_NORMAL:
                root = inflateTargetView(R.layout.list_item_image, parent);
                if (root == null) {
                    root = inflateTargetView(R.layout.list_item_image, parent);
                }
                holder = new ViewHolder(root);
                break;
        }
        return holder;
    }

    public View inflateTargetView(int id, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View temp;
        try {
            temp = inflater.inflate(id, parent, false);
        } catch (Exception e) {
            Fresco.initialize(mContext, FrescoFactory.getImagePipelineConfig(mContext));
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
                ((ViewHolder) holder).bindData(mImages.get(position - 1));
                ((ViewHolder) holder).image.setTag(R.id.image, mImages.get(position - 1));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return showCamera ? mImages.size() + 1 : mImages.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView image;
        ImageView indicator;
        View mask;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (SimpleDraweeView) itemView.findViewById(R.id.image);
            image.setOnClickListener(mClickListener);
            ViewGroup.LayoutParams params = image.getLayoutParams();
            params.height = mGridWidth * 2;
            params.width = mGridWidth * 2;
            image.setLayoutParams(params);
            indicator = (ImageView) itemView.findViewById(R.id.checkmark);
            mask = itemView.findViewById(R.id.mask);
        }

        void bindData(final Image data) {
            if (data == null) return;
            // 处理单选和多选状态
            if (showSelectIndicator) {
                indicator.setVisibility(View.VISIBLE);
                if (mSelectedImages.contains(data)) {
                    // 设置选中状态
                    indicator.setImageResource(R.drawable.btn_selected);
                    mask.setVisibility(View.VISIBLE);
                } else {
                    // 未选择
                    indicator.setImageResource(R.drawable.btn_unselected);
                    mask.setVisibility(View.GONE);
                }
            } else {
                indicator.setVisibility(View.GONE);
            }
            File imageFile = new File(data.path);
            if (imageFile.exists()) {
                // 显示图片
                ImageUtils.display(imageFile,image,mContext,mGridWidth,mGridWidth);
            }
        }
    }

    class CameraHolder extends RecyclerView.ViewHolder {
        TextView mCamera;

        public CameraHolder(View itemView) {
            super(itemView);
            mCamera = (TextView) itemView.findViewById(R.id.tv_camera);
            itemView.setOnClickListener(mShowCameraActionListener);
            mCamera.setOnClickListener(mShowCameraActionListener);
        }
    }
}
