package com.joyplus.showkey.screensaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
			Intent i = new Intent(context,ScreenSaverService.class);
	        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        context.startService(i);
		}
	}
}
