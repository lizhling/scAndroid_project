package com.sunrise.javascript.utils.blutbooth;
import java.lang.reflect.Method;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

public class BluetoothUtils {
//private static final String TAG="BluetoothUtils";
private BluetoothAdapter mBluetoothAdapter;
private static BluetoothUtils sBluetoothUtils;
public static final String CONG_XING_DEVICE_FLAG="SREGA";
public static final String RENG_YING_KE_JI_DEVICE_FLAG="CB1";
private BluetoothUtils(){
	mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
}

public static BluetoothUtils getInstance(){
	if(sBluetoothUtils==null)
		sBluetoothUtils=new BluetoothUtils();
	return sBluetoothUtils;
}

public void enableBluetooth(Context context) {
	Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	((Activity) context).startActivity(intent);
}

public boolean isEnabled(){
	if(mBluetoothAdapter!=null)
		return mBluetoothAdapter.isEnabled();
	return false;
}

public void doCanclePair(final BluetoothDevice bluetoothDevice) {
	new Thread(){public void run() {
			try {
				unPair(bluetoothDevice);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
	};}.start();
}

public void doPair(final BluetoothDevice bluetoothDevice) {
	new Thread(){public void run() {
			try {
				pair(bluetoothDevice);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
	};}.start();
}

public void startFindBlueDevice() {
	mBluetoothAdapter.startDiscovery();
}

public boolean deviceHaveBluetooth(){
	  if(mBluetoothAdapter==null)
		  return false;
	  return true;
	}


private boolean pair(BluetoothDevice remoteDevice) throws Exception {

	if (remoteDevice.getBondState() == BluetoothDevice.BOND_NONE) {
		Method createBond = BluetoothDevice.class.getMethod("createBond");
		return (Boolean) createBond.invoke(remoteDevice);
	}

	return false;
}

private boolean unPair(BluetoothDevice remoteDevice) throws Exception {
	if (remoteDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
		Method createBond = BluetoothDevice.class.getMethod("removeBond");
		return (Boolean) createBond.invoke(remoteDevice);
	}
	return false;
}

}
