package com.sunrise.javascript.activity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.sunrise.businesstransaction.service.vo.ShenFenZheng2Data;
import com.sunrise.javascript.JavaScriptConfig;
import com.sunrise.javascript.R;
import com.sunrise.javascript.utils.ActivityUtils;
import com.sunrise.javascript.utils.FileUtils;
import com.sunrise.javascript.utils.JsonUtils;
import com.yunmai.android.engine.OcrEngine;
import com.yunmai.android.other.CameraManager;
import com.yunmai.android.vo.IDCard;

public class IDCardImageActivity extends Activity implements SurfaceHolder.Callback {
	public final static String TXT_FILENAME="idcardImageStr.txt";
	public final static String IMAGE_FILENAME = "idcardImage.jpg";
	public static byte sImageByte[];
	public final static String IDCARDIAMGEPATHDIR = "cmuc"+File.separator+"html"+File.separator+"idCardImg";
	private static final String TAG = "IDCardImageActivity";
	private static final int CANCLE_REG=1001;
	private final static String IDCARDIAMGERE_REL_APATH = IDCARDIAMGEPATHDIR+ File.separator + IMAGE_FILENAME;
	private final static String IDCARDIAMGE_ABS_PATH = getSDPath() + File.separator+IDCARDIAMGERE_REL_APATH;
	private ProgressDialog mProgressDialog;
	private int mMaxZoom;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mProgressDialog != null)
				mProgressDialog.dismiss();
			if(msg.what==CANCLE_REG){
				sendResultBroadcast();
			}else{
				converIDStrToJson(msg.what);
				sendResultBroadcast();
			}
		}
	};
	
	private Handler mTakePicHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				idcardA = msg.getData().getByteArray("picData");
				if (idcardA == null) {
					Toast.makeText(IDCardImageActivity.this, "请拍摄证件正面", Toast.LENGTH_LONG).show();
					mShutter.setEnabled(true);
					mCameraManager.initDisplay();
					return;
				}
				startRecognize();
			} else {
				Toast.makeText(IDCardImageActivity.this,R.string.camera_take_picture_error, Toast.LENGTH_SHORT).show();
			}
		}

	};
	
	private ShenFenZheng2Data mShenFenZheng2Data = new ShenFenZheng2Data();
	private SurfaceView mSurfaceView;
    private VerticalSeekBar mVerticalSeekBar;
	private SurfaceHolder mSurfaceHolder;
	private ImageButton mShutter;
	private CameraManager mCameraManager;
	private byte[] idcardA = null;
	private IDCard idCard;
	private boolean isTouchFocus=false;
	
	private OnClickListener mLsnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int vId=v.getId();
			if(vId==R.id.camera_shutter_a){
				mShutter.setEnabled(false);
				mCameraManager.setTakeIdcardA();
				if(isTouchFocus){
					mCameraManager.takePicture();
				}else{
					mCameraManager.requestFocuse();
				}
				isTouchFocus=false;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getString(R.string.analysising_imgage_message));
		FileUtils.createSDDir(IDCARDIAMGEPATHDIR);
		Log.d(TAG, IDCARDIAMGE_ABS_PATH);
		setContentView(R.layout.bcr_camera);
		mCameraManager = new CameraManager(IDCardImageActivity.this, mTakePicHandler);
		initViews();
	}

	private void startRecognize() {
		new Thread(){
			public void run() {
				FileUtils.deleteFileAbsolutePath(IDCARDIAMGE_ABS_PATH);
				FileUtils.saveToFileByAbsPath(idcardA, IDCARDIAMGE_ABS_PATH);
				zoomBitmap(IDCARDIAMGE_ABS_PATH, 640, 480, 60);
				addWaterMark(IDCARDIAMGE_ABS_PATH, parseLongToDate(System.currentTimeMillis()));
				handlePicture(IDCARDIAMGE_ABS_PATH);
				OcrEngine ocrEngine = new OcrEngine();
				try {
					idCard = ocrEngine.recognize(IDCardImageActivity.this,idcardA, null);
					int resultStatusCode=idCard.getRecogStatus();
					mHandler.sendEmptyMessage(resultStatusCode);
				} catch (Error e) {
					mHandler.sendEmptyMessage(OcrEngine.RECOG_FAIL);
				}
			};
		}.start();
		mProgressDialog.show();
	}
	
	private void handlePicture(String path){
		try {
			Bitmap bitmap =BitmapFactory.decodeFile(path);
			byte[] bytes=compressImage(bitmap);
			sImageByte=bytes;
			recycleBitmap(bitmap);
			FileUtils.deleteFileAbsolutePath(path);
			FileUtils.saveToFileByAbsPath(bytes, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 private byte[] compressImage(Bitmap image) {  		  
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
	        int options = 100;  
	        while (baos.toByteArray().length / 1024>30) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
	            baos.reset();//重置baos即清空baos  
	            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
	            options -= 10;//每次都减少10  
	        }  
	        return baos.toByteArray();  
	    }  

	private String parseLongToDate(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return String.format("仅用于%02d年%02d月%02d日四川移动业务办理",
				calendar.get(Calendar.YEAR)%100, calendar.get(Calendar.MONTH) + 1,
				calendar.get(Calendar.DAY_OF_MONTH));
	}

	private  void addWaterMark(String imagePath, String content) {
		Bitmap org=BitmapFactory.decodeFile(imagePath);
		if (org == null)
			return ;

		if (TextUtils.isEmpty(content))
			return ;

		Bitmap watermark = getChaptarBitmap(content);// 创建水印文字图片

		Bitmap newb = Bitmap.createBitmap(org.getWidth(), org.getHeight(),
				Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		cv.drawBitmap(org, 0, 0, null);// 在 0，0坐标开始画入src

		watermark = getZoomBitmap(watermark, org.getWidth(), org.getHeight());
		int seperate = org.getHeight()/5;// 获取原始水印高度
		int centerX = org.getWidth() - watermark.getWidth() >> 1;
		int centerY = org.getHeight() - watermark.getHeight() >> 1;
		for (int i = 0; i < 5; ++i) {
			cv.drawBitmap(watermark, centerX + (i-2)*seperate, centerY+(i-2)*seperate, null);// 居中画入水印
		}
		
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		cv.restore();// 存储
		savePicture(newb, imagePath);
		recycleBitmap(org);		
		recycleBitmap(watermark);
		recycleBitmap(newb);
	}

	private  Bitmap getChaptarBitmap(String content) {

		if (TextUtils.isEmpty(content))
			return null;

		Paint p = new Paint();
		p.setColor(0x88444444);
		p.setTextScaleX(0.8f);
		p.setTextSize(32);
		p.setTypeface(Typeface.DEFAULT_BOLD);
		Rect bounds = new Rect();
		p.getTextBounds(content, 0, content.length(), bounds);

		// 生成文字图片
		Bitmap result = Bitmap.createBitmap(bounds.width() + 2,
				bounds.height() + 2, Config.ARGB_8888);
		Canvas canvasTemp = new Canvas(result);
		canvasTemp.drawText(content, 0, result.getHeight(), p);
		return result;
	}

	/**
	 * @param bitmap
	 *            水印图片
	 * @param w
	 *            背景图片宽高
	 * @param h
	 * @return 旋转后的图片
	 */
	private  Bitmap getZoomBitmap(Bitmap bitmap, float w, float h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int angle = (int) (Math.atan(h / w) / Math.PI * 180);
		float scale = Math.min(w / width, h / height);

		Matrix matrix = new Matrix();
		// 缩放图片动作
		matrix.postScale(scale, scale);

		// 旋转图片 动作
		matrix.postRotate(-angle);

		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);

		return resizedBitmap;
	}
	
	private void initViews() {
		// find view
		mShutter = (ImageButton) findViewById(R.id.camera_shutter_a);
		mShutter.setOnClickListener(mLsnClick);
		mVerticalSeekBar=(VerticalSeekBar) findViewById(R.id.zoom_verticalSeekBar);
		mVerticalSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int zo=(progress*mMaxZoom)/mVerticalSeekBar.getMax();
				mCameraManager.setZoom(zo);
			}
		});
		mSurfaceView = (SurfaceView) findViewById(R.id.camera_preview);
		mSurfaceView.setOnTouchListener(new OnTouchListener() {
	            @Override
	            public boolean onTouch(View v, MotionEvent event) {
	                if (event.getAction() == MotionEvent.ACTION_UP) {// 前置摄像头取消触摸自动聚焦功能
	                    View view = findViewById(R.id.camera_main);
	                    isTouchFocus=true;
	                    mCameraManager.autoFocus(IDCardImageActivity.this,view, event);
	                }
	                return true;
	            }
	        });

		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(IDCardImageActivity.this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		idcardA = null;
		super.onResume();
	}

	private void recycleBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			Log.d(TAG, "" + bitmap);
			bitmap.recycle();
			bitmap = null;
		}
	}

	public Bitmap zoomBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap newbmp = bitmap;
		if (height > 480) {
			Matrix matrix = new Matrix();
			int newHeight = 480;
			int newWidth = (width * 480) / height;
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			matrix.postScale(scaleWidth, scaleHeight);
			newbmp = Bitmap.createBitmap(newbmp, 0, 0, width, height, matrix,
					false);
			return newbmp;
		}
		return newbmp;
	}

	/**
	 * @param resultStr
	 * @return
	 */
	private void converIDStrToJson(int regStatusCode) {
		if(regStatusCode==OcrEngine.RECOG_OK){
			if(idCard!=null){
				String name=idCard.getName();
				String sex=idCard.getSex();
				String birthday=idCard.getBirth();
				String ethnicity=idCard.getEthnicity();
				String address=idCard.getAddress();
				String cardNo=idCard.getCardNo();
				if(name!=null)
				mShenFenZheng2Data.setName(name);
				if(sex!=null)
				mShenFenZheng2Data.setSex(sex);
				if(birthday!=null)
				mShenFenZheng2Data.setBirthdate(birthday);
				if(ethnicity!=null)
				mShenFenZheng2Data.setMinzu(ethnicity);
				if(address!=null)
				mShenFenZheng2Data.setAddress(address);
				if(cardNo!=null)
				mShenFenZheng2Data.setShenfenzhengID(cardNo);
			}
			
		}
		mShenFenZheng2Data.setIdCardImagePath(IDCARDIAMGERE_REL_APATH);	
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			mHandler.sendEmptyMessage(CANCLE_REG);
		}
		return super.onKeyDown(keyCode, event);
	}

	/*private void discernFront() {
		mProgressDialog.show();
		new Thread() {
			@Override
			public void run() {
				try {
					zoomBitmap(IDCARDIAMGEPATH, 2048, 1536, 100);
					resultStr = IDCardImage.getInstance().getFrontInfo(
							IDCardImageActivity.this, IDCARDIAMGEPATH,
							WarningView.ocrBorder);
					zoomBitmap(IDCARDIAMGEPATH, 640, 480, 60);
					// handlePicture();
					if (resultStr == null) {
						resultStr = "识别失败";
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					mHandler.sendEmptyMessage(1);
				}
			}
		}.start();
	}*/

	private String savePicture(Bitmap bitmap, String photo) {
		String ret = "";
		BufferedOutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(new File(photo)));
			if (bitmap != null)
				bitmap.compress(CompressFormat.JPEG, 100, os);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null)
					os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	/**
	 * 获取sd卡的路径
	 * 
	 * @return 路径的字符串
	 */
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		}
		return sdDir.toString();
	}

	private void sendResultBroadcast() {
		Intent intent = new Intent(ActivityUtils.IDCARD_INFO_ACTION);
		String jsonStr = JsonUtils.writeObjectToJsonStr(mShenFenZheng2Data);
		Log.d(TAG, "jsonStr:" + jsonStr);
		intent.putExtra(JavaScriptConfig.EXTRAL_ID_CARD_INFO, jsonStr);
		sendBroadcast(intent);
		finish();
	}

	/**
	 * @param pathName
	 *            图片文件路径
	 * @param limitWidth
	 *            限制图片宽度
	 * @param limitHeight
	 *            限制图片高度
	 * @param quality
	 *            保存的图片质量。取值范围1-100
	 */
	@SuppressWarnings("unused")
	private void zoomBitmap(String pathName, float limitWidth,
			float limitHeight, int quality) {

		// 获取源图片的大小
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, opts);

		if (opts.outHeight <= limitHeight && opts.outWidth <= limitWidth)
			return;

		// 按比例计算缩放后的图片大小
		float ratio = 1;
		if ((opts.outWidth > opts.outHeight) == (limitWidth > limitHeight))
			ratio = Math.max(opts.outWidth / limitWidth, opts.outHeight
					/ limitHeight);
		else
			ratio = Math.max(opts.outWidth / limitHeight, opts.outHeight
					/ limitWidth);

		// 对图片进行压缩，是在读取的过程中进行压缩，而不是把图片读进了内存再进行压缩
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = false;
		// 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
		newOpts.inSampleSize = (int) ratio + 1;
		// 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
		newOpts.outWidth = (int) (opts.outWidth / ratio);
		newOpts.outHeight = (int) (opts.outHeight / ratio);

		try {
			// 获取缩放后图片
			Bitmap destBm = BitmapFactory.decodeFile(pathName, newOpts);
			Log.e(getClass().getName(), "width = " + destBm.getWidth()
					+ " , height = " + destBm.getHeight());
			// 创建文件输出流
			FileOutputStream os = new FileOutputStream(new File(pathName));
			// 存储
			destBm.compress(CompressFormat.JPEG, quality, os);
			// 关闭流
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (width > height) {
			mCameraManager.setPreviewSize(width, height);
		} else {
			mCameraManager.setPreviewSize(height, width);
		}
		mCameraManager.initDisplay();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mCameraManager.openCamera(holder);
			mMaxZoom=mCameraManager.getMaxZoom();
			if(!mCameraManager.isSupportAutoFocus()){
				mVerticalSeekBar.setVisibility(View.VISIBLE);
				Toast.makeText(getBaseContext(), "不支持自动对焦！", Toast.LENGTH_LONG).show();
			}
		} catch (RuntimeException e) {
			Toast.makeText(IDCardImageActivity.this, R.string.camera_open_error,
					Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(IDCardImageActivity.this, R.string.camera_open_error,
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCameraManager.closeCamera();
	}
}