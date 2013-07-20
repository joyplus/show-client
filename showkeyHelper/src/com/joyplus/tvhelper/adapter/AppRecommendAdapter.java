package com.joyplus.tvhelper.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.entity.AppRecommendInfo;

public class AppRecommendAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<AppRecommendInfo> mList;
	
	public AppRecommendAdapter(Context context,List<AppRecommendInfo> list){
		
		this.mContext = context;
		this.mList = list;
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
		
		viewHolder.iv.setBackgroundResource(mList.get(position).getIconSrcId());
		
		AbsListView.LayoutParams layoutParams = new AbsListView.
				LayoutParams(parent.getWidth()/4 -1*(int)mContext.getResources().getDimension(R.dimen.item_grid_padding),
						parent.getHeight()/2 - 1 * (int)mContext.getResources().getDimension(R.dimen.item_grid_padding));
		convertView.setLayoutParams(layoutParams);
		return convertView;
	}
	
	class ViewHolder{
		
		public ImageView iv;
	}

}
