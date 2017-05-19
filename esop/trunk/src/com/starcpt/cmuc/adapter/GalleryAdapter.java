package com.starcpt.cmuc.adapter;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.model.WebViewWindow;
import com.starcpt.cmuc.ui.view.MyImageView;
import com.sunrise.javascript.utils.LogUtlis;

public class GalleryAdapter extends BaseAdapter {
private static String TAG="GalleryAdapter";
private ArrayList<WebViewWindow> mWebViewWindows;
private LayoutInflater mInflater;
private OnItemRemoveListener itemRemoveListener;
private CmucApplication cmucApplication;
private ArrayList<Bitmap> refs=new ArrayList<Bitmap>();
	public GalleryAdapter(ArrayList<WebViewWindow> mWebViewWindows,Context context) {
	super();
	cmucApplication=(CmucApplication) context.getApplicationContext();
	this.mWebViewWindows = mWebViewWindows;
	mInflater=LayoutInflater.from(context);
}

	@Override
	public int getCount() {
		return mWebViewWindows.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mWebViewWindows.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		WebViewWindow webViewWindow=cmucApplication.getWebViewWindows().get(position);
		LogUtlis.d(TAG, "position:"+position);
		ViewHolder viewHolder = null;
		if(convertView==null){
			 convertView=mInflater.inflate(R.layout.webview_window_item, null);
			 viewHolder = new ViewHolder();
			 viewHolder.windowContent=(MyImageView) convertView.findViewById(R.id.webview_image);
			 viewHolder.closeView=(ImageView) convertView.findViewById(R.id.web_window_close);
			 convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		 if(mWebViewWindows.size()<=1){
			 viewHolder.closeView.setVisibility(View.INVISIBLE);
		 }
		 Bitmap bitmap=null;
		 if(refs.size()>0){
			 try {
				 bitmap=refs.get(position);
			} catch (Exception e) {
				e.printStackTrace();
			}
		 }
		 if(bitmap==null){
			 bitmap=webViewWindow.getBitmap();
			 refs.add(bitmap);
		 }
		 viewHolder.windowContent.setImageBitmap(bitmap);
		 viewHolder.closeView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cmucApplication.getWebViewWindows().remove(position);
				notifyDataSetChanged();
				itemRemoveListener.onItemRemove(position);
			}
		});
		 return convertView;
	}
	
	public interface OnItemRemoveListener{
		public void onItemRemove(int position);
	}

	public void setOnItemRemoveListener(OnItemRemoveListener itemRemoveListener) {
		this.itemRemoveListener = itemRemoveListener;
	}
	
	class ViewHolder{
		MyImageView windowContent;
		ImageView closeView;
	}
	
	public void recycleImage(){
		for(Bitmap bitmap:refs){
			if(bitmap!=null&&!bitmap.isRecycled()){
				bitmap.recycle();
				bitmap=null;
				System.gc();
			}
		}
		refs.clear();
	}
	
	/*private Bitmap comp(Bitmap image) {   	       
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();          
	    image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
	    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());   
	    BitmapFactory.Options newOpts = new BitmapFactory.Options();   
	    newOpts.inJustDecodeBounds = true;   
	    Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);   
	    newOpts.inJustDecodeBounds = false;   
	    int w = newOpts.outWidth;   
	    int h = newOpts.outHeight;   
	    float hh = 400f;
	    float ww = 240f;
	    int be = 1;
	    if (w > h && w > ww) {
	        be = (int) (newOpts.outWidth / ww);   
	    } else if (w < h && h > hh) {
	        be = (int) (newOpts.outHeight / hh);   
	    }   
	    if (be <= 0)   
	        be = 1;   
	    newOpts.inSampleSize = be;
	    isBm = new ByteArrayInputStream(baos.toByteArray());   
	    bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);   
	    return bitmap;
	}*/
	
}
