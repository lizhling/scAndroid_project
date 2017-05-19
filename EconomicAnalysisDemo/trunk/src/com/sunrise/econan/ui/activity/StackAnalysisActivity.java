package com.sunrise.econan.ui.activity;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;

import com.sunrise.econan.model.CityInfo;
import com.sunrise.econan.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ViewFlipper;

public class StackAnalysisActivity extends Activity {

	private ViewFlipper mViewFlipper;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stack_analysis_layout);

		mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper1);
		mViewFlipper.addView(initStackedView(getIntent().getParcelableArrayListExtra("data")));
		mViewFlipper.addView(getDoughnutChartView(getIntent().getParcelableArrayListExtra("data")));
		mViewFlipper.addView(getLineChart(getIntent().getParcelableArrayListExtra("data")));
	}

	private StackAnalysisActivity getThis() {
		return this;
	}

	/**
	 * 创建环状图
	 * 
	 * @param arrayList
	 * @return
	 */
	private View getDoughnutChartView(ArrayList<Parcelable> arrayList) {
		DefaultRenderer renderer = new DefaultRenderer();
		{
			renderer.setBackgroundColor(Color.TRANSPARENT);
			renderer.setApplyBackgroundColor(true);
			renderer.setLabelsTextSize(getResources().getDimensionPixelSize(R.dimen.textsize_small));
			renderer.setChartTitle("城市平均指数图");
			renderer.setLegendTextSize(getResources().getDimensionPixelSize(R.dimen.textsize_small));
		}

		// CategorySeries categorySeries = new CategorySeries("Vehicles Chart");
		MultipleCategorySeries categorySeries = new MultipleCategorySeries("平均指数图");

		float[] hsv = { 0, 1, 1 };
		double[] values = new double[arrayList.size()];
		String[] titles = new String[arrayList.size()];
		for (int index = 0; index < arrayList.size(); ++index) {
			CityInfo information = (CityInfo) arrayList.get(index);
			values[index] = information.getAverageScore();
			titles[index] = information.getName();

			SimpleSeriesRenderer childr = new SimpleSeriesRenderer();

			hsv[0] = 360f * index / arrayList.size();
			childr.setColor(Color.HSVToColor(hsv));
			renderer.addSeriesRenderer(childr);
		}
		categorySeries.add(titles, values);

		return ChartFactory.getDoughnutChartView(getThis(), categorySeries, renderer);
	}

	/**
	 * 创建柱状图
	 * 
	 * @param trueTask
	 */
	private View initStackedView(ArrayList<Parcelable> arrayList) {
		GraphicalView view = ChartFactory.getBarChartView(getThis(), getBarDataset(arrayList), getBarRenderer(arrayList), Type.STACKED); // Type.STACKED
		// view.setBackgroundColor(Color.BLACK);

		view.addPanListener(new PanListener() {
			@Override
			public void panApplied() {
				Log.e(this.getClass().getName(), "panApplied");
			}
		});

		return view;
	}

	/**
	 * 描绘器设置
	 * 
	 * @param arrayList
	 * @return
	 */
	public XYMultipleSeriesRenderer getBarRenderer(ArrayList<Parcelable> arrayList) {

		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		// 通过SimpleSeriesDenderer设置描绘器的颜色

		SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		r.setColor(Color.rgb(1, 128, 205)); // 定义柱状图的颜色
		r.setGradientEnabled(true);
		r.setGradientStart(0, 0xff0000ff);
		r.setGradientStop(100, 0xffff0000);
		renderer.addSeriesRenderer(r);

		setChartSettings(renderer, arrayList);// 设置描绘器的其他属性

		return renderer;
	}

	private void setChartSettings(XYMultipleSeriesRenderer renderer, ArrayList<Parcelable> arrayList) {

		renderer.setChartTitle("城市指标得分表");// 设置柱图名称
		renderer.setXTitle("城市名称");// 设置X轴名称
		renderer.setYTitle("指标得分");// 设置Y轴名称

		renderer.setXAxisMin(0);// 设置X轴的最小值为0.5
		renderer.setXAxisMax(22);// 设置X轴的最大值为5
		renderer.setYAxisMin(0);// 设置Y轴的最小值为0
		renderer.setYAxisMax(100);// 设置Y轴最大值为500
		// renderer.setDisplayChartValues(true); // 设置是否在柱体上方显示值
		renderer.setShowGrid(true);// 设置是否在图表中显示网格
		// renderer.setXLabels(informations.size());// 设置X轴显示的刻度标签的个数
		renderer.setBarSpacing(2); // 柱状间的间隔
		renderer.setZoomButtonsVisible(true);

		renderer.setAxisTitleTextSize(getResources().getDimension(R.dimen.textsize_small));
		renderer.setXLabelsAngle(30f);
		renderer.setLabelsTextSize(getResources().getDimension(R.dimen.textsize_tiny));

		// 为X轴的每个柱状图设置底下的标题 比如 福建 ，广东.....
		int count = 1;
		for (Parcelable parcel : arrayList) {
			CityInfo information = (CityInfo) parcel;
			renderer.addXTextLabel(count, information.getName());
			count++;
		}

	}

	/**
	 * 数据设置
	 * 
	 * @param arrayList
	 * @return
	 */
	private XYMultipleSeriesDataset getBarDataset(ArrayList<Parcelable> arrayList) {

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		CategorySeries series = new CategorySeries("标题 (单位：分)");
		// 声明一个柱形图
		// 为柱形图添加值
		for (Parcelable parcel : arrayList) {
			CityInfo info = (CityInfo) parcel;
			series.add(info.getIndicatorScore());
		}

		dataset.addSeries(series.toXYSeries());// 添加该柱形图到数据设置列表

		return dataset;
	}

	public void onRefresh(View view) {
		mViewFlipper.showNext();
	}

	private View getLineChart(ArrayList<Parcelable> arrayList) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		{
			{// 第一条线
				CategorySeries series = new CategorySeries("指标得分 (单位：分)");
				for (Parcelable parcel : arrayList) {
					CityInfo info = (CityInfo) parcel;
					series.add(info.getIndicatorScore());
				}
				dataset.addSeries(series.toXYSeries());
			}
			{// 第二条线
				CategorySeries series = new CategorySeries("平均得分 (单位：分)");
				for (Parcelable parcel : arrayList) {
					CityInfo info = (CityInfo) parcel;
					series.add(info.getAverageScore());
				}
				dataset.addSeries(series.toXYSeries());
			}
		}
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		{
			renderer.setXAxisMax(20);
			renderer.setYAxisMax(100);
			renderer.setXAxisMin(0);
			renderer.setYAxisMin(0);
			renderer.setBarSpacing(2);
			renderer.setChartTitle("有道理");
			renderer.setLegendHeight(20);

			renderer.setYTitle("得分");
			renderer.setXTitle("城市名称");
			renderer.setMarginsColor(Color.TRANSPARENT);

			int count = 1;
			for (Parcelable parcel : arrayList) {
				CityInfo information = (CityInfo) parcel;
				renderer.addXTextLabel(count, information.getName());
				count++;
			}
			{// 第一条线的绘制颜色等信息
				XYSeriesRenderer srenderer = new XYSeriesRenderer();
				srenderer.setColor(Color.BLUE);
				srenderer.setPointStyle(PointStyle.DIAMOND);
				renderer.addSeriesRenderer(srenderer);
			}
			{// 第二条线的绘制颜色等信息
				XYSeriesRenderer srenderer = new XYSeriesRenderer();
				srenderer.setColor(Color.RED);
				srenderer.setPointStyle(PointStyle.CIRCLE);
				renderer.addSeriesRenderer(srenderer);
			}
			System.err.println("============================= " + renderer.getSeriesRendererCount());
		}
		renderer.setBackgroundColor(Color.TRANSPARENT);
		return ChartFactory.getRangeBarChartView(getThis(), dataset, renderer, Type.STACKED);
	}
}
