package com.rokolabs.app.rokostickers.utils;

public class CQURL {
	public String protocol;
	public String host;
	public String path;
	public String file;

	public CQURL(String raw) {
		int pIdx = raw.indexOf("://");

		if (pIdx != -1) {
			protocol = raw.substring(0, pIdx);
			String rest = raw.substring(pIdx + 3);

			int idx = rest.indexOf('/');
			if (idx != -1) {
				host = rest.substring(0, idx);

				int idx2 = rest.lastIndexOf('/');

				if (idx2 != idx) {
					path = rest.substring(idx + 1, idx2);
					if (idx2 + 1 < rest.length())
						file = rest.substring(idx2 + 1);
				} else {
					if (idx + 1 < rest.length())
						file = rest.substring(idx + 1);
				}

			} else
				host = rest;
		}

	}
	
	public boolean isValid() {
		return protocol != null && host != null;
	}

	public String getServerRoot() {
		StringBuffer buf = new StringBuffer();
		if (protocol != null) {
			buf.append(protocol);
			buf.append("://");
		}
		if (host != null) {
			buf.append(host);
		}
		return buf.toString();
	}

	public String getServerWithPath() {
		StringBuffer buf = new StringBuffer();
		buf.append(getServerRoot());
		if (buf.length() > 0)
			buf.append('/');
		if (path != null) {
			buf.append(path);
			buf.append('/');
		}

		return buf.toString();
	}

	public String resolveRelativePath(String path) {
		int pIdx = path.indexOf("://");
		if (pIdx != -1)
			return path;
		else if (path.startsWith("/")) {
			return getServerRoot() + path;
		} else {
			return getServerWithPath() + path;
		}
	}
}
