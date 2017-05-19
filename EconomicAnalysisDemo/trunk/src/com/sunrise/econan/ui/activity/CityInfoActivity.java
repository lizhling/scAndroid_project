package com.sunrise.econan.ui.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.PopupWindow;

import com.sunrise.econan.listener.ClickAnimationListener;
import com.sunrise.econan.listener.OnClickAnimationListener;
import com.sunrise.econan.model.CityInfo;
import com.sunrise.econan.task.CityInfoAnalysisTask;
import com.sunrise.econan.task.GenericTask;
import com.sunrise.econan.task.TaskListener;
import com.sunrise.econan.task.TaskParams;
import com.sunrise.econan.task.TaskResult;
import com.sunrise.econan.R;
import com.sunrise.econan.adapter.CityGalleryAdapter;

/**
 * 城市信息列表
 * 
 * @author fuheng
 * 
 */
public class CityInfoActivity extends Activity implements OnClickListener,
		OnTouchListener, OnItemClickListener {

	private GridView gridview;

	private PopupWindow popupMenubar;

	/**
	 * 城市信息列表
	 */
	protected ArrayList<CityInfo> array_cityinfo;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = LayoutInflater.from(getThis()).inflate(
				R.layout.cityinfo_layout, null);
		view.setOnTouchListener(getThis());
		setContentView(view);
		initView();
	}

	public void onDestroy() {
		hidePopToolbar();
		super.onDestroy();
	}

	private CityInfoActivity getThis() {
		return this;
	}

	private void initView() {
		// 初始化城市列表
		initGridView();

		// 添加返回按钮监听
		OnClickAnimationListener.initAnimationClickListener(getThis(),
				R.id.imageView_refresh, R.anim.click_scale, getThis());

		// 初始化底部菜单
		initPopupMenu();
	}

	private void initGridView() {
		gridview = (GridView) findViewById(R.id.gridView1);
		gridview.setOnTouchListener(getThis());
		gridview.setOnItemClickListener(getThis());
		CityInfoAnalysisTask task = new CityInfoAnalysisTask();
		task.setListener(new TaskListener() {

			@Override
			public void onProgressUpdate(GenericTask task, Object param) {
			}

			@Override
			public void onPreExecute(GenericTask task) {
			}

			@Override
			public void onPostExecute(GenericTask task, TaskResult result) {
				if (result == TaskResult.OK) {
					CityInfoAnalysisTask trueTask = (CityInfoAnalysisTask) task;
					array_cityinfo = trueTask.getArraylist();
					CityGalleryAdapter adapter = new CityGalleryAdapter(
							getThis(), array_cityinfo);
					adapter.setEyeClickListener(eyeClickListener);
					gridview.setAdapter(adapter);
				}
			}

			@Override
			public void onCancelled(GenericTask task) {
			}

			@Override
			public String getName() {
				return null;
			}
		});
		TaskParams params = new TaskParams();
		params.put(CityInfoAnalysisTask.KEY_CONTEXT, getThis());
		params.put(CityInfoAnalysisTask.KEY_URL, "cityinfo.xml");
		task.execute(params);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.imageView_refresh: {

		}
			break;

		default:
			break;
		}
		hidePopToolbar();
	}

	/**
	 * 选中gridview中的眼睛进行的操作
	 */
	private OnClickListener eyeClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO 选中gridview中的眼睛进行的操作
			Intent intent = new Intent(getThis(), StackAnalysisActivity.class);
			Bundle bundler = new Bundle();
			bundler.putParcelableArrayList("data", array_cityinfo);
			intent.putExtras(bundler);
			getThis().startActivity(intent);
		}
	};

	public void onBackPressed() {
		if (popupMenubar.isShowing())
			hidePopToolbar();
		else
			finish();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (popupMenubar.isShowing())
			hidePopToolbar();
		return false;
	}

	/**
	 * 底部工具栏显示与消失
	 */
	private void onPopupmenuAction() {
		if (popupMenubar.isShowing())
			hidePopToolbar();
		else
			showPopToolbar();
	}

	private void initPopupMenu() {
		View contentView = getLayoutInflater().inflate(R.layout.popup_toolbar,
				null);
		popupMenubar = new PopupWindow(contentView, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		OnClickAnimationListener.initAnimationClickListener(contentView,
				R.id.imageView_qxzc, R.anim.click_scale, getThis());
		OnClickAnimationListener.initAnimationClickListener(contentView,
				R.id.imageView_channel, R.anim.click_scale, getThis());
		OnClickAnimationListener.initAnimationClickListener(contentView,
				R.id.imageView_client, R.anim.click_scale, getThis());
		OnClickAnimationListener.initAnimationClickListener(contentView,
				R.id.imageView_indicatortool, R.anim.click_scale, getThis());
		OnClickAnimationListener.initAnimationClickListener(contentView,
				R.id.imageView_marketing, R.anim.click_scale, getThis());
		OnClickAnimationListener.initAnimationClickListener(contentView,
				R.id.imageView_yyjkzx, R.anim.click_scale, getThis());
	}

	// 控制条的显示与消失
	private void showPopToolbar() {
		// popupMenubar.setAnimationStyle(0);
		popupMenubar.showAtLocation(gridview, Gravity.BOTTOM, 0, 0);
	}

	private void hidePopToolbar() {
		if (popupMenubar.isShowing())
			popupMenubar.dismiss();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("");
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onMenuOpened(int featureId, Menu menu) {
		onPopupmenuAction();
		return false;// true--显示系统自带菜单；false--不显示。
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {
		// TODO 选中gridview中的选项进行的操作
		new ClickAnimationListener(view, R.anim.click_scale, null)
				.startAnimation();
	}
}
