package com.starcpt.cmuc.ui.fragment;
import java.util.ArrayList;
import java.util.List;
import android.app.ActivityGroup;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.adapter.GridGalleryAdapter;
import com.starcpt.cmuc.adapter.GridViewAdapter;
import com.starcpt.cmuc.cache.preferences.Preferences;
import com.starcpt.cmuc.db.CmucDbManager;
import com.starcpt.cmuc.db.CmucStore;
import com.starcpt.cmuc.exception.business.BusinessException;
import com.starcpt.cmuc.exception.http.HttpException;
import com.starcpt.cmuc.model.AppMenu;
import com.starcpt.cmuc.model.AppMenus;
import com.starcpt.cmuc.model.Application;
import com.starcpt.cmuc.model.Applications;
import com.starcpt.cmuc.model.DataPackage;
import com.starcpt.cmuc.model.Item;
import com.starcpt.cmuc.model.bean.AppMenuBean;
import com.starcpt.cmuc.model.bean.AppMenusBean;
import com.starcpt.cmuc.model.bean.SubAccountBeen;
import com.starcpt.cmuc.task.GenericTask;
import com.starcpt.cmuc.task.GetAppMenusTask;
import com.starcpt.cmuc.task.GetTopDataTask;
import com.starcpt.cmuc.task.TaskListener;
import com.starcpt.cmuc.task.TaskParams;
import com.starcpt.cmuc.task.TaskResult;
import com.starcpt.cmuc.ui.activity.ActivityGroupPage;
import com.starcpt.cmuc.ui.activity.BusinessActivity;
import com.starcpt.cmuc.ui.activity.CommonActions;
import com.starcpt.cmuc.ui.activity.LoginActivity;
import com.starcpt.cmuc.ui.activity.SettingActivity;
import com.starcpt.cmuc.ui.activity.WebViewActivity;
import com.starcpt.cmuc.ui.view.DragableGridview;
import com.starcpt.cmuc.ui.view.DragableGridview.OnItemClickListener;
import com.starcpt.cmuc.ui.view.DragableGridview.OnItemDeleteListener;
import com.starcpt.cmuc.ui.view.DragableGridview.OnSwappingListener;
import com.starcpt.cmuc.ui.view.GridGallery;
import com.starcpt.cmuc.ui.view.GridGallery.ChildGridViewItemClickListener;
import com.starcpt.cmuc.ui.view.GridGallery.ChildGridViewItemLongClickListener;
import com.starcpt.cmuc.ui.view.SelectSubAccountDialog;
import com.sunrise.javascript.utils.ActivityUtils;
import com.sunrise.javascript.utils.JsonUtils;
public class MenuFragment extends Fragment {
	//private static final String TAG="MenuFragment";
	private static final int LOGIN_REQUEST_CODE=0;
	private static final int THREAD_LIST_QUERY_TOKEN       = 1701;
	private static final int DELETE_ONE_COLLECTION_TOKEN      = 1801;
	private static final int DELETE_ALL_COLLECTIONS_TOKEN     =1802;
	
	private final static int PAD_GRID_NUMCOLUMNS=4;
	private final static int PHONE_GRID_NUMCOLUMNS=3;
	private final static int PAD_OPEN_GRID_NUMCLOLUMS=2;
	
	private final static int PAD_GRID_PAGENUMBERS=12;
	private final static int PHONE_GRID_PAGENUMBERS=6;

	
	private TextView mTitleView;
	private TextView mMeunTrackView;
	private TextView mCurrentSubAccountView;
	private Button mBackView;
	private DataPackage mDataPackage;
	//private ListView mListView;
	//private ListViewAdapter mListViewAdapter;
	private GridGallery mGridGallery;
	private RelativeLayout mLoadPagePanelView;
	private ProgressBar mLoadPageProgressBar;
	private LinearLayout mLoadPageStausView;
	private Button mReGetPageView;
	private RelativeLayout mRemoveWebviewFragmentPanel;
	private ImageButton mRemoveWebviewFragment;
	private ImageView mCommonLineView;
	private DragableGridview mCollectionMenusGridview;
	private Button mSyncCollectionButton;
	private GridViewAdapter mCollectionMenusGridViewAdapter;
	private ArrayList<Item> mCollectionBusinesses;
	private BroadcastReceiver mScreenDirectionReceiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			if(action.equals(SettingActivity.CHANGE_SCERRN_DIRECTION_ACTION)){
				int screenDirection=intent.getIntExtra(SettingActivity.SCREEN_DIRECTION_EXTRAL, -1);
				if(screenDirection==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
					if(mRemoveWebviewFragment.getVisibility()==View.VISIBLE){
						closeWeb();
					}
				}
			}
		}
	};
	
	
	private OnItemClickListener mDragGridViewOnItemClickListener=new OnItemClickListener() {
		
		@Override
		public void click(int index) {
			Item item=mCollectionBusinesses.get(index);
			//visit(item);
			visitNextPage(item);
		}
	};
	
	private OnSwappingListener mDragGridViewOnSwappingListener=new OnSwappingListener() {
		
		@Override
		public void waspping(int oldIndex, int newIndex) {
			Item oldItem = mCollectionBusinesses.get(oldIndex);
			Item newItem=mCollectionBusinesses.get(newIndex);
			mCollectionBusinesses.set(oldIndex, newItem);
			mCollectionBusinesses.set(newIndex, oldItem);			
			mCollectionMenusGridViewAdapter.notifyDataSetChanged();
			mCmucDbManager.swapCollectionBusiness(oldItem, newItem);
		}
	};
	
	private OnItemDeleteListener mDragGridViewOnItemDeleteListener=new OnItemDeleteListener() {
		
		@Override
		public void delete(int index) {
			Item item=mCollectionBusinesses.get(index);
			createDeleteDialog(item);
		}
	};
	
	private String mPagePositions;
	private String mTitle;
	private String mPageId;
	private String mAppId;
	private String mMenuId;
	private long mChildVersion;
	private String mListDisplayStyle;
	private String mUserName;
	private boolean mIsCollectionMenu;
	private boolean mWebViewIsOpen=false;
	
	private BusinessActivity mActivity;
	
	private CmucDbManager mCmucDbManager;
	private CollectionBusinessObserver mCollectionBusinessObserver;
	private ContentResolver mContentResolver;
	private AsyncQueryHandler mQueryHandler;
	private static boolean isSyncCollections=false;
	
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
		}
	};
	
	private int mGridNumColumns=PHONE_GRID_NUMCOLUMNS;
	private int mGridPageNumbers=PHONE_GRID_PAGENUMBERS;
	private  WebViewFragment mWebViewFragment;
	private FragmentManager mFragmentManager; 
	
	private ChildGridViewItemClickListener mChildGridViewItemClickListener=new ChildGridViewItemClickListener() {
		
		@Override
		public void onItemClik(AdapterView<?> arg0, View view, final int position,
				long id, final List<Item> list) {
		   ScaleAnimation scale = new ScaleAnimation(1.0f, 1.1f,1.0f,1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		   scale.setDuration(200);
           view.startAnimation(scale);      
           Item item=list.get(position);
		   visitNextPage(item);
		}
		
	};
	
private ChildGridViewItemLongClickListener mChildGridViewItemLongClickListener=new ChildGridViewItemLongClickListener() {

	@Override
	public void onItemLongClick(AdapterView<?> arg0, View arg1, int position,
			long id, List<Item> list) {
		Item item=list.get(position);
		int menuType=item.getMenuType();
		if(menuType==Item.WEB_TYPE){
			if(!mIsCollectionMenu){
				if(isSyncCollections){
					Toast.makeText(mActivity, R.string.can_not_collcetion, Toast.LENGTH_LONG).show();
				}else{
					createCollectionDialog(item);
				}
			}
		}
	}
				
	};
	
	private TaskListener mGetAppMenusListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPreExecute(GenericTask task) {
			preGetAppMenusBean();	
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			if (result == TaskResult.OK) {
				onGetAppMenusSuccess();
			} else {
				onGetAppMenusFailed(task);
			}
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

	private GetAppMenusTask mGetAppMenusTask;
	
	private TaskListener mSyncCollectionsTaskListener=new TaskListener() {
		
		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPreExecute(GenericTask task) {
			mSyncProgressDialog=ProgressDialog.show(mActivity, null, getString(R.string.syncing), true, false);
		}
		
		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			mSyncProgressDialog.dismiss();
			isSyncCollections=false;
			if(result==TaskResult.OK){
				refreshCollectionMenus();
			}else{
				handleException(task, mActivity);
			}
			
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

	private SyncCollectionTask mSyncCollectionTask;
	
	private ProgressDialog mSyncProgressDialog;
	
	private CmucApplication cmucApplication;
	
	private SelectSubAccountDialog mSelectSubAccountDialog;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initDatabase();		
		mCommonLineView=(ImageView) mActivity.findViewById(R.id.common_line);
		mFragmentManager = ((FragmentActivity) mActivity).getSupportFragmentManager();
		initTitleBar();
		if(!mIsCollectionMenu){
			mGridGallery.setVisibility(View.VISIBLE);
			updateDataPackage(mPageId,mAppId,mMenuId,mChildVersion);
		}else{
			mCollectionMenusGridview.setOnItemClick(mDragGridViewOnItemClickListener);
			mCollectionMenusGridview.setOnSwappingListener(mDragGridViewOnSwappingListener);
			mCollectionMenusGridview.setOnItemDeleteListener(mDragGridViewOnItemDeleteListener);
			mCollectionMenusGridview.setVisibility(View.VISIBLE);
			mSyncCollectionButton.setVisibility(View.VISIBLE);
		}
		
	}

	private void refreshCollectionMenus() {
		mCollectionMenusGridview.setNumColumns(mGridNumColumns);
		if(cmucApplication.getCollectionAppmenus()==null)
			return;
		mCollectionBusinesses=cmucApplication.getCollectionAppmenus().getDatas();
		mCollectionMenusGridViewAdapter=new GridViewAdapter(mCollectionBusinesses,mActivity);
		mCollectionMenusGridview.setAdapter(mCollectionMenusGridViewAdapter);
	}

	private void getDataFromIntent() {
		Intent intent=mActivity.getIntent();		
		mPageId=intent.getStringExtra(CmucApplication.PAGE_ID_EXTRAL);
		mAppId=intent.getStringExtra(CmucApplication.APP_TAG_EXTRAL);
		mMenuId=intent.getStringExtra(CmucApplication.MENU_ID);
		mListDisplayStyle=intent.getStringExtra(CmucApplication.CHILDSTYLENAME_EXTRAL);
		mChildVersion=intent.getLongExtra(CmucApplication.CHILD_VERSION_EXTRAL, 0);
		mIsCollectionMenu=intent.getBooleanExtra(CmucApplication.COLLECTION_MENU_EXTRAL, false);
		mTitle=intent.getStringExtra(CmucApplication.TITLE_EXTRAL);
		String parentPositions=intent.getStringExtra(CmucApplication.POSITIONS_EXTRAL);
		if(parentPositions!=null)
			mPagePositions=parentPositions+">>"+mTitle;
		else
			mPagePositions=mTitle;
	}
	
	private void initDatabase() {
		mContentResolver=mActivity.getContentResolver();
		mCmucDbManager=CmucDbManager.getInstance(mContentResolver);	
		mQueryHandler=new QueryHandler(mContentResolver);
		mCollectionBusinessObserver=new CollectionBusinessObserver(mHandler);	
		mContentResolver.registerContentObserver(CmucStore.CollectionBusinesses.CONTENT_URI, true, mCollectionBusinessObserver);
	}
	
	@Override
	public void onResume() {
		if(mIsCollectionMenu){
			refreshCollectionMenus();
		}
		updateCurrentSubAccountView();
		super.onResume();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity=(BusinessActivity) getActivity();
		getDataFromIntent();
		cmucApplication=(CmucApplication) mActivity.getApplicationContext();
		mUserName=cmucApplication.getSettingsPreferences().getUserName();
		mActivity.registerReceiver(mScreenDirectionReceiver, new IntentFilter(SettingActivity.CHANGE_SCERRN_DIRECTION_ACTION));
		if(cmucApplication.isIsPad()){
			mGridNumColumns=PAD_GRID_NUMCOLUMNS;
			mGridPageNumbers=PAD_GRID_PAGENUMBERS;
		}else{
			mGridNumColumns=PHONE_GRID_NUMCOLUMNS;
			mGridPageNumbers=PHONE_GRID_PAGENUMBERS;
		}
		View view = initFragmentView(inflater, container);
		return view;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mContentResolver.unregisterContentObserver(mCollectionBusinessObserver);	
		mActivity.unregisterReceiver(mScreenDirectionReceiver);
	}
	
	private void removeWebViewFragment() {
		if(mWebViewFragment!=null){
			FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction(); 
			fragmentTransaction.hide(mWebViewFragment);
			fragmentTransaction.commitAllowingStateLoss();
		}
	}
	
	
	
	private void visitNextPage(Item item){
		if(item instanceof Application){
			createSelectSubAccountDialog(item);
		}else{
			visit(item);
		}
	}
	
	private void createSelectSubAccountDialog(final Item item) {
		final String appTag=item.getAppTag();
		String name=item.getName();
		SubAccountBeen subAccountBeen = cmucApplication.getSubAccountMap().get(appTag);
		final String[] subAccountNames = subAccountBeen.getSubAccounts();
		String currenSubAccount = cmucApplication.getCurrentSubAccountHashMap().get(appTag);
		int selectSubAccountIndex = 0;
		if (subAccountNames != null) {
			if (currenSubAccount != null) {
				int length = subAccountNames.length;
				for (int i = 0; i < length; i++) {
					if (currenSubAccount.equalsIgnoreCase(subAccountNames[i])) {
						selectSubAccountIndex = i;
						break;
					}
				}
			}
			mSelectSubAccountDialog = new SelectSubAccountDialog(
					mActivity.getParent() != null ? mActivity.getParent()
							: mActivity, name, subAccountNames,
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							cmucApplication.getCurrentSubAccountHashMap().put(appTag, subAccountNames[mSelectSubAccountDialog.getCurrentItem()]);
							visit(item);
							mSelectSubAccountDialog.dismiss();
						}
					});
			mSelectSubAccountDialog.setSelectItem(selectSubAccountIndex);
			mSelectSubAccountDialog.show();
		} else {
			visit(item);
		}
	}

	
	private void visit(Item item){
		int menuType=item.getMenuType();
		String content=item.getContent();
		String name=item.getName();
		String appTag=item.getAppTag();
		String menuId=item.getMenuId()+"";
	    String pageId=item.getAppTag()+item.getMenuId()+"";
	    String childStyleName=item.getChildStyleName();
	    long childVersion=item.getChildVersion();
	    int businessId=item.getBusinessId();
		if(menuType==Item.MENU_TYPE){
			ActivityGroup activityGroup=(ActivityGroup)mActivity.getParent();
			Intent intent=new Intent(mActivity, BusinessActivity.class);
			intent.putExtra(CmucApplication.TITLE_EXTRAL, name);
			intent.putExtra(CmucApplication.APP_TAG_EXTRAL, appTag);
			intent.putExtra(CmucApplication.MENU_ID, menuId);
		    intent.putExtra(CmucApplication.PAGE_ID_EXTRAL, pageId);
		    intent.putExtra(CmucApplication.CHILDSTYLENAME_EXTRAL,childStyleName);
		    intent.putExtra(CmucApplication.POSITIONS_EXTRAL, mPagePositions);
		    intent.putExtra(CmucApplication.CHILD_VERSION_EXTRAL, childVersion);
		    if(mRemoveWebviewFragment.getVisibility()==View.VISIBLE){
				closeWeb();
			}		    
		    if(cmucApplication.isOpenWebViewInFragment()){
				cmucApplication.getWebViewWindows().clear();
		    }
			activityGroup.startActivity(intent);
			}
		else if(menuType==Item.WEB_TYPE){
			if(cmucApplication.isOpenWebViewInFragment()){
				FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction(); 
				if(mWebViewFragment==null){
					mWebViewFragment=new WebViewFragment(item, true,null);
					mWebViewFragment.setParentPositions(mPagePositions);
					mWebViewFragment.setTitle(name);
					mWebViewFragment.setManualStart(true);			
					fragmentTransaction.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out);  
					fragmentTransaction.add(R.id.business_panel, mWebViewFragment);
				}					
				else
				{
					mWebViewFragment.setTitle(name);
					mWebViewFragment.setParentPositions(mPagePositions);
					mWebViewFragment.setManualStart(true);
					mWebViewFragment.update(item, true,null);
					mWebViewFragment.refreshWebView();
					fragmentTransaction.show(mWebViewFragment);
				}
				fragmentTransaction.commitAllowingStateLoss();
				
				if(!mWebViewIsOpen){
					mGridNumColumns=PAD_OPEN_GRID_NUMCLOLUMS;
					if(mIsCollectionMenu){
						refreshCollectionMenus();
					}
					bindPageBeanToView(mGridNumColumns);
					mWebViewIsOpen=true;
					mCommonLineView.setVisibility(View.VISIBLE);
					mRemoveWebviewFragmentPanel.setVisibility(View.VISIBLE);
				}
			}else{
				Intent intent = new Intent(mActivity,WebViewActivity.class);
				intent.putExtra(CmucApplication.CONTENT_EXTRAL, content);
				intent.putExtra(CmucApplication.MENU_ID, menuId);
				intent.putExtra(CmucApplication.APP_TAG_EXTRAL,appTag);
				intent.putExtra(CmucApplication.BUSINESS_ID_EXTRAL, businessId);
				intent.putExtra(CmucApplication.IS_BUSINESS_WEB_EXTRAL,true);
				intent.putExtra(CmucApplication.TITLE_EXTRAL, name);
			    intent.putExtra(CmucApplication.CHILD_VERSION_EXTRAL, childVersion);
				if(content!=null){
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
				}
			}
		}else if(menuType==Item.APP_TYPE){
		    CommonActions.installApp(item,mActivity,item.getThreePackageNameString(),new CommonActions.OpenThirdAppCallBack() {			
				@Override
				public void refreshUi() {
					for(GridGalleryAdapter gridViewAdapter:mGridGallery.getAdapters()){
						gridViewAdapter.notifyDataSetChanged();
					}				
				}
			});
		}
	}

	
	
	
	private View initFragmentView(LayoutInflater inflater,ViewGroup container) {
		View view = inflater.inflate(R.layout.app_menus_fragment, container, false);
		mTitleView=(TextView) view.findViewById(R.id.top_title_text);
		mMeunTrackView=(TextView) view.findViewById(R.id.menu_track);
		mCurrentSubAccountView=(TextView) view.findViewById(R.id.current_sub_account_tv);
		mBackView=(Button) view.findViewById(R.id.top_back_btn);
		mSyncCollectionButton=(Button) view.findViewById(R.id.sync_colleciton);
		mSyncCollectionButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doSyncCollections();
			}
		});
		
		mRemoveWebviewFragment=(ImageButton) view.findViewById(R.id.remove_webview_fragment);
		mRemoveWebviewFragmentPanel=(RelativeLayout) view.findViewById(R.id.remove_webview_fragment_panel);
		mCollectionMenusGridview=(DragableGridview) view.findViewById(R.id.app_menu_dragable_gridview);
		
		mRemoveWebviewFragment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeWeb();
			}
		});
		
		mGridGallery=(GridGallery) view.findViewById(R.id.grid_gallery);
		mGridGallery.setNumbersOfPage(mGridPageNumbers);
		
		mGridGallery.setChildGridViewItemClickListener(mChildGridViewItemClickListener);
		mGridGallery.setChildGridViewItemLongClickListener(mChildGridViewItemLongClickListener);
		/*mListView=(ListView) view.findViewById(R.id.list);
		mListView.setOnItemClickListener(mOnItemClickListener);*/
		
		mLoadPagePanelView=(RelativeLayout) view.findViewById(R.id.load_page);
		mLoadPageProgressBar=(ProgressBar) view.findViewById(R.id.update_page_progress);
		mLoadPageStausView=(LinearLayout) view.findViewById(R.id.load_page_failed);
		mReGetPageView=(Button) view.findViewById(R.id.re_get_data);
		mReGetPageView.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				doGetAppMenus(mAppId,mMenuId,mChildVersion);
			}
		});	
		return view;
	}

	private void updateCurrentSubAccountView() {
		if(mIsCollectionMenu){
			mCurrentSubAccountView.setVisibility(View.GONE);
		}else{
			if (cmucApplication.getCurrentSubAccount(mAppId) != null) {
				mCurrentSubAccountView.setVisibility(View.VISIBLE);
				String ind = getString(R.string.current_sub_account)
						+ "<font color=#<font color=#000000>"
						+ cmucApplication.getCurrentSubAccount(mAppId) + "</font>";
				mCurrentSubAccountView.setText(Html.fromHtml(ind));
			} else {
				mCurrentSubAccountView.setVisibility(View.GONE);
			}
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	private void updateDataPackage(String pageId,String appId,String menuId,long childVersion){
			 mDataPackage=cmucApplication.getDataPackage(pageId);
			if(mDataPackage!=null){
				bindPageBeanToView(mGridNumColumns);
			}else{
				if(appId!=null){
					doGetAppMenus(appId,menuId,childVersion);
				}
				else{
					doGetTopListData();
				}
			}		
	 }
	 
	private void closeWeb() {
		if(mWebViewIsOpen){
			removeWebViewFragment();
			mRemoveWebviewFragmentPanel.setVisibility(View.GONE);
			mGridNumColumns=PAD_GRID_NUMCOLUMNS;
			mCommonLineView.setVisibility(View.GONE);
			if(mIsCollectionMenu)
				refreshCollectionMenus();
		    else
		    	bindPageBeanToView(mGridNumColumns);
			mWebViewIsOpen=false;
		}
	}
		
	/* private void initVerticalListView(ArrayList<Item> list){
			if(list!=null)
			mListViewAdapter=new ListViewAdapter(list, mActivity);
			mListView.setAdapter(mListViewAdapter);
		}*/
	 
	 private void doGetAppMenus(String appId,String menuId,long childVersion){
		 if(mGetAppMenusTask!=null)
			 mGetAppMenusTask.cancle();
		 mGetAppMenusTask=new GetAppMenusTask(mActivity);
		 mGetAppMenusTask.setListener(mGetAppMenusListener);
		 TaskParams params=new TaskParams();
		 params.put(CmucApplication.APP_TAG_EXTRAL, appId);
		 params.put(CmucApplication.MENU_ID, menuId);
		 params.put(CmucApplication.CHILD_VERSION_EXTRAL, childVersion);
		 mGetAppMenusTask.execute(params);
	 }
	 
	private void doGetTopListData() {
		GetTopDataTask getTopListDataTask = new GetTopDataTask(mActivity);
		getTopListDataTask.setListener(mGetAppMenusListener);
		getTopListDataTask.execute();
	}
		
	 private void doSyncCollections(){
		 if(mSyncCollectionTask!=null)
			 mSyncCollectionTask.cancle();
		 mSyncCollectionTask=new SyncCollectionTask();
		 mSyncCollectionTask.setListener(mSyncCollectionsTaskListener);
		 mSyncCollectionTask.execute();
	 }
	 
	 private void preGetAppMenusBean(){
		mGridGallery.setVisibility(View.GONE);
		mLoadPagePanelView.setVisibility(View.VISIBLE);
		mLoadPageProgressBar.setVisibility(View.VISIBLE);
		mLoadPageStausView.setVisibility(View.GONE);
	 }
	 
	 private void onGetAppMenusSuccess(){
		mDataPackage=cmucApplication.getDataPackage(mPageId);
		mLoadPagePanelView.setVisibility(View.GONE);
		mGridGallery.setVisibility(View.VISIBLE);
		bindPageBeanToView(mGridNumColumns);
	 }
	 
	 private void onGetAppMenusFailed(GenericTask task){
		mLoadPagePanelView.setVisibility(View.VISIBLE);
		mLoadPageProgressBar.setVisibility(View.GONE);
		mGridGallery.setVisibility(View.GONE);
		mLoadPageStausView.setVisibility(View.VISIBLE);
		handleException(task, mActivity);
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
							mActivity.startActivityForResult(intent, LOGIN_REQUEST_CODE);
						}
						
						@Override
						public void onNegativeHandle(Dialog dialog, View v) {
							dialog.dismiss();
						}
					},
					false
			);
		}
	 
	 private void bindPageBeanToView(int numColumns){
		 if(mDataPackage==null)
			 return;
		 ArrayList<Item> list=mDataPackage.getDatas();
		 if(list!=null&&list.size()>0){
		 bindDatasToView(list,numColumns);
		 }
	 }
	 
	 private void bindDatasToView(ArrayList<Item> list,int numColumns){
		if(mListDisplayStyle==null){
			mListDisplayStyle=((Applications)mDataPackage).getAppListDisplayStyle();
		}
		else{
			mListDisplayStyle=DataPackage.GRID_DISPALY_STYLE_1;
		}
		
		if(mListDisplayStyle.equalsIgnoreCase(DataPackage.GRID_DISPALY_STYLE_1)){
			if(mGridGallery!=null){
				mGridGallery.setVisibility(View.VISIBLE);
			//if(mListView!=null)
				//mListView.setVisibility(View.GONE);
				mGridGallery.initGallery(list,numColumns);
			}
		}else if(mListDisplayStyle.equalsIgnoreCase(DataPackage.VERTICAL_LIST_DISPLAY_STYLE)){/*
			if(mGridGallery!=null){
				mGridGallery.setVisibility(View.GONE);
			if(mListView!=null)
				mListView.setVisibility(View.VISIBLE);		
				initVerticalListView(list);		    
			}
		*/}
		}
	 
	 private void initTitleBar() {
		    Spanned	tracksSpanned=CommonActions.getMenuTracks(mPagePositions);
		    mMeunTrackView.setText(tracksSpanned);
		    mMeunTrackView.setVisibility(View.VISIBLE);
			ActivityGroupPage mActivityGroup=mActivity.getActivityGroup(); 
			if(mTitle!=null)
			mTitleView.setText(mTitle);			
			if(mActivityGroup!=null){
				int childCount=mActivityGroup.getChildActivityCount();
				if(childCount>1){
					mBackView.setVisibility(View.VISIBLE);
				}else{
					mBackView.setVisibility(View.GONE);
				}
			}else{
				mBackView.setVisibility(View.GONE);
			}
			
			mBackView.setOnClickListener(new OnClickListener() {		
				@Override
				public void onClick(View v) {
					mActivity.exitActivity();
					if(cmucApplication.isOpenWebViewInFragment())
					cmucApplication.getWebViewWindows().clear();
				}
			});
		}

	 public  void createCollectionDialog(final Item item) {
		 item.setDeleteFlag(Item.UN_DELETE);
		 final int collectionStaus=mCmucDbManager.checkCollectionBusiness(mUserName, item);
		 String dialogMessage=	mActivity.getString(R.string.collection_business_question);
		 if(collectionStaus==CmucDbManager.NO_DELETE_FALG_ITEM){
			 dialogMessage=mActivity.getString(R.string.update_collection_business_question);
		 }
		 CommonActions.createTwoBtnMsgDialog(mActivity.getParent(),
				mActivity.getString(R.string.collection), 
				dialogMessage, 
				mActivity.getString(R.string.confirm), 
				mActivity.getString(R.string.cancel), 
				new CommonActions.OnTwoBtnDialogHandler() {			
					@Override
					public void onPositiveHandle(Dialog dialog,View v) {
						item.setUserName(mUserName);
						long time=System.currentTimeMillis();
						item.setCollectionTime(time);
						item.setDeleteFlag(Item.UN_DELETE);
						item.setListOrder(time);
						if(collectionStaus==CmucDbManager.NO_CONTAIN_ITEM){
							mCmucDbManager.recordCollectionBusiness(item);
							}
						else{
							mCmucDbManager.updateCollectionBusiness(item);
						}
						dialog.dismiss();
					}
					
					@Override
					public void onNegativeHandle(Dialog dialog,View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				},
				false);
	}
		
	 public  void createDeleteDialog(final Item item) {
		 item.setDeleteFlag(Item.DELETED);
		 item.setCollectionTime(System.currentTimeMillis());
		 String dialogMessage=	mActivity.getString(R.string.delete_collection_business_question);
		 CommonActions.createTwoBtnMsgDialog(mActivity.getParent(),
				mActivity.getString(R.string.delete_collection_business), 
				dialogMessage, 
				mActivity.getString(R.string.confirm), 
				mActivity.getString(R.string.cancel), 
				new CommonActions.OnTwoBtnDialogHandler() {			
					@Override
					public void onPositiveHandle(Dialog dialog,View v) {
						mCollectionBusinesses.remove(item);
						mCollectionMenusGridViewAdapter.notifyDataSetChanged();
						mCmucDbManager.deleteCollectionBusiness(item);
						dialog.dismiss();
					}
					
					@Override
					public void onNegativeHandle(Dialog dialog,View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				},
				false
		 );
	}
	 
	 class QueryHandler extends AsyncQueryHandler{
			
			public QueryHandler(ContentResolver cr) {
				super(cr);
				// TODO Auto-generated constructor stub
			}
			 
			@Override
			protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
				switch(token){
				case THREAD_LIST_QUERY_TOKEN:
						cmucApplication.setCollectionAppmenus(mCmucDbManager.cursorToAppmenus(cursor,false));
					break;
				}
			}

			
			@Override
			protected void onDeleteComplete(int token, Object cookie, int result) {
				switch(token){
				case DELETE_ONE_COLLECTION_TOKEN:
					
					break;
				case DELETE_ALL_COLLECTIONS_TOKEN:
				
					break;
				}
			}
		 }
		
	 class CollectionBusinessObserver extends ContentObserver{

			public CollectionBusinessObserver(Handler handler) {
				super(handler);
			}

			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				mCmucDbManager.startQueryCollectionBusiness(mQueryHandler, THREAD_LIST_QUERY_TOKEN, cmucApplication.getSettingsPreferences().getUserName());
			}
			
		}
	 
	 class SyncCollectionTask extends GenericTask{

		@Override
		protected TaskResult _doInBackground(TaskParams... params) {
			isSyncCollections=true;
			Preferences preferences=cmucApplication.getSettingsPreferences();
			String authentication=preferences.getAuthentication();
			String userName=preferences.getUserName();
			ArrayList<AppMenuBean> submitAppMenuBeans=new ArrayList<AppMenuBean>(); 
			try {
				AppMenus appMenusFromServer=CmucApplication.sServerClient.searchCollectionBusinesses(authentication, 1+"", 10000+"");		
				ArrayList<AppMenuBean> appMenuBeansFromServer=appMenusFromServer.getAppMenusBean().getDatas();
				AppMenus appMenusFromDatabase=mCmucDbManager.queryCollectionBusiness(userName,true);
				ArrayList<AppMenuBean> appMenuBeansFromDatabase=appMenusFromDatabase.getAppMenusBean().getDatas();
				if(appMenuBeansFromServer.size()==0){
					if(appMenuBeansFromDatabase.size()!=0){
						mCmucDbManager.deleteCollectionBusiness(userName);
						appMenusFromDatabase=mCmucDbManager.queryCollectionBusiness(userName, false);
						cmucApplication.setCollectionAppmenus(appMenusFromDatabase);
						submitAppMenuBeans=appMenusFromDatabase.getAppMenusBean().getDatas();
						if(submitAppMenuBeans.size()!=0)
							commitCollectionBusiness(authentication,submitAppMenuBeans);
					}
				}else{
					if(appMenuBeansFromDatabase.size()==0){
						submitAppMenuBeans=appMenuBeansFromServer;
						
						ArrayList<AppMenuBean> deleteAppMenuBeans=new ArrayList<AppMenuBean>();				
						for(AppMenuBean appMenuBean:submitAppMenuBeans){
							if(appMenuBean.getDeleteFlag()==Item.DELETED){
								deleteAppMenuBeans.add(appMenuBean);
							}
						}
						submitAppMenuBeans.removeAll(deleteAppMenuBeans);
						
						commitCollectionBusiness(authentication,submitAppMenuBeans);
						
						if(submitAppMenuBeans.size()!=0){
							for(AppMenuBean appMenuBean:submitAppMenuBeans){
								mCmucDbManager.recordCollectionBusiness(new AppMenu(appMenuBean));
							}
						}				
						updateCollections(submitAppMenuBeans);					
					}else{						
						ArrayList<AppMenuBean> commonAppMenuBeans=new ArrayList<AppMenuBean>();
						ArrayList<AppMenuBean> commonAppMenuBeansServer=new ArrayList<AppMenuBean>();
						ArrayList<AppMenuBean> commonAppMenuBeansDatabase=new ArrayList<AppMenuBean>();
						ArrayList<AppMenuBean> currentDeleteAppMenuBeansDatabase=new ArrayList<AppMenuBean>();
						
						for(AppMenuBean appMenuBeanFromServer:appMenuBeansFromServer){
							long collectionTimeServer=appMenuBeanFromServer.getCollectionTime();
							int businessIdServer=appMenuBeanFromServer.getBusinessId();			
							for(AppMenuBean appMenuBeanFromDatabase:appMenuBeansFromDatabase){
								long collectionTimeDatabase=appMenuBeanFromDatabase.getCollectionTime();
								int businessIdDatabase=appMenuBeanFromDatabase.getBusinessId();
								if(businessIdServer==businessIdDatabase){
									if(collectionTimeServer>=collectionTimeDatabase){
										commonAppMenuBeans.add(appMenuBeanFromServer);
									}
									else{
										commonAppMenuBeans.add(appMenuBeanFromDatabase);
										if(appMenuBeanFromDatabase.getDeleteFlag()==1){
											currentDeleteAppMenuBeansDatabase.add(appMenuBeanFromDatabase);
										}
									}
									commonAppMenuBeansServer.add(appMenuBeanFromServer);
									commonAppMenuBeansDatabase.add(appMenuBeanFromDatabase);
									break;
								}
							}
						}
						
						appMenuBeansFromServer.removeAll(commonAppMenuBeansServer);
						appMenuBeansFromDatabase.removeAll(commonAppMenuBeansDatabase);
						submitAppMenuBeans.addAll(commonAppMenuBeans);
						submitAppMenuBeans.addAll(appMenuBeansFromServer);
						submitAppMenuBeans.addAll(appMenuBeansFromDatabase);
						
						ArrayList<AppMenuBean> deleteAppMenuBeans=new ArrayList<AppMenuBean>();				
						for(AppMenuBean appMenuBean:submitAppMenuBeans){
							if(appMenuBean.getDeleteFlag()==Item.DELETED){
								deleteAppMenuBeans.add(appMenuBean);
							}
						}
						submitAppMenuBeans.removeAll(deleteAppMenuBeans);
						
						ArrayList<AppMenuBean> tempSubitAppMenuBeans=new ArrayList<AppMenuBean>();
						tempSubitAppMenuBeans.addAll(submitAppMenuBeans);
						
						submitAppMenuBeans.addAll(currentDeleteAppMenuBeansDatabase);				
						commitCollectionBusiness(authentication,submitAppMenuBeans);	
											
						mCmucDbManager.deleteAllCollectionBusiness(userName);	
						if(tempSubitAppMenuBeans.size()!=0){
							for(AppMenuBean appMenuBean:tempSubitAppMenuBeans){
								mCmucDbManager.recordCollectionBusiness(new AppMenu(appMenuBean));
							}						
						}
						
						updateCollections(tempSubitAppMenuBeans);					
						
					}
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

		private void updateCollections(ArrayList<AppMenuBean> submitAppMenuBeans) {
			AppMenusBean appMenusBean=new AppMenusBean();
			appMenusBean.setDatas(submitAppMenuBeans);
			cmucApplication.setCollectionAppmenus(new AppMenus(appMenusBean, AppMenus.TIME_SORT));
		}

		private void commitCollectionBusiness(String authentication,
				ArrayList<AppMenuBean> submitAppMenuBeans)
				throws HttpException, BusinessException {
			long commintTime=System.currentTimeMillis();
			for(AppMenuBean appmenuBean:submitAppMenuBeans){
				appmenuBean.setCollectionTime(commintTime);
			}
			String jasonStr=JsonUtils.writeObjectToJsonStr(submitAppMenuBeans);
			CmucApplication.sServerClient.uploadCollectionBusinesses(authentication, jasonStr);
		}
		 
		
	 }

	public boolean isWebViewIsOpen() {
		return mWebViewIsOpen;
	}
	
	 
	 
}
