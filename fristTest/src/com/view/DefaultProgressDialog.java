package com.view;

import com.example.fristtest.R;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.TextView;

public class DefaultProgressDialog extends DefaultDialog {

	private TextView textView;
	private AnimationDrawable animation;
	
	public DefaultProgressDialog(Context context) {
		super(context,R.layout.default_progress_dialog);
		textView = (TextView) findViewById(android.R.id.text1);
		//getCompoundDrawables主要是获取组件中的四个方位（左，上，右，下）四个方位的图片
		//在下面的方法中，主要是为了获取滚动条进然效果，所以只取一个方位的图片，
		//并取该方位的图片是一个动画效果组件图，放在animation_list_loadingbar.xml，被default_progress_dialog.xml中引用
		animation = (AnimationDrawable) textView.getCompoundDrawables()[0];
		//设置对话框屏幕高度，getWindow() 是父类Dialog的方法
		getWindow().getAttributes().y = Math.round(context.getResources().getDisplayMetrics()
					.heightPixels*0.118f);
	}
	
	public void setMessage(CharSequence text){
		textView.setText(text);
	}
	
	public void show(){
		if(animation != null)
			animation.start();
		super.show();
	}
	
	public void dismiss(){
		if(animation != null)
			animation.stop();
		super.dismiss();
	}	 	
	
}
