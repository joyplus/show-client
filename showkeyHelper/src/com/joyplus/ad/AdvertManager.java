package com.joyplus.ad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.location.Location;

import com.joyplus.adkey.AdRequest;
import com.joyplus.adkey.Const;
import com.joyplus.adkey.RequestRichMediaAd;
import com.joyplus.adkey.Util;
import com.joyplus.adkey.download.ImpressionThread;
import com.joyplus.adkey.video.ResourceManager;
import com.joyplus.adkey.video.RichMediaAd;
import com.joyplus.adkey.video.VideoData;
import com.joyplus.adkey.widget.SerializeManager;
import com.joyplus.tvhelper.utils.Log;


public class AdvertManager {

	private boolean Debug = false;
	private String TAG = "AdvertManager";
	private boolean mIncludeLocation = false;
	private String mRequestURL = null;
	private SerializeManager serializeManager;

	private Context mContext;
	private String PUBLISHERID = null;
	private String mUniqueId1;
	private String mUniqueId2;
	private String mUserAgent;
	private boolean mEnabled = false;

	private AdRequest mAdRequest = null;
	private Thread mRequestThread;
	private RichMediaAd mRichMediaAd; // respone media

	private AdListener mListener;

	public void setListener(AdListener listener) {
		mListener = listener;
	}

	// this should be the same as it in BootAnimation.cpp
	public   static String DefaultFILEPATH = "JoyplusAd";
	public   String getPlayUri(){
		if(PATH == null)return null;
		File file = new File(PATH);
		if (file.exists()) {
			String[] temp = file.list();
			if (temp != null) {
				for (int i = 0; i < temp.length; i++) {
					if (temp[i].contains(DefaultFILEPATH)) {						
						return PATH+temp[i];
					}
				}
			}
		}
		return null;
	}
	private  String DefaultAD = "tv_patch_advert";
	private  String PATH = null;


	public AdvertManager(Context ctx, final String publisherId,
			final boolean cacheMode) {
		this.PUBLISHERID = publisherId;
		this.PATH = Const.DOWNLOAD_PATH+ctx.getPackageName()+"/"+PUBLISHERID+"/";
		DefaultAD = PATH+DefaultAD;
		mContext = ctx;
		Util.CACHE_MODE = cacheMode;
		InitResource();
	}
    
	// Interface for user to Manager the resource.
	public void UpdateAdvert() {
		// resave advert file first.
		ResaveCacheLoaded();
		requestAd();
		if (!WaitAd(20000)) {
			notifyAdClose();
			return;
		}
		// now we can download mp4 file and report count.		
		DownloadFile();
	}

	// Interface Report count
	public void ReportCount() {
		new Thread(new ReportCountRunnable()).start();
	}

	// Tnterface Download file
	private void DownloadFile() {
		new Thread(new DownloadFileRunnable()).start();
	}

	private class DownloadFileRunnable implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			RichMediaAd tempAd = (RichMediaAd) serializeManager
					.readSerializableData(DefaultAD);
			if (tempAd != null) {
				VideoData video = tempAd.getVideo();
				if (Util.CACHE_MODE && video != null) {
					String Download_path = video.getVideoUrl();
					URL url = null;
					try {
						url = new URL(Download_path);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (url != null) {
						Util.ExternalName = "."
								+ Util.getExtensionName(url.getPath());
					} else {
						Util.ExternalName = ".mp4";
					}
					Downloader downloader = new Downloader(Download_path,PATH);
					if (Download_path.startsWith("http:")
							|| Download_path.startsWith("https:")) {
						downloader.download();
						if (Debug)
							Log.i(TAG, "download starting");
					}
				} else {
					if (Debug)
						Log.i(TAG, "download fail video url fail");
					notifyAdNofound();
				}
			} else {
				if (Debug)
					Log.i(TAG, "download fail ad null");
				notifyAdNofound();
			}
		}
	}

	private class ReportCountRunnable implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (Debug)
				Log.d(TAG, "ReportCountRunnable run()");
			new ImpressionThread(mContext, mRichMediaAd.getmImpressionUrl(),
					PUBLISHERID, Util.AD_TYPE.FULL_SCREEN_VIDEO).start();
		}
	}

	/* Wait for Ad return form Server,Time out : faslse , Result : true. */
	private boolean WaitAd(int Time) {
		// waiting for Ad result.
		long now = System.currentTimeMillis();
		long timeoutTime = now + Time;// 20s
		while ((mRichMediaAd == null) && (now < timeoutTime)) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
			now = System.currentTimeMillis();
		}
		if (mRichMediaAd == null) {// no Ad can use
			notifyAdClose();
			return false;
		}
		return true;
	}

	/* request Ad from Server */
	private void requestAd() {
		requestAd(null);
	}

	private void requestAd(final InputStream xml) {
		if (Debug)
			Log.i(TAG, "requestAd mEnabled=" + mEnabled +" mRequestThread="+(mRequestThread!=null));
		if (!mEnabled)
			return;
		if (mRequestThread == null) {
			mRichMediaAd = null;
			mRequestThread = new Thread(new Request(xml));
			mRequestThread
					.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
						@Override
						public void uncaughtException(Thread thread,
								Throwable ex) {
							ex.printStackTrace();
							mRichMediaAd = new RichMediaAd();
							mRichMediaAd.setType(Const.AD_FAILED);
							mRequestThread = null;
						}
					});
			mRequestThread.start();
		}
	}

	/* get Ad from Server */
	private class Request implements Runnable {
		private InputStream XML = null;
		private RequestRichMediaAd requestAd;

		public Request(InputStream xml) {
			XML = xml;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (ResourceManager.isDownloading()) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
			}
			try {
				if (XML == null) {
					requestAd = new RequestRichMediaAd();
				} else {
					requestAd = new RequestRichMediaAd(XML);
				}
				AdRequest request = getRequest();
				File cacheDir = new File(PATH);
				if (!cacheDir.exists())cacheDir.mkdirs();
				mRichMediaAd = (RichMediaAd) serializeManager
						.readSerializableData(DefaultAD);
				RichMediaAd nextResponse = requestAd.sendRequest(request);
				serializeManager.writeSerializableData(DefaultAD, nextResponse);				
				if (mRichMediaAd == null) {
					mRichMediaAd = nextResponse;
				}
			} catch (Throwable t) {
				Log.e(TAG,"Request error ");
				t.printStackTrace();
				mRichMediaAd = (RichMediaAd) serializeManager
						.readSerializableData(DefaultAD);
			} finally {
				notifyResponse();
			}
		}
	}

	private void notifyResponse() {
		if (mRichMediaAd == null) {
			if (Debug)
				Log.d(TAG, "notifyResponse() mReponse ==null ");
			mRichMediaAd = new RichMediaAd();
			mRichMediaAd.setType(Const.AD_FAILED);
		}
		if (mRichMediaAd.getType() == Const.VIDEO_TO_INTERSTITIAL
				|| mRichMediaAd.getType() == Const.INTERSTITIAL_TO_VIDEO
				|| mRichMediaAd.getType() == Const.VIDEO
				|| mRichMediaAd.getType() == Const.INTERSTITIAL) {
			notifyAdLoadSuccessed();
		} else {
			notifyAdNofound();
		}
	}

	// get the AdRequest
	private AdRequest getRequest() {
		if (mAdRequest == null) {
			mAdRequest = new AdRequest();
			mAdRequest.setDeviceId(mUniqueId1);
			mAdRequest.setDeviceId2(mUniqueId2);
			mAdRequest.setPublisherId(PUBLISHERID);
			mAdRequest.setUserAgent(mUserAgent);
			mAdRequest.setUserAgent2(Util.buildUserAgent());
		}
		Location location = null;
		if (this.mIncludeLocation) {
			location = Util.getLocation(mContext);
		}
		if (location != null) {
			mAdRequest.setLatitude(location.getLatitude());
			mAdRequest.setLongitude(location.getLongitude());
		} else {
			mAdRequest.setLatitude(0.0);
			mAdRequest.setLongitude(0.0);
		}
		mAdRequest.setConnectionType(Util.getConnectionType(mContext));
		mAdRequest.setIpAddress(Util.getLocalIpAddress());
		mAdRequest.setTimestamp(System.currentTimeMillis());

		mAdRequest.setType(AdRequest.VAD);
		mAdRequest.setRequestURL(this.mRequestURL);
		return mAdRequest;
	}

	/*
	 * Resave advert file to default dir,make sure its same as BootAnimation.cpp
	 * support.
	 */
	private boolean ResaveCacheLoaded() {
		// TODO Auto-generated method stub
		File file = new File(PATH);
		if (file.exists()) {
			String[] temp = file.list();
			if (temp != null) {
				for (int i = 0; i < temp.length; i++) {
					if (temp[i].contains(Const.DOWNLOAD_PLAY_FILE)) {
						String dstFile = PATH+DefaultFILEPATH;
						String extra   = getExtensionName(temp[i]);
						if(extra!=null && !extra.equals(temp[i])){
							dstFile += ("."+getExtensionName(temp[i]));
						}
						copyFile(new File(PATH + temp[i]), new File(dstFile));
						return true;
					}
				}
			}
		}
		return false;
	}
	public static String getExtensionName(String filename) {   
        if ((filename != null) && (filename.length() > 0)) {   
            int dot = filename.lastIndexOf('.');   
            if ((dot >-1) && (dot < (filename.length() - 1))) {   
                return filename.substring(dot + 1);   
            }   
        }   
        return null;   
    }
	private boolean copyFile(File srcFile, File dstFile) {
		try {
			InputStream in = new FileInputStream(srcFile);
			if (dstFile.exists())
				dstFile.delete();
			OutputStream out = new FileOutputStream(dstFile);
			try {
				int cnt;
				byte[] buf = new byte[4096];
				while ((cnt = in.read(buf)) >= 0) {
					out.write(buf, 0, cnt);
				}
			} finally {
				out.close();
				in.close();
			}
			if (Debug)
				Log.d(TAG, "copyFile() success");
			return true;
		} catch (IOException e) {
			if (Debug)
				Log.d(TAG, "copyFile() fail");
			return false;
		}
	}

	private void InitResource() {
		// TODO Auto-generated method stub
		this.mIncludeLocation = true;
		this.mRequestURL = Const.REQUESTURL;
		Util.GetPackage(mContext);
		serializeManager = new SerializeManager();
		mUserAgent = Util.getDefaultUserAgentString(mContext);
		this.mUniqueId1 = Util.getTelephonyDeviceId(mContext);
		this.mUniqueId2 = Util.getDeviceId(mContext);
		if ((PUBLISHERID == null) || (PUBLISHERID.length() == 0)) {
			throw new IllegalArgumentException(
					"User Id cannot be null or empty");
		}
		if ((mUniqueId2 == null) || (mUniqueId2.length() == 0)) {
			throw new IllegalArgumentException(
					"System Device Id cannot be null or empty");
		}
		mEnabled = (Util.getMemoryClass(mContext) > 16);
		Util.initializeAnimations(mContext);
	}

	private synchronized void notifyAdLoadSuccessed() {
		if (mListener == null)
			return;
		new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mListener.adLoadSucceeded();
			}
		}.run();
	}

	private synchronized void notifyAdNofound() {
		if (mListener == null)
			return;
		new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mListener.noAdFound();
			}
		}.run();
	}

	private synchronized void notifyAdClose() {
		if (mListener == null)
			return;
		new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mListener.Closed();
			}
		}.run();
	}

}
