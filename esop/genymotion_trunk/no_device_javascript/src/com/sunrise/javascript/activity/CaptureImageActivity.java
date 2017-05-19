package com.sunrise.javascript.activity;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.Toast;

import com.sunrise.javascript.JavaScriptConfig;
import com.sunrise.javascript.R;
import com.sunrise.javascript.utils.ActivityUtils;

/**
 * 拍照识别demo
 * @author guoh
 *
 */
public class CaptureImageActivity extends Activity  {
	//private final static String TAG="CaptureImageActivity";
	private String strImgPath;
	private static final int RESULT_CAPTURE_IMAGE_REQUECT_CODE=0;
	//private String mBitmapBase64;
	private static final int CAPTURE_IMAGE_SUCCESS=0;
	private static final int CAPTURE_IMAGE_FAILED=1;
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case CAPTURE_IMAGE_SUCCESS:
				sendResultBroadcast();
				break;
			case CAPTURE_IMAGE_FAILED:
				Toast.makeText(CaptureImageActivity.this, getString(R.string.capture_image_failed), Toast.LENGTH_LONG).show();
				break;
			}
			finish();
		};
	};
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		cameraMethod();
	}
	
	 /**
     * 照相功能
     */
    private void cameraMethod() {
            Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";//照片命名
            String fileDir;
            if(JavaScriptConfig.sCaptureImageDir==null){
            	fileDir =  Environment.getExternalStorageDirectory()
     					.getAbsolutePath()+File.separator + "captureImage"+File.separator;//存放照片的文件夹
            }else{
            	fileDir=JavaScriptConfig.sCaptureImageDir+File.separator;
            }
           
            File out = new File(fileDir);
            if (!out.exists()) {
                    out.mkdirs();
            }
            out = new File(fileDir, fileName);
            strImgPath = fileDir + fileName;//该照片的绝对路径
            Uri uri = Uri.fromFile(out);
            imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(imageCaptureIntent, RESULT_CAPTURE_IMAGE_REQUECT_CODE);
    }
    
    private void sendResultBroadcast(){
      	Intent intent = new Intent(ActivityUtils.IDCARD_INFO_ACTION);
      	intent.putExtra(JavaScriptConfig.EXTRAL_BITMAP, "file://"+strImgPath);
  		sendBroadcast(intent);
  		finish();
      }
    
    public  Bitmap readBitmapFromFile(String filePath) throws IOException {
		InputStream inStream = new FileInputStream(filePath);
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outstream.write(buffer, 0, len);
		}
		outstream.close();
		inStream.close();
		byte[] datas=outstream.toByteArray();
		Bitmap bitmap=BitmapFactory.decodeByteArray(datas, 0, datas.length);	
		return bitmap;
	}
    
  @Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub
	super.onActivityResult(requestCode, resultCode, data);
	if(requestCode==RESULT_CAPTURE_IMAGE_REQUECT_CODE){
		if(resultCode==RESULT_OK){
			new Thread(){
				public void run() {
					if(handlePicture()){
						mHandler.sendEmptyMessage(CAPTURE_IMAGE_SUCCESS);
					}else{
						mHandler.sendEmptyMessage(CAPTURE_IMAGE_FAILED);
					}
				}
			}.start();
		}
	}
}

private boolean handlePicture() {

	try {
		  Bitmap bitmap=readBitmapFromFile(strImgPath);
		  if(bitmap==null)
			  return false;
		  BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(new File(strImgPath)));
		  bitmap.compress(CompressFormat.JPEG, 50, os);
		 /* ByteArrayOutputStream os = new ByteArrayOutputStream();   
		  bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);
		  os.flush();
          os.close();
          byte[] temp=os.toByteArray();
          mBitmapBase64=new String(temp);
          Log.d(TAG, "mBitmapBase64:"+mBitmapBase64);*/
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}
	return true;
}

}
