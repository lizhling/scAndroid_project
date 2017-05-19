package com.sunrise.scmbhc.ui.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.adapter.ShareAppAdatper;
import com.sunrise.scmbhc.ui.activity.BaseActivity;
import com.sunrise.scmbhc.ui.activity.GetContactsActivity;
import com.sunrise.scmbhc.ui.view.ClickAnimationListner;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.DesCrypUtil;
import com.sunrise.scmbhc.utils.LogUtlis;
import com.sunrise.scmbhc.utils.UnitUtils;

public class FriendsShareFragment extends BaseFragment implements OnClickListener, OnItemClickListener {

	private EditText mEditFriendMobile;
	private GridView mGridOtherWay;
	private ShareAppAdatper mShareAppAdatper;
	private ImageView mImage2DCode;// 二维码
	private ImageView mBtAddressList;// 通讯录

	private PopupWindow mPopupWindow_help;
	private TextView mViewTwoDimensCodeHelp;

	/**
	 * 校园直销队模式
	 */
	private boolean isSchoolType;

	private TextWatcher mPhoneNunberWacher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			LogUtlis.showLogD("onTextChanged", arg0 + " , " + arg1 + " ," + arg2 + "," + arg3);
			boolean isTextEmpty = arg1 + arg3 == 0;
			if (isTextEmpty)
				mBtAddressList.setImageResource(R.drawable.ic_contacts);
			else
				mBtAddressList.setImageResource(android.R.drawable.presence_offline);

			if (arg1 + arg2 == 11 && arg2 > 0) {
				mImage2DCode.setImageResource(R.drawable.friends_share);
				isSchoolType = false;
			}
		}

	};

	public View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_more_friends_share, container, false);

		view.findViewById(R.id.send).setOnClickListener(this);

		mBtAddressList = (ImageView) view.findViewById(R.id.button_addressList);
		mBtAddressList.setOnClickListener(this);

		// view.findViewById(R.id.send).requestFocus();
		mEditFriendMobile = (EditText) view.findViewById(R.id.editText_friend_mobile);
		mEditFriendMobile.addTextChangedListener(mPhoneNunberWacher);

		mGridOtherWay = (GridView) view.findViewById(R.id.gridView_share_by_other_way);
		mShareAppAdatper = new ShareAppAdatper(mBaseActivity, getShareApps(mBaseActivity));
		mGridOtherWay.setAdapter(mShareAppAdatper);
		mGridOtherWay.setOnItemClickListener(this);

		mImage2DCode = (ImageView) view.findViewById(R.id.imageView_twodimensioncode);
		mImage2DCode.setOnClickListener(this);

		mViewTwoDimensCodeHelp = (TextView) view.findViewById(R.id.twoDimensCodeHelp);
		mViewTwoDimensCodeHelp.setOnClickListener(this);
		mViewTwoDimensCodeHelp.getPaint().setUnderlineText(true);

		return view;

	}

	public void onStart() {
		super.onStart();
		BaseActivity baseActivity = (BaseActivity) getActivity();
		baseActivity.setLeftButtonVisibility(View.VISIBLE);
		baseActivity.setTitle(getString(R.string.friendsShare));

		if (!UserInfoControler.getInstance().checkUserLoginIn())
			mBaseActivity.setRightButton(getString(R.string.login), mLoginClickListener);
		else
			mBaseActivity.setRightButtonVisibility(View.GONE);

		File file = new File("file:///android_asset/drawable/corner.xml");
		LogUtlis.showLogD("TEST",
				"file = " + file.isFile() + " ," + file.isDirectory() + " ," + file.getAbsolutePath() + "," + file.getName() + "," + file.length());

		file = new File("file:///android_asset/systemnotice.json");
		LogUtlis.showLogD("TEST",
				"file = " + file.isFile() + " ," + file.isDirectory() + " ," + file.getAbsolutePath() + "," + file.getName() + "," + file.length());

	}

	public void onStop() {
		super.onStop();
		mBaseActivity.setRightButtonVisibility(View.GONE);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == BaseActivity.RESULT_OK) {
			String str = data.getStringExtra(ExtraKeyConstant.KEY_PHONE_NUMBER);
			if (!TextUtils.isEmpty(str))
				mEditFriendMobile.setText(str);
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.send:
			sendShareToFriendByMsg();
			break;
		case R.id.button_addressList:
			new ClickAnimationListner(arg0, new OnClickListener() {
				public void onClick(View arg0) {
					if (TextUtils.isEmpty(mEditFriendMobile.getText().toString()))
						startActivityForResult(new Intent(mBaseActivity, GetContactsActivity.class), GetContactsActivity.REQUEST_CONTACT);
					else
						mEditFriendMobile.setText(null);
				}
			}, R.anim.click_scale).startAnim();
			break;
		case R.id.imageView_twodimensioncode:
			if (!UserInfoControler.getInstance().checkUserLoginIn()) {
				showToast(getString(R.string.unlogin_notice));
				return;
			}
			createNew2DCodeImage();
			break;
		case R.id.twoDimensCodeHelp:
			new ClickAnimationListner(arg0, new OnClickListener() {
				public void onClick(View arg0) {
					showTwoDimensCodeHelp();
				}
			}, R.anim.click_scale).startAnim();
			break;
		default:
			break;
		}
	}

	private void showTwoDimensCodeHelp() {
		if (mPopupWindow_help == null) {
			TextView view = new TextView(mBaseActivity);
			view.setTextColor(Color.WHITE);// 设置颜色

			// 输入写卡内容
			view.setText(Html.fromHtml(getString(R.string.towDimensCodeHelp)));
			int textsize = UnitUtils.px2dip(mBaseActivity, getResources().getDimension(R.dimen.text_size_2));

			view.setTextSize(textsize);
			view.setBackgroundResource(android.R.drawable.toast_frame);
			mPopupWindow_help = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
			mPopupWindow_help.setBackgroundDrawable(new ColorDrawable());
			mPopupWindow_help.setOutsideTouchable(false);
		}
		mPopupWindow_help.showAsDropDown(mViewTwoDimensCodeHelp);
	}

	private void createNew2DCodeImage() {
		String phoneNum = mEditFriendMobile.getText().toString();
		if (!CommUtil.isMobilePhone(phoneNum)) {
			// mEditFriendMobile.requestFocus();
			showToast(getString(R.string.inputCorrectPhoneNumber));
			return;
		}

		String url = getShareContentWithFromAndTo(phoneNum);
		LogUtlis.showLogD("shareUrl", "url = " + url);

		Bitmap bitmap = createQRImage(url, mImage2DCode.getDrawable().getIntrinsicWidth(), mImage2DCode.getDrawable().getIntrinsicHeight());
		if (bitmap != null) {
			// 显示到一个ImageView上面
			mImage2DCode.setImageBitmap(bitmap);
			showToast(getString(R.string.hasCreateNew2DimensCodeImage));
			isSchoolType = true;
		} else {
			mImage2DCode.setImageResource(R.drawable.friends_share);
			showToast(getString(R.string.failedToCreateNew2DimensCodeImage));
			isSchoolType = false;
		}
	}

	private String getShareContentWithFromAndTo(String phoneNum) {
		String url = App.sServerClient.getRecommondDownloadUrl();
		try {
			url += DesCrypUtil.DESEncrypt(UserInfoControler.getInstance().getUserName() + "," + phoneNum);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getString(R.string.schoolSaleWord) + url;
	}

	private void sendShareToFriendByMsg() {
		String phoneNum = mEditFriendMobile.getText().toString();
		if (!CommUtil.isMobilePhone(phoneNum)) {
			// mEditFriendMobile.requestFocus();
			showToast(getString(R.string.inputCorrectPhoneNumber));
			return;
		}

		Uri uri = Uri.parse("smsto:" + phoneNum);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		it.putExtra("sms_body", getShareContent());
		startActivity(it);
	}

	private List<ResolveInfo> getShareApps(Context context) {
		List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();
		Intent intent = new Intent(Intent.ACTION_SEND, null);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
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
		ResolveInfo appInfo = (ResolveInfo) mShareAppAdatper.getItem(position);
		shareIntent.setComponent(new ComponentName(appInfo.activityInfo.packageName, appInfo.activityInfo.name));
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, getShareContent());
		shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(shareIntent);
	}

	private String getShareContent() {
		if (isSchoolType)
			return getShareContentWithFromAndTo(mEditFriendMobile.getText().toString());

		if (UserInfoControler.getInstance().checkUserLoginIn())
			return String.format(getResources().getString(R.string.shareContentLogined), UserInfoControler.getInstance().getUserName());
		else
			return getResources().getString(R.string.shareContent);
	}

	// 要转换的地址或字符串,可以是中文
	private Bitmap createQRImage(String url, final int QR_WIDTH, final int QR_HEIGHT) {
		try {
			// 判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1) {
				return null;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);

			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Toast mToast;

	void showToast(CharSequence str) {
		if (mToast == null)
			mToast = Toast.makeText(mBaseActivity, str, Toast.LENGTH_LONG);
		else
			mToast.setText(str);

		mToast.show();
	}

	/*
	 * 功能: 为用户行为提供页面名称
	 * 
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.FriendsShareFragment;
	}

}
