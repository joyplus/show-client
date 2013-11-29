package com.joyplus.tvhelper.utils;

import com.joyplus.tvhelper.ui.SettingDialog;

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
	private static final String TOKEN = "token";
	private static final String ERWEIMA_URL = "erweima_url";
	private static final String QQ_NAME = "qq_name";
	private static final String QQ_AVATAR = "qq_avatar";
    private static final String SUB_SIZE  = "sub_size";
    private static final String SUB_SWITCH = "sub_visible_switch";
    private static final String PLAY_IF_JUST_NOW = "play_if_just_now";
    private static final String PLAY_DEFINATION = "play_defination";
	
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
	
	public static void setQQName(Context c ,String nickname){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		Editor editor = s.edit();
		editor.putString(QQ_NAME, nickname);
		editor.commit();
	}
	
	public static void setQQAvatare(Context c ,String avatare){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		Editor editor = s.edit();
		editor.putString(QQ_AVATAR, avatare);
		editor.commit();
	}
	
	public static void setToken(Context c ,String token){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		Editor editor = s.edit();
		editor.putString(TOKEN, token);
		editor.commit();
	}
	
	public static void setErweima_url(Context c ,String erweima_url){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		Editor editor = s.edit();
		editor.putString(ERWEIMA_URL, erweima_url);
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
	
	public static String getQQName(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getString(QQ_NAME, "");
	}
	public static String getQQAvatar(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getString(QQ_AVATAR, "");
	}
	public static String getToken(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getString(TOKEN, "");
	}
	public static String getErweima_url(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getString(ERWEIMA_URL, "");
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
	
	public static void setSubSize(Context c, int size){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		Editor editor = s.edit();
		editor.putInt(SUB_SIZE, size);
		editor.commit();
	}
	
	public static void removeSubSize(Context c){
		removeByKey(c, SUB_SIZE);
	}
	
	private static void removeByKey(Context c, String key){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		Editor editor = s.edit();
		editor.remove(key);
		editor.commit();
	}
	
	public static int getSubSize(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getInt(SUB_SIZE, SettingDialog.FONT_SIZE_MIDDLE);
	}
	
	public static void setSubSwitch(Context c, boolean isOn){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		Editor editor = s.edit();
		editor.putBoolean(SUB_SWITCH, isOn);
		editor.commit();
	}
	public static void removeSubSwitch(Context c){
		removeByKey(c, SUB_SWITCH);
	}
	public static boolean getSubSwitch(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getBoolean(SUB_SWITCH, true);
	}
	
	public static void setDefualteDefination(Context c, int defination){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		Editor editor = s.edit();
		editor.putInt(PLAY_DEFINATION, defination);
		editor.commit();
	}
	public static void removeDefualteDefination(Context c){
		removeByKey(c, PLAY_DEFINATION);
	}
	public static int getDefualteDefination(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getInt(PLAY_DEFINATION, Constant.DEFINATION_HD2);
	}
	
	public static void setDefualtePlayChoice(Context c, boolean isJustNow){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		Editor editor = s.edit();
		editor.putBoolean(PLAY_IF_JUST_NOW, isJustNow);
		editor.commit();
	}
	public static void removeDefualtePlayChoice(Context c){
		removeByKey(c, PLAY_IF_JUST_NOW);
	}
	
	public static boolean getDefualtePlayChoice(Context c){
		SharedPreferences s = c.getSharedPreferences(JOYPLUS, 0);
		return s.getBoolean(PLAY_IF_JUST_NOW, true);
	}
	
	
}
