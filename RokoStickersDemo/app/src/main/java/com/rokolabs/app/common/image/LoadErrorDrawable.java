package com.rokolabs.app.common.image;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class LoadErrorDrawable extends BitmapDrawable
{
    public LoadErrorDrawable(Resources res, Bitmap bitmap){
        super(res, bitmap);
    }
}
