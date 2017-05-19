package com.starcpt.cmuc.ui.view;

import com.starcpt.cmuc.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.widget.AutoCompleteTextView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class BubbleEditText extends AutoCompleteTextView {
	private static final int HANDLER_BUBBLE_WINDOW_MSG = 0;
	private TextView mBubbleTxt;
	private PopupWindow mBubblePopuWindow;
	private int mTextMaxLen;
	private boolean editable;
	private String mPromptInfo;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_BUBBLE_WINDOW_MSG:
				dismissBubble();
				break;
			}
		};
	};

	public BubbleEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public BubbleEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BubbleEditText);
		mTextMaxLen=array.getInteger(R.styleable.BubbleEditText_textMaxLength, 1000);
		mPromptInfo= String.format(context.getString(R.string.input_length_prompt), mTextMaxLen);
		createBubble(context);
		addTextChangedListener(new InputTextWatcher());
	}

	public BubbleEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}


	 public void setEditable(boolean editable) {  
		 this.editable=editable;
		 setEnabled(editable);
		 setFocusable(editable);
		 setFocusableInTouchMode(editable);
	   }  

	 
	 public boolean isEditable() {
		return editable;
	}

	/**
		 * 显示气泡提示信息
		 * 
		 * @param view
		 *            附着view
		 * @param text
		 *            显示内容
		 */
		public void showBubbleTxtInfo(String text) {
			int location[] = new int[2];
			mBubbleTxt.setText(text);
			if (mBubblePopuWindow.isShowing()) {
				mBubblePopuWindow.dismiss();
			}
			getLocationOnScreen(location);
			mBubblePopuWindow.showAtLocation(this, Gravity.LEFT | Gravity.TOP,
					location[0], location[1] - getHeight());
			if (mHandler.hasMessages(HANDLER_BUBBLE_WINDOW_MSG))
				mHandler.removeMessages(HANDLER_BUBBLE_WINDOW_MSG);
			mHandler.sendEmptyMessageDelayed(HANDLER_BUBBLE_WINDOW_MSG, 1500);
		}
	
		/**
		 * 创建气泡弹框
		 */
		private void createBubble(Context context) {
			mBubbleTxt = (TextView) LayoutInflater.from(context).inflate(R.layout.bubble_text, null);
			mBubblePopuWindow = new PopupWindow(mBubbleTxt, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
		}
		
		public void dismissBubble() {
			if (mHandler.hasMessages(HANDLER_BUBBLE_WINDOW_MSG))
				mHandler.removeMessages(HANDLER_BUBBLE_WINDOW_MSG);
			if (mBubblePopuWindow.isShowing())
				mBubblePopuWindow.dismiss();
		}
		
		private class InputTextWatcher implements TextWatcher{
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Editable editable = getText();
				int len = editable.length();
				if(len > mTextMaxLen){
					showBubbleTxtInfo(mPromptInfo);
					int endIndex = Selection.getSelectionEnd(editable);
					String str = editable.toString();
					
					String newStr = str.substring(0, mTextMaxLen);
					setText(newStr);
					editable = getText();
					int newLen = editable.length();
					if(endIndex > newLen){
						endIndex = newLen;
					}
					Selection.setSelection(editable, endIndex);
				}
			}
			
		}
}
