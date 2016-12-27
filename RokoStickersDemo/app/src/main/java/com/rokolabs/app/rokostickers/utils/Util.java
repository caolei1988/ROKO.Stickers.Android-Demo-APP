package com.rokolabs.app.rokostickers.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public final class Util {
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 8;
	public static final int MIN_MINUTES_IN_DAY = 0;
	public static final int MAX_MINUTES_IN_DAY = 24 * 60 - 1;

	public static final int CHANNEL_EMAIL = 1;
	public static final int CHANNEL_TWITTER = 2;
	public static final int CHANNEL_FACEBOOK = 3;
	public static final int CHANNEL_SMS = 4;
	public static final int EXITOPTION_CANCEL = 1;
	public static final int EXITOPTION_CLOSE = 2;
	private static final int maxNumOfPixels = 512;

	public static byte[] IS2ByteArray(InputStream inputStream)
			throws IOException {
		// this dynamically extends to take the bytes you read
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

		// this is storage overwritten on each iteration with bytes
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

		// we need to know how may bytes were read to write them to the
		// byteBuffer
		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			byteBuffer.write(buffer, 0, len);
		}

		// and then we can return your byte array.
		return byteBuffer.toByteArray();
	}

	public static Bitmap createImageThumb(Uri uri, Context context)
			throws FileNotFoundException, IOException {
		ContentResolver resolver = context.getContentResolver();
		byte[] data = IS2ByteArray(resolver.openInputStream(uri));
		Bitmap bitmap = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		opts.inSampleSize = computeSampleSize(opts, -1, maxNumOfPixels
				* maxNumOfPixels);
		Constants.i("createImageThumb() inSampleSize: " + opts.inSampleSize);
		opts.inJustDecodeBounds = false;
		try {
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		} catch (Exception e) {
		}
		return bitmap;
	}

	public static Bitmap createImageThumb(Uri uri, int maxWidth, int maxHeight, Context context) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeStream(context.getContentResolver()
					.openInputStream(uri), null, options);
			int srcWidth = options.outWidth;
			int srcHeight = options.outHeight;
			Log.v("test", "srcWidth = "+srcWidth + " , srcHeight = "+srcHeight + " , maxWidth = "+maxWidth + " , maxHeight = "+maxHeight);
//			options.inSampleSize = 1;
//			if (picWidth > picHeight) {
//				if (picWidth > screenWidth)
//					options.inSampleSize = picWidth / screenWidth;
//			} else {
//				if (picHeight > screenHeight)
//					options.inSampleSize = picHeight / screenHeight;
//			}
			
			int desWidth = 0;  
            int desHeight = 0;  
            // 缩放比例  
            double ratio = 0.0;  
            if (srcWidth > srcHeight) {  
                    ratio = srcWidth / maxWidth;  
                    desWidth = maxWidth;  
                    desHeight = (int) (srcHeight / ratio);  
            } else {  
                    ratio = srcHeight / maxHeight;  
                    desHeight = maxHeight;  
                    desWidth = (int) (srcWidth / ratio);  
            }  
			
            options.inSampleSize = (int) (ratio) + 1;  
			options.inJustDecodeBounds = false;
			options.outWidth = desWidth;  
			options.outHeight = desHeight;  
			bitmap = BitmapFactory.decodeStream(context.getContentResolver()
					.openInputStream(uri), null, options);
			/*
			 * if (bitmap.isRecycled() == false) { bitmap.recycle(); }
			 */
			System.gc();
		} catch (Exception e1) {
		}
		return bitmap;

	}

	public static Bitmap createImageThumb(String filePath) {
		Bitmap bitmap = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, opts);
		opts.inSampleSize = computeSampleSize(opts, -1, maxNumOfPixels
				* maxNumOfPixels);
		opts.inJustDecodeBounds = false;
		try {
			bitmap = BitmapFactory.decodeFile(filePath, opts);
		} catch (Exception e) {
		}
		return bitmap;
	}

	public static Bitmap createImageThumb(byte[] data) {
		Bitmap bitmap = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		opts.inSampleSize = computeSampleSize(opts, -1, maxNumOfPixels
				* maxNumOfPixels);
		Constants.i("createImageThumb() inSampleSize: " + opts.inSampleSize);
		opts.inJustDecodeBounds = false;
		try {
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		} catch (Exception e) {
		}
		return bitmap;
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? maxNumOfPixels : (int) Math
				.min(Math.floor(w / minSideLength),
						Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

}