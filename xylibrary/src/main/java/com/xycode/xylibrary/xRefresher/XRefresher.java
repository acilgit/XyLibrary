package com.xycode.xylibrary.xRefresher;

/**
 * Created by XY on 2016/6/18.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.xycode.xylibrary.R;
import com.xycode.xylibrary.adapter.XAdapter;
import com.xycode.xylibrary.annotation.SaveState;
import com.xycode.xylibrary.base.BaseActivity;
import com.xycode.xylibrary.okHttp.OkHttp;
import com.xycode.xylibrary.okHttp.Param;
import com.xycode.xylibrary.uiKit.recyclerview.FlexibleDividerDecoration;
import com.xycode.xylibrary.uiKit.recyclerview.HorizontalDividerItemDecoration;
import com.xycode.xylibrary.utils.L;

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

    public static final int LOADER_MORE = 0;
    public static final int LOADER_LOADING = 1;
    public static final int LOADER_NO_MORE = 2;

    private static final int REFRESH = 1;
    private static final int LOAD = 2;
    private static int FIRST_PAGE = 1;
    private static String PAGE = "page";
    private static String PAGE_SIZE = "pageSize";

    private static String defaultNoDataText = "";

    private static int defaultBackgroundNoData = 1;
    private static int[] loadingColorRes = null;

    private static InitRefresher initRefresher = null;

    private int loadMoreState = LOADER_NO_MORE;

    private LoadMoreView loadMoreView;
    @SaveState
    private int background;
    @SaveState
    private boolean backgroundIsRes = false;
    @SaveState
    private int backgroundNoData;
    @SaveState
    private boolean backgroundNoDataIsRes = false;


    @SaveState
    private int hintColor;
    @SaveState
    private float hintSize;
    @SaveState
    private String hint;

    @SaveState
    private BaseActivity activity;

    private RecyclerView.LayoutManager layoutManager;

    private RefreshState state;
    private XAdapter<T> adapter;

    private SwipeRefreshLayout swipe;
    private RecyclerView recyclerView;
    private TextView textView;

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

    public static void setCustomerLoadMoreView(@LayoutRes int footerLayout) {
//        XRefresher.loaderLayout = footerLayout;
        LoadMoreView.setLayoutId(footerLayout);
    }

    public XRefresher(Context context) {
        super(context, null);
    }

    public XRefresher(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_refresher, this, true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XRefresher);

        hint = typedArray.getString(R.styleable.XRefresher_hint);
        hintSize = typedArray.getDimensionPixelSize(R.styleable.XRefresher_hintSize, 1);
        hintColor = typedArray.getColor(R.styleable.XRefresher_hintColor, 1);
        background = typedArray.getColor(R.styleable.XRefresher_bg, 1);
        if (background == 1) {
            background = typedArray.getResourceId(R.styleable.XRefresher_bg, 1);
            backgroundIsRes = background != 1;
        }
        backgroundNoData = typedArray.getColor(R.styleable.XRefresher_bgNoData, 1);
        if (backgroundNoData == 1) {
            backgroundNoData = typedArray.getResourceId(R.styleable.XRefresher_bgNoData, 1);
            if (defaultBackgroundNoData != 1 && backgroundNoData == 1)
                backgroundNoData = defaultBackgroundNoData;
            backgroundNoDataIsRes = backgroundNoData != 1;
        }
        if (hint == null) hint = "";

        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        rlMain = (CoordinatorLayout) findViewById(R.id.rlMain);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        recyclerView = (RecyclerView) findViewById(R.id.rvMain);
        loadMoreView = (LoadMoreView) findViewById(R.id.loadMoreView);
        textView = (TextView) findViewById(R.id.tvMain);

        textView.setText(hint.isEmpty() ? defaultNoDataText : hint);
        if (hintSize != 1) textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, hintSize);
        if (hintColor != 1) textView.setTextColor(hintColor);
        if (backgroundIsRes) {
            rlMain.setBackgroundResource(background);
        } else if (background != 1) {
            rlMain.setBackgroundColor(background);
        }
        if (backgroundNoDataIsRes) {
            textView.setBackgroundResource(backgroundNoData);
        } else if (backgroundNoData != 1) {
            textView.setBackgroundColor(backgroundNoData);
        }

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
        ((SwipeRefreshLayout) findViewById(R.id.swipe)).setOnRefreshListener(() -> {
            if (swipeListener != null) swipeListener.onRefresh();
            if (refreshRequest != null) refreshList();
        });
        if (loadMore) {
            setLoadMoreListener();
        }
        if (loadingColorRes != null) {
            swipe.setColorSchemeResources(loadingColorRes);
        }
        if (refreshRequest == null) {
            textView.setVisibility(GONE);
        }
    }

    /**
     * 可使用布流式布局
     * @param spanCount
     * @param orientation
     */
    public XRefresher setStaggeredGridLayoutManager(int spanCount, int orientation) {
        this.layoutManager = new StaggeredGridLayoutManager(spanCount, orientation);
        return this;
    }

    private void setLoadMoreListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean swipeMore = false;
            private boolean noScrolled = true;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int scrollState = recyclerView.getScrollState();
                L.e("scrollState --> " + scrollState);
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && swipeMore && lastVisibleItem + 2 >= getAdapter().getItemCount()) {
                    if ((!state.lastPage) && loadMoreState == LOADER_MORE) {
                        setLoadMoreState(LOADER_LOADING);
                        getDataByRefresh(state.pageIndex + 1, state.pageDefaultSize);
                    }
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem > 0
                        && adapter.getNoFilteredDataList().size() > 0
                        && noScrolled
                        && lastVisibleItem + 2 >= getAdapter().getItemCount()) {
                    if ((!state.lastPage) && loadMoreState == LOADER_MORE) {
                        setLoadMoreState(LOADER_LOADING);
                        getDataByRefresh(state.pageIndex + 1, state.pageDefaultSize);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && noScrolled) {
                    noScrolled = false;
                }
                swipeMore = dy > 0;
                if (layoutManager instanceof StaggeredGridLayoutManager) {
                    int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
                    lastVisibleItem = lastVisibleItemPositions[0];
                } else  if (layoutManager instanceof GridLayoutManager) {
                    lastVisibleItem = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                } else {
                    lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();

                }
            }
        });
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
        final int actualPage = refreshType == REFRESH ? FIRST_PAGE : page;
        params.put(PAGE, String.valueOf(actualPage));
        params.put(PAGE_SIZE, String.valueOf(postPageSize));
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
                setLoadMoreState(state.lastPage ? LOADER_NO_MORE : LOADER_MORE);
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
                textView.setVisibility(getAdapter().getNoFilteredDataList().size() == 0 ? VISIBLE : GONE);
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
                        setLoadMoreState(state.lastPage ? LOADER_NO_MORE : LOADER_MORE);
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

    public void setNoDataHint(String hint) {
        textView.setText(hint);
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
        this.state.pageIndex =page;
    }

    public void resetLastPage() {
        this.state.pageIndex =FIRST_PAGE;
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

    public static void setLoadingArrowColor(@ColorRes int... loadingColorRes) {
        XRefresher.loadingColorRes = new int[loadingColorRes.length];
        for (int i = 0; i < loadingColorRes.length; i++) {
            XRefresher.loadingColorRes[i] = loadingColorRes[i];
        }
    }


    public XAdapter.CustomHolder getHeader() {
        return getHeader(HEADER_ONE);
    }

    public XAdapter.CustomHolder getHeader(int headerKey) {
        int headerPos = adapter.getHeaderPos(headerKey);
        if (headerPos < 0) {
            return null;
        }
        XAdapter.CustomHolder holder = (XAdapter.CustomHolder) getRecyclerView().getChildViewHolder(getRecyclerView().getChildAt(headerPos));
        return holder;
    }

    public XAdapter.CustomHolder getFooter() {
        if (!getAdapter().hasFooter()) return null;
        XAdapter.CustomHolder holder = (XAdapter.CustomHolder) getRecyclerView().getChildViewHolder(getRecyclerView().getChildAt(adapter.getItemCount() - 1));
        return holder;
    }

    public static void resetPageParamsNames(String page, String pageSize, int firstPage) {
        XRefresher.PAGE = page;
        XRefresher.PAGE_SIZE = pageSize;
        XRefresher.FIRST_PAGE = firstPage;
    }

    public static void setDefaultNoDataText(String hint, @ColorRes int bgColor) {
        XRefresher.defaultNoDataText = hint;
        XRefresher.defaultBackgroundNoData = bgColor;
    }

    public void setOnLastPageListener(OnLastPageListener onLastPageListener) {
        this.onLastPageListener = onLastPageListener;
    }

    public boolean isLastPage() {
        return state.lastPage;
    }

    private void setLoadMoreState(int loadMoreState) {
        this.loadMoreState = loadMoreState;
        switch (loadMoreState) {
            case LOADER_LOADING:
                loadMoreView.show();
                break;
            default:
                loadMoreView.hide();
                break;
        }
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

    public static abstract class RefreshRequest<T> implements IRefreshRequest<T> {

        /**
         * ignore the same item in the list，use return newItem.getId().equals(listItem.getId());
         * if not,  don't override it;
         *
         * @param newItem
         * @param listItem
         * @return
         */
        protected boolean ignoreSameItem(T newItem, T listItem) {
            return false;
        }

        /**
         * reorder the list，returns:
         * -1 large to small
         * 1 small to large
         * 0 same
         * eg: long Long.compareTo(), item0:item1,default result is 1;
         * if no use, don't override if.
         *
         * @param item0
         * @param item1
         * @return
         */
        protected int compareTo(T item0, T item1) {
            return 0;
        }

        /**
         * 返回错误判断的时候会调用此方法，如果返回结果是true则不执行InitRefresher的handleError()方法
         *
         * @param call
         * @param json
         * @return if true to stop InitRefresher.handleError()
         */
        protected boolean handleError(Call call, JSONObject json) {
            return false;
        }

        /**
         * 返回错误判断的时候会调用此方法，如果返回结果是true则不执行InitRefresher的handleError()方法
         *
         * @param call
         * @param resultCode
         * @return if true to stop InitRefresher.handleAllFailureSituation()
         */
        protected boolean handleAllFailureSituation(Call call, int resultCode) {
            return false;
        }
    }

    private interface IRefreshRequest<T> {
        /**
         * return the url you need to post, and set the params in the method;
         *
         * @param params
         * @return
         */
        String setRequestParamsReturnUrl(Param params);

        /**
         * handle the JSON and get the List from the json, then return it.
         *
         * @param json
         * @return
         */
        List<T> setListData(JSONObject json);
    }

    /**
     * 刷新监听
     */
    public interface OnSwipeListener {
        /**
         * Refresh
         */
        void onRefresh();
    }

    /**
     * 最后一页
     */
    public interface OnLastPageListener {
        void receivedList(boolean isLastPage);
    }

    /**
     * 设置Refresher的默认选项
     */
    public interface InitRefresher {

        /**
         * 默认的结果出错操作，例如toast
         *
         * @param call
         * @param json
         */
        void handleError(Call call, JSONObject json);

        /**
         * 默认的所有出错的操作，例如 关闭LoadingDialog显示
         *
         * @param call
         * @param resultCode 结果值
         */
        void handleAllFailureSituation(Call call, int resultCode);

        /**
         * 设置默认的Header
         *
         * @return
         */
        boolean addDefaultHeader();

        /**
         * 设置默认的参数
         *
         * @return
         */
        boolean addDefaultParam();
    }

    public static void init(InitRefresher initRefresher) {
        XRefresher.initRefresher = initRefresher;
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
}
