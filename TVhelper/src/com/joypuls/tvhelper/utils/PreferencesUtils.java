package com.joypuls.tvhelper.utils;

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
		return s.getBoolean(ISAUTODELETE, true);
	}
	
	public static String getPincode(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getString(PINCODE, null);
	}
	public static String getChannel(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getString(CHANNEL, null);
	}
}
