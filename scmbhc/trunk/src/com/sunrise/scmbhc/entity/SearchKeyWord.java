package com.sunrise.scmbhc.entity;

public class SearchKeyWord {
	public static final int ALL_TYPE=0;
	public static final int BUSINESS_TYPE=1;
	public static final int DISCOUNT_TYPE=2;
	private int searchType;
	private String keyWord;
	private int tagType;
	
	
	public SearchKeyWord(int searchType, String keyWord, int tagType) {
		super();
		this.searchType = searchType;
		this.keyWord = keyWord;
		this.tagType = tagType;
	}
	
	public int getSearchType() {
		return searchType;
	}
	public void setSearchType(int searchType) {
		this.searchType = searchType;
	}
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public int getTagType() {
		return tagType;
	}

	public void setTagType(int tagType) {
		this.tagType = tagType;
	}
	
	
	
}
