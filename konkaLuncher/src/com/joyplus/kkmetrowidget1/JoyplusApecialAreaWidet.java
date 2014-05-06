package com.joyplus.kkmetrowidget1;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.joyplus.konka.luncher.R;
import com.joyplus.konka.luncher.SpecialAreaActivity;
import com.joyplus.konka.utils.Log;


public class JoyplusApecialAreaWidet extends AppWidgetProvider {

	private static final String TAG =	JoyplusApecialAreaWidet.class.getSimpleName();
	
	private RemoteViews   mRemoteViews;
	private PendingIntent mPendingIntent;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
			
		mPendingIntent    = GetPendingIntent(context);
		if(mRemoteViews == null){
			mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
		}
		if(mPendingIntent!=null){
			mRemoteViews.setOnClickPendingIntent(R.id.parent, mPendingIntent);
			mRemoteViews.setOnClickPendingIntent(R.id.key, mPendingIntent);
		}
		appWidgetManager.updateAppWidget(appWidgetIds, mRemoteViews);
	}
	
	private PendingIntent GetPendingIntent(Context context){
		try{
			Intent intent = new Intent(context,SpecialAreaActivity.class);
//			intent.setClassName("com.joyplus.konka.luncher", "com.joyplus.konka.luncher.SpecialAreaActivity");
	//		mRemoteViews.setImageViewResource(R.id.click, R.drawable.icon);
			PendingIntent mPending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//			PendingIntent mPending = PendingIntent.
			return mPending;
		}catch(Exception e){
			
		}
		return null;
	}
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		Log.d(TAG, "----------------onReceive-----------");
	}
	
	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
		Log.d(TAG, "----------------onDisabled-----------");
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
		Log.d(TAG, "----------------onDeleted-----------");
	}
}
