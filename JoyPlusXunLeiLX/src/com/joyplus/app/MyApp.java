package com.joyplus.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;

public class MyApp extends Application {
	
	private static final String TAG = "MyApp";
	
	public static ExecutorService pool = null;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		pool = Executors.newFixedThreadPool(2);
	}

}
