//package com.sunrise.marketingassistant.database;
//
//import java.util.ArrayList;
//
//import com.sunrise.marketingassistant.entity.CollectBranch;
//import com.sunrise.marketingassistant.utils.LogUtlis;
//
//import android.content.AsyncQueryHandler;
//import android.content.ContentResolver;
//import android.content.ContentValues;
//import android.database.Cursor;
//import android.text.TextUtils;
//
//public class CmmaDbManager {
//	public final static int NO_DELETE_FALG_ITEM = 0;
//	public final static int DELETE_FALG_ITEM = 1;
//	public final static int NO_CONTAIN_ITEM = 2;
//	private ContentResolver cr;
//	// private static String TAG="MessageManager";
//	private static String sCollectBranchProjection[] = { CmmaStore.CollectBranch._ID, CmmaStore.CollectBranch.ACCOUNT, CmmaStore.CollectBranch.GROUP_ID,
//			CmmaStore.CollectBranch.GROUP_NAME, CmmaStore.CollectBranch.CLASS_NAME, CmmaStore.CollectBranch.GROUP_ADDRESS };
//	private static CmmaDbManager instance = null;
//
//	// private static String sBusinessMenuProjection[] = {
//	// CmmaStore.CollectBranch._ID, CmmaStore.CollectBranch.PARENT_ID,
//	// CmmaStore.CollectBranch.MENU_TYPE,
//	// CmmaStore.CollectBranch.ORDER, CmmaStore.CollectBranch.NAME,
//	// CmmaStore.CollectBranch.ICON, CmmaStore.CollectBranch.DESCRIPTION,
//	// CmmaStore.CollectBranch.CHARGES, CmmaStore.CollectBranch.WARMPROMPT,
//	// CmmaStore.CollectBranch.BUSINESS_DATA,
//	// CmmaStore.CollectBranch.SERVICE_URL, CmmaStore.CollectBranch.PROD_PRCID,
//	// CmmaStore.CollectBranch.BUS_TAG, CmmaStore.CollectBranch.BUS_APP_DATA };
//
//	private CmmaDbManager(ContentResolver cr) {
//		super();
//		this.cr = cr;
//	}
//
//	public static CmmaDbManager getInstance(ContentResolver cr) {
//		if (instance == null)
//			instance = new CmmaDbManager(cr);
//		return instance;
//	}
//
//	public void startCollectBranch(AsyncQueryHandler queryHandler, int token, String groupId) {
//		StringBuilder where = new StringBuilder();
//		where.append(CmmaStore.CollectBranch.GROUP_ID + "==" + groupId);
//		queryHandler
//				.startQuery(token, where.toString(), CmmaStore.CollectBranch.CONTENT_URI, sCollectBranchProjection, where.toString(), null, CmmaStore.ORDER);
//	}
//
//	public int getCollectBranchCount() {
//		int count = 0;
//		Cursor cursor = cr.query(CmmaStore.CollectBranch.CONTENT_URI, sCollectBranchProjection, null, null, null);
//		if (cursor != null)
//			count = cursor.getCount();
//		return count;
//	}
//
//	public ArrayList<CollectBranch> searchCollectBranchByGroupId(String groupid) {
//		StringBuilder where = new StringBuilder();
//		where.append(CmmaStore.CollectBranch.GROUP_ID + " LIKE " + "'%" + groupid + "%'");
//		ArrayList<CollectBranch> businessMenus = new ArrayList<CollectBranch>();
//		Cursor cursor = cr.query(CmmaStore.CollectBranch.CONTENT_URI, sCollectBranchProjection, where.toString(), null, CmmaStore.ORDER);
//		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//			CollectBranch businessMenu = (cursor);
//			businessMenus.add(businessMenu);
//		}
//		return businessMenus;
//	}
//
//	public ArrayList<CollectBranch> searchCollectBranchByAccount(String account) {
//		StringBuilder where = new StringBuilder();
//		where.append(CmmaStore.CollectBranch.ACCOUNT + " LIKE " + "'%" + account + "%'");
//		ArrayList<CollectBranch> businessMenus = new ArrayList<CollectBranch>();
//		Cursor cursor = cr.query(CmmaStore.CollectBranch.CONTENT_URI, sCollectBranchProjection, where.toString(), null, CmmaStore.ORDER);
//		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//			CollectBranch businessMenu = cursorToCollectBranch(cursor);
//			businessMenus.add(businessMenu);
//		}
//		return businessMenus;
//	}
//
//	public ArrayList<CollectBranch> queryCollectBranch() {
//		ArrayList<CollectBranch> businessMenus = new ArrayList<CollectBranch>();
//		Cursor cursor = cr.query(CmmaStore.CollectBranch.CONTENT_URI, sCollectBranchProjection, null, null, CmmaStore.ORDER);
//		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//			CollectBranch businessMenu = cursorToCollectBranch(cursor);
//			businessMenus.add(businessMenu);
//		}
//		return businessMenus;
//	}
//
//	public CollectBranch queryCollectBranchByAccount(String account) {
//		StringBuilder where = new StringBuilder();
//		where.append(CmmaStore.CollectBranch.ACCOUNT + "==" + "'" + account + "'");
//		Cursor cursor = cr.query(CmmaStore.CollectBranch.CONTENT_URI, sCollectBranchProjection, where.toString(), null, CmmaStore.ORDER);
//		CollectBranch businessMenu = null;
//		if (cursor.getCount() > 0) {
//			cursor.moveToFirst();
//			businessMenu = cursorToCollectBranch(cursor);
//		}
//		return businessMenu;
//	}
//
//	public ArrayList<CollectBranch> queryCollectBranchByGroupId(String groupId) {
//		StringBuilder where = new StringBuilder();
//		where.append(CmmaStore.CollectBranch.GROUP_ID + "==" + "'" + groupId + "'");
//		Cursor cursor = cr.query(CmmaStore.CollectBranch.CONTENT_URI, sCollectBranchProjection, where.toString(), null, CmmaStore.ORDER);
//		ArrayList<CollectBranch> businessMenu = null;
//		if (cursor.getCount() > 0) {
//			businessMenu = new ArrayList<CollectBranch>();
//			cursor.moveToFirst();
//			do {
//				businessMenu.add(cursorToCollectBranch(cursor));
//			} while ((cursor.moveToNext()));
//		}
//		return businessMenu;
//	}
//
//	public BusinessMenu queryBusinessMenuByPrcID(String prcID) {
//		StringBuilder where = new StringBuilder();
//		where.append(CmmaStore.CollectBranch.PROD_PRCID).append(" like \'%" + getCodingObject().encode(prcID) + "%\'");
//		Cursor cursor = cr.query(CmmaStore.CollectBranch.CONTENT_URI, sBusinessMenuProjection, where.toString(), null,
//				CmmaStore.CollectBranch.DEFAULT_SORT_ORDER);
//		CollectBranch businessMenu = null;
//		if (cursor.getCount() > 0) {
//			cursor.moveToFirst();
//			businessMenu = cursorToBusinessBusinessMenu(cursor);
//		}
//		cursor.close();
//		return businessMenu;
//	}
//
//	public ArrayList<CollectBranch> cursorToBusinessBusinessMenus(Cursor cursor) {
//		ArrayList<CollectBranch> messages = new ArrayList<CollectBranch>();
//		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//			CollectBranch businessMenu = cursorToBusinessBusinessMenu(cursor);
//			messages.add(businessMenu);
//		}
//		return messages;
//	}
//
//	public synchronized void recordBusinessMenu(CollectBranch collect) {
//		ContentValues values = CollectBranch2ContentValue(collect);
//		cr.insert(CmmaStore.CollectBranch.CONTENT_URI, values);
//	}
//
//	public void recordBusinessMenuList(ArrayList<CollectBranch> datas) {
//		if (datas == null)
//			return;
//		for (CollectBranch collect : datas) {
//			recordBusinessMenu(collect);
//		}
//	}
//
//	public synchronized void updateMenuBusiness(String groupId) {
//		StringBuilder where = new StringBuilder();
//		where.append(CmmaStore.CollectBranch._ID + "==" + groupId);
//		ContentValues values = CollectBranch2ContentValue(collect);
//		cr.update(CmmaStore.CollectBranch.CONTENT_URI, values, where.toString(), null);
//	}
//
//	public synchronized void deleteCollectBranchByAccount(String account) {
//		StringBuilder where = new StringBuilder();
//		where.append(CmmaStore.CollectBranch.ACCOUNT + "==" + account);
//		cr.delete(CmmaStore.CollectBranch.CONTENT_URI, where.toString(), null);
//	}
//
//	public synchronized void deleteCollectBranch() {
//		cr.delete(CmmaStore.CollectBranch.CONTENT_URI, null, null);
//	}
//
//	public synchronized void startDeleteBusinessMenu(AsyncQueryHandler handler, int token, CollectBranch businessMenu) {
//		StringBuilder where = new StringBuilder();
//		where.append(CmmaStore.CollectBranch._ID + "==" + businessMenu.getId());
//		handler.startDelete(token, null, CmmaStore.CollectBranch.CONTENT_URI, where.toString(), null);
//	}
//
//	public synchronized void startDeleteAllBusinessMenu(AsyncQueryHandler handler, int token) {
//		handler.startDelete(token, null, CmmaStore.CollectBranch.CONTENT_URI, null, null);
//	}
//
//	public CollectBranch cursorToCollectBranch(Cursor cursor) {
//		long id = cursor.getLong(cursor.getColumnIndex(CmmaStore.CollectBranch._ID));
//		String groupId = cursor.getString(cursor.getColumnIndex(CmmaStore.CollectBranch.GROUP_ID));
//		String account = cursor.getString(cursor.getColumnIndex(CmmaStore.CollectBranch.ACCOUNT));
//		String groupname = cursor.getString(cursor.getColumnIndex(CmmaStore.CollectBranch.GROUP_NAME));
//		String classname = cursor.getString(cursor.getColumnIndex(CmmaStore.CollectBranch.CLASS_NAME));
//		String address = cursor.getString(cursor.getColumnIndex(CmmaStore.CollectBranch.GROUP_ADDRESS));
//		CollectBranch collect = new CollectBranch();
//		collect.setAccount(account);
//		collect.setGROUP_ADDRESS(address);
//		collect.setCLASS_NAME(classname);
//		collect.setGROUP_ID(groupId);
//		collect.setGROUP_NAME(groupname);
//		return collect;
//	}
//
//	private ContentValues CollectBranch2ContentValue(CollectBranch collect) {
//		ContentValues value = new ContentValues();
//		String account = collect.getAccount();
//		String groupid = collect.getGROUP_ID();
//		String classname = collect.getCLASS_NAME();
//		String address = collect.getGROUP_ADDRESS();
//		String groupName = collect.getGROUP_NAME();
//		if (groupid != null)
//			value.put(CmmaStore.CollectBranch.GROUP_ID, groupid);
//		if (account != null)
//			value.put(CmmaStore.CollectBranch.ACCOUNT, account);
//		if (classname != null)
//			value.put(CmmaStore.CollectBranch.CLASS_NAME, classname);
//		if (address != null)
//			value.put(CmmaStore.CollectBranch.GROUP_ADDRESS, address);
//		if (groupName != null)
//			value.put(CmmaStore.CollectBranch.GROUP_NAME, groupName);
//		return value;
//	}
//
//}
