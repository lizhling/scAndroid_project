package com.sunrise.scmbhc.adapter;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.database.ScmbhcStore;
import com.sunrise.scmbhc.entity.Message;

@SuppressLint("SimpleDateFormat")
public class MessageAdapter extends CursorAdapter {
	// private final static String TAG="MessageAdapter";
	private ColorStateList mOperationCompleteTitleColor;
	private ColorStateList mUnOperationCompleteTitleColor;
	private LayoutInflater mInflater;
	private OnContentChangedListener mOnContentChangedListener;
	private App cmucApplication;

	public MessageAdapter(Context context, Cursor cursor) {
		super(context, cursor, false);
		cmucApplication = (App) context.getApplicationContext();
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Resources resource = (Resources) context.getResources();
		mUnOperationCompleteTitleColor = (ColorStateList) resource
				.getColorStateList(R.color.black);
		mOperationCompleteTitleColor = (ColorStateList) resource
				.getColorStateList(R.color.darkdark_gray);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		bindView(view, cursor);
	}

	private void bindView(View view, Cursor cursor) {
		String title = cursor.getString(cursor
				.getColumnIndex(ScmbhcStore.Messages.TITLE));
		String textContent = cursor.getString(cursor
				.getColumnIndex(ScmbhcStore.Messages.TEXT_CONTENT));
		long messageTime = cursor.getLong(cursor
				.getColumnIndex(ScmbhcStore.Messages.MESSAGE_TIME));
		int readed = cursor.getInt(cursor
				.getColumnIndex(ScmbhcStore.Messages.READED));
		int opreationComplete = cursor.getInt(cursor
				.getColumnIndex(ScmbhcStore.Messages.OPERATION_COMPLETE));
		String optionType = cursor.getString(cursor
				.getColumnIndex(ScmbhcStore.Messages.OPTION_TYPE));

		TextView titleView = (TextView) view
				.findViewById(R.id.message_title_view);
		TextView messageContentView = (TextView) view
				.findViewById(R.id.tv_message_content);
		TextView messageTimeView = (TextView) view
				.findViewById(R.id.message_date_view);
		ImageView cloudImageView = (ImageView) view.findViewById(R.id.cloud);
		ImageView typeImageView = (ImageView) view.findViewById(R.id.type_icon);

		if (readed != 0) {
			// 已读
			titleView.setTextColor(mOperationCompleteTitleColor);
			//cloudImageView.setImageResource(R.drawable.ic_launcher);
		} else {
			// 未读
			titleView.setTextColor(mUnOperationCompleteTitleColor);
			//cloudImageView.setImageResource(R.drawable.ic_launcher);
		}

		if (optionType.equals(Message.WEB_OPTION_TYPE)) {
			typeImageView.setImageResource(R.drawable.ic_launcher);
		} else if (optionType.equals(Message.FEEDBACK_OPTION_TYPE)) {
			if (opreationComplete != 0)
				typeImageView.setImageResource(R.drawable.ic_launcher);
			else
				typeImageView.setImageResource(R.drawable.ic_launcher);
		} else if (optionType.equals(Message.FUNCTION_OPTION_TYPE)) {
			typeImageView.setImageResource(R.drawable.ic_launcher);
		} else {
			typeImageView.setImageDrawable(null);
		}
		titleView.setText(title);
		messageContentView.setText(textContent);
		SimpleDateFormat dateformat2 = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm");
		messageTimeView.setText(dateformat2.format(messageTime));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.message_list_item, null);
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
