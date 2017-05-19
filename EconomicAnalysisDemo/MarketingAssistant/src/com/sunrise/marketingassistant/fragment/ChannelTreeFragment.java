package com.sunrise.marketingassistant.fragment;

import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.activity.HallDetailActivity;
import com.sunrise.marketingassistant.entity.TabContent;
import com.sunrise.marketingassistant.entity.TabContentManager;

public class ChannelTreeFragment extends NodeCheckFragment {
	protected void goNextPage(int index) {
		TabContent temp = TabContentManager.getInstance(mBaseActivity).getTabContent(ExtraKeyConstant.ID_TAB_ITEM_CHANNEL_DETAIL);
		startActivity(HallDetailActivity.createIntent(mBaseActivity, WebViewFragment2.class, temp.getZipContent(), temp.getTabName(), temp.getLastModify(),
				channelDataList.get(index).getGROUP_ID()));
	}
}
