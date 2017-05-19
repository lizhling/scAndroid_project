package com.starcpt.cmuc.ui.activity;
import java.util.ArrayList;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.adapter.GalleryAdapter;
import com.starcpt.cmuc.model.WebViewWindow;
import com.starcpt.cmuc.ui.view.UGallery;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MultiwindowActivity extends Activity{
	private UGallery mGallery;
	private GalleryAdapter mGalleryAdapter;	
	private TextView mTextView;
	private LinearLayout mWebWindowGuidePanel;
	private int mPageCount;
	private int mSelectItemIndex=0;
	private ArrayList<ImageView> mImageViews=new ArrayList<ImageView>();
	private GalleryAdapter.OnItemRemoveListener mItemRemoveListener=new GalleryAdapter.OnItemRemoveListener(){
		@Override
		public void onItemRemove(int position) {
			removeGuideIndex(position);
		}
		
	};
	public CmucApplication cmucApplication;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		cmucApplication=(CmucApplication) getApplicationContext();
		CommonActions.setScreenOrientation(this);
		setContentView(R.layout.multiwindow);
		mWebWindowGuidePanel=(LinearLayout) findViewById(R.id.web_window_guide_panel);
		mPageCount=cmucApplication.getWebViewWindows().size();
		
		mTextView=(TextView) findViewById(R.id.web_window_title);
		WebViewWindow webViewWindow=cmucApplication.getWebViewWindows().get(0);
		if(webViewWindow!=null){
			mTextView.setText(webViewWindow.getTitle());
		}
		
		mGallery=(UGallery) findViewById(R.id.multiwindow_gallery);
		mGalleryAdapter=new GalleryAdapter(cmucApplication.getWebViewWindows(), this);
		mGalleryAdapter.setOnItemRemoveListener(mItemRemoveListener);
		mGallery.setAdapter(mGalleryAdapter);
		mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int position, long id) {
				WebViewWindow webViewWindow=cmucApplication.getWebViewWindows().get(position);
				if(webViewWindow!=null)
				mTextView.setText(webViewWindow.getTitle());
				if(mPageCount>1){
					updateGuideIndex(position);
				}
					mSelectItemIndex=position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		mGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				WebViewWindow webViewWindow=cmucApplication.getWebViewWindows().get(position);
				Intent intent=new Intent(MultiwindowActivity.this, WebViewActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra(CmucApplication.CONTENT_EXTRAL, webViewWindow.getUrl());
				intent.putExtra(CmucApplication.MENU_ID, webViewWindow.getMenuId());
				intent.putExtra(CmucApplication.APP_TAG_EXTRAL,webViewWindow.getAppTag());
				intent.putExtra(CmucApplication.BUSINESS_ID_EXTRAL, webViewWindow.getBusinessId());
				intent.putExtra(CmucApplication.CHILD_VERSION_EXTRAL, webViewWindow.getChildVersion());
				intent.putExtra(CmucApplication.IS_BUSINESS_WEB_EXTRAL,true);
				intent.putExtra(CmucApplication.TITLE_EXTRAL, webViewWindow.getTitle());
				startActivity(intent);
				finish();
				mGalleryAdapter.recycleImage();	
			}
		});
		
		initGuideIndex();
		updateGuideIndex(mSelectItemIndex);
		CommonActions.addActivity(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(CmucApplication.sNeedShowLock){
			CommonActions.showLockScreen(this);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}
	
	private void initGuideIndex() {
		if(mPageCount>1){
			for(int i=0;i<mPageCount;i++){
				ImageView view=new ImageView(this);
				view.setImageResource(R.drawable.multiwindow_tabindicator_unselected);			
				LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				view.setLayoutParams(layoutParams);
				mWebWindowGuidePanel.addView(view);
				mImageViews.add(view);
			}
		}
	}
	
	private void removeGuideIndex(int position) {
		int size = cmucApplication.getWebViewWindows().size();
		if (size <= 0) {
			finish();
		}else{
			if(position==size){
				mSelectItemIndex=position-1;
			}		
			if (size == 1) {
				mImageViews.clear();
				mWebWindowGuidePanel.removeAllViews();
			} else {
				ImageView imageView = mImageViews.get(position);
				mImageViews.remove(position);
				mWebWindowGuidePanel.removeView(imageView);			
				updateGuideIndex(mSelectItemIndex);
			}
			updateItemTitle(mSelectItemIndex);
		}
	}

	private void updateItemTitle(int index) {
		try {
			String nextTitle = cmucApplication.getWebViewWindows().get(index).getTitle();
			mTextView.setText(nextTitle);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void updateGuideIndex(int index){
		for(int i=0;i<mImageViews.size();i++){
			ImageView view=mImageViews.get(i);
			if(i==index){
				view.setImageResource(R.drawable.multiwindow_tabindicator_selected);
			}else{
				view.setImageResource(R.drawable.multiwindow_tabindicator_unselected);
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			mGalleryAdapter.recycleImage();
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
