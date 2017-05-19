package com.starcpt.cmuc.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MyImageView extends ImageView {  
	  
    public MyImageView (Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    @Override  
    protected void onDraw(Canvas canvas) {  
        try {  
            super.onDraw(canvas);  
        } catch (Exception e) {  
            System.out.println("MyImageView  -> onDraw() Canvas: trying to use a recycled bitmap");  
        }  
    }  
  
}  
