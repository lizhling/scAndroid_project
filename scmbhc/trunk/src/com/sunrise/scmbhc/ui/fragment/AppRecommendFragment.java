package com.sunrise.scmbhc.ui.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.adapter.BusinessListAdapter;
import com.sunrise.scmbhc.adapter.RecommandAdapter;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.ui.activity.SingleFragmentActivity;

public class AppRecommendFragment extends BaseFragment implements Observer {

	public void onStart() {
		super.onStart();
		mBaseActivity.setLeftButtonVisibility(View.VISIBLE);
		mBaseActivity.setTitle(R.string.app_recommend_text);
	};
/**/
/*	R.drawable.icon_recommend_andread,R.drawable.icon_recommend_andread,
	R.drawable.icon_recommend_andread,R.drawable.icon_recommend_andread,
	R.drawable.icon_recommend_andread,R.drawable.icon_recommend_andread,
	R.drawable.icon_recommend_andread,R.drawable.icon_recommend_andread,
	R.drawable.icon_recommend_andread,R.drawable.icon_recommend_andread,
	R.drawable.icon_recommend_andread,R.drawable.icon_recommend_andread,
	R.drawable.icon_recommend_andread,R.drawable.icon_recommend_andread,
	R.drawable.icon_recommend_andread,R.drawable.icon_recommend_andread,
	R.drawable.icon_recommend_andread*/
	int bitmaps[] = {R.drawable.icon_recommend_mm, R.drawable.icon_recommend_andread, R.drawable.icon_recommend_andgame, R.drawable.icon_recommend_andmove,
			R.drawable.icon_recommend_andfilm, R.drawable.icon_recommend_andcontacts, R.drawable.icon_recommend_know_eachother,
			R.drawable.icon_recommend_color_cloud, R.drawable.icon_recommend_flymsg, R.drawable.icon_recommend_migo_music, R.drawable.icon_recommend_andlife,
			R.drawable.icon_recommend_andmap, R.drawable.icon_recommend_139email, R.drawable.icon_recommend_migo_love_sing, R.drawable.icon_recommend_follow_e,
			R.drawable.icon_recommend_weibo_icon, R.drawable.icon_recommend_migo_ring };
	String icons[] = { "MM", "和阅读", "和游戏", "和动漫", "和视频", "和通讯录", "灵犀", "彩云", "飞信", "咪咕音乐", "和生活", "和地图", "139邮箱", "咪咕爱唱", "随e行", "新浪微博4G版", "咪咕铃声 " };

	String urls[] = { "http://a.10086.cn/pams2/s.do?gId=300000863435&c=172&j=l&p=72&src=90.510007.002",
			"http://a.10086.cn/pams2/s.do?gId=300000013959&c=172&j=l&p=72&src=90.510007.002",
			"http://a.10086.cn/pams2/s.do?gId=300000034255&c=172&j=l&p=72&src=90.510007.002",
			"http://a.10086.cn/pams2/s.do?gId=300002437539&c=172&j=l&p=72&src=90.510007.002",
			"http://a.10086.cn/pams2/s.do?gId=300000008459&c=172&j=l&p=72&src=90.510007.002",
			"http://a.10086.cn/pams2/s.do?gId=300002575008&c=172&j=l&p=72&src=90.510007.002",
			"http://a.10086.cn/pams2/s.do?gId=300002734449&c=172&j=l&p=72&src=90.510007.002",
			"http://a.10086.cn/pams2/s.do?gId=300002584247&c=172&j=l&p=72&src=90.510007.002",
			"http://a.10086.cn/pams2/s.do?gId=300000004294&c=172&j=l&p=72&src=90.510007.002",
			"http://a.10086.cn/pams2/s.do?gId=300000004296&c=172&j=l&p=72&src=90.510007.002",
			"http://a.10086.cn/pams2/s.do?gId=300002797930&c=172&j=l&p=72&src=90.510007.002",
			"http://a.10086.cn/pams2/s.do?gId=300000004151&c=172&j=l&p=72&src=90.510007.002",
			"http://a.10086.cn/pams2/s.do?gId=300001502369&c=172&j=l&p=72&src=90.510007.002",
			"http://a.10086.cn/pams2/s.do?gId=300002785802&c=172&j=l&p=72&src=90.510007.002",
			"http://a.10086.cn/pams2/s.do?gId=300001141606&c=172&j=l&p=72&src=90.510007.002",
			"http://a.10086.cn/pams2/s.do?gId=300002721227&c=172&j=l&p=72&src=90.510007.002",
			"http://a.10086.cn/pams2/s.do?gId=300002757446&c=172&j=l&p=72&src=90.510007.002", };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.app_recommend, container, false);
		GridView gv = (GridView) view.findViewById(R.id.apps_info);
		List<BusinessMenu> list = getData();
		RecommandAdapter adapter = new RecommandAdapter(getActivity(), list);
//		MySideAdapter adapter = new MySideAdapter(getActivity(), list);
		gv.setAdapter(adapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				//ImageView img = (ImageView)view.findViewById(R.id.business_menu_image);
				Intent intent = new Intent(mBaseActivity, SingleFragmentActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				TextView tv = (TextView)view.findViewById(R.id.business_menu_name);
				intent.putExtra(ExtraKeyConstant.KEY_TITLE, getString(R.string.app_recommend_text));
				intent.putExtra(ExtraKeyConstant.KEY_FRAGMENT, WebViewFragment.class);
				BusinessMenu businessMenu = new BusinessMenu();
				businessMenu.setServiceUrl(urls[position]);
				if (tv != null) {
					businessMenu.setName(tv.getText().toString());
				} else {
					businessMenu.setName(getString(R.string.app_recommend_text));
				}
				Bundle bundle = new Bundle();
				bundle.putParcelable(ExtraKeyConstant.KEY_BUSINESS_INFO, businessMenu);
				intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, bundle);
				intent.putExtra(ExtraKeyConstant.KEY_FINISH_ACTIVITY, true);
				mBaseActivity.setLeftButtonVisibility(View.VISIBLE);
				mBaseActivity.startActivity(intent);// 应用推荐
				
			}
		});
		return view;
	}

	private List<BusinessMenu> getData() {
		List<BusinessMenu> list = new ArrayList<BusinessMenu>();
		for (int i = 0; i < icons.length; i++) {
			BusinessMenu menu = new BusinessMenu();
			menu.setName(icons[i]);
			menu.setIconRes(bitmaps[i]);
			list.add(menu);
		}

		return list;
	}

	@Override
	public void update(Observable arg0, Object arg1) {

	}

	protected void onBack() {
		mBaseActivity.finish();
	}

	private class MySideAdapter extends BusinessListAdapter {
		private List<BusinessMenu> list;

		public MySideAdapter(Context context, List<BusinessMenu> list) {
			super(context, list);
			this.list = list;
		}

		@Override
		public View getView(int position, View view, ViewGroup container) {
			View viewgroup = LayoutInflater.from(getContext()).inflate(R.layout.item_business_list2, null);

			BusinessMenu item = (BusinessMenu) getItem(position);

			TextView title = (TextView) viewgroup.findViewById(R.id.title);
			title.setText(item.getName());
			title.setTextColor(Color.BLACK);

			ImageView icon = (ImageView) viewgroup.findViewById(R.id.icon1);
			icon.setImageResource(item.getIconRes());
			return viewgroup;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

	}
	/* 
	 * 功能: 为用户行为提供页面名称
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 * 
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.AppRecommendFragment;
	}
}
