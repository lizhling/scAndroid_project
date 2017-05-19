package com.sunrise.businesstransaction.utils;

public class ConstantsUtils {
    
	
	//表示终端收到此帧
	public static String RESPONSE_SUCCESS_CODE = "E5";
	//请求对话框
	public static final int REQUEST_DIALOG_CODE = 11;
	//请求返回数据对话框
	public static final int REQUEST_RETURN_DATA_CODE = 12;
	
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;
    public static final int MESSAGE_CONNECTED = 3;
    public static final int MESSAGE_CONNECTED_FAILED = 4;
    public static final int MESSAGE_CONNECTED_CLOSED = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String TOAST = "toast";

    //返回的设备地址
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static String  photoPath = "";
    
    
}
