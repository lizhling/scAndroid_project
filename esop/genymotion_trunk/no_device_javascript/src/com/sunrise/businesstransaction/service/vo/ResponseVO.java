package com.sunrise.businesstransaction.service.vo;

import com.sunrise.businesstransaction.utils.StringConverter;

/**
 * 基类解释包，主要是解释时间区域
 * @author toshiba
 *
 */
public class ResponseVO {
	
	protected String[] responseStringArr = null;

	protected String contralArea;
	
	protected String typeFlag;
	
	protected String addressArea;
	
	protected String responseTime;

	public ResponseVO(){
		
	}
	
	public ResponseVO(String hex) {
		if(!checkResponse()){
			System.out.println("数据包校验不通过！");
			return;
		}
		responseStringArr = StringConverter.converterString2Array(hex, 2);
		if(responseStringArr.length < 8){
			return;
		}
		
		this.typeFlag = StringConverter.getIntegerFromArr(5, 6, responseStringArr) + "";

		int start = responseStringArr.length - 8;
		//秒 分 时 日 月 年
		int second = StringConverter.getIntegerFromArr(start, start+1, responseStringArr);
		start ++;
		int minite = StringConverter.getIntegerFromArr(start, start+1, responseStringArr);
		start ++;
		int hour = StringConverter.getIntegerFromArr(start, start+1, responseStringArr);
		start ++;
		int day = StringConverter.getIntegerFromArr(start, start+1, responseStringArr);
		start ++;
		int month = StringConverter.getIntegerFromArr(start, start+1, responseStringArr);
		start ++;
		int year = StringConverter.getIntegerFromArr(start, start+1, responseStringArr);
		
		this.responseTime = year + "-" + month + "-" + day + " " + hour + ":" + minite + ":" + second;
	}
	
	public boolean checkResponse(){
		
		return true;
	}
		
	public String getContralArea() {
		return contralArea;
	}

	public void setContralArea(String contralArea) {
		this.contralArea = contralArea;
	}

	public String getTypeFlag() {
		return typeFlag;
	}

	public void setTypeFlag(String typeFlag) {
		this.typeFlag = typeFlag;
	}

	public String getAddressArea() {
		return addressArea;
	}

	public void setAddressArea(String addressArea) {
		this.addressArea = addressArea;
	}

	public String getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}
	
	
}
