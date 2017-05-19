package com.sunrise.scmbhc.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.ui.activity.BaseActivity;
import com.sunrise.scmbhc.ui.view.CornerListView;

/**  
 *   
 * @Project: scmbhc  
 * @ClassName: ShowDetailFragment  
 * @Description: 显示详细信息页面
 * @Author qinhubao  
 * @CreateFileTime: 2014年1月15日 下午8:13:51  
 * @Modifier: qinhubao  
 * @ModifyTime: 2014年1月15日 下午8:13:51  
 * @ModifyNote: 
 * @version   
 *   
 */
public class ShowDetailFragment extends BaseFragment  {
/*public class ManualActivity extends BaseActivity {*/


	  
    private String function = null;
  
  
    public void onStart() {
		super.onStart();
		BaseActivity baseActivity = (BaseActivity) getActivity();
		baseActivity.setLeftButtonVisibility(View.VISIBLE);
		if(function != null && function.length() > 0 ) {
			baseActivity.setTitle(function);
		} else {
			baseActivity.setTitle(getResources().getString(R.string.more));
		}
	}
  

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.detail_info, container, false);
		WebView webView = (WebView) view.findViewById(R.id.wv_detailinfo);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDefaultTextEncodingName("utf-8");
		
	/*	TextView tv_title = (TextView)view.findViewById(R.id.tv_msg_title);
		TextView tv_date = (TextView)view.findViewById(R.id.tv_datetimeinfo);
		TextView tv_details = (TextView)view.findViewById(R.id.tv_detailinfo);*/
		// 获取传来的参数
		Bundle bundle = getArguments();
		String detailinfo = bundle.getString("details");
		if (detailinfo != null && detailinfo.length() > 0){
			webView.loadData(detailinfo,  "text/html; charset=UTF-8", null);
		} else {
			webView.loadData("<html>这是一段HTML的代码</html>",
					"text/html; charset=UTF-8", null);
			
		}
		function = bundle.getString("function");
		/*if (function != null && function.length() > 0 ) {
			if (function.equals(ManualFragment.HELP_FUN)){
				tv_date.setVisibility(View.GONE);
			} else if (function.equals(ManualFragment.NOTICE_FUN)) {
				tv_date.setVisibility(View.VISIBLE);
				String time = bundle.getString("datetime");
				tv_date.setText(time);
			} else if (function.equals(ManualFragment.PROBLEM_FUN)) {
				tv_date.setVisibility(View.GONE);
			}
		}
		String title = bundle.getString("title");
		if (title != null && title.trim().length() > 0) {
			tv_title.setText(bundle.getString("title"));
		}
		String detail = bundle.getString("detail");
		if (detail != null && detail.trim().length() > 0) {
			tv_details.setText(detail);
		}*/
		return view;
	}

	/* 
	 * 功能: 为用户行为提供页面名称
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 * 
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.ShowDetailFragment;
	}

}
