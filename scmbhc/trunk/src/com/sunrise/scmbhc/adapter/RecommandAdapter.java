package com.sunrise.scmbhc.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.BusinessMenu;

/**  
 *   
 * @Project: scmbhc  
 * @ClassName: RecommandAdapter  
 * @Description:
 * @Author qinhubao  
 * @CreateFileTime: 2014年3月27日 下午3:13:41  
 * @Modifier: qinhubao  
 * @ModifyTime: 2014年3月27日 下午3:13:41  
 * @ModifyNote: 
 * @version   
 *   
 */
public class RecommandAdapter extends BusinessListAdapter {
	private List<BusinessMenu> list;
	private Context mContext;
	public RecommandAdapter(Context context, List<BusinessMenu> list) {
		super(context, list);
		this.mContext = context;
		this.list = list;
	}
	class ViewHolder {
		ImageView imageview;
		TextView textView;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		BusinessMenu businessMenu = list.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_recommand, null);
			viewHolder.imageview = (ImageView) convertView.findViewById(R.id.business_menu_image);
			//viewHolder.imageview.setBackgroundResource(R.drawable.img_recommand_bg);
			viewHolder.imageview.setBackgroundResource(R.drawable.img_selector_bg);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.business_menu_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (businessMenu.getIconBitmap() != null) {
			viewHolder.imageview.setImageBitmap(businessMenu.getIconBitmap());
			
		} else if (businessMenu.getIconRes() != null){
			viewHolder.imageview.setImageResource(businessMenu.getIconRes());
		} else {
			if (businessMenu.getIcon() != null){
				viewHolder.imageview.setImageResource(R.drawable.icon_launcher);
			}
		}
		viewHolder.textView.setText(businessMenu.getName());
		//View view = businessMenu.bindDataToHoritalListItemView(convertView, mContext);
		
		return convertView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}
	

}
