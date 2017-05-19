package com.sunrise.marketingassistant.fragment;

import java.io.IOException;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.activity.SingleFragmentActivity;
import com.sunrise.marketingassistant.entity.TabContent;

/**
 * Initial the camera
 * 
 * @author Ryan.Tang
 */
public class MipcaCaptureFragment extends BaseFragment implements Callback, MipcaCaptureInterface, ExtraKeyConstant {
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
	private SurfaceView surfaceView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CameraManager.init(getActivity());
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return initView(inflater, container);
	}

	private View initView(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(R.layout.activity_capture, container, false);
		viewfinderView = (ViewfinderView) view.findViewById(R.id.viewfinder_view);
		surfaceView = (SurfaceView) view.findViewById(R.id.preview_view);
		Button mButtonBack = (Button) view.findViewById(R.id.button_back);
		mButtonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callBack("扫描取消", false);
				mBaseActivity.onExit(MipcaCaptureFragment.this);
			}
		});
		((View) mButtonBack.getParent()).setVisibility(View.GONE);
		return view;
	}

	public void onStart() {
		super.onStart();
		hasSurface = false;
		inactivityTimer = new InactivityTimer(mBaseActivity);
	}

	@Override
	public void onResume() {
		super.onResume();

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
		AudioManager audioService = (AudioManager) mBaseActivity.getSystemService(Context.AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

	}

	@Override
	public void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	public void onStop() {
		inactivityTimer.shutdown();
		CameraManager.get().stopPreview();
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultStr = result.getText();
		if (TextUtils.isEmpty(resultStr)) {
			Toast.makeText(mBaseActivity, "扫描出错", Toast.LENGTH_SHORT).show();
		} else {
			// handleResult(resultStr);
			// Toast.makeText(mBaseActivity, "扫描完成:\n" + resultStr,
			// Toast.LENGTH_SHORT).show();
			callBack(resultStr, true);
			// mBaseActivity.onBackPressed();
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
	@SuppressLint("DefaultLocale")
	private void callBack(String resultMessage, boolean isSuccess) {
		String jsonStr = resultMessage;
		jsonStr = String.format(FORMAT_JS_RETURN, isSuccess ? 0 : 1, resultMessage);

		// Intent intent = new Intent(ActivityUtils.DIMENSIONAL_BAR_CODE_SCAN);
		// intent.putExtra(Intent.EXTRA_TEXT, jsonStr);
		// mBaseActivity.sendBroadcast(intent);
		TabContent tabcontent = JsonUtils.parseJsonStrToObject(getArguments().getString(ExtraKeyConstant.KEY_URL), TabContent.class);
		mBaseActivity.startActivity(SingleFragmentActivity.createIntent(mBaseActivity, WebViewFragment2.class, tabcontent.getZipContent(), "扫描结果",
				tabcontent.getLastModify(), jsonStr));
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
			mBaseActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
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
			Vibrator vibrator = (Vibrator) mBaseActivity.getSystemService(Context.VIBRATOR_SERVICE);
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
		// mBaseActivity.setResult(Activity.RESULT_OK, (Intent) obj);
		// mBaseActivity.finish();
		callBack(obj.toString(), true);
	}

	@Override
	public void LanchProductQuery(Object obj) {
		String url = (String) obj;
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		startActivity(intent);
	}

}