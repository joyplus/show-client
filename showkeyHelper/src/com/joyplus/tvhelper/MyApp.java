package com.joyplus.tvhelper;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.joyplus.tvhelper.entity.CurrentPlayDetailData;
import com.joyplus.tvhelper.entity.service.ReturnProgramView;

public class MyApp extends Application {
	
	private static final String TAG = "MyApp";
	
	public static ExecutorService pool = null;
	
	private static MyApp instance;
	
	private CurrentPlayDetailData mCurrentPlayDetailData;
	private ReturnProgramView m_ReturnProgramView = null;
	
	private Map<String, String> headers;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		pool = Executors.newFixedThreadPool(2);
	}
	
	/**
	 * @return the main context of the App
	 */
	public static Context getAppContext() {
		return instance;
	}

	/**
	 * @return the main resources from the App
	 */
	public static Resources getAppResources() {
		return instance.getResources();
	}
	
	/**
	 * 播放时详细的基本参数
	 * @return
	 */
	public CurrentPlayDetailData getmCurrentPlayDetailData() {
		return mCurrentPlayDetailData;
	}

	/**
	 * 播放时详细的基本参数
	 * @return
	 */
	public void setmCurrentPlayDetailData(
			CurrentPlayDetailData mCurrentPlayDetailData) {
		this.mCurrentPlayDetailData = mCurrentPlayDetailData;
	}
	
	public ReturnProgramView get_ReturnProgramView() {
		return m_ReturnProgramView;
	}

	public void set_ReturnProgramView(ReturnProgramView m_ReturnProgramView) {
		this.m_ReturnProgramView = m_ReturnProgramView;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

}
