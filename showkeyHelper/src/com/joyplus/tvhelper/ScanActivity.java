package com.joyplus.tvhelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.joyplus.tvhelper.entity.ProcessInfo;
import com.joyplus.tvhelper.utils.PackageUtils;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootTools.Result;
import com.stericson.RootTools.exceptions.RootToolsException;

public class ScanActivity extends Activity implements OnClickListener{
	
	private static final String TAG = "ScanActivity";
	
	private static final int DELAY = 33;
	private static final int MESSAGE_UPDATE_SCAN_ANIM = 0;
	private static final int MESSAGE_UPDATE_SCAN_ANIM_TEMP = MESSAGE_UPDATE_SCAN_ANIM + 1;
	private static final int MESSAGE_UPDATE_PROGRESSBAR = MESSAGE_UPDATE_SCAN_ANIM_TEMP + 1;
	private static final int MESSAGE_SCAN_END = MESSAGE_UPDATE_PROGRESSBAR + 1;
	private static final int MESSAGE_KILL_BACKGROUND_OK = MESSAGE_SCAN_END + 1;
	private static final int MESSAGE_CLEAN_CACHE_OK = MESSAGE_KILL_BACKGROUND_OK + 1;
	private static final int MESSAGE_DISABLE_RECIVER_OK = MESSAGE_CLEAN_CACHE_OK + 1;
	private static final int MESSAGE_DELETE_DIRTYFILE_OK = MESSAGE_DISABLE_RECIVER_OK + 1;
	
	private static final int STEP_SCAN_BACKGROUND = 1;
	private static final int STEP_SCAN_CACHE = 2;
	private static final int STEP_SCAN_AUTOSTART = 3;
	private static final int STEP_SCAN_DIRTY = 4;
	private static final int STEP_SCAN_END = 5;

	private Button back_button;
	private Button jiasu_Button;
	private LinearLayout layout_houtaijincheng, layout_lajihuanchun, layout_ziqiruanjian, layout_xiezaicanliu;
	private ImageView big_image_1, big_image_2, big_image_3, big_image_4;
	private ImageView imag_scan_anim_1, imag_scan_anim_2, imag_scan_anim_3, imag_scan_anim_4;
	private ProgressBar mPorgressBar;
	private TextView backgroud_count_text;
	private TextView cache_size_text;
	private TextView auto_start_count_text;
	private TextView canliu_size_text;

	private ImageView icon_1;
	private ImageView icon_2;
	private ImageView icon_3;
	private ImageView icon_4;
	
	private TextView notice_jiasu_text;
	private TextView notice_auto_start_title;
	
	private TextView titleText;
	private int mStep;
	
	private long mCacheSize;
	private long dirtyFileSize;
	
	private List<ProcessInfo> processInfoList = null;
	private ActivityManager mActivityManager;
	
	private List<String> protectList = new ArrayList<String>();
	private List<ResolveInfo> autoStartLists = new ArrayList<ResolveInfo>();
	
	private List<FileIndex> fileIndexList = new ArrayList<ScanActivity.FileIndex>();
	private List<File> dirtyFile = new ArrayList<File>();
	
	private boolean hasRootPermission = false;

	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_UPDATE_SCAN_ANIM:
				updateScanImage();
				break;
			case MESSAGE_UPDATE_SCAN_ANIM_TEMP:
				mStep ++;
				updateStep();
				switch (mStep) {
				case STEP_SCAN_CACHE:
					getCacheSize();
					break;
				case STEP_SCAN_AUTOSTART:
					queryAutoStartApp();
					break;
				case STEP_SCAN_DIRTY:
					getSdcardDirtyFile();
					break;
				default:
					break;
				}
				break;
			case MESSAGE_UPDATE_PROGRESSBAR:
				Log.d(TAG, "message progressbar -->" + msg.arg1);
				switch (mStep) {
				case STEP_SCAN_BACKGROUND:
					backgroud_count_text.setText(msg.arg2 + "个");
					big_image_1.setImageBitmap(layout_houtaijincheng.getDrawingCache());
					break;
				case STEP_SCAN_CACHE:
					long cacheSize = (Long) msg.obj;
					if(cacheSize!=0){
						mCacheSize += cacheSize;
						cache_size_text.setText(PackageUtils.fomartSize(mCacheSize));
						big_image_2.setImageBitmap(layout_lajihuanchun.getDrawingCache());
					}
				case STEP_SCAN_AUTOSTART:
					int count = msg.arg2;
					if(count!=0){
						auto_start_count_text.setText(count + "个");
						big_image_3.setImageBitmap(layout_ziqiruanjian.getDrawingCache());
					}
					break;
				case STEP_SCAN_DIRTY:
					Log.d(TAG, "dirty app name --->" + msg.obj);
					Log.d(TAG, "dirty file size --->" + PackageUtils.fomartSize(msg.arg2));
					if(msg.arg2!=0){
						dirtyFileSize += msg.arg2;
						canliu_size_text.setText(PackageUtils.fomartSize(dirtyFileSize));
						big_image_4.setImageBitmap(layout_xiezaicanliu.getDrawingCache());
					}
				default:
					break;
				}
				mPorgressBar.setProgress(msg.arg1);
				break;
			case MESSAGE_SCAN_END:
				titleText.setText("电脑运行速度缓慢");
				ScaleAnimation animation_disappar = new ScaleAnimation(
						1.0f, 
						layout_houtaijincheng.getWidth()/(layout_houtaijincheng.getWidth()+40f),
						1.0f, 
						layout_houtaijincheng.getHeight()/(layout_houtaijincheng.getHeight()+40f), 
						Animation.RELATIVE_TO_SELF, 
						0.5f, 
						Animation.RELATIVE_TO_SELF, 
						0.5f);
				animation_disappar.setDuration(250);
				if(dirtyFileSize>0){
					icon_4.setImageResource(R.drawable.jiasu_icon_tanhao);
				}else{
					icon_4.setImageResource(R.drawable.jiasu_icon_duihao);
				}
				layout_xiezaicanliu.setBackgroundColor(getResources().getColor(R.color.jiasu_background_blue));
				big_image_4.setImageBitmap(layout_xiezaicanliu.getDrawingCache());
				big_image_4.startAnimation(animation_disappar);
				big_image_4.setVisibility(View.INVISIBLE);
				mHandler.removeMessages(MESSAGE_UPDATE_SCAN_ANIM);
				imag_scan_anim_4.setVisibility(View.INVISIBLE);
				mPorgressBar.setVisibility(View.INVISIBLE);
				notice_jiasu_text.setVisibility(View.GONE);
				back_button.setVisibility(View.GONE);
				jiasu_Button.setVisibility(View.VISIBLE);
				jiasu_Button.requestFocus();
				break;
			case MESSAGE_KILL_BACKGROUND_OK:
				icon_1.setImageResource(R.drawable.jiasu_icon_duihao);
				backgroud_count_text.setText("0个");
				processInfoList.clear();
				break;
			case MESSAGE_CLEAN_CACHE_OK:
				icon_2.setImageResource(R.drawable.jiasu_icon_duihao);
				cache_size_text.setText("0B");
				mCacheSize = 0;
				break;
			case MESSAGE_DISABLE_RECIVER_OK:
				icon_3.setImageResource(R.drawable.jiasu_icon_duihao);
				auto_start_count_text.setText("0个");
				autoStartLists.clear();
				break;
			case MESSAGE_DELETE_DIRTYFILE_OK:
				icon_4.setImageResource(R.drawable.jiasu_icon_duihao);
				canliu_size_text.setText("0B");
				dirtyFileSize = 0;
				dirtyFile.clear();
				break;
			default:
				break;
			}
		};
	};
	
	private void updateScanImage(){
		
		switch (mStep) {
		case STEP_SCAN_BACKGROUND:
			moveView(imag_scan_anim_1);
			break;
		case STEP_SCAN_CACHE:
			moveView(imag_scan_anim_2);
			break;
		case STEP_SCAN_AUTOSTART:
			moveView(imag_scan_anim_3);
			break;
		case STEP_SCAN_DIRTY:
			moveView(imag_scan_anim_4);
			break;

		}
		mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_SCAN_ANIM, DELAY);
	}
	
	private void moveView(View v){
		RelativeLayout.LayoutParams params =  (LayoutParams) v.getLayoutParams();
		if(params.leftMargin+5>(big_image_1.getWidth()-v.getWidth())){
			params.setMargins(0, params.topMargin, params.rightMargin, params.bottomMargin);
		}else{
			params.setMargins(params.leftMargin+5, params.topMargin, params.rightMargin, params.bottomMargin);
		}
		v.setVisibility(View.VISIBLE);
		v.setLayoutParams(params);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		try {
			Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		back_button = (Button) findViewById(R.id.back_Button);
		jiasu_Button = (Button) findViewById(R.id.jiasu_Button);
		jiasu_Button.setOnClickListener(this);
		back_button.setOnClickListener(this);
		
		layout_houtaijincheng = (LinearLayout) findViewById(R.id.layout_houtaijincheng);
		layout_lajihuanchun = (LinearLayout) findViewById(R.id.layout_lajihuanchun);
		layout_ziqiruanjian = (LinearLayout) findViewById(R.id.layout_ziqiruanjian);
		layout_xiezaicanliu = (LinearLayout) findViewById(R.id.layout_xiezaicanliu);
		
		layout_houtaijincheng.setDrawingCacheEnabled(true);
		layout_lajihuanchun.setDrawingCacheEnabled(true);
		layout_ziqiruanjian.setDrawingCacheEnabled(true);
		layout_xiezaicanliu.setDrawingCacheEnabled(true);
		
		big_image_1 = (ImageView) findViewById(R.id.big_image_1);
		big_image_2 = (ImageView) findViewById(R.id.big_image_2);
		big_image_3 = (ImageView) findViewById(R.id.big_image_3);
		big_image_4 = (ImageView) findViewById(R.id.big_image_4);
		
		imag_scan_anim_1 = (ImageView) findViewById(R.id.imag_scan_anim_1);
		imag_scan_anim_2 = (ImageView) findViewById(R.id.imag_scan_anim_2);
		imag_scan_anim_3 = (ImageView) findViewById(R.id.imag_scan_anim_3);
		imag_scan_anim_4 = (ImageView) findViewById(R.id.imag_scan_anim_4);
		
		mPorgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		mPorgressBar.setMax(100);
		backgroud_count_text = (TextView) findViewById(R.id.backgroud_count_text);
		cache_size_text = (TextView) findViewById(R.id.cache_size_text);
		auto_start_count_text = (TextView) findViewById(R.id.auto_start_count_text);
		canliu_size_text = (TextView) findViewById(R.id.canliu_size_text);
		
		icon_1 = (ImageView) findViewById(R.id.notice_icon_1);
		icon_2 = (ImageView) findViewById(R.id.notice_icon_2);
		icon_3 = (ImageView) findViewById(R.id.notice_icon_3);
		icon_4 = (ImageView) findViewById(R.id.notice_icon_4);
		
		notice_jiasu_text = (TextView) findViewById(R.id.notice_jiasu_text);
		notice_auto_start_title = (TextView) findViewById(R.id.notice_autoStart_titie);
		
		titleText = (TextView) findViewById(R.id.notice_title);
		mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		getProtectAppList();
		for(String str:protectList){
			Log.d(TAG, str);
		}
		titleText.setText("正在扫描");
		if(RootTools.isAccessGiven()){
			hasRootPermission = true;
			notice_auto_start_title.setText("自启软件");
		}else{
			hasRootPermission = false;
			notice_auto_start_title.setText("自启软件\n(需要root)");
		}
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mStep = STEP_SCAN_BACKGROUND;
				updateStep();
				getRunningAppProcessInfo();
			}
		},500);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_Button:
			finish();
			break;
		case R.id.jiasu_Button:
			jiasu();
			break;
		default:
			break;
		}
	}
	
	private void updateStep(){
		mHandler.removeMessages(MESSAGE_UPDATE_SCAN_ANIM);
		Log.d("TAG", "with -->" +layout_houtaijincheng.getWidth()/(layout_houtaijincheng.getWidth()+40f));
		Log.d("TAG", "getHeight -->" +layout_houtaijincheng.getHeight()/(layout_houtaijincheng.getHeight()+40f));
		ScaleAnimation animation_appear = new ScaleAnimation(
				layout_houtaijincheng.getWidth()/(layout_houtaijincheng.getWidth()+40f),
				1.0f, 
				layout_houtaijincheng.getHeight()/(layout_houtaijincheng.getHeight()+40f), 
				1.0f, 
				Animation.RELATIVE_TO_SELF, 
				0.5f, 
				Animation.RELATIVE_TO_SELF, 
				0.5f);
		animation_appear.setDuration(500);
		ScaleAnimation animation_disappar = new ScaleAnimation(
				1.0f, 
				layout_houtaijincheng.getWidth()/(layout_houtaijincheng.getWidth()+40f),
				1.0f, 
				layout_houtaijincheng.getHeight()/(layout_houtaijincheng.getHeight()+40f), 
				Animation.RELATIVE_TO_SELF, 
				0.5f, 
				Animation.RELATIVE_TO_SELF, 
				0.5f);
		animation_disappar.setDuration(250);
		switch (mStep) {
		case STEP_SCAN_BACKGROUND:
			layout_houtaijincheng.setBackgroundColor(getResources().getColor(R.color.btn_background_nomarl));
			big_image_1.setImageBitmap(layout_houtaijincheng.getDrawingCache());
			big_image_1.startAnimation(animation_appear);
			big_image_1.setVisibility(View.VISIBLE);
			break;
		case STEP_SCAN_CACHE:
			if(processInfoList.size()>0){
				icon_1.setImageResource(R.drawable.jiasu_icon_tanhao);
			}else{
				icon_1.setImageResource(R.drawable.jiasu_icon_duihao);
			}
			layout_houtaijincheng.setBackgroundColor(getResources().getColor(R.color.jiasu_background_blue));
			layout_lajihuanchun.setBackgroundColor(getResources().getColor(R.color.btn_background_nomarl));
			big_image_2.setImageBitmap(layout_lajihuanchun.getDrawingCache());
			big_image_2.startAnimation(animation_appear);
			big_image_2.setVisibility(View.VISIBLE);
			big_image_1.setImageBitmap(layout_houtaijincheng.getDrawingCache());
			big_image_1.startAnimation(animation_disappar);
			big_image_1.setVisibility(View.INVISIBLE);
			imag_scan_anim_1.setVisibility(View.INVISIBLE);
			break;
		case STEP_SCAN_AUTOSTART:
			if(mCacheSize>0){
				icon_2.setImageResource(R.drawable.jiasu_icon_tanhao);
			}else{
				icon_2.setImageResource(R.drawable.jiasu_icon_duihao);
			}
			layout_lajihuanchun.setBackgroundColor(getResources().getColor(R.color.jiasu_background_blue));
			layout_ziqiruanjian.setBackgroundColor(getResources().getColor(R.color.btn_background_nomarl));
			big_image_3.setImageBitmap(layout_ziqiruanjian.getDrawingCache());
			big_image_3.startAnimation(animation_appear);
			big_image_3.setVisibility(View.VISIBLE);
			big_image_2.setImageBitmap(layout_lajihuanchun.getDrawingCache());
			big_image_2.startAnimation(animation_disappar);
			big_image_2.setVisibility(View.INVISIBLE);
			imag_scan_anim_2.setVisibility(View.INVISIBLE);
			break;
		case STEP_SCAN_DIRTY:
			if(autoStartLists.size()>0){
				icon_3.setImageResource(R.drawable.jiasu_icon_tanhao);
			}else{
				icon_3.setImageResource(R.drawable.jiasu_icon_duihao);
			}
			layout_ziqiruanjian.setBackgroundColor(getResources().getColor(R.color.jiasu_background_blue));
			layout_xiezaicanliu.setBackgroundColor(getResources().getColor(R.color.btn_background_nomarl));
			big_image_4.setImageBitmap(layout_xiezaicanliu.getDrawingCache());
			big_image_4.startAnimation(animation_appear);
			big_image_4.setVisibility(View.VISIBLE);
			big_image_3.setImageBitmap(layout_ziqiruanjian.getDrawingCache());
			big_image_3.startAnimation(animation_disappar);
			big_image_3.setVisibility(View.INVISIBLE);
			imag_scan_anim_3.setVisibility(View.INVISIBLE);
			break;
		default:
			break;
		}
		mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_SCAN_ANIM, 500);
//		if(mStep>STEP_SCAN_AUTOSTART){
//			mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_SCAN_ANIM_TEMP, 5000);
//		}
	}
	
	private void jiasu(){
		if(processInfoList.size()>0){
			killBackGroundProgress();
		}
		if(mCacheSize>0){
			cleanCache();
		}
		if(autoStartLists.size()>0){
			disableBootReciver();
		}
		if(dirtyFile.size()>0){
			cleanDirtyFile();
		}
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				titleText.setText("电视运行良好");
			}
		});
	}
	
	private void killBackGroundProgress(){
		for(ProcessInfo info:processInfoList){
			killProgress(info.getProcessName());
		}
		mHandler.sendEmptyMessage(MESSAGE_KILL_BACKGROUND_OK);
	}
	
	private void cleanCache(){
		try {
			delteCache();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	private void disableBootReciver(){
		if(hasRootPermission){
			String [] cmds = new String[autoStartLists.size()];
			for(int i=0; i<autoStartLists.size(); i++){
				ResolveInfo info = autoStartLists.get(i);
				String cmd = "pm disable " + info.activityInfo.packageName + "/" +info.activityInfo.name;
				Log.d(TAG, cmd);
				cmds[i] = cmd;
			}
			try {
				RootTools.sendShell(cmds, 0, new Result() {
					
					@Override
					public void processError(String arg0) throws Exception {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void process(String arg0) throws Exception {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onFailure(Exception arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onComplete(int arg0) {
						// TODO Auto-generated method stub
						mHandler.sendEmptyMessage(MESSAGE_DISABLE_RECIVER_OK);
					}
				}, true, 3000);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RootToolsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void cleanDirtyFile(){
		for(File f: dirtyFile){
			if(f.isDirectory()){
				deleteDir(f);
			}else{
				deleteFile(f);
			}
			
			
		}
		mHandler.sendEmptyMessage(MESSAGE_DELETE_DIRTYFILE_OK);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		mHandler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}
	
	private void deleteFile(File f){
		if(f.exists()){
			f.delete();
		}
	}
	
    public boolean deleteDir(File file){
        boolean success = true ;
        if(file.exists()){
                File[] list = file.listFiles() ;
                if(list != null){
                        int len = list.length ;
                        for(int i = 0 ; i < len ; ++i){
                                if(list[i].isDirectory()){
                                        deleteDir(list[i]) ;
                                } else {
                                        boolean ret = list[i].delete() ;
                                        if(!ret){
                                                success = false ;
                                        }
                                }
                        }
                }
        } else {
                success = false ;
        }
        if(success){
                file.delete() ;
        }
        return success ;
}
	
	/**
	 * clean cache
	 * @throws Exception
	 */
	private void delteCache() throws Exception{
		PackageManager pm = getPackageManager();
		Class[] arrayOfClass = new Class[2];
		Class localClass2 = Long.TYPE;
		arrayOfClass[0] = localClass2;
		arrayOfClass[1] = IPackageDataObserver.class;
		Method localMethod = pm.getClass().getMethod("freeStorageAndNotify", arrayOfClass);
		Long localLong = Long.valueOf(getEnvironmentSize() - 1L);
		Object[] arrayOfObject = new Object[2];
		arrayOfObject[0] = localLong;
		localMethod.invoke(pm,localLong,new IPackageDataObserver.Stub(){
		  public void onRemoveCompleted(String packageName,boolean succeeded) throws RemoteException {
		       // TODO Auto-generated method stub
			  mHandler.sendEmptyMessage(MESSAGE_CLEAN_CACHE_OK);
			  }
		  });
		
	}
	/**
	 * get all running application  progress info
	 */
	private void getRunningAppProcessInfo() {
		// ProcessInfo Model类   用来保存所有进程信息
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				processInfoList = new ArrayList<ProcessInfo>();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				PackageManager pm = getPackageManager();
				// 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
				List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager
						.getRunningAppProcesses();
				Message msg = mHandler.obtainMessage(MESSAGE_UPDATE_PROGRESSBAR);
				msg.arg1 = 10;
				mHandler.sendMessage(msg);
//				for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
				for (int i=0; i<appProcessList.size(); i++) {
					ActivityManager.RunningAppProcessInfo appProcessInfo = appProcessList.get(i);
					// 进程ID号
					int pid = appProcessInfo.pid;
					// 用户ID 类似于Linux的权限不同，ID也就不同 比如 root等
					int uid = appProcessInfo.uid;
					if(uid!=1000){
						// 进程名，默认是包名或者由属性android：process=""指定
//						String processName = appProcessInfo.processName;
						String appName;
						try {
							appName = pm.getApplicationInfo(appProcessInfo.processName, 0).loadLabel(pm).toString();
							int[] myMempid = new int[] { pid };
							// 此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
							Debug.MemoryInfo[] memoryInfo = mActivityManager
									.getProcessMemoryInfo(myMempid);
							// 获取进程占内存用信息 kb单位
							int memSize = memoryInfo[0].dalvikPrivateDirty;

							Log.i(TAG, "processName: " + appName + "  pid: " + pid
									+ " uid:" + uid + " memorySize is -->" + memSize + "kb");
							// 构造一个ProcessInfo对象
							ProcessInfo processInfo = new ProcessInfo();
							processInfo.setPid(pid);
							processInfo.setUid(uid);
							processInfo.setAppName(appName);
							processInfo.setMemSize(memSize);
							processInfo.setPocessName(appProcessInfo.processName);
							//保存所有运行在该应用程序的包名
							processInfo.pkgnameList = appProcessInfo.pkgList ;
							for (String pkg : processInfo.pkgnameList) {
								if(!protectList.contains(pkg)){
									Log.d(TAG, "add ---->" + processInfo.getProcessName());
									processInfoList.add(processInfo);
									break;
								}
							}
							Message msg1 = mHandler.obtainMessage(MESSAGE_UPDATE_PROGRESSBAR);
							msg1.arg1 = 10 + (i*15)/appProcessList.size();
							msg1.arg2 = processInfoList.size();
							msg1.obj = processInfo.getAppName();
							mHandler.sendMessage(msg1);
						} catch (NameNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					try {
						Thread.sleep(2000/appProcessList.size());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				mHandler.sendEmptyMessage(MESSAGE_UPDATE_SCAN_ANIM_TEMP);
			}
		}).start();
	    
	}
	/**
	 * kill all background processes associated with the given package
	 * @param packgeName
	 */
	private void killProgress(String packgeName){
		 mActivityManager.killBackgroundProcesses(packgeName);
	}
	/**
	 * get ProtectAppList from asset
	 */
	private void getProtectAppList(){
		try {
			InputStream in = getAssets().open("protect.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			 while (reader.ready()) {
				 protectList.add(reader.readLine().trim());
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static long getEnvironmentSize()
    {
      File localFile = Environment.getDataDirectory();
      long l1;
      if (localFile == null)
        l1 = 0L;
      while (true)
      {
        
        String str = localFile.getPath();
        StatFs localStatFs = new StatFs(str);
        long l2 = localStatFs.getBlockSize();
        l1 = localStatFs.getBlockCount() * l2;
        return l1;
      }
      
    }
	
	private void getCacheSize(){
  	  new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			PackageManager pm = getPackageManager();
			List<PackageInfo> packageList = pm.getInstalledPackages(0);
			for(int i=0; i<packageList.size(); i++){
				try {
					PackageInfo info = packageList.get(i);
					PackageUtils.getInstalledApkSize(ScanActivity.this, 
							packageList.get(i).packageName, new PkgSizeObserver(i, packageList.size(), info.applicationInfo.loadLabel(pm).toString()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					Thread.sleep(2500/packageList.size());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}).start();
    }
	
	
	class PkgSizeObserver extends IPackageStatsObserver.Stub{

		private int index;
		private int size;
		private String appName;
		
		
		public PkgSizeObserver(int index, int size, String appName) {
			super();
			this.index = index;
			this.size = size;
			this.appName = appName;
		}
		
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			// TODO Auto-generated method stub
			Log.d(TAG, appName + " cache----->" + PackageUtils.fomartSize(pStats.cacheSize));
			Message msg = mHandler.obtainMessage(MESSAGE_UPDATE_PROGRESSBAR);
			msg.arg1 = 25 + (index*25)/size;
			msg.obj = pStats.cacheSize;
			mHandler.sendMessage(msg);
			if(index == size-1){
				mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_SCAN_ANIM_TEMP, 500);
			}
		}
		
	}
	
	private void queryAutoStartApp(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg = mHandler.obtainMessage(MESSAGE_UPDATE_PROGRESSBAR);
				msg.arg1 = 60;
				mHandler.sendMessage(msg);
				PackageManager p = getPackageManager();
				Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
				List<ResolveInfo> resolveInfoList = p.queryBroadcastReceivers(intent, PackageManager.GET_DISABLED_COMPONENTS);
//				for(ResolveInfo info : resolveInfoList){
				List<ResolveInfo> resolveInfoList_1 = new ArrayList<ResolveInfo>();
				for(ResolveInfo info : resolveInfoList){
					if ((info.activityInfo.applicationInfo.flags & info.activityInfo.applicationInfo.FLAG_SYSTEM) <= 0) {
						// customs applications
						resolveInfoList_1.add(info);
					}
				}
				for(int i=0; i<resolveInfoList_1.size();i++){ 
					try {
						Thread.sleep(1500/resolveInfoList_1.size());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ResolveInfo info = resolveInfoList_1.get(i);
					ComponentName mComponentName = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
					Log.d(TAG, "Enabled-->" + p.getComponentEnabledSetting(mComponentName));
					if(p.getComponentEnabledSetting(mComponentName) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED){
						if(!protectList.contains(info.activityInfo.packageName)){
							Log.d(TAG, "Enabled appname-->" + info.activityInfo.loadLabel(p));
							Log.d("TAG", info.activityInfo.packageName);
							autoStartLists.add(info);
							Message msg_1 = mHandler.obtainMessage(MESSAGE_UPDATE_PROGRESSBAR);
							msg_1.arg1 = 60 + ((i+1)*15)/resolveInfoList_1.size();
							msg_1.arg2 = autoStartLists.size();
							msg_1.obj = info.activityInfo.loadLabel(p);
							mHandler.sendMessage(msg_1);
						}	
					}
				}
				mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_SCAN_ANIM_TEMP, 500);
			}
		}).start();
	}
	
	/*** 获取文件夹大小 ***/
	private long getFileSize(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}
	
	private void getSdcardDirtyFile(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				getSdCardFileIndex();
				Message msg = mHandler.obtainMessage(MESSAGE_UPDATE_PROGRESSBAR);
				msg.arg1 = 80;
				mHandler.sendMessage(msg);
				File sdcardFile = Environment.getExternalStorageDirectory();
				PackageManager pm = getPackageManager();
				List<PackageInfo> packages = pm.getInstalledPackages(0);
				if(sdcardFile!=null){
					File[] files = sdcardFile.listFiles();
					if(files!=null){
						for(int i=0; i<files.length; i++){
							File f = files[i];
							for(FileIndex index: fileIndexList){
								if(index.fileName.equals(f.getName())){
									boolean isInstalled = false;
									for(String pkg:index.packageList){
										if(PackageUtils.isInstalled(ScanActivity.this, pkg)){
											isInstalled = true;
											Log.d(TAG, pkg+"------> installed");
										}
									}
									if(!isInstalled){
										dirtyFile.add(f);
										Log.d(TAG, "file path -->" + f.getAbsolutePath());
										long filesize = 0;
										try {
											filesize = getFileSize(f);
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										Message msg_1 = mHandler.obtainMessage(MESSAGE_UPDATE_PROGRESSBAR);
										msg_1.arg1 = 80 + (20*i)/files.length;
										msg_1.arg2 = (int) filesize;
										msg_1.obj = index.appName;
										mHandler.sendMessage(msg_1);
									}
								}
							}
						}
					}
				}
				Message msg_1 = mHandler.obtainMessage(MESSAGE_UPDATE_PROGRESSBAR);
				msg_1.arg1 = 100;
				mHandler.sendMessage(msg_1);
				mHandler.sendEmptyMessageDelayed(MESSAGE_SCAN_END,200);
			}
		}).start();
	}
	
	private void getSdCardFileIndex(){
		try {
			InputStream in = getAssets().open("sd_files.idx");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			 while (reader.ready()) {
				 String str = reader.readLine().trim();
				 if(str.contains(";")){
					 String temp[] = str.split(";");
					 if(temp.length>=3){
						 FileIndex fileIndex = new FileIndex();
						 fileIndex.appName = temp[0];
						 fileIndex.fileName = temp[1];
						 List<String> pkgs = new ArrayList<String>();
						 for(int i=2; i<temp.length; i++){
							 pkgs.add(temp[i]);
						 }
						 fileIndex.packageList = pkgs;
						 fileIndexList.add(fileIndex);
					 }
				 }
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class FileIndex{
		public String appName;
		public String fileName;
		List<String> packageList;
	}
}
