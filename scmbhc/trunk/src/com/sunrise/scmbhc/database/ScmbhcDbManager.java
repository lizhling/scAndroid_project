package com.sunrise.scmbhc.database;

import java.util.ArrayList;

import com.sunrise.scmbhc.utils.LogUtlis;
import com.sunrise.scmbhc.utils.coding.CodingInterface;
import com.sunrise.scmbhc.utils.coding.DesCrypCoding;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.entity.Message;

public class ScmbhcDbManager {
	public final static int NO_DELETE_FALG_ITEM = 0;
	public final static int DELETE_FALG_ITEM = 1;
	public final static int NO_CONTAIN_ITEM = 2;
	ContentResolver cr;
	CodingInterface mCodingObj;
	// private static String TAG="MessageManager";
	private static String sMessageProjection[] = { ScmbhcStore.Messages._ID, ScmbhcStore.Messages.USER_NAME, ScmbhcStore.Messages.TITLE,
			ScmbhcStore.Messages.MESSAGE_TIME, ScmbhcStore.Messages.TEXT_CONTENT, ScmbhcStore.Messages.MISSED_FLAG, ScmbhcStore.Messages.NOTIFICATION_ID,
			ScmbhcStore.Messages.OPTION_TYPE, ScmbhcStore.Messages.OPTION_CONTENT, ScmbhcStore.Messages.OPERATION_COMPLETE,
			ScmbhcStore.Messages.FEED_BACK_CONTENT, ScmbhcStore.Messages.FEED_BACK_TIME, ScmbhcStore.Messages.READED };
	private static ScmbhcDbManager instance = null;
	private static String sBusinessMenuProjection[] = { ScmbhcStore.BusinessMenu._ID, ScmbhcStore.BusinessMenu.PARENT_ID, ScmbhcStore.BusinessMenu.MENU_TYPE,
			ScmbhcStore.BusinessMenu.ORDER, ScmbhcStore.BusinessMenu.NAME, ScmbhcStore.BusinessMenu.ICON, ScmbhcStore.BusinessMenu.DESCRIPTION,
			ScmbhcStore.BusinessMenu.CHARGES, ScmbhcStore.BusinessMenu.WARMPROMPT, ScmbhcStore.BusinessMenu.BUSINESS_DATA,
			ScmbhcStore.BusinessMenu.SERVICE_URL, ScmbhcStore.BusinessMenu.PROD_PRCID, ScmbhcStore.BusinessMenu.BUS_TAG, ScmbhcStore.BusinessMenu.BUS_APP_DATA };

	private ScmbhcDbManager(ContentResolver cr) {
		super();
		this.cr = cr;
	}

	public static ScmbhcDbManager getInstance(ContentResolver cr) {
		if (instance == null)
			instance = new ScmbhcDbManager(cr);
		return instance;
	}

	public void startQueryBusinessMenu(AsyncQueryHandler queryHandler, int token, long parentId) {
		StringBuilder where = new StringBuilder();
		where.append(ScmbhcStore.BusinessMenu.PARENT_ID + "==" + parentId);
		queryHandler.startQuery(token, where.toString(), ScmbhcStore.BusinessMenu.CONTENT_URI, sBusinessMenuProjection, where.toString(), null,
				ScmbhcStore.BusinessMenu.DEFAULT_SORT_ORDER);
	}

	public int getBusinessMenuCount() {
		int count = 0;
		Cursor cursor = cr.query(ScmbhcStore.BusinessMenu.CONTENT_URI, sBusinessMenuProjection, null, null, null);
		if (cursor != null)
			count = cursor.getCount();
		return count;
	}

	private CodingInterface getCodingObject() {

		if (mCodingObj == null) {
			synchronized (this) {
				if (mCodingObj == null)
					mCodingObj = new DesCrypCoding();
			}
		}

		return mCodingObj;
	}

	public ArrayList<com.sunrise.scmbhc.entity.BusinessMenu> searchBusinessMenuByKey(String key) {
		StringBuilder where = new StringBuilder();
		where.append(ScmbhcStore.BusinessMenu.NAME + " LIKE " + "'%" + key + "%'");
		where.append(" AND " + ScmbhcStore.BusinessMenu.MENU_TYPE + "<>" + com.sunrise.scmbhc.entity.BusinessMenu.MENU_TYPE);
		ArrayList<com.sunrise.scmbhc.entity.BusinessMenu> businessMenus = new ArrayList<com.sunrise.scmbhc.entity.BusinessMenu>();
		Cursor cursor = cr.query(ScmbhcStore.BusinessMenu.CONTENT_URI, sBusinessMenuProjection, where.toString(), null,
				ScmbhcStore.BusinessMenu.DEFAULT_SORT_ORDER);
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			com.sunrise.scmbhc.entity.BusinessMenu businessMenu = cursorToBusinessBusinessMenu(cursor);
			businessMenus.add(businessMenu);
		}
		return businessMenus;
	}

	public ArrayList<com.sunrise.scmbhc.entity.BusinessMenu> queryBusinessMenu() {
		ArrayList<com.sunrise.scmbhc.entity.BusinessMenu> businessMenus = new ArrayList<com.sunrise.scmbhc.entity.BusinessMenu>();
		Cursor cursor = cr.query(ScmbhcStore.BusinessMenu.CONTENT_URI, sBusinessMenuProjection, null, null, ScmbhcStore.BusinessMenu.DEFAULT_SORT_ORDER);
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			com.sunrise.scmbhc.entity.BusinessMenu businessMenu = cursorToBusinessBusinessMenu(cursor);
			businessMenus.add(businessMenu);
		}
		return businessMenus;
	}

	public BusinessMenu queryBusinessMenuByBusTag(String busTag) {
		StringBuilder where = new StringBuilder();
		where.append(ScmbhcStore.BusinessMenu.BUS_TAG + "==" + "'" + busTag + "'");
		Cursor cursor = cr.query(ScmbhcStore.BusinessMenu.CONTENT_URI, sBusinessMenuProjection, where.toString(), null,
				ScmbhcStore.BusinessMenu.DEFAULT_SORT_ORDER);
		com.sunrise.scmbhc.entity.BusinessMenu businessMenu = null;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			businessMenu = cursorToBusinessBusinessMenu(cursor);
		}
		return businessMenu;
	}

	public ArrayList<BusinessMenu> queryBusinessMenuByBusParentId(long parentId) {
		StringBuilder where = new StringBuilder();
		where.append(ScmbhcStore.BusinessMenu.PARENT_ID + "==" + "'" + parentId + "'");
		Cursor cursor = cr.query(ScmbhcStore.BusinessMenu.CONTENT_URI, sBusinessMenuProjection, where.toString(), null,
				ScmbhcStore.BusinessMenu.DEFAULT_SORT_ORDER);
		ArrayList<BusinessMenu> businessMenu = null;
		if (cursor.getCount() > 0) {
			businessMenu = new ArrayList<BusinessMenu>();
			cursor.moveToFirst();
			do {
				businessMenu.add(cursorToBusinessBusinessMenu(cursor));
			} while ((cursor.moveToNext()));
		}
		return businessMenu;
	}

	public BusinessMenu queryBusinessMenuByPrcID(String prcID) {
		StringBuilder where = new StringBuilder();
		where.append(ScmbhcStore.BusinessMenu.PROD_PRCID).append(" like \'%" + getCodingObject().encode(prcID) + "%\'");
		Cursor cursor = cr.query(ScmbhcStore.BusinessMenu.CONTENT_URI, sBusinessMenuProjection, where.toString(), null,
				ScmbhcStore.BusinessMenu.DEFAULT_SORT_ORDER);
		com.sunrise.scmbhc.entity.BusinessMenu businessMenu = null;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			businessMenu = cursorToBusinessBusinessMenu(cursor);
		}
		cursor.close();
		return businessMenu;
	}

	public ArrayList<com.sunrise.scmbhc.entity.BusinessMenu> cursorToBusinessBusinessMenus(Cursor cursor) {
		ArrayList<com.sunrise.scmbhc.entity.BusinessMenu> messages = new ArrayList<com.sunrise.scmbhc.entity.BusinessMenu>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			com.sunrise.scmbhc.entity.BusinessMenu businessMenu = cursorToBusinessBusinessMenu(cursor);
			messages.add(businessMenu);
		}
		return messages;
	}

	public synchronized void recordBusinessMenu(com.sunrise.scmbhc.entity.BusinessMenu businessMenu) {
		ContentValues values = BusinessMenuToContentValue(businessMenu);
		cr.insert(ScmbhcStore.BusinessMenu.CONTENT_URI, values);
	}

	public void recordBusinessMenuList(ArrayList<com.sunrise.scmbhc.entity.BusinessMenu> datas) {
		if (datas == null)
			return;
		for (BusinessMenu businessMenu : datas) {
			recordBusinessMenu(businessMenu);
		}
	}

	public synchronized void updateMenuBusiness(com.sunrise.scmbhc.entity.BusinessMenu businessMenu) {
		StringBuilder where = new StringBuilder();
		where.append(ScmbhcStore.BusinessMenu._ID + "==" + businessMenu.getId());
		ContentValues values = BusinessMenuToContentValue(businessMenu);
		cr.update(ScmbhcStore.BusinessMenu.CONTENT_URI, values, where.toString(), null);
	}

	public synchronized void deleteBusinessMenu(com.sunrise.scmbhc.entity.BusinessMenu businessMenu) {
		StringBuilder where = new StringBuilder();
		where.append(ScmbhcStore.BusinessMenu._ID + "==" + businessMenu.getId());
		cr.delete(ScmbhcStore.BusinessMenu.CONTENT_URI, where.toString(), null);
	}

	public synchronized void deleteAllBusinessMenu() {
		cr.delete(ScmbhcStore.BusinessMenu.CONTENT_URI, null, null);
	}

	public synchronized void startDeleteBusinessMenu(AsyncQueryHandler handler, int token, com.sunrise.scmbhc.entity.BusinessMenu businessMenu) {
		StringBuilder where = new StringBuilder();
		where.append(ScmbhcStore.BusinessMenu._ID + "==" + businessMenu.getId());
		handler.startDelete(token, null, ScmbhcStore.BusinessMenu.CONTENT_URI, where.toString(), null);
	}

	public synchronized void startDeleteAllBusinessMenu(AsyncQueryHandler handler, int token) {
		handler.startDelete(token, null, ScmbhcStore.BusinessMenu.CONTENT_URI, null, null);
	}

	public com.sunrise.scmbhc.entity.BusinessMenu cursorToBusinessBusinessMenu(Cursor cursor) {
		long id = cursor.getLong(cursor.getColumnIndex(ScmbhcStore.BusinessMenu._ID));
		long parentId = cursor.getLong(cursor.getColumnIndex(ScmbhcStore.BusinessMenu.PARENT_ID));
		int menuType = cursor.getInt(cursor.getColumnIndex(ScmbhcStore.BusinessMenu.MENU_TYPE));
		int order = cursor.getInt(cursor.getColumnIndex(ScmbhcStore.BusinessMenu.ORDER));
		String name = cursor.getString(cursor.getColumnIndex(ScmbhcStore.BusinessMenu.NAME));
		String icon = cursor.getString(cursor.getColumnIndex(ScmbhcStore.BusinessMenu.ICON));
		String description = cursor.getString(cursor.getColumnIndex(ScmbhcStore.BusinessMenu.DESCRIPTION));
		String charges = cursor.getString(cursor.getColumnIndex(ScmbhcStore.BusinessMenu.CHARGES));
		String warmPrompt = cursor.getString(cursor.getColumnIndex(ScmbhcStore.BusinessMenu.WARMPROMPT));
		String businessData = cursor.getString(cursor.getColumnIndex(ScmbhcStore.BusinessMenu.BUSINESS_DATA));
		String serviceUrl = cursor.getString(cursor.getColumnIndex(ScmbhcStore.BusinessMenu.SERVICE_URL));
		String prodPrcid = cursor.getString(cursor.getColumnIndex(ScmbhcStore.BusinessMenu.PROD_PRCID));
		String busTag = cursor.getString(cursor.getColumnIndex(ScmbhcStore.BusinessMenu.BUS_TAG));
		String busAppData = cursor.getString(cursor.getColumnIndex(ScmbhcStore.BusinessMenu.BUS_APP_DATA));
		com.sunrise.scmbhc.entity.BusinessMenu businessMenu = new com.sunrise.scmbhc.entity.BusinessMenu();
		if (!TextUtils.isEmpty(serviceUrl)) {
			// serviceUrl = DesCrypUtil.DESDecrypt(serviceUrl.trim());
			serviceUrl = getCodingObject().decode(serviceUrl.trim());
		}
		if (!TextUtils.isEmpty(prodPrcid)) {
			// prodPrcid = DesCrypUtil.DESDecrypt(prodPrcid.trim());
			prodPrcid = getCodingObject().decode(prodPrcid.trim());
		}
		if (!TextUtils.isEmpty(busAppData)) {
			// busAppData = DesCrypUtil.DESDecrypt(busAppData.trim());
			busAppData = getCodingObject().decode(busAppData.trim());
		}
		businessMenu.setId(id);
		businessMenu.setParentId(parentId);
		businessMenu.setMenuType(menuType);
		businessMenu.setOrder(order);
		businessMenu.setName(name);
		businessMenu.setIcon(icon);
		businessMenu.setDescription(description);
		businessMenu.setCharges(charges);
		businessMenu.setWarmPrompt(warmPrompt);
		businessMenu.setBusinessData(businessData);
		businessMenu.setServiceUrl(serviceUrl);
		businessMenu.setProdPrcid(prodPrcid);
		businessMenu.setBusTag(busTag);
		businessMenu.setBusAppData(busAppData);
		return businessMenu;
	}

	private ContentValues BusinessMenuToContentValue(com.sunrise.scmbhc.entity.BusinessMenu businessMenu) {
		ContentValues value = new ContentValues();
		String name = businessMenu.getName();
		long id = businessMenu.getId();
		value.put(ScmbhcStore.BusinessMenu._ID, id);
		long parentId = businessMenu.getParentId();
		value.put(ScmbhcStore.BusinessMenu.PARENT_ID, parentId);
		int menuType = businessMenu.getMenuType();
		value.put(ScmbhcStore.BusinessMenu.MENU_TYPE, menuType);
		int order = businessMenu.getOrder();
		value.put(ScmbhcStore.BusinessMenu.ORDER, order);
		if (businessMenu.getName() != null)
			value.put(ScmbhcStore.BusinessMenu.NAME, name);
		String icon = businessMenu.getIcon();
		if (icon != null)
			value.put(ScmbhcStore.BusinessMenu.ICON, icon);
		String description = businessMenu.getDescription();
		if (description != null)
			value.put(ScmbhcStore.BusinessMenu.DESCRIPTION, description);
		String charges = businessMenu.getCharges();
		if (charges != null)
			value.put(ScmbhcStore.BusinessMenu.CHARGES, charges);
		String warmPrompt = businessMenu.getWarmPrompt();
		if (warmPrompt != null)
			value.put(ScmbhcStore.BusinessMenu.WARMPROMPT, warmPrompt);
		String businessData = businessMenu.getBusinessData();
		if (businessData != null)
			value.put(ScmbhcStore.BusinessMenu.BUSINESS_DATA, businessData);
		String serviceUrl = businessMenu.getServiceUrl();
		if (!TextUtils.isEmpty(serviceUrl)) {
			try {
				// serviceUrl = DesCrypUtil.DESEncrypt(serviceUrl);
				serviceUrl = getCodingObject().encode(serviceUrl.trim());
			} catch (Exception e) {
				e.printStackTrace();
				serviceUrl = businessMenu.getServiceUrl();
			}
			value.put(ScmbhcStore.BusinessMenu.SERVICE_URL, serviceUrl);
		}

		String prodPrcid = businessMenu.getProdPrcid();
		if (!TextUtils.isEmpty(prodPrcid)) {
			try {
				// prodPrcid = DesCrypUtil.DESEncrypt(prodPrcid);
				prodPrcid = getCodingObject().encode(prodPrcid.trim());
			} catch (Exception e) {
				e.printStackTrace();
				prodPrcid = businessMenu.getProdPrcid();
			}
			value.put(ScmbhcStore.BusinessMenu.PROD_PRCID, prodPrcid);
		}

		String busTag = businessMenu.getBusTag();
		if (busTag != null) {
			value.put(ScmbhcStore.BusinessMenu.BUS_TAG, busTag);
			LogUtlis.showLogD("busTag", busTag);
		}
		String busAppData = businessMenu.getBusAppData();
		if (!TextUtils.isEmpty(busAppData)) {
			try {
				// busAppData = DesCrypUtil.DESEncrypt(busAppData);
				busAppData = getCodingObject().encode(busAppData.trim());
			} catch (Exception e) {
				e.printStackTrace();
				busAppData = businessMenu.getBusAppData();

			}
			value.put(ScmbhcStore.BusinessMenu.BUS_APP_DATA, busAppData);
		}
		return value;
	}

	// 推送消息处理

	public int getMissMessageCount() {
		StringBuilder where = new StringBuilder();
		where.append(ScmbhcStore.Messages.MISSED_FLAG + "=" + com.sunrise.scmbhc.entity.Message.MISS_FLAG);
		Cursor cursor = cr.query(ScmbhcStore.Messages.CONTENT_URI, sMessageProjection, where.toString(), null, ScmbhcStore.Messages.DEFAULT_SORT_ORDER);
		int count = cursor.getCount();
		return count;
	}

	public void recordMessage(com.sunrise.scmbhc.entity.Message message) {
		ContentValues values = messageToContentValue(message);
		cr.insert(ScmbhcStore.Messages.CONTENT_URI, values);
	}

	public void startQueryMessage(AsyncQueryHandler queryHandler, int token, String userName) {
		StringBuilder where = new StringBuilder();
		where.append(ScmbhcStore.Messages.USER_NAME + "=" + "'" + userName + "'");
		queryHandler.startQuery(token, where.toString(), ScmbhcStore.Messages.CONTENT_URI, sMessageProjection, null, null,
				ScmbhcStore.Messages.DEFAULT_SORT_ORDER + " desc");
	}

	private ContentValues messageToContentValue(com.sunrise.scmbhc.entity.Message message) {
		ContentValues value = new ContentValues();
		if (message.getUserName() != null)
			value.put(ScmbhcStore.Messages.USER_NAME, message.getUserName());
		if (message.getTitle() != null)
			value.put(ScmbhcStore.Messages.TITLE, message.getTitle());
		if (message.getTextContent() != null)
			value.put(ScmbhcStore.Messages.TEXT_CONTENT, message.getTextContent());
		if (message.getMessageTime() != 0)
			value.put(ScmbhcStore.Messages.MESSAGE_TIME, message.getMessageTime());
		if (message.getNotificationId() != null)
			value.put(ScmbhcStore.Messages.NOTIFICATION_ID, message.getNotificationId());
		if (message.getOptionType() != null)
			value.put(ScmbhcStore.Messages.OPTION_TYPE, message.getOptionType());
		if (message.getOptionContent() != null)
			value.put(ScmbhcStore.Messages.OPTION_CONTENT, message.getOptionContent());
		value.put(ScmbhcStore.Messages.OPERATION_COMPLETE, message.getOperationComplete());
		value.put(ScmbhcStore.Messages.MISSED_FLAG, message.getMissedFlag());
		return value;
	}

	public Message cursorToMessage(Cursor cursor) {
		int id = cursor.getInt(cursor.getColumnIndex(ScmbhcStore.Messages._ID));
		long messageTime = cursor.getLong(cursor.getColumnIndex(ScmbhcStore.Messages.MESSAGE_TIME));
		String textContent = cursor.getString(cursor.getColumnIndex(ScmbhcStore.Messages.TEXT_CONTENT));
		String title = cursor.getString(cursor.getColumnIndex(ScmbhcStore.Messages.TITLE));
		int readedFlag = cursor.getInt(cursor.getColumnIndex(ScmbhcStore.Messages.MISSED_FLAG));

		String notificationId = cursor.getString(cursor.getColumnIndex(ScmbhcStore.Messages.NOTIFICATION_ID));
		String optionType = cursor.getString(cursor.getColumnIndex(ScmbhcStore.Messages.OPTION_TYPE));
		String optionContent = cursor.getString(cursor.getColumnIndex(ScmbhcStore.Messages.OPTION_CONTENT));
		int operationComplete = cursor.getInt(cursor.getColumnIndex(ScmbhcStore.Messages.OPERATION_COMPLETE));

		long feedbackTime = cursor.getLong(cursor.getColumnIndex(ScmbhcStore.Messages.FEED_BACK_TIME));
		String feedbackContent = cursor.getString(cursor.getColumnIndex(ScmbhcStore.Messages.FEED_BACK_CONTENT));
		int readed = cursor.getInt(cursor.getColumnIndex(ScmbhcStore.Messages.READED));
		Message message = new Message(id, readedFlag, messageTime, textContent, title);
		message.setNotificationId(notificationId);
		message.setOptionType(optionType);
		message.setOptionContent(optionContent);
		message.setOperationComplete(operationComplete);
		message.setFeedbackTime(feedbackTime);
		message.setFeedbackContent(feedbackContent);
		message.setReaded(readed);
		return message;
	}

	public synchronized void readMissMessage() {
		StringBuilder where = new StringBuilder();
		where.append(ScmbhcStore.Messages.MISSED_FLAG + "==" + Message.MISS_FLAG);
		ContentValues value = new ContentValues();
		value.put(ScmbhcStore.Messages.MISSED_FLAG, Message.READED_FLAG);
		cr.update(ScmbhcStore.Messages.CONTENT_URI, value, where.toString(), null);
	}

	public synchronized void startDeleteMessage(AsyncQueryHandler handler, int token, int _id) {
		StringBuilder where = new StringBuilder();
		where.append(ScmbhcStore.Messages._ID + "==" + _id);
		handler.startDelete(token, null, ScmbhcStore.Messages.CONTENT_URI, where.toString(), null);
	}

	public synchronized void startDeleteAllMessages(AsyncQueryHandler handler, int token) {
		handler.startDelete(token, null, ScmbhcStore.Messages.CONTENT_URI, null, null);
	}

	public synchronized void readed(int _id) {
		StringBuilder where = new StringBuilder();
		where.append(ScmbhcStore.Messages._ID + "==" + _id);
		ContentValues values = new ContentValues();
		values.put(ScmbhcStore.Messages.READED, Message.READED_FLAG);
		cr.update(ScmbhcStore.Messages.CONTENT_URI, values, where.toString(), null);
	}

	public synchronized void updateFeedbackMessage(int _id, String feedBackCotent, long feedbackTime) {
		StringBuilder where = new StringBuilder();
		where.append(ScmbhcStore.Messages._ID + "==" + _id);
		ContentValues values = new ContentValues();
		values.put(ScmbhcStore.Messages.OPERATION_COMPLETE, Message.COMPLETE);
		values.put(ScmbhcStore.Messages.FEED_BACK_CONTENT, feedBackCotent);
		values.put(ScmbhcStore.Messages.FEED_BACK_TIME, feedbackTime);
		cr.update(ScmbhcStore.Messages.CONTENT_URI, values, where.toString(), null);
	}

	public synchronized void updateReadedMessage(int _id) {
		StringBuilder where = new StringBuilder();
		where.append(ScmbhcStore.Messages._ID + "==" + _id);
		ContentValues values = new ContentValues();
		values.put(ScmbhcStore.Messages.OPERATION_COMPLETE, Message.COMPLETE);
		cr.update(ScmbhcStore.Messages.CONTENT_URI, values, where.toString(), null);
	}
}
