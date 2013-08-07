package com.joyplus.tvhelper.https;

import org.apache.http.Header;
import org.apache.http.NameValuePair;

import android.content.Context;

import com.joyplus.tvhelper.helper.HttpClientHelper;
import com.joyplus.tvhelper.helper.NetWorkHelper;
import com.joyplus.tvhelper.utils.Log;

public class HttpUtils {
	
	private static final String TAG = "HttpUtils";
	
	public static byte[] getBinary(String paramString,
			Header[] paramArrayOfHeader,
			NameValuePair[] paramArrayOfNameValuePair) {
		
		byte[] arrayOfByte = null;
		HttpResult localHttpResult = HttpClientHelper.get(paramString,
				paramArrayOfHeader, paramArrayOfNameValuePair, null, 0);
		
		if ((localHttpResult != null)
				&& (localHttpResult.getStatuCode() == 200)) {
			
			arrayOfByte = localHttpResult.getResponse();
			Log.d("HttpUtils", "binary= " + arrayOfByte);
		}

		return arrayOfByte;
	}
	
	public static String getContent(String paramString,
			Header[] paramArrayOfHeader,
			NameValuePair[] paramArrayOfNameValuePair) {
		
		String str = null;
		HttpResult localHttpResult = HttpClientHelper.get(paramString,
				paramArrayOfHeader, paramArrayOfNameValuePair);
		
		Log.d(TAG, "getContent--->" + localHttpResult);
		
		if (localHttpResult != null
				&& localHttpResult.getStatuCode() == 200) {
			
			str = localHttpResult.getHtml();
			Log.d("HttpUtils", "content= " + str);
		}

		return str;
	}
	
	public static boolean isMobileDataEnable(Context context) {
		
		try {
			boolean isMoblieDataEnable = NetWorkHelper.isMobileDataEnable(context);
			
			if(isMoblieDataEnable) {
				
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("httpUtils.isMobileDataEnable()",e.getMessage());
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static boolean isNetworkAvailable(Context context) {
		return NetWorkHelper.isNetworkAvailable(context);
	}

	public static boolean isNetworkRoaming(Context context) {
		return NetWorkHelper.isNetworkRoaming(context);
	}

	public static boolean isWifiDataEnable(Context context) {
		
		try {
			boolean isWifiDataEnable = NetWorkHelper.isWifiDataEnable(context);
			
			if(isWifiDataEnable) {
				
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("httpUtils.isWifiDataEnable()",e.getMessage());
			e.printStackTrace();
		}
		
		return false;
	}

}
