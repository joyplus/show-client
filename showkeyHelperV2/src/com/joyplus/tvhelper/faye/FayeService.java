package com.joyplus.tvhelper.faye;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.joyplus.JoyplusMediaPlayerActivity;
import com.joyplus.mediaplayer.JoyplusMediaPlayerDataManager;
import com.joyplus.mediaplayer.VideoViewInterface.DecodeType;
import com.joyplus.network.filedownload.manager.DownLoadListner;
import com.joyplus.network.filedownload.manager.DownloadManager;
import com.joyplus.network.filedownload.model.DownloadTask;
import com.joyplus.sub.SUBTYPE;
import com.joyplus.sub.SubURI;
import com.joyplus.tvhelper.DialogActivity;
import com.joyplus.tvhelper.MyApp;
import com.joyplus.tvhelper.PlayBaiduActivity;
import com.joyplus.tvhelper.db.DBServices;
import com.joyplus.tvhelper.entity.ApkDownloadInfoParcel;
import com.joyplus.tvhelper.entity.ApkInfo;
import com.joyplus.tvhelper.entity.BTEpisode;
import com.joyplus.tvhelper.entity.CurrentPlayDetailData;
import com.joyplus.tvhelper.entity.MoviePlayHistoryInfo;
import com.joyplus.tvhelper.entity.PushedApkDownLoadInfo;
import com.joyplus.tvhelper.entity.PushedMovieDownLoadInfo;
import com.joyplus.tvhelper.faye.FayeClient.FayeListener;
import com.joyplus.tvhelper.utils.Constant;
import com.joyplus.tvhelper.utils.DesUtils;
import com.joyplus.tvhelper.utils.Global;
import com.joyplus.tvhelper.utils.HttpTools;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.PackageUtils;
import com.joyplus.tvhelper.utils.PreferencesUtils;
import com.joyplus.tvhelper.utils.Utils;
import com.lenovo.lsf.installer.PackageInstaller;


public class FayeService extends Service implements  Observer, DownLoadListner{

	private static final String TAG = "FayeService";
	
	public static boolean isSystemApp;
	
	private ExecutorService pool = Executors.newFixedThreadPool(5); 
	
	private static File APK_PATH = null;
	private static File MOVIE_PATH = null;

	private JoyplusMediaPlayerDataManager mediaPlayerDataManager;
	
	private static final long TIME_OUT = 60*1000;
//	private boolean isNeedReconnect = false;
	
	public static final int MESSAGE_DOWNLOAD_GET_FILESIE_SUCCESS = 0;
	public static final int MESSAGE_DOWNLOAD_CREAT_FILE_SUCCESS = 1;
	public static final int MESSAGE_DOWNLOAD_PROGRESS_CHANGED = 2;
	public static final int MESSAGE_DOWNLOAD_COMPLETE = 3;
	public static final int MESSAGE_DOWNLOAD_FAILE = 4;
	
	
	public static final int MESSAGE_SHOW_DIALOG = 101;
	public static final int MESSAGE_NEW_DOWNLOAD_ADD = 102;
	public static final int MESSAGE_APK_INSTALLED_PROGRESS = 103;
	public static final int MESSAGE_APK_INSTALLED_SUCCESS= 104;
	public static final int MESSAGE_APK_INSTALLED_FAIL= 105;
	
	public static final int MESSAGE_LISTEN_APP_LOOPER = 201;
	
	private String channel;
	private FayeClient myClient;
	private MyFayeListener fayeListener;
	private DBServices services;
	private DownloadManager downloadManager;
	private PackageInstaller packageInstaller;
	private PushedApkDownLoadInfo currentUserApkInfo; 
	private PushedMovieDownLoadInfo currentMovieInfo;
	private PushedApkDownLoadInfo currentNotUserApkInfo; 
	public static List<PushedApkDownLoadInfo> userPushApkInfos = new ArrayList<PushedApkDownLoadInfo>();
	public static List<PushedApkDownLoadInfo> notuserPushedApkInfos = new ArrayList<PushedApkDownLoadInfo>();
	public static List<PushedMovieDownLoadInfo> movieDownLoadInfos = new ArrayList<PushedMovieDownLoadInfo>();;
	private MyApp app;
	
	private MoviePlayHistoryInfo play_info;
	private PushedApkDownLoadInfo apkdownload_info;
	private int push_type;
	private String pincode_md5;
//	private String currentPackage = null;
	
//	private boolean isConnect = false;
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			Log.d(TAG, "recive broadcast --->" + action);
			if(Global.ACTION_CONFIRM_ACCEPT.equals(action)){
				
				PreferencesUtils.setPincodeMd5(FayeService.this, pincode_md5);
//				play_info.setId((int)services.insertMoviePlayHistory(play_info));
				if(push_type == 0&&apkdownload_info!=null){//apk
					userPushApkInfos.add(apkdownload_info);
					handler.sendEmptyMessage(MESSAGE_NEW_DOWNLOAD_ADD);
					if(currentUserApkInfo==null){
						currentUserApkInfo = apkdownload_info;
						currentUserApkInfo.setDownload_state(PushedApkDownLoadInfo.STATUE_DOWNLOADING);
						downloadManager.startTast(apkdownload_info.getTast());
						services.updateApkInfo(currentUserApkInfo);
					}
				}else if(push_type == 1&&play_info!=null){
					if(play_info!=null&&play_info.getPlay_type() == MoviePlayHistoryInfo.PLAY_TYPE_BAIDU){
//						if(play_info.getRecivedDonwLoadUrls().startsWith("bdhd")){
							Intent intent_baidu = new Intent(FayeService.this,PlayBaiduActivity.class);
							intent_baidu.putExtra("url", play_info.getRecivedDonwLoadUrls());
							intent_baidu.putExtra("name", play_info.getName());
							intent_baidu.putExtra("push_url", play_info.getPush_url());
							intent_baidu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent_baidu);
//						}
					}else{
						CurrentPlayDetailData playDate = new CurrentPlayDetailData();
//						final Intent intent_play = new Intent(FayeService.this,VideoPlayerJPActivity.class);
						final Intent intent_play = Utils.getIntent(FayeService.this);
						
						intent_play.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						if(play_info.getPlay_type()==MoviePlayHistoryInfo.PLAY_TYPE_BT_EPISODES){
							playDate.prod_type = JoyplusMediaPlayerActivity.TYPE_PUSH_BT_EPISODE;
							if(play_info.getBtEpisodes().size()>0){
								playDate.prod_sub_name = play_info.getBtEpisodes().get(0).getName();
							}
						}else{
							playDate.prod_type = JoyplusMediaPlayerActivity.TYPE_PUSH;
							
						}
						playDate.prod_name = play_info.getName();
						if(mediaPlayerDataManager.getDecodeType()==DecodeType.Decode_SW){
							playDate.prod_qua = Constant.DEFINATION_HD;
						}else{
							playDate.prod_qua = Constant.DEFINATION_HD2;
						}
//						playDate.prod_time =  Math.round(play_info.getPlayback_time()*1000);
						playDate.obj = play_info;
						playDate.isOnline = true;
//						playDate.prod_url = play_info.getDownload_url();
						app.setmCurrentPlayDetailData(playDate);
						app.set_ReturnProgramView(null);
						handler.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								startActivity(intent_play);
							}
						}, 0);
						sendBroadcast(new Intent(Global.ACTION_RECIVE_NEW_PUSH_MOVIE));
						sendBroadcast(new Intent(Global.ACTION_BAND_SUCCESS));
					}
				}
				
//				JSONObject json = new JSONObject();
//				try {
//					json.put("msg_type", 3);
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				myClient.sendMessage(json);
				
			}else if(Global.ACTION_CONFIRM_REFUSE.equals(action)){
//				JSONObject json = new JSONObject();
//				try {
//					json.put("msg_type", 4);
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				myClient.sendMessage(json);
				if(push_type == 0){//apk
					if(apkdownload_info!=null){
						services.deleteApkInfo(apkdownload_info);
					}
					apkdownload_info = null;
				}else if(push_type == 1){
					if(play_info!=null){
						services.deleteMoviePlayHistory(play_info);
					}
					play_info = null;
				}
				pincode_md5 = null;
			}else if(Global.ACTION_DOWNLOAD_PAUSE.equals(action)){
				
			}else if(Global.ACTION_APK_DOWNLOAD_CONTINUE.equals(action)){
				if(currentUserApkInfo == null){
					startNextUserApkDownLoad();
				}
			}else if(Global.ACTION_APK_DELETE_DOWNLOAD.equals(action)){
				
			}
//			else if(Global.ACTION_PINCODE_REFRESH.equals(action)){
//				myClient.disconnectFromServer();
//				isNeedReconnect = false;
//				stopSelf();
//			}
			else if(Global.ACTION_MOVIE_DOWNLOAD_CONTINUE.equals(action)){
				Log.i(TAG, "receiver---->" + action);
				if(currentMovieInfo ==null){
					startNextMovieDownLoad();
				}
			}else if(Global.ACTION_MOVIE_DELETE_DOWNLOAD.equals(action)){
				
			}else if(Global.ACTION_NEW_APK_DWONLOAD.equals(action)){
				ApkDownloadInfoParcel apkInfo = intent.getParcelableExtra("new_apk_download");
				PushedApkDownLoadInfo info = new PushedApkDownLoadInfo();
				info.setName(apkInfo.getApp_name());
				info.setIcon_url(apkInfo.getIcon_url());
				String url = apkInfo.getApk_url();
				String file_name = Utils.getFileNameforUrl(url);
				DownloadTask task = new DownloadTask(url, APK_PATH.getAbsolutePath(), file_name);
				info.setFile_path(APK_PATH.getAbsolutePath()+ File.separator + file_name);
				downloadManager.addTast(task);
				info.setPackageName(apkInfo.getPackage_name());
				info.setTast(task);
				info.setDownload_state(PushedApkDownLoadInfo.STATUE_WAITING_DOWNLOAD);
				info.setIsUser(PushedApkDownLoadInfo.IS_USER);
				info.set_id((int) services.insertApkInfo(info));
				userPushApkInfos.add(info);
				handler.sendEmptyMessage(MESSAGE_NEW_DOWNLOAD_ADD);
				if(currentUserApkInfo==null){
					currentUserApkInfo = info;
					currentUserApkInfo.setDownload_state(PushedApkDownLoadInfo.STATUE_DOWNLOADING);
					downloadManager.startTast(task);
					services.updateApkInfo(currentUserApkInfo);
				}
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
				Intent dowanlaodAddIntent = new Intent(Global.ACTION_DOWNLOAD_RECIVED);
				sendBroadcast(dowanlaodAddIntent);
				break;
			case MESSAGE_APK_INSTALLED_SUCCESS:
				Utils.showToast(FayeService.this, msg.obj + "安装成功");
				Log.d(TAG, "MESSAGE_APK_INSTALLED_SUCCESS -- >");
				break;
			case MESSAGE_APK_INSTALLED_FAIL:
				Log.d(TAG, "MESSAGE_APK_INSTALLED_FAIL -- >");
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
			case MESSAGE_DOWNLOAD_COMPLETE:
				String uuid = (String) msg.obj;
				handleDownLoadCompelte(uuid);
				break;
			case MESSAGE_DOWNLOAD_FAILE:
				String uuid_1 = (String) msg.obj;
				handleDownLoadFile(uuid_1);
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
//		com.joyplus.utils.Log.mbLoggable = true;
		mediaPlayerDataManager = new JoyplusMediaPlayerDataManager(this);
		APK_PATH = new File(Environment.getExternalStorageDirectory(), "showkey/apk");
		MOVIE_PATH = new File(Environment.getExternalStorageDirectory(), "showkey/movie");
		app = (MyApp) getApplication();
		services = DBServices.getInstance(this);
		downloadManager = DownloadManager.getInstance(this);
		downloadManager.setDownLoadListner(this);
		packageInstaller = new PackageInstaller(this);
		packageInstaller.addObserver(this);
		userPushApkInfos = services.queryUserApkDownLoadInfo();
		notuserPushedApkInfos = services.queryNotUserApkDownLoadInfo();
		movieDownLoadInfos = services.queryMovieDownLoadInfos();
		isSystemApp = isSystemApp();
		IntentFilter filter = new IntentFilter(Global.ACTION_CONFIRM_ACCEPT);
		filter.addAction(Global.ACTION_CONFIRM_REFUSE);
		filter.addAction(Global.ACTION_DOWNLOAD_PAUSE);
		filter.addAction(Global.ACTION_APK_DOWNLOAD_CONTINUE);
		filter.addAction(Global.ACTION_APK_DELETE_DOWNLOAD);
//		filter.addAction(Global.ACTION_PINCODE_REFRESH);
		filter.addAction(Global.ACTION_MOVIE_DELETE_DOWNLOAD);
		filter.addAction(Global.ACTION_MOVIE_DOWNLOAD_CONTINUE);
		filter.addAction(Global.ACTION_NEW_APK_DWONLOAD);
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
		com.joyplus.utils.Log.mbLoggable = false;
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		URI url = URI.create(Constant.FAYE_SERVICE+"/uploadApk");
		channel = "/" + PreferencesUtils.getChannel(this);
		if(myClient!=null){
			myClient.disconnectFromServer();
		}
		myClient = new FayeClient(handler, url, channel);
		Log.d(TAG, "Server----->" + Constant.BASE_URL+"/uploadApk");
		Log.d(TAG, "channel----->" + channel);
		
		if(fayeListener != null){
			fayeListener.setActive(false);
		}
		fayeListener = new MyFayeListener(); 
		myClient.setFayeListener(fayeListener);
		myClient.connectToServer(null); 
//		isNeedReconnect = true;
//		getLostUserPushApk();
		getLostUserPushMovie();
//		if(isSystemApp()){
//			getNotUsrPushApk();
//		}
		return super.onStartCommand(intent, START_STICKY, startId); 
	}
	
	private void getNotUsrPushApk(){
		pool.execute(new Runnable() {	
			@Override
			public void run() {
				// TODO Auto-generated method stub
//				infolist = services.GetPushedApklist(infolist);
				Log.d(TAG, "infolist size" + notuserPushedApkInfos.size());
				String url = Constant.BASE_URL + "/silent_app?app_key=" + Constant.APPKEY 
						+ "&mac_address=" + Utils.getMacAdd(FayeService.this) 
						+ "&page_num=" + 1
						+ "&page_size=" + 50;
				Log.d(TAG, url);
				String str = HttpTools.get(FayeService.this, url);
				Log.d(TAG, "PushApkHistories response-->" + str);
				try {
					JSONObject json = new JSONObject(str);
					
					JSONArray array = json.getJSONArray("resources");
					Log.d(TAG, "miss length ---------------------------->" + array.length());
					for(int i=0; i<array.length(); i++){
						JSONObject item = array.getJSONObject(i);
						String versionCode_str = item.getString("version_code"); 
						String packageName = item.getString("package_name");
						String appName = item.getString("app_name");
						String downloadUrl = item.getString("apk_url");
						if(versionCode_str == null|| "null".equals(versionCode_str) 
								|| packageName ==null|| "null".equals(packageName)  
								|| downloadUrl==null|| "null".equals(downloadUrl)  ){
							Log.e(TAG, "data is not enough");
							continue;
						}
						int versionCode = Integer.valueOf(versionCode_str);
						if(PackageUtils.isNeedInstalled(FayeService.this, packageName, versionCode)&&isNeedAddToList(packageName)){
							PushedApkDownLoadInfo info = new PushedApkDownLoadInfo();
							info.setName(appName);
							info.setPackageName(packageName);
							info.setIsUser(PushedApkDownLoadInfo.IS_NOT_USER);
							String fileName = Utils.getFileNameforUrl(downloadUrl);
							info.setDownload_state(PushedApkDownLoadInfo.STATUE_WAITING_DOWNLOAD);
							DownloadTask task = new DownloadTask(downloadUrl, APK_PATH.getAbsolutePath(), fileName);
							info.setFile_path(APK_PATH.getAbsolutePath() + File.separator + fileName);
							info.setTast(task);
							info.set_id((int) services.insertApkInfo(info));
							Log.d(TAG, appName + " add");
							downloadManager.addTast(task);
							notuserPushedApkInfos.add(info);
						}
//						PushedApkDownLoadInfo info = new PushedApkDownLoadInfo();
//						String file_url = item.getString("file_url");
//						info.setPush_id(item.getInt("id"));
//						info.setName(item.getString("app_name"));
//						info.setIsUser(PushedApkDownLoadInfo.IS_USER);
//						String fileName = Utils.getFileNameforUrl(file_url);
//						info.setDownload_state(PushedApkDownLoadInfo.STATUE_WAITING_DOWNLOAD);
//						DownloadTask task = new DownloadTask(file_url, APK_PATH.getAbsolutePath(), fileName, 3);
//						info.setFile_path(APK_PATH.getAbsolutePath() + File.separator + fileName);
//						info.setTast(task);
//						downloadManager.addTast(task);
//						info.set_id((int) services.insertApkInfo(info));
//						userPushApkInfos.add(info);
//						handler.sendEmptyMessage(MESSAGE_NEW_DOWNLOAD_ADD);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(currentNotUserApkInfo==null){
					startNextNotUserApkDownLoad(); 
				}
			}
		});
	}
	
	
	private boolean isNeedAddToList(String packageName){
		boolean flag = true;
		for(PushedApkDownLoadInfo info: notuserPushedApkInfos){
			if(packageName.equalsIgnoreCase(info.getPackageName())){
				flag = false;
			}
		}
		return flag;
	}
	
	
	private void getLostUserPushMovie(){
		pool.execute(new Runnable() {	
			@Override
			public void run() {
				// TODO Auto-generated method stub
//				infolist = services.GetPushedApklist(infolist);
				Log.d(TAG, "infolist size" + movieDownLoadInfos.size());
				String url = Constant.BASE_URL + "/pushVodHistories?app_key=" + Constant.APPKEY 
						+ "&mac_address=" + Utils.getMacAdd(FayeService.this) 
						+ "&page_num=" + 1
						+ "&page_size=" + 50;
				Log.d(TAG, url);
				String str = HttpTools.get(FayeService.this, url);
				Log.d(TAG, "pushMovieHistories response-->" + str);
				try {
					JSONArray array = new JSONArray(str);
					Log.d(TAG, "miss length ---------------------------->" + array.length());
					for(int i=0; i<array.length(); i++){
						try {
							JSONObject item = array.getJSONObject(i);
							int push_id = item.getInt("id");
//							String push_name = URLDecoder.decode(item.getString("name"), "utf-8");
							String push_name = item.getString("name");
//							String push_url = URLDecoder.decode(item.getString("playurl"), "utf-8");
							String push_url = item.getString("playurl");
							String push_play_url = item.getString("downurl");
							String time_token = item.getString("time_token");
							String md5_code = item.getString("md5_code");
							List<BTEpisode> es = null; 
							if(item.has("prodName")){
								es = new ArrayList<BTEpisode>();
								JSONArray array_name = item.getJSONArray("prodName");
								Log.d(TAG, array_name.toString());
								for(int j = 0; j< array_name.length() ; j++){
									BTEpisode e = new BTEpisode();
									e.setDefination(Constant.DEFINATION_HD2);
									e.setName(array_name.getString(j));
									es.add(e);
									Log.d(TAG, array_name.getString(j));
								}
								
							}
							int type = item.getInt("type");
							if(PreferencesUtils.getPincodeMd5(FayeService.this)!=null &&PreferencesUtils.getPincodeMd5(FayeService.this).equals(md5_code)){
								if(type == 5){//漏掉的播放
									MoviePlayHistoryInfo play_info = services.hasMoviePlayHistory(MoviePlayHistoryInfo.PLAY_TYPE_ONLINE, push_url);
									if(play_info == null){
										play_info = new MoviePlayHistoryInfo();
										play_info.setName(push_name);
										play_info.setPush_id(push_id);
										play_info.setPush_url(push_url);
										play_info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_ONLINE);
										play_info.setRecivedDonwLoadUrls(push_play_url);
										play_info.setDefination(Constant.DEFINATION_HD2);
										play_info.setCreat_time(System.currentTimeMillis());
										play_info.setTime_token(time_token+",");
										if(es!=null && es.size()>0){
											play_info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_BT_EPISODES);
											play_info.setBtEpisodes(es);
										}
										play_info.setId((int)services.insertMoviePlayHistory(play_info));
									}else{
										play_info.setDefination(Constant.DEFINATION_HD2);
										play_info.setName(push_name);
										play_info.setRecivedDonwLoadUrls(push_play_url);
										play_info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_ONLINE);
										if(play_info.getTime_token()==null){
											play_info.setTime_token("");
										}
										play_info.setTime_token(play_info.getTime_token() + time_token+",");
										if(es!=null && es.size()>0){
											play_info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_BT_EPISODES);
											play_info.setBtEpisodes(es);
										}
//										play_info.setPush_id(push_id);
										services.updateMoviePlayHistory(play_info);
//										if(play_info.getTime_token()==null){
//											play_info.setTime_token("");
//										}
//										play_info.setTime_token(play_info.getTime_token() + time_token+",");
//										services.updateMoviePlayHistory(play_info);
									}
								}else if(type == 6){//漏掉的下载
									
								}else if(type == 11){
									MoviePlayHistoryInfo play_info = services.hasMoviePlayHistory(MoviePlayHistoryInfo.PLAY_TYPE_ONLINE, push_url);
									if(play_info == null){
										play_info = new MoviePlayHistoryInfo();
										play_info.setName(push_name);
										play_info.setPush_id(push_id);
										play_info.setPush_url(push_url);
										play_info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_BAIDU);
										play_info.setRecivedDonwLoadUrls(push_play_url);
										play_info.setDefination(Constant.DEFINATION_HD2);
										play_info.setCreat_time(System.currentTimeMillis());
										play_info.setTime_token(time_token+",");
										play_info.setId((int)services.insertMoviePlayHistory(play_info));
									}else{
										if(play_info.getTime_token()==null){
											play_info.setTime_token("");
										}
										play_info.setTime_token(play_info.getTime_token() + time_token+",");
										services.updateMoviePlayHistory(play_info);
									}
								}
							}
							updateMovieHistory(push_id);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
//						PushedMovieDownLoadInfo info = new PushedMovieDownLoadInfo();
//						String push_url = "";
//						try {
//							push_url = Utils.getUrl(item.getString("file_url"));
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//							continue;
//						}
//						String downLoad_url = Utils.getRedirectUrl(push_url);
//						boolean isSupport = true;
//						for(int j=0; j<Constant.video_dont_support_extensions.length; j++){
//							if(downLoad_url.contains(Constant.video_dont_support_extensions[j])){
//								Log.e(TAG, "not support down load m3u8 !");
//								isSupport = false;
//								break;
//							}
//						}
//						if(!isSupport){
//							continue;
//						}
//						info.setPush_id(item.getInt("id"));
//						info.setName(item.getString("name"));
////						info.setName(item.getString("name"));
//						info.setPush_url(downLoad_url);
//						String fileName = Utils.getFileNameforUrl(downLoad_url);
//						Log.d(TAG, fileName);
////						info.setName(fileName);
//						info.setDownload_state(PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSE);
//						DownloadTask task = new DownloadTask(downLoad_url, MOVIE_PATH.getAbsolutePath(), fileName, 3);
//						info.setFile_path(MOVIE_PATH.getAbsolutePath() + File.separator + fileName);
//						info.setTast(task);
//						downloadManager.addTast(task);
//						info.set_id((int) services.insertMovieDownLoadInfo(info));
//						movieDownLoadInfos.add(info);
//						updateMovieHistory(info.getPush_id());
//						handler.sendEmptyMessage(MESSAGE_NEW_DOWNLOAD_ADD);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void getLostUserPushApk(){
		pool.execute(new Runnable() {	
			@Override
			public void run() {
				// TODO Auto-generated method stub
//				infolist = services.GetPushedApklist(infolist);
//				07-25 14:53:32.444: D/FayeService(3890): http://tt.yue001.com:8080/pushMsgHistories?app_key=ijoyplus_android_0001bj&mac_address=0:9d:b:0:7d:b8&page_num=1&page_size=50

				Log.d(TAG, "infolist size" + userPushApkInfos.size());
				String url = Constant.BASE_URL + "/pushMsgHistories?app_key=" + Constant.APPKEY 
						+ "&mac_address=" + Utils.getMacAdd(FayeService.this) 
						+ "&page_num=" + 1
						+ "&page_size=" + 50;
				Log.d(TAG, url);
				String str = HttpTools.get(FayeService.this, url);
				Log.d(TAG, "pushMsg_USER_APK_Histories response-->" + str);
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
						String fileName = Utils.getFileNameforUrl(file_url);
						info.setDownload_state(PushedApkDownLoadInfo.STATUE_DOWNLOAD_PAUSE);
						DownloadTask task = new DownloadTask(file_url, APK_PATH.getAbsolutePath(), fileName);
						info.setFile_path(APK_PATH.getAbsolutePath() + File.separator + fileName);
						info.setTast(task);
						downloadManager.addTast(task);
						info.set_id((int) services.insertApkInfo(info));
						userPushApkInfos.add(info);
						updateHistory(info.getPush_id());
						handler.sendEmptyMessage(MESSAGE_NEW_DOWNLOAD_ADD);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
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
	private void startNextNotUserApkDownLoad(){
		Log.d(TAG, "startNextNotUserApkDownLoad--->");
		for(PushedApkDownLoadInfo info :notuserPushedApkInfos){
			if(info.getDownload_state()==PushedApkDownLoadInfo.STATUE_WAITING_DOWNLOAD){
				Log.d(TAG, info.getName() +"start loading");
				Log.d(TAG, info.getName() + info.getDownload_state()); 
				if(info.getTast().getState()==-1){
					downloadManager.startTast(info.getTast());
				}else{
					downloadManager.resumeTask(info.getTast());
				}
				info.setDownload_state(PushedApkDownLoadInfo.STATUE_DOWNLOADING);
				currentNotUserApkInfo = info;
				services.updateApkInfo(currentNotUserApkInfo); 
				return ;
			}
		}
	}
	
	private void startNextMovieDownLoad(){
		Log.d(TAG, "startNextMovieDownLoad--->");
		for(PushedMovieDownLoadInfo info :movieDownLoadInfos){
			if(info.getDownload_state()==PushedMovieDownLoadInfo.STATUE_WAITING_DOWNLOAD){
				if(info.getTast().getState()==-1){
					downloadManager.startTast(info.getTast());
				}else{
					downloadManager.resumeTask(info.getTast());
				}
				info.setDownload_state(PushedMovieDownLoadInfo.STATUE_DOWNLOADING);
				currentMovieInfo = info;
				services.updateMovieDownLoadInfo(currentMovieInfo);
				return ;
			}
		}
	}
	
	private void updateHistory(final int id){
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String url = Constant.BASE_URL + "/updateHistory?app_key=" + Constant.APPKEY 
						+ "&mac_address=" + Utils.getMacAdd(FayeService.this)
						+ "&id=" + id;
				Log.d(TAG, url);
				String str = HttpTools.get(FayeService.this, url);
				Log.d(TAG, "updateHistory response-->" + str);
			}
		});
	}
	
	private void updateMovieHistory(final int id){
		
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String url = Constant.BASE_URL + "/updateVodHistory?app_key=" + Constant.APPKEY 
						+ "&mac_address=" + Utils.getMacAdd(FayeService.this)
						+ "&id=" + id;
				Log.d(TAG, url);
				String str = HttpTools.get(FayeService.this, url);
				Log.d(TAG, "updateHistory response-->" + str);
			}
		});
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
					Message msg = handler.obtainMessage(MESSAGE_APK_INSTALLED_SUCCESS);
					msg.obj = currentUserApkInfo.getName();
					handler.sendMessage(msg);
					Intent intent = new Intent(Global.ACTION_DOWNL_INSTALL_SUCESS);
					intent.putExtra("_id", currentUserApkInfo.get_id());
					services.deleteApkInfo(currentUserApkInfo);
					userPushApkInfos.remove(currentUserApkInfo);
					sendBroadcast(intent);
					if(!PreferencesUtils.isautodelete(FayeService.this)){
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
					Message msg = handler.obtainMessage(MESSAGE_APK_INSTALLED_FAIL);
					msg.obj = currentUserApkInfo.getName();
					handler.sendMessage(msg);
					currentUserApkInfo.setDownload_state(PushedApkDownLoadInfo.STATUE_INSTALL_FAILE);
					services.updateApkInfo(currentUserApkInfo);
					sendBroadcast(intent);
				}
				currentUserApkInfo = null;
				// down load next
				startNextUserApkDownLoad();
				return;
			}
			if(currentNotUserApkInfo!=null && currentNotUserApkInfo.getPackageName().equalsIgnoreCase(b.getString(PackageInstaller.KEY_PACKAGE_NAME))){
				Log.d(TAG, currentNotUserApkInfo.getName() + " install "+b.getString(PackageInstaller.KEY_RESULT_DESC));
				if(currentNotUserApkInfo!=null&&currentNotUserApkInfo.getFile_path()!=null){
					File f = new File(currentNotUserApkInfo.getFile_path());
					if(f!=null&&f.exists()){
						f.delete();
					}
				}
				notuserPushedApkInfos.remove(currentNotUserApkInfo);
				currentNotUserApkInfo = null;
				startNextNotUserApkDownLoad();
			}else{
				Log.e(TAG, "not start next");
				if(currentNotUserApkInfo == null){
					Log.e(TAG, "currentNotUserApkInfo is null");
				}else if(!currentNotUserApkInfo.getPackageName().equalsIgnoreCase(b.getString(PackageInstaller.KEY_PACKAGE_NAME))){
					Log.e(TAG, currentNotUserApkInfo.getPackageName() +"!=" + b.getString(PackageInstaller.KEY_PACKAGE_NAME));
				}
				if(currentNotUserApkInfo!=null&&currentNotUserApkInfo.getFile_path()!=null){
					File f = new File(currentNotUserApkInfo.getFile_path());
					if(f!=null&&f.exists()){
						f.delete();
					}
				}
				notuserPushedApkInfos.remove(currentNotUserApkInfo);
				currentNotUserApkInfo = null;
				startNextNotUserApkDownLoad();
			} 
		}
	}

	@Override
	public void onDownloadComplete(String uiid) {
		// TODO Auto-generated method stub
		Message msg = handler.obtainMessage(MESSAGE_DOWNLOAD_COMPLETE);
		msg.obj = uiid;
		handler.sendMessage(msg);
	}

	@Override
	public synchronized void onDownloadFaile(String uiid) {
		// TODO Auto-generated method stub
		Message msg = handler.obtainMessage(MESSAGE_DOWNLOAD_FAILE);
		msg.obj = uiid;
		handler.sendMessage(msg);
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
		if (currentUserApkInfo != null
				&& uiid.equalsIgnoreCase(currentUserApkInfo.getTast().getUUId())) {
			
			switch (currentUserApkInfo.getDownload_state()) {
			case PushedMovieDownLoadInfo.STATUE_DOWNLOADING:
			case PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSE:
			case PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSEING:

				switch (currentUserApkInfo.getTast().getState()) {
				case DownloadTask.STATE_STARTED:
//					currentUserApkInfo.setDownload_state(PushedMovieDownLoadInfo.STATUE_WAITING_DOWNLOAD);
					break;
				case DownloadTask.STATE_CONNECTING:
					
					break;
				case DownloadTask.STATE_FINISHED:
					break;
				case DownloadTask.STATE_DOWNLOADING:
					currentUserApkInfo.setDownload_state(PushedMovieDownLoadInfo.STATUE_DOWNLOADING);
					break;
				case DownloadTask.STATE_PAUSED:
					currentUserApkInfo.setDownload_state(PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSE);
					break;
				case DownloadTask.STATE_FAILED:
					currentUserApkInfo.setDownload_state(PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSE);
					break;

				default:
					break;
				}
				break;
			case PushedMovieDownLoadInfo.STATUE_WAITING_DOWNLOAD:
				switch (currentUserApkInfo.getTast().getState()) {
				case DownloadTask.STATE_DOWNLOADING:
					currentUserApkInfo.getTast().setState(DownloadTask.STATE_PAUSED);
					break;

				default:
					break;
				}
				break;
			default:
				break;
			}
		}
		
		if (currentNotUserApkInfo != null
				&& uiid.equalsIgnoreCase(currentNotUserApkInfo.getTast().getUUId())) {
			
			switch (currentNotUserApkInfo.getDownload_state()) {
			case PushedMovieDownLoadInfo.STATUE_DOWNLOADING:
			case PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSE:
			case PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSEING:

				switch (currentNotUserApkInfo.getTast().getState()) {
				case DownloadTask.STATE_STARTED:
//					currentNotUserApkInfo.setDownload_state(PushedMovieDownLoadInfo.STATUE_WAITING_DOWNLOAD);
					break;
				case DownloadTask.STATE_CONNECTING:
					
					break;
				case DownloadTask.STATE_FINISHED:
					break;
				case DownloadTask.STATE_DOWNLOADING:
					currentNotUserApkInfo.setDownload_state(PushedMovieDownLoadInfo.STATUE_DOWNLOADING);
					break;
				case DownloadTask.STATE_PAUSED:
					currentNotUserApkInfo.setDownload_state(PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSE);
					break;
				case DownloadTask.STATE_FAILED:
					currentNotUserApkInfo.setDownload_state(PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSE);
					break;

				default:
					break;
				}
				break; 
			case PushedMovieDownLoadInfo.STATUE_WAITING_DOWNLOAD:
				switch (currentUserApkInfo.getTast().getState()) {
				case DownloadTask.STATE_DOWNLOADING:
					currentUserApkInfo.getTast().setState(DownloadTask.STATE_PAUSED);
					break;

				default:
					break;
				}
				break;
			default:
				break;
			}
		}
		
		if (currentMovieInfo != null
				&& uiid.equalsIgnoreCase(currentMovieInfo.getTast().getUUId())) {
			
			Log.i(TAG, "onDownloadPogressed currentMovieInfo--->getDownload_state" + currentMovieInfo.getDownload_state()
					+ " currentMovieInfo.getTast().getState():" + currentMovieInfo.getTast().getState());
			
			switch (currentMovieInfo.getDownload_state()) {
			case PushedMovieDownLoadInfo.STATUE_DOWNLOADING:
			case PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSE:
			case PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSEING:

				switch (currentMovieInfo.getTast().getState()) {
				case DownloadTask.STATE_STARTED:
//					currentMovieInfo.setDownload_state(PushedMovieDownLoadInfo.STATUE_WAITING_DOWNLOAD);
					break;
				case DownloadTask.STATE_CONNECTING:
					
					break;
				case DownloadTask.STATE_FINISHED:
					break;
				case DownloadTask.STATE_DOWNLOADING:
					currentMovieInfo.setDownload_state(PushedMovieDownLoadInfo.STATUE_DOWNLOADING);
					break;
				case DownloadTask.STATE_PAUSED:
					currentMovieInfo.setDownload_state(PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSE);
					break;
				case DownloadTask.STATE_FAILED:
					currentMovieInfo.setDownload_state(PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSE);
					break;

				default:
					break;
				}
				break; 
			case PushedMovieDownLoadInfo.STATUE_WAITING_DOWNLOAD:
				switch (currentUserApkInfo.getTast().getState()) {
				case DownloadTask.STATE_DOWNLOADING:
					currentUserApkInfo.getTast().setState(DownloadTask.STATE_PAUSED);
					break;

				default:
					break;
				}
				break;
			default:
				break;
			}
		}
		

	}

	@Override
	public void onFileSizeLoaded(String uiid) {
		// TODO Auto-generated method stub
		//判断当前文件是否能完整的存到sdcard中
	}

	@Override
	public void onPused(String uiid) {
		// TODO Auto-generated method stub
		onDownloadFaile(uiid);
	}
	
	
	private void handleDownLoadFile(String uiid){
		//Log.d(TAG, downloadManager.findTaksByUUID(uiid).getFileName()+"down load Faile");
		if(currentUserApkInfo!=null&&uiid.equalsIgnoreCase(currentUserApkInfo.getTast().getUUId())){
			//用户推送的apk文件下载失败
			currentUserApkInfo.setDownload_state(PushedApkDownLoadInfo.STATUE_DOWNLOAD_PAUSE);
//			通知ui
			Intent downLoadfaileIntent = new Intent(Global.ACTION_APK_DOWNLOAD_FAILE);
			downLoadfaileIntent.putExtra("_id", currentUserApkInfo.get_id());
			sendBroadcast(downLoadfaileIntent);
			services.updateApkInfo(currentUserApkInfo);
			currentUserApkInfo = null;
			startNextUserApkDownLoad();
			return ;
		}
		if(currentNotUserApkInfo!=null&&uiid.equalsIgnoreCase(currentNotUserApkInfo.getTast().getUUId())){
			//。。。
			currentNotUserApkInfo.setDownload_state(PushedApkDownLoadInfo.STATUE_DOWNLOAD_PAUSE);
			services.updateApkInfo(currentNotUserApkInfo);
			currentNotUserApkInfo = null;
			startNextNotUserApkDownLoad();
			return ;
		}
		if(currentMovieInfo!=null &&uiid.equalsIgnoreCase(currentMovieInfo.getTast().getUUId())){
			//下载完成
			currentMovieInfo.setDownload_state(PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSE);
			//通知ui
			Intent downLoadCompletIntent = new Intent(Global.ACTION_MOVIE_DOWNLOAD_FAILE);
		    downLoadCompletIntent.putExtra("_id", currentMovieInfo.get_id());
		    sendBroadcast(downLoadCompletIntent);
		    services.updateMovieDownLoadInfo(currentMovieInfo);
			//开始下一个
			currentMovieInfo = null;
			startNextMovieDownLoad();
			return ;
		}
		Log.d(TAG, downloadManager.findTaksByUUID(uiid).getFileName()+"  not handle the Faile");
	}
	
	private void handleDownLoadCompelte(String uiid){
		Log.d(TAG, downloadManager.findTaksByUUID(uiid).getFileName()+"down load complete");
		if(currentUserApkInfo!=null&&uiid.equalsIgnoreCase(currentUserApkInfo.getTast().getUUId())){
			//用户推送的apk文件下载完成
			ApkInfo info = PackageUtils.getUnInstalledApkInfo(FayeService.this, currentUserApkInfo.getFile_path());
			
			if(info!=null){
				currentUserApkInfo.setPackageName(info.getPackageName());
				currentUserApkInfo.setIcon(info.getDrawble());
				currentUserApkInfo.setDownload_state(PushedApkDownLoadInfo.STATUE_DOWNLOAD_COMPLETE);
				services.updateApkInfo(currentUserApkInfo);
				//通知ui
				Intent downLoadCompletIntent = new Intent(Global.ACTION_APK_DOWNLOAD_COMPLETE);
			    downLoadCompletIntent.putExtra("_id", currentUserApkInfo.get_id());
			    sendBroadcast(downLoadCompletIntent);
				if(isSystemApp){
					packageInstaller.instatll(currentUserApkInfo.getFile_path(), info.getPackageName());
				}else{
					currentUserApkInfo = null;
					startNextUserApkDownLoad();
				}
			}else{
				Log.d(TAG, "unInstall apk info get fiale load next");
				currentUserApkInfo.setDownload_state(PushedApkDownLoadInfo.STATUE_INSTALL_FAILE);
				services.updateApkInfo(currentUserApkInfo);
				currentUserApkInfo = null;
				startNextUserApkDownLoad();
			}
			
			return ;
		}
		if(currentNotUserApkInfo!=null&&uiid.equalsIgnoreCase(currentNotUserApkInfo.getTast().getUUId())){
			//。。。
			ApkInfo info = PackageUtils.getUnInstalledApkInfo(FayeService.this, currentNotUserApkInfo.getFile_path());
			
			if(info!=null){
				currentNotUserApkInfo.setPackageName(info.getPackageName());
//				currentNotUserApkInfo.setIcon(info.getDrawble());
				currentNotUserApkInfo.setDownload_state(PushedApkDownLoadInfo.STATUE_DOWNLOAD_COMPLETE);
				services.updateApkInfo(currentNotUserApkInfo);
				packageInstaller.instatll(currentNotUserApkInfo.getFile_path(), info.getPackageName());
			}else{
				Log.e(TAG, "unInstall apk info get fiale load next");
				File f = new File(currentNotUserApkInfo.getFile_path());
				if(f!=null&&f.exists()){
					f.delete();
				}
				currentNotUserApkInfo.setDownload_state(PushedApkDownLoadInfo.STATUE_DOWNLOAD_COMPLETE);
				services.updateApkInfo(currentNotUserApkInfo);
				currentNotUserApkInfo = null;
				startNextNotUserApkDownLoad();
			}
			return ;
		}
		
		if(currentMovieInfo!=null &&uiid.equalsIgnoreCase(currentMovieInfo.getTast().getUUId())){
			//下载完成
			currentMovieInfo.setDownload_state(PushedMovieDownLoadInfo.STATUE_DOWNLOAD_COMPLETE);
			services.updateMovieDownLoadInfo(currentMovieInfo);
			//通知ui
			Intent downLoadCompletIntent = new Intent(Global.ACTION_MOVIE_DOWNLOAD_COMPLETE);
		    downLoadCompletIntent.putExtra("_id", currentMovieInfo.get_id());
		    sendBroadcast(downLoadCompletIntent);
		    movieDownLoadInfos.remove(currentMovieInfo);
			//开始下一个
			currentMovieInfo = null;
			startNextMovieDownLoad();
		}
		Log.d(TAG, downloadManager.findTaksByUUID(uiid).getFileName()+"can handle the complete");
	}
	
	@Override
	public void onDownloadFileUnusual(String uiid) {
		// TODO Auto-generated method stub
		onDownloadFaile(uiid);
	}
	
	private boolean isSystemApp(){
		if ((getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
			return false;
		}else{
			return true;
		}
		
	}
	
//	mp4{m}http://hot.vrs.sohu.com/ipad1244506_4585881117442_4455827.m3u8?plat=0{mType}hd2{m}http://hot.vrs.sohu.com/ipad1244507_4585881117442_4455828.m3u8?plat=0 

	
//	private String getUrl(String push_urls) throws Exception{
////		push_urls = DES.decryptDES(push_urls, Constant.DES_KEY);
//		Log.d(TAG, push_urls);
//		String[] urls = push_urls.split("\\{mType\\}");
//		List<URLS_INDEX> list = new ArrayList<URLS_INDEX>();
//		for(String str : urls){
//			URLS_INDEX url_index_info = new URLS_INDEX();
//			String[] p = str.split("\\{m\\}");
//			if("hd2".equalsIgnoreCase(p[0])){
//				url_index_info.defination = 0;
//			}else if("mp4".equalsIgnoreCase(p[0])){
//				url_index_info.defination = 1;
//			}else if("3gp".equalsIgnoreCase(p[0])){
//				url_index_info.defination = 2;
//			}else{
//				url_index_info.defination = 3;
//			}
//			url_index_info.url = p[1];
//			list.add(url_index_info);
//		}
//		if(list.size()>1){
//			Collections.sort(list, new DefinationComparatorIndex());
//		}
//		if(list.size()<=0){
//			return  null;
//		}else{
//			return list.get(0).url;
//		}
//	}
//	
	class PUSH_URL_INDEX{
		int defination;
		String url;
	}
	
	class MyFayeListener implements FayeListener{

		private boolean isactive;
		
		public MyFayeListener(){
			isactive = true;
		}
		
		public void setActive(boolean isActive){
			this.isactive = isActive;
		}
		
		
		@Override
		public void connectedToServer() {
			// TODO Auto-generated method stub
			Log.d(TAG, "server connected----->");
			Intent intent = new Intent(Global.ACTION_CONNECT_SUCCESS);
			sendBroadcast(intent);
		}

		@Override
		public void disconnectedFromServer() {
			// TODO Auto-generated method stub
			
				Log.w(TAG, "server disconnected!----->");
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(isactive){
							myClient.connectToServer(null);
							Intent intent = new Intent(Global.ACTION_DISCONNECT_SERVER);
							sendBroadcast(intent);
						}
					}
				}, 2000);
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
					JSONObject data;
					switch (type) {
					case 1:
//						data = json.getJSONObject("body");
//						final int id = data.getInt("id");
//						try{
//							PushedApkDownLoadInfo info = new PushedApkDownLoadInfo();
//							info.setName(data.getString("app_name"));
//							String url = data.getString("file_url");
//							String packageName = data.getString("package_name");
//							String file_name = Utils.getFileNameforUrl(url);
//							info.setPush_id(id);
//							DownloadTask task = new DownloadTask(url, APK_PATH.getAbsolutePath(), file_name);
//							info.setFile_path(APK_PATH.getAbsolutePath()+ File.separator + file_name);
//							downloadManager.addTast(task);
//							info.setTast(task);
//							info.setPackageName(packageName);
//							info.setIsUser(PushedApkDownLoadInfo.IS_USER);
//							info.setDownload_state(PushedApkDownLoadInfo.STATUE_WAITING_DOWNLOAD);
//							info.set_id((int) services.insertApkInfo(info));
//							apkdownload_info = info;
//							push_type = 0;
//							pincode_md5 = data.getString("md5_code");
//							Log.d(TAG, pincode_md5);
//							for(PushedApkDownLoadInfo info_1: userPushApkInfos){
//								if(packageName!=null&&packageName.equals(info_1.getPackageName())){
//									updateHistory(id);
//									return;
//								}
//								
//								if(getApplicationInfo().packageName.equals(packageName)){
//									updateHistory(id);
//									return;
//								}
//							}
//							if(PreferencesUtils.getPincodeMd5(FayeService.this)!=null
//									&&PreferencesUtils.getPincodeMd5(FayeService.this).equals(pincode_md5)){
//								userPushApkInfos.add(info);
//								handler.sendEmptyMessage(MESSAGE_NEW_DOWNLOAD_ADD);
//								if(currentUserApkInfo==null){
//									currentUserApkInfo = info;
//									currentUserApkInfo.setDownload_state(PushedApkDownLoadInfo.STATUE_DOWNLOADING);
//									downloadManager.startTast(task);
//									services.updateApkInfo(currentUserApkInfo);
//								}
//							}else{
//								handler.sendEmptyMessage(MESSAGE_SHOW_DIALOG);
//							}
//						}catch (Exception e) {
//							// TODO: handle exception
//							e.printStackTrace();
//						}
//						updateHistory(id);
						break;
					case 5:
//						JSONObject data_1 = json.getJSONObject("body");
						data = json.getJSONObject("body");
						int push_id = Integer.valueOf(data.getString("id"));
						String time_token = data.getString("time");
						List<BTEpisode> es = null; 
						if(data.has("prodName")){
							es = new ArrayList<BTEpisode>();
							JSONArray array = data.getJSONArray("prodName");
							Log.d(TAG, array.toString());
							for(int i = 0; i< array.length() ; i++){
								BTEpisode e = new BTEpisode();
								e.setDefination(Constant.DEFINATION_HD2);
								e.setName(array.getString(i));
								es.add(e);
								Log.d(TAG, array.getString(i));
							}
						}
						List<SubURI> subList = null;
						if(data.has("subtitle")){
							Log.d(TAG, data.get("subtitle").toString());
							if(!"".equals(data.get("subtitle").toString())){
								JSONArray array_sub = data.getJSONArray("subtitle");
								subList = new ArrayList<SubURI>();
								for(int i = 0; i< array_sub.length() ; i++){
									JSONObject subObj = array_sub.getJSONObject(i);
									SubURI subInfo = new SubURI();
									subInfo.setName(subObj.getString("name"));
									subInfo.setUrl(subObj.getString("url"));
									subInfo.SubType = SUBTYPE.NETWORK;
									subList.add(subInfo);
								}
							}
							
						}
//						long time = System.currentTimeMillis() - Long.valueOf(data.getString("time"));
//						Log.d(TAG, "time ---->" + time);
//						if(time>TIME_OUT){
//							updateMovieHistory(push_id);
//							return ;
//						}
						if(services.hasMoviePushHistory(time_token)!=null){
							updateMovieHistory(push_id);
							return ;
						}
//						intent.putExtra("ID", json.getString("prod_id"));
//						String movie_play_url = null;
//						try {
//							movie_play_url = Utils.getUrl(data.getString("downurl"));
//						} catch (Exception e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
//						if(movie_play_url == null){
//							Log.e(TAG, "movie_play_url error !"); 
//							return ;
//						}
						play_info = services.hasMoviePlayHistory(MoviePlayHistoryInfo.PLAY_TYPE_ONLINE, data.getString("playurl"));
						if(play_info == null){
							play_info = new MoviePlayHistoryInfo();
//							play_info.setDownload_url(movie_play_url);
//							play_info.setName(URLDecoder.decode(data.getString("name"), "utf-8"));
							play_info.setName(data.getString("name"));
							play_info.setPush_id(push_id);
							play_info.setPush_url(data.getString("playurl"));
							play_info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_ONLINE);
							play_info.setRecivedDonwLoadUrls(data.getString("downurl"));
//							play_info.setId((int)services.insertMoviePlayHistory(play_info));
							play_info.setDefination(Constant.DEFINATION_HD2);
							play_info.setCreat_time(System.currentTimeMillis());
							play_info.setTime_token(time_token+",");
							if(es!=null && es.size()>0){
								play_info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_BT_EPISODES);
								play_info.setBtEpisodes(es);
							}
							play_info.setId((int)services.insertMoviePlayHistory(play_info));
						}else{
							play_info.setDefination(Constant.DEFINATION_HD2);
							play_info.setName(data.getString("name"));
							play_info.setRecivedDonwLoadUrls(data.getString("downurl"));
							play_info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_ONLINE);
							if(play_info.getTime_token()==null){
								play_info.setTime_token("");
							}
							play_info.setTime_token(play_info.getTime_token() + time_token+",");
							if(es!=null && es.size()>0){
								play_info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_BT_EPISODES);
								play_info.setBtEpisodes(es);
							}
							play_info.setPush_id(push_id);
							services.updateMoviePlayHistory(play_info);
						}
						if(subList!=null){
							Log.d(TAG, "subList size = " +subList.size());
						}
						play_info.setSubList(subList);
						push_type = 1;
						pincode_md5 = data.getString("md5_code");
						Log.d(TAG, pincode_md5);
						if(PreferencesUtils.getPincodeMd5(FayeService.this)!=null
								&&(PreferencesUtils.getPincodeMd5(FayeService.this).equals(pincode_md5)||PreferencesUtils.getToken(FayeService.this).equals(pincode_md5))){
							CurrentPlayDetailData playDate = new CurrentPlayDetailData();
//							final Intent intent = new Intent(FayeService.this,VideoPlayerJPActivity.class);
							final Intent intent = Utils.getIntent(FayeService.this);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//							playDate.prod_id = data.getString("id");
							
//							playDate.prod_type = Integer.valueOf(json.getString("prod_type"));
							if(play_info.getPlay_type()==MoviePlayHistoryInfo.PLAY_TYPE_BT_EPISODES){
								playDate.prod_type = JoyplusMediaPlayerActivity.TYPE_PUSH_BT_EPISODE;
								if(play_info.getBtEpisodes().size()>0){
									playDate.prod_sub_name = play_info.getBtEpisodes().get(0).getName();
								}
							}else{
								playDate.prod_type = JoyplusMediaPlayerActivity.TYPE_PUSH;
								
							}
							playDate.prod_name = play_info.getName();
//							playDate.prod_time =  Math.round(play_info.getPlayback_time()*1000);
							playDate.obj = play_info;
//							playDate.prod_name = json.getString("prod_name");
							
							
//							playDate.prod_url = play_info.getDownload_url();
//							playDate.prod_src = json.getString("prod_src");
//							playDate.prod_time = Math.round(Float.valueOf(json.getString("prod_time"))*1000);
							if(mediaPlayerDataManager.getDecodeType()==DecodeType.Decode_SW){
								playDate.prod_qua = Constant.DEFINATION_HD;
							}else{
								playDate.prod_qua = Constant.DEFINATION_HD2;
							}
//							playDate.prod_qua = play_info.getDefination();
//							if(playDate.prod_type==2||playDate.prod_type==3||playDate.prod_type==131){
//								if(json.has("prod_subname")){//旧版android 没有传递该参数
//									playDate.prod_sub_name = json.getString("prod_subname");
//								}else{
//									playDate.prod_type = -1;
//								}
//							}
							playDate.isOnline = true;
							app.setmCurrentPlayDetailData(playDate);
							app.set_ReturnProgramView(null);
							sendBroadcast(new Intent(Global.ACTION_RECIVE_NEW_PUSH_MOVIE));
							handler.postDelayed(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									startActivity(intent);
								}
							}, 0);
							
						}else{
							handler.sendEmptyMessage(MESSAGE_SHOW_DIALOG);
						}
						updateMovieHistory(push_id);
						break;
					case 6:
//						data = json.getJSONObject("body");
//						PushedMovieDownLoadInfo movieDownLoadInfo = new PushedMovieDownLoadInfo();
//						String push_url = null;
//						try {
//							push_url = Utils.getUrl(data.getString("downurl"));
//						} catch (Exception e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
//						if(push_url == null){
//							Log.e(TAG, "push download url error");
//							return ;
//						}
//						movieDownLoadInfo.setPush_url(push_url);
//						movieDownLoadInfo.setPush_id(data.getInt("id"));
//						String downLoad_url = Utils.getRedirectUrl(push_url);
//						Log.d(TAG, "push download url--->" + push_url);
//						String movie_file_name = Utils.getFileNameforUrl(downLoad_url);
//						for(int i=0; i<Constant.video_dont_support_extensions.length; i++){
//							if(downLoad_url.contains(Constant.video_dont_support_extensions[i])){
////								Log.e(TAG, "not support down load m3u8 !");
//								Utils.showToast(FayeService.this, "本视频不支持下载");
//								return ; 
//							}
//						}
//						for(int i=0; i<Constant.video_dont_download_sign.length; i++){
//							if(downLoad_url.contains(Constant.video_dont_download_sign[i])){
////								Log.e(TAG, "not support down load m3u8 !");
//								Utils.showToast(FayeService.this, "本视频不支持下载");
//								return ; 
//							}
//						}
//						movieDownLoadInfo.setName(data.getString("name"));
//						movieDownLoadInfo.setFile_path(MOVIE_PATH.getAbsolutePath()+ File.separator + movie_file_name);
//						DownloadTask movieTask = new DownloadTask(downLoad_url, MOVIE_PATH.getAbsolutePath(), movie_file_name);
//						movieDownLoadInfo.setTast(movieTask);
//						downloadManager.addTast(movieTask);
//						movieDownLoadInfo.setDownload_state(PushedMovieDownLoadInfo.STATUE_WAITING_DOWNLOAD);
//						movieDownLoadInfo.set_id((int) services.insertMovieDownLoadInfo(movieDownLoadInfo));
//						movieDownLoadInfos.add(movieDownLoadInfo);
//						handler.sendEmptyMessage(MESSAGE_NEW_DOWNLOAD_ADD);
//						updateMovieHistory(movieDownLoadInfo.getPush_id());
//						if(currentMovieInfo==null){
//							currentMovieInfo = movieDownLoadInfo;
//							currentMovieInfo.setDownload_state(PushedMovieDownLoadInfo.STATUE_DOWNLOADING);
//							downloadManager.startTast(movieTask);
//							services.updateMovieDownLoadInfo(currentMovieInfo);
//						}
						break;
					case 10:
					case 2:
						JSONObject json_accept = new JSONObject();
						try {
							json_accept.put("msg_type", 3);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						myClient.sendMessage(json_accept);
						break;
					case 11://百度
						data = json.getJSONObject("body");
						int baidu_push_id = Integer.valueOf(data.getString("id"));
						String baidu_time_token = data.getString("time");
//						long time = System.currentTimeMillis() - Long.valueOf(data.getString("time"));
//						Log.d(TAG, "time ---->" + time);
//						if(time>TIME_OUT){
//							updateMovieHistory(push_id);
//							return ;
//						}
						if(services.hasMoviePushHistory(baidu_time_token)!=null){
							updateMovieHistory(baidu_push_id);
							return ;
						}
//						long time_1 = System.currentTimeMillis() - Long.valueOf(data.getString("time"));
//						Log.d(TAG, "time ---->" + time_1);
//						if(time_1>TIME_OUT){
//							updateMovieHistory(baidu_push_id);
//							return ;
//						}
						push_type = 1;
						pincode_md5 = data.getString("md5_code");
//						String baidu_play_url = DesUtils.decode(Constant.DES_KEY, data.getString("downurl"));
						String baidu_play_url = data.getString("downurl");
						String baidu_push_url = data.getString("playurl");
						Log.d(TAG, "baidu_play_url  -> " + baidu_play_url);
						
						play_info = services.hasMoviePlayHistory(MoviePlayHistoryInfo.PLAY_TYPE_ONLINE, baidu_push_url);
						if(play_info == null){
							play_info = new MoviePlayHistoryInfo();
//							play_info.setDownload_url(movie_play_url);
							play_info.setName(Utils.getBaiduName(DesUtils.decode(Constant.DES_KEY, baidu_play_url)));
							Log.d(TAG, "name ---->" + play_info.getName());
							play_info.setPush_id(baidu_push_id);
							play_info.setPush_url(baidu_push_url);
							play_info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_BAIDU);
							play_info.setRecivedDonwLoadUrls(baidu_play_url);
//							play_info.setId((int)services.insertMoviePlayHistory(play_info));
							play_info.setDefination(Constant.DEFINATION_HD2);
							play_info.setCreat_time(System.currentTimeMillis());
							play_info.setTime_token(baidu_time_token+",");
							play_info.setId((int)services.insertMoviePlayHistory(play_info));
						}else{
							play_info.setDefination(Constant.DEFINATION_HD2);
							play_info.setName(data.getString("name"));
							play_info.setRecivedDonwLoadUrls(data.getString("downurl"));
							play_info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_ONLINE);
							if(play_info.getTime_token()==null){
								play_info.setTime_token("");
							}
							play_info.setTime_token(play_info.getTime_token() + baidu_time_token+",");
							services.updateMoviePlayHistory(play_info);
						}
						if(PreferencesUtils.getPincodeMd5(FayeService.this)!=null
								&&(PreferencesUtils.getPincodeMd5(FayeService.this).equals(pincode_md5)||PreferencesUtils.getToken(FayeService.this).equals(pincode_md5))){
//							if(baidu_play_url.startsWith("bdhd")){
								Intent intent = new Intent(FayeService.this,PlayBaiduActivity.class);
								intent.putExtra("url", baidu_play_url);
								intent.putExtra("name", play_info.getName());
								intent.putExtra("push_url", play_info.getPush_url());
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
//							}
						}else{
							handler.sendEmptyMessage(MESSAGE_SHOW_DIALOG);
						}
						
						updateMovieHistory(baidu_push_id);
						break;
					default:
						break;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
	}
}
