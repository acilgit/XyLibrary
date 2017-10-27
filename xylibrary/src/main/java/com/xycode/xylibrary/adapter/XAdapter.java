package com.xycode.xylibrary.adapter;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.GridLayoutManager;
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
import com.xycode.xylibrary.utils.LogUtil.L;
import com.xycode.xylibrary.xRefresher.XRefresher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiu on 2016/4/3.
 */
public abstract class XAdapter<T> extends RecyclerView.Adapter {

    public static final int VIEW_TYPE_BLANK = -20330;
    public static final int VIEW_TYPE_FOOTER = -20331;
    public static final int VIEW_TYPE_FOOTER_LOADING = -20332;
    public static final int VIEW_TYPE_FOOTER_NO_MORE = -20333;
    public static final int VIEW_TYPE_FOOTER_RETRY = -20344;
    public static final int VIEW_TYPE_FOOTER_NO_DATA = -20355;

    public static final int[] VIEW_TYPE_FOOTERS = new int[]{VIEW_TYPE_FOOTER,
            VIEW_TYPE_FOOTER_LOADING,
            VIEW_TYPE_FOOTER_NO_MORE,
            VIEW_TYPE_FOOTER_RETRY,
            VIEW_TYPE_FOOTER_NO_DATA };
    /**
     * LOADER_NO_DATA   没有数据
     * LOADER_NO_MORE   已加载全部，有footerLayoutId显示Footer
     * LOADER_CAN_LOAD  可以加载更多
     * LOADER_LOADING   正在加载中，等待LoadMoreListener返回结果
     * LOADER_RETRY     加载更多失败
     */
    public static final int LOADER_INIT = 0;
    public static final int LOADER_NO_DATA = 1;
    public static final int LOADER_NO_MORE = 2;
    public static final int LOADER_CAN_LOAD = 3;
    public static final int LOADER_LOADING = 4;
    public static final int LOADER_RETRY = 5;

    private int loadMoreState = LOADER_INIT;
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
    /**
     * footerLayout 只有在没有更多数据的时候才会显示
     */
    private int footerLayoutId = 0;

    // 以下3个加载项可以有默认LayoutId
    private int loadingLayoutId = 0;
    private int noMoreLayoutId = 0;
    private int loadRetryLayoutId = 0;
    private int noDataLayoutId = 0;

    /**
     * 此动画部分参考https://github.com/CymChad/BaseRecyclerViewAdapterHelper
     */
    private boolean openAnimationEnable = false;
    private Interpolator animationInterpolator = new LinearInterpolator();
    private int animationDuration = 300;
    private int lastVisibleItemPos = -1;
    private BaseAnimation customAnimation;
    private BaseAnimation selectAnimation = new SlideInBottomAnimation();

    private OnInitList onInitList;
    /**
     * 加载更多监听器
     */
    private ILoadMoreListener loadMoreListener;

    /**
     * 是否使用默认加载Layout
     * 如果加载的Layout == 0 则使用默认Layout，否则使用当前设置的Layout
     */
    private boolean useDefaultLoaderLayout = false;
    private boolean showNoDataFooter = false;

    /**
     * use single Layout
     *
     * @param context
     */
    public XAdapter(Context context) {
        init(context, null);
    }

    public XAdapter(Context context, OnInitList initList) {
        init(context, initList);
    }

    private void init(Context context, OnInitList initList) {
        this.context = context;
        this.onInitList = initList;
        List<T> dataList = null;
        if (initList != null) {
            try {
                dataList = (List<T>) initList.getList();
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
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
        switch (viewType) {
            case VIEW_TYPE_BLANK:
                return new CustomHolder(createHolderRootView(R.layout.layout_blank, parent));
            case VIEW_TYPE_FOOTER:
                return new CustomHolder(createHolderRootView(footerLayoutId, parent)) {
                    @Override
                    protected void createHolder(final CustomHolder holder) {
                        ViewTypeUnit viewTypeUnit = new ViewTypeUnit(viewType, footerLayoutId);
                        holder.setOnClickListener(v -> handleItemViewClick(holder, null, v.getId(), viewTypeUnit));
                        holder.setOnLongClickListener(v -> handleItemViewLongClick(holder, null, v.getId(), viewTypeUnit));
                        creatingFooter(holder);
                    }
                };
            case VIEW_TYPE_FOOTER_LOADING:
                return new CustomHolder(createHolderRootView(getLoadingLayoutId(), parent));
            case VIEW_TYPE_FOOTER_NO_MORE:
                return new CustomHolder(createHolderRootView(getNoMoreLayoutId(), parent));
            case VIEW_TYPE_FOOTER_NO_DATA:
                return new CustomHolder(createHolderRootView(getNoDataLayoutId(), parent));
            case VIEW_TYPE_FOOTER_RETRY:
                CustomHolder holder = new CustomHolder(createHolderRootView(getLoadRetryLayoutId(), parent));
                if (getLoadRetryLayoutId() == R.layout.layout_base_load_retry) {
                    holder.setClick(R.id.lMain, v -> {
                        loadMoreState = LOADER_LOADING;
                        notifyDataSetChanged();
                        loadMoreListener.onStartLoadMore();
                    });
                }
                return holder;
            default:
                // Header创建
                for (int i = 0; i < headerLayoutIdList.size(); i++) {
                    final int headerKey = headerLayoutIdList.keyAt(i);
                    if (viewType == headerKey) {
                        View itemView = createHolderRootView(headerLayoutIdList.get(headerKey), parent);
                        return new CustomHolder(itemView) {
                            @Override
                            protected void createHolder(final CustomHolder holder1) {
                                holder1.setOnClickListener(v -> handleItemViewClick(holder1, null, v.getId(), new ViewTypeUnit(headerKey, headerLayoutIdList.get(headerKey))));

                                holder1.setOnLongClickListener(v -> handleItemViewLongClick(holder1, null, v.getId(), new ViewTypeUnit(headerKey, headerLayoutIdList.get(headerKey))));
                                try {
                                    creatingHeader(holder1, headerKey);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    L.e("creatingHeader Exception", e.toString());

                                }
                            }
                        };
                    }
                }
                // 默认Item Holder
                int l = R.layout.layout_blank;
                final ViewTypeUnit viewTypeUnit = multiLayoutMap.get(viewType);
                if (viewTypeUnit != null) {
                    l = viewTypeUnit.getLayoutId();
                }
                @LayoutRes int layoutId = l;
                View itemView = createHolderRootView(layoutId, parent);
                return new CustomHolder(itemView) {
                    @Override
                    protected void createHolder(final CustomHolder holder) {
                        holder.setOnClickListener(v -> handleItemViewClick(holder, dataList.get(holder.getAdapterPosition() - getHeaderCount()), v.getId(), viewTypeUnit));
                        holder.setOnLongClickListener(v -> handleItemViewLongClick(holder, dataList.get(holder.getAdapterPosition() - getHeaderCount()), v.getId(), viewTypeUnit));
                        try {
                            creatingHolder(holder, viewTypeUnit);
                        } catch (Exception e) {
                            e.printStackTrace();
                            L.e("createHolder Exception", e.toString());
                        }
                    }
                };
        }
    }

    private View createHolderRootView(@LayoutRes int layoutId, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(layoutId, parent, false);
    }

    /**
     * 展示
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == dataList.size() + headerLayoutIdList.size()) {
            int itemViewType = getItemViewType(position);
            if (itemViewType == VIEW_TYPE_FOOTER) {
                bindingFooter(((CustomHolder) holder));
            } else if (itemViewType != VIEW_TYPE_BLANK) {
                if (itemViewType == VIEW_TYPE_FOOTER_LOADING) {
                    if (loadMoreState == LOADER_CAN_LOAD) {
                        loadMoreState = LOADER_LOADING;
                        loadMoreListener.onStartLoadMore();
                    }
//                } else if (itemViewType == LAYOUT_FOOTER_RETRY) {
                }
                bindingFooterLoader((CustomHolder) holder, itemViewType);
            }
            return;
        } else if (position < headerLayoutIdList.size()) {
            for (int i = 0; i < headerLayoutIdList.size(); i++) {
                final int headerKey = headerLayoutIdList.keyAt(i);
                if (getItemViewType(position) == headerKey) {
                    try {
                        bindingHeader(((CustomHolder) holder), headerKey);
                    } catch (Exception e) {
                        L.e("BindingHeader Exception", e.toString());
                        e.printStackTrace();
                    }
                }
            }
            return;
        }
        try {
            bindingHolder(((CustomHolder) holder), dataList, position - headerLayoutIdList.size());
        } catch (Exception e) {
            e.printStackTrace();
            L.e("BindingHolder Exception", e.toString());
        }
        addAnimation(holder);
    }

    /**
     * Called when a view created by this adapter has been attached to a window.
     * simple to solve item will layout using all
     * {@link #setFullSpan(RecyclerView.ViewHolder)}
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int type = holder.getItemViewType();
        boolean isFooter = false;
        for (int viewTypeFooterView : VIEW_TYPE_FOOTERS) {
            if(type == viewTypeFooterView) isFooter = true;
        }
        if (isFooter || headerLayoutIdList.indexOfKey(type) >= 0) {
            setFullSpan(holder);
        } else {
            ViewTypeUnit viewTypeUnit = multiLayoutMap.get(type);
            if (viewTypeUnit != null && viewTypeUnit.isFullSpan()) {
                setFullSpan(holder);
            }
        }
    }

    /**
     * thanks CymChad for this method
     * When set to true, the item will layout using all span area. That means, if orientation
     * is vertical, the view will have full width; if orientation is horizontal, the view will
     * have full height.
     * if the hold view use StaggeredGridLayoutManager they should using all span area
     *
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
     *
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
     *
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
     * @param viewTypeUnit
     */
    public void creatingHolder(CustomHolder holder, ViewTypeUnit viewTypeUnit)  throws Exception {

    }

    /**
     * 绑定展示数据时执行，多次执行
     *
     * @param holder
     * @param dataList
     * @param pos
     */
    public void bindingHolder(CustomHolder holder, List<T> dataList, int pos) throws Exception{

    }

    /**
     * 根据Mark确定展示的Layout
     * override this method can show different holder for layout
     * don't return LAYOUT_FOOTER = -20331
     *
     * @param item
     * @return
     */
    protected ViewTypeUnit getViewTypeUnitForLayout(T item) {
        return new ViewTypeUnit(0, R.layout.layout_blank);
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
        int dataSize = getShowingList().size();
        if (position == dataList.size() + headerCount) {
            if (dataSize == 0 && loadMoreState == LOADER_NO_MORE) {
                loadMoreState = LOADER_NO_DATA;
            }
            switch (loadMoreState) {
                case LOADER_LOADING:
                case LOADER_CAN_LOAD:
                    if (loadMoreListener == null || getLoadingLayoutId() == 0 || dataSize == 0) {
                        return VIEW_TYPE_BLANK;
                    }
                    return VIEW_TYPE_FOOTER_LOADING;
                case LOADER_RETRY:
                    if (loadMoreListener == null || getLoadRetryLayoutId() == 0 || dataSize == 0) {
                        return VIEW_TYPE_BLANK;
                    }
                    return VIEW_TYPE_FOOTER_RETRY;
                case LOADER_NO_MORE:
                    if (loadMoreListener != null && footerLayoutId == 0) {
                        return getNoMoreLayoutId() == 0 ? VIEW_TYPE_BLANK : VIEW_TYPE_FOOTER_NO_MORE;
                    } else {
                        return footerLayoutId == 0 ? VIEW_TYPE_BLANK : VIEW_TYPE_FOOTER;
                    }
                case LOADER_NO_DATA:
                    if (footerLayoutId == 0 && showNoDataFooter && dataSize == 0) {
                        return getNoDataLayoutId() == 0 ? VIEW_TYPE_BLANK : VIEW_TYPE_FOOTER_NO_DATA;
                    } else {
                        return footerLayoutId == 0 ? VIEW_TYPE_BLANK : VIEW_TYPE_FOOTER;
                    }
                case LOADER_INIT:
                    return footerLayoutId == 0 ? VIEW_TYPE_BLANK : VIEW_TYPE_FOOTER;
                default:
                    return VIEW_TYPE_BLANK;
            }
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
        int footerCount = 1;
        int headerCount = headerLayoutIdList.size();
        if (dataList != null) {
            return dataList.size() + footerCount + headerCount;
        }
        return footerCount + headerCount;
    }

    public boolean isHeader(int pos) {
        return pos < headerLayoutIdList.size();
    }

    /**
     * 取得列表Item
     * @param itemPosWithoutHeaderCount 列表中的Item位置, 不包括Header的Item,
     *                如使用getAdapterPosition()需要减掉getHeaderCount()
     * @return
     */
    public T getItem(int itemPosWithoutHeaderCount) {
        int pos = itemPosWithoutHeaderCount;
        if (dataList.size() > pos && pos >= 0) {
            return dataList.get(pos);
        }
        return null;
    }

    public T getCurrentItem(CustomHolder holder) {
        return dataList.get(holder.getAdapterPosition() - getHeaderCount());
    }

    public OnInitList getOnInitList() {
        return onInitList;
    }

    public void setAnimationDuration(int animationDuration) {
        this.animationDuration = animationDuration;
    }

    public void setAnimationInterpolator(Interpolator animationInterpolator) {
        this.animationInterpolator = animationInterpolator;
    }

    public boolean hasFooter() {
//        return footerLayout != 0;
        return true;
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
     *
     * @return
     */
    public List<T> getNoFilteredDataList() {
        return mainList;
    }

    /**
     * 取得下在展示的数据集合
     *
     * @return
     */
    public List<T> getShowingList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
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
    @Deprecated
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
        notifyItemInserted(getItemCount() - 2);
    }

    public void setFooter(@LayoutRes int footerLayout) {
        this.footerLayoutId = footerLayout;
    }

    public void addHeader(@LayoutRes int headerLayoutId) {
        addHeader(XRefresher.HEADER_ONE, headerLayoutId);
    }

    public void addHeader(int headerKey, @LayoutRes int headerLayoutId) {
        headerLayoutIdList.put(headerKey, headerLayoutId);
    }

    protected void creatingHeader(CustomHolder holder, int headerKey) throws Exception {

    }

    protected void bindingHeader(CustomHolder holder, int headerKey) throws Exception{

    }

    protected void creatingFooter(CustomHolder holder) {

    }

    protected void bindingFooter(CustomHolder holder) {

    }

    /**
     * 启用加载更多时绑定Footer
     * 建议不想新建Layout的可以继承XAdapter
     * 重写此方法，适应更种加载提示语等
     *
     * @param holder
     * @param layoutFooterViewType LAYOUT_FOOTER
     */
    protected void bindingFooterLoader(CustomHolder holder, int layoutFooterViewType) {

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
     * 是否使用过滤列表
     *
     * @return
     */
    protected boolean useFilter() {
        return false;
    }

    /**
     * 加载更多结束
     *
     * @param noMoreData true:没有更多的数据了， false:可以再次加载更多
     */
    public void loadingMoreEnd(boolean noMoreData) {
        if (loadMoreListener != null) {
            loadMoreState = noMoreData ? LOADER_NO_MORE : LOADER_CAN_LOAD;
            if (loadMoreState == LOADER_NO_MORE) {
                notifyDataSetChanged();
            }
        }
    }

    /**
     * 加载更多异常
     * 显示加载重试Footer
     */
    public void loadingMoreError() {
        if (loadMoreListener != null) {
            loadMoreState = LOADER_RETRY;
            notifyDataSetChanged();
        }
    }

    public void refreshedNoData() {
        if (showNoDataFooter && loadMoreState == LOADER_INIT) {
            loadMoreState = LOADER_NO_DATA;
        }
    }

    /**
     * 加载更多接口
     */
    public interface ILoadMoreListener {
        void onStartLoadMore();
    }

    public ILoadMoreListener getLoadMoreListener() {
        return loadMoreListener;
    }

    public void setLoadMoreListener(ILoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public void setLoadingLayout(@LayoutRes int loadingLayoutId) {
        this.loadingLayoutId = loadingLayoutId;
    }

    public int getLoadingLayoutId() {
        return useDefaultLoaderLayout && loadingLayoutId == 0 ? R.layout.layout_base_loading : loadingLayoutId;
    }

    private int getNoMoreLayoutId() {
        return useDefaultLoaderLayout && noMoreLayoutId == 0 ? R.layout.layout_base_no_more : noMoreLayoutId;
    }

    public void setNoMoreLayoutId(@LayoutRes int noMoreLayoutId) {
        this.noMoreLayoutId = noMoreLayoutId;
    }

    public int getNoDataLayoutId() {
        return useDefaultLoaderLayout && noDataLayoutId == 0 ? R.layout.layout_base_no_data : noDataLayoutId;
    }

    public void setNoDataLayoutId(int noDataLayoutId) {
        this.noDataLayoutId = noDataLayoutId;
    }

    public int getLoadRetryLayoutId() {
        return useDefaultLoaderLayout && loadRetryLayoutId == 0 ? R.layout.layout_base_load_retry : loadRetryLayoutId;
    }

    public void setLoadRetryLayoutId(int loadRetryLayoutId) {
        this.loadRetryLayoutId = loadRetryLayoutId;
    }

    public void setUseDefaultLoaderLayout(boolean useDefaultLoaderLayout) {
        this.useDefaultLoaderLayout = useDefaultLoaderLayout;
    }

    public void setShowNoDataFooter(boolean showNoDataFooter) {
        this.showNoDataFooter = showNoDataFooter;
    }
}
