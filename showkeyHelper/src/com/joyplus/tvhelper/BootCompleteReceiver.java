package com.joyplus.tvhelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.joyplus.tvhelper.faye.FayeService;
import com.joyplus.tvhelper.utils.PreferencesUtils;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(PreferencesUtils.getPincodeMd5(context)!=null){
			context.startService(new Intent(context, FayeService.class));
		}
	}

}
