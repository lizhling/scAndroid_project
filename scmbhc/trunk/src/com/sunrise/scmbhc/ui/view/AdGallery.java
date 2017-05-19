package com.sunrise.scmbhc.ui.view;

import com.sunrise.scmbhc.ui.view.ScrollAdGallery.OnGalleryClickListener;
import com.sunrise.scmbhc.ui.view.ScrollAdGallery.OnGallerySwitchListener;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Gallery;

@SuppressLint("HandlerLeak")
public class AdGallery extends Gallery implements android.widget.AdapterView.OnItemClickListener, android.widget.AdapterView.OnItemSelectedListener,
		OnTouchListener {
	private int mSwitchTime; // 图片切换时间
	private int mImageNumber;
	private boolean runflag = false;
	// private Timer mTimer; // 自动滚动的定时器
	private OnGallerySwitchListener mGallerySwitchListener;
	private OnGalleryClickListener mGalleryClickListener;

//	private static final String ACTION_PICTURE_SCROLL = "com.sunrise.scmbhc.ui.view.picture_scroll";// 滚动广播
	
	private String mActionPictureScoll = null;
	
	private static PendingIntent mPanddingIntent;
	private ScrollReceiver mScrollReceiver;// 广播接收器对象
	private static AlarmManager sAlarmManager;

	public AdGallery(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (sAlarmManager == null)
			sAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		mActionPictureScoll = context.getPackageCodePath() + toString();
		
		this.setOnTouchListener(this);
		this.setOnItemSelectedListener(this);
		this.setOnItemClickListener(this);
		this.setSoundEffectsEnabled(false);
		setFocusableInTouchMode(true);
	}

	public void onWindowVisibilityChanged(int visible) {
		if (visible == VISIBLE) {

			// 设置广播接收
			addReceiver();

			if (runflag) {
				removeScrollIntent();
				addScrollIntent();
			}

		} else {

			if (runflag)
				removeScrollIntent();

			// 取消广播接收
			removeReceiver();
		}
	}

	public void setSwitchTime(int switchTime) {

		if (runflag && this.mSwitchTime != switchTime) {
			removeScrollIntent();
			addScrollIntent();
		}
		this.mSwitchTime = switchTime;
	}

	public void setGallerySwitchListener(OnGallerySwitchListener mGallerySwitchListener) {
		this.mGallerySwitchListener = mGallerySwitchListener;
	}

	public void setGalleryClickListener(OnGalleryClickListener mGalleryClickListener) {
		this.mGalleryClickListener = mGalleryClickListener;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		int kEvent;
		if (isScrollingLeft(e1, e2)) {
			// 检查是否往左滑动
			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		} else {// 检查是否往右滑动
			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(kEvent, null);
		return true;

	}

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return e2.getX() > (e1.getX() + 50);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return super.onScroll(e1, e2, distanceX, distanceY);
	}

	/**
	 * 开始自动滚动
	 */
	public void startAutoScroll() {
		setRunFlag(true);

		removeScrollIntent();
		addScrollIntent();
	}

	/**
	 * 重置自动滚动任务
	 * 
	 * @param flag
	 *            true:自动
	 */
	public void setRunFlag(boolean flag) {

		if (flag == runflag)
			return;

		if (flag)
			addReceiver();

		runflag = flag;

	}

	public boolean isAutoScrolling() {
		return runflag;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (MotionEvent.ACTION_UP == event.getAction() || MotionEvent.ACTION_CANCEL == event.getAction()) {
			// 重置自动滚动任务
			// setRunFlag(true);
			addReceiver();
		} else {
			// 停止自动滚动任务
			// setRunFlag(false);
			removeReceiver();
		}
		return false;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		mGallerySwitchListener.onGallerySwitch(position % mImageNumber);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	public int getImageNumber() {
		return mImageNumber;
	}

	public void setImageNumber(int mImageNumber) {
		this.mImageNumber = mImageNumber;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		mGalleryClickListener.onGalleryClick(position % mImageNumber);
	}

	private void removeScrollIntent() {
		if (mPanddingIntent != null) {
			sAlarmManager.cancel(mPanddingIntent);
			mPanddingIntent = null;
		}
	}

	private void addScrollIntent() {
		mPanddingIntent = PendingIntent.getBroadcast(getContext(), hashCode(), new Intent(mActionPictureScoll), 0);
		sAlarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), mSwitchTime, mPanddingIntent);
	}

	private void addReceiver() {
		if (mScrollReceiver == null) {
			mScrollReceiver = new ScrollReceiver();
			getContext().registerReceiver(mScrollReceiver, new IntentFilter(mActionPictureScoll));
		}
	}

	private void removeReceiver() {
		if (mScrollReceiver != null) {
			getContext().unregisterReceiver(mScrollReceiver);
			mScrollReceiver = null;
		}
	}

	/**
	 * 流量提醒广播接收器
	 * 
	 * @author fuheng
	 * 
	 */
	class ScrollReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(mActionPictureScoll)) {

				int position = getSelectedItemPosition();
				if (position >= (getCount() - 1)) {
					setSelection(getCount() / 2, true); // 跳转到第二张图片，在向左滑动一张就到了第一张图片
					onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
				} else {
					onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
				}

			}
		}
	}

}
