package com.joyplus.tvhelper.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.entity.XLLXFileInfo;
import com.joyplus.tvhelper.utils.Utils;

public class PlayExpandListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<XLLXFileInfo> files;
	
	public PlayExpandListAdapter(Context context ,ArrayList<XLLXFileInfo> files) {
		// TODO Auto-generated constructor stub
		
		this.context = context;
		setFiles(files);
	}
	
	private void setFiles(ArrayList<XLLXFileInfo> paramArrayList) {
		
		if (paramArrayList != null) {
			
			this.files = paramArrayList;
		} else {
			
			this.files = new ArrayList<XLLXFileInfo>();
		}
		
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		if(files != null ) {
			
			return files.size();
		}
		
		return 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		if(files.get(groupPosition)!= null && files.get(groupPosition).isDir
				&& files.get(groupPosition).btFiles != null) {
			
			return files.get(groupPosition).btFiles.length;
		}
		
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		if(files.get(groupPosition)!= null) {
			
			return files.get(groupPosition);
		}
		return null;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		if(files.get(groupPosition)!= null && files.get(groupPosition).btFiles != null
				&& files.get(groupPosition).btFiles.length > childPosition) {
			
			return files.get(groupPosition).btFiles[childPosition];
		}
		
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater
					.inflate(R.layout.item_play_list_layout, null);
			holder.nameTv = (TextView) convertView
					.findViewById(R.id.tv_movie_name);
			holder.sizeTv = (TextView) convertView.findViewById(R.id.tv_size);
			holder.imageView = (ImageView) convertView.findViewById(R.id.iv_movie);

			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		LayoutParams params = new AbsListView.LayoutParams(
				LayoutParams.FILL_PARENT, 72);
		convertView.setLayoutParams(params);

		holder.nameTv.setText(files.get(groupPosition).file_name);
		
		if(files.get(groupPosition).isDir) {
			
			holder.sizeTv.setText("文件夹");
			
			holder.imageView.setImageResource(R.drawable.icon_bt_file);
		} else {
			
			holder.sizeTv
			.setText(Utils.byte2Mbyte(files.get(groupPosition).filesize));
			holder.imageView.setImageResource(R.drawable.icon_movie);
		}
		
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater
					.inflate(R.layout.item_play_list_layout, null);
			holder.nameTv = (TextView) convertView
					.findViewById(R.id.tv_movie_name);
			holder.sizeTv = (TextView) convertView.findViewById(R.id.tv_size);
			holder.imageView = (ImageView) convertView.findViewById(R.id.iv_movie);

			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		LayoutParams params = new AbsListView.LayoutParams(
				LayoutParams.FILL_PARENT, 62);
		convertView.setLayoutParams(params);
		
		convertView.setPadding(30, 0, 20, 0);
		
		holder.sizeTv
		.setText(Utils.byte2Mbyte(files.get(groupPosition).btFiles[childPosition].filesize));
		holder.imageView.setImageResource(R.drawable.icon_movie);

		holder.nameTv.setText(files.get(groupPosition).btFiles[childPosition].file_name);
		
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
	
	class ViewHolder {
		
		public ImageView imageView;
		public TextView nameTv;
		public TextView sizeTv;
	}

}
