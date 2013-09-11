package com.joyplus.tvhelper.adapter;

import java.net.URLDecoder;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.entity.BTEpisode;
import com.joyplus.tvhelper.entity.MoviePlayHistoryInfo;
import com.joyplus.tvhelper.entity.PushedApkDownLoadInfo;
import com.joyplus.tvhelper.utils.Utils;

public class MoviePlayHistoryAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private List<MoviePlayHistoryInfo> data;
	
	public MoviePlayHistoryAdapter(Context c, List<MoviePlayHistoryInfo> data){
		this.data = data;
		this.mContext = c;
	}
//	
//	@Override
//	public int getCount() {
//		// TODO Auto-generated method stub
//		return data.size();
////		return FayeService.infolist.size();
//	}
//
//	@Override
//	public Object getItem(int position) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public long getItemId(int position) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		// TODO Auto-generated method stub
//		ViewHolder holder = null;
//		MoviePlayHistoryInfo info = data.get(position);
//		if(convertView == null){
//			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_downloaded_movie, null);
//			holder = new ViewHolder();
//			holder.name = (TextView) convertView.findViewById(R.id.movie_name);
//			holder.size = (TextView) convertView.findViewById(R.id.movie_size);
//			holder.statue_icon = (ImageView) convertView.findViewById(R.id.movie_statue_icon);
//			convertView.setTag(holder);
//		}else{
//			holder = (ViewHolder) convertView.getTag();
//		}
//		
//		
//		if(info.getName()==null||"".equals(info.getName())){
//			if(info.getPlay_type() == MoviePlayHistoryInfo.PLAY_TYPE_LOCAL){
//				holder.name.setText(info.getLocal_url());
//			}else{
//				String str = info.getPush_url();
//				try{
//					str = URLDecoder.decode(str, "utf-8");
//				}catch (Exception e) {
//					// TODO: handle exception
//				}
//				holder.name.setText(str);
//			}
//		}else{
//			holder.name.setText(info.getName());
//		}
//		
////		if(info.getPlay_type() == MoviePlayHistoryInfo.PLAY_TYPE_LOCAL){
////			holder.name.setText(Utils.getDisPlayFileNameforUrl(info.getLocal_url()));
////		}else{
////			holder.name.setText(Utils.getDisPlayFileNameforUrl(info.getPush_url()));
////		}
//		if(info.getPlay_type()==MoviePlayHistoryInfo.PLAY_TYPE_BAIDU){
//			holder.size.setText("");
//		}else{
//			if(info.getDuration()<10){
//				holder.size.setText("已观看："+Utils.formatDuration(info.getPlayback_time()*1000)+
//						"  /  "+ "--:--:--");
//			}else if(info.getDuration()<=info.getPlayback_time()+10&&info.getDuration()>10){
//				holder.size.setText("已看完");
//			}else{
//				holder.size.setText("已观看："+Utils.formatDuration(info.getPlayback_time()*1000)+
//						"  /  "+ Utils.formatDuration(info.getDuration()*1000));
//			}
//		}
//		
//		switch (info.getEdite_state()) {
//		case PushedApkDownLoadInfo.EDITE_STATUE_NOMAL:
//			holder.statue_icon.setImageDrawable(null);
//			break;
//		case PushedApkDownLoadInfo.EDITE_STATUE_EDIT:
//			holder.statue_icon.setImageResource(R.drawable.item_statue_selete);
//			break;
//		case PushedApkDownLoadInfo.EDITE_STATUE_SELETED:
//			holder.statue_icon.setImageResource(R.drawable.item_statue_seleted);
//			break;
//		}
//		return convertView;
//	}
	
	class ViewHolder{
		TextView name;
		TextView size;
		ImageView statue_icon;
		ImageView type_icon;
		ImageView divider_line;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return data.get(groupPosition).getBtEpisodes().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
			ViewHolder holder = null;
			MoviePlayHistoryInfo info = data.get(groupPosition);
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_downloaded_movie, null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView.findViewById(R.id.movie_name);
				holder.size = (TextView) convertView.findViewById(R.id.movie_size);
				holder.statue_icon = (ImageView) convertView.findViewById(R.id.movie_statue_icon);
				holder.type_icon = (ImageView) convertView.findViewById(R.id.movie_type);
				holder.divider_line = (ImageView) convertView.findViewById(R.id.divider_line);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			
			if(info.getName()==null||"".equals(info.getName())){
				if(info.getPlay_type() == MoviePlayHistoryInfo.PLAY_TYPE_LOCAL){
					holder.name.setText(info.getLocal_url());
				}else{
					String str = info.getPush_url();
					try{
						str = URLDecoder.decode(str, "utf-8");
					}catch (Exception e) {
						// TODO: handle exception
					}
					holder.name.setText(str);
				}
			}else{
				holder.name.setText(info.getName());
			}
			
	//		if(info.getPlay_type() == MoviePlayHistoryInfo.PLAY_TYPE_LOCAL){
	//			holder.name.setText(Utils.getDisPlayFileNameforUrl(info.getLocal_url()));
	//		}else{
	//			holder.name.setText(Utils.getDisPlayFileNameforUrl(info.getPush_url()));
	//		}
			
			if(info.getPlay_type() == MoviePlayHistoryInfo.PLAY_TYPE_BT_EPISODES){
				if(isExpanded){
					holder.type_icon.setImageResource(R.drawable.icon_bt_file_open);
				}else{
					holder.type_icon.setImageResource(R.drawable.icon_bt_file);
				}
			}else{
				holder.type_icon.setImageResource(R.drawable.icon_movie);
			}
			switch (info.getPlay_type()) {
			case MoviePlayHistoryInfo.PLAY_TYPE_BAIDU:
				holder.size.setText("");
				break;
			case MoviePlayHistoryInfo.PLAY_TYPE_ONLINE:
			case MoviePlayHistoryInfo.PLAY_TYPE_LOCAL:
				if(info.getDuration()<10){
					holder.size.setText("已观看："+Utils.formatDuration(info.getPlayback_time()*1000)+
							"  /  "+ "--:--:--");
				}else if(info.getDuration()<=info.getPlayback_time()+10&&info.getDuration()>10){
					holder.size.setText("已看完");
				}else{
					holder.size.setText("已观看："+Utils.formatDuration(info.getPlayback_time()*1000)+
							"  /  "+ Utils.formatDuration(info.getDuration()*1000));
				}
				break;
			case MoviePlayHistoryInfo.PLAY_TYPE_BT_EPISODES:
				holder.size.setText("");
				break;
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
//			if(groupPosition<data.size()-1){
//				holder.divider_line.setVisibility(View.INVISIBLE);
//			}else{
//				holder.divider_line.setVisibility(View.VISIBLE);
//			}
			return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		BTEpisode info = data.get(groupPosition).getBtEpisodes().get(childPosition);
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bt_episode, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.movie_name);
			holder.size = (TextView) convertView.findViewById(R.id.movie_size);
			holder.statue_icon = (ImageView) convertView.findViewById(R.id.movie_statue_icon);
			holder.type_icon = (ImageView) convertView.findViewById(R.id.movie_type);
			holder.divider_line = (ImageView) convertView.findViewById(R.id.divider_line);
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
		

		holder.type_icon.setImageResource(R.drawable.icon_movie);
 
		if(info.getDuration()<10){
			holder.size.setText("已观看："+Utils.formatDuration(info.getPlayback_time()*1000)+
					"  /  "+ "--:--:--");
		}else if(info.getDuration()<=info.getPlayback_time()+10&&info.getDuration()>10){
			holder.size.setText("已看完");
		}else{
			holder.size.setText("已观看："+Utils.formatDuration(info.getPlayback_time()*1000)+
					"  /  "+ Utils.formatDuration(info.getDuration()*1000));
		}
//		if(childPosition<data.get(groupPosition).getBtEpisodes().size()-1){
//			holder.divider_line.setVisibility(View.INVISIBLE);
//		}else{
//			holder.divider_line.setVisibility(View.VISIBLE);
//		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
