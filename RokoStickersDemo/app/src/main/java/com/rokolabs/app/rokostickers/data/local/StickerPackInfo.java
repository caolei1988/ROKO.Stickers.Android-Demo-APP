package com.rokolabs.app.rokostickers.data.local;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StickerPackInfo implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public String title;
    public int                iconDefault;
    public int                iconSelected;
    public List<Integer> stickerInfos     = new ArrayList<Integer>();
}
