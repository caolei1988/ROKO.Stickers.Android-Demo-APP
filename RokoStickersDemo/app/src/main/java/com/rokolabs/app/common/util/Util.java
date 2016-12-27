package com.rokolabs.app.common.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.rokolabs.app.common.http.AsyncHttpClient;

import org.apache.http.HttpRequest;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Util {
	private static String HTTP_HOST = "NO_HOST";
	
	public static String getHttpHost(){
		return HTTP_HOST;
	}

	public static String GCM_SENDER_ID;

	public static boolean loadMeta(Context ctx) {
		SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(ctx);
		String curEnvironment = SP.getString("currentEnvironment", "");
		Log.i("Util","curEnvironment = "+curEnvironment);
		try {
			ApplicationInfo ai = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			String key = bundle.getString("ROKOMobiAPIToken"+curEnvironment);
			String host = bundle.getString("ROKOMobiAPIURL"+curEnvironment);
			//GCM_SENDER_ID = String.valueOf(bundle.get("ROKOMobiGCMSenderID"));
			GCM_SENDER_ID = bundle.getString("ROKOMobiGCMSenderID");
			if (!TextUtils.isEmpty(key)) {
				AsyncHttpClient.HEADER_API_KEY_CONTENT = key;
			}
			if (!TextUtils.isEmpty(host)) {
				HTTP_HOST = "https://" + host + "/";
				return true;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static final int devicetype = 101;
	public static final int REQUEST_TIMEOUT = 5 * 1000;

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 8;
	public static final int MIN_MINUTES_IN_DAY = 0;
	public static final int MAX_MINUTES_IN_DAY = 24 * 60 - 1;

	public static final int GROUP_PRIVATE = 1000100;
	public static final int GROUP_PUBLIC = 1000101;

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

	/**
	 * Check if a JSON string is empty
	 * 
	 * @param str
	 *            json string
	 * @return true if the string is null or empty
	 */
	public static boolean isEmpty(String str) {
		if (str == null || str.length() == 0 || str.equalsIgnoreCase("null"))
			return true;
		return false;
	}

	public static String getDateStringGMT(Context context, long time) {
		java.text.DateFormat df = DateFormat.getTimeFormat(context);

		return getDateStringGMT("MMM-dd-yyyy, ", time)
				+ df.format(new Date(time));
	}

	public static String getDateStringGMT(String pattern, long time) {
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		// df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return df.format(new Date(time));
	}

	public static int px2sp(Context context, float pxValue) {
		return (int) (pxValue
				/ context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
	}

	public static int dip(Context context, int px) {
		return Math.round(context.getResources().getDisplayMetrics().density
				* px);
	}

	/**
	 * This method convets dp unit to equivalent device specific value in
	 * pixels.
	 * 
	 * @param dp
	 *            A value in dp(Device independent pixels) unit. Which we need
	 *            to convert into pixels
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent Pixels equivalent to dp according to
	 *         device
	 */
	public static float convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	/**
	 * This method converts device specific pixels to device independent pixels.
	 * 
	 * @param px
	 *            A value in px (pixels) unit. Which we need to convert into db
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent db equivalent to px value
	 */
	public static float convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;

	}

	public static Bitmap getRoundedCornerBitmap(Context context, Bitmap input,
												int pixels, int w, int h, boolean squareTL, boolean squareTR,
												boolean squareBL, boolean squareBR) {

		Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final float densityMultiplier = context.getResources()
				.getDisplayMetrics().density;

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);

		// make sure that our rounded corner is scaled appropriately
		final float roundPx = pixels * densityMultiplier;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		// draw rectangles over the corners we want to be square
		if (squareTL) {
			canvas.drawRect(0, 0, w / 2, h / 2, paint);
		}
		if (squareTR) {
			canvas.drawRect(w / 2, 0, w, h / 2, paint);
		}
		if (squareBL) {
			canvas.drawRect(0, h / 2, w / 2, h, paint);
		}
		if (squareBR) {
			canvas.drawRect(w / 2, h / 2, w, h, paint);
		}

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(input, 0, 0, paint);

		return output;
	}

	public static boolean isEmail(String email) {
		if (email != null && email.length() >= 0 && !email.contains(" ")) {
			String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(email);
			return m.find();
		}
		return false;
	}

	public static boolean isImageFile(String extension) {
		if (isEmpty(extension)) {
			return false;
		}
		return extension.equalsIgnoreCase("png")
				|| extension.equalsIgnoreCase("jpg")
				|| extension.equalsIgnoreCase("gif")
				|| extension.equalsIgnoreCase("bmp");
	}

	/**
	 * Get rid of "null" string if you try to get a string object
	 * 
	 * @param jobj
	 *            JSON object contains the string
	 * @param key
	 *            key for the string
	 * @return value for the key with "null" removed
	 */
	public static String optString(JSONObject jobj, String key) {
		String str = jobj.optString(key);
		if (str.equalsIgnoreCase("null"))
			str = "";
		return str;
	}

	public static void shareViaEmail(Context context, String title,
									 Bundle bundle) {
		shareVia(context, "mail", "message/rfc822", title, bundle);
	}

	public static void shareVia(Context context, String packageFilter,
								String mime, String title, Bundle bundle) {
		List<Intent> targetedShareIntents = new ArrayList<Intent>();
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType(mime);
		List<ResolveInfo> resInfo = context.getPackageManager()
				.queryIntentActivities(share, 0);
		if (!resInfo.isEmpty()) {
			for (ResolveInfo info : resInfo) {
				Intent targetedShare = new Intent(
						android.content.Intent.ACTION_SEND);
				targetedShare.setType(mime);

				if (info.activityInfo.packageName.toLowerCase().contains(
						packageFilter)
						|| info.activityInfo.name.toLowerCase().contains(
								packageFilter)) {
					// bundle.
					targetedShare.putExtras(bundle);
					targetedShare.setPackage(info.activityInfo.packageName);
					targetedShareIntents.add(targetedShare);
				}
			}

			Intent chooserIntent = Intent.createChooser(
					targetedShareIntents.remove(0), title);
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
					targetedShareIntents.toArray(new Parcelable[] {}));
			context.startActivity(chooserIntent);
		}

	}

	public static String MD5(String originString) {
		if (originString != null) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] results = md.digest(originString.getBytes());
				String resultString = byteArrayToHexString(results);
				return resultString;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	
	public static String printIntent(Intent data){
		StringBuffer buf = new StringBuffer();
		Bundle b = data.getExtras();
		if (b != null) {
			for (String str : b.keySet()) {
				buf.append(str + ": " + b.get(str) + "\n");
			}
		}
		return buf.toString();
	}

	public static boolean isRokoRequest(HttpRequest request) {
		String host = request.getFirstHeader("host").getValue();
		Uri roko = Uri.parse(getHttpHost());
		if(roko.getHost().equalsIgnoreCase(host)){
			return true;
		}
		return false;
	}

}