package com.xycode.xylibrary.xRefresher;

/**
 * Created by XY on 2016/6/18.
 */

        import android.content.Context;
        import android.content.res.TypedArray;
        import android.os.Parcelable;
        import android.support.annotation.ColorRes;
        import android.support.annotation.DimenRes;
        import android.support.annotation.LayoutRes;
        import android.support.annotation.NonNull;
        import android.support.design.widget.CoordinatorLayout;
        import android.support.v4.widget.SwipeRefreshLayout;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.StaggeredGridLayoutManager;
        import android.util.AttributeSet;
        import android.util.TypedValue;
        import android.view.LayoutInflater;
        import android.widget.TextView;

        import com.alibaba.fastjson.JSONObject;
        import com.xycode.xylibrary.R;
        import com.xycode.xylibrary.adapter.CustomHolder;
        import com.xycode.xylibrary.adapter.OnInitList;
        import com.xycode.xylibrary.adapter.XAdapter;
        import com.xycode.xylibrary.annotation.SaveState;
        import com.xycode.xylibrary.base.BaseActivity;
        import com.xycode.xylibrary.okHttp.OkHttp;
        import com.xycode.xylibrary.okHttp.Param;
        import com.xycode.xylibrary.uiKit.recyclerview.FlexibleDividerDecoration;
        import com.xycode.xylibrary.uiKit.recyclerview.HorizontalDividerItemDecoration;

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

//    public static final int LOADER_MORE = 0;
//    public static final int LOADER_LOADING = 1;
//    public static final int LOADER_NO_MORE = 2;

    private static final int REFRESH = 1;
    private static final int LOAD = 2;

    private static InitRefresher initRefresher = null;

//    private int loadMoreState = XAdapter.LOADER_NO_MORE;

    //    private LoadMoreView loadMoreView;
    @SaveState
    private int background;
    @SaveState
    private boolean backgroundIsRes = false;
    @SaveState
    private int backgroundNoData;
    @SaveState
    private boolean backgroundNoDataIsRes = false;

//    @SaveState
//    private int hintColor;
//    @SaveState
//    private float hintSize;
//    @SaveState
//    private String hint;

    @SaveState
    private BaseActivity activity;

    private RecyclerView.LayoutManager layoutManager;

    @SaveState
    private RefreshState state;
    private XAdapter<T> adapter;

    private SwipeRefreshLayout swipe;
    private RecyclerView recyclerView;
//    private TextView textView;

    private RefreshRequest refreshRequest;
    private OnLastPageListener onLastPageListener;

    private HorizontalDividerItemDecoration horizontalDividerItemDecoration;
    @SaveState
    private int dividerSize = 0;

    @SaveState
    private int lastVisibleItem = 0;
    @SaveState
    private boolean loadMore;
    private CoordinatorLayout rlMain;
    private OnSwipeListener swipeListener;

    private static Options options;

   /* public static void setCustomerLoadMoreView(@LayoutRes int footerLayout) {
        LoadMoreView.setLayoutId(footerLayout);
    }*/

    public XRefresher(Context context) {
        super(context, null);
    }

    public XRefresher(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_refresher, this, true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XRefresher);

//        hint = typedArray.getString(R.styleable.XRefresher_hint);
//        hintSize = typedArray.getDimensionPixelSize(R.styleable.XRefresher_hintSize, 1);
//        hintColor = typedArray.getColor(R.styleable.XRefresher_hintColor, 1);
        background = typedArray.getColor(R.styleable.XRefresher_bg, 1);
        if (background == 1) {
            background = typedArray.getResourceId(R.styleable.XRefresher_bg, 1);
            backgroundIsRes = background != 1;
        }
      /*  backgroundNoData = typedArray.getColor(R.styleable.XRefresher_bgNoData, 1);
        if (backgroundNoData == 1) {
            backgroundNoData = typedArray.getResourceId(R.styleable.XRefresher_bgNoData, 1);
            if (options.defaultBackgroundColorNoData != 0 && backgroundNoData == 1)
                backgroundNoData = options.defaultBackgroundColorNoData;
            backgroundNoDataIsRes = backgroundNoData != 1;
        }*/
//        if (hint == null) hint = "";

        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        rlMain = (CoordinatorLayout) findViewById(R.id.rlMain);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        recyclerView = (RecyclerView) findViewById(R.id.rvMain);
//        loadMoreView = (LoadMoreView) findViewById(R.id.loadMoreView);
//        textView = (TextView) findViewById(R.id.tvMain);

//        textView.setText(hint.isEmpty() ? options.defaultNoDataText : hint);
//        if (hintSize != 1) textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, hintSize);
//        if (hintColor != 1) textView.setTextColor(hintColor);
        if (backgroundIsRes) {
            rlMain.setBackgroundResource(background);
        } else if (background != 1) {
            rlMain.setBackgroundColor(background);
        }
/*        if (backgroundNoDataIsRes) {
            textView.setBackgroundResource(backgroundNoData);
        } else if (backgroundNoData != 1) {
            textView.setBackgroundColor(backgroundNoData);
        }*/

    }

    public void setup(BaseActivity activity, XAdapter<T> adapter, boolean loadMore, OnSwipeListener swipeListener, RefreshRequest refreshRequest) {
        init(activity, adapter, loadMore, swipeListener, refreshRequest, 10);
    }

    public void setup(BaseActivity activity, XAdapter<T> adapter, boolean loadMore, OnSwipeListener swipeListener, RefreshRequest refreshRequest, int refreshPageSize) {
        init(activity, adapter, loadMore, swipeListener, refreshRequest, refreshPageSize);
    }

    public void setup(BaseActivity activity, XAdapter<T> adapter, boolean loadMore, @NonNull RefreshRequest refreshRequest) {
        init(activity, adapter, loadMore, null, refreshRequest, 10);
    }

    public void setup(BaseActivity activity, XAdapter<T> adapter, boolean loadMore, @NonNull RefreshRequest refreshRequest, int refreshPageSize) {
        init(activity, adapter, loadMore, null, refreshRequest, refreshPageSize);
    }

    private void init(BaseActivity activity, XAdapter<T> adapter, boolean loadMore, final OnSwipeListener swipeListener, final RefreshRequest refreshRequest, int refreshPageSize) {
        layoutManager = layoutManager == null ? new LinearLayoutManager(activity) : layoutManager;
        recyclerView.setLayoutManager(layoutManager);
        this.loadMore = loadMore;
        this.activity = activity;
        this.refreshRequest = refreshRequest;
        this.swipeListener = swipeListener;
        this.state = new RefreshState(refreshPageSize);
        this.adapter = adapter;
        this.recyclerView.setAdapter(adapter);
        this.adapter.setUseDefaultLoaderLayout(true);
        ((SwipeRefreshLayout) findViewById(R.id.swipe)).setOnRefreshListener(() -> {
            if (swipeListener != null) swipeListener.onRefresh();
            if (refreshRequest != null) refreshList();
        });
        if (loadMore) {
            adapter.setLoadingLayout(options.loadMoreLayoutId);
            adapter.setNoMoreLayoutId(options.noMoreLayoutId);
            adapter.setNoMoreLayoutId(options.loadRetryLayoutId);
            adapter.setLoadMoreListener(()->{
//                setLoadMoreState(XAdapter.LOADER_LOADING);
                getDataByRefresh(state.pageIndex + 1, state.pageDefaultSize);
            });
        }
        if (options.loadingRefreshingArrowColorRes != null) {
            swipe.setColorSchemeResources(options.loadingRefreshingArrowColorRes);
        }
     /*   if (refreshRequest == null) {
            textView.setVisibility(GONE);
        }*/
    }

    /**
     * 可使用布流式布局
     *
     * @param spanCount
     * @param orientation
     */
    public XRefresher setStaggeredGridLayoutManager(int spanCount, int orientation) {
        this.layoutManager = new StaggeredGridLayoutManager(spanCount, orientation);
        return this;
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
        boolean addDefaultParam = false;
        boolean addDefaultHeader = true;
        if (initRefresher != null) {
            addDefaultParam = initRefresher.addDefaultParam();
            addDefaultHeader = initRefresher.addDefaultHeader();
        }
        activity.postForm(url, OkHttp.setFormBody(params, addDefaultParam), null, addDefaultHeader, new OkHttp.OkResponseListener() {
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
                if (onLastPageListener != null) onLastPageListener.receivedList(state.lastPage);
                if (refreshType == REFRESH) {
                    adapter.refreshedNoData();
                }
//                textView.setVisibility(getAdapter().getNoFilteredDataList().size() == 0 ? VISIBLE : GONE);
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

    /*public void setNoDataHint(String hint) {
        textView.setText(hint);
    }*/

    /**
     * refresh list
     */

    public void refresh() {
        swipeRefresh();
        refreshList();
    }

    public void swipeRefresh() {
        if (swipeListener != null) {
//            swipe.setRefreshing(true);
            swipeListener.onRefresh();
        }
    }

    public void refreshList() {
        refreshList(false);
    }

    private void refreshList(boolean showDialog) {
        if (refreshRequest != null) {
//            swipe.setRefreshing(true);
            if (getAdapter().getNoFilteredDataList().size() > 0) {
                getDataByRefresh(getAdapter().getNoFilteredDataList().size());
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


    public void setRecyclerViewDivider(@ColorRes int dividerColor, @DimenRes int dividerHeight) {
        setRecyclerViewDivider(dividerColor, dividerHeight, R.dimen.zero, R.dimen.zero);
    }

    /**
     * use after xRefresher setup
     *
     * @param dividerColor
     * @param dividerHeight
     * @param marginLeft
     * @param marginRight
     */
    public void setRecyclerViewDivider(@ColorRes int dividerColor, @DimenRes int dividerHeight, @DimenRes int marginLeft, @DimenRes int marginRight) {
        HorizontalDividerItemDecoration.Builder builder = new HorizontalDividerItemDecoration.Builder(activity)
                .visibilityProvider(this)
                .sizeProvider(this)
                .colorResId(dividerColor)/*.sizeResId(dividerHeight)*/
                .marginResId(marginLeft, marginRight);
        horizontalDividerItemDecoration = builder.build();
        dividerSize = activity.getResources().getDimensionPixelSize(dividerHeight);
        recyclerView.addItemDecoration(horizontalDividerItemDecoration);
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

   /* public static void resetPageParamsNames(String page, String pageSize, int firstPage) {
        XRefresher.PAGE = page;
        XRefresher.PAGE_SIZE = pageSize;
        XRefresher.FIRST_PAGE = firstPage;
    }*/

    public void setOnLastPageListener(OnLastPageListener onLastPageListener) {
        this.onLastPageListener = onLastPageListener;
    }

    public boolean isLastPage() {
        return state.lastPage;
    }

  /*  private void setLoadMoreState(int loadMoreState) {
        this.loadMoreState = loadMoreState;
        switch (loadMoreState) {
            case XAdapter.LOADER_LOADING:
//                adapter.setFooter();
//                loadMoreView.show();
                break;
            default:
//                adapter.setFooter();
//                loadMoreView.hide();
                break;
        }
    }*/

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

        public RefreshState(int pageDefaultSize) {
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
//        int defaultBackgroundColorNoData = 0;

        public Options setPageParams(String page,String pageSize, int firstPage) {
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

      /*  public int getDefaultBackgroundColorNoData() {
            return defaultBackgroundColorNoData;
        }*/

       /* public Options setDefaultBackgroundColorNoData(int defaultBackgroundColorNoData) {
            this.defaultBackgroundColorNoData = defaultBackgroundColorNoData;
            return this;
        }*/
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        if (adapter!= null) {
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
