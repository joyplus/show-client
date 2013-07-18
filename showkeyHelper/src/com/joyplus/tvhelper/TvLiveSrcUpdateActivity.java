package com.joyplus.tvhelper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

import com.joyplus.tvhelper.adapter.TvLiveSrcUpdateAdapter;
import com.joyplus.tvhelper.entity.service.TvLiveView;

public class TvLiveSrcUpdateActivity extends Activity {
	
	private List<TvLiveView> list = new ArrayList<TvLiveView>();
	private GridView gridView;
	private TvLiveSrcUpdateAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_tv_live_src_update);
		
		gridView = (GridView) findViewById(R.id.gridview);
		
		for(int i=0;i< 10;i++) {
			
			TvLiveView info = new TvLiveView();
			list.add(info);
		}
		
		adapter = new TvLiveSrcUpdateAdapter(this, list);
		gridView.setAdapter(adapter);
		
	}

}
