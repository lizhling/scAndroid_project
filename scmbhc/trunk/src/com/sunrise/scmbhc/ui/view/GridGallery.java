package com.sunrise.scmbhc.ui.view;

import java.util.ArrayList;
import java.util.List;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.adapter.GridGalleryAdapter;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.ui.view.HorizontalScrollPanel.OnViewChangedListener;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class GridGallery extends RelativeLayout implements OnViewChangedListener{
	private HorizontalScrollPanel mGalleryScrollPanel;
	private ArrayList<ImageView> mGalleryIndexImageViews=new ArrayList<ImageView>();
	private ArrayList<GridGalleryAdapter> adapters=new ArrayList<GridGalleryAdapter>();
	private LinearLayout mGalleryPageIndexPanel;
	private int mCurrentGalleryIndex=0;
	private int mGalleryPageNumbers;
	private Context mContext;
	private int numbersOfPage;
	private ChildGridViewItemClickListener childGridViewItemClickListener;
	private ChildGridViewItemLongClickListener childGridViewItemLongClickListener;
	public GridGallery(Context context) {
		super(context);
		mContext=context;
	}

	public GridGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GridGallery);
		numbersOfPage=array.getInteger(R.styleable.GridGallery_numbersOfPage, 8);
		LayoutInflater.from(context).inflate(R.layout.grid_gallery, this, true);
		init();
	}

   private void init(){
	   mGalleryScrollPanel=(HorizontalScrollPanel) findViewById(R.id.horizontal_scroll_panel);
	   mGalleryPageIndexPanel=(LinearLayout) findViewById(R.id.imglinear);
   }

   
   public void initGallery(ArrayList<BusinessMenu> datas,int numColumns){
		mGalleryScrollPanel.setBackgroundColor(Color.TRANSPARENT);
	 	mGalleryScrollPanel.removeAllViews();
	 	mGalleryPageIndexPanel.removeAllViews();
	 	mGalleryIndexImageViews.clear();
	 	mGalleryScrollPanel.setOnViewChangedListener(this);
	 	int mPageNum = (int) Math.ceil(datas.size() / (double)numbersOfPage);
	 	for(int i = 0; i <mPageNum; i++){
	 		GridView view=createView(datas,numbersOfPage,i, numColumns);
	 		ImageView imageView=(ImageView) createGuideDot(mContext);
	 		mGalleryScrollPanel.addView(view);
	 		mGalleryPageIndexPanel.addView(imageView);
	 	}
	 	
	 	mGalleryPageNumbers = mGalleryScrollPanel.getChildCount();
	 	mCurrentGalleryIndex = 0;
	 	for(int j = 0; j < mGalleryPageNumbers; j++){
	 		ImageView view = (ImageView) mGalleryPageIndexPanel.getChildAt(j);
	 		view.setEnabled(true);
	 		view.setTag(j);
	 		mGalleryIndexImageViews.add(view);
	 	}	    	
	 	mGalleryIndexImageViews.get(mCurrentGalleryIndex).setImageResource(R.drawable.dot_item_index_focused);
	 	if(mGalleryPageNumbers<=1)
	 		mGalleryPageIndexPanel.setVisibility(View.GONE);
	 	else
	 		mGalleryPageIndexPanel.setVisibility(View.VISIBLE);
	 }

	    private View createGuideDot(final Context context){
			LayoutInflater inflate = LayoutInflater.from(context);
			ImageView view = (ImageView) inflate.inflate(R.layout.dot_item, null);
			return view;
		}
		
		private GridView createView(ArrayList<BusinessMenu> datas,int numberOfPage,int pageIndex,int numColumns){
			LayoutInflater inflate = LayoutInflater.from(mContext);
			GridView gridView = (GridView) inflate.inflate(R.layout.app_menus_gridview, null);
			gridView.setNumColumns(numColumns);
			GridGalleryAdapter gridViewAdapter=new GridGalleryAdapter(mContext,datas,numberOfPage,pageIndex);
			final List<BusinessMenu> subList=gridViewAdapter.getSubList();
			gridView.setAdapter(gridViewAdapter);
			adapters.add(gridViewAdapter);
			gridViewAdapter.notifyDataSetChanged();
			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position,long id) {
					if(childGridViewItemClickListener!=null){
						childGridViewItemClickListener.onItemClik(arg0, arg1, position, id, subList);
					}
				}
				
			});
			
			gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int position, long id) {
					if(childGridViewItemLongClickListener!=null){
						childGridViewItemLongClickListener.onItemLongClick(arg0, arg1, position, id, subList);
					}
					return true;
				}
			});
			return gridView;
		}

		private void setCurPoint(int index){
			if(index <0 || index > mGalleryPageNumbers - 1 || index == mCurrentGalleryIndex)
				return ;
			mGalleryIndexImageViews.get(mCurrentGalleryIndex).setImageResource(R.drawable.dot_item_index_noraml);
			mGalleryIndexImageViews.get(index).setImageResource(R.drawable.dot_item_index_focused);
			mCurrentGalleryIndex = index;
		}
		
		@Override
		public void onViewChange(int curView) {
			setCurPoint(curView);
		}
		
		
		public int getNumbersOfPage() {
			return numbersOfPage;
		}

		public void setNumbersOfPage(int numbersOfPage) {
			this.numbersOfPage = numbersOfPage;
		}


		public interface ChildGridViewItemClickListener {
             void onItemClik(AdapterView<?> arg0, View arg1, int position,long id,List<BusinessMenu> list);
		}

		public interface ChildGridViewItemLongClickListener {
            void onItemLongClick(AdapterView<?> arg0, View arg1, int position,long id,List<BusinessMenu> list);
		}

		public void setChildGridViewItemClickListener(
				ChildGridViewItemClickListener childGridViewItemClickListener) {
			this.childGridViewItemClickListener = childGridViewItemClickListener;
		}
		
		public void setChildGridViewItemLongClickListener(
				ChildGridViewItemLongClickListener childGridViewItemLongClickListener) {
			this.childGridViewItemLongClickListener = childGridViewItemLongClickListener;
		}

		public ArrayList<GridGalleryAdapter> getAdapters() {
			return adapters;
		}
		
		
}
