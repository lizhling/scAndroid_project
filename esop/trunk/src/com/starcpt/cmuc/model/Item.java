package com.starcpt.cmuc.model;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
 
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.cache.preferences.Preferences;
import com.starcpt.cmuc.utils.AsyncImageLoader;
import com.starcpt.cmuc.utils.AsyncImageLoader.ImageCallBack;

public abstract class Item{
	//private final String TAG="Item";
	public final static String DISPLAY_STYLE_1="STYLE_ONE";
	public final static String DISPLAY_STYLE_2="STYLE_TWO";
	public final static String DISPLAY_STYLE_3="display_style_3";
	public static final int MENU_TYPE=0;
	public static final int WEB_TYPE=1;
	public static final int APP_TYPE=2;
	public static final int UN_DELETE=0;
	public static final int DELETED=1;
	private Bitmap bitmap;
	private int downPercentage;

	public abstract String getName(); 
	abstract void setName(String name); 
	public abstract String getDescription(); 
	public abstract void setDescription(String description); 
	public abstract String getIcon(); 
	abstract void setIcon(String icon);
	abstract String getIconClick(); 
	abstract void setIconClick(String iconClick); 
	public abstract String getAppTag();
	abstract void setAppTag(String appTag);	
	public abstract long getListOrder(); 
	public abstract void setListOrder(long listOrder);
	public abstract String getItemStyleName();
	public abstract void setItemStyleName(String itemStyleName); 
	public abstract String getChildStyleName(); 
	abstract void setChildStyleName(String childStyleName); 
	public abstract int getMenuId() ;
	abstract void setMenuId(int menuId); 
	public abstract int getBusinessId();
	abstract void setBusinessId(int businessId);
	public abstract int getMenuType() ;
	abstract void setMenuType(int menuType);
	public abstract String getContent(); 
	abstract void setContent(String content);	
	public abstract long getChildVersion(); 
	public abstract void setChildVersion(long childVersion); 
	abstract public long getCollectionTime() ;
	abstract public void setCollectionTime(long collectionTime) ;
	abstract public String getUserName();	
	abstract public void setUserName(String userName) ;
	abstract public int getId() ;
	abstract public int getDeleteFlag();
	abstract public void setDeleteFlag(int deleteFlag) ;	
	abstract public void setId(int id);
	abstract public void setApplicationName(String applicationName);
	abstract public String getApplicationName();
	abstract public  boolean getAppDowningLoadStaus(); 
	abstract public  void setAppDownLoadingStaus(boolean status);
	abstract public String getThreePackageNameString();
	abstract public void setThreePackageNameString(String str);
	
	public Bitmap getBitmap(){
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap){
		this.bitmap=bitmap;
	}
	
	
	
	public int getDownPercentage() {
		return downPercentage;
	}
	
	public void setDownPercentage(int downPercentage) {
		this.downPercentage = downPercentage;
	}
	
	public View bindItemBeanToView(View convertView,Context context){
	String itemStyleName=getItemStyleName();
	if(itemStyleName!=null){
			if(itemStyleName.equals(Item.DISPLAY_STYLE_1))
			convertView = bindDisplayStyle1ItemBeanToView(convertView,context);	
			else if(itemStyleName.equals(Item.DISPLAY_STYLE_2)){
			convertView=bindDisplayStyle2ItemBeanToView(convertView,context);
			}else if(itemStyleName.equals(Item.DISPLAY_STYLE_3)){
			convertView=bindDisplayStyle3ItemBeanToView(convertView,context);
			}			
		}else{
		convertView = bindDisplayStyle1ItemBeanToView(convertView,context);	
		}
		return convertView;
	}
	
	//private static final int BITMAP_WIDTH=84;
	private View bindDisplayStyle1ItemBeanToView(View convertView,Context context){
		String title=getName();
	    String iconUrl=getIcon();
    	convertView=LayoutInflater.from(context).inflate(R.layout.display_style_1_item, null);
    	final ImageView imgView=(ImageView) convertView.findViewById(R.id.display_style_1_item_icon);
    	TextView txtView=(TextView) convertView.findViewById(R.id.display_style_1_item_text);
    	TextView downLoaingStatusView=(TextView) convertView.findViewById(R.id.display_style_1_item_text_1);
	      if(iconUrl!=null){
			   if(bitmap!=null){
				   imgView.setImageBitmap(bitmap);
			   }
			   else{
				   CmucApplication cmucApplication=(CmucApplication) context.getApplicationContext();
				   imgView.setImageBitmap(cmucApplication.getGridViewItemDefaultBitmap());
				   asyncLoadImage(imgView,context);
			   }
	 }
	      if(getAppDowningLoadStaus()){
	    	  downLoaingStatusView.setVisibility(View.VISIBLE);
	    	  String staus=context.getString(R.string.app_downloading)+getDownPercentage()+"%";
	    	  downLoaingStatusView.setText(staus);
	      }else{
	    	  downLoaingStatusView.setVisibility(View.GONE);
	      }
	      if(title!=null) 
	    	  txtView.setText(title);
	   return convertView;
	}

	private void asyncLoadImage(final ImageView imageView,Context context){
		final AsyncImageLoader asyncImageLoader=new AsyncImageLoader(context);
		final ImageCallBack imageCallBack=new ImageCallBack(){
			@Override
			public void loadImage(Bitmap d) {
				if(d!=null){
					  /* if(CmucApplication.sIsPad)
						   bitmap=BitmapUtils.zoomBitmap(d, BITMAP_WIDTH, BITMAP_WIDTH);
					   else*/
						   bitmap=d;
						imageView.setImageBitmap(bitmap);
					}else{
						//asyncImageLoader.loadDrawable(getIcon(), this);
					}
			}
			
		};
		asyncImageLoader.loadDrawable(getIcon(), imageCallBack);
	}
	
	private View bindDisplayStyle2ItemBeanToView(View convertView,Context context) {
		String title=getName();
		String iconUrl=getIcon();
		String des=getDescription();
	    String appName=getApplicationName();
	    String appTag=getAppTag();
	    if(appName==null){
	    	 if(appTag!=null){
	   		  Integer resId=CmucApplication.sAppSplashNames.get(appTag);
	   		  if(resId!=null){
	   			  appName=context.getString(resId.intValue());
	   		  }
	   	    }
	    }
		DisplayStyle2ViewHolder displayStyle2ViewHolder;
		if(convertView==null){
			displayStyle2ViewHolder=new DisplayStyle2ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.display_style_2_item, null);
			displayStyle2ViewHolder.imageView=(ImageView) convertView.findViewById(R.id.display_style_2_item_icon);
		    displayStyle2ViewHolder.nameView=(TextView) convertView.findViewById(R.id.dispaly_style_2_item_name);
		    displayStyle2ViewHolder.desView=(TextView) convertView.findViewById(R.id.dispaly_style_2_item_des);
		    displayStyle2ViewHolder.appNameView=(TextView) convertView.findViewById(R.id.dispaly_style_2_item_app_name);
		    displayStyle2ViewHolder.downLoaingStatusView=(TextView) convertView.findViewById(R.id.dispaly_style_3_item_downloading);
		    convertView.setTag(displayStyle2ViewHolder);
		}else{
			displayStyle2ViewHolder=(DisplayStyle2ViewHolder) convertView.getTag();
		}
		Bitmap icon=getBitmap();
		if(iconUrl!=null){
			   if(icon!=null){
				   displayStyle2ViewHolder.imageView.setImageBitmap(icon);
			   }
			   else{
				   asyncLoadImage(displayStyle2ViewHolder.imageView,context);
			   }
          }
		  if(title!=null) 
			  displayStyle2ViewHolder.nameView.setText(title);
		  if(des!=null)
			  displayStyle2ViewHolder.desView.setText(des);
		  if(appName!=null){
			  appName="（"+appName+"）";
			  displayStyle2ViewHolder.appNameView.setText(appName);
		  }
		  if(getAppDowningLoadStaus()){
			  displayStyle2ViewHolder.downLoaingStatusView.setVisibility(View.VISIBLE);
			  String staus=context.getString(R.string.app_downloading)+getDownPercentage()+"%";
			  displayStyle2ViewHolder.downLoaingStatusView.setText(staus);
	      }else{
	    	  displayStyle2ViewHolder.downLoaingStatusView.setVisibility(View.GONE);
	      }
		return convertView;
	}
		
	private View bindDisplayStyle3ItemBeanToView(View convertView,Context context) {
		String title=getName();
		DisplayStyle3ViewHolder displayStyle3ViewHolder;
		if(convertView==null){
			displayStyle3ViewHolder=new DisplayStyle3ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.display_style_3_item, null);
		    displayStyle3ViewHolder.textView=(TextView) convertView.findViewById(R.id.dispaly_style_3_item_text);
		    convertView.setTag(displayStyle3ViewHolder);
		}else{
			displayStyle3ViewHolder=(DisplayStyle3ViewHolder) convertView.getTag();
		}
		  if(title!=null) 
			  displayStyle3ViewHolder.textView.setText(title);
		return convertView;
	}


	class DisplayStyle2ViewHolder{
		ImageView imageView;
		TextView nameView;
		TextView desView;
		TextView appNameView;
		TextView downLoaingStatusView;
	}

	class DisplayStyle3ViewHolder{
		TextView textView;
	}
	
	
	
}
