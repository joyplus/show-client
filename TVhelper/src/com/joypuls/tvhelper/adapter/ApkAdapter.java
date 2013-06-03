package com.joypuls.tvhelper.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joypuls.tvhelper.R;
import com.joypuls.tvhelper.entity.ApkInfo;
import com.joypuls.tvhelper.utils.PackageUtils;

public class ApkAdapter extends BaseAdapter {

	private Context mContext;
	private List<ApkInfo> data;
	
	public ApkAdapter(Context c, List<ApkInfo> data){
		this.data = data;
		this.mContext = c;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
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
		ApkInfo apkInfo = data.get(position);
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_apk_grid, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.item_apk_icon);
			holder.name = (TextView) convertView.findViewById(R.id.item_apk_name);
			holder.size = (TextView) convertView.findViewById(R.id.item_apk_size);
			holder.version = (TextView) convertView.findViewById(R.id.item_apk_version);
			holder.seleted_icon = (ImageView) convertView.findViewById(R.id.item_apk_seleted);
			holder.iconIstalled = (ImageView) convertView.findViewById(R.id.icon_installed);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
//		LayoutInflater inflater = LayoutInflater.from(mContext);
//		View v = inflater.inflate(R.layout.item_apk_grid, null);
//		ImageView img = (ImageView) v.findViewById(R.id.item_apk_icon);
//		ImageView seleted_icon = (ImageView) v.findViewById(R.id.item_apk_seleted);
//		TextView name = (TextView) v.findViewById(R.id.item_apk_name);
//		TextView size = (TextView) v.findViewById(R.id.item_apk_size);
//		TextView version = (TextView) v.findViewById(R.id.item_apk_version);
//		ImageView iconIstalled = (ImageView) v.findViewById(R.id.icon_installed);
		holder.icon.setImageDrawable(apkInfo.getDrawble());
		holder.name.setText(apkInfo.getAppName());
		holder.size.setText(PackageUtils.fomartSize(apkInfo.getSize()));
		switch (apkInfo.getStatue()) {
		case 0:
			holder.seleted_icon.setImageDrawable(null);
			break;
		case 1:
			holder.seleted_icon.setImageResource(R.drawable.item_statue_selete);
			break;
		case 2:
			holder.seleted_icon.setImageResource(R.drawable.item_statue_seleted);
			break;
		}
		if(apkInfo.getVision()!=null&&!"".equals(apkInfo.getVision())){
			holder.version.setText("[ " + apkInfo.getVision() + " ]");
		}else{
			holder.version.setText("");
		}
		
		if(apkInfo.isInstalled()){
			holder.iconIstalled.setVisibility(View.VISIBLE);
		}else{
			holder.iconIstalled.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	
	class ViewHolder{
		ImageView icon;
		ImageView seleted_icon;
		TextView name;
		TextView size;
		TextView version;
		ImageView iconIstalled;
	}

}
