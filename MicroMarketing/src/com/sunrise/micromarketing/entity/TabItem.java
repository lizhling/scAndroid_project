package com.sunrise.micromarketing.entity;

public class TabItem {
	private int iconNormalResId;
	private int iconFocusResId;
	private String stringContent;
	private int iconClickResId;

	public TabItem(int iconNormalResId, int iconFocusResId,
			String stringContent, int iconClickResId) {
		super();
		this.iconNormalResId = iconNormalResId;
		this.iconFocusResId = iconFocusResId;
		this.stringContent = stringContent;
		this.iconClickResId = iconClickResId;
	}

	public int getIconNormalResId() {
		return iconNormalResId;
	}

	public String getStringContent() {
		return stringContent;
	}

	public void setStringContent(String stringContent) {
		this.stringContent = stringContent;
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
