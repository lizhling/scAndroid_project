package com.sunrise.marketingassistant.exception.logic;

public class BusinessException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int returnCode;
	public BusinessException(String msg) {
	    super(msg);
	}
	
	public BusinessException(Exception cause) {
	    super(cause);
	}
	
	public BusinessException(String msg, int statusCode) {
	    super(msg);
	    this.returnCode = statusCode;
	}
	
	public BusinessException(String msg, Exception cause) {
	    super(msg, cause);
	}
	
	public BusinessException(String msg, Exception cause, int statusCode) {
	    super(msg, cause);
	    this.returnCode = statusCode;
	}
	
	public int getStatusCode() {
	    return this.returnCode;
	}
}
