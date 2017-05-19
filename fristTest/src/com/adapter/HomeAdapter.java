package com.adapter;

import java.util.List;

import com.example.fristtest.R;
import com.fragment.ViewHolder;
import com.listview.BaseHolder;

public class HomeAdapter<T> extends BasicAdapter<T> {

	public HomeAdapter(List<T> datas) {
		super(datas);
	}

	@Override
	protected BaseHolder createHolder() {
		return new ViewHolder(R.layout.bcr_camera);
	}

}
