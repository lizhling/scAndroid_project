package com.sunrise.econan.ui.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

public class CoverflowGallery extends Gallery {

	/**
	 * Camera 用于对图片做旋转、缩放调整
	 */
	private Camera mCamera = new Camera();

	/**
	 * 图片旋转最大角度
	 */
	private int mMaxRotationAngle = 100;

	/**
	 * 图片最大缩放大小（负数是放大，正数为缩小）
	 */
	private int mMaxZoom = 0;// -100;

	/**
	 * 缩放倍率
	 */
	private float zoomRate = 2;

	/**
	 * 用于判断图片旋转方像与缩放大小
	 */
	private int mCoveflowCenter;

	public CoverflowGallery(Context context) {
		super(context);
		this.setStaticTransformationsEnabled(true);
	}

	public CoverflowGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setStaticTransformationsEnabled(true);
	}

	public CoverflowGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setStaticTransformationsEnabled(true);
	}

	/**
	 * 取得图片旋转最大角度
	 * 
	 * @return the mMaxRotationAngle
	 */
	public int getMaxRotationAngle() {
		return mMaxRotationAngle;
	}

	/**
	 * 设置图片旋转最大角度
	 * 
	 * @param maxRotationAngle
	 *            the mMaxRotationAngle to set
	 */
	public void setMaxRotationAngle(int maxRotationAngle) {
		mMaxRotationAngle = maxRotationAngle;
	}

	/**
	 * 取得最大缩放大小
	 * 
	 * @return the mMaxZoom
	 */
	public int getMaxZoom() {
		return mMaxZoom;
	}

	/**
	 * 设置最大缩放大小
	 * 
	 * @param maxZoom
	 *            the mMaxZoom to set
	 */
	public void setMaxZoom(int maxZoom) {
		mMaxZoom = maxZoom;
	}

	/**
	 * Get the Centre of the Coverflow
	 * 
	 * @return The centre of this Coverflow.
	 */
	private int getCenterOfCoverflow() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
				+ getPaddingLeft();
	}

	/**
	 * Get the Centre of the View
	 * 
	 * @return The centre of the given view.
	 */
	private static int getCenterOfView(View view) {
		return view.getLeft() + view.getWidth() / 2;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #setStaticTransformationsEnabled(boolean)
	 */
	protected boolean getChildStaticTransformation(View child, Transformation t) {

		final int childCenter = getCenterOfView(child);
		final int childWidth = child.getWidth();
		int rotationAngle = 0;

		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);

		if (Math.abs(childCenter - mCoveflowCenter) < childWidth) {
			transformImageBitmap(
					(ImageView) child,
					t,
					(int) (((float) (mCoveflowCenter - childCenter) / childWidth) * getMaxRotationAngle()));
		} else {
			// rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) /
			// childWidth) * mMaxRotationAngle);
			// if (Math.abs(rotationAngle) > mMaxRotationAngle) {
			// rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle
			// : mMaxRotationAngle;
			// }
			// transformImageBitmap((ImageView) child, t, rotationAngle);
			transformImageBitmap((ImageView) child, t, getMaxRotationAngle());
		}

		return true;
	}

	/**
	 * This is called during layout when the size of this view has changed. If
	 * you were just added to the view hierarchy, you're called with the old
	 * values of 0.
	 * 
	 * @param w
	 *            Current width of this view.
	 * @param h
	 *            Current height of this view.
	 * @param oldw
	 *            Old width of this view.
	 * @param oldh
	 *            Old height of this view.
	 */
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mCoveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * Transform the Image Bitmap by the Angle passed
	 * 
	 * @param imageView
	 *            ImageView the ImageView whose bitmap we want to rotate
	 * @param t
	 *            transformation
	 * @param rotationAngle
	 *            the Angle by which to rotate the Bitmap
	 */
	private void transformImageBitmap(ImageView child, Transformation t,
			int rotationAngle) {
		mCamera.save();
		final Matrix imageMatrix = t.getMatrix();
		final int imageHeight = child.getLayoutParams().height;
		final int imageWidth = child.getLayoutParams().width;
		final int rotation = Math.abs(rotationAngle);

		// 在Z轴上正向移动camera的视角，实际效果为放大图片。
		// 如果在Y轴上移动，则图片上下移动；X轴上对应图片左右移动。
		mCamera.translate(0.0f, 0.0f, -100.0f);

		// As the angle of the view gets less, zoom in
		// if (rotation < mMaxRotationAngle) {
		// Log.e("rotation", "" + rotation);
		float zoomAmount = (float) (mMaxZoom + (rotation * zoomRate));
		mCamera.translate(0.0f, 0.0f, zoomAmount);
		child.setAlpha(Math.max(0x55, 0xFF - rotation));
		// }

		// 在Y轴上旋转，对应图片竖向向里翻转。
		// mCamera.rotateY(rotationAngle);
		// 如果在X轴上旋转，则对应图片横向向里翻转。
		// mCamera.rotateX(Math.abs(rotationAngle));
		mCamera.getMatrix(imageMatrix);
		imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
		imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
		mCamera.restore();
	}
}
