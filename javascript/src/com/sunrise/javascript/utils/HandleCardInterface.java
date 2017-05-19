package com.sunrise.javascript.utils;

public interface HandleCardInterface {
	
	int GetOPSErrorMsg(int ErrorCode, char[] ErrorMsg);
	
	int ConfigReader(int ReaderType, char[] DeviceID, char[] Password);

	int GetOPSVersion (char[] Version);
	 
	int GetCardSN(char[] CardSN);
	
	int GetCardInfo(char[] CardInfo);
	
	int WriteCard(char[] IssueData, char[] Result);

	
}
