package com.starcpt.cmuc.exception.business;

public class UserNameOrPasswordError extends BusinessException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserNameOrPasswordError(String msg, int statusCode) {
		super(msg, statusCode);
		// TODO Auto-generated constructor stub
	}

}
