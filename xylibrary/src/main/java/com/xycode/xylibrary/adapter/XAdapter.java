package com.xycode.xylibrary.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xycode.xylibrary.R;
import com.xycode.xylibrary.unit.ViewTypeUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiu on 2016/4/3.
 */
public abstract class XAdapter<T> extends RecyclerView.Adapter {

    public static final int  LAYOUT_FOOTER = -20331;

    private List<T> mainList;
    private List<T> dataList;
    private Context context;
    private SparseArray<Integer> headerLayoutIdList;
    private Map<Integer, ViewTypeUnit> multiLayoutMap;
    // item long click on long click Listener
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    private int footerLayout = R.layout.layout_blank;

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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == LAYOUT_FOOTER) {
            View itemView = LayoutInflater.from(context).inflate(footerLayout, parent, false);
            final CustomHolder holder = new CustomHolder(itemView) {
                @Override
                protected void createHolder(final CustomHolder holder) {
                    creatingFooter(holder);
                }
            };
            return holder;
        } else {
            for (int i = 0; i < headerLayoutIdList.size(); i++) {
                final int headerKey = headerLayoutIdList.keyAt(i);
                if (viewType == headerKey) {
                    View itemView = LayoutInflater.from(context).inflate(headerLayoutIdList.get(headerKey), parent, false);
                    final CustomHolder holder = new CustomHolder(itemView) {
                        @Override
                        protected void createHolder(final CustomHolder holder) {
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
                    holder.getRootView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handleItemViewClick(holder, dataList.get(holder.getAdapterPosition()));
                            if (onItemClickListener != null) {
                                onItemClickListener.onItemClick(holder, dataList.get(holder.getAdapterPosition()));
                            }
                        }
                    });

                    if (onItemLongClickListener != null) {
                        holder.getRootView().setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                onItemLongClickListener.onItemLongClick(holder, dataList.get(holder.getAdapterPosition()));
                                return false;
                            }
                        });
                    }
                    creatingHolder(holder, dataList, viewTypeUnit);
                }
            };
            return holder;

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == dataList.size() + headerLayoutIdList.size()) {
            bindingFooter(((CustomHolder) holder));
            return;
        } else if (position < headerLayoutIdList.size()) {
            bindingHeader(((CustomHolder) holder), position);
            return;
        }
        bindingHolder(((CustomHolder) holder), dataList, position - headerLayoutIdList.size());
    }

    /**
     * when create Holder
     *
     * @param holder
     * @param dataList
     * @param viewTypeUnit
     */
    public void creatingHolder(CustomHolder holder, List<T> dataList, ViewTypeUnit viewTypeUnit) {

    }

    /**
     * bind holder
     *
     * @param holder
     * @param dataList
     * @param pos
     */
    public abstract void bindingHolder(CustomHolder holder, List<T> dataList, int pos);

 /*   public int getLoadMoreState() {
        return loadMoreState;
    }

    public void setLoadMoreState(int loadMoreState) {
        this.loadMoreState = loadMoreState;
        notifyDataSetChanged();
    }*/

    /**
     * override this method can show different holder for layout
     * don't return LAYOUT_FOOTER = -20331
     *
     * @param item
     * @return
     */
    protected abstract ViewTypeUnit getViewTypeUnitForLayout(T item);

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
        for (Integer key : multiLayoutMap.keySet()) {
            if (multiLayoutMap.get(key).getMark().equals(viewTypeUnit.getMark())) {
                return key;
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
        int footerCount = 1;
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
        notifyItemRemoved(pos);
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
        notifyItemInserted(pos);
    }

    public void updateItem(int pos, T item) {
        T itemOld = dataList.get(pos);
        int mainPos = mainList.indexOf(itemOld);
        mainList.set(mainPos, item);
        dataList.set(pos, item);
        notifyItemChanged(pos);
    }

    public void addItem(T item) {
        dataList.add(item);
        mainList.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (this.onItemClickListener != null) this.onItemClickListener = null;
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        if (this.onItemLongClickListener != null) this.onItemLongClickListener = null;
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setFooter(@LayoutRes int footerLayout) {
        this.footerLayout = footerLayout;
    }

    public void addHeader(int headerKey, @LayoutRes int headerLayoutId) {
        headerLayoutIdList.put(headerKey, headerLayoutId);
    }


    protected void creatingHeader(CustomHolder holder, int headerKey) {

    }

    protected void bindingHeader(CustomHolder holder, int pos) {

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
     */
    protected void handleItemViewClick(CustomHolder holder, T item) {

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

    /**
     *
     */
    public interface OnItemClickListener<T> {
        void onItemClick(CustomHolder holder, T item);
    }

    /**
     *
     */
    public interface OnItemLongClickListener<T> {
        void onItemLongClick(CustomHolder holder, T item);
    }

    public static abstract class CustomHolder extends RecyclerView.ViewHolder {

        private SparseArray<View> viewList;
        private View itemView;

        public CustomHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            viewList = new SparseArray<>();
            createHolder(this);
        }

        protected abstract void createHolder(CustomHolder holder);

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


        public CustomHolder setText(int viewId, @StringRes int textRes) {
            setText(viewId, itemView.getContext().getString(textRes));
            return this;
        }

        public CustomHolder setText(int viewId, String text) {
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

        public CustomHolder setImageUrl(int viewId, String url) {
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

        public CustomHolder setImageURI(int viewId, Uri uri) {
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

        public CustomHolder setClick(int viewId, View.OnClickListener listener) {
            View view = getView(viewId);
            if (view != null) {
                view.setOnClickListener(listener);
            }
            return this;
        }

        public CustomHolder setLongClick(int viewId, View.OnLongClickListener listener) {
            View view = getView(viewId);
            if (view != null) {
                view.setOnLongClickListener(listener);
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
    }
}
