package com.joyplus.mediaplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.joyplus.mediaplayer.VideoViewInterface.DecodeType;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.utils.Log;

public class JoyplusMediaPlayerDataManager{
	private Context mDataContext;
 	
    private static final String  JOYPLUS_CONFIG_XML = "joyplus_mediaplayer_config_xml";
    
    /*Interface of Decode type*/
    private static final String  KEY_DECODETYPE     = "KEY_DECODETYPE";
    private static final String  KEY_SWITCHINTERNAL = "KEY_SWITCHINTERNAL";
    private static final String  KEY_SUPPORTVITAMIO = "KEY_SUPPORTVITAMIO";
    private static final String  KEY_SCREENPARAMS   = "KEY_SCREENPARAMS";
    public JoyplusMediaPlayerDataManager(Context context){
    	this.mDataContext = context;
    }
    public  DecodeType getDecodeType(){
    	return getDecodeType(getString(mDataContext,JOYPLUS_CONFIG_XML,KEY_DECODETYPE));
    }
    public  boolean setDecodeType(DecodeType type){
    	return saveString(mDataContext,JOYPLUS_CONFIG_XML,KEY_DECODETYPE,getDecodeType(type));
    }
    public String getDecodeType(DecodeType type){
    	if(type == DecodeType.Decode_HW){
    		return mDataContext.getString(R.string.Decode_HW);
    	}else if(type == DecodeType.Decode_SW){
    		return mDataContext.getString(R.string.Decode_SW);
    	}else{
    		return mDataContext.getString(R.string.Default_Decode);
    	}
    }
    private DecodeType getDecodeType(String type){
    	if(!type.equals(mDataContext.getString(R.string.Decode_HW)) &&
    			!type.equals(mDataContext.getString(R.string.Decode_SW))){
    		type = mDataContext.getString(R.string.Default_Decode);
    	}
    	if(type.equals(mDataContext.getString(R.string.Decode_HW))){
    		return DecodeType.Decode_HW;
    	}else if(type.equals(mDataContext.getString(R.string.Decode_SW))){
    		return DecodeType.Decode_SW;
    	}else return null;//this can't be happen.
    }
    
    public boolean setSwitchEnable(boolean en){
    	return saveString(mDataContext,JOYPLUS_CONFIG_XML,KEY_SWITCHINTERNAL,(en?"true":"false"));
    }
    public boolean getSwitchEnable(){
    	if("false".equals(getString(mDataContext,JOYPLUS_CONFIG_XML,KEY_SWITCHINTERNAL))
    		|| "false".equals(mDataContext.getString(R.string.switch_internal))){
    	    return false;
    	}
        return true;
    }
    public boolean setVitamioEnable(boolean en){
    	return saveString(mDataContext,JOYPLUS_CONFIG_XML,KEY_SUPPORTVITAMIO,(en?"true":"false"));
    }
    public boolean getVitamioEnable(){
    	if("true".equals(getString(mDataContext,JOYPLUS_CONFIG_XML,KEY_SUPPORTVITAMIO))){
    	    return true;
    	}
        return false;
    }
    public  boolean setScreenParamsDefault(int type){
    	return saveString(mDataContext,JOYPLUS_CONFIG_XML,KEY_SCREENPARAMS,Integer.toString(type));
    } 
	public  int getScreenParamsDefault(){
		if(getString(mDataContext,JOYPLUS_CONFIG_XML,KEY_SCREENPARAMS)==null || "".equals(getString(mDataContext,JOYPLUS_CONFIG_XML,KEY_SCREENPARAMS))){
			return JoyplusMediaPlayerScreenManager.LINEARLAYOUT_PARAMS_DEFAULT;
		}
		return Integer.parseInt(getString(mDataContext,JOYPLUS_CONFIG_XML,KEY_SCREENPARAMS));
	}
    /*Interface for base*/
    public String getString(Context context,String XML,String KEY){
         if(XML == null || XML.equals(""))return null;
    	 if(KEY == null || KEY.equals(""))return null;
    	 SharedPreferences sp = context.getSharedPreferences(XML,Context.MODE_PRIVATE);
 		 return sp.getString(KEY, "");
    }
    
    public boolean saveString(Context context,String XML,String KEY,String VALUE){
    	Log.i("TAG", "saveString--->" + VALUE);
    	if(XML == null || XML.equals(""))return false;
   	    if(KEY == null || KEY.equals(""))return false;
   	    if(VALUE == null) return false;
   	    Log.i("TAG", "saveString2--->" + VALUE + " KEY--->" + KEY);
   	    SharedPreferences sp = context.getSharedPreferences(XML,Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(KEY, VALUE);
		editor.commit();
		if(VALUE.equals(getString(context,XML,KEY)))return true;
		return false;
    }
}
