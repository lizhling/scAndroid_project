package com.sunrise.marketingassistant.fragment;

import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.activity.SingleFragmentActivity;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup;

public class PickMessageFragment extends BaseFragment implements OnClickListener, TaskListener {
	private TextView base_info;
	private TextView activity_info;
	public static String chlId;
	public static String imgId;
	private LinearLayout buttom_bar_group;

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_pick_message, container, false);
		if (this.getArguments() != null) {
			String parm = this.getArguments().getString(Intent.EXTRA_TEXT);
			if(parm.contains("&")){
				if(parm.split("&").length==2){
					chlId = parm.split("&")[0];
					imgId = parm.split("&")[1];
				}else{
					chlId = parm.split("&")[0];
					imgId = "";
				}

			}
		} else {
			chlId = null;
		}

		buttom_bar_group = (LinearLayout) view.findViewById(R.id.buttom_bar_group);
		if (chlId == null || chlId.equals("-999")) {
			buttom_bar_group.setVisibility(View.GONE);
		} else {
			buttom_bar_group.setVisibility(View.VISIBLE);
		}
		base_info = (TextView) view.findViewById(R.id.base_info);
		activity_info = (TextView) view.findViewById(R.id.activity_info);
		base_info.setOnClickListener(this);
		activity_info.setOnClickListener(this);
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mBaseActivity.setTitle("渠道网点信息");
		mBaseActivity.setTitleBarLeftClick(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mBaseActivity.finish();
			}
		});
		if (chlId == null || chlId.equals("-999")) {

		} else {
			mBaseActivity.setAddActivityClick(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ActivityInfoCollectionFragment.newInstance(null, chlId);
					startActivity(SingleFragmentActivity.createIntent(mBaseActivity, ActivityInfoCollectionFragment.class, null, null, null, null));
				}
			});
		}

		FragmentManager fm = mBaseActivity.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ChannelsInfoCollectionFragment baseInfoFragment = new ChannelsInfoCollectionFragment();
		ft.replace(R.id.content, baseInfoFragment, "MainActivity");
		ft.commit();
		setBackgroundColor(1);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onCancelled(GenericTask task) {
		// TODO Auto-generated method stub

	}

	private void setBackgroundColor(int index) {
		if (index == 1) {
			base_info.setBackgroundResource(R.color.bottom_click);
			activity_info.setBackgroundResource(R.color.bottom_unclick);
		} else {
			base_info.setBackgroundResource(R.color.bottom_unclick);
			activity_info.setBackgroundResource(R.color.bottom_click);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		FragmentManager fm = mBaseActivity.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		switch (v.getId()) {
		case R.id.base_info:
			ChannelsInfoCollectionFragment baseInfoFragment = new ChannelsInfoCollectionFragment();
			ft.replace(R.id.content, baseInfoFragment, "MainActivity");
			ft.commit();
			setBackgroundColor(1);
			break;
		case R.id.activity_info:
			ActivityInfoCollectionListFragment activityInfoFragment = new ActivityInfoCollectionListFragment();
			Bundle bundle = new Bundle();
			bundle.putString(Intent.EXTRA_DATA_REMOVED, chlId);
			activityInfoFragment.setArguments(bundle);
			ft.replace(R.id.content, activityInfoFragment, "MainActivity");
			ft.commit();
			setBackgroundColor(2);
			break;
		default:
			break;
		}

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
