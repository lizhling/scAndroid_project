package com.sunrise.micromarketing.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.sunrise.javascript.utils.CommonUtils;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.micromarketing.ExtraKeyConstant;
import com.sunrise.micromarketing.R;
import com.sunrise.micromarketing.entity.UpdateInfo;
import com.sunrise.micromarketing.ui.adapter.ShareAppAdatper;
import com.sunrise.micromarketing.utils.CommUtil;
import com.sunrise.micromarketing.utils.LogUtlis;

public class FriendsShareActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener, ExtraKeyConstant {

	private static final int REQUEST_CONTACT = 109;

	private EditText mEditFriendMobile;
	private GridView mGridStringWay;
	private GridView mGridImageWay;
	private ImageView mImage2DCode;// 二维码
	private ImageView mBtAddressList;// 通讯录

	private ViewSwitcher mViewSwitcher;
	private RadioGroup mTabWidget;
	private String url;
	private String type;
	private String bussinessName;

	private TextWatcher mPhoneNunberWacher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			LogUtlis.d("onTextChanged", arg0 + " , " + arg1 + " ," + arg2 + "," + arg3);
			boolean isTextEmpty = arg1 + arg3 == 0;
			if (isTextEmpty)
				mBtAddressList.setImageResource(R.drawable.ic_contacts);
			else
				mBtAddressList.setImageResource(android.R.drawable.presence_offline);
			
			

			// if (arg1 + arg2 == 11 && arg2 > 0) {
			// mImage2DCode.setImageResource(R.drawable.icon_ssjf_share);
			// }
		}

	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_more_friends_share);

		findViewById(R.id.send).setOnClickListener(this);

		mBtAddressList = (ImageView) findViewById(R.id.button_addressList);
		mBtAddressList.setOnClickListener(this);

		// view.findViewById(R.id.send).requestFocus();
		mEditFriendMobile = (EditText) findViewById(R.id.editText_friend_mobile);
		mEditFriendMobile.addTextChangedListener(mPhoneNunberWacher);
		
		

		bussinessName = getIntent().getStringExtra("businessName");
		type = getIntent().getStringExtra("type");
		mGridStringWay = (GridView) findViewById(R.id.gridView_share_by_string);
		{
			mGridStringWay.setEmptyView(findViewById(R.id.none_gridview1));
			mGridStringWay.setAdapter(new ShareAppAdatper(this, getShareApps(this, false)));
			mGridStringWay.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
					Intent shareIntent = new Intent(Intent.ACTION_SEND);
					ResolveInfo appInfo = (ResolveInfo) arg0.getAdapter().getItem(position);
					shareIntent.setComponent(new ComponentName(appInfo.activityInfo.packageName, appInfo.activityInfo.name));
					shareIntent.setType("text/plain");
					if (type == null) {
						shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareContent) + "\n" + getShareContent());
						// url = getString(R.string.shareContent) + "\n" + url;
					} else {
						shareIntent.putExtra(Intent.EXTRA_TEXT, "分享业务：" + bussinessName + "\n" + "分享链接：" + getShareContent());
					}

					shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(shareIntent);
				}
			});
		}

		mGridImageWay = (GridView) findViewById(R.id.gridView_share_by_image);
		{
			mGridImageWay.setEmptyView(findViewById(R.id.none_gridview2));
			mGridImageWay.setAdapter(new ShareAppAdatper(this, getShareApps(this, true)));
			mGridImageWay.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
					Intent shareIntent = new Intent(Intent.ACTION_SEND);
					ResolveInfo appInfo = (ResolveInfo) arg0.getAdapter().getItem(position);
					shareIntent.setComponent(new ComponentName(appInfo.activityInfo.packageName, appInfo.activityInfo.name));
					shareIntent.setType("image/*");
					if (type == null) {
						shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareContent) + "\n" + getShareContent());
						// url = getString(R.string.shareContent) + "\n" + url;
					} else {
						shareIntent.putExtra(Intent.EXTRA_TEXT, "分享业务：" + bussinessName + "\n" + "分享链接：" + getShareContent());
					}
					shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri());
					shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(shareIntent);
				}
			});
		}

		mImage2DCode = (ImageView) findViewById(R.id.imageView_twodimensioncode);
		mImage2DCode.setOnClickListener(this);
		mImage2DCode.setImageBitmap(CommUtil.createQRCode(getShareContent(), BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher)));

		mTabWidget = (RadioGroup) findViewById(R.id.tabs);
		mTabWidget.setOnCheckedChangeListener(this);

		mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher01);

		String businesstitle = getIntent().getStringExtra(Intent.EXTRA_TEMPLATE);
		if (!TextUtils.isEmpty(businesstitle)) {
			TextView textShareTitle = (TextView) findViewById(R.id.share_description);
			textShareTitle.setText(businesstitle);
			textShareTitle.setVisibility(View.VISIBLE);
		}

	}

	public void onStart() {
		super.onStart();

		String title = getIntent().getStringExtra(Intent.EXTRA_TITLE);
		if (TextUtils.isEmpty(title))
			title = "好友分享";
		setTitle(title);

		setTitleBarLeftClick(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		setTitleBarRightClick(null);

	}

	public void onResume() {
		super.onResume();
	}

	public void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.send:
			sendShareToFriendByMsg();
			break;
		case R.id.button_addressList:
			if (TextUtils.isEmpty(mEditFriendMobile.getText().toString())) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_PICK);
				intent.setData(ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, REQUEST_CONTACT);
			} else
				mEditFriendMobile.setText(null);
			break;
		case R.id.imageView_twodimensioncode:
			createNew2DCodeImage();
			break;
		default:
			break;
		}
	}

	private void createNew2DCodeImage() {
		String phoneNum = mEditFriendMobile.getText().toString();
		if (!CommonUtils.isPhoneNumber(phoneNum)) {
			// mEditFriendMobile.requestFocus();
			showToast(getString(R.string.inputCorrectMobileNumber));
			return;
		}

		String url = getShareContent();
		LogUtlis.d("shareUrl", "url = " + url);

		Bitmap bitmap = CommUtil.createQRCode(getShareContent(), BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		if (bitmap != null) {
			// 显示到一个ImageView上面
			mImage2DCode.setImageBitmap(bitmap);
			showToast("新二维码生成");
		} else {
			mImage2DCode.setImageResource(R.drawable.icon_ssjf_share);
			showToast("二维码生成失败");
		}
	}

	private void sendShareToFriendByMsg() {
		String phoneNum = mEditFriendMobile.getText().toString();
		if (!CommonUtils.isPhoneNumber(phoneNum)) {
			// mEditFriendMobile.requestFocus();
			showToast(getString(R.string.inputCorrectMobileNumber));
			return;
		}
		type = getIntent().getStringExtra("type");
		Uri uri = Uri.parse("smsto:" + phoneNum);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		if (type == null) {
			it.putExtra("sms_body", getString(R.string.shareContent) + "\n" + getShareContent());
			// url = getString(R.string.shareContent) + "\n" + url;
		} else {
			it.putExtra("sms_body", "分享业务：" + bussinessName + "\n" + "分享链接：" + getShareContent());
		}

		startActivity(it);
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

	private String getShareContent() {
		url = getIntent().getStringExtra(Intent.EXTRA_TEXT);
		if (url == null) {
			url = getPreferences().getString(ExtraKeyConstant.APP_DOWNLOAD_INFO);
			if (url == null)
				url = getString(R.string.defaultDownloadUrl);
			else
				url = JsonUtils.parseJsonStrToObject(url, UpdateInfo.class).getDownloadUrl();
		}

		return url;
	}

	private Uri getImageUri() {
		Bitmap bitmap = CommUtil.createQRCode(getShareContent(), BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
		return uri;
	}

	private Toast mToast;

	void showToast(CharSequence str) {
		if (mToast == null)
			mToast = Toast.makeText(this, str, Toast.LENGTH_LONG);
		else
			mToast.setText(str);

		mToast.show();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			setResult(resultCode);
			return;
		}

		switch (requestCode) {
		case REQUEST_CONTACT:
			if (data == null) {
				return;
			}
			Uri result = data.getData();
			String contactId = result.getLastPathSegment();

			// 显示
			ContentResolver resolver = getContentResolver();
			String[] PHONES_PROJECTION = new String[] { Phone.DISPLAY_NAME, Phone.NUMBER };
			// 获取手机联系人
			Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, Phone.CONTACT_ID + " = ?", new String[] { contactId }, null);

			if (phoneCursor != null && phoneCursor.moveToFirst()) {
				// 得到手机号码
				String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER));
				phoneNumber = CommonUtils.removeNoNecessaryWordsFromPhoneNumber(phoneNumber);
				phoneCursor.close();
				mEditFriendMobile.setText(phoneNumber);
			}

			break;
		default:
			setResult(resultCode);
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int id) {
		int index = arg0.indexOfChild(arg0.findViewById(id));
		mViewSwitcher.setDisplayedChild(index);
	}

}
