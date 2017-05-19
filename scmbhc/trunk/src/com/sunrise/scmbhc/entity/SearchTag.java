package com.sunrise.scmbhc.entity;

public class SearchTag {
public static final String DATAS="datas";
public static final String TAGID="tagId";
public static final String TAGNAME="tagName";
public static final String TAGTYPE="tagType";
public static final String SCOUNT="scount";
public static final int SYSTEM_TYPE=0;
public static final int USER_TYPE=1;
private long tagId;
private String tagName;
private int tagType;
private int scount;

public long getTagId() {
	return tagId;
}
public void setTagId(long tagId) {
	this.tagId = tagId;
}
public String getTagName() {
	return tagName;
}
public void setTagName(String tagName) {
	this.tagName = tagName;
}
public int getTagType() {
	return tagType;
}
public void setTagType(int tagType) {
	this.tagType = tagType;
}
public int getScount() {
	return scount;
}
public void setScount(int scount) {
	this.scount = scount;
}


}
