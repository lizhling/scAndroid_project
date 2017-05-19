package com.starcpt.cmuc.exception.business;

/**
 * Business ResultCode is not 000
 */
public class BusinessException extends Exception{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private int resultCode=-1;
	public BusinessException(String msg) {
	    super(msg);
	}
	
	public BusinessException(Exception cause) {
	    super(cause);
	}
	
	public BusinessException(String msg, int statusCode) {
	    super(msg);
	    this.resultCode = statusCode;
	}
	
	public BusinessException(String msg, Exception cause) {
	    super(msg, cause);
	}
	
	public BusinessException(String msg, Exception cause, int statusCode) {
	    super(msg, cause);
	    this.resultCode = statusCode;
	}
	
	public int getStatusCode() {
	    return this.resultCode;
	}
}
