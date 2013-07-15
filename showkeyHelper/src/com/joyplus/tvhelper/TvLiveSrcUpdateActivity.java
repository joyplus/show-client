package com.joyplus.tvhelper;

import com.joyplus.tvhelper.adapter.TvLiveSrcUpdateAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

public class TvLiveSrcUpdateActivity extends Activity {
	
	private TvLiveSrcUpdateAdapter adapter;
	private GridView gv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_tv_live_src_update);
		
		gv = (GridView) findViewById(R.id.gv_live_src);
		
		adapter = new TvLiveSrcUpdateAdapter(this);
		gv.setAdapter(adapter);
	}

}
