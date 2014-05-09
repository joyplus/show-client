package com.joyplus.konka.jas;

import com.joyplus.adkey.AdKeySDKManager;
import com.joyplus.adkey.AdKeySDKManagerException;
import com.joyplus.adkey.downloads.AdFileManager;
import com.joyplus.adkey.downloads.DownLoadManager;
import com.joyplus.konka_jas.joyplus.konka.ADRequest;
import com.joyplus.konka_jas.joyplus.konka.KonkaConfig;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.util.Log;

import java.io.File;

public class Konka extends Application{

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mKonka = this;
		Log.d("Jas", "Konka Application"); 
        KonkaConfig.Init(this.getApplicationContext());
        Log.d("Jas","Konka Application -->"+KonkaConfig.ToString());
        File cache    = new File(KonkaConfig.GetCacheDir());
        cache.mkdirs();
        File Resource = new File(KonkaConfig.GetResourceDir());
        Resource.mkdirs();
        try {
            AdKeySDKManager.Init(this.getApplicationContext());
        } catch (AdKeySDKManagerException e) {
            e.printStackTrace();
        }
		ADRequest.Init(this.getApplicationContext());
		SetClock();
	}

    private static Konka mKonka;
    public  static Konka GetInstance(){
    	return mKonka;
    }
	private boolean SetClock = false;
	public  void SetClock(){
		if(SetClock)return;
		SetClock = true;
		Intent intent = new Intent("com.joyplus.konka");
		intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
		PendingIntent pi = PendingIntent.getBroadcast(Konka.this, 0, intent, 0);
		AlarmManager  am = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
		am.cancel(pi);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+KonkaConfig.GetRequstFrq(), KonkaConfig.GetRequstFrq(), pi);
		SetClock = false;
	}
	
}
