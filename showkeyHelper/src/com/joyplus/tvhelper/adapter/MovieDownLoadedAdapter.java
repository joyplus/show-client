package com.joyplus.tvhelper.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.joyplus.network.filedownload.model.DownloadTask;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.entity.PushedApkDownLoadInfo;
import com.joyplus.tvhelper.entity.PushedMovieDownLoadInfo;
import com.joyplus.tvhelper.utils.PackageUtils;
import com.joyplus.utils.FileUtil;

public class MovieDownLoadedAdapter extends BaseAdapter {

	private Context mContext;
	private List<PushedMovieDownLoadInfo> data;
	
	public MovieDownLoadedAdapter(Context c, List<PushedMovieDownLoadInfo> data){
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
		PushedMovieDownLoadInfo info = data.get(position);
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_downloaded_movie, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.app_name);
			holder.size = (TextView) convertView.findViewById(R.id.app_size);
			holder.statue_icon = (ImageView) convertView.findViewById(R.id.app_statue_icon);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(info.getName());
		holder.size.setText(PackageUtils.fomartSize(info.getTast().getSize()));
		switch (info.getEdite_state()) {
		case PushedApkDownLoadInfo.EDITE_STATUE_NOMAL:
			if(info.getDownload_state()==PushedApkDownLoadInfo.STATUE_DOWNLOADING){
				holder.statue_icon.setImageResource(R.drawable.icon_continue);
			}else{
				holder.statue_icon.setImageResource(R.drawable.icon_puse);
			}
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
