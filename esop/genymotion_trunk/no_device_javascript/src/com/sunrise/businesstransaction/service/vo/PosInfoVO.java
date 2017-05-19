package com.sunrise.businesstransaction.service.vo;

import com.sunrise.businesstransaction.utils.StringConverter;

public class PosInfoVO extends ResponseVO{
	private String terminalNo;//终端SN
	private String netTerminalNo;//客户入网终端号
	private String bankTerminalNo;//银联终端号
	public String getTerminalNo() {
		return terminalNo;
	}
	public void setTerminalNo(String terminalNo) {
		this.terminalNo = terminalNo;
	}
	public String getNetTerminalNo() {
		return netTerminalNo;
	}
	public void setNetTerminalNo(String netTerminalNo) {
		this.netTerminalNo = netTerminalNo;
	}
	public String getBankTerminalNo() {
		return bankTerminalNo;
	}
	public void setBankTerminalNo(String bankTerminalNo) {
		this.bankTerminalNo = bankTerminalNo;
	}
	
	
	public PosInfoVO(String hex) {
		super(hex);
		
		int start = 8;
		String terminalNoFlg = "" + StringConverter.getIntegerFromArr(start, start+1, responseStringArr);
		start ++;
		int terminalNoLength = StringConverter.getIntegerFromArr(start, start+2, responseStringArr);
		start +=2;
		this.terminalNo = StringConverter.getStringFromArr(start, (start + terminalNoLength), responseStringArr);
		start = start + terminalNoLength;
		System.out.println("terminalNo="+terminalNo);
		
		String netTerminalNoFlg = "" + StringConverter.getIntegerFromArr(start, start+1, responseStringArr);
		start ++;
		int netTerminalNoLength = StringConverter.getIntegerFromArr(start, start+2, responseStringArr);
		start +=2;
		this.netTerminalNo = StringConverter.getStringFromArr(start, (start+netTerminalNoLength), responseStringArr)+"";
		start = start + netTerminalNoLength;
		System.out.println("netTerminalNo="+netTerminalNo);
		
		String bankTerminalNoFlag = "" + StringConverter.getIntegerFromArr(start, start+1, responseStringArr);
		start ++;
		int bankTerminalNoLength = StringConverter.getIntegerFromArr(start, start+2, responseStringArr);
		start +=2;
		this.bankTerminalNo = StringConverter.getStringFromArr(start, (start+bankTerminalNoLength), responseStringArr)+"";
		start = start + bankTerminalNoLength;
		System.out.println("bankTerminalNo="+bankTerminalNo);
	}
}
