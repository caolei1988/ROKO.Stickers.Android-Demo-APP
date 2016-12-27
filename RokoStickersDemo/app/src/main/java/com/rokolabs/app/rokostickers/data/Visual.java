package com.rokolabs.app.rokostickers.data;

import java.io.Serializable;
//import com.goldrun.android.media.ImageLoaderArgs;
//import com.goldrun.android.media.MediaManager;

public class Visual implements Serializable {
	/**
     * 
     */
	private static final long serialVersionUID = -6993983970294859431L;
	private final int id;
	private final String name;
	private final String desc;

	private final String caption;
	private final double maxScale;
	private final double minScale;
	private final double defScale;
	private final boolean canFlip;
	private final boolean canRotate;
	private final ImageFile imageFile;

	private final String productURL;

	public int campaignId;

	public Visual(int campaignId, int id, String name, String desc,
				  String caption, ImageFile imageFile, String productURL,
				  double maxScale, double minScale, double defScale, boolean canFlip,
				  boolean canRotate) {
		this.campaignId = campaignId;
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.caption = caption;
		this.imageFile = imageFile;
		this.productURL = productURL;
		this.maxScale = maxScale;
		this.minScale = minScale;
		this.defScale = defScale;
		this.canFlip = canFlip;
		this.canRotate = canRotate;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getDesc() {
		return this.desc;
	}

	public String getCaption() {
		return this.caption;
	}

	public ImageFile getImageFile() {
		return this.imageFile;
	}

	public double getMaxScale() {
		return this.maxScale;
	}

	public double getMinScale() {
		return this.minScale;
	}

	public double getDefaultScale() {
		return this.defScale;
	}

	public boolean canFlip() {
		return this.canFlip;
	}

	public boolean canRotate() {
		return this.canRotate;
	}

	public String getProductURL() {
		return productURL;
	}

}
