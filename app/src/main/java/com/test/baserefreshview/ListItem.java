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
    protected int getLayoutId(int type) {
        switch (type) {
            case 1:
                return R.layout.list_item_icon_text;
            default:
            return R.layout.list_item_text;
        }
    }

    @Override
    public void setViews(int type) {
        switch (type) {
            case 1:
                setImageRes(R.id.ivIcon, itemIcon);
                break;
            default:

                break;
        }
        setText(R.id.tvName, itemName);
    }

    @Override
    protected int setItemTypeEnumStyle() {
        return R.styleable.ListItem_type;
    }

    @Override
    protected int[] setExtendEnumStyle() {
        return R.styleable.ListItem;
    }
}
