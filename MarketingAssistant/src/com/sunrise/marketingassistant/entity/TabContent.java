package com.sunrise.marketingassistant.entity;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.sunrise.javascript.utils.FileUtils;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.utils.AsyncImageLoader;
import com.sunrise.marketingassistant.utils.AsyncImageLoader.ImageCallBack;

public class TabContent {
	private int type = 0;// 0、web页面,1、本地地图,2、二维码扫描
	private int id;
	private String content;// 内容
	private String zipContent;// 压缩内容
	private String iconNormalPath;
	private String iconFocusPath;
	private String iconOnclickPath;
	private String tabName;
	private String lastModify;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getIconNormalPath() {
		return iconNormalPath;
	}

	public void setIconNormalPath(String iconNormalPath) {
		this.iconNormalPath = iconNormalPath;
	}

	public String getIconFocusPath() {
		return iconFocusPath;
	}

	public void setIconFocusPath(String iconFocusPath) {
		this.iconFocusPath = iconFocusPath;
	}

	public String getIconOnclickPath() {
		return iconOnclickPath;
	}

	public void setIconOnclickPath(String iconOnclickPath) {
		this.iconOnclickPath = iconOnclickPath;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	private static HashMap<String, Bitmap> hashmap = new HashMap<String, Bitmap>();

	public void setIconFocus2ImageView(ImageView imageView) {
		setIcon2Image(imageView, iconFocusPath);
	}

	public void setIconNormal2ImageView(ImageView imageView) {
		setIcon2Image(imageView, iconNormalPath);
	}

	public void setIconClick2ImageView(ImageView imageView) {
		setIcon2Image(imageView, iconOnclickPath);
	}

	private void setIcon2Image(ImageView imageView, String path) {
		if (imageView == null)
			return;

		else if (TextUtils.isEmpty(path)) {
			imageView.setImageResource(R.drawable.ic_launcher);
		}

		else if (path.startsWith("file:///android_asset/")) {
			if (!hashmap.containsKey(path)) {
				hashmap.put(path, FileUtils.getBitmapFromAssets(imageView.getContext(), path.substring("file:///android_asset/".length())));
			}
			imageView.setImageBitmap(hashmap.get(path));
		}

		else if (path.startsWith("http")) {
			new AsyncImageLoader(imageView.getContext()).loadDrawable(path, new TabContentCallback(imageView));
		}
	}

	public String getZipContent() {
		return zipContent;
	}

	public void setZipContent(String zipContent) {
		this.zipContent = zipContent;
	}

	public String getLastModify() {
		return lastModify;
	}

	public void setLastModify(String lastModify) {
		this.lastModify = lastModify;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	class TabContentCallback implements ImageCallBack {
		ImageView imageView;

		TabContentCallback(ImageView imageView) {
			this.imageView = imageView;
		}

		@Override
		public void loadImage(Bitmap d) {
			imageView.setImageBitmap(d);
		}
	}
}
