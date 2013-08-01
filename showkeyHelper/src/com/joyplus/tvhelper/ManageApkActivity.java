package com.joyplus.tvhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joyplus.tvhelper.adapter.ApkAdapter;
import com.joyplus.tvhelper.entity.ApkInfo;
import com.joyplus.tvhelper.ui.RoundProgressBar;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.PackageUtils;

public class ManageApkActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private RoundProgressBar progressBar;
	private GridView gridView;
	private ApkAdapter adapter;
	private List<ApkInfo> apks = new ArrayList<ApkInfo>();

	private Button backButton, deleteButton, cancleButton, editeButton;
	private LinearLayout layout1, layout2;

	private TextView notice_key, notice_action;
//	private boolean isEdite = false;

	private Handler myHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				ApkInfo info = (ApkInfo) msg.obj;
				Log.d("TAG", info.getAppName());
				apks.add(info);
				adapter.notifyDataSetChanged();
				break;
			 case 1:
			 	removeDialog(0);
			 	updateEditButton();
			 break;
			// case 2:
			// String fileName = (String) msg.obj;
			// install(fileName);
			// break;

			}

		};
	};
	
	private BroadcastReceiver reciver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			Log.d("TAG", "--------------------------> app installed");
			for(ApkInfo apk  : apks){
				if(PackageUtils.isInstalled(context, apk.getPackageName(), apk.getVersionCode())){
					apk.setInstalled(true);
				}else{
					apk.setInstalled(false);
				}
			}
			adapter.notifyDataSetChanged();
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apk_manager);
		progressBar = (RoundProgressBar) findViewById(R.id.progressbar);
		gridView = (GridView) findViewById(R.id.gridView);
		layout1 = (LinearLayout) findViewById(R.id.fistBtn_group);
		layout2 = (LinearLayout) findViewById(R.id.secondBtn_group);

		backButton = (Button) findViewById(R.id.back_Button);
		deleteButton = (Button) findViewById(R.id.del_Button);
		cancleButton = (Button) findViewById(R.id.cancel_Button);
		editeButton = (Button) findViewById(R.id.edit_Button);

		backButton.setOnClickListener(this);
		deleteButton.setOnClickListener(this);
		cancleButton.setOnClickListener(this);
		editeButton.setOnClickListener(this);

		notice_key = (TextView) findViewById(R.id.notice_key);
		notice_action = (TextView) findViewById(R.id.notice_action);

//		progressBar.setProgress(50);
		adapter = new ApkAdapter(this, apks);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		filter.addDataScheme("package");
		this.registerReceiver(reciver, filter);
		updateEditButton();
		showDialog(0);
		new Thread(new FindApkTask()).start();
	}

	class FindApkTask implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// FindAllAPKFile(Environment.getExternalStorageDirectory());
			FindAllAPKFile(new File("/mnt"));
			myHandler.sendEmptyMessage(1);
		}
	}

	public void FindAllAPKFile(File file) {
		// 手机上的文件,目前只判断SD卡上的APK文件
		// file = Environment.getDataDirectory();
		// SD卡上的文件目录
		if (file.isFile()) {
			String name_s = file.getName();
			// MyFile myFile = newMyFile();
			String apk_path = null;
			// MimeTypeMap.getSingleton()
			if (name_s.toLowerCase().endsWith(".apk")) {
				apk_path = file.getAbsolutePath();// apk文件的绝对路劲
				Log.d("TAG", "----------------------------->" + apk_path);
				PackageInfo info = PackageUtils.getAppPackageInfo(
						ManageApkActivity.this, apk_path);
				if (info != null) {
					ApkInfo apkInfo = PackageUtils.getUnInstalledApkInfo(
							ManageApkActivity.this, apk_path);
					if(apkInfo!=null){ 
						apkInfo.setVision(info.versionName);
						apkInfo.setVersionCode(info.versionCode);
						if (apkInfo != null) {
							if (PackageUtils.isInstalled(ManageApkActivity.this,
									info.packageName, info.versionCode)) {
								apkInfo.setInstalled(true); 
								Log.d("TAG", apkInfo.getAppName()+"已安装");
							} else {
								apkInfo.setInstalled(false);
							}
							apkInfo.setSize(file.length());
							apkInfo.setFilePath(apk_path);
							Message msg = myHandler.obtainMessage(0);
							msg.obj = apkInfo;
							myHandler.sendMessage(msg);
						}
					}
				}

				// Log.d("TAG",
				// PackageUtils.getUnInstalledAppName(MainActivity.this,
				// apk_path));

			}
			// String apk_app = name_s.substring(name_s.lastIndexOf("."));
		} else {
			File[] files = file.listFiles();
			if (files != null && files.length > 0) {
				for (File file_str : files) {
					if("/mnt/asec".equalsIgnoreCase(file_str.getAbsolutePath())
							||"/mnt/secure".equalsIgnoreCase(file_str.getAbsolutePath())){
					
					}else{
						FindAllAPKFile(file_str);
					}
				}
			}
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_Button:
			finish();
			break;
		case R.id.del_Button:
			
			Iterator<ApkInfo> iterator = null;
			iterator = apks.iterator();  
	         while(iterator.hasNext()) {  
	        	 ApkInfo info = iterator.next();  
	             if(info.getStatue()==2) { 
		            	File f = new File(info.getFilePath());
						if(f.exists()){
							f.delete();
						}
						iterator.remove();  
	             }else{
	            	 info.setStatue(0);
	             }
	               
	         }
	        updateEditButton();
	        layout1.setVisibility(View.VISIBLE);
			layout2.setVisibility(View.GONE);
			notice_key.setText("\t返回");
			notice_action.setText("\t离开");
			adapter.notifyDataSetChanged();
			break;
		case R.id.edit_Button:
			layout1.setVisibility(View.GONE);
			layout2.setVisibility(View.VISIBLE);
			notice_key.setText("\t确定");
			notice_action.setText("\t标记");
			for (int i = 0; i < apks.size(); i++) {
				apks.get(i).setStatue(1);
			}
			gridView.requestFocus();
			adapter.notifyDataSetChanged();
			cancleButton.requestFocus();
			break;
		case R.id.cancel_Button:
			layout1.setVisibility(View.VISIBLE);
			layout2.setVisibility(View.GONE);
			notice_key.setText("\t返回");
			notice_action.setText("\t离开");
			for (int i = 0; i < apks.size(); i++) {
				apks.get(i).setStatue(0);
			}
			gridView.requestFocus();
			adapter.notifyDataSetChanged();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch (apks.get(position).getStatue()) {
		case 0:
			Uri packageURI =Uri.parse("file://"+apks.get(position).getFilePath());
			Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE, packageURI);
			startActivity(intent);
			break;
		case 1:
			apks.get(position).setStatue(2);
			break;
		case 2:
			apks.get(position).setStatue(1);
			break;

		default:
			break;
		}
		adapter.notifyDataSetChanged();
	}

	private void getSize() {
		File path = Environment.getDataDirectory();
		// 取得sdcard文件路径
		StatFs statfs = new StatFs(path.getPath());
		long blocSize = statfs.getBlockSize();
		long totalBlocks = statfs.getBlockCount();
		long totleSize = blocSize * totalBlocks; // 计算总容量
		long availableSize = statfs.getAvailableBlocks()*blocSize; // 获取可用容量
		
		File rootPath = Environment.getRootDirectory();
		StatFs statfs_root = new StatFs(rootPath.getPath());
		long blocSize_root = statfs_root.getBlockSize();
		long totalBlocks_root = statfs_root.getBlockCount();
		long totleSize_root = blocSize_root * totalBlocks_root; // 计算总容量
		
		
		long allTotal = totleSize+totleSize_root;
		long usedSize = totleSize-availableSize+totleSize_root;
		
		TextView usedSizeTextView = (TextView) findViewById(R.id.text_usedSize);
		TextView unUsedSizeTextView = (TextView) findViewById(R.id.text_unusedSize);
		usedSizeTextView.setText("已使用空间:"  + PackageUtils.fomartSize(usedSize));
		unUsedSizeTextView.setText("可使用空间:"  + PackageUtils.fomartSize(availableSize));
		
//		int progress1 = (int) ((totleSize_root*100)/allTotal);
//		int progress2 = (int) ((usedSize*100)/allTotal);
//		
//		progressBar.setProgress(progress1);
//		progressBar.setSecondaryProgress(progress2);
		
		int progress = (int) ((usedSize*100)/allTotal);
		progressBar.setProgress(progress);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getSize();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(reciver);
		super.onDestroy();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case 0:
			ProgressDialog d = ProgressDialog.show(this, null, "正在努力寻找安装包···");
			return d; 
		default:
			break;
		}
		return super.onCreateDialog(id);
	}
	
	private void updateEditButton(){
		if(apks.size()>0){
        	editeButton.setVisibility(View.VISIBLE);
        	gridView.requestFocus();
        }else{
        	editeButton.setVisibility(View.INVISIBLE);
        }
	}
}
