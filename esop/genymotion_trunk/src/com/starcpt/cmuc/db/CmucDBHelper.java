package com.starcpt.cmuc.db;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CmucDBHelper extends SQLiteOpenHelper {
	private final static String DB_NAME="cmuc.db";
	private final static int VERSION=4;
	public final static String MESSAGE_TABLE="messages";
	public final static String COLLECTION_BUSINESS_TABLE="collection_businesses";
	public final static String BOOKMARK_TABLE="bookmarks";
	public final static String VISIT_HISTORY_TABLE="visit_history";
	public CmucDBHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	public CmucDBHelper(Context context){
		this(context, DB_NAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		 db.execSQL("CREATE TABLE "+MESSAGE_TABLE+"("+
	    		 CmucStore.Messages._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
	    		 CmucStore.Messages.MESSAGE_TIME+" LONG NOT NULL,"+
	    		 CmucStore.Messages.MISSED_FLAG+" INTEGER DEFAULT 0,"+
	    		 CmucStore.Messages.READED+" INTEGER DEFAULT 0,"+
	    		 CmucStore.Messages.NOTIFICATION_ID+" TEXT NOT NULL,"+
	    		 CmucStore.Messages.OPTION_TYPE+" TEXT NOT NULL,"+
	    		 CmucStore.Messages.OPTION_CONTENT+" TEXT,"+
	    		 CmucStore.Messages.FEED_BACK_CONTENT+" TEXT,"+
	    		 CmucStore.Messages.FEED_BACK_TIME+" LONG,"+
	    		 CmucStore.Messages.OPERATION_COMPLETE+" INTEGER DEFAULT 0,"+    		 
	    		 CmucStore.Messages.TITLE+" TEXT,"+
	    		 CmucStore.Messages.USER_NAME+" TEXT,"+
	    		 CmucStore.Messages.TEXT_CONTENT+" TEXT"+");");
		 
		 db.execSQL("CREATE TABLE "+COLLECTION_BUSINESS_TABLE+"("+
	    		 CmucStore.CollectionBusinesses._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
	    		 CmucStore.CollectionBusinesses.COLLECTION_TIME+" LONG NOT NULL,"+
	    		 CmucStore.CollectionBusinesses.LISTORDER+" LONG NOT NULL,"+
	    		 CmucStore.CollectionBusinesses.BUSINESS_ID+" INTEGER DEFAULT 0,"+
	    		 CmucStore.CollectionBusinesses.DELETE_FLAG+" INTEGER DEFAULT 0,"+
	    		 CmucStore.CollectionBusinesses.USER_NAME+" TEXT NOT NULL,"+ 
	    		 CmucStore.CollectionBusinesses.CONTENT+" TEXT NOT NULL,"+
	    		 CmucStore.CollectionBusinesses.ICON+" TEXT NOT NULL,"+
	    		 CmucStore.CollectionBusinesses.APP_TAG+" TEXT NOT NULL,"+
	    		 CmucStore.CollectionBusinesses.NAME+" TEXT"+");");
		 
		 db.execSQL("CREATE TABLE "+BOOKMARK_TABLE+"("+
	    		 CmucStore.BookMarks._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
	    		 CmucStore.BookMarks.TIME+" LONG NOT NULL,"+
	    		 CmucStore.BookMarks.TITLE+" TEXT NOT NULL,"+ 
	    		 CmucStore.BookMarks.USER_NAME+" TEXT,"+
	    		 CmucStore.BookMarks.URL+" TEXT NOT NULL"+");");
		 
		 db.execSQL("CREATE TABLE "+VISIT_HISTORY_TABLE+"("+
	    		 CmucStore.VisitHistory._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
	    		 CmucStore.VisitHistory.TIME+" LONG NOT NULL,"+
	    		 CmucStore.VisitHistory.USER_NAME+" TEXT,"+
	    		 CmucStore.VisitHistory.TITLE+" TEXT NOT NULL,"+ 
	    		 CmucStore.VisitHistory.URL+" TEXT NOT NULL"+");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion!=newVersion){
			 // Kills the table and existing data
	        db.execSQL("DROP TABLE IF EXISTS "+MESSAGE_TABLE);
	        db.execSQL("DROP TABLE IF EXISTS "+COLLECTION_BUSINESS_TABLE);
	        db.execSQL("DROP TABLE IF EXISTS "+BOOKMARK_TABLE);
	        db.execSQL("DROP TABLE IF EXISTS "+VISIT_HISTORY_TABLE);
	        // Recreates the database with a new version
	        onCreate(db);
		}		
	}

}
