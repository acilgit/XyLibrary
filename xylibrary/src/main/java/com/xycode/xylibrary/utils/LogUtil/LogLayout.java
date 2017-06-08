package com.xycode.xylibrary.utils.LogUtil;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xycode.xylibrary.R;
import com.xycode.xylibrary.adapter.CustomHolder;
import com.xycode.xylibrary.adapter.XAdapter;
import com.xycode.xylibrary.unit.MsgEvent;
import com.xycode.xylibrary.unit.ViewTypeUnit;
import com.xycode.xylibrary.utils.Tools;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by XY on 2017-06-03.
 * 在Debug模式下，用于显示Log的内容
 * 在屏幕右边滑动展开，展开后右滑隐藏
 */

public class LogLayout {

    private final Context context;
    private RelativeLayout rootView;
    private XAdapter<L.LogItem> adapter;
    private int screenWidth;
    private int screenHeight;
    private CustomHolder holder;
    private int selectItemPos = -1;

    /**
     * 正在显示
     */
    private boolean showing = false;

    private static int miniSlideWidth = 40;
    private static final int maxItemContentLength = 400;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Integer x = (Integer) msg.obj;
                slideLayout(x);
            }
        }
    };

    public LogLayout(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        rootView = (RelativeLayout) LayoutInflater.from(context).inflate(com.xycode.xylibrary.R.layout.layout_base_console_view, null);
        rootView.setOnTouchListener(rootViewTouchListener);
        holder = new CustomHolder(rootView);
        holder.getView(R.id.vTouch).setOnTouchListener(slideBackTouchListener);
        adapter = new XAdapter<L.LogItem>(context, L.getLogList()) {
            @Override
            protected ViewTypeUnit getViewTypeUnitForLayout(L.LogItem item) {
                return new ViewTypeUnit(0, R.layout.item_log);
            }

            @Override
            public void creatingHolder(CustomHolder holder, ViewTypeUnit viewTypeUnit) {
                holder.setClick(R.id.tvContent, v -> {
                    selectItemPos = selectItemPos == holder.getAdapterPosition() ? -1 : holder.getAdapterPosition();
                    notifyDataSetChanged();
                });
            }

            @Override
            public void bindingHolder(CustomHolder holder, List<L.LogItem> dataList, int pos) {
                L.LogItem item = dataList.get(pos);
                int contentColor;
                switch (item.getType()) {
                    case L.LOG_TYPE_CRASH:
                        contentColor = android.R.color.holo_red_light;
                        break;
                    case L.LOG_TYPE_D:
                        contentColor = R.color.logTextDebug;
                        break;
                    case L.LOG_TYPE_I:
                        contentColor = R.color.logTextInfo;
                        break;
                    default:
                        contentColor = android.R.color.white;
                        break;
                }
                String content = (selectItemPos != pos && item.getContent().length() > maxItemContentLength) ? item.getContent().substring(0, maxItemContentLength) : item.getContent();
                holder.setText(R.id.tvDateTime, item.getDateTime() + " [" + (pos + 1) + "]")
                        .setText(R.id.tvTitle, item.getTitle())
                        .setVisibility(R.id.tvTitle, TextUtils.isEmpty(item.getTitle()) ? View.GONE : View.VISIBLE)
                        .setVisibility(R.id.tvContent, TextUtils.isEmpty(item.getContent()) ? View.GONE : View.VISIBLE)
                        .setText(R.id.tvContent, content)
                        .setTextColor(R.id.tvContent, context.getResources().getColor(contentColor));
                holder.getView(R.id.llItem).setBackgroundResource((selectItemPos != pos && item.getContent().length() > maxItemContentLength) ? R.color.bgBlue : 0);
            }
        };

        screenWidth = Tools.getScreenSize().x;
        screenHeight = Tools.getScreenSize().y;
        slideLayout(screenWidth);
        LinearLayout llLog = holder.getView(R.id.llLog);
        llLog.setLayoutTransition(new LayoutTransition());

        RecyclerView rv = holder.getRecyclerView(R.id.rv);
        rv.addItemDecoration(Tools.getHorizontlDivider(R.color.grayLite, R.dimen.dividerLineHeight, R.dimen.zero, R.dimen.zero));
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setAdapter(adapter);
        holder.setClick(R.id.tvTop, v -> {
            if (adapter.getShowingList().size() > 0) {
                rv.scrollToPosition(0);
            }
        });
        holder.setClick(R.id.tvBottom, v -> {
            if (adapter.getShowingList().size() > 0) {
                rv.scrollToPosition(adapter.getShowingList().size() - 1);
            }
        });
        holder.setClick(R.id.tvClear, v -> {
            L.getLogList().clear();
            EventBus.getDefault().post(new MsgEvent(L.EVENT_LOG, null, null));
            slideAnimate(0, true);
        });
    }

    public View getView() {
        return rootView;
    }

    public void refreshData() {
        adapter.notifyDataSetChanged();
    }

    private View.OnTouchListener slideBackTouchListener = new View.OnTouchListener() {
        boolean touching = false;
        boolean sliding = false;
        boolean canMove = false;
        float previousX, previousY, downX, downY;

        @Override
        public boolean onTouch(View v, MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            float dx;

            showing = holder.getView(R.id.llLog).getX() < screenWidth;
            touching = false;
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touching = true;
                    downX = x;
                    downY = y;
                    canMove = downX < screenWidth / 5 && showing;
                    break;
                case MotionEvent.ACTION_MOVE:
                    touching = true;
                    dx = x - downX;
                    if (canMove && (x >= downX)) {
                        sliding = true;
                        slideLayout((int) (dx));
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    touching = false;
                    dx = x - downX;
                    if (sliding && dx > 0) {
                        slideAnimate((int) (dx), dx > screenWidth / 20);
                        sliding = false;
                    }
                    canMove = false;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    touching = false;
                    dx = x - downX;
                    if (sliding && dx > 0) {
                        slideAnimate((int) (dx), false);
                        sliding = false;
                    }
                    canMove = false;
                    break;
            }
            previousX = x;
            previousY = y;
            return canMove;
        }
    };

    private View.OnTouchListener rootViewTouchListener = new View.OnTouchListener() {
        boolean touching = false;
        boolean sliding = false;
        float previousX, previousY, downX, downY;

        @Override
        public boolean onTouch(View v, MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            float dx;
            touching = false;
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touching = true;
                    downX = x;
                    downY = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    touching = true;
                    dx = x - downX;
                    if (downX > screenWidth - miniSlideWidth && dx < 0 && (sliding || y > screenHeight - (screenHeight / 5))) {
                        sliding = true;
                        slideLayout((int) x);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    touching = false;
                    dx = x - downX;
                    if (sliding) {
                        slideAnimate((int) x, previousX < x);
                        sliding = false;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    touching = false;
                    if (sliding) {
                        slideAnimate((int) x, true);
                        sliding = false;
                    }
                    break;
            }
            previousX = x;
            previousY = y;
            return touching && downX > screenWidth - miniSlideWidth;
        }
    };

    private void slideLayout(int x) {
        LinearLayout llLog = holder.getView(R.id.llLog);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llLog.getLayoutParams();
        params.setMargins(x, 0, -x, 0);
        llLog.setLayoutParams(params);
    }

    private void slideAnimate(int x, boolean isHide) {
        int step = (isHide ? (screenWidth - x) : x) / 40;
        for (int i = 0; i < 39; i++) {
            Message newMsg = new Message();
            newMsg.what = 1;
            newMsg.obj = isHide ? x + step * i : x - step * i;
            handler.sendMessageDelayed(newMsg, 5 * i);
        }
        Message newMsg = new Message();
        newMsg.what = 1;
        newMsg.obj = isHide ? screenWidth : 0;
        handler.sendMessageDelayed(newMsg, 5 * 39);

    }

}
