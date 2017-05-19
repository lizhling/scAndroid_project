package com.sunrise.scmbhc.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.sunrise.scmbhc.R;

public class SwitchButton extends Button {
private boolean isChecked=false;
private OnCheckedChangeListener checkedChangeListener;
	public SwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setBackgroundResource(R.drawable.off);
		setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isChecked=!isChecked;
				setChecked(isChecked);
				checkedChangeListener.onCheckedChanged(null, isChecked);
			}
		});
	}
	
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
		if(isChecked){
			setBackgroundResource(R.drawable.on);
		}else{
			setBackgroundResource(R.drawable.off);
		}
	}

	public OnCheckedChangeListener getCheckedChangeListener() {
		return checkedChangeListener;
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener checkedChangeListener) {
		this.checkedChangeListener = checkedChangeListener;
	}
	
	
	
	}


