package com.rokolabs.app.rokostickers.camera;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Matrix;

public class TranslateUtil
{
    public static Matrix cropCenter(int w, int h, int tw, int th){
        Matrix mx = new Matrix();
        float ws = (float)tw/w;
        float hs = (float)th/h;
        
        float scale = Math.max(ws, hs);
        
        float dx = -((w * scale) - tw)/2;
        float dy = -((h * scale) - th)/2;
        
        
        mx.postScale(scale, scale);
        mx.postTranslate(dx, dy);
        
        return mx;
    }
    
    public static Matrix revCropCenter(int w, int h, int tw, int th){
        Matrix mx = new Matrix();
        float ws = (float)w/tw;
        float hs = (float)h/th;
        
        float scale = Math.min(ws, hs);
        
        float dx = ((tw * scale) - w)/2;
        float dy = ((th * scale) - h)/2;
        
        
        mx.postTranslate(dx, dy);
        mx.postScale(scale, scale);
        
        return mx;
    }
    
    public static Matrix cropCenterRatio(int w, int h, int tw, int th, float rX, float rY){
        Matrix mx = new Matrix();
        float ws = (float)tw/w;
        float hs = (float)th/h;
        
        float scale = Math.max(ws, hs);
        
        float dx = -((w * scale) - tw)*rX;
        float dy = -((h * scale) - th)*rY;
        
        
        mx.postScale(scale, scale);
        mx.postTranslate(dx, dy);
        
        return mx;        
    }
    
    public static Matrix fitWidthAlignBottom(int w, int h, int tw, int th){
        Matrix mx = new Matrix();
        float ws = (float)tw/w;
        
        float dy = th - h * ws;
        
        
        mx.postScale(ws, ws);
        mx.postTranslate(0, dy);
        
        return mx;
    }

    public static int getMaxImageMemory(Context ctx){
        int max = ((ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass()*1024*1024/8;
        
        max = Math.max(max, 1024*1024*4);
        
        return max;
    }
}
