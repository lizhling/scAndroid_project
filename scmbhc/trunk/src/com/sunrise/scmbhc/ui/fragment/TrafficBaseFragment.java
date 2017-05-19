package com.sunrise.scmbhc.ui.fragment;

import java.util.ArrayList;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.database.ScmbhcDbManager;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskParams;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.activity.SingleFragmentActivity;

//import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
//import android.database.Cursor;
import android.os.Bundle;

public abstract class TrafficBaseFragment extends BaseFragment implements App.ExtraKeyConstant {

	public static final int KEY_ID_BASE = 100;
	public static final int KEY_ID_OVERLAY = 200;
	public static final int KEY_ID_IDLE = 300;
	public static final int STEP2 = 9527;// 查找上网套餐的下级菜单操作
	public static final int STEP1 = 2795;// 检索根菜单

	public static final String NAME_NET_PACKAGE = "menu_swtc";// "上网套餐";
	public static final String NAME_BASE_PACKAGE = "traffic_base";// "基础套餐";
	public static final String NAME_OVERLAY_PACKAGE = "traffic_overlay";// "叠加套餐";
	public static final String NAME_IDLE_PACKAGE = "traffic_idle";// "闲时套餐";

	protected ArrayList<BusinessMenu> mArrayBaseInfo, mArrayOverlay, mArrayIdle;
	protected String mNameBaseInfo, mNameOverlay, mNameIdle;

//	private ScmbhcDbManager mScmbhcDbManager;
//	private ContentResolver mContentResolver;
//	private AsyncQueryHandler mQueryHandler;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = savedInstanceState == null ? getArguments() : savedInstanceState;
		if (bundle != null) {
			mArrayBaseInfo = bundle.getParcelableArrayList(NAME_BASE_PACKAGE);
			mArrayOverlay = bundle.getParcelableArrayList(NAME_OVERLAY_PACKAGE);
			mArrayIdle = bundle.getParcelableArrayList(NAME_IDLE_PACKAGE);
			String[] tempArr = bundle.getStringArray(NAME_NET_PACKAGE);
			if (tempArr != null) {
				mNameBaseInfo = tempArr[0];
				mNameOverlay = tempArr[1];
				mNameIdle = tempArr[2];
			}
		} else {
			newListSpace();
//			initDatabase();
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList(NAME_BASE_PACKAGE, mArrayBaseInfo);
		outState.putParcelableArrayList(NAME_OVERLAY_PACKAGE, mArrayOverlay);
		outState.putParcelableArrayList(NAME_IDLE_PACKAGE, mArrayIdle);
		outState.putStringArray(NAME_NET_PACKAGE, new String[] { mNameBaseInfo, mNameOverlay, mNameIdle });
		super.onSaveInstanceState(outState);
	}

//	protected void initDatabase() {
//		mContentResolver = getActivity().getContentResolver();
//		mScmbhcDbManager = ScmbhcDbManager.getInstance(mContentResolver);
//		mQueryHandler = new QueryHandler(mContentResolver);
//	}

	/**
	 * 为三种容器初始化
	 */
	private void newListSpace() {
		// 基础
		if (mArrayBaseInfo == null)
			mArrayBaseInfo = new ArrayList<BusinessMenu>();

		// 叠加
		if (mArrayOverlay == null)
			mArrayOverlay = new ArrayList<BusinessMenu>();

		// 闲时
		if (mArrayIdle == null)
			mArrayIdle = new ArrayList<BusinessMenu>();
	}

	/**
	 * 开始从数据库查询所需要的业务列表
	 * @param listener 
	 */
	protected void startQueryBusinessMenus(TaskListener listener) {
		// mScmbhcDbManager.startQueryBusinessMenu(mQueryHandler, STEP1,
		// BusinessMenu.ROOT_BUSINESSMEUN_ID);
		AsyncQueryBusinessMenu task = new AsyncQueryBusinessMenu();
		task.setListener(listener);
		task.execute(new TaskParams("tag", NAME_NET_PACKAGE));
	}
	
	protected void startQueryBusinessMenus(){
		startQueryBusinessMenus(new TaskListener() {

			@Override
			public void onProgressUpdate(GenericTask task, Object param) {

			}

			@Override
			public void onPreExecute(GenericTask task) {
			}

			@Override
			public void onPostExecute(GenericTask task, TaskResult result) {
				onCompleteBaseNetList();
				onCompleteOverlayNetList();
				onCompleteIdleNetList();
			}

			@Override
			public void onCancelled(GenericTask task) {
				// TODO Auto-generated method stub

			}

			@Override
			public String getName() {
				return null;
			}
		});
	}

	/**
	 * 启动业务办理
	 * 
	 * @param item
	 */
	public void goBusinessDetailPage(BusinessMenu item) {

		Intent intent = new Intent(mBaseActivity, SingleFragmentActivity.class);
		intent.putExtra(App.ExtraKeyConstant.KEY_FRAGMENT, DefaultBusinessDetailFragment1.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable(KEY_BUSINESS_INFO, item);
		intent.putExtra(App.ExtraKeyConstant.KEY_BUNDLE, bundle);
		mBaseActivity.startActivity(intent);
	}

	/**
	 * 办理成功，刷新业务逻辑
	 */

//	class QueryHandler extends AsyncQueryHandler {
//
//		public QueryHandler(ContentResolver cr) {
//			super(cr);
//		}
//
//		@Override
//		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
//			ArrayList<BusinessMenu> subBusinessMenus = mScmbhcDbManager.cursorToBusinessBusinessMenus(cursor);
//			switch (token) {
//			case STEP1:
//				for (BusinessMenu item : subBusinessMenus) {
//					// if (item.getName().equals(NAME_NET_PACKAGE)) {
//					if (NAME_NET_PACKAGE.equalsIgnoreCase(item.getBusTag())) {
//						mScmbhcDbManager.startQueryBusinessMenu(this, STEP2, item.getId());
//						break;
//					}
//				}
//				break;
//			case STEP2:
//				for (BusinessMenu item : subBusinessMenus) {
//					if (NAME_BASE_PACKAGE.equalsIgnoreCase(item.getBusTag())) {// 基础套餐
//						mScmbhcDbManager.startQueryBusinessMenu(this, KEY_ID_BASE, item.getId());
//						mNameBaseInfo = item.getName();
//					} else if (NAME_OVERLAY_PACKAGE.equalsIgnoreCase(item.getBusTag())) {// 叠加套餐
//						mScmbhcDbManager.startQueryBusinessMenu(this, KEY_ID_OVERLAY, item.getId());
//						mNameOverlay = item.getName();
//					} else if (NAME_IDLE_PACKAGE.equalsIgnoreCase(item.getBusTag())) {// 闲时套餐
//						mScmbhcDbManager.startQueryBusinessMenu(this, KEY_ID_IDLE, item.getId());
//						mNameIdle = item.getName();
//					}
//				}
//				break;
//			case KEY_ID_BASE:
//				// sortingAlgorithm(subBusinessMenus);
//				mArrayBaseInfo.addAll(subBusinessMenus);
//				onCompleteBaseNetList();
//				break;
//			case KEY_ID_OVERLAY:
//				// sortingAlgorithm(subBusinessMenus);
//				mArrayOverlay.addAll(subBusinessMenus);
//				onCompleteOverlayNetList();
//				break;
//			case KEY_ID_IDLE:
//				// sortingAlgorithm(subBusinessMenus);
//				mArrayIdle.addAll(subBusinessMenus);
//				onCompleteIdleNetList();
//				break;
//			default:
//				break;
//			}
//		}
//	}

	/**
	 * 基础流量列表
	 */
	protected abstract void onCompleteBaseNetList();

	/**
	 * 叠加流量列表设置
	 */
	protected abstract void onCompleteOverlayNetList();

	/**
	 * 显示列表设置
	 */
	protected abstract void onCompleteIdleNetList();

	// protected void sortingAlgorithm(ArrayList<BusinessMenu> array) {
	// final int length = array.size();
	// for (int i = 0; i < length - 1; ++i) {
	// float f1 = CommUtil.parse2Float(array.get(i).getName());
	//
	// for (int j = i + 1; j < length; ++j) {
	// BusinessMenu item2 = array.get(j);
	// if (f1 > CommUtil.parse2Float(item2.getName())) {
	// array.remove(item2);
	// array.add(i, item2);
	// }
	// }
	// }
	//
	// }

	/*
	 * 功能: 为用户行为提供页面名称
	 * 
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.TrafficBaseFragment;
	}

	class AsyncQueryBusinessMenu extends GenericTask {

		@Override
		protected TaskResult _doInBackground(TaskParams... params) {

			String busTag = params[0].getString("tag");
			
			ContentResolver contentResolver = getActivity().getContentResolver();
			ScmbhcDbManager scmbhcDbManager = ScmbhcDbManager.getInstance(contentResolver);
			
			BusinessMenu rootmenu = scmbhcDbManager.queryBusinessMenuByBusTag(busTag);
			ArrayList<BusinessMenu> subBusinessMenus = scmbhcDbManager.queryBusinessMenuByBusParentId(rootmenu.getId());
			for (BusinessMenu item : subBusinessMenus) {
				if (NAME_BASE_PACKAGE.equalsIgnoreCase(item.getBusTag())) {// 基础套餐
					mArrayBaseInfo = scmbhcDbManager.queryBusinessMenuByBusParentId(item.getId());
					mNameBaseInfo = item.getName();
				} else if (NAME_OVERLAY_PACKAGE.equalsIgnoreCase(item.getBusTag())) {// 叠加套餐
					mArrayOverlay = scmbhcDbManager.queryBusinessMenuByBusParentId(item.getId());
					mNameOverlay = item.getName();
				} else if (NAME_IDLE_PACKAGE.equalsIgnoreCase(item.getBusTag())) {// 闲时套餐
					mArrayIdle = scmbhcDbManager.queryBusinessMenuByBusParentId(item.getId());
					mNameIdle = item.getName();
				}
			}

			return TaskResult.OK;
		}
	}

}
