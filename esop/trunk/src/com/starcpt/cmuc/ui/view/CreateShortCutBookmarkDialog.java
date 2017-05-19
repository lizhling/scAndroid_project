package com.starcpt.cmuc.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.starcpt.cmuc.R;

public class CreateShortCutBookmarkDialog extends Dialog {
	private TextView mTitleView;
	private Button mConfirmButton;
	private Button mCancleButton;
	private EditText mBookmarkEditText;
	
	public CreateShortCutBookmarkDialog(Context context,int resId,View.OnClickListener onClickListener) {
		super(context);
		initView(context,context.getString(resId),onClickListener);
	}

	public CreateShortCutBookmarkDialog(Context context,String title,View.OnClickListener onClickListener) {
		super(context);
		initView(context,title,onClickListener);
	}
	
	
	protected void initView(Context context,String title,View.OnClickListener onClickListener)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.create_shortcut_dialog);
		mTitleView=(TextView) findViewById(R.id.tvTitle);
		mTitleView.setText(title);
		mConfirmButton=(Button) findViewById(R.id.confirm);
		mConfirmButton.setOnClickListener(onClickListener);
		
		mCancleButton=(Button) findViewById(R.id.cancel);		
		mCancleButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		mBookmarkEditText=(EditText) findViewById(R.id.bookmark_title);
	    Window dialogWindow = getWindow();         	        
		ColorDrawable dw = new ColorDrawable(0x0000ff00);
	    dialogWindow.setBackgroundDrawable(dw);
	}
	
	public String getBookmarkTitle(){
		return mBookmarkEditText.getText().toString();
	}
	
	public void setBookmarkTitle(String bookMarkTitle){
		mBookmarkEditText.setText(bookMarkTitle);
	}

}
