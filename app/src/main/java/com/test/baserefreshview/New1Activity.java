package com.test.baserefreshview;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.test.baserefreshview.items.MyFragmentAdapter;
import com.xycode.xylibrary.utils.LogUtil.L;
import com.xycode.xylibrary.utils.TS;

public class New1Activity extends ABaseActivity {

    private ViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new1);
        Button btn = (Button) findViewById(R.id.btn);
        vp = (ViewPager) findViewById(R.id.vpMain);

        HotItem hotItem = new HotItem();
        hotItem.setAge(10);
        hotItem.setName("Acil");

        vp.setAdapter(new MyFragmentAdapter(getSupportFragmentManager()));

        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hotItem.setAge(hotItem.getAge()+1);
                        TS.show(hotItem.toString());
                      /*  postEvent("anEventName", "ABC", obj -> {
                            L.e((String) obj);
                            return "AA";
                        });*/
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
