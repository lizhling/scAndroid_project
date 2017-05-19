package com.sunrise.micromarketing.listener;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;

/**
 * 封装用于点击动画的监听。
 * 
 * @author fuheng
 * 
 */
public class ClickAnimationListener implements AnimationListener {
	private AnimationListener animationListener;

	private View v;

	private Animation animation;

	public ClickAnimationListener(View v, int animResId,
			AnimationListener animationListener) {
		this.animationListener = animationListener;
		this.v = v;

		animation = AnimationUtils.loadAnimation(v.getContext(), animResId);
	}

	public ClickAnimationListener(View v, Animation animation,
			AnimationListener animationListener) {
		this.animationListener = animationListener;
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
		if (animationListener != null)
			animationListener.onAnimationEnd(animation);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		if (animationListener != null)
			animationListener.onAnimationRepeat(animation);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		if (animationListener != null)
			animationListener.onAnimationStart(animation);
		v.setEnabled(false);
	}

}
