package com.joyplus;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.joyplus.mediaplayer.MediaInfo;
import com.joyplus.mediaplayer.VideoViewInterface.STATE;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.Utils;

public class JoyplusMediaPlayerBar implements JoyplusMediaPlayerInterface{
    
	private boolean Debug = true;
	private String  TAG   = "JoyplusMediaPlayerBar";
	private JoyplusMediaPlayerActivity mActivity;
	private VideoViewController        mBottomBar;
	private VideoViewTopBar            mTopBar;
	private View                       mTopMask,mBottomMask;

	private static final int SHOWTIME = 1500;//1.5s
	
	private static final int MSG_BASE        = 300;
	private static final int MSG_SHOWVIEW    = MSG_BASE+1;
	private static final int MSG_HIDEVIEW    = MSG_BASE+2;
	//private static final int MSG_UPDATETIME  = MSG_BASE+3;
	private static final int MSG_REQUESTSHOW = MSG_BASE+4;
	private static final int MSG_REQUESTHIDE = MSG_BASE+5;
	private static final int LAYOUT_BAR      = MSG_BASE+7;
    
	public static final int  MSG_SHOWANDKEYLONGPRESS = MSG_BASE+8;
	public static final int  MSG_SHOWANDKEYDOWN      = MSG_BASE+9;
	public static final int  MSG_SHOWANDHOLD         = MSG_BASE+10;
	
	private static final int MSG_UPDATE_SEEKBAR      = MSG_BASE+11;
	private static final int MSG_UPDATE_SEEKBARMODE  = MSG_BASE+12;
	/*use to control seekbar*/
	enum SEEKMODE{
		NORMAL , LONGPRESS ,PRESS
	}
	enum SEEKTYPE{
		NORMAL , FORWARD , BACKWARD
	}
	enum SPEED{
		X0 ("x0"),X1 ("x1"), X2 ("x2"), X3 ("x3");
		private String speed;
		SPEED(String Speed){
			speed = Speed;
		}
		public String toString(){
			return speed;
		}
		public int toInt(){
			if(speed.equals(SPEED.X0.toString()))return 0;
			else if(speed.equals(SPEED.X1.toString()))return 1;
			else if(speed.equals(SPEED.X2.toString()))return 2;
			else if(speed.equals(SPEED.X3.toString()))return 3;
			else return 0;
		}
	}
	public float getIntSpeed(SPEED speed){
		if(speed == null)return 0;
		if(speed == SPEED.X3)return 20f;
		if(speed == SPEED.X2)return 10f;
		if(speed == SPEED.X1)return 5f;
		if(speed == SPEED.X0)return 0;
		return 0;
	}
	public SPEED getNextSpeed(SPEED speed){
		if(speed == null)return null;
		if(speed == SPEED.X0){
			return SPEED.X1;
		}else if(speed == SPEED.X1){
			return SPEED.X2;
		}else if(speed == SPEED.X2){
			return SPEED.X3;
		}else if(speed == SPEED.X3){
			return SPEED.X3;
		}else{
			return SPEED.X0;
		}
	}
	public void Init(){
		mHandler.removeCallbacksAndMessages(null);
		mBottomBar.Init();
		mTopBar.Init();
	}
	
	private void setMaskVisible(boolean isVisible){
		if(!isVisible && mTopMask.getVisibility() == View.VISIBLE)
			mTopMask.startAnimation(JoyplusMediaPlayerActivity.mAlphaDispear);
		if(!isVisible && mBottomMask.getVisibility() == View.VISIBLE)
			mBottomMask.startAnimation(JoyplusMediaPlayerActivity.mAlphaDispear);
		mTopMask.setVisibility(isVisible?View.VISIBLE:View.GONE);
		mBottomMask.setVisibility(isVisible?View.VISIBLE:View.GONE);
	}
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case JoyplusMediaPlayerActivity.MSG_MEDIAINFO:
				mBottomBar.dispatchMessage(msg);
				break;
			case MSG_SHOWVIEW:
				setMaskVisible(true);
				mTopBar.setVisible(true);
				mBottomBar.setVisible(true);
				if(!mBottomBar.SEEKING
					|| !(mBottomBar.mSeekBarState.mSeekBarType == SEEKTYPE.NORMAL))
				      setVisible(false,SHOWTIME);
				break;
			case MSG_HIDEVIEW:				
				mTopBar.setVisible(false);
				mBottomBar.setVisible(false);
				setMaskVisible(false);
				mHandler.removeCallbacksAndMessages(null);
				break;
			case MSG_REQUESTSHOW:
				mHandler.removeCallbacksAndMessages(null);
				setVisible(true,500);
				break;
			case MSG_REQUESTHIDE:
				mHandler.removeCallbacksAndMessages(null);
				setVisible(false,0);
				break;
			case JoyplusMediaPlayerActivity.MSG_UPDATEPLAYERINFO:
				mTopBar.UpdatePlayerInfo();
				break;
			case MSG_SHOWANDKEYLONGPRESS:
			case MSG_SHOWANDKEYDOWN:
				mHandler.removeCallbacksAndMessages(null);
				setMaskVisible(true);
				mTopBar.setVisible(true);
				mBottomBar.setVisible(true);
				mBottomBar.dispatchMessage(msg);
				break;
			case MSG_SHOWANDHOLD:
				setMaskVisible(true);
				mTopBar.setVisible(true);
				mBottomBar.setVisible(true);
				mHandler.removeCallbacksAndMessages(null);
				break;
			case MSG_UPDATE_SEEKBAR:
				mBottomBar.UpdateSeekBarUI();
				break;
			case MSG_UPDATE_SEEKBARMODE:
				if(mBottomBar.mSeekBarState != null){
					mBottomBar.mSeekBarState.notifySeekBarMode();
				}
				break;
			}
		}
	};

	public JoyplusMediaPlayerBar(JoyplusMediaPlayerActivity context){
		mActivity  = context;
		mBottomBar = new VideoViewController();
		mTopBar    = new VideoViewTopBar();
		mTopMask   = context.findViewById(R.id.mediacontroller_mask_top);
		mBottomMask= context.findViewById(R.id.mediacontroller_mask_bottom);
		setVisible(true,0);
	}
	private void setVisible(boolean visible,int delay){
		Message m ;
		if(visible)
			m=Message.obtain(mHandler,MSG_SHOWVIEW,"MSG_SHOWVIEW");
		else
			m=Message.obtain(mHandler,MSG_HIDEVIEW,"MSG_HIDEVIEW");
		mHandler.removeCallbacksAndMessages("MSG_SHOWVIEW");
		mHandler.removeCallbacksAndMessages("MSG_HIDEVIEW");
		mHandler.sendMessageDelayed(m,delay);
	}
	
	/**
	 * for Mask top and bottom
	 * @author Administrator
	 *
	 */
	
	

	/*add by Jas@20130812 for TopBar in JoyPlus VideoView
	 * it use to display Media name , media resolution .current time
	 * */
	private class VideoViewTopBar {

		private boolean     Debug = true;
		private String      TAG   = "VideoViewTopBar";

		private ImageView   MediaResolution;
		private TextView    MediaName;
		private TextView    Click;
		private LinearLayout Layout; 
		public void Init(){
			Layout.setVisibility(View.VISIBLE);
		};
		public void UpdatePlayerInfo() {
			// TODO Auto-generated method stub
			if(Layout.getVisibility() == View.VISIBLE){
				 InitView();
			}
		}
		public VideoViewTopBar(){
			InitResource();
		}
		private Runnable UpdateTime = new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(Layout.getVisibility() == View.VISIBLE)InitView();
			}
		};
		public void setVisible(boolean Visiblility){
			if(Debug)Log.d(TAG,"setVisibility("+Visiblility+")");
			if(!Visiblility && Layout.getVisibility()==View.VISIBLE){
				Layout.startAnimation(JoyplusMediaPlayerActivity.mAlphaDispear);
			}
			Layout.setVisibility(Visiblility?View.VISIBLE:View.GONE);
			if(Layout.getVisibility() == View.VISIBLE){
				InitView();
				mHandler.removeCallbacks(UpdateTime);
				mHandler.postDelayed(UpdateTime, 1000);
			}else{
				mHandler.removeCallbacks(UpdateTime);
			}
		}

		private void InitView() {
			// TODO Auto-generated method stub
			if(Layout.getVisibility() != View.VISIBLE)return;
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("H:mm");
			Click.setText(format.format(date));
			MediaName.setText(JoyplusMediaPlayerActivity.mInfo.mPlayerName);
			
			if("hd2".equals(JoyplusMediaPlayerActivity.mInfo.mQua)){
				MediaResolution.setImageResource(R.drawable.icon_def_hd2);
				MediaResolution.setVisibility(View.VISIBLE);
			}else if("hd".equals(JoyplusMediaPlayerActivity.mInfo.mQua)){
				MediaResolution.setImageResource(R.drawable.icon_def_hd);
				MediaResolution.setVisibility(View.VISIBLE);
			}else if("mp4".equals(JoyplusMediaPlayerActivity.mInfo.mQua)){
				MediaResolution.setImageResource(R.drawable.icon_def_mp4);
				MediaResolution.setVisibility(View.VISIBLE);
			}else if("flv".equals(JoyplusMediaPlayerActivity.mInfo.mQua)){
				MediaResolution.setImageResource(R.drawable.icon_def_flv);
				MediaResolution.setVisibility(View.VISIBLE);
			}else{
				MediaResolution.setVisibility(View.GONE);
			}
		}

		private void InitResource() {
			// TODO Auto-generated method stub
			Layout          = (LinearLayout)   mActivity.findViewById(R.id.mediacontroller_topbar);
			MediaName       = (TextView)       mActivity.findViewById(R.id.mediacontroller_topbar_playname);
			MediaResolution = (ImageView)      mActivity.findViewById(R.id.mediacontroller_topbar_resolution);
			Click           = (TextView)       mActivity.findViewById(R.id.mediacontroller_topbar_time);
			setVisible(true);
		}
	}

	/*Add by Jas@20130813 for add the BottomBar in JoyPlus VideoView 
	 * it depends on the MediaInfo which report from JoyPlusMediaPlayerStateTrack
	 * and depends on the layout of joyplusvideoview.xml 
	 * */
	private class VideoViewController {
        
		private boolean     Debug = true;
		private String      TAG   = "VideoViewController";
        private boolean        SEEKING = false;
		private TextView       CurrentTimeView;
		private TextView       TotalTimeView;
		private SeekBar        SeekBar;
		private RelativeLayout Layout_Time;
		private RelativeLayout Layout_seek;
		private RelativeLayout Layout_Speed;
		private TextView       SpeedView;
	    private int            DefaultSpeedSpace = 1000;
	    private int OFFSET = 33;
		private int seekBarWidthOffset  = 40;
		private SeekBarState   mSeekBarState;
		private long           LongPressStartTime = 0;
		private class SeekBarState {
			private SEEKMODE mSeekBarMode;
			private SEEKTYPE mSeekBarType;
			private SPEED    mSpeed;
			private boolean  StopFlog = false;
			private boolean  Notify   = false;
			public String toString(){
				StringBuffer ap = new StringBuffer();
				ap.append("mSeekBarMode="+mSeekBarMode.toString()+"\n");
			    ap.append("mSeekBarType="+mSeekBarType.toString()+"\n");
			    ap.append("mSpeed="+mSpeed.toString()+"\n");
			    ap.append("StopFlog="+StopFlog);
			    return ap.toString();				  
			}
			public SeekBarState(){
				InitSpeed();
				this.StopFlog = false;
			}
			public void InitSpeed(){
				mSeekBarMode   = SEEKMODE.NORMAL;
				mSeekBarType   = SEEKTYPE.NORMAL;
				mSpeed         = SPEED.X0;
			}
			private void notifySeekBarMode(){
				if(Notify)return;
				if(mSeekBarType != SEEKTYPE.NORMAL){
					Notify  = true;
					SEEKING = false;
					if(mSeekBarMode == SEEKMODE.LONGPRESS){
						UpdateLongPress();
					}
					UpdateSeekUI();
					Notify    = false;					
					mHandler.removeMessages(MSG_HIDEVIEW);
					mHandler.removeMessages(MSG_UPDATE_SEEKBARMODE);
					mHandler.sendEmptyMessageDelayed(MSG_UPDATE_SEEKBARMODE, 10);
				}else{
					InitSpeed();
					mHandler.removeMessages(MSG_UPDATE_SEEKBARMODE);
					Notify    = false;
				}	
				
			}
			private void UpdateLongPress(){
				if(mSeekBarMode != SEEKMODE.LONGPRESS)return;
				long DelayTime = System.currentTimeMillis()-LongPressStartTime;
				Log.d("Jas","UpdateLongPress() speed="+mSpeed.toString()+" DelayTime="+DelayTime);
				if(DelayTime>6*500){
					if(mSpeed.toInt()<SPEED.X3.toInt())mSpeed = SPEED.X3;
				}else if(DelayTime<2*500){
					if(mSpeed.toInt()<SPEED.X1.toInt())mSpeed = SPEED.X1; 
				}else{
					if(mSpeed.toInt()<SPEED.X2.toInt())mSpeed = SPEED.X2;
				}
			}
			public void UpdateSeekUI(){
				mHandler.removeMessages(MSG_UPDATE_SEEKBAR);
                mHandler.sendEmptyMessage(MSG_UPDATE_SEEKBAR);
			}
		}
		public boolean UpdateUIRunnableRunning = false;
		public void UpdateSeekBarUI(){ 
			if(UpdateUIRunnableRunning)return;			
			if(mSeekBarState.mSeekBarType == SEEKTYPE.NORMAL) return;
			UpdateUIRunnableRunning = true;
			int position =0;
			if(mSeekBarState.mSeekBarType == SEEKTYPE.FORWARD)
			    position  = (int) (SeekBar.getProgress()+getSpeedSpace()*getIntSpeed(mSeekBarState.mSpeed));
			else if(mSeekBarState.mSeekBarType == SEEKTYPE.BACKWARD)
				position  = (int) (SeekBar.getProgress()-getSpeedSpace()*getIntSpeed(mSeekBarState.mSpeed));
			if(position<0)position = 0;
			if(position>SeekBar.getMax())position = SeekBar.getMax();
			SeekBar.setProgress(position);
			UpdateProgress(null);
			UpdateUIRunnableRunning = false;
		}
		public void Init(){
			SeekBar.setEnabled(false);
			InitSeekBarState();
			setVisible(true);
			UpdateProgress(null);
			Layout_Time.setVisibility(View.VISIBLE);
		}
		private void InitSeekBarState(){
			if(mSeekBarState != null){
				mSeekBarState.InitSpeed();
				return;
			}
			mSeekBarState = new SeekBarState();
		}		
		private int getSpeedSpace(){
			return 500;
//			int Space = SeekBar.getMax();
//			if(Space == 0)
//				return 0;
//			else 
//				return Space/DefaultSpeedSpace;
		}
		public void dispatchMessage(Message m){
			switch(m.what){
			case JoyplusMediaPlayerActivity.MSG_MEDIAINFO:
				 if(Layout_Time.getVisibility() == View.VISIBLE){
					 if(mSeekBarState.mSeekBarMode != SEEKMODE.NORMAL)return;
					 if(SEEKING && mActivity.mVideoView.hasMediaInfoChange()
							 && mSeekBarState.mSeekBarMode == SEEKMODE.NORMAL){
						 SEEKING = false;
						 mHandler.sendEmptyMessage(MSG_REQUESTHIDE);
					 }
					 Log.i(TAG, "dispatchMessage--->MediaInfo:" + ((MediaInfo) m.obj).CreateMediaInfo().toString() );
					 UpdateProgress(((MediaInfo) m.obj).CreateMediaInfo());
				 }
				 break;
			case MSG_SHOWANDKEYLONGPRESS:
				JoyplusonKeyLongPress((Integer) m.obj,null);
				break;
			case MSG_SHOWANDKEYDOWN:
				JoyplusonKeyDown((Integer) m.obj,null);
				break;
			}
		}
		public boolean JoyplusonKeyLongPress(int keyCode, KeyEvent event){
			Log.d("KeyCode","Bar JoyplusonKeyLongPress keyCode="+keyCode);
			if(Layout_Time.getVisibility() == View.VISIBLE && (mActivity.getPlayer()!=null)){
				Log.d("KeyCode","mSeekBarState--->"+mSeekBarState.toString());
				SEEKING = false;
				switch(keyCode){
				case KeyEvent.KEYCODE_DPAD_LEFT:
					LongPressStartTime         = System.currentTimeMillis();
					mSeekBarState.mSeekBarMode = SEEKMODE.LONGPRESS;
					if(mSeekBarState.mSpeed == SPEED.X0)mSeekBarState.mSpeed = SPEED.X1;						
					mSeekBarState.mSeekBarType = SEEKTYPE.BACKWARD;					
					mSeekBarState.notifySeekBarMode();
					return true;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					LongPressStartTime         = System.currentTimeMillis();
					mSeekBarState.mSeekBarMode = SEEKMODE.LONGPRESS;
					if(mSeekBarState.mSpeed == SPEED.X0)mSeekBarState.mSpeed = SPEED.X1;
					mSeekBarState.mSeekBarType = SEEKTYPE.FORWARD;
					mSeekBarState.notifySeekBarMode();
					return true;
				}
			}
			return false;
		}
		public boolean JoyplusonKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			Log.d("KeyCode","Bar JoyplusonKeyDown keyCode="+keyCode);
			if(Layout_Time.getVisibility() == View.VISIBLE && (mActivity.getPlayer()!=null)){
				Log.d("KeyCode","mSeekBarState--->"+mSeekBarState.toString());
				SEEKING = false;
				switch(keyCode){
				case KeyEvent.KEYCODE_DPAD_LEFT:
					if(mSeekBarState.mSeekBarMode == SEEKMODE.LONGPRESS){
						JoyplusonKeyDown(KeyEvent.KEYCODE_DPAD_CENTER,null);
						return true;
					}else{
						mSeekBarState.mSeekBarMode = SEEKMODE.PRESS;
						if(mSeekBarState.mSeekBarType == SEEKTYPE.FORWARD){
							if(mSeekBarState.mSpeed == SPEED.X1)mSeekBarState.mSeekBarType = SEEKTYPE.BACKWARD;
							mSeekBarState.mSpeed = SPEED.X1;
						}else if(mSeekBarState.mSpeed == SPEED.X3)return true;
						else {mSeekBarState.mSpeed = getNextSpeed(mSeekBarState.mSpeed);
						    mSeekBarState.mSeekBarType = SEEKTYPE.BACKWARD;}
						mSeekBarState.notifySeekBarMode();
					}
					return true;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					if(mSeekBarState.mSeekBarMode == SEEKMODE.LONGPRESS){
						JoyplusonKeyDown(KeyEvent.KEYCODE_DPAD_CENTER,null);
						return true;
					}else{
						mSeekBarState.mSeekBarMode = SEEKMODE.PRESS;
						if(mSeekBarState.mSeekBarType == SEEKTYPE.BACKWARD){
							if(mSeekBarState.mSpeed == SPEED.X1)mSeekBarState.mSeekBarType = SEEKTYPE.FORWARD;
							mSeekBarState.mSpeed = SPEED.X1;
						}else if(mSeekBarState.mSpeed == SPEED.X3)return true;
						else {mSeekBarState.mSpeed = getNextSpeed(mSeekBarState.mSpeed);
						mSeekBarState.mSeekBarType = SEEKTYPE.FORWARD;}
						mSeekBarState.notifySeekBarMode();
					}
					return true;
				case KeyEvent.KEYCODE_DPAD_CENTER:
				case KeyEvent.KEYCODE_ENTER:
					if(mSeekBarState.mSeekBarType != SEEKTYPE.NORMAL){
						int progress = SeekBar.getProgress();						
						if(!(progress == SeekBar.getMax())){							
							mHandler.removeCallbacksAndMessages(null);		
							mActivity.getPlayer().SeekVideo(progress);
							SEEKING = true;
							InitSeekBarState();
						}else {
							InitSeekBarState();
							UpdateProgress(JoyplusMediaPlayerVideoView.CurrentMediaInfo);
							mHandler.removeCallbacksAndMessages(null);
							mHandler.sendEmptyMessage(MSG_REQUESTSHOW);
						}
						return true;
					}
					return false;
				case KeyEvent.KEYCODE_BACK:
				case 111://the keycode was be change to 111 ,but don't know where change
					if(mSeekBarState.mSeekBarType != SEEKTYPE.NORMAL){
						InitSeekBarState();
						mSeekBarState.notifySeekBarMode();
						UpdateProgress(JoyplusMediaPlayerVideoView.CurrentMediaInfo);
						mHandler.removeCallbacksAndMessages(null);
						mHandler.sendEmptyMessage(MSG_REQUESTSHOW);
						return true;
					}
				case KeyEvent.KEYCODE_MENU:
					if(mSeekBarState.mSpeed!=SPEED.X0){
						return true;
					}
				}
			} 
			return false;
		} 
		
		public VideoViewController(){
			InitResource();
		}
		public void setVisible(boolean Visiblility){
			if(Debug)Log.d(TAG,"setVisibility("+Visiblility+")");
			if(!Visiblility && Layout_Time.getVisibility()==View.VISIBLE)
				Layout_Time.startAnimation(JoyplusMediaPlayerActivity.mAlphaDispear);
			if(!Visiblility && Layout_seek.getVisibility()==View.VISIBLE)
				Layout_seek.startAnimation(JoyplusMediaPlayerActivity.mAlphaDispear);
			Layout_Time.setVisibility(Visiblility?View.VISIBLE:View.GONE);
			Layout_seek.setVisibility(Visiblility?View.VISIBLE:View.GONE);
			if(Visiblility && mSeekBarState.mSeekBarType == SEEKTYPE.NORMAL)
				UpdateProgress(JoyplusMediaPlayerVideoView.CurrentMediaInfo);	
			if(!Visiblility && mSeekBarState!=null)InitSeekBarState();
		}
		private void InitResource(){
			if(Debug)Log.d(TAG,"VideoViewController InitResource()");
			Layout_Time       = (RelativeLayout)mActivity.findViewById(R.id.mediacontroller_bottombar);
			CurrentTimeView   = (TextView)      mActivity.findViewById(R.id.mediacontroller_bottombar_current_time);
			TotalTimeView     = (TextView)      mActivity.findViewById(R.id.mediacontroller_bottombar_total_time);
			SeekBar           = (SeekBar)       mActivity.findViewById(R.id.mediacontroller_bottombar_seekbar);
			Layout_seek       = (RelativeLayout)mActivity.findViewById(R.id.mediacontroller_bottombar_seek);
			Layout_Speed      = (RelativeLayout)mActivity.findViewById(R.id.mediacontroller_bottombar_time_fast);
			SpeedView         = (TextView)      mActivity.findViewById(R.id.mediacontroller_bottombar_time_fasttext);
			SeekBar.setEnabled(false);			
		}
		public void UpdateProgress(MediaInfo info){
			if(Layout_seek.getVisibility() != View.VISIBLE)return;
			if(mSeekBarState.mSeekBarType == SEEKTYPE.BACKWARD || mSeekBarState.mSeekBarType == SEEKTYPE.FORWARD){
				Layout_Speed.setVisibility(View.VISIBLE);
				if(mSeekBarState.mSeekBarType == SEEKTYPE.FORWARD)
					Layout_Speed.setBackgroundResource(R.drawable.play_time_right);
				else if(mSeekBarState.mSeekBarType == SEEKTYPE.BACKWARD)
					Layout_Speed.setBackgroundResource(R.drawable.play_time_left);
				CurrentTimeView.setText(getTimeString(SeekBar.getProgress()));
				SpeedView.setText(mSeekBarState.mSpeed.toString());
				updateSeekBar(null);
				return;
			}
			if(mSeekBarState.mSeekBarType != SEEKTYPE.NORMAL)return;
			Layout_Speed.setVisibility(View.GONE);
			if(SEEKING)return;
			if(info != null){
			    if(info.getState() == STATE.MEDIA_STATE_PLAYING
						||info.getState() == STATE.MEDIA_STATE_INITED){
					CurrentTimeView.setText(getTimeString((int)info.getCurrentTime()));
					if(info.getTotleTime()<=0){
						TotalTimeView.setText("--:--");
					}else{
						TotalTimeView.setText(getTimeString((int)info.getTotleTime()));
					}					
					updateSeekBar(info);
				}
			}else{
				CurrentTimeView.setText(getTimeString(0));
				TotalTimeView.setText("--:--");
				SeekBar.setMax(100);
				SeekBar.setProgress(0);
				updateSeekBar(null);
			}
		}
		private void updateSeekBar(MediaInfo info){		
			if (info != null && (info.getState().toInt()>=STATE.MEDIA_STATE_INITED.toInt()) && mSeekBarState.mSeekBarType == SEEKTYPE.NORMAL){
				SeekBar.setMax((int) info.getTotleTime());
				SeekBar.setProgress((int) info.getCurrentTime());
			}else if(info == null && mSeekBarState.mSeekBarType == SEEKTYPE.NORMAL){
				SeekBar.setMax(100);
				SeekBar.setProgress(0);
			}
			UpdateSeekTime();
		}
		private void UpdateSeekTime(){
			RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			parms.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);	
			if (SeekBar.getProgress()>0){
				double mLeft = (double) SeekBar.getProgress() / SeekBar.getMax()* 
						(SeekBar.getMeasuredWidth() - Utils.getStandardValue(mActivity, seekBarWidthOffset)) + Utils.getStandardValue(mActivity, OFFSET);
				parms.leftMargin = (int) mLeft;
			}else{
				parms.leftMargin = Utils.getStandardValue(mActivity, OFFSET);
			}
			parms.bottomMargin = Utils.getStandardValue(mActivity,(20 + 10));
			Layout_Time.setLayoutParams(parms);
		}
		private String getTimeString(int time){
			if(time<0)time = 0;
			StringBuffer sb = new StringBuffer();
			time/=1000;
	        sb.append(getString(time/(60*60)));
	        sb.append(":");
	        time%=(60*60);
	        sb.append(getString(time/60));
	        sb.append(":");
	        time%=60;
	        sb.append(getString(time));
	        return sb.toString();
		}
		private String getString(int time){
			StringBuffer sb = new StringBuffer();
			sb.append(time/10).append(time%10);
			return sb.toString();
		}
	}

	@Override
	public boolean JoyplusdispatchMessage(Message msg) {
		// TODO Auto-generated method stub
		mHandler.dispatchMessage(msg);
		return false;
	}
	@Override
	public boolean JoyplusonKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(mBottomBar.Layout_Time.getVisibility()== View.VISIBLE){
			if(mBottomBar.JoyplusonKeyDown(keyCode, event)){
				return true;
			}
		}
		return false;
	}

	@Override
	public void JoyplussetVisible(boolean visible,int layout) {
		// TODO Auto-generated method stub
		if(Debug)Log.d(TAG,"setVisible("+visible+")");
		setVisible(visible,0);
	}
	@Override
	public int JoyplusgetLayout() {
		// TODO Auto-generated method stub
		return LAYOUT_BAR;
	}
	@Override
	public boolean JoyplusonKeyLongPress(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(mBottomBar.Layout_Time.getVisibility()== View.VISIBLE){
			if(mBottomBar.JoyplusonKeyLongPress(keyCode, event)){
				return true;
			}
		}
		return false;
	}
}
