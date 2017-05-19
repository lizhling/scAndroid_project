package com.sunrise.scmbhc.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.ui.activity.BaseActivity;
import com.sunrise.scmbhc.ui.activity.LocationOverlayActivity;
import com.sunrise.scmbhc.ui.activity.MipcaActivityCapture;
import com.sunrise.scmbhc.ui.activity.SingleFragmentActivity;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.coding.Base64Coding;

public class MoreFragment extends BaseFragment implements OnItemClickListener {

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// BaseActivity baseActivity = (BaseActivity) getActivity();
		// BaseFragment fragment = null;

		Class<?> cls = null;
		Bundle bundle = null;

		if (arg0.getId() == R.id.listView_commFunction) {
			switch (arg2) {
			case 0:
				cls = TopUpHistoryFragment.class;
				break;
			case 1:
				mBaseActivity.startActivity(new Intent(mBaseActivity, LocationOverlayActivity.class));
				break;
			case 2:
				cls = TrafficNotificationFragment.class;
				break;
			case 3: // 使用帮助
				cls = ManualFragment.class;
				bundle = new Bundle();
				bundle.putString("function", ManualFragment.HELP_FUN);
				break;
			default:
				break;
			}
		} else {
			switch (arg2) {
			case 0: // 系统公告
				// fragment = new ManualFragment();
				// Bundle bundle = new Bundle();
				// bundle.putString("function", ManualFragment.NOTICE_FUN);
				// fragment.setArguments(bundle);
				cls = ManualFragment.class;
				bundle = new Bundle();
				bundle.putString("function", ManualFragment.NOTICE_FUN);
				break;
			case 1:
				// cls = FriendsShareFragment.class;
				// break;
				Intent intent = new Intent(mBaseActivity, MipcaActivityCapture.class);
				startActivity(intent);
				return;
			case 2:
				cls = AboutFragment.class;
				break;
			case 3:
				cls = MessageListFragment.class; // 消息推送
				break;
			case 4:
				cls = ChangePasswordFragment.class;// 修改密码
				break;
			case 5:
				if (mBaseActivity.checkLoginIn(null)) {
					cls = WebViewFragment.class;
					bundle = new Bundle();
					BusinessMenu businessMenu = new BusinessMenu();
					String url = "http://223.87.10.254/photoprint/photoprint.do?" + new Base64Coding().encode(UserInfoControler.getInstance().getUserName());

					businessMenu.setServiceUrl(url);
					businessMenu.setName("照片上传");
					bundle.putParcelable(ExtraKeyConstant.KEY_BUSINESS_INFO, businessMenu);
				} else {
					Toast.makeText(mBaseActivity, R.string.unlogin_notice, Toast.LENGTH_SHORT).show();
				}
				break;
			case 6:
			// if (mBaseActivity.checkLoginIn(null)) {
			// cls = WebViewFragment.class;
			// bundle = new Bundle();
			// BusinessMenu businessMenu = new BusinessMenu();
			//
			// String url
			// ="http://183.221.33.188:8092/scmbhi/edu30onsale50/html/edu_half_onsale.html";
			// //"http://wx.scmccboss.com:83/platformMobile/gateway/executor/goPage.do?executorName=goTo&from=appmm&redirect=assets/html/hejiahuan/yhjhwenxintishi.html";
			// //
			// "http://wx.scmccboss.com:83/platformMobile/gateway/executor/goPage.do?executorName=goTo&from=appmm&redirect=assets/html/edu_wlan5sale/edu_wlan.html";
			// //
			// "http://223.87.19.91:83/platformMobile/gateway/executor/goPage.do?executorName=goTo&from=appmm&redirect=assets/html/business.html";
			// businessMenu.setServiceUrl(url);
			// businessMenu.setName("EDU全年套餐(测试)");
			// bundle.putParcelable(ExtraKeyConstant.KEY_BUSINESS_INFO,
			// businessMenu);
			// } else {
			// Toast.makeText(mBaseActivity, R.string.unlogin_notice,
			// Toast.LENGTH_SHORT).show();
			// }

			{
				cls = WebViewFragment.class;
				bundle = new Bundle();
				BusinessMenu businessMenu = new BusinessMenu();
				String url = "http://183.221.33.188:8092/scmbhi/edu30onsale50/html/edu_half_onsale.html";
				businessMenu.setServiceUrl(url);
				businessMenu.setName("EDU全年套餐(测试)");
				bundle.putParcelable(ExtraKeyConstant.KEY_BUSINESS_INFO, businessMenu);
			}
				break;
			case 7: {
				cls = TestWebViewFragment.class;
				bundle = new Bundle();
				BusinessMenu businessMenu = new BusinessMenu();
				String url = "http://wx.scmccboss.com:83/platformMobile/gateway/executor/goPage.do?executorName=goTo&from=scmbhc&redirect=assets/html/4G/4G_zxtc.html";
				String param = "app_version=" + App.sAPKVersionCode;
				url += (url.indexOf('?') == -1 ? '?' : '&') + param;
				System.out.println("url = " + url);
				businessMenu.setServiceUrl(url);
				businessMenu.setName("合家欢(测试)");
				bundle.putParcelable(ExtraKeyConstant.KEY_BUSINESS_INFO, businessMenu);
			}
				break;
			default:
				break;
			}
		}

		if (cls != null) {
			Intent intent = new Intent(mBaseActivity, SingleFragmentActivity.class);
			intent.putExtra(App.ExtraKeyConstant.KEY_FRAGMENT, cls);
			intent.putExtra(App.ExtraKeyConstant.KEY_BUNDLE, bundle);
			mBaseActivity.startActivity(intent);
		}
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.more, container, false);

		ListView listView_commFunction = (ListView) view.findViewById(R.id.listView_commFunction);
		listView_commFunction.setAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.moreFunctionInCommon, R.layout.item_more_comm_factory));
		CommUtil.expandListView((BaseAdapter) listView_commFunction.getAdapter(), listView_commFunction);
		listView_commFunction.setOnItemClickListener(this);

		ListView listView_other = (ListView) view.findViewById(R.id.listView_other);
		listView_other.setAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.moreFunctionOther, R.layout.item_more_comm_factory));
		CommUtil.expandListView((BaseAdapter) listView_other.getAdapter(), listView_other);
		listView_other.setOnItemClickListener(this);

		return view;
	}

	public void onStart() {
		super.onStart();
		BaseActivity baseActivity = (BaseActivity) getActivity();
		baseActivity.setLeftButtonVisibility(View.VISIBLE);
		baseActivity.setTitle(getResources().getString(R.string.more));
	}

	/*
	 * 功能: 为用户行为提供页面名称
	 * 
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.MoreFragment;
	}

}
