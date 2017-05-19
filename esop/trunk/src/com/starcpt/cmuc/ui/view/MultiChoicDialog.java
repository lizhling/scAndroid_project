package com.starcpt.cmuc.ui.view;

import java.util.List;

import com.starcpt.cmuc.R;
import com.starcpt.cmuc.adapter.MultiChoicAdapter;

import android.content.Context;


@SuppressWarnings("rawtypes")
public class MultiChoicDialog extends AbstractChoickDialog{

	private MultiChoicAdapter<String> mMultiChoicAdapter;

	@SuppressWarnings("unchecked")
	public <T> MultiChoicDialog(Context context, List<T> list) {
		super(context, list);
		
		initData();
	}
	

	@SuppressWarnings("unchecked")
	protected void initData() {
		// TODO Auto-generated method stub
		mMultiChoicAdapter = new MultiChoicAdapter<String>(mContext, mList,R.drawable.selector_checkbox1);
		
		mListView.setAdapter(mMultiChoicAdapter);
		mListView.setOnItemClickListener(mMultiChoicAdapter);   
		
		setListViewHeightBasedOnChildren(mListView);

	}


	public boolean[] getSelectItem()
	{
		return mMultiChoicAdapter.getSelectItem();
	}
	
}
