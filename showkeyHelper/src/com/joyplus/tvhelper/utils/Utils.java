package com.joyplus.tvhelper.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

public class Utils {
	
	public static void showToast(Context context,String str) {
		
		Toast.makeText(context, str, Toast.LENGTH_LONG).show();
	}
	
	public static String formatDuration(long duration) {
		duration = duration / 1000;
		int h = (int) duration / 3600;
		int m = (int) (duration - h * 3600) / 60;
		int s = (int) duration - (h * 3600 + m * 60);
		String durationValue;
		// if (h == 0) {
		// durationValue = String.format("%1$02d:%2$02d", m, s);
		// } else {
		durationValue = String.format("%1$02d:%2$02d:%3$02d", h, m, s);
		// }
		return durationValue;
	}
	
	public static void recycleBitmap(Bitmap bitmap) {
		
		if(bitmap != null) {
			
			if(!bitmap.isRecycled()) {
				
				bitmap.recycle();
			}
			
			bitmap = null;
		}
	}
	
	//把字节换算成M
	public static String byte2Mbyte(String byteStr) {
		
		if(byteStr != null && !byteStr.equals("")
				&&!byteStr.equals("null")) {
			
			long fileSize = -1l;
			try {
				fileSize = Long.valueOf(byteStr);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(fileSize > 0) {
				
				float tempLong = fileSize/(1024 * 1.0f);
				if(tempLong < 1024) {
					
					return String.format("%.2f", tempLong) + "KB";
				} else {
					
					tempLong = tempLong/(1024 * 1.0f);
					
					if(tempLong < 1024) {
						
						return String.format("%.2f", tempLong) + "M";
					} else {
						
						float tempFloat = tempLong/(1024 * 1.0f);
						
						return String.format("%.2f", tempFloat) + "G";
					}
				}
			}
		}
		
		return "未知";
	}
	
	public static String setFileSize(long paramLong) {
		DecimalFormat localDecimalFormat = new DecimalFormat("###.##");
		float f = (float) paramLong / 1048576.0F;
		
		if (f < 1.0D)
			return localDecimalFormat.format(new Float(
					(float) paramLong / 1024.0F).doubleValue()) + "KB";
		
		if ((f >= 1.0D) && (f < 1024.0D))
			return localDecimalFormat.format(new Float(f).doubleValue()) + "M";
		
		return localDecimalFormat.format(new Float(f / 1024.0F).doubleValue())
				+ "G";
	}
	
	public  static boolean isSame4Str(String str1, String str2){
		if(str1==null||str2==null){
			return false;
		}
		if(str1.equalsIgnoreCase(str2)){
			return true;
		}else{
			if(str1.trim().equalsIgnoreCase(str2.trim())){
				return true;
			}else{
				if(str1.length()>=str2.length()){
					if(str1.startsWith(str2)){
						return true;
					}else{
						return false;
					}
				}else{
					if(str2.startsWith(str1)){
						return true;
					}else{
						return false;
					}
				}
			}
		}
	}
	
	
public static InetAddress getLocalIpAddress(){
		
		try{
			 for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				 NetworkInterface intf = en.nextElement();  
	                for (Enumeration<InetAddress> enumIpAddr = intf  
	                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {  
	                    InetAddress inetAddress = enumIpAddr.nextElement();  
	                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {  
	                    	return inetAddress;  
	                    }  
	                }  
			 }
		}catch (SocketException e) {
			// TODO: handle exception
			Log.e("TAG","WifiPreference IpAddress---error-" + e.toString());
		}
		
		return null; 
	}
  
  public static String getMacAdd(){
	  String  str = "";
	  try {
		  byte[] b = null;
		  b = NetworkInterface.getByInetAddress(getLocalIpAddress()).getHardwareAddress();
		  for(int i =0; i<b.length; i++){
			  if(i!=0){
				  str += ":";
			  }
			  str += Integer.toHexString(0xFF & b[i]);
		  }
//		  str = new String(b);
	} catch (SocketException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
	  return str;
  }

}
