package com.sunrise.scmbhc.ui.view;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.UseCondition;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

public class InstrumentPanel extends View {

	private Drawable mInstrumentPanelBg;
	private Drawable mInstrumentPointer;
	private Drawable mLine1;
	private Scroller mScroller;

	private final int MAX = 30 << 10;
	private int max = MAX;
	private int mTrueMax;
	private int progress;

	private final static int MAX_SCROLLER_LENGTH = 10000;// 固定的长度
	private static final int NUM_IMSTRUMENT = 6;// 刻度数量

	private float mDensity;
	private Paint mPaint;
	private Rect mBounds;
	private String[] mInstrumentTag;// 刻度显示的文字。

	public InstrumentPanel(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public InstrumentPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public InstrumentPanel(Context context) {
		super(context);
		init();
	}

	private void init() {

		mDensity = getContext().getResources().getDisplayMetrics().density / 1.5f;

		mInstrumentPanelBg = getResources().getDrawable(R.drawable.instrument_panel);
		mInstrumentPointer = getResources().getDrawable(R.drawable.instrument_pointer);
		mLine1 = getResources().getDrawable(R.drawable.line_green);
	}

	protected void onFinishInflate() {
		mScroller = new Scroller(getContext(), new AccelerateDecelerateInterpolator());
		mTrueMax = max;

		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);
		mPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_4));

		mBounds = new Rect();
		mInstrumentTag = new String[NUM_IMSTRUMENT];
		initInstrumentTags();
	}

	private void initInstrumentTags() {
		mInstrumentTag[NUM_IMSTRUMENT - 1] = UseCondition.getFlowString(mTrueMax);

		for (int i = 0; i < NUM_IMSTRUMENT - 1; i++) {
			float f = mTrueMax;
			f /= NUM_IMSTRUMENT;
			f *= i + 1;
			f = Math.round(f / 10) * 10;

			mInstrumentTag[i] = UseCondition.getFlowString(f);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(resolveSize(mInstrumentPanelBg.getIntrinsicWidth(), widthMeasureSpec),
				resolveSize(mInstrumentPanelBg.getIntrinsicHeight(), heightMeasureSpec));

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int width = mInstrumentPanelBg.getIntrinsicWidth();
		int height = mInstrumentPanelBg.getIntrinsicHeight();
		mInstrumentPanelBg.setBounds(left, bottom - height, left + width, bottom);

		if (right - left - width > 0) {
			int length = right - left - width >> 1;
			width = Math.max(length, mLine1.getIntrinsicWidth());
			height = mLine1.getIntrinsicHeight();
			mLine1.setBounds(mInstrumentPanelBg.getBounds().right - 1, mInstrumentPanelBg.getBounds().bottom - height, right,
					mInstrumentPanelBg.getBounds().bottom);
		}

		width = mInstrumentPointer.getIntrinsicWidth();
		height = mInstrumentPointer.getIntrinsicHeight();
		// mInstrumentPointer.setBounds(-width >> 1, -height >> 1, width >> 1,
		// height >> 1);
		int offset = Math.round(22 * mDensity);
		mInstrumentPointer.setBounds(-width + offset, -height + offset, offset, offset);
	}

	public void computeScroll() {
		// 先判断mScroller滚动是否完成
		if (mScroller.computeScrollOffset()) {
			// 必须调用该方法，否则不一定能看到滚动效果
			resetAngle();
			postInvalidate();
		}
		super.computeScroll();

	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {

		if (this.progress == Math.abs(progress))
			return;

		this.progress = Math.abs(progress);
		double tempD = progress;
		tempD /= mTrueMax;
		tempD *= MAX_SCROLLER_LENGTH;
		int fx = (int) Math.round(tempD);
		smoothScrollTo(fx, 0);
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mInstrumentPanelBg.draw(canvas);
		mLine1.draw(canvas);

		int offset = Math.round(33 * mDensity);
		Rect instrumentBounds = mInstrumentPanelBg.getBounds();

		// 绘制文字
		final float offy = 33 * mDensity;
		final float limitH = 28 * mDensity;// (limitH - mBounds.height()) / 2
		for (int i = 0; i < NUM_IMSTRUMENT; i++) {
			canvas.save();
			canvas.translate(mInstrumentPanelBg.getBounds().width() >> 1, mInstrumentPanelBg.getBounds().height() - offset);
			canvas.rotate(i * 30 - 75);
			canvas.translate(-mInstrumentPanelBg.getBounds().width() >> 1, -mInstrumentPanelBg.getBounds().height() + offset);

			mPaint.getTextBounds(mInstrumentTag[i], 0, mInstrumentTag[i].length(), mBounds);
			canvas.drawText(mInstrumentTag[i], instrumentBounds.width() - mBounds.width() >> 1, offy, mPaint);
			canvas.restore();
		}

		// 绘制指针
		canvas.save();
		canvas.translate(instrumentBounds.width() >> 1, instrumentBounds.height() - offset);
		canvas.rotate(mInstrumentPointer.getLevel() - 45);
		mInstrumentPointer.draw(canvas);
		canvas.restore();
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {

		if (this.max == Math.abs(max))
			return;

		mTrueMax = Math.max(Math.abs(max), MAX);

		smoothScrollTo(MAX_SCROLLER_LENGTH * progress / mTrueMax, 0);

		initInstrumentTags();
	}

	public void setProgressAndMax(int progress, int max) {

		if (this.progress == Math.abs(progress) && this.max == Math.abs(max))
			return;

		this.progress = Math.abs(progress);
		mTrueMax = Math.max(Math.abs(max), MAX);
		initInstrumentTags();

		double tempD = progress;
		tempD /= mTrueMax;
		tempD *= MAX_SCROLLER_LENGTH;
		int fx = (int) Math.round(tempD);
		smoothScrollTo(fx, 0);

	}

	private void resetAngle() {
		int angle = 0;

		angle = 180 * mScroller.getCurrX() / MAX_SCROLLER_LENGTH;
		if (randomWave) {
			if (mScroller.getCurrX() == mScroller.getFinalX()) {
				smoothScrollTo((int) Math.round(MAX_SCROLLER_LENGTH * Math.random()), 0);
			}
		}
		mInstrumentPointer.setLevel(angle);
	}

	// 调用此方法设置滚动的相对偏移
	private void smoothScrollBy(int dx, int dy) {
		// 设置mScroller的滚动偏移量
		mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, 800);
		// 这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
		invalidate();
	}

	// 调用此方法滚动到目标位置
	private void smoothScrollTo(int fx, int fy) {
		int dx = fx - mScroller.getFinalX();
		int dy = fy - mScroller.getFinalY();
		smoothScrollBy(dx, dy);
	}

	// private float mX, mY;

	// public boolean onTouchEvent(MotionEvent event) {
	// switch (event.getAction()) {
	// case MotionEvent.ACTION_DOWN:
	// mX = event.getX();
	// mY = event.getY();
	// break;
	// case MotionEvent.ACTION_MOVE:
	//
	// smoothScrollBy((int) (event.getX() - mX), (int) (event.getY() - mY));
	//
	// mX = event.getX();
	// mY = event.getY();
	// break;
	// case MotionEvent.ACTION_UP:
	// case MotionEvent.ACTION_CANCEL:
	// smoothScrollTo(0, 0);
	// return false;
	// default:
	// return false;
	// }
	//
	// return true;
	// }

	private boolean randomWave;

	public void randomWave(boolean random) {
		randomWave = random;
		if (random) {
			smoothScrollTo((int) Math.round(MAX_SCROLLER_LENGTH * Math.random()), 0);
		} else {
			setProgress(getProgress());
		}
	}
}
