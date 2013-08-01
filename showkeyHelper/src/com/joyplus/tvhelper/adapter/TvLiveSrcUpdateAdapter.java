package com.joyplus.tvhelper.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.entity.TvLiveInfo;

public class TvLiveSrcUpdateAdapter extends BaseAdapter {

	private Context mContext;

	private List<TvLiveInfo> mList;
	private AQuery aq;

	public TvLiveSrcUpdateAdapter(Context context, List<TvLiveInfo> list,
			AQuery aq) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mList = list;
		this.aq = aq;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder viewHolder = null;
		if (convertView == null) {

			viewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_tv_src_update_grid,
					null);

			viewHolder.iv = (ImageView) convertView
					.findViewById(R.id.iv_icon_tv);
			viewHolder.nameTv = (TextView) convertView
					.findViewById(R.id.tv_tv_name);
			viewHolder.updateTv = (TextView) convertView
					.findViewById(R.id.tv_update);
			viewHolder.newestTv = (TextView) convertView
					.findViewById(R.id.tv_newssrc);
			viewHolder.rlLayout = convertView
					.findViewById(R.id.rl_recommoned_download);

			convertView.setTag(viewHolder);
		} else {

			viewHolder = (ViewHolder) convertView.getTag();
		}

		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
				parent.getHeight() - 10, parent.getHeight() - 10);
		convertView.setLayoutParams(layoutParams);

		TvLiveInfo info = mList.get(position);
		viewHolder.nameTv.setText(info.getApp_name());
		aq.id(viewHolder.iv).image(info.getIcon_url(), true, true, 0,
				R.drawable.ic_launcher);// 默认的图

		View[] views = {viewHolder.newestTv,viewHolder.updateTv,viewHolder.rlLayout};
		
		if(!info.isInstall()){
			
			setStatusVisible(views, 2);
		}else {
			
			switch (info.getStatus()) {
			case 0:
				setStatusVisible(views, 0);
				break;
			case 1:
				setStatusVisible(views, 1);
				break;
				
			default:
				break;
			}
		}

		return convertView;
	}
	
	private void setStatusVisible(View[] views,int status ){
		
		for(int i=0;i<views.length;i++){
			
			if(i == status){
				
				views[i].setVisibility(View.VISIBLE);
			}else{
				
				views[i].setVisibility(View.INVISIBLE);
			}
		}
	}
	
	public List<TvLiveInfo> getList(){
		
		return mList;
	}

	class ViewHolder {

		public ImageView iv;
		public TextView nameTv;
		public TextView updateTv;
		public TextView newestTv;
		public View rlLayout;

	}

}
