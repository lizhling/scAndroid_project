package com.sunrise.marketingassistant.database;

import java.util.ArrayList;

import com.sunrise.marketingassistant.entity.CollectBranch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CmmaDBHelper extends SQLiteOpenHelper {
	private final static String DB_NAME = "marketingasstant.db";
	private final static int VERSION = 5;
	public final static String TABLE_COLLECT_BRANCH = "collect_branch";

	public CmmaDBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public CmmaDBHelper(Context context) {
		this(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_COLLECT_BRANCH + "(" + CmmaStore.CollectBranch._ID + " LONG PRIMARY KEY," + CmmaStore.CollectBranch.ACCOUNT
				+ " TEXT," + CmmaStore.CollectBranch.GROUP_ID + " TEXT," + CmmaStore.CollectBranch.GROUP_NAME + " TEXT," + CmmaStore.CollectBranch.CLASS_NAME
				+ " TEXT," + CmmaStore.CollectBranch.GROUP_ADDRESS + " TEXT" + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion != newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_COLLECT_BRANCH);
			onCreate(db);
		}
	}

	public Cursor select() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_COLLECT_BRANCH, null, null, null, null, null, null);
		return cursor;
	}

	// 增加操作
	private long insert(ContentValues cv) {
		SQLiteDatabase db = this.getWritableDatabase();
		/* ContentValues */
		long row = db.insert(TABLE_COLLECT_BRANCH, null, cv);
		db.close();
		return row;
	}

	// 删除操作
	public int deleteByGroupId(String account, String groupId) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = CmmaStore.CollectBranch.GROUP_ID + "=? and " + CmmaStore.CollectBranch.ACCOUNT + "=?";
		String[] whereValue = { groupId, account };
		int result = db.delete(TABLE_COLLECT_BRANCH, where, whereValue);
		db.close();
		return result;
	}

	// 删除操作
	public int deleteByAccount(String account) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = CmmaStore.CollectBranch.ACCOUNT + " = ?";
		String[] whereValue = { account };
		int result = db.delete(TABLE_COLLECT_BRANCH, where, whereValue);
		db.close();
		return result;
	}

	private static String sCollectBranchProjection[] = { CmmaStore.CollectBranch.ACCOUNT, CmmaStore.CollectBranch.GROUP_ID, CmmaStore.CollectBranch.GROUP_NAME,
			CmmaStore.CollectBranch.CLASS_NAME, CmmaStore.CollectBranch.GROUP_ADDRESS };

	public int getCollectBranchCountByGroupID(String account, String groupId) {
		SQLiteDatabase db = this.getReadableDatabase();
		int count = 0;
		String[] args = { account, groupId };
		String where = CmmaStore.CollectBranch.ACCOUNT + "=?" + " and " + CmmaStore.CollectBranch.GROUP_ID + "=?";
		Cursor cursor = db.query(TABLE_COLLECT_BRANCH, sCollectBranchProjection, where, args, null, null, null);
		if (cursor != null)
			count = cursor.getCount();
		db.close();
		return count;
	}

	public int getCollectBranchCountByAccount(String account) {
		SQLiteDatabase db = this.getReadableDatabase();
		int count = 0;
		String[] args = { account };
		String where = CmmaStore.CollectBranch.ACCOUNT + " = ?";
		Cursor cursor = db.query(TABLE_COLLECT_BRANCH, sCollectBranchProjection, where, args, null, null, null);
		if (cursor != null)
			count = cursor.getCount();
		db.close();
		return count;
	}

	public int getCollectBranchCountByAccountAndGroupId(String account, String groupId) {
		SQLiteDatabase db = this.getReadableDatabase();
		int count = 0;
		String[] args = { account, groupId };
		String where = CmmaStore.CollectBranch.ACCOUNT + "=?" + " and " + CmmaStore.CollectBranch.GROUP_ID + "=?";
		Cursor cursor = db.query(TABLE_COLLECT_BRANCH, sCollectBranchProjection, where, args, null, null, null);
		if (cursor != null)
			count = cursor.getCount();
		db.close();
		return count;
	}

	public ArrayList<CollectBranch> searchCollectBranchByGroupId(String groupid) {
		SQLiteDatabase db = this.getReadableDatabase();
		StringBuilder where = new StringBuilder();
		where.append(CmmaStore.CollectBranch.GROUP_ID + " LIKE " + "'%" + groupid + "%'");
		ArrayList<CollectBranch> businessMenus = new ArrayList<CollectBranch>();
		Cursor cursor = db.query(TABLE_COLLECT_BRANCH, sCollectBranchProjection, where.toString(), null, null, null, null);
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			CollectBranch businessMenu = cursorToCollectBranch(cursor);
			businessMenus.add(businessMenu);
		}
		db.close();
		return businessMenus;
	}

	public ArrayList<CollectBranch> searchCollectBranchByAccount(String account) {
		SQLiteDatabase db = this.getReadableDatabase();
		StringBuilder where = new StringBuilder();
		where.append(CmmaStore.CollectBranch.ACCOUNT + " LIKE " + "'%" + account + "%'");
		ArrayList<CollectBranch> businessMenus = new ArrayList<CollectBranch>();
		Cursor cursor = db.query(TABLE_COLLECT_BRANCH, sCollectBranchProjection, where.toString(), null, null, null, null);
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			CollectBranch businessMenu = cursorToCollectBranch(cursor);
			businessMenus.add(businessMenu);
		}
		db.close();
		return businessMenus;
	}

	public ArrayList<CollectBranch> queryCollectBranch() {
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<CollectBranch> businessMenus = new ArrayList<CollectBranch>();
		Cursor cursor = db.query(TABLE_COLLECT_BRANCH, sCollectBranchProjection, null, null, null, null, null);
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			CollectBranch businessMenu = cursorToCollectBranch(cursor);
			businessMenus.add(businessMenu);
		}
		db.close();
		return businessMenus;
	}

	public ArrayList<CollectBranch> queryCollectBranchByAccount(String account) {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] args = { account };
		String where = CmmaStore.CollectBranch.ACCOUNT + "=?";
		Cursor cursor = db.query(TABLE_COLLECT_BRANCH, sCollectBranchProjection, where, args, null, null, null);
		ArrayList<CollectBranch> array = null;
		if (cursor.getCount() > 0) {
			array = new ArrayList<CollectBranch>();
			cursor.moveToFirst();
			do {
				array.add(cursorToCollectBranch(cursor));
			} while ((cursor.moveToNext()));
		}
		db.close();
		return array;
	}

	public ArrayList<CollectBranch> queryCollectBranchByGroupId(String account, String groupId) {
		SQLiteDatabase db = this.getReadableDatabase();

		String[] args = { account, groupId };
		String where = CmmaStore.CollectBranch.ACCOUNT + "=?" + " and " + CmmaStore.CollectBranch.GROUP_ID + "=?";
		Cursor cursor = db.query(TABLE_COLLECT_BRANCH, sCollectBranchProjection, where, args, null, null, null);

		ArrayList<CollectBranch> array = null;
		if (cursor.getCount() > 0) {
			array = new ArrayList<CollectBranch>();
			cursor.moveToFirst();
			do {
				array.add(cursorToCollectBranch(cursor));
			} while ((cursor.moveToNext()));
		}
		db.close();
		return array;
	}

	public long insert(CollectBranch collect) {
		if (collect == null)
			return -2;

		if (getCollectBranchCountByAccountAndGroupId(collect.getAccount(), collect.getGROUP_ID()) == 0)
			return insert(CollectBranch2ContentValue(collect));
		return -1;
	}

	private CollectBranch cursorToCollectBranch(Cursor cursor) {
		String groupId = cursor.getString(cursor.getColumnIndex(CmmaStore.CollectBranch.GROUP_ID));
		String account = cursor.getString(cursor.getColumnIndex(CmmaStore.CollectBranch.ACCOUNT));
		String groupname = cursor.getString(cursor.getColumnIndex(CmmaStore.CollectBranch.GROUP_NAME));
		String classname = cursor.getString(cursor.getColumnIndex(CmmaStore.CollectBranch.CLASS_NAME));
		String address = cursor.getString(cursor.getColumnIndex(CmmaStore.CollectBranch.GROUP_ADDRESS));
		CollectBranch collect = new CollectBranch();
		collect.setAccount(account);
		collect.setGROUP_ADDRESS(address);
		collect.setCLASS_NAME(classname);
		collect.setGROUP_ID(groupId);
		collect.setGROUP_NAME(groupname);
		return collect;
	}

	private ContentValues CollectBranch2ContentValue(CollectBranch collect) {
		ContentValues value = new ContentValues();
		String account = collect.getAccount();
		String groupid = collect.getGROUP_ID();
		String classname = collect.getCLASS_NAME();
		String address = collect.getGROUP_ADDRESS();
		String groupName = collect.getGROUP_NAME();
		if (groupid != null)
			value.put(CmmaStore.CollectBranch.GROUP_ID, groupid);
		if (account != null)
			value.put(CmmaStore.CollectBranch.ACCOUNT, account);
		if (classname != null)
			value.put(CmmaStore.CollectBranch.CLASS_NAME, classname);
		if (address != null)
			value.put(CmmaStore.CollectBranch.GROUP_ADDRESS, address);
		if (groupName != null)
			value.put(CmmaStore.CollectBranch.GROUP_NAME, groupName);
		return value;
	}

}
