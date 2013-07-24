package com.joyplus.tvhelper;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.joyplus.network.filedownload.manager.DownloadManager;
import com.joyplus.tvhelper.adapter.MovieDownLoadedAdapter;
import com.joyplus.tvhelper.adapter.MoviePlayHistoryAdapter;
import com.joyplus.tvhelper.adapter.PushedMovieDownLoadAdapter;
import com.joyplus.tvhelper.db.DBServices;
import com.joyplus.tvhelper.entity.CurrentPlayDetailData;
import com.joyplus.tvhelper.entity.MoviePlayHistoryInfo;
import com.joyplus.tvhelper.entity.PushedApkDownLoadInfo;
import com.joyplus.tvhelper.entity.PushedMovieDownLoadInfo;
import com.joyplus.tvhelper.faye.FayeService;
import com.joyplus.tvhelper.utils.Global;
import com.joyplus.tvhelper.utils.Log;

public class CloudDataDisplayActivity extends Activity implements OnItemClickListener, OnClickListener {

	private static final String TAG = "CloudDataDisplayActivity";
	
	private ListView listView;
	private PushedMovieDownLoadAdapter adpter_downloading;
	private MovieDownLoadedAdapter adpter_downloaded;
	private MoviePlayHistoryAdapter adpter_play_history;
	private DownloadManager downloadManager;
	private DBServices dbService;
	private int selectedIndex = 0;
	private Button title_playHistory, title_downloading, title_downloaded;
	private Button backButton, deleteButton, cancleButton, editeButton;
	private LinearLayout layout1, layout2;
	private List<PushedMovieDownLoadInfo> downloadedMovies;
	private List<MoviePlayHistoryInfo> playinfos;
	private MyApp app;
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(Global.ACTION_DOWNLOAD_PROGRESS.equals(action)){
				adpter_downloading.notifyDataSetChanged();
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
				adpter_downloading.notifyDataSetChanged();
			}else if(Global.ACTION_DOWNL_GETSIZE_SUCESS.equals(action)){
				Log.d(TAG, "CloudDataDisplayActivity onReceive" + action);
				adpter_downloading.notifyDataSetChanged();
			}else if(Global.ACTION_MOVIE_DOWNLOAD_COMPLETE.equals(action)){
				Log.d(TAG, "CloudDataDisplayActivity onReceive" + action);
//				int _id = intent.getIntExtra("_id", 0);
//				editeButton.setEnabled(false);
//				layout2.setVisibility(View.GONE);
//				layout1.setVisibility(View.VISIBLE);
				downloadedMovies = dbService.queryMovieDownLoadedInfos();
				adpter_downloaded = new MovieDownLoadedAdapter(CloudDataDisplayActivity.this, downloadedMovies);
				if(selectedIndex == 2){
					listView.setAdapter(adpter_downloading);
				}
				adpter_downloading.notifyDataSetChanged();
//				updateInstallProgress(_id);
			}else if(Global.ACTION_DOWNLOAD_START.equals(action)){
				Log.d(TAG, "CloudDataDisplayActivity onReceive" + action);
				adpter_downloading.notifyDataSetChanged();
			}else if(Global.ACTION_MOVIE_DOWNLOAD_FAILE.equals(action)){
				Log.d(TAG, "CloudDataDisplayActivity onReceive" + action);
				adpter_downloading.notifyDataSetChanged();
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clouddate_display);
		backButton = (Button) findViewById(R.id.back_Button);
		deleteButton = (Button) findViewById(R.id.del_Button);
		cancleButton = (Button) findViewById(R.id.cancel_Button);
		editeButton = (Button) findViewById(R.id.edit_Button);
		listView = (ListView) findViewById(R.id.movieList);
		title_playHistory = (Button) findViewById(R.id.title_play_history);
		title_downloading = (Button) findViewById(R.id.title_downloading);
		title_downloaded = (Button) findViewById(R.id.title_downloaded);
		layout1 = (LinearLayout) findViewById(R.id.fistBtn_group);
		layout2 = (LinearLayout) findViewById(R.id.secondBtn_group);
		title_playHistory.setOnClickListener(this);
		title_downloading.setOnClickListener(this);
		title_downloaded.setOnClickListener(this);
		backButton.setOnClickListener(this);
		deleteButton.setOnClickListener(this);
		cancleButton.setOnClickListener(this);
		editeButton.setOnClickListener(this);
		adpter_downloading = new PushedMovieDownLoadAdapter(this, FayeService.movieDownLoadInfos);
		dbService = DBServices.getInstance(this);
		downloadedMovies = dbService.queryMovieDownLoadedInfos();
		playinfos = dbService.queryMoviePlayHistoryList();
		adpter_downloaded = new MovieDownLoadedAdapter(this, downloadedMovies);
		adpter_play_history = new MoviePlayHistoryAdapter(this, playinfos);
		listView.setAdapter(adpter_play_history);
		selectedIndex = 0;
		listView.setOnItemClickListener(this);
		downloadManager = DownloadManager.getInstance(this);
		app = (MyApp) getApplication();
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
		Log.d(TAG, "selectedIndex -- >" + selectedIndex);
		switch (selectedIndex) {
		case 0:
			MoviePlayHistoryInfo playInfo = playinfos.get(position);
			switch (playInfo.getEdite_state()) {
			case MoviePlayHistoryInfo.EDITE_STATUE_NOMAL:
				
				CurrentPlayDetailData playDate = new CurrentPlayDetailData();
				Intent intent = new Intent(this,VideoPlayerJPActivity.class);
//				intent.putExtra("ID", json.getString("prod_id"));
//				playDate.prod_id = data.getString("id");
//				playDate.prod_type = Integer.valueOf(json.getString("prod_type"));
//				playDate.prod_type = playInfo.getPlay_type();
//				playDate.prod_name = json.getString("prod_name");
				if(playInfo.getPlay_type()==MoviePlayHistoryInfo.PLAY_TYPE_LOCAL){
					playDate.prod_url = playInfo.getLocal_url();
					playDate.prod_type = VideoPlayerJPActivity.TYPE_LOCAL;
				}else{
					playDate.prod_url = playInfo.getPush_url();
					playDate.prod_type = VideoPlayerJPActivity.TYPE_PUSH;
				}
				Log.d(TAG, "prod_type" + playDate.prod_type);
//				playDate.prod_src = json.getString("prod_src");
				playDate.prod_time = Math.round(playInfo.getPlayback_time()*1000);
//				playDate.prod_qua = Integer.valueOf(json.getString("prod_qua"));
//				if(playDate.prod_type==2||playDate.prod_type==3||playDate.prod_type==131){
//					if(json.has("prod_subname")){//旧版android 没有传递该参数
//						playDate.prod_sub_name = json.getString("prod_subname");
//					}else{
//						playDate.prod_type = -1;
//					}
//				}
				app.setmCurrentPlayDetailData(playDate);
				app.set_ReturnProgramView(null);
				startActivity(intent);
				break;
			case MoviePlayHistoryInfo.EDITE_STATUE_EDIT:
				playInfo.setEdite_state(PushedMovieDownLoadInfo.EDITE_STATUE_SELETED);
				adpter_play_history.notifyDataSetChanged();
				break;
			case MoviePlayHistoryInfo.EDITE_STATUE_SELETED:
				playInfo.setEdite_state(PushedMovieDownLoadInfo.EDITE_STATUE_EDIT);
				adpter_play_history.notifyDataSetChanged();
				break;
			}
			break;
		case 1:
			PushedMovieDownLoadInfo info = FayeService.movieDownLoadInfos.get(position);
			Log.d(TAG, "info  --getEdite_state() >" + info.getEdite_state());
			switch (info.getEdite_state()) {
			case PushedMovieDownLoadInfo.EDITE_STATUE_NOMAL:
				Log.d(TAG, "info  --getDownload_state() >" + info.getDownload_state());
				switch (info.getDownload_state()) {
				case PushedMovieDownLoadInfo.STATUE_DOWNLOADING:
				case PushedMovieDownLoadInfo.STATUE_WAITING_DOWNLOAD:
					downloadManager.pauseTask(info.getTast());
					dbService.updateMovieDownLoadInfo(info);
					info.setDownload_state(PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSEING);
					adpter_downloading.notifyDataSetChanged();
					break;
				case PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSE:
					info.setDownload_state(PushedMovieDownLoadInfo.STATUE_WAITING_DOWNLOAD);
					dbService.updateMovieDownLoadInfo(info);
					Intent intentpause = new Intent(Global.ACTION_MOVIE_DOWNLOAD_CONTINUE);
					sendBroadcast(intentpause);
					adpter_downloading.notifyDataSetChanged();
					break;
				}
				break;
			case PushedMovieDownLoadInfo.EDITE_STATUE_EDIT:
				info.setEdite_state(PushedMovieDownLoadInfo.EDITE_STATUE_SELETED);
				adpter_downloading.notifyDataSetChanged();
				break;
			case PushedMovieDownLoadInfo.EDITE_STATUE_SELETED:
				info.setEdite_state(PushedMovieDownLoadInfo.EDITE_STATUE_EDIT);
				adpter_downloading.notifyDataSetChanged();
				break;
			}
			break;
		case 2:
			PushedMovieDownLoadInfo info_complete = downloadedMovies.get(position);
			switch (info_complete.getEdite_state()) {
			case PushedMovieDownLoadInfo.EDITE_STATUE_NOMAL:
				Log.d(TAG, info_complete.getFile_path());
				CurrentPlayDetailData playDate = new CurrentPlayDetailData();
				Intent intent = new Intent(this,VideoPlayerJPActivity.class);
//				intent.putExtra("ID", json.getString("prod_id"));
//				playDate.prod_id = data.getString("id");
//				playDate.prod_type = Integer.valueOf(json.getString("prod_type"));
				playDate.prod_type = VideoPlayerJPActivity.TYPE_LOCAL;
//				playDate.prod_name = json.getString("prod_name");
				playDate.prod_url = info_complete.getFile_path();
//				playDate.prod_src = json.getString("prod_src");
//				playDate.prod_time = Math.round(Float.valueOf(json.getString("prod_time"))*1000);
//				playDate.prod_qua = Integer.valueOf(json.getString("prod_qua"));
//				if(playDate.prod_type==2||playDate.prod_type==3||playDate.prod_type==131){
//					if(json.has("prod_subname")){//旧版android 没有传递该参数
//						playDate.prod_sub_name = json.getString("prod_subname");
//					}else{
//						playDate.prod_type = -1;
//					}
//				}
				app.setmCurrentPlayDetailData(playDate);
				app.set_ReturnProgramView(null);
				startActivity(intent);
				break;
			case PushedMovieDownLoadInfo.EDITE_STATUE_EDIT:
				info_complete.setEdite_state(PushedMovieDownLoadInfo.EDITE_STATUE_SELETED);
				adpter_downloaded.notifyDataSetChanged();
				break;
			case PushedMovieDownLoadInfo.EDITE_STATUE_SELETED:
				info_complete.setEdite_state(PushedMovieDownLoadInfo.EDITE_STATUE_EDIT);
				adpter_downloaded.notifyDataSetChanged();
				break;

			}
			break;
		default:
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_downloaded:
			selectedIndex = 2;
			listView.setAdapter(adpter_downloaded);
			break;
		case R.id.title_downloading:
			selectedIndex = 1;
			listView.setAdapter(adpter_downloading);
			break;
		case R.id.title_play_history:
			selectedIndex = 0;
			listView.setAdapter(adpter_play_history);
			break;
		case R.id.back_Button:
			finish();
			break;
		case R.id.del_Button:
			switch (selectedIndex) {
			case 0:
				Iterator<MoviePlayHistoryInfo> iterator = null;
				iterator = playinfos.iterator();  
		         while(iterator.hasNext()) {  
		        	 MoviePlayHistoryInfo info = iterator.next();  
		             if(info.getEdite_state()==PushedApkDownLoadInfo.EDITE_STATUE_SELETED) {  
							dbService.deleteMoviePlayHistory(info);
							iterator.remove();  
		             }  
		               
		         }
				break;
			case 1:
				Iterator<PushedMovieDownLoadInfo> iterator_1 = FayeService.movieDownLoadInfos.iterator();  
		         while(iterator_1.hasNext()) {  
		        	 PushedMovieDownLoadInfo info = iterator_1.next();  
		             if(info.getEdite_state()==PushedApkDownLoadInfo.EDITE_STATUE_SELETED) {  
							File f = new File(info.getFile_path());
							if(f!=null&&f.exists()){
								f.delete();
							}
							dbService.deleteMovieDownLoadInfo(info);
							iterator_1.remove();  
		             }  
		               
		         }
				break;
			case 2:
				Iterator<PushedMovieDownLoadInfo> iterator_2 = downloadedMovies.iterator();  
				while(iterator_2.hasNext()) {  
		        	 PushedMovieDownLoadInfo info = iterator_2.next();  
		             if(info.getEdite_state()==PushedApkDownLoadInfo.EDITE_STATUE_SELETED) {  
							File f = new File(info.getFile_path());
							if(f!=null&&f.exists()){
								f.delete();
							}
							dbService.deleteMovieDownLoadInfo(info);
							iterator_2.remove();  
		             }  
		               
		         }
				break;
			}
			((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
			layout2.setVisibility(View.GONE);
			layout1.setVisibility(View.VISIBLE);
			break;
		case R.id.edit_Button:
			layout1.setVisibility(View.GONE);
			layout2.setVisibility(View.VISIBLE);
			switch (selectedIndex) {
			case 0:
				for(int i=0; i<playinfos.size(); i++){
					MoviePlayHistoryInfo info = playinfos.get(i);
					info.setEdite_state(PushedMovieDownLoadInfo.EDITE_STATUE_EDIT);
				}
				break;
			case 1:
				for(int i=0; i<FayeService.movieDownLoadInfos.size(); i++){
					PushedMovieDownLoadInfo info = FayeService.movieDownLoadInfos.get(i);
					if(info.getDownload_state()==PushedMovieDownLoadInfo.STATUE_DOWNLOADING){
						downloadManager.pauseTask(info.getTast());
						info.setDownload_state(PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSE);
					}
					info.setEdite_state(PushedMovieDownLoadInfo.EDITE_STATUE_EDIT);
				}
				break;
			case 2:
				for(int i=0; i<downloadedMovies.size(); i++){
					PushedMovieDownLoadInfo info = downloadedMovies.get(i);
					info.setEdite_state(PushedApkDownLoadInfo.EDITE_STATUE_EDIT);
				}
				break;
			}
			((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
			cancleButton.requestFocus();
			break;
		case R.id.cancel_Button:
			layout1.setVisibility(View.VISIBLE);
			layout2.setVisibility(View.GONE);
			switch (selectedIndex) {
			case 0:
				for(int i=0; i<playinfos.size(); i++){
					MoviePlayHistoryInfo info = playinfos.get(i);
					info.setEdite_state(PushedMovieDownLoadInfo.EDITE_STATUE_NOMAL);
				}
				break;
			case 1:
				for(int i=0; i<FayeService.movieDownLoadInfos.size(); i++){
					PushedMovieDownLoadInfo info = FayeService.movieDownLoadInfos.get(i);
					info.setEdite_state(PushedMovieDownLoadInfo.EDITE_STATUE_NOMAL);
				}
				break;
			case 2:
				for(int i=0; i<downloadedMovies.size(); i++){
					PushedMovieDownLoadInfo info = downloadedMovies.get(i);
					info.setEdite_state(PushedApkDownLoadInfo.EDITE_STATUE_NOMAL);
				}
				break;
			}
			((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		playinfos = dbService.queryMoviePlayHistoryList();
		adpter_play_history = new MoviePlayHistoryAdapter(this, playinfos);
		if(selectedIndex == 0){
			listView.setAdapter(adpter_play_history);
		}
		super.onResume();
	}
}
