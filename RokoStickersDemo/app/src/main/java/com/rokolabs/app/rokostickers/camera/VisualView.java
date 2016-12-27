package com.rokolabs.app.rokostickers.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.rokolabs.app.rokostickers.R;
import com.rokolabs.app.rokostickers.StickersActivity;
import com.rokolabs.app.rokostickers.analytics.Property;

public class VisualView extends ImageView
{
    private static final int TOUCH_RANGE = 30;
    //	Visual mVisual;
    private boolean          deleteMode  = false;

    public Property         mProperty;

    public VisualView(Context context, /* Visual v, */boolean isPortraitDevice)
    {
        super(context);
        //		this.mVisual = v;
        this.isPortraitDevice = isPortraitDevice;
        super.setScaleType(ScaleType.MATRIX);

        dp = context.getResources().getDisplayMetrics().density;
    }

    public void setProperty(Property property)
    {
        this.mProperty = property;
    }

    public Property getProperty()
    {
        return mProperty;
    }

    public boolean shouldFlip;
    public int     relativeDeviceRotation;
    public boolean isPortraitDevice;

    public float   dp;

    public float   transX;
    public float   transY;
    public float   rotate = 0;
    public float   scale  = 1.0f;

    /**
     * check if the point is on a non transparent pixel in the image
     * 
     * @param x
     * @param y
     * @return
     */
    public boolean isOnImage(float x, float y)
    {
        if (getDrawable() instanceof BitmapDrawable)
        {
            Bitmap bmp = ((BitmapDrawable) getDrawable()).getBitmap();
            if (bmp == null)
                return false;
            int w = bmp.getWidth();
            int h = bmp.getHeight();

            Matrix revert = new Matrix();
            getImageMatrix().invert(revert);

            float[] pt = new float[] { x, y };
            revert.mapPoints(pt);

			float range = TOUCH_RANGE * dp;
			if (pt[0] >= -range && pt[1] >= -range && pt[0] < w + range
					&& pt[1] < h + range) {
				return checkPixel(bmp, pt[0], pt[1]);// Color.alpha(bmp.getPixel((int)
														// pt[0], (int) pt[1]))
														// > 0;
			}
        }
        return false;
    }

    private boolean checkPixel(Bitmap bmp, float x, float y)
    {
        int r0 = (int) (y - TOUCH_RANGE * dp);
        int r1 = (int) (y + TOUCH_RANGE * dp);
        int c0 = (int) (x - TOUCH_RANGE * dp);
        int c1 = (int) (x + TOUCH_RANGE * dp);
        r0 = Math.max(r0, 0);
        c0 = Math.max(c0, 0);
        r1 = Math.min(r1, bmp.getHeight());
        c1 = Math.min(c1, bmp.getWidth());
        for (int r = r0; r < r1; r++)
        {
            for (int c = c0; c < c1; c++)
            {
                if (Color.alpha(bmp.getPixel(c, r)) > 0)
                    return true;
            }
        }

        return false;
    }

    @Override
    public void setImageBitmap(Bitmap bm)
    {
        super.setImageBitmap(bm);
        initMatrix();
        if (bm != null)
        {
            if (getContext() instanceof StickersActivity)
            {
                StickersActivity cameraActivity = ((StickersActivity) getContext());
                // cameraActivity.loadingDialog.dismiss();
            }/*
              * else if (getContext() instanceof CategoryCameraActivity) {
              * CategoryCameraActivity cameraActivity =
              * ((CategoryCameraActivity) getContext());
              * cameraActivity.loadingDialog.dismiss(); }
              */
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable)
    {
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(((BitmapDrawable)drawable).getBitmap(), (int)(100*dp), (int)(100*dp), true));
        super.setImageDrawable(d);

        initMatrix();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        if (changed)
            initMatrix();
    }

    private void initMatrix()
    {
        transX = getWidth() / 2;
        transY = getHeight() / 2;
        //		scale = (float) mVisual.getDefaultScale()
        //				* getResources().getDisplayMetrics().density / 2;
        scale = 1.0f;
        rotate = 0;

        move(0, 0, 1, 0);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (deleteMode)
        {
            Drawable d = getResources().getDrawable(R.drawable.snaps_delete_icon);
            int w = d.getIntrinsicWidth() / 2;
            int h = d.getIntrinsicHeight() / 2;
            d.setBounds(0, 0, 2 * w, 2 * h);
            canvas.save();

            canvas.translate(transX - w, transY - h);
            d.draw(canvas);
            canvas.restore();
        }
    }

    public void move(float dx, float dy, float dscale, float drotate)
    {
        if (!(getDrawable() instanceof BitmapDrawable))
            return;
        int bw = getDrawable().getIntrinsicWidth();
        int bh = getDrawable().getIntrinsicHeight();
        transX += dx;
        transY += dy;
        scale *= dscale;

        transX = Math.min(transX, getWidth());
        transX = Math.max(transX, 0);
        transY = Math.min(transY, getHeight());
        transY = Math.max(transY, 0);

        //		scale = (float) Math.min(scale, mVisual.getMaxScale()
        //				* getResources().getDisplayMetrics().density / 2);
        //		scale = (float) Math.max(scale, mVisual.getMinScale()
        //				* getResources().getDisplayMetrics().density / 2);

        //		if (mVisual.canRotate())
        //			rotate += drotate;
        if (true)
            rotate += drotate;
        Matrix matrix = new Matrix();
        matrix.postTranslate(-bw / 2, -bh / 2);
        matrix.postRotate(-relativeDeviceRotation); // reverse the angle
        if (shouldFlip)
        {
            if (isPortraitDevice)
            {
                if (relativeDeviceRotation == 0 || relativeDeviceRotation == 180)
                {
                    matrix.postScale(-1, 1, 1, -1);
                } else
                {
                    matrix.postScale(1, -1, -1, 1);
                }
            } else
            {
                if (relativeDeviceRotation == 0 || relativeDeviceRotation == 180)
                {
                    matrix.postScale(1, -1, -1, 1);
                } else
                {
                    matrix.postScale(-1, 1, 1, -1);
                }
            }
        }
        //		if (mVisual.canRotate())
        //			matrix.postRotate(rotate);
        matrix.postRotate(rotate);
        matrix.postScale(scale, scale);
        matrix.postTranslate(transX, transY);
        setImageMatrix(matrix);
        postInvalidate();
    }

    public void flip()
    {
        //		if (mVisual.canFlip()) {
        if (true)
        {
            shouldFlip = !shouldFlip;
            move(0, 0, -1, 1);
        }
    }

    public void setRelativeDeviceRotation(int rotation)
    {
        if (relativeDeviceRotation != rotation)
        {
            this.relativeDeviceRotation = rotation;
            move(0, 0, 1, 0);
        }
    }

    public boolean isDeleteMode()
    {
        return this.deleteMode;
    }

    public void setDeleteMode(boolean deleteMode)
    {
        this.deleteMode = deleteMode;
        postInvalidate();
        if (deleteMode)
        {
            RotateAnimation rotate = new RotateAnimation(-2, 2, transX, transY);
            rotate.setDuration(50);
            rotate.setRepeatCount(RotateAnimation.INFINITE);
            rotate.setRepeatMode(RotateAnimation.REVERSE);
            rotate.setStartOffset((long) (Math.random() * 100));
            this.startAnimation(rotate);
        } else
        {
            this.clearAnimation();
        }
    }
}
