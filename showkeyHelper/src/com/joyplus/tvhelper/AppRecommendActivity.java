package com.joyplus.tvhelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.tvhelper.adapter.AppRecommendAdapter;
import com.joyplus.tvhelper.entity.ApkDownloadInfoParcel;
import com.joyplus.tvhelper.entity.ApkInfo;
import com.joyplus.tvhelper.entity.AppRecommendInfo;
import com.joyplus.tvhelper.entity.TvLiveInfo;
import com.joyplus.tvhelper.entity.service.AppRecommendView;
import com.joyplus.tvhelper.utils.Constant;
import com.joyplus.tvhelper.utils.Global;
import com.joyplus.tvhelper.utils.PackageUtils;
import com.joyplus.tvhelper.utils.Utils;

public class AppRecommendActivity extends Activity {
	
	public static final String TAG = "AppRecommendActivity";
	
//	private List<AppRecommendInfo> list = new ArrayList<AppRecommendInfo>();
	
//	private int[] egAppIds = {R.drawable.app_bg_1,R.drawable.app_bg_2,R.drawable.app_bg_3,
//							   R.drawable.app_bg_4,R.drawable.app_bg_5,R.drawable.app_bg_6,
//			                   R.drawable.app_bg_7,R.drawable.app_bg_8};
	
	private GridView gridView;
	private TextView downloadTv;
	private AppRecommendAdapter adapter;
	
	private SparseArray<View> sparseArrayView = new SparseArray<View>();
	private int preSelectedIndex = -1;
	
	private List<ApkInfo> apkLists = new ArrayList<ApkInfo>();
	private List<AppRecommendInfo> serviceList = new ArrayList<AppRecommendInfo>();
	
	private MyApp app;
	private AQuery aq;
	
	private FrameLayout flGv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_app_recommend);
		
		app = (MyApp) getApplication();
		aq = new AQuery(this);
		
		gridView = (GridView) findViewById(R.id.gv);
		downloadTv = (TextView) findViewById(R.id.tv_download_bg);
		
		apkLists = PackageUtils.getInstalledApkInfos(this);
		
		gridView.setNextFocusUpId(R.id.bt_back);
		flGv = (FrameLayout) findViewById(R.id.fl_gv);
		
		initListener();
		
		adapter = new AppRecommendAdapter(this,aq, serviceList);
		gridView.setAdapter(adapter);
		
		getAppRecommendServiceData();
		
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
					 
					 serviceList.get(i).setInstalled(true);
					 adapter.notifyDataSetChanged();
					 return;
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
	
	private void updateList(){
		
		for(int i=0;i<serviceList.size();i++){
			
			String servicePackageName = serviceList.get(i).getPackage_name();
			for(ApkInfo apkInfo:apkLists){
				
				String localPackageName = apkInfo.getPackageName();
				if(servicePackageName.equals(localPackageName)){
					
					serviceList.get(i).setInstalled(true);
				}
			}
		}
		
		adapter.notifyDataSetChanged();
	}
	
	private void initListener(){
		
		gridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				if(view == null) {
					
					return;
				} else {
					
					sparseArrayView.put(position, view);
				}
				
				AppRecommendInfo info = serviceList.get(position);
				
				
//				setStartDownLoadVisible(view, true,info.isInstalled());
				
				preSelectedIndex = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		gridView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				
				if(!hasFocus){
					
					if(preSelectedIndex != -1) {
						
						View view = sparseArrayView.get(preSelectedIndex);
						if(view != null) {
							
//							setStartDownLoadVisible(v, false,false);
						}
					}
					
				}
			}
		});
		
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				AppRecommendInfo info = serviceList.get(position);
				if(!info.isInstalled()){
					
					//进行下载
					ApkDownloadInfoParcel infoParcel = new ApkDownloadInfoParcel();
					infoParcel.setApk_url(info.getApk_url());
					infoParcel.setApp_name(info.getApp_name());
					infoParcel.setIcon_url(info.getIcon_url());
					infoParcel.setMd5(info.getMd5());
					infoParcel.setVersion(info.getVersion_name());
					infoParcel.setPackage_name(info.getPackage_name());
					
//					infoParcel.setApk_url("http://upgrade.joyplus.tv/joyplustv/joyplustv.apk");
//					infoParcel.setApp_name("悦视频");
//					infoParcel.setIcon_url("");
//					infoParcel.setMd5("");
//					infoParcel.setVersion("");
//					infoParcel.setPackage_name("");
					Intent downloadApkIntent  = new Intent(Global.ACTION_NEW_APK_DWONLOAD);
					downloadApkIntent.putExtra("new_apk_download", infoParcel);
					sendBroadcast(downloadApkIntent);
					startActivity(new Intent(AppRecommendActivity.this,ManagePushApkActivity.class));
				}else {
					
					Utils.showToast(AppRecommendActivity.this, "已经安装");
				}
			}
		});
	}
	
	private void setStartDownLoadVisible(View v,boolean isVisible,boolean isInstall) {
		
		if( v == null) {
			
			return;
		}
//		
//		Log.i(TAG, "x:" + v.getX() + " y:" + v.getY());
//		
//		Log.i(TAG, "isVisible--->x:" + v.getX() + " y:" + v.getY()
//				+ " w:" + v.getWidth() + " h:"+ v.getHeight());
////		downloadTv.setVisibility(View.VISIBLE);
//		downloadTv.postInvalidate();
//		downloadTv.layout((int)v.getX(), (int)(v.getY()+v.getHeight()/5 * 4), (int)(v.getX() + v.getWidth()), (int)(v.getY() + v.getHeight()));
//
//
//		downloadTv.forceLayout();
//		downloadTv.requestLayout();
//
//		
//		Log.i(TAG, "downloadTv--->x:" + downloadTv.getX() + " y:" + downloadTv.getY()
//				+ " w:" + downloadTv.getWidth() + " h:"+ downloadTv.getHeight());
//		
////		if(isInstall){
////			
////			downloadTv.setText("已安装");
////		}else{
////			
////			downloadTv.setText("立即下载");
////		}
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
	
	private void getAppRecommendServiceData(){
		
		String url = Constant.TV_LIVING_BASE_URL + "/top_app";
		getServiceData(url, "initAppRecommendServiceData");
	}
	
	public void initAppRecommendServiceData(String url, JSONObject json, AjaxStatus status) {

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
			AppRecommendView appRecommendView = mapper.readValue(json.toString(),
					AppRecommendView.class);

			if(serviceList != null ){
				
				serviceList.clear();
			}
			if(appRecommendView != null && appRecommendView.resources != null) {
				
				for(int i=0;i<appRecommendView.resources.length;i++){
					
					AppRecommendInfo info = new AppRecommendInfo();
					info.setApk_url(appRecommendView.resources[i].apk_url);
					info.setApp_name(appRecommendView.resources[i].app_name);
					
					info.setIcon_url(appRecommendView.resources[i].icon_url);
					info.setPic_url(appRecommendView.resources[i].pic_url);
					info.setMd5(appRecommendView.resources[i].md5);
					info.setPackage_name(appRecommendView.resources[i].package_name);
					info.setVersion_name(appRecommendView.resources[i].version_name);
					info.setVersion_code(appRecommendView.resources[i].version_code);
					info.setApk_size(appRecommendView.resources[i].apk_size);
					Log.d(TAG, "info--->" + info.toString());
					serviceList.add(info);
				}
			}
			updateList();
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

}
