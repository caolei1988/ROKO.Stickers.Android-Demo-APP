package com.rokolabs.app.common.util;

import android.graphics.Color;

public class ColorUtils
{
    public static int getColor(String color)
    {
        int[] icolors = getArgbValue(color);
        int icolor = Color.rgb(0, 0, 0);
        if (icolors.length == 3)
        {
            icolor = Color.rgb(icolors[0], icolors[1], icolors[2]);
        } else if (icolors.length == 4)
        {
            icolor = Color.argb(icolors[0], icolors[1], icolors[2], icolors[3]);
        } else {
        	icolor = Color.parseColor(color);
        }
        return icolor;

    }

    public static int[] getArgbValue(String color)
    {
        String[] colors = color.split(",");
        int[] icolors = new int[colors.length];
        if(icolors.length>1)
	        for (int i = 0; i < colors.length; i++)
	        {
	            icolors[i] = Integer.valueOf(colors[i].trim());
	        }
        return icolors;
    }
}
