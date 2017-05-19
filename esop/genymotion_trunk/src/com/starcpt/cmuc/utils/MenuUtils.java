package com.starcpt.cmuc.utils;



import java.lang.reflect.Constructor;

import android.app.Activity;
import android.content.Context;
import android.view.Menu;

public class MenuUtils {
@SuppressWarnings({ "rawtypes", "unchecked" })
public static Menu createMenu(Activity activity,int resId){
	 Object localObject;
	 Menu menu=null;
	try {
		localObject = Class.forName("com.android.internal.view.menu.MenuBuilder");
		Class[] arrayOfClass = new Class[1];
	     arrayOfClass[0] = Context.class;
	     localObject = ((Class)localObject).getConstructor(arrayOfClass);
	     Object[] arrayOfObject = new Object[1];
	     arrayOfObject[0] = activity;
	     menu = (Menu)((Constructor)localObject).newInstance(arrayOfObject);
	     activity.getMenuInflater().inflate(resId, menu);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
     return menu;
}
}
