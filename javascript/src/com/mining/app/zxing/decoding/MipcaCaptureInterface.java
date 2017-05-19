package com.mining.app.zxing.decoding;

import com.google.zxing.Result;
import com.mining.app.zxing.view.ViewfinderView;

import android.graphics.Bitmap;
import android.os.Handler;

public interface MipcaCaptureInterface {

	Handler getHandler();

	ViewfinderView getViewfinderView();

	void handleDecode(Result obj, Bitmap barcode);

	void returnScanResult(Object obj);

	void LanchProductQuery(Object obj);
	
}
