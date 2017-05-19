package com.sunrise.businesstransaction.service;

import com.sunrise.businesstransaction.service.vo.PrintVO;
import com.sunrise.businesstransaction.service.vo.ShenFenZheng2Data;
import com.sunrise.businesstransaction.utils.DateUtil;
import com.sunrise.businesstransaction.utils.ProtocolBuildFactory;
import com.sunrise.businesstransaction.utils.StringConvertUtils;
/**
 * 针对ＰＯＳ机的指令代码操作
 * @author toshiba
 *
 */
public class PosDataControlManager {

	//确认指令E5
	private static String CONFIM_CODE = "E5";
	
	public static String R_BFCB_TRUE_CODE = "122";
	public static String R_BFCB_FALSE_CODE = "90";
	
	
	/**
	 * 发送信息给POS请求读身份证信息
	 * PAD发（终端收）：68   0A 00  68    5A         64          01 01           10 36 0A 1B 03  0C  CRC 16
     *                       帧长8字节    控制域            类型标识100    总包长  第1包                 时间 秒 分 时 日 月  年
	 * @return 680A....
	 */
	public static byte[] requestReadIDCard() {

		StringBuffer dataBuffer = new StringBuffer("");
		String year = DateUtil.getYear().substring(2, 4);
		String month =  DateUtil.getMonth();
		String day = DateUtil.getDay();
		String hhmmss = DateUtil.getTime();
		String hh = hhmmss.split(":")[0];
		String mm = hhmmss.split(":")[1];
		String ss = hhmmss.split(":")[2];
		
		String message = dataBuffer.append(ss.trim()).append(",").append(mm.trim()).append(",").append(hh.trim()).append(",").append(day.trim()).append(",").append(month.trim()).append(",").append(year.trim()).append(",").toString();
		String[] strArr = message.split(",");
		byte[] b = new byte[strArr.length+4];
		int i = 0;
		b[i++] = 0x5A;
		
		b[i++] = 0x64;//读身份证 100
		b[i++] = 0x01;//总包长
		b[i++] = 0x01;//第1包
		
		for(String tmp:strArr){
			b[i++] = Byte.valueOf(tmp);
		}
		
		return  getRequestPacket(b);
	}
	
	/**
	 * 请求发送信息部分
	 * @param data 数据体区域
	 * @return
	 */
	private static byte[] getRequestPacket2(byte[] data) {
		if (null == data) return null;
		byte[] bytes = new byte[1 + data.length + 2];
		
		bytes[0] = 0x10;
		
		for (int i = 1; i < data.length + 1; i++) {
			bytes[i] = data[i - 1];
		}
		
		byte chksum = 0;
		for (int i = 0; i < data.length; i++) {
			chksum += data[i];
		}
		bytes[bytes.length - 2] = (byte) (chksum & 0xff);
		bytes[bytes.length - 1] = 0x16;
		
		return bytes;
	}
	
	/**
	 * 请求数据体
	 * @param data
	 * @return
	 */
	private static byte[] getRequestPacket(byte[] data) {
		if (null == data) return null;
		
		byte[] bytes = new byte[4 + data.length + 2];
		bytes[0] = 0x68;
		if (data.length <= 256) {
			bytes[1] = (byte) data.length;
			bytes[2] = 0;
		} else {
			int hsize = data.length / 256;
			int lsize = data.length % 256;
			bytes[1] = (byte) lsize;
			bytes[2] = (byte) hsize;
		}
		bytes[3] = 0x68;
		
		for (int i = 4; i < data.length + 4; i++) {
			bytes[i] = data[i - 4];
		}
		byte chksum = 0;
		for (int i = 0; i < data.length; i++) {
			chksum += data[i];
		}
		System.out.println("chksum = "+StringConvertUtils.byte2HexStr(new byte[]{chksum}));
		bytes[bytes.length - 2] = (byte) (chksum & 0xff);
		bytes[bytes.length - 1] = 0x16;
		System.out.println("最后一个 = "+StringConvertUtils.byte2HexStr(new byte[]{bytes[bytes.length - 1]}));
		return bytes;
	}
	
	/**
	 * PAD请求POS机把请求的信息发回来
	   * PAD发：10 7A      00 00  7A     16 
	 * @return
	 */
	public static byte[] requestReturnCommon(boolean bFCB){
		StringBuffer hexStr = new StringBuffer("");
		if(bFCB){
			hexStr.append(R_BFCB_TRUE_CODE);
		} else {
			hexStr.append(R_BFCB_FALSE_CODE);
		}
		hexStr.append(",0,0");
		
		String message = hexStr.toString();
		String[] strArr = message.split(",");
		byte[] b = new byte[strArr.length];
		int i = 0;
		for(String tmp:strArr){
			b[i++] = Byte.valueOf(tmp);
		}
		return getRequestPacket2(b);
	}
	
	public static byte[] requestE5Over(){
		byte[] b = new byte[1];
		b[0] = (byte) 0xE5;
		return b;
		
	}
	/**
	 * 读取身份证的信息
	 * @param data 16进制的字符串
	 * @return
	 */
	public static ShenFenZheng2Data getIDCardInfo(String data) {
		String.valueOf(Integer.parseInt("0c", 16));
		String strData = data;
//		strData = "AAAAAA96690508000090010004004E67D75F735E200020002000200020002000200020002000200020002000310030003100310039003800320030003800300038007F5E1C4E0177F16D3357025E9D5B895B3A537F89614EF2949B5CEF8DFF7E776D0D54455C3100F7537C6941005553435134003000330020002000200020002000200020002000330036003200340032003200310039003800320030003800300038003000300031003200F16D3357025E6C51895B405C9D5B895B0652405C200020002000200020003200300031003000300031003000370032003000330030003000310030003700200020002000200020002000200020002000200020002000200020002000200020002000574C66007E00320000FF85005151513E710DD564F336A76921E2295CBA5B03DBC3974C028463E082191CA8E69993A71747702B49EFE2B174483466D9333545049E1E4BF32AEFDF93DE663081ECCAEC942FBDD2AB91EEC7C61DDB36A77D9151AED35251515A3EA6CA51AB9BEB77FF049FEABDBC550BF449DD1D3622DCA2667D7CF11BBA6B32350D67AF75AC10AA70A7B520C0BC7618684BFC9F1EDAA3B1176B913FD937D776BA7B6DD5472DADFE05377E97BD838EEE1D3E392370661E616D99E427A111C70122B2391BA7B2FFF93E5FCBDDBA6684701C05BB2EE6FD5D98D976646C01E56C7582508231D5D61367A895C9980F50DE5874ED825FFE2E604D27530EB4E1FB0AC656ECA6F70F03573073DEC53EE994666BBCD97ACA6B46392020329D59D92899CDA1D45EB466285C3824C82E9136872866B7482AE37A6DD28CB53EC5DAB557BBD35D81EEB82841D75C18CA78707973EC29DCDAE0902151EA5725771F0CF723A5028FD5796FA2870CF55D77ABC4A50BBA2B1720F0C87278CAA231F5A763509C45A6D4EF4413F4D92EAE51996812068047C522977FACA49D033994A8EF66C5DB9104FD52E699DD8CE26A7069018B1941E16C6D9FAF79C65C81611568483E6DC762F1437488E316EEBFAF5CE2E239B7CE63531886339C3081518C0CC04DAC9046CC8946969601C75DF08A3C8168A11D3D8F393D9B71C11B76D1FA5D87CDC7E10D0D3C441A7670EDB2881478D85E9AD3B4C76D27D698D52A788593CE7F6BB8444F986ED147082427B2BC5F55951472BD998D07DC3D3A7A085BEBFB8B16F1F7BFB99DBA214E2786142B82DA1D1930C3A6C53DBB885E6C21BFCA2FF7D57AF8CCDA185E33731A588A40EB8D1A4DC948D8E8B8B8219046215DE1374B0326D95DA6B54E8F806FC5E14FA32C38D06726964854A4873BE65C2AD195602FA14C7E4D04F93801C6817AD16D09C6850AA43CD7E854EE2CBA0D152A5B476EC73C62E45B2061902F3EBD3FCF91B2DDA5A9EA5E7559DC7A79B3AFF25A3F7AE26166F8EC5D40B99CA328BBB36EF3F1BD46169312D9E5DF7E748D9ACC72F4739758BE29C4EFD17E37C60DB1292140033B93E865DEA09702161705B367C77E21920FB192F4E4CA658DFDC53EF0B6FB10F797D54EF9497E3232EC15102ABEFE2E9CBC95828EE9F06655EF94CE1545669A3D08711BCC373B6E83531AA60E755D473DA494CBAF8D6B73F380B25A3EE994E8D1CE4EE7EE803B1D70CB4094A12E83CFE2E2F2E8305FB2432157F5A455079EEEFE705C8B0EBB87105296A8A826C3062C2C3AD50A0A9FF0B11ADEA8EAED3837E0551E2848A688349C3A51AF7C5A3E0ECF111E531E7EB8D061B32E18C116CA269423AD2AAD4413BBB63346B1DD85125E45DA9F005ED4C9564F26C59D55691ECEA2C13609522451E2F7B969AF2B323CA53B07DE3CAA146233";
        
        int bsize = 2;
        byte[] dataBytes = new byte[strData.length() / bsize];
        int bidx = 0;
        int idx = 0;

        while (true) {
            String strByte = strData.substring(idx, idx + bsize);
            
            dataBytes[bidx++] = (byte) Integer.parseInt(strByte, 16);
            
            idx = idx + bsize;
            if (idx >= strData.length()) {
                break;
            }
        }
        byte[] bytes = dataBytes;
        ShenFenZheng2Data sfz = ShenFenZheng2Data.convert(bytes);
        
		return sfz;
	}
	
	/**
	 * 判断回应是否收到E5指令
	 * @param code
	 * @return
	 */
	public static boolean isResponseE5Code(String code){
		boolean isSuccess = false;
		if(code.equals(CONFIM_CODE)){
			isSuccess = true;
		}
		return isSuccess;
	}
	
	/**
	 * 发送信息给POS请求读银联卡信息
	 * PAD发（终端收）：68   17 00  68    5A       65         01 01       01      02 00     32 38    02      01 00  00     03       01 00  00       10 03 0A 01 04  0C      28  16
     *                                  控制域   类型标识101   总包长  第1包     01     金额 2字节     12856    02备用1 长度1         03备用2  长度1          时间：12年4月1日10点3分16秒   CRC 
	 * @param payValue 金额
	 * @return 680A....
	 */
	public static byte[] requestReadUnionPay(String payValue) {
		StringBuffer dataBuffer = new StringBuffer("");
		String year = DateUtil.getYear().substring(2, 4);
		String month =  DateUtil.getMonth();
		String day = DateUtil.getDay();
		String hhmmss = DateUtil.getTime();
		String hh = hhmmss.split(":")[0];
		String mm = hhmmss.split(":")[1];
		String ss = hhmmss.split(":")[2];
		
		String message = dataBuffer.append(ss.trim()).append(",").append(mm.trim()).append(",").append(hh.trim()).append(",").append(day.trim()).append(",").append(month.trim()).append(",").append(year.trim()).append(",").toString();
		String[] strArr = message.split(",");
		
		String payHexStr = Integer.toHexString(Integer.parseInt(payValue));
		if(payHexStr.length() % 2 != 0){
			payHexStr = "0" + payHexStr;
		}
		byte[] payBytes = new byte[payHexStr.length()/2];
		int g = 0;
		StringBuffer str = new StringBuffer(payHexStr);   
	    int start = 0;   
	    int end = start+2;   
	    while(true){   
          if(start>=str.length())   
               break;   
           String temp = str.substring(start, end);   
           payBytes[g++] = (byte) Integer.parseInt(temp, 16);              
           start = end;   
           end = end+2;   
           if(end>=str.length()){   
               end = str.length();   
           }   
	   }
		
		byte[] b = new byte[21 + payBytes.length];
		
		int i = 0;
		b[i++] = 0x5A;
		
		b[i++] = 0x65;//银联刷卡 101 
		b[i++] = 0x01;//总包长
		b[i++] = 0x01;//第1包
		b[i++] = 0x01;
		
		b[i++] = (byte) (payBytes.length % 256);
		b[i++] = (byte) (payBytes.length / 256);
		
		for (int j = 0; j < payBytes.length; j++) {
			b[i++] = payBytes[j];
		}
		
		b[i++] = 0x02;
		b[i++] = 0x01;
		b[i++] = 0x0;
		b[i++] = 0x0;
		
		b[i++] = 0x03;
		b[i++] = 0x01;
		b[i++] = 0x0;
		b[i++] = 0x0;
		
		for(String tmp:strArr){
			b[i++] = Byte.valueOf(tmp);
		}
		return getRequestPacket(b);
	}
	
	
	/**
	 * 发送信息给POS请求银联冲正信息
	 * PAD发（终端收）：68   26 00  68    5A       66     01 01          01            13 00     36 34 30 36 35 30 31 36 30 30 31 36 30 30 30 30 30 30 31   02 01 00 00   03 01 00 00    13 31 0A 01 04 0C  1D 16
     *                                  控制域      类型标识 总包长  第1包         01银行交易流水号：19字节        6406501600160000001                                         02备用1 长度1 03备用2  长度1    时标
	 * @param no 银行交易流水号
	 * @return 680A....
	 */
	public static byte[] requestReadUnionReversed(String no) {
		StringBuffer dataBuffer = new StringBuffer("");
		String year = DateUtil.getYear().substring(2, 4);
		String month =  DateUtil.getMonth();
		String day = DateUtil.getDay();
		String hhmmss = DateUtil.getTime();
		String hh = hhmmss.split(":")[0];
		String mm = hhmmss.split(":")[1];
		String ss = hhmmss.split(":")[2];
		
		String message = dataBuffer.append(ss.trim()).append(",").append(mm.trim()).append(",").append(hh.trim()).append(",").append(day.trim()).append(",").append(month.trim()).append(",").append(year.trim()).append(",").toString();
		String[] strArr = message.split(",");
		
		byte[] unionCardNoBytes = no.getBytes();
	    
		byte[] b = new byte[21 + unionCardNoBytes.length];
		
		int i = 0;
		b[i++] = 0x5A;
		
		b[i++] = 0x66;//银联冲正  102 
		b[i++] = 0x01;//总包长
		b[i++] = 0x01;//第1包
		b[i++] = 0x01;
		
		b[i++] = (byte) (unionCardNoBytes.length % 256);
		b[i++] = (byte) (unionCardNoBytes.length / 256);
		
		for (int j = 0; j < unionCardNoBytes.length; j++) {
			b[i++] = unionCardNoBytes[j];
		}
		
		b[i++] = 0x02;
		b[i++] = 0x01;
		b[i++] = 0x0;
		b[i++] = 0x0;
		
		b[i++] = 0x03;
		b[i++] = 0x01;
		b[i++] = 0x0;
		b[i++] = 0x0;
		
		for(String tmp:strArr){
			b[i++] = Byte.valueOf(tmp);
		}
		return getRequestPacket(b);
	}
	
	/**
	 * 发送信息给POS请求读SIM信息
	 * PAD发（终端收）：68   08 00  68    5A         67         01 01        10 36 0A  01 04  0C     36  16
     *                       帧长8字节    控制域    类型标识103    总包长  第1包          时间 秒 分    时      日    月     年           CRCS
	 * @return 680A....
	 */
	public static byte[] requestReadSIMCard() {
		StringBuffer dataBuffer = new StringBuffer("");
		String year = DateUtil.getYear().substring(2, 4);
		String month =  DateUtil.getMonth();
		String day = DateUtil.getDay();
		String hhmmss = DateUtil.getTime();
		String hh = hhmmss.split(":")[0];
		String mm = hhmmss.split(":")[1];
		String ss = hhmmss.split(":")[2];
		
		String message = dataBuffer.append(ss.trim()).append(",").append(mm.trim()).append(",").append(hh.trim()).append(",").append(day.trim()).append(",").append(month.trim()).append(",").append(year.trim()).append(",").toString();
		String[] strArr = message.split(",");
		byte[] b = new byte[strArr.length+4];
		int i = 0;
		b[i++] = 0x5A;
		
		b[i++] = 0x67;//读SIM卡信息   103 
		b[i++] = 0x01;//总包长
		b[i++] = 0x01;//第1包
		
		for(String tmp:strArr){
			b[i++] = Byte.valueOf(tmp);
		}
		
		return  getRequestPacket(b);
	}
	
	
	/**
	 * 发送信息给POS请求读IC卡信息
	 * PAD发（终端收）：68   08 00  68    5A         68         01 01        10 36 0A  01 04  0C     37  16
     *                       帧长8字节    控制域    类型标识104     总包长  第1包         时间 秒 分    时      日    月     年           CRC
	 * @return 680A....
	 */
	public static byte[] requestReadICCard() {
		StringBuffer dataBuffer = new StringBuffer("");
		String year = DateUtil.getYear().substring(2, 4);
		String month =  DateUtil.getMonth();
		String day = DateUtil.getDay();
		String hhmmss = DateUtil.getTime();
		String hh = hhmmss.split(":")[0];
		String mm = hhmmss.split(":")[1];
		String ss = hhmmss.split(":")[2];
		
		String message = dataBuffer.append(ss.trim()).append(",").append(mm.trim()).append(",").append(hh.trim()).append(",").append(day.trim()).append(",").append(month.trim()).append(",").append(year.trim()).append(",").toString();
		String[] strArr = message.split(",");
		byte[] b = new byte[strArr.length+4];
		int i = 0;
		b[i++] = 0x5A;
		
		b[i++] = 0x68;//读取IC卡 104  
		b[i++] = 0x01;//总包长
		b[i++] = 0x01;//第1包
		
		for(String tmp:strArr){
			b[i++] = Byte.valueOf(tmp);
		}
		
		return  getRequestPacket(b);
	}
	
	public static byte[] requestReadPrint(PrintVO vo){
		byte[] s = ProtocolBuildFactory.buildPrintProtocolByte(vo);
		byte[] data = getRequestPacket(s);
		return data;
	}

	public static byte[] requestReadPrint(String print,byte[] sign){
		byte[] s = ProtocolBuildFactory.buildPrintProtocol(print,sign);
		byte[] data = getRequestPacket(s);
		return data;
	}
	
	
	/**
	 * 获取POS机信息
	 * 	PAD发（终端收）：68   08 00  68    5A         6A         01 01        10 36 0A  01 04  0C     37  16
     *                       帧长8字节    控制域    类型标识106     总包长  第1包         时间 秒 分    时      日    月     年           CRC
	 * @return 680A....
	 */
	public static byte[] requestPosInfo(){
		StringBuffer dataBuffer = new StringBuffer("");
		String year = DateUtil.getYear().substring(2, 4);
		String month =  DateUtil.getMonth();
		String day = DateUtil.getDay();
		String hhmmss = DateUtil.getTime();
		String hh = hhmmss.split(":")[0];
		String mm = hhmmss.split(":")[1];
		String ss = hhmmss.split(":")[2];
		
		String message = dataBuffer.append(ss.trim()).append(",").append(mm.trim()).append(",").append(hh.trim()).append(",").append(day.trim()).append(",").append(month.trim()).append(",").append(year.trim()).append(",").toString();
		String[] strArr = message.split(",");
		byte[] b = new byte[strArr.length+4];
		int i = 0;
		b[i++] = 0x5A;
		
		b[i++] = 0x6A;//获取POS机信息 106  
		b[i++] = 0x01;//总包长
		b[i++] = 0x01;//第1包
		
		for(String tmp:strArr){
			b[i++] = Byte.valueOf(tmp);
		}
		
		return  getRequestPacket(b);
	}
}
