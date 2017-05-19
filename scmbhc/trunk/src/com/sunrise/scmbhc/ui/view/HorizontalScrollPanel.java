package com.sunrise.scmbhc.ui.view;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class HorizontalScrollPanel extends ViewGroup {    
	//private static String TAG="HorizontalScrollPanel";
    private static final int INVALID_SCREEN = -1;         
    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;     
    private static final int SNAP_VELOCITY = 100; 
    private int mDefaultScreen;         
    private int mCurrentScreen;         
    private int mNextScreen = INVALID_SCREEN;            
    private int mMaximumVelocity;         
    private Scroller mScroller;         
    private int mTouchState;         
    private boolean mFirstLayout = true;         
    private float mLastMotionX;                
    private int mTouchSlop;         
    private VelocityTracker mVelocityTracker;
    private int mPaintFlag = 0;            
    private OnViewChangedListener mViewChangeListener;
    /** * @param context */
    public HorizontalScrollPanel(Context context) {
            super(context);
            initViewGroup();
    }

    /**
     * * @param context * @param attrs
     * */
    public HorizontalScrollPanel(Context context, AttributeSet attrs) {
            super(context, attrs);
            initViewGroup();
    }

    /**
     * * @param context * @param attrs * @param defStyle
     * */
    public HorizontalScrollPanel(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            initViewGroup();
    }

    /*
     * * (non-Javadoc) * @see android.view.ViewGroup#onLayout(boolean, int, int,
     * int, int)
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//    	if(changed){
            int childLeft = 0;
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                    final View child = getChildAt(i);
                    if (child.getVisibility() != View.GONE) {
                            final int childWidth = child.getMeasuredWidth();
                            child.layout(childLeft, 0, childLeft + childWidth, child
                                            .getMeasuredHeight());
                            childLeft += childWidth;
                    }
            }
//    	}
    }

    /**
     * * Initializes various states for this viewgroup.
     * */
    private void initViewGroup() {
            mScroller = new Scroller(getContext());
            mCurrentScreen = mDefaultScreen;
            final ViewConfiguration configuration = ViewConfiguration
                            .get(getContext());
            mTouchSlop = configuration.getScaledTouchSlop();
             mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    public boolean isDefaultViewShowing() {
            return mCurrentScreen == mDefaultScreen;
    }

    public int getCurrentView() {
          return mCurrentScreen;
    }

    public void setCurrentView(int currentView) {
            // snapToScreen(currentView);
            mCurrentScreen = Math
                            .max(0, Math.min(currentView, getChildCount() - 1));
            scrollTo(mCurrentScreen * getWidth(), 0);
            if(mViewChangeListener != null){
    			mViewChangeListener.onViewChange(mCurrentScreen);
    		}
            invalidate();
    }


    /**
	 * Call this when you want to know the new location. If it returns true, 
	 * the animation is not yet finished. loc will be altered to provide the new location. 
	 */
	@Override
	public void computeScroll() {
		super.computeScroll();
		if(mScroller.computeScrollOffset()){
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		if (action == MotionEvent.ACTION_MOVE && mTouchState != TOUCH_STATE_REST) {
			return true;
		}
		/* if(!CmucApplication.sIsPad){
			 if(action == MotionEvent.ACTION_MOVE && mTouchState == TOUCH_STATE_DRAG){
					mTouchState=TOUCH_STATE_REST;
					return false;
				} 
		 }	*/	
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				mLastMotionX = (int)ev.getX();
				//mLastMotionY = y;
				mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
				break;
			case MotionEvent.ACTION_MOVE:
				final int x = (int)ev.getX();
				final int diffX = (int)Math.abs(x - mLastMotionX);
				if(diffX > mTouchSlop){
					mTouchState = TOUCH_STATE_SCROLLING;
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				mTouchState = TOUCH_STATE_REST;
				break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mVelocityTracker == null){
			mVelocityTracker = VelocityTracker.obtain();
		}
		//加入event
		mVelocityTracker.addMovement(event);
		final int action = event.getAction();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				//aborting the animating cause the scroller to move to the final x and y position
				if(!mScroller.isFinished()){
					mScroller.abortAnimation();
				}
				mLastMotionX = (int) event.getX();
				//mLastMotionY = (int) event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				final int x = (int) event.getX();
				int tempX = (int)(mLastMotionX - x);
				mLastMotionX = x;
				scrollBy(tempX, 0);
				break;
			case MotionEvent.ACTION_UP:
				final VelocityTracker tempVelocityTracker = mVelocityTracker;
				tempVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int velocityX = (int)tempVelocityTracker.getXVelocity();
				
				final int screenWidth = getWidth();
				final int whichScreen = (getScrollX() + (screenWidth*3/ 4)) / screenWidth;
				final float scrolledPos = (float) getScrollX() / screenWidth;
				
				if(velocityX > SNAP_VELOCITY && mCurrentScreen > 0){
					final int bound = scrolledPos < whichScreen ? mCurrentScreen - 1 : mCurrentScreen;
					snapToScreen(Math.min(whichScreen, bound));
				}else if(velocityX < - SNAP_VELOCITY && mCurrentScreen < getChildCount() - 1){
					final int bound = scrolledPos < whichScreen ? mCurrentScreen + 1 : mCurrentScreen;
					snapToScreen(Math.min(whichScreen, bound));
				}else{
					snapToScreen(whichScreen);
				}
				if(mVelocityTracker != null){
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
				mTouchState = TOUCH_STATE_REST;
				break;
			case MotionEvent.ACTION_CANCEL:
				mTouchState = TOUCH_STATE_REST;
				break;
		}
		return true;
	}
	
	private void snapToScreen(int whichScreen){
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
    	if (getScrollX() != (whichScreen * getWidth())&&mViewChangeListener!=null) {    		
			final int delta = whichScreen * getWidth() - getScrollX();
    		mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
    		mCurrentScreen = whichScreen;
    		mViewChangeListener.onViewChange(mCurrentScreen);
    		invalidate();
    	}
	}
    /*
     * * (non-Javadoc) * @see
     * android.view.ViewGroup#dispatchDraw(android.graphics.Canvas)
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
            boolean fastDraw = mTouchState != TOUCH_STATE_SCROLLING
                            && mNextScreen == INVALID_SCREEN;
            if (fastDraw) {
            	    if(getChildCount()>0)
                    drawChild(canvas, getChildAt(mCurrentScreen), getDrawingTime());
            } else {
                    final long drawingTime = getDrawingTime();
                    // If we are flinging, draw only the current screen and the target
                    // screen
                    if (/*mNextScreen >= 0
                                    && mNextScreen < getChildCount()*/
                                    mNextScreen != INVALID_SCREEN
                                    /*&& (Math.abs(mCurrentScreen - mNextScreen) == 1 || mPaintFlag != 0)*/) {
                            final View viewCurrent = getChildAt(mCurrentScreen), viewNext = getChildAt(mNextScreen);
                            drawChild(canvas, viewCurrent, drawingTime);
                            
                            if (mPaintFlag == 0) {
                                    drawChild(canvas, viewNext, drawingTime);
                            } else {
                                    Paint paint = new Paint();
                                    if (mPaintFlag < 0) {
                                            canvas.drawBitmap(viewNext.getDrawingCache(), -viewNext
                                                            .getWidth(), viewNext.getTop(), paint);
                                    } else {
                                            canvas.drawBitmap(viewNext.getDrawingCache(),
                                                            getWidth() * getChildCount(),
                                                            viewNext.getTop(), paint);
                                    }
                            }
                    } else {
                            // If we are scrolling, draw all of our children
                            final int count = getChildCount();
                            for (int i = 0; i < count; i++) {
                                    drawChild(canvas, getChildAt(i), drawingTime);
                            }
                            if (mPaintFlag != 0) {
                                    final View viewNext;
                                    Paint paint = new Paint();
                                    if (mPaintFlag < 0) {
                                            viewNext = getChildAt(getChildCount() - 1);
                                            canvas.drawBitmap(viewNext.getDrawingCache(), -viewNext
                                                            .getWidth(), viewNext.getTop(), paint);
                                    } else {
                                            viewNext = getChildAt(0);
                                            canvas.drawBitmap(viewNext.getDrawingCache(),
                                                            getWidth() * getChildCount(),
                                                            viewNext.getTop(), paint);
                                    }
                            }
                    }
            }
    }


    /*
     * * (non-Javadoc) * * @see android.view.View#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            final int width = MeasureSpec.getSize(widthMeasureSpec);
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                    getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
            }
            if (mFirstLayout) {
                    scrollTo(mCurrentScreen * width, 0);
                    mFirstLayout = false;
            }
    }
    
    @Override
    protected boolean onRequestFocusInDescendants(int direction,
                    Rect previouslyFocusedRect) {
            int focusableScreen;
            if (mNextScreen != INVALID_SCREEN) {
                    focusableScreen = mNextScreen;
            } else {
                    focusableScreen = mCurrentScreen;
            }
            View view= getChildAt(focusableScreen);
            if(view!=null)
            view.requestFocus(direction,
                            previouslyFocusedRect);
            return false;
    }

    void enableChildrenCache() {
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                    final View layout = getChildAt(i);
                    layout.setDrawingCacheEnabled(true);
                    if (layout instanceof ViewGroup) {
                            ((ViewGroup) layout).setAlwaysDrawnWithCacheEnabled(true);
                    }
            }
    }


    void clearChildrenCache() {
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                    final View layout = getChildAt(i);
                    if (layout instanceof ViewGroup) {
                            ((ViewGroup) layout).setAlwaysDrawnWithCacheEnabled(false);
                    }
            }
    }
    

    public void setOnViewChangedListener(OnViewChangedListener listener){
		mViewChangeListener = listener;
	}
    
    public void reset(){
    	snapToScreen(0);
    }
    
    public interface OnViewChangedListener {
    	void onViewChange(int curView);
    }

}
