package com.joyplus.konka.luncher;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class InstalledApplication extends Activity implements
		OnItemClickListener {

	private static final String TAG = InstalledApplication.class
			.getSimpleName();
	private GridView mGridView;
	private List<ResolveInfo> mApps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_installedapps);
		mGridView = (GridView) findViewById(R.id.grid_view);
		mGridView.setOnItemClickListener(this);
		loadApps();
		mGridView.setAdapter(new AppsAdapter());
	}

	private void loadApps() {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mApps = getPackageManager().queryIntentActivities(mainIntent,
				PackageManager.GET_UNINSTALLED_PACKAGES);
		Iterator<ResolveInfo> iterator = mApps.iterator();
		while (iterator.hasNext()) {
			ResolveInfo info = iterator.next();
			if ("com.waxrain.airplaydmr".equals(info.activityInfo.packageName)) {
				iterator.remove();
			}
			if ("com.wind.s1mobileserver.client"
					.equals(info.activityInfo.packageName)) {
				iterator.remove();
			}
			if ("com.joyplus.konka.luncher"
					.equals(info.activityInfo.packageName)) {
				iterator.remove();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		ResolveInfo info = mApps.get(position);
		// 该应用的包名
		String pkg = info.activityInfo.packageName;
		// 应用的主activity类
		String cls = info.activityInfo.name;
		ComponentName componet = new ComponentName(pkg, cls);
		Intent i = new Intent();
		i.setComponent(componet);
		startActivity(i);
	}

	public class AppsAdapter extends BaseAdapter {
		public AppsAdapter() {
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			ResolveInfo info = mApps.get(position);
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.item_app,
						null);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.app_icon);
				holder.text = (TextView) convertView
						.findViewById(R.id.app_name);
				convertView.setLayoutParams(new AbsListView.LayoutParams(128,
						128));
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.image.setImageDrawable(info.activityInfo
					.loadIcon(getPackageManager()));
			holder.text.setText(info.activityInfo
					.loadLabel(getPackageManager()));
			return convertView;
		}

		public final int getCount() {
			return mApps.size();
		}

		public final Object getItem(int position) {
			return mApps.get(position);
		}

		public final long getItemId(int position) {
			return position;
		}
	}

	class ViewHolder {
		ImageView image;
		TextView text;
	}
}
