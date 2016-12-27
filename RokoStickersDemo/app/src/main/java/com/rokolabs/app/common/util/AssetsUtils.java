package com.rokolabs.app.common.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AssetsUtils
{
    public static String getFromAssets(String fileName, Context context)
    {
        String line = "";
        String Result = "";
        try
        {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return Result;
    }
}
