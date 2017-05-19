package com.sunrise.marketingassistant.adapter;

import java.util.List;

import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.entity.ChannelInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChannelAdapter extends BaseAdapter {

	private List<ChannelInfo> data;
	private LayoutInflater mInflater;

	public ChannelAdapter(Context context, List<ChannelInfo> data) {
		super();
		this.data = data;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.tree_list_item, null);
		}

		ImageView icon = (ImageView) convertView.findViewById(R.id.id_treenode_icon);
		TextView label = (TextView) convertView.findViewById(R.id.id_treenode_label);

		ChannelInfo dto = data.get(position);
		label.setText(dto.getGROUP_NAME());
		if (Integer.parseInt(dto.getHAS_CHILD()) == 1) {
			icon.setVisibility(View.INVISIBLE);
		} else {
			icon.setVisibility(View.VISIBLE);
		}

		return convertView;
	}
	// private void initView(ViewHolder viewHolder,View convertView){
	// viewHolder.icon = (ImageView)
	// convertView.findViewById(R.id.id_treenode_icon);
	// viewHolder.label = (TextView)
	// convertView.findViewById(R.id.id_treenode_label);
	// }
	// private final class ViewHolder {
	// ImageView icon;
	// TextView label;
	// }

}
