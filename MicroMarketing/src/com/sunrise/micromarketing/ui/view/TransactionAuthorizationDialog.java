package com.sunrise.micromarketing.ui.view;

import com.sunrise.javascript.utils.CommonUtils;
import com.sunrise.micromarketing.R;
import com.sunrise.micromarketing.task.CheckValidateCodeTask;
import com.sunrise.micromarketing.task.GenericTask;
import com.sunrise.micromarketing.task.SendValidateCodeTask;
import com.sunrise.micromarketing.task.TaskListener;
import com.sunrise.micromarketing.task.TaskResult;
import com.sunrise.micromarketing.utils.CommUtil;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 业务授权对话框
 * 
 * @author fuheng
 * 
 */
public class TransactionAuthorizationDialog extends DefaultDialog implements android.view.View.OnClickListener, TaskListener {
	private EditText mMessageView;
	private EditText mAuthCode;
	private View mTextCheckCode;
	private TextView mTitleView;

	private Button mCancelButton;
	private Button mConfirmButton;

	private OnConfirmListener mOnConfirmListener;
	private android.content.DialogInterface.OnClickListener mOnCancelListener;
	private ImageView mBtDel1,mBtDel2;
	private Toast mToast;
	private GenericTask mTask;

	private String businessId;
	private String authenticationID;

	public TransactionAuthorizationDialog(Context context, String businessId, String authenticationID) {
		super(context, R.layout.dialog_transaction_authorization_layout);

		this.businessId = businessId;
		this.authenticationID = authenticationID;

		mTitleView = (TextView) findViewById(R.id.dialog_title);

		mMessageView = (EditText) findViewById(R.id.dialog_message);

		mCancelButton = (Button) findViewById(R.id.cancel);
		mCancelButton.setOnClickListener(this);
		mConfirmButton = ((Button) (findViewById(R.id.confirm)));
		mConfirmButton.setOnClickListener(this);

		mTextCheckCode = findViewById(R.id.textCheckCode);
		mTextCheckCode.setOnClickListener(this);

		mBtDel1 = (ImageView) findViewById(R.id.btn_delete1);
		mBtDel1.setOnClickListener(this);
		mMessageView.addTextChangedListener(new EditTextWacher(mBtDel1));

		// 验证码输入框初始化
		mAuthCode = (EditText) findViewById(R.id.editText_checkcode);
		mBtDel2 = (ImageView) findViewById(R.id.btn_delete2);
		mBtDel2.setOnClickListener(this);
		mAuthCode.addTextChangedListener(new EditTextWacher(mBtDel2));
		
	}

	protected void onStop() {
		super.onStop();
		cancelTask();
	}

	private void cancelTask() {
		if (mTask != null)
			mTask.cancle();
	}

	public TransactionAuthorizationDialog setDilaogTitle(CharSequence text) {
		mTitleView.setText(text);
		mTitleView.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
		return this;
	}

	public TransactionAuthorizationDialog setInputFieldHint(CharSequence text) {
		mMessageView.setHint(text);
		return this;
	}

	public TransactionAuthorizationDialog setPhoneNumber(CharSequence phoneNum){
		mMessageView.setText(phoneNum);
		return this;
	}
	
	public TransactionAuthorizationDialog setInputFieldSingleLine(boolean singleLine) {
		mMessageView.setSingleLine(singleLine);
		return this;
	}

	public TransactionAuthorizationDialog setInputType(int type) {
		mMessageView.setInputType(type);
		return this;
	}

	public TransactionAuthorizationDialog setConfirmText(CharSequence text) {
		mConfirmButton.setText(text);
		return this;
	}

	public TransactionAuthorizationDialog setCancelText(CharSequence text) {
		mCancelButton.setText(text);
		return this;
	}

	public TransactionAuthorizationDialog setOnConfirmListener(OnConfirmListener listener) {
		mOnConfirmListener = listener;
		return this;
	}

	public TransactionAuthorizationDialog setOnCancelListener(android.content.DialogInterface.OnClickListener listener) {
		mOnCancelListener = listener;
		return this;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.cancel:
			if (mOnCancelListener != null)
				mOnCancelListener.onClick(this, R.id.confirm);
			dismiss();
			break;
		case R.id.confirm:
			if (mOnConfirmListener != null) {
				confirm();
			} else
				dismiss();
			break;
		case R.id.textCheckCode:
			checkCode();
			break;
		case R.id.btn_delete1:
			mMessageView.setText(null);
			break;
		case R.id.btn_delete2:
			mAuthCode.setText(null);
			break;
		default:
			break;
		}
	}

	private void confirm() {

		String mobile = mMessageView.getText().toString();
		if (!CommonUtils.isMobilePhone(mobile)) {
			showToast(getContext().getResources().getString(R.string.inputCorrectMobileNumber));
			return;
		}
		String checkCode = mAuthCode.getText().toString();
		if (TextUtils.isEmpty(checkCode)) {
			showToast(getContext().getResources().getString(R.string.inputValidateCode));
			return;
		}
		// TODO 验证号码和验证码接口
		mTask = new CheckValidateCodeTask().execute(mobile, checkCode, businessId, authenticationID, this);
	}

	private void checkCode() {
		String mobile = mMessageView.getText().toString();
		if (!CommonUtils.isMobilePhone(mobile)) {
			showToast(getContext().getResources().getString(R.string.inputCorrectMobileNumber));
			return;
		}

		// TODO 访问接口获取验证码
		mTask = new SendValidateCodeTask().execute(mobile, businessId, authenticationID, this);
	}

	private void showToast(String content) {
		if (mToast == null)
			mToast = Toast.makeText(getContext(), content, Toast.LENGTH_SHORT);
		else
			mToast.setText(content);

		mToast.show();
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {
		if (task instanceof SendValidateCodeTask)
			mTextCheckCode.setEnabled(false);

	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		if (task instanceof SendValidateCodeTask)
			mTextCheckCode.setEnabled(true);

		if (result != TaskResult.OK) {
			dismiss();
			CommUtil.showAlert(getContext(), null, task.getException().getMessage(), null);
			return;
		}
		
		if(task instanceof CheckValidateCodeTask){
			showToast(getContext().getResources().getString(R.string.checkValidateSusscess));
			mOnConfirmListener.onClick(this, mMessageView.getText().toString(), R.id.confirm);
		}
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (task instanceof SendValidateCodeTask)
			showToast((String) param);
	}

	@Override
	public void onCancelled(GenericTask task) {

	}
}
