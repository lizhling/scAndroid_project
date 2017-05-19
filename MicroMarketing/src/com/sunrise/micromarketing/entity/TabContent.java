package com.sunrise.micromarketing.entity;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.sunrise.javascript.utils.FileUtils;
import com.sunrise.micromarketing.ExtraKeyConstant;
import com.sunrise.micromarketing.R;
import com.sunrise.micromarketing.ui.activity.BusinessActivity;
import com.sunrise.micromarketing.ui.activity.WebViewActivity;
import com.sunrise.micromarketing.utils.AsyncImageLoader;
import com.sunrise.micromarketing.utils.AsyncImageLoader.ImageCallBack;

public class TabContent {
	private int type = 0;// 0、web页面,1、本地
	private String content;// 内容
	private String zipContent;// 压缩内容
	private String iconNormalPath;
	private String iconFocusPath;
	private String iconOnclickPath;
	private String tabName;
	private String lastModify;

	private final static Class<?>[] sTabActivitys = { WebViewActivity.class, BusinessActivity.class };

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

	public Intent getIntent(Context packageContext) {
		Intent intent = new Intent(packageContext, sTabActivitys[type]);

		if (type == 0) {
			intent.putExtra(Intent.EXTRA_TEXT, getZipContent());
			intent.putExtra(ExtraKeyConstant.KEY_LAST_MODIFY, getLastModify());
			intent.putExtra(Intent.EXTRA_LOCAL_ONLY, false);
			intent.putExtra(Intent.EXTRA_TITLE, getTabName());
		}
		intent.putExtra(Intent.EXTRA_TITLE, tabName);
		return intent;
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
