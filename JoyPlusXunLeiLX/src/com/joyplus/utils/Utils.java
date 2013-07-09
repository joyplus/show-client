package com.joyplus.utils;

import android.content.Context;
import android.graphics.Bitmap;
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
				
				float tempLong = fileSize/(1024 * 1024 * 1.0f);
				if(tempLong < 1000) {
					
					return String.format("%.2f", tempLong) + "M";
				} else {
					
					float tempFloat = tempLong/(1024 * 1.0f);
					
					return String.format("%.2f", tempFloat) + "G";
				}
			}
		}
		
		return "未知";
	}

}
