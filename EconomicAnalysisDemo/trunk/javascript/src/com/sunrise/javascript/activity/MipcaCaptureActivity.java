package com.sunrise.javascript.activity;

import java.io.IOException;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.decoding.CaptureActivityHandler;
import com.mining.app.zxing.decoding.InactivityTimer;
import com.mining.app.zxing.decoding.MipcaCaptureInterface;
import com.mining.app.zxing.view.ViewfinderView;
import com.sunrise.javascript.R;
import com.sunrise.javascript.utils.ActivityUtils;

/**
 * Initial the camera
 * 
 * @author Ryan.Tang
 */
public class MipcaCaptureActivity extends Activity implements Callback, MipcaCaptureInterface {
	private final static String BUS_TAG = "busTag";
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		// ViewUtil.addTopView(getApplicationContext(), this,
		// R.string.scan_card);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

		Button mButtonBack = (Button) findViewById(R.id.button_back);
		mButtonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callBack("扫描取消", false);
				finish();

			}
		});
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultStr = result.getText();
		if (TextUtils.isEmpty(resultStr)) {
			Toast.makeText(MipcaCaptureActivity.this, "扫描出错", Toast.LENGTH_SHORT).show();
		} else {
			// handleResult(resultStr);
			callBack(resultStr, true);
			Intent intent = new Intent();
			intent.putExtra(Intent.EXTRA_TEXT, resultStr);
			setResult(Activity.RESULT_OK, intent );
			finish();
		}
	}

	// private void handleResult(String resultStr) {
	// boolean displayScanRreslut = true;
	// boolean displayVisitBusiness = false;
	// String tag = null;
	// String url = null;
	// if (resultStr.contains(BUS_TAG)) {
	// String array[] = resultStr.split("\n");
	// String busTag = array[0];
	// if (busTag != null) {
	// String busTagArray[] = busTag.split(":");
	// if (busTagArray.length > 1) {
	// tag = busTagArray[1];
	// if (tag != null) {
	// displayScanRreslut = false;
	// displayVisitBusiness = true;
	// }
	// }
	// }
	// } else {
	// if (resultStr.contains("http")) {
	// int index = resultStr.indexOf("http");
	// url = resultStr.substring(resultStr.indexOf("http"));
	// if (index == 0) {
	// if (url.contains(App.sChinaMobileNetFlag)) {
	// displayScanRreslut = false;
	// }
	// }
	// }
	// }
	// if (displayScanRreslut) {
	// displayScanReslut(resultStr);
	// } else {
	// if (displayVisitBusiness) {
	// visitBusiness(tag);
	// } else {
	// if (url != null)
	// CommUtil.visitWebView(this, url);
	// }
	// }
	// // finish();
	// }

	// private void displayScanReslut(String resultStr) {
	// Intent intent = new Intent(this, SingleFragmentActivity.class);
	// intent.putExtra(ExtraKeyConstant.KEY_FRAGMENT,
	// QrCodeSacnReslutFragment.class);
	// BusinessMenu businessMenu = new BusinessMenu();
	// businessMenu.setDescription(resultStr);
	// businessMenu.setName("扫描结果");
	// Bundle bundle = new Bundle();
	// bundle.putParcelable(ExtraKeyConstant.KEY_BUSINESS_INFO, businessMenu);
	// intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, bundle);
	// intent.putExtra(ExtraKeyConstant.KEY_FINISH_ACTIVITY, true);
	// startActivity(intent);
	// }

	// private void visitBusiness(String tag) {
	// Intent resultIntent = new Intent(this, BusinessDetailActivity.class);
	// resultIntent.putExtra(ExtraKeyConstant.KEY_QR_CODE, tag);
	// startActivity(resultIntent);
	// }
	private void callBack(String resultMessage, boolean isSuccess) {
		String jsonStr = resultMessage;
		try {
			JSONObject jsobj = new JSONObject();
			jsobj.put("resultMsg", resultMessage);
			jsobj.put("result", String.valueOf(isSuccess));
			jsonStr = jsobj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Intent intent = new Intent(ActivityUtils.DIMENSIONAL_BAR_CODE_SCAN);
		intent.putExtra(Intent.EXTRA_TEXT, jsonStr);
		sendBroadcast(intent);
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	public void returnScanResult(Object obj) {
		setResult(Activity.RESULT_OK, (Intent) obj);
		finish();
	}

	@Override
	public void LanchProductQuery(Object obj) {
		String url = (String) obj;
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		startActivity(intent);
	}

}