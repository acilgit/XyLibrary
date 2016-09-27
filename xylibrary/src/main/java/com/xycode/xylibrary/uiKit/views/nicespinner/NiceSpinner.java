package com.xycode.xylibrary.uiKit.views.nicespinner;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xycode.xylibrary.R;
import com.xycode.xylibrary.interfaces.Interfaces;
import com.xycode.xylibrary.unit.StringData;
import com.xycode.xylibrary.utils.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * @author angelo.marchesin
 */
@SuppressWarnings("unused")
public class NiceSpinner<T> extends TextView {

    private static final int MAX_LEVEL = 10000;
    private static final int DEFAULT_ELEVATION = 16;
    private static final String INSTANCE_STATE = "instance_state";
    private static final String SELECTED_INDEX = "selected_index";
    private static final String IS_POPUP_SHOWING = "is_popup_showing";
    private static final float defaultNarrowSize = 0.5f;
    private int selectedIndex;
    private Drawable drawable;
    private PopupWindow popupWindow;
    private ListView listView;
    private NiceSpinnerBaseAdapter adapter;
    private AdapterView.OnItemClickListener onItemClickListener;
    private AdapterView.OnItemSelectedListener onItemSelectedListener;
    private boolean isArrowHide;
    private int textColor;
    private int backgroundSelector;

    @SuppressWarnings("ConstantConditions")
    public NiceSpinner(Context context) {
        super(context);
        init(context, null);
    }

    public NiceSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NiceSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(SELECTED_INDEX, selectedIndex);
        if (popupWindow != null) {
            bundle.putBoolean(IS_POPUP_SHOWING, popupWindow.isShowing());
            dismissDropDown();
        }
        return bundle;
    }


    @Override
    public void onRestoreInstanceState(Parcelable savedState) {
        if (savedState instanceof Bundle) {
            Bundle bundle = (Bundle) savedState;
            selectedIndex = bundle.getInt(SELECTED_INDEX);
            if (adapter != null) {
                setText(adapter.getItemInDataset(selectedIndex).getString());
                adapter.notifyItemSelected(selectedIndex);
            }

            if (bundle.getBoolean(IS_POPUP_SHOWING)) {
                if (popupWindow != null) {
                    // Post the show request into the looper to avoid bad token exception
                    post(this::showDropDown);
                }
            }
            savedState = bundle.getParcelable(INSTANCE_STATE);
        }
        super.onRestoreInstanceState(savedState);
    }

    private void init(Context context, AttributeSet attrs) {
        Resources resources = getResources();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NiceSpinner);
        int defaultPadding = resources.getDimensionPixelSize(R.dimen.one_and_a_half_grid_unit);

        setGravity(Gravity.CENTER);
        /*setPadding(resources.getDimensionPixelSize(R.dimen.three_grid_unit), defaultPadding, defaultPadding,
            defaultPadding);*/
        setPadding(0, 0, 0, 0);
        setClickable(true);

        backgroundSelector = typedArray.getResourceId(R.styleable.NiceSpinner_backgroundSelector, R.drawable.selector_nice_spinner);
        setBackgroundResource(backgroundSelector);
        textColor = typedArray.getColor(R.styleable.NiceSpinner_textTint, -1);
        setTextColor(textColor);


        listView = new ListView(context);
        // Set the spinner's id into the listview to make it pretend to be the right parent in
        // onItemClick
        listView.setId(getId());
        listView.setDivider(null);
        listView.setItemsCanFocus(true);
        //hide vertical and horizontal scrollbars
        listView.setVerticalScrollBarEnabled(false);
        listView.setHorizontalScrollBarEnabled(false);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= selectedIndex && position < adapter.getCount()) {
                position++;
            }

            // Need to set selected index before calling listeners or getSelectedIndex() can be
            // reported incorrectly due to race conditions.
            selectedIndex = position;

            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(parent, view, position, id);
            }

            if (onItemSelectedListener != null) {
                onItemSelectedListener.onItemSelected(parent, view, position, id);
            }

            adapter.notifyItemSelected(position);
            setText(adapter.getItemInDataset(position).getString());
            dismissDropDown();
        });

        popupWindow = new PopupWindow(context);
        popupWindow.setContentView(listView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(DEFAULT_ELEVATION);
            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.spinner_drawable));
        } else {*/
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.nice_spinner_drop_down_shadow));
        // }

        popupWindow.setOnDismissListener(() -> {
            if (!isArrowHide) {
                animateArrow(false);
            }
        });

        isArrowHide = typedArray.getBoolean(R.styleable.NiceSpinner_hideArrow, false);
        if (!isArrowHide) {
            Drawable basicDrawable = ContextCompat.getDrawable(context, R.drawable.nice_spinner_arrow);
            int resId = typedArray.getColor(R.styleable.NiceSpinner_arrowTint, -1);
            if (basicDrawable != null) {
                drawable = DrawableCompat.wrap(basicDrawable);
                if (resId != -1) {
                    DrawableCompat.setTint(drawable, resId);
                }
            }

            int width = typedArray.getDimensionPixelSize(R.styleable.NiceSpinner_narrow_width, 0);
            int height = typedArray.getDimensionPixelSize(R.styleable.NiceSpinner_narrow_height, 0);
            if (width == 0 && height != 0) {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), height);
            } else if (width != 0 && height == 0) {
                drawable.setBounds(0, 0, width, drawable.getIntrinsicHeight());
            } else if (width != 0 && height != 0) {
                drawable.setBounds(0, 0, width, height);
            } else {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            }
            setCompoundDrawables(null, null, drawable, null);
            //setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        }

        typedArray.recycle();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Set the default spinner item using its index
     *
     * @param position the item's position
     */
    public void setSelectedIndex(int position) {
        if (adapter != null) {
            if (position >= 0 && position <= adapter.getCount()) {
                adapter.notifyItemSelected(position);
                selectedIndex = position;
                setText(adapter.getItemInDataset(position).getString());
            } else {
                throw new IllegalArgumentException("Position must be lower than adapter count!");
            }
        }
    }

    public void addOnItemClickListener(@NonNull AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectedListener(@NonNull AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public void setDataList(@NonNull List list, Interfaces.OnStringData<T> onStringData) {
        setAttachDataSource(list, onStringData);
    }

    public void setDataList(@NonNull List<StringData> dataSet) {
        setAttachDataSource(dataSet, null);
    }

    private void setAttachDataSource(@NonNull List dataSet, Interfaces.OnStringData<T> onStringData) {
        if (dataSet.size() > 0) {
            if (dataSet.get(0) instanceof StringData) {
                adapter = new NiceSpinnerAdapter(getContext(), dataSet, textColor, backgroundSelector);
            } else {
                adapter = new NiceSpinnerAdapter(getContext(), Tools.getStringDataList(dataSet, onStringData), textColor, backgroundSelector);
            }
        } else {
            adapter = new NiceSpinnerAdapter(getContext(), new ArrayList<>(), textColor, backgroundSelector);
        }
        setAdapterInternal(adapter);
    }


    public void setAdapter(@NonNull ListAdapter adapter) {
        this.adapter = new NiceSpinnerAdapterWrapper(getContext(), adapter, textColor, backgroundSelector);
        setAdapterInternal(this.adapter);
    }

    private void setAdapterInternal(@NonNull NiceSpinnerBaseAdapter adapter) {
        // If the adapter needs to be settled again, ensure to reset the selected index as well
        selectedIndex = 0;
        listView.setAdapter(adapter);
        setText(adapter.getItemInDataset(selectedIndex).getString());
    }

    public T getSelectedData() {
        return  ((StringData<T>) adapter.getCurrentItem()).getObject();
    }

    public String getSelectedString() {
        return  adapter.getCurrentItem().getString();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        popupWindow.setWidth(View.MeasureSpec.getSize(widthMeasureSpec));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!popupWindow.isShowing()) {
                showDropDown();
            } else {
                dismissDropDown();
            }
        }
        return super.onTouchEvent(event);
    }

    private void animateArrow(boolean shouldRotateUp) {
     /*   int start = shouldRotateUp ? 0 : MAX_LEVEL;
        int end = shouldRotateUp ? MAX_LEVEL : 0;
//        AnimationUtils.loadAnimation(getContext(), R.anim.loading_bg);
//        ObjectAnimator animator = ObjectAnimator.ofInt(drawable, "level", start, end);
        ObjectAnimator animator = ObjectAnimator.ofFloat(drawable, "rotationX", shouldRotateUp ? 0f : 180f, shouldRotateUp ? 180f : 360f);
//        ObjectAnimator.
//        RotateAnimation animation = new RotateAnimation(shouldRotateUp ? 0 : 180, shouldRotateUp ? 180 : 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        animation.setDuration(500);
//        animation.setRepeatCount(1);
//        animation.setInterpolator(new LinearOutSlowInInterpolator());
        animator.setDuration(300);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();*/
    }

    public void dismissDropDown() {
        if (!isArrowHide) {
            animateArrow(false);
        }
        popupWindow.dismiss();
    }

    public void showDropDown() {
        if (!isArrowHide) {
            animateArrow(true);
        }
        popupWindow.showAsDropDown(this);
    }

    public void setTintColor(@ColorRes int resId) {
        if (drawable != null && !isArrowHide) {
            DrawableCompat.setTint(drawable, getContext().getResources().getColor(resId));
        }
    }
}
