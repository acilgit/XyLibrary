package com.xycode.xylibrary.uiKit.views.nicespinner;

import android.content.Context;
import android.widget.ListAdapter;

import com.xycode.xylibrary.unit.StringData;

import java.util.List;

/**
 * @author angelo.marchesin
 */

public class NiceSpinnerAdapterWrapper extends NiceSpinnerBaseAdapter {

    public ListAdapter getBaseAdapter() {
        return mBaseAdapter;
    }

    private final ListAdapter mBaseAdapter;

    public NiceSpinnerAdapterWrapper(Context context, ListAdapter toWrap, int textColor, int backgroundSelector) {
        super(context, textColor, backgroundSelector);
        mBaseAdapter = toWrap;
    }

    @Override
    public int getCount() {
        return mBaseAdapter.getCount() - 1;
    }

    @Override
    public List getItems() {
        return null;
    }

    @Override
    public StringData getItem(int position) {
        if (position >= mSelectedIndex) {
            return (StringData) mBaseAdapter.getItem(position + 1);
        } else {
            return (StringData) mBaseAdapter.getItem(position);
        }
    }

    @Override
    public StringData getItemInDataset(int position) {
        return (StringData) mBaseAdapter.getItem(position);
    }

    @Override
    public StringData getCurrentItem() {
        return getItemInDataset(mSelectedIndex);
    }
}