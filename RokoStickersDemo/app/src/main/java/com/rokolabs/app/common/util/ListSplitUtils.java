package com.rokolabs.app.common.util;

import com.rokolabs.app.rokostickers.data.Sticker;

import java.util.List;

public class ListSplitUtils
{
    public static List<Sticker> splitStickerList(List<Sticker> list, int page)
    {
        List<Sticker> newList = null;
        int rows = 10;
        int total = list.size();
        newList = list.subList(rows * (page - 1), ((rows * page) > total ? total : (rows * page)));
        return newList;
    }

    public static List<Integer> splitIntegerList(List<Integer> list, int page)
    {
        List<Integer> newList = null;
        int rows = 10;
        int total = list.size();
        newList = list.subList(rows * (page - 1), ((rows * page) > total ? total : (rows * page)));
        return newList;
    }

}
