package com.sunrise.businesstransaction.service.vo;

import com.sunrise.businesstransaction.utils.StringConverter;

public class ShengFenZhengResponseVO extends ResponseVO {
	private String seqFlag;
	
	private int seqLength;
	
	private String seq;
	
	private String successFlag;
	
	private int successLength;
	
	private String success;
	
	public ShengFenZhengResponseVO(String hex) {
		super(hex);
		
		int start = 8;
		this.seqFlag = "" + StringConverter.getIntegerFromArr(start, start+1, responseStringArr);
		start ++;
		this.seqLength = StringConverter.getIntegerFromArr(start, start+2, responseStringArr);
		start +=2;
		this.seq = StringConverter.getStringFromArr(start, (start + seqLength), responseStringArr);
		start = start + seqLength;
		
		// 要确定成功标志的长度为2个字节，高位在前
		this.successFlag = "" + StringConverter.getIntegerFromArr(start, start+1, responseStringArr);
		start ++;
		this.successLength = StringConverter.getIntegerFromArr(start, start+2, responseStringArr);
		start +=2;
		this.success = StringConverter.getStringFromArr(start, (start+successLength), responseStringArr)+"";
		start = start + successLength;
	}

	public String getSeqFlag() {
		return seqFlag;
	}

	public void setSeqFlag(String seqFlag) {
		this.seqFlag = seqFlag;
	}

	public int getSeqLength() {
		return seqLength;
	}

	public void setSeqLength(int seqLength) {
		this.seqLength = seqLength;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
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
