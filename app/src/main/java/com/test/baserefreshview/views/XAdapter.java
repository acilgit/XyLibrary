package com.test.baserefreshview.views;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiu on 2016/4/3.
 */
public abstract class XAdapter<T> extends RecyclerView.Adapter {

    public static final int SINGLE_LAYOUT = -1;

    public static final int FOOTER_MORE = 0;
    public static final int FOOTER_LOADING = 1;
    public static final int FOOTER_NO_MORE = 2;

    private static final int LAYOUT_HEADER = -20330;
    private static final int LAYOUT_FOOTER = -20331;
    private List<T> mainList;
    private List<T> dataList;
    private Context context;
    private SparseArray<Integer> layoutIdList;
    // 点击及长按Listener
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    private final int defaultFooterLayout = android.R.layout.simple_list_item_1;
    private int footerLayout = defaultFooterLayout;
    private int footerState = FOOTER_NO_MORE;

    /**
     * 使用单一Layout
     *
     * @param context
     * @param dataList
     * @param layoutId
     */
    public XAdapter(Context context, List<T> dataList, @LayoutRes int layoutId) {
        this.context = context;
        this.dataList = new ArrayList<>();
        this.mainList = dataList;
        this.layoutIdList = new SparseArray<>();
        layoutIdList.put(SINGLE_LAYOUT, layoutId);
    }

    /**
     * 使用Holder分类列表Layout
     *
     * @param context
     * @param dataList
     * @param layoutIdList key: viewType  value: layoutId
     */
    public XAdapter(Context context, List<T> dataList, SparseArray layoutIdList) {
        this.context = context;
        this.dataList = new ArrayList<>();
        this.mainList = dataList;
        this.layoutIdList = layoutIdList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == LAYOUT_FOOTER) {
            View itemView = LayoutInflater.from(context).inflate(footerLayout, parent, false);
            final CustomHolder holder = new CustomHolder(itemView) {
                @Override
                protected void createHolder(final CustomHolder holder) {
                    if (footerLayout == defaultFooterLayout) {
                        TextView textView = ((CustomHolder) holder).getView(android.R.id.text1);
                        textView.setGravity(Gravity.CENTER);
                    }
                }
            };
            return holder;
        } else {
            @LayoutRes int layoutId = (layoutIdList.size() == 1 ? layoutIdList.get(SINGLE_LAYOUT) : layoutIdList.get(viewType));
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
                    creatingHolder(holder, dataList, viewType);
                }
            };
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == dataList.size()) {
            if (footerLayout == defaultFooterLayout) {
                TextView textView = ((CustomHolder) holder).getView(android.R.id.text1);
                switch (footerState) {
                    case FOOTER_MORE:
                        textView.setText("上拉加载更多");
                        break;
                    case FOOTER_LOADING:
                        textView.setText("加载中...");
                        break;
                    case FOOTER_NO_MORE:
                        break;
                    default:
                        break;
                }
            } else {
                bindFooterView((CustomHolder) holder, footerState);
            }
            ((CustomHolder) holder).getRootView().setVisibility(footerState == FOOTER_NO_MORE ? View.GONE : View.VISIBLE);
            return;
        }
        bindingHolder(((CustomHolder) holder), dataList, position);
    }

    /**
     * 创建Holder时绑定控件
     *
     * @param holder
     * @param dataList
     * @param viewType
     */
    public abstract void creatingHolder(CustomHolder holder, List<T> dataList, int viewType);

    /**
     * 在适配器中显示数据集
     *
     * @param holder
     * @param dataList
     * @param pos
     */
    public abstract void bindingHolder(CustomHolder holder, List<T> dataList, int pos);

    public void setFooterLayout(@LayoutRes int footerLayout) {
        this.footerLayout = footerLayout;
    }

    protected void bindFooterView(CustomHolder holder, int footerState) {

    }

    public int getFooterState() {
        return footerState;
    }

    protected void setFooterState(int footerState) {
        this.footerState = footerState;
        notifyDataSetChanged();
    }

    /**
     * 复写此方法可以在不同的layout中显示
     * 请不要返回 LAYOUT_FOOTER = -20331
     *
     * @param item
     * @return
     */
    protected int getItemType(T item) {
        return SINGLE_LAYOUT;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == dataList.size()) {
            return LAYOUT_FOOTER;
        }
        int type = getItemType(dataList.get(position));
        if (type == SINGLE_LAYOUT) {
            return super.getItemViewType(position);
        } else {
            return type;
        }
    }

    /**
     * 请使用getDataList().size()取得列表元素个数，这方法会加上Footer
     *
     * @return
     */
    @Override
    public int getItemCount() {
        int footerCount = footerLayout == defaultFooterLayout ? 0 : 1;
        if (dataList != null) {
            return dataList.size() + footerCount;
        }
        return footerCount;
    }

    public T getItem(int pos) {
        if (dataList.size() > pos && pos > 0) {
            return dataList.get(pos);
        }
        return null;
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
        dataList.remove(pos);
        notifyItemRemoved(pos);
    }

    public void addItem(int pos, T item) {
        dataList.add(pos, item);
        notifyItemInserted(pos);
    }

    public void updateItem(int pos, T item) {
        dataList.set(pos, item);
        notifyItemChanged(pos);
    }

    public void addItem(T item) {
        dataList.add(item);
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

    /**
     * 重写此事件，用于处理holder rootView点击事件，处理完毕后再处理onItemClickListener()
     * 如果根组件已重写Touch等触摸方法，可能会使该方法失效，如：RippleView
     *
     * @param holder
     * @param item
     */
    protected void handleItemViewClick(CustomHolder holder, T item) {

    }

    /**
     * 过滤数据
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
     * 点击接口
     */
    public interface OnItemClickListener<T> {
        void onItemClick(CustomHolder holder, T item);
    }

    /**
     * 长按接口
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

        public CustomHolder setImageURI(int viewId, String uri) {
            View view = getView(viewId);
            if (view != null) {
                if (view instanceof ImageView) {
                    ((ImageView) view).setImageURI(Uri.parse(uri));
                }
            }
            return this;
        }

        public CustomHolder setImageURI(int viewId, int visibility) {
            View view = getView(viewId);
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
            return this;
        }
    }
}
