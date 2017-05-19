package com.sunrise.marketingassistant.entity;

import java.util.ArrayList;

public class SearchGroupIdResult {

	private ArrayList<GroupId> RETURN_INFO;

	public static class GroupId {
		private String LOGIN_NO;
		private String GROUP_ID;
		private String REGION_ID;
		private String GROUP_NAME;

		public String getLOGIN_NO() {
			return LOGIN_NO;
		}

		public void setLOGIN_NO(String lOGIN_NO) {
			LOGIN_NO = lOGIN_NO;
		}

		public String getGROUP_ID() {
			return GROUP_ID;
		}

		public void setGROUP_ID(String gROUP_ID) {
			GROUP_ID = gROUP_ID;
		}

		public String getREGION_ID() {
			return REGION_ID;
		}

		public void setREGION_ID(String rEGION_ID) {
			REGION_ID = rEGION_ID;
		}

		public String getGROUP_NAME() {
			return GROUP_NAME;
		}

		public void setGROUP_NAME(String gROUP_NAME) {
			GROUP_NAME = gROUP_NAME;
		}
	}

	public ArrayList<GroupId> getGroupIdArray() {
		return RETURN_INFO;
	}

	public void setRETURN_INFO(ArrayList<GroupId> rETURN_INFO) {
		RETURN_INFO = rETURN_INFO;
	}
}
