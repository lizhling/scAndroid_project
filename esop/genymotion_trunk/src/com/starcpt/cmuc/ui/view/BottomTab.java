package com.starcpt.cmuc.ui.view;

import com.starcpt.cmuc.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Scroller;

/**
 * menu bar
 * 
 * @author luoyun 20120307
 */
public class BottomTab extends ViewGroup {

	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private OnMenuItemSelectedListener mOnMenuItemSelectedListener;
	private BaseAdapter mAdapter;
	private Context mContext;
	private int mMenuNumber = 3;
	private int mMenuWidth = 0;
	private int mBarWidth = 0;
	
	
	private int mSplitWidth;
	private int mSplitHeight;
	private int mSplitMarginTop;
	private Drawable mSplitBgResource;
	
	private int mLastSelectedPosition = 0;

	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;

	private int mTouchState = TOUCH_STATE_REST;
	private int mTouchSlop;
	private float mLastMotionX;
	
	private boolean mInvalidateFlag = false;

	public interface MenuViewSwitcher{
		/**
		 * menu item changed
		 * 
		 * @param postion 
		 * 			  selected item position
		 * @param currentView
		 *            selected menu item
		 * @param lastItem
		 *            lastSelected item default the first menu item
		 */
		public void onSwitcher(int position ,View currentItem, View lastItem);
	} 
	
	public interface OnScreenChangedListener {
		/**
		 * screen changed listener
		 * 
		 * @param currentScreen
		 *            current screen number
		 * @param isFirstScreen
		 *            Whether for the first screen
		 * @param isLastScreen
		 *            Whether for the last screen
		 */
		public void onScreenChanged(int currentScreen, boolean isFirstScreen,
				boolean isLastScreen);
	}

	public interface OnMenuItemSelectedListener {
		/**
		 * bottom menu item selected listener
		 * 
		 * @param postion 
		 * 			  selected item position
		 * @param currentView
		 *            selected menu item
		 * @param lastItem
		 *            lastSelected item default the first menu item
		 */
	public void onMenuItemSelected(int position ,View currentItem, View lastItem);
	}

	/**
	 * set menu number on one screen
	 * 
	 * @param menuNumber
	 *            number
	 */
	public void setMenuNum(int menuNumber) {
		this.mMenuNumber = menuNumber;
	}

	public int getMenuNum() {
		return mMenuNumber;
	}

	public BottomTab(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public void setOnMenuItemSelectedListener(OnMenuItemSelectedListener menuItemSelectedListener){
		this.mOnMenuItemSelectedListener = menuItemSelectedListener;
	}
	
	public BottomTab(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mContext = context;
		mScroller = new Scroller(context);   
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BottomTab);
		mSplitHeight = (int) array.getDimension(R.styleable.BottomTab_splitHeight, 14);
		mSplitMarginTop = (int) array.getDimension(R.styleable.BottomTab_splitMarginTop, 0);
		mSplitWidth = (int) array.getDimension(R.styleable.BottomTab_splitWidth, 2);
		mSplitBgResource = array.getDrawable(R.styleable.BottomTab_splitBgResource);
		if(mSplitHeight==LayoutParams.WRAP_CONTENT){
			mSplitHeight=mSplitBgResource.getIntrinsicHeight();
		}
		if(mSplitWidth==LayoutParams.WRAP_CONTENT){
			mSplitWidth=mSplitBgResource.getIntrinsicWidth();
		}
	}
	
	public void setAdapter(BaseAdapter adaptor){
		this.mAdapter = adaptor;
		initalizeData();
	}
	 
	private void initalizeData(){
		 mMenuLeft = 0;
		 int count = mAdapter.getCount();
		 removeAllViews();
		 for (int i = 0; i < count; i++) {
			View view = mAdapter.getView(i, null, this);
			view.setId(i);  
			addView(view);
			if (i!=count-1) {
				addView(createSplitView());
			}
			
		 }
		 invalidate();
	}
	
	private ImageView createSplitView(){
		ImageView view = new ImageView(mContext);
		view.setEnabled(false);
		view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		if (mSplitBgResource!=null) {
			view.setBackgroundDrawable(mSplitBgResource);
		}
		return view;
	}
	
	/**
	 * notify view
	 */
	public void notifyView(){
		if (mAdapter!=null) {
			mMenuWidth = 0;
			mLastSelectedPosition = 0;
			mInvalidateFlag = true;
			initalizeData();
		}
	}
	/**
	 * set split image width
	 * @param width    
	 * 		width
	 */
	public void setSpiltWidth(int width){
		mSplitWidth = width;
	}
	
	/**
	 * set split image height
	 * @param height  height
	 */
	public void setSplitHeight(int height){
		mSplitHeight = height;
	}
	/**
	 * set set split image source  id
	 * @param resid
	 */
	public void setSplitResrouce(int resid){
		mSplitBgResource = getResources().getDrawable(resid);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
			calculateMenuWidth();
			int childLeft = mMenuLeft + 1;
			final int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {				   
					  if (i%2==0) {
							childView.setOnClickListener(menuItemSelectedListener);
							childView.layout(childLeft, mSplitMarginTop, childLeft + mMenuWidth,
									childView.getMeasuredHeight());
							childLeft += mMenuWidth;
						}else {
							int mSplitTop=(int) (mSplitMarginTop/40.0*getHeight());
							int mSplitH = (int) (mSplitHeight/40.0*getHeight());
							childView.layout(childLeft, mSplitTop, childLeft + mSplitWidth,
									mSplitTop+mSplitH);
							childLeft += mSplitWidth;
						}
				}
			}
		initScreen();
	}

	private OnClickListener menuItemSelectedListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int position = v.getId(); 
			if (position!=mLastSelectedPosition) {
				View lastView = getChildAt(mLastSelectedPosition*2);
				if (mOnMenuItemSelectedListener!=null) {
					mOnMenuItemSelectedListener.onMenuItemSelected(position,v, lastView);
					mLastSelectedPosition = position;
				}
			}
		}
	};
	
	public void updateLastPosition(int position){
		mLastSelectedPosition=position;
	}

	private int mMenuLeft;
	/**
	 * calculate every menu width
	 */
	private void calculateMenuWidth() {
		if (mMenuWidth == 0) {
			int screenWidth = getMeasuredWidth();
			int childsNum = mAdapter.getCount();
			mMenuWidth = screenWidth / mMenuNumber-mSplitWidth;
			mBarWidth = childsNum*(mMenuWidth+mSplitWidth);
			if (childsNum<mMenuNumber&&childsNum!=0) {
				mMenuLeft = (getMeasuredWidth()-mMenuWidth*childsNum)/2;
			} 
		}
	}
  

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only canmCurScreen run at EXACTLY mode!");
		}
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only can run at EXACTLY mode!");
		}
		// The children are given the same width and height as the scrollLayout
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		 
	}
 
	private void initScreen(){
		if (mInvalidateFlag) {
			final int delta =  - getScrollX();
	    	mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
	    	invalidate();
	    	mInvalidateFlag = false;
		}
	}

	public void snapToScreen() {
		// get the valid layout page
		int scrollX = getScrollX();
		int maxX =  mBarWidth-getMeasuredWidth();
		if (mMenuLeft!=0) {
			maxX = 0;
		}
		if (scrollX < 0) {
			mScroller.startScroll(scrollX, 0, Math.abs(scrollX), 0,Math.abs(scrollX) * 2);
		}else if (getScrollX()>maxX) {
			mScroller.startScroll(scrollX, 0,maxX-scrollX, 0,Math.abs(maxX-scrollX) * 2);
		}else {
			int remainder = scrollX%mMenuWidth;
			if (remainder!=0) {
				double consult = scrollX/(double)(mMenuWidth+mSplitWidth);
				int delta; 
					delta = (int) Math.round(consult)*(mMenuWidth+mSplitWidth)-scrollX;
				if (scrollX>delta) {
					mScroller.startScroll(scrollX, 0,delta, 0,Math.abs(delta) * 2);
				}else { 
					mScroller.startScroll(scrollX, 0,-delta, 0,Math.abs(delta) * 2);
				}
			}
		}
		invalidate(); // Redraw the layout
	}
	
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), 0);
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
		final int action = event.getAction();
		final float x = event.getX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			break;

		case MotionEvent.ACTION_MOVE:
			int deltaX = (int) (mLastMotionX - x);
			mLastMotionX = x;
			scrollBy(deltaX, 0);
			break;

		case MotionEvent.ACTION_UP:
			// if (mTouchState == TOUCH_STATE_SCROLLING) {
			snapToScreen();
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}

		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}

		final float x = ev.getX();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(mLastMotionX - x);
			if (xDiff > mTouchSlop) {
				mTouchState = TOUCH_STATE_SCROLLING;

			}
			break;

		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}

		return mTouchState != TOUCH_STATE_REST;
	}
	
}


