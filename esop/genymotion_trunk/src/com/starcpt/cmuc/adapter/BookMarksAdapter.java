package com.starcpt.cmuc.adapter;

import com.starcpt.cmuc.R;
import com.starcpt.cmuc.db.CmucStore;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class BookMarksAdapter extends CursorAdapter {
	
	private OnContentChangedListener mOnContentChangedListener;
	private LayoutInflater mInflater;

	public BookMarksAdapter(Context context, Cursor c) {
		super(context, c);
		mInflater = LayoutInflater.from(context);
	}
    
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String bookmarkTitle=cursor.getString(cursor.getColumnIndex(CmucStore.BookMarks.TITLE));
		String bookmarkUrl=cursor.getString(cursor.getColumnIndex(CmucStore.BookMarks.URL));
		TextView bookmarkTitleView=(TextView) view.findViewById(R.id.bookmark_title);
		TextView bookmarkUrlView=(TextView) view.findViewById(R.id.bookmark_url);
		bookmarkTitleView.setText(bookmarkTitle);
		bookmarkUrlView.setText(bookmarkUrl);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view=mInflater.inflate(R.layout.bookmark_item, null);
		return view;
	}
	
	public interface OnContentChangedListener {
        void onContentChanged(BookMarksAdapter adapter);
    }
    
    public void setOnContentChangedListener(OnContentChangedListener l) {
        mOnContentChangedListener = l;
    }
    
    @Override
	protected void onContentChanged() {
	 if (getCursor() != null && !getCursor().isClosed()) {
            if (mOnContentChangedListener != null) {
                mOnContentChangedListener.onContentChanged(this);
            }
        }
	}

}
