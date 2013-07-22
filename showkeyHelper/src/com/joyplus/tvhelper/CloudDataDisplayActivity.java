package com.joyplus.tvhelper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.joyplus.network.filedownload.manager.DownloadManager;
import com.joyplus.tvhelper.adapter.PushedMovieDownLoadAdapter;
import com.joyplus.tvhelper.db.DBServices;
import com.joyplus.tvhelper.entity.PushedMovieDownLoadInfo;
import com.joyplus.tvhelper.faye.FayeService;
import com.joyplus.tvhelper.utils.Global;
import com.joyplus.tvhelper.utils.Log;

public class CloudDataDisplayActivity extends Activity implements OnItemClickListener {

	private static final String TAG = "CloudDataDisplayActivity";
	
	private ListView listView;
	private PushedMovieDownLoadAdapter adpter;
	private DownloadManager downloadManager;
	private DBServices dbService;
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(Global.ACTION_DOWNLOAD_PROGRESS.equals(action)){
				adpter.notifyDataSetChanged();
			}else if(Global.ACTION_DOWNLOAD_RECIVED.equals(action)){
//				Log.d(TAG, "receve --- > " + Global.ACTION_APK_RECIVED);
//				if(FayeService.userPushApkInfos.size() == 0){
//					editeButton.setEnabled(false);
//				}else{
//					editeButton.setEnabled(true);
//				}
//				layout2.setVisibility(View.GONE);
//				layout1.setVisibility(View.VISIBLE);
//				backButton.requestFocus();
				adpter.notifyDataSetChanged();
			}else if(Global.ACTION_DOWNL_GETSIZE_SUCESS.equals(action)){
				Log.d(TAG, "CloudDataDisplayActivity onReceive" + action);
				adpter.notifyDataSetChanged();
			}else if(Global.ACTION_MOVIE_DOWNLOAD_COMPLETE.equals(action)){
				Log.d(TAG, "CloudDataDisplayActivity onReceive" + action);
//				int _id = intent.getIntExtra("_id", 0);
//				editeButton.setEnabled(false);
//				layout2.setVisibility(View.GONE);
//				layout1.setVisibility(View.VISIBLE);
				adpter.notifyDataSetChanged();
//				updateInstallProgress(_id);
			}else if(Global.ACTION_DOWNLOAD_START.equals(action)){
				Log.d(TAG, "CloudDataDisplayActivity onReceive" + action);
				adpter.notifyDataSetChanged();
			}else if(Global.ACTION_MOVIE_DOWNLOAD_FAILE.equals(action)){
				Log.d(TAG, "CloudDataDisplayActivity onReceive" + action);
				adpter.notifyDataSetChanged();
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clouddate_display);
		listView = (ListView) findViewById(R.id.movieList);
		adpter = new PushedMovieDownLoadAdapter(this, FayeService.movieDownLoadInfos);
		dbService = DBServices.getInstance(this);
		listView.setAdapter(adpter);
		listView.setOnItemClickListener(this);
		downloadManager = DownloadManager.getInstance(this);
		IntentFilter filter = new IntentFilter(Global.ACTION_DOWNLOAD_PROGRESS);
		filter.addAction(Global.ACTION_DOWNL_GETSIZE_SUCESS);
		filter.addAction(Global.ACTION_DOWNLOAD_RECIVED);
		filter.addAction(Global.ACTION_MOVIE_DOWNLOAD_FAILE);
		filter.addAction(Global.ACTION_MOVIE_DOWNLOAD_COMPLETE);
		filter.addAction(Global.ACTION_DOWNLOAD_START);
		registerReceiver(receiver, filter);
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		PushedMovieDownLoadInfo info = FayeService.movieDownLoadInfos.get(position);
		
		switch (info.getEdite_state()) {
		case PushedMovieDownLoadInfo.EDITE_STATUE_NOMAL:
			switch (info.getDownload_state()) {
			case PushedMovieDownLoadInfo.STATUE_DOWNLOADING:
			case PushedMovieDownLoadInfo.STATUE_WAITING_DOWNLOAD:
				downloadManager.pauseTask(info.getTast());
				dbService.updateMovieDownLoadInfo(info);
				info.setDownload_state(PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSEING);
//				Intent intentContinue = new Intent(Global.ACTION_DOWNLOAD_PAUSE);
//				sendBroadcast(intentContinue);
				adpter.notifyDataSetChanged();
				break;
			case PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSE:
				info.setDownload_state(PushedMovieDownLoadInfo.STATUE_WAITING_DOWNLOAD);
				dbService.updateMovieDownLoadInfo(info);
				Intent intentpause = new Intent(Global.ACTION_MOVIE_DOWNLOAD_CONTINUE);
				sendBroadcast(intentpause);
				adpter.notifyDataSetChanged();
				break;
			}
			break;
		case PushedMovieDownLoadInfo.EDITE_STATUE_EDIT:
			info.setEdite_state(PushedMovieDownLoadInfo.EDITE_STATUE_SELETED);
			adpter.notifyDataSetChanged();
			break;
		case PushedMovieDownLoadInfo.EDITE_STATUE_SELETED:
			info.setEdite_state(PushedMovieDownLoadInfo.EDITE_STATUE_EDIT);
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
}
