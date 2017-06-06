package com.test.baserefreshview;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.xycode.xylibrary.utils.LogUtil.L;

public class New1Activity extends ABaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new1);
        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postEvent("anEventName", "ABC", obj -> {
                            L.e((String) obj);
                            return "AA";
                        });
                    }
                }
        );
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    protected AlertDialog setLoadingDialog() {
        return null;
    }


    public void lookPhoto(View view) {
        start(PhotoDemoActivity.class);
    }
}
