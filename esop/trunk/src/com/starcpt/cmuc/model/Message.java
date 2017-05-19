package com.starcpt.cmuc.model;

public class Message {
 //private static final String TAG="Message";
 private int id;
 private String userName;
 private int missedFlag=0;
 private long messageTime=0;
 private String textContent;
 private String title; 
 
 private String notificationId;
 private String optionType;
 private String optionContent;
 private int operationComplete;
 private int readed;
 private long feedbackTime=0;
 private String feedbackContent;
 
 
 public static final int MISS_FLAG=0;
 public static final int READED_FLAG=1;
 
 public static final int UNCOMPLETE=0;
 public static final int COMPLETE=1;
 
 public static final String READED_OPTION_TYPE="0";
 public static final String FEEDBACK_OPTION_TYPE="1";
 public static final String WEB_OPTION_TYPE="2";
 public static final String FUNCTION_OPTION_TYPE="3";
 

protected Message(int missedFlag,String text_content,
		String title,long messageTime) {
	super();
	this.missedFlag = missedFlag;
	this.messageTime = messageTime;
	this.textContent = text_content;
	this.title = title;
}


protected Message(String userName, int missedFlag,
		String textContent, String title,long messageTime) {
	super();
	this.userName = userName;
	this.missedFlag = missedFlag;
	this.messageTime = messageTime;
	this.textContent = textContent;
	this.title = title;
}


public Message(int id,int missedFlag, long messageTime, String text_content,
		String title){
	this(missedFlag,text_content,title,messageTime);
	this.id=id;
}



public Message(String textContent,String title,long messageTime){
	this(0,textContent,title,messageTime);
}

public Message(){};

public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public int getMissedFlag() {
	return missedFlag;
}
public void setMissedFlag(int missedFlag) {
	this.missedFlag = missedFlag;
}
public long getMessageTime() {
	return messageTime;
}
public void setMessageTime(long messageTime) {
	this.messageTime = messageTime;
}

public String getTextContent() {
	return textContent;
}

public void setTextContent(String textContent) {
	this.textContent = textContent;
}

public String getTitle() {
	return title;
}

public void setTitle(String title) {
	this.title = title;
}

public String getUserName() {
	return userName;
}

public void setUserName(String userName) {
	this.userName = userName;
}


public String getNotificationId() {
	return notificationId;
}


public void setNotificationId(String notificationId) {
	this.notificationId = notificationId;
}


public String getOptionType() {
	return optionType;
}


public void setOptionType(String optionType) {
	this.optionType = optionType;
}



public int getOperationComplete() {
	return operationComplete;
}


public void setOperationComplete(int operationComplete) {
	this.operationComplete = operationComplete;
}


public String getOptionContent() {
	return optionContent;
}


public void setOptionContent(String optionContent) {
	this.optionContent = optionContent;
}
 

public long getFeedbackTime() {
	return feedbackTime;
}


public void setFeedbackTime(long feedbackTime) {
	this.feedbackTime = feedbackTime;
}


public String getFeedbackContent() {
	return feedbackContent;
}


public void setFeedbackContent(String feedbackContent) {
	this.feedbackContent = feedbackContent;
}



public int getReaded() {
	return readed;
}


public void setReaded(int readed) {
	this.readed = readed;
}


/*class ReadedTask extends GenericTask{

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		// TODO Auto-generated method stub
		TaskParams param = params[0];
		String notificationId = param.getString(CmucApplication.NOTIFICATION_ID);
		try {
			ReadedBean readedBean=CmucApplication.sServerClient.readed(notificationId);
			return TaskResult.OK;
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return TaskResult.FAILED;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return TaskResult.FAILED;
		}
	}
	
}*/

}
