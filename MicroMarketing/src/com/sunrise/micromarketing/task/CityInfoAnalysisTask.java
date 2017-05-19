package com.sunrise.micromarketing.task;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.sunrise.micromarketing.entity.CityInfo;

import android.content.Context;

/**
 * 解析用户列表
 * 
 * @author fuheng
 */
public class CityInfoAnalysisTask extends GenericTask {

	private ArrayList<CityInfo> arraylist;

	public static final String KEY_CONTEXT = "context";

	public static final String KEY_URL = "url";

	/**
	 * 获取数据流
	 * 
	 * @param param
	 * @return
	 * @throws IOException
	 */
	private InputStream getInputStream(TaskParams param) throws IOException {
		Context context = (Context) param.get(KEY_CONTEXT);
		return context.getAssets().open(param.getString(KEY_URL));
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {

		InputStream inputstream = null;
		try {
			inputstream = getInputStream(params[0]);

			arraylist = new ArrayList<CityInfo>();

			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();

			xpp.setInput(inputstream, "utf-8");

			int eventType = xpp.getEventType();

			Stack<String> stack = new Stack<String>();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_DOCUMENT) {
				} else if (eventType == XmlPullParser.START_TAG) {
					stack.add(xpp.getName());

					if (stack.lastElement().equals("element")) {
						CityInfo info = new CityInfo();
						for (int i = 0; i < xpp.getAttributeCount(); i++) {
							String key = xpp.getAttributeName(i);
							String value = xpp.getAttributeValue(i);
							if (key.equals("id"))
								info.setId(value);
							else if (key.equals("name"))
								info.setName(value);
							else if (key.equals("average"))
								info.setAverageScore(Float.parseFloat(value));
							else if (key.equals("indicator"))
								info.setIndicatorScore(Float.parseFloat(value));
							else if (key.equals("ranking"))
								info.setRanking(Integer.parseInt(value));
							else if (key.equals("level"))
								info.setLevel(Integer.parseInt(value));
						}
						getArraylist().add(info);
					}

				} else if (eventType == XmlPullParser.END_TAG) {
					stack.removeElement(stack.lastElement());
				} else if (eventType == XmlPullParser.TEXT) {

				}
				eventType = xpp.next();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return TaskResult.IO_ERROR;
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return TaskResult.NOT_FOLLOWED_ERROR;
		} finally {
			if (inputstream != null)
				try {
					inputstream.close();
				} catch (IOException e) {
					e.printStackTrace();
					return TaskResult.IO_ERROR;
				}
		}

		return TaskResult.OK;
	}

	public ArrayList<CityInfo> getArraylist() {
		return arraylist;
	}
}
