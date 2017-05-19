//package com.sunrise.marketingassistant.fragment;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//
//import org.achartengine.ChartFactory;
//import org.achartengine.GraphicalView;
//import org.achartengine.model.XYMultipleSeriesDataset;
//import org.achartengine.model.XYSeries;
//import org.achartengine.renderer.XYMultipleSeriesRenderer;
//import org.achartengine.renderer.XYSeriesRenderer;
//
//import com.baidu.mapapi.model.LatLng;
//import com.starcpt.chartengine.chart.PointStyle;
//import com.starcpt.chartengine.chartview.LineChartView;
//import com.sunrise.businesstransaction.utils.DateUtil;
//import com.sunrise.javascript.utils.UnitUtils;
//import com.sunrise.marketingassistant.ExtraKeyConstant;
//import com.sunrise.marketingassistant.R;
//import com.sunrise.marketingassistant.activity.SingleFragmentActivity;
//import com.sunrise.marketingassistant.adapter.MyPagerAdapter;
//import com.sunrise.marketingassistant.database.CmmaDBHelper;
//import com.sunrise.marketingassistant.entity.CollectBranch;
//import com.sunrise.marketingassistant.entity.MobMenberScore;
//import com.sunrise.marketingassistant.entity.MobileBusinessHall;
//import com.sunrise.marketingassistant.task.DoMobRegisteTask;
//import com.sunrise.marketingassistant.task.GenericTask;
//import com.sunrise.marketingassistant.task.GetIndexBrothersortTask;
//import com.sunrise.marketingassistant.task.GetMobMenberScoreTask;
//import com.sunrise.marketingassistant.task.MobInfoByGroupIdTask;
//import com.sunrise.marketingassistant.task.TaskListener;
//import com.sunrise.marketingassistant.task.TaskResult;
//import com.sunrise.marketingassistant.utils.BaiduMapUtils;
//import com.sunrise.marketingassistant.utils.CommUtil;
//import com.sunrise.marketingassistant.utils.HardwareUtils;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.PointF;
//import android.graphics.Paint.Align;
//import android.os.Bundle;
//import android.support.v4.view.ViewPager;
//import android.support.v4.view.ViewPager.OnPageChangeListener;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.ViewGroup.LayoutParams;
//import android.webkit.WebView;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.RadioButton;
//import android.widget.TableLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.ToggleButton;
//import android.widget.ViewSwitcher;
//
//public class HallDetailFragment2 extends BaseFragment implements OnClickListener, OnCheckedChangeListener, TaskListener, OnPageChangeListener {
//	private MobileBusinessHall mMobHallInfo;
//
//	private ArrayList<GenericTask> mArrayTask;
//
//	/** 店员积分 */
//	private ArrayList<MobMenberScore> mArrayMobMenberScore;
//
//	private ArrayList<MobileBusinessHall> mBusinessDataMonth, mBusinessDataDays;
//
//	/** 店员人数 */
//	private TextView mTV_numOfEmployees;
//
//	/** 数据库 */
//	private CmmaDBHelper mDatabase;
//
//	/** 员工详情表格 */
//	private TableLayout mTableEmployeesDetail;
//	private TableLayout mTableBusinessData;
//
//	private TextView hall_name;
//
//	private TextView hall_address;
//
//	private TextView hall_linkman;
//
//	private TextView hall_tel;
//
//	private TextView hall_starLevel;
//
//	private WebView mWebView;
//
//	private ViewPager mViewPager;
//	private ArrayList<View> mListViews;
//
//	private RadioButton mRadio1, mRadio3, mRadio2;
//
//	/** 线型图容器 */
//	private ViewGroup mLinearLayoutData;
//	/** 数据切换器 */
//	private ViewSwitcher mViewSwitcherData;
//	/** 日数据和月数据切换器 */
//	private CheckBox mCheckBoxSwithcMonthOrDay;
//	/** 表格和甘特图切换器 */
//	private ToggleButton mCheckTableOrAchart;
//
//	/** 数据标题 */
//	private TextView mBusinessDataTitle;
//	/** 类型排名列名称，时间列名称 */
//	private TextView mTitleOfBusinessTableColumnRanking, mTitleOfBusinessTableColumnFirst;
//
//	@SuppressLint("SimpleDateFormat")
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		mArrayTask = new ArrayList<GenericTask>();
//
//		mDatabase = new CmmaDBHelper(mBaseActivity);
//
//		if (savedInstanceState == null)
//			savedInstanceState = getArguments();
//
//		mMobHallInfo = savedInstanceState.getParcelable(Intent.EXTRA_DATA_REMOVED);
//
//		mArrayMobMenberScore = savedInstanceState.getParcelableArrayList(Intent.EXTRA_ALARM_COUNT);
//
//		boolean isWhole = savedInstanceState.getBoolean(Intent.EXTRA_REPLACING, true);
//		if (!isWhole) {
//			doGetGroupInfo(mMobHallInfo.getGROUP_ID());
//		}
//
//		if (mArrayMobMenberScore == null || mArrayMobMenberScore.isEmpty())
//			doGetMobMenberScore();
//
//		new GetIndexBrothersortTask().execute(mMobHallInfo.getGROUP_ID(), new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis())),
//				getResources().getStringArray(R.array.indexsortValue)[2], this);
//		new GetIndexBrothersortTask().execute(mMobHallInfo.getGROUP_ID(), new SimpleDateFormat("yyyyMM").format(new Date(System.currentTimeMillis())),
//				getResources().getStringArray(R.array.indexsortValue)[2], this);
//	}
//
//	public void onSaveInstanceState(Bundle outState) {
//
//		outState.putParcelable(Intent.EXTRA_DATA_REMOVED, mMobHallInfo);
//		outState.putParcelableArrayList(Intent.EXTRA_ALARM_COUNT, mArrayMobMenberScore);
//		super.onSaveInstanceState(outState);
//	}
//
//	public void onStart() {
//		super.onStart();
//
//	}
//
//	public void onStop() {
//
//		super.onStop();
//	}
//
//	public void onDestroy() {
//		for (int i = 0; i < mArrayTask.size(); i++) {
//			mArrayTask.get(i).cancle();
//		}
//
//		super.onDestroy();
//	}
//
//	@Override
//	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.layout_hall_detail2, null, false);
//		view.findViewById(R.id.btn_sign).setOnClickListener(this);
//		view.findViewById(R.id.btn_editInfo).setOnClickListener(this);
//
//		hall_name = (TextView) view.findViewById(R.id.hall_name);
//		hall_address = (TextView) view.findViewById(R.id.hall_address);
//		hall_linkman = (TextView) view.findViewById(R.id.hall_linkman);
//		hall_tel = (TextView) view.findViewById(R.id.hall_tel);
//		hall_starLevel = (TextView) view.findViewById(R.id.hall_starLevel);
//
//		mTableEmployeesDetail = (TableLayout) view.findViewById(R.id.table_employee);
//		mTableBusinessData = (TableLayout) view.findViewById(R.id.table_businessData);
//		mLinearLayoutData = (ViewGroup) view.findViewById(R.id.linearLayout_Data);
//
//		mBusinessDataTitle = (TextView) view.findViewById(R.id.textView_tableTitle);
//		mTitleOfBusinessTableColumnFirst = (TextView) view.findViewById(R.id.tableColumnTitle);
//		mTitleOfBusinessTableColumnRanking = (TextView) view.findViewById(R.id.textView_columnRunkingTitle);
//
//		mViewSwitcherData = (ViewSwitcher) view.findViewById(R.id.viewSwitcher1);
//		mCheckTableOrAchart = (ToggleButton) view.findViewById(R.id.check_table_achart);
//		mCheckTableOrAchart.setOnCheckedChangeListener(this);
//		mCheckBoxSwithcMonthOrDay = (CheckBox) view.findViewById(R.id.btn_switch_day_month);
//		mCheckBoxSwithcMonthOrDay.setOnCheckedChangeListener(this);
//
//		refreshTableOrEchat(false);
//
//		mWebView = (WebView) view.findViewById(R.id.webview);
//		mTV_numOfEmployees = (TextView) view.findViewById(R.id.hall_numOfEmployees);
//		{// 收藏按钮
//			ToggleButton tb = (ToggleButton) view.findViewById(R.id.btn_collection);
//			int num = mDatabase.getCollectBranchCountByGroupID(getPreferences().getSubAccount(), mMobHallInfo.getGROUP_ID());
//			tb.setChecked(num > 0);
//			tb.setOnCheckedChangeListener(this);
//		}
//
//		// 分页
//		mViewPager = (ViewPager) view.findViewById(R.id.viewPager1);
//		mListViews = new ArrayList<View>();
//
//		for (int i = 0; i < mViewPager.getChildCount(); i++) {
//			mListViews.add(mViewPager.getChildAt(i));
//		}
//
//		mViewPager.setAdapter(new MyPagerAdapter(mListViews));
//		mViewPager.setOnPageChangeListener(this);
//
//		{
//			mRadio1 = (RadioButton) view.findViewById(R.id.radio0);
//			mRadio1.setOnCheckedChangeListener(this);
//		}
//		{
//			mRadio2 = (RadioButton) view.findViewById(R.id.radio1);
//			mRadio2.setOnCheckedChangeListener(this);
//		}
//		{
//			mRadio3 = (RadioButton) view.findViewById(R.id.radio2);
//			mRadio3.setOnCheckedChangeListener(this);
//		}
//
//		return view;
//	}
//
//	private void refreashView() {
//		hall_name.setText(mMobHallInfo.getGROUP_NAME());
//		hall_address.setText(mMobHallInfo.getGROUP_ADDRESS());
//		hall_linkman.setText(mMobHallInfo.getCONTACT_PERSON());
//		hall_tel.setText(mMobHallInfo.getCONTACT_PHONE());
//		hall_starLevel.setText(mMobHallInfo.getGRADE_CODE());
//		if (mArrayMobMenberScore != null)
//			mTV_numOfEmployees.setText(mArrayMobMenberScore.size() + "人");
//
//		if (mMobHallInfo != null && mMobHallInfo.getLATITUDE() != 0 && mMobHallInfo.getLONGITUDE() != 0) {
//			/** 静态地图 */
//			mWebView.setVisibility(View.VISIBLE);
//			int width = 360;
//			int height = 240;
//			String url = new BaiduMapUtils(mBaseActivity)
//					.getStaticImage(new LatLng(mMobHallInfo.getLATITUDE(), mMobHallInfo.getLONGITUDE()), width, height, 17);
//			mWebView.loadUrl(url);
//			mWebView.setOnClickListener(this);
//		}
//
//		if (mArrayMobMenberScore != null && !mArrayMobMenberScore.isEmpty()) {
//			mTableEmployeesDetail.setVisibility(View.VISIBLE);
//			mTV_numOfEmployees.setText(mArrayMobMenberScore.size() + "人");
//			mTableEmployeesDetail.removeAllViews();
//			for (MobMenberScore item : mArrayMobMenberScore) {
//				View view = LayoutInflater.from(mBaseActivity).inflate(R.layout.item_employee_info, null, false);
//				((TextView) view.findViewById(R.id.employee_no)).setText(item.getPERSON_ID());
//				((TextView) view.findViewById(R.id.employee_name)).setText(item.getPERSON_NAME());
//				((TextView) view.findViewById(R.id.employee_currentScore)).setText(item.getCUR_SCORE());
//				((TextView) view.findViewById(R.id.employee_totleScore)).setText(item.getTOL_SCORE());
//				mTableEmployeesDetail.addView(view);
//			}
//		}
//	}
//
//	@Override
//	public void onClick(View arg0) {
//		switch (arg0.getId()) {
//		case R.id.btn_sign:
//			doSignIn();
//			break;
//		case R.id.btn_editInfo:
//			break;
//		case R.id.webview:
//			gotoMapPage();
//			break;
//		default:
//			break;
//		}
//	}
//
//	@Override
//	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
//		switch (arg0.getId()) {
//		case R.id.btn_collection:
//			doCollection(arg1);
//			break;
//		case R.id.radio0:
//			if (arg1)
//				goPagerSmooth(0);
//			break;
//		case R.id.radio1:
//			if (arg1)
//				goPagerSmooth(1);
//			break;
//		case R.id.radio2:
//			if (arg1)
//				goPagerSmooth(2);
//			break;
//		case R.id.check_table_achart:
//			mViewSwitcherData.showNext();
//			break;
//		case R.id.btn_switch_day_month:
//			refreshTableOrEchat(arg1);
//			break;
//		default:
//			break;
//		}
//	}
//
//	private void refreshTableOrEchat(boolean isMonth) {
//		if (isMonth) {
//			mBusinessDataTitle.setText(R.string.dataOfMonths);
//			mTitleOfBusinessTableColumnFirst.setText(R.string.month);
//			mTitleOfBusinessTableColumnRanking.setText(R.string.rankingOfSameTypeInSameMonth);
//			doRefreshMonthData(mBusinessDataMonth);
//			doRefreshMonthDataAChart(mBusinessDataMonth);
//		} else {
//			mBusinessDataTitle.setText(R.string.dataOfDays);
//			mTitleOfBusinessTableColumnFirst.setText(R.string.date);
//			mTitleOfBusinessTableColumnRanking.setText(R.string.rankingOfSameTypeInSameDay);
//			doRefresh7dayData(mBusinessDataDays);
//			doRefresh7dayDataAChar(mBusinessDataDays);
//		}
//	}
//
//	private void doCollection(boolean arg1) {
//		if (arg1) {
//			CollectBranch collect = new CollectBranch();
//			collect.setCLASS_NAME(mMobHallInfo.getCLASS_NAME());
//			collect.setAccount(getPreferences().getSubAccount());
//			collect.setGROUP_ADDRESS(mMobHallInfo.getGROUP_ADDRESS());
//			collect.setGROUP_ID(mMobHallInfo.getGROUP_ID());
//			collect.setGROUP_NAME(mMobHallInfo.getGROUP_NAME());
//			long result = mDatabase.insert(collect);
//			if (result > -1)
//				Toast.makeText(mBaseActivity, mMobHallInfo.getGROUP_NAME() + " 被收藏成功", Toast.LENGTH_SHORT).show();
//			else
//				Toast.makeText(mBaseActivity, "收藏夹中已存在" + mMobHallInfo.getGROUP_NAME(), Toast.LENGTH_SHORT).show();
//		}
//
//		else {
//			int result = mDatabase.deleteByGroupId(getPreferences().getSubAccount(), mMobHallInfo.getGROUP_ID());
//			if (result >= 0)
//				Toast.makeText(mBaseActivity, mMobHallInfo.getGROUP_NAME() + " 被取消收藏", Toast.LENGTH_SHORT).show();
//			else
//				Toast.makeText(mBaseActivity, mMobHallInfo.getGROUP_NAME() + " 从未被收藏", Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	private void doGetMobMenberScore() {
//		new GetMobMenberScoreTask().execute(mMobHallInfo.getGROUP_ID(), this);
//	}
//
//	/** 签到的动作 */
//	private void doSignIn() {
//		new DoMobRegisteTask().execute(mMobHallInfo.getGROUP_ID(), getPreferences().getSubAccount(), getPreferences().getMobile(), mMobHallInfo.getLATITUDE(),
//				mMobHallInfo.getLONGITUDE(), HardwareUtils.getPhoneIMSI(mBaseActivity), this);
//
//	}
//
//	@Override
//	public String getName() {
//		return null;
//	}
//
//	@Override
//	public void onPreExecute(GenericTask task) {
//
//		mArrayTask.add(task);
//
//		if (task instanceof DoMobRegisteTask) {
//			initDialog();
//			showDialog(mMobHallInfo.getGROUP_NAME() + " 签到过程中……");
//		}
//	}
//
//	@Override
//	public void onPostExecute(GenericTask task, TaskResult result) {
//		mArrayTask.remove(task);
//
//		dismissDialog();
//		if (result != TaskResult.OK)
//			Toast.makeText(mBaseActivity, task.getException().getMessage(), Toast.LENGTH_LONG).show();
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public void onProgressUpdate(GenericTask task, Object param) {
//		if (task instanceof GetMobMenberScoreTask) {
//			mArrayMobMenberScore = (ArrayList<MobMenberScore>) param;
//			refreashView();
//		}
//
//		else if (task instanceof DoMobRegisteTask)
//			CommUtil.showAlert(mBaseActivity, null, mMobHallInfo.getGROUP_NAME() + "\n" + param.toString(), null, false, null);
//
//		else if (task instanceof MobInfoByGroupIdTask) {
//			mMobHallInfo.merge((MobileBusinessHall) param);
//			refreashView();
//		}
//
//		else if (task instanceof GetIndexBrothersortTask) {
//			GetIndexBrothersortTask t = (GetIndexBrothersortTask) task;
//			if (t.isMonth) {
//				mBusinessDataMonth = (ArrayList<MobileBusinessHall>) param;
//				refreshTableOrEchat(t.isMonth);
//			} else {
//				mBusinessDataDays = (ArrayList<MobileBusinessHall>) param;
//				refreshTableOrEchat(t.isMonth);
//			}
//		}
//	}
//
//	private void doRefresh7dayData(ArrayList<MobileBusinessHall> array) {
//		mBusinessDataTitle.setText(R.string.dataOfDays);
//		mTitleOfBusinessTableColumnFirst.setText(R.string.date);
//		mTitleOfBusinessTableColumnRanking.setText(R.string.rankingOfSameTypeInSameDay);
//
//		while (mTableBusinessData.getChildCount() > 2)
//			mTableBusinessData.removeViewAt(2);
//
//		boolean isEmpty = array == null || array.isEmpty();
//
//		// 创建列标题
//		LayoutInflater inflater = LayoutInflater.from(mBaseActivity);
//
//		int size = 7;
//		if (!isEmpty)
//			size = array.size();
//
//		for (int i = 0; i < size; i++) {
//			View view = inflater.inflate(R.layout.item_business_indexsort, null, false);
//			String dayname = null;
//			if (isEmpty) {
//				dayname = DateUtil.getPastDate(i, null);
//			} else
//				dayname = array.get(i).getDATA_CYCLE();
//			((TextView) view.findViewById(R.id.textView1)).setText(dayname);
//
//			if (isEmpty) {
//				((TextView) view.findViewById(R.id.textView2)).setText("0");
//			} else {
//				((TextView) view.findViewById(R.id.textView2)).setText(array.get(i).getG4_TARIFF_ADD());
//			}
//
//			if (isEmpty) {
//				((TextView) view.findViewById(R.id.textView3)).setText("0");
//			} else {
//				((TextView) view.findViewById(R.id.textView3)).setText(array.get(i).getG4_TERM_SALES());
//			}
//
//			if (isEmpty) {
//				((TextView) view.findViewById(R.id.textView4)).setText("0");
//			} else {
//				((TextView) view.findViewById(R.id.textView4)).setText(array.get(i).getBROADBAND_NUMS());
//			}
//
//			if (isEmpty)
//				((TextView) view.findViewById(R.id.textView5)).setText("-/-");
//			else
//				((TextView) view.findViewById(R.id.textView5)).setText(array.get(i).getRANK());
//
//			mTableBusinessData.addView(view);
//		}
//
//	}
//
//	private void doRefresh7dayDataAChar(ArrayList<MobileBusinessHall> array) {
//		boolean isEmpty = array == null || array.isEmpty();
//		int size = 7;
//		if (!isEmpty)
//			size = array.size();
//
//		String[] nameOfIndexsort = getResources().getStringArray(R.array.indexsortName);
//		String[] legends = new String[nameOfIndexsort.length - 1];
//		int[] colors = new int[legends.length];
//		ArrayList<PointF[]> pointsList = new ArrayList<PointF[]>();
//		for (int i = 0; i < legends.length; i++) {
//			legends[i] = nameOfIndexsort[i + 1];
//			colors[i] = Color.HSVToColor(new float[] { 360 / legends.length * i, 1, 1 });
//			pointsList.add(new PointF[size]);
//		}
//
//		// 创建列标题
//		int minY = 0, maxY = 0;
//		int minX = 0, maxX = 0;
//
//		for (int i = 0; i < size; i++) {
//			int date = 0;// 日期值
//			int temp = 0;// 临时转换变量
//			if (isEmpty) {
//				date = Integer.parseInt(DateUtil.getPastDate(i, null));
//			} else
//				date = CommUtil.parse2Integer(array.get(i).getDATA_CYCLE(), 0);
//
//			// if (date == 0) {
//			// LogUtlis.e("异常", "" +
//			// JsonUtils.writeObjectToJsonStr(array.get(i)));
//			// }
//
//			if (minX == 0)
//				minX = date;
//			if (maxX == 0)
//				maxX = date;
//			if (date > maxX)
//				maxX = date;
//			if (date < minX)
//				minX = date;
//
//			if (isEmpty) {
//				pointsList.get(0)[i] = new PointF(date, 0);
//			} else {
//				temp = CommUtil.parse2Integer(array.get(i).getG4_TARIFF_ADD(), 0);
//				if (temp > maxY)
//					maxY = temp;
//				pointsList.get(0)[i] = new PointF(date, temp);
//			}
//
//			if (isEmpty) {
//				pointsList.get(1)[i] = new PointF(date, 0);
//			} else {
//				temp = CommUtil.parse2Integer(array.get(i).getG4_TERM_SALES(), 0);
//				if (temp > maxY)
//					maxY = temp;
//				pointsList.get(1)[i] = new PointF(date, temp);
//			}
//
//			if (isEmpty) {
//				pointsList.get(2)[i] = new PointF(date, 0);
//			} else {
//				temp = CommUtil.parse2Integer(array.get(i).getBROADBAND_NUMS(), 0);
//				if (temp > maxY)
//					maxY = temp;
//				pointsList.get(2)[i] = new PointF(date, temp);
//			}
//		}
//
//		LineChartView lineChartView = new LineChartView(mBaseActivity, "日数据折线图", pointsList, colors, legends);
//		lineChartView.setShowGrid(false);
//		lineChartView.setClickEnabled(true);
//		// lineChartView.setRoundChartHeight(20);
//		lineChartView.setZoomEnabled(false);
//
//		lineChartView.setZoomButtonsVisible(false);
//		lineChartView.setFillPoints(0, false);
//		lineChartView.setPointStyle(0, PointStyle.POINT);
//		lineChartView.setBackgroundColor(Color.TRANSPARENT);
//		lineChartView.setXLabelsColor(Color.BLACK);
//		lineChartView.setAxesColor(Color.BLACK);
//		lineChartView.setApplyBackgroundColor(true);
//		lineChartView.setAntialiasing(true);
//		lineChartView.setDisplayChartValues(true);
//		// lineChartView.setGridColor(Color.TRANSPARENT);
//		lineChartView.setLabelsColor(Color.BLACK);
//		lineChartView.setMarginsColor(0xf2f2f2);
//		lineChartView.setPanEnabled(false, false);
//		lineChartView.setYAxisMin(Math.max(minY - 1, 0));
//		lineChartView.setYAxisMax(maxY + 1);
//		lineChartView.setXAxisMin(Math.max(minX - 1, 0));
//		lineChartView.setXAxisMax(maxX + 1);
//		lineChartView.setXLabelsAlign(Align.CENTER);
//
//		lineChartView.setChartTitleTextSize(UnitUtils.sp2px(mBaseActivity, 18));
//		// lineChartView.setLabelsTextSize(UnitUtils.sp2px(mBaseActivity, 12));
//		// lineChartView.setLegendTextSize(UnitUtils.sp2px(mBaseActivity, 10));
//		// lineChartView.setChartValuesTextSize(UnitUtils.sp2px(mBaseActivity,
//		// 10));
//		lineChartView.setXTitle(getString(R.string.date));
//
//		mLinearLayoutData.removeAllViews();
//		mLinearLayoutData.addView(lineChartView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//	}
//
//	private void doRefreshMonthData(ArrayList<MobileBusinessHall> array) {
//		while (mTableBusinessData.getChildCount() > 2)
//			mTableBusinessData.removeViewAt(2);
//
//		boolean isEmpty = array == null || array.isEmpty();
//
//		// 创建列标题
//		LayoutInflater inflater = LayoutInflater.from(mBaseActivity);
//
//		int size = 3;
//		if (!isEmpty)
//			size = array.size();
//		for (int i = 0; i < size; i++) {
//			View view = inflater.inflate(R.layout.item_business_indexsort, null, false);
//
//			if (isEmpty) {
//				((TextView) view.findViewById(R.id.textView1)).setText(DateUtil.getPastMonth(i, null));
//			} else
//				((TextView) view.findViewById(R.id.textView1)).setText(array.get(i).getDATA_CYCLE());
//
//			if (isEmpty)
//				((TextView) view.findViewById(R.id.textView2)).setText("0");
//			else
//				((TextView) view.findViewById(R.id.textView2)).setText(array.get(i).getG4_TARIFF_ADD());
//
//			if (isEmpty)
//				((TextView) view.findViewById(R.id.textView3)).setText("0");
//			else
//				((TextView) view.findViewById(R.id.textView3)).setText(array.get(i).getG4_TERM_SALES());
//
//			if (isEmpty)
//				((TextView) view.findViewById(R.id.textView4)).setText("0");
//			else
//				((TextView) view.findViewById(R.id.textView4)).setText(array.get(i).getBROADBAND_NUMS());
//
//			if (isEmpty)
//				((TextView) view.findViewById(R.id.textView5)).setText("-/-");
//			else
//				((TextView) view.findViewById(R.id.textView5)).setText(array.get(i).getRANK());
//
//			mTableBusinessData.addView(view);
//		}
//	}
//
//	private void doRefreshMonthDataAChart(ArrayList<MobileBusinessHall> array) {
//		boolean isEmpty = array == null || array.isEmpty();
//		int size = 7;
//		if (!isEmpty)
//			size = array.size();
//
//		String[] nameOfIndexsort = getResources().getStringArray(R.array.indexsortName);
//
//		// 创建列标题
//		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
//		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
//		renderer.setDisplayValues(true);
//		renderer.setApplyBackgroundColor(true);
//		renderer.setBackgroundColor(Color.TRANSPARENT);
//		renderer.setBackgroundColor(Color.TRANSPARENT);
//		renderer.setAxesColor(Color.DKGRAY);
//		renderer.setAntialiasing(true);
//		renderer.setExternalZoomEnabled(false);
//		renderer.setFitLegend(true);
//		renderer.setLabelsColor(Color.BLACK);
//		renderer.setPanEnabled(true);
//		renderer.setShowLabels(true);
//		renderer.setShowLegend(true);
//		renderer.setZoomEnabled(false, false);
//		renderer.setPanEnabled(true, false);
//		renderer.setZoomButtonsVisible(false);
//		renderer.setChartTitle("近期月数据");
//
//		renderer.setChartTitleTextSize(UnitUtils.sp2px(mBaseActivity, 18));
//		renderer.setLabelsTextSize(UnitUtils.sp2px(mBaseActivity, 12));
//		renderer.setLegendTextSize(UnitUtils.sp2px(mBaseActivity, 10));
//
//		for (int i = 0; i < nameOfIndexsort.length - 1; i++) {
//			XYSeries series = new XYSeries(nameOfIndexsort[i + 1]);
//			dataset.addSeries(series);
//
//			XYSeriesRenderer ssr = new XYSeriesRenderer();
//			ssr.setColor(Color.HSVToColor(new float[] { 360 / (nameOfIndexsort.length - 1) * i, 1, 1 }));
//			ssr.setDisplayBoundingPoints(true);
//			ssr.setShowLegendItem(true);
//			ssr.setShowLegendItem(true);
//			ssr.setAnnotationsTextSize(UnitUtils.sp2px(mBaseActivity, 8));
//			ssr.setFillPoints(true);
//			ssr.setLineWidth(UnitUtils.dip2px(mBaseActivity, 2));
//			ssr.setPointStyle(org.achartengine.chart.PointStyle.DIAMOND);
//			renderer.addSeriesRenderer(ssr);
//		}
//
//		int minY = 0, maxY = 0;
//		for (int i = 0; i < size; i++) {
//			int temp = 0;// 临时转换变量
//			int date = 0;
//			if (isEmpty)
//				date = CommUtil.parse2Integer(DateUtil.getPastMonth(i + 1, null), 0);
//			else
//				date = CommUtil.parse2Integer(array.get(i).getDATA_CYCLE(), 0);
//
//			if (isEmpty) {
//				dataset.getSeriesAt(0).add(date, 0);
//			} else {
//				temp = CommUtil.parse2Integer(array.get(i).getG4_TARIFF_ADD(), 0);
//				if (temp > maxY)
//					maxY = temp;
//				dataset.getSeriesAt(0).add(i + 1, temp);
//			}
//
//			if (isEmpty) {
//				dataset.getSeriesAt(1).add(date, 0);
//			} else {
//				temp = CommUtil.parse2Integer(array.get(i).getG4_TERM_SALES(), 0);
//				if (temp > maxY)
//					maxY = temp;
//				dataset.getSeriesAt(1).add(i + 1, temp);
//			}
//
//			if (isEmpty) {
//				dataset.getSeriesAt(2).add(date, 0);
//			} else {
//				temp = CommUtil.parse2Integer(array.get(i).getBROADBAND_NUMS(), 0);
//				if (temp > maxY)
//					maxY = temp;
//				dataset.getSeriesAt(2).add(i + 1, temp);
//			}
//
//		}
//		renderer.setYAxisMax(maxY + 1);
//		renderer.setYAxisMin(Math.max(minY - 1, 0));
//
//		GraphicalView lineChartView = ChartFactory.getCubeLineChartView(mBaseActivity, dataset, renderer, 1);
//
//		mLinearLayoutData.removeAllViews();
//		mLinearLayoutData.addView(lineChartView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//	}
//
//	@Override
//	public void onCancelled(GenericTask task) {
//		mArrayTask.remove(task);
//		dismissDialog();
//	}
//
//	private void gotoMapPage() {
//		Intent intent = null;
//		intent = new Intent(mBaseActivity, SingleFragmentActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		Bundle bundle = new Bundle();
//		bundle.putBoolean(Intent.EXTRA_CC, true);
//		ArrayList<MobileBusinessHall> array = new ArrayList<MobileBusinessHall>();
//		array.add(mMobHallInfo);
//		bundle.putParcelableArrayList(Intent.EXTRA_DATA_REMOVED, array);
//		bundle.putInt(Intent.EXTRA_UID, 0);
//		intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, bundle);
//		intent.putExtra(ExtraKeyConstant.KEY_FRAGMENT, MapLocationShowFragment.class);
//		startActivity(intent);
//	}
//
//	private void doGetGroupInfo(String groupId) {
//		for (int i = 0; i < mArrayTask.size(); i++) {
//			if (mArrayTask.get(i) instanceof MobInfoByGroupIdTask)
//				mArrayTask.get(i).cancle();
//
//		}
//		new MobInfoByGroupIdTask().execute(getPreferences().getMobile(), groupId, getPreferences().getSubAccount(), this);
//	}
//
//	@Override
//	public void onPageScrollStateChanged(int position) {
//
//	}
//
//	@Override
//	public void onPageScrolled(int arg0, float arg1, int arg2) {
//
//	}
//
//	@Override
//	public void onPageSelected(int position) {
//		switch (position) {
//		case 0:
//			mRadio1.setChecked(true);
//			break;
//		case 1:
//			mRadio2.setChecked(true);
//			break;
//		case 2:
//			mRadio3.setChecked(true);
//			break;
//		default:
//			break;
//		}
//	}
//
//	@SuppressWarnings("deprecation")
//	private void goPagerSmooth(int index) {
//		mViewPager.setCurrentItem(index);
//	}
//
//}
