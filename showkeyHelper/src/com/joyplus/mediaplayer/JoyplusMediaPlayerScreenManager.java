package com.joyplus.mediaplayer;


import com.joyplus.JoyplusMediaPlayerActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.LinearLayout;

public class JoyplusMediaPlayerScreenManager {

	private static JoyplusMediaPlayerScreenManager mManager;
	public  static JoyplusMediaPlayerScreenManager getInstance(){
		return mManager;
	}
	
	private  boolean  Debug = true;
	private  String   TAG   = "JoyplusMediaPlayerScreenManager";
	private  Activity mActivity;
	
	public final static int LINEARLAYOUT_PARAMS_16x9     = 0;
    public final static int LINEARLAYOUT_PARAMS_4x3      = 1;
    public final static int LINEARLAYOUT_PARAMS_FULL     = 2;
    public final static int LINEARLAYOUT_PARAMS_ORIGINAL = 3;
    public final static int LINEARLAYOUT_PARAMS_DEFAULT  = 3;
    public static boolean IsAviableType(int type){
    	return (LINEARLAYOUT_PARAMS_16x9<=type && type<=LINEARLAYOUT_PARAMS_ORIGINAL);
    }
	private ScreenDataManager   mData;
	public JoyplusMediaPlayerScreenManager(Activity activity) throws Exception{
		if(! (activity instanceof Activity))throw new Exception("use it in Activity");
		mManager = this;
		mActivity = activity;
		InitResource();
	}
	//Interface for screen control.set screen and save value to datbase
    public boolean setScreenParams(int type){
    	if(!IsAviableType(type))return false;
    	if(mActivity != null && mActivity instanceof JoyplusMediaPlayerActivity){
    		if(((JoyplusMediaPlayerActivity)mActivity).getPlayer() != null){
    			return ((JoyplusMediaPlayerActivity)mActivity).getPlayer().setScreenLayoutParams(type);
    		}
    	}
    	return false;
    }
    public int getScreenParams(){
    	if(mActivity != null && mActivity instanceof JoyplusMediaPlayerActivity){
    		if(((JoyplusMediaPlayerActivity)mActivity).getPlayer() != null){
    			return ((JoyplusMediaPlayerActivity)mActivity).getPlayer().getScreenLayoutParams();
    		}
    	}
    	return LINEARLAYOUT_PARAMS_DEFAULT;
    }
    public boolean setScreenParamsDefault(int type){
    	if(!IsAviableType(type))return false;
    	return mData.setScreenParamsType(type);
    }
    public int getScreenParamsDefault(){
    	return mData.getScreenParamsType();
    }
	private void InitResource() {
		// TODO Auto-generated method stub
		mData   = new ScreenDataManager(mActivity);
	}
	
	/*Interface of screen params*/	
	private class ScreenDataManager{
		private static final String  JOYPLUS_CONFIG_XML = "joyplus_mediaplayer_config_xml";
		private static final String  KEY_SCREENPARAMS   = "KEY_SCREENPARAMS";
		private Context mDataContext;
		public ScreenDataManager(Context context){
			this.mDataContext  = context;
		}
		public  boolean setScreenParamsType(int type){
        	return saveString(mDataContext,JOYPLUS_CONFIG_XML,KEY_SCREENPARAMS,Integer.toString(type));
        } 
		public  int getScreenParamsType(){
			if(getString(mDataContext,JOYPLUS_CONFIG_XML,KEY_SCREENPARAMS)==null || "".equals(getString(mDataContext,JOYPLUS_CONFIG_XML,KEY_SCREENPARAMS))){
				return JoyplusMediaPlayerScreenManager.LINEARLAYOUT_PARAMS_DEFAULT;
			}
			return Integer.parseInt(getString(mDataContext,JOYPLUS_CONFIG_XML,KEY_SCREENPARAMS));
		}
		/*Interface for base*/
		public  String getString(Context context,String XML,String KEY){
	        if(XML == null || XML.equals(""))return null;
	   	 if(KEY == null || KEY.equals(""))return null;
	   	 SharedPreferences sp = context.getSharedPreferences(XML,Context.MODE_PRIVATE);
			 return sp.getString(KEY, "");
	   }
	   
	   public  boolean saveString(Context context,String XML,String KEY,String VALUE){
	   	if(XML == null || XML.equals(""))return false;
	  	    if(KEY == null || KEY.equals(""))return false;
	  	    if(VALUE == null) return false;
	  	    SharedPreferences sp = context.getSharedPreferences(XML,Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString(KEY, VALUE);
			editor.commit();
			if(VALUE.equals(getString(context,XML,KEY)))return true;
			return false;
	   } 
	}
}
