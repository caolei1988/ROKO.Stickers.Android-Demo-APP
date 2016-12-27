package com.rokolabs.app.rokostickers.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class WatermarkView extends ImageView
{
    public WatermarkHelper mWatermark;
    int       relativeDeviceRotation;

    public WatermarkView(Context context)
    {
        this(context, null);
    }

    public WatermarkView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public WatermarkView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageBitmap(Bitmap bm)
    {
        super.setImageBitmap(bm);
        updateMatrix();
    }

    @Override
    public void setImageDrawable(Drawable drawable)
    {
        super.setImageDrawable(drawable);
        updateMatrix();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        // TODO Auto-generated method stub
        super.onLayout(changed, left, top, right, bottom);
        updateMatrix();
    }

    private void updateMatrix()
    {
        if (getDrawable() instanceof BitmapDrawable && mWatermark != null)
        {
            Bitmap bmp = ((BitmapDrawable) getDrawable()).getBitmap();
            if (bmp != null && getWidth() > 0 && getHeight() > 0)
            {
                Matrix matrix = mWatermark.createPositioningMatrix(bmp.getWidth(), bmp.getHeight(), getWidth(), getHeight(), relativeDeviceRotation);
                setImageMatrix(matrix);
            }
        }
    }

    public void setRelativeDeviceRotation(int rotation)
    {
        if (relativeDeviceRotation != rotation)
        {
            this.relativeDeviceRotation = rotation;
            updateMatrix();
        }
    }

}
