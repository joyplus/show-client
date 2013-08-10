package com.joyplus.tvhelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.joyplus.tvhelper.entity.MoviePlayHistoryInfo;
import com.joyplus.tvhelper.utils.PackageUtils;
import com.joyplus.tvhelper.utils.Utils;


public class PlayBaiduActivity extends Activity {
	
	private MoviePlayHistoryInfo play_info;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String url = getIntent().getStringExtra("url");
		startPlayer(url);
//		setContentView(R.layout.activity_playbaidu);
		
	}
	
private void startPlayer(String url){
		
		String[] str = url.split("|");
		String name = null;
		if(str.length>=3){
			name = str[2];
		}
		
		if(isBaiduInstalled()){
			Intent localIntent = new Intent("com.baidu.search.video");
//			Intent localIntent = new Intent("com.baidu.player");
//		    localIntent.putExtra("title", "爱爱囧事_DVDscr国语中字.rmvb|");
		    localIntent.putExtra("title", name);
//		    localIntent.putExtra("refer", "http://www.77vcd.com/Drama/shiershengxiao/");
//		    localIntent.putExtra("bdhdurl", "bdhd://199202767|7218455282C420D033467A75EBCCCF5D|小夫妻时代01.HDTV.rmvb|");
//		    localIntent.putExtra("bdhdurl", "bdhd://423797339|F438C1DF87CADAB226828D0F95F9E698|爱爱囧事_DVDscr国语中字.rmvb|");
		    localIntent.putExtra("bdhdurl", url);
		    localIntent.setClassName("com.baidu.video", "com.baidu.video.ui.ThirdInvokeActivtiy");
//		    localIntent.setClassName("com.baidu.video.pad", "com.baidu.video.player.PlayerActivity");
//		    startActivity(localIntent.addFlags(131072));
		    startActivity(localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}else{
			AlertDialog.Builder tDialog = new AlertDialog.Builder(this);
			tDialog.setTitle("安装提示");
			tDialog.setMessage("该视频需要百度影音支持播放，是否安装百度影音播放器");
			tDialog.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Utils.retrieveApkFromAssets(PlayBaiduActivity.this, "baidushipin_1040402251.apk");
							finish();
						}
					});

			tDialog.setNegativeButton(
					"取消",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});

			tDialog.show();
		}
	}

	public boolean isBaiduInstalled(){
	//	return PackageUtils.isInstalled(this, "com.baidu.video")||PackageUtils.isInstalled(this, "com.baidu.video.pad");
		return PackageUtils.isInstalled(this, "com.baidu.video");
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		finish();
	}
}
