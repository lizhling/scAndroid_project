package com.sunrise.businesstransaction.service.vo;

import com.sunrise.businesstransaction.utils.StringConverter;

/**
 * 冲正解释
 * @author toshiba
 *
 */
public class ChongZhengResponseVO extends ResponseVO {
	
private String seqFlag;
	
	private int seqLength;
	
	private String seq;//交易流水号
	
	private String cardIdFlag;
	
	private int cardIdLength;
	
	private String cardId;//卡号
	
	private String successFlag;
	
	private int successLength;
	
	private String success;
	
	//指令：68 0C 00 68   28        66    00 00      01         01 00   30 30     13 34 0A 01 04 0C  4D  16
    //                   控制域  类型标识      地址域        01成功与否             1字节            成功              时标
	public ChongZhengResponseVO(String hex) {
		super(hex);

		int start = 8;
		this.seqFlag = "" + StringConverter.getIntegerFromArr(start, start+1, responseStringArr);
		start ++;
		this.seqLength = StringConverter.getIntegerFromArr(start, start+2, responseStringArr);
		start +=2;
		this.seq = StringConverter.getStringFromArr(start, (start + seqLength), responseStringArr);
		start = start + seqLength;
		
		// 卡号标志的长度为2个字节，高位在前
		this.cardIdFlag = "" + StringConverter.getIntegerFromArr(start, start+1, responseStringArr);
		start ++;
		this.cardIdLength = StringConverter.getIntegerFromArr(start, start+2, responseStringArr);
		start +=2;
		this.cardId = StringConverter.getStringFromArr(start, (start+cardIdLength), responseStringArr)+"";
		start = start + cardIdLength;
//		
		
		// 要确定成功标志的长度为2个字节，高位在前
		this.successFlag = "" + StringConverter.getIntegerFromArr(start, start+1, responseStringArr);
		start ++;
		this.successLength = StringConverter.getIntegerFromArr(start, start+2, responseStringArr);
		start +=2;
		this.success = StringConverter.getStringFromArr(start, (start+successLength), responseStringArr)+"";
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

	public String getCardIdFlag() {
		return cardIdFlag;
	}

	public void setCardIdFlag(String cardIdFlag) {
		this.cardIdFlag = cardIdFlag;
	}

	public int getCardIdLength() {
		return cardIdLength;
	}

	public void setCardIdLength(int cardIdLength) {
		this.cardIdLength = cardIdLength;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	
}
