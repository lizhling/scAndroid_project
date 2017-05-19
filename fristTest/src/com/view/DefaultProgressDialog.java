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
		//getCompoundDrawables��Ҫ�ǻ�ȡ����е��ĸ���λ�����ϣ��ң��£��ĸ���λ��ͼƬ
		//������ķ����У���Ҫ��Ϊ�˻�ȡ��������ȻЧ��������ֻȡһ����λ��ͼƬ��
		//��ȡ�÷�λ��ͼƬ��һ������Ч�����ͼ������animation_list_loadingbar.xml����default_progress_dialog.xml������
		animation = (AnimationDrawable) textView.getCompoundDrawables()[0];
		//���öԻ�����Ļ�߶ȣ�getWindow() �Ǹ���Dialog�ķ���
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
