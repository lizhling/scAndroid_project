package com.sunrise.scmbhc.ui.fragment;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.ui.activity.SingleFragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class TopUpCompleteFragment extends BaseFragment implements OnClickListener {

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_top_up_complete, container, false);

		TextView phoneNum = (TextView) view.findViewById(R.id.phoneNum);
		phoneNum.setText(getArguments().getString(App.ExtraKeyConstant.KEY_PHONE_NUMBER));

		TextView topupSum = (TextView) view.findViewById(R.id.topupSum);
		topupSum.setText(getArguments().getString(App.ExtraKeyConstant.KEY_TOP_UP_AMOUNT));

		view.findViewById(R.id.feedBack).setOnClickListener(this);

		return view;
	}

	public void onStart() {
		super.onStart();
		mBaseActivity.setTitle(getResources().getString(R.string.topUpComplete));
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.feedBack:
			startFeedBackActivity();
			// new UserFeedbackFragment().startFragment(mBaseActivity,
			// R.id.fragmentContainer);
			break;

		default:
			break;
		}
	}

	private void startFeedBackActivity() {
		Intent intent = new Intent(mBaseActivity, SingleFragmentActivity.class);
		intent.putExtra(App.ExtraKeyConstant.KEY_FRAGMENT, UserFeedbackFragment.class);
		startActivity(intent);
	}
	/* 
	 * 功能: 为用户行为提供页面名称
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 * 
	 */
	@Override
	int getClassNameTitleId() {
		// TODO Auto-generated method stub
		return R.string.TopUpCompleteFragment;
	}

}
