package com.starcpt.cmuc.ui.view;

import java.util.ArrayList;
import java.util.List;

import com.starcpt.cmuc.R;
import com.starcpt.cmuc.adapter.ShareAppAdatper;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class ShareDialog extends Dialog implements OnItemClickListener {

	private String mShareMessage;
	private Bitmap mBitmap;

	public ShareDialog(Context context, CharSequence title, String shareMessage, Bitmap bitmap) {
		super(context, R.style.dialog);
		// super(context);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_app_share);

		mShareMessage = shareMessage;
		mBitmap = bitmap;

		setCancelable(true);

		TextView viewTitle = (TextView) findViewById(R.id.tvTitle);
		viewTitle.setText(title);

		GridView view = (GridView) findViewById(R.id.gridView_share_by_image);
		view.setAdapter(new ShareAppAdatper(context, getShareApps(context, true)));
		view.setOnItemClickListener(this);
	}

	private List<ResolveInfo> getShareApps(Context context, boolean isBitmapType) {
		List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();
		Intent intent = new Intent(Intent.ACTION_SEND, null);
		intent.addCategory(Intent.CATEGORY_DEFAULT);

		if (isBitmapType)
			intent.setType("image/*");
		else
			intent.setType("text/plain");

		PackageManager pManager = context.getPackageManager();
		List<ResolveInfo> apps = pManager.queryIntentActivities(intent, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
		if (apps != null)
			mApps = apps;
		return mApps;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		ResolveInfo appInfo = (ResolveInfo) arg0.getAdapter().getItem(position);
		shareIntent.setComponent(new ComponentName(appInfo.activityInfo.packageName, appInfo.activityInfo.name));
		shareIntent.setType("image/*");
//		shareIntent.putExtra(Intent.EXTRA_TEXT, mShareMessage);
		shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri());
		shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getContext().startActivity(shareIntent);
		dismiss();
	}

	private Uri getImageUri() {
		Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContext().getContentResolver(), mBitmap, null, null));
		return uri;
	}
}
