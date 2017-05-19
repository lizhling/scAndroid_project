package com.fragment;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.App;
import com.domain.AppInfo;
import com.listview.BaseHolder;
import com.sunrise.javascript.utils.LogUtlis;
import com.util.AsyncImageLoader;
import com.util.AsyncImageLoader.ImageCallBack;

public class ViewHolder<T> extends BaseHolder<AppInfo> {
	
	ImageView item_icon;
	TextView item_tile,item_size,item_bottom;
	RatingBar item_rating;

	public ViewHolder(int resId) {
		super(resId);

		this.item_icon = (ImageView)mConvertView.findViewById(R.id.button1);
		this.item_tile = (TextView)mConvertView.findViewById(2);
		this.item_size = (TextView)mConvertView.findViewById(2);
		this.item_bottom = (TextView)mConvertView.findViewById(2);
		this.item_rating = (RatingBar)mConvertView.findViewById(1);
	}

	@Override
	protected void refreshView(AppInfo data) {
		this.item_tile.setText(data.getName());
		String size = Formatter.formatFileSize(App.getInstance(), data.getSize());
		this.item_size.setText(size);
		this.item_bottom.setText(data.getName());
		float stars = 12f;
		this.item_rating.setRating(stars);
		String iconUrl = data.getIconUrl();
		App.getImageLoader().loadDrawable(iconUrl, new ImageViewCallback(this.item_icon));
	}

	public View getView(int position,View convertView,ViewGroup parent){
		
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder(1);
		}else{
			holder = (ViewHolder)convertView.getTag();
			
		}
		holder.setData(new ArrayList().get(position));
		return holder.getConvertView();
	}
	
	
	
	
	
	
	
	
	
	
	class ImageViewCallback implements ImageCallBack {
		ImageView imageView;

		ImageViewCallback(ImageView imageView) {
			this.imageView = imageView;
			LogUtlis.e("Í¼Æ¬Â·¾¶ÐÅÏ¢£º", imageView.toString());
		}

		@Override
		public void loadImage(Bitmap d) {
			imageView.setImageBitmap(d);
		}
	}
}
