package com.xycode.xylibrary.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.RawRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.xycode.xylibrary.base.BaseItemView;
import com.xycode.xylibrary.instance.FrescoLoader;
import com.xycode.xylibrary.interfaces.Interfaces;
import com.xycode.xylibrary.uiKit.views.MultiImageView;
import com.xycode.xylibrary.uiKit.views.nicespinner.NiceSpinner;
import com.xycode.xylibrary.utils.DateUtils;
import com.xycode.xylibrary.utils.ImageUtils;
import com.xycode.xylibrary.utils.LogUtil.L;


/**
 * 自定义Holder
 * 可用于不同的View中
 */
public class CustomHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> viewList;
    private View itemView;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;

    /**
     * 动画展开的View的Id
     */
    private int expandViewId = 0;

    public CustomHolder(View view) {
        super(view);
        this.itemView = view;
        viewList = new SparseArray<>();
        createHolder(this);
    }

    /**
     * 创建时执行
     *
     * @param holder
     */
    protected void createHolder(CustomHolder holder) {

    }

    public <T extends View> T getView(int viewId) {
        View view = viewList.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            viewList.put(viewId, view);
        }
        if (view == null) L.d("getView null id:" + viewId);
        return (T) view;
    }

    public View getRootView() {
        return itemView;
    }

    public XAdapter getRecyclerViewXAdapter(int recyclerViewId) {
        RecyclerView rv = getView(recyclerViewId);
        if (rv != null) {
            RecyclerView.Adapter adapter = rv.getAdapter();
            if (adapter != null && adapter instanceof XAdapter) {
                return (XAdapter) adapter;
            }
        }
        return null;
    }

    public RecyclerView getRecyclerView(int recyclerViewId) {
        View rv = getView(recyclerViewId);
        if (rv != null && rv instanceof RecyclerView) {
            return (RecyclerView) rv;
        }
        return null;
    }

    public MultiImageView getMultiImageView(int multiImageViewId) {
        View v = getView(multiImageViewId);
        if (v != null && v instanceof MultiImageView) {
            return (MultiImageView) v;
        }
        return null;
    }

    public NiceSpinner getNiceSpinner(int niceSpinnerId) {
        View v = getView(niceSpinnerId);
        if (v != null && v instanceof NiceSpinner) {
            return (NiceSpinner) v;
        }
        return null;
    }

    public TextView getTextView(int textViewId) {
        View v = getView(textViewId);
        if (v != null && v instanceof TextView) {
            return (TextView) v;
        }
        return null;
    }

    public String getText(int textViewId) {
        View v = getView(textViewId);
        if (v != null && v instanceof TextView) {
            return ((TextView) v).getText().toString();
        }
        return null;
    }

    public SimpleDraweeView getSimpleDraweeView(int draweeViewId) {
        View v = getView(draweeViewId);
        if (v != null && v instanceof SimpleDraweeView) {
            return (SimpleDraweeView) v;
        }
        return null;
    }

    public BaseItemView getXItem(int viewId) {
        View rv = getView(viewId);
        if (rv != null && rv instanceof BaseItemView) {
            return (BaseItemView) rv;
        }
        return null;
    }

    public CustomHolder setText(int viewId, @StringRes int resText) {
        setTextForView(viewId, itemView.getResources().getString(resText));
        return this;
    }

    public CustomHolder setText(int viewId, Object text) {
        String string;
        if (text == null) {
            string = "";
        } else if (text instanceof String) {
            string = (String) text;
        } else {
            string = String.valueOf(text);
        }
        setTextForView(viewId, string);
        return this;
    }

    public CustomHolder setFormat(int viewId, int formatRes, Object... objects) {
        setTextForView(viewId, String.format(itemView.getContext().getString(formatRes), objects));
        return this;
    }

    public CustomHolder setDate(int viewId, String dateFormat, long dateTime) {
        setTextForView(viewId, DateUtils.formatDateTime(dateFormat, dateTime));
        return this;
    }

    private CustomHolder setTextForView(int viewId, String text) {
        View view = getView(viewId);
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setText(TextUtils.isEmpty(text) ? "" : text);
            }
        }
        return this;
    }

    public CustomHolder setTextColor(int viewId, int textColor) {
        View view = getView(viewId);
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(textColor);
            }
        }
        return this;
    }

    public CustomHolder setViewBackground(int viewId, int bgRes) {
        View view = getView(viewId);
        if (view != null) {
            (view).setBackgroundResource(bgRes);
        }
        return this;
    }

    public CustomHolder setImageUrl(int viewId, String url, ResizeOptions resizeOptions) {
        View view = getView(viewId);
        if (view != null) {
            if (view instanceof SimpleDraweeView) {
                ImageUtils.setImageUriWithPreview((SimpleDraweeView) view, url, FrescoLoader.getPreviewUri(url), resizeOptions);
            } else if (view instanceof ImageView) {
                ((ImageView) view).setImageURI(Uri.parse(url));
            }

        }
        return this;
    }

    /**
     * SimpleDrawee 请使用此方法
     * @param viewId
     * @param url
     * @return
     */
    public CustomHolder setImageUrl(int viewId, String url) {
        return setImageUrl(viewId, url, null);
    }

    @Deprecated
    public CustomHolder setImageURI(int viewId, String url) {
        return setImageUrl(viewId, url);
    }

    private CustomHolder setImageURI(int viewId, Uri uri) {
        View view = getView(viewId);
        if (view != null) {
            if (view instanceof SimpleDraweeView) {
                ((SimpleDraweeView) view).setImageURI(uri);
//                    ImageUtils.setImageUriWithPreview((SimpleDraweeView) view, uri, FrescoLoader.getPreviewUri(uri.));
            } else if (view instanceof ImageView) {
                ((ImageView) view).setImageURI(uri);
            }
        }
        return this;
    }

    public CustomHolder setImageBitmap(int viewId, Bitmap bitmap) {
        View view = getView(viewId);
        if (view != null) {
            if (view instanceof ImageView) {
                ((ImageView) view).setImageBitmap(bitmap);
            }
        }
        return this;
    }

    public CustomHolder setImageRes(int viewId, @DrawableRes int drawableRes) {
        View view = getView(viewId);
        if (view != null) {
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(drawableRes);
            }
        }
        return this;
    }

    public CustomHolder setClick() {
        itemView.setOnClickListener(onClickListener);
        return this;
    }

    public CustomHolder setClick(int viewId) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnClickListener(onClickListener);
        }
        return this;
    }

    public CustomHolder setClick(int viewId, View.OnClickListener clickListener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnClickListener(clickListener);
        }
        return this;
    }

    public CustomHolder setLongClick(int viewId) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnLongClickListener(onLongClickListener);
        }
        return this;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public CustomHolder setEnable(int viewId, boolean enable) {
        View view = getView(viewId);
        if (view != null) {
            view.setEnabled(enable);
        }
        return this;
    }

    public CustomHolder setSelected(int viewId, boolean selected) {
        View view = getView(viewId);
        if (view != null) {
            view.setSelected(selected);
        }
        return this;
    }

    public CustomHolder setVisibility(int viewId, int visibility) {
        View view = getView(viewId);
        if (view != null) {
            view.setVisibility(visibility);
        }
        return this;
    }

    public CustomHolder hideView(int viewId, boolean isHidden) {
        return setVisibility(viewId, isHidden ? View.GONE : View.VISIBLE);
    }

    public CustomHolder showView(int viewId) {
        return setVisibility(viewId, View.VISIBLE);
    }

    /**
     * 设置动画展开的view, 分3步
     * 1、请在creatingHolder时设置 setExpandViewId
     * 2、bindingHolder时，setExpand(animate, false)
     * 3、onClick时，setExpand(animate, true)
     *
     * @param expandViewId
     */
    public void setExpandViewId(@IdRes int expandViewId) {
        this.expandViewId = expandViewId;
    }

    View getExpandView() {
        return expandViewId == 0 ? null : getView(expandViewId);
    }

    public void setExpand(boolean toExpand, final boolean animate) {
        setExpand(toExpand, animate, null);
    }

    /**
     * 如果在动画结束时需要NotifyDatasetChange可以在onAnimationEndListener中设置
     *
     * @param toExpand
     * @param animate
     * @param onAnimationEndListener
     */
    public void setExpand(boolean toExpand, final boolean animate, Interfaces.CB onAnimationEndListener) {
        View expandView = getExpandView();
        if (expandView == null) {
            return;
        }
        if (toExpand) {
            if (animate) {
                expandView.setVisibility(View.VISIBLE);
                final Animator animator = ViewHolderAnimator.ofItemViewHeight(this);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (onAnimationEndListener != null) onAnimationEndListener.go(null);
                    }
                });
                animator.start();
            } else {
                expandView.setVisibility(View.VISIBLE);
            }
        } else {
            if (animate) {
                expandView.setVisibility(View.GONE);
                final Animator animator = ViewHolderAnimator.ofItemViewHeight(this);
                expandView.setVisibility(View.VISIBLE);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        expandView.setVisibility(View.GONE);
                        if (onAnimationEndListener != null) onAnimationEndListener.go(null);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        expandView.setVisibility(View.GONE);
                    }
                });
                animator.start();
            } else {
                expandView.setVisibility(View.GONE);
            }
        }
    }
}
