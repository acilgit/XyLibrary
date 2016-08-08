package com.test.baserefreshview;

import android.content.Context;
import android.util.AttributeSet;

import com.xycode.xylibrary.base.BaseItemView;

/**
 * Created by XY on 2016-07-29.
 */
public class ListItem extends BaseItemView {

    public ListItem(Context context) {
        super(context, null);
    }

    public ListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected int getLayoutId() {
        switch (itemType) {
            case 1:
                return R.layout.list_item_icon_text;
            default:
                return R.layout.list_item_icon_text;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        switch (itemType) {
            case 1:

                break;
            default:

                break;
        }
        setImageRes(R.id.ivIcon, itemIcon);
        setText(R.id.tvName, itemName);
    }

}
