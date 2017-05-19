package com.sunrise.scmbhc.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class ExpandingGridView extends GridView {

	public ExpandingGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ExpandingGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExpandingGridView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
