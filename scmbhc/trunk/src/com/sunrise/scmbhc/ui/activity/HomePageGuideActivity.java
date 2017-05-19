package com.sunrise.scmbhc.ui.activity;

import com.sunrise.scmbhc.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class HomePageGuideActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageView view = new ImageView(this);
		view.setAdjustViewBounds(true);
		view.setScaleType(ScaleType.FIT_XY);
		view.setImageResource(R.drawable.homepage_guide);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		setContentView(view);
	}

	protected void onDestory() {
		super.onDestroy();
	}
}
