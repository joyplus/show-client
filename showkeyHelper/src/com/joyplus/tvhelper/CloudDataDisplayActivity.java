package com.joyplus.tvhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.joyplus.JoyplusMediaPlayerActivity;
import com.joyplus.network.filedownload.manager.DownloadManager;
import com.joyplus.tvhelper.adapter.MovieDownLoadedAdapter;
import com.joyplus.tvhelper.adapter.MoviePlayHistoryAdapter;
import com.joyplus.tvhelper.adapter.PushedMovieDownLoadAdapter;
import com.joyplus.tvhelper.db.DBServices;
import com.joyplus.tvhelper.entity.BTEpisode;
import com.joyplus.tvhelper.entity.CurrentPlayDetailData;
import com.joyplus.tvhelper.entity.MoviePlayHistoryInfo;
import com.joyplus.tvhelper.entity.PushedApkDownLoadInfo;
import com.joyplus.tvhelper.entity.PushedMovieDownLoadInfo;
import com.joyplus.tvhelper.faye.FayeService;
import com.joyplus.tvhelper.ui.NotificationView;
import com.joyplus.tvhelper.utils.Constant;
import com.joyplus.tvhelper.utils.Global;
import com.joyplus.tvhelper.utils.HttpTools;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.PreferencesUtils;
import com.joyplus.tvhelper.utils.Utils;
import com.umeng.analytics.MobclickAgent;

public class CloudDataDisplayActivity extends Activity implements OnItemClickListener, OnClickListener, OnGroupClickListener, OnChildClickListener, OnGroupExpandListener {

	private static final String TAG = "CloudDataDisplayActivity";
	
	private ExpandableListView listView;
	private PushedMovieDownLoadAdapter adpter_downloading;
	private MovieDownLoadedAdapter adpter_downloaded;
	private MoviePlayHistoryAdapter adpter_play_history;
	private DownloadManager downloadManager;
	private DBServices dbService;
	private int selectedIndex = 0;
	private Button title_playHistory, title_downloading, title_downloaded;
	private Button backButton, deleteButton, cancleButton, editeButton, clearButton;
	private LinearLayout layout1, layout2;
	private List<PushedMovieDownLoadInfo> downloadedMovies;
	private List<MoviePlayHistoryInfo> playinfos;
	private ImageView defult_img;
	private MyApp app;
	private NotificationView connectStatueText;
	
	private ExecutorService pool = Executors.newFixedThreadPool(5);
	
	private Button selectedButon;
	private boolean isFirstResume = true;
	
//	private int expandGroupIndex = -1;
	
	private static final int MESSAGE_UPDATE = 0;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_UPDATE:
				playinfos = dbService.queryMoviePlayHistoryList();
				adpter_play_history = new MoviePlayHistoryAdapter(CloudDataDisplayActivity.this, playinfos);
				if(selectedIndex == 0){
					listView.setAdapter(adpter_play_history);
					listView.requestFocus();
					listView.setSelection(0);
				}
				break;

			default:
				break;
			}
		};
	};
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(Global.ACTION_DOWNLOAD_PROGRESS.equals(action)){
				adpter_downloading.notifyDataSetChanged();
			}else if(Global.ACTION_DOWNLOAD_RECIVED.equals(action)){
//				Log.d(TAG, "receve --- > " + Global.ACTION_APK_RECIVED);
				if(FayeService.userPushApkInfos.size() == 0){
					editeButton.setEnabled(false);
				}else{
					editeButton.setEnabled(true);
				}
				layout2.setVisibility(View.GONE);
				layout1.setVisibility(View.VISIBLE);
				updateEditBottn();
//				backButton.requestFocus();
				adpter_downloading.notifyDataSetChanged();
			}else if(Global.ACTION_DOWNL_GETSIZE_SUCESS.equals(action)){
//				Log.d(TAG, "CloudDataDisplayActivity onReceive" + action);
//				adpter_downloading.notifyDataSetChanged();
			}else if(Global.ACTION_MOVIE_DOWNLOAD_COMPLETE.equals(action)){
				Log.d(TAG, "CloudDataDisplayActivity onReceive" + action);
//				int _id = intent.getIntExtra("_id", 0);
//				editeButton.setEnabled(false);
				layout2.setVisibility(View.GONE);
				layout1.setVisibility(View.VISIBLE);
				downloadedMovies = dbService.queryMovieDownLoadedInfos();
				adpter_downloaded = new MovieDownLoadedAdapter(CloudDataDisplayActivity.this, downloadedMovies);
				if(selectedIndex == 2){
					listView.setAdapter(adpter_downloading);
				}
				updateEditBottn();
				adpter_downloading.notifyDataSetChanged();
//				updateInstallProgress(_id);
			}else if(Global.ACTION_DOWNLOAD_START.equals(action)){
				Log.d(TAG, "CloudDataDisplayActivity onReceive" + action);
				adpter_downloading.notifyDataSetChanged();
			}else if(Global.ACTION_MOVIE_DOWNLOAD_FAILE.equals(action)){
				Log.d(TAG, "CloudDataDisplayActivity onReceive" + action);
				adpter_downloading.notifyDataSetChanged();
			}else if(Global.ACTION_CONNECT_SUCCESS_MAIN.equals(action)){
				connectStatueText.setText("已连接");
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						connectStatueText.setText("");
					}
				}, 2000);
			}else if(Global.ACTION_DISCONNECT_SERVER_MAIN.equals(action)){
				if(!"正在连接服务器···".equals(connectStatueText.getText())){
					connectStatueText.setText("正在连接服务器···");
					handler.removeCallbacksAndMessages(null);
				}
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
		clearButton = (Button) findViewById(R.id.clear_Button);
		listView = (ExpandableListView) findViewById(R.id.movieList);
		title_playHistory = (Button) findViewById(R.id.title_play_history);
		title_downloading = (Button) findViewById(R.id.title_downloading);
		title_downloaded = (Button) findViewById(R.id.title_downloaded);
		layout1 = (LinearLayout) findViewById(R.id.fistBtn_group);
		layout2 = (LinearLayout) findViewById(R.id.secondBtn_group);
		defult_img = (ImageView)findViewById(R.id.defult_img);
		title_playHistory.setOnClickListener(this);
		title_downloading.setOnClickListener(this);
		title_downloaded.setOnClickListener(this);
		backButton.setOnClickListener(this);
		deleteButton.setOnClickListener(this);
		cancleButton.setOnClickListener(this);
		editeButton.setOnClickListener(this);
		clearButton.setOnClickListener(this);
		adpter_downloading = new PushedMovieDownLoadAdapter(this, FayeService.movieDownLoadInfos);
		dbService = DBServices.getInstance(this);
		downloadedMovies = dbService.queryMovieDownLoadedInfos();
		playinfos = dbService.queryMoviePlayHistoryList();
		adpter_downloaded = new MovieDownLoadedAdapter(this, downloadedMovies);
		adpter_play_history = new MoviePlayHistoryAdapter(this, playinfos);
		listView.setAdapter(adpter_play_history);
		listView.setGroupIndicator(null);
		listView.setOnGroupClickListener(this);
		listView.setOnChildClickListener(this);
		listView.setOnGroupExpandListener(this);
		selectedIndex = 0;
		selectedButon = title_playHistory;
		selectedButon.setBackgroundResource(R.drawable.highlight);
		selectedButon.setTextColor(Color.BLACK);
		connectStatueText = (NotificationView) findViewById(R.id.statue_connect);
		if(MainActivity.isConnect){
			connectStatueText.setText("");
		}else{
			connectStatueText.setText("正在连接服务器···");
		}
//		listView.setOnItemClickListener(this);
		downloadManager = DownloadManager.getInstance(this);
		updateEditBottn();
		app = (MyApp) getApplication();
		IntentFilter filter = new IntentFilter(Global.ACTION_DOWNLOAD_PROGRESS);
		filter.addAction(Global.ACTION_DOWNL_GETSIZE_SUCESS);
		filter.addAction(Global.ACTION_DOWNLOAD_RECIVED);
		filter.addAction(Global.ACTION_MOVIE_DOWNLOAD_FAILE);
		filter.addAction(Global.ACTION_MOVIE_DOWNLOAD_COMPLETE);
		filter.addAction(Global.ACTION_DOWNLOAD_START);
		filter.addAction(Global.ACTION_CONNECT_SUCCESS_MAIN);
		filter.addAction(Global.ACTION_DISCONNECT_SERVER_MAIN);
		registerReceiver(receiver, filter);
		getLostUserPushMovie();
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.d(TAG, "selectedIndex -- >" + selectedIndex);
		switch (selectedIndex) {
		case 0:
			MoviePlayHistoryInfo playInfo = playinfos.get(position);
			switch (playInfo.getEdite_state()) {
			case MoviePlayHistoryInfo.EDITE_STATUE_NOMAL:
				if(playInfo.getPlay_type()==MoviePlayHistoryInfo.PLAY_TYPE_BAIDU){
//					if(playInfo.getRecivedDonwLoadUrls().startsWith("bdhd")){
					playInfo.setCreat_time(System.currentTimeMillis());
					dbService.updateMoviePlayHistory(playInfo);
						Intent intent_baidu = new Intent(this,PlayBaiduActivity.class);
						intent_baidu.putExtra("url", playInfo.getRecivedDonwLoadUrls());
						intent_baidu.putExtra("name", playInfo.getName());
						intent_baidu.putExtra("push_url", playInfo.getPush_url());
						intent_baidu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent_baidu);
//					}
				}else{
					CurrentPlayDetailData playDate = new CurrentPlayDetailData();
//					Intent intent = new Intent(this,VideoPlayerJPActivity.class);
					Intent intent = Utils.getIntent(this);
//					intent.putExtra("ID", json.getString("prod_id"));
//					playDate.prod_id = data.getString("id");
//					playDate.prod_type = Integer.valueOf(json.getString("prod_type"));
//					playDate.prod_type = playInfo.getPlay_type();
					playDate.prod_name = playInfo.getName();
					if(playInfo.getPlay_type()==MoviePlayHistoryInfo.PLAY_TYPE_LOCAL){
						playDate.prod_url = playInfo.getLocal_url();
						playDate.prod_type = VideoPlayerJPActivity.TYPE_LOCAL;
					}else{
//						playDate.prod_url = playInfo.getDownload_url();
						playDate.prod_type = VideoPlayerJPActivity.TYPE_PUSH;
					}
					playDate.obj = playInfo;
					Log.d(TAG, "prod_type" + playDate.prod_type);
//					playDate.prod_src = json.getString("prod_src");
					playDate.prod_time = Math.round(playInfo.getPlayback_time()*1000);
					playDate.prod_qua = playInfo.getDefination();
					playDate.isOnline = false;
//					if(playDate.prod_type==2||playDate.prod_type==3||playDate.prod_type==131){
//						if(json.has("prod_subname")){//旧版android 没有传递该参数
//							playDate.prod_sub_name = json.getString("prod_subname");
//						}else{
//							playDate.prod_type = -1;
//						}
//					}
					isFirstResume = false;
					app.setmCurrentPlayDetailData(playDate);
					app.set_ReturnProgramView(null);
					startActivity(intent);
				}
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
					downloadManager.pauseTask(info.getTast());
					info.setDownload_state(PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSEING);
					dbService.updateMovieDownLoadInfo(info);
					adpter_downloading.notifyDataSetChanged();
					break;
				case PushedMovieDownLoadInfo.STATUE_WAITING_DOWNLOAD:
					downloadManager.pauseTask(info.getTast());
					info.setDownload_state(PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSE);
					dbService.updateMovieDownLoadInfo(info);
					adpter_downloading.notifyDataSetChanged();
					
					break;
				case PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSE:
					Log.i(TAG, "PushedMovieDownLoadInfo.STATUE_DOWNLOAD_PAUSE--->Edit");
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
				
				
				MoviePlayHistoryInfo play_info = dbService.queryMoviePlayHistoryByLoaclUrl(info_complete.getFile_path());
				
				if(play_info == null){
					play_info = new MoviePlayHistoryInfo();
					play_info.setName(info_complete.getName());
					play_info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_LOCAL);
					play_info.setLocal_url(info_complete.getFile_path());
					play_info.setCreat_time(System.currentTimeMillis());
					play_info.setId((int) dbService.insertMoviePlayHistory(play_info));
				}
				
				CurrentPlayDetailData playDate = new CurrentPlayDetailData();
//				Intent intent = new Intent(this,VideoPlayerJPActivity.class);
				Intent intent = Utils.getIntent(this);
//				intent.putExtra("ID", json.getString("prod_id"));
//				playDate.prod_id = data.getString("id");
//				playDate.prod_type = Integer.valueOf(json.getString("prod_type"));
				playDate.prod_type = VideoPlayerJPActivity.TYPE_LOCAL;
				playDate.prod_name = play_info.getName();
//				playDate.prod_name = json.getString("prod_name");
				playDate.prod_url = play_info.getLocal_url();
				playDate.obj = play_info;
//				playDate.prod_src = json.getString("prod_src");
				if(play_info.getDuration()-play_info.getPlayback_time()<=Constant.END_TIME){
					playDate.prod_time = 0;
				}else{
					playDate.prod_time = Math.round(play_info.getPlayback_time()*1000);
				}
				playDate.isOnline = false;
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
			selectedButon.setBackgroundResource(R.drawable.bg_title_setting_selector);
			selectedButon.setTextColor(getResources().getColorStateList(R.color.setting_title_selector));
			selectedButon = title_downloaded;
			selectedButon.setBackgroundResource(R.drawable.highlight);
			selectedButon.setTextColor(Color.BLACK);
			downloadedMovies = dbService.queryMovieDownLoadedInfos();
			adpter_downloaded = new MovieDownLoadedAdapter(CloudDataDisplayActivity.this, downloadedMovies);
			listView.setAdapter(adpter_downloaded);
			layout2.setVisibility(View.GONE);
			layout1.setVisibility(View.VISIBLE);
			updateEditBottn();
			break;
		case R.id.title_downloading:
			for(PushedMovieDownLoadInfo info : FayeService.movieDownLoadInfos){
				info.setEdite_state(PushedMovieDownLoadInfo.EDITE_STATUE_NOMAL);
			}
			selectedIndex = 1;
			selectedButon.setBackgroundResource(R.drawable.bg_title_setting_selector);
			selectedButon.setTextColor(getResources().getColorStateList(R.color.setting_title_selector));
			selectedButon = title_downloading;
			selectedButon.setBackgroundResource(R.drawable.highlight);
			selectedButon.setTextColor(Color.BLACK);
			layout2.setVisibility(View.GONE);
			layout1.setVisibility(View.VISIBLE);
			listView.setAdapter(adpter_downloading);
			updateEditBottn();
			break;
		case R.id.title_play_history:
			playinfos = dbService.queryMoviePlayHistoryList();
			adpter_play_history = new MoviePlayHistoryAdapter(CloudDataDisplayActivity.this, playinfos);
			selectedIndex = 0;
			selectedButon.setBackgroundResource(R.drawable.bg_title_setting_selector);
			selectedButon.setTextColor(getResources().getColorStateList(R.color.setting_title_selector));
			selectedButon = title_playHistory;
			selectedButon.setBackgroundResource(R.drawable.highlight);
			selectedButon.setTextColor(Color.BLACK);
			listView.setAdapter(adpter_play_history);
			layout2.setVisibility(View.GONE);
			layout1.setVisibility(View.VISIBLE);
			updateEditBottn();
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
		             if(info.getEdite_state()==MoviePlayHistoryInfo.EDITE_STATUE_SELETED) {  
		            	 info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_HIDE);
		            	 dbService.updateMoviePlayHistory(info);
						 iterator.remove();  
		             }else{
		            	 info.setEdite_state(MoviePlayHistoryInfo.EDITE_STATUE_NOMAL);
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
		             }else{
		            	 info.setEdite_state(PushedMovieDownLoadInfo.EDITE_STATUE_NOMAL);
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
		             } else{
		            	 info.setEdite_state(PushedMovieDownLoadInfo.EDITE_STATUE_NOMAL);
		             } 
		               
		         }
				break;
			}
			((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
			layout2.setVisibility(View.GONE);
			layout1.setVisibility(View.VISIBLE);
			updateEditBottn();
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
					if(info.getDownload_state()==PushedMovieDownLoadInfo.STATUE_DOWNLOADING
							||info.getDownload_state()==PushedMovieDownLoadInfo.STATUE_WAITING_DOWNLOAD){
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
			for(int i = 0; i < adpter_play_history.getGroupCount(); i++){
				   listView.collapseGroup(i);
			}
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
		case R.id.clear_Button:
			final Dialog dialog = new AlertDialog.Builder(this).create();
			dialog.show();
			LayoutInflater inflater = LayoutInflater.from(this);
			View view = inflater.inflate(R.layout.layout_clear_dialog, null);
			Button delButton = (Button) view.findViewById(R.id.btn_ok);
			Button cancelButton = (Button) view.findViewById(R.id.btn_canle);
			dialog.setContentView(view);
			cancelButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			delButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					Iterator<MoviePlayHistoryInfo> iterator = null;
					iterator = playinfos.iterator();  
			         while(iterator.hasNext()) {  
			        	 MoviePlayHistoryInfo info = iterator.next();  
		            	 info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_HIDE);
		            	 dbService.updateMoviePlayHistory(info);
						 iterator.remove();  
			         }
			         updateEditBottn();
				}
			});
			break;
		default:
			break;
		}
	}
	
	private void updateEditBottn(){
		if(((BaseAdapter)listView.getAdapter()).getCount()>0){
			editeButton.setVisibility(View.VISIBLE);
			clearButton.setVisibility(View.VISIBLE);
			listView.requestFocus();
			defult_img.setVisibility(View.GONE);
		}else{
			editeButton.setVisibility(View.INVISIBLE);
			clearButton.setVisibility(View.INVISIBLE);
			defult_img.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		int selected = listView.getSelectedItemPosition();
		playinfos = dbService.queryMoviePlayHistoryList();
		adpter_play_history = new MoviePlayHistoryAdapter(this, playinfos);
		if(selectedIndex == 0){
			listView.setAdapter(adpter_play_history);
			listView.requestFocus();
			if(!isFirstResume){
				listView.expandGroup(0, true);
			}
			listView.setSelection(0);
		}else{
			listView.requestFocus();
			listView.setSelection(selected);
		}
		updateEditBottn();
		super.onResume();
		
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	
	private void getLostUserPushMovie(){
		pool.execute(new Runnable() {	
			@Override
			public void run() {
				// TODO Auto-generated method stub
//				infolist = services.GetPushedApklist(infolist);
				String url = Constant.BASE_URL + "/pushVodHistories?app_key=" + Constant.APPKEY 
						+ "&mac_address=" + Utils.getMacAdd(CloudDataDisplayActivity.this) 
						+ "&page_num=" + 1
						+ "&page_size=" + 50;
				Log.d(TAG, url);
				String str = HttpTools.get(CloudDataDisplayActivity.this, url);
				Log.d(TAG, "pushMsgHistories response-->" + str);
				try {
					JSONArray array = new JSONArray(str);
					Log.d(TAG, "miss length ---------------------------->" + array.length());
					for(int i=0; i<array.length(); i++){
						try {
							JSONObject item = array.getJSONObject(i);
							int push_id = item.getInt("id");
//							String push_name = URLDecoder.decode(item.getString("name"), "utf-8");
							String push_name = item.getString("name");
//							String push_url = URLDecoder.decode(item.getString("playurl"), "utf-8");
							String push_url = item.getString("playurl");
							String push_play_url = item.getString("downurl");
							String time_token = item.getString("time_token");
							String md5_code = item.getString("md5_code");
							List<BTEpisode> es = null; 
							if(item.has("prodName")){
								es = new ArrayList<BTEpisode>();
								JSONArray array_name = item.getJSONArray("prodName");
								Log.d(TAG, array_name.toString());
								for(int j = 0; j< array_name.length() ; j++){
									BTEpisode e = new BTEpisode();
									e.setDefination(Constant.DEFINATION_HD2);
									e.setName(array_name.getString(j));
									es.add(e);
									Log.d(TAG, array_name.getString(j));
								}
								
							}
							int type = item.getInt("type");
							if(PreferencesUtils.getPincodeMd5(CloudDataDisplayActivity.this)!=null &&PreferencesUtils.getPincodeMd5(CloudDataDisplayActivity.this).equals(md5_code)){
								if(type == 5){//漏掉的播放
									MoviePlayHistoryInfo play_info = dbService.hasMoviePlayHistory(MoviePlayHistoryInfo.PLAY_TYPE_ONLINE, push_url);
									if(play_info == null){
										play_info = new MoviePlayHistoryInfo();
										play_info.setName(push_name);
										play_info.setPush_id(push_id);
										play_info.setPush_url(push_url);
										play_info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_ONLINE);
										play_info.setRecivedDonwLoadUrls(push_play_url);
										play_info.setDefination(Constant.DEFINATION_HD2);
										play_info.setCreat_time(System.currentTimeMillis());
										play_info.setTime_token(time_token+",");
										if(es!=null && es.size()>0){
											play_info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_BT_EPISODES);
											play_info.setBtEpisodes(es);
										}
										play_info.setId((int)dbService.insertMoviePlayHistory(play_info));
									}else{
										play_info.setDefination(Constant.DEFINATION_HD2);
										play_info.setName(push_name);
										play_info.setRecivedDonwLoadUrls(push_play_url);
										play_info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_ONLINE);
										if(play_info.getTime_token()==null){
											play_info.setTime_token("");
										}
										play_info.setTime_token(play_info.getTime_token() + time_token+",");
										if(es!=null && es.size()>0){
											play_info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_BT_EPISODES);
											play_info.setBtEpisodes(es);
										}
//										play_info.setPush_id(push_id);
										dbService.updateMoviePlayHistory(play_info);
//										if(play_info.getTime_token()==null){
//											play_info.setTime_token("");
//										}
//										play_info.setTime_token(play_info.getTime_token() + time_token+",");
//										services.updateMoviePlayHistory(play_info);
									}
								}else if(type == 6){//漏掉的下载
									
								}else if(type == 11){
									MoviePlayHistoryInfo play_info = dbService.hasMoviePlayHistory(MoviePlayHistoryInfo.PLAY_TYPE_ONLINE, push_url);
									if(play_info == null){
										play_info = new MoviePlayHistoryInfo();
										play_info.setName(push_name);
										play_info.setPush_id(push_id);
										play_info.setPush_url(push_url);
										play_info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_BAIDU);
										play_info.setRecivedDonwLoadUrls(push_play_url);
										play_info.setDefination(Constant.DEFINATION_HD2);
										play_info.setCreat_time(System.currentTimeMillis());
										play_info.setTime_token(time_token+",");
										play_info.setId((int)dbService.insertMoviePlayHistory(play_info));
									}else{
										if(play_info.getTime_token()==null){
											play_info.setTime_token("");
										}
										play_info.setTime_token(play_info.getTime_token() + time_token+",");
										dbService.updateMoviePlayHistory(play_info);
									}
								}
							}
							updateMovieHistory(push_id);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendEmptyMessage(MESSAGE_UPDATE);
			}
			
		});
	}
	
	private void updateMovieHistory(final int id){
		
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String url = Constant.BASE_URL + "/updateVodHistory?app_key=" + Constant.APPKEY 
						+ "&mac_address=" + Utils.getMacAdd(CloudDataDisplayActivity.this)
						+ "&id=" + id;
				Log.d(TAG, url);
				String str = HttpTools.get(CloudDataDisplayActivity.this, url);
				Log.d(TAG, "updateHistory response-->" + str);
			}
		});
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		// TODO Auto-generated method stub
		MoviePlayHistoryInfo playInfo = playinfos.get(groupPosition);
		if(playInfo.getPlay_type() == MoviePlayHistoryInfo.PLAY_TYPE_BT_EPISODES){
			switch (playInfo.getEdite_state()) {
			case MoviePlayHistoryInfo.EDITE_STATUE_NOMAL:
				
				return false;
			case MoviePlayHistoryInfo.EDITE_STATUE_EDIT:
				playInfo.setEdite_state(PushedMovieDownLoadInfo.EDITE_STATUE_SELETED);
				adpter_play_history.notifyDataSetChanged();
				return true;
			case MoviePlayHistoryInfo.EDITE_STATUE_SELETED:
				playInfo.setEdite_state(PushedMovieDownLoadInfo.EDITE_STATUE_EDIT);
				adpter_play_history.notifyDataSetChanged();
				return true;
			};
			return false;
		}else{
			
			switch (playInfo.getEdite_state()) {
			case MoviePlayHistoryInfo.EDITE_STATUE_NOMAL:
				if(playInfo.getPlay_type()==MoviePlayHistoryInfo.PLAY_TYPE_BAIDU){
//					if(playInfo.getRecivedDonwLoadUrls().startsWith("bdhd")){
					playInfo.setCreat_time(System.currentTimeMillis());
					dbService.updateMoviePlayHistory(playInfo);
						Intent intent_baidu = new Intent(this,PlayBaiduActivity.class);
						intent_baidu.putExtra("url", playInfo.getRecivedDonwLoadUrls());
						intent_baidu.putExtra("name", playInfo.getName());
						intent_baidu.putExtra("push_url", playInfo.getPush_url());
						intent_baidu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent_baidu);
//					}
				}else{
					CurrentPlayDetailData playDate = new CurrentPlayDetailData();
//					Intent intent = new Intent(this,JoyplusMediaPlayerActivity.class);
					Intent intent = Utils.getIntent(this);
//					intent.putExtra("ID", json.getString("prod_id"));
//					playDate.prod_id = data.getString("id");
//					playDate.prod_type = Integer.valueOf(json.getString("prod_type"));
//					playDate.prod_type = playInfo.getPlay_type();
					playDate.prod_name = playInfo.getName();
					if(playInfo.getPlay_type()==MoviePlayHistoryInfo.PLAY_TYPE_LOCAL){
//						playDate.prod_url = playInfo.getLocal_url();
//						playDate.prod_type = JoyplusMediaPlayerActivity.TYPE_LOCAL;
					}else{
//						playDate.prod_url = playInfo.getDownload_url();
						playDate.prod_type = JoyplusMediaPlayerActivity.TYPE_PUSH;
					}
					playDate.obj = playInfo;
					Log.d(TAG, "prod_type" + playDate.prod_type);
//					playDate.prod_src = json.getString("prod_src");
					if(playInfo.getDuration()-playInfo.getPlayback_time()<=Constant.END_TIME){
						playDate.prod_time = 0;
					}else{
						playDate.prod_time = Math.round(playInfo.getPlayback_time()*1000);
					}
					playDate.prod_qua = playInfo.getDefination();
					playDate.isOnline = false;
//					if(playDate.prod_type==2||playDate.prod_type==3||playDate.prod_type==131){
//						if(json.has("prod_subname")){//旧版android 没有传递该参数
//							playDate.prod_sub_name = json.getString("prod_subname");
//						}else{
//							playDate.prod_type = -1;
//						}
//					}
					app.setmCurrentPlayDetailData(playDate);
					app.set_ReturnProgramView(null);
					startActivity(intent);
				}
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
			return true;
		}
		
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		// TODO Auto-generated method stub
		MoviePlayHistoryInfo playInfo = playinfos.get(groupPosition);
		BTEpisode epInfo = playInfo.getBtEpisodes().get(childPosition);
		CurrentPlayDetailData playDate = new CurrentPlayDetailData();
//		Intent intent = new Intent(this,JoyplusMediaPlayerActivity.class);
		Intent intent = Utils.getIntent(this);
//		intent.putExtra("ID", json.getString("prod_id"));
//		playDate.prod_id = data.getString("id");
//		playDate.prod_type = Integer.valueOf(json.getString("prod_type"));
//		playDate.prod_type = playInfo.getPlay_type();
		playDate.prod_name = playInfo.getName();
//		if(playInfo.getPlay_type()==MoviePlayHistoryInfo.PLAY_TYPE_LOCAL){
//			playDate.prod_url = playInfo.getLocal_url();
//			playDate.prod_type = VideoPlayerJPActivity.TYPE_LOCAL;
//		}else{
//			playDate.prod_url = playInfo.getDownload_url();
		playDate.prod_type = VideoPlayerJPActivity.TYPE_PUSH_BT_EPISODE;
		playDate.prod_sub_name = epInfo.getName();
//		}
		playDate.obj = playInfo;
		Log.d(TAG, "prod_type" + playDate.prod_type);
//		playDate.prod_src = json.getString("prod_src");
		if(epInfo.getDuration()-epInfo.getPlayback_time()<=Constant.END_TIME){
			playDate.prod_time = 0;
		}else{
			playDate.prod_time = Math.round(epInfo.getPlayback_time()*1000);
		}
		playDate.prod_qua = epInfo.getDefination();
		playDate.isOnline = false;
//		if(playDate.prod_type==2||playDate.prod_type==3||playDate.prod_type==131){
//			if(json.has("prod_subname")){//旧版android 没有传递该参数
//				playDate.prod_sub_name = json.getString("prod_subname");
//			}else{
//				playDate.prod_type = -1;
//			}
//		}
		isFirstResume = false;
		app.setmCurrentPlayDetailData(playDate);
		app.set_ReturnProgramView(null);
		startActivity(intent);
		return true;
	}

	@Override
	public void onGroupExpand(int groupPosition) {
		// TODO Auto-generated method stub
//		if(expandGroupIndex < playinfos.size() && expandGroupIndex>=0&&expandGroupIndex!=groupPosition){
//			listView.collapseGroup(expandGroupIndex);
//		}
//		expandGroupIndex = groupPosition;
//		listView.setSelection(groupPosition);
	}
}
