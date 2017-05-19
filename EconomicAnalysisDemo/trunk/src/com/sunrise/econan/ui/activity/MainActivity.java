package com.sunrise.econan.ui.activity;

import java.util.ArrayList;

import com.sunrise.econan.ui.animation.Rotate3dAnimation;
import com.sunrise.econan.R;
import com.sunrise.econan.adapter.CoverflowImageAdapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Gallery gallery1;

	private OnItemSelectedListener listener = new OnItemSelectedListener() {

		private Animation anim;

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (anim == null)
				anim = new Rotate3dAnimation(0, 360, arg1.getWidth() / 2.0f,
						arg1.getHeight() / 2.0f, -120.0f, true);
			anim.setDuration(800);
			anim.setFillAfter(true);

			for (int i = 0; i < arg0.getChildCount(); ++i) {
				arg0.getChildAt(i).clearAnimation();
			}
			arg1.startAnimation(anim);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			System.out.println("nothing");
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		gallery1 = (Gallery) findViewById(R.id.coverflowGallery1);
		initGallery();

		gallery1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				startActivity(new Intent(getThis(), CityInfoActivity.class));
				overridePendingTransition(R.anim.activity_in,
						R.anim.activity_out);
			}
		});
	}

	private MainActivity getThis() {
		return this;
	}

	private void initGallery() {
		ArrayList<Bitmap> array = new ArrayList<Bitmap>();
		pullBitmapIntoArray(array);
		gallery1.setAdapter(new CoverflowImageAdapter(this, array));
		gallery1.setSelection(gallery1.getAdapter().getCount() >> 1);
	}

	private void pullBitmapIntoArray(ArrayList<Bitmap> array) {
		int[] resIds = { R.drawable.option_chanpingxinxi,
				R.drawable.option_chenyuanguanli,
				R.drawable.option_choujingguanli, R.drawable.option_dgcpcx,
				R.drawable.option_fujiachanpingguanli,
				R.drawable.option_haomayuyue,
				R.drawable.option_guojimanyoubanli, R.drawable.option_hftxfwdz };
		for (int id : resIds) {
			Bitmap object = BitmapFactory.decodeResource(getResources(), id);
			array.add(object);
		}
	}

	public void onStart() {
		super.onStart();
		gallery1.setOnItemSelectedListener(listener);
		initBatterymanager();
	}

	public void onStop() {
		super.onStop();
		gallery1.setOnItemSelectedListener(null);
		unregisterReceiver(BtStatusReceiver);
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	private void initBatterymanager() {
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(BtStatusReceiver, mIntentFilter);
	}

	public BroadcastReceiver BtStatusReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				Log.d("Battery", "" + intent.getIntExtra("plugged", 0));
				Toast text = Toast.makeText(
						context,
						"ACTION_USB_DEVICE_ATTACHED"
								+ intent.getIntExtra("plugged", 0),
						Toast.LENGTH_LONG);
				text.show();
			}
		}
	};

}
