package com.starcpt.cmuc.db;

import java.util.ArrayList;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import com.starcpt.cmuc.model.AppMenus;
import com.starcpt.cmuc.model.Item;
import com.starcpt.cmuc.model.Message;
import com.starcpt.cmuc.model.bean.AppMenuBean;
import com.starcpt.cmuc.model.bean.AppMenusBean;
import com.starcpt.cmuc.model.bean.BookmarkBean;
import com.starcpt.cmuc.model.bean.VisitHistoryBean;
import com.starcpt.cmuc.utils.StarCpyDateUtils;



public class CmucDbManager {
public final static int NO_DELETE_FALG_ITEM=0;
public final static int DELETE_FALG_ITEM=1;
public final static int NO_CONTAIN_ITEM=2;
ContentResolver cr;
//private static String TAG="MessageManager";
private static CmucDbManager instance=null;
private static String sMessageProjection[]={
	CmucStore.Messages._ID,
	CmucStore.Messages.USER_NAME,
	CmucStore.Messages.TITLE,
	CmucStore.Messages.MESSAGE_TIME,
	CmucStore.Messages.TEXT_CONTENT,
	CmucStore.Messages.MISSED_FLAG,
	CmucStore.Messages.NOTIFICATION_ID,
	CmucStore.Messages.OPTION_TYPE,
	CmucStore.Messages.OPTION_CONTENT,
	CmucStore.Messages.OPERATION_COMPLETE,
	CmucStore.Messages.FEED_BACK_CONTENT,
	CmucStore.Messages.FEED_BACK_TIME,
	CmucStore.Messages.READED
};

private static String sCollectionBusinessesProjection[]={
	CmucStore.CollectionBusinesses._ID,
	CmucStore.CollectionBusinesses.NAME,
	CmucStore.CollectionBusinesses.CONTENT,
	CmucStore.CollectionBusinesses.ICON,
	CmucStore.CollectionBusinesses.COLLECTION_TIME,
	CmucStore.CollectionBusinesses.APP_TAG,
	CmucStore.CollectionBusinesses.BUSINESS_ID,
	CmucStore.CollectionBusinesses.USER_NAME,
	CmucStore.CollectionBusinesses.DELETE_FLAG,
	CmucStore.CollectionBusinesses.LISTORDER
};

private static String sBookmarksProjection[]={
	CmucStore.BookMarks._ID,
	CmucStore.BookMarks.TITLE,
	CmucStore.BookMarks.URL,
	CmucStore.BookMarks.TIME,
	CmucStore.BookMarks.USER_NAME
};

private static String sVisitHistoryProjection[]={
	CmucStore.VisitHistory._ID,
	CmucStore.VisitHistory.TITLE,
	CmucStore.VisitHistory.URL,
	CmucStore.VisitHistory.TIME,
	CmucStore.VisitHistory.USER_NAME
};

private CmucDbManager(ContentResolver cr) {
	super();
	this.cr = cr;
}

public static CmucDbManager getInstance(ContentResolver cr){
	if(instance==null)
		instance=new CmucDbManager(cr);
	return instance;
}

public synchronized void readMissMessage(){
	StringBuilder where = new StringBuilder();
	where.append(CmucStore.Messages.MISSED_FLAG+"=="+Message.MISS_FLAG);
    ContentValues value=new ContentValues();
    value.put(CmucStore.Messages.MISSED_FLAG, Message.READED_FLAG);
	cr.update(CmucStore.Messages.CONTENT_URI, value, where.toString(), null);
   }

public synchronized void updateReadedMessage(int _id){
	StringBuilder where = new StringBuilder();
	where.append(CmucStore.Messages._ID+"=="+_id);
	ContentValues values=new ContentValues();
	values.put(CmucStore.Messages.OPERATION_COMPLETE, Message.COMPLETE);
	cr.update(CmucStore.Messages.CONTENT_URI, values, where.toString(), null);
}

public synchronized void readed(int _id){
	StringBuilder where = new StringBuilder();
	where.append(CmucStore.Messages._ID+"=="+_id);
	ContentValues values=new ContentValues();
	values.put(CmucStore.Messages.READED, Message.READED_FLAG);
	cr.update(CmucStore.Messages.CONTENT_URI, values, where.toString(), null);
}

public synchronized void updateFeedbackMessage(int _id,String feedBackCotent,long feedbackTime){
	StringBuilder where = new StringBuilder();
	where.append(CmucStore.Messages._ID+"=="+_id);
	ContentValues values=new ContentValues();
	values.put(CmucStore.Messages.OPERATION_COMPLETE, Message.COMPLETE);
	values.put(CmucStore.Messages.FEED_BACK_CONTENT,feedBackCotent);
	values.put(CmucStore.Messages.FEED_BACK_TIME, feedbackTime);
	cr.update(CmucStore.Messages.CONTENT_URI, values, where.toString(), null);
}

public int getMissMessageCount(){
	StringBuilder where = new StringBuilder();
	where.append(CmucStore.Messages.MISSED_FLAG+"="+Message.MISS_FLAG);
    Cursor cursor=cr.query(CmucStore.Messages.CONTENT_URI, sMessageProjection, where.toString(), null, CmucStore.Messages.DEFAULT_SORT_ORDER);   
    int count=cursor.getCount();
    return count;
}

public void recordMessage(Message message){
	ContentValues values=messageToContentValue(message);
	cr.insert(CmucStore.Messages.CONTENT_URI, values);
}

public void startQueryMessage(AsyncQueryHandler queryHandler,int token,String userName){
	 StringBuilder where = new StringBuilder();
	 where.append(CmucStore.Messages.USER_NAME+"="+"'"+userName+"'");
	 queryHandler.startQuery(token, where.toString(), CmucStore.Messages.CONTENT_URI, sMessageProjection,null,null, CmucStore.Messages.DEFAULT_SORT_ORDER+" desc");
}

public Cursor queryMessage(){
	 ArrayList<Message> messages=new ArrayList<Message>();
	 Cursor cursor=cr.query(CmucStore.Messages.CONTENT_URI, sMessageProjection, null, null, CmucStore.Messages.DEFAULT_SORT_ORDER);  
	  for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
		  Message message=cursorToMessage(cursor);
		  messages.add(message);
	  }
	return cursor;
}

public synchronized void deleteMessage(int _id){
	StringBuilder where = new StringBuilder();
	where.append(CmucStore.Messages._ID+"=="+_id);
	cr.delete(CmucStore.Messages.CONTENT_URI, where.toString(), null);
}

public synchronized void startDeleteMessage(AsyncQueryHandler handler, int token,int _id) {
	StringBuilder where = new StringBuilder();
	where.append(CmucStore.Messages._ID+"=="+_id);
    handler.startDelete(token, null, CmucStore.Messages.CONTENT_URI, where.toString(), null);
}

public synchronized void startDeleteAllMessages(AsyncQueryHandler handler, int token) {
    handler.startDelete(token, null, CmucStore.Messages.CONTENT_URI, null, null);
}

private ContentValues messageToContentValue(Message message){
   ContentValues value=new ContentValues();
   if(message.getUserName()!=null)
	   value.put(CmucStore.Messages.USER_NAME, message.getUserName());
   if(message.getTitle()!=null)
	   value.put(CmucStore.Messages.TITLE,message.getTitle());
   if(message.getTextContent()!=null)
	   value.put(CmucStore.Messages.TEXT_CONTENT,message.getTextContent());
   if(message.getMessageTime()!=0)
	   value.put(CmucStore.Messages.MESSAGE_TIME, message.getMessageTime());   
   if(message.getNotificationId()!=null)
	   value.put(CmucStore.Messages.NOTIFICATION_ID, message.getNotificationId());
   if(message.getOptionType()!=null)
	   value.put(CmucStore.Messages.OPTION_TYPE, message.getOptionType());
   if(message.getOptionContent()!=null)
	   value.put(CmucStore.Messages.OPTION_CONTENT, message.getOptionContent()); 
   value.put(CmucStore.Messages.OPERATION_COMPLETE, message.getOperationComplete());  
   value.put(CmucStore.Messages.MISSED_FLAG, message.getMissedFlag());
   return value;
}

public Message cursorToMessage(Cursor cursor){
	int id=cursor.getInt(cursor.getColumnIndex(CmucStore.Messages._ID));
	long messageTime=cursor.getLong(cursor.getColumnIndex(CmucStore.Messages.MESSAGE_TIME));
	String textContent=cursor.getString(cursor.getColumnIndex(CmucStore.Messages.TEXT_CONTENT));
	String title=cursor.getString(cursor.getColumnIndex(CmucStore.Messages.TITLE));
	int readedFlag=cursor.getInt(cursor.getColumnIndex(CmucStore.Messages.MISSED_FLAG));
	
	String notificationId=cursor.getString(cursor.getColumnIndex(CmucStore.Messages.NOTIFICATION_ID));
	String optionType=cursor.getString(cursor.getColumnIndex(CmucStore.Messages.OPTION_TYPE));
	String optionContent=cursor.getString(cursor.getColumnIndex(CmucStore.Messages.OPTION_CONTENT));
	int operationComplete=cursor.getInt(cursor.getColumnIndex(CmucStore.Messages.OPERATION_COMPLETE));
	
	long feedbackTime=cursor.getLong(cursor.getColumnIndex(CmucStore.Messages.FEED_BACK_TIME));
	String feedbackContent=cursor.getString(cursor.getColumnIndex(CmucStore.Messages.FEED_BACK_CONTENT));
	int readed=cursor.getInt(cursor.getColumnIndex(CmucStore.Messages.READED));
	Message message=new Message(id, readedFlag, messageTime, textContent, title);
	message.setNotificationId(notificationId);
	message.setOptionType(optionType);
	message.setOptionContent(optionContent);
	message.setOperationComplete(operationComplete);
	message.setFeedbackTime(feedbackTime);
	message.setFeedbackContent(feedbackContent);
	message.setReaded(readed);
	return message;
}

public void startQueryCollectionBusiness(AsyncQueryHandler queryHandler,int token,String userName){
	 StringBuilder where = new StringBuilder();
	 where.append(CmucStore.CollectionBusinesses.USER_NAME+"=="+"'"+userName+"'");
	 queryHandler.startQuery(token, where.toString(), CmucStore.CollectionBusinesses.CONTENT_URI, sCollectionBusinessesProjection,where.toString(),null, CmucStore.CollectionBusinesses.DEFAULT_SORT_ORDER+" desc");
}

public void recordCollectionBusiness(Item item){
	ContentValues values=CollectionBusinessToContentValue(item);
	cr.insert(CmucStore.CollectionBusinesses.CONTENT_URI, values);
}

public synchronized void updateCollectionBusiness(Item item){
	StringBuilder where = new StringBuilder();
	where.append(CmucStore.CollectionBusinesses.BUSINESS_ID+"=="+item.getBusinessId());
	where.append(" AND "+CmucStore.CollectionBusinesses.USER_NAME+"=="+"'"+item.getUserName()+"'");
	ContentValues values=CollectionBusinessToContentValue(item);
	cr.update(CmucStore.CollectionBusinesses.CONTENT_URI, values, where.toString(), null);
}

public synchronized int checkCollectionBusiness(String userName,Item item){
	StringBuilder where = new StringBuilder();
	where.append(CmucStore.CollectionBusinesses.BUSINESS_ID+"=="+item.getBusinessId());
	where.append(" AND "+CmucStore.CollectionBusinesses.USER_NAME+"=="+"'"+userName+"'");
	Cursor cursor=cr.query(CmucStore.CollectionBusinesses.CONTENT_URI, sCollectionBusinessesProjection, where.toString(), null, CmucStore.CollectionBusinesses.DEFAULT_SORT_ORDER);  
	if(cursor!=null){		
		if(cursor.getCount()!=0){
			cursor.moveToFirst();
			long deleteFlag=cursor.getInt(cursor.getColumnIndex(CmucStore.CollectionBusinesses.DELETE_FLAG));
			if(deleteFlag==Item.DELETED){
				return DELETE_FALG_ITEM;
			}else{
				return NO_DELETE_FALG_ITEM;
			}
		}else{
			return NO_CONTAIN_ITEM;
		}
	}
	else{
		return NO_CONTAIN_ITEM;
	}
}

public synchronized boolean swapCollectionBusiness(Item oldItem,Item newItem){
	long oldListOrder=oldItem.getListOrder();
	long newListOrder=newItem.getListOrder();
	oldItem.setListOrder(newListOrder);
	newItem.setListOrder(oldListOrder);
	oldItem.setCollectionTime(System.currentTimeMillis());
	newItem.setCollectionTime(System.currentTimeMillis());
	updateCollectionBusiness(oldItem);
	updateCollectionBusiness(newItem);
	return true;
}

public  AppMenus queryCollectionBusiness(String userName,boolean isContainDelete){
	 StringBuilder where = new StringBuilder();
	 where.append(CmucStore.CollectionBusinesses.USER_NAME+"=="+"'"+userName+"'");
	 Cursor cursor=cr.query(CmucStore.CollectionBusinesses.CONTENT_URI, sCollectionBusinessesProjection, where.toString(), null, CmucStore.CollectionBusinesses.DEFAULT_SORT_ORDER); 
	 if(cursor==null){
		 return null;
	 }else{
		 return cursorToAppmenus(cursor,isContainDelete);
	 }
	
}

public AppMenus cursorToAppmenus(Cursor cursor,boolean isContainDelete) {
	ArrayList<AppMenuBean> collectionBusinesses=new ArrayList<AppMenuBean>();
	 for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
		 AppMenuBean collectionBusiness=cursorToCollectionBusiness(cursor);
		  if(!isContainDelete){
			  if(collectionBusiness.getDeleteFlag()==1){
				  continue;
			  }
		  }	  
		  collectionBusinesses.add(collectionBusiness);
	  }
	 AppMenusBean appMenusBean=new AppMenusBean();
	 appMenusBean.setDatas(collectionBusinesses);
	return new AppMenus(appMenusBean,AppMenus.TIME_SORT);
}

public synchronized void deleteCollectionBusiness(Item item){
	StringBuilder where = new StringBuilder();
	where.append(CmucStore.CollectionBusinesses.BUSINESS_ID+"=="+item.getBusinessId());
	where.append(" AND "+CmucStore.CollectionBusinesses.USER_NAME+"=="+"'"+item.getUserName()+"'");
	updateCollectionBusiness(item);
}

public synchronized void deleteCollectionBusiness(String userName){
	StringBuilder where = new StringBuilder();
	where.append(CmucStore.CollectionBusinesses.DELETE_FLAG+"=="+Item.DELETED);
	where.append(" AND "+CmucStore.CollectionBusinesses.USER_NAME+"=="+"'"+userName+"'");
	cr.delete(CmucStore.CollectionBusinesses.CONTENT_URI, where.toString(), null);
}


public synchronized void deleteAllCollectionBusiness(String userName){
	StringBuilder where = new StringBuilder();
	where.append(CmucStore.CollectionBusinesses.USER_NAME+"=="+"'"+userName+"'");
	cr.delete(CmucStore.CollectionBusinesses.CONTENT_URI, where.toString(), null);
}

public AppMenuBean cursorToCollectionBusiness(Cursor cursor){
	int id=cursor.getInt(cursor.getColumnIndex(CmucStore.CollectionBusinesses._ID));
	long collectionTime=cursor.getLong(cursor.getColumnIndex(CmucStore.CollectionBusinesses.COLLECTION_TIME));
	String userName=cursor.getString(cursor.getColumnIndex(CmucStore.CollectionBusinesses.USER_NAME));
	String appTag=cursor.getString(cursor.getColumnIndex(CmucStore.CollectionBusinesses.APP_TAG));
	String icon=cursor.getString(cursor.getColumnIndex(CmucStore.CollectionBusinesses.ICON));
	int businessId=cursor.getInt(cursor.getColumnIndex(CmucStore.CollectionBusinesses.BUSINESS_ID));
	String content=cursor.getString(cursor.getColumnIndex(CmucStore.CollectionBusinesses.CONTENT));
	String name=cursor.getString(cursor.getColumnIndex(CmucStore.CollectionBusinesses.NAME));
	int deleteFlag=cursor.getInt(cursor.getColumnIndex(CmucStore.CollectionBusinesses.DELETE_FLAG));
	long listOrder=cursor.getLong(cursor.getColumnIndex(CmucStore.CollectionBusinesses.LISTORDER));
	
	AppMenuBean collectionBusiness=new AppMenuBean(name, content, businessId, icon, appTag);
	collectionBusiness.setId(id);
	collectionBusiness.setCollectionTime(collectionTime);
	collectionBusiness.setUserName(userName);
	collectionBusiness.setDeleteFlag(deleteFlag);
	collectionBusiness.setListOrder(listOrder);
	return collectionBusiness;
}

private ContentValues CollectionBusinessToContentValue(Item item){
	   ContentValues value=new ContentValues();
	   if(item.getName()!=null)
		   value.put(CmucStore.CollectionBusinesses.NAME,item.getName());
	   if(item.getContent()!=null)
		   value.put(CmucStore.CollectionBusinesses.CONTENT,item.getContent());
	   if(item.getCollectionTime()!=0)
		   value.put(CmucStore.CollectionBusinesses.COLLECTION_TIME, item.getCollectionTime());   
	   if(item.getBusinessId()!=0)
		   value.put(CmucStore.CollectionBusinesses.BUSINESS_ID, item.getBusinessId());
	   if(item.getAppTag()!=null)
		   value.put(CmucStore.CollectionBusinesses.APP_TAG, item.getAppTag());
	   if(item.getIcon()!=null)
		   value.put(CmucStore.CollectionBusinesses.ICON, item.getIcon()); 
	   if(item.getUserName()!=null)
		   value.put(CmucStore.CollectionBusinesses.USER_NAME, item.getUserName());
	   value.put(CmucStore.CollectionBusinesses.DELETE_FLAG, item.getDeleteFlag());
	   value.put(CmucStore.CollectionBusinesses.LISTORDER, item.getListOrder());
	   return value;
	}


private ContentValues bookmarkToContentValue(BookmarkBean bookMarkBean){
	   ContentValues value=new ContentValues();
	   value.put(CmucStore.BookMarks.TITLE, bookMarkBean.getTitle());
	   value.put(CmucStore.BookMarks.URL,bookMarkBean.getUrl());
	   value.put(CmucStore.BookMarks.TIME, bookMarkBean.getTime());
	   value.put(CmucStore.BookMarks.USER_NAME, bookMarkBean.getUserName());
	   return value;
	}

public void recordBookmark(BookmarkBean bookMarkBean){
	ContentValues values=bookmarkToContentValue(bookMarkBean);
	cr.insert(CmucStore.BookMarks.CONTENT_URI, values);
}

public synchronized boolean checkBookmarkExist(BookmarkBean bookMarkBean){
	StringBuilder where = new StringBuilder();
	where.append(CmucStore.BookMarks.TITLE+"=="+"'"+bookMarkBean.getTitle()+"'");
	//where.append(" AND "+CmucStore.BookMarks.USER_NAME+"=="+"'"+bookMarkBean.getUserName()+"'");
	Cursor cursor=cr.query(CmucStore.BookMarks.CONTENT_URI, sBookmarksProjection, where.toString(), null, CmucStore.BookMarks.DEFAULT_SORT_ORDER);  
	if(cursor==null){
		return false;
	}
	return cursor.getCount()!=0;
}

public synchronized void updateBookmark(BookmarkBean oldBookMarkBean,BookmarkBean newBookMarkBean){
	StringBuilder where = new StringBuilder();
	where.append(CmucStore.BookMarks.TITLE+"=="+"'"+oldBookMarkBean.getTitle()+"'");
	//where.append(" AND "+CmucStore.BookMarks.USER_NAME+"=="+"'"+oldBookMarkBean.getUserName()+"'");
	ContentValues values=bookmarkToContentValue(newBookMarkBean);
	cr.update(CmucStore.BookMarks.CONTENT_URI, values, where.toString(), null);
}

public void startQueryBookmarks(AsyncQueryHandler queryHandler,int token,String userName){
	//StringBuilder where = new StringBuilder();
	//where.append(CmucStore.BookMarks.USER_NAME+"=="+"'"+userName+"'");
	 queryHandler.startQuery(token, null, CmucStore.BookMarks.CONTENT_URI,sBookmarksProjection,null,null, CmucStore.BookMarks.DEFAULT_SORT_ORDER+" desc");
}

public BookmarkBean cursorToBookmark(Cursor cursor){
	int id=cursor.getInt(cursor.getColumnIndex(CmucStore.BookMarks._ID));
	long time=cursor.getLong(cursor.getColumnIndex(CmucStore.BookMarks.TIME));
	String title=cursor.getString(cursor.getColumnIndex(CmucStore.BookMarks.TITLE));
	String url=cursor.getString(cursor.getColumnIndex(CmucStore.BookMarks.URL));	
	String userName=cursor.getString(cursor.getColumnIndex(CmucStore.BookMarks.USER_NAME));
	BookmarkBean bookMarkBean=new BookmarkBean(time, title, url);
	bookMarkBean.setUserName(userName);
	bookMarkBean.setId(id);
	return bookMarkBean;
}

public synchronized void deleteBookmark(BookmarkBean bookMarkBean){
	StringBuilder where = new StringBuilder();
	where.append(CmucStore.BookMarks._ID+"=="+bookMarkBean.getId());
	cr.delete(CmucStore.BookMarks.CONTENT_URI, where.toString(), null);
}

public void recordVisitHistory(VisitHistoryBean visitHistoryBean){
	ContentValues values=visitHistoryToContentValue(visitHistoryBean);
	cr.insert(CmucStore.VisitHistory.CONTENT_URI, values);
}

private ContentValues visitHistoryToContentValue(VisitHistoryBean visitHistoryBean){
	   ContentValues value=new ContentValues();
	   value.put(CmucStore.VisitHistory.TITLE, visitHistoryBean.getTitle());
	   value.put(CmucStore.VisitHistory.URL,visitHistoryBean.getUrl());
	   value.put(CmucStore.VisitHistory.TIME, visitHistoryBean.getTime());
	   value.put(CmucStore.VisitHistory.USER_NAME, visitHistoryBean.getUserName());
	   return value;
	}

public void startQueryVisitHistory(AsyncQueryHandler queryHandler,int token){
	 queryHandler.startQuery(token, null, CmucStore.VisitHistory.CONTENT_URI,sVisitHistoryProjection,null,null, CmucStore.VisitHistory.DEFAULT_SORT_ORDER+" desc");
	
}

public synchronized void deleteVisitHistory(VisitHistoryBean visitHistoryBean){
	StringBuilder where = new StringBuilder();
	where.append(CmucStore.VisitHistory._ID+"=="+visitHistoryBean.getId());
	cr.delete(CmucStore.VisitHistory.CONTENT_URI, where.toString(), null);
}

public void cursorToVisitHistorys(Cursor cursor,ArrayList<ArrayList<VisitHistoryBean>> historyChildArray) {
	long todayFrom = StarCpyDateUtils.getTodayFrom();
	long todayEnd = StarCpyDateUtils.getTodayEnd();
	long yesterdayFrom = StarCpyDateUtils.getYesterdayFrom();
	long yesterdayEnd = StarCpyDateUtils.getYesterdayEnd();
	 for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
		 VisitHistoryBean visitHistoryBean=cursorToVisitHistoryBean(cursor);
		 long time=visitHistoryBean.getTime();
		 if(time > todayFrom && time < todayEnd){ 
			 historyChildArray.get(0).add(visitHistoryBean);
	        }else if(time > yesterdayFrom && time < yesterdayEnd){    
        	 historyChildArray.get(1).add(visitHistoryBean);
	        }else {
	         historyChildArray.get(2).add(visitHistoryBean);
	        }
	  }
}

private VisitHistoryBean cursorToVisitHistoryBean(Cursor cursor){
	int id=cursor.getInt(cursor.getColumnIndex(CmucStore.VisitHistory._ID));
	long time=cursor.getLong(cursor.getColumnIndex(CmucStore.VisitHistory.TIME));
	String userName=cursor.getString(cursor.getColumnIndex(CmucStore.VisitHistory.USER_NAME));
	String title=cursor.getString(cursor.getColumnIndex(CmucStore.VisitHistory.TITLE));
	String url=cursor.getString(cursor.getColumnIndex(CmucStore.VisitHistory.URL));
	VisitHistoryBean visitHistoryBean=new VisitHistoryBean(time, title, url);
	visitHistoryBean.setId(id);
	visitHistoryBean.setUserName(userName);
	return visitHistoryBean;
}


}
