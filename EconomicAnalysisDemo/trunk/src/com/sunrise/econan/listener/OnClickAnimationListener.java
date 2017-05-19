package com.sunrise.econan.listener;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;

/**
 * 封装用于点击动画的监听。
 * 
 * @author fuheng
 * 
 */
public class OnClickAnimationListener implements OnClickListener,
		AnimationListener {
	private OnClickListener clickListener;

	private View v;

	private Animation animation;

	public OnClickAnimationListener(View v, int animResId,
			OnClickListener clickListener) {
		this.clickListener = clickListener;
		this.v = v;

		animation = AnimationUtils.loadAnimation(v.getContext(), animResId);
	}

	public OnClickAnimationListener(View v, Animation animation,
			OnClickListener clickListener) {
		this.clickListener = clickListener;
		this.v = v;
		this.animation = animation;
	}

	public void startAnimation() {
		animation.setAnimationListener(this);
		v.startAnimation(animation);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		v.setEnabled(true);
		if (clickListener != null)
			clickListener.onClick(v);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
		v.setEnabled(false);
	}

	@Override
	public void onClick(View arg0) {
		startAnimation();
	}

	/**
	 * 初始化按钮动作。
	 * 
	 * @param viewparent
	 * @param viewResId
	 * @param animResId
	 * @param listener
	 */
	public static final void initAnimationClickListener(View viewparent,
			int viewResId, int animResId, OnClickListener listener) {
		View view = viewparent.findViewById(viewResId);
		initAnimationClickListener(view, animResId, listener);
	}

	/**
	 * 初始化按钮动作。
	 * 
	 * @param view
	 * @param animResId
	 * @param listener
	 */
	public static final void initAnimationClickListener(View view,
			int animResId, OnClickListener listener) {
		view.setOnClickListener(new OnClickAnimationListener(view, animResId,
				listener));
	}

	/**
	 * 初始化按钮动作。
	 * 
	 * @param activity
	 * @param viewResId
	 * @param animResId
	 * @param listener
	 */
	public static final void initAnimationClickListener(Activity activity,
			int viewResId, int animResId, OnClickListener listener) {
		View view = activity.findViewById(viewResId);
		initAnimationClickListener(view, animResId, listener);
	}
}
