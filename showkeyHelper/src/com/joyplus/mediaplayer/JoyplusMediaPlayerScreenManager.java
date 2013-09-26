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
	
	public JoyplusMediaPlayerScreenManager(Activity activity) throws Exception{
		if(! (activity instanceof Activity))throw new Exception("use it in Activity");
		mManager = this;
		mActivity = activity;
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
    	if(JoyplusMediaPlayerManager.getInstance()!=null){
    		JoyplusMediaPlayerManager.getInstance().getDataManager().setScreenParamsDefault(type);
    	}
    	return false;
    }
    public int getScreenParamsDefault(){
    	if(JoyplusMediaPlayerManager.getInstance()!=null){
    		return JoyplusMediaPlayerManager.getInstance().getDataManager().getScreenParamsDefault();
    	}
    	return LINEARLAYOUT_PARAMS_DEFAULT;
    }
	
}
