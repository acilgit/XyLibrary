package com.xycode.xylibrary.xRefresher;

/**
 * Created by XY on 2016/6/18.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.alibaba.fastjson.JSONObject;
import com.xycode.xylibrary.R;
import com.xycode.xylibrary.adapter.CustomHolder;
import com.xycode.xylibrary.adapter.OnInitList;
import com.xycode.xylibrary.adapter.XAdapter;
import com.xycode.xylibrary.annotation.SaveState;
import com.xycode.xylibrary.base.XyBaseActivity;
import com.xycode.xylibrary.okHttp.OkResponseListener;
import com.xycode.xylibrary.okHttp.Param;
import com.xycode.xylibrary.uiKit.recyclerview.FlexibleDividerDecoration;
import com.xycode.xylibrary.uiKit.recyclerview.HorizontalDividerItemDecoration;
import com.xycode.xylibrary.uiKit.recyclerview.XLinearLayoutManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by XY on 2016/6/17.
 * 列表刷新器
 */
public class XRefresher<T> extends CoordinatorLayout implements FlexibleDividerDecoration.VisibilityProvider, FlexibleDividerDecoration.SizeProvider {

    public static final int HEADER_ONE = 0;

    private static final int REFRESH = 1;
    private static final int LOAD = 2;

    private static InitRefresher initRefresher = null;

    @SaveState
    private int background;
    @SaveState
    private boolean backgroundIsRes = false;

    private XyBaseActivity activity;

    private RecyclerView.LayoutManager layoutManager;

    @SaveState
    private RefreshState state;
    private XAdapter<T> adapter;
    private RefreshSetter refreshSetter;

    private SwipeRefreshLayout swipe;
    private RecyclerView recyclerView;

    private RefreshRequest refreshRequest;
    private OnLastPageListener onLastPageListener;

    private HorizontalDividerItemDecoration horizontalDividerItemDecoration;
    @SaveState
    private int dividerSize = 0;

    @SaveState
    private int lastVisibleItem = 0;
    @SaveState
    private boolean loadMore = false;
    private CoordinatorLayout rlMain;
    private OnSwipeListener swipeListener;

    private boolean defaultHeaderAdded = false;
    private boolean defaultParamAdded = false;
   private boolean addDefaultParam = true;
   private boolean addDefaultHeader = true;

    private static Options options;

    public XRefresher(Context context) {
        super(context, null);
    }

    public XRefresher(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_refresher, this, true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XRefresher);

        background = typedArray.getColor(R.styleable.XRefresher_bg, 1);
        if (background == 1) {
            background = typedArray.getResourceId(R.styleable.XRefresher_bg, 1);
            backgroundIsRes = background != 1;
        }
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        rlMain = (CoordinatorLayout) findViewById(R.id.rlMain);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        recyclerView = (RecyclerView) findViewById(R.id.rvMain);
        if (backgroundIsRes) {
            rlMain.setBackgroundResource(background);
        } else if (background != 1) {
            rlMain.setBackgroundColor(background);
        }
    }

    @Deprecated
    public void setup(XyBaseActivity activity, XAdapter<T> adapter, boolean loadMore, OnSwipeListener swipeListener, RefreshRequest refreshRequest) {
        RefreshSetter setter = setup(activity, adapter).setOnSwipeListener(swipeListener).setRefreshRequest(refreshRequest);
        if (loadMore) setter.setLoadMore();
    }

    /**
     * 初始化XRefresher
     * @param activity
     * @param adapter
     * @return
     */
    public RefreshSetter setup(XyBaseActivity activity, XAdapter<T> adapter) {
        refreshSetter = new RefreshSetter(this);
        layoutManager = layoutManager == null ? new XLinearLayoutManager(activity) : layoutManager;
        recyclerView.setLayoutManager(layoutManager);
        this.activity = activity;
        this.adapter = adapter;
        this.state = new RefreshState();
        this.recyclerView.setAdapter(adapter);
        this.adapter.setUseDefaultLoaderLayout(true);
        if (options.loadingRefreshingArrowColorRes != null) {
            swipe.setColorSchemeResources(options.loadingRefreshingArrowColorRes);
        }
        recyclerView.setAdapter(adapter);
        adapter.setUseDefaultLoaderLayout(true);
        return refreshSetter;
    }

    public static class RefreshSetter {
        XRefresher refresher;
        SwipeRefreshLayout swipeRefreshLayout;

        RefreshSetter(XRefresher refresher) {
            this.refresher = refresher;
            swipeRefreshLayout = (SwipeRefreshLayout) refresher.findViewById(R.id.swipe);
        }

        public RefreshSetter setLoadMore() {
            refresher.loadMore = true;
            refresher.adapter.setLoadingLayout(options.loadMoreLayoutId);
            refresher.adapter.setNoMoreLayoutId(options.noMoreLayoutId);
            refresher.adapter.setNoMoreLayoutId(options.loadRetryLayoutId);
            refresher.adapter.setLoadMoreListener(() -> {
                refresher.getDataByRefresh(refresher.state.pageIndex + 1, refresher.state.pageDefaultSize);
            });
            return this;
        }

        public RefreshSetter setRefreshPageSize(int refreshPageSize) {
            refresher.state.setPageDefaultSize(refreshPageSize);
            return this;
        }

        public RefreshSetter setRefreshRequest(RefreshRequest refreshRequest) {
            refresher.refreshRequest = refreshRequest;
            setSwipeRefresh();
            return this;
        }

        public RefreshSetter setOnSwipeListener(OnSwipeListener swipeListener) {
            refresher.swipeListener = swipeListener;
            setSwipeRefresh();
            return this;
        }

        /**
         * 可使用布流式布局
         *
         * @param spanCount
         * @param orientation
         */
        public RefreshSetter setStaggeredGridLayoutManager(int spanCount, int orientation) {
            refresher.layoutManager = new StaggeredGridLayoutManager(spanCount, orientation);
            refresher.getRecyclerView().setLayoutManager(refresher.layoutManager);
            return this;
        }

        public RefreshSetter setGridLayoutManager(int spanCount, int orientation, boolean reverseLayout, ILayoutManagerSpanListener layoutManagerSpanListener) {
            refresher.layoutManager = new GridLayoutManager(refresher.activity, spanCount, orientation, reverseLayout);
            GridLayoutManager layoutManager = (GridLayoutManager) refresher.layoutManager;
            refresher.getRecyclerView().setLayoutManager(layoutManager);
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (refresher.getAdapter().isHeader(position)) {
                        return layoutManager.getSpanCount();
                    }
                    switch (refresher.getAdapter().getItemViewType(position)) {
                        case XAdapter.VIEW_TYPE_FOOTER :
                        case XAdapter.VIEW_TYPE_FOOTER_LOADING :
                        case XAdapter.VIEW_TYPE_FOOTER_NO_MORE :
                        case XAdapter.VIEW_TYPE_FOOTER_RETRY :
                            return  layoutManager.getSpanCount();
                        default:
                            if(layoutManagerSpanListener != null) return layoutManagerSpanListener.setSpanCount(position);
                    }
                    return 1;
                }
            });
            return this;
        }

        public RefreshSetter setRecyclerViewDivider(@ColorRes int dividerColor, @DimenRes int dividerHeight) {
            setRecyclerViewDivider(dividerColor, dividerHeight, R.dimen.zero, R.dimen.zero);
            return this;
        }

        /**
         * use after xRefresher setup
         *
         * @param dividerColor
         * @param dividerHeight
         * @param marginLeft
         * @param marginRight
         */
        public RefreshSetter setRecyclerViewDivider(@ColorRes int dividerColor, @DimenRes int dividerHeight, @DimenRes int marginLeft, @DimenRes int marginRight) {
            HorizontalDividerItemDecoration.Builder builder = new HorizontalDividerItemDecoration.Builder(refresher.activity)
                    .visibilityProvider(refresher)
                    .sizeProvider(refresher)
                    .colorResId(dividerColor)/*.sizeResId(dividerHeight)*/
                    .marginResId(marginLeft, marginRight);
            refresher.horizontalDividerItemDecoration = builder.build();
            refresher.dividerSize = refresher.activity.getResources().getDimensionPixelSize(dividerHeight);
            refresher.recyclerView.addItemDecoration(refresher.horizontalDividerItemDecoration);
            return this;
        }

        public RefreshSetter setRecyclerViewDividerWithGap(@ColorRes int dividerColor, @DimenRes int dividerHeight, @DimenRes int gapWidthId) {
            HorizontalDividerItemDecoration.Builder builder = new HorizontalDividerItemDecoration.Builder(refresher.activity)
                    .visibilityProvider(refresher)
                    .sizeProvider(refresher)
                    .colorResId(dividerColor)/*.sizeResId(dividerHeight)*/
                    .setGapProvider(new HorizontalDividerItemDecoration.GapProvider(){
                        @Override
                        public int gapLeft(int pos, RecyclerView parent) {
                            int firstItemPos = refresher.getAdapter().getHeaderCount()-1;
                            int lastItemPos = refresher.getAdapter().getHeaderCount()-1 + refresher.getAdapter().getShowingList().size();

                            if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                                StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams)parent.getChildAt(pos).getLayoutParams();
                                int spanIndex = lp.getSpanIndex();
                                if(!lp.isFullSpan() && pos >firstItemPos && pos <= lastItemPos) {
                                    if(spanIndex == 1){
                                        return refresher.getContext().getResources().getDimensionPixelSize(gapWidthId);
                                    }
                                }
                            }
                            return 0;
                        }

                        @Override
                        public int gapRight(int pos, RecyclerView parent) {
                            int firstItemPos = refresher.getAdapter().getHeaderCount()-1;
                            int lastItemPos = refresher.getAdapter().getHeaderCount()-1 + refresher.getAdapter().getShowingList().size();

                            if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                                StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams)parent.getLayoutParams();
                                int spanIndex = lp.getSpanIndex();
                                if(!lp.isFullSpan() && pos >firstItemPos && pos <= lastItemPos) {
                                    if(spanIndex == 0){
                                        return refresher.getContext().getResources().getDimensionPixelSize(gapWidthId);
                                    }
                                }
                            }
                            return 0;
                        }

                        @Override
                        public int gapWidth(int position, RecyclerView parent) {
                            return refresher.getContext().getResources().getDimensionPixelSize(gapWidthId);
                        }
                    });
            refresher.horizontalDividerItemDecoration = builder.build();
            refresher.dividerSize = refresher.activity.getResources().getDimensionPixelSize(dividerHeight);
            refresher.recyclerView.addItemDecoration(refresher.horizontalDividerItemDecoration);
            return this;
        }

        public void setOnLastPageListener(OnLastPageListener onLastPageListener) {
            refresher.onLastPageListener = onLastPageListener;
        }

        private void setSwipeRefresh() {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                if (refresher.swipeListener != null) refresher.swipeListener.onRefresh();
                if (refresher.refreshRequest != null) refresher.refreshList();
            });
        }
    }


    /**
     * @param pageSize page size shown in one time
     */
    private void getDataByRefresh(int pageSize) {
        getDataByRefresh(1, pageSize, REFRESH);
    }

    private void getDataByRefresh(int page, int pageSize) {
        getDataByRefresh(page, pageSize, LOAD);
    }

    private void getDataByRefresh(final int page, final int pageSize, final int refreshType) {
        Param params = new Param();
        final int postPageSize = (pageSize < state.pageDefaultSize) ? state.pageDefaultSize : pageSize;
        final int actualPage = refreshType == REFRESH ? options.firstPage : page;
        params.put(options.page, String.valueOf(actualPage));
        params.put(options.pageSize, String.valueOf(postPageSize));
        String url = refreshRequest.setRequestParamsReturnUrl(params);
        boolean addDefaultParam = true;
        boolean addDefaultHeader = true;
        if (initRefresher != null) {
            addDefaultParam = initRefresher.addDefaultParam();
            addDefaultHeader = initRefresher.addDefaultHeader();
        }
        this.addDefaultHeader = defaultHeaderAdded ? this.addDefaultHeader : addDefaultHeader;
        this.addDefaultParam = defaultParamAdded ? this.addDefaultParam : addDefaultParam;
        activity.newCall().url(url)
                .body(params)
                .addDefaultParams(this.addDefaultParam)
                .addDefaultHeader(this.addDefaultHeader)
                .call(new OkResponseListener() {
                    @Override
                    public void handleJsonSuccess(Call call, Response response, JSONObject json) {
                        List<T> getList = refreshRequest.setListData(json);
                        final List<T> newList;
                        if (getList == null) {
                            newList = new ArrayList<>();
                        } else {
                            newList = getList;
                        }
                        state.setLastPage(/* (refreshType != REFRESH) &&*/ newList.size() < postPageSize);
                        final List<T> list = new ArrayList<>();
                        switch (refreshType) {
                            case REFRESH:
                                swipe.setRefreshing(false);
                                if (state.pageIndex == 0) state.pageIndex++;
                                break;
                            default:
                                list.addAll(getAdapter().getNoFilteredDataList());
                                state.pageIndex++;
                                break;
                        }
                        adapter.loadingMoreEnd(state.lastPage);
                        if (newList.size() > 0) {
                            for (T newItem : newList) {
                                boolean hasSameItem = false;
                                for (T listItem : list) {
                                    if (refreshRequest.ignoreSameItem(newItem, listItem)) {
                                        hasSameItem = true;
                                        break;
                                    }
                                }
                                if (!hasSameItem) list.add(newItem);
                            }
                            Collections.sort(list, (arg0, arg1) -> refreshRequest.compareTo(arg0, arg1));
                            getAdapter().setDataList(list);
                        } else if (refreshType == REFRESH) {
                            getAdapter().setDataList(list);
                        }
                        if (onLastPageListener != null)
                            onLastPageListener.receivedList(state.lastPage);
                        if (refreshType == REFRESH) {
                            adapter.refreshedNoData();
                        }
                    }

                    @Override
                    public void handleJsonError(Call call, Response response, JSONObject json) {
                        if (!refreshRequest.handleError(call, json) && initRefresher != null) {
                            initRefresher.handleError(call, json);
                        }
                    }

                    @Override
                    protected void handleAllFailureSituation(Call call, int resultCode) {
                        switch (refreshType) {
                            case REFRESH:
                                swipe.setRefreshing(false);
                                break;
                            case LOAD:
                                adapter.loadingMoreError();
//                        setLoadMoreState(state.lastPage ? XAdapter.LOADER_NO_MORE : XAdapter.LOADER_CAN_LOAD);
                                break;
                        }
                        if (!refreshRequest.handleAllFailureSituation(call, resultCode) && initRefresher != null) {
                            initRefresher.handleAllFailureSituation(call, resultCode);
                        }
                    }
                });
    }

    public void setRefreshing(boolean refreshing) {
        swipe.setRefreshing(refreshing);
    }

    /**
     * refresh list
     */
    public void refresh() {
        swipeRefresh();
        refreshList();
    }

    public void swipeRefresh() {
        if (swipeListener != null) {
            swipeListener.onRefresh();
        }
    }

    public void refreshList() {
        refreshList(false);
    }

    private void refreshList(boolean showDialog) {
        if (refreshRequest != null) {
            int size = getAdapter().getNoFilteredDataList().size();
            if (size > 0) {
                // 小于整页倍数时，把请求数量调整为整页倍数
                int requestPageSize = (size / state.pageDefaultSize) * state.pageDefaultSize;
                if(size % state.pageDefaultSize > 0) requestPageSize = requestPageSize + state.pageDefaultSize;
                getDataByRefresh(requestPageSize);
            } else {
                getDataByRefresh(state.pageDefaultSize);
                swipe.setRefreshing(false);
            }
        }
    }

    public void setLastPage(int page) {
        this.state.pageIndex = page;
    }

    public void resetLastPage() {
        this.state.pageIndex = options.firstPage;
    }

    public XAdapter<T> getAdapter() {
        return adapter;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipe;
    }

    public XRefresher addDefaultHeader(boolean addDefaultHeader){
        defaultHeaderAdded = true;
        this.addDefaultHeader = addDefaultHeader;
        return this;
    }
    public XRefresher addDefaultParam(boolean addDefaultParam){
        defaultParamAdded = true;
        this.addDefaultParam = addDefaultParam;
        return this;
    }

    public CustomHolder getHeader() {
        return getHeader(HEADER_ONE);
    }

    public CustomHolder getHeader(int headerKey) {
        int headerPos = adapter.getHeaderPos(headerKey);
        if (headerPos < 0) {
            return null;
        }
        CustomHolder holder = (CustomHolder) getRecyclerView().getChildViewHolder(getRecyclerView().getChildAt(headerPos));
        return holder;
    }

    public CustomHolder getFooter() {
        if (!getAdapter().hasFooter()) return null;
        CustomHolder holder = (CustomHolder) getRecyclerView().getChildViewHolder(getRecyclerView().getChildAt(adapter.getItemCount() - 1));
        return holder;
    }


    public boolean isLastPage() {
        return state.lastPage;
    }

    @Override
    public boolean shouldHideDivider(int position, RecyclerView parent) {
        if (position < getAdapter().getHeaderCount()) {
            return true;
        } else if (getAdapter().hasFooter() && position == getAdapter().getItemCount() - 2) {
            return true;
        }
        return false;
    }

    @Override
    public int dividerSize(int position, RecyclerView parent) {
        if (position < getAdapter().getHeaderCount()) {
            return 0;
        } else if (getAdapter().hasFooter() && position == getAdapter().getItemCount() - 2) {
            return 0;
        }
        return dividerSize;
    }

    /**
     * 可在App中使用以设置通用选项
     *
     * @param initRefresher
     */
    public static void init(InitRefresher initRefresher) {
        init(initRefresher, new Options());
    }

    public static void init(InitRefresher initRefresher, Options options) {
        XRefresher.initRefresher = initRefresher;
        XRefresher.options = options;
        if (options.loadMoreLayoutId != 0) {
            LoadMoreView.setLayoutId(options.loadMoreLayoutId);
        }
    }

    public static class RefreshState implements Serializable {
        boolean lastPage = false;
        int pageIndex = 0;
        int pageDefaultSize = 10;

        public RefreshState() {
        }

        public void setPageDefaultSize(int pageDefaultSize) {
            this.pageDefaultSize = pageDefaultSize;
        }

        public void setLastPage(boolean lastPage) {
            this.lastPage = lastPage;
        }
    }

    public static class Options {
        String page = "page";
        String pageSize = "pageSize";
        int firstPage = 1;
        int loadMoreLayoutId = 0;
        int noMoreLayoutId = 0;
        int noDataLayoutId = 0;
        int loadRetryLayoutId = 0;

        @ColorRes
        int[] loadingRefreshingArrowColorRes = null;

        public Options setPageParams(String page, String pageSize, int firstPage) {
            this.page = page;
            this.pageSize = pageSize;
            this.firstPage = firstPage;
            return this;
        }

        public int getLoadMoreLayoutId() {
            return loadMoreLayoutId;
        }

        public Options setLoadMoreLayoutId(int loadMoreLayoutId) {
            this.loadMoreLayoutId = loadMoreLayoutId;
            return this;
        }

        public int getNoMoreLayoutId() {
            return noMoreLayoutId;
        }

        public Options setNoMoreLayoutId(int noMoreLayoutId) {
            this.noMoreLayoutId = noMoreLayoutId;
            return this;
        }

        public int getNoDataLayoutId() {
            return noDataLayoutId;
        }

        public Options setNoDataLayoutId(int noDataLayoutId) {
            this.noDataLayoutId = noDataLayoutId;
            return this;
        }

        public int getLoadRetryLayoutId() {
            return loadRetryLayoutId;
        }

        public Options setLoadRetryLayoutId(int loadRetryLayoutId) {
            this.loadRetryLayoutId = loadRetryLayoutId;
            return this;
        }

        public int[] getLoadingRefreshingArrowColorRes() {
            return loadingRefreshingArrowColorRes;
        }

        public Options setLoadingRefreshingArrowColorRes(int[] loadingRefreshingArrowColorRes) {
            this.loadingRefreshingArrowColorRes = loadingRefreshingArrowColorRes;
            return this;
        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        if (adapter != null) {
            OnInitList onInitList = adapter.getOnInitList();
            if (onInitList != null) {
                try {
                    adapter.setDataList(onInitList.getList());
                } catch (Exception e) {
//                e.printStackTrace();
                }
            }
        }
    }
}
