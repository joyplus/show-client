package com.joyplus.tvhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.os.StatFs;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.joyplus.tvhelper.adapter.ApkAdapter;
import com.joyplus.tvhelper.entity.ApkInfo;
import com.joyplus.tvhelper.ui.RoundProgressBar;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.PackageUtils;

public class UninstallAppActivity extends Activity implements OnItemClickListener{
	
	private RoundProgressBar progressBar;
	private GridView gridView;
	private ApkAdapter adapter;
	private Button backBtn;
	private List<ApkInfo> apks = new ArrayList<ApkInfo>(); 
	
	private Handler myHandler = new Handler(){
		
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case 0:
					adapter.notifyDataSetChanged();
					gridView.requestFocus();
					break;
				}
			}
			
	};
	
	private BroadcastReceiver reciver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			updateApps();
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		progressBar = (RoundProgressBar) findViewById(R.id.progressbar);
		gridView = (GridView) findViewById(R.id.gridView);
		backBtn = (Button) findViewById(R.id.back_Button);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		adapter = new ApkAdapter(this, apks);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);
		updateApps();
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		this.registerReceiver(reciver, filter);

	}
	
	private void updateApps(){
		apks = PackageUtils.getUsrInstalledApkInfos(UninstallAppActivity.this, apks);
		for(ApkInfo info : apks){
			try {
				PackageUtils.getInstalledApkSize(this, info.getPackageName(), new PkgSizeObserver(info));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		TextView usedSizeTextView = (TextView) findViewById(R.id.text_usedSize);
		usedSizeTextView.setText("软件安装包:"  + apks.size());
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
		
//		TextView usedSizeTextView = (TextView) findViewById(R.id.text_usedSize);
		TextView unUsedSizeTextView = (TextView) findViewById(R.id.text_unusedSize);
//		usedSizeTextView.setText("已使用空间:"  + PackageUtils.fomartSize(usedSize));
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
//		PackageInstaller pi = new PackageInstaller(UninstallAppActivity.this);
//	     pi.addObserver(this);
////	     pi.instatll("/sdcard/joyplus.apk", "com.joyplus");
//	     pi.uninstall(apks.get(position).getPackageName());
		Uri packageURI=Uri.parse("package:"+apks.get(position).getPackageName());//xx是包名
        Intent intent=new Intent(Intent.ACTION_DELETE,packageURI);
        startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		getSize();
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(reciver);
		super.onDestroy();
	}
	
	
	class PkgSizeObserver extends IPackageStatsObserver.Stub{

		private ApkInfo apkInfo;
		public PkgSizeObserver(ApkInfo apkInfo) {
			super();
			this.apkInfo = apkInfo;
		}

		
		
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			// TODO Auto-generated method stub
			Log.d("TAG", "size ------- >" + apkInfo.getPackageName() + "--" + pStats.codeSize);
			apkInfo.setSize(pStats.codeSize+pStats.cacheSize+pStats.dataSize);
			myHandler.sendEmptyMessage(0);
		}
		
	}
}
