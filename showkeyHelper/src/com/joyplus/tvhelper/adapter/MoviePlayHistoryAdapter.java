package com.joyplus.tvhelper.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.entity.MoviePlayHistoryInfo;
import com.joyplus.tvhelper.entity.PushedApkDownLoadInfo;
import com.joyplus.tvhelper.utils.Utils;

public class MoviePlayHistoryAdapter extends BaseAdapter {

	private Context mContext;
	private List<MoviePlayHistoryInfo> data;
	
	public MoviePlayHistoryAdapter(Context c, List<MoviePlayHistoryInfo> data){
		this.data = data;
		this.mContext = c;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
//		return FayeService.infolist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		MoviePlayHistoryInfo info = data.get(position);
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_downloaded_movie, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.movie_name);
			holder.size = (TextView) convertView.findViewById(R.id.movie_size);
			holder.statue_icon = (ImageView) convertView.findViewById(R.id.movie_statue_icon);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(info.getName());
//		if(info.getPlay_type() == MoviePlayHistoryInfo.PLAY_TYPE_LOCAL){
//			holder.name.setText(Utils.getDisPlayFileNameforUrl(info.getLocal_url()));
//		}else{
//			holder.name.setText(Utils.getDisPlayFileNameforUrl(info.getPush_url()));
//		}
		if(info.getDuration()<=info.getPlayback_time()+10&&info.getDuration()>0){
			holder.size.setText("已看完");
		}else{
			holder.size.setText("已观看："+Utils.formatDuration(info.getPlayback_time()*1000)+
					"  /  "+ Utils.formatDuration(info.getDuration()*1000));
		}
		switch (info.getEdite_state()) {
		case PushedApkDownLoadInfo.EDITE_STATUE_NOMAL:
			holder.statue_icon.setImageDrawable(null);
			break;
		case PushedApkDownLoadInfo.EDITE_STATUE_EDIT:
			holder.statue_icon.setImageResource(R.drawable.item_statue_selete);
			break;
		case PushedApkDownLoadInfo.EDITE_STATUE_SELETED:
			holder.statue_icon.setImageResource(R.drawable.item_statue_seleted);
			break;
		}
		return convertView;
	}
	
	class ViewHolder{
		TextView name;
		TextView size;
		ImageView statue_icon;
	}

}
