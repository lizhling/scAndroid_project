package com.sunrise.businesstransaction.utils;

import java.io.IOException;
import java.io.InputStream;

import android.util.Log;


/**
 * 解释从ＰＯＳ机读出来的数据包
 * @author toshiba
 *
 */
public class SocketReader {		
	private final String TAG="SocketReader";
	private InputStream mInputStream;
	public SocketReader(InputStream mInputStream) {
		this.mInputStream = mInputStream;
	}
	
	/**
	 * 读包解释
	 * @param inputstream
	 * @return
	 * @throws IOException
	 */
	public  byte[] readPacket()throws IOException{
		InputStream is = mInputStream;
		try {
			if (is.available() <= 0) {
				return null;
			}
			long endtime = 10L * 1000L + System.currentTimeMillis();
			
	    	int b1 = readByte(is, endtime);
	    	if (b1 == 0x10) {
	    		System.err.println("10");
	    		int b2 = readByte(is, endtime);
		    	int b3 = readByte(is, endtime);
		    	int b4 = readByte(is, endtime);
		    	int b5 = readByte(is, endtime);
		    	int b6 = readByte(is, endtime);
	    		return new byte[] {(byte) b1,(byte) b2,(byte) b3,(byte) b4,(byte) b5,(byte) b6};
	    	} else if (b1 != 0x68) {
	    		throw new Exception("第1个字节不是0x68. :" + b1);
	    	}
	    	int b2 = readByte(is, endtime);
	    	int b3 = readByte(is, endtime);
	    	int b4 = readByte(is, endtime);
	    	int size = b2;
	    	if (b3 > 0) {
	    		size = b3 * 256 + b2;
	    	}
	    	if (b4 != 0x68) {
	    		throw new Exception("第4个字节不是0x68. :" + b4);
	    	}
	    	
	    	byte[] bytes = new byte[4 + size + 2];
	    	for (int i = 4; i < size + 4; i++) {
	    		bytes[i] = (byte) readByte(is, endtime);
	    	}
	    	
	    	bytes[0] = (byte) b1;
	    	bytes[1] = (byte) b2;
	    	bytes[2] = (byte) b3;
	    	bytes[3] = (byte) b4;
	    	
	    	int bf1 = readByte(is, endtime);
	    	int bf2 = readByte(is, endtime);
	    	
	    	if (bf2 != 0x16) {
	    		throw new Exception("倒数第1个字节不是0x16. :" + bf2);
	    	}
	    	
	    	bytes[bytes.length - 2] = (byte) bf1;
	    	bytes[bytes.length - 1] = (byte) bf2;	    	
	    	Log.d(TAG, "bytes:"+StringConvertUtils.byte2HexStr(bytes));
	    	return bytes;
		} catch (IOException ex) {
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			if (is.available() > 0) {
				is.skip(is.available());
			}
			
			throw new IOException(ex.getMessage());
		} 
    }
	
	private int readByte(InputStream is, long endtime) throws Exception {
		while (System.currentTimeMillis() <= endtime) {
			if (is.available() > 0) {
				int b = is.read();
				if (b >= 0) return b;
			} else {
			}
		}
		throw new Exception("read packet time out.");
	}
	
	/**
	 * 关闭读取流，释放资源
	 */
	public void close() {
		if (mInputStream != null) {
			try {
				mInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
