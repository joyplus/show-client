package com.joypuls.tvhelper.faye;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.joypuls.tvhelper.DialogActivity;
import com.joypuls.tvhelper.db.DBServices;
import com.joypuls.tvhelper.db.PushedApkDownLoadInfo;
import com.joypuls.tvhelper.download.DownLoadTask;
import com.joypuls.tvhelper.entity.ApkInfo;
import com.joypuls.tvhelper.faye.FayeClient.FayeListener;
import com.joypuls.tvhelper.utils.Global;
import com.joypuls.tvhelper.utils.HttpTools;
import com.joypuls.tvhelper.utils.PackageUtils;
import com.joypuls.tvhelper.utils.PreferencesUtils;
import com.lenovo.lsf.installer.PackageInstaller;


public class FayeService extends Service implements FayeListener ,Observer{

	private static final String TAG = "FayeService";
	
	private boolean isNeedReconnect = false;
	
	public static final int MESSAGE_DOWNLOAD_GET_FILESIE_SUCCESS = 0;
	public static final int MESSAGE_DOWNLOAD_CREAT_FILE_SUCCESS = 1;
	public static final int MESSAGE_DOWNLOAD_PROGRESS_CHANGED = 2;
	public static final int MESSAGE_DOWNLOAD_COMPLETE = 3;
	public static final int MESSAGE_DOWNLOAD_FAILE = 4;
	public static final int MESSAGE_SHOW_DIALOG = 101;
	public static final int MESSAGE_NEW_DOWNLOAD_ADD = 102;
	public static final int MESSAGE_APK_INSTALLED_PROGRESS = 102;
	
	private String channel;
	private FayeClient myClient;
	private DBServices services;
	private PushedApkDownLoadInfo currentDownLoadInfo; 
	public static List<PushedApkDownLoadInfo> infolist = new ArrayList<PushedApkDownLoadInfo>();
	
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
				int push_id = intent.getIntExtra("push_id", 0);
				if(currentDownLoadInfo!=null && push_id == currentDownLoadInfo.getPush_id()){
					Log.d(TAG, currentDownLoadInfo.getName() + "------------------pause");
					currentDownLoadInfo.getTast().stopLoad();
					currentDownLoadInfo.setDownload_state(2);
					services.updatePushedApkInfo(currentDownLoadInfo);
					startNextDownLoad(infolist.indexOf(currentDownLoadInfo));
				}
				
			}else if(Global.ACTION_DOWNLOAD_CONTINUE.equals(action)){
				int push_id = intent.getIntExtra("push_id", 0);
				PushedApkDownLoadInfo info = null;
				for(int i=0; i<infolist.size(); i++){
					if(infolist.get(i).getPush_id()==push_id){
						info = infolist.get(i);
					}
				}
				if(info != null){
					info.setDownload_state(0);
					services.updatePushedApkInfo(info);
					if(currentDownLoadInfo==null){
						Log.d(TAG, "currentDownLoadInfo is null----------------app->" +info.getName()+" start down load");
						currentDownLoadInfo = info;
						DownLoadTask tast = new DownLoadTask(FayeService.this, info, handler); 
						info.setTast(tast);
						new Thread(tast).start();
						Intent startIntent = new Intent(Global.ACTION_DOWNLOAD_START);
						startIntent.putExtra("push_id", info.getPush_id());
						sendBroadcast(startIntent);
					}else{
						infolist.remove(info);
						int index = infolist.indexOf(currentDownLoadInfo);
						Log.d(TAG, "currentDownLoadInfo is not null----------------index + 1 = " + (index+1));
						infolist.add(index+1, info);
					}
				}
			}else if(Global.ACTION_DELETE_DOWNLOAD.equals(action)){
				int push_id = intent.getIntExtra("push_id", 0);
				PushedApkDownLoadInfo info = null;
				for(int i=0; i<infolist.size(); i++){
					if(infolist.get(i).getPush_id()==push_id){
						info = infolist.get(i);
					}
				}
				if(info != null){
					infolist.remove(info);
					services.deleteDownLoadInfo(info);
					if(info.getFile_path()!=null){
						File f = new File(info.getFile_path());
						if(f.exists()){
							f.delete();
						}
					}
					
				}
			}else if(Global.ACTION_PINCODE_REFRESH.equals(action)){
				myClient.disconnectFromServer();
				isNeedReconnect = false;
				stopSelf();
			}
		}
	};
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what>100){
				switch (msg.what) {
				case MESSAGE_SHOW_DIALOG:
					Intent intent = new Intent(FayeService.this,DialogActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					break;
				case MESSAGE_NEW_DOWNLOAD_ADD:
					Intent dowanlaodAddIntent = new Intent(Global.ACTION_APK_RECIVED);
					sendBroadcast(dowanlaodAddIntent);
					break;
				}
			}else{
				int pushedId = msg.arg1;
				PushedApkDownLoadInfo info = null;
				if(services == null){
					services = DBServices.getInstance(FayeService.this);
				}
				for(int i=0; i<infolist.size(); i++){
					if(infolist.get(i).getPush_id()==pushedId){
						info = infolist.get(i);
					}
				}
				switch (msg.what) {
				case MESSAGE_DOWNLOAD_CREAT_FILE_SUCCESS: 
//					services.updatePushedApkInfo(info);
					Log.d(TAG, "progress ---------------------" + info.getFile_path());
					break;
				case MESSAGE_DOWNLOAD_GET_FILESIE_SUCCESS:
//					services.updatePushedApkInfo(info);
					Intent fileSizeIntent = new Intent(Global.ACTION_DOWNL_GETSIZE_SUCESS);
					fileSizeIntent.putExtra("push_id", info.getPush_id());
					fileSizeIntent.putExtra("file_size", info.getFileSize());
					sendBroadcast(fileSizeIntent);
					break;
				case MESSAGE_DOWNLOAD_PROGRESS_CHANGED:
//					services.updatePushedApkInfo(info);
					Log.d(TAG, "progress ---------------------" + (msg.arg2*100)/info.getFileSize());
					if(info.getDownload_state()==1){
						Intent progressIntent = new Intent(Global.ACTION_DOWNLOAD_PROGRESS);
						progressIntent.putExtra("push_id", info.getPush_id());
						progressIntent.putExtra("progress", (info.getCompeleteSize()*100)/info.getFileSize());
						sendBroadcast(progressIntent);
					}
					 
					break;
				case MESSAGE_DOWNLOAD_COMPLETE:
//					services.updatePushedApkInfo(info);
					Log.d(TAG, "down load success ---------------------" + (info.getCompeleteSize()*100)/info.getFileSize() + "id == " + info.getPush_id());
					PackageInstaller pi = new PackageInstaller(FayeService.this);
				    pi.addObserver(FayeService.this);
//				     pi.instatll("/sdcard/joyplus.apk", "com.joyplus");
				    info.setDownload_state(3);
				    services.updatePushedApkInfo(info);
				    ApkInfo apkInfo = PackageUtils.getUnInstalledApkInfo(FayeService.this, currentDownLoadInfo.getFile_path());
				    if(apkInfo!=null){
				    	info.setIcon(apkInfo.getDrawble());
					    info.setPackageName(apkInfo.getPackageName());
				    }
				    pi.instatll(info.getFile_path(), info.getPackageName());
				    Intent downLoadCompletIntent = new Intent(Global.ACTION_DOWNLOAD_COMPLETE);
				    downLoadCompletIntent.putExtra("push_id", info.getPush_id());
				    sendBroadcast(downLoadCompletIntent);
					break;
				case MESSAGE_DOWNLOAD_FAILE:
					int location1 = infolist.indexOf(info);
					startNextDownLoad(location1);
//					services.saveApkInfo(info);
					break;
				}
			}
		};
	};

	public static String getMacAdd(Context c) {
		String macAddress = null;
		WifiManager wifiMgr = (WifiManager) c
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
		if (info != null) {
			macAddress = info.getMacAddress();
		}
		return macAddress;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		services = DBServices.getInstance(this);
		channel = "/" + PreferencesUtils.getChannel(this);
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
				infolist = services.GetPushedApklist(infolist);
				String url = Global.serverUrl + "/pushMsgHistories?app_key=" + Global.app_key 
						+ "&mac_address=" + getMacAdd(FayeService.this) 
						+ "&page_num=" + 1
						+ "&page_size=" + 50;
				Log.d(TAG, url);
				String str = HttpTools.get(FayeService.this, url);
				Log.d(TAG, "pushMsgHistories response-->" + str);
				try {
//					JSONObject obj = new JSONObject(str);
//					JSONArray array = obj.getJSONArray("");
					JSONArray array = new JSONArray(str);
					Log.d(TAG, "miss length ---------------------------->" + array.length());
					for(int i=0; i<array.length(); i++){
						JSONObject item = array.getJSONObject(i);
						PushedApkDownLoadInfo info = new PushedApkDownLoadInfo();
						info.setPush_id(item.getInt("id"));
						info.setUrl(item.getString("file_url"));
						info.setName(item.getString("app_name"));
						if(!services.isHasPushedApk(info)){
							services.saveApkInfo(info);
							updateHistory(info.getPush_id());
							services.updatePushedApkInfo(info);
							infolist.add(info);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for(int i=0; i<infolist.size(); i++){
					PushedApkDownLoadInfo nextInfo = infolist.get(i);
					if(nextInfo.getDownload_state()<2){
						currentDownLoadInfo = nextInfo;
						DownLoadTask tast = new DownLoadTask(FayeService.this, nextInfo, handler);
						nextInfo.setTast(tast);
						new Thread(tast).start();
						return;
					}
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
					info.setUrl(data.getString("file_url"));
					info.setPush_id(id);
					services.saveApkInfo(info);
					updateHistory(id);
					handler.sendEmptyMessage(MESSAGE_NEW_DOWNLOAD_ADD);
					infolist.add(info);
					if(currentDownLoadInfo==null){
						currentDownLoadInfo = info;
						DownLoadTask tast = new DownLoadTask(FayeService.this, info, handler);
						info.setTast(tast);
						new Thread(tast).start();
					}
					break;
				case 2:
					handler.sendMessage(msg);
					break;

				default:
					break;
				}
				
//				String apkUrl = json.getString("file_url");
//				Intent intent = new Intent(ACTION_APK_RECIVED);
//				intent.putExtra("url", Uri.encode(apkUrl));
//				sendBroadcast(intent);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void startNextDownLoad(int currentIndex){
		Log.d(TAG, "currentIndex------------->" + currentIndex);
		if(currentIndex<infolist.size()-1){
			PushedApkDownLoadInfo nextInfo = infolist.get(currentIndex+1);
			if(nextInfo.getDownload_state()==0){
				currentDownLoadInfo = nextInfo;
				DownLoadTask tast = new DownLoadTask(FayeService.this, nextInfo, handler); 
				nextInfo.setTast(tast);
				new Thread(tast).start();
				Intent intent = new Intent(Global.ACTION_DOWNLOAD_START);
				intent.putExtra("push_id", nextInfo.getPush_id());
				sendBroadcast(intent);
			}else{
				startNextDownLoad(currentIndex+1);
			}
			
		}else{
			currentDownLoadInfo = null;
		}
	}
	
	private void updateHistory(final int id){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String url = Global.serverUrl + "/updateHistory?app_key=" + Global.app_key 
						+ "&mac_address=" + getMacAdd(FayeService.this) 
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
			Log.i("TAG", "packageName=" + b.getString(PackageInstaller.KEY_PACKAGE_NAME));
			Log.i("TAG", "resultCode=" + b.getInt(PackageInstaller.KEY_RESULT_CODE));
			Log.i("TAG", "resultDesc=" + b.getString(PackageInstaller.KEY_RESULT_DESC));
			if(currentDownLoadInfo.getPackageName().equals(b.getString(PackageInstaller.KEY_PACKAGE_NAME))){
				if("INSTALL_SUCCEEDED".equals(b.getString(PackageInstaller.KEY_RESULT_DESC))){
					Intent intent = new Intent(Global.ACTION_DOWNL_INSTALL_SUCESS);
					intent.putExtra("push_id", currentDownLoadInfo.getPush_id());
					if(PreferencesUtils.isautodelete(FayeService.this)){
						if(currentDownLoadInfo!=null&&currentDownLoadInfo.getFile_path()!=null){
							File f = new File(currentDownLoadInfo.getFile_path());
							if(f.exists()){
								f.delete();
							}
						}
					}
					currentDownLoadInfo.setDownload_state(-1);
					sendBroadcast(intent);
				}else{
					Intent intent = new Intent(Global.ACTION_DOWNL_INSTALL_FAILE);
					intent.putExtra("push_id", currentDownLoadInfo.getPush_id());
					currentDownLoadInfo.setDownload_state(4);
					sendBroadcast(intent);
				}

				int location = infolist.indexOf(currentDownLoadInfo);
				services.updatePushedApkInfo(currentDownLoadInfo);
				PushedApkDownLoadInfo  info= currentDownLoadInfo;
				currentDownLoadInfo = null;
				startNextDownLoad(location);
				infolist.remove(info);
			}
		}
	}
	
	
}
