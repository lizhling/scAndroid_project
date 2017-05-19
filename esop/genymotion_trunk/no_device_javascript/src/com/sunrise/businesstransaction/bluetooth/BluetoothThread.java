package com.sunrise.businesstransaction.bluetooth;

import java.io.IOException;
import java.io.InputStream;

import android.bluetooth.BluetoothSocket;

import com.sunrise.businesstransaction.utils.ConstantsUtils;
import com.sunrise.businesstransaction.utils.SocketReader;

public class BluetoothThread extends Thread {
	private BluetoothSocket mBluetoothSocket;
	protected BluetoothThread(BluetoothSocket bluetoothSocket) {
		this.mBluetoothSocket = bluetoothSocket;
	}



	public void run() {}
}
