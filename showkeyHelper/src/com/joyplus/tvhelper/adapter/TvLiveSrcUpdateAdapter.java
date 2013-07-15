package com.joyplus.tvhelper.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.joyplus.tvhelper.R;

public class TvLiveSrcUpdateAdapter extends BaseAdapter {
	
	private Context context;
	
	public TvLiveSrcUpdateAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		if(convertView == null) {
			
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_tv_src_update, null);
		}
		
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(187, 187);
		convertView.setLayoutParams(layoutParams);
		return convertView;
	}

}
