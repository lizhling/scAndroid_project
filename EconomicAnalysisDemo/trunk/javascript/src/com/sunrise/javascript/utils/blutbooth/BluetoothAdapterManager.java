package com.sunrise.javascript.utils.blutbooth;
import java.util.ArrayList;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

public class BluetoothAdapterManager {
private ArrayList<BluetoothDevice> mCongXingBluetoothDevices=new ArrayList<BluetoothDevice>();
private ArrayList<BluetoothDevice> mRenYingBluetoothDevices=new ArrayList<BluetoothDevice>();
private BluetoothDevicesAdapter mCongXingBluetoothDevicesAdapter;
private BluetoothDevicesAdapter mRenYingBluetoothDevicesAdapter;
private BluetoothAdapter mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

public BluetoothAdapterManager(Context context){
	mCongXingBluetoothDevicesAdapter=new BluetoothDevicesAdapter(context, mCongXingBluetoothDevices);
	mRenYingBluetoothDevicesAdapter=new BluetoothDevicesAdapter(context, mRenYingBluetoothDevices);
}

public int initBluetootDevices() {
	Set<BluetoothDevice> bonedeDevices=mBluetoothAdapter.getBondedDevices();
	mCongXingBluetoothDevices.clear();
	mRenYingBluetoothDevices.clear();
    for(BluetoothDevice bluetoothDevice:bonedeDevices){
    	String name=bluetoothDevice.getName();
    	if(name.contains(BluetoothUtils.CONG_XING_DEVICE_FLAG)){
    		if(!mCongXingBluetoothDevices.contains(bluetoothDevice))
        		mCongXingBluetoothDevices.add(bluetoothDevice);
    	}else if(name.contains(BluetoothUtils.RENG_YING_KE_JI_DEVICE_FLAG)){
    		if(!mRenYingBluetoothDevices.contains(bluetoothDevice)){
    			mRenYingBluetoothDevices.add(bluetoothDevice);
    		}
    	}
    }
    
    freshBluetoothDevices();
    return mCongXingBluetoothDevices.size()+mRenYingBluetoothDevices.size();
}


protected void addBlueDeviceToBluetoothDevicesAdapter(BluetoothDevice bluetoothDevice){
	String name=bluetoothDevice.getName();
	if(name!=null){
		if(name!=null&&name.contains(BluetoothUtils.CONG_XING_DEVICE_FLAG)){
			if(!mCongXingBluetoothDevices.contains(bluetoothDevice))
	    		mCongXingBluetoothDevices.add(bluetoothDevice);
		}else if(name.contains(BluetoothUtils.RENG_YING_KE_JI_DEVICE_FLAG)){
			if(!mRenYingBluetoothDevices.contains(bluetoothDevice)){
				mRenYingBluetoothDevices.add(bluetoothDevice);
			}
		}
	}
	
	 freshBluetoothDevices();
}


protected BluetoothDevicesAdapter getCongXingBluetoothDevicesAdapter() {
	return mCongXingBluetoothDevicesAdapter;
}

protected BluetoothDevicesAdapter getRenYingBluetoothDevicesAdapterAdapter() {
	return mRenYingBluetoothDevicesAdapter;
}

public BluetoothDevice getCongXingBluetoothDevice(int position){
	if(mCongXingBluetoothDevices.size()>0)
		return mCongXingBluetoothDevices.get(position);
	return null;
}

public BluetoothDevice getRenYingBluetoothDevice(int position){
	if(mRenYingBluetoothDevices.size()>0)
		return mRenYingBluetoothDevices.get(position);
	return null;
}

public void freshBluetoothDevices(){
	if(mCongXingBluetoothDevicesAdapter!=null)
		mCongXingBluetoothDevicesAdapter.notifyDataSetChanged();
	if(mRenYingBluetoothDevicesAdapter!=null)
		mRenYingBluetoothDevicesAdapter.notifyDataSetChanged();
}
}
