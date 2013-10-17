package com.joyplus.ad;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import com.joyplus.adkey.Const;
import com.joyplus.adkey.Util;
import com.joyplus.adkey.widget.Log;

import android.content.Context;

public class Downloader {
	private String urlstr;// 娑撳娴囬惃鍕勾閸э拷
	private String localfile;// 娣囨繂鐡ㄧ捄顖氱窞
	private int fileSize = 0;//閺傚洣娆㈡径褍鐨�
	private int compeleteSize = 0;//閺傚洣娆㈡稉瀣祰鐎瑰本鍨氭径褍鐨�
	private Context context;
	private static final int INIT = 1;// 鐎规矮绠熸稉澶岊瀸娑撳娴囬惃鍕Ц閹緤绱伴崚婵嗩瀶閸栨牜濮搁幀渚婄礉濮濓絽婀稉瀣祰閻樿埖锟介敍灞炬畯閸嬫粎濮搁幀锟�	private static final int DOWNLOADING = 2;//濮濓絽婀稉瀣祰娑擄拷
	private static final int DOWNLOADING = 2;
	private static final int PAUSE = 3;//閺嗗倸浠�
	private static final int STOP = 4;//閸嬫粍顒�
	private static final int FAILED = 5;//婢惰精瑙�
	private int state = INIT;
    
	private String PATH = null;
	
	public Downloader(String urlstr, String path) {
		this.urlstr = urlstr;
		PATH = path;
		File cacheDir = new File(PATH);
		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	public boolean isdownloading() {
		return state == DOWNLOADING;
	}

	private void init() {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(urlstr);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			fileSize = connection.getContentLength();
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(connection!=null)
				connection.disconnect();
		}
	}

	public void download() {
		new Thread(new downloadRunnable()).start();
	}
    private class downloadRunnable implements Runnable{
    	public void run() {			
			localfile = Const.DOWNLOAD_PATH+Util.VideoFileDir+Const.DOWNLOADING_FILE;
			if(PATH != null)localfile = PATH+Const.DOWNLOADING_FILE;
			Log.d("JoyplusMediaPlayerAd","JoyplusMediaPlayerAd Download -->"+localfile);
			HttpURLConnection connection = null;
			RandomAccessFile randomAccessFile = null;
			InputStream inputstream = null;
			try {
				URL url = new URL(urlstr);
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(60*1000);
				connection.setRequestMethod("GET");
				fileSize = connection.getContentLength();
				randomAccessFile = new RandomAccessFile(localfile, "rwd");
				inputstream = connection.getInputStream();
				byte[] buffer = new byte[1024 * 50];
				int length = -1;
				while ((length = inputstream.read(buffer)) != -1) {
					randomAccessFile.write(buffer, 0, length);
					compeleteSize += length;
					if (compeleteSize == fileSize) {
						state = STOP;
						File file = new File(PATH+Const.DOWNLOAD_READY_FILE);
						if(file.exists())file.delete();
						randomAccessFile.close();
						File filetemp = new File(PATH+Const.DOWNLOADING_FILE);
						if(filetemp.exists())
						{
							File filedone = new File(PATH+Const.DOWNLOAD_PLAY_FILE+Util.ExternalName);
							if(filedone.exists())filedone.delete();
							filetemp.renameTo(filedone);
						}
					}
					if (state == PAUSE||state == STOP) {
						return;
					}
				}
			} catch (Exception e) {
				state = STOP;
				e.printStackTrace();
				if(connection!=null)
					connection.disconnect();
			} finally {
				state = STOP;
				if(connection!=null)
					connection.disconnect();
			}
		}
    };
	

	public void delete(String urlstr) {
		
	}

	public void pause() {
		state = PAUSE;
	}

	public void reset() {
		state = INIT;
	}

	public int getstate() {
		return state;
	}
}