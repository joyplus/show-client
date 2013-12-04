package com.joyplus.tvhelper.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.cocos2dx.lib.Cocos2dxHandler;
import org.cocos2dx.lib.Cocos2dxHelper;

import android.content.Context;

import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.Utils;


public class DownLoadUpdateApkThread implements Runnable {

	public static final String NAME_APK_DOWNLOADED = "updateapk.apk";
	public static final String NAME_APK_DOWNLOADING = "updateapk_temp";
	
	private Context mContext;
	private String donwLoadURL; 
	
	public DownLoadUpdateApkThread(Context c, String url) {
		
		this.mContext = c;
		this.donwLoadURL = url;
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		URL m;
		InputStream i = null;
		try {
			m = new URL(donwLoadURL);
			HttpURLConnection connection = (HttpURLConnection) m.openConnection();
			connection.setDoInput(true);
			connection.connect();
			i =  connection.getInputStream();
//			String filename = getFilenameFromUrl(url);
			File cacheDir = mContext.getCacheDir();
//			if(!dir.exists()){
//				dir.mkdirs();
//			}
			File f = new File(Cocos2dxHelper.getCocos2dxWritablePath(), NAME_APK_DOWNLOADING);
			Log.d("TAG", f.getAbsolutePath());
			if(!f.exists()){
				f.createNewFile();
			}
			FileOutputStream out = new FileOutputStream(f);
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    byte[] b = new byte[1024];
		    int len = 0;
		    while ((len = i.read(b, 0, 1024)) != -1){
			   out.write(b, 0, len);
			   out.flush();
		    }
		    File f_downloaded = new File(Cocos2dxHelper.getCocos2dxWritablePath(), NAME_APK_DOWNLOADED);
		    f.renameTo(f_downloaded);
		    Utils.chmod("777", f_downloaded.getAbsolutePath());
		    Log.d("TAG", "---------------download");
		    
		    i.close();
		    out.flush();
		    out.close();
//		    RootTools t;
//			t.isRootAvailable()
		}catch (FileNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
			
		}catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
