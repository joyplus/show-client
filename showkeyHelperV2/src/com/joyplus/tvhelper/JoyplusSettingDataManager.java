package com.joyplus.tvhelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.utils.Log;

public class JoyplusSettingDataManager{
	private Context mDataContext;
 	
    private static final String  SETTING_CONFIG_XML = "joyplus_setting_config_xml";
    
    /*Interface of Decode type*/
    private static final String  KEY_SIZE_DECREASE  = "KEY_SIZE_DECREASE";


    public JoyplusSettingDataManager(Context context){
    	this.mDataContext = context;
    }
    public  SizeDecreaseType getSizeDecreaseType(){
    	return getSizeDecreaseType(getString(mDataContext,SETTING_CONFIG_XML,KEY_SIZE_DECREASE));
    }
    public  boolean setSizeDecreaseType(SizeDecreaseType type){
    	return saveString(mDataContext,SETTING_CONFIG_XML,KEY_SIZE_DECREASE,getSizeDecreaseType(type));
    }
    public String getSizeDecreaseType(SizeDecreaseType type){
    	if(type == SizeDecreaseType.Small_Size_Decreaes){
    		return mDataContext.getString(R.string.Small_Size_Decreaes);
    	}else if(type == SizeDecreaseType.Mid_Size_Decreaes){
    		return mDataContext.getString(R.string.Mid_Size_Decreaes);
    	}else if(type == SizeDecreaseType.Big_Size_Decreaes){
    		return mDataContext.getString(R.string.Big_Size_Decreaes);
    	}else {
    		return mDataContext.getString(R.string.Default_Size_decreas);
    	}
    }
    private SizeDecreaseType getSizeDecreaseType(String type){
    	if(!type.equals(mDataContext.getString(R.string.Big_Size_Decreaes)) &&
    			!type.equals(mDataContext.getString(R.string.Small_Size_Decreaes))&&
    			  !type.equals(mDataContext.getString(R.string.Mid_Size_Decreaes))){
    		type = mDataContext.getString(R.string.Default_Size_decreas);
    	}
    	if(type.equals(mDataContext.getString(R.string.Small_Size_Decreaes))){
    		return SizeDecreaseType.Small_Size_Decreaes;
    	}else if(type.equals(mDataContext.getString(R.string.Mid_Size_Decreaes))){
    		return SizeDecreaseType.Mid_Size_Decreaes;
    	}else if(type.equals(mDataContext.getString(R.string.Big_Size_Decreaes))){
    		return SizeDecreaseType.Big_Size_Decreaes;
        }else return null;//this can't be happen.
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
    
	public enum SizeDecreaseType{
		   Small_Size_Decreaes   (0),
		   Mid_Size_Decreaes   (1),
		   Big_Size_Decreaes (2);
		   private int TYPE;
		   SizeDecreaseType(int type){
			   TYPE = type;
		   }
		   public int toInt(){
			   return TYPE;
		   }
	}
}
