package com.sunrise.scmbhc.entity;

public class TabItem {
private int iconNormalResId;
private int iconFocusResId;
private int stringResId;
private int iconClickResId;

public TabItem(int iconNormalResId, int iconFocusResId, int stringResId, int iconClickResId) {
	super();
	this.iconNormalResId = iconNormalResId;
	this.iconFocusResId = iconFocusResId;
	this.stringResId = stringResId;
	this.iconClickResId = iconClickResId;
}

public int getIconNormalResId() {
	return iconNormalResId;
}

public int getStringResId() {
	return stringResId;
}

public void setStringResId(int stringResId) {
	this.stringResId = stringResId;
}

public int getIconFocusResId() {
	return iconFocusResId;
}

/**
 * @return the iconClickResId
 */
public int getIconClickResId() {
	return iconClickResId;
}



}
