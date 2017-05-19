package com.starcpt.cmuc.model.bean;

import java.util.List;

public class SkinsBean extends ResultBean{
	
	@Override
	public String toString() {
		return "SkinsBean [skinBeanList=" + skinBeanList + "]";
	}

	private List<SkinBean> skinBeanList;

	/**
	 * @return the skinBeanList
	 */
	public List<SkinBean> getSkinBeanList() {
		return skinBeanList;
	}

	/**
	 * @param skinBeanList the skinBeanList to set
	 */
	public void setSkinBeanList(List<SkinBean> skinBeanList) {
		this.skinBeanList = skinBeanList;
	}
	 
}
