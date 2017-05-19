package com.sunrise.micromarketing.ui.view;

import com.sunrise.micromarketing.R;
import com.sunrise.micromarketing.entity.TabItem;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyTab_old extends LinearLayout {
	private static int SELECT_PANEL_WIDTH = -1;
	private Adapter mBaseAdapter;
	private ItemSelectedListener mItemSelectedListener;
	private int mCurrentSelectPosition = 0;
	private int mSelectPosition = -1;
	private Context mContext;
	private View mLastView;
	private final int mFocusTextColor;
	private final int mNormalTextColor;

	private OnClickListener menuItemSelectedListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int position = v.getId();
			TabItem tabItem = (TabItem) mBaseAdapter.getItem(position);
			if (position != mCurrentSelectPosition) {
				View currentView = getChildAt(position);
				if (mItemSelectedListener != null) {
					mItemSelectedListener.onItemSelected(position, v,
							currentView);
					// moveIndexCurrentSelectPosition(position);
				}
				setItemClick(currentView, position);
				if (mLastView != null) {
					setItemNoraml(mLastView, mCurrentSelectPosition);
				}
				mLastView = currentView;
				mCurrentSelectPosition = position;
			}
		}
	};

	private OnTouchListener onItemTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View convertView, MotionEvent event) {
			int position = convertView.getId();
			TabItem tabItem = (TabItem) mBaseAdapter.getItem(position);
			if (position != mCurrentSelectPosition) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (convertView.findViewById(R.id.tab_image) != null) {
						ImageView imageView = (ImageView) convertView
								.findViewById(R.id.tab_image);
						imageView.setImageResource(tabItem.getIconClickResId());
					}
					break;
				case MotionEvent.ACTION_UP:
					if (convertView.findViewById(R.id.tab_image) != null) {
						ImageView imageView = (ImageView) convertView
								.findViewById(R.id.tab_image);
						imageView
								.setImageResource(tabItem.getIconNormalResId());
					}
					break;
				default:
					break;
				}
			}
			return false;
		}
	};

	public MyTab_old(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		Resources resources = context.getResources();
		mFocusTextColor = 0x0f5ec7;
		mNormalTextColor = resources.getColor(android.R.color.darker_gray);
	}

	public Adapter getAdapter() {
		return mBaseAdapter;
	}

	public void setAdapter(Adapter mBaseAdapter) {
		this.mBaseAdapter = mBaseAdapter;
		initView();
	}

	public void initView() {
		int count = mBaseAdapter.getCount();
		if (SELECT_PANEL_WIDTH == -1) {
			SELECT_PANEL_WIDTH = mContext.getResources().getDisplayMetrics().widthPixels
					/ count;
		}
		removeAllViews();
		/*
		 * mSelectPanel=new LinearLayout(mContext); LinearLayout.LayoutParams
		 * tabSelectLinearlayoutParams=new
		 * LinearLayout.LayoutParams(SELECT_PANEL_WIDTH,
		 * LayoutParams.WRAP_CONTENT); LinearLayout.LayoutParams
		 * tapTopSideLayoutParams=new
		 * LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
		 * LayoutParams.WRAP_CONTENT); tapTopSideLayoutParams.weight=1.0f;
		 * 
		 * mSelectPanel.setLayoutParams(tabSelectLinearlayoutParams);
		 * mSelectPanel.setBackgroundResource(R.drawable.tab_select);
		 */

		LinearLayout.LayoutParams tabItemlayoutParams = new LinearLayout.LayoutParams(
				SELECT_PANEL_WIDTH, LayoutParams.WRAP_CONTENT);
		tabItemlayoutParams.weight = 1.0f;
		tabItemlayoutParams.gravity = Gravity.CENTER;
		for (int i = 0; i < count; i++) {
			View view = mBaseAdapter.getView(i, null, this);
			View contentView = view.findViewById(R.id.tab_item_content);
			if (mCurrentSelectPosition == i) {
				mLastView = contentView;
				setItemClick(contentView, i);
			}
			view.setLayoutParams(tabItemlayoutParams);
			contentView.setId(i);
			contentView.setOnClickListener(menuItemSelectedListener);
			contentView.setOnTouchListener(onItemTouchListener);
			addView(view);
		}
		invalidate();
	}

	private void setItemClick(View view, int position) {
		TabItem tabItem = (TabItem) mBaseAdapter.getItem(position);
		ImageView imageView = (ImageView) view.findViewById(R.id.tab_image);
		TextView textView = (TextView) view.findViewById(R.id.tab_text);
		imageView.setImageResource(tabItem.getIconFocusResId());
		textView.setTextColor(mFocusTextColor);
	}

	public void setItemClick(int position) {
		View view = getChildAt(position);
		if (view != null) {
			TabItem tabItem = (TabItem) mBaseAdapter.getItem(position);
			ImageView imageView = (ImageView) view.findViewById(R.id.tab_image);
			TextView textView = (TextView) view.findViewById(R.id.tab_text);
			imageView.setImageResource(tabItem.getIconFocusResId());
			textView.setTextColor(mFocusTextColor);

			if (mLastView != null) {
				setItemNoraml(mLastView, mCurrentSelectPosition);
			}
			mLastView = view;
			mCurrentSelectPosition = position;
		}
	}

	private void setItemNoraml(View view, int position) {
		TabItem tabItem = (TabItem) mBaseAdapter.getItem(position);
		ImageView imageView = (ImageView) view.findViewById(R.id.tab_image);
		TextView textView = (TextView) view.findViewById(R.id.tab_text);
		imageView.setImageResource(tabItem.getIconNormalResId());
		textView.setTextColor(mNormalTextColor);
	}

	public void setItemSelectedListener(
			ItemSelectedListener mItemSelectedListener) {
		this.mItemSelectedListener = mItemSelectedListener;
	}

	/*
	 * public void moveIndexCurrentSelectPosition(final int position) {
	 * if(mSelectPanel!=null) { if(!isAnimationFinish){ if(mTranslate!=null){
	 * mTranslate.cancel(); mTranslate=null; } mSelectPanel.clearAnimation();
	 * mCurrentSelectPosition=mLastSelectPosition; } float
	 * distance=(position-mCurrentSelectPosition)*SELECT_PANEL_WIDTH;
	 * if(mAnimaStartX==-1){ mAnimaStartX=mSelectPanel.getLeft(); }
	 * mToXDelta=mAnimaStartX+distance; mTranslate = new
	 * TranslateAnimation(mAnimaStartX, mToXDelta, 0, 0);
	 * mTranslate.setDuration(500); mTranslate.setFillAfter(true);
	 * mTranslate.setAnimationListener(new AnimationListener() { public void
	 * onAnimationEnd(Animation _animation) { isAnimationFinish=true;
	 * mAnimaStartX=(int) mToXDelta; } public void onAnimationRepeat(Animation
	 * _animation) {
	 * 
	 * } public void onAnimationStart(Animation _animation) { } });
	 * isAnimationFinish=false; mLastSelectPosition=mCurrentSelectPosition;
	 * mCurrentSelectPosition = position;
	 * mSelectPanel.startAnimation(mTranslate); } }
	 */

	public int getCurrentSelectPosition() {
		return mCurrentSelectPosition;
	}

	public int getSelectPosition() {
		if (mSelectPosition == -1) {
			mSelectPosition = 0;
			return mSelectPosition;
		} else {
			return mCurrentSelectPosition;
		}
	}

	public interface ItemSelectedListener {
		public void onItemSelected(int position, View currentItem, View lastItem);
	}
}
