package com.joyplus.mediaplayer;


import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.joyplus.mediaplayer.VideoViewInterface.STATE;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.utils.Log;

/*define by Jas@20130723 for monitor player state*/
public class JoyplusPlayerMonitor{

		private boolean Debug = false;
	    private String  TAG   = "JoyplusPlayerMonitor";
	    public  Handler mHandler;
	    private VideoViewInterface  mPlayer;
	    private static  int DELAY = 500;
	    private boolean Flog = false;
	    public  static final int MSG_STATEUPDATE       = 1;
	    public  static final int MSG_NOPROCESSCOMMEND  = 2;
	    private Object mObject = new Object();
	    public JoyplusPlayerMonitor(Context context,VideoViewInterface player){
	    	  mPlayer  = player;
	    	  setUpdateTime(Integer.parseInt(context.getString(R.string.defaultUpdateTime)));
	    }

		public void setUpdateTime(int time){
			if(Debug)Log.d(TAG,"setUpdateTime("+time+")");
			if(time>=300 && time<=800){
				DELAY  = time;
			}
		}

	  
		private void notityState(){
			if(Debug)Log.d(TAG,"notityState()mHandler="+(mHandler == null)+" mPlayer="+(mPlayer == null));
			if(mHandler == null || mPlayer == null)return;
			synchronized (mObject) {
				mHandler.removeCallbacksAndMessages(null);
				Message m = new Message();
				m.what    = MSG_STATEUPDATE;
				m.obj     = mCurrentInfo;			
				mHandler.sendMessage(m);
			}			
		}
	    public void stopMonitor(){
	    	if(Debug)Log.d(TAG,"stopMonitor()");
	    	synchronized (mObject) {
		    	Flog = false;
		    	mRunnable = null;
				if(mHandler != null)
					mHandler.removeCallbacksAndMessages(null);
				mHandler = null;
	    	}
	    }
	    public void startMonitor(Handler handler){
	    	if(Debug)Log.d(TAG,"startMonitor()");
	    	mHandler = handler;
	    	if(mRunnable==null){	
	    	  Flog = true;
	    	  mRunnable = new MediaPlayerMonitor();
	    	  mRunnable.start();
	    	}
	    }
	    private MediaPlayerMonitor mRunnable;
	    private class MediaPlayerMonitor extends Thread{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(Flog){
					try {
						Thread.sleep(DELAY);
						CheckMediaInfo();
						notityState();
						System.gc();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace(); 
					}
				}
			}
	    };
	    /*Add AutoMediaInfo judge*/
	    private MediaInfo mCurrentInfo  = new MediaInfo();
	    private MediaInfo mPreMediaInfo = new MediaInfo();
	    private final int MAXCount      = 30*1000/DELAY;
	    private int       Count         = 0;
	    private void CheckMediaInfo(){
	    	mCurrentInfo = mPlayer.getMediaInfo();
	    	if(mCurrentInfo.getState() == STATE.MEDIA_STATE_PLAYING
	    			&& mPreMediaInfo.getState() == STATE.MEDIA_STATE_PLAYING){
	    		if(mCurrentInfo.getCurrentTime() == mPreMediaInfo.getCurrentTime()){
	    			if(++Count>=MAXCount)mCurrentInfo.setState(STATE.MEDIA_STATE_UNKNOW);
	    		}else{
	    			Count = 0;
	    		}
	    	}else Count = 0;
	    	mPreMediaInfo = mCurrentInfo.CreateMediaInfo();
	    }
}
