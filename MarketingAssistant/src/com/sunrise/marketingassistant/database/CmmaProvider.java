//package com.sunrise.marketingassistant.database;
//
//import android.content.ContentProvider;
//import android.content.ContentUris;
//import android.content.ContentValues;
//import android.content.UriMatcher;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteQueryBuilder;
//import android.net.Uri;
//import android.text.TextUtils;
//
//public class CmmaProvider extends ContentProvider {
//	// private static String TAG="MessageProvider";
//	private final static int BUSINESSMENU = 1;
//	private final static int BUSINESSMENU_ID = 2;
//	private final static int MESSAGES = 3;
//	private final static int MESSAGE_ID = 4;
//	public CmmaDBHelper openHelper = null;
//
//	private final static UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
//
//	static {
//		URI_MATCHER.addURI(CmmaStore.AUTHORIY, CmmaStore.CollectBranch.ACCOUNT, BUSINESSMENU);
//		URI_MATCHER.addURI(CmmaStore.AUTHORIY, CmmaStore.CollectBranch.ACCOUNT + "/#", BUSINESSMENU_ID);
//		URI_MATCHER.addURI(CmmaStore.AUTHORIY, "messages", MESSAGES);
//		URI_MATCHER.addURI(CmmaStore.AUTHORIY, "messages/#", MESSAGE_ID);
//	}
//
//	static final GetTableAndWhereOutParameter sGetTableAndWhereParam = new GetTableAndWhereOutParameter();
//
//	@Override
//	public int delete(Uri uri, String userWhere, String[] whereArgs) {
//		// TODO Auto-generated method stub
//		int count;
//		int match = URI_MATCHER.match(uri);
//		SQLiteDatabase db = openHelper.getWritableDatabase();
//		getTableAndWhere(uri, match, userWhere, sGetTableAndWhereParam);
//		count = db.delete(sGetTableAndWhereParam.table, sGetTableAndWhereParam.where, whereArgs);
//		return count;
//	}
//
//	@Override
//	public String getType(Uri uri) {
//		// TODO Auto-generated method stub
//		switch (URI_MATCHER.match(uri)) {
//		case BUSINESSMENU:
//			return ScmbhcStore.BusinessMenu.CONTENT_TYPE;
//		case BUSINESSMENU_ID:
//			return ScmbhcStore.BusinessMenu.ENTRY_CONTENT_TYPE;
//		case MESSAGES:
//			return ScmbhcStore.Messages.CONTENT_TYPE;
//		case MESSAGE_ID:
//			return ScmbhcStore.Messages.ENTRY_CONTENT_TYPE;
//		}
//		throw new IllegalStateException("Unknown URL");
//	}
//
//	@Override
//	public Uri insert(Uri uri, ContentValues initialValues) {
//		Uri newUri = insertInternal(uri, initialValues);
//		return newUri;
//	}
//
//	private Uri insertInternal(Uri uri, ContentValues initialValues) {
//		long rowId;
//		Uri newUri = null;
//		;
//		String table;
//		String nullColumnHack;
//		SQLiteDatabase db = openHelper.getWritableDatabase();
//		// TODO Auto-generated method stub
//		switch (URI_MATCHER.match(uri)) {
//		case BUSINESSMENU:
//		case BUSINESSMENU_ID:
//			table = CmmaDBHelper.BUSINESSMEMNU_TABLE;
//			nullColumnHack = BusinessMenu._ID;
//			break;
//		case MESSAGES:
//		case MESSAGE_ID:
//			table = CmmaDBHelper.MESSAGE_TABLE;
//			nullColumnHack = Messages._ID;
//			break;
//		default:
//			throw new UnsupportedOperationException("Invalid URI " + uri);
//		}
//
//		if (table != null) {
//			rowId = db.insert(table, nullColumnHack, initialValues);
//			if (rowId > 0) {
//				newUri = ContentUris.withAppendedId(uri, rowId);
//			}
//		} else {
//			throw new UnsupportedOperationException("Invalid URI " + uri);
//		}
//
//		return newUri;
//	}
//
//	@Override
//	public boolean onCreate() {
//		// TODO Auto-generated method stub
//		openHelper = new CmmaDBHelper(this.getContext());
//		return true;
//	}
//
//	@Override
//	public Cursor query(Uri uri, String[] projectionIn, String selection, String[] selectionArgs, String sort) {
//		int table = URI_MATCHER.match(uri);
//		SQLiteDatabase db = openHelper.getReadableDatabase();
//		SQLiteQueryBuilder sq = new SQLiteQueryBuilder();
//		String limit = uri.getQueryParameter("limit");
//		String groupBy = null;
//		sq.setTables(CmmaStore.TABLE_COLLECT_BRANCH);
//		Cursor c = sq.query(db, projectionIn, selection, selectionArgs, groupBy, null, sort, limit);
//		return c;
//	}
//
//	@Override
//	public int update(Uri uri, ContentValues initialValues, String userWhere, String[] whereArgs) {
//		// TODO Auto-generated method stub
//		int count;
//		int match = URI_MATCHER.match(uri);
//		SQLiteDatabase db = openHelper.getWritableDatabase();
//		getTableAndWhere(uri, match, userWhere, sGetTableAndWhereParam);
//		count = db.update(sGetTableAndWhereParam.table, initialValues, sGetTableAndWhereParam.where, whereArgs);
//		return count;
//	}
//
//	private static final class GetTableAndWhereOutParameter {
//		public String table;
//		public String where;
//	}
//
//	private void getTableAndWhere(Uri uri, int match, String userWhere, GetTableAndWhereOutParameter out) {
//		String where = null;
//		switch (match) {
//		case BUSINESSMENU:
//			out.table = CmmaDBHelper.BUSINESSMEMNU_TABLE;
//			break;
//		case BUSINESSMENU_ID:
//			out.table = CmmaDBHelper.BUSINESSMEMNU_TABLE;
//			where = "_id=" + uri.getPathSegments().get(1);
//		case MESSAGES:
//			out.table = CmmaDBHelper.MESSAGE_TABLE;
//			break;
//		case MESSAGE_ID:
//			out.table = CmmaDBHelper.MESSAGE_TABLE;
//			where = "_id=" + uri.getPathSegments().get(1);
//		default:
//			throw new UnsupportedOperationException("Unknown or unsupported URL: " + uri.toString());
//		}
//		// Add in the user requested WHERE clause, if needed
//		if (!TextUtils.isEmpty(userWhere)) {
//			if (!TextUtils.isEmpty(where)) {
//				out.where = where + " AND (" + userWhere + ")";
//			} else {
//				out.where = userWhere;
//			}
//		} else {
//			out.where = where;
//		}
//	}
//
//}
