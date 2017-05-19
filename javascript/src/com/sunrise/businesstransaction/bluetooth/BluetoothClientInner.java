package com.sunrise.businesstransaction.bluetooth;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.Timer;
import java.util.TimerTask;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;

import com.sunrise.businesstransaction.utils.SocketReader;
import com.sunrise.businesstransaction.utils.SocketWriter;
import com.sunrise.javascript.utils.CommonUtils;
import com.sunrise.javascript.utils.JsonUtils;

public class BluetoothClientInner extends Thread{
	//private final String TAG="BluetoothClientInner";
	private  BluetoothSocket mSocket;
	private  SocketReader socketReader;
	private  SocketWriter socketWriter;
	public BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private BluetoothDevice mBluetoothDevice;
	private BluetoothClient mBluetoothClient;
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); 
    protected BluetoothClientInner(String address,BluetoothClient bluetoothClient) {
		super();
		this.mBluetoothClient=bluetoothClient;
		this.mBluetoothDevice=mBluetoothAdapter.getRemoteDevice(address);
	}

	public void run() {
        try {		   

        	//Method m = mBluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}); 
			//(BluetoothSocket) m.invoke(mBluetoothDevice, Integer.valueOf(1)); 
        	int sdk = Integer.parseInt(Build.VERSION.SDK);
        	if (sdk >= 14) {
        		mSocket = mBluetoothDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
        	} else {
        		Method m = mBluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}); 
            	mSocket = (BluetoothSocket) m.invoke(mBluetoothDevice, Integer.valueOf(1));    
        	}      	
        /*timeOut=false;
        	Timer timer = new Timer();
    		timer.schedule(new TimerTask() {
    			@Override
    			public void run() {
    				if(!timeOut){
    					timeOut=true;
    					try {
							mSocket.close();
						} catch (IOException e) {
							e.printStackTrace();
							mBluetoothAdapter.disable();
				        	mBluetoothClient.connectionFailed();
						}
    				}
    			}
    		}, CONNECT_BLUETOOTH_TIME_DURATION);
    	*/       	
        	mSocket.connect();
    		socketReader=new SocketReader(mSocket.getInputStream());
    		socketWriter=new SocketWriter(mSocket.getOutputStream());
    		mBluetoothClient.connected();
    		while (isConnected()) {
    			byte[] bytes=socketReader.readPacket();
    			if(bytes!=null){
        			mBluetoothClient.onRecieve(bytes);
    			}
    			Thread.sleep(100);
    		}  
        } catch (Exception e) {
        	e.printStackTrace();
        	mBluetoothAdapter.disable();
        	mBluetoothClient.connectionFailed();
        	 try {
        		 close();
             } catch (IOException e2) {
                e.printStackTrace();
             }
        } 
    }

    /**
	 * 获取当前socket是否已经连接到服务器
	 * @return
	 */
	public boolean isConnected() {
		if (mSocket == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * 停止客户端socket连接
	 */
	public void close() throws IOException {
		if (mSocket != null) {
			if(socketReader!=null){
				socketReader.close();
				socketReader=null;
			}
			if(socketWriter!=null){
				socketWriter.close();
				socketWriter=null;
			}
			mSocket.close();
			mSocket=null;
		}
	}
	
	
	/**
	 * 
	 * @throws IOException
	 */
	public void sendMessage(byte[] buffer) throws IOException {
		socketWriter.sendMessage(buffer);
	}
	
    public void cancel() {
        try {
        	mSocket.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
}
