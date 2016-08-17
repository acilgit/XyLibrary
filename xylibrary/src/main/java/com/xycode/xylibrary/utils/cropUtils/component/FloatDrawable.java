package com.xycode.xylibrary.utils.cropUtils.component;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**

 * @author mingchaogui
 *
 */
public class FloatDrawable extends Drawable {

	//
	private Paint mLinePaint = new Paint();
	{
		mLinePaint.setARGB(200, 50, 50, 50);
		mLinePaint.setStrokeWidth(1F);
		mLinePaint.setStyle(Paint.Style.STROKE);
		mLinePaint.setAntiAlias(true);
		mLinePaint.setColor(Color.WHITE);
	}

	@Override
	public void draw(Canvas canvas) {
		int left = getBounds().left;
		int top = getBounds().top;
		int right = getBounds().right;
		int bottom = getBounds().bottom;

		Rect mRect=new Rect(
				left,
				top,
				right,
				bottom);

		//
		canvas.drawRect(mRect, mLinePaint);
	}

	@Override
	public void setBounds(Rect bounds) {
		super.setBounds(new Rect(
				bounds.left,
				bounds.top,
				bounds.right,
				bounds.bottom));
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return PixelFormat.UNKNOWN;
	}

}
