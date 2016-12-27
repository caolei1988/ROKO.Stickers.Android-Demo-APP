package com.rokolabs.app.rokostickers.data;

import com.google.gson.Gson;
import com.rokolabs.app.common.util.Logg;

import java.util.ArrayList;
import java.util.List;

public class StickersListItem
{
    public boolean            hasMoreRecords;
    public String apiStatusCode;
    public List<StickersList> data;

    public static StickersListItem getStickersListItem(String st)
    {
    	Logg.d(st);
        StickersListItem item = new Gson().fromJson(st, StickersListItem.class);
        return item;
    }
    
    public List<StickersList> getActiveStickers()
    {
        List<StickersList> list = new ArrayList<StickersList>();
        for (int i = 0; i < data.size(); i++)
        {
            if ("active".equals(data.get(i).liveStatus))
                list.add(data.get(i));
        }
        return list;
    }

}
