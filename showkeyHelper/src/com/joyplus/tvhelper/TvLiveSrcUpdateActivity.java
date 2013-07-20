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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.GridView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.tvhelper.adapter.TvLiveSrcUpdateAdapter;
import com.joyplus.tvhelper.entity.ApkInfo;
import com.joyplus.tvhelper.entity.TvLiveInfo;
import com.joyplus.tvhelper.entity.service.TvLiveViews;
import com.joyplus.tvhelper.utils.Constant;
import com.joyplus.tvhelper.utils.PackageUtils;
import com.joyplus.tvhelper.utils.Utils;

public class TvLiveSrcUpdateActivity extends Activity {
	
	public static final String TAG = "TvLiveSrcUpdateActivity";
	
	public static final int STRAT_DOWNLOAD_FILE = 0;
	public static final int DOWNLOAD_FILES_SUCESS = STRAT_DOWNLOAD_FILE+1;
	
	private List<TvLiveInfo> list = new ArrayList<TvLiveInfo>();
	private List<TvLiveInfo> serviceList = new ArrayList<TvLiveInfo>();
	private List<String> downloadFileUrls = new ArrayList<String>();
	private GridView gridView;
	private TvLiveSrcUpdateAdapter adapter;
	
	private List<ApkInfo> apkLists = new ArrayList<ApkInfo>();
	
	private MyApp app;
	private AQuery aq;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
//			super.handleMessage(msg);
			
			switch (msg.arg1) {
			case DOWNLOAD_FILES_SUCESS:
				
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
		
		gridView = (GridView) findViewById(R.id.gridview);
		
		for(int i=0;i< 10;i++) {
			
			TvLiveInfo info = new TvLiveInfo();
			list.add(info);
		}
		
		adapter = new TvLiveSrcUpdateAdapter(this, list);
		gridView.setAdapter(adapter);
		
		apkLists = PackageUtils.getInstalledApkInfos(this);
		getTvLivingServiceData();
		
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
					
					tvLiveInfo.setStatus(TvLiveInfo.DOWNLOAD);
					list.add(tvLiveInfo);
				}
			}

		}
		
		if(downloadFileUrls.size() > 0){
			
			MyApp.pool.execute(new TvLivingDownloadTask(downloadFileUrls));
		}
	}
	
	protected void getServiceData(String url, String interfaceName) {
		// TODO Auto-generated method stub

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, interfaceName);

		cb.SetHeader(app.getHeaders());

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
					if(tvLiveViews.resources[i].is_specific_app.equals("1")){
						info.setIs_specific_app(true);
					}else if(tvLiveViews.resources[i].is_specific_app.equals("0")){
						info.setIs_specific_app(false);
					}
					info.setMd5(tvLiveViews.resources[i].md5);
					info.setPackage_name(tvLiveViews.resources[i].package_name);
					info.setVersion(tvLiveViews.resources[i].version);
					info.setStatus(TvLiveInfo.UNKOWN);
					
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
			
			File dir = new File(Constant.TV_LIVING_FILE_PATH);
			if(!dir.exists()){
				
				dir.mkdirs();
			}
			
			if(list != null && list.size() > 0){
				
				for(int i=0;i<list.size();i++){
					
					String urlStr = list.get(i);
					String filename = Utils.getFilenameFromUrl(urlStr);
					
					if(urlStr != null && URLUtil.isNetworkUrl(urlStr)){
						
						File file = new File(dir, filename);
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
				handler.sendEmptyMessage(DOWNLOAD_FILES_SUCESS);
			}
		}
	}

}
