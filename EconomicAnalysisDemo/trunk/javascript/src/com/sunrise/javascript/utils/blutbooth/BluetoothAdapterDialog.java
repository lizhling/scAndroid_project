package com.sunrise.javascript.utils.blutbooth;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.sunrise.javascript.R;

public class BluetoothAdapterDialog extends Dialog {
	//private final static String TAG="BluetoothAdapterActivity";
	private Context mContext;
    private ListView mCongXingListView;
    private ListView mRenYinKejiListView;
    
    private BluetoothUtils mBluetoothUtils=BluetoothUtils.getInstance();
    private BluetoothDevicesAdapter mCongXingBluetoothDevicesAdapter;
    private BluetoothDevicesAdapter mRenYingBluetoothDevicesAdapter;   
    private BluetoothAdapterManager mAdapterManager;
    //private static final int ENABLE_BLUETOOTH_REQUEST_CODE=0;
    private Handler mHandler=new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		startSearchBluetooth();
    	};
    };
    
    private BroadcastReceiver mBluetoothReceiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			String action=intent.getAction();
			if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
			  int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
		       switch(state) {
			   case BluetoothAdapter.STATE_ON:
				   mHandler.sendEmptyMessage(0);
			        break;
			   case BluetoothAdapter.STATE_OFF:
			        break;
			    }	
			}
			else if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
				 mHandler.sendEmptyMessage(0);
			}
		}
	}; 
    
    private OnItemClickListener mRenYingKeJiClickListener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View biew, int position,
				long id) {
			BluetoothDevice bluetoothDevice=mAdapterManager.getRenYingBluetoothDevice(position);
			requestBluetooth(bluetoothDevice);
		}
	};
	
	 private OnItemClickListener mChongXingClickListener=new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View biew, int position,
					long id) {
			    BluetoothDevice bluetoothDevice=mAdapterManager.getCongXingBluetoothDevice(position);
			    requestBluetooth(bluetoothDevice);
			}
	};
		
	public BluetoothAdapterDialog(Context context) {
		super(context);
		this.mContext=context;
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.bluebooth_device_list);
        
        mCongXingListView=(ListView) findViewById(R.id.cong_xing_devices);
        mRenYinKejiListView=(ListView) findViewById(R.id.ren_ying_ke_ji_devices);
        mRenYinKejiListView.setOnItemClickListener(mRenYingKeJiClickListener);
    	mCongXingListView.setOnItemClickListener(mChongXingClickListener); 
    	
    	mAdapterManager=new BluetoothAdapterManager(mContext);
    	mCongXingBluetoothDevicesAdapter=mAdapterManager.getCongXingBluetoothDevicesAdapter();
    	mRenYingBluetoothDevicesAdapter=mAdapterManager.getRenYingBluetoothDevicesAdapterAdapter();
    	mCongXingListView.setAdapter(mCongXingBluetoothDevicesAdapter);
    	mRenYinKejiListView.setAdapter(mRenYingBluetoothDevicesAdapter);
    	registBluetoothReceiver();
	}
    
    private void requestBluetooth(BluetoothDevice bluetoothDevice) {
		int state=bluetoothDevice.getBondState();
		switch (state) {
		case BluetoothDevice.BOND_BONDED:	
			dismiss();
			break;
		case BluetoothDevice.BOND_BONDING:
			Toast.makeText(mContext, mContext.getString(R.string.bonding_please_wait),Toast.LENGTH_LONG).show();				
			break;
		case BluetoothDevice.BOND_NONE:
			mBluetoothUtils.doPair(bluetoothDevice);
			break;
		default:
			break;
		}
	}
    
    private void registBluetoothReceiver(){
    	IntentFilter intentFilter=new IntentFilter();
    	intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
    	intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
    	mContext.registerReceiver(mBluetoothReceiver, intentFilter);
    }
    
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	startSearchBluetooth();
    }
    
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	mContext.unregisterReceiver(mBluetoothReceiver);
    }
	private void searchBluetoothfinish(int deviceNumber) {
		if(deviceNumber==0)
			setTitle(R.string.no_bunded_device);
		else
			setTitle(R.string.select_bluetooth_device);
	}
	
	public void startSearchBluetooth() {
		if(!mBluetoothUtils.deviceHaveBluetooth()){
			Toast.makeText(mContext, R.string.bluebooth_be_not_enabel, Toast.LENGTH_SHORT).show();
		    return;	
		}
		if(!mBluetoothUtils.isEnabled()){
			enableBluetooth();
		}else{
			startFindBluetoothDevice();
		}
	}


	private void startFindBluetoothDevice(){
		int deviceNumber=mAdapterManager.initBluetootDevices();
		searchBluetoothfinish(deviceNumber);
	}
	
	public void enableBluetooth() {
		Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		mContext.startActivity(intent);
	}
	
    
}