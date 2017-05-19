package com.sunrise.scmbhc.ui.view;

import java.util.ArrayList;
import java.util.HashMap;

import com.sunrise.scmbhc.utils.CommUtil;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * 通讯录对话框
 * 
 * @author fuheng
 * 
 */
public class ContacksDialog extends Dialog implements OnItemClickListener {

	private ArrayList<HashMap<String, String>> mPhoneContacks;

	private ContacksCallBack mCallback;

	private static final int[] TO = { android.R.id.text1, android.R.id.text2 };

	private static final String[] FROM = { Phone.DISPLAY_NAME, Phone.NUMBER };

	public ContacksDialog(Context context, ContacksCallBack callback) {
		super(context);

		mCallback = callback;

		initView();
	}

	private void initView() {

		mPhoneContacks = getPhoneContacts();

		ListView popupview = new ListView(getContext());
		popupview.setCacheColorHint(Color.TRANSPARENT);
		setContentView(popupview);
		popupview.setAdapter(new SimpleAdapter(getContext(), mPhoneContacks, android.R.layout.simple_list_item_2, FROM, TO));
		popupview.setOnItemClickListener(this);
	}

	/** 得到手机通讯录联系人信息 **/
	private ArrayList<HashMap<String, String>> getPhoneContacts() {
		ContentResolver resolver = getContext().getContentResolver();
		String[] PHONES_PROJECTION = new String[] { Phone.DISPLAY_NAME, Phone.NUMBER };
		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				HashMap<String, String> hash = new HashMap<String, String>();
				// 得到手机号码
				String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER));
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.DISPLAY_NAME));

				hash.put(Phone.NUMBER, phoneNumber);
				hash.put(Phone.DISPLAY_NAME, contactName);

				result.add(hash);
			}
			phoneCursor.close();
		}
		return result;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

		String number = mPhoneContacks.get(position).get(Phone.NUMBER);

		number = CommUtil.removeNoNecessaryWordsFromPhoneNumber(number);

		if (mCallback != null)
			mCallback.callback(number);
		dismiss();
	}

	public interface ContacksCallBack {
		public void callback(String phoneNunber);
	}
}
