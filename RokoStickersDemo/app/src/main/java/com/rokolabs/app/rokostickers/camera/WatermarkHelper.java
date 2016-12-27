package com.rokolabs.app.rokostickers.camera;

import android.content.Context;
import android.graphics.Matrix;
import android.util.Log;


import com.rokolabs.app.rokostickers.R;
import com.rokolabs.app.rokostickers.data.IconFile;

import java.io.Serializable;

public class WatermarkHelper implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -6282161595029696529L;
    private final IconFile    imageURL;
    private final Layout      layout;
    private final double      ratio;
    private final int         mPicWith, mPicHeight;
    private int               mToolBarHeight;
    private Context mContext;
    private int               mOffsetHeight    = 0;

    public enum Layout //(V-H)
    {
        fullscreen, top_left, top_center, top_right, centerTop_left, centerTop_center, centerTop_right, centerBottom_left, centerBottom_center, centerBottom_right, bottom_left, bottom_center, bottom_right
    }

    public enum HDock
    {
        left, right, center
    }

    public enum VDock
    {
        top, bottom, centerTop, centerBottom
    }

    public WatermarkHelper(IconFile imageURL, Layout layout, double ratio, int screenWith, int screenHeight, Context context)
    {
        this.imageURL = imageURL;
        this.layout = layout;
        this.ratio = ratio < 0.0 ? 0.0 : ratio > 1.0 ? 1.0 : ratio;
        this.mPicWith = screenWith;
        this.mPicHeight = screenHeight;
        this.mContext = context;
        this.mToolBarHeight = context.getResources().getDimensionPixelSize(R.dimen.tool_bar_height);
        Log.v("test", " layout = " + layout + " , ratio = " + ratio + " , with = " + screenWith + " , height = " + screenHeight);
    }

    public WatermarkHelper clone()
    {
        return new WatermarkHelper(imageURL, layout, ratio, mPicWith, mPicHeight, mContext);
    }

    public IconFile getImageURL()
    {
        return imageURL;
    }

    public Layout getLayout()
    {
        return layout;
    }

    public double getRatio()
    {
        return ratio;
    }

    public HDock getHDock()
    {
        switch (getLayout())
        {
        case top_left:
        case centerTop_left:
        case centerBottom_left:
        case bottom_left:
            return HDock.left;
        case top_center:
        case centerTop_center:
        case centerBottom_center:
        case bottom_center:
            return HDock.center;
        case top_right:
        case centerTop_right:
        case centerBottom_right:
        case bottom_right:
            return HDock.right;
        default:
            return HDock.center;
        }
    }

    public VDock getVDock()
    {
        switch (getLayout())
        {
        case top_left:
        case top_center:
        case top_right:
            return VDock.top;
        case centerTop_left:
        case centerTop_center:
        case centerTop_right:
            return VDock.centerTop;
        case centerBottom_left:
        case centerBottom_center:
        case centerBottom_right:
            return VDock.centerBottom;
        case bottom_left:
        case bottom_center:
        case bottom_right:
            return VDock.bottom;
        default:
            return VDock.top;
        }
    }

    public HDock getHDock(int rotationDeg)
    {
        VDock vdock = getVDock();
        HDock hdock = getHDock();

        switch (rotationDeg)
        {
        case 90:
            return vdock == VDock.top ? HDock.left : vdock == VDock.bottom ? HDock.right : HDock.center;
        case 180:
            return hdock == HDock.left ? HDock.right : hdock == HDock.right ? HDock.left : HDock.center;
        case 270:
            return vdock == VDock.top ? HDock.right : vdock == VDock.bottom ? HDock.left : HDock.center;
        default:
            return hdock;
        }
    }

    public VDock getVDock(int rotationDeg)
    {
        VDock vdock = getVDock();
        HDock hdock = getHDock();

        switch (rotationDeg)
        {
        case 90:
            return hdock == HDock.left ? VDock.bottom : hdock == HDock.right ? VDock.top : VDock.centerTop;
        case 180:
            return vdock == VDock.top ? VDock.bottom : vdock == VDock.bottom ? VDock.top : VDock.centerTop;
        case 270:
            return hdock == HDock.left ? VDock.top : hdock == HDock.right ? VDock.bottom : VDock.centerTop;
        default:
            return vdock;
        }
    }

    public Matrix createPositioningMatrix(double watermarkWidth, double watermarkHeight, int screenWidth, int screenHeight, int rotation)
    {

        Log.v("test", " layout = " + layout + " , ratio = " + ratio + " , watermarkWidth = " + watermarkWidth + " , watermarkHeight = " + watermarkHeight + " , screenWidth = " + screenWidth + " , screenHeight = " + screenHeight);
        int realHeight = mPicHeight * screenWidth / mPicWith;
        Log.v("test", " realHeight = " + realHeight);

        mOffsetHeight = isBigger(screenHeight, realHeight) ? mToolBarHeight : 0;

        double watermarkHalfWidth = watermarkWidth / 2.0f;
        double watermarkHalfHeight = watermarkHeight / 2.0f;

        double rotatedWatermarkWidth = rotation == 90 || rotation == 270 ? watermarkHeight : watermarkWidth;
        double rotatedWatermarkHeight = rotation == 90 || rotation == 270 ? watermarkWidth : watermarkHeight;

        double rotatedWatermarkHalfWidth = rotatedWatermarkWidth / 2.0f;
        double rotatedWatermarkHalfHeight = rotatedWatermarkHeight / 2.0f;

        Matrix matrix = new Matrix();

        matrix.postTranslate((float) -watermarkHalfWidth, (float) -watermarkHalfHeight);
        matrix.postRotate(-rotation); //reverse the angle
        matrix.postTranslate((float) rotatedWatermarkHalfWidth, (float) rotatedWatermarkHalfHeight);

        if (getLayout() == WatermarkHelper.Layout.fullscreen)
        {
            matrix.postScale((float) (screenWidth / rotatedWatermarkWidth), (float) (screenHeight / rotatedWatermarkHeight));
        } else
        {

            double scale = 1.0;
            if (ratio != 0.0)
            {
                scale = ratio;// * Math.min(watermarkWidth, watermarkHeight) / watermarkWidth;
            }

            Log.v("test", "scale ========== " + scale);
            matrix.postScale((float) scale, (float) scale);

            double hTrans = 0.0;
            double vTrans = 0.0;

            VDock vdock = getVDock(rotation);
            HDock hdock = getHDock(rotation);

            switch (vdock)
            {
            case top:
                vTrans = (screenHeight - realHeight) / 2 + mOffsetHeight;
                break;
            case bottom:
                vTrans = screenHeight - (rotatedWatermarkHeight * scale) - (screenHeight - realHeight) / 2 - mOffsetHeight;
                break;
            case centerTop:
                vTrans = (screenHeight - realHeight) / 2 + realHeight / 4 + mOffsetHeight;
                break;
            case centerBottom:
                vTrans = screenHeight - (rotatedWatermarkHeight * scale) - (screenHeight - realHeight) / 2 - realHeight / 4 - mOffsetHeight;
                break;
            default:
                vTrans = (screenHeight - (rotatedWatermarkHeight * scale)) / 2;
                break;
            }
            switch (hdock)
            {
            case left:
                break;
            case right:
                hTrans = screenWidth - (rotatedWatermarkWidth * scale);
                break;
            case center:
                hTrans = (screenWidth - (rotatedWatermarkWidth * scale)) / 2;
            default:
                break;
            }

            Log.v("test", "hTrans = " + hTrans + " ,  vTrans = " + vTrans);
            matrix.postTranslate((float) hTrans, (float) vTrans);
        }

        return matrix;
    }

    public Matrix createPositioningMergeMatrix(double watermarkWidth, double watermarkHeight, int screenWidth, int screenHeight, int rotation, float srcScale)
    {
        Log.v("test", " layout = " + layout + " , ratio = " + ratio + " , watermarkWidth = " + watermarkWidth + " , watermarkHeight = " + watermarkHeight + " , screenWidth = " + screenWidth + " , screenHeight = " + screenHeight);

        double watermarkHalfWidth = watermarkWidth / 2.0f;
        double watermarkHalfHeight = watermarkHeight / 2.0f;

        double rotatedWatermarkWidth = rotation == 90 || rotation == 270 ? watermarkHeight : watermarkWidth;
        double rotatedWatermarkHeight = rotation == 90 || rotation == 270 ? watermarkWidth : watermarkHeight;

        double rotatedWatermarkHalfWidth = rotatedWatermarkWidth / 2.0f;
        double rotatedWatermarkHalfHeight = rotatedWatermarkHeight / 2.0f;

        Matrix matrix = new Matrix();

        matrix.postTranslate((float) -watermarkHalfWidth, (float) -watermarkHalfHeight);
        matrix.postRotate(-rotation); //reverse the angle
        matrix.postTranslate((float) rotatedWatermarkHalfWidth, (float) rotatedWatermarkHalfHeight);

        if (getLayout() == WatermarkHelper.Layout.fullscreen)
        {
            matrix.postScale((float) (screenWidth / rotatedWatermarkWidth), (float) (screenHeight / rotatedWatermarkHeight));
        } else
        {

            double scale = 1.0;
            if (ratio != 0.0)
            {
                scale = ratio * srcScale;// * Math.min(watermarkWidth, watermarkHeight) / watermarkWidth;
            }

            Log.v("test", "scale ========== " + scale);
            matrix.postScale((float) scale, (float) scale);

            double hTrans = 0.0;
            double vTrans = 0.0;

            VDock vdock = getVDock(rotation);
            HDock hdock = getHDock(rotation);

            switch (vdock)
            {
            case top:
                vTrans = mOffsetHeight;
                break;
            case bottom:
                vTrans = screenHeight - (rotatedWatermarkHeight * scale) - mOffsetHeight;
                break;
            case centerTop:
                vTrans = screenHeight / 4 + mOffsetHeight;
                break;
            case centerBottom:
                vTrans = screenHeight - (rotatedWatermarkHeight * scale) - screenHeight / 4 - mOffsetHeight;
                break;
            default:
                vTrans = (screenHeight - (rotatedWatermarkHeight * scale)) / 2;
                break;
            }
            switch (hdock)
            {
            case left:
                break;
            case right:
                hTrans = screenWidth - (rotatedWatermarkWidth * scale);
                break;
            case center:
                hTrans = (screenWidth - (rotatedWatermarkWidth * scale)) / 2;
            default:
                break;
            }

            Log.v("test", "hTrans = " + hTrans + " ,  vTrans = " + vTrans);
            matrix.postTranslate((float) hTrans, (float) vTrans);
        }

        return matrix;
    }

    public boolean isBigger(int screenHeight, int realHeight)
    {
        int maxHeight = screenHeight - mToolBarHeight * 2;
        if (realHeight > maxHeight)
            return true;
        return false;
    }
}
