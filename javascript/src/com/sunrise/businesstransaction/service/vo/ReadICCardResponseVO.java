package com.sunrise.businesstransaction.service.vo;

import com.sunrise.businesstransaction.utils.StringConverter;
/**
 * IC卡解释
 * @author toshiba
 *
 */
public class ReadICCardResponseVO extends ResponseVO {
	
	private String iccidFlag;
	
	private int iccidLength;
	
	private String iccid;
	
	private String field1Flag;
	
	private int field1Length;
	
	private String field1;

	private String field2Flag;
	
	private int field2Length;
	
	private String field2;
	
	//PAD收：68 29 00 68   28        68   00 00   01           14 00    38 39 38 36 30 30 34 30 30 39 31 30 31 31 33 30 39 34 32 35 	   02 01 00 00   03 01 00 00    13 31 0A 01 04 0C  40  16
	//                    控制域      类型标识  地址域    01SIM卡ICCID  20字节       ICCID值为：89860040091011309425                                 02备用1 长度1 03备用2  长度1           时标                                         CRC

	public ReadICCardResponseVO(String hex) {
		super(hex);

		int start = 8;
		this.iccidFlag = "" + StringConverter.getIntegerFromArr(start, start+1, responseStringArr);
		start ++;
		this.iccidLength = StringConverter.getIntegerFromArr(start, start+2, responseStringArr);
		start +=2;
		this.iccid = StringConverter.getHexStringFromArr(start, (start + iccidLength), responseStringArr);
		start = start + iccidLength;
		
		//TODO 备用字段
		this.field1Flag = "" + StringConverter.getIntegerFromArr(start, start+1, responseStringArr);
		start ++;
		this.field1Length = StringConverter.getIntegerFromArr(start, start+2, responseStringArr);
		start +=2;
		this.field1 = StringConverter.getHexStringFromArr(start, (start+field1Length), responseStringArr)+"";
		start = start + field1Length;
		
		this.field2Flag = "" + StringConverter.getIntegerFromArr(start, start+1, responseStringArr);
		start ++;
		this.field2Length = StringConverter.getIntegerFromArr(start, start+2, responseStringArr);
		start +=2;
		this.field2 = StringConverter.getHexStringFromArr(start, (start+field2Length), responseStringArr)+"";
		start = start + field2Length;
	}

	public String getField1Flag() {
		return field1Flag;
	}

	public void setField1Flag(String field1Flag) {
		this.field1Flag = field1Flag;
	}

	public int getField1Length() {
		return field1Length;
	}

	public void setField1Length(int field1Length) {
		this.field1Length = field1Length;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2Flag() {
		return field2Flag;
	}

	public void setField2Flag(String field2Flag) {
		this.field2Flag = field2Flag;
	}

	public int getField2Length() {
		return field2Length;
	}

	public void setField2Length(int field2Length) {
		this.field2Length = field2Length;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	public String getIccidFlag() {
		return iccidFlag;
	}

	public void setIccidFlag(String iccidFlag) {
		this.iccidFlag = iccidFlag;
	}

	public int getIccidLength() {
		return iccidLength;
	}

	public void setIccidLength(int iccidLength) {
		this.iccidLength = iccidLength;
	}

	public String getIccid() {
		return iccid;
	}

	public void setIccid(String iccid) {
		this.iccid = iccid;
	}

	
}
