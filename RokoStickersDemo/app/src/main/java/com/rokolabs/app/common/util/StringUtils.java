package com.rokolabs.app.common.util;

import java.math.BigInteger;
import java.util.Random;

public class StringUtils {
	private static Random random = new Random();
	
	public static String getHexRandomString(int length){
		return getRadomString(length, 16);
	}
	
	public static String getRadomString(int length, int radix){
		byte[] buf = new byte[length];
		random.nextBytes(buf);
		BigInteger bi = new BigInteger(buf).abs();
		return bi.toString(radix);
	}
}
