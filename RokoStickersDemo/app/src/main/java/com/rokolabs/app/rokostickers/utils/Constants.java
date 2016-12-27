package com.rokolabs.app.rokostickers.utils;

import android.util.Log;

public class Constants
{
    public static final String TAG        = "ROKOLABS";

    //this should always been false in SVN.
    public static boolean      DEBUG      = true;

    public static final int    DEV_TYPE   = 5;

    public static final String devicetype = "android";

    public static void d(String str)
    {
        if (DEBUG)
            Log.d(TAG, ""+str);
    }

    public static void e(String msg, Throwable t)
    {
        if (DEBUG)
        {
            if (t == null)
            {
                Log.e(TAG, ""+msg);
            } else
            {
                Log.e(TAG, ""+msg, t);
            }
        }
    }
    public static void i(String str)
    {
        if (DEBUG)
            Log.i(TAG, ""+str);
    }

}
