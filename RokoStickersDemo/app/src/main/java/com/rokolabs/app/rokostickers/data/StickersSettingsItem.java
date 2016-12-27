package com.rokolabs.app.rokostickers.data;

import com.google.gson.Gson;

public class StickersSettingsItem
{
    public String apiStatusCode;

    public StickersSettings data;

    public static StickersSettingsItem getStickersSettings(String st)
    {
        StickersSettingsItem item = new Gson().fromJson(st, StickersSettingsItem.class);
        return item;
    }

}
