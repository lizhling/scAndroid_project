package com.sunrise.scmbhc.ui.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;



import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.adapter.SpinnerAdapter;

public class MySpinner extends LinearLayout {
	private PopupWindow pop;
	private boolean isShow = false;
	private BaseAdapter adapter;
	private SpinnerAdapter spinneradapter;
	private ListView listView;
	private Button mContentView;
	private Context mContext;
	private int selectIndex;
	private OnItemClickListener onItemClickListener;

	public MySpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.my_spinner, this, true);  
        mContext=context;
        mContentView=(Button) findViewById(R.id.content);
        mContentView.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				showSpinner(mContext, MySpinner.this, getWidth());
				
			}
		});
	}
	
	public void setEnabled(boolean enabled){
		mContentView.setEnabled(enabled);
	}
	
	public void showSpinner(final Context context,View view,int width) {
		if (pop == null) {
			createPop(context,width);
		}
		if (isShow) {
			pop.dismiss();
		} else {
			pop.showAsDropDown(view);
			isShow = true;
		}
	}
	
	private void createPop(Context context,int width){
		listView = new ListView(context);
		listView.setAdapter(adapter);
		listView.setCacheColorHint(0x00000000);
		listView.setBackgroundDrawable(new ColorDrawable(0xFFFFFFF));
		listView.setDivider(null);
		listView.setSelector(R.color.transparent);
		listView.setVerticalScrollBarEnabled(false);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				pop.dismiss();
				mContentView.setText((CharSequence) adapter.getItem(position));
				spinneradapter = (SpinnerAdapter)adapter;
				
				selectIndex=position;
				if (spinneradapter != null ){
					spinneradapter.setCurrentPosition(selectIndex);
					setAdapter(spinneradapter);
					adapter.notifyDataSetChanged();
				}
				if(onItemClickListener!=null)
					onItemClickListener.onItemClick(arg0, arg1, position, arg3);
			}
		});
		pop = new PopupWindow(listView,width,
				LayoutParams.WRAP_CONTENT, true);
		pop.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_spinner_dropdown));
		pop.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				isShow = false;
			}
		});
	}
	
	public String getContent(){
		return mContentView.getText().toString();
	}
	
	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;		
		setSelectIndex(0);
	}

	public void setSelectIndex(int selectIndex) {
		this.selectIndex = selectIndex;
		mContentView.setText((CharSequence) this.adapter.getItem(selectIndex));
	}

	public int getSelectIndex() {
		return selectIndex;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public SpinnerAdapter getSpinneradapter() {
		return spinneradapter;
	}

	public void setSpinneradapter(SpinnerAdapter spinneradapter) {
		this.spinneradapter = spinneradapter;
	}
	
	
	
}
