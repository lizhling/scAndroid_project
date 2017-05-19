package com.sunrise.scmbhc.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.adapter.ManualAdapter;
import com.sunrise.scmbhc.entity.ContentInfo;
import com.sunrise.scmbhc.ui.activity.BaseActivity;
import com.sunrise.scmbhc.ui.view.CornerListView;
import com.sunrise.scmbhc.utils.LogUtlis;

public class ManualFragment extends BaseFragment {
	/* public class ManualActivity extends BaseActivity { */

	private String TAG = "ManualFragment";
	private CornerListView cornerListView = null;
	private static List<ContentInfo> contentinfo = null;
	private List<Map<String, String>> listData = null;
	private SimpleAdapter adapter = null;
	public static final String HELP_FUN = "使用帮助";
	public static final String NOTICE_FUN = "系统公告";
	public static final String PROBLEM_FUN = "常见问题";
	public static final int HELP_TYPE = 2;
	public static final int NOTICE_TYPE = 0;
	public static final int PROBLEM_TYPE = 1;
	private String function;

	public ManualFragment() {
		// TODO Auto-generated constructor stub
		function = HELP_FUN;
	}

	public void onStart() {
		super.onStart();
		BaseActivity baseActivity = (BaseActivity) getActivity();
		baseActivity.setLeftButtonVisibility(View.VISIBLE);
		if (function != null && function.length() > 0) {
			baseActivity.setTitle(function);
		} else {
			baseActivity.setTitle(getResources().getString(R.string.more));
		}

	}

	/**
	 * 设置列表数据
	 */
	private void setListData(String function) {

		initDialog();
		showDialog("加载数据中...");

		if ((1 == 2) && App.test) {
			contentinfo = null;
		} else {
			if (function.equals(HELP_FUN)) {
				// 请求帮助数据
				contentinfo = App.sServerClient.getContentList(getActivity(), HELP_TYPE);
				LogUtlis.showLogI(TAG, "使用帮助:" + contentinfo.size());
			} else if (function.equals(NOTICE_FUN)) {
				// 系统公告数据
				contentinfo = App.sServerClient.getContentList(getActivity(), NOTICE_TYPE);
				LogUtlis.showLogI(TAG, "系统公告:" + contentinfo.size());
			} else if (function.equals(PROBLEM_FUN)) {
				// 请求常见问题数据
				contentinfo = App.sServerClient.getContentList(getActivity(), PROBLEM_TYPE);
				LogUtlis.showLogI(TAG, "常见问题:" + contentinfo.size());
			}
		}

		dismissDialog();

		listData = new ArrayList<Map<String, String>>();
		if (contentinfo == null) {
			contentinfo = new ArrayList<ContentInfo>();
			ContentInfo cinfo = new ContentInfo();
			cinfo.setContentId(1L);
			cinfo.setTitle("分享");
			cinfo.setContentText("苹果近期人事任命暗示其将开发与健康和保健方面有关的可穿戴设备");
			cinfo.setContentType(HELP_TYPE);
			contentinfo.add(cinfo);
			cinfo = new ContentInfo();
			cinfo.setContentId(2L);
			cinfo.setTitle("检查新版本");
			cinfo.setContentType(NOTICE_TYPE);
			cinfo.setContentText("苹果近期人事任命暗示其将开发与健康和保健方面有关的可穿戴设备");
			contentinfo.add(cinfo);
			cinfo = new ContentInfo();
			cinfo.setContentId(2L);
			cinfo.setTitle("反馈意见");
			cinfo.setContentType(PROBLEM_TYPE);
			cinfo.setContentText("苹果近期人事任命暗示其将开发与健康和保健方面有关的可穿戴设备");
			contentinfo.add(cinfo);
			Map<String, String> map = new HashMap<String, String>();
			map.put("text", "分享");
			map.put("content", "苹果近期人事任命暗示其将开发与健康和保健方面有关的可穿戴设备");

			listData.add(map);

			map = new HashMap<String, String>();
			map.put("text", "检查新版本");
			map.put("content", "苹果近期人事任命暗示其将开发与健康和保健方面有关的可穿戴设备");
			listData.add(map);

			map = new HashMap<String, String>();
			map.put("text", "反馈意见");
			map.put("content", "苹果近期人事任命暗示其将开发与健康和保健方面有关的可穿戴设备");
			listData.add(map);

			map = new HashMap<String, String>();
			map.put("text", "关于我们");
			map.put("content", "苹果近期人事任命暗示其将开发与健康和保健方面有关的可穿戴设备");
			listData.add(map);

			map = new HashMap<String, String>();
			map.put("text", "支持我们，请点击这里的广告");
			map.put("content", "苹果近期人事任命暗示其将开发与健康和保健方面有关的可穿戴设备");
			listData.add(map);
		} else {
			for (int i = 0; i < contentinfo.size(); i++) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("text", contentinfo.get(i).getTitle());
				map.put("content", contentinfo.get(i).getContentText());
				if (contentinfo.get(i).getContentType() == NOTICE_TYPE) {
					map.put("datetime", contentinfo.get(i).getUpdateTime());
				} else {
					map.put("datetime", null);
				}
				listData.add(map);
			}
		}
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle tag = getArguments();
		String falg = tag.getString("function");
		if (falg != null && !falg.equals(HELP_FUN) && !falg.equals(NOTICE_FUN) && !falg.equals(PROBLEM_FUN)) {
			function = HELP_FUN;
		} else {
			function = falg;
		}
		View view = inflater.inflate(R.layout.manual, container, false);
		cornerListView = (CornerListView) view.findViewById(R.id.help_item_list);
		TextView tv_title = (TextView) view.findViewById(R.id.tv_handertitle);

		tv_title.setText(function);
		setListData(falg);
		BaseAdapter adapter1 = new ManualAdapter(getActivity(), contentinfo);

		cornerListView.setAdapter(adapter1);
		TextView tv_titlenum = (TextView) view.findViewById(R.id.tv_handernum);
		tv_titlenum.setText("共" + contentinfo.size() + "条");
		expandListView((BaseAdapter) cornerListView.getAdapter(), cornerListView);
		cornerListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				LinearLayout ll_layout = (LinearLayout) arg1;
				RelativeLayout llnum = (RelativeLayout) ll_layout.getChildAt(0);
				TextView tv_title = (TextView) llnum.getChildAt(0);
				BaseActivity baseActivity = (BaseActivity) getActivity();
				BaseFragment fragment = null;

				fragment = new ShowDetailFragment();

				// 请求数据
				// 传递参数
				Bundle retInfo = new Bundle();
				retInfo.putString("function", function);
				if (contentinfo != null) {// && (!App.test)
					String content = contentinfo.get(arg2).getContentText();
					retInfo.putString("details", content);
				} else {
					retInfo.putString("details", "1. 输入手机号\n 2. 点击获取验证码  3. 输入收到的短信验证码   4. 点击登录进行相关信息查询与办理");
				}
				fragment.setArguments(retInfo);
				if (fragment != null) {
					baseActivity.setCurrentFragment(fragment);
					fragment.startFragment(baseActivity, R.id.fragmentContainer);
				}

			}
		});

		return view;
	}

	public static void expandListView(BaseAdapter adapter, ListView listView) {
		// 统计高度
		int totalHeight = 0;
		for (int i = 0, len = adapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = adapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1)) + listView.getPaddingTop() + listView.getPaddingBottom();
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	/*
	 * 功能: 为用户行为提供页面名称
	 * 
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 */
	@Override
	int getClassNameTitleId() {
		// TODO Auto-generated method stub
		return R.string.ManualFragment;
	}

}
