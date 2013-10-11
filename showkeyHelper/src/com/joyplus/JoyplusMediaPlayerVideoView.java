package com.joyplus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joyplus.mediaplayer.JoyplusMediaPlayerScreenManager;
import com.joyplus.mediaplayer.JoyplusVideoView;
import com.joyplus.mediaplayer.MediaInfo;
import com.joyplus.mediaplayer.VideoViewInterface;
import com.joyplus.mediaplayer.VideoViewInterface.STATE;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.utils.Log;
/*videoview layout
 * It use to displayer media
 * it have: Android system VideoView 
 *          vitamio VideoView
 *          
 **/
public class JoyplusMediaPlayerVideoView implements JoyplusMediaPlayerInterface{

	private boolean Debug = false;
	private String  TAG   = "JoyplusMediaPlayerVideoView";
	
	private JoyplusMediaPlayerActivity mActivity;
	private VideoViewInterface         Player = null;
	private JoyplusVideoView           VideoView;
	public  static MediaInfo           CurrentMediaInfo;
	private MediaInfo                  PreMediaInfo;	
	private LoadingWindows             mWaitingWindows;
	private final static int MSG_BASE  = 100;
	public  final static int LAYOUT_VIDEOVIEW = MSG_BASE+1;

	public void Init(){
		update();
		CurrentMediaInfo = new MediaInfo();
		PreMediaInfo     = new MediaInfo();
		mWaitingWindows.Init();
	}
    public JoyplusMediaPlayerVideoView(JoyplusMediaPlayerActivity activity){
    	mActivity = activity;
    	InitResource();
    }
    private void InitResource() {
		// TODO Auto-generated method stub
    	VideoView = (JoyplusVideoView) mActivity.findViewById(R.id.JoyplusVideoView);
    	//mWaitingDialog   = new RoundProcessDialog(mActivity);
    	mWaitingWindows  = new LoadingWindows();
	}
	public VideoViewInterface getPlayer(){
    	return Player;
    }
    public void update(){
    	if(Debug)Log.d(TAG,"VideoViewControl update()");
    	VideoView.Update();
        Player = VideoView.getVideoView();
    }
    
	@Override
	public boolean JoyplusdispatchMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case JoyplusMediaPlayerActivity.MSG_MEDIAINFO:
			PreMediaInfo     = new MediaInfo(CurrentMediaInfo);
			CurrentMediaInfo = ((MediaInfo) msg.obj).CreateMediaInfo();
			return CheckMediaInfo(); 
		}
		return false;
	}
	private boolean CheckMediaInfo(){
		//don't do this twice
		//if(CurrentMediaInfo.getState() == STATE.MEDIA_STATE_UNKNOW)Init();
		if(hasMediaInfoChange()){
			mWaitingWindows.setVisible(false);
		}else if(CurrentMediaInfo.getState().toInt()>STATE.MEDIA_STATE_INITED.toInt()
		     && CurrentMediaInfo.getState().toInt()<STATE.MEDIA_STATE_FINISH.toInt()
		     && CurrentMediaInfo.getState().toInt() != STATE.MEDIA_STATE_PUSE.toInt()){
			 if( CurrentMediaInfo.getINFO() == 701 //loading
					&& CurrentMediaInfo.getCurrentTime()>1000
					&& JoyplusMediaPlayerActivity.StateOk
					&& CurrentMediaInfo.getState()==STATE.MEDIA_STATE_PLAYING
					){
				mWaitingWindows.setVisible(true);
			 }else{
				mWaitingWindows.setVisible(false);
			 }
		}
		if(CurrentMediaInfo.getPath()==null || "".equals(CurrentMediaInfo.getPath()))return true;
		if(PreMediaInfo.getPath()!=null && !PreMediaInfo.getPath().equals(CurrentMediaInfo.getPath()))return true;
		return false;
	}
	@Override
	public boolean JoyplusonKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub 
		return false;
	}
	@Override
	public void JoyplussetVisible(boolean visible,int layout) {
		// TODO Auto-generated method stub
		if(Debug)Log.d(TAG,"setVisibliable("+visible+")");
    	if(visible){
    		update();
    	}else{
    		VideoView.hideView();
    	}
	}
	@Override
	public int JoyplusgetLayout() {
		// TODO Auto-generated method stub
		return LAYOUT_VIDEOVIEW;
	}
	public boolean setScreenLayoutParams(int type){
		if(!JoyplusMediaPlayerScreenManager.IsAviableType(type))return false;
		return VideoView.setScreenLayoutParams(type);
	}  
	public int getScreenLayoutParams(){
		return VideoView.getScreenLayoutParams();
	}
	public boolean hasMediaInfoChange(){
		long delay = Math.abs(CurrentMediaInfo.getCurrentTime()-PreMediaInfo.getCurrentTime());
		if(Debug)Log.e(TAG,"eeeeeeeeeeeeeeeee  "+delay +" eeeeeeeeeeee");
		return (delay<2000&&delay>=300);
	}
	private class LoadingWindows {
		private final static int MSG_SHOW = 1;
		private final static int MSG_HIDE = 2;
		private final static int DELAYTIME  = 1*60*1000; //1 min
		
		private class WindowsInfo{
			public long ShowTime;
			public WindowsInfo(long time){
				ShowTime = time;
			}
		}
		private List<WindowsInfo> mWindowsInfo = new ArrayList<WindowsInfo>();
		
	    private TextView       mInfo;
		private LinearLayout   mLayout;
		private TextView       mNotify;
		
		private long mStartRX    = 0;
		private long rxByteslast = 0;
		public void Init(){
			mHandler.removeCallbacksAndMessages(null);
			mWindowsInfo = new ArrayList<WindowsInfo>();
			mStartRX    = 0;
			rxByteslast = 0;
			if((mLayout.getVisibility()==View.VISIBLE))mLayout.setVisibility(View.GONE);
		}
		public LoadingWindows(){
			mInfo   = (TextView)       mActivity.findViewById(R.id.joyplus_videoview_buffer_info);
			mLayout = (LinearLayout)   mActivity.findViewById(R.id.joyplus_videoview_buffer);
			mNotify = (TextView)       mActivity.findViewById(R.id.joyplus_videoview_buffer_notify);
		}
		public void setVisible(boolean Visible){
			if(!Visible)mHandler.removeCallbacksAndMessages(null);
			if((mLayout.getVisibility()==View.VISIBLE) == Visible)return;
			mHandler.removeCallbacksAndMessages(null);
			if(Visible){
				mHandler.sendEmptyMessage(MSG_SHOW);
				UpdateWindowsInfo();
			}else mHandler.sendEmptyMessage(MSG_HIDE);
		}
		private Handler mHandler = new Handler(){
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch(msg.what){
				case MSG_SHOW:
					if(mLayout.getVisibility()==View.VISIBLE)return;
					mLayout.setVisibility(View.VISIBLE);
					mHandler.removeCallbacksAndMessages(null);
					StartTrafficStates();
					break;
				case MSG_HIDE:
					if(!(mLayout.getVisibility()==View.VISIBLE))return;
					mLayout.setVisibility(View.GONE);
					mHandler.removeCallbacksAndMessages(null);
					break;
				}
			}
		};
		private void StartTrafficStates(){
			if(Debug)Log.d("Jas","=========StartTrafficStates===========");
			mStartRX = TrafficStats.getTotalRxBytes();// ��ȡ�����ٶ�
			rxByteslast = 0;
			if (mStartRX == TrafficStats.UNSUPPORTED) {
			     
			} else {
				mHandler.removeCallbacks(UpdateTrafficStats);
				mHandler.postDelayed(UpdateTrafficStats, 500);
			}
		}
		private Runnable UpdateTrafficStats = new Runnable(){
			long beginTimeMillis, timeTakenMillis, m_bitrate;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(Debug)Log.d("Jas","-------------run-------------");
				long rxBytes = TrafficStats.getTotalRxBytes() - mStartRX;
				timeTakenMillis = System.currentTimeMillis() - beginTimeMillis;
				beginTimeMillis = System.currentTimeMillis();
				if(timeTakenMillis>0){
					m_bitrate = (rxBytes - rxByteslast) / timeTakenMillis;
					rxByteslast = rxBytes;
					UpdateInfo(m_bitrate);
				}	
				mHandler.removeCallbacks(UpdateTrafficStats);
				mHandler.postDelayed(UpdateTrafficStats, 500);
			}
		};
		private void UpdateInfo(long speed){
			if(Debug)Log.d("Jas","============= speed="+speed+" =========================");
			if(!(mLayout.getVisibility()==View.VISIBLE))return;
			mInfo.setVisibility(View.VISIBLE);
			if(speed>=0)
				mInfo.setText(mActivity.getApplicationContext().getString(R.string.meidaplayer_loading_string_buffer,speed));
			else
				mInfo.setText(mActivity.getApplicationContext().getString(R.string.meidaplayer_loading_string_buffer_loading));
			UpdateNotify();
		}
		private void UpdateNotify(){
			if(!(mLayout.getVisibility()==View.VISIBLE))return;
			if(mWindowsInfo.size()>=10)
				mNotify.setVisibility(View.VISIBLE);
			else 
				mNotify.setVisibility(View.GONE);
		}
		private void UpdateWindowsInfo(){
			long currenttime = System.currentTimeMillis();
			mWindowsInfo.add(new WindowsInfo(currenttime)); 
			Iterator<WindowsInfo> it = mWindowsInfo.iterator();
			while(it.hasNext()){
				WindowsInfo index = it.next();
				if((currenttime-index.ShowTime)>DELAYTIME){
					it.remove();
				}
			}
		}
	}
	

	@Override
	public boolean JoyplusonKeyLongPress(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}
