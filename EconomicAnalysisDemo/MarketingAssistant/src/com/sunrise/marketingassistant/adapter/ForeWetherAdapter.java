package com.sunrise.marketingassistant.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.location.AMapLocalDayWeatherForecast;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.utils.CommUtil;

public class ForeWetherAdapter extends BaseAdapter {

	private ArrayList<AMapLocalDayWeatherForecast> data;
	private Context context;

	private static final int[] fResIdsForWeather = { R.drawable.ic_sunshine, R.drawable.ic_clouds, R.drawable.ic_cloudysky, R.drawable.ic_fog,
			R.drawable.ic_littlerain, R.drawable.ic_midrain, R.drawable.ic_bigrain, R.drawable.ic_thuderrain, R.drawable.ic_littlesnow, R.drawable.ic_midsnow,
			R.drawable.ic_bigsnow };
	private static final String[] fKeyWordsWeather = { "晴", "多云", "阴", "雾", "小雨", "中雨", "大雨", "阵雨", "小雪", "中雪", "大雪" };
	private float[] hsv = { 1, 1f, 1f };

	public ForeWetherAdapter(Context context, ArrayList<AMapLocalDayWeatherForecast> wetherInfo) {
		data = wetherInfo;
		this.context = context;
	}

	@Override
	public int getCount() {
		if (data == null)
			return 0;
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		if (data == null)
			return null;
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (arg1 == null)
			arg1 = LayoutInflater.from(context).inflate(R.layout.item_weather_forecase, null);
		AMapLocalDayWeatherForecast obj = data.get(arg0);
		TextView title = (TextView) arg1.findViewById(android.R.id.title);
		String week = obj.getWeek();
		switch (arg0) {
		case 0:
			week = "明天";
			break;
		case 1:
			week = "后天";
			break;
		default:
			week = CommUtil.getWeekName(week);
			break;
		}
		title.setText(week);

		TextView text1 = (TextView) arg1.findViewById(android.R.id.text1);
		text1.setText(Html.fromHtml(String.format("[昼]<font color='%d'>%s°</font>", getTemperatureColor(CommUtil.parse2Float(obj.getDayTemp()), hsv),
				obj.getDayTemp())));
		text1.setCompoundDrawablesWithIntrinsicBounds(0, getWetherResId(obj.getDayWeather()), 0, 0);

		TextView text2 = (TextView) arg1.findViewById(android.R.id.text2);
		text2.setText(Html.fromHtml(String.format("[夜]<font color='%d'>%s°</font>", getTemperatureColor(CommUtil.parse2Float(obj.getNightTemp()), hsv),
				obj.getNightTemp())));
		return arg1;
	}

	public static int getWetherResId(String weather) {
		for (int i = 0; i < fKeyWordsWeather.length; ++i)
			if (weather.contains(fKeyWordsWeather[i]))
				return fResIdsForWeather[i];

		return fResIdsForWeather[1];
	}
	
	public static int getTemperatureColor(float temperature, float[] hsv) {
		// hsv[1] = Math.min(Math.abs((temperature - 20) / 10 + 0.2f), 1f);
		if (temperature > 30) {
			hsv[0] = Math.max((40 - temperature), 0);
			hsv[0] /= 10;
			hsv[0] = hsv[0] * 50;// / 360;
		} else if (temperature < 10) {
			hsv[0] = Math.min((10 - temperature), 20);
			hsv[0] /= 20;
			hsv[0] = hsv[0] * 80 + 180;
			// hsv[0] /= 360;
		} else {
			return 0xaa686868;
		}

		return Color.HSVToColor(hsv);
	}
}
