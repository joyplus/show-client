package com.joyplus.konka.update;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joyplus.konka.utils.Log;

import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class Utils {

	public static void simulateKey(final int KeyCode) {
		new Thread() {
			public void run() {
				try {
					Instrumentation inst = new Instrumentation();
					// inst.sendKeySync(new KeyEvent(KeyEvent.ACTION_UP,KeyCode));
					inst.sendKeyDownUpSync(KeyCode);
				} catch (Exception e) {
					Log.e("Exception when sendKeyDownUpSync", e.toString());
				}
			}
		}.start();
	}
	
	public static PackageInfo getAppPackageInfo(Context c, String apkPath){
		PackageManager pm = c.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
		return info;
	}

	/** s**/
	public static String formatDuration1(long duration) {
		int h = (int) duration / 3600;
		int m = (int) (duration - h * 3600) / 60;
		int s = (int) duration - (h * 3600 + m * 60);
		String durationValue;
		if (h == 0) {
			durationValue = String.format("%1$02d:%2$02d", m, s);
		} else {
			durationValue = String.format("%1$d:%2$02d:%3$02d", h, m, s);
		}
		return durationValue;
	}
	
	/** mms**/
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
	
	public static long formateTimeLong(String timeStr) {
		if(timeStr != null && !timeStr.equals("")) {
			Log.i("Yzg", "formateTimeLong--->" + timeStr);
//			int index = timeStr.indexOf("分钟");
			Pattern p = Pattern.compile("\\d+");
			Matcher m=p.matcher(timeStr);
			if(m.find()){
				String tempStr = m.group();
				try {
					return Integer.valueOf(tempStr) * 60 * 1000;
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return 0l;
	}
	
	
	public static String formateScore(String score) {
		if (score != null && !score.equals("") && !score.equals("0")
				&& !score.equals("-1")) {
			return score;
		}
		return "";
	}
	
    public static boolean isUTF_8(byte[] file){
        if (file.length>3 &&file[0] == -17 && 
        		file[1] == -69 && file[2] == -65) 
            return true;
        return false;
    }
    
	/**
	 * 获取权限
	 * 
	 * @param permission
	 *            权限
	 * @param path
	 *            路径
	 */
	public static void chmod(String permission, String path) {
		try {
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
