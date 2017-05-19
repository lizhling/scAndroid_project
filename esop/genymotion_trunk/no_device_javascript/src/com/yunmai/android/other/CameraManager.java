package com.yunmai.android.other;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.sunrise.javascript.R;

public class CameraManager implements Camera.AutoFocusCallback
 {
 public static final int mWidth = 1600;
 public static final int mHeight = 1200;
 private Camera mCamera;
 private Handler mHandler;
 private int takeType = 1;
 private boolean isShowFrame;

 private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
   public void onAutoFocus(boolean success, Camera camera) {  } } ;

 private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
   public void onShutter() {  } } ;

 private Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
   public void onPictureTaken(byte[] _data, Camera _camera) {  } } ;

 private Camera.PictureCallback jpegCallback = new Camera.PictureCallback()
 {
   public void onPictureTaken(byte[] _data, Camera _camera) {
     try {
       Message msg = new Message();
       msg.what = CameraManager.this.takeType;
       Bundle bundle = new Bundle();
       bundle.putByteArray("picData", _data);
       msg.setData(bundle);
       CameraManager.this.mHandler.sendMessage(msg);
     } catch (NullPointerException e) {
       CameraManager.this.mHandler.sendEmptyMessage(0);
     } 
   }
 };

 public CameraManager(Context context, Handler handler)
 {
   this.mHandler = handler;
 }

 public void openCamera(SurfaceHolder holder) throws RuntimeException, IOException
 {
   if (this.mCamera == null) {
     this.mCamera = Camera.open();
     this.mCamera.setPreviewDisplay(holder);
     setPictureSize();
   }
 }

 public void initDisplay()
 {
   if (this.mCamera != null)
     this.mCamera.startPreview();
 }

 public void closeCamera()
 {
   if (this.mCamera != null) {
     this.mCamera.stopPreview();
     this.mCamera.release();
     this.mCamera = null;
   }
 }

 public void requestFocuse()
 {
   if (this.mCamera != null&&isSupportAutoFocus())
       this.mCamera.autoFocus(this);
   else{
 	  takePicture();
   }
 }

 public void autoFouce()
 {
   if (this.mCamera != null)
     this.mCamera.autoFocus(this.autoFocusCallback);
 }


   public void autoFocus(final Context context,View v, MotionEvent event) {
    if (isShowFrame) {
        return;
    }
    if (this.mCamera != null){
    	if(isSupportAutoFocus())
        mCamera.autoFocus(new AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
        }
    });

    RelativeLayout layout = (RelativeLayout) v;

    final ImageView imageView = new ImageView(context);
    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
            R.drawable.camera_focusing);
    imageView.setImageBitmap(bitmap);
    LayoutParams params = new RelativeLayout.LayoutParams(
            bitmap.getWidth(), bitmap.getHeight());
    params.leftMargin = (int) (event.getX() - bitmap.getWidth() / 2);
    params.topMargin = (int) (event.getY() - bitmap.getHeight() / 2);
    layout.addView(imageView, params);
    imageView.setVisibility(View.VISIBLE);
    ScaleAnimation animation = new ScaleAnimation(1, 0.5f, 1, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f);
    animation.setDuration(300);
    animation.setFillAfter(true);
    animation.setAnimationListener(new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(final Animation animation) {
            imageView.setImageResource(R.drawable.camera_focusing_g);

            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(400);
                        ((Activity) (context))
                                .runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView
                                                .setImageResource(R.drawable.camera_focusing_g_ok);
                                    }
                                });
                        Thread.sleep(200);
                        ((Activity) (context))
                                .runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        imageView.clearAnimation();
                                        imageView.setVisibility(View.GONE);
                                        isShowFrame = false;
                                    }
                                });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                };
            }.start();
        }
    });
    imageView.startAnimation(animation);
    isShowFrame = true;
    }
}

   public void takePicture()
   {
     if (this.mCamera == null) return;
     try {
       Camera.Parameters parameters = this.mCamera.getParameters();
       parameters.setJpegQuality(100);
       this.mCamera.setParameters(parameters);
       this.mCamera.takePicture(this.shutterCallback, this.rawCallback, this.jpegCallback);
     }
     catch (RuntimeException localRuntimeException)
     {
     }
   }
 
   public void setPreviewSize(int width, int height)
   {
     if (this.mCamera != null) {
       Camera.Parameters parameters = this.mCamera.getParameters();
       List<Size> previewSize = parameters.getSupportedPreviewSizes();
       Collections.sort(previewSize, new SizeComparator());
       if (previewSize != null) {
         int size = previewSize.size();
         int picIndex = 0;
         for (int i = 0; i < size; ++i) {
           if (((Camera.Size)previewSize.get(i)).width == width) {
             picIndex = i;
             break;
           }if (((Camera.Size)previewSize.get(i)).width < width) {
             picIndex = i - 1;
             if (picIndex < 0) {
               picIndex = 0;
             }
             int diffW1 = ((Camera.Size)previewSize.get(picIndex)).width - width;
             int diffW2 = width - ((Camera.Size)previewSize.get(i)).width;
             if (diffW1 <= diffW2) break;
             picIndex = i;
 
             break;
           }
         }
         if (Build.MODEL.startsWith("MI-ONE")) {
           if (Build.VERSION.INCREMENTAL.equals("2.10.12"))
             parameters.setPreviewSize(640, 480);
           else
             parameters.setPreviewSize(1280, 720);
         }
         else {
           parameters.setPreviewSize(((Camera.Size)previewSize.get(picIndex)).width, 
             ((Camera.Size)previewSize.get(picIndex)).height);
         }
       }
       this.mCamera.setParameters(parameters);
     }
   }
 
   private void setPictureSize()
   {
     Camera.Parameters parameters = this.mCamera.getParameters();
     parameters.setPictureFormat(256);
     List<Size> pictureSize = parameters.getSupportedPictureSizes();
     Collections.sort(pictureSize, new SizeComparator());
     if (pictureSize != null) {
       int size = pictureSize.size();
       int picIndex = 0;
       for (int i = 0; i < size; ++i) {
         if (((Camera.Size)pictureSize.get(i)).width == mWidth) {
           picIndex = i;
           break;
         }if (((Camera.Size)pictureSize.get(i)).width < mWidth) {
           picIndex = i - 1;
           if (picIndex < 0) {
             picIndex = 0;
           }
           int diffW1 = ((Camera.Size)pictureSize.get(picIndex)).width - mWidth;
           int diffW2 = mWidth - ((Camera.Size)pictureSize.get(i)).width;
           if ((diffW1 <= diffW2) || (((Camera.Size)pictureSize.get(i)).width <= 1280)) break;
           picIndex = i;
 
           break;
         }
       }
       parameters.setPictureSize(((Camera.Size)pictureSize.get(picIndex)).width, 
         ((Camera.Size)pictureSize.get(picIndex)).height);
     }
     this.mCamera.setParameters(parameters);
   }
 
   public void setCameraFlashMode(String mode)
   {
     Camera.Parameters parameters = this.mCamera.getParameters();
     parameters.setFlashMode(mode);
     this.mCamera.setParameters(parameters);
   }
 
   public boolean isSupportFlash(String mode)
   {
      List<String> modes = this.mCamera.getParameters().getSupportedFlashModes();
 
     return (this.mCamera != null) && (modes != null) && (modes.contains(mode));
   }
 
	public int getMaxZoom() {
	    if (mCamera != null) {
	    	Camera.Parameters parameters  = mCamera.getParameters();
		    return parameters.getMaxZoom();
	    }
	    else
	    	return -1;
	}
	
	// 设置Zoom
	public void setZoom(int value) {
	    Log.i("tag", "value:" + value);
	    if(mCamera!=null){
	    Camera.Parameters parameters  = mCamera.getParameters();
	    parameters.setZoom(value);
	    mCamera.setParameters(parameters);
	   }
	}

   public boolean isSupportAutoFocus()
   {
     List<String> list = getSupportedFocusModes();
     if (list == null) return false;
     for (String string : list) {
       if (Camera.Parameters.FOCUS_MODE_AUTO.equals(string)) {
         return true;
       }
     }
     return false;
   }
 
   public boolean isSupportFocus(String mode)
   {
     List<String> list = getSupportedFocusModes();
     if (list == null) return false;
     for (String string : list) {
       if (mode.equals(string)) {
         return true;
       }
     }
     return false;
   }
 
   private List<String> getSupportedFocusModes()
   {
     List<String> list = null;
     if (this.mCamera != null) {
       Camera.Parameters parameters = this.mCamera.getParameters();
       list = parameters.getSupportedFocusModes();
       for (String string : list) {
         Log.d("path", "------SupportedFocusModes----------->>" + string);
       }
     }
     return list;
   }
 
   public void setTakeIdcardA()
   {
     this.takeType = 1;
   }
 
   public void setTakeIdcardB()
   {
     this.takeType = 2;
   }
 
   public String getDefaultFlashMode()
   {
     if (this.mCamera.getParameters().getSupportedFlashModes() != null) {
       return (String)this.mCamera.getParameters().getSupportedFlashModes().get(0);
     }
     return "off";
   }
 
   public void onAutoFocus(boolean success, Camera camera)
   {
     takePicture(success);
   }
 
   private void takePicture(boolean captureOnly) throws RuntimeException {
     if (this.mCamera == null) return;
     try {
       Camera.Parameters parameters = this.mCamera.getParameters();
       parameters.setJpegQuality(100);
       this.mCamera.setParameters(parameters);
       this.mCamera.takePicture(this.shutterCallback, this.rawCallback, this.jpegCallback);
     }
     catch (RuntimeException localRuntimeException)
     {
     }
   }
 
   public class SizeComparator
     implements Comparator<Camera.Size>
   {
     public SizeComparator()
     {
     }
 
     public int compare(Camera.Size s1, Camera.Size s2)
     {
       return s2.width * s2.height - s1.width * s1.height;
     }
   }
 }