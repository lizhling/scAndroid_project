package com.sunrise.micromarketing.adapter;

import java.util.List;

import com.sunrise.micromarketing.R;
import com.sunrise.micromarketing.entity.BusinessMenu;
import com.sunrise.micromarketing.utils.AsyncImageLoader;
import com.sunrise.micromarketing.utils.AsyncImageLoader.ImageCallBack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * 
 * @author fuheng
 * 
 */
public class BusinessListAdapter extends BaseAdapter {

	private Context mContext;
	private List<BusinessMenu> mList;
	private int Currentposition = -1;

	public void setCurrentposition(int currentposition) {
		Currentposition = currentposition;
	}

	public BusinessListAdapter(Context context, List<BusinessMenu> list) {
		mContext = context;
		mList = list;
	}

	@Override
	public int getCount() {
		if (mList != null)
			return mList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {

		if (mList != null)
			return mList.get(position);

		return null;
	}

	@Override
	public long getItemId(int position) {
		if (mList != null)
			return mList.get(position).getId();
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup container) {
		View viewgroup = LayoutInflater.from(getContext()).inflate(R.layout.item_business_list, null);

		TextView title = (TextView) viewgroup.findViewById(R.id.title);
		title.setText(mList.get(position).getName());
		if (position == Currentposition) {
			title.setTextColor(0xff0085D2);
		} else {
			title.setTextColor(0xff484848);
		}
		TextView discribe = (TextView) viewgroup.findViewById(R.id.discribe);
		ImageView icon = (ImageView) viewgroup.findViewById(R.id.icon1);
		discribe.setText(mList.get(position).getDescription());

		loadBitmapForImageView(mContext, icon, mList.get(position));

		return viewgroup;
	}

	public Context getContext() {
		return mContext;
	}

	protected void loadBitmapForImageView(Context context, final ImageView imageView, final BusinessMenu menu) {
		final AsyncImageLoader asyncImageLoader = new AsyncImageLoader(context);
		asyncImageLoader.loadDrawable(menu.getIcon(), new ImageCallBackObject(imageView, menu));
	}

	class ImageCallBackObject implements ImageCallBack {

		ImageView imageView;
		BusinessMenu menu;

		public ImageCallBackObject(ImageView imageView, BusinessMenu menu) {
			this.imageView = imageView;
			this.menu = menu;
		}

		@Override
		public void loadImage(Bitmap d) {
			if (d != null) {
				Drawable drawable = imageView.getDrawable();

				menu.setIconBitmap(d);
				if (drawable == null)
					imageView.setImageBitmap(d);
				else {
					Drawable newDrawable = new BitmapDrawable(d);
					newDrawable.setBounds(drawable.copyBounds());
					imageView.setImageDrawable(newDrawable);
				}
			}

		}

	}
}
