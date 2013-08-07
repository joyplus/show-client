package com.joyplus.tvhelper.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.entity.AppRecommendInfo;

public class AppRecommendAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<AppRecommendInfo> mList;
	
	private AQuery aq;
	
	public AppRecommendAdapter(Context context,AQuery aq,List<AppRecommendInfo> list){
		
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
		if(convertView == null) {
			
			viewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_app_recommend_grid, null);
			viewHolder.iv = (ImageView) convertView.findViewById(R.id.imageview);
			convertView.setTag(viewHolder);
		} else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
//		viewHolder.iv.setBackgroundResource(mList.get(position).getIconSrcId());
		aq.id(viewHolder.iv).image(mList.get(position).getPic_url(), true, true, 0,
				R.drawable.default_app_bg);// 默认的图
		
		AbsListView.LayoutParams layoutParams = new AbsListView.
				LayoutParams(parent.getWidth()/4 - 4,parent.getHeight()/2 - 4);
		convertView.setLayoutParams(layoutParams);
//		convertView.setPadding(4, 4, 4, 4);
		return convertView;
	}
	
	class ViewHolder{
		
		public ImageView iv;
	}

}
