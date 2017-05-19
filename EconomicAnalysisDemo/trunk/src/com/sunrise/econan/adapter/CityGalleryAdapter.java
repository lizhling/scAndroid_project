package com.sunrise.econan.adapter;

import java.util.ArrayList;

import com.sunrise.econan.listener.OnClickAnimationListener;
import com.sunrise.econan.model.CityInfo;
import com.sunrise.econan.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CityGalleryAdapter extends BaseAdapter {

	private ArrayList<CityInfo> list;
	private Context context;

	private OnClickListener eyeClickListener;

	private int[] array_level = { R.drawable.level_0, R.drawable.level_1,
			R.drawable.level_2, R.drawable.level_3 };

	public CityGalleryAdapter(Context context, ArrayList<CityInfo> array) {
		this.context = context;
		list = array;
	}

	@Override
	public int getCount() {
		if (list != null)
			return list.size();
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		if (list != null)
			return list.get(arg0);
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		if (arg1 == null) {
			arg1 = LayoutInflater.from(context).inflate(
					R.layout.item_grid_city, null);
		}

		CityInfo cityinfo = list.get(position);
		{

			TextView textView_city = (TextView) arg1
					.findViewById(R.id.textView_city);
			textView_city.setText(cityinfo.getName());
		}
		{
			TextView textView_indicatorScore = (TextView) arg1
					.findViewById(R.id.textView_indicatorScore);
			textView_indicatorScore.setText("" + cityinfo.getIndicatorScore());
		}
		{
			TextView textView_averageScore = (TextView) arg1
					.findViewById(R.id.textView_averageScore);
			textView_averageScore.setText("" + cityinfo.getAverageScore());
		}
		{
			TextView textView_ranking = (TextView) arg1
					.findViewById(R.id.textView_ranking);
			textView_ranking.setText("" + cityinfo.getRanking());
		}
		{
			ImageView level = (ImageView) arg1
					.findViewById(R.id.imageView_level);
			level.setImageResource(array_level[cityinfo.getLevel() - 1]);
		}
		{
			View view = arg1.findViewById(R.id.imageView_eyeview);
			view.setTag(position);
			OnClickAnimationListener.initAnimationClickListener(view,
					R.anim.click_scale, eyeClickListener);
		}

		arg1.setBackgroundResource(position % 2 == 0 ? R.drawable.bg_item1_xml
				: R.drawable.bg_item2_xml);
		return arg1;
	}

	public void setEyeClickListener(OnClickListener eyeClickListener) {
		this.eyeClickListener = eyeClickListener;
	}
}
