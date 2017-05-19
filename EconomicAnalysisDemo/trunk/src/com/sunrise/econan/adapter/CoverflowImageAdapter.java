package com.sunrise.econan.adapter;

//import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.LinearGradient;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.PorterDuffXfermode;
//import android.graphics.Bitmap.Config;
//import android.graphics.PorterDuff.Mode;
//import android.graphics.Shader.TileMode;
//import android.net.Uri;
//import android.os.Handler;
//import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class CoverflowImageAdapter extends BaseAdapter {
	int mGalleryItemBackground;
	private Context mContext;

	// private ImageView[] mImages;
	private int width;
	private int height;
	private ArrayList<Bitmap> array;

	// public CoverflowImageAdapter(Context c, String[] mImage) {
	// mContext = c;
	// this.mImage = mImage;
	// hash_bitmap = new HashMap<String, Bitmap>();
	//
	// float density = mContext.getResources().getDisplayMetrics().density;
	// width = (int) (density * 350);
	// height = (int) (density * 300);
	//
	// createReflectedImages();
	//
	// }

	public CoverflowImageAdapter(Context context, ArrayList<Bitmap> array) {
		mContext = context;
		this.array = array;

		float density = mContext.getResources().getDisplayMetrics().density;
		width = (int) (density * 100);
		height = (int) (density * 100);
	}

	public int getCount() {
		if (array != null)
			return Integer.MAX_VALUE;// array.size();
		return 0;
	}

	public Object getItem(int position) {
		if (array != null)
			return array.get(position % array.size());
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {

			ImageView i = new ImageView(mContext);
			i.setScaleType(ImageView.ScaleType.FIT_CENTER);
			i.setLayoutParams(new Gallery.LayoutParams(width, height));
			convertView = i;
		}

		Bitmap bitmap = array.get(position % array.size());
		if (bitmap != null) {
			((ImageView) convertView).setImageBitmap(bitmap);
		}

		return convertView;
	}

	// private Handler handler = new Handler() {
	// public void handleMessage(Message msg) {
	// if (msg.obj == null)
	// return;
	// DownloadImageUpdateToShow obj = (DownloadImageUpdateToShow) msg.obj;
	// if (obj.getUrl() != null && obj.getImageView() != null) {
	// obj.getImageView().setImageURI(Uri.parse(obj.getUrl()));
	// // obj.getImageView().setImageBitmap(
	// // array.get(obj.getIndex()).getBitmap());
	// }
	// }
	// };

	// class DownloadImageUpdateToShow {
	// private ImageView imageView;
	// private String url;
	// private int index;
	//
	// public ImageView getImageView() {
	// return imageView;
	// }
	//
	// public void setImageView(ImageView imageView) {
	// this.imageView = imageView;
	// }
	//
	// public String getUrl() {
	// return url;
	// }
	//
	// public void setUrl(String url) {
	// this.url = url;
	// }
	//
	// public int getIndex() {
	// return index;
	// }
	//
	// public void setIndex(int index) {
	// this.index = index;
	// }
	// }

	/**
	 * 改变图片大小与是否添加倒影
	 * 
	 * @param mContext
	 *            Comtext
	 * @param id
	 *            图片R.id
	 * @param imgWidth
	 *            将图片修改为此Width
	 * @param imgHeight
	 *            将图片修改为此Height
	 * @param isShadow
	 *            图片是否添加倒影
	 * @return Bitmap
	 * @throws IOException
	 */
	// private Bitmap changeImage(String assetPath, int imgWidth, int imgHeight,
	// boolean isShadow) throws IOException {
	// // 加载需要操作的图片
	// Bitmap bitmapOrg = BitmapFactory.decodeStream(mContext.getAssets()
	// .open(assetPath));
	// // 获取这个图片的宽和高
	// int width = bitmapOrg.getWidth();
	// int height = bitmapOrg.getHeight();
	//
	// // 计算缩放率，新尺寸除原始尺寸
	// float scaleWidth = ((float) imgWidth) / width;
	// float scaleHeight = ((float) imgHeight) / height;
	//
	// // 创建操作图片用的matrix对象
	// Matrix matrix = new Matrix();
	// // 缩放图片动作
	// matrix.postScale(Math.min(scaleWidth, scaleHeight),
	// Math.min(scaleWidth, scaleHeight));
	//
	// // 是否有倒影
	// if (isShadow) {
	// return addImageShadow(Bitmap.createBitmap(bitmapOrg, 0, 0, width,
	// height, matrix, true));
	// } else {
	// return Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix,
	// true);
	// }
	// }

	/**
	 * 给图片添加倒影
	 * 
	 * @param bitmap
	 *            需要添加倒影的图片
	 * @return 添加好倒影的 Bitmap
	 */
	// private Bitmap addImageShadow(Bitmap bitmap) {
	// Bitmap originalImage = bitmap;
	// int width = originalImage.getWidth();
	// int height = originalImage.getHeight();
	//
	// // This will not scale but will flip on the Y axis
	// Matrix matrix = new Matrix();
	// matrix.preScale(1, -2);
	// // Create a Bitmap with the flip matrix applied to it.
	// // We only want the bottom half of the image
	// Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, 0,
	// width, height, matrix, false);
	//
	// // Create a new bitmap with same width but taller to fit
	// // reflection
	// Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
	// (height + height / 8), Config.ARGB_8888);
	//
	// // Create a new Canvas with the bitmap that's big enough for
	// // the image plus gap plus reflection
	// Canvas canvas = new Canvas(bitmapWithReflection);
	// // Draw in the original image
	// canvas.drawBitmap(originalImage, 0, 0, null);
	// // Draw in the gap
	// Paint deafaultPaint = new Paint();
	// canvas.drawRect(0, height, width, height, deafaultPaint);
	// // Draw in the reflection
	// canvas.drawBitmap(reflectionImage, 0, height, null);
	//
	// // Create a shader that is a linear gradient that covers the
	// // reflection
	// Paint paint = new Paint();
	//
	// // 设置渐变
	// LinearGradient shader = new LinearGradient(0,
	// originalImage.getHeight(), 0, bitmapWithReflection.getHeight(),
	// 0x66ffffff, 0x00ffff, TileMode.CLAMP);
	// // Set the paint to use this shader (linear gradient)
	// paint.setShader(shader);
	// // Set the Transfer mode to be porter duff and destination in
	// paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
	// // Draw a rectangle using the paint with our linear gradient
	// canvas.drawRect(0, height, width, bitmapWithReflection.getHeight(),
	// paint);
	// return bitmapWithReflection;
	// }
}
