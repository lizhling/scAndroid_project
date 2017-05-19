package com.starcpt.cmuc.model;

public class CachItem {
private String name;
private String dirName;
private long allFileSize;

public CachItem(String name, String dirName) {
	super();
	this.name = name;
	this.dirName = dirName;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getDirName() {
	return dirName;
}
public void setDirName(String dirName) {
	this.dirName = dirName;
}
public long getAllFileSize() {
	return allFileSize;
}
public void setAllFileSize(long allFileSize) {
	this.allFileSize = allFileSize;
}


}
