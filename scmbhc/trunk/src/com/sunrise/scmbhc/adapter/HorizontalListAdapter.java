package com.sunrise.scmbhc.adapter;
import java.util.List;
import com.sunrise.scmbhc.entity.BusinessMenu;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class HorizontalListAdapter extends BaseAdapter{
	private List<BusinessMenu> list;
	private Context mContext;
	public HorizontalListAdapter(Context context, List<BusinessMenu> list){
		this.mContext=context;
		this.list = list;
	}


	@Override
	public int getCount() {
		  return list.size();  
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		BusinessMenu businessMenu = list.get(position);
		return businessMenu.bindDataToHoritalListItemView(convertView, mContext);
	}
	
}
