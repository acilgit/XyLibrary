package com.xycode.xylibrary.uiKit.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
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

import java.util.ArrayList;
import java.util.List;

public class MultiImageView extends LinearLayout {
    public static int MAX_WIDTH = 0;

    private List<String> imagesList;

    private List<SimpleDraweeView> imageViewList;

    /**
     * unit Pixel
     **/

    private float pxOneMaxAspectRatio = 1f;
    private int pxOneMaxWandHeight;  // single max width
    private int pxOneMaxWandWidth;  // single max width
    private int pxMoreWandSide = 0;// multi max width
    private int pxImagePadding = (int) Math.ceil(Tools.dp2px(getContext(), 3.0f));// image padding

    private int MAX_PER_ROW_COUNT = 3;// max count in one row

    private int att_maxRow = 3;

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
    private boolean att_itemSameSize = false;
    private boolean att_largeSingleImage = false;

    private int att_roundedCornerRadius = -1;

    private OnItemClickListener onItemClickListener;
    private OnImageLoadListener imageLoadListener = null;
    private OnImageOverlayListener imageOverlayListener = null;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MultiImageView(Context context) {
        super(context);
    }

    public MultiImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        imageViewList = new ArrayList<>();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MultiImageView);

        att_itemSameSize = typedArray.getBoolean(R.styleable.MultiImageView_itemSameSize, false);
        att_largeSingleImage = typedArray.getBoolean(R.styleable.MultiImageView_largeSingleImage, false);
        att_maxRow = typedArray.getInt(R.styleable.MultiImageView_maxRow, 3);
        att_actualScale = typedArray.getInt(R.styleable.MultiImageView_imageScaleType, -1);
        att_failureScale = typedArray.getInt(R.styleable.MultiImageView_onFailureImageScaleType, -1);
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
        if (att_imagePadding != -1) pxImagePadding = att_imagePadding;
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
            if (!att_itemSameSize && (lists.size() == 2 || lists.size() == 4)) {
                pxMoreWandSide = (MAX_WIDTH - pxImagePadding) / 2; // solve right image align problem
            } else {
                pxMoreWandSide = (MAX_WIDTH - pxImagePadding * 2) / 3; // solve right image align problem

            }
            pxOneMaxWandWidth = att_largeSingleImage ? MAX_WIDTH : MAX_WIDTH * 2 / 3;
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

        onePicPara = new LayoutParams(pxOneMaxWandWidth, /*pxOneMaxWandHeight > 0 ? pxOneMaxWandHeight : pxOneMaxWandWidth*/ wrap);

        moreParaColumnFirst = new LayoutParams(pxMoreWandSide, pxMoreWandSide);

        morePara = new LayoutParams(pxMoreWandSide, pxMoreWandSide);
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
        if (att_maxRow != -1 && allCount > (att_maxRow * 3)) allCount = att_maxRow * 3;
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
        if (imagesList.size() == 1) {
            imageView.setAspectRatio(pxOneMaxAspectRatio);
        }
        imageViewList.add(imageView);
        if (isMultiImage) {
            imageView.setLayoutParams(position % MAX_PER_ROW_COUNT == 0 ? moreParaColumnFirst : morePara);
        } else {
            imageView.setAdjustViewBounds(true);
            imageView.setMaxHeight(pxOneMaxWandWidth);
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
                if (att_placeHolder != -1)
                    hierarchyBuilder.setPlaceholderImage(getResources().getDrawable(att_placeHolder));
                if (att_failureHolder != -1) {
                    if (att_failureScale != -1) {
                        hierarchyBuilder.setFailureImage(getResources().getDrawable(att_failureHolder), failureScale);
                    } else {
                        hierarchyBuilder.setFailureImage(getResources().getDrawable(att_failureHolder));
                    }
                }
                if (att_pressedOverlayHolder != -1)
                    hierarchyBuilder.setPressedStateOverlay(getResources().getDrawable(att_pressedOverlayHolder));
                if (att_roundedCornerRadius != -1)
                    hierarchyBuilder.setRoundingParams(new RoundingParams().setCornersRadius(att_roundedCornerRadius));
                if (imageOverlayListener != null) {
                    Drawable drawable = imageOverlayListener.setOverlayDrawable(position);
                    if (drawable != null) hierarchyBuilder.setOverlay(drawable);
                }
            }
        });

        ImageUtils.setImageUriWithPreview(imageView, Uri.parse(url), previewUri);
        imageView.setId(url.hashCode());
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, position);
                }
            }
        });
        return imageView;
    }

    public void setSingleImageRatio(float aspectRatio) {
        pxOneMaxAspectRatio = aspectRatio;
    }

    public void setLoadImageListener(OnImageLoadListener imageLoadListener) {
        this.imageLoadListener = imageLoadListener;
    }

    public void setOverlayDrawableListener(OnImageOverlayListener imageOverlayListener) {
        this.imageOverlayListener = imageOverlayListener;
    }

    public List<String> getDraweeViewList() {
        return imagesList;
    }

    public interface OnImageLoadListener {
        Uri setPreviewUri(int position);
    }

    public interface OnImageOverlayListener {
        Drawable setOverlayDrawable(int position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}