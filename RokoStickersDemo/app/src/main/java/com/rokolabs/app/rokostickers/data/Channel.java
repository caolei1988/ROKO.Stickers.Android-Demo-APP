package com.rokolabs.app.rokostickers.data;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class Channel implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5282679142416600952L;
	public boolean        isSubjectFromSource;
    public String subjectText;
    public String headerText;
    public String footerText;
    public String bodyText;
    public boolean        enabled;
    public ImageFileGroup imageFileGroup;
    public transient Drawable imageDrawable;
    public String channelType;
}
