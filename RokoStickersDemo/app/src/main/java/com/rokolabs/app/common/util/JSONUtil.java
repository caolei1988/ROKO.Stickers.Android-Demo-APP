package com.rokolabs.app.common.util;

import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class JSONUtil {
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

	public static String getString(JSONObject jobj, String key) {
		if (jobj == null || key == null || jobj.isNull(key))
			return "";
		return jobj.optString(key);
	}

	public static int getInt(JSONObject jobj, String key) {
		if (jobj == null || key == null || jobj.isNull(key))
			return 0;
		return jobj.optInt(key);
	}

	public static long getDate(JSONObject jobj, String key) {
		if (jobj == null || key == null || jobj.isNull(key))
			return 0;
		String dStr = jobj.optString(key);

		try {
			return dateFormatter.parse(dStr).getTime();
		} catch (Exception e) {
		}
		return 0;
	}
}
