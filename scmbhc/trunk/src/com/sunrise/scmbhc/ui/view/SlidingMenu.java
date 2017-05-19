package com.sunrise.scmbhc.ui.view;

import com.sunrise.scmbhc.utils.LogUtlis;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

public class SlidingMenu extends ViewGroup {

	private static final int INVALID_POINTER = -1;

	private Scroller mScroller;

	private VelocityTracker mVelocityTracker;

	private View mMenu;

	private View mContent;

	private float mLastX;

	/**
	 * 最小内容显示宽度
	 */
	private int mCountMinWidth;

	/**
	 * 偏移量
	 */
	private int mXOffsetOfContent;

	private boolean mIsOpened;

	private boolean mIsSliding;

	private int mMaximumVelocity;

	private long mLastTime;

	private int mMenuLayout;

	private int mTouchSlop;

	private OnSlidingListener mSlidinglistener;

	private int mActivePointerId = INVALID_POINTER;

	private boolean mIsSlidingByScream;

	private final OnSlidingListener DEFAULT_SLIDING_LISTENER = new OnSlidingListener() {

		@Override
		public void onStoped(SlidingMenu slidingmenu, View content, View menu, boolean mIsOpened) {

		}

		@Override
		public void onSliding(SlidingMenu slidingmenu, View content, View menu, int offset, double rate) {

		}

	};

	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SlidingMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlidingMenu(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		if (getChildCount() < 2)
			throw new IllegalArgumentException("The count of childViews must > 2.");

		mScroller = new Scroller(getContext(), new AccelerateDecelerateInterpolator());
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

		mIsSliding = false;
		mIsOpened = false;
		mContent = getChildAt(0);
		mMenu = getChildAt(1);

		setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				mIsSliding = true;
				return false;
			}
		});
	}

	public boolean onTouchEvent(MotionEvent event) {

		boolean result = super.onTouchEvent(event);
		if (mMenu == null)
			return result;

		if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
			// Don't handle edge touches immediately -- they may actually belong
			// to one of our
			// descendants.
			return result;
		}

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		// 加入event
		mVelocityTracker.addMovement(event);

		int x = 0;

		final int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:

			if (!mScroller.isFinished())
				mScroller.abortAnimation();

			mIsSliding = false;
			mLastX = event.getX();
			mLastTime = System.currentTimeMillis();
			mActivePointerId = event.getPointerId(0);
			result = true;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mIsSliding) {
				x = (int) (event.getX() - mLastX);
				if (mScroller.getCurrX() + x <= 0 && mScroller.getCurrX() + x >= mCountMinWidth - getWidth()) {
					smoothScrollBy(x, 0);
					result = true;
				} else {
					flingScrollBy(x, 0);
				}
				mLastX = event.getX();
				mLastTime = System.currentTimeMillis();
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mIsSliding) {
				mVelocityTracker.computeCurrentVelocity((int) (System.currentTimeMillis() - mLastTime), mMaximumVelocity);
				x = (int) mVelocityTracker.getXVelocity();

				// 回归位置
				setOpened(mScroller.getFinalX() + x < mCountMinWidth - getWidth() >> 1);

				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			}
		case MotionEvent.ACTION_CANCEL:
			if (mIsSliding && getChildCount() > 0) {
				mActivePointerId = INVALID_POINTER;
				mIsSliding = false;
				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			onSecondaryPointerUp(event);
			break;
		}
		return result;
	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {

		if (!mIsSlidingByScream)
			return super.onInterceptTouchEvent(ev);

		final int action = ev.getAction();
		if (mIsSliding && (action == MotionEvent.ACTION_MOVE))
			return true;
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_MOVE:

			final int activePointerId = mActivePointerId;
			if (activePointerId == INVALID_POINTER) {
				break;
			}

			final int pointerIndex = ev.findPointerIndex(activePointerId);
			final float x = ev.getX(pointerIndex);
			if (Math.abs(mLastX - x) > mTouchSlop) {
				mIsSliding = true;
				mLastX = x;
			}

			break;
		case MotionEvent.ACTION_DOWN:
			mLastX = ev.getX();
			mActivePointerId = ev.getPointerId(0);
			mIsSliding = !mScroller.isFinished();
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mIsSliding = false;
			mActivePointerId = INVALID_POINTER;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			break;
		}
		return mIsSliding;
	}

	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		final int pointerId = ev.getPointerId(pointerIndex);
		if (pointerId == mActivePointerId) {
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mLastX = ev.getX(newPointerIndex);
			mActivePointerId = ev.getPointerId(newPointerIndex);
			if (mVelocityTracker != null) {
				mVelocityTracker.clear();
			}
		}
	}

	// 调用此方法滚动到目标位置
	private void smoothScrollTo(int fx, int fy) {
		int dx = fx - mScroller.getFinalX();
		int dy = fy - mScroller.getFinalY();
		smoothScrollBy(dx, dy);
	}

	// 调用此方法设置滚动的相对偏移
	public void smoothScrollBy(int dx, int dy) {
		// 设置mScroller的滚动偏移量
		mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, 300);
		invalidate();// 这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
	}

	public void flingScrollBy(int dx, int dy) {
		mScroller.fling(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, mCountMinWidth - getWidth(), 0, 0, Integer.MAX_VALUE);
		invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		LogUtlis.showLogD(getClass().getName(), "onLayout" + changed + " , " + left + " , " + top + " , " + right + " , " + bottom);

		if (mCountMinWidth == 0) {// 内容自留边未初始化的时候
			mCountMinWidth = right - left >> 2;
		}
		if (mIsOpened) {
			if (mMenu != null)
				mMenu.layout(left + mCountMinWidth, top, right, bottom);

			if (mContent != null) {
				int offx = offsetOfContentLeft();
				mContent.layout(left + offx, top, right + offx, bottom);
			}
		} else {
			if (mContent != null)
				mContent.layout(left, top, right, bottom);
			if (mMenu != null)
				mMenu.layout(right, top, right * 2 - left + mCountMinWidth, bottom);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		LogUtlis.showLogD(getClass().getName(), "onMeasure" + widthMeasureSpec + " , " + heightMeasureSpec + " ,mCountMinWidth =  " + mCountMinWidth);

		measureChild(mContent, widthMeasureSpec, heightMeasureSpec);
		mContent.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY));

		mMenu.measure(MeasureSpec.makeMeasureSpec(mContent.getMeasuredWidth() - mCountMinWidth, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY));

		setMeasuredDimension(resolveSize(mContent.getMeasuredWidth(), widthMeasureSpec), resolveSize(mContent.getMeasuredHeight(), heightMeasureSpec));
	}

	public void computeScroll() {
		// 先判断mScroller滚动是否完成
		if (mScroller.computeScrollOffset()) {

			double rate = (double) mScroller.getCurrX() / mMenu.getWidth();
			LogUtlis.showLogI("slidemenu", "currentx in compute:" + mScroller.getCurrX());
			mMenu.layout(getWidth() + mScroller.getCurrX(), mMenu.getTop(), getWidth() + mMenu.getWidth() + mScroller.getCurrX(), mMenu.getBottom());
			// 调整内容界面的
			int offX = offsetOfContentLeft();
			mContent.layout(offX, 0, offX + getWidth(), getHeight());
			// 必须调用该方法，否则不一定能看到滚动效果

			getSlidinglistener().onSliding(this, mContent, mMenu, mScroller.getCurrX(), rate);

			if (mScroller.isFinished()) {// 停止滑动时，通知监听

				boolean tempOpened = mScroller.getCurrX() != 0;
				if (mIsOpened != tempOpened) {
					mIsOpened = tempOpened;
					getSlidinglistener().onStoped(this, mContent, mMenu, mIsOpened);
				}
			}

			postInvalidate();
		}
		super.computeScroll();

	}

	/**
	 * @return 获取内容界面的滑动距离。
	 */
	private int offsetOfContentLeft() {
		double rate = (double) mScroller.getCurrX() / mMenu.getWidth();
		int distance = getWidth() - mCountMinWidth >> 1;// mMenu.getWidth();
		int offsetContent = (int) Math.round(distance * rate);
		return offsetContent;
		// return mMenu.getLeft();
	}

	public View getMenu() {
		return mMenu;
	}

	public void setMenu(View menu) {

		if (mMenu != null)
			removeView(mMenu);

		this.mMenu = menu;
		if (mMenuLayout > 0) {
			addView(menu, 1, new LayoutParams(getWidth() - mCountMinWidth, LayoutParams.WRAP_CONTENT));
		} else {
			addView(menu, 1, new LayoutParams(getWidth() - mCountMinWidth, LayoutParams.MATCH_PARENT));
		}
	}

	public View getContent() {
		return mContent;
	}

	public void setContent(View content) {
		if (mContent != null)
			removeView(mContent);

		this.mContent = content;
		addView(content, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	public boolean isOpened() {
		return mIsOpened;
	}

	private void setOpened(boolean open) {
		if (open)
			doOpen();
		else
			doClose();
	}

	private void doOpen() {
		mScroller.startScroll(mScroller.getFinalX(), 0, mCountMinWidth - getWidth() - mScroller.getFinalX(), 0, 200);
		invalidate();
	}

	private void doClose() {
		mScroller.startScroll(mScroller.getFinalX(), 0, -mScroller.getFinalX(), 0, 200);
		invalidate();
	}

	private void doCloseSlideDirect() {
		//
		mScroller.startScroll(mScroller.getFinalX(), 0, -mScroller.getFinalX(), 0, 0);
		invalidate();

	}

	public void open() {
		if (!mIsOpened) {
			setOpened(true);
		}
	}

	public void close() {
		if (mIsOpened) {
			setOpened(false);
		}
	}

	public void closeDirect() {
		if (mIsOpened) {

			doCloseSlideDirect();

		}
	}

	public void toggle() {
		if (!mIsSliding)
			setOpened(!mIsOpened);
	}

	public boolean isSliding() {
		return mIsSliding;
	}

	private OnSlidingListener getSlidinglistener() {
		return mSlidinglistener == null ? DEFAULT_SLIDING_LISTENER : mSlidinglistener;
	}

	public void setOnSlidinglistener(OnSlidingListener mSlidinglistener) {
		this.mSlidinglistener = mSlidinglistener;
	}

	public boolean isIsSlidingByScream() {
		return mIsSlidingByScream;
	}

	public void setSlidingByScream(boolean isSlidingByScream) {
		this.mIsSlidingByScream = isSlidingByScream;
	}

	public void setXOffsetOfContent(int length) {
		this.mXOffsetOfContent = length;
		postInvalidate();
	}

	public void setCountMinWidth(int width) {
		this.mCountMinWidth = width;
		postInvalidate();
	}

	public void setmMenuLayout(int mMenuLayout) {
		this.mMenuLayout = mMenuLayout;
	}

	public interface OnSlidingListener {
		public void onStoped(SlidingMenu slidingmenu, View content, View menu, boolean isOpen);

		public void onSliding(SlidingMenu slidingmenu, View content, View menu, int offset, double rate);
	}

}
