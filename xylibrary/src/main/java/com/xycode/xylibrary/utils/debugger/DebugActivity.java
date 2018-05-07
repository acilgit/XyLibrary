package com.xycode.xylibrary.utils.debugger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.xycode.xylibrary.R;
import com.xycode.xylibrary.Xy;
import com.xycode.xylibrary.adapter.CustomHolder;
import com.xycode.xylibrary.adapter.XAdapter;
import com.xycode.xylibrary.base.XyBaseActivity;
import com.xycode.xylibrary.okHttp.Param;
import com.xycode.xylibrary.unit.ViewTypeUnit;
import com.xycode.xylibrary.utils.TS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理请求的Debug页面
 *
 * @author xiuye
 */
public class DebugActivity extends XyBaseActivity {

    public static final String DEBUG_KEY = "DEBUG_KEY";
    public static final String PARAMS_JSON = "PARAMS_JSON";
    public static final String POST_IS_FINISH = "POST_IS_FINISH";
    public static final String POST_BEGIN = "POST_BEGIN";
    /**
     *
     */
    public static final String POST_URL = "POST_URL";
    private static DebugActivity instance;
    private boolean postBegin;

    public static DebugActivity getInstance() {
        return instance;
    }

    private Param param;

    private DebugItem debugItem;

    private XAdapter<ParamItem> adapter = null;
    /*public static Param getParam() {
        return param;
    }

    public static void setParam(Param param) {
        DebugActivity.param = param;
    }*/

    private static Map<String, DebugItem> debugItems = new ConcurrentHashMap<>();

    public static DebugItem addDebugItem(String url) {
        DebugItem debugItem = new DebugItem(url);
        debugItems.put(debugItem.getKey(), debugItem);
        return debugItem;
    }

    public static DebugItem getDebugItem(String key) {
        return debugItems.get(key);
    }

    /**
     * 修改参数
     *
     * @param param
     */
    public static void startThis(String debugKey, Param param) {
        Intent intent = new Intent(Xy.getContext(), DebugActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(DebugActivity.DEBUG_KEY, debugKey)
                .putExtra(DebugActivity.POST_BEGIN, true);
        getDebugItem(debugKey).setParam(param);
        Xy.getContext().startActivity(intent);
    }

    public static void startThis(String debugKey) {
        Intent intent = new Intent(Xy.getContext(), DebugActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(DebugActivity.DEBUG_KEY, debugKey);
//        getDebugItem(debugKey);
        Xy.getContext().startActivity(intent);
    }

    @Override
    protected int setActivityLayout() {
        return R.layout.activity_debug;
    }

    @Override
    protected void initOnCreate(Bundle savedInstanceState) {
        DebugActivity.instance = this;
//        param  = JSON.parseObject(getIntent().getStringExtra(PARAMS_JSON), Param.class);
        String key = getIntent().getStringExtra(DEBUG_KEY);
        debugItem = getDebugItem(key);

        /* 删除已经完成的结果 */
      /*  for (Map.Entry<String, DebugItem> itemEntry : debugItems.entrySet()) {
            if (itemEntry.getValue().isPostFinished()) {
                debugItems.remove(itemEntry.getKey());
            }
        }*/

        if (debugItem == null) {
            TS.show(getThis(), "Error: No DebugItem [" + key + "]", null);
            finish();
            return;
        }

        /* 修改参数 */
        postBegin = getIntent().getBooleanExtra(DebugActivity.POST_BEGIN, false);
        if (postBegin) {
            rootHolder().setVisibility(R.id.rv, View.VISIBLE)
                    .setVisibility(R.id.scrollView, View.GONE);
            RecyclerView rv = rootHolder().getRecyclerView(R.id.rv);
            rv.setLayoutManager(new LinearLayoutManager(getThis()));
            adapter = new XAdapter<ParamItem>(getThis(), () -> {
                List list = new ArrayList();
                for (Map.Entry<String, String> entry : debugItem.getParam().entrySet()) {
                    list.add(new ParamItem(entry.getKey(), entry.getValue()));
                }
                return list;
            }) {
                @Override
                protected ViewTypeUnit getViewTypeUnitForLayout(ParamItem item) {
                    return new ViewTypeUnit(0, R.layout.item_debug_param);
                }

                @Override
                public void creatingHolder(CustomHolder holder, ViewTypeUnit viewTypeUnit) throws Exception {
                    ((EditText) holder.getView(R.id.etName)).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            adapter.getShowingList().get(holder.getAdapterPosition()).setKey(s.toString());
                        }
                    });
                    ((EditText) holder.getView(R.id.etValue)).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            adapter.getShowingList().get(holder.getAdapterPosition()).setValue(s.toString());
                        }
                    });
                   /* ((EditText) holder.getView(R.id.etName)).setOnFocusChangeListener((v, hasFocus) -> {
                        if (!hasFocus) {
                            adapter.getShowingList().get(holder.getAdapterPosition()).setKey(holder.getText(R.id.etName));
                        }
                    });
                    ((EditText) holder.getView(R.id.etValue)).setOnFocusChangeListener((v, hasFocus) -> {
                        if (!hasFocus) {
                            adapter.getShowingList().get(holder.getAdapterPosition()).setValue(holder.getText(R.id.etValue));
                        }
                    });*/
                }

                @Override
                public void bindingHolder(CustomHolder holder, List<ParamItem> dataList, int pos) throws Exception {
                    holder.setText(R.id.etName, dataList.get(pos).getKey())
                            .setText(R.id.etValue, dataList.get(pos).getValue());
                }

                @Override
                protected void creatingFooter(CustomHolder holder) {
                    holder.setClick(R.id.tvAdd, v -> {
                        adapter.addItem(new ParamItem("", ""));
                    });
                }
            };
            adapter.setFooter(R.layout.footer_debug);
            adapter.setShowNoDataFooter(true);
            rv.setAdapter(adapter);

        } else {
            rootHolder().setVisibility(R.id.rv, View.GONE)
                    .setVisibility(R.id.scrollView, View.VISIBLE);

        }

        rootHolder().setText(R.id.tvTitle, "[" + (postBegin ? "Param" : "Result") + " Debug] " + (postBegin ? "" : debugItem.getUrl()));
        if(postBegin) {
            ((EditText) rootHolder().getView(R.id.etUrl)).setText(debugItem.getUrl());
        }else{
            rootHolder().setVisibility(R.id.etUrl, View.GONE);
        }
        ((EditText) rootHolder().getView(R.id.et)).setText(debugItem.getJson());
        rootHolder().setClick(R.id.tvCancel, v -> {
            setStatus();
            finish();
        }).setClick(R.id.tvCommit, v -> {
            if (postBegin) {
                Param param = new Param();
                for (ParamItem paramItem : adapter.getShowingList()) {
                    if (paramItem.getKey() != null && !TextUtils.isEmpty(paramItem.getKey().trim())) {
                        param.add(paramItem.getKey().trim(), paramItem.getValue());
                    }
                }
                debugItem.setParam(param);
                debugItem.setUrl(((EditText) rootHolder().getView(R.id.etUrl)).getEditableText().toString());
            } else {
                debugItem.setJsonModify(rootHolder().getText(R.id.et));
            }
            setStatus();
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        setStatus();
//        if (!debugItem.isPostFinished())
//            debugItem.setPostFinished(true);
        super.onBackPressed();

    }

    private void setStatus() {
        debugItem.setPostBegun(postBegin);
        debugItem.setPostFinished(!postBegin);
        hideSoftInput();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected AlertDialog setLoadingDialog() {
        return null;
    }

    static class ParamItem {
        String key;
        String value;

        public ParamItem(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
