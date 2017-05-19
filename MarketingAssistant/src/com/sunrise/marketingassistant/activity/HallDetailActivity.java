package com.sunrise.marketingassistant.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sunrise.businesstransaction.utils.DateUtil;
import com.sunrise.javascript.utils.UnitUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.entity.MobMenberScore;
import com.sunrise.marketingassistant.entity.MobileBusinessHall;
import com.sunrise.marketingassistant.entity.ShareOfChannels;
import com.sunrise.marketingassistant.fragment.BaseFragment;
import com.sunrise.marketingassistant.fragment.CardNoCheckFragment;
import com.sunrise.marketingassistant.fragment.MapLocationShowFragment;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.GetAppQuotaByGroupid;
import com.sunrise.marketingassistant.task.GetIndexBrothersortTask;
import com.sunrise.marketingassistant.task.GetMobMenberScoreTask;
import com.sunrise.marketingassistant.task.MobInfoByGroupIdTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import com.sunrise.marketingassistant.utils.CommUtil;
import com.sunrise.marketingassistant.view.PopupWindowPieChartViewForRateOfChannels;

public class HallDetailActivity extends SingleFragmentActivity implements OnClickListener, OnCheckedChangeListener, TaskListener {

	public static Intent createIntent(Context context, Class<? extends BaseFragment> cls, String Durl, String Dname, String DlastModify, String outPageInfo) {
		Intent intent = SingleFragmentActivity.createIntent(context, cls, Durl, Dname, DlastModify, outPageInfo);
		intent.setClass(context, HallDetailActivity.class);
		return intent;
	}

	private String mGroupId;
	private MobileBusinessHall mMobHallInfo;

	private ArrayList<GenericTask> mArrayTask;

	/** 店员积分 */
	private ArrayList<MobMenberScore> mArrayMobMenberScore;

	private ArrayList<MobileBusinessHall> mBusinessDataMonth, mBusinessDataDays;

	/** 店员人数 */
	private TextView mTV_numOfEmployees;

	/** 员工详情表格 */
	private TableLayout mTableEmployeesDetail;
	private TableLayout mTableBusinessData, mTableBusinessData2;

	// private ViewPager mViewPager;
	// private ArrayList<View> mListViews;
	private ViewFlipper mViewFlipper1;

	private RadioButton mRadio1, mRadio3, mRadio2;

	/** 线型图容器 */
	private ViewGroup mLinearLayoutData, mLinearLayoutData2;
	/** 数据切换器 */
	private ViewSwitcher mViewSwitcherData, mViewSwitcherData2;
	/** 表格和甘特图切换器 */
	private CheckBox mCheckTableOrAchart, mCheckTableOrAchart2;

	/** 饼状图容器 */
	private ViewGroup mContainterPieChart;
	/** 渠道份额信息 */
	private ArrayList<ShareOfChannels> mArrayCityAppQuota;
	/** * 近三月酬金 */
	private ViewGroup mContainterRwd;

	@SuppressLint("SimpleDateFormat")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initView();

		mArrayTask = new ArrayList<GenericTask>();
		if (savedInstanceState == null) {
			savedInstanceState = getIntent().getBundleExtra(KEY_BUNDLE);
			mGroupId = savedInstanceState.getString(KEY_CONTENT);
			doGetGroupInfo(mGroupId);
		} else {
			mGroupId = savedInstanceState.getString(KEY_CONTENT);
			mMobHallInfo = savedInstanceState.getParcelable(Intent.EXTRA_DATA_REMOVED);
			mArrayMobMenberScore = savedInstanceState.getParcelableArrayList(Intent.EXTRA_ALARM_COUNT);

			mBusinessDataMonth = savedInstanceState.getParcelableArrayList("mBusinessDataMonth");
			mBusinessDataDays = savedInstanceState.getParcelableArrayList("mBusinessDataDays");

			mArrayCityAppQuota = savedInstanceState.getParcelableArrayList("mArrayCityAppQuota");
		}

		if (mArrayMobMenberScore == null || mArrayMobMenberScore.isEmpty())
			doGetMobMenberScore();

		if (mBusinessDataDays == null || mBusinessDataDays.isEmpty())
			new GetIndexBrothersortTask().execute(mGroupId, new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis())), getResources()
					.getStringArray(R.array.indexsortValue)[2], this);
		if (mBusinessDataMonth == null || mBusinessDataMonth.isEmpty())
			new GetIndexBrothersortTask().execute(mGroupId, new SimpleDateFormat("yyyyMM").format(new Date(System.currentTimeMillis())), getResources()
					.getStringArray(R.array.indexsortValue)[2], this);

		if (mArrayCityAppQuota == null || mArrayCityAppQuota.isEmpty())
			doGetAppQuotaByGroupId(mGroupId);
	}

	public void onSaveInstanceState(Bundle outState) {

		outState.putString(KEY_CONTENT, mGroupId);
		outState.putParcelable(Intent.EXTRA_DATA_REMOVED, mMobHallInfo);
		outState.putParcelableArrayList(Intent.EXTRA_ALARM_COUNT, mArrayMobMenberScore);

		outState.putParcelableArrayList("mBusinessDataMonth", mBusinessDataMonth);
		outState.putParcelableArrayList("mBusinessDataDays", mBusinessDataDays);
		outState.putParcelableArrayList("mArrayCityAppQuota", mArrayCityAppQuota);

		super.onSaveInstanceState(outState);
	}

	public void onStart() {
		super.onStart();
	}

	public void onStop() {

		super.onStop();
	}

	public void onDestroy() {
		for (int i = 0; i < mArrayTask.size(); i++) {
			mArrayTask.get(i).cancle();
		}

		super.onDestroy();
	}

	protected void initView() {
		setContentView(R.layout.layout_hall_detail3);
		/**** homepage ****/
		{
			mRadio1 = (RadioButton) findViewById(R.id.radio0);
			mRadio1.setOnCheckedChangeListener(this);
		}
		{
			mRadio2 = (RadioButton) findViewById(R.id.radio1);
			mRadio2.setOnCheckedChangeListener(this);
		}
		{
			mRadio3 = (RadioButton) findViewById(R.id.radio2);
			mRadio3.setOnCheckedChangeListener(this);
		}

		/** page1 ***/
		mTableBusinessData = (TableLayout) findViewById(R.id.table_businessData);
		mTableBusinessData2 = (TableLayout) findViewById(R.id.table_businessData2);
		mLinearLayoutData = (ViewGroup) findViewById(R.id.linearLayout_Data);
		mLinearLayoutData2 = (ViewGroup) findViewById(R.id.linearLayout_Data2);

		mViewSwitcherData = (ViewSwitcher) findViewById(R.id.viewSwitcher1);
		mViewSwitcherData2 = (ViewSwitcher) findViewById(R.id.viewSwitcher2);
		mCheckTableOrAchart = (CheckBox) findViewById(R.id.check_table_achart);
		mCheckTableOrAchart.setOnCheckedChangeListener(this);
		mCheckTableOrAchart2 = (CheckBox) findViewById(R.id.check_table_achart2);
		mCheckTableOrAchart2.setOnCheckedChangeListener(this);

		/** page2 ***/
		mCurrentFragment = getFragment();
		mCurrentFragment.startFragment(this, R.id.fragmentContainer);
		/** page3 ***/
		mTableEmployeesDetail = (TableLayout) findViewById(R.id.table_employee);
		mTV_numOfEmployees = (TextView) findViewById(R.id.hall_numOfEmployees);
		mContainterPieChart = (ViewGroup) findViewById(R.id.container_pieChart);
		mContainterRwd = (ViewGroup) findViewById(R.id.container_rwd);

		{
			TextView textView = (TextView) findViewById(R.id.rwd_detail);
			textView.getPaint().setUnderlineText(true);
			textView.setOnClickListener(this);
		}

		// 分页
		// mViewPager = (ViewPager) findViewById(R.id.viewPager1);
		// mListViews = new ArrayList<View>();
		//
		// for (int i = 0; i < mViewPager.getChildCount(); i++) {
		// mListViews.add(mViewPager.getChildAt(i));
		// }
		//
		// mViewPager.setAdapter(new MyPagerAdapter(mListViews));
		// mViewPager.setOnPageChangeListener(this);
		mViewFlipper1 = (ViewFlipper) findViewById(R.id.viewFlipper1);
	}

	/** * 刷新员工详情 */
	private void refreshViewEmployeDetail() {
		if (mArrayMobMenberScore != null)
			mTV_numOfEmployees.setText(mArrayMobMenberScore.size() + "人");

		if (mArrayMobMenberScore != null && !mArrayMobMenberScore.isEmpty()) {
			mTableEmployeesDetail.setVisibility(View.VISIBLE);
			mTV_numOfEmployees.setText(mArrayMobMenberScore.size() + "人");
			while (mTableEmployeesDetail.getChildCount() > 1)
				mTableEmployeesDetail.removeViewAt(1);
			for (MobMenberScore item : mArrayMobMenberScore) {
				View view = LayoutInflater.from(this).inflate(R.layout.item_employee_info, null, false);
				((TextView) view.findViewById(R.id.employee_no)).setText(item.getPERSON_ID());
				((TextView) view.findViewById(R.id.employee_name)).setText(item.getPERSON_NAME());
				((TextView) view.findViewById(R.id.employee_currentScore)).setText(item.getCUR_SCORE());
				((TextView) view.findViewById(R.id.employee_totleScore)).setText(item.getTOL_SCORE());
				mTableEmployeesDetail.addView(view);
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.webview:
			gotoMapPage();
			break;

		case R.id.rwd_detail:
			startActivity(SingleFragmentActivity.createIntent(this, CardNoCheckFragment.class, mGroupId, null, null, null));
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		switch (arg0.getId()) {
		case R.id.radio0:
			if (arg1)
				goPagerSmooth(0);
			break;
		case R.id.radio1:
			if (arg1)
				goPagerSmooth(1);
			break;
		case R.id.radio2:
			if (arg1)
				goPagerSmooth(2);
			break;
		case R.id.check_table_achart:
			mViewSwitcherData.showNext();
			break;
		case R.id.check_table_achart2:
			mViewSwitcherData2.showNext();
			// refreshTableOrEchat(arg1);
			break;
		default:
			break;
		}
	}

	private void goPagerSmooth(int i) {
		int cur = mViewFlipper1.indexOfChild(mViewFlipper1.getCurrentView());
		if (cur > i) {
			mViewFlipper1.setInAnimation(this, R.anim.push_left_in);
			mViewFlipper1.setOutAnimation(this, R.anim.push_right_out);
		} else if (cur < i) {
			mViewFlipper1.setInAnimation(this, R.anim.push_right_in);
			mViewFlipper1.setOutAnimation(this, R.anim.push_left_out);
		}
		mViewFlipper1.setDisplayedChild(i);
	}

	/** 刷新指数 */
	private void refreshTableOrEchat(boolean isMonth) {
		ArrayList<MobileBusinessHall> array = null;
		if (isMonth) {
			if (mBusinessDataMonth == null || mBusinessDataMonth.isEmpty())
				array = createDefaultData(isMonth, 3);
			else
				array = mBusinessDataMonth;
			doRefreshData(array, mTableBusinessData2);

			mLinearLayoutData2.removeAllViews();
			mLinearLayoutData2.addView(getDataLineChart(array, "近期月数据", getString(R.string.month)), LayoutParams.MATCH_PARENT, LayoutParams.FILL_PARENT);
		} else {
			if (mBusinessDataDays == null || mBusinessDataDays.isEmpty())
				doRefreshData(createDefaultData(isMonth, 7), mTableBusinessData);
			else
				doRefreshData(mBusinessDataDays, mTableBusinessData);

			mLinearLayoutData.removeAllViews();
			mLinearLayoutData.addView(getDataLineChart(mBusinessDataDays, "日数据折线图", getString(R.string.date)), LayoutParams.MATCH_PARENT,
					LayoutParams.FILL_PARENT);

		}
	}

	private void doGetMobMenberScore() {
		new GetMobMenberScoreTask().execute(mGroupId, this);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {
		mArrayTask.add(task);
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		mArrayTask.remove(task);

		dismissDialog();
		if (task instanceof GetIndexBrothersortTask)
			refreshTableOrEchat(((GetIndexBrothersortTask) task).isMonth);

		if (result != TaskResult.OK) {
			Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (task instanceof GetMobMenberScoreTask) {
			mArrayMobMenberScore = (ArrayList<MobMenberScore>) param;
			refreshViewEmployeDetail();
		}

		else if (task instanceof MobInfoByGroupIdTask) {
			mMobHallInfo = (MobileBusinessHall) param;
			refreshRWDInfo(mMobHallInfo);
		}

		else if (task instanceof GetIndexBrothersortTask) {
			GetIndexBrothersortTask t = (GetIndexBrothersortTask) task;
			if (t.isMonth) {
				mBusinessDataMonth = (ArrayList<MobileBusinessHall>) param;
				// refreshTableOrEchat(t.isMonth);
			} else {
				mBusinessDataDays = (ArrayList<MobileBusinessHall>) param;
				// refreshTableOrEchat(t.isMonth);
			}
		} else if (task instanceof GetAppQuotaByGroupid) {
			mArrayCityAppQuota = (ArrayList<ShareOfChannels>) param;
			refreshCityAppQuota(mArrayCityAppQuota);
		}

	}

	private void refreshRWDInfo(MobileBusinessHall hall) {

		String rwd = hall.getRWD_TOTAL();// "201507:85065.8;201506:274403.2;201505:67131.85;";
		if (TextUtils.isEmpty(rwd))
			return;

		ArrayList<String[]> array = null;
		{
			String[] temp = rwd.split(";");
			if (temp == null)
				return;

			array = new ArrayList<String[]>(temp.length);
			for (int i = 0; i < temp.length; ++i) {
				int index = temp[i].indexOf(':');
				String[] as = new String[2];
				if (index == -1) {
					as[0] = "<unknown>";
					as[1] = "0";
					array.add(as);
					continue;
				}

				as[0] = temp[i].substring(0, index);
				as[1] = temp[i].substring(index);

				if (array.isEmpty())
					array.add(as);
				else {
					String[] bs = array.get(array.size() - 1);
					if (bs[0].equals("<unknown>") || bs[0].compareTo(as[0]) <= 0)
						array.add(as);
					else
						array.add(array.size() - 1, as);
				}

			}
		}

		while (mContainterRwd.getChildCount() > 1)
			mContainterRwd.removeViewAt(1);

		BarChart barChart = new BarChart(this);
		barChart.setDrawBorders(false); // //是否在折线图上添加边框

		barChart.setDescription("");// 数据描述

		// 如果没有数据的时候，会显示这个，类似ListView的EmptyView
		barChart.setNoDataTextDescription("You need to provide data for the chart.");

		barChart.setDrawGridBackground(false); // 是否显示表格颜色
		barChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度

		barChart.setTouchEnabled(true); // 设置是否可以触摸

		barChart.setDragEnabled(true);// 是否可以拖拽
		barChart.setScaleEnabled(true);// 是否可以缩放

		barChart.setPinchZoom(false);//

		// barChart.setBackgroundColor();// 设置背景

		barChart.setDrawBarShadow(true);

		{

			List<IBarDataSet> barDataSets = new ArrayList<IBarDataSet>();
			List<String> xValues = new ArrayList<String>();
			for (int i = 0; i < array.size(); i++) {
				xValues.add(array.get(i)[0]);

				ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();

				yValues.add(new BarEntry(CommUtil.parse2Float(array.get(i)[1]), i));

				// y轴的数据集合
				BarDataSet barDataSet = new BarDataSet(yValues, xValues.get(i));
				barDataSet.setColor(Color.HSVToColor(new float[] { 360 / array.size() * i, 1, 1 }));
				barDataSets.add(barDataSet);
			}

			BarData barData = new BarData(xValues, barDataSets);

			barChart.setData(barData);
		}

		Legend mLegend = barChart.getLegend(); // 设置比例图标示

		mLegend.setForm(LegendForm.CIRCLE);// 样式
		mLegend.setFormSize(6f);// 字体
		mLegend.setTextColor(Color.BLACK);// 颜色

		// X轴设定
		// XAxis xAxis = barChart.getXAxis();
		// xAxis.setPosition(XAxisPosition.BOTTOM);

		barChart.animateX(2500); // 立即执行的动画,x轴

		// 加入显示
		mContainterRwd.addView(barChart, LayoutParams.MATCH_PARENT, UnitUtils.dip2px(this, 230));
	}

	private void refreshCityAppQuota(ArrayList<ShareOfChannels> array) {
		while (mContainterPieChart.getChildCount() > 1)
			mContainterPieChart.removeViewAt(1);

		if (array == null || array.isEmpty())
			mContainterPieChart.setVisibility(View.GONE);
		else
			mContainterPieChart.setVisibility(View.VISIBLE);

		for (ShareOfChannels s : array) {
			mContainterPieChart.addView(createPieChart(s), -1, UnitUtils.dip2px(this, 230));
		}
	}

	private PieChart createPieChart(ShareOfChannels s) {
		PieChart chart = new PieChart(this);
		chart.setCenterText("渠道份额");
		chart.setCenterTextColor(0x4433ff);
		chart.setCenterTextRadiusPercent(1);// 边界框的矩形半径为中心文本,派孔默认1的比例
		chart.setCenterTextSize(8);

		// 设置标题
		String title = null;
		if (s.getCOUNTRY_NAME() != null)
			title = s.getCITY_NAME() + s.getCOUNTRY_NAME() + s.getDATA_CYCLE();
		else
			title = s.getCITY_NAME() + s.getDATA_CYCLE();
		// 设置数据
		chart.setData(PopupWindowPieChartViewForRateOfChannels.getPieData(this, s, getResources().getStringArray(R.array.namesOfChannelType), title));

		chart.setDrawCenterText(true);
		chart.setDrawHoleEnabled(true);
		chart.setDrawSliceText(true);
		chart.setRotationAngle(90);// 初始旋转角度
		chart.setRotationEnabled(true); // 可以手动旋转
		chart.setUsePercentValues(true); // 显示成百分比
		chart.setExtraOffsets(10, 20, 10, 20);
		chart.setHardwareAccelerationEnabled(true);// 设置为true将视图层类型硬件,错误的将层类型软件
		// chart.setHighlighter(highlighter)//设置一个自定义highligher对象的图表处理/处理所有突出触摸事件在图表视图上执行。
		chart.setHighlightPerTapEnabled(true);// 设置为false,以防止值被点击手势了。
												// 值仍然可以通过拖动或编程方式突出显示。 默认值:true
		chart.setHoleColor(Color.RED);
		chart.setHoleColorTransparent(true);
		chart.setHoleRadius(60);// 半径
		chart.setLogEnabled(true);
		chart.setTransparentCircleRadius(50f);
		// chart.setMarkerView(v)
		// chart.setMaxAngle(0.5f);
		chart.setNoDataText(getString(R.string.noCotent));
		chart.setCenterTextColor(Color.BLACK); // 饼状图中间的文字
		// no description text
		chart.setDescription("");
		chart.setNoDataTextDescription(getString(R.string.noCotent));

		// enable touch gestures
		chart.setTouchEnabled(true);

		chart.setDescription("单位%");
		// chart.setDescriptionPosition(x, y);//设置一个自定义的描述文本的位置像素在屏幕上。
		chart.setDescriptionColor(0xff0000ff);
		chart.setDragDecelerationEnabled(true);// 如果设置为真,图表润色后继续滚动。
		chart.setDragDecelerationFrictionCoef(1);// 减速摩擦系数在[0,1]区间,值越大表示速度会慢慢减少,例如如果设置为0,它将立即停止。1是一个无效的值,并将自动转换为0.999度。

		chart.animateX(2500, Easing.EasingOption.EaseInOutQuart);

		return chart;
	}

	/**
	 * 对数组的时间进行升序排列
	 * 
	 * @param array
	 */
	private void reorder(ArrayList<MobileBusinessHall> array) {
		if (array == null)
			return;

		for (int i = 0; i < array.size() - 1; i++) {
			String temp = array.get(i).getDATA_CYCLE();
			if (temp == null)
				continue;
			for (int j = i + 1; j < array.size(); j++) {
				String temp2 = array.get(j).getDATA_CYCLE();
				if (temp2 == null || temp.compareTo(temp2) > 0) {
					temp = temp2;
					array.set(i, array.get(j));
				}
			}
		}
	}

	// private void doRefresh7dayDataAChar(ArrayList<MobileBusinessHall> array,
	// final int NUM_DEFAULT_ROWS, final String TITLE_NAME, final String
	// TITLE_OF_X) {
	// boolean isEmpty = array == null || array.isEmpty();
	// int size = NUM_DEFAULT_ROWS;
	// if (!isEmpty)
	// size = array.size();
	//
	// String[] nameOfIndexsort =
	// getResources().getStringArray(R.array.indexsortName);
	// String[] legends = new String[nameOfIndexsort.length - 1];
	// int[] colors = new int[legends.length];
	// ArrayList<PointF[]> pointsList = new ArrayList<PointF[]>();
	// for (int i = 0; i < legends.length; i++) {
	// legends[i] = nameOfIndexsort[i + 1];
	// colors[i] = Color.HSVToColor(new float[] { 360 / legends.length * i, 1, 1
	// });
	// pointsList.add(new PointF[size]);
	// }
	//
	// // 创建列标题
	// int minY = 0, maxY = 0;
	// int minX = 0, maxX = 0;
	//
	// for (int i = 0; i < size; i++) {
	// int date = 0;// 日期值
	// int temp = 0;// 临时转换变量
	// if (isEmpty) {
	// date = Integer.parseInt(DateUtil.getPastDate(i, null));
	// } else
	// date = CommUtil.parse2Integer(array.get(i).getDATA_CYCLE(), 0);
	//
	// if (minX == 0)
	// minX = date;
	// if (maxX == 0)
	// maxX = date;
	// if (date > maxX)
	// maxX = date;
	// if (date < minX)
	// minX = date;
	//
	// if (isEmpty) {
	// pointsList.get(0)[i] = new PointF(date, 0);
	// } else {
	// temp = CommUtil.parse2Integer(array.get(i).getG4_TARIFF_ADD(), 0);
	// if (temp > maxY)
	// maxY = temp;
	// pointsList.get(0)[i] = new PointF(date, temp);
	// }
	//
	// if (isEmpty) {
	// pointsList.get(1)[i] = new PointF(date, 0);
	// } else {
	// temp = CommUtil.parse2Integer(array.get(i).getG4_TERM_SALES(), 0);
	// if (temp > maxY)
	// maxY = temp;
	// pointsList.get(1)[i] = new PointF(date, temp);
	// }
	//
	// if (isEmpty) {
	// pointsList.get(2)[i] = new PointF(date, 0);
	// } else {
	// temp = CommUtil.parse2Integer(array.get(i).getBROADBAND_NUMS(), 0);
	// if (temp > maxY)
	// maxY = temp;
	// pointsList.get(2)[i] = new PointF(date, temp);
	// }
	// }
	//
	// LineChartView lineChartView = new LineChartView(this, TITLE_NAME,
	// pointsList, colors, legends);
	// lineChartView.setShowGrid(false);
	// lineChartView.setClickEnabled(true);
	// // lineChartView.setRoundChartHeight(20);
	// lineChartView.setZoomEnabled(false);
	//
	// lineChartView.setZoomButtonsVisible(false);
	// lineChartView.setFillPoints(0, false);
	// lineChartView.setPointStyle(0, PointStyle.POINT);
	// lineChartView.setBackgroundColor(Color.TRANSPARENT);
	// lineChartView.setXLabelsColor(Color.BLACK);
	// lineChartView.setXLabels(size + 2);
	// lineChartView.setAxesColor(Color.BLACK);
	// lineChartView.setApplyBackgroundColor(true);
	// lineChartView.setAntialiasing(true);
	// lineChartView.setDisplayChartValues(true);
	// // lineChartView.setGridColor(Color.TRANSPARENT);
	// lineChartView.setLabelsColor(Color.BLACK);
	// lineChartView.setMarginsColor(0xf2f2f2);
	// lineChartView.setPanEnabled(false, false);
	// lineChartView.setYAxisMin(Math.max(minY - 1, 0));
	// lineChartView.setYAxisMax(maxY + 1);
	// lineChartView.setXAxisMin(Math.max(minX - 1, 0));
	// lineChartView.setXAxisMax(maxX + 1);
	// lineChartView.setXLabelsAlign(Align.CENTER);
	// lineChartView.setMargins(new int[] { 20, 20, 20, 20 });
	//
	// lineChartView.setChartTitleTextSize(UnitUtils.sp2px(this, 18));
	// // lineChartView.setLabelsTextSize(UnitUtils.sp2px(this, 12));
	// // lineChartView.setLegendTextSize(UnitUtils.sp2px(this, 10));
	// // lineChartView.setChartValuesTextSize(UnitUtils.sp2px(this,
	// // 10));
	// lineChartView.setXTitle(TITLE_OF_X);
	//
	// mLinearLayoutData.removeAllViews();
	// mLinearLayoutData.addView(lineChartView, LayoutParams.MATCH_PARENT,
	// LayoutParams.MATCH_PARENT);
	// }

	private void doRefreshData(ArrayList<MobileBusinessHall> array, ViewGroup container) {
		while (container.getChildCount() > 2)
			container.removeViewAt(2);

		// 创建列标题
		LayoutInflater inflater = LayoutInflater.from(this);

		for (int i = 0; i < array.size(); i++) {
			View view = inflater.inflate(R.layout.item_business_indexsort, null, false);

			((TextView) view.findViewById(R.id.textView1)).setText(array.get(i).getDATA_CYCLE());

			((TextView) view.findViewById(R.id.textView2)).setText(array.get(i).getG4_TARIFF_ADD());

			((TextView) view.findViewById(R.id.textView3)).setText(array.get(i).getG4_TERM_SALES());

			((TextView) view.findViewById(R.id.textView4)).setText(array.get(i).getBROADBAND_NUMS());

			((TextView) view.findViewById(R.id.textView5)).setText(array.get(i).getRANK());

			container.addView(view);
		}
	}

	private void doRefreshMonthDataAChart(ArrayList<MobileBusinessHall> array, final int NUM_DEFAULT_ROWS, final String TITLE_NAME, final String TITLE_OF_X,
			boolean isMonth) {
		BarChart barChart = new BarChart(this);
		barChart.setDrawBorders(false); // //是否在折线图上添加边框

		barChart.setDescription("");// 数据描述

		// 如果没有数据的时候，会显示这个，类似ListView的EmptyView
		barChart.setNoDataTextDescription("You need to provide data for the chart.");

		barChart.setDrawGridBackground(false); // 是否显示表格颜色
		barChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度

		barChart.setTouchEnabled(true); // 设置是否可以触摸

		barChart.setDragEnabled(true);// 是否可以拖拽
		barChart.setScaleEnabled(true);// 是否可以缩放

		barChart.setPinchZoom(false);//

		// barChart.setBackgroundColor();// 设置背景

		barChart.setDrawBarShadow(true);

		barChart.setData(getBarData(array, NUM_DEFAULT_ROWS, TITLE_NAME, TITLE_OF_X, isMonth)); // 设置数据

		Legend mLegend = barChart.getLegend(); // 设置比例图标示

		mLegend.setForm(LegendForm.CIRCLE);// 样式
		mLegend.setFormSize(6f);// 字体
		mLegend.setTextColor(Color.BLACK);// 颜色

		// X轴设定
		// XAxis xAxis = barChart.getXAxis();
		// xAxis.setPosition(XAxisPosition.BOTTOM);

		barChart.animateX(2500); // 立即执行的动画,x轴

		// 加入显示
		mLinearLayoutData.removeAllViews();
		mLinearLayoutData.addView(barChart, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	/**
	 * @param isMonth
	 * @param size
	 * @return 默认数据
	 */
	private ArrayList<MobileBusinessHall> createDefaultData(boolean isMonth, int size) {
		ArrayList<MobileBusinessHall> array = new ArrayList<MobileBusinessHall>();
		for (int i = 0; i < size; i++) {
			MobileBusinessHall item = new MobileBusinessHall();
			item.setG4_TARIFF_ADD("0");
			item.setG4_TERM_SALES("0");
			item.setBROADBAND_NUMS("0");
			item.setREGISTIME(isMonth ? DateUtil.getPastMonth(i, null) : DateUtil.getPastDate(i, null));
			item.setRANK("-/-");
			array.add(item);
		}
		return array;
	}

	private LineChart getDataLineChart(ArrayList<MobileBusinessHall> array, final String TITLE_NAME, final String TITLE_OF_X) {
		LineChart barChart = new LineChart(this);
		barChart.setDrawBorders(false); // //是否在折线图上添加边框

		barChart.setDescription("");// 数据描述

		// 如果没有数据的时候，会显示这个，类似ListView的EmptyView
		barChart.setNoDataTextDescription("You need to provide data for the chart.");

		barChart.setDrawGridBackground(false); // 是否显示表格颜色
		barChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度

		barChart.setTouchEnabled(true); // 设置是否可以触摸

		barChart.setDragEnabled(true);// 是否可以拖拽
		barChart.setScaleEnabled(true);// 是否可以缩放

		barChart.setPinchZoom(false);//

		// barChart.setBackgroundColor();// 设置背景

		barChart.setData(getLineData(array, TITLE_NAME, TITLE_OF_X)); // 设置数据

		Legend mLegend = barChart.getLegend(); // 设置比例图标示

		mLegend.setForm(LegendForm.CIRCLE);// 样式
		mLegend.setFormSize(6f);// 字体
		mLegend.setTextColor(Color.BLACK);// 颜色

		// X轴设定
		// XAxis xAxis = barChart.getXAxis();
		// xAxis.setPosition(XAxisPosition.BOTTOM);

		barChart.animateX(2500); // 立即执行的动画,x轴

		// 加入显示
		return barChart;
	}

	@Override
	public void onCancelled(GenericTask task) {
		mArrayTask.remove(task);
		dismissDialog();
	}

	private void gotoMapPage() {
		Intent intent = null;
		intent = new Intent(this, SingleFragmentActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle = new Bundle();
		bundle.putBoolean(Intent.EXTRA_CC, true);
		ArrayList<MobileBusinessHall> array = new ArrayList<MobileBusinessHall>();
		array.add(mMobHallInfo);
		bundle.putParcelableArrayList(Intent.EXTRA_DATA_REMOVED, array);
		bundle.putInt(Intent.EXTRA_UID, 0);
		intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, bundle);
		intent.putExtra(ExtraKeyConstant.KEY_FRAGMENT, MapLocationShowFragment.class);
		startActivity(intent);
	}

	private void doGetGroupInfo(String groupId) {
		for (int i = 0; i < mArrayTask.size(); i++) {
			if (mArrayTask.get(i) instanceof MobInfoByGroupIdTask)
				mArrayTask.get(i).cancle();

		}
		new MobInfoByGroupIdTask().execute(getPreferences().getMobile(), groupId, getPreferences().getSubAccount(), this);
	}

	@Override
	public void onBackPressed() {
		if (mViewFlipper1.indexOfChild(mViewFlipper1.getCurrentView()) == 1)
			super.onBackPressed();
		else
			finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mViewFlipper1.indexOfChild(mViewFlipper1.getCurrentView()) == 1 && mCurrentFragment != null)
			mCurrentFragment.onActivityResult(requestCode, resultCode, data);
		else
			super.onActivityResult(requestCode, resultCode, data);
	}

	private BarData getBarData(ArrayList<MobileBusinessHall> array, final int NUM_DEFAULT_ROWS, final String TITLE_NAME, final String TITLE_OF_X,
			boolean isMonth) {

		boolean isEmpty = array == null || array.isEmpty();
		int size = NUM_DEFAULT_ROWS;
		if (!isEmpty)
			size = array.size();

		String[] nameOfIndexsort = getResources().getStringArray(R.array.indexsortName);
		String[] legends = new String[nameOfIndexsort.length - 1];
		int[] colors = new int[legends.length];
		List<IBarDataSet> barDataSets = new ArrayList<IBarDataSet>();
		ArrayList<double[]> pointsList = new ArrayList<double[]>();
		for (int i = 0; i < legends.length; i++) {
			legends[i] = nameOfIndexsort[i + 1];
			colors[i] = Color.HSVToColor(new float[] { 360 / legends.length * i, 1, 1 });
			pointsList.add(new double[size]);
		}

		// x轴名称
		ArrayList<String> xValues = new ArrayList<String>();
		for (int j = 0; j < size; j++) {
			if (isEmpty) {
				if (isMonth)
					xValues.add(DateUtil.getPastMonth(j, null));
				else
					xValues.add(DateUtil.getPastDate(j, null));
			} else
				xValues.add(array.get(j).getDATA_CYCLE());
		}

		// y轴数据
		for (int j = 0; j < legends.length; j++) {

			ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();

			for (int i = 0; i < size; i++) {
				if (isEmpty) {
					yValues.add(new BarEntry(0, i));
				} else {
					String temp = null;
					switch (j) {
					case 0:
						temp = array.get(i).getG4_TARIFF_ADD();
						break;
					case 1:
						temp = array.get(i).getG4_TERM_SALES();
						break;
					default:
						temp = array.get(i).getBROADBAND_NUMS();
						break;
					}
					yValues.add(new BarEntry(CommUtil.parse2Integer(temp, 0), i));
				}
			}

			// y轴的数据集合
			BarDataSet barDataSet = new BarDataSet(yValues, legends[j]);
			barDataSet.setColor(colors[j]);
			barDataSets.add(barDataSet); // add the datasets
		}

		BarData barData = new BarData(xValues, barDataSets);

		return barData;
	}

	private LineData getLineData(ArrayList<MobileBusinessHall> array, final String TITLE_NAME, final String TITLE_OF_X) {

		String[] nameOfIndexsort = getResources().getStringArray(R.array.indexsortName);
		String[] legends = new String[nameOfIndexsort.length - 1];
		int[] colors = new int[legends.length];
		List<ILineDataSet> lineDataSets = new ArrayList<ILineDataSet>();
		ArrayList<double[]> pointsList = new ArrayList<double[]>();
		for (int i = 0; i < legends.length; i++) {
			legends[i] = nameOfIndexsort[i + 1];
			colors[i] = Color.HSVToColor(new float[] { 360 / legends.length * i, 1, 1 });
			pointsList.add(new double[array.size()]);
		}

		// x轴名称
		ArrayList<String> xValues = new ArrayList<String>();
		for (int j = 0; j < array.size(); j++) {
			xValues.add(array.get(j).getDATA_CYCLE());
		}

		// y轴数据
		for (int j = 0; j < legends.length; j++) {

			ArrayList<Entry> yValues = new ArrayList<Entry>();
			boolean cubicEnable = true;
			for (int i = 0; i < array.size(); i++) {
				String temp = null;
				switch (j) {
				case 0:
					temp = array.get(i).getG4_TARIFF_ADD();
					break;
				case 1:
					temp = array.get(i).getG4_TERM_SALES();
					break;
				default:
					temp = array.get(i).getBROADBAND_NUMS();
					break;
				}
				int val = CommUtil.parse2Integer(temp, 0);
				if (val == 0)
					cubicEnable = false;
				yValues.add(new BarEntry(val, i));
			}

			// y轴的数据集合
			LineDataSet lineDataSet = new LineDataSet(yValues, legends[j]);

			// 用y轴的集合来设置参数
			lineDataSet.setLineWidth(1.75f); // 线宽
			lineDataSet.setCircleRadius(3f);// 显示的圆形大小
			lineDataSet.setColor(colors[j]);// 显示颜色
			lineDataSet.setCircleColor(colors[j]);// 圆形的颜色
			lineDataSet.setDrawCubic(cubicEnable);// 设置允许曲线平滑
			lineDataSet.setCubicIntensity(0.3f);// 设置折线平滑度
			lineDataSet.setHighLightColor(colors[j]); // 高亮的线的颜色

			lineDataSets.add(lineDataSet); // add the datasets
		}

		LineData lineData = new LineData(xValues, lineDataSets);

		return lineData;
	}

	/** 获取渠道份额 */
	@SuppressLint("DefaultLocale")
	private void doGetAppQuotaByGroupId(String groupId) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) - 1;
		if (month < 0) {
			month += 12;
			year--;
		}
		String endMonth = String.format("%04d%02d", year, month + 1);
		month -= 2;
		if (month < 0) {
			month += 12;
			year--;
		}
		String startMonth = String.format("%04d%02d", year, month + 1);
		new GetAppQuotaByGroupid().execute(groupId, startMonth, endMonth, this);
	}
}
