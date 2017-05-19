package com.starcpt.cmuc.adapter;

import java.util.List;

import com.starcpt.cmuc.R;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShareAppAdatper extends BaseAdapter {

	private List<ResolveInfo> apps;
	private LayoutInflater mInflater;
	private PackageManager mPackageManager;

	public ShareAppAdatper(Context context, List<ResolveInfo> apps) {
		super();
		this.apps = apps;
		mInflater = LayoutInflater.from(context);
		mPackageManager = context.getPackageManager();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return apps.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return apps.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		ResolveInfo appInfo = apps.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.share_app_item, null);
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) convertView
					.findViewById(R.id.app_icon);
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.app_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.icon.setImageDrawable(appInfo.loadIcon(mPackageManager));
		viewHolder.name.setText(appInfo.loadLabel(mPackageManager).toString());
		return convertView;
	}

	class ViewHolder {
		ImageView icon;
		TextView name;
	}

}
