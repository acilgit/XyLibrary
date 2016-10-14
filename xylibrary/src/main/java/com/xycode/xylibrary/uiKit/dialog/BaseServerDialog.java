package com.xycode.xylibrary.uiKit.dialog;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONArray;
import com.xycode.xylibrary.R;
import com.xycode.xylibrary.adapter.XAdapter;
import com.xycode.xylibrary.unit.ViewTypeUnit;
import com.xycode.xylibrary.utils.ShareStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/6 0006.
 *
 */
public abstract class BaseServerDialog implements View.OnClickListener {

    private static ShareStorage storage;
    private static final String serverSP = "serverSP";
    private static final String SERVER_LIST = "serverList";

    private static ShareStorage getStorage(Context context) {
        if (storage == null) {
            storage = new ShareStorage(context, serverSP);
        }
        return storage;
    }

    private Activity context;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LinearLayout layout;
    private TextInputLayout tl;
    private RecyclerView rv;
    private List<String> serverList;

    public BaseServerDialog(Activity context) {
        this.context = context;
        builder = new AlertDialog.Builder(context);
        layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog_base_server, null);
        tl = (TextInputLayout) layout.findViewById(R.id.tl);
        tl.getEditText().setText(getServerUrl());

        rv = (RecyclerView) layout.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(context));
        serverList = new ArrayList<>();
        String list = getStorage(context).getString(SERVER_LIST);
        if (list.isEmpty()) {
            serverList.addAll(defaultServerUrlList());
        } else {
            serverList = JSONArray.parseArray(getStorage(context).getString(SERVER_LIST), String.class);
        }

        rv.setAdapter(new XAdapter<String>(context, serverList) {
            @Override
            public void creatingHolder(CustomHolder holder, List<String> dataList, ViewTypeUnit viewTypeUnit) {
                holder.setClick(R.id.tv);
            }

            @Override
            public void bindingHolder(CustomHolder holder, List<String> dataList, int pos) {
                holder.setText(R.id.tv, dataList.get(pos));
            }

            @Override
            protected ViewTypeUnit getViewTypeUnitForLayout(String item) {
                return new ViewTypeUnit(0, R.layout.item_server_url);
            }

            @Override
            protected void handleItemViewClick(CustomHolder holder, String item, int viewId, ViewTypeUnit viewTypeUnit) {
                setServerUrl(item);
                dismiss();
            }
        });
        Button btnNext = (Button) layout.findViewById(R.id.btn);
        btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn) {
            String string = tl.getEditText().getText().toString();
            if (!string.isEmpty()) {
                String url = tl.getEditText().getText().toString();
                if (!serverList.contains(url)) {
                    serverList.add(0, url);
                    getStorage(context).put(SERVER_LIST, JSONArray.toJSONString(serverList));
                }
                setServerUrl(url);
            }
            dismiss();

        }
    }

    /**
     * use this method to show dialog, do not use getBuilder to show
     */
    public void show() {
        if (dialog == null) {
            dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);//点击外部是否消失
        }
        dialog.show();
        dialog.getWindow().setContentView(layout);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    protected abstract void setServerUrl(String selectedUrl);

    protected abstract String getServerUrl();

    protected abstract List<String> defaultServerUrlList();
}
