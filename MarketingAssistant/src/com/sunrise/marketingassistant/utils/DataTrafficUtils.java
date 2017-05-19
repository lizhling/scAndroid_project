package com.sunrise.marketingassistant.utils;

import android.annotation.SuppressLint;

@SuppressLint("DefaultLocale")
public class DataTrafficUtils {
	private static final String FORMAT_KB = "%.0fK";
	private static final String FORMAT_MB = "%.1fM";
	private static final String FORMAT_GB = "%.1fG";

	public static String getFlowString(double numOfKB) {
		if (numOfKB / 1024 > 999)
			return String.format(FORMAT_GB, numOfKB / (1 << 20));
		else if (numOfKB > 999)
			return String.format(FORMAT_MB, numOfKB / 1024);
		else
			return String.format(FORMAT_KB, numOfKB);
	}

	public static String getFlowBitString(double numOfBit) {
		return getFlowString(numOfBit / 1024);
	}
}
