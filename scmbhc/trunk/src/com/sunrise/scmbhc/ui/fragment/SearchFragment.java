package com.sunrise.scmbhc.ui.fragment;

import java.util.Observable;
import java.util.Observer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.adapter.SpinnerAdapter;
import com.sunrise.scmbhc.entity.SearchKeyWord;
import com.sunrise.scmbhc.entity.SearchTag;
import com.sunrise.scmbhc.ui.view.KeywordsFlow;
import com.sunrise.scmbhc.ui.view.MySpinner;
public class SearchFragment extends BaseFragment implements Observer {
public static SearchKeyWord sSearchKeyWord;
private MySpinner mSpinner;
private EditText mEditText;
private SpinnerAdapter mSpinnerAdapter;
private String[] mSearchTypes;
private KeywordsFlow mKeywordsFlow;
	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search, container, false);
		mEditText=(EditText) view.findViewById(R.id.searck_key);
		mSearchTypes=mBaseActivity.getResources().getStringArray(R.array.searchType);
		mSpinnerAdapter=new SpinnerAdapter(mBaseActivity,mSearchTypes);
		mSpinner=(MySpinner) view.findViewById(R.id.search_type);
		mSpinner.setAdapter(mSpinnerAdapter);
		if(sSearchKeyWord!=null){
			mSpinner.setSelectIndex(sSearchKeyWord.getSearchType());
			mEditText.setText(sSearchKeyWord.getKeyWord());
		}else{
			mSpinner.setSelectIndex(0);
		}
		mKeywordsFlow=(KeywordsFlow) view.findViewById(R.id.keywords_flow);
		updateKeywordsFlow();
		mKeywordsFlow.setOnItemClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int id = ((TextView) v).getId();
				SearchTag searchTag=mKeywordsFlow.getSearchTag(id);
				goToSearch(searchTag.getTagName(),SearchTag.SYSTEM_TYPE);		
			}
		});   
		view.findViewById(R.id.search_bt).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String key=mEditText.getText().toString();
				if(TextUtils.isEmpty(key)){
					Toast.makeText(mBaseActivity, R.string.search_hint, Toast.LENGTH_SHORT).show();
					return;
				}else{
					goToSearch(key,SearchTag.USER_TYPE);
				}
				
			}
		});
		
		App.sCommonlySearchsObservable.addObserver(this);
		return view;
	}
	
	private void goToSearch(String key,int tagType) {
		int type=mSpinner.getSelectIndex();
		sSearchKeyWord=new SearchKeyWord(type, key,tagType);
		BaseFragment searchReslutFragment=new SearchReslutFragment();
		searchReslutFragment.startFragment(mBaseActivity, R.id.fragmentContainer);
	}
	
	@Override
		public void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			mKeywordsFlow.go2Show(KeywordsFlow.ANIMATION_OUT);
		}
	
	@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			mBaseActivity.setLeftButtonVisibility(View.VISIBLE);
			mBaseActivity.setTitle(R.string.search);
		}
	
	private void updateKeywordsFlow() {
		mKeywordsFlow.rubKeywords();
		int size=App.sCommonlySearchs.size();
	    for (int i = 0; i < size; i++) {
		     SearchTag searchTag=App.sCommonlySearchs.get(i);
		     mKeywordsFlow.feedKeyword(searchTag);
	      }
	}

	@Override
	public void update(Observable observable, Object data) {
		updateKeywordsFlow();
	}
	/* 
	 * 功能: 为用户行为提供页面名称
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 * 
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.SearchFragment;
	}
	
	

}
 