package com.joyplus.tvhelper;

import java.io.File;
import java.util.Iterator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.joyplus.network.filedownload.manager.DownloadManager;
import com.joyplus.tvhelper.adapter.PushedApkAdapter;
import com.joyplus.tvhelper.db.DBServices;
import com.joyplus.tvhelper.entity.PushedApkDownLoadInfo;
import com.joyplus.tvhelper.faye.FayeService;
import com.joyplus.tvhelper.utils.Global;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.PreferencesUtils;

public class ManagePushApkActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private static final String TAG = "ManagePushApkActivity";
	private static final int MESSAGE_UPDATE_INSTALLE_PROGRESS = 0;
	
	private Button backButton, deleteButton, cancleButton, editeButton;
	private LinearLayout layout1, layout2;
	private ListView list;
	private TextView pincodeTextView;
	private DownloadManager downloadManager;
	private DBServices dbService;
	private PushedApkAdapter adpter;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			updateInstallProgress(msg.arg1);
		};
	};
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(Global.ACTION_DOWNLOAD_PROGRESS.equals(action)){
				adpter.notifyDataSetChanged();
			}else if(Global.ACTION_DOWNLOAD_RECIVED.equals(action)){
				Log.d(TAG, "receve --- > " + Global.ACTION_DOWNLOAD_RECIVED);
				updateEditBottn();
				layout2.setVisibility(View.GONE);
				layout1.setVisibility(View.VISIBLE);
				backButton.requestFocus();
				adpter.notifyDataSetChanged();
			}else if(Global.ACTION_DOWNL_GETSIZE_SUCESS.equals(action)){
				Log.d(TAG, "ManagePushApkActivity onReceive" + action);
				adpter.notifyDataSetChanged();
			}else if(Global.ACTION_APK_DOWNLOAD_COMPLETE.equals(action)){
				Log.d(TAG, "ManagePushApkActivity onReceive" + action);
				int _id = intent.getIntExtra("_id", 0);
				editeButton.setEnabled(false);
				layout2.setVisibility(View.GONE);
				layout1.setVisibility(View.VISIBLE);
				adpter.notifyDataSetChanged();
				if(FayeService.isSystemApp){
					updateInstallProgress(_id);
				}else{
					
				}
				
			}else if(Global.ACTION_APK_DOWNLOAD_FAILE.equals(action)){
				Log.d(TAG, "ManagePushApkActivity onReceive" + action);
//				if(FayeService.userPushApkInfos.size() == 0){
//					editeButton.setEnabled(false);
//				}else{
//					editeButton.setEnabled(true);
//				}
//				layout2.setVisibility(View.GONE);
//				layout1.setVisibility(View.VISIBLE);
				adpter.notifyDataSetChanged();
			}else if(Global.ACTION_DOWNL_INSTALL_SUCESS.equals(action)){
				Log.d(TAG, "ManagePushApkActivity onReceive" + action);
				int _id = intent.getIntExtra("_id", 0);
				handler.removeMessages(MESSAGE_UPDATE_INSTALLE_PROGRESS);
				LinearLayout layout = (LinearLayout) list.findViewWithTag(_id);
				if(layout!=null){
					ProgressBar bar = (ProgressBar) layout.findViewById(R.id.progressbar);
					TextView valueText = (TextView) layout.findViewById(R.id.progress_value);
					if(bar!=null){
						bar.setProgress(100);
					}
					if(valueText!=null){
						valueText.setText(100+"%");
					}
				}
				updateEditBottn();
				layout2.setVisibility(View.GONE);
				layout1.setVisibility(View.VISIBLE);
				adpter.notifyDataSetChanged();
			}else if(Global.ACTION_DOWNLOAD_START.equals(action)){
				Log.d(TAG, "ManagePushApkActivity onReceive" + action);
				adpter.notifyDataSetChanged();
			}else if(Global.ACTION_DOWNL_INSTALL_FAILE.equals(action)){
				Log.d(TAG, "ManagePushApkActivity onReceive" + action);
				handler.removeMessages(MESSAGE_UPDATE_INSTALLE_PROGRESS);
				updateEditBottn();
				layout2.setVisibility(View.GONE);
				layout1.setVisibility(View.VISIBLE);
				adpter.notifyDataSetChanged();
			}
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pushed_apk_manager);
		layout1 = (LinearLayout) findViewById(R.id.fistBtn_group);
		layout2 = (LinearLayout) findViewById(R.id.secondBtn_group);

		backButton = (Button) findViewById(R.id.back_Button);
		deleteButton = (Button) findViewById(R.id.del_Button);
		cancleButton = (Button) findViewById(R.id.cancel_Button);
		editeButton = (Button) findViewById(R.id.edit_Button);
		list = (ListView) findViewById(R.id.listView);
		pincodeTextView = (TextView) findViewById(R.id.pincode_text);
		displayPincode();
		updateEditBottn();
		layout2.setVisibility(View.GONE);
		layout1.setVisibility(View.VISIBLE);
		for(PushedApkDownLoadInfo info: FayeService.userPushApkInfos){
			info.setEdite_state(PushedApkDownLoadInfo.EDITE_STATUE_NOMAL);
		}
		adpter = new PushedApkAdapter(ManagePushApkActivity.this,FayeService.userPushApkInfos);
		list.setAdapter(adpter);
		list.setOnItemClickListener(this);
		backButton.setOnClickListener(this);
		deleteButton.setOnClickListener(this);
		cancleButton.setOnClickListener(this);
		editeButton.setOnClickListener(this);
		
		downloadManager = DownloadManager.getInstance(this);
		dbService = DBServices.getInstance(this);
		IntentFilter filter = new IntentFilter(Global.ACTION_DOWNLOAD_PROGRESS);
		filter.addAction(Global.ACTION_DOWNL_GETSIZE_SUCESS);
		filter.addAction(Global.ACTION_DOWNLOAD_RECIVED);
		filter.addAction(Global.ACTION_APK_DOWNLOAD_COMPLETE);
		filter.addAction(Global.ACTION_DOWNL_INSTALL_SUCESS);
		filter.addAction(Global.ACTION_DOWNL_INSTALL_FAILE);
		filter.addAction(Global.ACTION_DOWNLOAD_START);
		filter.addAction(Global.ACTION_APK_DOWNLOAD_FAILE);
		registerReceiver(receiver, filter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_Button:
			finish();
			break;
		case R.id.del_Button:
//			for(int i=0; i<FayeService.userPushApkInfos.size(); i++){
//				PushedApkDownLoadInfo info = FayeService.userPushApkInfos.get(i);
//				if(info.getEdite_state()==PushedApkDownLoadInfo.EDITE_STATUE_SELETED){
//					FayeService.userPushApkInfos.remove(info);
//					File f = new File(info.getFile_path());
//					if(f!=null&&f.exists()){
//						f.delete();
//					}
//					dbService.deleteApkInfo(info);
//				}
//			}
//			
			Iterator<PushedApkDownLoadInfo> iterator = FayeService.userPushApkInfos.iterator();  
	         while(iterator.hasNext()) {  
	        	 PushedApkDownLoadInfo info = iterator.next();  
	             if(info.getEdite_state()==PushedApkDownLoadInfo.EDITE_STATUE_SELETED) {  
						File f = new File(info.getFile_path());
						if(f!=null&&f.exists()){
							f.delete();
						}
						dbService.deleteApkInfo(info);
						iterator.remove();  
	             }else{
	            	 info.setEdite_state(PushedApkDownLoadInfo.EDITE_STATUE_NOMAL);
	             }
	               
	         }  
			adpter.notifyDataSetChanged();
			layout2.setVisibility(View.GONE);
			layout1.setVisibility(View.VISIBLE);
			break;
		case R.id.edit_Button:
			layout1.setVisibility(View.GONE);
			layout2.setVisibility(View.VISIBLE);
			for(int i=0; i<FayeService.userPushApkInfos.size(); i++){
				PushedApkDownLoadInfo info = FayeService.userPushApkInfos.get(i);
				if(info.getDownload_state()==PushedApkDownLoadInfo.STATUE_DOWNLOADING){
					downloadManager.pauseTask(info.getTast());
					info.setDownload_state(PushedApkDownLoadInfo.STATUE_DOWNLOAD_PAUSE);
				}
				info.setEdite_state(PushedApkDownLoadInfo.EDITE_STATUE_EDIT);
			}
			adpter.notifyDataSetChanged();
			cancleButton.requestFocus();
			break;
		case R.id.cancel_Button:
			layout1.setVisibility(View.VISIBLE);
			layout2.setVisibility(View.GONE);
			for(int i=0; i<FayeService.userPushApkInfos.size(); i++){
				PushedApkDownLoadInfo info = FayeService.userPushApkInfos.get(i);
				info.setEdite_state(PushedApkDownLoadInfo.EDITE_STATUE_NOMAL);
			}
			adpter.notifyDataSetChanged();
			break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		PushedApkDownLoadInfo info = FayeService.userPushApkInfos.get(position);
		
		switch (info.getEdite_state()) {
		case PushedApkDownLoadInfo.EDITE_STATUE_NOMAL:
			switch (info.getDownload_state()) {
			case PushedApkDownLoadInfo.STATUE_DOWNLOADING:
				downloadManager.pauseTask(info.getTast());
				info.setDownload_state(PushedApkDownLoadInfo.STATUE_DOWNLOAD_PAUSEING);
				dbService.updateApkInfo(info);
//				Intent intentContinue = new Intent(Global.ACTION_DOWNLOAD_PAUSE);
//				sendBroadcast(intentContinue);
				adpter.notifyDataSetChanged();
				break;
			case PushedApkDownLoadInfo.STATUE_WAITING_DOWNLOAD:
				info.setDownload_state(PushedApkDownLoadInfo.STATUE_DOWNLOAD_PAUSE);
				dbService.updateApkInfo(info);
//				Intent intentContinue = new Intent(Global.ACTION_DOWNLOAD_PAUSE);
//				sendBroadcast(intentContinue);
				adpter.notifyDataSetChanged();
				break;
			case PushedApkDownLoadInfo.STATUE_DOWNLOAD_PAUSE:
				info.setDownload_state(PushedApkDownLoadInfo.STATUE_WAITING_DOWNLOAD);
				dbService.updateApkInfo(info);
				Intent intentpause = new Intent(Global.ACTION_APK_DOWNLOAD_CONTINUE);
				sendBroadcast(intentpause);
				adpter.notifyDataSetChanged();
				break;
			case PushedApkDownLoadInfo.STATUE_DOWNLOAD_COMPLETE:
				if(info.getPackageName()!=null){
			     	//filePath为文件路径
			     	Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE, Uri.parse("file://"+info.getFile_path()));
					startActivity(intent);
				}
				break;
			}
			break;
		case PushedApkDownLoadInfo.EDITE_STATUE_EDIT:
			info.setEdite_state(PushedApkDownLoadInfo.EDITE_STATUE_SELETED);
			adpter.notifyDataSetChanged();
			break;
		case PushedApkDownLoadInfo.EDITE_STATUE_SELETED:
			info.setEdite_state(PushedApkDownLoadInfo.EDITE_STATUE_EDIT);
			adpter.notifyDataSetChanged();
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private void updateInstallProgress(int _id){
		LinearLayout layout = (LinearLayout) list.findViewWithTag(_id);
		if(layout!=null){
			ProgressBar bar = (ProgressBar) layout.findViewById(R.id.progressbar);
			TextView valueText = (TextView) layout.findViewById(R.id.progress_value);
			int progress = bar.getProgress();
			progress += 3;
			if(progress>=100){
				progress = 99;
			}
			if(bar!=null){
				bar.setProgress(progress);
			}
			if(valueText!=null){
				valueText.setText(progress+"%");
			}
			Message msg =  new Message();
			msg.what = MESSAGE_UPDATE_INSTALLE_PROGRESS;
			msg.arg1 = _id;
			handler.sendMessageDelayed(msg, 500);
			Log.d(TAG, "updateInstallProgress!!!!!!!!!!!!!!!!!!!!>");
		}
	}
	
	private void displayPincode(){
//		String displayString = "";
		String pincode = PreferencesUtils.getPincode(ManagePushApkActivity.this);
//		if(pincode!=null){
//			for(int i= 0; i<pincode.length(); i++){
//				if(i==pincode.length()-1){
//					displayString += pincode.substring(i);
//				}else{
//					displayString += (pincode.substring(i,i+1) + "  ");
//					Log.d(TAG, displayString);
//				}
//			}
//		}
//		Log.d(TAG, displayString);
		pincodeTextView.setText("PIN:\t" + pincode);
	}
	private void updateEditBottn(){
		if(FayeService.userPushApkInfos.size()>0){
			editeButton.setVisibility(View.VISIBLE);
			list.requestFocus();
		}else{
			editeButton.setVisibility(View.INVISIBLE);
		}
	}
}
