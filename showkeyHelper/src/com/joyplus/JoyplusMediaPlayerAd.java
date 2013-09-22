package com.joyplus;

import java.io.File;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.joyplus.ad.AdvertManager;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.utils.Constant;
import com.joyplus.tvhelper.utils.Log;

public class JoyplusMediaPlayerAd implements MediaPlayer.OnCompletionListener,
                                               MediaPlayer.OnPreparedListener{
	
	private String TAG = "JoyplusMediaPlayerAd";
	
	private JoyplusMediaPlayerActivity mActivity;
	private VideoView mAdVideoView;
	private RelativeLayout mLayout;
	private TextView  mTime;
	private long Time = 16*1000;
	private final static long MAXTIME = 16*1000;
	private AdvertManager mManager;
	enum ADSTATE{
		NOAD , PREPARE , PLAYING , FINISH
	};
	private ADSTATE mAdState;
	public void setAdState(ADSTATE adstate){
		mAdState = adstate;
	}
	public ADSTATE getAdState(){
		return mAdState;
	}
	public JoyplusMediaPlayerAd(JoyplusMediaPlayerActivity activity){
		mActivity = activity;
		mAdState = ADSTATE.NOAD;
		mAdVideoView = (VideoView)      mActivity.findViewById(R.id.joyplusmediaplayer_advideoView);
		mAdVideoView.setOnCompletionListener(this);
		mAdVideoView.setOnPreparedListener(this);
		mLayout      = (RelativeLayout) mActivity.findViewById(R.id.joyplusmediaplayer_advideoView_layout);
		mTime        = (TextView)       mActivity.findViewById(R.id.joyplusmediaplayer_advideoView_time);
		try {
			//mManager = new AdvertManager(mActivity, getPUBLISHERID(), true);	
			mManager = new AdvertManager(mActivity, Constant.PLAYERPACH_ADV_PUBLISHERID, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			mManager = null;
			e.printStackTrace();
		}
		UpdateAd();
	}
	public void UpdateAd() {
		// TODO Auto-generated method stub
		Log.i(TAG," UpdateAd()");
		new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(mManager != null){
					mManager.UpdateAdvert();
				}
			}}
		).start();		
	}
	public boolean isPlaying(){
		return mLayout.getVisibility()==View.VISIBLE;
	}
	public void startAD(){
		mAdState = ADSTATE.PLAYING;
		if(!CheckAdFile())return;
		mLayout.setVisibility(View.VISIBLE);
		Time = MAXTIME;
		mAdVideoView.setVideoPath(getPlayerUri());
		mAdState = ADSTATE.PREPARE;
		new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ADWait();	
				if(mManager != null){
					mManager.ReportCount();
				}
			}			
		}).start();
	}
	private void ADWait(){
		long time1 ;
		long time2 ;
		while(mAdState != ADSTATE.FINISH){
			try {
				time1 = System.currentTimeMillis();
				Thread.sleep(500);
				time2 = System.currentTimeMillis();
				Time -=(time2-time1);
				CheckTime();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mActivity.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mLayout.setVisibility(View.GONE);
			}			
		});	
		
	}
	private void CheckTime() {
		// TODO Auto-generated method stub
		if((Time<=0 && mAdState != ADSTATE.FINISH) 
			||(mActivity.StateOk && mAdState != ADSTATE.FINISH)){
			mAdVideoView.stopPlayback();
			mAdState = ADSTATE.FINISH;
		}
		if(Time<0)Time = 0;
		mActivity.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mTime.setText(String.valueOf(Time/1000));
			}			
		});		
	}
	private boolean CheckAdFile(){
		if(mManager == null)return false;		
		String uri = getPlayerUri();
		Log.d(TAG,"CheckAdFile() "+uri);
		if(uri == null)return false;
		File ad = new File(uri);
		if(ad.exists() && ad.canRead())return true;
		mAdState = ADSTATE.NOAD;
		return false;
	}
	public String getPlayerUri(){
		if(mManager == null)return null;
		return mManager.getPlayUri(); 
	}
	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		mAdState = ADSTATE.FINISH;
	}
	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub 
		mAdVideoView.start();
		mAdState = ADSTATE.PLAYING;
	}
}
