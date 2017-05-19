package com.starcpt.cmuc.ui.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.adapter.BookMarksAdapter;
import com.starcpt.cmuc.adapter.HistoryExpandableAdapter;
import com.starcpt.cmuc.adapter.HistoryExpandableAdapter.ChildViewHolder;
import com.starcpt.cmuc.adapter.ViewPagerAdapter;
import com.starcpt.cmuc.db.CmucDbManager;
import com.starcpt.cmuc.db.CmucStore;
import com.starcpt.cmuc.model.bean.BookmarkBean;
import com.starcpt.cmuc.model.bean.VisitHistoryBean;
import com.starcpt.cmuc.ui.view.CreateShortCutBookmarkDialog;
import com.starcpt.cmuc.ui.view.EditBookmarkDialog;
import com.starcpt.cmuc.ui.view.OperateBrowserRecordDialog;

public class BrowserActivity extends Activity {
	private static final int QUERY_BOOKMARK_TOKEN = 1701;
	private static final int QUERY_VISIT_HISTORY_TOKEN = 1702;
	private static final String URL_MATH_STR="^http://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";
	private View mRootView;
	private Button mVisitWebButton;
	private EditText mUrlEditText;

	private LinearLayout mHomePanel;
	private TextView mBookMarksView;
	private TextView mHistoryView;
	private ColorStateList mBookMarkTextNormalColor;
	private ViewPager mViewPager;
	private ArrayList<View> mViews;
	private BookMarksAdapter mBookMarksAdapter;
	
	private final BookMarksAdapter.OnContentChangedListener mContentChangedListener = new BookMarksAdapter.OnContentChangedListener() {

		@Override
		public void onContentChanged(BookMarksAdapter adapter) {
			startAsyncQuery();
		}
	};
	
	private final OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View view,
				int position, long id) {
			Cursor cursor=mBookMarksAdapter.getCursor();
			cursor.moveToFirst();
			cursor.move(position);
			BookmarkBean bookMarkBean=mCmucDbManager.cursorToBookmark(cursor);
			createOperateBookmarksDialog(bookMarkBean);
			return true;
		}
	};
	
	private final OnItemClickListener mOnItemSelectedListener =new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long id) {
			Cursor cursor=mBookMarksAdapter.getCursor();
			cursor.moveToFirst();
			cursor.move(position);
			BookmarkBean bookMarkBean=mCmucDbManager.cursorToBookmark(cursor);
			mAddressVisited=bookMarkBean.getUrl();
			visitWeb();
		}
	};
	private String[] mOperateBookmarkItems;
	private LinearLayout mWebViewPanel;
	private WebView mWebView;
	private ImageView mBrowserButtonBack;
	private ImageView mBrowserButtonPresious;
	private ImageView mBrowserButtonRefresh;
	private ImageView mBrowserButtonHome;
	private ImageView mBrowserButtonCollection;
	private ProgressBar mVisitWebProgressBar;
	private EditBookmarkDialog mEditBookmarkDialog;
	private OperateBrowserRecordDialog mOperateBookmarksDialog;
	private OperateBrowserRecordDialog mOperateHistorysDialog;
	private CreateShortCutBookmarkDialog mCreateShortCutBookmarkDialog;
	
	private String[] mOperateHistoryItems;
	private ExpandableListView mHistoryExpandableListView;
	private String[] mHistoryGroupNames;
	private ArrayList<ArrayList<VisitHistoryBean>> mHistoryChildArray=new ArrayList<ArrayList<VisitHistoryBean>>(); 
	private HistoryExpandableAdapter mHistoryExpandableAdapter;

	private String mAddressVisited;
	private boolean isLoadingWeb = false;
	private CmucDbManager mCmucDbManager;
	private ContentResolver mContentResolver;
	private VisitHistoryObserver mVisitHistoryObserver;
	private AsyncQueryHandler mQueryHandler;
	private String mUserName;
	

	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
		}
	};
	
	private CmucApplication cmucApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		cmucApplication=(CmucApplication) getApplicationContext();
		CommonActions.setScreenOrientation(this);
		setContentView(R.layout.browser);
		initData();
		initView();
		visitWeb();
		CommonActions.addActivity(this);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		initVisitUrl(intent);
		visitWeb();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(CmucApplication.sNeedShowLock){
			CommonActions.showLockScreen(this);
		}
	}
	private void updateTabItem(int index) {
		TextView selectView = index == 0 ? mBookMarksView : mHistoryView;
		TextView unSelectView = index == 0 ? mHistoryView : mBookMarksView;
		selectView.setBackgroundResource(R.color.browser_book_mark_text_normal);
		selectView.setTextColor(Color.WHITE);
		unSelectView.setBackgroundDrawable(null);
		unSelectView.setTextColor(mBookMarkTextNormalColor);
	}

	private void browserBarHomeStatus() {
		disableBackButton();
		disablePresiousButton();
		disableRefreshButton();
		disableHomeButton();
		disableCollectionButton();
	}

	private void disableCollectionButton() {
		mBrowserButtonCollection.setEnabled(false);
		mBrowserButtonCollection
				.setImageResource(R.drawable.browser_menu_collection_no_click);
	}

	private void disableHomeButton() {
		mBrowserButtonHome.setEnabled(false);
		mBrowserButtonHome
				.setImageResource(R.drawable.browser_menu_home_no_click);
	}

	private void disableRefreshButton() {
		mBrowserButtonRefresh.setEnabled(false);
		mBrowserButtonRefresh
				.setImageResource(R.drawable.browser_menu_refersh_no_click);
	}

	private void disablePresiousButton() {
		mBrowserButtonPresious.setEnabled(false);
		mBrowserButtonPresious
				.setImageResource(R.drawable.browser_menu_previous_no_click);
	}

	private void disableBackButton() {
		mBrowserButtonBack.setEnabled(false);
		mBrowserButtonBack
				.setImageResource(R.drawable.browser_menu_back_no_click);
	}

	private void browserBarVisitStatus() {
		enbaleHomeButton();
		enableCollectionButton();
	}

	private void enableCollectionButton() {
		mBrowserButtonCollection.setEnabled(true);
		mBrowserButtonCollection
				.setImageResource(R.drawable.browser_menu_collection);
	}

	private void enbaleHomeButton() {
		mBrowserButtonHome.setEnabled(true);
		mBrowserButtonHome.setImageResource(R.drawable.browser_menu_home);
	}

	private void enableBackButton() {
		mBrowserButtonBack.setEnabled(true);
		mBrowserButtonBack.setImageResource(R.drawable.browser_menu_back);
	}

	private void enablePresiousButton() {
		mBrowserButtonPresious.setEnabled(true);
		mBrowserButtonPresious
				.setImageResource(R.drawable.browser_menu_previous);
	}

	private void enableRefreshButton() {
		mBrowserButtonRefresh.setEnabled(true);
		mBrowserButtonRefresh.setImageResource(R.drawable.browser_menu_refersh);
	}

	private void changeRefreshButtonIcon() {
		isLoadingWeb = !isLoadingWeb;
		mBrowserButtonRefresh.setEnabled(true);
		if (isLoadingWeb) {
			mBrowserButtonRefresh
					.setImageResource(R.drawable.browser_menu_cancel);
		} else {
			mBrowserButtonRefresh
					.setImageResource(R.drawable.browser_menu_refersh);
		}
	}

	private void displayWebview() {
		if (mHomePanel.getVisibility() == View.VISIBLE) {
			mHomePanel.setVisibility(View.GONE);
		}
		if (mWebViewPanel.getVisibility() == View.GONE) {
			mWebViewPanel.setVisibility(View.VISIBLE);
		}
	}

	public void createCoverBookmarkDialog(final BookmarkBean oldBookMarkBean,final BookmarkBean newbBookMarkBean) {
		CommonActions.createTwoBtnMsgDialog(this,
				getString(R.string.bookmark_exist),
				getString(R.string.bookemark_exist_question),
				getString(R.string.cover), getString(R.string.cancel),
				new CommonActions.OnTwoBtnDialogHandler() {

					@Override
					public void onPositiveHandle(Dialog dialog, View v) {
						mCmucDbManager.updateBookmark(oldBookMarkBean,newbBookMarkBean);
						Toast.makeText(BrowserActivity.this,
								R.string.update_bookmark_success,
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}

					@Override
					public void onNegativeHandle(Dialog dialog, View v) {
						dialog.dismiss();
					}
				}, false);
	}

	private void createOperateBookmarksDialog(final BookmarkBean bookMarkBean){
		mOperateBookmarksDialog=new OperateBrowserRecordDialog(BrowserActivity.this, mRootView,mOperateBookmarkItems
				,new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View view,
							int position, long id) {
					switch(position){
					case 0:
						createDeleteBookmarkDialog(bookMarkBean);
						break;
					case 1:
						createAddBookmarkDialog(bookMarkBean);
						break;
					case 2:
						createShortCutBookmarkDialog(bookMarkBean);
						break;			
					}
					mOperateBookmarksDialog.show(false);
					}
				});
		mOperateBookmarksDialog.show(true);
	}
	
	private void createOperateHistorysDialog(final VisitHistoryBean visitHistoryBean){
		mOperateHistorysDialog=new OperateBrowserRecordDialog(BrowserActivity.this, mRootView,mOperateHistoryItems
				,new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View view,
							int position, long id) {
					switch(position){
					case 0:
						createDeleteHistoryDialog(visitHistoryBean);
						break;	
					}
					mOperateHistorysDialog.show(false);
					}
				});
		mOperateHistorysDialog.show(true);
	}
	
	private void createDeleteBookmarkDialog(final BookmarkBean bookMarkBean){
		String message=mOperateBookmarkItems[0]+"“"+bookMarkBean.getTitle()+"”"+"？";
		CommonActions.createTwoBtnMsgDialog(this, 
				mOperateBookmarkItems[0], 
				message, 
				getString(R.string.confirm), 
				getString(R.string.cancel), 
				new CommonActions.OnTwoBtnDialogHandler() {
					
					@Override
					public void onPositiveHandle(Dialog dialog, View v) {
						mCmucDbManager.deleteBookmark(bookMarkBean);
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
	
	private void createDeleteHistoryDialog(final VisitHistoryBean visitHistoryBean){
		String message=mOperateHistoryItems[0]+"“"+visitHistoryBean.getTitle()+"”"+"？";
		CommonActions.createTwoBtnMsgDialog(this, 
				mOperateHistoryItems[0], 
				message, 
				getString(R.string.confirm), 
				getString(R.string.cancel), 
				new CommonActions.OnTwoBtnDialogHandler() {
					
					@Override
					public void onPositiveHandle(Dialog dialog, View v) {
						mCmucDbManager.deleteVisitHistory(visitHistoryBean);
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
	
	private void createShortCutBookmarkDialog(final BookmarkBean bookMarkBean) {
		mCreateShortCutBookmarkDialog = new CreateShortCutBookmarkDialog(this,
				R.string.add_desk_bookmark, new OnClickListener() {

					@Override
					public void onClick(View v) {
						boolean flag = hasShortcut(bookMarkBean);// 如果已经创建，则不需要在创建
						String bookmarkName=mCreateShortCutBookmarkDialog.getBookmarkTitle();
						if (TextUtils.isEmpty(bookmarkName)) {
							Toast.makeText(BrowserActivity.this,
									R.string.please_input_bookmark_title,
									Toast.LENGTH_LONG).show();
							return;
						}
						if (flag == false) {
							addShortcut(bookmarkName,bookMarkBean);
						}
						mCreateShortCutBookmarkDialog.dismiss();
					}
				});
		mCreateShortCutBookmarkDialog.setBookmarkTitle(bookMarkBean.getTitle());
		mCreateShortCutBookmarkDialog.show();
	};
	
	/**
	 * 为程序创建桌面快捷方式
	 */
	private void addShortcut(String title,BookmarkBean bookMarkBean){ 
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT"); 
               
        //快捷方式的名称 
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title); 
        shortcut.putExtra("duplicate", false); //不允许重复创建 
        
        Intent shortcutIntent = new Intent(this,  BrowserActivity.class);  
        shortcutIntent.setAction("com.starcpt.cmuc.BROWSER");
        shortcutIntent.setData(Uri.parse(bookMarkBean.getUrl()));
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        
        ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this, R.drawable.bookmark); 
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes); 
               
        sendBroadcast(shortcut); 
    }

	private boolean hasShortcut(BookmarkBean oldBookMarkBean) {
		boolean isInstallShortcut = false;
		final ContentResolver cr = getContentResolver();
		final String AUTHORITY = "com.android.launcher.settings";
		final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/favorites?notify=true");
		Cursor c = cr.query(CONTENT_URI,
				new String[] { "title", "iconResource" }, "title=?",
				new String[] { oldBookMarkBean.getTitle() }, null);
		if (c != null && c.getCount() > 0) {
			isInstallShortcut = true;
		}
		return isInstallShortcut;
	}
	 
	private void createAddBookmarkDialog(final BookmarkBean oldBookMarkBean) {
		String title=oldBookMarkBean==null?getString(R.string.add_bookmark):mOperateBookmarkItems[1];
		mEditBookmarkDialog = new EditBookmarkDialog(BrowserActivity.this,
				title, new OnClickListener() {
					@Override
					public void onClick(View v) {
						String bookmarkTitle = mEditBookmarkDialog
								.getBookmarkTitle();
						String bookmarkUrl = mEditBookmarkDialog
								.getBookmarkUrl();
						if (TextUtils.isEmpty(bookmarkTitle)) {
							Toast.makeText(BrowserActivity.this,
									R.string.please_input_bookmark_title,
									Toast.LENGTH_LONG).show();
							return;
						}
						if (bookmarkUrl==null||TextUtils.isEmpty(bookmarkUrl.trim())) {
							Toast.makeText(BrowserActivity.this,
									R.string.please_input_bookmark_url,
									Toast.LENGTH_SHORT).show();
							return;
						}
						if(!bookmarkUrl.matches(URL_MATH_STR)){
							Toast.makeText(BrowserActivity.this,
									R.string.please_visit_vaild_url, Toast.LENGTH_LONG)
									.show();
							return;
						}
						BookmarkBean newBookMarkBean = new BookmarkBean(System
								.currentTimeMillis(), bookmarkTitle,
								bookmarkUrl);
						newBookMarkBean.setUserName(mUserName);
						if(oldBookMarkBean==null){
							if (!mCmucDbManager.checkBookmarkExist(newBookMarkBean)) {
								mCmucDbManager.recordBookmark(newBookMarkBean);
								Toast.makeText(BrowserActivity.this,
										R.string.add_bookmark_success,
										Toast.LENGTH_SHORT).show();
							} else {
								createCoverBookmarkDialog(oldBookMarkBean,newBookMarkBean);
							}
						}else{
							newBookMarkBean.setTime(oldBookMarkBean.getTime());
							mCmucDbManager.updateBookmark(oldBookMarkBean,newBookMarkBean);
						}						
						mEditBookmarkDialog.dismiss();
					}
				});
		if(oldBookMarkBean==null){
			mEditBookmarkDialog.setBookmarkTitle(mWebView.getTitle());
			mEditBookmarkDialog.setBookmarkUrl(mWebView.getUrl());
		}else{
			mEditBookmarkDialog.setBookmarkTitle(oldBookMarkBean.getTitle());
			mEditBookmarkDialog.setBookmarkUrl(oldBookMarkBean.getUrl());
		}
		mEditBookmarkDialog.show();
	}

   private void startAsyncQuery() {
	    String userName=cmucApplication.getSettingsPreferences().getUserName();
    	mCmucDbManager.startQueryBookmarks(mQueryHandler,QUERY_BOOKMARK_TOKEN,userName);
    	mCmucDbManager.startQueryVisitHistory(mQueryHandler,QUERY_VISIT_HISTORY_TOKEN);
    }
   
	private void visitWeb() {
		if(mAddressVisited!=null){
			displayWebview();
			browserBarVisitStatus();
			mUrlEditText.setText(mAddressVisited);
			mWebView.loadUrl(mAddressVisited);
		}	
	}
	
	private void initView() {
		mRootView=findViewById(R.id.browser_root_view);
		initCommonView();		
		initHomeView();
		initWebView();
	}

	private void initWebView() {
		mWebViewPanel = (LinearLayout) findViewById(R.id.browser_web_panel);
		mVisitWebProgressBar = (ProgressBar) findViewById(R.id.load_web_progress);

		mBrowserButtonBack = (ImageView) findViewById(R.id.browser_bar_back);
		mBrowserButtonPresious = (ImageView) findViewById(R.id.browser_bar_preivous);
		mBrowserButtonRefresh = (ImageView) findViewById(R.id.browser_bar_refresh);
		mBrowserButtonHome = (ImageView) findViewById(R.id.browser_bar_home);
		mBrowserButtonCollection = (ImageView) findViewById(R.id.brwoser_bar_collection);
		browserBarHomeStatus();
		mBrowserButtonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mWebView.canGoBack()) {
					mWebView.goBack();
				}
			}

		});

		mBrowserButtonPresious.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mWebViewPanel.getVisibility() == View.GONE) {
					displayWebview();
					enableCollectionButton();
					enbaleHomeButton();
					enableRefreshButton();
					if (!mWebView.canGoForward()) {
						disablePresiousButton();
					}

					if (mWebView.canGoBack()) {
						enableBackButton();
					}
				} else {
					if (mWebView.canGoForward()) {
						mWebView.goForward();
					}
				}

			}

		});

		mBrowserButtonRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isLoadingWeb) {
					mWebView.stopLoading();
				} else {
					mWebView.reload();
				}
			}

		});

		mBrowserButtonHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mHomePanel.getVisibility() == View.GONE) {
					mHomePanel.setVisibility(View.VISIBLE);
				}
				if (mWebViewPanel.getVisibility() == View.VISIBLE) {
					mWebViewPanel.setVisibility(View.GONE);
				}
				disableHomeButton();
				disableCollectionButton();
				disableRefreshButton();
				disableBackButton();
				enablePresiousButton();
			}

		});

		mBrowserButtonCollection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				createAddBookmarkDialog(null);
			}

		});

		mWebView = (WebView) findViewById(R.id.browser_webview);
		mWebView.setWebChromeClient(new WebChromeClient() {
			// update the progress bar
			public void onProgressChanged(WebView view, int progress) {
				if (progress == 100) {
					mVisitWebProgressBar.setVisibility(View.GONE);
				}
			}
		});

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mVisitWebProgressBar.setVisibility(View.GONE);
				changeRefreshButtonIcon();
				if (mWebView.canGoBack()) {
					enableBackButton();
				} else {
					disableBackButton();
				}

				if (mWebView.canGoForward()) {
					enablePresiousButton();
				} else {
					disablePresiousButton();
				}
				VisitHistoryBean visitHistoryBean = new VisitHistoryBean(System
						.currentTimeMillis(), mWebView.getTitle(),
						mWebView.getUrl());
				visitHistoryBean.setUserName(mUserName);
				mCmucDbManager.recordVisitHistory(visitHistoryBean);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				mVisitWebProgressBar.setVisibility(View.VISIBLE);
				changeRefreshButtonIcon();
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				mVisitWebProgressBar.setVisibility(View.GONE);
			}
		});
	}

	private void initHomeView() {
		mBookMarksView = (TextView) findViewById(R.id.book_mark_text);
		mHistoryView = (TextView) findViewById(R.id.history_text);
		mBookMarksView.setOnClickListener(new SwitchViewOnClickListener(0));
		mHistoryView.setOnClickListener(new SwitchViewOnClickListener(1));
		mViews = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater();

		LinearLayout bookMarksPanel = (LinearLayout) inflater.inflate(
				R.layout.book_marks_panel, null);
		ListView mBookmarkListView = (ListView) bookMarksPanel
				.findViewById(R.id.bookmarks_list);
		mBookMarksAdapter=new BookMarksAdapter(this, null);
		mBookMarksAdapter.setOnContentChangedListener(mContentChangedListener);
		mBookmarkListView.setAdapter(mBookMarksAdapter);
		mBookmarkListView.setOnItemLongClickListener(mOnItemLongClickListener);
		mBookmarkListView.setOnItemClickListener(mOnItemSelectedListener);

		LinearLayout historyPanel = (LinearLayout) inflater.inflate(
				R.layout.history_panel, null);
		mHistoryExpandableListView=(ExpandableListView) historyPanel.findViewById(R.id.history_content);
		mHistoryExpandableAdapter=new HistoryExpandableAdapter(this, mHistoryGroupNames, mHistoryChildArray);
		mHistoryExpandableListView.setAdapter(mHistoryExpandableAdapter);
		mHistoryExpandableListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				VisitHistoryBean item = mHistoryChildArray.get(groupPosition).get(childPosition);
				mAddressVisited=item.getUrl();
				visitWeb();
				return false;
			}
		});
		
		mHistoryExpandableListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int postion, long arg3) {
				// TODO Auto-generated method stub
				Object obj =  v.getTag();
				if (obj instanceof ChildViewHolder) {
					ChildViewHolder holder = (ChildViewHolder)obj;
					VisitHistoryBean visitHistoryBean=mHistoryChildArray.get(holder.groupId).get(holder.childId);
					createOperateHistorysDialog(visitHistoryBean);
				}
				return true;
			}
		});
		
		mViews.add(bookMarksPanel);
		mViews.add(historyPanel);
		mViewPager = (ViewPager) findViewById(R.id.vPager);
		mViewPager.setAdapter(new ViewPagerAdapter(mViews));
		mViewPager.setCurrentItem(0);
		updateTabItem(0);
		mHomePanel = (LinearLayout) findViewById(R.id.browser_home);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				updateTabItem(arg0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private void initCommonView() {
		mVisitWebButton = (Button) findViewById(R.id.visi_web);
		mVisitWebButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAddressVisited = mUrlEditText.getText().toString();
				if (mAddressVisited == null|| TextUtils.isEmpty(mAddressVisited.trim())) {
					Toast.makeText(BrowserActivity.this,
							R.string.please_visit_url, Toast.LENGTH_LONG)
							.show();
					return;
				}
				if(!mAddressVisited.matches(URL_MATH_STR)){
					Toast.makeText(BrowserActivity.this,
							R.string.please_visit_vaild_url, Toast.LENGTH_LONG)
							.show();
					return;
				}
				visitWeb();
			}
		});
		mUrlEditText = (EditText) findViewById(R.id.address_visited);
	}

	private void initData() {
		Resources resource = (Resources) getBaseContext().getResources();
		mBookMarkTextNormalColor = resource.getColorStateList(R.color.browser_book_mark_text_normal);
		mHistoryGroupNames=resource.getStringArray(R.array.history_group_names);
		mOperateBookmarkItems=resource.getStringArray(R.array.operate_bookmarks); 
		mOperateHistoryItems=resource.getStringArray(R.array.operate_historys);
		mUserName = cmucApplication.getSettingsPreferences().getUserName();
		int groupSize=mHistoryGroupNames.length;
		for(int i=0;i<groupSize;i++){
			ArrayList<VisitHistoryBean> list=new ArrayList<VisitHistoryBean>();
			mHistoryChildArray.add(list);
		}
		initVisitUrl(getIntent());
		initDatabase();
	}

	private void initVisitUrl(Intent intent) {
		Uri uri = intent.getData();
		if (uri != null)
			mAddressVisited = uri.toString();
	}

	private void initDatabase() {
		mContentResolver=getContentResolver();
		mCmucDbManager = CmucDbManager.getInstance(mContentResolver);
		mQueryHandler=new QueryHandler(mContentResolver);
		mVisitHistoryObserver=new VisitHistoryObserver(mHandler);	
		mContentResolver.registerContentObserver(CmucStore.VisitHistory.CONTENT_URI, true, mVisitHistoryObserver);
		startAsyncQuery();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mContentResolver.unregisterContentObserver(mVisitHistoryObserver);	
	}
	
	class SwitchViewOnClickListener implements View.OnClickListener {
		private int index = 0;

		public SwitchViewOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			updateTabItem(index);
			mViewPager.setCurrentItem(index);
		}
	};

	class QueryHandler extends AsyncQueryHandler {

		public QueryHandler(ContentResolver cr) {
			super(cr);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			switch (token) {
			case QUERY_BOOKMARK_TOKEN:
				mBookMarksAdapter.changeCursor(cursor);
				mBookMarksAdapter.notifyDataSetInvalidated();
				break;
			case QUERY_VISIT_HISTORY_TOKEN:
				clearHistoryChildArray();
				int cout=cursor.getCount();
				if(cout>0){
					mCmucDbManager.cursorToVisitHistorys(cursor, mHistoryChildArray);
					mHistoryExpandableAdapter.notifyDataSetChanged();
				}
				break;
			}
		}

		private void clearHistoryChildArray() {
			for(ArrayList<VisitHistoryBean> list:mHistoryChildArray){
				list.clear();
			}
		}
	}
	
	
 class VisitHistoryObserver extends ContentObserver{

		public VisitHistoryObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			mCmucDbManager.startQueryVisitHistory(mQueryHandler, QUERY_VISIT_HISTORY_TOKEN);
		}
		
	}
 
}
