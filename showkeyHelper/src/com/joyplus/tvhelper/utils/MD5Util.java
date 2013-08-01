package com.joyplus.tvhelper.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

	private static final String TAG = "MD5Util";

	protected static char[] hexDigits;
	protected static MessageDigest messagedigest;

	static {
		char[] arrayOfChar = new char[16];

		arrayOfChar[0] = 48;
		arrayOfChar[1] = 49;
		arrayOfChar[2] = 50;
		arrayOfChar[3] = 51;
		arrayOfChar[4] = 52;
		arrayOfChar[5] = 53;
		arrayOfChar[6] = 54;
		arrayOfChar[7] = 55;
		arrayOfChar[8] = 56;
		arrayOfChar[9] = 57;
		arrayOfChar[10] = 97;
		arrayOfChar[11] = 98;
		arrayOfChar[12] = 99;
		arrayOfChar[13] = 100;
		arrayOfChar[14] = 101;
		arrayOfChar[15] = 102;

		hexDigits = arrayOfChar;

		try {

			messagedigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			Log.i(TAG, MD5Util.class.getName()
					+ "初始化失败，MessageDigest不支持MD5Util。");
			e.printStackTrace();
		}
	}

	private static void appendHexPair(byte paramByte,
			StringBuffer paramStringBuffer) {

		char c1 = hexDigits[((paramByte & 0xF0) >> 4)];
		char c2 = hexDigits[(paramByte & 0xF)];
		paramStringBuffer.append(c1);
		paramStringBuffer.append(c2);
	}

	private static String bufferToHex(byte[] paramArrayOfByte) {
		
		return bufferToHex(paramArrayOfByte, 0, paramArrayOfByte.length);
	}

	private static String bufferToHex(byte[] paramArrayOfByte, int paramInt1,
			int paramInt2) {
		
		StringBuffer localStringBuffer = new StringBuffer(paramInt2 * 2);
		int i = paramInt1 + paramInt2;
		for (int j = paramInt1;j < i; j++) {
				
			appendHexPair(paramArrayOfByte[j], localStringBuffer);
		}
		
		return localStringBuffer.toString();
	}

	public static boolean checkPassword(String paramString1, String paramString2) {
		return getMD5String(paramString1).equals(paramString2);
	}

	public static String getFileMD5String(File paramFile) throws IOException {
		FileInputStream localFileInputStream = new FileInputStream(paramFile);
		byte[] arrayOfByte = new byte[1024];
		while (true) {
			int i = localFileInputStream.read(arrayOfByte);
			if (i <= 0) {
				localFileInputStream.close();
				return bufferToHex(messagedigest.digest());
			}
			messagedigest.update(arrayOfByte, 0, i);
		}
	}

	public static String getFileMD5String_old(File paramFile)
			throws IOException {
		FileInputStream fis = new FileInputStream(paramFile);
		MappedByteBuffer localMappedByteBuffer = fis.getChannel().
				map(FileChannel.MapMode.READ_ONLY, 0L,paramFile.length());
		messagedigest.update(localMappedByteBuffer);
		fis.close();
		return bufferToHex(messagedigest.digest());
	}

	public static String getMD5String(String paramString) {
		
		return getMD5String(paramString.getBytes());
	}

	public static String getMD5String(byte[] paramArrayOfByte) {
		
		messagedigest.update(paramArrayOfByte);
		return bufferToHex(messagedigest.digest());
	}

	public static void main(String[] paramArrayOfString) throws IOException {
		long l1 = System.currentTimeMillis();
		String str = getFileMD5String(new File("C:/12345.txt"));
		long l2 = System.currentTimeMillis();
		System.out.println("md5:" + str + " time:" + (l2 - l1) / 1000L + "s");
	}

}
