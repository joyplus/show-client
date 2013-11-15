package com.joyplus.tvhelper.utils;

import android.R.bool;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesUtils {
	private static final String JOYPLUS = "ijoypus";
	private static final String ISACCEPTED = "isAccepted";
	private static final String PINCODE = "pincode";
	private static final String CHANNEL = "channel";
	private static final String ISNEEDCONFIRM = "isneedconfirm";
	private static final String ISAUTODELETE = "isautodelete";
	private static final String PINCODE_MD5 = "md5_code";
	private static final String MAC_ADDR = "mac_addr";
	private static final String DISPLAYURL = "web_url";
	private static final String LASTVERSION = "last_version";
	private static final String BAIDU_ACCESS_TOKEN = "baidu_access_token";
	
	public static void changeAcceptedStatue(Context c ,boolean isAccepted){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		Editor editor = s.edit();
		editor.putBoolean(ISACCEPTED, isAccepted);
		editor.commit();
	}
	
	public static void setPincode(Context c ,String pincode){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		Editor editor = s.edit();
		editor.putString(PINCODE, pincode);
		editor.commit();
	}
	public static void setMac(Context c ,String macAddr){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		Editor editor = s.edit();
		editor.putString(MAC_ADDR, macAddr);
		editor.commit();
	}
	public static void setIsneedConfirm(Context c , boolean isneedConfirm){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		Editor editor = s.edit();
		editor.putBoolean(ISNEEDCONFIRM, isneedConfirm);
		editor.commit();
	}
	
	public static void setIsautodelete(Context c , boolean isautodelete){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		Editor editor = s.edit();
		editor.putBoolean(ISAUTODELETE, isautodelete);
		editor.commit();
	}
	
	public static void setChannel(Context c ,String channel){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		Editor editor = s.edit();
		editor.putString(CHANNEL, channel);
		editor.commit();
	}
	
	public static void setWebUrl(Context c ,String url){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		Editor editor = s.edit();
		editor.putString(DISPLAYURL, url);
		editor.commit();
	}
	
	public static String getWebUrl(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getString(DISPLAYURL, null);
	}
	
	public static boolean isAcceped(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getBoolean(ISACCEPTED, false);
	}
	
	public static boolean isneedConfirm(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getBoolean(ISNEEDCONFIRM, false);
	}
	
	public static boolean isautodelete(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getBoolean(ISAUTODELETE, false);
	}
	
	public static String getPincode(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getString(PINCODE, null);
	}
	
	public static String getMac(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getString(MAC_ADDR, null);
	}
	public static String getChannel(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getString(CHANNEL, null);
	}
	
	public static void setPincodeMd5(Context c,String md5){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		Editor editor = s.edit();
		editor.putString(PINCODE_MD5, md5);
		editor.commit();
	}
	
	public static String getBaiduAccessToken(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getString(BAIDU_ACCESS_TOKEN, "");
	}
	public static void setBaiduAccessToken(Context c,String token){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		Editor editor = s.edit();
		editor.putString(BAIDU_ACCESS_TOKEN, token);
		editor.commit();
	}
	
	public static String getPincodeMd5(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getString(PINCODE_MD5, null);
	}
	
	public static int getGuidLastVersion(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getInt(LASTVERSION, -1);
	}
	
	public static void setGuidLastVersion(Context c, int version){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		Editor editor = s.edit();
		editor.putInt(LASTVERSION, version);
		editor.commit();
	}
}
