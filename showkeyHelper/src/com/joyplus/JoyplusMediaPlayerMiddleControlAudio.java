package com.joyplus;


import android.content.Context;
import android.media.AudioManager;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.LinearLayout;

import com.joyplus.tvhelper.R;

public class JoyplusMediaPlayerMiddleControlAudio extends LinearLayout implements JoyplusMediaPlayerInterface{
	private Context      mContext;
	private AudioManager mAudioManager;
	private ArcView      mArcView;
	
	public JoyplusMediaPlayerMiddleControlAudio(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
	}
	
	public JoyplusMediaPlayerMiddleControlAudio(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context; 
	}
	
	protected void onFinishInflate() {
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
		mArcView      = (ArcView) findViewById(R.id.joyplusvideoview_audio_arcview);
		UpdateAudioUI();
	}
	@Override
	public boolean JoyplusdispatchMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
	private void UpdateAudioUI(){
		int mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int index      = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		int mAngle = index * 360 / mMaxVolume;
		if (index == 0)
			mArcView.setBackgroundResource(R.drawable.player_volume_mute);
		else {
			mArcView.setBackgroundResource(R.drawable.player_volume);
		}
		mArcView.SetAngle(mAngle);
	}
	@Override
	public boolean JoyplusonKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch(keyCode){
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_DPAD_UP:
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_VIBRATE);
			UpdateAudioUI();
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_DPAD_DOWN:
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_VIBRATE);
			UpdateAudioUI();
			return true;
		case KeyEvent.KEYCODE_VOLUME_MUTE:
			//return true;
			break;
		}
		return false;
	}
	@Override
	public void JoyplussetVisible(boolean visible, int layout) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int JoyplusgetLayout() {
		// TODO Auto-generated method stub
		return JoyplusMediaPlayerMiddleControl.LAYOUT_AUDIO;
	}
	@Override
	public boolean JoyplusonKeyLongPress(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
