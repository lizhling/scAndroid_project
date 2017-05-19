package com.sunrise.marketingassistant.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.cache.preference.Preferences;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import com.sunrise.marketingassistant.task.UploadImageTask;
import com.sunrise.marketingassistant.utils.CommUtil;
import com.sunrise.marketingassistant.utils.PictureUtil;
import com.sunrise.marketingassistant.view.DefaultProgressDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class UploadActivity extends Activity implements TaskListener,OnClickListener {
	private ImageView image_view;
	private Bitmap myBitmap;
	private GenericTask mTask;
	
	private Button submit_btn;
	private Button re_select_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_upload);
		image_view = (ImageView) findViewById(R.id.image_view);
		submit_btn = (Button)findViewById(R.id.submit_btn);
		submit_btn.setOnClickListener(this);
		re_select_btn = (Button)findViewById(R.id.re_select_btn);
		re_select_btn.setOnClickListener(this);
		PictureUtil.doPickPhotoAction(UploadActivity.this);
	}

	private Bitmap bitImage =null;
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Intent intent = new Intent("com.android.camera.action.CROP");
		Uri data2 = null;
		if (data == null) {
			data2 = PictureUtil.getImageUri();
		} else {
			data2 = data.getData();
		}
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PictureUtil.PHOTO_PICKED_WITH_DATA:
				intent.setDataAndType(data2, "image/*");
				intent.putExtra("crop", true);
				// 设置裁剪尺寸
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", 160);
				intent.putExtra("outputY", 130);
				intent.putExtra("return-data", true);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, PictureUtil.getImageCaiUri());
				startActivityForResult(intent, PictureUtil.PHOTO_CROP);
				break;
			case PictureUtil.CAMERA_WITH_DATA:
				intent.setDataAndType(data2, "image/*");
				intent.putExtra("crop", true);
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", 160);
				intent.putExtra("outputY", 130);
				intent.putExtra("return-data", true);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, PictureUtil.getImageCaiUri());
				startActivityForResult(intent, PictureUtil.PHOTO_CROP);
				break;
			case PictureUtil.PHOTO_CROP:
				Bundle bundle = data.getExtras();
				myBitmap = (Bitmap) bundle.get("data");
				bitImage = comp(myBitmap);

				String fileName = getCharacterAndNumber();
				File file = new File(PictureUtil.PHOTO_DIR, "channel"+ fileName + ".jpg");
				saveMyBitmap(bitImage, file);
				image_view.setImageBitmap(bitImage);
				break;

			default:
				break;
			}
		}
	}

	// 将Bitmap转换成字符串
	public String bitmaptoString(Bitmap bitmap) {
		String string = null;

		ByteArrayOutputStream bStream = new ByteArrayOutputStream();

		bitmap.compress(CompressFormat.JPEG, 100, bStream);

		byte[] bytes = bStream.toByteArray();

		string = Base64.encodeToString(bytes, Base64.DEFAULT);
		return string;
	}

	// 压缩图片(第二次)
	private Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 200) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	// 压缩图片(第一次)
	private Bitmap comp(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 200) {// 判断如果图片大于200KB,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 160f;// 这里设置高度为800f
		float ww = 130f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	public void saveMyBitmap(Bitmap mBitmap, File file) {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
			byte[] bitmapData = baos.toByteArray();

			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bitmapData);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 得到系统当前时间并转化为字符串
	@SuppressLint("SimpleDateFormat")
	public static String getCharacterAndNumber() {
		String rel = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());
		rel = formatter.format(curDate);
		return rel;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {
		// TODO Auto-generated method stub
		initDialog(true, false, null);
		if (task instanceof UploadImageTask) {
			showDialog("正在上传图片，请稍候……");
		}
	}

	private DefaultProgressDialog mDialog;

	private void initDialog(boolean cancelAble, boolean cancelOnTouchOutside, OnCancelListener cancellistener) {
		if (mDialog == null) {
			mDialog = new DefaultProgressDialog(this);
		}
		mDialog.setCancelable(cancelAble);
		mDialog.setCanceledOnTouchOutside(cancelOnTouchOutside);
		mDialog.setOnCancelListener(cancellistener);
	}

	protected void showDialog(CharSequence text) {
		if (mDialog != null) {
			mDialog.setMessage(text);
			if (!mDialog.isShowing())
				mDialog.show();
		}
	}

	protected void dismissDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		// TODO Auto-generated method stub
		dismissDialog();
		if (result != TaskResult.OK) {
			CommUtil.showAlert(this, null, task.getException().getMessage(), getString(R.string.Return), null);
		}
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		// TODO Auto-generated method stub
		if (task instanceof UploadImageTask) {
			if (param != null) {
				JSONObject json;
				try {
					json = new JSONObject((String)param);
					if(json.has("resultCode")){
						int resultCode = json.getInt("resultCode");
						if(resultCode==0){
							if(json.has("fileName")){
								Preferences.getInstance(this).saveImageName(json.getString("fileName"));
								Toast.makeText(this, "上传图片成功!", Toast.LENGTH_SHORT).show();
								finish();
							}
						}else{
							Toast.makeText(this, json.getString("resultMessage"), Toast.LENGTH_SHORT).show();
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onCancelled(GenericTask task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.submit_btn:
			// 上传图片
			if (mTask != null) {
				mTask.cancle();
			}
			mTask = new UploadImageTask().execute(bitmaptoString(bitImage), this);
			break;
		case R.id.re_select_btn:
			PictureUtil.doPickPhotoAction(UploadActivity.this);
			break;
		default:
			break;
		}
	}

}
