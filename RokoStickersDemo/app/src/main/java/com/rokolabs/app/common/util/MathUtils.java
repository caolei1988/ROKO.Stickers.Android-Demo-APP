package com.rokolabs.app.common.util;

import android.content.Context;

import com.rokolabs.app.rokostickers.camera.TranslateUtil;

public class MathUtils
{
    public static float calcSpace(float x1, float y1, float x2, float y2)
    {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static float calcAngle(float x1, float y1, float x2, float y2)
    {
        float dx = x1 - x2;
        float dy = y1 - y2;
        double r = Math.atan2(dy, dx);
        return (float) Math.toDegrees(r);
    }

    public static int calcRatio(int width, int height, Context context)
    {
        int max = TranslateUtil.getMaxImageMemory(context);

        int ratio = 1;
        int mem = width * height * 4;
        int pow = 0;
        while (mem > max)
        {
            pow++;
            ratio = (int) Math.pow(2, pow);
            mem /= ratio;
        }
        return ratio;
    }
}
