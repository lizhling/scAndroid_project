package com.sunrise.marketingassistant.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

/**
 * @Description 调用系统拍照或进入图库中选择照片,再进行裁剪,压缩.
 */
public class PictureUtil {
	/** 用来请求照相功能的常量 */
	public static final int CAMERA_WITH_DATA = 168;
	/** 用来请求图片选择器的常量 */
	public static final int PHOTO_PICKED_WITH_DATA = CAMERA_WITH_DATA + 1;
	/** 用来请求图片裁剪的 */
	public static final int PHOTO_CROP = PHOTO_PICKED_WITH_DATA + 1;
	/**	拍照的照片存储位置 */
	public static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/");

	private static File mCurrentPhotoFile;// 照相机拍照得到的图片

	public File file;		// 截图后得到的图片
	public static Uri imageUri;	// 拍照后的图片路径
	public static Uri imageCaiUri;	// 裁剪后的图片路径
	
	/**
	 * 得到当前图片文件的路径
	 * @return
	 */
	public static File getmCurrentPhotoFile() {
		if (mCurrentPhotoFile == null) {
			if (!PHOTO_DIR.exists()){
				PHOTO_DIR.mkdirs();		// 创建照片的存储目录
			}
			mCurrentPhotoFile = new File(PHOTO_DIR, getCharacterAndNumber()+".png"/*此处可更换文件后缀*/);
			if (!mCurrentPhotoFile.exists())	// 判断存储文件是否存在>不存在则创建
				try {
					mCurrentPhotoFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return mCurrentPhotoFile;
	}

	/**
	 * 开始启动照片选择框
	 * @param context
	 * @param iscrop
	 * 是否裁剪
	 */
	public static void doPickPhotoAction(final Activity context) {
		final Context dialogContext = new ContextThemeWrapper(context,android.R.style.Theme_Light);
		String[] choices;
		choices = new String[2];
		choices[0] = "拍照"; 			// 拍照
		choices[1] = "从相册中选择" ;	// 从相册中选择
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,	android.R.layout.simple_list_item_1, choices);

		final AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
		builder.setTitle("选择上传图片方式");
		builder.setSingleChoiceItems(adapter, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0: {
							String status = Environment
									.getExternalStorageState();
							if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
								doTakePhoto(context);// 用户点击了从照相机获取
							} else {
								Toast.makeText(dialogContext, "没有找到SD卡", Toast.LENGTH_SHORT).show();
							}
							break;
						}
						case 1:
							doPickPhotoFromGallery(context);// 从相册中去获取
							break;
						}
					}
				});
		builder.setNegativeButton(
				"取消",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	/**
	 * 调用拍照功能
	 */
	public static void doTakePhoto(Activity context) {
		try {
			if (!PHOTO_DIR.exists()){
				PHOTO_DIR.mkdirs();	// 创建照片的存储目录
			}
			imageUri = Uri.fromFile(getmCurrentPhotoFile());
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			context.startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(context, "没有找到照相机",Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 调用相册程序
	 * @param context
	 * @param iscrop
	 */
	public static void doPickPhotoFromGallery(Context context) {
		try {
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,	"image/*");
			((Activity) context).startActivityForResult(intent,	PHOTO_PICKED_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(context, "没有找到相册",Toast.LENGTH_SHORT).show();
		}
	}
	
	public static Uri getImageUri(){
		File temporaryFile = new File(PHOTO_DIR, getCharacterAndNumber()+".png");
		imageUri = Uri.fromFile(temporaryFile);
		return imageUri;
	}
	
	public static Uri getImageCaiUri(){
		File temporaryFile = new File(PHOTO_DIR, getCharacterAndNumber()+".png");
		imageCaiUri = Uri.fromFile(temporaryFile);
		return imageCaiUri;
	}
	
	//	得到系统当前时间并转化为字符串
	@SuppressLint("SimpleDateFormat")
	public static String getCharacterAndNumber() {  
        String rel="";  
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");   
        Date curDate = new Date(System.currentTimeMillis());  
        rel = formatter.format(curDate);     
        return rel;  
    }
}