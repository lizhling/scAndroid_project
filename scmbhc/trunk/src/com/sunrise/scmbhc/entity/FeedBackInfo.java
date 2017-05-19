package com.sunrise.scmbhc.entity;

/**  
 *   
 * @Project: scmbhc  
 * @ClassName: FeedBackInof  
 * @Description: 用户反馈实体类
 * @Author qinhubao  
 * @CreateFileTime: 2014年1月21日 下午3:58:42  
 * @Modifier: qinhubao  
 * @ModifyTime: 2014年1月21日 下午3:58:42  
 * @ModifyNote: 
 * @version   1.0
 *   
 */
public class FeedBackInfo {
	public static final int SYSTEM_DEBUG = 1;  		// 反馈类型--系统漏洞
	public static final int IMPROVE_SUGGESTION =2;	// 反馈类型 -- 反馈意见
	
	private long feekbackType;	// 反馈类型
	private String requestUrl;	// 服务器网址
	private String content;		// 反馈内容
	private String phone_no;	// 反馈手机号
	private String imei;		// 手机imei值
	public long getFeekbackType() {
		return feekbackType;
	}
	public void setFeekbackType(long id) {
		this.feekbackType = id;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPhone_no() {
		return phone_no;
	}
	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}




}
