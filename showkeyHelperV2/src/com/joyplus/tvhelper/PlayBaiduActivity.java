package com.joyplus.tvhelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.joyplus.tvhelper.utils.Constant;
import com.joyplus.tvhelper.utils.DesUtils;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.PackageUtils;
import com.joyplus.tvhelper.utils.Utils;
import com.umeng.analytics.MobclickAgent;


public class PlayBaiduActivity extends Activity {
	
	private static final String TAG = "PlayBaiduActivity";
//	private MoviePlayHistoryInfo play_info;
	
	private String url;
//	private String name;
	private String from;
	
	
	private BroadcastReceiver receiver1 = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String packageName = intent.getData().getSchemeSpecificPart();
			if("com.baidu.video".equals(packageName)){
				startPlayer();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		url = DesUtils.decode(Constant.DES_KEY, getIntent().getStringExtra("url"));
		Log.d("PlayBaidu", "url---->" + url);
//		name = getIntent().getStringExtra("name");
		from = getIntent().getStringExtra("push_url");
		IntentFilter filter1 = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		filter1.addDataScheme("package");
		this.registerReceiver(receiver1, filter1);
		startPlayer();
//		setContentView(R.layout.activity_playbaidu);
		
	}
	
private void startPlayer(){
		
//		String[] str = url.split("|");
//		String name = null;
//		if(str.length>=3){
//			name = str[2];
//		}
//	
//		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//		ComponentName cn = manager.getRunningTasks(1).get(0).topActivity;
//		String packageName = cn.getPackageName();
//		Log.d(TAG, "activity----->" + cn.getClassName());
////		08-12 16:08:29.444: D/PlayBaiduActivity(19215): activity----->com.joyplus.tvhelper.PlayBaiduActivity
//
//		Log.d(TAG, "packageName----->" + packageName);
//	
//		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//		List<ActivityManager.RunningAppProcessInfo> appProcessList = am.getRunningAppProcesses();
//		int pid = -1;
//		for(ActivityManager.RunningAppProcessInfo info : appProcessList){
//			if(info.processName.equals("com.baidu.video")){
//				pid = info.pid;
//			}
//		}
		
//		Log.d(TAG, "my pid --->" + Process.myPid());
//		Log.d(TAG, "baidu pid --->" + pid);
//		
//		if(pid!=-1){
//			Process.killProcess(pid);
//		}
		
		if(isBaiduInstalled()){
			Intent localIntent = new Intent("com.baidu.search.video");
//			Intent localIntent = new Intent("com.baidu.player");
//		    localIntent.putExtra("title", "爱爱囧事_DVDscr国语中字.rmvb|");
//		    localIntent.putExtra("title", name);
		    localIntent.putExtra("refer", from);
//		    localIntent.putExtra("bdhdurl", "bdhd://199202767|7218455282C420D033467A75EBCCCF5D|小夫妻时代01.HDTV.rmvb|");
//		    localIntent.putExtra("bdhdurl", "bdhd://423797339|F438C1DF87CADAB226828D0F95F9E698|爱爱囧事_DVDscr国语中字.rmvb|");
		    localIntent.putExtra("bdhdurl", url);
		    localIntent.setClassName("com.baidu.video", "com.baidu.video.ui.ThirdInvokeActivtiy");
//		    localIntent.setClassName("com.baidu.video.pad", "com.baidu.video.player.PlayerActivity");
//		    startActivity(localIntent.addFlags(131072));
		    startActivity(localIntent.addFlags(131072));
//		    startActivity(localIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
		}else{
			if(!Constant.isSimple){
				AlertDialog.Builder tDialog = new AlertDialog.Builder(this);
				tDialog.setTitle("安装提示");
				tDialog.setMessage("该视频需要百度影音支持播放，是否安装百度影音播放器");
				tDialog.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Utils.retrieveApkFromAssets(PlayBaiduActivity.this, "baidushipin_1040402251.apk");
//								finish();
							}
						});

				tDialog.setNegativeButton(
						"取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						});
				tDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						finish();
					}
				});
				tDialog.show();
			}else{
				AlertDialog.Builder tDialog = new AlertDialog.Builder(this);
				tDialog.setTitle("安装提示");
				tDialog.setMessage("该视频需要百度影音支持播放");
				tDialog.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						});
				tDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						finish();
					}
				});
				tDialog.show();
			}
			
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
	
		
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
//		Log.d(TAG, data.getDataString());
//		super.onActivityResult(requestCode, resultCode, data);
//		finish();
//	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver1);
		super.onDestroy();
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
		super.onResume();
		MobclickAgent.onResume(this);
	}
}
