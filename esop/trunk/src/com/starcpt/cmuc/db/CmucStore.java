package com.starcpt.cmuc.db;
import android.net.Uri;
import android.provider.BaseColumns;

public class CmucStore {
public final static String AUTHORIY="com.starcpt.cumc.db.CmucProvider";
public final static String CONTENT_URI_SLASH="content://"+AUTHORIY+"/";

public final static class Messages implements BaseColumns{
	public final static String MESSAGE_TIME="message_time";
	public final static String TITLE="title";
	public final static String TEXT_CONTENT="content";
	public final static String MISSED_FLAG="missed_flag";
	public final static String DEFAULT_SORT_ORDER=MESSAGE_TIME;
	
	public final static String NOTIFICATION_ID="notification_id";
	public final static String OPTION_TYPE="option_type";
	public final static String OPTION_CONTENT="option_content";
	public final static String OPERATION_COMPLETE="operation_complete";
	public final static String FEED_BACK_CONTENT="feed_back_content";
	public final static String FEED_BACK_TIME="feed_back_time";
	public final static String READED="readed";
	public final static String USER_NAME="user_name";
	
	public final static Uri CONTENT_URI=Uri.parse(CONTENT_URI_SLASH+"messages");
	public final static String CONTENT_TYPE="vnd.android.cursor.dir/messages";
	public final static String ENTRY_CONTENT_TYPE="vnd.android.cursor.item/messages";
}

public final static class CollectionBusinesses implements BaseColumns{
	public final static String COLLECTION_TIME="collection_time";
	public final static String NAME="name";
	public final static String CONTENT="content";
	public final static String BUSINESS_ID="business_id";
	public final static String ICON="icon";
	public final static String LISTORDER="listOrder";
	public final static String DEFAULT_SORT_ORDER=LISTORDER;
	public final static String USER_NAME="user_name";
	public final static String APP_TAG="app_tag";
	public final static String DELETE_FLAG="deleteFlag";
	
	public final static Uri CONTENT_URI=Uri.parse(CONTENT_URI_SLASH+"collection_business");
	public final static String CONTENT_TYPE="vnd.android.cursor.dir/collection_business";
	public final static String ENTRY_CONTENT_TYPE="vnd.android.cursor.item/collection_business";
}

public final static class BookMarks implements BaseColumns{
	public final static String TIME="time";
	public final static String TITLE="title";
	public final static String URL="url";
	public final static String USER_NAME="user_name";
	public final static String DEFAULT_SORT_ORDER=TIME;
	
	public final static Uri CONTENT_URI=Uri.parse(CONTENT_URI_SLASH+"bookmark");
	public final static String CONTENT_TYPE="vnd.android.cursor.dir/bookmark";
	public final static String ENTRY_CONTENT_TYPE="vnd.android.cursor.item/bookmark";
}

public final static class VisitHistory implements BaseColumns{
	public final static String TIME="time";
	public final static String TITLE="title";
	public final static String URL="url";
	public final static String USER_NAME="user_name";
	public final static String DEFAULT_SORT_ORDER=TIME;
	
	public final static Uri CONTENT_URI=Uri.parse(CONTENT_URI_SLASH+"visit_history");
	public final static String CONTENT_TYPE="vnd.android.cursor.dir/visit_history";
	public final static String ENTRY_CONTENT_TYPE="vnd.android.cursor.item/visit_history";
}

}
