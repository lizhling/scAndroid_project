package com.starcpt.cmuc.db;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.starcpt.cmuc.db.CmucStore.BookMarks;
import com.starcpt.cmuc.db.CmucStore.CollectionBusinesses;
import com.starcpt.cmuc.db.CmucStore.Messages;
import com.starcpt.cmuc.db.CmucStore.VisitHistory;

public class CmucProvider extends ContentProvider {
	//private static String TAG="MessageProvider";
	private final static int MESSAGES=1;
	private final static int MESSAGE_ID=2;
	private final static int COLLECTION_BUSINESSES=3;
	private final static int COLLECTION_BUSINESS_ID=4;
	private final static int BOOKMARKS=5;
	private final static int BOOKMARK_ID=6;
	private final static int VISIT_HISTORYS=7;
	private final static int VISIT_HISTORY_ID=8;
	public CmucDBHelper openHelper = null;  
	
	private final static UriMatcher URI_MATCHER=new UriMatcher(UriMatcher.NO_MATCH);
	
	static{
		URI_MATCHER.addURI(CmucStore.AUTHORIY, "messages", MESSAGES);
		URI_MATCHER.addURI(CmucStore.AUTHORIY, "messages/#", MESSAGE_ID);
		URI_MATCHER.addURI(CmucStore.AUTHORIY, "collection_business", COLLECTION_BUSINESSES);
		URI_MATCHER.addURI(CmucStore.AUTHORIY, "collection_business/#", COLLECTION_BUSINESS_ID);
		URI_MATCHER.addURI(CmucStore.AUTHORIY, "bookmark", BOOKMARKS);
		URI_MATCHER.addURI(CmucStore.AUTHORIY, "bookmark/#", BOOKMARK_ID);
		URI_MATCHER.addURI(CmucStore.AUTHORIY, "visit_history", VISIT_HISTORYS);
		URI_MATCHER.addURI(CmucStore.AUTHORIY, "visit_history/#", VISIT_HISTORY_ID);
		
	}

    static final GetTableAndWhereOutParameter sGetTableAndWhereParam =
        new GetTableAndWhereOutParameter();

    
	@Override
	public int delete(Uri uri, String userWhere, String[] whereArgs) {
		// TODO Auto-generated method stub
		    int count;
	        int match = URI_MATCHER.match(uri);
	        SQLiteDatabase db=openHelper.getWritableDatabase();
	        getTableAndWhere(uri, match, userWhere, sGetTableAndWhereParam);
	        count=db.delete(sGetTableAndWhereParam.table,sGetTableAndWhereParam.where, whereArgs);
	        if (count > 0 && !db.inTransaction()) {
	            getContext().getContentResolver().notifyChange(uri, null);
	        }
		    return count;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch(URI_MATCHER.match(uri)){
		case MESSAGES:
			return CmucStore.Messages.CONTENT_TYPE;
		case MESSAGE_ID:
		    return CmucStore.Messages.ENTRY_CONTENT_TYPE;
		case COLLECTION_BUSINESSES:
			return CmucStore.CollectionBusinesses.CONTENT_TYPE;
		case COLLECTION_BUSINESS_ID:
			return CmucStore.CollectionBusinesses.ENTRY_CONTENT_TYPE;
		case BOOKMARKS:
			return CmucStore.BookMarks.CONTENT_TYPE;
		case BOOKMARK_ID:
			return CmucStore.BookMarks.ENTRY_CONTENT_TYPE;
		case VISIT_HISTORYS:
			return CmucStore.VisitHistory.CONTENT_TYPE;
		case VISIT_HISTORY_ID:
			return CmucStore.VisitHistory.ENTRY_CONTENT_TYPE;
		}
		throw new IllegalStateException("Unknown URL");
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
	    Uri newUri = insertInternal(uri, initialValues);
        if (newUri != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return newUri;
	}
	
	private Uri insertInternal(Uri uri, ContentValues initialValues){
		long rowId;
		Uri newUri = null;;
		String table;
		String nullColumnHack;
		SQLiteDatabase db=openHelper.getWritableDatabase();
		// TODO Auto-generated method stub
		switch(URI_MATCHER.match(uri)){
		case MESSAGES:
		case MESSAGE_ID:
			table=CmucDBHelper.MESSAGE_TABLE;
			nullColumnHack=Messages._ID;
			break;
		case COLLECTION_BUSINESSES:
		case COLLECTION_BUSINESS_ID:
			 table=CmucDBHelper.COLLECTION_BUSINESS_TABLE;
			 nullColumnHack=CollectionBusinesses._ID;
			 break;
		case BOOKMARKS:
		case BOOKMARK_ID:
			table=CmucDBHelper.BOOKMARK_TABLE;
			nullColumnHack=BookMarks._ID;
			break;
		case VISIT_HISTORYS:
		case VISIT_HISTORY_ID:
			table=CmucDBHelper.VISIT_HISTORY_TABLE;
			nullColumnHack=VisitHistory._ID;
			break;
		default:
           throw new UnsupportedOperationException("Invalid URI " + uri);
		}
		
		if(table!=null){
			 rowId = db.insert(table, nullColumnHack, initialValues);
	         if (rowId > 0) {
	             newUri = ContentUris.withAppendedId(uri, rowId);
	         }
		}else{
			  throw new UnsupportedOperationException("Invalid URI " + uri);
		} 
       
		return newUri;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		openHelper=new CmucDBHelper(this.getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projectionIn, String selection, String[] selectionArgs,
			String sort) {
		int table = URI_MATCHER.match(uri);
		SQLiteDatabase db=openHelper.getReadableDatabase();
		SQLiteQueryBuilder sq=new SQLiteQueryBuilder();
		String limit = uri.getQueryParameter("limit");
		String groupBy = null;
		String tables;
		switch(table){
		case MESSAGES:
		case MESSAGE_ID:
			tables=CmucDBHelper.MESSAGE_TABLE;
            break;
		case COLLECTION_BUSINESSES:
		case COLLECTION_BUSINESS_ID:
			tables=CmucDBHelper.COLLECTION_BUSINESS_TABLE;
            break;
		case BOOKMARKS:
		case BOOKMARK_ID:
			tables=CmucDBHelper.BOOKMARK_TABLE;
			break;
		case VISIT_HISTORYS:
		case VISIT_HISTORY_ID:
			tables=CmucDBHelper.VISIT_HISTORY_TABLE;
			break;
		default:
            throw new UnsupportedOperationException("Invalid URI " + uri);
		}
		  sq.setTables(tables);
		  Cursor c = sq.query(db, projectionIn, selection,
	                selectionArgs, groupBy, null, sort, limit);
		  if (c != null) {
	            c.setNotificationUri(getContext().getContentResolver(), uri);
	        }
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues initialValues, String userWhere, String[] whereArgs) {
		// TODO Auto-generated method stub
	    int count;
        int match = URI_MATCHER.match(uri);
        SQLiteDatabase db=openHelper.getWritableDatabase();
        getTableAndWhere(uri, match, userWhere, sGetTableAndWhereParam);
        count=db.update(sGetTableAndWhereParam.table, initialValues, sGetTableAndWhereParam.where, whereArgs);
        if (count > 0 && !db.inTransaction()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
	    return count;
	}

    private static final class GetTableAndWhereOutParameter {
        public String table;
        public String where;
    }
    
    private void getTableAndWhere(Uri uri,int match,String userWhere,GetTableAndWhereOutParameter out){
    	String where=null;
    	switch(match){
    	case MESSAGES:
    		  out.table = CmucDBHelper.MESSAGE_TABLE;
    		  break;
		case MESSAGE_ID:
			  out.table = CmucDBHelper.MESSAGE_TABLE;
	             where = "_id=" + uri.getPathSegments().get(1);
		case COLLECTION_BUSINESSES:
  		  out.table = CmucDBHelper.COLLECTION_BUSINESS_TABLE;
  		  break;
		case COLLECTION_BUSINESS_ID:
			  out.table = CmucDBHelper.COLLECTION_BUSINESS_TABLE;
	             where = "_id=" + uri.getPathSegments().get(1);
	      break;
		case BOOKMARKS:
			 out.table = CmucDBHelper.BOOKMARK_TABLE;
			break;
		case BOOKMARK_ID:
			 out.table = CmucDBHelper.BOOKMARK_TABLE;
             where = "_id=" + uri.getPathSegments().get(1);
			break;
		case VISIT_HISTORYS:
			 out.table = CmucDBHelper.VISIT_HISTORY_TABLE;
			 break;
		case VISIT_HISTORY_ID:
			 out.table = CmucDBHelper.VISIT_HISTORY_TABLE;
             where = "_id=" + uri.getPathSegments().get(1);
             break;
         default:
             throw new UnsupportedOperationException(
                     "Unknown or unsupported URL: " + uri.toString());
    	}
    	 // Add in the user requested WHERE clause, if needed
        if (!TextUtils.isEmpty(userWhere)) {
            if (!TextUtils.isEmpty(where)) {
                out.where = where + " AND (" + userWhere + ")";
            } else {
                out.where = userWhere;
            }
        } else {
            out.where = where;
        }
    }

}
