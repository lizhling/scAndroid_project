package com.sunrise.scmbhc.adapter;
import java.util.ArrayList;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.sunrise.scmbhc.entity.PreferentialInfo;

public class PreferentialInfoAdapter extends BaseAdapter {
	private ArrayList<PreferentialInfo> mPreferentialInfos;
	private Context mContext;
	
	public PreferentialInfoAdapter(Context context,ArrayList<PreferentialInfo> preferentialInfos) {
		super();
		this.mPreferentialInfos = preferentialInfos;
		this.mContext=context;
	}

	@Override
	public int getCount() {
		int size=mPreferentialInfos.size();
		if(size!=0){
			size=size * (Integer.MAX_VALUE / size);
		}
		return size;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PreferentialInfo preferentialInfo=mPreferentialInfos.get(position%mPreferentialInfos.size());
		return preferentialInfo.bindDataToGallery(convertView, mContext);
	}

	
	
}
