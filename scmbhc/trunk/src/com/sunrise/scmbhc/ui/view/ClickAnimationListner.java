package com.sunrise.scmbhc.ui.view;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

public class ClickAnimationListner implements AnimationListener {

	private OnClickListener mListener;
	private View view;
	private Animation animation;

	public ClickAnimationListner(View view, OnClickListener clicklistener, int animResId) {
		this.view = view;
		mListener = clicklistener;
		animation = AnimationUtils.loadAnimation(view.getContext(), animResId);
		animation.setAnimationListener(this);
	}

	public void startAnim() {
		view.startAnimation(animation);
	}

	@Override
	public void onAnimationEnd(Animation arg0) {
		if (mListener != null)
			mListener.onClick(view);
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {

	}

	@Override
	public void onAnimationStart(Animation arg0) {

	}

}
