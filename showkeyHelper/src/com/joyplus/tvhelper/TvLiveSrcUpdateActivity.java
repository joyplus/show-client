package com.joyplus.tvhelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.GridView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.tvhelper.adapter.TvLiveSrcUpdateAdapter;
import com.joyplus.tvhelper.entity.ApkDownloadInfoParcel;
import com.joyplus.tvhelper.entity.ApkInfo;
import com.joyplus.tvhelper.entity.TvLiveInfo;
import com.joyplus.tvhelper.entity.service.TvLiveViews;
import com.joyplus.tvhelper.utils.Constant;
import com.joyplus.tvhelper.utils.Global;
import com.joyplus.tvhelper.utils.PackageUtils;
import com.joyplus.tvhelper.utils.Utils;

public class TvLiveSrcUpdateActivity extends Activity {
	
	public static final String TAG = "TvLiveSrcUpdateActivity";
	
	public static final int STRAT_DOWNLOAD_FILE = 0;
	public static final int DOWNLOAD_FILES_SUCESS = STRAT_DOWNLOAD_FILE+1;
	
	public static final String HAIMEIDI_Q_FILE = "/Diytvlist";
	
	private List<TvLiveInfo> list = new ArrayList<TvLiveInfo>();
	private List<TvLiveInfo> serviceList = new ArrayList<TvLiveInfo>();
	private List<String> downloadFileUrls = new ArrayList<String>();
	private GridView gridView;
	private TvLiveSrcUpdateAdapter adapter;
	
	private List<ApkInfo> apkLists = new ArrayList<ApkInfo>();
	
	private MyApp app;
	private AQuery aq;
	
	private File tempStoreTvLivingFileDir;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
//			super.handleMessage(msg);
			
			switch (msg.what) {
			case DOWNLOAD_FILES_SUCESS://下载完成
				//对比文件
				setTvLivingStaus();
				break;

			default:
				break;
			}
		}
		
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_tv_live_src_update);
		
		app = (MyApp) getApplication();
		aq = new AQuery(this);
		
		tempStoreTvLivingFileDir = new File(Constant.TV_LIVING_FILE_PATH);
		if(!tempStoreTvLivingFileDir.exists()){
			
			tempStoreTvLivingFileDir.mkdirs();
		}
		
		gridView = (GridView) findViewById(R.id.gridview);
		
//		for(int i=0;i< 10;i++) {
//			
//			TvLiveInfo info = new TvLiveInfo();
//			list.add(info);
//		}
		
		initListener();
		
		apkLists = PackageUtils.getInstalledApkInfos(this);
		getTvLivingServiceData();
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		filter.addDataScheme("package");
		this.registerReceiver(reciver, filter);
		
	}
	
	private BroadcastReceiver reciver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			Log.d("TAG", "--------------------------> app installed");
			 String packageName = intent.getData().getEncodedSchemeSpecificPart();
			 for(int i=0;i<serviceList.size();i++){
				 
				 if(packageName.equals(serviceList.get(i).getPackage_name())){
					 
					 apkLists = PackageUtils.getInstalledApkInfos(TvLiveSrcUpdateActivity.this);
					 for(ApkInfo info:apkLists){
						 
						 if(packageName.equals(info.getPackageName())){
							 
//							 list.add(serviceList.get(i));
							 list.get(i).setInstall(true);
							 setTvLivingStaus();
							 adapter.notifyDataSetChanged();
							 return; 
						 }
					 }
				 }
			 }
			
		}
		
	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(reciver);
		super.onDestroy();
	}
	
	private void initListener(){
		
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				if(list != null && list.size() > 0){
					
					TvLiveInfo info = list.get(position);
					Log.i(TAG, "setOnItemClickListener---->" + info.isInstall());
					if(!info.isInstall()){
						
						ApkDownloadInfoParcel infoParcel = new ApkDownloadInfoParcel();
						infoParcel.setApk_url(info.getApk_url());
						infoParcel.setApp_name(info.getApp_name());
						infoParcel.setIcon_url(info.getIcon_url());
						infoParcel.setMd5(info.getMd5());
						infoParcel.setVersion(info.getVersion());
						infoParcel.setPackage_name(info.getPackage_name());
						Intent downloadApkIntent  = new Intent(Global.ACTION_NEW_APK_DWONLOAD);
						downloadApkIntent.putExtra("new_apk_download", infoParcel);
						sendBroadcast(downloadApkIntent);
						startActivity(new Intent(TvLiveSrcUpdateActivity.this,ManagePushApkActivity.class));
					} else {
						
						switch (info.getStatus()) {
						case 0:
							
							break;
						case 1://点击更新
							List<File> dstListFile = info.getDstFileLists();
							List<File> srcListFile = info.getSrcFileLists();
							
							if(dstListFile.size() == srcListFile.size()){
								
								for(int i=0;i<dstListFile.size();i++){
									
									File file = dstListFile.get(i);
									if(file != null && srcListFile.get(i)!= null
											&& srcListFile.get(i).exists()){
										
										File parentFile = file.getParentFile();
										if(parentFile != null){
											
											if(!parentFile.exists()){
												
												parentFile.mkdirs();
											}
										}
										
										if(!file.exists()){
											
											try {
												file.createNewFile();
												
												Utils.copyFile(srcListFile.get(i),file );
												info.setStatus(TvLiveInfo.NEWS);
												adapter.notifyDataSetChanged();
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
									}
								}
							}
							

							break;

						default:
							break;
						}
					}
				}
			}
		});
	}
	
	private void setTvLivingStaus() {

		for (int i = 0; i < list.size(); i++) {

			TvLiveInfo tvLiveInfo = list.get(i);
			String[] fileNames = tvLiveInfo.getFileNames();
			if (fileNames != null && fileNames.length > 0) {

				List<File> srcFileList = new ArrayList<File>();
				for (int j = 0; j < fileNames.length; j++) {

					String fileName = fileNames[j];
					if (fileName != null && !fileName.equals("")) {

						File file = new File(tempStoreTvLivingFileDir, fileName);
						if (file.exists()) {

							srcFileList.add(file);
						}
					}
				}
				tvLiveInfo.setSrcFileLists(srcFileList);//保存临时文件
				if (srcFileList.size() < fileNames.length) {

					if(tvLiveInfo.isInstall()){
						
						tvLiveInfo.setStatus(TvLiveInfo.NEWS);
					}
					
				} else {

					// 获取fileList中所有文件大小总和
					long listFileTotalSize = Utils
							.getTotalSize4ListFiles(srcFileList);
					File dstDir;
					if (tvLiveInfo.getPackage_name().equals("dfdf")) {// 海美迪Q播放器包名

						dstDir = new File(
								Environment.getExternalStorageDirectory()
										+ HAIMEIDI_Q_FILE);
					} else {

						dstDir = Environment.getExternalStorageDirectory();

					}

					if (dstDir.exists() && dstDir.isDirectory()) {

						List<File> dstFileList = Utils.getListFile4FileNames(
								dstDir, fileNames);
						tvLiveInfo.setDstFileLists(dstFileList);//存储目的文件
						long fileTotalSize = Utils
								.getTotalSize4ListFiles(dstFileList);
						if (listFileTotalSize > fileTotalSize) {

							if(tvLiveInfo.isInstall()){
								
								tvLiveInfo.setStatus(TvLiveInfo.UPDATE);
							}
						} else {

							if(tvLiveInfo.isInstall()){
								
								tvLiveInfo.setStatus(TvLiveInfo.NEWS);
							}
						}
					} else {

						if(tvLiveInfo.isInstall()){
							
							tvLiveInfo.setStatus(TvLiveInfo.NEWS);
						}
					}
				}
			}
		}
		Log.i(TAG, "list.size-->"+ list.size());
		adapter = new TvLiveSrcUpdateAdapter(this, list,aq);
		gridView.setAdapter(adapter);
	}
	
	private void getLivingUpdateList(){
		if(list != null){
			list.clear();
		}
		
		if(downloadFileUrls != null){
			
			downloadFileUrls.clear();
		}
		
		for(TvLiveInfo tvLiveInfo:serviceList){
			

			if(tvLiveInfo.getPackage_name()!= null
					&& !tvLiveInfo.getPackage_name().equals("")){
				boolean isSame = false;
				for(ApkInfo apkInfo:apkLists){
					
					if(apkInfo.getPackageName()!= null && 
							apkInfo.getPackageName().equals(tvLiveInfo.getPackage_name())){

						tvLiveInfo.setInstall(true);
						list.add(tvLiveInfo);
						isSame = true;
						if(tvLiveInfo.getFile_urls() != null){
							for(int i=0;i<tvLiveInfo.getFile_urls().length;i++){
								
								if(tvLiveInfo.getFile_urls()[i] != null 
										&& URLUtil.isNetworkUrl(tvLiveInfo.getFile_urls()[i])){
									downloadFileUrls.add(tvLiveInfo.getFile_urls()[i]);
								}
							}
							
						}
					}
				}
				if(!isSame && !tvLiveInfo.isIs_specific_app()){
				
					tvLiveInfo.setInstall(false);
					list.add(tvLiveInfo);
					
					if(tvLiveInfo.getFile_urls() != null){
						for(int i=0;i<tvLiveInfo.getFile_urls().length;i++){
							
							if(tvLiveInfo.getFile_urls()[i] != null 
									&& URLUtil.isNetworkUrl(tvLiveInfo.getFile_urls()[i])){
								downloadFileUrls.add(tvLiveInfo.getFile_urls()[i]);
							}
						}
						
					}
				}
			}

		}
		
		//list 完成加载 可以setAdapter
		
		//下载直播源文件
		if(downloadFileUrls.size() > 0){
			
			MyApp.pool.execute(new TvLivingDownloadTask(downloadFileUrls));
		}
	}
	
	protected void getServiceData(String url, String interfaceName) {
		// TODO Auto-generated method stub

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.SetHeader(app.getHeaders());
		cb.url(url).type(JSONObject.class).weakHandler(this, interfaceName);

		Log.d(TAG, url);
		Log.d(TAG, "header appkey" + app.getHeaders().get("app_key"));

		aq.ajax(cb);
	}
	
	private void getTvLivingServiceData(){
		
		String url = Constant.TV_LIVING_BASE_URL + "/living_res";
		getServiceData(url, "initTvLivingServiceData");
	}
	
	public void initTvLivingServiceData(String url, JSONObject json, AjaxStatus status) {

		if (status.getCode() == AjaxStatus.NETWORK_ERROR || json == null) {
			Utils.showToast(this,
					getResources().getString(R.string.networknotwork));

			return;
		}

		if (json == null || json.equals(""))
			return;

		Log.d(TAG, "initTvLivingServiceData = " + json.toString());
		ObjectMapper mapper = new ObjectMapper();
		try {
			TvLiveViews tvLiveViews = mapper.readValue(json.toString(),
					TvLiveViews.class);

			if(serviceList != null ){
				
				serviceList.clear();
			}
			if(tvLiveViews != null && tvLiveViews.resources != null) {
				
				for(int i=0;i<tvLiveViews.resources.length;i++){
					
					TvLiveInfo info = new TvLiveInfo();
					info.setApk_url(tvLiveViews.resources[i].apk_url);
					info.setApp_name(tvLiveViews.resources[i].app_name);
					
					if(tvLiveViews.resources[i].file_urls != null 
							&& tvLiveViews.resources[i].file_urls.length > 0){
						
						String[] urls = new String[tvLiveViews.resources[i].file_urls.length];
						String[] fileNames = new String[tvLiveViews.resources[i].file_urls.length];
						for(int j=0;j<urls.length;j++){
							
							urls[j] = tvLiveViews.resources[i].file_urls[j].file_url;
							fileNames[j]=Utils.getFilenameFromUrl(urls[j]);
						}
						info.setFile_urls(urls);
						info.setFileNames(fileNames);
					}
					
					info.setIcon_url(tvLiveViews.resources[i].icon_url);
					if(tvLiveViews.resources[i].is_specific_app.equals("0")){
						info.setIs_specific_app(true);
					}else if(tvLiveViews.resources[i].is_specific_app.equals("1")){
						info.setIs_specific_app(false);
					}
					info.setMd5(tvLiveViews.resources[i].md5);
					info.setPackage_name(tvLiveViews.resources[i].package_name);
					info.setVersion(tvLiveViews.resources[i].version_name);
					info.setStatus(TvLiveInfo.NEWS);
					Log.d(TAG, "info--->" + info.toString());
					serviceList.add(info);
				}
			}
			getLivingUpdateList();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class TvLivingDownloadTask implements Runnable {
		
		private List<String> list;
		
		public TvLivingDownloadTask(List<String> list){
			
			this.list = list;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			URL url = null;
			InputStream is = null;
			FileOutputStream fos = null;
			
			if(list != null && list.size() > 0){
				
				for(int i=0;i<list.size();i++){
					
					String urlStr = list.get(i);
					String filename = Utils.getFilenameFromUrl(urlStr);
					
					if(urlStr != null && URLUtil.isNetworkUrl(urlStr)){
						
						File file = new File(tempStoreTvLivingFileDir, filename);
						if(!file.exists()){
							
							try {
								file.createNewFile();
								url = new URL(Uri.encode(urlStr,"UTF-8").replaceAll("%3A", ":").replaceAll("%2F", "/").replaceAll("%3F", "?"));
								HttpURLConnection connection = (HttpURLConnection) url.openConnection();
								connection.setDoInput(true);
								connection.connect();
								is =  connection.getInputStream();
								
								fos = new FileOutputStream(file);
								
								byte[] b = new byte[4096];
								int len = 0;
								 while ((len = is.read(b, 0, 4096)) != -1){
									 
									 fos.write(b, 0, len);
									 fos.flush();
								 }
								 fos.flush();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}finally{
								
								if(fos != null){
									
									try {
										fos.close();
										is.close();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						}
						
					}
				}
				
				File[] files = tempStoreTvLivingFileDir.listFiles();
				for(int i=0;i<files.length;i++){
					
					Log.i(TAG, "files--->" + files[i].getAbsolutePath());
				}
				handler.sendEmptyMessage(DOWNLOAD_FILES_SUCESS);
			}
		}
	}

}
