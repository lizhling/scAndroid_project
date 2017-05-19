package com.sunrise.scmbhc.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SlidingPaneLayout extends android.support.v4.widget.SlidingPaneLayout {

	private boolean isLocked;

	public SlidingPaneLayout(Context context) {
		super(context);
	}

	public SlidingPaneLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SlidingPaneLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {
		if (isLocked)
			switch (ev.getAction()) {
			case MotionEvent.ACTION_MOVE:
				return false;
			default:
				break;
			}
		return super.onInterceptTouchEvent(ev);
	}

	public boolean onTouchEvent(android.view.MotionEvent ev) {
		if (isLocked)
			switch (ev.getAction()) {
			case MotionEvent.ACTION_MOVE:
				return false;
			default:
				break;
			}

		return super.onTouchEvent(ev);
	}

	public boolean openPane() {
		if (!isLocked)
			return super.openPane();
		return false;
	}

	public boolean closePane() {
		if (!isLocked)
			return super.closePane();
		return false;
	}
}
