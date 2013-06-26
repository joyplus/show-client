package com.joyplus.showkey.screensaver;//

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ScreenSaverService extends Service {

	private static int mNewScreenSaver = 1 ;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d(AlarmAlertWakeLock.TAG, "ScreenSaverService--->onCreate()");
		
		/* ï¿½ï¿½È¡ ï¿½ï¿½ï¿½ï¿½ï¿½Ø»ï¿½ï¿½ï¿½ */
		KeyguardManager mKeyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardLock mKeyguardLock = mKeyguardManager.newKeyguardLock("AlarmClock");
		mKeyguardLock.disableKeyguard();
		
		buildScreenBroadcastReceiver();
	}


	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		Log.d(AlarmAlertWakeLock.TAG, "ScreenSaverService--->onStart()");
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mMasterResetReciever);
		PowerManagerWakeLock.release();
		super.onDestroy();
		Log.d(AlarmAlertWakeLock.TAG, "ScreenSaverService--->onDestroy()");
	}
	
	private void buildScreenBroadcastReceiver(){
		
		IntentFilter f = new IntentFilter();	
		 
		f.addAction(Intent.ACTION_SCREEN_ON);
		f.addAction(Intent.ACTION_SCREEN_OFF);
//		f.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
		f.addAction(Intent.ACTION_PACKAGE_REMOVED);
		f.addAction("ScreenShow");
		f.addAction("NewScreenSaver");
		f.addDataScheme("package");
		registerReceiver(mMasterResetReciever, f);

	}
	
	BroadcastReceiver mMasterResetReciever= new BroadcastReceiver() {		
    	public void onReceive(Context context, Intent intent){ 
    		try{
    			String action = intent.getAction();
    			Log.d("BroadcastReceiver", "action = "  + action);
//    			if(action.equals(Intent.ACTION_SCREEN_ON) ){
//    				Log.d(AlarmAlertWakeLock.TAG,"-------->Intent.ACTION_SCREEN_ON");
//    				stopScreenSaverTimeOut();    				
//    			}
//    			if(action.equals(Intent.ACTION_SCREEN_OFF) ){
//    				Log.d(AlarmAlertWakeLock.TAG,"-------->Intent.ACTION_SCREEN_OFF||mNewScreenSaver:="+mNewScreenSaver);
//    				
//    				if( mNewScreenSaver == 1 ){  					
//    					startScreenSaverTimeOut(); 
//    				}
//    			}
    			if(action.equals(Intent.ACTION_PACKAGE_REMOVED)){
    				if(mNewScreenSaver == 1){
    					Intent localIntent = new Intent();
        			    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        			    localIntent.setClass(ScreenSaverService.this, ScreenShow.class);
        			    startActivity(localIntent);
    				}
    			}else if(action.equals("ScreenShow") ){
    				mNewScreenSaver = 0 ;
    			}else if(action.equals("NewScreenSaver") ){
    				Log.d(AlarmAlertWakeLock.TAG,"-------->mNewScreenSaver=1");
    				mNewScreenSaver = 1 ;
    			}if (action.equals(Intent.ACTION_SCREEN_ON)) {  
					  
	            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {//Èç¹û½ÓÊÜµ½¹Ø±ÕÆÁÄ»µÄ¹ã²¥  
	                if (mNewScreenSaver==0) {  
	                    //¿ªÆôÆÁÄ»»½ÐÑ£¬³£ÁÁ  
	                	PowerManagerWakeLock.acquire(ScreenSaverService.this);  
	                }  
	                PowerManagerWakeLock.acquire(ScreenSaverService.this);  
	                Intent intent2 = new Intent(ScreenSaverService.this,  
	                		ScreenShow.class);  
	                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
	                startActivity(intent2);  
	                PowerManagerWakeLock.release();  
	            }  
    			
              }catch(Exception e){
                Log.d("Output:", e.toString());
              }     
    			
    		}
    	};
//    private void  startScreenSaverTimeOut(){
//    	Log.d(AlarmAlertWakeLock.TAG,"------>startScreenSaverTimeOut");
//    	Intent intent = new Intent(this, AlarmReceiver.class); 
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (5*1000), pendingIntent);
//        alarmManager.cancel(null);

//    }
//    private void stopScreenSaverTimeOut(){
//    	Log.d(AlarmAlertWakeLock.TAG,"------>stopScreenSaverTimeOut");
//    	Intent intent = new Intent(this, AlarmReceiver.class); 
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        
//    	AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//    	alarmManager.cancel(pendingIntent);
//    }
    
}
