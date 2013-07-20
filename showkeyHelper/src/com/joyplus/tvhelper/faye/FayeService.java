package com.joyplus.tvhelper.faye;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.joyplus.network.filedownload.manager.DownLoadListner;
import com.joyplus.network.filedownload.manager.DownloadManager;
import com.joyplus.network.filedownload.model.DownloadTask;
import com.joyplus.tvhelper.DialogActivity;
import com.joyplus.tvhelper.db.DBServices;
import com.joyplus.tvhelper.entity.ApkInfo;
import com.joyplus.tvhelper.entity.PushedApkDownLoadInfo;
import com.joyplus.tvhelper.faye.FayeClient.FayeListener;
import com.joyplus.tvhelper.utils.Global;
import com.joyplus.tvhelper.utils.HttpTools;
import com.joyplus.tvhelper.utils.PackageUtils;
import com.joyplus.tvhelper.utils.PreferencesUtils;
import com.joyplus.tvhelper.utils.Utils;
import com.lenovo.lsf.installer.PackageInstaller;


public class FayeService extends Service implements FayeListener ,Observer, DownLoadListner{

	private static final String TAG = "FayeService";
	
	private static File APK_PATH = null;
	
	private boolean isNeedReconnect = false;
	
	public static final int MESSAGE_DOWNLOAD_GET_FILESIE_SUCCESS = 0;
	public static final int MESSAGE_DOWNLOAD_CREAT_FILE_SUCCESS = 1;
	public static final int MESSAGE_DOWNLOAD_PROGRESS_CHANGED = 2;
	public static final int MESSAGE_DOWNLOAD_COMPLETE = 3;
	public static final int MESSAGE_DOWNLOAD_FAILE = 4;
	public static final int MESSAGE_SHOW_DIALOG = 101;
	public static final int MESSAGE_NEW_DOWNLOAD_ADD = 102;
	public static final int MESSAGE_APK_INSTALLED_PROGRESS = 102;
	
	public static final int MESSAGE_LISTEN_APP_LOOPER = 201;
	
	private String channel;
	private FayeClient myClient;
	private DBServices services;
	private DownloadManager downloadManager;
	private PackageInstaller packageInstaller;
	private PushedApkDownLoadInfo currentUserApkInfo; 
	private PushedApkDownLoadInfo currentNotUserApkInfo; 
	public static List<PushedApkDownLoadInfo> userPushApkInfos;
	public static List<PushedApkDownLoadInfo> notuserPushedApkInfos;
//	private String currentPackage = null;
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			Log.d(TAG, "recive broadcast --->" + action);
			if(Global.ACTION_CONFIRM_ACCEPT.equals(action)){
				JSONObject json = new JSONObject();
				try {
					json.put("msg_type", 3);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				myClient.sendMessage(json);
			}else if(Global.ACTION_CONFIRM_REFUSE.equals(action)){
				JSONObject json = new JSONObject();
				try {
					json.put("msg_type", 4);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				myClient.sendMessage(json);
			}else if(Global.ACTION_DOWNLOAD_PAUSE.equals(action)){
				currentUserApkInfo = null;
				startNextUserApkDownLoad();
			}else if(Global.ACTION_DOWNLOAD_CONTINUE.equals(action)){
				currentUserApkInfo = null;
				startNextUserApkDownLoad();
			}else if(Global.ACTION_DELETE_DOWNLOAD.equals(action)){
				
			}else if(Global.ACTION_PINCODE_REFRESH.equals(action)){
				myClient.disconnectFromServer();
				isNeedReconnect = false;
				stopSelf();
			}
		}
	};
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_SHOW_DIALOG:
				Intent intent = new Intent(FayeService.this,DialogActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				break;
			case MESSAGE_NEW_DOWNLOAD_ADD:
				Log.d(TAG, "MESSAGE_NEW_DOWNLOAD_ADD -- >");
				Intent dowanlaodAddIntent = new Intent(Global.ACTION_APK_RECIVED);
				sendBroadcast(dowanlaodAddIntent);
				break;
			case MESSAGE_LISTEN_APP_LOOPER:
				Log.d(TAG, "MESSAGE_LISTEN_APP_LOOPER-----");
//					try {
//						int tagCode = EventLog.getTagCode("am_proc_start");
//						Collection<Event> output = new ArrayList<EventLog.Event>();
//						EventLog.readEvents(new int[] { tagCode }, output);
//						for (Event event : output) {
//							// PID, UID, Process Name, Type, Component
//							Object[] objects = (Object[]) event.getData();
//							ComponentName componentName = ComponentName
//									.unflattenFromString(objects[4].toString());
//							if(componentName!=null){
//								String packageName = componentName.getPackageName();
//								Log.d(TAG, "packageName=" + packageName);
//							}
//						}
//						ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//						ComponentName cn = manager.getRunningTasks(1).get(0).topActivity;
//						String packageName = cn.getPackageName();
//						if(packageName!=null){
//							Log.d(TAG, "packageName=" + packageName);
//							if(currentPackage!=null&&!currentPackage.equals(packageName)&&packageName.equals("com.joyplus.tv")){
//								Intent intent_show = new Intent();
//								intent_show.setClassName("com.joyplus.showkey.screensaver","com.joyplus.showkey.screensaver.ScreenShow");
////								intent_show.setClassName("com.qihoo360.mobilesafe_tv","com.qihoo360.mobilesafe.applock.ui.TvLockWorkActivity");
//								intent_show.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//								startActivity(intent_show);
//							}
//							if(!packageName.equals("com.joyplus.showkey.screensaver")){
//								currentPackage = packageName;
//							}
//						}

//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					handler.sendEmptyMessageDelayed(MESSAGE_LISTEN_APP_LOOPER, 500);
				break;
			}
		};
	};

	
//	public static String getMacAdd(Context c) {
//		String macAddress = null;
//		WifiManager wifiMgr = (WifiManager) c
//				.getSystemService(Context.WIFI_SERVICE);
//		WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
//		if (info != null) {
//			macAddress = info.getMacAddress();
//		}
//		return macAddress;
//	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		APK_PATH = new File(Environment.getExternalStorageDirectory(), "showkey/apk");
		services = DBServices.getInstance(this);
		channel = "/" + PreferencesUtils.getChannel(this);
		downloadManager = DownloadManager.getInstance(this);
		downloadManager.setDownLoadListner(this);
		packageInstaller = new PackageInstaller(this);
		packageInstaller.addObserver(this);
		userPushApkInfos = services.queryUserApkDownLoadInfo();
		notuserPushedApkInfos = services.queryNotUserApkDownLoadInfo();
		Log.d(TAG, channel);
		URI url = URI.create(Global.serverUrl+"/uploadApk");
		Log.d(TAG, "Server----->" + Global.serverUrl+"/uploadApk");
		Log.d(TAG, "channel----->" + channel);
		myClient = new FayeClient(handler, url, channel);
		myClient.setFayeListener(this);
		myClient.connectToServer(null); 
		IntentFilter filter = new IntentFilter(Global.ACTION_CONFIRM_ACCEPT);
		filter.addAction(Global.ACTION_CONFIRM_REFUSE);
		filter.addAction(Global.ACTION_DOWNLOAD_PAUSE);
		filter.addAction(Global.ACTION_DOWNLOAD_CONTINUE);
		filter.addAction(Global.ACTION_DELETE_DOWNLOAD);
		filter.addAction(Global.ACTION_PINCODE_REFRESH);
		registerReceiver(receiver, filter);
//		handler.sendEmptyMessageDelayed(MESSAGE_LISTEN_APP_LOOPER, 500);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		isNeedReconnect = true;
		new Thread(new Runnable() {	
			@Override
			public void run() {
				// TODO Auto-generated method stub
//				infolist = services.GetPushedApklist(infolist);
				Log.d(TAG, "infolist size" + userPushApkInfos.size());
				String url = Global.serverUrl + "/pushMsgHistories?app_key=" + Global.app_key 
						+ "&mac_address=" + Utils.getMacAdd() 
						+ "&page_num=" + 1
						+ "&page_size=" + 50;
				Log.d(TAG, url);
				String str = HttpTools.get(FayeService.this, url);
				Log.d(TAG, "pushMsgHistories response-->" + str);
				try {
					JSONArray array = new JSONArray(str);
					Log.d(TAG, "miss length ---------------------------->" + array.length());
					for(int i=0; i<array.length(); i++){
						JSONObject item = array.getJSONObject(i);
						PushedApkDownLoadInfo info = new PushedApkDownLoadInfo();
						String file_url = item.getString("file_url");
						info.setPush_id(item.getInt("id"));
						info.setName(item.getString("app_name"));
						info.setIsUser(PushedApkDownLoadInfo.IS_USER);
						String fileName = getApkFileNameforUrl(file_url);
						DownloadTask tast = new DownloadTask(file_url, APK_PATH.getAbsolutePath(), fileName, 3);
						info.setFile_path(APK_PATH.getAbsolutePath() + File.separator + fileName);
						info.setTast(tast);
						info.set_id((int) services.insertApkInfo(info));
						userPushApkInfos.add(info);
						updateHistory(info.getPush_id());
						handler.sendEmptyMessage(MESSAGE_NEW_DOWNLOAD_ADD);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(currentUserApkInfo==null){
					startNextUserApkDownLoad();
				}
			}
		}).start();
		return super.onStartCommand(intent, START_STICKY, startId); 
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void connectedToServer() {
		// TODO Auto-generated method stub
		Log.d(TAG, "server connected----->");
	}

	@Override
	public void disconnectedFromServer() {
		// TODO Auto-generated method stub
		Log.w(TAG, "server disconnected!----->");
		if(isNeedReconnect){
			myClient.connectToServer(null);
		}
	}

	@Override
	public void subscribedToChannel(String subscription) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Channel subscribed success!----->" + subscription);
	}

	@Override
	public void subscriptionFailedWithError(String error) {
		// TODO Auto-generated method stub
		Log.w(TAG, "Channel subscribed FailedWithError!----->" + error);
	}

	@Override
	public void messageReceived(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			if(json!=null){
				Log.d(TAG, "Receive message:" + json.toString());
				Message msg = handler.obtainMessage(MESSAGE_SHOW_DIALOG);
				msg.obj = json.toString();
				int type =  json.getInt("msg_type");
				switch (type) {
				case 1:
					JSONObject data = json.getJSONObject("body");
					PushedApkDownLoadInfo info = new PushedApkDownLoadInfo();
					final int id = data.getInt("id");
					info.setName(data.getString("app_name"));
					String url = data.getString("file_url");
					String file_name = getApkFileNameforUrl(url);
					info.setPush_id(id);
					DownloadTask tast = new DownloadTask(url, APK_PATH.getAbsolutePath(), file_name, 3);
					info.setFile_path(APK_PATH.getAbsolutePath()+ File.separator + file_name);
					info.setTast(tast);
					info.setIsUser(PushedApkDownLoadInfo.IS_USER);
					info.set_id((int) services.insertApkInfo(info));
					userPushApkInfos.add(info);
					updateHistory(id);
					handler.sendEmptyMessage(MESSAGE_NEW_DOWNLOAD_ADD);
					if(currentUserApkInfo==null){
						currentUserApkInfo = info;
						currentUserApkInfo.setDownload_state(PushedApkDownLoadInfo.STATUE_DOWNLOADING);
						downloadManager.startTast(tast);
						services.updateApkInfo(currentUserApkInfo);
					}
					break;
				case 2:
					handler.sendMessage(msg);
					break;

				default:
					break;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private String getApkFileNameforUrl(String url){
		String [] strs = url.split("/");
		String filename = strs[strs.length - 1];
//		if(filename.contains(".")){
//			filename = filename.substring(0, filename.lastIndexOf("."));
//		}
		return System.currentTimeMillis() + filename;
	}
	
	private void startNextUserApkDownLoad(){
		Log.d(TAG, "startNextUserApkDownLoad--->");
		for(PushedApkDownLoadInfo info :userPushApkInfos){
			if(info.getDownload_state()==PushedApkDownLoadInfo.STATUE_WAITING_DOWNLOAD){
				if(info.getTast().getState()==-1){
					downloadManager.startTast(info.getTast());
				}else{
					downloadManager.resumeTask(info.getTast());
				}
				info.setDownload_state(PushedApkDownLoadInfo.STATUE_DOWNLOADING);
				currentUserApkInfo = info;
				services.updateApkInfo(currentUserApkInfo);
				return ;
			}
		}
	}
	
	private void updateHistory(final int id){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String url = Global.serverUrl + "/updateHistory?app_key=" + Global.app_key 
						+ "&mac_address=" + Utils.getMacAdd()
						+ "&id=" + id;
				Log.d(TAG, url);
				String str = HttpTools.get(FayeService.this, url);
				Log.d(TAG, "updateHistory response-->" + str);
			}
		}).start();
	}

	@Override
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		if (data != null && data instanceof Bundle){
			Bundle b = (Bundle) data;
			Log.i(TAG, "packageName=" + b.getString(PackageInstaller.KEY_PACKAGE_NAME));
			Log.i(TAG, "resultCode=" + b.getInt(PackageInstaller.KEY_RESULT_CODE));
			Log.i(TAG, "resultDesc=" + b.getString(PackageInstaller.KEY_RESULT_DESC));
			
			if(currentUserApkInfo!=null && currentUserApkInfo.getPackageName().equalsIgnoreCase(b.getString(PackageInstaller.KEY_PACKAGE_NAME))){
				Log.d(TAG, currentUserApkInfo.getName() + " install " +b.getString(PackageInstaller.KEY_RESULT_DESC));
				if("INSTALL_SUCCEEDED".equals(b.getString(PackageInstaller.KEY_RESULT_DESC))){
					Intent intent = new Intent(Global.ACTION_DOWNL_INSTALL_FAILE);
					intent.putExtra("_id", currentUserApkInfo.get_id());
					services.deleteApkInfo(currentUserApkInfo);
					userPushApkInfos.remove(currentUserApkInfo);
					sendBroadcast(intent);
					if(PreferencesUtils.isautodelete(FayeService.this)){
						if(currentUserApkInfo!=null&&currentUserApkInfo.getFile_path()!=null){
							File f = new File(currentUserApkInfo.getFile_path());
							if(f!=null&&f.exists()){
								f.delete();
							}
						}
					}
				}else{
					Intent intent = new Intent(Global.ACTION_DOWNL_INSTALL_FAILE);
					intent.putExtra("_id", currentUserApkInfo.get_id());
					currentUserApkInfo.setDownload_state(PushedApkDownLoadInfo.STATUE_INSTALL_FAILE);
					services.updateApkInfo(currentUserApkInfo);
					sendBroadcast(intent);
				}
				currentUserApkInfo = null;
				// down load next
				startNextUserApkDownLoad();
			}
			
			if(currentNotUserApkInfo!=null && currentNotUserApkInfo.getPackageName().equalsIgnoreCase(b.getString(PackageInstaller.KEY_PACKAGE_NAME))){
				Log.d(TAG, currentNotUserApkInfo.getName() + " install "+b.getString(PackageInstaller.KEY_RESULT_DESC));
				if(currentNotUserApkInfo!=null&&currentNotUserApkInfo.getFile_path()!=null){
					File f = new File(currentNotUserApkInfo.getFile_path());
					if(f!=null&&f.exists()){
						f.delete();
					}
				}
				// down load next
			}
		}
	}

	@Override
	public void onDownloadComplete(String uiid) {
		// TODO Auto-generated method stub
		Log.d(TAG, downloadManager.findTaksByUUID(uiid).getFileName()+"down load complete");
		if(currentUserApkInfo!=null&&uiid.equalsIgnoreCase(currentUserApkInfo.getTast().getUUId())){
			//用户推送的apk文件下载完成
			ApkInfo info = PackageUtils.getUnInstalledApkInfo(FayeService.this, currentUserApkInfo.getFile_path());
			
			if(info!=null){
				currentUserApkInfo.setPackageName(info.getPackageName());
				currentUserApkInfo.setIcon(info.getDrawble());
				currentUserApkInfo.setDownload_state(PushedApkDownLoadInfo.STATUE_DOWNLOAD_COMPLETE);
				services.updateApkInfo(currentUserApkInfo);
				packageInstaller.instatll(currentUserApkInfo.getFile_path(), info.getPackageName());
				//通知ui
				Intent downLoadCompletIntent = new Intent(Global.ACTION_DOWNLOAD_COMPLETE);
			    downLoadCompletIntent.putExtra("_id", currentUserApkInfo.get_id());
			    sendBroadcast(downLoadCompletIntent);
			}else{
				Log.d(TAG, "unInstall apk info get fiale load next");
				currentUserApkInfo = null;
				startNextUserApkDownLoad();
			}
			
			return ;
		}
		if(currentNotUserApkInfo!=null&&uiid.equalsIgnoreCase(currentNotUserApkInfo.getTast().getUUId())){
			//。。。
			ApkInfo info = PackageUtils.getUnInstalledApkInfo(FayeService.this, currentNotUserApkInfo.getFile_path());
			
			if(info!=null){
				currentNotUserApkInfo.setName(info.getAppName());
				currentNotUserApkInfo.setIcon(info.getDrawble());
				currentNotUserApkInfo.setDownload_state(PushedApkDownLoadInfo.STATUE_DOWNLOAD_COMPLETE);
				services.updateApkInfo(currentNotUserApkInfo);
				packageInstaller.instatll(currentNotUserApkInfo.getFile_path(), info.getPackageName());
			}else{
				Log.d(TAG, "unInstall apk info get fiale load next");
			}
			return ;
		}
		Log.d(TAG, downloadManager.findTaksByUUID(uiid).getFileName()+"can handle the complete");
	}

	@Override
	public void onDownloadFaile(String uiid) {
		// TODO Auto-generated method stub
		Log.d(TAG, downloadManager.findTaksByUUID(uiid).getFileName()+"down load Faile");
		if(currentUserApkInfo!=null&&uiid.equalsIgnoreCase(currentUserApkInfo.getTast().getUUId())){
			//用户推送的apk文件下载失败
			currentUserApkInfo.setDownload_state(PushedApkDownLoadInfo.STATUE_DOWNLOAD_PAUSE);
			services.updateApkInfo(currentUserApkInfo);
//			通知ui
			Intent downLoadfaileIntent = new Intent(Global.ACTION_DOWNLOAD_FAILE);
			downLoadfaileIntent.putExtra("_id", currentUserApkInfo.get_id());
			sendBroadcast(downLoadfaileIntent);
			currentUserApkInfo = null;
			startNextUserApkDownLoad();
			return ;
		}
		if(currentNotUserApkInfo!=null&&uiid.equalsIgnoreCase(currentNotUserApkInfo.getTast().getUUId())){
			//。。。
			currentNotUserApkInfo.setDownload_state(PushedApkDownLoadInfo.STATUE_DOWNLOAD_PAUSE);
			services.updateApkInfo(currentNotUserApkInfo);
			return ;
		}
		Log.d(TAG, downloadManager.findTaksByUUID(uiid).getFileName()+"  not handle the Faile");
	}

	@Override
	public void onDownloadPogressed(String uiid) {
		// TODO Auto-generated method stub
//		if(currentUserApkInfo!=null&&uiid.equalsIgnoreCase(currentUserApkInfo.getTast().getUUId())){
			Intent progressIntent = new Intent(Global.ACTION_DOWNLOAD_PROGRESS);
//			progressIntent.putExtra("push_id", info.getPush_id());
//			progressIntent.putExtra("progress", (info.getCompeleteSize()*100)/info.getFileSize());
			sendBroadcast(progressIntent);
//			return;
//		}
//		Log.d(TAG, downloadManager.findTaksByUUID(uiid).getFileName()+"can handle the Faile");
	}

	@Override
	public void onFileSizeLoaded(String uiid) {
		// TODO Auto-generated method stub
		//判断当前文件是否能完整的存到sdcard中
	}
	
	
}
