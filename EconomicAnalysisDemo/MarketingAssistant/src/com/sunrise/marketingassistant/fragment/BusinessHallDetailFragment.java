//package com.sunrise.marketingassistant.fragment;
//
//import com.baidu.lbsapi.panoramaview.PanoramaView;
//import com.baidu.lbsapi.panoramaview.PanoramaViewListener;
//import com.sunrise.marketingassistant.R;
//import com.sunrise.marketingassistant.utils.LogUtlis;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemSelectedListener;
//import android.widget.EditText;
//import android.widget.Spinner;
//
//public class BusinessHallDetailFragment extends BaseFragment implements OnItemSelectedListener, PanoramaViewListener {
//
//	private EditText mEditFeedback;
//	private PanoramaView mPanoView;
//
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//	}
//
//	public void onStart() {
//		super.onStart();
//		mPanoView.setShowTopoLink(true);
//	}
//
//	@Override
//	public void onDestroy() {
//		mPanoView.destroy();
//		super.onDestroy();
//	}
//
//	@Override
//	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.layout_businesshall_detail, null);
//
//		Spinner spinner = (Spinner) view.findViewById(R.id.spinner_template);
//		// spinner.setAdapter(ArrayAdapter.createFromResource(mBaseActivity,
//		// android.R.layout.simple_spinner_dropdown_item,
//		// R.array.feedback_template_list));
//		spinner.setOnItemSelectedListener(this);
//
//		mEditFeedback = (EditText) view.findViewById(R.id.editText_feedback);
//
//		mPanoView = (PanoramaView) view.findViewById(R.id.panorama);
//		mPanoView.setShowTopoLink(true);
//		mPanoView.setPanoramaZoomLevel(5);
//		mPanoView.setPanoramaViewListener(this);
//		mPanoView.setIndoorAlbumVisible();
//		mPanoView.setPanoramaImageLevel(PanoramaView.ImageDefinition.ImageDefinitionHigh);
////		mPanoView.setPanoramaByUid("28e700f15aae5418085cb3a7", PanoramaView.PANOTYPE_INTERIOR);
//		mPanoView.setPanorama(39.945, 116.404);
//		
//		return view;
//	}
//
//	@Override
//	public void onItemSelected(AdapterView<?> arg0, View arg1, int index, long arg3) {
//		String[] strs = getResources().getStringArray(R.array.feedback_template_content);
//		mEditFeedback.setText(strs[index]);
//	}
//
//	@Override
//	public void onNothingSelected(AdapterView<?> arg0) {
//		mEditFeedback.setText(null);
//	}
//
//	@Override
//	public void onLoadPanoramaBegin() {
//		LogUtlis.d(getClass().getSimpleName(), "全景图加载开始时回调");
//	}
//
//	@Override
//	public void onLoadPanoramaEnd(String arg0) {
//		LogUtlis.d(getClass().getSimpleName(), "全景图加载完成时回调");
//	}
//
//	@Override
//	public void onLoadPanoramaError(String arg0) {
//		LogUtlis.d(getClass().getSimpleName(), "全景图加载失败时回调");
//	}
//
//}
