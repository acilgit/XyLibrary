package com.xycode.xylibrary.uiKit.views.nicespinner;

import android.content.Context;

import com.xycode.xylibrary.unit.StringData;
import com.xycode.xylibrary.utils.Tools;

import java.util.List;

/**
 * @author angelo.marchesin
 */

public class NiceSpinnerAdapter extends NiceSpinnerBaseAdapter {

    @Override
    public List getItems() {
        return mItems;
    }

    private final List<StringData> mItems;

    public NiceSpinnerAdapter(Context context, List<StringData> items, int textColor, int backgroundSelector) {
        super(context, textColor, backgroundSelector);
        mItems = items;
    }

    //我是一个饼
    @Override
    public int getCount() {
        return mItems.size() - 1;
    }

    @Override
    public StringData getItem(int position) {
        if (position >= mSelectedIndex) {
            return mItems.get(position + 1);
        } else {
            return mItems.get(position);
        }
    }


    @Override
    public StringData getItemInDataset(int position) {
        return mItems.get(position);
    }

    @Override
    public StringData getCurrentItem() {
        return getItemInDataset(mSelectedIndex);
    }

}