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

public class PushedMovieDownLoadAdapter extends BaseAdapter {

	private Context mContext;
	private List<PushedMovieDownLoadInfo> data;
	
	public PushedMovieDownLoadAdapter(Context c, List<PushedMovieDownLoadInfo> data){
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_download_movie, null);
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
//		if(info.getIcon()!=null){
//			holder.icon.setImageDrawable(info.getIcon());
//		}else{
//			holder.icon.setImageResource(R.drawable.ic_launcher);
//		}
		holder.name.setText(info.getName());
		int progress = 0;
		if(info.getTast().getSize()>0){
			holder.progress.setMax(info.getTast().getSize());
			progress = (int) ((info.getTast().getCurLength()*100d) / info.getTast().getSize());
		}else{
			holder.progress.setMax(100);
		}
		switch (info.getDownload_state()) {
		case PushedApkDownLoadInfo.STATUE_WAITING_DOWNLOAD://等待下载
			holder.statue.setText("等待下载");
//			holder.progress.setProgress(progress);
			holder.progress.setProgress(info.getTast().getCurLength());
			holder.progress.setSecondaryProgress(0);
			holder.progressLayout.setTag(info.get_id());
			holder.size.setText(PackageUtils.fomartSize(info.getTast().getSize()));
			holder.progressText.setText(progress+"%");
			break;
		case PushedApkDownLoadInfo.STATUE_DOWNLOADING://正在下载
			holder.statue.setText("正在下载");
			holder.progress.setProgress(info.getTast().getCurLength());
			holder.size.setText(getRTInfo(info.getTast()));
			holder.progress.setSecondaryProgress(0);
			holder.progressLayout.setTag(info.get_id());
			holder.progressText.setText(progress+"%");
			break;
		case PushedApkDownLoadInfo.STATUE_DOWNLOAD_PAUSE://暂停下载
			holder.statue.setText("已暂停下载");
			holder.progress.setProgress(0);
//			holder.progress.setSecondaryProgress(progress);
			holder.progress.setSecondaryProgress(info.getTast().getCurLength());
			holder.size.setText(PackageUtils.fomartSize(info.getTast().getSize()));
			holder.progressLayout.setTag(info.get_id());
			holder.progressText.setText(progress+"%");
			break;
//		case PushedApkDownLoadInfo.STATUE_DOWNLOAD_COMPLETE://下载完成
//			holder.statue.setText("正在安装");
////			holder.progress.setProgress(info.getProgress());
////				holder.progress.setSecondaryProgress(info.getProgress());
////			holder.progress.setSecondaryProgress(0);
//			holder.progressLayout.setTag(info.get_id());
////			holder.progressText.setText(info.getProgress()+"%");
//			break;
		case PushedApkDownLoadInfo.STATUE_DOWNLOAD_PAUSEING://下载完成
			holder.statue.setText("正在暂停");
			holder.progress.setProgress(0);
//			holder.progress.setSecondaryProgress(progress);
			holder.progress.setSecondaryProgress(info.getTast().getCurLength());
			holder.size.setText(PackageUtils.fomartSize(info.getTast().getSize()));
			holder.progressLayout.setTag(info.get_id());
			holder.progressText.setText(progress+"%");
			break;
//		case PushedApkDownLoadInfo.STATUE_INSTALL_FAILE://安装失败
//			holder.statue.setText("安装失败");
//			holder.progress.setProgress(progress);
//			holder.progress.setVisibility(View.INVISIBLE);
//			holder.progressLayout.setTag(info.get_id());
//			break;
		}
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
	
	/**
     * 实时任务信息
     * @return
     */
    private String getRTInfo(DownloadTask task) {
        StringBuffer buffer = new StringBuffer();
        if (task.getSize() > 0) {
            buffer.append(PackageUtils.fomartSize(task.getSize()));
        } else {
            buffer.append("未知大小");
        }
        if (task.getState() != DownloadTask.STATE_DOWNLOADING) {
            return buffer.toString();
        }
        buffer.append("/").append(task.getSpeed()).append("k/s");

        return buffer.toString();
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
