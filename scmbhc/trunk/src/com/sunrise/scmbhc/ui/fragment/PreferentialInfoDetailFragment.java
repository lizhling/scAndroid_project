package com.sunrise.scmbhc.ui.fragment;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.PreferentialInfo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("ValidFragment")
public class PreferentialInfoDetailFragment extends BaseFragment {

	private PreferentialInfo mPreferentialInfo;
	
	public PreferentialInfoDetailFragment(PreferentialInfo mPreferentialInfo) {
		super();
		this.mPreferentialInfo = mPreferentialInfo;
	}


	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mBaseActivity.setTitle(mPreferentialInfo.getTitle());
		mBaseActivity.setLeftButtonVisibility(View.VISIBLE);
	}
	
	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.preferentialinfo_detail, container, false);
		WebView webView=(WebView) view.findViewById(R.id.preferntila_web);
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return true;
			}
		});  
		webView.loadUrl(mPreferentialInfo.getDetailsUrl());
		return view;
	}

	/* 
	 * 功能: 为用户行为提供页面名称
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 * 
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.PreferentialInfoDetailFragment;
	}

}
