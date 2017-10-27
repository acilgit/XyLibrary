package com.xycode.xylibrary.utils.serverApiHelper;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.xycode.xylibrary.R;
import com.xycode.xylibrary.adapter.CustomHolder;
import com.xycode.xylibrary.adapter.XAdapter;
import com.xycode.xylibrary.uiKit.recyclerview.XLinearLayoutManager;
import com.xycode.xylibrary.unit.ViewTypeUnit;

import java.util.List;

/**
 * Created by Administrator on 2016/9/6 0006.
 *
 */
public class ServerSelectDialog implements View.OnClickListener {

    private ServerControllerActivity activity;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LinearLayout layout;
    private EditText et;
    //    private TextInputLayout tl;
    private RecyclerView rv;
    private List<String> serverList;
    private final ApiHelper api;

    public ServerSelectDialog(ServerControllerActivity activity) {
        this.activity = activity;
        api = activity.getApi();
        builder = new AlertDialog.Builder(activity);
         layout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.dialog_base_server, null);
//        tl = (TextInputLayout) layout.findViewById(R.id.tl);
//        tl.getEditText().setText(api.getServer());
        et = (EditText) layout.findViewById(R.id.et);
        et.setText(api.getServer());
        rv = (RecyclerView) layout.findViewById(R.id.rv);
        rv.setLayoutManager(new XLinearLayoutManager(activity));
        serverList = api.getStoredServerList();

        rv.setAdapter(new XAdapter<String>(activity, ()->serverList) {
            @Override
            public void creatingHolder(CustomHolder holder, ViewTypeUnit viewTypeUnit) {
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
                api.setServerUrl(item);
                dismiss();
                activity.finish();
            }
        });
        Button btn = (Button) layout.findViewById(R.id.btn);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn) {
            String string = et.getText().toString();
            if (!string.isEmpty()) {
                String url = et.getText().toString();
                api.setServerUrl(url);
                if (!serverList.contains(url)) {
                    serverList.add(url);
                    api.setStoredServerList(serverList);
                }
            }
            dismiss();
            activity.finish();
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

}
