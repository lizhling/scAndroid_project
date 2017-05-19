package com.sunrise.scmbhc.ui.fragment;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.utils.CommUtil;

public class QrCodeSacnReslutFragment extends BaseFragment{

	private BusinessMenu mBusinessMenu;
	private String mScanReslut;
	private String name;
	private TextView scanTextView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle bundle=getArguments();
		mBusinessMenu=bundle.getParcelable(ExtraKeyConstant.KEY_BUSINESS_INFO);
		mScanReslut=mBusinessMenu.getDescription();
		name=mBusinessMenu.getName();
	}
	@Override
	public void onStart() {
		super.onStart();
		mBaseActivity.setTitle(name);
		mBaseActivity.setLeftButtonVisibility(View.VISIBLE);
	}
	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_qr_code_reslut, container,false);
		scanTextView=(TextView) view.findViewById(R.id.scan_reslut);
		scanTextView.setText(mScanReslut);
		if(!mScanReslut.contains(App.sChinaMobileNetFlag)
				&& (mScanReslut.contains(App.sHttpFlag) || mScanReslut.contains(App.sHttpsFlag))){
			view.findViewById(R.id.safety_warning).setVisibility(View.VISIBLE);
		}
		setClikSpan();
		return view;
	}
	
	private void setClikSpan() {
		CharSequence text = scanTextView.getText(); 
		if (text instanceof Spannable) {  
            int end = text.length();
            Spannable sp = (Spannable) scanTextView.getText();  
            URLSpan[] spans = sp.getSpans(0, end, URLSpan.class);  
            SpannableStringBuilder style = new SpannableStringBuilder(text);  
            style.clearSpans();
            for (URLSpan span : spans) {  
                JayceSpan mySpan = new JayceSpan(span.getURL());  
                style.setSpan(mySpan, sp.getSpanStart(span), sp.getSpanEnd(span), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);  
            }  
            scanTextView.setText(style); 
        }
	}

	private  class JayceSpan extends ClickableSpan {  
        
        private String mSpan;  
  
        JayceSpan(String span) {  
            mSpan = span;  
        }  
  
        @Override  
        public void onClick(View widget) {
//        	if(mSpan.contains(App.sChinaMobileNetFlag)){
//        		CommUtil.visitWebView(mBaseActivity, mSpan);
//        	}else{
//        		Intent intent = new Intent(Intent.ACTION_VIEW);
//    	        intent.setData(Uri.parse(mSpan));
//    	        startActivity(intent);
//        	}
        	//都在应用内打开
        	CommUtil.visitWebView(mBaseActivity, mSpan);
	    
        }  
    }
	/* 
	 * 功能: 为用户行为提供页面名称
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 * 
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.QrCodeSacnReslutFragment;
	}
}
