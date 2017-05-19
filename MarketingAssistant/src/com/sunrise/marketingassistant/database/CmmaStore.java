package com.sunrise.marketingassistant.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class CmmaStore {
	public final static String AUTHORIY = "com.sunrise.scmbhc.database.ScmbhcProvider";
	public final static String CONTENT_URI_SLASH = "content://" + AUTHORIY + "/";
	public final static String CONTENT_TYPE_HEADER = "vnd.android.cursor.dir/";
	public final static String ENTRY_CONTENT_TYPE_HEADER = "vnd.android.cursor.item/";

	public final static class CollectBranch implements BaseColumns {
		public final static String ACCOUNT = "account";
		public final static String GROUP_ID = "groupId";
		public final static String GROUP_NAME = "groupName";
		public final static String GROUP_ADDRESS = "groupAddress";
		public final static String CLASS_NAME = "className";

		public final static Uri CONTENT_URI = Uri.parse(CONTENT_URI_SLASH + ACCOUNT);
		public final static String CONTENT_TYPE = CONTENT_TYPE_HEADER + ACCOUNT;
		public final static String ENTRY_CONTENT_TYPE = ENTRY_CONTENT_TYPE_HEADER + ACCOUNT;

	}

	// public final static class BusinessMenu implements BaseColumns {
	// public final static String ENTITY_NAME = "businessmenu";
	// public final static String PARENT_ID = "parent_id";
	 public final static String ORDER = "order_no";
	// public final static String NAME = "name";
	// public final static String ICON = "icon";
	// public final static String DESCRIPTION = "description";
	// public final static String CHARGES = "charges";
	// public final static String WARMPROMPT = "warmPrompt";
	// public final static String BUSINESS_DATA = "business_data";
	// public final static String SERVICE_URL = "service_url";
	// public final static String PROD_PRCID = "prod_prcid";
	// public final static String BUS_TAG = "busTag";
	// public final static String BUS_APP_DATA = "busAppData";
	// public final static String DEFAULT_SORT_ORDER = ORDER;
	//
	// public final static Uri CONTENT_URI = Uri.parse(CONTENT_URI_SLASH +
	// ENTITY_NAME);
	// public final static String CONTENT_TYPE = CONTENT_TYPE_HEADER +
	// ENTITY_NAME;
	// public final static String ENTRY_CONTENT_TYPE = ENTRY_CONTENT_TYPE_HEADER
	// + ENTITY_NAME;
	//
	// }

	// public final static class Messages implements BaseColumns {
	// public final static String MESSAGE_TIME = "message_time";
	// public final static String TITLE = "title";
	// public final static String TEXT_CONTENT = "content";
	// public final static String MISSED_FLAG = "missed_flag";
	// public final static String DEFAULT_SORT_ORDER = MESSAGE_TIME;
	//
	// public final static String NOTIFICATION_ID = "notification_id";
	// public final static String OPTION_TYPE = "option_type";
	// public final static String OPTION_CONTENT = "option_content";
	// public final static String OPERATION_COMPLETE = "operation_complete";
	// public final static String FEED_BACK_CONTENT = "feed_back_content";
	// public final static String FEED_BACK_TIME = "feed_back_time";
	// public final static String READED = "readed";
	// public final static String USER_NAME = "user_name";
	//
	// public final static Uri CONTENT_URI = Uri.parse(CONTENT_URI_SLASH +
	// "messages");
	// public final static String CONTENT_TYPE =
	// "vnd.android.cursor.dir/messages";
	// public final static String ENTRY_CONTENT_TYPE =
	// "vnd.android.cursor.item/messages";
	// }
}
