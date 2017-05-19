package com.starcpt.cmuc.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Gallery.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.exception.business.BusinessException;
import com.starcpt.cmuc.exception.http.HttpException;
import com.starcpt.cmuc.model.bean.ResultBean;
import com.starcpt.cmuc.task.GenericTask;
import com.starcpt.cmuc.task.TaskListener;
import com.starcpt.cmuc.task.TaskParams;
import com.starcpt.cmuc.task.TaskResult;
import com.starcpt.cmuc.ui.activity.CommonActions;

public class BindIMEIDialog extends Dialog implements
		android.view.View.OnClickListener, TaskListener {
	private EditText mAccountsView;

	private Context mContext;

	private TextView mBtnOK;

	private String IMEI;

	public ResultBean mResult;
	
	private TextView title;
	
	public BindIMEIDialog(Context context, String imei, String oneAccount,boolean isFromGetDynamicCode) {/*add for request id RA-IR-0018: 优化imei号绑定流程 	by liuyitian*/
		super(context, R.style.dialog);
		mContext = context;
		this.IMEI = imei;
		this.setContentView(R.layout.binding_imei);
		/* add for request id RA-IR-0018: 优化imei号绑定流程 by liuyitian Start */
		title = (TextView) findViewById(R.id.tvTitle);
		setTitle(isFromGetDynamicCode);
		TextView mIMEI = (TextView) findViewById(R.id.phone_imei_textView);
		mIMEI.setText(imei);
		/* add for request id RA-IR-0018: 优化imei号绑定流程 by liuyitian End */
		mBtnOK = (TextView) findViewById(R.id.btnOK);
		mBtnOK.setOnClickListener(this);
		findViewById(R.id.btnBACK).setOnClickListener(this);
		mAccountsView = (EditText) findViewById(R.id.accounts_4a);
		mAccountsView.addTextChangedListener(new InputTextWatcher());
		/* add for request id RA-IR-0018: 优化imei号绑定流程 by liuyitian Start */
		TextView helpView = (TextView) findViewById(R.id.help);
//		helpView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		helpView.setText(Html.fromHtml("<u>"+mContext.getString(R.string.why_to_bind)+"</u>"));
		helpView.setOnClickListener(this);
		/* add for request id RA-IR-0018: 优化imei号绑定流程 by liuyitian End */
		setAccount(oneAccount);
		setCancelable(false);
	}

	/*add for request id RA-IR-0018: 优化imei号绑定流程 	by liuyitian Start*/
	public void setTitle(boolean isFromGetDynamicCode) {
		if (isFromGetDynamicCode) {
			title.setText(R.string.alert_bind_phone_imsi);
		} else {
			title.setText(R.string.bind_phone_imsi);
		}
	}
	/*add for request id RA-IR-0018: 优化imei号绑定流程 	by liuyitian End*/

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnOK:
			commit();
			break;
		case R.id.btnBACK:
			dismiss();
			break;
		/*add for request id RA-IR-0018: 优化imei号绑定流程 	by liuyitian Start*/
		case R.id.help:
			showHelpInfo(v);
			break;
		/*add for request id RA-IR-0018: 优化imei号绑定流程 	by liuyitian End*/
		default:
			break;
		}
	}
	
	/*add for request id RA-IR-0018: 优化imei号绑定流程 	by liuyitian Start*/
	/**
	 * 弹出"为什么要进行绑定工作内容"
	 * @param v
	 */
	public void showHelpInfo(View v) {
		PopupWindow pupupwindow = null;
		String content = mContext.getString(R.string.bind_help);
		if (content != null) {
			TextView view = new TextView(v.getContext());
			view.setText(content);
			view.setBackgroundResource(R.drawable.op_bookmark_bg);
			view.setTextColor(Color.WHITE);
			pupupwindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			pupupwindow.setBackgroundDrawable(new ColorDrawable(0));
			pupupwindow.setOutsideTouchable(true);
			pupupwindow.setFocusable(true);
		}
		pupupwindow.showAtLocation(
				(ViewGroup) (findViewById(R.id.login_status).getParent()),
				Gravity.BOTTOM, 0, 20);
	}
	/*add for request id RA-IR-0018: 优化imei号绑定流程 	by liuyitian End*/

	private void commit() {
		BindingTask task = new BindingTask();
		task.setListener(this);
		task.execute();
		dismiss();
	}

	private class InputTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable arg0) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int arg1, int arg2,
				int arg3) {

		}

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
			if (TextUtils.isEmpty(s))
				disableOkButton();
			else
				enabelOkButton();

		}
	}

	private void disableOkButton() {
		mBtnOK.setTextColor(Color.DKGRAY);
		mBtnOK.setBackgroundResource(R.drawable.grey_button_bg_normal);
		mBtnOK.setEnabled(false);
	}

	private void enabelOkButton() {
		mBtnOK.setTextColor(Color.WHITE);
		mBtnOK.setBackgroundResource(R.drawable.green_button_bg);
		mBtnOK.setEnabled(true);
	}

	class BindingTask extends GenericTask {

		@Override
		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				mResult = CmucApplication.sServerClient.bindingIMEI(IMEI,
						mAccountsView.getText().toString());
			} catch (HttpException e) {
				e.printStackTrace();
				return TaskResult.FAILED;
			} catch (BusinessException e) {
				e.printStackTrace();
				return TaskResult.FAILED;
			}
			return TaskResult.OK;
		}

	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, mContext.getString(R.string.requesting_bind), Toast.LENGTH_SHORT).show();/*add for request id RA-IR-0018: 优化imei号绑定流程 	by liuyitian*/
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		if (result == TaskResult.OK) {
			String resultMsg = "无返回结果";
			/*add for request id RA-IR-0018: 优化imei号绑定流程 	by liuyitian Start*/
			if (mResult.getResultMesage() != null) {
				resultMsg = mResult.getResultMesage().replace(";", ";\r\n\r\n");
			}
			CommonActions.createSingleBtnMsgDialog(mContext, mContext.getString(R.string.send_request_bind_success), resultMsg, mContext.getString(R.string.continu),
					new CommonActions.OnTwoBtnDialogHandler() {
						@Override
						public void onPositiveHandle(Dialog dialog, View v) {
							dialog.dismiss();
						}

						@Override
						public void onNegativeHandle(Dialog dialog, View v) {}
					});

		} else {
			Toast.makeText(mContext, mContext.getString(R.string.send_request_bind_fail), Toast.LENGTH_SHORT).show();
		}
		/*add for request id RA-IR-0018: 优化imei号绑定流程 	by liuyitian End*/
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCancelled(GenericTask task) {
		// TODO Auto-generated method stub

	}

	public void setAccount(String account) {
		mAccountsView.setText(account);
	}

}
