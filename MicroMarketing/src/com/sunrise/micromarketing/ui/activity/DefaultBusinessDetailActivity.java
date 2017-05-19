package com.sunrise.micromarketing.ui.activity;

import com.sunrise.micromarketing.ExtraKeyConstant;
import com.sunrise.micromarketing.R;
import com.sunrise.micromarketing.entity.BusinessMenu;
import com.sunrise.micromarketing.task.GenericTask;
import com.sunrise.micromarketing.task.GetBusinessMenuDataTask;
import com.sunrise.micromarketing.task.HandleBusinessTask;
import com.sunrise.micromarketing.task.TaskListener;
import com.sunrise.micromarketing.task.TaskResult;
import com.sunrise.micromarketing.ui.view.TransactionAuthorizationDialog;
import com.sunrise.micromarketing.ui.view.TwoButtonDialog;
import com.sunrise.micromarketing.utils.CommUtil;
import com.sunrise.micromarketing.utils.ZipAndBaseUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * @author fuheng
 */
public class DefaultBusinessDetailActivity extends BaseActivity implements OnClickListener, TaskListener, OnCheckedChangeListener {
	protected BusinessMenu mBusinessMenu;
	private GenericTask mTask;
	private GetBusinessMenuDataTask mGetShareTask;

	private TwoButtonDialog mDialogCertain;
	private String prodPrcid = null;
	private String mShareUrl = null;

	private final String KEY_SHARE_URL = "share url";

	private final String FORMAT_OPEN_UP = "是否办理：<font color=\"%d\">%s?</font>\n资费标准：%s";
	private final String FORMAT_EXIT = "是否退订：<font color=\"%d\">%s?</font>\n资费标准：%s";
	private final String FORMAT_WELCOME = "欢迎您！<font color=\"%d\">%s</font>\n用户";

	/**
	 * 同意办理
	 */
	private OnClickListener mOnClickListenerForHanlleBusiness = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mDialogCertain.dismiss();
			getAuthorization();
		}
	};
	/**
	 * 取消操作
	 */
	private OnClickListener mOnClickListenerForCancle = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mDialogCertain.dismiss();
		}
	};

	/**
	 * 获取验证操作
	 */
	private TransactionAuthorizationDialog.OnConfirmListener mOnClickListenerForAuthorization = new TransactionAuthorizationDialog.OnConfirmListener() {

		@Override
		public void onClick(DialogInterface dialog, String inputText, int which) {
			dialog.dismiss();
			doBusinessHandle(mBusinessMenu, inputText);
		}
	};

	private View mBtShare;
	private RadioGroup mTabWidget;
	private ViewSwitcher mViewSwitcher;
	private TextView mTextViewWarmNotice;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null)
			savedInstanceState = getIntent().getExtras();

		if (savedInstanceState != null) {
			this.mBusinessMenu = (BusinessMenu) savedInstanceState.get(Intent.EXTRA_SUBJECT);
			if (savedInstanceState.containsKey(KEY_SHARE_URL))
				this.mShareUrl = savedInstanceState.getString(KEY_SHARE_URL);
		}

		if (TextUtils.isEmpty(this.mShareUrl))// 访问接口获取分享内容信息
			prepareShare();

		setContentView(R.layout.layout_business_deal);
		initView();

		if (getIntent().getStringExtra(Intent.EXTRA_PHONE_NUMBER) != null)// TODO
																			// 推荐业务直接弹出号码授权对话框
			getAuthorization();
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(Intent.EXTRA_SUBJECT, mBusinessMenu);
		outState.putString(KEY_SHARE_URL, this.mShareUrl);
		super.onSaveInstanceState(outState);
	}

	protected void initView() {
		if (mBusinessMenu == null)
			return;

		setTitle(R.string.business_deal);
		{
			mTabWidget = (RadioGroup) findViewById(R.id.tabs);
			mTabWidget.setOnCheckedChangeListener(this);
		}
		{
			mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher01);
		}
		{
			TextView textview = (TextView) findViewById(R.id.text_businessTitle);
			textview.setText(mBusinessMenu.getName());
		}
		{
			TextView textview = (TextView) findViewById(R.id.text_id);
			textview.setText(Html.fromHtml(String.format(FORMAT_WELCOME, getResources().getColor(R.color.blue), getPreferences().getSubAccount())));
		}
		{
			TextView textview_showHtml = (TextView) findViewById(R.id.textView_showHtml);
			String description = mBusinessMenu.getDescription();
			if (description != null)
				textview_showHtml.setText(description);
		}
		{
			TextView textView_charge = (TextView) findViewById(R.id.textView_charge);
			String charge = mBusinessMenu.getCharges();
			if (charge != null)
				textView_charge.setText(charge);
		}
		{
			mTextViewWarmNotice = (TextView) findViewById(R.id.textView_warmNotice);
		}

		setTitleBarLeftClick(new OnClickListener() {
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		setTitleBarRightClick(null);

		mBtShare = findViewById(R.id.share);// 分享按钮
		mBtShare.setOnClickListener(this);
		if (TextUtils.isEmpty(this.mShareUrl))// 根据分享内容是否存在，显示/隐藏 按钮
			mBtShare.setVisibility(View.GONE);
		else
			mBtShare.setVisibility(View.VISIBLE);

		findViewById(R.id.submit).setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void onStart() {
		super.onStart();
	}

	public void onStop() {
		super.onStop();
		doCancelTask();
	}

	private void doCancelTask() {
		if (mTask != null && mTask.getStatus() != GenericTask.Status.FINISHED)
			mTask.cancle();
	}

	/**
	 * 处理业务
	 * 
	 * @param item
	 * @param phoneNo
	 */
	private void doBusinessHandle(BusinessMenu item, String phoneNo) {
		if (item == null)
			return;

		doCancelTask();
		prodPrcid = item.getProdPrcid();
		mTask = new HandleBusinessTask().execute(item, phoneNo, getPreferences().getAuthentication(), this);
	}

	/**
	 * 获取授权
	 */
	private void getAuthorization() {
		new TransactionAuthorizationDialog(this, String.valueOf(mBusinessMenu.getId()), getPreferences().getAuthentication())
				.setDilaogTitle(mBusinessMenu.getName()).setPhoneNumber(getIntent().getStringExtra(Intent.EXTRA_PHONE_NUMBER)).setConfirmText("验证")
				.setOnConfirmListener(mOnClickListenerForAuthorization).setOnCancelListener(new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						doCancelTask();
					}
				}).show();
	}

	@Override
	public void onClick(View arg0) {
		// if (mDialogCertain == null) {
		// mDialogCertain = new TwoButtonDialog(this,
		// mOnClickListenerForHanlleBusiness, mOnClickListenerForCancle);
		//
		// mDialogCertain.setMessage(Html.fromHtml(String.format(FORMAT_OPEN_UP,
		// getResources().getColor(R.color.blue), mBusinessMenu.getName(),
		// mBusinessMenu.getCharges())));
		// }
		// mDialogCertain.show();
		switch (arg0.getId()) {
		case R.id.share:
			startShare();
			break;
		case R.id.submit:
			getAuthorization();
			break;
		default:
			break;
		}
	}

	/**
	 * 准备分享信息
	 */
	private void prepareShare() {
		mGetShareTask = new GetBusinessMenuDataTask();
		mGetShareTask.execute(String.valueOf(mBusinessMenu.getId()), getPreferences().getSubAccount(), new TaskListener() {

			@Override
			public void onProgressUpdate(GenericTask task, Object param) {
				String url = (String) param;
				// String url
				// ="http://218.205.252.26:28081/scWyxAppManagePlatform/w/c/i.html?bid=242&bNo=B008";

				if (TextUtils.isEmpty(url)) {
					mBtShare.setVisibility(View.GONE);
					return;
				}

				if (url.contains("&")) {
					String data = ZipAndBaseUtils.encodeBase64(url.substring(url.lastIndexOf('&') + 1));
					// Toast.makeText(DefaultBusinessDetailActivity.this,
					// "data:"+data, Toast.LENGTH_SHORT).show();
					url = url.substring(0, url.lastIndexOf("&")) + "&" + data;
					// Toast.makeText(DefaultBusinessDetailActivity.this,
					// "url:"+url, Toast.LENGTH_SHORT).show();

				}

				mBtShare.setVisibility(View.VISIBLE);

				mShareUrl = url;
			}

			@Override
			public void onPreExecute(GenericTask task) {

			}

			@Override
			public void onPostExecute(GenericTask task, TaskResult result) {
				if (result != TaskResult.OK)
					CommUtil.showAlert(getThis(), null, "分享失败，请重试……", null);
			}

			@Override
			public void onCancelled(GenericTask task) {

			}

			@Override
			public String getName() {
				return null;
			}
		});
	}

	private void startShare() {
		Intent intent = new Intent(getThis(), FriendsShareActivity.class);
		intent.putExtra(Intent.EXTRA_TEXT, mShareUrl);
		intent.putExtra("type", "0");
		intent.putExtra("businessName", mBusinessMenu.getName());
		intent.putExtra(Intent.EXTRA_TITLE, "分享业务：\n" + mBusinessMenu.getName());
		// System.out.println("param:"+param);
		startActivity(intent);
	}

	@Override
	public String getName() {
		return "业务办理";
	}

	@Override
	public void onPreExecute(GenericTask task) {
		initDialog(false, false, null);
		showDialog(getName());
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		dismissDialog();

		if (result == TaskResult.OK) {

		} else {
			if (task.getException() != null && task.getException().getMessage() != null) {
				String strMsg = task.getException().getMessage();
				strMsg = strMsg.replaceAll("\\[[a-z0-9A-Z]+\\$\\$[a-z0-9A-Z]+\\]", "");
				strMsg = strMsg.replaceAll("\\[[a-z0-9A-Z]+~[a-z0-9A-Z]+\\]", "");
				strMsg = strMsg.replaceAll("~", ",");
				if (prodPrcid != null) {
					prodPrcid = prodPrcid.replaceAll("\\$", "\\\\\\$");
					strMsg = strMsg.replaceAll("\\[" + prodPrcid + "\\]", "");
					strMsg = strMsg.replaceAll("【" + prodPrcid + "】", "");
					if (prodPrcid.indexOf(',') > -1) {
						String prcidArray[] = prodPrcid.split(",");
						if (prcidArray != null && prcidArray.length > 0) {
							for (int i = 0; i < prcidArray.length; i++) {
								strMsg = strMsg.replaceAll("\\[" + prcidArray[i] + "\\]", "");
								strMsg = strMsg.replaceAll("【" + prcidArray[i] + "】", "");
							}
						}
					}

				}
				CommUtil.showAlert(this, "业务办理失败", strMsg, null);
				prodPrcid = null;
			}
		}
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		// 启动刷新用户已办业务的线程
		// HandleBusinessTask tTask = (HandleBusinessTask) task;
		String title = mBusinessMenu.getName() + " 办理成功";
		String message = title;
		if (CommUtil.isContainChinese((String) param)) {
			message = (String) param;
		} else {
			title = null;
		}
		String strMsg = message;
		strMsg = strMsg.replaceAll("\\[[a-z0-9A-Z]+\\$\\$[a-z0-9A-Z]+\\]", "");
		strMsg = strMsg.replaceAll("\\[[a-z0-9A-Z]+~[a-z0-9A-Z]+\\]", "");
		strMsg = strMsg.replaceAll("~", ",");
		if (prodPrcid != null && strMsg != null) {
			prodPrcid = prodPrcid.replaceAll("\\$", "\\\\\\$");
			strMsg = strMsg.replaceAll("\\[" + prodPrcid + "\\]", "");
			strMsg = strMsg.replaceAll("【" + prodPrcid + "】", "");
			if (prodPrcid.indexOf(',') > -1) {
				String prcidArray[] = prodPrcid.split(",");
				if (prcidArray != null && prcidArray.length > 0) {
					for (int i = 0; i < prcidArray.length; i++) {
						strMsg = strMsg.replaceAll("\\[" + prcidArray[i] + "\\]", "");
						strMsg = strMsg.replaceAll("【" + prcidArray[i] + "】", "");
					}
				}
			}

		}
		CommUtil.showAlert(this, title, strMsg, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		prodPrcid = null;
	}

	@Override
	public void onCancelled(GenericTask task) {
		dismissDialog();
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int id) {
		int index = arg0.indexOfChild(arg0.findViewById(id));

		if (index == 0) {
			// mViewSwitcher.setInAnimation(this, R.anim.slide_left_in);
			// mViewSwitcher.setOutAnimation(this, R.anim.slide_right_out);
			cleanWarmPrompt();
		} else {
			// mViewSwitcher.setInAnimation(this, R.anim.slide_right_in);
			// mViewSwitcher.setOutAnimation(this, R.anim.slide_left_out);
			setWarmPrompt();
		}

		mViewSwitcher.setDisplayedChild(index);

	}

	private void setWarmPrompt() {
		String warm = mBusinessMenu.getWarmPrompt();
		if (warm != null)
			mTextViewWarmNotice.setText(warm);
	}

	private void cleanWarmPrompt() {
		mTextViewWarmNotice.setText(null);
	}
}
