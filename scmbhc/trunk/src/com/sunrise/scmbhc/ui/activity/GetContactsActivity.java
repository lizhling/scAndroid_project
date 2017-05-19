package com.sunrise.scmbhc.ui.activity;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.utils.CommUtil;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;

/**
 * 用来间接读取通讯录的无界面activity
 * 
 * @author fuheng
 * 
 */
public class GetContactsActivity extends Activity {
	public static final int REQUEST_CONTACT = 109;

	public void onStart() {
		super.onStart();
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);
		intent.setData(ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, REQUEST_CONTACT);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			setResult(resultCode);
			finish();
			return;
		}

		switch (requestCode) {
		case REQUEST_CONTACT:
			if (data == null) {
				return;
			}
			Uri result = data.getData();
			String contactId = result.getLastPathSegment();

			// 显示
			ContentResolver resolver = getContentResolver();
			String[] PHONES_PROJECTION = new String[] { Phone.DISPLAY_NAME, Phone.NUMBER };
			// 获取手机联系人
			Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, Phone.CONTACT_ID + " = ?", new String[] { contactId }, null);

			if (phoneCursor != null && phoneCursor.moveToFirst()) {
				// 得到手机号码
				String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER));
				Intent intent = new Intent();
				if (!TextUtils.isEmpty(phoneNumber))
					intent.putExtra(App.ExtraKeyConstant.KEY_PHONE_NUMBER, CommUtil.removeNoNecessaryWordsFromPhoneNumber(phoneNumber));
				setResult(resultCode, intent);
				phoneCursor.close();
			}

			break;
		default:
			setResult(resultCode);
			break;
		}
		finish();
	}
}
