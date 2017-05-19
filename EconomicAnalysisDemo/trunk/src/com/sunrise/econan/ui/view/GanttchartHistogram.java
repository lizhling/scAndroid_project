package com.sunrise.econan.ui.view;

import java.util.ArrayList;

import com.sunrise.econan.model.GanttchartValue;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

public class GanttchartHistogram implements Callback {
	/**
	 * 尺寸
	 */
	private int mWidth, mHeight;

	private ArrayList<GanttchartValue> mArray;

	public GanttchartHistogram(ArrayList<GanttchartValue> array,
			SurfaceHolder holder) {
		mArray = array;
		holder.addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		System.out.println("surfaceChanged :" + holder + "," + format + ","
				+ width + "," + height);

		mWidth = width;
		mHeight = height;

		Canvas canvas = holder.lockCanvas();
		paintBackground(canvas);
		Paint paint = new Paint();
		paint.setColor(0xff000000);
		canvas.drawText("牛逼完了", 30, 30, paint);
		drawAxis(canvas);
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		System.out.println("surfaceCreated :" + holder + " , iscreating = "
				+ holder.isCreating());

		Canvas canvas = holder.lockCanvas();
		Paint paint = new Paint();
		paint.setColor(0xffffff00);
		canvas.drawText("牛逼启动", 30, 30, paint);
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		System.out.println("surfaceDestroyed :" + holder);
	}

	private void paintBackground(Canvas canvas) {
		canvas.drawColor(0xeeffffff);
	}

	/**
	 * 绘制坐标轴
	 */
	private void drawAxis(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(0xff000000);

		canvas.drawLine(30, mHeight - 30, mWidth - 2, mHeight - 30, paint);
		canvas.drawLine(mWidth - 10, mHeight - 30 + 5, mWidth - 2,
				mHeight - 30, paint);
		canvas.drawLine(mWidth - 10, mHeight - 30 - 5, mWidth - 2,
				mHeight - 30, paint);

		canvas.drawLine(30, 2, 30, mHeight - 30, paint);
		canvas.drawLine(30, 2, 25, 12, paint);
		canvas.drawLine(30, 2, 35, 12, paint);

		Rect bounds = new Rect();
		paint.getTextBounds("0", 0, 1, bounds);
		canvas.drawText("0", 30 - bounds.right + bounds.left - 2, mHeight - 30
				+ bounds.bottom - bounds.top + 2, paint);
	}

	private void drawZhuzhuang(Canvas canvas) {
		if (mArray == null || mArray.size() == 0)
			return;

		int size = mArray.size();
		Paint paint = new Paint();
		float[] hsv = { 0, 1, 1 };

		int perWidth = mWidth - 30 - 12;// 每条柱的使用宽度
		perWidth /= size;

		int offy = mHeight - 31;
		for (int i = 0; i < size; ++i) {
			hsv[0] = 1.0f / size * i;
			paint.setColor(Color.HSVToColor(hsv));

			int offx = 30 + i * perWidth;
			canvas.drawRect(offx + perWidth / 4, offy, offx + perWidth * 3 / 4,
					offy - mArray.get(i).getValue(), paint);

			paint.setColor(0xff000000);

		}

	}

	private void drawText(Canvas canvas, String str, float x, float y,
			Paint paint, Gravity gravity) {
		if (gravity == Gravity.TOP_LEFT) {
			canvas.drawText(str, x, y, paint);
		}
	}

	enum Gravity {
		TOP_LEFT, BOTTOM_LEFT, TOP_RIGHT, BOTTOM_RIGHT, HCENTER_TOP, HCENTER_BOTTOM, VCENTER, CENTER;
	}
}
