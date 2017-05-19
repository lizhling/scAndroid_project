package com.sunrise.businesstransaction.service.vo;

import com.sunrise.businesstransaction.utils.StringConverter;
/**
 * 打印解释
 * @author toshiba
 *
 */
public class PrintResponseVO extends ResponseVO {
	
	private String successFlag;
	
	private int successLength;
	
	private String success;
	//指令：68 0C 00 68   28        69    00 00      01         01 00   30 30     13 34 0A 01 04 0C  4D  16
    //                   控制域  类型标识      地址域        01成功与否             1字节            成功              时标
	public PrintResponseVO(String hex) {
		super(hex);

		int start = 8;
		this.successFlag = "" + StringConverter.getIntegerFromArr(start, start+1, responseStringArr);
		start ++;
		// 长度到底是两个字节
		this.successLength = StringConverter.getIntegerFromArr(start, start+2, responseStringArr);
		start +=2;
		this.success = StringConverter.getStringFromArr(start, start + successLength, responseStringArr)+"";
		start = start + successLength;
		
	}

	public String getSuccessFlag() {
		return successFlag;
	}

	public void setSuccessFlag(String successFlag) {
		this.successFlag = successFlag;
	}

	public int getSuccessLength() {
		return successLength;
	}

	public void setSuccessLength(int successLength) {
		this.successLength = successLength;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	
}
