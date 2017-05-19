package com.sunrise.scmbhc.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.ui.activity.BaseActivity;
import com.sunrise.scmbhc.ui.view.ContacksDialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class GiveTrafficAsGiftFragment extends BaseFragment implements OnClickListener, OnItemClickListener {

	private GenericTask mTask;
	private TextView mPhoneNumber;
	private Spinner mSpinnerPackage;
	private Spinner mSpinnerDuration;

	private TextView mBusinessIntroduce;
	private TextView mWarmNotice;

	private ArrayList<BusinessMenu> mArraySubBusinessinfos;

	private Dialog mContacksDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			mArraySubBusinessinfos = savedInstanceState.getParcelableArrayList(App.ExtraKeyConstant.KEY_BUSINESS_INFO);
		} else {
			mArraySubBusinessinfos = getArguments().getParcelableArrayList(App.ExtraKeyConstant.KEY_BUSINESS_INFO);
		}
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_give_traffic_package, container, false);

		view.findViewById(R.id.button_addressList).setOnClickListener(this);
		view.findViewById(R.id.button_giveCertain).setOnClickListener(this);

		mPhoneNumber = (TextView) view.findViewById(R.id.editText_phoneNumber);

		mSpinnerPackage = (Spinner) view.findViewById(R.id.spinner_business);

		mSpinnerDuration = (Spinner) view.findViewById(R.id.spinner_duration);

		mBusinessIntroduce = (TextView) view.findViewById(R.id.businessIntroduce);
		mWarmNotice = (TextView) view.findViewById(R.id.textView_warmNotice);

		// 判断包数据，如果已经读取过业务列表，直接生成组件，否则从数据库加载。
		if (mArraySubBusinessinfos != null && !mArraySubBusinessinfos.isEmpty())
			initSpinnerDropDownBusiness();

		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList(App.ExtraKeyConstant.KEY_BUSINESS_INFO, mArraySubBusinessinfos);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		super.onStart();
		BaseActivity baseActivity = (BaseActivity) getActivity();
		baseActivity.setLeftButtonVisibility(View.VISIBLE);
		baseActivity.setTitle(R.string.titleMoblileTrafficGiveAsGift);
	}

	public void onStop() {
		super.onStop();
		if (mTask != null)
			mTask.cancle();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.button_addressList:// 通讯录获取
			if (mContacksDialog == null)
				mContacksDialog = new ContacksDialog(mBaseActivity, new ContacksDialog.ContacksCallBack() {

					@Override
					public void callback(String phoneNunber) {
						mPhoneNumber.setText(phoneNunber);
					}
				});
			mContacksDialog.show();
			break;
		case R.id.button_giveCertain:// 确认赠送
			break;
		default:
			break;
		}
	}

	private void initSpinnerDropDownBusiness() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(mArraySubBusinessinfos.size());
		for (BusinessMenu item : mArraySubBusinessinfos) {
			HashMap<String, String> hash = new HashMap<String, String>();
			hash.put("name", item.getName());
			list.add(hash);
		}
		// new
		// ArrayAdapter<String>(mBaseActivity,R.layout.radiobutton_for_spinner
		// ,list)
		SimpleAdapter adpater = new SimpleAdapter(mBaseActivity, list, android.R.layout.simple_dropdown_item_1line, new String[] { "name" },
				new int[] { android.R.id.checkbox });
		adpater.setDropDownViewResource(android.R.layout.simple_list_item_1);
		mSpinnerPackage.setAdapter(adpater);
		// mSpinnerPackage.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {

		// 用按钮记录下当前选中的序号。用以确定对应业务信息。
		mSpinnerPackage.setTag(position);

		// 显示当前业务的业务介绍和温馨提示
		mBusinessIntroduce.setText(mArraySubBusinessinfos.get(position).getDescription());
		mWarmNotice.setText(mArraySubBusinessinfos.get(position).getWarmPrompt());

	}
	/* 
	 * 功能: 为用户行为提供页面名称
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 * 
	 */
	@Override
	int getClassNameTitleId() {
		// TODO Auto-generated method stub
		return R.string.GiveTrafficAsGiftFragment;
	}

}
