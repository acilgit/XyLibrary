package com.xycode.xylibrary.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xycode.xylibrary.R;

/**
 * Created by XY on 2016-08-08.
 */
public abstract class BaseItemView extends RelativeLayout {

    private SparseArray<View> viewList;

    protected int itemType;
    protected int itemIcon;
    protected int itemColor;
    protected int itemRes;
    protected int itemNum;
    protected int itemCount;
    protected int itemVisible;
    protected float itemFloat;
    protected String itemName;
    protected String itemContent;
    protected String itemDetail;
    protected String itemDescription;

    protected int layoutId = R.layout.layout_blank;

    public BaseItemView(Context context) {
        super(context, null);
    }

    public BaseItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        viewList = new SparseArray<>();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BaseItemView);
        itemType = a.getInt(R.styleable.BaseItemView_itemType, 0);
        itemNum = a.getInt(R.styleable.BaseItemView_itemNum, 0);
        itemCount = a.getInt(R.styleable.BaseItemView_itemCount, 0);
        itemVisible = a.getInt(R.styleable.BaseItemView_itemVisible, VISIBLE);
        itemFloat = a.getFloat(R.styleable.BaseItemView_itemFloat, 0);
        itemIcon = a.getResourceId(R.styleable.BaseItemView_itemIcon, 0);
        itemColor = a.getResourceId(R.styleable.BaseItemView_itemColor, 0);
        itemRes = a.getResourceId(R.styleable.BaseItemView_itemRes, 0);
        itemName = a.getString(R.styleable.BaseItemView_itemName);
        itemContent = a.getString(R.styleable.BaseItemView_itemContent);
        itemDetail = a.getString(R.styleable.BaseItemView_itemDetail);
        itemDescription = a.getString(R.styleable.BaseItemView_itemDescription);
        itemName = itemName == null ? "" : itemName;
        itemContent = itemContent == null ? "" : itemContent;
        itemDetail = itemDetail == null ? "" : itemDetail;
        itemDescription = itemDescription == null ? "" : itemDescription;

        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
    }


    public <T extends View> T getView(int viewId) {
        View view = viewList.get(viewId);
        if (view == null) {
            view = findViewById(viewId);
            viewList.put(viewId, view);
        }
        return (T) view;
    }

    public BaseItemView setText(int viewId, @StringRes int textRes) {
        setText(viewId, getContext().getString(textRes));
        return this;
    }

    public BaseItemView setText(int viewId, String text) {
        View view = getView(viewId);
        if (view != null) {
            if (view instanceof EditText) {
                ((EditText) view).setText(text);
            } else if (view instanceof Button) {
                ((Button) view).setText(text);
            } else if (view instanceof TextView) {
                ((TextView) view).setText(text);
            }
        }
        return this;
    }

    public BaseItemView setImageUrl(int viewId, String url) {
        View view = getView(viewId);
        if (view != null) {
            if (view instanceof SimpleDraweeView) {
                ((SimpleDraweeView) view).setImageURI(Uri.parse(url));
            } else if (view instanceof ImageView) {
                ((ImageView) view).setImageURI(Uri.parse(url));
            }
        }
        return this;
    }

    public BaseItemView setImageURI(int viewId, Uri uri) {
        View view = getView(viewId);
        if (view != null) {
            if (view instanceof SimpleDraweeView) {
                ((SimpleDraweeView) view).setImageURI(uri);
            } else if (view instanceof ImageView) {
                ((ImageView) view).setImageURI(uri);
            }
        }
        return this;
    }

    public BaseItemView setImageBitmap(int viewId, Bitmap bitmap) {
        View view = getView(viewId);
        if (view != null) {
            if (view instanceof ImageView) {
                ((ImageView) view).setImageBitmap(bitmap);
            }
        }
        return this;
    }

    public BaseItemView setImageRes(int viewId, @DrawableRes int drawableRes) {
        View view = getView(viewId);
        if (view != null) {
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(drawableRes);
            }
        }
        return this;
    }

    public BaseItemView setClick(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnClickListener(listener);
        }
        return this;
    }

    public BaseItemView setLongClick(int viewId, View.OnLongClickListener listener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnLongClickListener(listener);
        }
        return this;
    }

    public BaseItemView setEnable(int viewId, boolean enable) {
        View view = getView(viewId);
        if (view != null) {
            view.setEnabled(enable);
        }
        return this;
    }

    public BaseItemView setVisibilility(int viewId, int visibilility) {
        View view = getView(viewId);
        if (view != null) {
            view.setVisibility(visibilility);
        }
        return this;
    }

    protected abstract int getLayoutId();
}
