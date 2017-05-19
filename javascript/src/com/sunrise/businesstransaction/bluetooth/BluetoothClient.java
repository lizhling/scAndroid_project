package com.sunrise.businesstransaction.bluetooth;

import java.io.IOException;
import android.os.Handler;

import com.sunrise.businesstransaction.utils.ConstantsUtils;

public class BluetoothClient {
	private Handler mHandler;
	private BluetoothClientInner bluetoothClientInner;
 	
	public BluetoothClient(Handler mHandler, String address) {
	super();
	this.mHandler = mHandler;
	bluetoothClientInner = new BluetoothClientInner(address,this);
}
	 public void connect(String address) {
		bluetoothClientInner.start();
	}	
	 
	public void onRecieve(byte[] bytes){
		mHandler.obtainMessage(ConstantsUtils.MESSAGE_READ, bytes.length, -1, bytes).sendToTarget();
	}
	
	public void connectionFailed(){
		 mHandler.obtainMessage(ConstantsUtils.MESSAGE_CONNECTED_FAILED).sendToTarget();
	}
	
	public void connected(){
		mHandler.obtainMessage(ConstantsUtils.MESSAGE_CONNECTED).sendToTarget();
	}
	
	public void disconnect() {
		try {
			bluetoothClientInner.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        mHandler.obtainMessage(ConstantsUtils.MESSAGE_CONNECTED_CLOSED).sendToTarget();
	}
	
	// 判断客户端是否已经链接,除了已经连接socket之外还必须确保已经注册了信息
	public boolean isConnected() {
		return (bluetoothClientInner != null && bluetoothClientInner.isConnected());
	}
	
	/**
	 * 向服务器端发送命令
	 * @param mess
	 * @throws IOException
	 */
	public void sendMessage(byte[] buffer)  {
		try {
			bluetoothClientInner.sendMessage(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
