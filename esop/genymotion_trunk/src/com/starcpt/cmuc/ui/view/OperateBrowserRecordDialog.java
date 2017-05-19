package com.starcpt.cmuc.ui.view;
import com.starcpt.cmuc.R;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;

public class OperateBrowserRecordDialog {
	private ListView mListView;
	private PopupWindow mPopupWindow;
	private View mParentView;
	public OperateBrowserRecordDialog(Context context,View parentView,String[] items,OnItemClickListener onItemClickListener) {
		this.mParentView=parentView;
		initView(context,items,onItemClickListener);
	}

	private void initView(Context context,String[] items,OnItemClickListener onItemClickListener)
	{
		View view = LayoutInflater.from(context).inflate(R.layout.operate_browser_record_dialog, null);
		mListView=(ListView) view.findViewById(R.id.operate_list);
		mListView.setAdapter(new ArrayAdapter<String>(context, R.layout.operate_browser_record_item, R.id.operate_item, items));	
		mListView.setOnItemClickListener(onItemClickListener);
		mPopupWindow = new PopupWindow(view, 260, LayoutParams.WRAP_CONTENT);
    	mPopupWindow.setFocusable(true);
    	ColorDrawable dw = new ColorDrawable(0x00);
		mPopupWindow.setBackgroundDrawable(dw);
	
	}
	
	public void show(boolean bShow)
	{
		if (bShow)
		{		
			mPopupWindow.showAtLocation(mParentView, Gravity.CENTER_VERTICAL, 0, 0);
		}else{
			mPopupWindow.dismiss();
		}
	}
	
	
}
