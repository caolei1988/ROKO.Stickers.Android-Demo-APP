package com.rokolabs.app.rokostickers.data;

import java.util.List;

public class StickersList
{
    public String liveStatus;
    public String displayType;
    public PackIconFileGroup packIconFileGroup;
    public PackIconFileGroup unselectedPackIconFileGroup;
    public Watermark         watermark;
    public boolean           useWatermark;
    public boolean           useApplicationDefaultWatermark;
    public int               watermarkPosition;
    public float             watermarkScaleFactor;
    public List<Sticker> stickers;
    public String name;
    public String createDate;
    public String updateDate;
    public int               objectId;

}
