package com.joyplus.mediaplayer;

import android.content.Context;
import android.util.AttributeSet;
import com.baidu.cyberplayer.core.BVideoView;
import com.joyplus.tvhelper.utils.Log;

public class BaiduVideoView extends BVideoView implements VideoViewInterface{
	private boolean Debug = true;
	private String  TAG   = "BaiduVideoView";	
	private MediaInfo mMediaInfo;
	public BaiduVideoView(Context context, String pkgName) {
		super(context, pkgName);
		// TODO Auto-generated constructor stub
		Init();
	}
	
	public BaiduVideoView(Context context, AttributeSet attrs, String pkgName) {
		super(context, attrs, pkgName);
		// TODO Auto-generated constructor stub
		Init();
	}

	public BaiduVideoView(Context context, AttributeSet attrs, int defStyle,
			String pkgName) {
		super(context, attrs, defStyle, pkgName);
		// TODO Auto-generated constructor stub
		Init();
	}

	public BaiduVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		Init();
	}

	public BaiduVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		Init();
	}

	public BaiduVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		Init();
	}
	
	private void Init() {
		// TODO Auto-generated method stub
		if(Debug)Log.d(TAG,"Init()");
		mMediaInfo = new MediaInfo();
	}
	@Override
	public MediaInfo getMediaInfo() {
		// TODO Auto-generated method stub
		if(Debug)Log.d(TAG,"getMediaInfo() "+mMediaInfo.toString());
		if(mMediaInfo.getState() == STATE.MEDIA_STATE_PUSE
				||mMediaInfo.getState() == STATE.MEDIA_STATE_PLAYING
				||mMediaInfo.getState() == STATE.MEDIA_STATE_INITED){
			mMediaInfo.setCurrentTime(this.getCurrentPosition()*1000);//for it report by second
			mMediaInfo.setTotleTime(this.getDuration()*1000);//for it report by second
		}else if(mMediaInfo.getPath() == null){
			mMediaInfo.setCurrentTime(0);
			mMediaInfo.setTotleTime(0);
		}
		return mMediaInfo;
	}
	@Override
	public void SetState(STATE state) {
		// TODO Auto-generated method stub
		if(Debug)Log.d(TAG,"setState("+state.toInt()+")");
		mMediaInfo.setState(state);
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setVisibility(boolean visible) {
		// TODO Auto-generated method stub
		if(Debug)Log.d(TAG,"SetVideoVisibility()");
	}

	@Override
	public VideoViewInterface getVideoViewInterface() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MediaInfo getTargetMediaInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void SetVideoPaths(String video) {
		// TODO Auto-generated method stub
		if(Debug)Log.d(TAG,"setVideoPaths("+video+")");
		this.setVideoPath(video);
		mMediaInfo.setPath(video);
	}

	@Override
	public void SetVideoVisibility(boolean visible) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void StartVideo() {
		// TODO Auto-generated method stub
		if(Debug)Log.d(TAG,"startVideo()");
		this.start();  
		mMediaInfo.setState(STATE.MEDIA_STATE_PLAYING);
	}

	@Override
	public void StopVideo() {
		// TODO Auto-generated method stub
		if(Debug)Log.d(TAG,"stopVideo()");
		mMediaInfo.setState(STATE.MEDIA_STATE_IDLE);
		this.stopPlayback();
	}

	@Override
	public void PauseVideo() {
		// TODO Auto-generated method stub
		if(Debug)Log.d(TAG,"pauseVideo()");
		this.pause();
		mMediaInfo.setState(STATE.MEDIA_STATE_PUSE);
	}

	@Override
	public void SeekVideo(int seekTo) {
		// TODO Auto-generated method stub
		if(Debug)Log.d(TAG,"seekVideo("+seekTo+")");
		this.seekTo(seekTo);
		//mMediaInfo.setState(STATE.MEDIA_STATE_LOADING);
	}

	@Override
	public boolean IsPlaying() {
		// TODO Auto-generated method stub
		if(Debug)Log.d(TAG,"IsPlaying()");
    	return this.isPlaying();
	}

	@Override
	public void SetINFO(int info) {
		// TODO Auto-generated method stub
		mMediaInfo.setINFO(info);
	}

	@Override
	public boolean setScreenLayoutParams(int type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getScreenLayoutParams() {
		// TODO Auto-generated method stub
		return 0;
	}

}
