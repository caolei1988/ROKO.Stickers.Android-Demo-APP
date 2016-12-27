package com.rokolabs.app.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtils {
	public static SimpleDateFormat DF_TZ = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
	
	public static SimpleDateFormat DF_HMS = new SimpleDateFormat(
			"HH:mm:ss", Locale.US);
	
	static {
		DF_TZ.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	public static long parseTZ(String date) {
		try {
			int idx = date.lastIndexOf(".");
			String expDate = date;
			if(idx != -1){
				expDate = expDate.substring(0, idx)+"Z";
			}
			return DF_TZ.parse(expDate).getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static String toTZ(long time){
		return DF_TZ.format(new Date(time));
	}
	
	public static long parseHMS(String date) {
		try {
			return DF_HMS.parse(date).getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
