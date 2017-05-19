package com.starcpt.cmuc.ui.view;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.starcpt.cmuc.R;
import com.starcpt.cmuc.ui.skin.SkinManager;

public abstract class AbstractChoickDialog<T> extends Dialog implements OnClickListener{

	protected Context mContext;
	protected View mParentView;
	protected TextView mTVTitle;
	protected Button mButtonOK;
	protected Button mButtonCancel;
	private LinearLayout mButtonPanel;
	protected ListView mListView;
	
	protected List<T> mList;
	protected OnClickListener mOkClickListener;
	
	public AbstractChoickDialog(Context context, List<T> list) {
		super(context);
		// TODO Auto-generated constructor stub	
		mContext = context;
		mList = list;	
		initView(mContext);	
	}
	
	
	protected void initView(Context context)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.popwindow_listview_layout);
		findViewById(R.id.dialog_title_bar).setBackgroundDrawable(SkinManager.getSkinDrawable(context, R.drawable.dialog_title_bar));
		findViewById(R.id.dialog_layout).setBackgroundDrawable(SkinManager.getSkinDrawable(context, R.drawable.dialog_bg));
		mButtonPanel=(LinearLayout) findViewById(R.id.button_panel);
		mTVTitle = (TextView) findViewById(R.id.tvTitle);
		mTVTitle.setTextColor(SkinManager.getSkinColor(context, R.color.dilog_title_text));
		mButtonOK = (Button) findViewById(R.id.btnOK);
		mButtonOK.setBackgroundDrawable(SkinManager.getGreenBtnDrawable(context));
		mButtonOK.setOnClickListener(this);
		mButtonCancel = (Button) findViewById(R.id.btnCancel);
		mButtonCancel.setBackgroundDrawable(SkinManager.getGreyBtnDrawable(context));
		mButtonCancel.setOnClickListener(this);
		mListView = (ListView)findViewById(R.id.listView);   	
	    Window dialogWindow = getWindow();         	    
		ColorDrawable dw = new ColorDrawable(0x0000ff00);
	    dialogWindow.setBackgroundDrawable(dw);
	}
	
	public void setTitle(String title)
	{
		mTVTitle.setText(title);
	}
	
	public void setOnOKButtonListener(OnClickListener onClickListener) {
		mOkClickListener = onClickListener;
	}

	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.btnOK:
			onButtonOK();
			break;
		case R.id.btnCancel:
			onButtonCancel();
			break;
		}
	}
	
	protected void onButtonOK()
	{
		dismiss();	
		if (mOkClickListener != null)
		{
			mOkClickListener.onClick(this, 0);
		}
	}
	
	protected void onButtonCancel()
	{
		dismiss();

	}
	
	 public void setListViewHeightBasedOnChildren(ListView listView) { 
         ListAdapter listAdapter = listView.getAdapter(); 
         if (listAdapter == null ) { 
                 // pre-condition 
                 return; 
         } 
   
         int totalHeight = 0; 
         for (int i = 0; i < listAdapter.getCount(); i++) { 
                 View listItem = listAdapter.getView(i, null, listView); 
                 listItem.measure(0, 0); 
                 totalHeight += listItem.getMeasuredHeight();   
         } 
         totalHeight += 10;
         ViewGroup.LayoutParams params = listView.getLayoutParams(); 
         params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)); 
         listView.setLayoutParams(params); 
     } 
	 
	 public void setVisibleButton(int visibility){
		 if(mButtonPanel!=null){
			 mButtonPanel.setVisibility(visibility);
		 }
	 }
}
