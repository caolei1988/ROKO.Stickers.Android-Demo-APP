package com.rokolabs.app.rokostickers.utils;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map.Entry;

public class RequestBuilder {
	private HashMap<String, String> params = new HashMap<String, String>();
	private StringBuffer path = new StringBuffer();

	public void appendPath(String str) {
		if(str == null || str.length() == 0)
			return;
		if(path.length() == 0)
			path.append(str);
		else {
			CQURL url = new CQURL(path.toString());
			path = new StringBuffer();
			path.append(url.resolveRelativePath(str));
		}
	}

	/**
	 * Duplicate param will override old one
	 * 
	 * @param name
	 * @param value
	 */
	public void appendParam(String name, String value) {
		if (name == null)
			return;
		name = name.trim().toLowerCase();
		if (name.length() == 0)
			return;
		params.put(name, value);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(path);
		if (params.size() > 0) {
			buf.append("?");
			for (Entry<String, String> entry : params.entrySet()) {
				if (buf.charAt(buf.length() - 1) != '?')
					buf.append("&");
				buf.append(entry.getKey());
				buf.append("=");
				buf.append(URLEncoder.encode(entry.getValue()));
			}
		}
		return buf.toString();
	}

	public void appendParam(String name, double v) {
		appendParam(name, String.valueOf(v));

	}

	public void appendParam(String name, long v) {
		appendParam(name, String.valueOf(v));

	}

	public void appendParam(String name, int v) {
		appendParam(name, String.valueOf(v));

	}
	
	public void removeParam(String name){
		params.remove(name);
	}
}
