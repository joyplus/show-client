package com.joyplus.ad;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/*define by Jas@20130723 for AdBoot data W/R*/
public class AdDataManager {
     
	/*Interface for PublisherId*/
	public static final String  KEY_PUBLISHERID = "KEY_PUBLISHERID";
    public static final String  ADKEY_PUBLISHERID_CONFIG_XML = "adkey_publisherid_config_xml";
    
    public static String getPublisherID(Context context){
    	return getString(context,ADKEY_PUBLISHERID_CONFIG_XML,KEY_PUBLISHERID);
    }
    public static boolean setPublisherID(Context context,String ID){
    	return saveString(context,ADKEY_PUBLISHERID_CONFIG_XML,KEY_PUBLISHERID,ID);
    }
    

    
    /*Interface for base*/
    public static String getString(Context context,String XML,String KEY){
         if(XML == null || XML.equals(""))return null;
    	 if(KEY == null || KEY.equals(""))return null;
    	 SharedPreferences sp = context.getSharedPreferences(XML,Context.MODE_PRIVATE);
 		 return sp.getString(KEY, "");
    }
    
    public static boolean saveString(Context context,String XML,String KEY,String VALUE){
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
