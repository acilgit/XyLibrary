package com.xycode.xylibrary.utils.cropUtils.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.xycode.xylibrary.R;

/**
 * @author mingchaogui
 *
 */
public class BaseImageView extends View {

    //
    private float mOldX = 0;
    private float mOldY = 0;

    private float mOldX_0 = 0;
    private float mOldY_0 = 0;

    private float mOldX_1 = 0;
    private float mOldY_1 = 0;

    private final int STATUS_TOUCH_IDLE = 0;
    private final int STATUS_TOUCH_SINGLE = 1;//
    private final int STATUS_TOUCH_MULTI = 2;//
    //
    private int mStatus = STATUS_TOUCH_IDLE;
    //
    private boolean mRotating = false;

    //
    private final int DEFAULT_CROP_WIDTH = 512;
    private final int DEFAULT_CROP_HEIGHT = 512;
    //
    private int mOutWidth = DEFAULT_CROP_WIDTH;
    private int mOutHeight = DEFAULT_CROP_HEIGHT;

    protected float mOriRationWH = 1.0f;//

    protected BitmapDrawable mDrawable;//
    protected FloatDrawable mFloatDrawable;//
    protected int mHighLightBorderColor;//

    protected Rect mTmpRect = new Rect();
    protected Rect mAdjustRect = new Rect();
    protected Rect mFloatRect = new Rect();//
    protected boolean mShouldInitBounds = true;

    public final int DOUBLE_CLICK_TIME = 200;
    protected long mLastTouchUpTime = 0;

    public BaseImageView(Context context) {
        super(context);
        init();
    }

    public BaseImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mFloatDrawable = new FloatDrawable();
        mHighLightBorderColor = getContext().getResources().getColor(R.color.transparentBlackDark);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDrawable == null) {
            return;
        }

        if (mDrawable.getIntrinsicWidth() == 0 || mDrawable.getIntrinsicHeight() == 0) {
            return;     // nothing to draw (empty bounds)
        }

        configureBounds();

        mDrawable.draw(canvas);
        canvas.save();
        canvas.clipRect(mFloatRect, Region.Op.DIFFERENCE);
        canvas.drawColor(mHighLightBorderColor);
        canvas.restore();
        mFloatDrawable.draw(canvas);
    }

    protected void configureBounds() {
        if(mShouldInitBounds) {
            //
            float viewWidth = this.getWidth();
            float viewHeight = this.getHeight();
            //
            float srcWidth = mDrawable.getIntrinsicWidth();
            float srcHeight = mDrawable.getIntrinsicHeight();

            //
            float floatWidth = viewWidth;
            //
            float scaleFloat = floatWidth / mOutWidth;
            //
            float floatHeight = mOutHeight * scaleFloat;
            //
            float floatTop = (viewHeight - floatHeight) / 2;
            float floatBottom = floatTop + floatHeight;
            float floatLeft = viewWidth - floatWidth;
            float floatRight = floatLeft + floatWidth;


            mOriRationWH = srcWidth / srcHeight;

            //
            float scaleWidth = floatWidth / srcWidth;
            //
            float scaleHeight = floatHeight / srcHeight;
            //
            float scale = scaleWidth > scaleHeight ? scaleWidth : scaleHeight;

            //
            float dWidth = srcWidth * scale;
            float dHeight = srcHeight * scale;
            //
            float dLeft = (viewWidth - dWidth) / 2;
            float dRight = dLeft + dWidth;
            float dTop = (viewHeight - dHeight) / 2;
            float dBottom = dTop + dHeight;
            mTmpRect.set((int) dLeft, (int) dTop, (int) dRight, (int) dBottom);

            mAdjustRect.set(mTmpRect);
            mFloatRect.set((int) floatLeft, (int) floatTop, (int) floatRight, (int) floatBottom);

            mShouldInitBounds = false;
        }

        mDrawable.setBounds(mAdjustRect);
        mFloatDrawable.setBounds(mFloatRect);
    }

    /**
     * @param drawable
     */
    public void setDrawable(BitmapDrawable drawable) {
        if (drawable == null) {
            return;
        }

        setDrawable(drawable, mOutWidth, mOutHeight);
    }

    /**
     * @param drawable
     * @param outWidth
     * @param outHeight
     */
    public void setDrawable(BitmapDrawable drawable,int outWidth,int outHeight) {
        this.mDrawable = drawable;
        this.mOutWidth =  outWidth;
        this.mOutHeight = outHeight;
        this.mShouldInitBounds = true;

        this.invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mStatus = STATUS_TOUCH_SINGLE;
                saveFingersPosition(event);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mStatus = STATUS_TOUCH_MULTI;
                saveFingersPosition(event);
                break;

            case MotionEvent.ACTION_MOVE:
                if(mStatus == STATUS_TOUCH_MULTI) {
                    this.onZoom(event);
                } else if (mStatus == STATUS_TOUCH_SINGLE) {
                    this.onDrag(event);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (System.currentTimeMillis() - mLastTouchUpTime < DOUBLE_CLICK_TIME) {
                    mLastTouchUpTime = 0;
                    scale(2.0f);
                } else {
                    mLastTouchUpTime = System.currentTimeMillis();
                }

            case MotionEvent.ACTION_POINTER_UP:
                mStatus = STATUS_TOUCH_IDLE;
                break;
        }

        return true;
    }

    protected void saveFingersPosition(MotionEvent event) {
        int pointerCount = event.getPointerCount();

        if (pointerCount > 1) {
            //
            mOldX_0 = event.getX(0);
            mOldY_0 = event.getY(0);
            //
            mOldX_1 = event.getX(1);
            mOldY_1 = event.getY(1);
        } else if (pointerCount == 1) {
            mOldX = event.getX();
            mOldY = event.getY();
        }
    }

    protected void onDrag(MotionEvent event) {
        //
        int dx = (int)(event.getX() - mOldX);
        int dy = (int)(event.getY() - mOldY);

        mAdjustRect.offset(dx, dy);
        this.adjustBoundOutSide();
        this.invalidate();

        //
        saveFingersPosition(event);
    }

    protected void onZoom(MotionEvent event) {
        //
        float newX_0 = event.getX(0);
        float newY_0 = event.getY(0);
        float newX_1 = event.getX(1);
        float newY_1 = event.getY(1);

        //
        float scale = distance(newX_0, newX_1, newY_0, newY_1) / distance(mOldX_0, mOldX_1, mOldY_0, mOldY_1);

        scale(scale);


        saveFingersPosition(event);
    }

    public float distance(float x0, float x1, float y0, float y1){
        float dx = Math.abs(x0 - x1);
        float dy = Math.abs(y0 - y1);
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    public void scale(float scale) {
        //
        float dWidth = mAdjustRect.width() * scale;
        float dHeight = dWidth / mOriRationWH;

        //
        float vCenterX = this.getWidth() / 2;
        float vCenterY = this.getHeight() / 2;


        float distanceX = mAdjustRect.centerX() - vCenterX;
        float distanceY = mAdjustRect.centerY() - vCenterY;

        //
        float centerX = (int)(vCenterX + distanceX * scale);
        float centerY = (int)(vCenterY + distanceY * scale);

        int left = (int)(centerX - dWidth / 2);
        int top = (int)(centerY - dHeight / 2);
        int right = (int)(centerX + dWidth / 2);
        int bottom = (int)(centerY + dHeight / 2);

        mAdjustRect.set(left, top, right, bottom);

        this.adjustBoundOverSize();
        this.adjustBoundOutSide();
        this.invalidate();
    }

    public void rotate(final float angle) {
        if (mRotating || mDrawable == null) {
            return;
        }

        final Bitmap oldBitmap = mDrawable.getBitmap();
        if (oldBitmap == null) {
            return;
        }

        mRotating = true;
//        CropToast.show(getContext(), getResources().getString(R.string.rotating));

        final Handler handler = new Handler();
        final Runnable rotateRunnable = new Runnable() {
            @Override
            public void run() {
                Matrix matrix = new Matrix();
                matrix.postRotate(angle);

                Bitmap rotatedBitmap;
                try {
                    rotatedBitmap = Bitmap.createBitmap(oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, true);
                } catch (OutOfMemoryError error) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
//                            CropToast.show(getContext(), getContext().getString(R.string.out_of_memory));
                        }
                    });

                    return;
                }

                final BitmapDrawable drawable = new BitmapDrawable(getResources(), rotatedBitmap);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setDrawable(drawable);
                        oldBitmap.recycle();

                        mRotating = false;
                        CropToast.cancel();
                    }
                });
            }
        };
        new Thread(rotateRunnable).start();
    }

    /**
     */
    protected void adjustBoundOutSide() {
        //
        boolean isOut = false;
        //
        int newLeft = mAdjustRect.left;
        int newTop = mAdjustRect.top;

        if (mAdjustRect.left > mFloatRect.left) {
            newLeft = mFloatRect.left;
            isOut = true;
        } else if (mAdjustRect.right < mFloatRect.right) {
            newLeft = mFloatRect.right - mAdjustRect.width();
            isOut = true;
        }
        if (mAdjustRect.top > mFloatRect.top) {
            newTop = mFloatRect.top;
            isOut = true;
        } else if (mAdjustRect.bottom < mFloatRect.bottom) {
            newTop = mFloatRect.bottom - mAdjustRect.height();
            isOut = true;
        }

        if (isOut) {
            mAdjustRect.offsetTo(newLeft, newTop);
        }
    }

    /**
     *
     */
    protected void adjustBoundOverSize() {
        boolean isAdjust = false;
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;


        if (mAdjustRect.width() < mFloatRect.width()) {

            int centerY = mAdjustRect.centerY();

            left = mFloatRect.left;
            right = mFloatRect.right;

            float height = mFloatRect.width() / mOriRationWH;

            top = (int)(centerY - height / 2);

            bottom = (int)(centerY + height / 2);

            isAdjust = true;
        } else if (mAdjustRect.height() < mFloatRect.height()) {

            int centerX = mAdjustRect.centerX();

            top = mFloatRect.top;
            bottom = mFloatRect.bottom;

            float width = mFloatRect.height() * mOriRationWH;
            left = (int)(centerX - width / 2);
            right = (int)(centerX + width / 2);

            isAdjust = true;
        }

        if (isAdjust) {
            mAdjustRect.set(left, top, right, bottom);
        }
    }

    public Bitmap cropImage() {
        if (mDrawable == null) {
            return null;
        }

        Bitmap src = mDrawable.getBitmap();

        float scaleX = (float)mAdjustRect.width() / (float)src.getWidth();
        float scaleY = (float)mAdjustRect.height() / (float)src.getHeight();

        int x = (int)(Math.abs(mAdjustRect.left - mFloatRect.left) / scaleX);
        int y = (int)(Math.abs(mAdjustRect.top - mFloatRect.top) / scaleY);
        int width = (int)(mFloatRect.width() / scaleX);
        int height = (int)(mFloatRect.height() / scaleY);

        Matrix matrix = new Matrix();
        float outScaleX = (float)mOutWidth / (float)width;
        float outScaleY = (float)mOutHeight / (float)height;
        matrix.postScale(outScaleX, outScaleY);

        Bitmap bitmap = Bitmap.createBitmap(src, x, y, width, height, matrix, true);

        return bitmap;
    }
}
