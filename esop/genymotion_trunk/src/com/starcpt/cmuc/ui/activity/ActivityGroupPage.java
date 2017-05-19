package com.starcpt.cmuc.ui.activity;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ViewAnimator;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;

public class ActivityGroupPage extends ActivityGroup {
	//private String TAG="ServerActivityGroup";
	private LocalActivityManager mLocalActivityManager;
	private Stack<String> mActivityIds=new Stack<String>();
	private ViewAnimator mLocalViewAnimator;
	private Intent mFirstIntent;
	//private CmucApplication cmucApplication;
	
	@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			//cmucApplication=(CmucApplication) getApplicationContext();
			CommonActions.setScreenOrientation(this);
			setContentView(R.layout.activity_group);
			mLocalActivityManager=getLocalActivityManager();
			mLocalViewAnimator=(ViewAnimator) findViewById(R.id.animator);
			initFirstIntent();
			startActivity(mFirstIntent);
			CommonActions.addActivity(this);
		}

	private void initFirstIntent() {
		Intent from=getIntent();		
		String title=from.getStringExtra(CmucApplication.TITLE_EXTRAL);
		boolean isSearchBusiness=from.getBooleanExtra(CmucApplication.SEARCH_MENU_EXTRAL, false);
		if(isSearchBusiness){
			mFirstIntent=new Intent(this,SearchBusinessActivity.class);
		}else{
			mFirstIntent=new Intent(this,BusinessActivity.class);
		}	
		mFirstIntent.putExtra(CmucApplication.PAGE_ID_EXTRAL, CmucApplication.APP_MENU_FIRST_PAGE_ID);
		mFirstIntent.putExtra(CmucApplication.TITLE_EXTRAL, title);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
	    if(keyCode==KeyEvent.KEYCODE_BACK){
			doBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	public void superStartActivity(Intent paramIntent){
		super.startActivity(paramIntent);
	}

	public void startActivity(Intent paramIntent){
		 String activityId=paramIntent.getStringExtra(CmucApplication.PAGE_ID_EXTRAL);
		 mActivityIds.push(activityId);
		 Window window=mLocalActivityManager.startActivity(activityId, paramIntent);
		 View localView=window.getDecorView();
		 if(mLocalViewAnimator!=null){
		 mLocalViewAnimator.addView(localView);
		 int i1=mActivityIds.size()-1; 
		 mLocalViewAnimator.setDisplayedChild(i1);
		 }
	}

	public int getChildActivityCount(){
		return mActivityIds.size();
	}
	
	public  boolean destroy(ActivityGroup activityGroup, String id) {
		final LocalActivityManager activityManager = activityGroup
				.getLocalActivityManager();
		if (activityManager != null) {
			View localView=activityManager.destroyActivity(id, false).getDecorView();
			 if(mLocalViewAnimator!=null){
				 mLocalViewAnimator.removeView(localView);
			}
			try {
				final Field mActivitiesField = LocalActivityManager.class
						.getDeclaredField("mActivities");
				if (mActivitiesField != null) {
					mActivitiesField.setAccessible(true);
					@SuppressWarnings("unchecked")
					final Map<String, Object> mActivities = (Map<String, Object>) mActivitiesField
							.get(activityManager);
					if (mActivities != null) {
						mActivities.remove(id);
					}
					final Field mActivityArrayField = LocalActivityManager.class
							.getDeclaredField("mActivityArray");
					if (mActivityArrayField != null) {
						mActivityArrayField.setAccessible(true);
						@SuppressWarnings("unchecked")
						final ArrayList<Object> mActivityArray = (ArrayList<Object>) mActivityArrayField
								.get(activityManager);
						if (mActivityArray != null) {
							for (Object record : mActivityArray) {
								final Field idField = record.getClass()
										.getDeclaredField("id");
								if (idField != null) {
									idField.setAccessible(true);
									final String _id = (String) idField
											.get(record);
									if (id.equals(_id)) {
										mActivityArray.remove(record);
										break;
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	public void doBack(){
		if(mActivityIds.size()>1){
			String activityId=mActivityIds.pop();
			destroy(this, activityId);
		} else {
			CommonActions.exitAppDialog(getParent());
		}
	 }
	
	public Stack<String> getActivityIds() {
		return mActivityIds;
	}
	
	public void doBack(int level){
		for(int i=level;i<mActivityIds.size()-level;i++){
			String activityId=mActivityIds.pop();
			destroy(this, activityId);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
