package com.joyplus.tvhelper;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joyplus.tvhelper.ui.RoundProgressBar;
import com.joyplus.tvhelper.utils.PackageUtils;
import com.umeng.analytics.MobclickAgent;

public class ManageAppActivity extends Activity implements OnClickListener{
	
	private RoundProgressBar progressBar;
	private Button bacBtn;
	private LinearLayout layout1;
	private LinearLayout layout2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_soft_manager);
		progressBar = (RoundProgressBar) findViewById(R.id.progressbar);
		bacBtn = (Button) findViewById(R.id.back_Button);
		layout1 = (LinearLayout) findViewById(R.id.layout1);
		layout2 = (LinearLayout) findViewById(R.id.layout2);
		bacBtn.setOnClickListener(this);
		layout1.setOnClickListener(this);
		layout2.setOnClickListener(this);
		getSize();
		
		layout1.requestFocus();
//		progressBar.setProgress(50);
//		progressBar.startCartoom(10);
	}
	
//	private void getSize() {
//		File path = Environment.getDataDirectory();
//		// 取得sdcard文件路径
//		StatFs statfs = new StatFs(path.getPath());
//		long blocSize = statfs.getBlockSize();
//		long totalBlocks = statfs.getBlockCount();
//		long totleSize = blocSize * totalBlocks; // 计算总容量
//		long availableSize = statfs.getAvailableBlocks()*blocSize; // 获取可用容量
//		
//		File rootPath = Environment.getRootDirectory();
//		StatFs statfs_root = new StatFs(rootPath.getPath());
//		long blocSize_root = statfs_root.getBlockSize();
//		long totalBlocks_root = statfs_root.getBlockCount();
//		long totleSize_root = blocSize_root * totalBlocks_root; // 计算总容量
//		
//		
//		long allTotal = totleSize+totleSize_root;
//		long usedSize = totleSize-availableSize+totleSize_root;
//		
//		TextView usedSizeTextView = (TextView) findViewById(R.id.text_usedSize);
//		TextView unUsedSizeTextView = (TextView) findViewById(R.id.text_unusedSize);
//		usedSizeTextView.setText("已使用空间:"  + PackageUtils.fomartSize(usedSize));
//		unUsedSizeTextView.setText("可使用空间:"  + PackageUtils.fomartSize(availableSize));
//		
////		int progress1 = (int) ((totleSize_root*100)/allTotal);
////		int progress2 = (int) ((usedSize*100)/allTotal);
////		
////		progressBar.setProgress(progress1);
////		progressBar.setSecondaryProgress(progress2);
//		
//		int progress = (int) ((usedSize*100)/allTotal);
//		progressBar.setProgress(progress);
//		
//		
//	}
	
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_Button:
			finish();
			break;
		case R.id.layout1:
			startActivity(new Intent(ManageAppActivity.this, UninstallAppActivity.class));
			break;
		case R.id.layout2:
			startActivity(new Intent(ManageAppActivity.this, ManageApkActivity.class));
			break;
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
//		displayPincode();
		super.onResume();
		
		MobclickAgent.onResume(this);
	}
}
