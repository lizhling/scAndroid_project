package com.sunrise.scmbhc.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.adapter.SpinnerAdapter;
import com.sunrise.scmbhc.entity.UseCondition;
import com.sunrise.scmbhc.ui.view.MySpinner;
import com.sunrise.scmbhc.ui.view.ProgressSetDialog;
import com.sunrise.scmbhc.ui.view.SwitchButton;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.LogUtlis;

public class TrafficNotificationFragment extends BaseFragment {
	public static final int NOTIFICATION_MODE = 0;
	public static final int NOTIFICATION_DIALOG_MODE = 1;
	public static final int TRAFFIC_OVER_HANDLE_MODE_0 = 0;
	public static final int TRAFFIC_OVER_HANDLE_MODE_1 = 1;
	public static final int TRAFFIC_OVER_HANDLE_MODE_2 = 2;
	public static final int DEFAULT_TRAFFIC_THRESHOLD = 5;

	private static final String FORMAT = "<i><font color=\"%d\">%s</font></i> / %s";// 用于流量使用状况的显示（HTML格式）
	private static final int MAX_SEEKBAR_DIALOG = 100;

	private ProgressBar mTrafficProgress;
	private TextView mRemindTrafficView;
	private SwitchButton mTrafficSwitchButton;
	private MySpinner mNotificationModeSpinner;
	private MySpinner mTrafficOverHandleModeSpinner;
	private SpinnerAdapter mNotificationModeAdapter;
	private SpinnerAdapter mTrafficOverHandleModeAdapter;
	private String[] mNotificationModes;
	private String[] mTrafficOverHandleModes;
	private double mMaxTraffic = 100;
	private int mTrafficThreshold;// 流量提醒百分比
	private int mNotificationMode;// 流量提醒模式
	private int mTrafficOverHandleMode;//流量超限处理模式
	private boolean mIsTrafficNotificationFunction;//是否提醒
	private UserInfoControler mUserInfoControler = UserInfoControler.getInstance();
	private Button bt_notify;
	private Button bt;
	private Button mBtThreshold;
	private UseCondition mUseCondition;
	private ProgressSetDialog mProgressSetDialog;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		mIsTrafficNotificationFunction = App.sSettingsPreferences.isTrafficNotificationFunction(true);
		mTrafficThreshold = App.sSettingsPreferences.getTrafficThreshold(DEFAULT_TRAFFIC_THRESHOLD);
		mNotificationMode = App.sSettingsPreferences.getTrafficNotifictionMode(NOTIFICATION_MODE);
		mTrafficOverHandleMode = App.sSettingsPreferences.getTrafficOverHandleMode(TRAFFIC_OVER_HANDLE_MODE_1);
	}
	
	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.traffic_notification, container, false);
		
		mTrafficSwitchButton = (SwitchButton) view.findViewById(R.id.traffic_notification_switch);
		mTrafficSwitchButton.setChecked(mIsTrafficNotificationFunction);
		mTrafficSwitchButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				updateViewEnable(isChecked);
				App.sSettingsPreferences.setTrafficNotificationFunction(isChecked);
				CommUtil.startTrafficNotificationService(mBaseActivity);
			}
		});
		mRemindTrafficView = (TextView) view.findViewById(R.id.remind_traffic);
		mTrafficProgress = (ProgressBar) view.findViewById(R.id.traffic_progress);

		mNotificationModes = mBaseActivity.getResources().getStringArray(R.array.trafficNotificationMode);
		mNotificationModeAdapter = new SpinnerAdapter(mBaseActivity, mNotificationModes);
		mNotificationModeSpinner = (MySpinner) view.findViewById(R.id.traffic_notification_mode);
		mNotificationModeSpinner.setAdapter(mNotificationModeAdapter);
		mNotificationModeSpinner.setSelectIndex(mNotificationMode);
		mNotificationModeSpinner.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				App.sSettingsPreferences.setTrafficNotifictionMode(position);
			}
		});

		final Spinner sp_notify = (Spinner) view.findViewById(R.id.traffic_notification_mode_spinner);
		sp_notify.setAdapter(mNotificationModeAdapter);

		bt_notify = (Button) view.findViewById(R.id.traffic_notification_modetest);
		sp_notify.setSelection(mNotificationMode);
		sp_notify.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				LogUtlis.showLogI("TrafficNotify", "position: " + arg2 + ", id: " + arg3 + " text:" + mNotificationModes[arg2]);
				bt_notify.setText(mNotificationModes[arg2]);
				mNotificationModeAdapter.setCurrentPosition(arg2);

				mNotificationModeAdapter.notifyDataSetChanged();
				App.sSettingsPreferences.setTrafficNotifictionMode(arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		;
		bt_notify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sp_notify.performClick();
				// ((Button)v).setText(mTrafficOverHandleModes[sp.getSelectedItemPosition()]);
			}
		});

		mTrafficOverHandleModes = mBaseActivity.getResources().getStringArray(R.array.trafficOverHandleMode);
		mTrafficOverHandleModeAdapter = new SpinnerAdapter(mBaseActivity, mTrafficOverHandleModes);
		mTrafficOverHandleModeSpinner = (MySpinner) view.findViewById(R.id.traffic_over_handle_mode);
		mTrafficOverHandleModeSpinner.setAdapter(mTrafficOverHandleModeAdapter);
		mTrafficOverHandleModeSpinner.setSelectIndex(mTrafficOverHandleMode);
		mTrafficOverHandleModeSpinner.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				App.sSettingsPreferences.setTrafficOverHandleMode(position);
			}
		});
		final Spinner sp = (Spinner) view.findViewById(R.id.traffic_over_handle_mode_spinner);
		sp.setAdapter(mTrafficOverHandleModeAdapter);

		bt = (Button) view.findViewById(R.id.traffic_over_handle_modetest);
		sp.setSelection(mTrafficOverHandleMode);
		sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				bt.setText(mTrafficOverHandleModes[arg2]);
				mTrafficOverHandleModeAdapter.setCurrentPosition(arg2);
				mTrafficOverHandleModeAdapter.notifyDataSetChanged();
				
				App.sSettingsPreferences.setTrafficOverHandleMode(arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		;
		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sp.performClick();
				// ((Button)v).setText(mTrafficOverHandleModes[sp.getSelectedItemPosition()]);
			}
		});

		mBtThreshold = (Button) view.findViewById(R.id.bt_threshold);
		mBtThreshold.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showProgressSetDialog(v);
			}
		});

		updateViewEnable(mIsTrafficNotificationFunction);
		return view;
	}

	private void updateViewEnable(boolean enabled) {
		if (mTrafficOverHandleModeSpinner != null)
			mTrafficOverHandleModeSpinner.setEnabled(enabled);
		if (mNotificationModeSpinner != null)
			mNotificationModeSpinner.setEnabled(enabled);
		if (mBtThreshold != null) {
			mBtThreshold.setEnabled(enabled);
		}
		if (bt != null) {
			bt.setEnabled(enabled);
		}
		if (bt_notify != null) {
			bt_notify.setEnabled(enabled);
		}
	}

	private void refreshTrafficProgress() {
		mUseCondition = mUserInfoControler.getConditionFlow(null);

		if (mUseCondition.getTotle() > -1) {

			int color = mBaseActivity.getResources().getColor(R.color.text_color_red);
			int yellogreen = mBaseActivity.getResources().getColor(R.color.bg_color_yellow_green);
			double percent = 1;
			if (mUseCondition.getSurplus() > 0 && mUseCondition.getTotle() > 0) {
				percent = mUseCondition.getSurplus() / mUseCondition.getTotle();
			} else {
				percent = 0.05;
			}
			if ( percent > 0.1) {
				mRemindTrafficView.setText(Html.fromHtml(String.format(FORMAT, yellogreen, mUseCondition.getSurplusString(), mUseCondition.getTotleString())));
			} else {
				mRemindTrafficView.setText(Html.fromHtml(String.format(FORMAT, color, mUseCondition.getSurplusString(), mUseCondition.getTotleString())));
			}
			mTrafficProgress.setProgress((int) (mUseCondition.getSurplusRate() * mMaxTraffic));
		} else {
			mRemindTrafficView.setText(R.string.getTrafficInfoFailed);
			mTrafficProgress.setProgress(0);
		}

		mBtThreshold.setText(Html.fromHtml(String.format(FORMAT_TRAFFIC_SET_BUTTON, mTrafficThreshold,
				UseCondition.getFlowString(mUseCondition.getTotle() * mTrafficThreshold / MAX_SEEKBAR_DIALOG))));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == 1) {
			refreshTrafficProgress();
		} else {
			mBaseActivity.onKeyDown(KeyEvent.KEYCODE_BACK, null);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		mBaseActivity.setTitle(R.string.traffic_notification);
		mBaseActivity.setLeftButtonVisibility(View.VISIBLE);

		if (!mBaseActivity.checkLoginIn(null)) {
			return;
		}

		refreshTrafficProgress();

	}

	private static String FORMAT_SEEKBAR_DIALOG = "<b>%d%%<b> (<i><font color=\"0xff4444\">%s</font></i>)";
	private static String FORMAT_TRAFFIC_SET_BUTTON = "<b>%d%%<b> (<i>%s</i>)";

	private void showProgressSetDialog(View v) {
		final double totle = mUseCondition.getTotle();
		if (totle < 0) {
			Toast.makeText(mBaseActivity, R.string.no_traffic_info, Toast.LENGTH_SHORT).show();
			return;
		}

		if (mProgressSetDialog == null) {
			ProgressSetDialog dialog = new ProgressSetDialog(v.getContext(), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int progress) {
					App.sSettingsPreferences.setTrafficThreshold(progress);

					mTrafficThreshold = progress;

					mBtThreshold.setText(Html.fromHtml(String.format(FORMAT_TRAFFIC_SET_BUTTON, mTrafficThreshold,
							UseCondition.getFlowString(mUseCondition.getTotle() * mTrafficThreshold / MAX_SEEKBAR_DIALOG))));
					dialog.dismiss();
				}
			}, null, new ProgressSetDialog.SeekbarChangeListener() {

				@Override
				public CharSequence getSeekBarValue(int progress, int max) {
					float rate = progress * 1f / max;
					return Html.fromHtml(String.format(FORMAT_SEEKBAR_DIALOG, (int) (rate * 100), UseCondition.getFlowString(mUseCondition.getTotle() * rate)));
				}
			});

			dialog.setMax(MAX_SEEKBAR_DIALOG);
			dialog.setTitle(R.string.traffic_notification_surplus);
			dialog.setMessage(R.string.traffic_notification_intruduce);

			mProgressSetDialog = dialog;

		}

		mProgressSetDialog.show();
		mProgressSetDialog.setProgress(mTrafficThreshold);
	}

	/* 
	 * 功能: 为用户行为提供页面名称
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 * 
	 */
	@Override
	int getClassNameTitleId() {
		// TODO Auto-generated method stub
		return R.string.getClassNameTitle;
	}
}
