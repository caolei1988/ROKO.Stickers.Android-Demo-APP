package com.rokolabs.app.common.util;

public class Logg {
    private static final boolean DEBUG = true;

    private static final String TAG = "ROKO-MOBI";

    public static void d(String tag, String msg){
        if (DEBUG)
            android.util.Log.d(TAG+"-"+tag, msg);    	
    }
    
    public static void i(String msg) {
        if (DEBUG)
            android.util.Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if (DEBUG)
            android.util.Log.d(TAG, msg);
    }

    public static void w(String msg) {
        if (DEBUG)
            android.util.Log.w(TAG, msg);
    }

    public static void e(String msg) {
        if (DEBUG)
            android.util.Log.e(TAG, msg);
    }

    public static void e(Throwable e, String msg) {
        if (DEBUG)
            android.util.Log.e(TAG, msg);
    }
}
