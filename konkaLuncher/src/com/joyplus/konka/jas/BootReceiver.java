package com.joyplus.konka.jas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.joyplus.konka_jas.joyplus.konka.ADRequest;

public class BootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		ADRequest r = ADRequest.GetInstance();
		if(r!=null){
			r.request();
		}else{
			Log.d("Jas","BootReceiver ADRequest is null ");
		}
	}

}
