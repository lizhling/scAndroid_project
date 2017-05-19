package com.starcpt.cmuc.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.starcpt.cmuc.R;
import com.starcpt.cmuc.ui.view.widget.WheelView;
import com.starcpt.cmuc.ui.view.widget.adapters.ArrayWheelAdapter;

public class SelectSubAccountDialog extends Dialog {
	private WheelView mSubAccountView;
	private String[] subAccounts;
	private Context mContext;
	private TextView mTitleView;
	private String mTitle;
	private Button mButton;
	private ImageView mArrowUp;
	private ImageView mArrowDown;
	public SelectSubAccountDialog(Context context,String title,String[] subAccounts,View.OnClickListener onClickSureListener) {
		super(context);
		this.mContext=context;
		this.subAccounts=subAccounts;
		mTitle=title;
		initView(onClickSureListener);
	}

	private void initView(View.OnClickListener onClickSureListener){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.select_sub_account);
		mArrowUp=(ImageView) findViewById(R.id.arrow_up);
		
		mArrowDown=(ImageView) findViewById(R.id.arrow_down);
		mTitleView=(TextView) findViewById(R.id.tvTitle);
		String message=mContext.getString(R.string.please_select_account_l)+"<font color=#E61A6B>"+mTitle+"</font>"+mContext.getString(R.string.please_select_account_r);
		mTitleView.setText(Html.fromHtml(message));
		mButton=(Button) findViewById(R.id.btn_confirm);
		mButton.setOnClickListener(onClickSureListener);
		
		mSubAccountView=(WheelView) findViewById(R.id.sub_account_wheel);
		mSubAccountView.setArrowUp(mArrowUp);
		mSubAccountView.setArrowDown(mArrowDown);
		ArrayWheelAdapter<String> adapter=new ArrayWheelAdapter<String>(mContext,subAccounts);
		mSubAccountView.setViewAdapter(adapter);
	}
	public int getCurrentItem(){
		return mSubAccountView.getCurrentItem();
	}
	
	public void setSelectItem(int index){
		if(mSubAccountView!=null&&index<=subAccounts.length-1){
			mSubAccountView.setCurrentItem(index);
		}
	}
	
}
