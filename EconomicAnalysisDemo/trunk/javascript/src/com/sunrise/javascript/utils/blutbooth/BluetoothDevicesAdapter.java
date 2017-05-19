package com.sunrise.javascript.utils.blutbooth;

import java.util.ArrayList;

import com.sunrise.javascript.R;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BluetoothDevicesAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<BluetoothDevice> mBluetoothDevices;
	
	protected BluetoothDevicesAdapter(Context context,ArrayList<BluetoothDevice> mBluetoothDevices) {
		super();
		this.mBluetoothDevices = mBluetoothDevices;
		inflater=LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mBluetoothDevices.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mBluetoothDevices.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		BluetoothDevice bluetoothDevice=mBluetoothDevices.get(position);
		String name=bluetoothDevice.getName();
		String address=bluetoothDevice.getAddress();
		int state=bluetoothDevice.getBondState();
		if(convertView==null){
			convertView=inflater.inflate(R.layout.bluetooth_device_item, null);
			viewHolder=new ViewHolder();
			viewHolder.name=(TextView) convertView.findViewById(R.id.device_name);
			viewHolder.address=(TextView) convertView.findViewById(R.id.device_address);
			viewHolder.boundStatus=(TextView) convertView.findViewById(R.id.device_status);
            convertView.setTag(viewHolder);		
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.name.setText(name);
		viewHolder.address.setText(address);
		switch(state){
		case BluetoothDevice.BOND_BONDED:
			viewHolder.boundStatus.setText(R.string.bounded);
			break;
		case BluetoothDevice.BOND_BONDING:
			viewHolder.boundStatus.setText(R.string.bounding);
		    break;
		case BluetoothDevice.BOND_NONE:
			viewHolder.boundStatus.setText(R.string.none_bound);
			break;
		}
		return convertView;
	}
	
	class ViewHolder{
		TextView name;
		TextView address;
		TextView boundStatus;
	}

}
