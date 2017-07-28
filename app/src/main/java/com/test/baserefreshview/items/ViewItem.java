package com.test.baserefreshview.items;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import com.test.baserefreshview.R;
import com.xycode.xylibrary.base.BaseItemView;

//import android.support.design.widget.TextInputLayout;

/**
 * Created by XY on 2016-07-29.
 * 功能性组件
 */
public class ViewItem extends BaseItemView {

    private final int VIEW_ITEM_COUNT = 0;
    private final int VIEW_ITEM_TITLE_SELECTOR = 1;
    private final int editTextLayout = 2;
    private final int dateSelector = 3;
    private final int textDateTime = 4;
    //输入类型：itemNum 129：输入文字密码  1：明文  不指定：数字    小数：0x00002000
    private final int infoInput = 5;
    private final int selectUserType = 6;
    private final int selectPicture = 7;
    private final int upDownText = 8;
    private final int iconTextSelector = 9;
    private final int visibleSwitch = 10;
    private final int scanPicture = 11;

    public ViewItem(Context context) {
        super(context, null);
    }

    public ViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setViews(int type) {
        switch (type) {
            case VIEW_ITEM_COUNT:
                setText(R.id.etAmount, String.valueOf(itemCount));
                final EditText editText = getView(R.id.etAmount);
                OnClickListener clickListener = view -> {
                    String text = editText.getText().toString();
                    int count = 0;
                    try {
                        count = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                    }
                    switch (view.getId()) {
                        case R.id.tvPlus:
                            if (itemMax != -1) {
                                itemCount = (count >= itemMax ? itemMax : ++count);
                            } else {
                                itemCount = ++count;
                            }
                            editText.setText(String.valueOf(itemCount));
                            if (onViewSenseListener != null) {
                                onViewSenseListener.sense(view, itemCount);
                            }
                            break;
                        case R.id.tvMinus:
                            if (itemMin != -1) {
                                itemCount = (count <= itemMin ? itemMin : --count);
                            } else {
                                itemCount = (count <= 0 ? 0 : --count);
                            }
                            editText.setText(String.valueOf(itemCount));
                            if (onViewSenseListener != null) {
                                onViewSenseListener.sense(view, itemCount);
                            }
                            break;
                        default:
                            break;
                    }
                };
                setClick(R.id.tvPlus, clickListener);
                setClick(R.id.tvMinus, clickListener);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.toString().isEmpty()) {
                            itemCount = itemMin == -1 ? 0 : itemMin;
                            editText.setText(String.valueOf(itemCount));
                        }
                    }
                });
                break;
            case VIEW_ITEM_TITLE_SELECTOR:
                OnClickListener onClickListener = view -> {
                    setTitleSelectorViewIndex(view.getId() == R.id.tvLeft ? 0 : 1);
                    if (onViewSenseListener != null) {
                        onViewSenseListener.sense(view, itemIndex);
                    }
                };
                setClick(R.id.tvLeft, onClickListener);
                setClick(R.id.tvRight, onClickListener);
                setText(R.id.tvLeft, itemName);
                setText(R.id.tvRight, itemContent);
                setTitleSelectorViewIndex(itemIndex);
                break;
            case dateSelector:
//
                break;
            case textDateTime:
                break;
            case editTextLayout:
                break;
            case infoInput:
                break;
            case selectUserType:
                break;
            case selectPicture:
            case scanPicture:
                break;
            case upDownText:
                break;
            case iconTextSelector:
                break;
            case visibleSwitch:
                break;
            default:
                break;
        }
    }

    private void setTitleSelectorViewIndex(int index) {
        itemIndex = index;
        if (itemIndex == 0) {
            getView(R.id.tvLeft).setSelected(true);
            getView(R.id.tvRight).setSelected(false);
            setTextColor(R.id.tvLeft, R.color.colorAccent);
            setTextColor(R.id.tvRight, R.color.white);
        } else {
            getView(R.id.tvRight).setSelected(true);
            getView(R.id.tvLeft).setSelected(false);
            setTextColor(R.id.tvRight, R.color.colorAccent);
            setTextColor(R.id.tvLeft, R.color.white);
        }
    }

    @Override
    protected int getLayoutId(int type) {
        switch (type) {
            case VIEW_ITEM_COUNT:
                return R.layout.view_item_count;
            case VIEW_ITEM_TITLE_SELECTOR:
                return R.layout.view_item_title_selector;
            case editTextLayout:
//                return R.layout.view_item_text_input;
            case dateSelector:
//                return R.layout.view_item_date_selector;
            case textDateTime:
//                return R.layout.view_item_date_time;
            case infoInput:
//                return R.layout.xitem_account_input;
            case selectUserType:
//                return R.layout.view_item_select_user_ype;
            case selectPicture:
//                return R.layout.view_item_select_picture;
            case scanPicture:
//                return R.layout.view_item_scan_picture;
            case upDownText:
//                return R.layout.view_item_up_down_text;
            case iconTextSelector:
//                return R.layout.view_item_icon_text_selector;
            case visibleSwitch:
//                return R.layout.view_item_visible_switch;
            default:
                return R.layout.layout_blank;
        }
    }

    /**
     * view setTag is this.getId()
     *
     * @param viewId
     * @param listener
     * @return
     */
    public BaseItemView setClickWithParentIdInTag(int viewId, OnClickListener listener) {
        View view = getView(viewId);
        if (view != null) {
            view.setTag(viewId, this.getId());
        }
        return super.setClick(viewId, listener);
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemIndex(int index) {
        itemIndex = index;
        setTitleSelectorViewIndex(index);
//        invalidate();
    }

    public void setItemMax(int max) {
        itemMax = max;
    }

    public void onViewSense(View view, Object object) {
        onViewSenseListener.sense(view, object);
    }


    @Override
    protected int setItemTypeEnumStyle() {
        return R.styleable.ViewItem_viewType;
    }

    @Override
    protected int[] setExtendEnumStyle() {
        return R.styleable.ViewItem;
    }

    private int dp2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }
}
