package com.sunrise.scmbhc.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.ContentInfo;
import com.sunrise.scmbhc.ui.fragment.ManualFragment;
import com.sunrise.scmbhc.utils.DateUtil;


/**  
 *   
 * @Project: scmbhc  
 * @ClassName: ManualAdapter  
 * @Description: 为Manual绑定数据
 * @Author qinhubao  
 * @CreateFileTime: 2014年2月10日 下午4:51:15  
 * @Modifier: qinhubao  
 * @ModifyTime: 2014年2月10日 下午4:51:15  
 * @ModifyNote: 
 * @version   
 *   
 */
public class ManualAdapter extends BaseAdapter {

	private Context mContext;
	private List<ContentInfo> mList;

	private boolean isSimpleMode;

	public ManualAdapter(Context context, List<ContentInfo> list) {
		mContext = context;
		mList = list;
		setSimpleMode(false);
	}

	@Override
	public int getCount() {
		if (mList != null)
			return mList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {

		if (mList != null)
			return mList.get(position);

		return null;
	}

	@Override
	public long getItemId(int position) {
		if (mList != null)
			return mList.get(position).getContentId();
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup container) {
		View viewgroup = view;
		if (viewgroup == null)
			viewgroup = LayoutInflater.from(mContext).inflate(R.layout.manual_item, null);

		TextView title = (TextView) viewgroup.findViewById(R.id.tv_system_title);
		title.setText(mList.get(position).getTitle());

		TextView content = (TextView) viewgroup.findViewById(R.id.tv_system_content);
		content.setText(mList.get(position).getContentText());
		TextView datetime = (TextView) viewgroup.findViewById(R.id.tv_datetime);
		if (mList.get(position).getContentType() == ManualFragment.NOTICE_TYPE) {
			datetime.setVisibility(View.VISIBLE);
			if (mList.get(position).getUpdateTime() != null) {
				Date date = null;
				try {
					date = DateUtil.convertStringToDate(mList.get(position).getUpdateTime());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					SimpleDateFormat dateformat2 = new SimpleDateFormat(
							"yyyy-MM-dd hh:mm");
					if (date == null) {
						datetime.setText(mList.get(position).getUpdateTime());
					} else {
						datetime.setText(dateformat2.format(date));
					}
				}
				
			} else {
				datetime.setText(new Date().toLocaleString());
			}
		} else {
			datetime.setVisibility(View.GONE);
		}
		//datetime.setText(mList.get(position).getContentText());
		return viewgroup;
	}

	public void setSimpleMode(boolean isSimpleMode) {
		this.isSimpleMode = isSimpleMode;
		notifyDataSetChanged();
	}

}
