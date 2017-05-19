package com.sunrise.marketingassistant.view;

import java.util.ArrayList;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.sunrise.javascript.utils.UnitUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.entity.ShareOfChannels;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class PopupWindowPieChartViewForRateOfChannels implements ExtraKeyConstant {

	/** 渠道份额占比悬浮窗 */
	private PopupWindow mPopupWindowRate;
	private Context mContext;
	// private ViewPager mViewPager;
	// private ArrayList<View> mListViews;
	private String[] pieLabels = null;
	private ViewGroup container;

	public PopupWindowPieChartViewForRateOfChannels(Context context) {
		mContext = context;
		// mListViews = new ArrayList<View>();
		pieLabels = context.getResources().getStringArray(R.array.namesOfChannelType);
		init(context);
	}

	private void init(Context context) {
		container = new LinearLayout(context);// (ViewGroup)
												// LayoutInflater.from(context).inflate(R.layout.part_popup_rate_channels,
												// null, false);
		container.setBackgroundResource(R.drawable.shape_white_bg);
		{
			int padding = UnitUtils.dip2px(mContext, 4);
			container.setPadding(padding, padding, padding, padding);
		}

		// 分页
		// mViewPager = (ViewPager) container.findViewById(R.id.viewPager1);
		mPopupWindowRate = new PopupWindow(container, UnitUtils.dip2px(mContext, 260), UnitUtils.dip2px(mContext, 230), false);
		mPopupWindowRate.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindowRate.setFocusable(true);
		mPopupWindowRate.setOutsideTouchable(false);

		// mViewPager.setAdapter(new MyPagerAdapter(mListViews));
	}

	/**
	 * 
	 * @param string
	 */
	// public void setContent1(ArrayList<ShareOfChannels> items) {
	// cleanView();
	// if (items == null || items.isEmpty())
	// return;
	// ShareOfChannels item = items.get(0);
	// pieTastdata[0] = item.getCMCC_QUOTA() * 100;
	// pieTastdata[1] = item.getUNICOM_QUOTA() * 100;
	// pieTastdata[2] = item.getTELECOM_QUOTA() * 100;
	//
	// PieChartView pieChartView = new PieChartView(mContext,
	// String.format(FORMAT_SHARE_OF_CHANNELS, item.getCITY_NAME(),
	// item.getDATA_CYCLE()), pieTastdata,
	// COLORS_OF_CHANNEL_TYPE, pieLabels);
	// pieChartView.setRoundChartHeight(20);
	// pieChartView.setZoomEnabled(false);
	// pieChartView.setZoomButtonsVisible(false);
	// pieChartView.setBackgroundColor(Color.TRANSPARENT);
	// pieChartView.setAxesColor(Color.BLACK);
	// pieChartView.setApplyBackgroundColor(true);
	// pieChartView.setLabelsColor(Color.BLACK);
	//
	// pieChartView.setChartTitleTextSize(UnitUtils.sp2px(mContext, 18));
	// pieChartView.setLabelsTextSize(UnitUtils.sp2px(mContext, 12));
	// pieChartView.setLegendTextSize(UnitUtils.sp2px(mContext, 14));
	// container.addView(pieChartView, -1, -1);
	//
	// }

	public void setContent2(ArrayList<ShareOfChannels> items) {
		cleanView();
		if (items == null || items.isEmpty())
			return;

		ShareOfChannels item = items.get(0);

		PieChart chart = new PieChart(mContext);
		chart.setCenterText("渠道份额");
		chart.setCenterTextColor(0x4433ff);
		chart.setCenterTextRadiusPercent(1);// 边界框的矩形半径为中心文本,派孔默认1的比例
		chart.setCenterTextSize(8);

		// 设置数据
		chart.setData(getPieData(mContext, item, pieLabels, item.getCITY_NAME() + item.getDATA_CYCLE()));

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
		chart.setNoDataText(mContext.getString(R.string.noCotent));
		chart.setCenterTextColor(Color.BLACK); // 饼状图中间的文字
		// no description text
		chart.setDescription("");
		chart.setNoDataTextDescription(mContext.getString(R.string.noCotent));

		// enable touch gestures
		chart.setTouchEnabled(true);

		chart.setDescription("单位%");
		// chart.setDescriptionPosition(x, y);//设置一个自定义的描述文本的位置像素在屏幕上。
		chart.setDescriptionColor(0xff0000ff);
		chart.setDragDecelerationEnabled(true);// 如果设置为真,图表润色后继续滚动。
		chart.setDragDecelerationFrictionCoef(1);// 减速摩擦系数在[0,1]区间,值越大表示速度会慢慢减少,例如如果设置为0,它将立即停止。1是一个无效的值,并将自动转换为0.999度。

		chart.animateX(2500, Easing.EasingOption.EaseInOutQuart);

		// Legend mLegend = chart.getLegend(); // 设置比例图
		// mLegend.setForm(LegendForm.LINE);// 设置比例图的形状，默认是方形
		// mLegend.setPosition(LegendPosition.RIGHT_OF_CHART); // 最右边显示
		// mLegend.setXEntrySpace(7f);
		// mLegend.setYEntrySpace(5f);
		// chart.animateXY(1000, 1000); // 设置动画

		container.addView(chart, -1, -1);

	}

	public void showAsDropDown(View anchor) {
		mPopupWindowRate.showAsDropDown(anchor);
	}

	public void showAsDropDown(View anchor, int xoff, int yoff) {
		mPopupWindowRate.showAsDropDown(anchor, xoff, yoff);
	}

	public void showAtLocation(View parent, int gravity, int x, int y) {
		mPopupWindowRate.showAtLocation(parent, gravity, x, y);
	}

	private void cleanView() {
		while (container.getChildCount() != 0) {
			container.removeViewAt(0);
		}
	}

	/**
	 * 
	 * @param count
	 *            分成几部分
	 * @param range
	 */
	public static PieData getPieData(Context context, ShareOfChannels item, String[] names, String title) {

		// 饼图数据
		/**
		 * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38 所以 14代表的百分比就是14%
		 */
		ArrayList<Entry> yValues = new ArrayList<Entry>(); // yVals用来表示封装每个饼块的实际数据
		yValues.add(new Entry(item.getCMCC_QUOTA(), 0));
		yValues.add(new Entry(item.getUNICOM_QUOTA(), 1));
		yValues.add(new Entry(item.getTELECOM_QUOTA(), 2));

		// y轴的集合
		PieDataSet pieDataSet = new PieDataSet(yValues, title/* 显示在比例图上 */);
		pieDataSet.setSliceSpace(0f); // 设置个饼状图之间的距离

		// 饼图颜色
		pieDataSet.setColors(COLORS_OF_CHANNEL_TYPE);

		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float px = 5 * (metrics.densityDpi / 160f);
		pieDataSet.setSelectionShift(px); // 选中态多出的长度

		PieData pieData = new PieData(names, pieDataSet);
		pieData.setValueTextSize(11);

		return pieData;
	}

}
