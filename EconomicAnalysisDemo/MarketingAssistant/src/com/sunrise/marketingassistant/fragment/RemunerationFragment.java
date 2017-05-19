package com.sunrise.marketingassistant.fragment;

import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.activity.SingleFragmentActivity;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class RemunerationFragment extends BaseFragment implements OnClickListener, TaskListener {
	
	private Button number_check_btn;  //工号查询
	private Button node_check_btn;  //节点查询
	private Button list_check_btn;  //明细查询

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_remuneration, container, false);
		number_check_btn =(Button)view.findViewById(R.id.number_check_btn);
		number_check_btn.setOnClickListener(this);
		node_check_btn =(Button)view.findViewById(R.id.node_check_btn);
		node_check_btn.setOnClickListener(this);
		list_check_btn =(Button)view.findViewById(R.id.list_check_btn);
		list_check_btn.setOnClickListener(this);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.number_check_btn:
			startActivity(SingleFragmentActivity.createIntent(mBaseActivity, CardNoCheckFragment.class,
					getPreferences().getGroupId(), null, null, null));
			break;
		case R.id.node_check_btn:
			startActivity(SingleFragmentActivity.createIntent(mBaseActivity, NodeCheckFragment.class, null, null, null, null));
			break;
		case R.id.list_check_btn:
			startActivity(SingleFragmentActivity.createIntent(mBaseActivity, DetailCheckFragment.class, null, null, null, null));
			break;
		}

	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (param != null) {
		}
	}

	@Override
	public void onPreExecute(GenericTask task) {
		initDialog();
		showDialog(getString(R.string.check_apk_version_progress));
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		dismissDialog();
		if (result != TaskResult.OK)
			Toast.makeText(mBaseActivity, getString(R.string.latest_version), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCancelled(GenericTask task) {

	}

	@Override
	public String getName() {
		return null;
	}
}