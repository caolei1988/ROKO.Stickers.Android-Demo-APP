package com.rokolabs.app.rokostickers.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Surface;
import android.widget.ImageView;

public class StillImageView extends ImageView
{
    int rotation;

    public StillImageView(Context context)
    {
        this(context, null);
    }

    public StillImageView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public StillImageView(Context context, AttributeSet attrs, int defStyle)
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
        super.onLayout(changed, left, top, right, bottom);
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        rotation = display.getRotation();
        updateMatrix();
    }

    private void updateMatrix()
    {
        if (getDrawable() instanceof BitmapDrawable)
        {
            Bitmap bmp = ((BitmapDrawable) getDrawable()).getBitmap();
            if (bmp != null && getWidth() > 0 && getHeight() > 0)
            {
                Matrix matrix = new Matrix();
                matrix.postTranslate(-bmp.getWidth() / 2, -bmp.getHeight() / 2);
                //matrix.postRotate(getRotate());
                float scale = calcScale(bmp.getWidth(), bmp.getHeight());
                matrix.postScale(scale, scale);
                matrix.postTranslate(getWidth() / 2, getHeight() / 2);
                setScaleType(ScaleType.MATRIX);
                setImageMatrix(matrix);
            }
        }
    }

    private float calcScale(int bw, int bh)
    {
        if (rotation == 90 || rotation == 270)
        {
            int t = bw;
            bw = bh;
            bh = t;
        }

        float r1 = (float) getWidth() / bw;
        float r2 = (float) getHeight() / bh;
        return Math.min(r1, r2);
    }

    private float getRotate()
    {
        switch (rotation)
        {
        case Surface.ROTATION_90:
            return 90;
        case Surface.ROTATION_180:
            return 180;
        case Surface.ROTATION_270:
            return 270;
        }
        return 0;
    }
}
