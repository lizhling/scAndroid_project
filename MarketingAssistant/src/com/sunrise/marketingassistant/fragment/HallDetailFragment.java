package com.sunrise.marketingassistant.fragment;

import java.util.ArrayList;

import com.baidu.mapapi.model.LatLng;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.activity.SingleFragmentActivity;
import com.sunrise.marketingassistant.database.CmmaDBHelper;
import com.sunrise.marketingassistant.entity.CollectBranch;
import com.sunrise.marketingassistant.entity.MobMenberScore;
import com.sunrise.marketingassistant.entity.MobileBusinessHall;
import com.sunrise.marketingassistant.task.DoMobRegisteTask;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.GetMobMenberScoreTask;
import com.sunrise.marketingassistant.task.MobInfoByGroupIdTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import com.sunrise.marketingassistant.utils.BaiduMapUtils;
import com.sunrise.marketingassistant.utils.CommUtil;
import com.sunrise.marketingassistant.utils.HardwareUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class HallDetailFragment extends BaseFragment implements OnClickListener, OnCheckedChangeListener, TaskListener {
	private MobileBusinessHall mMobHallInfo;

	private GenericTask mTask;
	private MobInfoByGroupIdTask mMobInfoByGroupIdTask;
	/** 店员积分 */
	private ArrayList<MobMenberScore> mArrayMobMenberScore;

	/** 店员人数 */
	private TextView mTV_numOfEmployees;

	/** 数据库 */
	private CmmaDBHelper mDatabase;

	/** 员工详情表格 */
	private TableLayout mTableEmployeesDetail;

	private TextView hall_name;

	private TextView hall_address;

	private TextView hall_linkman;

	private TextView hall_tel;

	private TextView hall_starLevel;

	private WebView mWebView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDatabase = new CmmaDBHelper(mBaseActivity);

		if (savedInstanceState == null)
			savedInstanceState = getArguments();

		mMobHallInfo = savedInstanceState.getParcelable(Intent.EXTRA_DATA_REMOVED);

		mArrayMobMenberScore = savedInstanceState.getParcelableArrayList(Intent.EXTRA_ALARM_COUNT);

		boolean isWhole = savedInstanceState.getBoolean(Intent.EXTRA_REPLACING, true);
		if (!isWhole) {
			doGetGroupInfo(mMobHallInfo.getGROUP_ID());
		}

		if (mArrayMobMenberScore == null || mArrayMobMenberScore.isEmpty())
			doGetMobMenberScore();
	}

	public void onSaveInstanceState(Bundle outState) {

		outState.putParcelable(Intent.EXTRA_DATA_REMOVED, mMobHallInfo);
		outState.putParcelableArrayList(Intent.EXTRA_ALARM_COUNT, mArrayMobMenberScore);
		super.onSaveInstanceState(outState);
	}

	public void onStart() {
		super.onStart();

	}

	public void onStop() {

		super.onStop();
	}

	public void onDestroy() {
		if (mTask != null)
			mTask.cancle();
		if (mMobInfoByGroupIdTask != null)
			mMobInfoByGroupIdTask.cancle();

		super.onDestroy();
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_hall_detail, null, false);
		view.findViewById(R.id.btn_sign).setOnClickListener(this);
		view.findViewById(R.id.btn_editInfo).setOnClickListener(this);

		hall_name = (TextView) view.findViewById(R.id.hall_name);
		hall_address = (TextView) view.findViewById(R.id.hall_address);
		hall_linkman = (TextView) view.findViewById(R.id.hall_linkman);
		hall_tel = (TextView) view.findViewById(R.id.hall_tel);
		hall_starLevel = (TextView) view.findViewById(R.id.hall_starLevel);
		mTableEmployeesDetail = (TableLayout) view.findViewById(R.id.table_employee);
		mWebView = (WebView) view.findViewById(R.id.webview);
		mTV_numOfEmployees = (TextView) view.findViewById(R.id.hall_numOfEmployees);
		{// 收藏按钮
			ToggleButton tb = (ToggleButton) view.findViewById(R.id.btn_collection);
			int num = mDatabase.getCollectBranchCountByGroupID(getPreferences().getSubAccount(), mMobHallInfo.getGROUP_ID());
			tb.setChecked(num > 0);
			tb.setOnCheckedChangeListener(this);
		}
		return view;
	}

	private void refreashView() {
		hall_name.setText(mMobHallInfo.getGROUP_NAME());
		hall_address.setText(mMobHallInfo.getGROUP_ADDRESS());
		hall_linkman.setText(mMobHallInfo.getCONTACT_PERSON());
		hall_tel.setText(mMobHallInfo.getCONTACT_PHONE());
		hall_starLevel.setText(mMobHallInfo.getGRADE_CODE());
		if (mArrayMobMenberScore != null)
			mTV_numOfEmployees.setText(mArrayMobMenberScore.size() + "人");

		if (mMobHallInfo != null && mMobHallInfo.getLATITUDE() != 0 && mMobHallInfo.getLONGITUDE() != 0) {
			/** 静态地图 */
			mWebView.setVisibility(View.VISIBLE);
			int width = 360;
			int height = 240;
			String url = new BaiduMapUtils(mBaseActivity)
					.getStaticImage(new LatLng(mMobHallInfo.getLATITUDE(), mMobHallInfo.getLONGITUDE()), width, height, 17);
			mWebView.loadUrl(url);
			mWebView.setOnClickListener(this);
		}

		if (mArrayMobMenberScore != null && !mArrayMobMenberScore.isEmpty()) {
			mTableEmployeesDetail.setVisibility(View.VISIBLE);
			mTV_numOfEmployees.setText(mArrayMobMenberScore.size() + "人");
			mTableEmployeesDetail.removeAllViews();
			for (MobMenberScore item : mArrayMobMenberScore) {
				View view = LayoutInflater.from(mBaseActivity).inflate(R.layout.item_employee_info, null, false);
				((TextView) view.findViewById(R.id.employee_no)).setText(item.getPERSON_ID());
				((TextView) view.findViewById(R.id.employee_name)).setText(item.getPERSON_NAME());
				((TextView) view.findViewById(R.id.employee_currentScore)).setText(item.getCUR_SCORE());
				((TextView) view.findViewById(R.id.employee_totleScore)).setText(item.getTOL_SCORE());
				mTableEmployeesDetail.addView(view);
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_sign:
			doSignIn();
			break;
		case R.id.btn_editInfo:
			break;
		case R.id.webview:
			gotoMapPage();
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		switch (arg0.getId()) {
		case R.id.btn_collection:
			doCollection(arg1);
			break;

		default:
			break;
		}
	}

	private void doCollection(boolean arg1) {
		if (arg1) {
			CollectBranch collect = new CollectBranch();
			collect.setCLASS_NAME(mMobHallInfo.getCLASS_NAME());
			collect.setAccount(getPreferences().getSubAccount());
			collect.setGROUP_ADDRESS(mMobHallInfo.getGROUP_ADDRESS());
			collect.setGROUP_ID(mMobHallInfo.getGROUP_ID());
			collect.setGROUP_NAME(mMobHallInfo.getGROUP_NAME());
			long result = mDatabase.insert(collect);
			if (result > -1)
				Toast.makeText(mBaseActivity, mMobHallInfo.getGROUP_NAME() + " 被收藏成功", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(mBaseActivity, "收藏夹中已存在" + mMobHallInfo.getGROUP_NAME(), Toast.LENGTH_SHORT).show();
		}

		else {
			int result = mDatabase.deleteByGroupId(getPreferences().getSubAccount(), mMobHallInfo.getGROUP_ID());
			if (result >= 0)
				Toast.makeText(mBaseActivity, mMobHallInfo.getGROUP_NAME() + " 被取消收藏", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(mBaseActivity, mMobHallInfo.getGROUP_NAME() + " 从未被收藏", Toast.LENGTH_SHORT).show();
		}
	}

	private void doGetMobMenberScore() {
		mTask = new GetMobMenberScoreTask().execute(mMobHallInfo.getGROUP_ID(), this);
	}

	/** 签到的动作 */
	private void doSignIn() {
		new DoMobRegisteTask().execute(mMobHallInfo.getGROUP_ID(), getPreferences().getSubAccount(), getPreferences().getMobile(), mMobHallInfo.getLATITUDE(),
				mMobHallInfo.getLONGITUDE(), HardwareUtils.getPhoneIMSI(mBaseActivity), this);

	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {
		if (task instanceof DoMobRegisteTask) {
			initDialog();
			showDialog(mMobHallInfo.getGROUP_NAME() + " 签到过程中……");
		}
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		dismissDialog();
		if (result != TaskResult.OK)
			Toast.makeText(mBaseActivity, task.getException().getMessage(), Toast.LENGTH_LONG).show();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (task instanceof GetMobMenberScoreTask) {
			mArrayMobMenberScore = (ArrayList<MobMenberScore>) param;
			refreashView();
		}

		else if (task instanceof DoMobRegisteTask)
			CommUtil.showAlert(mBaseActivity, null, mMobHallInfo.getGROUP_NAME() + "\n" + param.toString(), null, false, null);

		else if (task instanceof MobInfoByGroupIdTask) {
			mMobHallInfo.merge((MobileBusinessHall) param);
			refreashView();
		}

	}

	@Override
	public void onCancelled(GenericTask task) {
		dismissDialog();
	}

	private void gotoMapPage() {
		Intent intent = null;
		intent = new Intent(mBaseActivity, SingleFragmentActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle = new Bundle();
		bundle.putBoolean(Intent.EXTRA_CC, true);
		ArrayList<MobileBusinessHall> array = new ArrayList<MobileBusinessHall>();
		array.add(mMobHallInfo);
		bundle.putParcelableArrayList(Intent.EXTRA_DATA_REMOVED, array);
		bundle.putInt(Intent.EXTRA_UID, 0);
		intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, bundle);
		intent.putExtra(ExtraKeyConstant.KEY_FRAGMENT, MapLocationShowFragment.class);
		startActivity(intent);
	}

	private void doGetGroupInfo(String groupId) {
		if (mMobInfoByGroupIdTask != null)
			mMobInfoByGroupIdTask.cancle();
		mMobInfoByGroupIdTask = new MobInfoByGroupIdTask().execute(getPreferences().getMobile(), groupId, getPreferences().getSubAccount(), this);
	}
}
