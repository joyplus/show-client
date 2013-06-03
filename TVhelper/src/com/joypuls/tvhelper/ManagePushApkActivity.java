package com.joypuls.tvhelper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.BoringLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.joypuls.tvhelper.adapter.PushedApkAdapter;
import com.joypuls.tvhelper.db.PushedApkDownLoadInfo;
import com.joypuls.tvhelper.entity.PushedApkInfo;
import com.joypuls.tvhelper.faye.FayeService;
import com.joypuls.tvhelper.faye.Log;
import com.joypuls.tvhelper.ui.RoundProgressBar;
import com.joypuls.tvhelper.utils.Global;
import com.joypuls.tvhelper.utils.PreferencesUtils;

public class ManagePushApkActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private static final String TAG = "ManagePushApkActivity";
	private static final int MESSAGE_UPDATE_INSTALLE_PROGRESS = 0;
	
	private Button backButton, deleteButton, cancleButton, editeButton;
	private LinearLayout layout1, layout2;
	private ListView list;
	private TextView pincodeTextView;
	
	private List<PushedApkInfo> app_list = new ArrayList<PushedApkInfo>();
	
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
				int push_id = intent.getIntExtra("push_id", 0);
				int progress = intent.getIntExtra("progress", 0);
				PushedApkInfo info = null;
				for(int i=0;i<app_list.size();i++){
					if(app_list.get(i).getPush_id() == push_id){
						info = app_list.get(i);
					}
				}
				if(info!=null&&info.getStatue()==1){
					LinearLayout layout = (LinearLayout) list.findViewWithTag(push_id);
					if(layout!=null){
						ProgressBar bar = (ProgressBar) layout.findViewById(R.id.progressbar);
						TextView valueText = (TextView) layout.findViewById(R.id.progress_value);
						if(bar!=null){
							bar.setProgress((80*progress)/100);
						}
						if(valueText!=null){
							valueText.setText((80*progress)/100+"%");
						}
					}
				}
				
			}else if(Global.ACTION_APK_RECIVED.equals(action)){
				initListDate();
				adpter.notifyDataSetChanged();
			}else if(Global.ACTION_DOWNL_GETSIZE_SUCESS.equals(action)){
				int push_id = intent.getIntExtra("push_id", 0);
				int size = intent.getIntExtra("file_size", 0);
				for(int i = 0; i<app_list.size(); i++){
					if(app_list.get(i).getPush_id() == push_id){
						app_list.get(i).setSize(size);
						app_list.get(i).setStatue(1);
					}
				}
				adpter.notifyDataSetChanged();
			}else if(Global.ACTION_DOWNLOAD_COMPLETE.equals(action)){
				int push_id = intent.getIntExtra("push_id", 0);
				Log.d(TAG, "push_id---->"+push_id);
				initListDate();
				adpter.notifyDataSetChanged();
				updateInstallProgress(push_id);
			}else if(Global.ACTION_DOWNL_INSTALL_SUCESS.equals(action)){
				int push_id = intent.getIntExtra("push_id", 0);
				handler.removeMessages(MESSAGE_UPDATE_INSTALLE_PROGRESS);
				PushedApkInfo info = null;
				for(int i=0;i<app_list.size();i++){
					if(app_list.get(i).getPush_id() == push_id){
						info = app_list.get(i);
					}
				}
				if(info!=null&&info.getStatue()==3){
					LinearLayout layout = (LinearLayout) list.findViewWithTag(push_id);
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
					app_list.remove(info);
					adpter.notifyDataSetChanged();
					Toast.makeText(context, info.getAppName() + "已安装成功", Toast.LENGTH_LONG).show();
				}
			}else if(Global.ACTION_DOWNLOAD_START.equals(action)){
				int push_id = intent.getIntExtra("push_id", 0);
				Log.d(TAG, "start id ---------->" + push_id);
				PushedApkInfo info = null;
				for(int i=0;i<app_list.size();i++){
					if(app_list.get(i).getPush_id() == push_id){
						info = app_list.get(i);
					}
				}
				info.setStatue(1);
			}else if(Global.ACTION_DOWNL_INSTALL_FAILE.equals(action)){
				int push_id = intent.getIntExtra("push_id", 0);
				PushedApkInfo info = null;
				for(int i=0;i<app_list.size();i++){
					if(app_list.get(i).getPush_id() == push_id){
						info = app_list.get(i);
					}
				}
				info.setStatue(4);
				Toast.makeText(context, info.getAppName() + "安装失败", Toast.LENGTH_LONG).show();
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
		initListDate();
		adpter = new PushedApkAdapter(ManagePushApkActivity.this,app_list);
		list.setAdapter(adpter);
		list.setOnItemClickListener(this);
		backButton.setOnClickListener(this);
		deleteButton.setOnClickListener(this);
		cancleButton.setOnClickListener(this);
		editeButton.setOnClickListener(this);
		IntentFilter filter = new IntentFilter(Global.ACTION_DOWNLOAD_PROGRESS);
		filter.addAction(Global.ACTION_DOWNL_GETSIZE_SUCESS);
		filter.addAction(Global.ACTION_APK_RECIVED);
		filter.addAction(Global.ACTION_DOWNLOAD_COMPLETE);
		filter.addAction(Global.ACTION_DOWNL_INSTALL_SUCESS);
		filter.addAction(Global.ACTION_DOWNL_INSTALL_FAILE);
		filter.addAction(Global.ACTION_DOWNLOAD_START);
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
			for(int i=0; i<app_list.size(); i++){
				if(app_list.get(i).getEdite_statue()==2){
					Intent deleteIntent = new Intent(Global.ACTION_DELETE_DOWNLOAD);
					deleteIntent.putExtra("push_id", app_list.get(i).getPush_id());
					sendBroadcast(deleteIntent);
					app_list.remove(i);
				}
			}
			adpter.notifyDataSetChanged();
			break;
		case R.id.edit_Button:
			layout1.setVisibility(View.GONE);
			layout2.setVisibility(View.VISIBLE);
			for(int i=0; i<app_list.size(); i++){
				PushedApkInfo info = app_list.get(i);
				if(info.getStatue()==1){
					info.setStatue(2);
					Intent intent = new Intent(Global.ACTION_DOWNLOAD_PAUSE);
					intent.putExtra("push_id", info.getPush_id());
					sendBroadcast(intent);
					LinearLayout layout = (LinearLayout) list.findViewWithTag(info.getPush_id());
					if(layout!=null){
						ProgressBar bar = (ProgressBar) layout.findViewById(R.id.progressbar);
						if(bar!=null){
							info.setProgress(bar.getProgress());
							bar.setProgress(0);
							bar.setSecondaryProgress(info.getProgress());
						}
					}
				}
				info.setEdite_statue(1);
			}
			adpter.notifyDataSetChanged();
			break;
		case R.id.cancel_Button:
			layout1.setVisibility(View.VISIBLE);
			layout2.setVisibility(View.GONE);
			for(int i=0; i<app_list.size(); i++){
				PushedApkInfo info = app_list.get(i);
				info.setEdite_statue(0);
			}
			adpter.notifyDataSetChanged();
			break;
		}
	}
	
	private void initListDate(){
		app_list.clear();
		for(int i=0; i<FayeService.infolist.size(); i++){
			PushedApkInfo info = new PushedApkInfo();
			PushedApkDownLoadInfo downloadInfo = FayeService.infolist.get(i);
			info.setPush_id(downloadInfo.getPush_id());
			info.setAppName(downloadInfo.getName());
			info.setStatue(downloadInfo.getDownload_state());
			info.setIcon(downloadInfo.getIcon());
			int progress = 0;
			if(downloadInfo.getFileSize()!=0){
				progress = ((downloadInfo.getCompeleteSize()*80)/downloadInfo.getFileSize());
				info.setProgress(progress);
				info.setSize(downloadInfo.getFileSize());
			}
			app_list.add(info); 
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		PushedApkInfo info = app_list.get(position);
		
		switch (info.getEdite_statue()) {
		case 0:
			switch (info.getStatue()) {
			case 1:
				info.setStatue(2);
				Intent intent = new Intent(Global.ACTION_DOWNLOAD_PAUSE);
				intent.putExtra("push_id", info.getPush_id());
				sendBroadcast(intent);
				LinearLayout layout = (LinearLayout) list.findViewWithTag(info.getPush_id());
				if(layout!=null){
					ProgressBar bar = (ProgressBar) layout.findViewById(R.id.progressbar);
					if(bar!=null){
						info.setProgress(bar.getProgress());
						bar.setProgress(0);
						bar.setSecondaryProgress(info.getProgress());
					}
				}
				adpter.notifyDataSetChanged();
				break;
			case 2:
				info.setStatue(0);
				Intent intentContinue = new Intent(Global.ACTION_DOWNLOAD_CONTINUE);
				intentContinue.putExtra("push_id", info.getPush_id());
				sendBroadcast(intentContinue);
				LinearLayout layout1 = (LinearLayout) list.findViewWithTag(info.getPush_id());
				if(layout1!=null){
					ProgressBar bar = (ProgressBar) layout1.findViewById(R.id.progressbar);
					if(bar!=null){
						bar.setProgress(info.getProgress());
						bar.setSecondaryProgress(0);
					}
				}
//				initListDate();
				adpter.notifyDataSetChanged();
				break;
			}
			break;
		case 1:
			info.setEdite_statue(2);
			adpter.notifyDataSetChanged();
			break;
		case 2:
			info.setEdite_statue(1);
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
	
	private void updateInstallProgress(int push_id){
		PushedApkInfo info = null;
		for(int i=0;i<app_list.size();i++){
			if(app_list.get(i).getPush_id() == push_id){
				info = app_list.get(i);
			}
		}
		Log.d(TAG, "updateInstallProgress---->");
		if(info!=null&&info.getStatue()==3){
			LinearLayout layout = (LinearLayout) list.findViewWithTag(push_id);
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
				msg.arg1 = push_id;
				handler.sendMessageDelayed(msg, 500);
				Log.d(TAG, "updateInstallProgress!!!!!!!!!!!!!!!!!!!!>");
			}
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
}
