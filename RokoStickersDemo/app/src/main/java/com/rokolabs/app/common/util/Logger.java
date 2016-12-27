package com.rokolabs.app.common.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class Logger
{
    public static boolean DEBUG = true;//BuildConfig.DEBUG;
	
    public static boolean DEBUG_LOGCAT = false;

    public static String TAG   = "Roko-Lib-Common";
    
    public static void saveLog(){
        if(!DEBUG_LOGCAT)
            return;
        try
        {
            //final String LOGCAT_COMMAND = "logcat -v time -f ";
            String logFileName = TAG + "_debug.log";
            File mLogFile = new File(Environment.getExternalStorageDirectory(), logFileName);
            if(mLogFile.exists() && mLogFile.length()>3E5)
                mLogFile.delete();
            mLogFile.createNewFile();
            //String cmd = LOGCAT_COMMAND + mLogFile.getAbsolutePath();
            //Runtime.getRuntime().exec(cmd);
            new ProcessBuilder().command("logcat","-v","time","-d","-f",mLogFile.getAbsolutePath()).start();
            
            int pid = android.os.Process.myPid();
            String script = "while [ -d /proc/" + pid + " ];do sleep 1;done; killall logcat";
            new ProcessBuilder().command("/system/bin/sh", "-c", script).start();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void d(String msg)
    {
        if (DEBUG)
            Log.d(TAG, String.valueOf(msg));
    }

    public static void d(String msg, Throwable t)
    {
        if (DEBUG)
            Log.d(TAG, String.valueOf(msg), t);
    }

    public static void e(String msg)
    {
        if (DEBUG)
            Log.e(TAG, String.valueOf(msg));
    }

    public static void e(String msg, Throwable t)
    {
        if (DEBUG)
            Log.e(TAG, String.valueOf(msg), t);
    }

    public static void printMemory(String msg)
    {
        d(msg);
        d("maxMemory: " + Runtime.getRuntime().maxMemory()/1024 + "KB");
        d("totalMemory: " + Runtime.getRuntime().totalMemory()/1024 + "KB");
        d("freeMemory: " + Runtime.getRuntime().freeMemory()/1024 + "KB");
        d("nativeHeapSize: " + android.os.Debug.getNativeHeapSize()/1024 + "KB");
        d("nativeHeapAllocatedSize: " + android.os.Debug.getNativeHeapAllocatedSize()/1024 + "KB");
        d("nativeHeapFreeSize: " + android.os.Debug.getNativeHeapFreeSize()/1024 + "KB");
    }
}
