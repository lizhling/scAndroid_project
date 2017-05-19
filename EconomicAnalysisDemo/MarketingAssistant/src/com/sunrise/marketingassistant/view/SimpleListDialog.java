package com.sunrise.marketingassistant.view;

import java.util.ArrayList;
import java.util.List;

import com.sunrise.marketingassistant.R;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * 
 * @author fuheng
 * 
 */
public class SimpleListDialog extends DefaultDialog implements OnItemClickListener {
	private ListView mListview;

	private OnConfirmListener mOnConfirmListener;
	private List<String> mList;

	public SimpleListDialog(Context context) {
		super(context, R.layout.simple_list_dialog);
		mListview = (ListView) findViewById(R.id.dialog_list);
		mListview.setOnItemClickListener(this);
	}

	public SimpleListDialog setListChoice(List<String> list) {
		mList = list;
		mListview.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.simple_list_item_single_choice, mList));
		return this;
	}

	public SimpleListDialog setListChoice(String[] args) {
		mList = new ArrayList<String>();
		for (String s : args) {
			mList.add(s);
		}
		mListview.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.simple_list_item_single_choice, mList));
		return this;
	}

	public SimpleListDialog setOnConfirmListener(OnConfirmListener listener) {
		mOnConfirmListener = listener;
		return this;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		dismiss();
		if (mOnConfirmListener != null)
			mOnConfirmListener.onClick(this, mList.get(arg2), arg2);
	}

}
