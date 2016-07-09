package com.test.baserefreshview.views;

/**
 * Created by XY on 2016/6/18.
 */

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by XY on 2016/6/17.
 */
public class XRefresher<T> {

    //    private static List<XRefresher> refreshListUtils = new ArrayList<>();

    private static final int REFRESH = 1;
    private static final int LOAD = 2;
    private static String PAGE = "page";
    private static String PAGE_SIZE = "pageSize";
    private static String TEXT_LOADING = "加载中...";


    private boolean loadMore;

    private Activity activity;
    private RefreshState state;

    private final SwipeRefreshLayout refreshView;
    private final RecyclerView recyclerView;
    private RefreshRequest refreshRequest;
    private int lastVisibleItem = 0;

    private XRefresher(Activity activity, SwipeRefreshLayout refreshView, RecyclerView recyclerView, boolean loadMore) {
        this.loadMore = loadMore;
        setRefreshViewListener();
        this.activity = activity;
        this.refreshView = refreshView;
        this.recyclerView = recyclerView;
    }

    private void setRefreshViewListener() {
        refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == getAdapter().getItemCount()) {
                    if (!state.lastPage && getAdapter().getFooterState() == XAdapter.FOOTER_MORE) {
                        getAdapter().setFooterState(XAdapter.FOOTER_LOADING);
                        getDataByRefresh(state.pageIndex + 1, state.pageDefaultSize);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            }
        });
    }

    public XRefresher(final Activity activity, SwipeRefreshLayout refreshView, RecyclerView recyclerView, boolean loadMore, @NonNull RefreshRequest refreshRequest) {
        this.loadMore = loadMore;
        this.activity = activity;
        this.refreshView = refreshView;
        this.recyclerView = recyclerView;
        this.refreshRequest = refreshRequest;
        this.state = new RefreshState(10);
        setRefreshViewListener();
    }

    public XRefresher(Activity activity, SwipeRefreshLayout refreshView, RecyclerView recyclerView, boolean loadMore, @NonNull RefreshRequest refreshRequest, int refreshPageSize) {
        this.loadMore = loadMore;
        this.activity = activity;
        this.refreshView = refreshView;
        this.recyclerView = recyclerView;
        this.refreshRequest = refreshRequest;
        this.state = new RefreshState(refreshPageSize);
        setRefreshViewListener();
    }


    /**
     * 获取小区
     *
     * @param pageSize 页面大小
     */
    private void getDataByRefresh(int pageSize) {
        getDataByRefresh(1, pageSize, REFRESH);
    }

    private void getDataByRefresh(int page, int pageSize) {
        getDataByRefresh(page, pageSize, LOAD);
    }

    private void getDataByRefresh(final int page, final int pageSize, final int refreshType) {
        HashMap<String, String> params = new HashMap<>();
        params.put(PAGE, refreshType == REFRESH ? "1" : String.valueOf(page));
        params.put(PAGE_SIZE, String.valueOf(pageSize));
        String url = refreshRequest.setRequestParamsReturnUrl(params);
        OkHttp.getInstance().postForm(url, OkHttp.getInstance().setFormBody(params), new OkHttp.OkResponse() {
            @Override
            public void handleJsonSuccess(Call call, Response response, JSONObject json) {
                List<T> newList = refreshRequest.setListData(json);
                state.setLastPage(refreshRequest.setIsLastPageWhenGotJson(json));
                final List<T> list = new ArrayList<>();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (refreshType) {
                            case REFRESH:
                                refreshView.setRefreshing(false);
                                if (state.pageIndex == 0) state.pageIndex++;
                                break;
                            default:
                                list.addAll(getAdapter().getDataList());
                                getAdapter().setFooterState(state.lastPage ? XAdapter.FOOTER_NO_MORE : XAdapter.FOOTER_MORE);
                                state.pageIndex++;
                                break;
                        }
                    }
                });
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
                    Collections.sort(list, new Comparator<T>() {
                        public int compare(T arg0, T arg1) {
                            return refreshRequest.compareTo(arg0, arg1);
                        }
                    });
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getAdapter().setDataList(list);
                        }
                    });
                }
            }

            @Override
            public void handleJsonError(Call call, Response response, JSONObject json) {
                switch (refreshType) {
                    case REFRESH:
                        refreshView.setRefreshing(false);
                        break;
                    case LOAD:
                        getAdapter().setFooterState(state.lastPage ? XAdapter.FOOTER_NO_MORE : XAdapter.FOOTER_MORE);
                        break;
                }
            }

            @Override
            protected void handleNoNetwork(Call call) {
                switch (refreshType) {
                    case REFRESH:
                        refreshView.setRefreshing(false);
                        break;
                    case LOAD:
                        getAdapter().setFooterState(state.lastPage ? XAdapter.FOOTER_NO_MORE : XAdapter.FOOTER_MORE);
                        break;
                }
            }
        });
    }

    /**
     * 刷新列表
     */
    public void refreshList() {
        refreshList(false);
    }

    public void refreshList(boolean showDialog) {
        if (showDialog) {
            if (activity instanceof BaseActivity)
                ((BaseActivity) activity).showProgressDialog(TEXT_LOADING);
        }
        if (getAdapter().getDataList().size() > 0) {
            getDataByRefresh(getAdapter().getDataList().size());
        } else {
            if (activity instanceof BaseActivity)
                ((BaseActivity) activity).showProgressDialog(TEXT_LOADING);
            getDataByRefresh(state.pageDefaultSize);
            refreshView.setRefreshing(false);
        }
    }

/*    protected Map<String, String> setParamsForUrl() {
        Map<String, String> params = new VolleyParams();
        this.url = refreshRequest.setRequestParamsReturnUrl(params);
        return params;
    }*/

    private XAdapter<T> getAdapter() {
        if (recyclerView.getAdapter() instanceof XAdapter) {
            return (XAdapter<T>) recyclerView.getAdapter();
        }
        return null;
    }

    public void resetPageParamsNames(String page, String pageSize) {
        this.PAGE = page;
        this.PAGE_SIZE = pageSize;
    }

    public static abstract class RefreshRequest<T> implements  IRefreshRequest<T>{

        /**
         * 去重，可以通过 return newItem.getId().equals(listItem.getId()); 返回结果，
         * 如果不去重就直接返回false;
         *
         * @param newItem
         * @param listItem
         * @return
         */
        protected boolean ignoreSameItem(T newItem, T listItem) {
            return false;
        }

        /**
         * 排列表中的数据，返回值：
         * -1 从大到小
         * 1 从小到大
         * 0 相同
         * 也可以通过： 如 long 的实例   Long.compareTo()来比较，item0:item1的话，默认从小到大
         * 若不进行比较可以直接返回 0
         *
         * @param item0
         * @param item1
         * @return
         */
        protected int compareTo(T item0, T item1) {
            return 0;
        }
    }


    private interface IRefreshRequest<T> {
        /**
         * 把对应要返回的url要设置的params设置好，Return url;
         *
         * @param params
         * @return
         */
        String setRequestParamsReturnUrl(HashMap<String, String> params);

        /**
         * 最后把JSON中的List return
         * @param json
         * @return
         */
        List<T> setListData(JSONObject json);

        /**
         * 处理Json 把JSON中的lastPage通过state.setLastPage() 传进去，
         * @param json
         * @return
         */
        boolean setIsLastPageWhenGotJson(JSONObject json);

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
