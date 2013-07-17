package com.joyplus.tvhelper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

import com.joyplus.tvhelper.adapter.AppRecommendAdapter;
import com.joyplus.tvhelper.entity.service.TvLiveView;
import com.joyplus.tvhelper.ui.GridSwitcherView;

public class AppRecommendActivity extends Activity {
	
	private AppRecommendAdapter adapter;
	private GridSwitcherView gridSwitcherView;
	private List<TvLiveView> list = new ArrayList<TvLiveView>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_app_recommend);
		
		gridSwitcherView = (GridSwitcherView) findViewById(R.id.gridswitcherview);
		for(int i=0;i< 25;i++) {
			
			TvLiveView info = new TvLiveView();
			list.add(info);
		}
		
		gridSwitcherView.setRows(GridSwitcherView.ROW_2);
		gridSwitcherView.initGridSwitcherView(list.size(), GridSwitcherView.NUM_CLOUMNS_5);
		
		adapter = new AppRecommendAdapter(this,gridSwitcherView.getNumCloumns(),gridSwitcherView,list);

		gridSwitcherView.setGridAdapter(adapter);
		
	}

}
