package com.joyplus.showkey.screensaver;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class ScreenShow extends Activity {
	
	private int[] resouces = {
			R.drawable.p1,
			R.drawable.p2,
			R.drawable.p3,
			R.drawable.p4,
			R.drawable.p5,
	};
	
	private int index = 0;
	private ImageView image1;
	private ImageView image2;
	private Animation appear;
	private Animation disappear;
	
	private static final int MESSAGE_SHOW_NEXT = 0;
	
	private Handler mHandler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_SHOW_NEXT:
				index +=1;
				showNext();
				break;

			default:
				break;
			}
		}

		
	};
	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		finish();
		return true;
	};
		
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
        		WindowManager.LayoutParams. FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 

        setContentView(R.layout.activity_main);
        image1 = (ImageView) findViewById(R.id.image_1);
        image2 = (ImageView) findViewById(R.id.image_2);
        appear = AnimationUtils.loadAnimation(this, R.anim.alpha_appear);
        disappear = AnimationUtils.loadAnimation(this, R.anim.alpha_disappear);
        showNext();
    }

    private void showNext() {
		// TODO Auto-generated method stub
    	if(index%2==0){
    		int resouceId = resouces[index%resouces.length];
    		image1.setImageResource(resouceId);
    		image1.setVisibility(View.VISIBLE);
    		image1.startAnimation(appear);
    		image2.setVisibility(View.GONE);
    		image2.startAnimation(disappear);
    	}else{
    		int resouceId = resouces[index%resouces.length];
    		image2.setImageResource(resouceId);
    		image1.setVisibility(View.GONE);
    		image1.startAnimation(disappear);
    		image2.setVisibility(View.VISIBLE);
    		image2.startAnimation(appear);
    	}
    	mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_NEXT, 15000);
	}
    
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		   	   		
		//AlarmAlertWakeLock.release();
		Intent i = new Intent("ScreenShow");
        sendBroadcast(i);
        
//        AlarmAlertWakeLock.release() ;
        
		Log.d(AlarmAlertWakeLock.TAG,"-------->req:release||Broadcast: ScreenShow");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Intent i = new Intent("NewScreenSaver");
		sendBroadcast(i);
//        Log.d(AlarmAlertWakeLock.TAG,"------->onStop req:NewScreenSaver");
		finish() ;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mHandler.removeMessages(MESSAGE_SHOW_NEXT);
		super.onDestroy();
	}
}
