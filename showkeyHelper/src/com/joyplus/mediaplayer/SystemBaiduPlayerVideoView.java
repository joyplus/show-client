package com.joyplus.mediaplayer;

import com.baidu.cyberplayer.core.BVideoView;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.utils.Log;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SystemBaiduPlayerVideoView extends LinearLayout implements  ViewInterface,VideoViewInterface,
                                                                         BVideoView.OnErrorListener,
																         BVideoView.OnCompletionListener,
																         BVideoView.OnPreparedListener,
																         BVideoView.OnInfoListener{
	private boolean Debug = true;
	private String  TAG   = "SystemBaiduPlayerVideoView";
	private BaiduVideoView mVideoView;
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		if(Debug)Log.d(TAG,"onFinishInflate()"); 
		super.onFinishInflate();
		mVideoView = (BaiduVideoView) findViewById(R.id.BaiDuVideoView);
		BVideoView.setAKSK(JoyplusMediaPlayerManager.getInstance().getDataManager().getBaiduAK(), 
	            JoyplusMediaPlayerManager.getInstance().getDataManager().getBaiduSK());
		mVideoView.setOnErrorListener(this);
		mVideoView.setOnCompletionListener(this);
		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnInfoListener(this);
		mVideoView.setDecodeMode(1);
	}

	public SystemBaiduPlayerVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SystemBaiduPlayerVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInfo(int arg0, int arg1) {
		// TODO Auto-generated method stub
		Log.i(TAG,"Biadu info="+arg0+" "+arg1);
		mVideoView.SetINFO(arg0);
		return false;
	}

	@Override
	public void onPrepared() {
		// TODO Auto-generated method stub
		if(Debug)Log.d(TAG,"onPrepared()");
		mVideoView.SetState(STATE.MEDIA_STATE_INITED);
		mVideoView.setScreenLayoutParams(JoyplusMediaPlayerScreenManager.getInstance().getScreenParamsDefault());
	}

	@Override
	public void onCompletion() {
		// TODO Auto-generated method stub
		if(Debug)Log.d(TAG,"onCompletion()");
		mVideoView.SetState(STATE.MEDIA_STATE_FINISH);
	}

	@Override
	public boolean onError(int arg0, int arg1) {
		// TODO Auto-generated method stub
		if(Debug)Log.d(TAG,"onError("+arg0+" ,"+arg1+")");
		mVideoView.SetState(STATE.MEDIA_STATE_UNKNOW);
		return true;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setVisibility(boolean visible) {
		// TODO Auto-generated method stub
		if(Debug)Log.d("TAG","setVisibility() "+visible);
		if(visible == false){
			mVideoView.StopVideo();
		}
	}

	@Override
	public VideoViewInterface getVideoViewInterface() {
		// TODO Auto-generated method stub
		return mVideoView;
	}

	@Override
	public MediaInfo getTargetMediaInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MediaInfo getMediaInfo() {
		// TODO Auto-generated method stub
		return mVideoView.getMediaInfo();
	}

	@Override
	public void SetState(STATE state) {
		// TODO Auto-generated method stub
		mVideoView.SetState(state);
	}

	@Override
	public void SetVideoPaths(String video) {
		// TODO Auto-generated method stub
		mVideoView.SetVideoPaths(video);
		mVideoView.StartVideo();
	}

	@Override
	public void SetVideoVisibility(boolean visible) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void StartVideo() {
		// TODO Auto-generated method stub
		mVideoView.StartVideo();
	}

	@Override
	public void StopVideo() {
		// TODO Auto-generated method stub
		mVideoView.StopVideo();
	}

	@Override
	public void PauseVideo() {
		// TODO Auto-generated method stub
		mVideoView.PauseVideo();
	}

	@Override
	public void SeekVideo(int seekTo) {
		// TODO Auto-generated method stub
		mVideoView.SeekVideo(seekTo);
	}

	@Override
	public boolean IsPlaying() {
		// TODO Auto-generated method stub
		return mVideoView.IsPlaying();
	}

	@Override
	public void SetINFO(int info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean setScreenLayoutParams(int type) {
		// TODO Auto-generated method stub
		return mVideoView.setScreenLayoutParams(type);
	}

	@Override
	public int getScreenLayoutParams() {
		// TODO Auto-generated method stub
		return mVideoView.getScreenLayoutParams();
	}
}
