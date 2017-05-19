package com.starcpt.cmuc.model;

import android.graphics.Bitmap;

public class Channel {
private int id;
private Bitmap iconNormal;
private Bitmap iconFocused;
private String title;

public Channel(int id, Bitmap iconNormal, Bitmap iconFocused, String title) {
	super();
	this.id = id;
	this.iconNormal = iconNormal;
	this.iconFocused = iconFocused;
	this.title = title;
}

public Bitmap getIconNormal() {
	return iconNormal;
}
public void setIconNormal(Bitmap iconNormal) {
	this.iconNormal = iconNormal;
}
public Bitmap getIconFocused() {
	return iconFocused;
}
public void setIconFocused(Bitmap iconFocused) {
	this.iconFocused = iconFocused;
}


public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}

public String getTitle() {
	return title;
}

}
