package com.sunrise.marketingassistant.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class SignPanel extends View {

	private final int MAX = 30 << 10;

	private Paint mPaint;

	private Bitmap mBitmapOrg;

	private Canvas mCanvasOfBitmap;

	private float X, Y;

	private int mMinWidth = 200;

	private int mMinHeight = 100;

	public SignPanel(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public SignPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SignPanel(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(Math.max(widthSize, mMinWidth), Math.max(heightSize, mMinHeight));
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

		if (!changed)
			return;
		mBitmapOrg = Bitmap.createBitmap(right - left, bottom - top, Config.ARGB_8888);
		mCanvasOfBitmap = new Canvas(mBitmapOrg);

		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeWidth(5);
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mBitmapOrg != null) {
			Log.e("onDraw", "mBitmapOrg = " + mBitmapOrg);
			canvas.drawBitmap(mBitmapOrg, 0, 0, mPaint);
		} else {
			Log.e("onDraw", "mBitmapOrg = " + mBitmapOrg);
		}
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.e("onSizeCHange", w + "," + h + "," + oldw + "," + oldh);
	}

	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("onTouchEvent", event.toString());
		float x = event.getX(), y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			X = x;
			Y = y;
			if (mToucherListener != null)
				mToucherListener.onTouched(true);
			return true;
		case MotionEvent.ACTION_MOVE:
			drawOnBitmap(x, y);
			return true;
		case MotionEvent.ACTION_UP:
			if (mToucherListener != null)
				mToucherListener.onTouched(false);
			break;
		default:
			break;
		}

		return super.onTouchEvent(event);
	}

	private void drawOnBitmap(float x, float y) {
		Log.e("drawOnBitmap", "x = " + x + ", y = " + y);
		
		mCanvasOfBitmap.drawLine(X-getX(), Y-getY(), x-getX(), y-getY(), mPaint);
		X = x;
		Y = y;
		postInvalidate();
	}

	public void clean() {
		Bitmap temp = Bitmap.createBitmap(mBitmapOrg.getWidth(), mBitmapOrg.getHeight(), Config.ARGB_8888);
		mBitmapOrg.recycle();

		mCanvasOfBitmap = new Canvas(temp);
		mBitmapOrg = temp;
	}

	private OnTouchListener mToucherListener;

	void setOnTouchListener(OnTouchListener listener) {
		mToucherListener = listener;
	}

	interface OnTouchListener {
		void onTouched(boolean isTouch);
	}
}
