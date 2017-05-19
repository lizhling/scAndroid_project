package com.sunrise.javascript.function;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.javascript.JavaScriptConfig;
import com.sunrise.javascript.JavaScriptInterface;
import com.sunrise.javascript.JavascriptHandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

public class ImageChooserForWeb implements JavaScriptInterface {

	private Activity context;

	private JavascriptHandler mJavascriptHandler;

	private String key;

	private boolean isOnUploadProgress;

	public static final String KEY_UPDATE_URL = "update url";

	public static final int FILECHOOSER_RESULTCODE = 620;
	public static final int IMAGECHOOSER_RESULTCODE = 621;
	public static final int UPLOAD_IMAGE_RESULTCODE = 622;

	private String mUploadUrl;

	public ImageChooserForWeb(Context context, JavascriptHandler handler) {
		this.context = (Activity) context;
		this.mJavascriptHandler = handler;
	}

	private void callBack(String info, boolean isSuccess) {
		String jsonStr = info;
		try {
			JSONObject jsobj = new JSONObject();
			jsobj.put("resultMsg", info);
			jsobj.put("resultCode", String.valueOf(isSuccess ? 0 : 1));
			jsonStr = jsobj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ArrayList<Object> objs = new ArrayList<Object>();
		objs.add(JavaScriptConfig.JAVASCRIPT_API_CALLBACK_NAME);

		if (TextUtils.isEmpty(key)) {
			objs.add(new String[] { jsonStr, "key" });
		} else {
			objs.add(new String[] { jsonStr, key });
		}
		System.out.println("callBack：" + objs.toString());
		mJavascriptHandler.obtainMessage(0, objs).sendToTarget();
	}

	public void js_uploadImage(String key, String url) {

		if (isOnUploadProgress) {
			Toast.makeText(context, "正在上传，请勿重复点击……", Toast.LENGTH_SHORT).show();
			return;
		}
		isOnUploadProgress = true;

		this.key = key;
		mUploadUrl = url;
		// registerIdCardImageReciever();
		// Intent ti = new Intent(context, ImageChooserForWebActivity.class);
		// ti.putExtra(ImageChooserForWebActivity.KEY_UPDATE_URL, url);
		// ti.putExtra(JavaScriptConfig.TAG, key);
		// ti.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		// ti.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// context.startActivity(ti);

		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.setType("image/*");
		context.startActivityForResult(Intent.createChooser(i, key), UPLOAD_IMAGE_RESULTCODE);
	}

	// private void registerIdCardImageReciever() {
	// IntentFilter filter = new IntentFilter();
	// filter.addAction(ImageChooserForWebActivity.UPLOAD_IMAGE_ACTION);
	// context.registerReceiver(mIdCardImageReciever, filter);
	// }

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == UPLOAD_IMAGE_RESULTCODE) {
			if (Activity.RESULT_OK == resultCode) {
				Uri result = data == null ? null : data.getData();
				if (result == null) {
					result = data.getExtras().getParcelable("data");
				}
				String img_path = getImagePathInItent(result);
				uploadFile(mUploadUrl, img_path);
			} else
				callBack("未获取照片", false);
			return true;
		}

		return false;
	}

	private void uploadFile(String url, String localFile) {
		new Thread(new UploadRunnable(url, localFile)).start();
	}

	private String getImagePathInItent(Uri uri) {
		String result = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = context.getContentResolver().query(uri, proj, null, null, null);
		if (actualimagecursor != null) {
			int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			actualimagecursor.moveToFirst();
			result = actualimagecursor.getString(actual_image_column_index);
		}
		actualimagecursor.close();
		return result;
	}

	private Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
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
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return bitmap;// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 通过HTTP协议向指定的网络地址发送文件。
	 * 
	 * @param params
	 *            传输过程中需要传送的参数
	 * @param filename
	 *            需要传送的文件在本地的位置。
	 * @throws Exception
	 * 
	 */
	public static String doPost(URL url, InputStream stream) throws Exception {
		URLConnection conn = null; // URL连结对象。
		BufferedReader in = null; // 请求后的返回信息的读取对象。
		String keyName = null;
		try {
			// 打开和URL之间的连接
			conn = url.openConnection();
			// 设置通用的请求属性
			conn.addRequestProperty("accept", "*/*");
			conn.addRequestProperty("connection", "Keep-Alive");
			conn.addRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行

			conn.setDoInput(true);

			conn.setUseCaches(true);
			conn.setDoOutput(true);
			conn.addRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryFBBbUBfeDgfDXoQ0"); // multipart/form-data

			// 构造传输文件
			// FileInputStream fis = new FileInputStream(filename);
			BufferedInputStream bis = new BufferedInputStream(stream);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int ch;
			while ((ch = bis.read()) != -1)
				baos.write(ch);
			byte[] fileData = baos.toByteArray();

			// 传输文件。
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(conn.getOutputStream()));

			dos.write(fileData);
			dos.flush();
			dos.close();

			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			// in.close();
		} catch (FileNotFoundException fe) {

			fe.printStackTrace();
			InputStream err = ((HttpsURLConnection) conn).getErrorStream();
			if (err == null)
				throw new Exception("网络传输时发生的未知错误");
			in = new BufferedReader(new InputStreamReader(err));
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new Exception("网络传输错误！");
		}
		// 返回提示信息
		StringBuffer response = new StringBuffer();
		String line;
		try {
			while ((line = in.readLine()) != null)
				// System.out.println("line"+line);
				response.append(line);
			in.close();
		} catch (IOException ioe) {
			ioe.getStackTrace();
			throw new Exception("网络响应错误！");
		}
		return response.toString();
	}

	private class UploadRunnable implements Runnable {
		String url;
		String localFile;

		public UploadRunnable(String url, String localFile) {
			this.url = url;
			this.localFile = localFile;
		}

		@Override
		public void run() {
			try {
				Bitmap bitmap = getimage(localFile);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
				int options = 100;
				while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
					baos.reset();// 重置baos即清空baos
					bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
					options -= 10;// 每次都减少10
				}
				byte[] data = baos.toByteArray();
				// saveByteIntoFile(data, tempImagePath);
				String jsonStr = doPost(new URL(url), new ByteArrayInputStream(data));
				{
					JSONObject jsobj = new JSONObject();
					jsobj.put("idCard", jsonStr);
					jsobj.put("result", String.valueOf(true));
					jsonStr = jsobj.toString();
				}
				callBack(jsonStr, true);
			} catch (Exception e) {
				e.printStackTrace();
				callBack("上传失败:" + e.getMessage(), false);
			} finally {
				isOnUploadProgress = false;
			}

		}
	}
}
