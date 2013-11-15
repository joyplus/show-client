package com.joyplus.tvhelper;

import java.net.URLEncoder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.joyplus.JoyplusMediaPlayerActivity;
import com.joyplus.tvhelper.db.DBServices;
import com.joyplus.tvhelper.entity.CurrentPlayDetailData;
import com.joyplus.tvhelper.entity.MoviePlayHistoryInfo;
import com.joyplus.tvhelper.utils.Constant;
import com.joyplus.tvhelper.utils.Utils;
import com.umeng.analytics.MobclickAgent;

public class ThirdPlayActivity extends Activity {

	private DBServices dbService;
	private MyApp app;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		MobclickAgent.updateOnlineConfig(this);
		dbService = DBServices.getInstance(this);
		app = (MyApp) getApplication();
		
		String url = getIntent().getStringExtra("url");
		url = URLEncoder.encode(url);
		String name  = getIntent().getStringExtra("name");
		MoviePlayHistoryInfo info = dbService.hasMoviePlayHistory(MoviePlayHistoryInfo.PLAY_TYPE_ONLINE, url);
		if(info == null){
			info =  new MoviePlayHistoryInfo();
			info.setCreat_time(System.currentTimeMillis());
			info.setName(name);
			info.setPush_url(url);
			info.setDefination(Constant.DEFINATION_HD2);
			info.setPlay_type(MoviePlayHistoryInfo.PLAY_TYPE_ONLINE);
			info.setId((int)dbService.insertMoviePlayHistory(info));
		}
		CurrentPlayDetailData playDate = new CurrentPlayDetailData();
//		Intent intent = new Intent(this,VideoPlayerJPActivity.class);
		Intent intent = Utils.getIntent(this);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		playDate.prod_type = JoyplusMediaPlayerActivity.TYPE_PUSH;
		playDate.prod_name = info.getName();
		playDate.obj = info;
		playDate.prod_qua = info.getDefination();
		app.setmCurrentPlayDetailData(playDate);
		app.set_ReturnProgramView(null);
		startActivity(intent);
		finish();
	}
}
