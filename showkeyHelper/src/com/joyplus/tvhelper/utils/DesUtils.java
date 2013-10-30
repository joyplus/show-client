package com.joyplus.tvhelper.utils;

import com.joyplus.des.JoyplusDes;

public class DesUtils {

    public DesUtils() {
    }
    
    public static String decode(String key, String data){
//    	return strDec(data, key, null, null);
    	return JoyplusDes.DES(data);
    	
    }
    
//    public static String encode(String key, String data){
//    	return strEnc(data, key, null, null);
//    }
    
}
