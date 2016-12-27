package com.rokolabs.app.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class SharedPreHelper
{
    private static SharedPreHelper helper;
    private static SharedPreferences sharedPref;
    private static final String SHAREDPREF_NAME   = "config_v2";

    public static final String JSON_USER         = "json_user";
    public static final String SESSION_KEY       = "session_key";
    public static final String EMAIL             = "email";
    public static final String PASSWORD          = "password";
    public static final String SOUND_AUTO_PLAY   = "sound_on_autoplay";
    public static final String STICKERS          = "stickers";
    public static final String STICKERS_SETTINGS = "stickers_settings";
    public static final String SHARE_SETTINGS    = "share_settings";

    public static SharedPreHelper getInstance(Context context)
    {
        if (helper == null)
        {
            sharedPref = context.getSharedPreferences(SHAREDPREF_NAME, Context.MODE_PRIVATE);
            helper = new SharedPreHelper();
        }
        return helper;
    }

    public boolean getBoolean(String key)
    {
        boolean defaultValue = true;
        if (SOUND_AUTO_PLAY.equals(key))
        {
            defaultValue = false;
        }
        return sharedPref.getBoolean(key, defaultValue);
    }

    public void setBoolean(String key, boolean value)
    {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public int getInt(String key)
    {
        return sharedPref.getInt(key, -1);
    }

    public void setInt(String key, int value)
    {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public String getString(String key)
    {
        return sharedPref.getString(key, "");
    }

    public void setString(String key, String value)
    {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void remove(String key)
    {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.commit();
    }

    public static boolean isLogined(Context context)
    {
        String sessionKey = getInstance(context).getString(SESSION_KEY);
        return !TextUtils.isEmpty(sessionKey) && !TextUtils.isEmpty(JSON_USER);
    }

    public static String getSessionKey(Context context)
    {
        return getInstance(context).getString(SESSION_KEY);
    }

}
