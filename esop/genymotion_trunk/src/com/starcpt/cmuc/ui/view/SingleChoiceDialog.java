package com.starcpt.cmuc.ui.view;

import java.util.List;
import android.content.Context;

import com.starcpt.cmuc.R;
import com.starcpt.cmuc.adapter.SingleChoicAdapter;

public class SingleChoiceDialog extends AbstractChoickDialog{

	private SingleChoicAdapter<String> mSingleChoicAdapter;

	public SingleChoiceDialog(Context context, List<String> list) {
		super(context, list);	
		initData();
	}

	protected void initData() {
		// TODO Auto-generated method stub
		mSingleChoicAdapter = new SingleChoicAdapter<String>(mContext, mList, R.drawable.selector_checkbox1);	
		mListView.setAdapter(mSingleChoicAdapter);
		mListView.setOnItemClickListener(mSingleChoicAdapter);   		
		setListViewHeightBasedOnChildren(mListView);	
	}


	public int getSelectItem()
	{
		return mSingleChoicAdapter.getSelectItem();
	}

	public void setSelectItem(int position)
	{
		if(position!=mSingleChoicAdapter.getSelectItem())
		 mSingleChoicAdapter.setSelectItem(position);
	}

}
