package com.starcpt.cmuc.ui.fragment;

import java.util.ArrayList;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.adapter.ListViewAdapter;
import com.starcpt.cmuc.exception.business.BusinessException;
import com.starcpt.cmuc.exception.http.HttpException;
import com.starcpt.cmuc.model.AppMenu;
import com.starcpt.cmuc.model.Item;
import com.starcpt.cmuc.model.bean.AppMenuBean;
import com.starcpt.cmuc.task.GenericTask;
import com.starcpt.cmuc.task.TaskListener;
import com.starcpt.cmuc.task.TaskParams;
import com.starcpt.cmuc.task.TaskResult;
import com.starcpt.cmuc.ui.activity.BusinessActivity;
import com.starcpt.cmuc.ui.activity.CommonActions;
import com.starcpt.cmuc.ui.activity.LoginActivity;
import com.starcpt.cmuc.ui.activity.SettingActivity;
import com.starcpt.cmuc.ui.activity.WebViewActivity;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.ui.skin.ViewEnum;
import com.starcpt.cmuc.ui.view.BubbleEditText;
import com.sunrise.javascript.utils.ActivityUtils;

public class SearchBusinessListFragment extends ListFragment {
	public  ArrayList<Item> sSearchBusinesses=new ArrayList<Item>();
	private TextView mTitleView;
	private Button mBackView;
	private ImageView mCommonLineView;
	private WebViewFragment mWebViewFragment;
	private TextView mSearchReslutLabel;
	private TextView mNoSearchResult;
	private BubbleEditText mSearchKeyWordView;
	private ImageButton mSearchButton;
	private Activity mActivity;
	private FragmentManager mFragmentManager; 
	private String mSearchKeyWord;
	private ListViewAdapter mListViewAdapter;
	private View mCurrView;
	private BroadcastReceiver mScreenDirectionReceiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			if(action.equals(SettingActivity.CHANGE_SCERRN_DIRECTION_ACTION)){
				int screenDirection=intent.getIntExtra(SettingActivity.SCREEN_DIRECTION_EXTRAL, -1);
				if(screenDirection==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
					RemoveWebViewFragment();
				}else{
					addWebViewFragment();
				}

			}
		}
	};
	
	private ProgressDialog mSearchProgressDialog;
	private SearchBusinessTask mSearchBusinessTask;
	private TaskListener mSearchBusinessListener=new TaskListener() {
		
		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPreExecute(GenericTask task) {
			mSearchProgressDialog=ProgressDialog.show(mActivity.getParent()!=null?mActivity.getParent():mActivity, null, getString(R.string.searching), true, false);
			mNoSearchResult.setVisibility(View.GONE);
		}
		
		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			mSearchProgressDialog.dismiss();
			if(result==TaskResult.OK){
				onSearchBusiness();
			}else{
				handleException(task, mActivity);
			}
			
		}

		private void onSearchBusiness() {
			mSearchReslutLabel.setVisibility(View.VISIBLE);
			if(sSearchBusinesses.size()<=0){
				mNoSearchResult.setVisibility(View.VISIBLE);
			}else{
				if(cmucApplication.isOpenWebViewInFragment()){
					Item firstItem=sSearchBusinesses.get(0);
					visit(firstItem);
				}
			}
			mListViewAdapter.notifyDataSetChanged();
		}
		
		@Override
		public void onCancelled(GenericTask task) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
	private void RemoveWebViewFragment() {
		if(mWebViewFragment!=null){
			FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction(); 
			fragmentTransaction.remove(mWebViewFragment);
			fragmentTransaction.commitAllowingStateLoss();
		}
	}
	
	private void addWebViewFragment() {
		if(mWebViewFragment!=null){
			FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction(); 
			fragmentTransaction.replace(R.id.search_list_panel, mWebViewFragment);
			fragmentTransaction.commitAllowingStateLoss();
		}
	}
	
	private CmucApplication cmucApplication;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity=getActivity();
		cmucApplication=(CmucApplication) mActivity.getApplicationContext();
		mFragmentManager = ((FragmentActivity) mActivity).getSupportFragmentManager();
		mActivity.registerReceiver(mScreenDirectionReceiver, new IntentFilter(SettingActivity.CHANGE_SCERRN_DIRECTION_ACTION));
		mCurrView =inflater.inflate(R.layout.search_business_list_fragment, container, false);
		mSearchKeyWordView=(BubbleEditText) mCurrView.findViewById(R.id.search_key_word);
		mSearchReslutLabel=(TextView) mCurrView.findViewById(R.id.search_reslut_label);
		if(sSearchBusinesses.size()>0){
			mSearchReslutLabel.setVisibility(View.VISIBLE);
			if(cmucApplication.isOpenWebViewInFragment())
			visit(sSearchBusinesses.get(0));
		}
		mNoSearchResult=(TextView) mCurrView.findViewById(R.id.search_no_reslut);
		mSearchButton=(ImageButton) mCurrView.findViewById(R.id.serach_business_button);
		mSearchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSearchKeyWord = mSearchKeyWordView.getText().toString().trim();
				if (TextUtils.isEmpty(mSearchKeyWord)) {
					String warnMessage = getString(R.string.key_work_is_null);
					mSearchKeyWordView.showBubbleTxtInfo(warnMessage);
					mSearchKeyWordView.requestFocus();
				}else{
					searchBusiness();
				}
				
			}

			private void searchBusiness() {
				 if(mSearchBusinessTask!=null)
					 mSearchBusinessTask.cancle();
				mSearchBusinessTask=new SearchBusinessTask();
				mSearchBusinessTask.setListener(mSearchBusinessListener);
				mSearchBusinessTask.execute();
			}
		});
		registerSkinChangedReceiver();
		initTitleBar(mCurrView);
		return mCurrView;
	}
	
	private void setSkin(){
		SkinManager.setSkin(mActivity,mCurrView, ViewEnum.SearchBusinessListFragment);
	}

 
	private BroadcastReceiver mSkinChangedReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			setSkin();
		}
	};
	
	public void onDestroyView() {
		super.onDestroyView();
		unRegisterSkinChangedReceiver();
	};
	
	private void registerSkinChangedReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(SkinManager.SKIN_CHANGED_RECEIVER);
		mActivity.registerReceiver(mSkinChangedReceiver, filter);
	}
	
	private void unRegisterSkinChangedReceiver(){
		mActivity.unregisterReceiver(mSkinChangedReceiver);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mListViewAdapter=new ListViewAdapter(mActivity, sSearchBusinesses);
		setListAdapter(mListViewAdapter);
		mCommonLineView=(ImageView) mActivity.findViewById(R.id.common_line);
		if(!cmucApplication.isIsPad())
			mCommonLineView.setVisibility(View.GONE);
		setSkin();
	}
	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Item item=sSearchBusinesses.get(position);
		visit(item);
	}
	
	
	public void visit(Item item) {
		String content = item.getContent();
		String name = item.getName();
		String appTag = item.getAppTag();
		String menuId = item.getMenuId() + "";
		String pageId=item.getAppTag()+item.getMenuId()+"";
		int menuType=item.getMenuType();
		long childVersion=item.getChildVersion();
		int businessId = item.getBusinessId();
		String childStyleName=item.getChildStyleName();
		if(menuType==Item.MENU_TYPE){
			ActivityGroup activityGroup=(ActivityGroup)mActivity.getParent();
			Intent intent=new Intent(mActivity, BusinessActivity.class);
			intent.putExtra(CmucApplication.TITLE_EXTRAL, name);
			intent.putExtra(CmucApplication.APP_TAG_EXTRAL, appTag);
			intent.putExtra(CmucApplication.MENU_ID, menuId);
		    intent.putExtra(CmucApplication.PAGE_ID_EXTRAL, pageId);
		    intent.putExtra(CmucApplication.CHILDSTYLENAME_EXTRAL,childStyleName);
		    intent.putExtra(CmucApplication.POSITIONS_EXTRAL, getString(R.string.search));  
		    intent.putExtra(CmucApplication.CHILD_VERSION_EXTRAL, childVersion);
		    activityGroup.startActivity(intent);
		}else if(menuType==Item.WEB_TYPE){
			if (cmucApplication.isOpenWebViewInFragment()) {
				mWebViewFragment = new WebViewFragment(item, true,null);
				mWebViewFragment.setTitle(name);
				FragmentTransaction fragmentTransaction = mFragmentManager
						.beginTransaction();
				fragmentTransaction.setCustomAnimations(R.anim.push_right_in,
						R.anim.push_right_out);
				fragmentTransaction.replace(R.id.search_list_panel, mWebViewFragment);
				fragmentTransaction.commitAllowingStateLoss();
			} else {
				Intent intent = new Intent(mActivity, WebViewActivity.class);
				intent.putExtra(CmucApplication.CONTENT_EXTRAL, content);
				intent.putExtra(CmucApplication.MENU_ID, menuId);
				intent.putExtra(CmucApplication.APP_TAG_EXTRAL, appTag);
				intent.putExtra(CmucApplication.BUSINESS_ID_EXTRAL, businessId);
				intent.putExtra(CmucApplication.IS_BUSINESS_WEB_EXTRAL, true);
				intent.putExtra(CmucApplication.TITLE_EXTRAL, name);
				intent.putExtra(CmucApplication.CHILD_VERSION_EXTRAL, childVersion);
				if (content != null)
					startActivity(intent);
			}
		}else if(menuType==Item.APP_TYPE){
		    CommonActions.installApp(item,mActivity,"com.sina.weibo",new CommonActions.OpenThirdAppCallBack() {			
				@Override
				public void refreshUi() {
					mListViewAdapter.notifyDataSetChanged();
				}
			});	
		}
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		mSearchKeyWordView.dismissBubble();
		super.onStop();
	}
	
	 private void handleException(GenericTask task,Context context){
			Exception exception=task.getException();
			if(task.getException()!=null)
			{
				if(exception instanceof BusinessException){
					if(((BusinessException) exception).getStatusCode()==-1){
						createLoginDialog();
					}else{
						Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		}
	 
	 public void createLoginDialog(){
			CommonActions.createTwoBtnMsgDialog(mActivity.getParent()!=null?mActivity.getParent():mActivity, 
					getString(R.string.token_time_out), 
					getString(R.string.token_time_out_question), 
					getString(R.string.confirm), 
					getString(R.string.cancel), 
					new CommonActions.OnTwoBtnDialogHandler() {
						
						@Override
						public void onPositiveHandle(Dialog dialog, View v) {
							dialog.dismiss();
							Intent intent=new Intent(mActivity, LoginActivity.class);
							intent.setAction(ActivityUtils.CMUC_LOGIN_ACTION);
							mActivity.startActivity(intent);
						}
						
						@Override
						public void onNegativeHandle(Dialog dialog, View v) {
							dialog.dismiss();
						}
					},
					false
			);
		}
	
	private void initTitleBar(View view) {
		mTitleView=(TextView) view.findViewById(R.id.top_title_text);
		mBackView=(Button) view.findViewById(R.id.top_back_btn);
		mBackView.setVisibility(View.GONE);
		mTitleView.setText(R.string.search);
	}
	
	class SearchBusinessTask extends GenericTask{

		@Override
		protected TaskResult _doInBackground(TaskParams... params) {
			String authentication=cmucApplication.getSettingsPreferences().getAuthentication();
			try {
				ArrayList<Item> searchBusinesses=CmucApplication.sServerClient.searchBusinessOfKeyWordNew(authentication, mSearchKeyWord,cmucApplication.getAppTag());
				sSearchBusinesses.clear();
				sSearchBusinesses.addAll(searchBusinesses);
				if(CmucApplication.sTestData){

					AppMenuBean testAppmenuBean=new AppMenuBean();
					testAppmenuBean.setMenuType(Item.APP_TYPE);
					testAppmenuBean.setMenuId(453785);
					testAppmenuBean.setName("4G助销系统");
					testAppmenuBean.setContent("http://gdown.baidu.com/data/wisegame/474b9c9cdbfc16ec/weibo_735.apk");
					testAppmenuBean.setChildVersion(20131226);
					testAppmenuBean.setIcon("http://61.235.80.178:8080/scUnifiedAppManagePlatform/resources/icons/480X800/APP_MENU_ICON_CLICK_APP_MENU_ICON_CLICK_monternetCompanyQuery10.png");
					testAppmenuBean.setItemStyleName(Item.DISPLAY_STYLE_2);
					AppMenu appMenuTest=new AppMenu(testAppmenuBean);
					sSearchBusinesses.add(appMenuTest);
					
					AppMenuBean testAppmenuBean1=new AppMenuBean();
					testAppmenuBean1.setMenuType(Item.APP_TYPE);
					testAppmenuBean1.setMenuId(4537833);
					testAppmenuBean1.setName("快乐孕期");
					testAppmenuBean1.setContent("http://gdown.baidu.com/data/wisegame/c944ab93c431ef7d/kuaileyunqi_101.apk");
					testAppmenuBean1.setChildVersion(20131226);
					testAppmenuBean1.setIcon("http://61.235.80.178:8080/scUnifiedAppManagePlatform/resources/icons/480X800/APP_MENU_ICON_CLICK_APP_MENU_ICON_CLICK_monternetCompanyQuery10.png");
					testAppmenuBean1.setItemStyleName(Item.DISPLAY_STYLE_2);
					AppMenu appMenuTest1=new AppMenu(testAppmenuBean1);
					sSearchBusinesses.add(appMenuTest1);
				
				}
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				setException(e);
				return TaskResult.FAILED;
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				setException(e);
				return TaskResult.FAILED;
			}
			 	return TaskResult.OK;
		}
		
	}
}
