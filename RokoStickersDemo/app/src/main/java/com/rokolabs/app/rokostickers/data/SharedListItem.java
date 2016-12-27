package com.rokolabs.app.rokostickers.data;

import com.google.gson.Gson;

public class SharedListItem
{
    public String apiStatusCode;

    public ShareSettings data;

    public static SharedListItem getShareSettings(String st)
    {
        SharedListItem item = new Gson().fromJson(st, SharedListItem.class);
        Channel sms = item.data.getChannel(ShareSettings.CHANNEL_SMS);
        sms.headerText=sms.bodyText;
        sms.bodyText=null;
        return item;
    }

}
