package com.sunrise.scmbhc.ui.activity;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.adapter.ViewPagerAdapter;
import com.sunrise.scmbhc.utils.FileUtils;

/**
 * 
 * @Project: scmbhc
 * @ClassName: GuideActivity
 * @Description: 显示启动是帮助指引
 * @Author qinhubao
 * @CreateFileTime: 2014年6月25日 下午5:15:52
 * @Modifier: qinhubao
 * @ModifyTime: 2014年6月25日 下午5:15:52
 * @ModifyNote:
 * @version
 * 
 */
public class GuideActivity extends Activity {

	private final int[] sFunctionGuideDrawableIds = { R.drawable.function_guide_1, R.drawable.function_guide_2 };
	private final int[] sOpreGuideDrawableIds = { R.drawable.opre_guide_1, R.drawable.opre_guide_2, R.drawable.opre_guide_3 };

	private ArrayList<View> mViews = new ArrayList<View>();
	private ViewPager mGuidePager;
	private ArrayList<Drawable> mGuideDrawabels = new ArrayList<Drawable>();
	public final static int AFTER_START_DISPLAY_GUIDE_TYPE = 1;
	public final static int DISPLAY_FUNCTION_GUIDE_TYPE = 2;
	public final static int DISPLAY_BUSINESS_GUIDE_TYPE = 3;
	public final static int DISPLAY_OPER_GUIDE_TYPE = 4;
	private int mDisplayGuideType;
	private boolean mIsBusinessGuideUpdated;
	private Button mExperienceButton;
	private LinearLayout mGuidePanel;

	private ArrayList<ImageView> mIndexViews = new ArrayList<ImageView>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide);
		Intent intent = getIntent();
		// 获取传来的显示方式
		mDisplayGuideType = intent.getIntExtra(App.ExtraKeyConstant.KEY_DISPLAY_GUIDE_TYPE, DISPLAY_FUNCTION_GUIDE_TYPE);
		mIsBusinessGuideUpdated = intent.getBooleanExtra(App.ExtraKeyConstant.KEY_IS_BUSINESS_GUIDES_UPDATED, false);
		mGuideDrawabels.clear();
		mViews.clear();
		// 初始化启动显示的图片
		initDrawables();
		for (Drawable drawable : mGuideDrawabels) {
			ImageView imageView = new ImageView(this);
			imageView.setBackgroundDrawable(drawable);
			// 全屏显示图片
			imageView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			mViews.add(imageView);
		}
		mGuidePanel = (LinearLayout) findViewById(R.id.guide_panel);
		mExperienceButton = (Button) findViewById(R.id.exp_button);
		mExperienceButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UserInfoControler.getInstance().setNeedGuide(false);
				Intent intent = new Intent(GuideActivity.this, MainActivity.class);
				intent.putExtras(getIntent());
				startActivity(intent);
				finish();
			}
		});
		mGuidePager = (ViewPager) findViewById(R.id.guide_pager);
		mGuidePager.setAdapter(new ViewPagerAdapter(mViews));
		mGuidePager.setCurrentItem(0);
		mGuidePager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				if (mDisplayGuideType == AFTER_START_DISPLAY_GUIDE_TYPE || mIsBusinessGuideUpdated) {
					if (position == mGuideDrawabels.size() - 1) {
						mExperienceButton.setVisibility(View.VISIBLE);
					} else {
						mExperienceButton.setVisibility(View.INVISIBLE);
					}
				}
				if (mGuideDrawabels.size() > 1)
					updateGuideIndex(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		initGuideIndex();
		if (mIsBusinessGuideUpdated) {
			if (mGuideDrawabels.size() == 1) {
				mExperienceButton.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void initDrawables() {
		switch (mDisplayGuideType) {
		case AFTER_START_DISPLAY_GUIDE_TYPE:
			loadFunctionGuideDrawables(mGuideDrawabels);// mGuideDrawabels.addAll(App.getFunctionGuideDrawables());
			loadBusinessGuideDrawables(mGuideDrawabels);// mGuideDrawabels.addAll(App.getBusinessGuideDrawables());
			loadOpreGuideDrawables(mGuideDrawabels);// mGuideDrawabels.addAll(App.getOpreGuideDrawables());
			break;
		case DISPLAY_FUNCTION_GUIDE_TYPE:
			loadFunctionGuideDrawables(mGuideDrawabels);// mGuideDrawabels =
														// App.getFunctionGuideDrawables();
			break;
		case DISPLAY_BUSINESS_GUIDE_TYPE:
			loadBusinessGuideDrawables(mGuideDrawabels);// mGuideDrawabels =
														// App.getBusinessGuideDrawables();
			break;
		case DISPLAY_OPER_GUIDE_TYPE:
			loadOpreGuideDrawables(mGuideDrawabels);// mGuideDrawabels =
			break;
		}
	}

	private void initGuideIndex() {
		int size = mGuideDrawabels.size();
		if (size > 1) {
			for (int i = 0; i < size; i++) {
				ImageView view = new ImageView(this);
				view.setImageResource(R.drawable.guide_index_focus);
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				view.setLayoutParams(layoutParams);
				mGuidePanel.addView(view);
				mIndexViews.add(view);
			}
			updateGuideIndex(0);
		}
	}

	private void updateGuideIndex(int index) {
		int size = mGuideDrawabels.size();
		for (int i = 0; i < size; i++) {
			ImageView view = mIndexViews.get(i);
			if (i == index) {
				view.setImageResource(R.drawable.guide_index_focus);
			} else {
				view.setImageResource(R.drawable.guide_index_normal);
			}
		}
	}

	private void loadFunctionGuideDrawables(ArrayList<Drawable> array) {

		if (array == null)
			return;

		for (int i = 0; i < sFunctionGuideDrawableIds.length; i++) {
			Drawable drawable = getResources().getDrawable(sFunctionGuideDrawableIds[i]);
			array.add(drawable);
		}
	}

	private void loadOpreGuideDrawables(ArrayList<Drawable> array) {

		if (array == null)
			return;

		for (int i = 0; i < sOpreGuideDrawableIds.length; i++) {
			Drawable drawable = getResources().getDrawable(sOpreGuideDrawableIds[i]);
			array.add(drawable);
		}
	}

	private void loadBusinessGuideDrawables(ArrayList<Drawable> array) {

		if (array == null)
			return;

		ArrayList<File> files = FileUtils.getFileListOfDir(App.AppDirConstant.APP_BUSINESS_GUIDE_DIR);

		for (int i = 0; i < files.size(); i++) {
			Bitmap bitmap = BitmapFactory.decodeFile(files.get(i).getAbsolutePath());
			BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
			array.add(bitmapDrawable);
		}
	}

}
