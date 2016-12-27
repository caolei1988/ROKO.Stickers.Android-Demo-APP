package com.rokolabs.app.rokostickers.data;

import com.rokolabs.app.common.util.ColorUtils;

import java.io.Serializable;

public class TextFont implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5702574198634932315L;
	public int size;
	public String color;

	public int getColor() {
		return ColorUtils.getColor(color);
	}
}
