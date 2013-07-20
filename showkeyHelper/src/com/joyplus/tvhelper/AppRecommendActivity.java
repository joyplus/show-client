package com.joyplus.tvhelper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.joyplus.tvhelper.adapter.AppRecommendAdapter;
import com.joyplus.tvhelper.entity.AppRecommendInfo;

public class AppRecommendActivity extends Activity {
	
	private List<AppRecommendInfo> list = new ArrayList<AppRecommendInfo>();
	
	private int[] egAppIds = {R.drawable.app_bg_1,R.drawable.app_bg_2,R.drawable.app_bg_3,
							   R.drawable.app_bg_4,R.drawable.app_bg_5,R.drawable.app_bg_6,
			                   R.drawable.app_bg_7,R.drawable.app_bg_8};
	
	private GridView gridView;
	private TextView downloadTv;
	private AppRecommendAdapter adapter;
	
	private SparseArray<View> sparseArrayView = new SparseArray<View>();
	private int preSelectedIndex = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_app_recommend);
		
		gridView = (GridView) findViewById(R.id.gv);
		downloadTv = (TextView) findViewById(R.id.tv_download_bg);
		
		for(int i=0,j=0;i< 25;i++,j++) {
			
			AppRecommendInfo info = new AppRecommendInfo();
			
			if(j >=egAppIds.length){
				
				j = 0;
			}
			info.setIconSrcId(egAppIds[j]);
			list.add(info);
		}
		
		adapter = new AppRecommendAdapter(this, list);
		gridView.setAdapter(adapter);
		
		gridView.setNextFocusUpId(R.id.bt_back);
		
		initListener();
		
	}
	
	private void initListener(){
		
		gridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				if(view == null) {
					
					return;
				} else {
					
					sparseArrayView.put(position, view);
				}
				
				setStartDownLoadVisible(view, true);
				
				preSelectedIndex = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		gridView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				
				if(!hasFocus){
					
					if(preSelectedIndex != -1) {
						
						View view = sparseArrayView.get(preSelectedIndex);
						if(view != null) {
							
							setStartDownLoadVisible(v, false);
						}
					}
					
				}
			}
		});
	}
	
	private void setStartDownLoadVisible(View v,boolean isVisible) {
		
		if( v == null) {
			
			return;
		}
		
		if(isVisible) {
			
			downloadTv.layout((int)v.getX(), (int)(v.getY()+v.getHeight()/5 * 4), (int)(v.getX() + v.getWidth()), (int)(v.getY() + v.getHeight()));
			downloadTv.setVisibility(View.VISIBLE);
		}else {
			
			downloadTv.setVisibility(View.INVISIBLE);
		}
	}

}
