package com.starcpt.cmuc.ui.activity;
import java.util.ArrayList;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.adapter.PopMenuAdapter;
import com.starcpt.cmuc.model.Channel;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.ui.skin.ViewEnum;
import com.starcpt.cmuc.ui.view.MyTab;
import com.starcpt.cmuc.ui.view.MyTab.ItemSelectedListener;
import com.starcpt.cmuc.utils.MenuUtils;


public class MainTabActivity extends TabActivity {
//private final static String TAG="MainTabActivity";
//for tab 
public static int mCurrentTabIndex=0;
public static final String BROADCAST_ACTION = "com.starcpt.esop.LOGIN_LOGOUT_STATE_ACTION";
private MyTab mBottomTab;
private TabHost mTabHost;
private ArrayList<Channel> mCurrentTapChannels;
private BottomTabAdapter mBottomTabAdaptor;
private PopMenuAdapter mAdapter;
private ItemSelectedListener itemSelectedListener = new ItemSelectedListener() {
	@Override
	public void onItemSelected(int position ,View currentItem, View lastItem) {
		updateBottomTabLabelSelected(position, currentItem, lastItem);
	}
};

//for function
private Menu mMenu;
private PopupWindow mPopMenu;
private View mMainScreen;

private LoginBroadcast mLoginBroadcast;
private CmucApplication cmucApplication;

@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	cmucApplication=(CmucApplication) getApplicationContext();
	if(!cmucApplication.isApplicationRunning()){
		startActivity(new Intent(this,StartActivity.class));
		finish();
		return;
	}
	mCurrentTapChannels=cmucApplication.getTapChannels();
	CommonActions.setScreenOrientation(this);
	setContentView(R.layout.main_tab);
	registerSkinChangedReceiver();
	initTabView();
	updateChannels();
	createPopMenu();	
	mLoginBroadcast = new LoginBroadcast();
	IntentFilter filter = new IntentFilter(MainTabActivity.BROADCAST_ACTION);
	registerReceiver(mLoginBroadcast, filter);
	CommonActions.addActivity(this);
	setSkin();
}

private BroadcastReceiver mSkinChangedReceiver = new BroadcastReceiver() {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		System.out.println("mSkinChangedReceiver:"+intent.getAction());
		setSkin();
	}
};

private void setSkin(){
	SkinManager.setSkin(this,null, ViewEnum.MainTabActivity);
}

private void registerSkinChangedReceiver(){
	IntentFilter filter = new IntentFilter();
	filter.addAction(SkinManager.SKIN_CHANGED_RECEIVER);
	registerReceiver(mSkinChangedReceiver, filter);
}

private void unRegisterSkinChangedReceiver(){
	unregisterReceiver(mSkinChangedReceiver);
}

private void initTabView(){
	mBottomTab=(MyTab) findViewById(R.id.bottomTab);
	mBottomTab.setmItemSelectedListener(itemSelectedListener);
	mBottomTabAdaptor=new BottomTabAdapter(this, mCurrentTapChannels);
	mBottomTab.setAdapter(mBottomTabAdaptor);
}

private void updateBottomTabLabelSelected(int position, View currentView, View lastView){
	if(position==mCurrentTapChannels.size()-1){
	    showMenu();
	}
	else{
	mCurrentTabIndex=position;
	setCurrentTab(position);
	}
	RelativeLayout currentBgView=(RelativeLayout) currentView.findViewById(R.id.bottom_focus_bg);
	currentBgView.setBackgroundResource(R.drawable.bottom_tab_label_selected_bg);	
	RelativeLayout lastBgView=(RelativeLayout) lastView.findViewById(R.id.bottom_focus_bg);
	lastBgView.setBackgroundDrawable(null);	
}

private void recoverTabIndex(){
	View currentView=mBottomTab.getChildAt((mCurrentTapChannels.size()-1));
	View lastView=mBottomTab.getChildAt(mCurrentTabIndex);
	mBottomTab.updateLastPosition(mCurrentTabIndex);
	RelativeLayout currentBgView=(RelativeLayout) currentView.findViewById(R.id.bottom_focus_bg);
	currentBgView.setBackgroundDrawable(null); 
	RelativeLayout lastBgView=(RelativeLayout) lastView.findViewById(R.id.bottom_focus_bg);
	lastBgView.setBackgroundResource(R.drawable.bottom_tab_label_selected_bg);	
}

private void markMoreTab(){
	View currentView=mBottomTab.getChildAt((mCurrentTapChannels.size()-1));
	View lastView=mBottomTab.getChildAt(mCurrentTabIndex);
	RelativeLayout currentBgView=(RelativeLayout) currentView.findViewById(R.id.bottom_focus_bg);
	currentBgView.setBackgroundResource(R.drawable.bottom_tab_label_selected_bg);	 
	RelativeLayout lastBgView=(RelativeLayout) lastView.findViewById(R.id.bottom_focus_bg);
	lastBgView.setBackgroundDrawable(null);
}

private void setCurrentTab(int index){
	Channel channel=mCurrentTapChannels.get(index);
	String title=channel.getTitle();
	mTabHost.setCurrentTabByTag(title);
}	

private void updateTabs() {
	mTabHost = getTabHost();
	mTabHost.setCurrentTab(0);
	mCurrentTabIndex=0;
	mTabHost.clearAllTabs();
	for(int i=0;i<mCurrentTapChannels.size();i++){
	    Channel channel=mCurrentTapChannels.get(i);
		int channelId=channel.getId();
		String title=channel.getTitle();
		Intent intent=null;
		if(channelId!=-1){
			switch(channelId){
			case 0:
				intent=new Intent(this, ActivityGroupPage.class);
				break;
			case 1:
				intent=new Intent(this, BusinessActivity.class);
				intent.putExtra(CmucApplication.COLLECTION_MENU_EXTRAL, true);
				break;
			case 2:
				intent=new Intent(this, ActivityGroupPage.class);
				intent.putExtra(CmucApplication.SEARCH_MENU_EXTRAL, true);
				break;
			case 3:
				intent=new Intent(this,MessageListActivity.class);
				break;
			}
		}
		if(intent!=null){
			if(title.equals(getString(R.string.home)))
			intent.putExtra(CmucApplication.TITLE_EXTRAL, cmucApplication.getAppName());
			else
			intent.putExtra(CmucApplication.TITLE_EXTRAL, title);
			mTabHost.addTab(mTabHost.newTabSpec(title).setIndicator(title).setContent(intent));
		}
	}
}

private void initBottomTabSelected(){
	//mBottomTab.notifyView();
	setDefaultTabView(mCurrentTabIndex);
}

private void setDefaultTabView(int index){
	View currentView = mBottomTab.getChildAt(index);
	RelativeLayout view=(RelativeLayout) currentView.findViewById(R.id.bottom_focus_bg);
	view.setBackgroundResource(R.drawable.bottom_tab_label_selected_bg);	 
}

private void updateChannels(){
	updateTabs();
	initBottomTabSelected();
}

private void createSwitchUser() {
	CommonActions.createTwoBtnMsgDialog(this,
			getString(R.string.menu_switch_user), 
			getString(R.string.switch_user_question), 
			null, null, 
			new CommonActions.OnTwoBtnDialogHandler() {
				
				@Override
				public void onPositiveHandle(Dialog dialog, View v) {
					// TODO Auto-generated method stub
					CommonActions.logout(new Handler(){
						@Override
						public void handleMessage(Message msg) {
							goLogin();
						}
					}, MainTabActivity.this);
					dialog.dismiss();
				}
				
				@Override
				public void onNegativeHandle(Dialog dialog, View v) {
					dialog.dismiss();
				}
			},
			false
	);
}

private void createPopMenu(){
	mMenu=MenuUtils.createMenu(this, R.menu.pop_menu);
	mMainScreen=findViewById(android.R.id.tabhost);
	View view = getLayoutInflater().inflate(R.layout.function_menu, null);
	GridView gridView=(GridView) view.findViewById(R.id.menu_gridview);
	gridView.setOnItemClickListener(new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View v,
				int position, long id) {
			// TODO Auto-generated method stub
			switch(position){
			case 0:
				goSetting();
				break;
			case 1:
				createSwitchUser();
			    break;
			case 2:
				visitWeb();
				break;
			case 3:
				CommonActions.exitAppDialog(MainTabActivity.this);
				break;
			}	
			showMenu();
		}

	});
	mAdapter=new PopMenuAdapter(this,mMenu);
	gridView.setAdapter(mAdapter);
	mPopMenu = new PopupWindow(view,LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
	mPopMenu.setBackgroundDrawable(new ColorDrawable(0));
	mPopMenu.setOutsideTouchable(true);
	mPopMenu.setFocusable(true);
  	view.setOnKeyListener(new OnKeyListener() {
  		
		@Override
		public boolean onKey(View arg0, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			 if ((keyCode == KeyEvent.KEYCODE_MENU)){
				 dispatchKeyEvent(event);
				 return false;
			 }
			return false;
		}
	});
  	
  	mPopMenu.setOnDismissListener(new OnDismissListener() {
		
		@Override
		public void onDismiss() {
		 recoverTabIndex();
		}
	});
}


private void visitWeb() {
	Intent intent=new Intent(MainTabActivity.this, BrowserActivity.class);
	startActivity(intent);
}

private void goSetting(){
	Intent intent=new Intent(MainTabActivity.this, SettingActivity.class);
	intent.putExtra(CmucApplication.TITLE_EXTRAL, getString(R.string.menu_setting));
	startActivity(intent);
}

/*private void goBluetoothAdapter() {
	Intent intent=new Intent(MainTabActivity.this, BlueAdapterActivity.class);
	intent.putExtra(ListItemBean.TITLE, getString(R.string.device_list));
	startActivity(intent);
}*/


private void goLogin() {
	// TODO Auto-generated method stub
	CommonActions.clearUserInfo();
	Intent intent = new Intent(this,LoginActivity.class);
	startActivity(intent);
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
public boolean onPrepareOptionsMenu(Menu menu) {
showMenu();
return super.onPrepareOptionsMenu(menu);
}

private void showMenu(){
	if(!mPopMenu.isShowing()){
	mPopMenu.showAtLocation(mMainScreen, Gravity.BOTTOM, 0,mBottomTab.getHeight());
	markMoreTab();
	}
	else{
	mPopMenu.dismiss();
	}
}

@Override
protected void onDestroy() {
	try {
		unregisterReceiver(mLoginBroadcast);
		unRegisterSkinChangedReceiver();
	} catch (Exception e) {
		e.printStackTrace();
	}	
	super.onDestroy();
}

class BottomTabAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	
	public BottomTabAdapter(Context context,ArrayList<Channel> channels) {
		this.inflater=LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mCurrentTapChannels.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mCurrentTapChannels.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		Channel channel = mCurrentTapChannels.get(position);
		Bitmap normalIcon=channel.getIconNormal();
		if (convertView==null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.bottom_tab_item,null);
			holder.imageView = (ImageView) convertView.findViewById(R.id.botttom_tab_imageview);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder)convertView.getTag();
		}
		if(normalIcon!=null)
		holder.imageView.setImageBitmap(normalIcon);
		return convertView;
	}
}

class ViewHolder{
	ImageView imageView;
}

class LoginBroadcast extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mAdapter.notifyDataSetChanged();
	}
	
}

}
