package com.sunrise.businesstransaction.service.vo;

/**
 * 分解数据包体
 * @author toshiba
 *
 */
public class RecieveDataPacketVO {

	private byte b1;//第一个字节
	private int len;//包长度
	private byte b4;//第四个字节
	private byte[] data;//数据包
	private byte chksum;//校验和
	private byte end;//结束字节
	
	public byte getB1() {
		return b1;
	}

	public int getLen() {
		return len;
	}

	public byte getB4() {
		return b4;
	}

	public byte[] getData() {
		return data;
	}

	public byte getChksum() {
		return chksum;
	}

	public byte getEnd() {
		return end;
	}
	public RecieveDataPacketVO(byte[] bytes) {
		int idx = 0;
		b1 = bytes[idx++];
		byte b2 = bytes[idx++];
		byte b3 = bytes[idx++];
		len = b3 * 256 + b2;
		b4 = bytes[idx++];
		data = new byte[len];
		for (int i = 0; i < len; i++) {
			data[i] = bytes[idx++];
		}
		chksum = bytes[idx++];
		end = bytes[idx++];
	}
	
}
