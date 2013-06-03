package com.joypuls.tvhelper.adapter;

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

import com.joypuls.tvhelper.R;
import com.joypuls.tvhelper.db.PushedApkDownLoadInfo;
import com.joypuls.tvhelper.entity.PushedApkInfo;
import com.joypuls.tvhelper.faye.FayeService;
import com.joypuls.tvhelper.utils.PackageUtils;

public class PushedApkAdapter extends BaseAdapter {

	private Context mContext;
	private List<PushedApkInfo> data;
	
	public PushedApkAdapter(Context c, List<PushedApkInfo> data){
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
		PushedApkInfo info = data.get(position);
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_apk_list, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.app_icon);
			holder.name = (TextView) convertView.findViewById(R.id.app_name);
			holder.size = (TextView) convertView.findViewById(R.id.app_size);
			holder.progress = (ProgressBar) convertView.findViewById(R.id.progressbar);
			holder.progressText = (TextView) convertView.findViewById(R.id.progress_value);
			holder.progressLayout = (LinearLayout) convertView.findViewById(R.id.progressLayout);
			holder.statue = (TextView) convertView.findViewById(R.id.app_statue);
			holder.statue_icon = (ImageView) convertView.findViewById(R.id.app_statue_icon);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		if(info.getIcon()!=null){
			holder.icon.setImageDrawable(info.getIcon());
		}else{
			holder.icon.setImageResource(R.drawable.ic_launcher);
		}
		holder.name.setText(info.getAppName());
		holder.size.setText(PackageUtils.fomartSize(info.getSize()));
		switch (info.getStatue()) {
		case 0://等待下载
			holder.statue.setText("等待下载");
			holder.progress.setProgress(info.getProgress());
			holder.progress.setSecondaryProgress(0);
			holder.progressLayout.setTag(info.getPush_id());
			holder.progressText.setText("");
			break;
		case 1://正在下载
			holder.statue.setText("正在下载");
			holder.progress.setProgress(info.getProgress());
			holder.progress.setSecondaryProgress(0);
			holder.progressLayout.setTag(info.getPush_id());
			holder.progressText.setText(info.getProgress()+"%");
			break;
		case 2://暂停下载
			holder.statue.setText("已暂停下载");
			holder.progress.setProgress(0);
			holder.progress.setSecondaryProgress(info.getProgress());
			holder.progressLayout.setTag(info.getPush_id());
			holder.progressText.setText(info.getProgress()+"%");
			break;
		case 3://安装
			holder.statue.setText("正在安装");
			holder.progress.setProgress(info.getProgress());
//				holder.progress.setSecondaryProgress(info.getProgress());
			holder.progress.setSecondaryProgress(0);
			holder.progressLayout.setTag(info.getPush_id());
			holder.progressText.setText(info.getProgress()+"%");
			break;
		case 4://安装失败
			holder.statue.setText("安装失败");
			holder.progress.setProgress(info.getProgress());
			holder.progress.setVisibility(View.INVISIBLE);
//				holder.progress.setSecondaryProgress(info.getProgress());
//				holder.progress.setSecondaryProgress(0);
			holder.progressLayout.setTag(info.getPush_id());
//				holder.progressText.setText(info.getProgress()+"%");
			break;
		}
		switch (info.getEdite_statue()) {
		case 0:
			if(info.getStatue()==2){
				holder.statue_icon.setImageResource(R.drawable.icon_continue);
			}else{
				holder.statue_icon.setImageResource(R.drawable.icon_puse);
			}
			break;
		case 1:
			holder.statue_icon.setImageResource(R.drawable.item_statue_selete);
			break;
		case 2:
			holder.statue_icon.setImageResource(R.drawable.item_statue_seleted);
			break;
		}
		return convertView;
	}
	
	class ViewHolder{
		ImageView icon;
		TextView name;
		TextView size;
		ProgressBar progress;
		TextView progressText;
		LinearLayout progressLayout;
		TextView statue;
		ImageView statue_icon;
	}
}
