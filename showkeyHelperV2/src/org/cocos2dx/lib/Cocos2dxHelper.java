/****************************************************************************
Copyright (c) 2010-2013 cocos2d-x.org

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 ****************************************************************************/
package org.cocos2dx.lib;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.zxing.WriterException;
import com.joyplus.tvhelper.MyApp;
import com.joyplus.tvhelper.db.DBServices;
import com.joyplus.tvhelper.entity.MoviePlayHistoryInfo;
import com.joyplus.tvhelper.entity.XLLXUserInfo;
import com.joyplus.tvhelper.utils.Constant;
import com.joyplus.tvhelper.utils.EncodingHandler;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.PreferencesUtils;
import com.joyplus.tvhelper.utils.Utils;
import com.joyplus.tvhelper.utils.XunLeiLiXianUtil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class Cocos2dxHelper {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final String PREFS_NAME = "cocos2dx";

	// ===========================================================
	// Fields
	// ===========================================================

	private static Cocos2dxMusic sCocos2dMusic;
	private static Cocos2dxSound sCocos2dSound;
	private static AssetManager sAssetManager;
	private static Cocos2dxAccelerometer sCocos2dxAccelerometer;
	private static boolean sAccelerometerEnabled;
	private static String sPackageName;
	private static String sFileDirectory;
	private static Context sContext = null;
	private static Cocos2dxHelperListener sCocos2dxHelperListener;

	// ===========================================================
	// Constructors
	// ===========================================================

	public static void init(final Context pContext, final Cocos2dxHelperListener pCocos2dxHelperListener) {
		final ApplicationInfo applicationInfo = pContext.getApplicationInfo();
		
		Cocos2dxHelper.sContext = pContext;
		Cocos2dxHelper.sCocos2dxHelperListener = pCocos2dxHelperListener;

		Cocos2dxHelper.sPackageName = applicationInfo.packageName;
		Cocos2dxHelper.sFileDirectory = pContext.getFilesDir().getAbsolutePath();
		Log.d("helper", "sFileDirectory");
		Cocos2dxHelper.nativeSetApkPath(applicationInfo.sourceDir);

		Cocos2dxHelper.sCocos2dxAccelerometer = new Cocos2dxAccelerometer(pContext);
		Cocos2dxHelper.sCocos2dMusic = new Cocos2dxMusic(pContext);
		Cocos2dxHelper.sCocos2dSound = new Cocos2dxSound(pContext);
		Cocos2dxHelper.sAssetManager = pContext.getAssets();
		Cocos2dxBitmap.setContext(pContext);
		Cocos2dxETCLoader.setContext(pContext);
	}
	

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	private static native void nativeSetApkPath(final String pApkPath);

	private static native void nativeSetEditTextDialogResult(final byte[] pBytes);
	
	private static native void nativeSetXunLeiLoginDialogResult(final int isBack);
	
	private static native void nativeSetBaiduLoginDialogResult(final int isBack);
	
	private static native void nativeSetPincodeResult(final int isSuccess);
	
	private static native void nativeUpdateQQ();
	
	private static native void nativeSetSettingResult(final int isSuccess);

	public static String getCocos2dxPackageName() {
		return Cocos2dxHelper.sPackageName;
	}

	public static String getCocos2dxWritablePath() {
		//
		File localFile;
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			localFile = Environment.getExternalStorageDirectory();
			if(localFile.canWrite()){
				localFile = new File(localFile.getAbsolutePath()+"/showkeyhelper");
				if(!localFile.exists()){
					try {
						localFile.mkdir();
						return localFile.getAbsolutePath();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return Cocos2dxHelper.sFileDirectory;
					}
				}
				return localFile.getAbsolutePath();
				
			}else{
				return Cocos2dxHelper.sFileDirectory;
			}
		}else{
			return Cocos2dxHelper.sFileDirectory;
		}
	}

	public static String getCurrentLanguage() {
		return Locale.getDefault().getLanguage();
	}
	
	public static String getDeviceModel(){
		return Build.MODEL;
    }

	public static AssetManager getAssetManager() {
		return Cocos2dxHelper.sAssetManager;
	}

	public static void enableAccelerometer() {
		Cocos2dxHelper.sAccelerometerEnabled = true;
		Cocos2dxHelper.sCocos2dxAccelerometer.enable();
	}


	public static void setAccelerometerInterval(float interval) {
		Cocos2dxHelper.sCocos2dxAccelerometer.setInterval(interval);
	}

	public static void disableAccelerometer() {
		Cocos2dxHelper.sAccelerometerEnabled = false;
		Cocos2dxHelper.sCocos2dxAccelerometer.disable();
	}

	public static void preloadBackgroundMusic(final String pPath) {
		Cocos2dxHelper.sCocos2dMusic.preloadBackgroundMusic(pPath);
	}

	public static void playBackgroundMusic(final String pPath, final boolean isLoop) {
		Cocos2dxHelper.sCocos2dMusic.playBackgroundMusic(pPath, isLoop);
	}

	public static void resumeBackgroundMusic() {
		Cocos2dxHelper.sCocos2dMusic.resumeBackgroundMusic();
	}

	public static void pauseBackgroundMusic() {
		Cocos2dxHelper.sCocos2dMusic.pauseBackgroundMusic();
	}

	public static void stopBackgroundMusic() {
		Cocos2dxHelper.sCocos2dMusic.stopBackgroundMusic();
	}

	public static void rewindBackgroundMusic() {
		Cocos2dxHelper.sCocos2dMusic.rewindBackgroundMusic();
	}

	public static boolean isBackgroundMusicPlaying() {
		return Cocos2dxHelper.sCocos2dMusic.isBackgroundMusicPlaying();
	}

	public static float getBackgroundMusicVolume() {
		return Cocos2dxHelper.sCocos2dMusic.getBackgroundVolume();
	}

	public static void setBackgroundMusicVolume(final float volume) {
		Cocos2dxHelper.sCocos2dMusic.setBackgroundVolume(volume);
	}

	public static void preloadEffect(final String path) {
		Cocos2dxHelper.sCocos2dSound.preloadEffect(path);
	}

	public static int playEffect(final String path, final boolean isLoop) {
		return Cocos2dxHelper.sCocos2dSound.playEffect(path, isLoop);
	}

	public static void resumeEffect(final int soundId) {
		Cocos2dxHelper.sCocos2dSound.resumeEffect(soundId);
	}

	public static void pauseEffect(final int soundId) {
		Cocos2dxHelper.sCocos2dSound.pauseEffect(soundId);
	}

	public static void stopEffect(final int soundId) {
		Cocos2dxHelper.sCocos2dSound.stopEffect(soundId);
	}

	public static float getEffectsVolume() {
		return Cocos2dxHelper.sCocos2dSound.getEffectsVolume();
	}

	public static void setEffectsVolume(final float volume) {
		Cocos2dxHelper.sCocos2dSound.setEffectsVolume(volume);
	}

	public static void unloadEffect(final String path) {
		Cocos2dxHelper.sCocos2dSound.unloadEffect(path);
	}

	public static void pauseAllEffects() {
		Cocos2dxHelper.sCocos2dSound.pauseAllEffects();
	}

	public static void resumeAllEffects() {
		Cocos2dxHelper.sCocos2dSound.resumeAllEffects();
	}

	public static void stopAllEffects() {
		Cocos2dxHelper.sCocos2dSound.stopAllEffects();
	}

	public static void end() {
		Cocos2dxHelper.sCocos2dMusic.end();
		Cocos2dxHelper.sCocos2dSound.end();
	}

	public static void onResume() {
		if (Cocos2dxHelper.sAccelerometerEnabled) {
			Cocos2dxHelper.sCocos2dxAccelerometer.enable();
		}
	}

	public static void onPause() {
		if (Cocos2dxHelper.sAccelerometerEnabled) {
			Cocos2dxHelper.sCocos2dxAccelerometer.disable();
		}
	}

	public static void terminateProcess() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	private static void showDialog(final String pTitle, final String pMessage) {
		Cocos2dxHelper.sCocos2dxHelperListener.showDialog(pTitle, pMessage);
	}
	
	private static void playVideo(final String date) {
		Cocos2dxHelper.sCocos2dxHelperListener.playVideo(date);
	}

	private static void showEditTextDialog(final String pTitle, final String pMessage, final int pInputMode, final int pInputFlag, final int pReturnType, final int pMaxLength) {
		Cocos2dxHelper.sCocos2dxHelperListener.showEditTextDialog(pTitle, pMessage, pInputMode, pInputFlag, pReturnType, pMaxLength);
	}
	
	private static void showXunleiLoginDialog(){
		Cocos2dxHelper.sCocos2dxHelperListener.showXunLeiLoginDialog();
	}
	private static void showBaiduLoginDialog(){
		Cocos2dxHelper.sCocos2dxHelperListener.showBaiduDailog();
	}
	
	public static void setEditTextDialogResult(final String pResult) {
		try {
			final byte[] bytesUTF8 = pResult.getBytes("UTF8");

			Cocos2dxHelper.sCocos2dxHelperListener.runOnGLThread(new Runnable() {
				@Override
				public void run() {
					Cocos2dxHelper.nativeSetEditTextDialogResult(bytesUTF8);
				}
			});
		} catch (UnsupportedEncodingException pUnsupportedEncodingException) {
			/* Nothing. */
		}
	}
	public static void setXunLeiLoginDialogResult(final boolean isBack) {
		try {
//			final byte[] bytesUTF8_account = account.getBytes("UTF8");
//			final byte[] bytesUTF8_password = password.getBytes("UTF8");
//			final byte[] bytesUTF8_yanzhengma = yanzhengma.getBytes("UTF8");
			final int isBackClick = isBack?1:0;
			Cocos2dxHelper.sCocos2dxHelperListener.runOnGLThread(new Runnable() {
				@Override
				public void run() {
					Cocos2dxHelper.nativeSetXunLeiLoginDialogResult(isBackClick);
				}
			});
		} catch (Exception Exception) {
			/* Nothing. */
		}
	}
	public static void setBaiduLoginDialogResult(final boolean isBack) {
		try {
			final int isBackClick = isBack?1:0;
			Cocos2dxHelper.sCocos2dxHelperListener.runOnGLThread(new Runnable() {
				@Override
				public void run() {
					Cocos2dxHelper.nativeSetBaiduLoginDialogResult(isBackClick);
				}
			});
		} catch (Exception Exception) {
			/* Nothing. */
		}
	}
	
	public static void setSettingResult(final boolean isBack){
		try {
			final int isBackClick = isBack?1:0;
			Cocos2dxHelper.sCocos2dxHelperListener.runOnGLThread(new Runnable() {
				@Override
				public void run() {
					Cocos2dxHelper.nativeSetSettingResult(isBackClick);
				}
			});
		} catch (Exception Exception) {
			/* Nothing. */
		}
	}

    public static int getDPI()
    {
		if (sContext != null)
		{
			DisplayMetrics metrics = new DisplayMetrics();
			WindowManager wm = ((Activity)sContext).getWindowManager();
			if (wm != null)
			{
				Display d = wm.getDefaultDisplay();
				if (d != null)
				{
					d.getMetrics(metrics);
					return (int)(metrics.density*160.0f);
				}
			}
		}
		return -1;
    }
    
    // ===========================================================
 	// Functions for CCUserDefault
 	// ===========================================================
    
    public static boolean getBoolForKey(String key, boolean defaultValue) {
    	SharedPreferences settings = ((Activity)sContext).getSharedPreferences(Cocos2dxHelper.PREFS_NAME, 0);
    	return settings.getBoolean(key, defaultValue);
    }
    
    public static int getIntegerForKey(String key, int defaultValue) {
    	SharedPreferences settings = ((Activity)sContext).getSharedPreferences(Cocos2dxHelper.PREFS_NAME, 0);
    	return settings.getInt(key, defaultValue);
    }
    
    public static float getFloatForKey(String key, float defaultValue) {
    	SharedPreferences settings = ((Activity)sContext).getSharedPreferences(Cocos2dxHelper.PREFS_NAME, 0);
    	return settings.getFloat(key, defaultValue);
    }
    
    public static double getDoubleForKey(String key, double defaultValue) {
    	// SharedPreferences doesn't support saving double value
    	SharedPreferences settings = ((Activity)sContext).getSharedPreferences(Cocos2dxHelper.PREFS_NAME, 0);
    	return settings.getFloat(key, (float)defaultValue);
    }
    
    public static String getStringForKey(String key, String defaultValue) {
    	SharedPreferences settings = ((Activity)sContext).getSharedPreferences(Cocos2dxHelper.PREFS_NAME, 0);
    	return settings.getString(key, defaultValue);
    }
    
    public static void setBoolForKey(String key, boolean value) {
    	SharedPreferences settings = ((Activity)sContext).getSharedPreferences(Cocos2dxHelper.PREFS_NAME, 0);
    	SharedPreferences.Editor editor = settings.edit();
    	editor.putBoolean(key, value);
    	editor.commit();
    }
    
    public static void setIntegerForKey(String key, int value) {
    	SharedPreferences settings = ((Activity)sContext).getSharedPreferences(Cocos2dxHelper.PREFS_NAME, 0);
    	SharedPreferences.Editor editor = settings.edit();
    	editor.putInt(key, value);
    	editor.commit();
    }
    
    public static void setFloatForKey(String key, float value) {
    	SharedPreferences settings = ((Activity)sContext).getSharedPreferences(Cocos2dxHelper.PREFS_NAME, 0);
    	SharedPreferences.Editor editor = settings.edit();
    	editor.putFloat(key, value);
    	editor.commit();
    }
    
    public static void setDoubleForKey(String key, double value) {
    	// SharedPreferences doesn't support recording double value
    	SharedPreferences settings = ((Activity)sContext).getSharedPreferences(Cocos2dxHelper.PREFS_NAME, 0);
    	SharedPreferences.Editor editor = settings.edit();
    	editor.putFloat(key, (float)value);
    	editor.commit();
    }
    
    public static void setStringForKey(String key, String value) {
    	SharedPreferences settings = ((Activity)sContext).getSharedPreferences(Cocos2dxHelper.PREFS_NAME, 0);
    	SharedPreferences.Editor editor = settings.edit();
    	editor.putString(key, value);
    	editor.commit();
    }
	
    public static String getResouceString(String name){
       	int id = ResourcesManager.newInstance(Cocos2dxActivity.getContext()).getStringID(name);
       	String str = Cocos2dxActivity.getContext().getResources().getString(id);
       	return str;
      }
    
    public static String getXunleiUserInfo(){
    	XLLXUserInfo userInfo = XunLeiLiXianUtil.getUserInfoFromLocal(sContext);
    	return userInfo.toJsonString();
    }
    
    public static String getXunleiCookies(){
    	return XunLeiLiXianUtil.getCookie(sContext);
    }
    
    public static String getCurrentTime(){
    	return String.valueOf(System.currentTimeMillis());
    }
    
    public static String decode(String str){
    	String result;
    	try {
			result = URLDecoder.decode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = str;
		}
    	return result;
    }
    
    public static String getBaiduAccessToken(){
    	return PreferencesUtils.getBaiduAccessToken(sContext);
    }
    
    public static String getPincode(){
    	return PreferencesUtils.getPincode(sContext);
    }
    
    public static void setPincode(String pincode){
    	PreferencesUtils.setPincode(sContext, pincode);
    }
    
    public static void setChannel(String channel){
    	PreferencesUtils.setChannel(sContext, channel);
    }
    
    public static String getMac(){
    	return Utils.getMacAdd(sContext);
    }
    
    public static String getErweimaUrl(){
    	String str = PreferencesUtils.getErweima_url(sContext);
    	if(str!=null){
    		return str;
    	}else{
    		return "";
    	}
    }
    
    public static String getQQName(){
    	String str = PreferencesUtils.getQQName(sContext);
    	if(str!=null){
    		return str;
    	}else{
    		return "";
    	}
    }
    public static String getQQAvatar(){
    	String str = PreferencesUtils.getQQAvatar(sContext);
    	if(str!=null){
    		return str;
    	}else{
    		return "";
    	}
    }
    
//    public static String getRequestUserInfoUrl(){
//    	return Constant.BASE_URL + "/account/getuserinfo?pin="+getPincode()+"&md="+getMac()+"&app_key="+Constant.APPKEY;
//    }
    
    public static void startService(){
    	Cocos2dxHelper.sCocos2dxHelperListener.startService();
    }
    
    public static void startSetting(){
    	Cocos2dxHelper.sCocos2dxHelperListener.startSetting();
    }
    
    public static void updateQQ(){
    	Cocos2dxHelper.sCocos2dxHelperListener.updateQQ();
    }
    
    public static void 	generatePincode(){
    	//pincode\\\
    	Cocos2dxHelper.sCocos2dxHelperListener.generatePincode();
    }
    
    public static byte[] generateErweima(String date, int width){
    	try {
    		Bitmap b = EncodingHandler.createQRCode(date, width);
    		if(b!=null){
    			ByteArrayOutputStream baos = new ByteArrayOutputStream();
    			b.compress(Bitmap.CompressFormat.PNG, 100, baos);
    			return baos.toByteArray();
    		}else{
    			return null;
    		}
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
    public static String getOnlineWebUrl(){
    	return Cocos2dxHelper.sCocos2dxHelperListener.getDisplayWebUrl();
    }
    
    public static String getPlayList(){
    	try{
    		DBServices services = DBServices.getInstance(sContext);
        	List<MoviePlayHistoryInfo> infoLists =  services.queryMoviePlayHistoryList();
        	JSONObject obj = new JSONObject();
        	JSONArray arrary = new JSONArray();
        	for(MoviePlayHistoryInfo info:infoLists){
        		JSONObject infoObj = new JSONObject();
        		infoObj.put("_id", info.getId());
        		infoObj.put("type", info.getPlay_type());
        		infoObj.put("duration", info.getDuration());
        		infoObj.put("playback_time", info.getPlayback_time());
        		infoObj.put("episodes", info.getBtEpisodesString());
        		infoObj.put("name", info.getName());
        		infoObj.put("push_url", info.getPush_url());
        		infoObj.put("pic_url", info.getPic_url());
        		arrary.put(infoObj);
        	}
        	obj.put("list", arrary);
        	Log.d("Helper", obj.toString());
        	return obj.toString();
    	}catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
    		return "";
		}
    }
    
    public static void updateQQdisplay(){
    	try {
			Cocos2dxHelper.sCocos2dxHelperListener.runOnGLThread(new Runnable() {
				@Override
				public void run() {
					Cocos2dxHelper.nativeUpdateQQ();
				}
			});
		} catch (Exception Exception) {
			/* Nothing. */
		}
    }
    
    public static void deletePlayList(final String str){
    	MyApp.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.d("Helper", "delete-->" +str);
		    	try{
		    		DBServices services = DBServices.getInstance(sContext);
		    		JSONObject obj = new JSONObject(str);
		    		JSONArray array = obj.getJSONArray("list");
		    		for(int i=0; i<array.length(); i++){
		    			int id = array.getInt(i);
		    			services.deleteMoviePlayHistory(id);
		    		}
		    	}catch (Exception e) {
					// TODO: handle exception
		    		e.printStackTrace();
				}
			}
		});
    }
    
    
    
    public static void setGeneratePincodeResult(boolean isSuccessed){
    	try {
			final int successed = isSuccessed?1:0;
			Cocos2dxHelper.sCocos2dxHelperListener.runOnGLThread(new Runnable() {
				@Override
				public void run() {
					Cocos2dxHelper.nativeSetPincodeResult(successed);
				}
			});
		} catch (Exception Exception) {
			/* Nothing. */
		}
    }
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static interface Cocos2dxHelperListener {
		public void showDialog(final String pTitle, final String pMessage);
		public void startSetting();
		public void playVideo(String date);
		
		public void showEditTextDialog(final String pTitle, final String pMessage, final int pInputMode, final int pInputFlag, final int pReturnType, final int pMaxLength);

		public void showXunLeiLoginDialog();
		
		public void showBaiduDailog();
		
		public void startService();
		
		public void generatePincode();
		
		public void updateQQ();
		
		public void runOnGLThread(final Runnable pRunnable);
		
		public String getDisplayWebUrl();
	}
}
