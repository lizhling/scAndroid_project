package com.starcpt.cmuc.adapter;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.db.CmucStore;
import com.starcpt.cmuc.model.Message;
import com.sunrise.javascript.utils.DateUtils;



public class MessageAdapter extends CursorAdapter{
//private final static String TAG="MessageAdapter";
	private ColorStateList mOperationCompleteTitleColor;
	private ColorStateList mUnOperationCompleteTitleColor;
	private LayoutInflater mInflater;
	private OnContentChangedListener mOnContentChangedListener;
	private CmucApplication cmucApplication;
	public MessageAdapter(Context context, Cursor cursor) {
		super(context, cursor, false);
		cmucApplication=(CmucApplication) context.getApplicationContext();
		 mInflater = (LayoutInflater) context.getSystemService(
	                Context.LAYOUT_INFLATER_SERVICE);
		 Resources resource = (Resources) context.getResources();  
		 mUnOperationCompleteTitleColor = (ColorStateList) resource.getColorStateList(R.color.message_item_title_unop);  
		 mOperationCompleteTitleColor = (ColorStateList) resource.getColorStateList(R.color.message_item_title_oped);  
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		  bindView(view, cursor);
	}


	private void bindView(View view, Cursor cursor) {
	  String title=cursor.getString(cursor.getColumnIndex(CmucStore.Messages.TITLE));
	  String textContent=cursor.getString(cursor.getColumnIndex(CmucStore.Messages.TEXT_CONTENT));
	  long messageTime=cursor.getLong(cursor.getColumnIndex(CmucStore.Messages.MESSAGE_TIME));
	  int readed=cursor.getInt(cursor.getColumnIndex(CmucStore.Messages.READED));
	  int opreationComplete=cursor.getInt(cursor.getColumnIndex(CmucStore.Messages.OPERATION_COMPLETE));
	  String optionType=cursor.getString(cursor.getColumnIndex(CmucStore.Messages.OPTION_TYPE));
	  
	  TextView titleView=(TextView) view.findViewById(R.id.message_title_view);
	  TextView messageContentView=(TextView) view.findViewById(R.id.message_text_view);
	  TextView messageTimeView=(TextView) view.findViewById(R.id.message_date_view);
	  ImageView cloudImageView=(ImageView) view.findViewById(R.id.cloud);
	  ImageView typeImageView=(ImageView) view.findViewById(R.id.type_icon);
	  
	  if(readed!=0){
		  titleView.setTextColor(mOperationCompleteTitleColor);
		  cloudImageView.setImageResource(R.drawable.readed_cloud);
	  }else{
		  titleView.setTextColor(mUnOperationCompleteTitleColor);
		  cloudImageView.setImageResource(R.drawable.cloud);
	  }
	  
	  if(optionType.equals(Message.WEB_OPTION_TYPE)){
		  typeImageView.setImageResource(R.drawable.message_web);
	  }else if(optionType.equals(Message.FEEDBACK_OPTION_TYPE)){
		  if(opreationComplete!=0)
			  typeImageView.setImageResource(R.drawable.message_feedbacked);
		  else
			  typeImageView.setImageResource(R.drawable.message_feedbacked_no);
	  }else if(optionType.equals(Message.FUNCTION_OPTION_TYPE)){
		  typeImageView.setImageResource(R.drawable.message_function);
	  }else{
		  typeImageView.setImageDrawable(null);
	  }
	  titleView.setText(title);
	  messageContentView.setText(textContent);
	  messageTimeView.setText(DateUtils.formatTime(messageTime, DateUtils.DATE_FORMAT));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view=mInflater.inflate(R.layout.message_list_item, null);
		if(!cmucApplication.isIsPad())
			view.setLayoutParams(new ListView.LayoutParams(cmucApplication.getRealScreenWidth(), LayoutParams.WRAP_CONTENT));
		else{
			if(cmucApplication.getScreenDirection()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
				view.setLayoutParams(new ListView.LayoutParams(300, LayoutParams.WRAP_CONTENT));
			else
				view.setLayoutParams(new ListView.LayoutParams(cmucApplication.getRealScreenWidth(), LayoutParams.WRAP_CONTENT));
		}
		return view;
	}

	@Override
		protected void onContentChanged() {
		 if (getCursor() != null && !getCursor().isClosed()) {
	            if (mOnContentChangedListener != null) {
	                mOnContentChangedListener.onContentChanged(this);
	            }
	        }
		}
	
    public interface OnContentChangedListener {
        void onContentChanged(MessageAdapter adapter);
    }
    
    public void setOnContentChangedListener(OnContentChangedListener l) {
        mOnContentChangedListener = l;
    }
    

}
