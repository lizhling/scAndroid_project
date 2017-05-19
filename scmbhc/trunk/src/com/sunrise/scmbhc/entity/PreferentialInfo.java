package com.sunrise.scmbhc.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.ui.activity.BaseActivity;
import com.sunrise.scmbhc.ui.fragment.BaseFragment;
import com.sunrise.scmbhc.ui.fragment.PreferentialInfoDetailFragment;
import com.sunrise.scmbhc.utils.AsyncImageLoader;
import com.sunrise.scmbhc.utils.AsyncImageLoader.ImageCallBack;

public class PreferentialInfo {
public final static int SCROLL_TYPE=0;
public final static int GENERAL_TYPE=1;
public final static int NEEDLOGIN = 1;
public final static int UNNEEDLOGIN = 0;
private long id;
private int type;
private int needLogin;	// 是否需要登录
private String title;
private String subtitle;
private String icon;
private String bigIcon;
private String detailsUrl;
private Bitmap iconBitmap;

public long getId() {
	return id;
}
public void setId(long id) {
	this.id = id;
}
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public String getSubtitle() {
	return subtitle;
}
public void setSubtitle(String subtitle) {
	this.subtitle = subtitle;
}
public String getIcon() {
	return icon;
}
public void setIcon(String icon) {
	this.icon = icon;
}
public String getBigIcon() {
	return bigIcon;
}
public void setBigIcon(String bigIcon) {
	this.bigIcon = bigIcon;
}
public String getDetailsUrl() {
	return detailsUrl;
}
public void setDetailsUrl(String detailsUrl) {
	this.detailsUrl = detailsUrl;
}
public Bitmap getIconBitmap() {
	return iconBitmap;
}
public void setIconBitmap(Bitmap iconBitmap) {
	this.iconBitmap = iconBitmap;
}



public int getType() {
	return type;
}
public void setType(int type) {
	this.type = type;
}

public View bindDataToGallery(View convertView,Context context){
	ViewHolder viewHolder;
	if (convertView == null) {
		viewHolder = new ViewHolder();
		convertView = LayoutInflater.from(context).inflate(R.layout.adgallery_image, null);
		Gallery.LayoutParams params = new Gallery.LayoutParams(Gallery.LayoutParams.FILL_PARENT,Gallery.LayoutParams.WRAP_CONTENT);
		convertView.setLayoutParams(params);
		viewHolder.imageview = (ImageView) convertView.findViewById(R.id.gallery_image);
		convertView.setTag(viewHolder);
	} else {
		viewHolder = (ViewHolder) convertView.getTag();
	}
	
	if(iconBitmap!=null){
		viewHolder.imageview.setImageBitmap(iconBitmap);
	}else{
		asyncLoadImage(context,viewHolder.imageview,bigIcon);
	}
	return convertView;
}

public View bindDataToSearchList(View convertView,Context context){
	SearchViewHolder viewHolder;
	if (convertView == null) {
		viewHolder = new SearchViewHolder();
		convertView = LayoutInflater.from(context).inflate(R.layout.search_item, null);
		viewHolder.textView = (TextView) convertView.findViewById(R.id.search_name);
		convertView.setTag(viewHolder);
	} else {
		viewHolder = (SearchViewHolder) convertView.getTag();
	}
	viewHolder.textView.setText(title);
	return convertView;
}
	
private void asyncLoadImage(Context context,final ImageView imageView,String url){
	final AsyncImageLoader asyncImageLoader=new AsyncImageLoader(context);
	final ImageCallBack imageCallBack=new ImageCallBack(){
		@Override
		public void loadImage(Bitmap d) {
			if(d!=null){
				iconBitmap=d;
				imageView.setImageBitmap(iconBitmap);
				}
		}
		
	};
	asyncImageLoader.loadDrawable(url, imageCallBack);
}

public void visit(BaseActivity baseActivity){
	BaseFragment fragment=new PreferentialInfoDetailFragment(this);
	fragment.startFragment(baseActivity, R.id.fragmentContainer);
}


class ViewHolder {
	ImageView imageview;
}

class SearchViewHolder {
	TextView textView;
}

public int getNeedLogin() {
	return needLogin;
}
public void setNeedLogin(int needLogin) {
	this.needLogin = needLogin;
}
}
