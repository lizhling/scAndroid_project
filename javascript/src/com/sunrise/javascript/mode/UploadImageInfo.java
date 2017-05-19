package com.sunrise.javascript.mode;

public class UploadImageInfo {
	private String ImageFileName;		// 拍照保存picture后的文件名   add by qhb
	private String ImageStrFileName;	// picture 经编码后的txt文件的文件名 add by qhb
	private String filePath;			// picture 保存的路径    add by qhb
	
	public String getImageFileName() {
		return ImageFileName;
	}
	public void setImageFileName(String imageFileName) {
		ImageFileName = imageFileName;
	}
	public String getImageStrFileName() {
		return ImageStrFileName;
	}
	public void setImageStrFileName(String imageStrFileName) {
		ImageStrFileName = imageStrFileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	
}
