package com.joyplus.mediaplayer;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.utils.Log;

public class SystemVitamioPlayerVideoView extends LinearLayout  implements ViewInterface,VideoViewInterface, OnErrorListener,
                                                                           OnCompletionListener, OnPreparedListener,OnInfoListener{
    
	private boolean Debug = true;
	private String  TAG   = "SystemVitamioPlayerVideoView";
	private VitamioVideoView mVideoView = null;
    @Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		if(Debug)Log.d(TAG,"onFinishInflate()");
		mVideoView = (VitamioVideoView) findViewById(R.id.VitamioVideoView);
		mVideoView.setOnErrorListener(this);
		mVideoView.setOnCompletionListener(this);
		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnInfoListener(this);
		mVideoView.setBufferSize(2048);
	}

	public SystemVitamioPlayerVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SystemVitamioPlayerVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setVisibility(boolean visible) {
		// TODO Auto-generated method stub
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
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		if(Debug)Log.d(TAG,"onPrepared()");
		mVideoView.SetState(STATE.MEDIA_STATE_INITED);
		mVideoView.setScreenLayoutParams(JoyplusMediaPlayerScreenManager.getInstance().getScreenParamsDefault());
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		if(Debug)Log.d(TAG,"onCompletion()");
		mVideoView.SetState(STATE.MEDIA_STATE_FINISH);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		if(Debug)Log.d(TAG,"onError("+what+" ,"+extra+")");
		mVideoView.SetState(STATE.MEDIA_STATE_UNKNOW);
		return true;
	}

	@Override
	public MediaInfo getTargetMediaInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void SetINFO(int info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		Log.i("Jas","Vitamio info="+what+" "+extra);
		mVideoView.SetINFO(what);
		return false;
	}

	@Override
	public boolean setScreenLayoutParams(int type) {
		// TODO Auto-generated method stub
		mVideoView.setScreenLayoutParams(type);
		return true;
	}

	@Override
	public int getScreenLayoutParams() {
		// TODO Auto-generated method stub
		return mVideoView.getScreenLayoutParams() ;
	}

	
}
