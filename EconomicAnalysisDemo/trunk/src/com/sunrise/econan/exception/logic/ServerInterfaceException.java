package com.sunrise.econan.exception.logic;

/**
 * Business ResultCode is not 000
 */
public class ServerInterfaceException extends Exception{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	 /** 处理成功*/
    public static final int BUSINESS_OK = 0;
    /**帐号或密码不正确*/
    public static final int BUSINESS_USERNAME_OR_PASSWORD = 1;
    /**系统错误*/
    public static final int BUSINESS_SYSTEM_ERROR=999;
    /**会话超时，请重新登录*/
    public static final int BUSINESS_CONVERSATION_TIME_OUT=2;
    public static final int BUSINESS_AUTHENTICATION_TIME_OUT=-1;
    private int resultCode=-1;
	public ServerInterfaceException(String msg) {
	    super(msg);
	}
	
	public ServerInterfaceException(Exception cause) {
	    super(cause);
	}
	
	public ServerInterfaceException(String msg, int statusCode) {
	    super(msg);
	    this.resultCode = statusCode;
	}
	
	public ServerInterfaceException(String msg, Exception cause) {
	    super(msg, cause);
	}
	
	public ServerInterfaceException(String msg, Exception cause, int statusCode) {
	    super(msg, cause);
	    this.resultCode = statusCode;
	}
	
	public int getStatusCode() {
	    return this.resultCode;
	}
}
