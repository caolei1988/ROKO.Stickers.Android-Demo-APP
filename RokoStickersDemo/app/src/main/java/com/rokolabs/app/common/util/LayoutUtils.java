package com.rokolabs.app.common.util;

import com.rokolabs.app.rokostickers.camera.WatermarkHelper;

public class LayoutUtils
{
    public static WatermarkHelper.Layout getLayoutPosition(int position)
    {
        WatermarkHelper.Layout wLayout = WatermarkHelper.Layout.fullscreen;
        int watermarkPos = position;
        if (watermarkPos == 0 || watermarkPos == 1)
            wLayout = WatermarkHelper.Layout.top_left;
        else if (watermarkPos == 2)
            wLayout = WatermarkHelper.Layout.top_center;
        else if (watermarkPos == 3)
            wLayout = WatermarkHelper.Layout.top_right;
        else if (watermarkPos == 4)
            wLayout = WatermarkHelper.Layout.centerTop_left;
        else if (watermarkPos == 5)
            wLayout = WatermarkHelper.Layout.centerTop_center;
        else if (watermarkPos == 6)
            wLayout = WatermarkHelper.Layout.centerTop_right;
        else if (watermarkPos == 7)
            wLayout = WatermarkHelper.Layout.centerBottom_left;
        else if (watermarkPos == 8)
            wLayout = WatermarkHelper.Layout.centerBottom_center;
        else if (watermarkPos == 9)
            wLayout = WatermarkHelper.Layout.centerBottom_right;
        else if (watermarkPos == 10)
            wLayout = WatermarkHelper.Layout.bottom_left;
        else if (watermarkPos == 11)
            wLayout = WatermarkHelper.Layout.bottom_center;
        else if (watermarkPos == 12)
            wLayout = WatermarkHelper.Layout.bottom_right;
        return wLayout;
    }
}
