package com.sunrise.scmbhc.ui.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sunrise.scmbhc.R;

/**
 * 
 */
/**  
 *   
 * @Project: scmbhc  
 * @ClassName: CornerListView  
 * @Description: 圆角ListView 实现
 * @Author qinhubao  
 * @CreateFileTime: 2014年1月15日 上午9:12:23  
 * @Modifier: qinhubao  
 * @ModifyTime: 2014年1月15日 上午9:12:23  
 * @ModifyNote: 
 * @version   
 *   
 */
public class CornerListView extends ListView {
	public CornerListView(Context context) {
		super(context);
	}

	public CornerListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CornerListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			int itemnum = pointToPosition(x, y);
			if (itemnum == AdapterView.INVALID_POSITION)
				break;
			else {
				if (itemnum == 0) {
					if (itemnum == (getAdapter().getCount() - 1)) {
						//只有一项
						setSelector(R.drawable.list_corner_round);
					} else {
						//第一项
						setSelector(R.drawable.list_corner_round_top);
					}
				} else if (itemnum == (getAdapter().getCount() - 1))
					//最后一项
					setSelector(R.drawable.list_corner_round_bottom);
				else {
					//中间项
					setSelector(R.drawable.list_corner_round_center);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}
}