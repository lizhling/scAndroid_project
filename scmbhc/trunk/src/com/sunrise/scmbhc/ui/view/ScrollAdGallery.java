package com.sunrise.scmbhc.ui.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.starcpt.analytics.PhoneClickAgent;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.adapter.PreferentialInfoAdapter;
import com.sunrise.scmbhc.entity.PreferentialInfo;
import com.sunrise.scmbhc.ui.activity.BaseActivity;
import com.sunrise.scmbhc.utils.Base64;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.HardwareUtils;
import com.sunrise.scmbhc.utils.LogUtlis;
import com.sunrise.scmbhc.utils.UnitUtils;
import com.sunrise.scmbhc.utils.ZipAndBaseUtils;

public class ScrollAdGallery {
	private AdGallery mAdGallery;
	private RadioGroup mRadioGroup;
	private RelativeLayout mGalleryLayout;
	private PreferentialInfoAdapter mPreferentialAdapter;
	private ArrayList<PreferentialInfo> mPreferentialInfos;
	private static final String PHOMENUMBER = "PHONE_NO";
	private static final String BUSINESSID = "activityId";
	private static final String IMEI = "imei";
	private static final String IMSI = "clientPhoneImsi";
	private static final String IMSI2 = "clientPhoneImsi2";
	private static final String APPVERSION = "app_version";
	private Context mContext;

	public ScrollAdGallery(Context context, ArrayList<PreferentialInfo> preferentialInfos) {
		this.mContext = context;
		this.mPreferentialInfos = preferentialInfos;
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		mGalleryLayout = (RelativeLayout) layoutInflater.inflate(R.layout.adgallery_hellper, null);
		mRadioGroup = (RadioGroup) mGalleryLayout.findViewById(R.id.home_pop_gallery_mark);
		refreshGallerMark(mContext, preferentialInfos);
		initAdGallery(mContext, preferentialInfos);
	}

	private void initAdGallery(Context context, ArrayList<PreferentialInfo> preferentialInfos) {
		mAdGallery = (AdGallery) mGalleryLayout.findViewById(R.id.gallerypop);
		mPreferentialAdapter = new PreferentialInfoAdapter(context, preferentialInfos);
		mAdGallery.setAdapter(mPreferentialAdapter);
		mAdGallery.setImageNumber(preferentialInfos.size());
		mAdGallery.setSwitchTime(5000);
		mAdGallery.setGallerySwitchListener(new OnGallerySwitchListener() {
			@Override
			public void onGallerySwitch(int position) {
				if (mRadioGroup != null) {
					mRadioGroup.check(mRadioGroup.getChildAt(position).getId());
				}
			}
		});
		mAdGallery.setGalleryClickListener(new OnGalleryClickListener() {

			@Override
			public void onGalleryClick(int position) {
				PreferentialInfo preferentialInfo = mPreferentialInfos.get(position);
				String url = preferentialInfo.getDetailsUrl();
				// 判断需要是否登录
				if (PreferentialInfo.NEEDLOGIN == preferentialInfo.getNeedLogin()) {
					// 判断是否已经登录
					if (((BaseActivity) mContext).checkLoginIn(null)) {// +UserInfoControler.getInstance().getUserName()
						String paramInfo = PHOMENUMBER + "=" + UserInfoControler.getInstance().getUserName() + "&routePhoneNumber="
								+ UserInfoControler.getInstance().getUserName() + "&SERVICE_NO=" + UserInfoControler.getInstance().getUserName() + "&"
								+ BUSINESSID + "=" + preferentialInfo.getId() + "&" + IMEI + "=" + HardwareUtils.getPhoneIMEI(mContext) + "&" + IMSI + "="
								+ HardwareUtils.getPhoneIMSI(mContext) + "&token=" + UserInfoControler.getInstance().getAuthorKey() + "&IP="
								+ HardwareUtils.getLocalIpAddress();
						{// 增加双卡判断
							String imsi2 = HardwareUtils.getPhoneIMSI2(mContext);
							if (imsi2 != null)
								paramInfo += "&" + IMSI2 + "=" + imsi2;
						}
						{// 增加客户端版本号
							paramInfo += "&" + APPVERSION + '=' + App.sAPKVersionCode;
						}

						String location = HardwareUtils.getLocation(mContext);
						if (location != null) {
							paramInfo += '&' + location;
						}
						// url = url.replaceAll("8092", "8094");
						// url =
						// "http://183.221.33.188:8094/scmbhi/jmyl/index.html";
						LogUtlis.showLogI("优惠活动", "优惠活动URl加密前:" + url + "?" + paramInfo);
						paramInfo = ZipAndBaseUtils.compressAndEncodeBase64(paramInfo, Base64.PREFERRED_ENCODING);

						if (url.indexOf('?') == -1)
							url = url + "?" + paramInfo;
						else
							url += '&' + paramInfo;
						
						LogUtlis.showLogI("优惠活动", "优惠活动URl" + url);
					} else {
						return;
					}
				}
				LogUtlis.showLogI("优惠活动", "业务id" + preferentialInfo.getId());
				if (url != null && !TextUtils.isEmpty(url)) {
					// 在应用内打开
					if (url.indexOf('?') < 0) {
						url = url + '?' + ZipAndBaseUtils.compressAndEncodeBase64("param=" + System.currentTimeMillis(), Base64.PREFERRED_ENCODING);
					}
					PhoneClickAgent.onPageStart("preInfo" + preferentialInfo.getId());
					PhoneClickAgent.onPageEnd(mContext, "preInfo" + preferentialInfo.getId());
					CommUtil.visitWebView(mContext, url, "优惠活动");
				}
			}
		});

		mAdGallery.setSelection(mAdGallery.getCount() / 2);
	}

	/**
	 * 功能： 实现自动切换布局的初始化（页面底部水平的切换圆点）
	 * 
	 * @param context
	 *            上下文
	 * @param preferentialInfos
	 *            滚动广告list
	 */
	private void refreshGallerMark(Context context, ArrayList<PreferentialInfo> preferentialInfos) {
		mRadioGroup.removeAllViews();
		Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.point_1);
		LayoutParams params = new LayoutParams(UnitUtils.dip2px(context, b.getWidth()), UnitUtils.dip2px(context, b.getHeight()));
		int size = preferentialInfos.size();
		for (int i = 0; i < size; i++) {
			RadioButton _rb = new RadioButton(context);
			_rb.setId(0x1234 + i);
			_rb.setButtonDrawable(context.getResources().getDrawable(R.drawable.gallery_selector));
			mRadioGroup.addView(_rb, params);
		}
	}

	/**
	 * 向外开放布局对象，使得可以将布局对象添加到外部的布局中去
	 * 
	 * @return
	 */
	public RelativeLayout getLayout() {
		return mGalleryLayout;
	}

	/**
	 * 开始自动循环切换
	 */
	public void startAutoSwitch() {
		mAdGallery.setRunFlag(true);
		mAdGallery.startAutoScroll();
	}

	public void stopAutoSwitch() {
		mAdGallery.setRunFlag(true);
	}

	/**
	 * 图片切换回调接口
	 * 
	 * @author wly
	 * 
	 */
	interface OnGallerySwitchListener {
		public void onGallerySwitch(int position);
	}

	interface OnGalleryClickListener {
		public void onGalleryClick(int position);
	}

	public void refreshGaller() {
		refreshGallerMark(mContext, mPreferentialInfos);
		mPreferentialAdapter.notifyDataSetChanged();
		mAdGallery.setImageNumber(mPreferentialInfos.size());
		mAdGallery.setSelection(mAdGallery.getCount() / 2);
	}
}
