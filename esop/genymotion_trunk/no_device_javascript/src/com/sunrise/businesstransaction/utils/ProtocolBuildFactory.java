package com.sunrise.businesstransaction.utils;

import java.util.ArrayList;
import java.util.List;

import com.sunrise.businesstransaction.service.vo.PrintVO;

public class ProtocolBuildFactory {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static byte[] buildPrintProtocolByte(PrintVO vo){
		List<Byte> list = new ArrayList<Byte>();
		list.add(stringToByte("5A"));
//		类型标识105
		list.add(stringToByte("69"));
		//地址域
		list.add(stringToByte("01"));
		list.add(stringToByte("01"));
		
		list.addAll(buildSingleField("01", vo.getCustName()));
		list.addAll(buildSingleField("02", vo.getTicketName()));
		list.addAll(buildSingleField("03", vo.getNumber()));
		list.addAll(buildSingleField("04", vo.getDate()));
		list.addAll(buildSingleField("05", vo.getName()));
		list.addAll(buildSingleField("06", vo.getPhoneNum()));
		list.addAll(buildSingleField("07", vo.getIdentity()));
		list.addAll(buildSingleField("08", vo.getBrand()));
		list.addAll(buildSingleField("09", vo.getCheckRightType()));
		list.addAll(buildSingleField("0a", vo.getBusiType()));
		list.addAll(buildSingleField("0b", vo.getAmount()));
		list.addAll(buildSingleField("0c", vo.getOrderNum()));
		list.addAll(buildSingleField("0d", vo.getPlanName()));
		list.addAll(buildSingleField("0e", vo.getProtocol()));
		list.addAll(buildSingleField("0f", vo.getCustDesc()));
		if(vo.getSign()!=null){
			list.addAll(getRequestSignPacket("10", vo.getSign()));
		}
		list.addAll(buildSingleField("11", vo.getPreUse1()));
		
		byte[] bb = listToArr(list);
		System.out.println("buildPrintProtocolByte =" + StringConvertUtils.byte2HexStr(bb));

		return bb;
	}
	
	public static byte[] buildPrintProtocol(String print,byte[] sign){
		List<Byte> list = new ArrayList<Byte>();
		list.add(stringToByte("5A"));
//		类型标识105
		list.add(stringToByte("69"));
		//地址域
		list.add(stringToByte("01"));
		list.add(stringToByte("01"));
		
		list.addAll(buildSingleField("01", print));
		if(sign!=null){
			list.addAll(getRequestSignPacket("02", sign));
		} else {
			list.addAll(buildSingleField("02", ""));
		}
		
		byte[] bb = listToArr(list);
		System.out.println("buildPrintProtocolByte =" + StringConvertUtils.byte2HexStr(bb));

		return bb;
	}
	
	/**
	 * 打印模板
	 * @param vo
	 * @return
	 */
	public static byte[] buildPrintProtocolTemplete(PrintVO vo){
		List<Byte> list = new ArrayList<Byte>();
		list.add(stringToByte("5A"));
//		类型标识105
		list.add(stringToByte("69"));
		//地址域
		list.add(stringToByte("01"));
		list.add(stringToByte("01"));
		
		list.addAll(buildSingleField("01", vo.getTemplate()));
		if(vo.getSign()!=null){
			list.addAll(getRequestSignPacket("10", vo.getSign()));
		}
		list.addAll(buildSingleField("11", vo.getPreUse1()));
		
		byte[] bb = listToArr(list);
		System.out.println("buildPrintProtocolByte =" + StringConvertUtils.byte2HexStr(bb));

		return bb;
	}
	
	private static byte stringToByte(String s) {
		return (byte)Integer.parseInt(s, 16);
	}
	

    private static byte[] listToArr(List<Byte> list) {
    	byte[] bytes = new byte[list.size()];
    	for(int i=0; i<bytes.length; i++){
    		bytes[i] = list.get(i);
    	}
    	return bytes;
    }
    
	private static List<Byte> buildSingleField(String seq, String content){
		List<Byte> list = new ArrayList<Byte>();
		list.add(stringToByte(seq));
		if(content != null){
			List<Byte> contentList = StringConvertUtils.chineseToCodeMulti(content);
//			sb.append(converInt2HexString(code.length()));
			int rlen = contentList.size();
			byte blen1 = (byte) (rlen % 256);
			byte blen2 = (byte) (rlen / 256);
			list.add(blen1);
			list.add(blen2);
			list.addAll(contentList);
		}else {
			list.add(stringToByte("00"));
			list.add(stringToByte("00"));
		}
		return list;
	}

	
	private static List<Byte> getRequestSignPacket(String seq,byte[] data) {
		List<Byte> list = new ArrayList<Byte>();
		if (null == data) {
			list.add(stringToByte("00"));
			list.add(stringToByte("00"));
		} else {
			list.add(stringToByte(seq));
			if (data.length <= 256) {
				list.add((byte) data.length);
				list.add(stringToByte("00"));
			} else {
				int hsize = data.length / 256;
				int lsize = data.length % 256;
				list.add((byte) lsize);
				list.add((byte) hsize);
			}
			
			for (int i = 0; i < data.length; i++) {
				list.add(data[i]);
			}
		}
		return list;
	}
	
	private static String converInt2HexString(int length){
		String hex = Integer.toHexString(length);
		if (hex.length() < 2) {
			return "0" + hex;
		}
		return hex;
	}
	
	public static String getStringZWByGb2312(String sb){
	
		StringBuffer result = new StringBuffer("");
		String[] hexArr = StringConverter.converterString2Array(sb, 4);
		for(int i=0; i<hexArr.length; i++){
			result.append(StringConvertUtils.codeToChinese(hexArr[i]));
		}
		return result.toString();
	}
	
}
