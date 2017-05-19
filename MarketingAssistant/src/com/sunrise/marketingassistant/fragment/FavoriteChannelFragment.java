package com.sunrise.marketingassistant.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.javascript.utils.FileUtils;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.activity.HallDetailActivity;
import com.sunrise.marketingassistant.activity.SingleFragmentActivity;
import com.sunrise.marketingassistant.adapter.BusinessHallsAdapter;
import com.sunrise.marketingassistant.cache.preference.Preferences;
import com.sunrise.marketingassistant.database.CmmaDBHelper;
import com.sunrise.marketingassistant.entity.CollectBranch;
import com.sunrise.marketingassistant.entity.MobileBusinessHall;
import com.sunrise.marketingassistant.entity.TabContent;
import com.sunrise.marketingassistant.entity.TabContentManager;
import com.sunrise.marketingassistant.entity.UpdateInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FavoriteChannelFragment extends BaseFragment implements OnItemClickListener, ExtraKeyConstant {

	private ListView mListVIew;
	/** 营业厅信息 */
	private ArrayList<MobileBusinessHall> mArrMobileBusinessHalls = new ArrayList<MobileBusinessHall>();
	private int mIndexTemp;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		CmmaDBHelper cdbh = new CmmaDBHelper(mBaseActivity);
		ArrayList<CollectBranch> data = cdbh.queryCollectBranchByAccount(getPreferences().getSubAccount());
		if (data != null && !data.isEmpty())
			for (CollectBranch item : data)
				mArrMobileBusinessHalls.add(item.toMobileBusinessHall());
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.simple_listpage, null, false);

		mListVIew = (ListView) view.findViewById(R.id.listView01);
		mListVIew.setEmptyView(view.findViewById(R.id.textView_noContent));
		mListVIew.setAdapter(new BusinessHallsAdapter(mArrMobileBusinessHalls, mBaseActivity));
		mListVIew.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		mIndexTemp = arg2;

		MobileBusinessHall hall = mArrMobileBusinessHalls.get(arg2);
		gotoHallDetail(hall);
	}

	private void gotoHallDetail(MobileBusinessHall hall) {
		Intent intent = null;
		TabContent temp = TabContentManager.getInstance(mBaseActivity).getTabContent(ID_TAB_ITEM_CHANNEL_DETAIL);
		intent = HallDetailActivity.createIntent(mBaseActivity, WebViewFragment2.class, temp.getZipContent(), temp.getTabName(), temp.getLastModify(),
				hall.getGROUP_ID());
		startActivity(intent);
	}

}
