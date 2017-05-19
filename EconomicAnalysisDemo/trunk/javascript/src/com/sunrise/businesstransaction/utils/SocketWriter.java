package com.sunrise.businesstransaction.utils;

import java.io.IOException;
import java.io.OutputStream;

public class SocketWriter {
	private OutputStream mOutputStream;	
	
	  public SocketWriter(OutputStream mOutputStream) {
		this.mOutputStream = mOutputStream;
	}

	/**
     * 写输出流
     * @param buffer  The bytes to write
     */
    private void write(byte[] buffer) {
        try {
        	mOutputStream.write(buffer);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
    
    /**
     * 发送指令到POS机.
     * @param b 字集二进制数组
     * @param needResponse 是否需要响应 默认为true
     */
    public synchronized void sendMessage(byte[] b) {
		write(b);
    }
    
    /**
	 * 关闭读取流，释放资源
	 */
	public void close() {
		if (mOutputStream != null) {
			try {
				mOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
