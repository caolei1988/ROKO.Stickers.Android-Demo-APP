package com.rokolabs.app.rokostickers.analytics;

import java.util.HashMap;

public class Property extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3911052935676047754L;

	public Property(int stickerId, int stickerPackId, String stickerPackName,
					int positionInPack, String imageId, boolean isResized) {
		put("stickerId", stickerId);
		put("stickerPackId", stickerPackId);
		put("positionInPack", positionInPack);
		put("stickerPackName", stickerPackName);
		put("imageId", imageId);
		put("isResized", isResized);
	}
}
