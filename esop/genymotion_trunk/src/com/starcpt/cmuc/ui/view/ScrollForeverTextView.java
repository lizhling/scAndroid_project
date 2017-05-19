package com.starcpt.cmuc.ui.view;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 自动滚动textview
 * @author fuheng
 *
 */
public class ScrollForeverTextView extends TextView {

	public ScrollForeverTextView(Context context) {
		super(context);
	}

	public ScrollForeverTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollForeverTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		setHorizontalFadingEdgeEnabled(true);
        setEllipsize(TruncateAt.MARQUEE);
        setSingleLine(true);
	}

	@Override
	public boolean isFocused() {
		return true;
	}

}
