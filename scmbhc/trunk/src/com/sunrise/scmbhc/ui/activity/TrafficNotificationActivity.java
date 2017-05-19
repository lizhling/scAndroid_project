package com.sunrise.scmbhc.ui.activity;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.ui.fragment.TrafficNotificationFragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class TrafficNotificationActivity extends Activity {
	public static boolean isShowing = false;
	private TextView mMessage1View;
	private TextView mMessage2View;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		UserInfoControler userInfoControler = UserInfoControler.getInstance();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.single_button_dialog);
		((TextView) (findViewById(R.id.dialog_title))).setText(getString(R.string.traffic_notification));
		((Button) (findViewById(R.id.confirm_button))).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dimiss();
			}
		});
		int trafficThreshold = App.sSettingsPreferences.getTrafficThreshold(TrafficNotificationFragment.DEFAULT_TRAFFIC_THRESHOLD);
		String tickerText = String.format(getString(R.string.traffic_low_threadshold), trafficThreshold + "%");
		String remindTrafficStr = String.format(getString(R.string.remiand_traffic), userInfoControler.getConditionFlow(null).getSurplusString());

		boolean isOverHandler = getIntent().getBooleanExtra(App.ExtraKeyConstant.KEY_IS_TRAFFIC_OVER, false);
		if (isOverHandler) {
			tickerText = getString(R.string.traffic_over);
		}
		mMessage1View = (TextView) findViewById(R.id.message_1);
		mMessage2View = (TextView) findViewById(R.id.message_2);
		mMessage1View.setText(tickerText);
		mMessage2View.setText(remindTrafficStr);
		isShowing = true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dimiss();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void dimiss() {
		finish();
		isShowing = false;
	}
}
