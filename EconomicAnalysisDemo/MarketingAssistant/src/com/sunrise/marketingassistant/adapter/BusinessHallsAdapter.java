package com.sunrise.marketingassistant.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.cache.preference.Preferences;
import com.sunrise.marketingassistant.entity.MobileBusinessHall;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.GetMobPictureTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BusinessHallsAdapter extends BaseAdapter implements TaskListener {
	private ArrayList<MobileBusinessHall> mMobileBusinessHalls;
	private Context mContext;
	private OnClickListener mViewMapListener;
	private ArrayList<GenericTask> mArrayTask;
	private String mSubContent;
	private Toast mToast;

	private HashMap<String, Bitmap> mHashIcon;

	private final static int[] sResIds_starUser = { R.drawable.ic_star_1, R.drawable.ic_star_2, R.drawable.ic_star_3, R.drawable.ic_star_4,
			R.drawable.ic_star_5, R.drawable.ic_star_6, R.drawable.ic_star_7 };

	public BusinessHallsAdapter(ArrayList<MobileBusinessHall> mobileBusinessHalls, Context context) {
		super();
		this.mMobileBusinessHalls = mobileBusinessHalls;
		this.mContext = context;
		this.mSubContent = Preferences.getInstance(context).getSubAccount();
		mArrayTask = new ArrayList<GenericTask>();
	}

	@Override
	public int getCount() {
		if (mMobileBusinessHalls == null)
			return 0;
		return mMobileBusinessHalls.size();
	}

	@Override
	public Object getItem(int position) {
		if (mMobileBusinessHalls == null)
			return null;
		return mMobileBusinessHalls.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		if (convertView == null)
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_hall_info, null);
		convertView.setFocusable(false);

		MobileBusinessHall hall = mMobileBusinessHalls.get(position);

		ImageView imageview = (ImageView) convertView.findViewById(R.id.image_hall);
		doGetBitmap(imageview, hall.getIMG_INFO_FIRST());// hall.getIMG_INFO());

		TextView phone_number = (TextView) convertView.findViewById(R.id.phone_number);
		if (TextUtils.isEmpty(hall.getGRADE_NAME()))
			phone_number.setVisibility(View.GONE);
		else {
			phone_number.setText(hall.getGRADE_NAME());
			phone_number.setVisibility(View.VISIBLE);
		}

		TextView hall_address = (TextView) convertView.findViewById(R.id.hall_address);
		if (hall.getLATITUDE() == 0 || hall.getLONGITUDE() == 0)
			hall_address.setVisibility(View.GONE);
		else {
			hall_address.setVisibility(View.VISIBLE);
			hall_address.setText(hall.getLATITUDE() + "," + hall.getLONGITUDE());
			hall_address.setTag(position);
			hall_address.setOnClickListener(mViewMapListener);
		}

		TextView hall_name = (TextView) convertView.findViewById(R.id.hall_name);
		hall_name.setText(hall.getGROUP_NAME());

		return convertView;
	}

	public void setViewMapClickListener(OnClickListener viewMapClickListener) {
		this.mViewMapListener = viewMapClickListener;
	}

	private void doGetBitmap(ImageView imageview, String iconurl) {
		String oldIconUrl = (String) imageview.getTag();
		if (iconurl == null) {
			imageview.setImageResource(R.drawable.hall_icon);
			return;
		}

		if (iconurl.equals(oldIconUrl)) {
			return;
		}

		Bitmap bitmap = getCacheImage(iconurl);
		if (bitmap != null) {
			imageview.setImageBitmap(bitmap);
			return;
		}

		imageview.setTag(iconurl);

		// 取消旧的下载
		for (int i = 0; i < mArrayTask.size(); i++) {
			GetMobPictureTask task = (GetMobPictureTask) mArrayTask.get(i);
			if (task.mView == imageview)
				task.cancle();
		}

		new GetMobPictureTask().execute(imageview, mSubContent, iconurl, this);
	}

	private void showToast(String content) {
		if (mToast == null) {
			mToast = new Toast(mContext);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		if (content == null)
			return;
		mToast.setText(content);
		mToast.show();
	}

	private Bitmap getCacheImage(String iconUrl) {
		if (mHashIcon == null)
			return null;

		return mHashIcon.get(iconUrl);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {
		mArrayTask.add(task);
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		mArrayTask.remove(task);
		if (result != TaskResult.OK)
			showToast(task.getException().toString());
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		Bitmap bitmap = (Bitmap) param;
		if (bitmap != null) {

			GetMobPictureTask t = (GetMobPictureTask) task;

			ImageView imageview = (ImageView) t.mView;
			imageview.setImageBitmap(bitmap);

			if (mHashIcon == null)
				mHashIcon = new HashMap<String, Bitmap>();
			mHashIcon.put(t.mIconInfo, bitmap);
		}
	}

	@Override
	public void onCancelled(GenericTask task) {
		mArrayTask.remove(task);
	}
}
