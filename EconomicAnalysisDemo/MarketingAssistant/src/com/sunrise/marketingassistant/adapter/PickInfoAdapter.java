package com.sunrise.marketingassistant.adapter;

import java.util.List;

import com.sunrise.javascript.utils.LogUtlis;
import com.sunrise.marketingassistant.App;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.entity.ChlBaseInfo;
import com.sunrise.marketingassistant.utils.AsyncImageLoader.ImageCallBack;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PickInfoAdapter extends BaseAdapter {
	
	private static final String PORT = ":18098";
	private static final String SERVER_IP = "218.205.252.26";

	private static final String HTTP = "http://";
	private static final String HTTP_SPLIT = "/";
	private static final String IMGS="scUnifiedAppManagePlatform/resources/info_img/";
	
	private List<ChlBaseInfo> data;
	private LayoutInflater mInflater;

	public PickInfoAdapter(Context context, List<ChlBaseInfo> data) {
		super();
		this.data = data;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder =null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_pick_info, null);
			viewHolder = new ViewHolder();
			initView(viewHolder,convertView);
			convertView.setTag(viewHolder);
			
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.pick_image.setImageResource(R.drawable.hall_icon);
		}
		ChlBaseInfo dto = data.get(position);
		//getDrawable(dto.getImageUrl(),viewHolder.pick_image, position);
		viewHolder.pick_image.setTag(dto.getOutdoorPic()+"&"+position);
		if(!TextUtils.isEmpty(dto.getOutdoorPic())){
			App.getImageLoader().loadDrawable(getUrl(IMGS)+dto.getOutdoorPic(), new ImageViewCallback(viewHolder.pick_image));
		}
         //.DisplayImage(, viewHolder.pick_image, false);
		viewHolder.name.setText(dto.getChlName());
		viewHolder.type.setText(dto.getChlType());
		viewHolder.address.setText(dto.getAddress());
		
		return convertView;
	}
	class ImageViewCallback implements ImageCallBack {
		ImageView imageView;

		ImageViewCallback(ImageView imageView) {
			this.imageView = imageView;
			LogUtlis.e("图片路径信息：", imageView.toString());
		}

		@Override
		public void loadImage(Bitmap d) {
			imageView.setImageBitmap(d);
		}
	}
	private void initView(ViewHolder viewHolder,View convertView){
		viewHolder.pick_image = (ImageView)convertView.findViewById(R.id.pick_image);
		viewHolder.name = (TextView)convertView.findViewById(R.id.name);
		viewHolder.type = (TextView)convertView.findViewById(R.id.type);
		viewHolder.address = (TextView)convertView.findViewById(R.id.address);
	}
	
	
	private String getUrl(String relUrl) {
		String absUrl = HTTP + SERVER_IP + PORT + HTTP_SPLIT + relUrl;
		return absUrl;
	}
	class ViewHolder{
		ImageView pick_image;
		TextView name;
		TextView type;
		TextView address;
	}

}
