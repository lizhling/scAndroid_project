package com.sunrise.scmbhc.broadcast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.AppActionConstant;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.entity.PhoneCurInfo;
import com.sunrise.scmbhc.entity.UseCondition;
import com.sunrise.scmbhc.entity.UserBaseInfo;
import com.sunrise.scmbhc.service.AppWidgetService;
import com.sunrise.scmbhc.ui.activity.SingleFragmentActivity;
import com.sunrise.scmbhc.ui.fragment.LoginFragment;
import com.sunrise.scmbhc.utils.LogUtlis;
import com.sunrise.scmbhc.utils.UnitUtils;

public class AppWidgetProviderBill extends AppWidgetProvider {

	private static final String FORMAT_SMS = "%.0f(剩)/%.0f(全)";
	private static final String FORMAT_CALL = "%.0f(剩)/%.0f(全)";
	private static final String FORMAT_OPTION = "<font color=\"#A7ED14\">%.0f(剩)</font>/<font color=\"#13C1ED\">%.0f(全)</font>";
	private static final int THRESHOLD = 90; // 变色阈值

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		LogUtlis.showLogD(getClass().getName(), "onUpdate ");
		startRefreshService(context);

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_bill);
		operateSet(context, appWidgetManager, views);
		appWidgetManager.updateAppWidget(appWidgetIds, views);
	}

	public void onEnabled(Context context) {
		super.onEnabled(context);
		LogUtlis.showLogD(getClass().getName(), "onEnabled ");
	}

	public void onDeleted(Context context, int[] appWidgetIds) {
		LogUtlis.showLogD(getClass().getName(), "onDeleted ");
		startRefreshService(context);
	}

	public void onDisabled(Context context) {
		LogUtlis.showLogD(getClass().getName(), "onDisabled ");
		UserInfoControler.getInstance().setWidgetPhoneNumber(null);
	}

	public void onReceive(Context context, Intent intent) {
		if (AppActionConstant.ACTION_REFRESH.equals(intent.getAction())) {// 刷新页面
			doChange(context, AppWidgetManager.getInstance(context), getAppWidgetIds(context), false);
		}

		else if (AppActionConstant.ACTION_REFRESH_REQUEST.equals(intent.getAction())) {// 请求刷新数据
			doChange(context, AppWidgetManager.getInstance(context), getAppWidgetIds(context), true);
			startRefreshService(context);
		}

		else
			super.onReceive(context, intent);

	}

	private void doChange(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, boolean isStartLoading) {
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_bill);

		operateSet(context, appWidgetManager, views);
		if (isStartLoading) {
			views.setViewVisibility(R.id.refreshing, View.VISIBLE);
			views.setViewVisibility(R.id.refresh, View.GONE);
		} else {
			viewSet(context, appWidgetManager, views, UserInfoControler.getInstance().getPhoneCurMsg(), UserInfoControler.getInstance().getUserBaseInfo());
			views.setViewVisibility(R.id.refreshing, View.GONE);
			views.setViewVisibility(R.id.refresh, View.VISIBLE);
		}

		appWidgetManager.updateAppWidget(appWidgetIds, views);
	}

	private void operateSet(Context context, AppWidgetManager appWidgetManager, RemoteViews views) {

		String packagename = context.getPackageName();
		LogUtlis.showLogI("appwidget", "packagename" + packagename);
		if (packagename == null) {
			packagename = context.getApplicationContext().getPackageName();
			LogUtlis.showLogI("appwidget", "when packagename is null packagename" + packagename);
		}
		String phoneNumber;
		boolean isout = App.sSettingsPreferences.getLoginOutCode();
		LogUtlis.showLogI("appwidget", "isout:" + isout);
		if ((!UserInfoControler.getInstance().isAutoLogin()) && isout) {
			phoneNumber = null;
		} else {
			phoneNumber = UserInfoControler.getInstance().getUserName();
		}
		if (phoneNumber != null) // 如果已登录
		{
			// 隐藏登录按钮
			views.setViewVisibility(R.id.login_parent, View.GONE);
			// 显示内容界面
			views.setViewVisibility(R.id.content, View.VISIBLE);
		} else {
			// 隐藏登录按钮
			views.setViewVisibility(R.id.login_parent, View.VISIBLE);
			// 隐藏内容界面
			views.setViewVisibility(R.id.content, View.GONE);
		}

		// 设置登录按钮动作
		views.setOnClickPendingIntent(R.id.login, getLoginIntent(context));
		// views.setOnClickPendingIntent(R.id.app_link,
		// getStartAppIntent(context));
		// 显示号码
		views.setTextViewText(R.id.userHoneNumber, UserInfoControler.getInstance().getUserName());
		// 设置连接app
		// views.setOnClickPendingIntent(R.id.app_link2,
		// getStartAppIntent(context));
		// 设置刷新按钮
		views.setOnClickPendingIntent(R.id.refresh, getRefreshIntent(context));
	}

	private void viewSet(Context context, AppWidgetManager appWidgetManager, RemoteViews views, PhoneCurInfo info, UserBaseInfo baseinfo) {

		if (info == null)// if (TextUtils.isEmpty(phoneinfo))
			return;
		views.setViewVisibility(R.id.refreshing, View.GONE);
		views.setViewVisibility(R.id.refresh, View.VISIBLE);

		// PhoneCurInfo info = JsonUtils.parseJsonStrToObject(phoneinfo,
		// PhoneCurInfo.class);

		// 显示积分
		if (info.getCredits() < 0) {
			views.setTextViewText(R.id.credits, "未开通");
		} else {
			views.setTextViewText(R.id.credits, String.valueOf(info.getCredits()));
		}

		// 显示账单余额
		updateViewsBill(views, info);

		// 显示套餐余量使用情况
		updateViewsFreeInfo(views, info, context);

		// 显示刷新
		views.setTextViewText(R.id.lastModify, new SimpleDateFormat("yy年MM月dd HH:mm:ss").format(new Date(System.currentTimeMillis())));

		if (baseinfo == null)// if (TextUtils.isEmpty(userBaseInfo))
			return;
		// 显示归属地
		// UserBaseInfo baseinfo = null;

		// try {
		// baseinfo = JsonUtils.parseJsonStrToObject(userBaseInfo,
		// UserBaseInfo.class);
		// } catch (JsonParseException e) {
		// try {
		// baseinfo = JsonUtils.parseJsonStrToObject(userBaseInfo,
		// UserBaseInfo.UserBaseAdapter.class);
		// } catch (Exception e1) {
		// LogUtlis.showLogE(e.getMessage(), "userBaseInfo = " + userBaseInfo);
		// e.printStackTrace();
		// }
		// }

		// if (baseinfo != null) {
		views.setTextViewText(R.id.brand, baseinfo.getBRAND_NAME());
		views.setViewVisibility(R.id.brand, View.VISIBLE);
		// }
	}

	/**
	 * 更新账单显示
	 * 
	 * @param views
	 * @param info
	 */
	private void updateViewsBill(RemoteViews views, PhoneCurInfo curInfo) {
		if (curInfo != null) {
			// 显示本月消费<font color=\"#A7ED14\">%.0f(剩)</font>/<font
			// color=\"#13C1ED\">%.0f(全)</font>

			views.setTextViewText(R.id.costInThisMonth, "本月消费：" + curInfo.getCoat());
			// 显示当前余额
			double percent = 0;
			double totle = curInfo.getBalance() + curInfo.getCoat();
			double used = curInfo.getCoat();
			if (totle > 0 && used > 0) {
				percent = used * 100 / totle;
			} else {
				percent = 0;
			}
			if (percent < THRESHOLD) {
				views.setTextViewText(R.id.balance, Html.fromHtml(String.format("当前余额：<font color=\"#A7ED14\">%.2f</font>", curInfo.getBalance())));
			} else {
				views.setTextViewText(R.id.balance, Html.fromHtml(String.format("当前余额：<font color=\"#ff1105\">%.2f</font>", curInfo.getBalance())));
			}

		} else {
			// 显示本月消费
			views.setTextViewText(R.id.costInThisMonth, "读取失败");
			// 显示当前余额
			views.setTextViewText(R.id.balance, "读取失败");
		}

	}

	/**
	 * 显示套餐余量使用情况
	 * 
	 * @param views
	 * @param info
	 * @param context
	 */
	private void updateViewsFreeInfo(RemoteViews views, PhoneCurInfo info, Context context) {
		// 设置话费图标：

		// 分钟
		UseCondition condition = info.getConditionCall();
		if (condition != null) {
			if (condition.getTotle() >= 0) {
				// views.setTextViewText(R.id.callRemainder,
				// String.format(FORMAT_CALL, condition.getSurplus(),
				// condition.getTotle()));
				views.setTextViewText(R.id.callRemainder, Html.fromHtml(String.format(FORMAT_OPTION, condition.getSurplus(), condition.getTotle())));
			}
			views.setImageViewBitmap(R.id.img_callRemainder, getBitmap(context, condition.getUsed(), condition.getTotle()));
		}
		// else
		// views.setTextViewText(R.id.callRemainder,
		// PhoneFreeQuery.getCodeMeans(condition.getTotle()));
		// 短信
		condition = info.getConditionSMS();
		if (condition != null) {
			if (condition.getTotle() >= 0) {
				// views.setTextViewText(R.id.msgRemainder,
				// String.format(FORMAT_SMS, condition.getSurplus(),
				// condition.getTotle()));
				views.setTextViewText(R.id.msgRemainder, Html.fromHtml(String.format(FORMAT_OPTION, condition.getSurplus(), condition.getTotle())));
			}
			views.setImageViewBitmap(R.id.img_msgRemainder, getBitmap(context, condition.getUsed(), condition.getTotle()));
		}
		// else
		// views.setTextViewText(R.id.msgRemainder,
		// PhoneFreeQuery.getCodeMeans(condition.getTotle()));
		// 流量
		condition = info.getConditionFlow(null);
		if (condition != null) {
			if (condition.getTotle() >= 0) {
				views.setTextViewText(R.id.flowRemainder, Html.fromHtml(String.format(
						"<font color=\"#A7ED14\">%s(剩)</font>/<font color=\"#13C1ED\">%s(全)</font>", condition.getSurplusString(), condition.getTotleString())));
			}
			// else
			// views.setTextViewText(R.id.msgRemainder,
			// PhoneFreeQuery.getCodeMeans(condition.getTotle()));

			// 流量图标
			views.setImageViewBitmap(R.id.progressBarFlow, getBitmap(context, condition.getUsed(), condition.getTotle()));
		}

		if (info != null) {
			views.setImageViewBitmap(R.id.img_balance, getBitmap(context, info.getCoat(), info.getBalance() + info.getCoat()));
		}
	}

	private Bitmap getBitmap(Context context, double used, double totle) {
		final int width = context.getResources().getDimensionPixelSize(R.dimen.text_size_3) * 3;
		final int height = width;
		// TODO: 注释修改之前的
		// final int color = 0xaaff1105;
		final int color = 0xaaA7ED14;
		final float width_circle = UnitUtils.dip2px(context, 3);

		Paint paint = new Paint();
		Rect bounds = new Rect();

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		final String text1 = "已用";
		double percent = 0;
		if (totle > 0 && used > 0) {
			percent = used * 100 / totle;
		} else {
			percent = 0;
		}
		final String text2 = UseCondition.getPercentString(used, totle);
		if (percent < THRESHOLD) {
			paint.setColor(color);
		} else {
			paint.setColor(0xaaff1105);
		}
		paint.setTextSize(context.getResources().getDimension(R.dimen.text_size_4));
		paint.setTextScaleX(0.8f);

		paint.getTextBounds(text1, 0, text1.length(), bounds);
		canvas.drawText(text1, width - bounds.width() >> 1, (height - width_circle) / 2, paint);

		paint.getTextBounds(text2, 0, text2.length(), bounds);
		canvas.drawText(text2, width - bounds.width() >> 1, (height + width_circle) / 2 + bounds.height(), paint);

		// 绘制圆环
		paint.setColor(0x55e9f0fe);
		// paint.setColor(0x55e9f0fe);// 修改前颜色值0x55e9f0fe
		paint.setStyle(Paint.Style.STROKE);

		RectF boundsf = new RectF();
		boundsf.set(width_circle / 2, width_circle / 2, width - width_circle / 2, height - width_circle / 2);
		paint.setStrokeWidth(width_circle);
		canvas.drawOval(boundsf, paint);

		// 绘制弧
		paint.setColor(color);
		float rate = (float) ((totle - used) * 360 / totle);
		canvas.drawArc(boundsf, 90, rate, false, paint);

		return bitmap;
	}

	private PendingIntent getLoginIntent(Context context) {
		Intent intent = new Intent(context, SingleFragmentActivity.class);
		// intent.putExtra(App.ExtraKeyConstant.KEY_FRAGMENT,
		// LoginForWidgetFragment.class);
		intent.putExtra(ExtraKeyConstant.KEY_FINISH_ACTIVITY, true);
		intent.putExtra(App.ExtraKeyConstant.KEY_FRAGMENT, LoginFragment.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}

	private PendingIntent getStartAppIntent(Context context) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		// intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setPackage(context.getPackageName());
		ResolveInfo resolveInfo = context.getPackageManager().queryIntentActivities(intent, 0).iterator().next();
		intent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
		// intent.setComponent(new
		// ComponentName(resolveInfo.activityInfo.packageName,
		// resolveInfo.activityInfo.packageName+".MainActivity"));
		return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	private PendingIntent getRefreshIntent(Context context) {
		Intent intent = new Intent(AppActionConstant.ACTION_REFRESH_REQUEST);
		return PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private int[] getAppWidgetIds(Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName provider = new ComponentName(context.getPackageName(), this.getClass().getName());
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(provider);
		return appWidgetIds;
	}

	/**
	 * 启动刷新信息线程
	 * 
	 * @param context
	 */
	private void startRefreshService(Context context) {
		String packagename = context.getPackageName();
		LogUtlis.showLogI("appwidget", "packagename" + packagename);
		if (packagename == null) {

			packagename = context.getApplicationContext().getPackageName();
			LogUtlis.showLogI("appwidget", "when packagename is null packagename" + packagename);
		}
		boolean isout = App.sSettingsPreferences.getLoginOutCode();
		String phoneNumber;
		if ((!UserInfoControler.getInstance().isAutoLogin()) && isout) {
			phoneNumber = null;
		} else {
			phoneNumber = UserInfoControler.getInstance().getUserName();
		}
		if (TextUtils.isEmpty(phoneNumber)) {

			/*
			 * doChange(context, AppWidgetManager.getInstance(context),
			 * getAppWidgetIds(context), );
			 */
			return;
		}
		Intent intent = new Intent(context, AppWidgetService.class);
		intent.putExtra(ExtraKeyConstant.KEY_PHONE_NUMBER, phoneNumber);
		context.startService(intent);

	}

	/**
	 * 判断指定包名的进程是否运行
	 * 
	 * @param context
	 * @param packageName
	 *            指定包名
	 * @return 是否运行
	 */
	public static boolean isRunning(Context context, String packageName) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		for (RunningAppProcessInfo rapi : infos) {
			if (rapi.processName.equals(packageName))
				return true;
		}
		return false;
	}
}
