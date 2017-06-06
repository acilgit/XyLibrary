package com.xycode.xylibrary.adapter;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.xycode.xylibrary.R;
import com.xycode.xylibrary.animation.BaseAnimation;
import com.xycode.xylibrary.animation.SlideInBottomAnimation;
import com.xycode.xylibrary.unit.ViewTypeUnit;
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
     * useFilter() == true 时使用
     */
    private List<T> dataList;
    protected Context context;
    private SparseArray<Integer> headerLayoutIdList;
    private Map<Integer, ViewTypeUnit> multiLayoutMap;
    private int footerLayout = 0;
    // item long click on long click Listener
//    private OnItemClickListener onItemClickListener;
//    private OnItemLongClickListener onItemLongClickListener;

    /**
     * 此动画部分参考https://github.com/CymChad/BaseRecyclerViewAdapterHelper
     */
    private boolean openAnimationEnable = false;
    private Interpolator animationInterpolator = new LinearInterpolator();
    private int animationDuration = 300;
    private int lastVisibleItemPos = -1;
    private BaseAnimation customAnimation;
    private BaseAnimation selectAnimation = new SlideInBottomAnimation();

    /**
     * use single Layout
     *
     * @param context
     */
    public XAdapter(Context context) {
        init(context, null);
    }

    public XAdapter(Context context, List<T> dataList) {
        init(context, dataList);
    }

    private void init(Context context, List<T> dataList) {
        this.context = context;
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        if (useFilter()) {
            this.dataList = new ArrayList<>();
            this.mainList = dataList;
            this.dataList.addAll(setFilterForAdapter(mainList));
        } else {
            this.mainList = dataList;
            this.dataList = dataList;
        }
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
                    holder.setOnClickListener(v -> handleItemViewClick(holder, null, v.getId(), new ViewTypeUnit(viewType, footerLayout)));
                    holder.setOnLongClickListener(v -> handleItemViewLongClick(holder, null, v.getId(), new ViewTypeUnit(viewType, footerLayout)));
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
                            holder.setOnClickListener(v -> handleItemViewClick(holder, null, v.getId(), new ViewTypeUnit(headerKey, headerLayoutIdList.get(headerKey))));

                            holder.setOnLongClickListener(v -> handleItemViewLongClick(holder, null, v.getId(), new ViewTypeUnit(headerKey, headerLayoutIdList.get(headerKey))));
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
                    holder.setOnClickListener(v -> handleItemViewClick(holder, dataList.get(holder.getAdapterPosition() - getHeaderCount()), v.getId(), viewTypeUnit));

                    holder.setOnLongClickListener(v -> handleItemViewLongClick(holder, dataList.get(holder.getAdapterPosition() - getHeaderCount()), v.getId(), viewTypeUnit));
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
        addAnimation(holder);
    }

    /**
     *  Called when a view created by this adapter has been attached to a window.
     *  simple to solve item will layout using all
     *  {@link #setFullSpan(RecyclerView.ViewHolder)}
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int type = holder.getItemViewType();
        if (type == LAYOUT_FOOTER || headerLayoutIdList.indexOfKey(type)>=0) {
            setFullSpan(holder);
        } else {
            ViewTypeUnit viewTypeUnit = multiLayoutMap.get(type);
            if (viewTypeUnit != null && viewTypeUnit.isFullSpan()) {
                setFullSpan(holder);
            }
        }
    }

    /**
     * When set to true, the item will layout using all span area. That means, if orientation
     * is vertical, the view will have full width; if orientation is horizontal, the view will
     * have full height.
     * if the hold view use StaggeredGridLayoutManager they should using all span area
     * @param holder True if this item should traverse all spans.
     */
    protected void setFullSpan(RecyclerView.ViewHolder holder) {
        if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            params.setFullSpan(true);
        }
    }

    /**
     * add animation when you want to show time
     * @param holder
     */
    private void addAnimation(RecyclerView.ViewHolder holder) {
        if (openAnimationEnable) {
            if (holder.getLayoutPosition() > lastVisibleItemPos) {
                BaseAnimation animation = null;
                if (customAnimation != null) {
                    animation = customAnimation;
                } else {
                    animation = selectAnimation;
                }
                startItemAnimation(animation, holder);
                lastVisibleItemPos = holder.getLayoutPosition();
            }
        }
    }

    /**
     * set anim to start when loading
     * @param animation
     * @param holder
     */
    protected void startItemAnimation(BaseAnimation animation, RecyclerView.ViewHolder holder) {
        for (Animator anim : animation.getAnimators(holder.itemView)) {
            anim.setDuration(animationDuration).start();
            anim.setInterpolator(animationInterpolator);
        }
    }

    /**
     * Set Custom ObjectAnimator
     *
     * @param animation ObjectAnimator
     */
    public void openLoadAnimation(BaseAnimation animation) {
        this.openAnimationEnable = true;
        this.customAnimation = animation;
    }

    /**
     * To open the animation when loading
     */
    public void openLoadAnimation() {
        this.openAnimationEnable = true;
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

    public void setAnimationDuration(int animationDuration) {
        this.animationDuration = animationDuration;
    }

    public void setAnimationInterpolator(Interpolator animationInterpolator) {
        this.animationInterpolator = animationInterpolator;
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

    /**
     * 适配器中的所有数据，包括被过滤掉的数据
     * @return
     */
    public List<T> getNoFilteredDataList() {
        return mainList;
    }

    /**
     * 取得下在展示的数据集合
     * @return
     */
    public List<T> getShowingList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        if (useFilter()) {
            mainList.clear();
            mainList.addAll(dataList);
            this.dataList.clear();
            this.dataList.addAll(setFilterForAdapter(mainList));
        } else {
            mainList = dataList;
            this.dataList = dataList;
        }
        lastVisibleItemPos = -1;
        beforeSetDataList(this.dataList);
        notifyDataSetChanged();
    }

    /**
     * useFilter() == true 时有效果
     */
    public void resetDataList() {
        if (useFilter()) {
            this.dataList.clear();
            this.dataList.addAll(setFilterForAdapter(mainList));
        }
        lastVisibleItemPos = -1;
        beforeSetDataList(this.dataList);
        notifyDataSetChanged();
    }

    public void removeItem(int pos) {
        T item = dataList.get(pos);
        if (useFilter()) {
            mainList.remove(item);
            dataList.remove(pos);
        } else {
            dataList.remove(pos);
        }
        notifyItemRemoved(headerLayoutIdList.size() + pos);
    }

    /**
     * this method can only use in no filter mode
     *
     * @param pos
     * @param item
     */
    public void addItemNoFilter(int pos, T item) {
        if (useFilter()) {
            dataList.add(pos, item);
            mainList.add(pos, item);
        } else {
            dataList.add(pos, item);
        }
        notifyItemInserted(headerLayoutIdList.size() + pos);
    }

    public void updateItem(int pos, T item) {
        T itemOld = dataList.get(pos);
        if (useFilter()) {
            int mainPos = mainList.indexOf(itemOld);
            mainList.set(mainPos, item);
            dataList.set(pos, item);
        } else {
            dataList.set(pos, item);
        }
        notifyItemChanged(headerLayoutIdList.size() + pos);
    }

    public void addItem(T item) {
        if (useFilter()) {
            dataList.add(item);
            mainList.add(item);
        } else {
            dataList.add(item);
        }
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

    protected void beforeSetDataList(List<T> dataList) {

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
     * 列表过滤器
     * @param mainList
     * @return
     */
    protected List<T> setFilterForAdapter(List<T> mainList) {
        List<T> list = new ArrayList<>();
        list.addAll(mainList);
        return list;
    }

    /**
     * 是否使用过滤列表
     * @return
     */
    protected boolean useFilter() {
        return false;
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

}
