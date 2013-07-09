package com.joyplus.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joyplus.entity.XLLXFileInfo;
import com.joyplus.xllx.R;

public class PlayListAdapter extends BaseAdapter {
	
	private Context context;
	private List<XLLXFileInfo> list;
	
	public PlayListAdapter(Context context,List<XLLXFileInfo> list) {
		// TODO Auto-generated constructor stub
		
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = ((Activity)context).getLayoutInflater().
					inflate(R.layout.item_play_list_layout, null);
			holder.nameTv = (TextView) convertView.findViewById(R.id.tv_movie_name);
			holder.sizeTv = (TextView) convertView.findViewById(R.id.tv_size);
			
			convertView.setTag(holder);
		} else {
			
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.nameTv.setText(list.get(position).file_name);
		holder.sizeTv.setText(list.get(position).filesize);
		
		return convertView;
	}

	
	class ViewHolder {
		
		public TextView nameTv;
		public TextView sizeTv;
	}
}
