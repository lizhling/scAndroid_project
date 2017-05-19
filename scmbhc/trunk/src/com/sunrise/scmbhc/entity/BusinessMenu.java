package com.sunrise.scmbhc.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.ui.activity.BaseActivity;
import com.sunrise.scmbhc.ui.activity.SingleFragmentActivity;
import com.sunrise.scmbhc.ui.fragment.BaseFragment;
import com.sunrise.scmbhc.ui.fragment.CallTransferFragment;
import com.sunrise.scmbhc.ui.fragment.DefaultBusinessDetailFragment1;
import com.sunrise.scmbhc.ui.fragment.TwoCityOneHomeFragment;
import com.sunrise.scmbhc.ui.fragment.WebViewFragment;
import com.sunrise.scmbhc.ui.fragment.WholeFamilyHappyFragment;
import com.sunrise.scmbhc.utils.AsyncImageLoader;
import com.sunrise.scmbhc.utils.AsyncImageLoader.ImageCallBack;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BusinessMenu implements Parcelable, ExtraKeyConstant {
	public static final long ROOT_BUSINESSMEUN_ID = 1;
	public static final int MENU_TYPE = 0;
	public static final int WEB_BUSINESS_TYPE = 1;
	public static final int LOCAL_BUSINESS_TYPE = 2;
	public static final int WHOLE_FAMILY_HAPPY_BUSINESS_TYPE = 3;
	public static final int TWOCITY_ONE_HOME_BUSINESS_TYPE = 4;
	public static final int CALL_TRANSFER_BUSINESS_TYPE = 5;
	private static final String TAG = "BusinessMenu";

	private static final String[] ARRAY_OPENED_TAG = { "OPERATE_TYPE", "op_type", "busi_type" };
	private static final HashMap<String, String> HASH_EXCHANGE_CANCLE;
	static {
		HASH_EXCHANGE_CANCLE = new HashMap<String, String>();
		HASH_EXCHANGE_CANCLE.put("KT1", "QX1");
		HASH_EXCHANGE_CANCLE.put("KT2", "QX1");
		HASH_EXCHANGE_CANCLE.put("KT3", "QX1");
		HASH_EXCHANGE_CANCLE.put("KT", "QX");
		HASH_EXCHANGE_CANCLE.put("1", "0");
		HASH_EXCHANGE_CANCLE.put("A", "D");
	}

	private long id;
	private long parentId;
	private int menuType;
	private int menuOrder;
	private String name;
	private String icon;
	private String description;
	private String charges;
	private String warmPrompt;
	private String businessData;
	private String serviceUrl;
	private String prodPrcid;
	private String busTag;
	private String busAppData;

	private Bitmap iconBitmap;
	private Integer iconRes;

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getMenuType() {
		return menuType;
	}

	public String getIcon() {
		return icon;
	}

	public String getDescription() {
		return description;
	}

	public String getCharges() {
		return charges;
	}

	public String getWarmPrompt() {
		return warmPrompt;
	}

	public String getBusinessData() {
		// businessData = businessData.replace(this.prodPrcid, "ACAZ08911");
		return businessData;
	}

	/**
	 * @return 获取删除动作
	 */
	private String getBusinessDataForCancle() {
		if (TextUtils.isEmpty(businessData))
			return null;

		String temp = businessData;

		{// 检测是否含有开通字段
			boolean iscontainOpenTag = false;
			for (String string : ARRAY_OPENED_TAG)
				if (temp.contains(string)) {
					iscontainOpenTag = true;
					break;
				}
			if (!iscontainOpenTag)
				return null;
		}

		HashMap<String, String> hash = analysisParamsData(temp);
		StringBuffer sb = new StringBuffer();
		if (hash != null) {
			Iterator<Entry<String, String>> it = hash.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> next = it.next();

				for (String string : ARRAY_OPENED_TAG)
					if (next.getKey().equalsIgnoreCase(string))
						if (HASH_EXCHANGE_CANCLE.containsKey(next.getValue()))
							next.setValue(HASH_EXCHANGE_CANCLE.get(next.getValue()));

				sb.append(next.getKey()).append('=').append(next.getValue()).append('&');
			}

			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
				temp = sb.toString();
			}
		}

		return temp;
	}

	public long getParentId() {
		return parentId;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public static Parcelable.Creator<BusinessMenu> getCreator() {
		return CREATOR;
	}

	public boolean isOpened() {
		// TODO
		return false;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMenuType(int menuType) {
		this.menuType = menuType;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCharges(String charges) {
		this.charges = charges;
	}

	public void setWarmPrompt(String warmPrompt) {
		this.warmPrompt = warmPrompt;
	}

	public void setBusinessData(String businessData) {
		this.businessData = businessData;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public String getProdPrcid() {
		return prodPrcid;
	}

	public void setProdPrcid(String prodPrcid) {
		if (prodPrcid == null)
			return;

		if (this.prodPrcid != null && !TextUtils.isEmpty(businessData) && !this.prodPrcid.equals(prodPrcid))
			businessData = businessData.replace(this.prodPrcid, prodPrcid);

		this.prodPrcid = prodPrcid;
	}

	@Override
	public void writeToParcel(Parcel p, int arg1) {
		p.writeLong(id);
		p.writeString(name);
		p.writeInt(menuType);
		p.writeString(icon);
		p.writeString(description);
		p.writeString(charges);
		p.writeString(warmPrompt);
		p.writeString(businessData);
		p.writeLong(parentId);
		p.writeString(serviceUrl);
		p.writeString(prodPrcid);
		p.writeString(busTag);
		p.writeString(busAppData);
	}

	public static final Parcelable.Creator<BusinessMenu> CREATOR = new Parcelable.Creator<BusinessMenu>() {

		@Override
		public BusinessMenu createFromParcel(Parcel p) {
			BusinessMenu item = new BusinessMenu();
			item.setId(p.readLong());
			item.setName(p.readString());
			item.setMenuType(p.readInt());
			item.setIcon(p.readString());
			item.setDescription(p.readString());
			item.setCharges(p.readString());
			item.setWarmPrompt(p.readString());
			item.setBusinessData(p.readString());
			item.setParentId(p.readLong());
			item.setServiceUrl(p.readString());
			item.setProdPrcid(p.readString());
			item.setBusTag(p.readString());
			item.setBusAppData(p.readString());
			return item;
		}

		@Override
		public BusinessMenu[] newArray(int size) {
			return new BusinessMenu[size];
		}

	};

	public Bitmap getIconBitmap() {
		return iconBitmap;
	}

	public void setIconBitmap(Bitmap iconBitmap) {
		this.iconBitmap = iconBitmap;
	}

	public Integer getIconRes() {
		return iconRes;
	}

	public void setIconRes(Integer iconRes) {
		this.iconRes = iconRes;
	}

	public int getOrder() {
		return menuOrder;
	}

	public void setOrder(int order) {
		this.menuOrder = order;
	}

	public String getBusTag() {
		return busTag;
	}

	public void setBusTag(String busTag) {
		this.busTag = busTag;
	}

	public String getBusAppData() {
		return busAppData;
	}

	public void setBusAppData(String busAppData) {
		this.busAppData = busAppData;
	}

	public void visit(BaseActivity baseActivity) {
		BaseFragment fragment = createBaseFragment();
		fragment.startFragment(baseActivity, R.id.fragmentContainer);
	}

	public void visitByNewActivity(BaseActivity baseActivity) {
		Intent intent = new Intent(baseActivity, SingleFragmentActivity.class);
		intent.putExtra(App.ExtraKeyConstant.KEY_FRAGMENT, getBaseFragmenetClass());
		Bundle bundle = new Bundle();
		bundle.putParcelable(KEY_BUSINESS_INFO, this);
		intent.putExtra(App.ExtraKeyConstant.KEY_BUNDLE, bundle);
		baseActivity.startActivity(intent);
	}

	public BaseFragment createBaseFragment() {
		BaseFragment fragment = null;
		try {
			fragment = getBaseFragmenetClass().newInstance();
			Bundle bundle = new Bundle();
			bundle.putParcelable(KEY_BUSINESS_INFO, this);
			fragment.setArguments(bundle);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}

		return fragment;
	}

	private Class<? extends BaseFragment> getBaseFragmenetClass() {
		Class<? extends BaseFragment> cls = null;

		switch (menuType) {
		case WEB_BUSINESS_TYPE:
			cls = WebViewFragment.class;
			String param = "app_version=" + App.sAPKVersionCode;
			serviceUrl += (serviceUrl.indexOf('?') == -1 ? '?' : '&') + param;
			break;
		case WHOLE_FAMILY_HAPPY_BUSINESS_TYPE:
			cls = WholeFamilyHappyFragment.class;
			break;
		case TWOCITY_ONE_HOME_BUSINESS_TYPE:
			cls = TwoCityOneHomeFragment.class;
			break;
		case CALL_TRANSFER_BUSINESS_TYPE:
			cls = CallTransferFragment.class;
			break;
		default:
			cls = DefaultBusinessDetailFragment1.class;
			break;
		}
		return cls;
	}

	public View bindDataToGalleryItemView(View convertView, Context context) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.gallery_business_menu_item, null);
			viewHolder.imageview = (ImageView) convertView.findViewById(R.id.business_menu_image);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.business_menu_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (iconBitmap != null) {
			viewHolder.imageview.setImageBitmap(iconBitmap);
		} else if (iconRes != null) {
			viewHolder.imageview.setImageResource(iconRes);
		} else {
			if (icon != null) {
				asyncLoadImage(context, viewHolder.imageview, icon);
			}
		}
		viewHolder.textView.setText(name);
		return convertView;
	}

	public View bindDataToHoritalListItemView(View convertView, Context context) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.home_business_menu_item, null);
			viewHolder.imageview = (ImageView) convertView.findViewById(R.id.business_menu_image);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.business_menu_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (iconBitmap != null) {
			viewHolder.imageview.setImageBitmap(iconBitmap);

		} else if (iconRes != null) {
			viewHolder.imageview.setImageResource(iconRes);
		} else {
			if (icon != null) {
				asyncLoadImage(context, viewHolder.imageview, icon);
			}
		}
		viewHolder.textView.setText(name);
		return convertView;
	}

	private Bitmap getDefaultIcon(ImageView img) {
		// iconView.get
		// BitmapFactory.
		byte[] compressData = null;
		img.setDrawingCacheEnabled(true);
		Bitmap bmp = Bitmap.createBitmap(img.getDrawingCache());
		img.setDrawingCacheEnabled(false);
		return bmp;
	}

	public View bindDataToSearchList(View convertView, Context context) {
		SearchViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new SearchViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.search_item, null);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.search_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (SearchViewHolder) convertView.getTag();
		}
		viewHolder.textView.setText(name);
		return convertView;
	}

	private void asyncLoadImage(Context context, final ImageView imageView, String url) {
		final AsyncImageLoader asyncImageLoader = new AsyncImageLoader(context);
		final ImageCallBack imageCallBack = new ImageCallBack() {
			@Override
			public void loadImage(Bitmap d) {
				if (d != null) {
					iconBitmap = d;
					imageView.setImageBitmap(iconBitmap);
				}
			}

		};
		asyncImageLoader.loadDrawable(url, imageCallBack);
	}

	class ViewHolder {
		ImageView imageview;
		TextView textView;
	}

	class SearchViewHolder {
		TextView textView;
	}

	private HashMap<String, String> analysisParamsData(String data) {
		if (TextUtils.isEmpty(data))
			return null;

		final String temp = data.trim();
		if (TextUtils.isEmpty(temp))
			return null;

		HashMap<String, String> hash = new HashMap<String, String>();

		for (int start = 0, end = 0; start != -1; start = end + 1) {

			end = temp.indexOf('=', start);
			if (end == -1)
				break;

			String key = temp.substring(start, end);
			start = end + 1;
			end = temp.indexOf('&', start);
			if (end == -1) {
				String value = temp.substring(start);
				hash.put(key, value);
				break;
			} else {
				String value = temp.substring(start, end);
				hash.put(key, value);
			}
		}

		return hash;
	}

	public BusinessMenu getQuietItem(String prod_PRCID) {

		String businessData = getBusinessDataForCancle();

		if (businessData == null)
			return null;

		BusinessMenu item = new BusinessMenu();
		item.setBusTag(busTag);
		item.setIcon(icon);
		item.setName(name);
		item.setIcon(icon);
		item.setServiceUrl(serviceUrl);
		item.setBusAppData(busAppData);
		item.setCharges(charges);
		item.setDescription(description);
		item.setMenuType(menuType);
		item.setOrder(menuOrder);
		item.setParentId(parentId);
		item.setId(id);
		item.setProdPrcid(prod_PRCID);
		item.setBusinessData(businessData);
		item.setWarmPrompt(warmPrompt);
		return item;
	}

	public String toString() {
		return JsonUtils.writeObjectToJsonStr(this);
	}

}
