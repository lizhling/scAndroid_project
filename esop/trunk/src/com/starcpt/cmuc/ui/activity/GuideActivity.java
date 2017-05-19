package com.starcpt.cmuc.ui.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.adapter.ViewPagerAdapter;

public class GuideActivity extends Activity {
	private ArrayList<View> mViews=new ArrayList<View>();
	private ViewPager mGuidePager;
	private ArrayList<Drawable> mGuideDrawabels=new ArrayList<Drawable>();
	public final static int AFTER_START_DISPLAY_GUIDE_TYPE=1;
	public final static int DISPLAY_FUNCTION_GUIDE_TYPE=2;
	public final static int DISPLAY_BUSINESS_GUIDE_TYPE=3;
	public final static int DISPLAY_OPER_GUIDE_TYPE=4;
	private int mDisplayGuideType;
	private boolean mIsBusinessGuideUpdated;
	private CmucApplication cmucApplication;
	private Button mExperienceButton;
	private LinearLayout mGuidePanel;
	private ArrayList<ImageView> mIndexViews=new ArrayList<ImageView>();
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.guide);
	cmucApplication=(CmucApplication) CmucApplication.sContext;
	Intent intent=getIntent();
	mDisplayGuideType=intent.getIntExtra(CmucApplication.DISPLAY_GUIDE_TYPE, DISPLAY_FUNCTION_GUIDE_TYPE);
	mIsBusinessGuideUpdated=intent.getBooleanExtra(CmucApplication.IS_BUSINESS_GUIDES_UPDATED, false);
	mGuideDrawabels.clear();
	mViews.clear();
	initDrawables();
	for(Drawable drawable:mGuideDrawabels){
		ImageView imageView=new ImageView(this);
		imageView.setBackgroundDrawable(drawable);
		imageView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		mViews.add(imageView);
	}
	mGuidePanel=(LinearLayout) findViewById(R.id.guide_panel);
	mExperienceButton=(Button) findViewById(R.id.exp_button);
	mExperienceButton.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(GuideActivity.this,LoginActivity.class);
			startActivity(intent);
			finish();
		}
	});
	mGuidePager = (ViewPager) findViewById(R.id.guide_pager);
	mGuidePager.setAdapter(new ViewPagerAdapter(mViews));
	mGuidePager.setCurrentItem(0);
	mGuidePager.setOnPageChangeListener(new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int position) {
			if(mDisplayGuideType==AFTER_START_DISPLAY_GUIDE_TYPE||mIsBusinessGuideUpdated){
				if(position==mGuideDrawabels.size()-1){
					mExperienceButton.setVisibility(View.VISIBLE);
				}else{
					mExperienceButton.setVisibility(View.INVISIBLE);
				}
			}
			if(mGuideDrawabels.size()>1)
			updateGuideIndex(position);
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}
	});	
	initGuideIndex();
	if(mIsBusinessGuideUpdated){
		if(mGuideDrawabels.size()==1){
			mExperienceButton.setVisibility(View.VISIBLE);
		}
	}
}

@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	if(CmucApplication.sNeedShowLock){
		CommonActions.showLockScreen(this);
	}
}
private void initDrawables() {
	switch(mDisplayGuideType){
	case AFTER_START_DISPLAY_GUIDE_TYPE:
			mGuideDrawabels.addAll(cmucApplication.getFunctionGuideDrawables());
			mGuideDrawabels.addAll(cmucApplication.getBusinessGuideDrawables());
			mGuideDrawabels.addAll(cmucApplication.getOpreGuideDrawables());
	break;
	case DISPLAY_FUNCTION_GUIDE_TYPE:
		mGuideDrawabels=cmucApplication.getFunctionGuideDrawables();
		break;
	case DISPLAY_BUSINESS_GUIDE_TYPE:
		mGuideDrawabels=cmucApplication.getBusinessGuideDrawables();
		break;
	case DISPLAY_OPER_GUIDE_TYPE:
		mGuideDrawabels=cmucApplication.getOpreGuideDrawables();
		break;
	}
}

	private void initGuideIndex() {
		int size = mGuideDrawabels.size();
		if (size > 1) {
			for (int i = 0; i < size; i++) {
				ImageView view = new ImageView(this);
				view.setImageResource(R.drawable.guide_index_focus);
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				view.setLayoutParams(layoutParams);
				mGuidePanel.addView(view);
				mIndexViews.add(view);
			}
			updateGuideIndex(0);
		}
	}

private void updateGuideIndex(int index){
	int size=mGuideDrawabels.size();
	for(int i=0;i<size;i++){
		ImageView view=mIndexViews.get(i);
		if(i==index){
			view.setImageResource(R.drawable.guide_index_focus);
		}else{
			view.setImageResource(R.drawable.guide_index_normal);
		}
	}
}

}
