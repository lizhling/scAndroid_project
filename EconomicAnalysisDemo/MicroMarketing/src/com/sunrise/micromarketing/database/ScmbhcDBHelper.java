package com.sunrise.micromarketing.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ScmbhcDBHelper extends SQLiteOpenHelper {
	private final static String DB_NAME="scmbhc.db";
	private final static int VERSION=4;
	public final static String BUSINESSMEMNU_TABLE="business_menu";
	public final static String MESSAGE_TABLE="messages";
	
	public ScmbhcDBHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	public ScmbhcDBHelper(Context context){
		this(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		 db.execSQL("CREATE TABLE "+BUSINESSMEMNU_TABLE+"("+
	    		 ScmbhcStore.BusinessMenu._ID+" LONG PRIMARY KEY,"+
	    		 ScmbhcStore.BusinessMenu.PARENT_ID+" LONG NOT NULL,"+
	    		 ScmbhcStore.BusinessMenu.MENU_TYPE+" INTEGER DEFAULT 2,"+
	    		 ScmbhcStore.BusinessMenu.ORDER+" INTEGER DEFAULT 0,"+
	    		 ScmbhcStore.BusinessMenu.NAME+" TEXT,"+
	    		 ScmbhcStore.BusinessMenu.ICON+" TEXT,"+
	    		 ScmbhcStore.BusinessMenu.DESCRIPTION+" TEXT,"+
	    		 ScmbhcStore.BusinessMenu.CHARGES+" TEXT,"+
	    		 ScmbhcStore.BusinessMenu.WARMPROMPT+" TEXT,"+		 
	    		 ScmbhcStore.BusinessMenu.BUSINESS_DATA+" TEXT,"+
	    		 ScmbhcStore.BusinessMenu.PROD_PRCID+" TEXT,"+
	    		 ScmbhcStore.BusinessMenu.BUS_TAG+" TEXT,"+
	    		 ScmbhcStore.BusinessMenu.BUS_APP_DATA+" TEXT,"+
	    		 ScmbhcStore.BusinessMenu.SERVICE_URL+" TEXT"+");");
		 db.execSQL("CREATE TABLE "+MESSAGE_TABLE+"("+
	    		 ScmbhcStore.Messages._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
	    		 ScmbhcStore.Messages.MESSAGE_TIME+" LONG NOT NULL,"+
	    		 ScmbhcStore.Messages.MISSED_FLAG+" INTEGER DEFAULT 0,"+
	    		 ScmbhcStore.Messages.READED+" INTEGER DEFAULT 0,"+
	    		 ScmbhcStore.Messages.NOTIFICATION_ID+" TEXT NOT NULL,"+
	    		 ScmbhcStore.Messages.OPTION_TYPE+" TEXT NOT NULL,"+
	    		 ScmbhcStore.Messages.OPTION_CONTENT+" TEXT,"+
	    		 ScmbhcStore.Messages.FEED_BACK_CONTENT+" TEXT,"+
	    		 ScmbhcStore.Messages.FEED_BACK_TIME+" LONG,"+
	    		 ScmbhcStore.Messages.OPERATION_COMPLETE+" INTEGER DEFAULT 0,"+    		 
	    		 ScmbhcStore.Messages.TITLE+" TEXT,"+
	    		 ScmbhcStore.Messages.USER_NAME+" TEXT,"+
	    		 ScmbhcStore.Messages.TEXT_CONTENT+" TEXT"+");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion!=newVersion){
			 // Kills the table and existing data
	        db.execSQL("DROP TABLE IF EXISTS "+BUSINESSMEMNU_TABLE);
	        db.execSQL("DROP TABLE IF EXISTS "+MESSAGE_TABLE);
	        // Recreates the database with a new version
	        onCreate(db);
		}		
	}
	
	/*private void initBusinessMenu() {
		ContentResolver contentResolver=App.sContext.getContentResolver();
		ScmbhcDbManager scmbhcDbManager=ScmbhcDbManager.getInstance(contentResolver);
		int count=scmbhcDbManager.getBusinessMenuCount();
		if(count<=0){
			String jsonStr=FileUtils.readFileFromAssets(App.sContext, AppDirConstant.APP_MENU_JSON_NAME);
			AllMenus allMenus=JsonUtils.parseJsonStrToObject(jsonStr, AllMenus.class);
			scmbhcDbManager.recordBusinessMenuList(allMenus.getDatas());
		}
	}*/

}
