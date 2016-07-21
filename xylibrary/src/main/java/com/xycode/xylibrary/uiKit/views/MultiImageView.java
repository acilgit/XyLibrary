package com.xycode.xylibrary.uiKit.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xycode.xylibrary.R;
import com.xycode.xylibrary.utils.ImageUtils;
import com.xycode.xylibrary.utils.Tools;

import java.util.List;

/**
 * @author shoyu
 * @ClassName MultiImageView.java
 * @Description: show 1~N image View
 */

public class MultiImageView extends LinearLayout {
    public static int MAX_WIDTH = 0;

    // 照片的Url列表
    private List<String> imagesList;

    /**
     * 长度 单位为Pixel
     **/
    private int pxOneMaxWandHeight;  // single max width
    private int pxMoreWandHeight = 0;// multi max width
    private int pxImagePadding = (int) Tools.dp2px(getContext(), 3.0f);// image padding

    private int MAX_PER_ROW_COUNT = 3;// max count in one row

    private LayoutParams onePicPara;
    private LayoutParams morePara, moreParaColumnFirst;
    private LayoutParams rowPara;

    private ScalingUtils.ScaleType actualScale;
    private ScalingUtils.ScaleType failureScale;

    private int att_actualScale = -1;
    private int att_failureScale = -1;

    private int att_placeHolder = -1;
    private int att_failureHolder = -1;
    private int att_pressedOverlayHolder = -1;

    private int att_imagePadding = -1;
    private int att_roundedCornerRadius = -1;

    private OnItemClickListener onItemClickListener;
    private OnImageLoadListener imageLoadListener = null;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MultiImageView(Context context) {
        super(context);
    }

    public MultiImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_refresher, this, true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XRefresher);

        att_actualScale =  typedArray.getInteger(R.styleable.MultiImageView_imageScaleType, -1);
        att_failureScale = typedArray.getInteger(R.styleable.MultiImageView_onFailureImageScaleType, -1);
        att_placeHolder = typedArray.getResourceId(R.styleable.MultiImageView_holderImage, -1);
        att_failureHolder = typedArray.getResourceId(R.styleable.MultiImageView_onFailureImage, -1);
        att_pressedOverlayHolder = typedArray.getResourceId(R.styleable.MultiImageView_pressedOverlayImage, -1);
        att_imagePadding = typedArray.getDimensionPixelSize(R.styleable.MultiImageView_imagePadding, -1);
        att_roundedCornerRadius = typedArray.getDimensionPixelSize(R.styleable.MultiImageView_cornerRadius, -1);

        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        actualScale = checkScaleType(att_actualScale);
        failureScale = checkScaleType(att_failureScale);
    }

    private ScalingUtils.ScaleType checkScaleType(int att_scaleType) {
        switch (att_scaleType) {
            case 0:
                return ScalingUtils.ScaleType.FIT_XY;
            case 1:
                return ScalingUtils.ScaleType.FIT_START;
            case 2:
                return ScalingUtils.ScaleType.FIT_CENTER;
            case 3:
                return ScalingUtils.ScaleType.FIT_END;
            case 4:
                return ScalingUtils.ScaleType.CENTER;
            case 5:
                return ScalingUtils.ScaleType.CENTER_INSIDE;
            case 6:
                return ScalingUtils.ScaleType.CENTER_CROP;
            case 7:
                return ScalingUtils.ScaleType.FOCUS_CROP;
        }
        return ScalingUtils.ScaleType.FIT_CENTER;
    }



    public void setList(List<String> lists) throws IllegalArgumentException {
        if (lists == null) {
            throw new IllegalArgumentException("imageList is null...");
        }
        imagesList = lists;

        if (MAX_WIDTH > 0) {
            pxMoreWandHeight = (MAX_WIDTH - pxImagePadding * 2) / 3; // solve right image align problem
            pxOneMaxWandHeight = MAX_WIDTH * 2 / 3;
            initImageLayoutParams();
        }

        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MAX_WIDTH == 0) {
            int width = measureWidth(widthMeasureSpec);
            if (width > 0) {
                MAX_WIDTH = width;
                if (imagesList != null && imagesList.size() > 0) {
                    setList(imagesList);
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            // result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
            // + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by
                // measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private void initImageLayoutParams() {
        int wrap = LayoutParams.WRAP_CONTENT;
        int match = LayoutParams.MATCH_PARENT;

        onePicPara = new LayoutParams(pxOneMaxWandHeight, wrap);

        moreParaColumnFirst = new LayoutParams(pxMoreWandHeight, pxMoreWandHeight);
        morePara = new LayoutParams(pxMoreWandHeight, pxMoreWandHeight);
        morePara.setMargins(pxImagePadding, 0, 0, 0);

        rowPara = new LayoutParams(match, wrap);
    }

    // according to imageView count to View Layout, and set View click listener
    private void initView() {
        this.setOrientation(VERTICAL);
        this.removeAllViews();
        if (MAX_WIDTH == 0) {
            // to onMeasure() MultiImageView max width，MultiImageView width set match_parent
            addView(new View(getContext()));
            return;
        }

        if (imagesList == null || imagesList.size() == 0) {
            return;
        }

        if (imagesList.size() == 1) {
            addView(createImageView(0, false));
            return;
        }
        int allCount = imagesList.size();
        if (allCount == 2 || allCount == 4) {
            MAX_PER_ROW_COUNT = 2;
        } else {
            MAX_PER_ROW_COUNT = 3;
        }
        int rowCount = allCount / MAX_PER_ROW_COUNT + (allCount % MAX_PER_ROW_COUNT > 0 ? 1 : 0);
        for (int rowCursor = 0; rowCursor < rowCount; rowCursor++) {
            LinearLayout rowLayout = new LinearLayout(getContext());
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            rowLayout.setLayoutParams(rowPara);
            if (rowCursor != 0) {
                rowLayout.setPadding(0, pxImagePadding, 0, 0);
            }

            int columnCount = allCount % MAX_PER_ROW_COUNT == 0 ? MAX_PER_ROW_COUNT : allCount % MAX_PER_ROW_COUNT;
            if (rowCursor != rowCount - 1) {
                columnCount = MAX_PER_ROW_COUNT;
            }
            addView(rowLayout);

            int rowOffset = rowCursor * MAX_PER_ROW_COUNT;// offset
            for (int columnCursor = 0; columnCursor < columnCount; columnCursor++) {
                int position = columnCursor + rowOffset;
                rowLayout.addView(createImageView(position, true));
            }
        }

    }

    private ImageView createImageView(final int position, final boolean isMultiImage) {
        String url = imagesList.get(position);
        SimpleDraweeView imageView;

        imageView = new SimpleDraweeView(getContext());
        if (isMultiImage) {
            imageView.setLayoutParams(position % MAX_PER_ROW_COUNT == 0 ? moreParaColumnFirst : morePara);
        } else {
            imageView.setAdjustViewBounds(true);
            imageView.setMaxHeight(pxOneMaxWandHeight);
            imageView.setLayoutParams(onePicPara);
        }
        Uri previewUri = null;
        if (imageLoadListener != null) {
            previewUri = imageLoadListener.setPreviewUri(position);
        }

        ImageUtils.setSimpleDraweeParams(imageView, new ImageUtils.ISetDraweeHierarchy() {
            @Override
            public void setHierarchyBuilder(GenericDraweeHierarchyBuilder hierarchyBuilder) {
                if (att_actualScale != -1) hierarchyBuilder.setActualImageScaleType(actualScale);
                if (att_placeHolder != -1) hierarchyBuilder.setPlaceholderImage(getResources().getDrawable(att_placeHolder) );
                if (att_failureHolder != -1) {
                    if (att_failureScale != -1) {
                        hierarchyBuilder.setFailureImage(getResources().getDrawable(att_failureHolder) ,failureScale);
                    } else {
                        hierarchyBuilder.setFailureImage(getResources().getDrawable(att_failureHolder));
                    }
                }
                if (att_pressedOverlayHolder != -1) hierarchyBuilder.setPressedStateOverlay(getResources().getDrawable(att_pressedOverlayHolder) );
                if (att_roundedCornerRadius != -1) hierarchyBuilder.setRoundingParams(new RoundingParams().setCornersRadius(att_roundedCornerRadius));
            }
        });
        if (att_imagePadding != -1) imageView.setPadding(att_imagePadding, att_imagePadding,att_imagePadding,att_imagePadding);
        ImageUtils.setImageUriWithPreview(imageView, Uri.parse(url), previewUri);
        imageView.setId(url.hashCode());
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, position);
            }
        });
        return imageView;
    }

    public interface OnImageLoadListener {
        Uri setPreviewUri(int position);
    }

    public void setLoadImageListener(OnImageLoadListener imageLoadListener) {
        this.imageLoadListener = imageLoadListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}