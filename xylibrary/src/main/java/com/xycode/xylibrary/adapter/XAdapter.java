package com.xycode.xylibrary.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.xycode.xylibrary.R;
import com.xycode.xylibrary.base.BaseItemView;
import com.xycode.xylibrary.instance.FrescoLoader;
import com.xycode.xylibrary.uiKit.views.MultiImageView;
import com.xycode.xylibrary.uiKit.views.nicespinner.NiceSpinner;
import com.xycode.xylibrary.unit.ViewTypeUnit;
import com.xycode.xylibrary.utils.DateUtils;
import com.xycode.xylibrary.utils.ImageUtils;
import com.xycode.xylibrary.xRefresher.XRefresher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiu on 2016/4/3.
 */
public abstract class XAdapter<T> extends RecyclerView.Adapter {

    public static final int LAYOUT_FOOTER = -20331;

    /**
     * 所有数据，包括被过滤的数据
     */
    private List<T> mainList;
    /**
     * 显示的数据
     */
    private List<T> dataList;
    private Context context;
    private SparseArray<Integer> headerLayoutIdList;
    private Map<Integer, ViewTypeUnit> multiLayoutMap;
    // item long click on long click Listener
//    private OnItemClickListener onItemClickListener;
//    private OnItemLongClickListener onItemLongClickListener;

    private int footerLayout = 0;

    /**
     * use single Layout
     *
     * @param context
     * @param dataList
     */
    public XAdapter(Context context, List<T> dataList) {
        init(context, dataList);
    }

    private void init(Context context, List<T> dataList) {
        this.context = context;
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        this.dataList = new ArrayList<>();
        this.mainList = dataList;
        this.dataList.addAll(setFilterForAdapter(mainList));
        this.headerLayoutIdList = new SparseArray<>();
        this.multiLayoutMap = new HashMap<>();
    }

    /**
     * only do once method
     * or
     * not use immediately adapterPosition method, like clickListener
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        // Footer创建
        if (viewType == LAYOUT_FOOTER) {
            View itemView = LayoutInflater.from(context).inflate(footerLayout, parent, false);
            final CustomHolder holder = new CustomHolder(itemView) {
                @Override
                protected void createHolder(final CustomHolder holder) {
                    holder.onClickListener = v -> handleItemViewClick(holder, null, v.getId(), new ViewTypeUnit(viewType, footerLayout));
                    holder.onLongClickListener = v -> handleItemViewLongClick(holder, null, v.getId(), new ViewTypeUnit(viewType, footerLayout));
                    creatingFooter(holder);
                }
            };
            return holder;
        } else {
            // Header创建
            for (int i = 0; i < headerLayoutIdList.size(); i++) {
                final int headerKey = headerLayoutIdList.keyAt(i);
                if (viewType == headerKey) {
                    View itemView = LayoutInflater.from(context).inflate(headerLayoutIdList.get(headerKey), parent, false);
                    final CustomHolder holder = new CustomHolder(itemView) {
                        @Override
                        protected void createHolder(final CustomHolder holder) {
                            holder.onClickListener = v -> handleItemViewClick(holder, null, v.getId(), new ViewTypeUnit(headerKey, headerLayoutIdList.get(headerKey)));

                            holder.onLongClickListener = v -> handleItemViewLongClick(holder, null, v.getId(), new ViewTypeUnit(headerKey, headerLayoutIdList.get(headerKey)));
                            creatingHeader(holder, headerKey);
                        }
                    };
                    return holder;
                }
            }
            int l = R.layout.layout_blank;
            final ViewTypeUnit viewTypeUnit = multiLayoutMap.get(viewType);
            if (viewTypeUnit != null) {
                l = viewTypeUnit.getLayoutId();
            }
            @LayoutRes int layoutId = l;
            View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            final CustomHolder holder = new CustomHolder(itemView) {
                @Override
                protected void createHolder(final CustomHolder holder) {
                    holder.onClickListener = v -> handleItemViewClick(holder, dataList.get(holder.getAdapterPosition() - getHeaderCount()), v.getId(), viewTypeUnit);

                    holder.onLongClickListener = v -> handleItemViewLongClick(holder, dataList.get(holder.getAdapterPosition() - getHeaderCount()), v.getId(), viewTypeUnit);
                    creatingHolder(holder, dataList, viewTypeUnit);
                }
            };
            return holder;
        }
    }

    /**
     * 展示
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == dataList.size() + headerLayoutIdList.size()) {
            bindingFooter(((CustomHolder) holder));
            return;
        } else if (position < headerLayoutIdList.size()) {
            for (int i = 0; i < headerLayoutIdList.size(); i++) {
                final int headerKey = headerLayoutIdList.keyAt(i);
                if (getItemViewType(position) == headerKey) {
                    bindingHeader(((CustomHolder) holder), headerKey);
                }
            }
            return;
        }
        bindingHolder(((CustomHolder) holder), dataList, position - headerLayoutIdList.size());
    }

    /**
     * 创建Holder时执行，只执行一次或被销毁后再次会被执行
     *
     * @param holder
     * @param dataList
     * @param viewTypeUnit
     */
    public void creatingHolder(CustomHolder holder, List<T> dataList, ViewTypeUnit viewTypeUnit) {

    }

    /**
     * 绑定展示数据时执行，多次执行
     *
     * @param holder
     * @param dataList
     * @param pos
     */
    public void bindingHolder(CustomHolder holder, List<T> dataList, int pos) {

    }

 /*   public int getLoadMoreState() {
        return loadMoreState;
    }

    public void setLoadMoreState(int loadMoreState) {
        this.loadMoreState = loadMoreState;
        notifyDataSetChanged();
    }*/

    /**
     * 根据Mark确定展示的Layout
     * override this method can show different holder for layout
     * don't return LAYOUT_FOOTER = -20331
     *
     * @param item
     * @return
     */
    protected ViewTypeUnit getViewTypeUnitForLayout(T item) {
        return null;
    }

    /**
     * when you use layout list, you can override this method when binding holder views
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        int headerCount = headerLayoutIdList.size();
        if (position < headerCount) {
            return headerLayoutIdList.keyAt(position);
        }
        if (position == dataList.size() + headerCount) {
            return LAYOUT_FOOTER;
        }

        ViewTypeUnit viewTypeUnit = getViewTypeUnitForLayout(dataList.get(position - headerCount));
        if (viewTypeUnit == null) {
            try {
                throw new Exception("XAdapter doesn't override getViewTypeUnitForLayout or set the dataList as null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Integer key : multiLayoutMap.keySet()) {
            if (multiLayoutMap.get(key).getMark().equals(viewTypeUnit.getMark())) {
                return key.intValue();
            }
        }
        int viewType = multiLayoutMap.size() + 10000;
        multiLayoutMap.put(viewType, viewTypeUnit);
        return viewType;
    }

    /**
     * Please use getDataList().size() to get items count，this method would add header and Footer if they exist
     *
     * @return
     */
    @Override
    public int getItemCount() {
        int footerCount = footerLayout == 0 ? 0 : 1;
        int headerCount = headerLayoutIdList.size();
        if (dataList != null) {
            return dataList.size() + footerCount + headerCount;
        }
        return footerCount + headerCount;
    }

    public T getItem(int pos) {
        if (dataList.size() > pos && pos > 0) {
            return dataList.get(pos);
        }
        return null;
    }

    public boolean hasFooter() {
        return footerLayout != 0;
    }


    public int getHeaderCount() {
        return headerLayoutIdList.size();
    }

    public int getHeaderPos(int headerKey) {
        return headerLayoutIdList.indexOfKey(headerKey);
    }

    public int getLayoutId(String mark) {
        for (ViewTypeUnit vt : multiLayoutMap.values()) {
            if (vt.getMark().equals(mark)) {
                return vt.getLayoutId();
            }
        }
        return 0;
    }

    public List<T> getDataList() {
        return mainList;
    }

    public List<T> getFilteredList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        mainList.clear();
        mainList.addAll(dataList);
        this.dataList.clear();
        this.dataList.addAll(setFilterForAdapter(mainList));
        notifyDataSetChanged();
    }

    public void resetDataList() {
        this.dataList.clear();
        this.dataList.addAll(setFilterForAdapter(mainList));
        notifyDataSetChanged();
    }

    public void removeItem(int pos) {
        T item = dataList.get(pos);
        mainList.remove(item);
        dataList.remove(pos);
        notifyItemRemoved(headerLayoutIdList.size() + pos);
    }

    /**
     * this method can only use in no filter mode
     *
     * @param pos
     * @param item
     */
    public void addItemNoFilter(int pos, T item) {
        dataList.add(pos, item);
        mainList.add(pos, item);
        notifyItemInserted(headerLayoutIdList.size() + pos);
    }

    public void updateItem(int pos, T item) {
        T itemOld = dataList.get(pos);
        int mainPos = mainList.indexOf(itemOld);
        mainList.set(mainPos, item);
        dataList.set(pos, item);
        notifyItemChanged(headerLayoutIdList.size() + pos);
    }

    public void addItem(T item) {
        dataList.add(item);
        mainList.add(item);
        notifyItemInserted(getItemCount() - (footerLayout == 0 ? 1 : 2));
    }

  /*  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (this.onItemClickListener != null) this.onItemClickListener = null;
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        if (this.onItemLongClickListener != null) this.onItemLongClickListener = null;
        this.onItemLongClickListener = onItemLongClickListener;
    }*/

    public void setFooter(@LayoutRes int footerLayout) {
        this.footerLayout = footerLayout;
    }

    public void addHeader(@LayoutRes int headerLayoutId) {
        addHeader(XRefresher.HEADER_ONE, headerLayoutId);
    }

    public void addHeader(int headerKey, @LayoutRes int headerLayoutId) {
        headerLayoutIdList.put(headerKey, headerLayoutId);
    }

    protected void creatingHeader(CustomHolder holder, int headerKey) {

    }

    protected void bindingHeader(CustomHolder holder, int headerKey) {

    }

    protected void creatingFooter(CustomHolder holder) {

    }

    protected void bindingFooter(CustomHolder holder) {

    }

    /**
     * override this method to add holder rootView onclick event，when handle over continue to on ClickListener in creating holder set.
     * some view if it override Touch method and did't return，can let it no use,  eg：RippleView
     *
     * @param holder
     * @param item
     * @param viewTypeUnit
     */
    protected void handleItemViewClick(CustomHolder holder, T item, int viewId, ViewTypeUnit viewTypeUnit) {

    }

    protected boolean handleItemViewLongClick(CustomHolder holder, T item, int viewId, ViewTypeUnit viewTypeUnit) {
        return false;
    }

    /**
     * filter local main data list, it can use any time, it won't change the main data list.
     *
     * @param mainList
     * @return
     */
    protected List<T> setFilterForAdapter(List<T> mainList) {
        List<T> list = new ArrayList<>();
        list.addAll(mainList);
        return list;
    }

  /*  *//**
     *
     *//*
    public interface OnItemClickListener<T> {
        void onItemClick(CustomHolder holder, T item);
    }

    */

    /**
     *
     *//*
    public interface OnItemLongClickListener<T> {
        void onItemLongClick(CustomHolder holder, T item);
    }*/

    /**
     * 自定义Holder
     * 可用于不同的View中
     */
    public static class CustomHolder extends RecyclerView.ViewHolder {

        private SparseArray<View> viewList;
        private View itemView;
        private View.OnClickListener onClickListener;
        private View.OnLongClickListener onLongClickListener;

        public CustomHolder(View view) {
            super(view);
            this.itemView = view;
            viewList = new SparseArray<>();
            createHolder(this);
        }

        /**
         * 创建时执行
         * @param holder
         */
        protected void createHolder(CustomHolder holder){

        }

        public <T extends View> T getView(int viewId) {
            View view = viewList.get(viewId);
            if (view == null) {
                view = itemView.findViewById(viewId);
                viewList.put(viewId, view);
            }
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
               /* if (view instanceof EditText) {
                    ((EditText) view).setText(text);
                } else if (view instanceof Button) {
                    ((Button) view).setText(text);
                } else*/
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

        public CustomHolder setImageURI(int viewId, String url) {
            return setImageUrl(viewId, url);
        }

        public CustomHolder setImageUrl(int viewId, String url) {
            return setImageUrl(viewId, url, null);
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

        public CustomHolder setLongClick(int viewId) {
            View view = getView(viewId);
            if (view != null) {
                view.setOnLongClickListener(onLongClickListener);
            }
            return this;
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
    }

}
