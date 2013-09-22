package com.joyplus;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.joyplus.mediaplayer.MediaInfo;
import com.joyplus.mediaplayer.VideoViewInterface.STATE;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.utils.Log;



public class JoyplusMediaPlayerMiddleControlMini extends LinearLayout implements JoyplusMediaPlayerInterface{

	//private Context        mContext;
	private static Handler mHandler;

	private boolean StateOk = false;
	//switch layout
	private LinearLayout mSwitch;
	private ImageButton  mSwitch_center;
	private ImageButton  mSwitch_left;
	private ImageButton  mSwitch_top;
	private ImageButton  mSwitch_right;
	private ImageButton  mSwitch_bottom;
	//pause or play layout
	private LinearLayout mPauseplay;
	private ImageButton  mPauseplay_button;

	/*Event of this layout*/
	public final static int MSG_KEYDOWN_CENTER    = 1;
	public final static int MSG_KEYDOWN_LEFT      = 2;
	public final static int MSG_KEYDOWN_TOP       = 3;
	public final static int MSG_KEYDOWN_RIGHT     = 4;
	public final static int MSG_KEYDOWN_BOTTOM    = 5;
	public final static int MSG_KEYDOWN_PAUSEPLAY = 6;
	public final static int MSG_REQUESTHIDEVIEW   = 7;
	public final static int MSG_PAUSEPLAY         = 8;

	public final static int LAYOUT_PAUSEPLAY    = 1;
	public final static int LAYOUT_SWITCH       = 2;
	public final static int LAYOUT_UNKNOW       = 3;
	private      static int mLayout             = LAYOUT_PAUSEPLAY;
	public static void setLayout(int layout){		
		if(layout == LAYOUT_SWITCH || layout == LAYOUT_PAUSEPLAY){
			mLayout = layout;
		}else{
			mLayout = LAYOUT_UNKNOW;
		}
	}
	public static void setHandler(Handler handler){
		mHandler = handler;
	}
	private class MessageOnClick implements OnClickListener,OnFocusChangeListener {
        private int mWhat;
        public MessageOnClick(int what) {
            mWhat = what;
        }
        public void onClick(View v) {
        	if(mSwitch.getVisibility() == View.VISIBLE){
				UpdateUI(LAYOUT_SWITCH,v.getId());
			}else if(mPauseplay.getVisibility() == View.VISIBLE){
				UpdateUI(LAYOUT_PAUSEPLAY,v.getId());
			}
            Message msg = Message.obtain(mHandler, mWhat);
            msg.sendToTarget();
        }
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if(mSwitch.getVisibility() == View.VISIBLE){
				UpdateUI(LAYOUT_SWITCH,v.getId());
			}else if(mPauseplay.getVisibility() == View.VISIBLE){
				UpdateUI(LAYOUT_PAUSEPLAY,v.getId());
			}
			Message msg = Message.obtain(mHandler, mWhat);
            msg.sendToTarget();
		}
    }
	public JoyplusMediaPlayerMiddleControlMini(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		//mContext = context;
	}
	public JoyplusMediaPlayerMiddleControlMini(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//mContext = context; 

	}
	public void UpdateShowLayout(){
		if(mLayout       == LAYOUT_SWITCH){
			ShowSwitch();
		}else if(mLayout == LAYOUT_PAUSEPLAY){
			ShowPlaypause();
		}else{
			Message.obtain(mHandler, MSG_REQUESTHIDEVIEW).sendToTarget();
		}
		UpdateUI(mLayout);
		mLayout             = LAYOUT_UNKNOW;
		Message.obtain(mHandler, MSG_PAUSEPLAY).sendToTarget();
	}
	protected void onFinishInflate() {
		super.onFinishInflate();
		mSwitch           = (LinearLayout) findViewById(R.id.joyplusvideoview_mini_switch);
		mPauseplay        = (LinearLayout) findViewById(R.id.joyplusvideoview_mini_pauseplay);
		//UpdateShowLayout();	    
	}
	private void ShowSwitch(){
		if(mPauseplay.getVisibility()!=View.VISIBLE &&mSwitch.getVisibility() == View.VISIBLE)return;
		mPauseplay.setVisibility(View.GONE);
		mSwitch.setVisibility(View.VISIBLE);
		mSwitch_center = (ImageButton) findViewById(R.id.joyplusvideoview_mini_switch_center);
		mSwitch_left   = (ImageButton) findViewById(R.id.joyplusvideoview_mini_switch_left);
		mSwitch_top    = (ImageButton) findViewById(R.id.joyplusvideoview_mini_switch_top);
		mSwitch_right  = (ImageButton) findViewById(R.id.joyplusvideoview_mini_switch_right);
		mSwitch_bottom = (ImageButton) findViewById(R.id.joyplusvideoview_mini_switch_bottom);
		mSwitch_center.setOnClickListener(new MessageOnClick(MSG_KEYDOWN_CENTER));
		mSwitch_center.setOnFocusChangeListener(new MessageOnClick(MSG_KEYDOWN_CENTER));
		mSwitch_left.setOnClickListener(new MessageOnClick(MSG_KEYDOWN_LEFT));
		mSwitch_left.setOnFocusChangeListener(new MessageOnClick(MSG_KEYDOWN_LEFT));
		mSwitch_top.setOnClickListener(new MessageOnClick(MSG_KEYDOWN_TOP));
		mSwitch_top.setOnFocusChangeListener(new MessageOnClick(MSG_KEYDOWN_TOP));
		mSwitch_right.setOnClickListener(new MessageOnClick(MSG_KEYDOWN_RIGHT));
		mSwitch_right.setOnFocusChangeListener(new MessageOnClick(MSG_KEYDOWN_RIGHT));
		mSwitch_bottom.setOnClickListener(new MessageOnClick(MSG_KEYDOWN_BOTTOM));
		mSwitch_bottom.setOnFocusChangeListener(new MessageOnClick(MSG_KEYDOWN_BOTTOM));
		UpdateButtonState();
		Message.obtain(mHandler, MSG_PAUSEPLAY).sendToTarget();
	}
	private void ShowPlaypause(){
		if(mSwitch.getVisibility()!=View.VISIBLE &&mPauseplay.getVisibility() == View.VISIBLE)return;
		mPauseplay.setVisibility(View.VISIBLE);
		mSwitch.setVisibility(View.GONE);
		mPauseplay_button = (ImageButton)  findViewById(R.id.joyplusvideoview_mini_pauseplay_button);
		mPauseplay_button.setOnClickListener(new MessageOnClick(MSG_KEYDOWN_PAUSEPLAY));
		Message.obtain(mHandler, MSG_PAUSEPLAY).sendToTarget();
	}
	private void UpdateButtonState(){
		if(!JoyplusMediaPlayerActivity.mInfo.getHaveNext()){
			mSwitch_right.setFocusable(false);
			mSwitch_right.setEnabled(false);
		}else{
			mSwitch_right.setFocusable(true);
			mSwitch_right.setEnabled(true);
		}
		if(!JoyplusMediaPlayerActivity.mInfo.getHavePre()){
			mSwitch_left.setFocusable(false);
			mSwitch_left.setEnabled(false);
		}else{
			mSwitch_left.setFocusable(true);
			mSwitch_left.setEnabled(true);
		}
		if(JoyplusMediaPlayerActivity.mInfo.mCollection == 0){
			mSwitch_bottom.setBackgroundResource(R.drawable.player_btn_unfav);
		}else{
			mSwitch_bottom.setBackgroundResource(R.drawable.player_btn_fav);
		}
	}
	@Override
	public boolean JoyplusdispatchMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case JoyplusMediaPlayerActivity.MSG_MEDIAINFO:
			if(mPauseplay.getVisibility()==View.VISIBLE
			   || mSwitch.getVisibility()==View.VISIBLE){
			   MediaInfo info = ((MediaInfo) msg.obj).CreateMediaInfo();
			   if(info.getState()!=STATE.MEDIA_STATE_PUSE){
				   mHandler.sendEmptyMessageDelayed(MSG_REQUESTHIDEVIEW,100);
			   }
			}
		}
		return false;
	}
	@Override
	public boolean JoyplusonKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.d("KeyCode","ControlMini JoyplusonKeyDown() keyCode="+keyCode);
		switch(keyCode){
		case KeyEvent.KEYCODE_DPAD_CENTER:
			if(mSwitch.getVisibility() == View.VISIBLE){
				UpdateUI(LAYOUT_SWITCH,mSwitch_center.getId());
				Message.obtain(mHandler, MSG_KEYDOWN_CENTER).sendToTarget();
			}else if(mPauseplay.getVisibility() == View.VISIBLE){
				UpdateUI(LAYOUT_PAUSEPLAY,mPauseplay_button.getId());
				Message.obtain(mHandler, MSG_KEYDOWN_PAUSEPLAY).sendToTarget();
			}
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if(mSwitch.getVisibility() == View.VISIBLE){
				UpdateUI(LAYOUT_SWITCH,mSwitch_bottom.getId());
				Message.obtain(mHandler, MSG_KEYDOWN_BOTTOM).sendToTarget();
			}
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if(mSwitch.getVisibility() == View.VISIBLE && mSwitch_left.isEnabled()){
				UpdateUI(LAYOUT_SWITCH,mSwitch_left.getId());
				Message.obtain(mHandler, MSG_KEYDOWN_LEFT).sendToTarget();
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if(mSwitch.getVisibility() == View.VISIBLE && mSwitch_right.isEnabled()){
				UpdateUI(LAYOUT_SWITCH,mSwitch_right.getId());
				Message.obtain(mHandler, MSG_KEYDOWN_RIGHT).sendToTarget();
			}
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			if(mSwitch.getVisibility() == View.VISIBLE){
				UpdateUI(LAYOUT_SWITCH,mSwitch_top.getId());
				Message.obtain(mHandler, MSG_KEYDOWN_TOP).sendToTarget();
			}
			break;
		case KeyEvent.KEYCODE_BACK:
			break;
		}
		return true;
	}

	/*follow was use to change UI ,it only use to adapter TV*/
	private void UpdateUI(int layout){
		switch(layout){
		case LAYOUT_SWITCH:
			if(mSwitch.getVisibility() == View.VISIBLE)
				UpdateUI(layout,mSwitch_center.getId());
			break;
		case LAYOUT_PAUSEPLAY:
			if(mPauseplay.getVisibility() == View.VISIBLE)
				UpdateUI(layout,mPauseplay_button.getId());
			break;
		}
	}
	private void UpdateUI(int layout,int id){
		UpdateNormal(layout);
		UpdateActive(layout,id);
	}
	private static final int TYPE_ACTIVE   = 1;
	private static final int TYPE_UNUSE    = 2;
	private static final int TYPE_NORMAL   = 3;
	private void UpdateActive(int layout,int Id){
		if(layout == LAYOUT_SWITCH && mSwitch.getVisibility() == View.VISIBLE ){
			if(Id == mSwitch_center.getId()){
				UpdatemSwitch_center(TYPE_ACTIVE);
			}else if(Id == mSwitch_left.getId()){
				UpdatemSwitch_left(TYPE_ACTIVE);
			}else if(Id == mSwitch_top.getId()){
				UpdatemSwitch_top(TYPE_ACTIVE);
			}else if(Id == mSwitch_right.getId()){
				UpdatemSwitch_right(TYPE_ACTIVE);
			}else if(Id == mSwitch_bottom.getId()){
				UpdatemSwitch_bottom(TYPE_ACTIVE);
			}
		}else if(layout == LAYOUT_PAUSEPLAY && mPauseplay.getVisibility() == View.VISIBLE){ 
			if(Id == mPauseplay_button.getId()){
			    UpdatemPauseplay_button(TYPE_ACTIVE);
		    }
		}
	}
	private void UpdateNormal(int layout){
		switch(layout){
		case LAYOUT_SWITCH:
			UpdatemSwitch_center(TYPE_NORMAL);
			UpdatemSwitch_top(TYPE_NORMAL);
			UpdatemSwitch_bottom(TYPE_NORMAL);
			UpdatemSwitch_left(TYPE_NORMAL);
			UpdatemSwitch_right(TYPE_NORMAL);
			break;
		case LAYOUT_PAUSEPLAY:
			UpdatemPauseplay_button(TYPE_NORMAL);
			break;
		}
	}
	private void UpdatemSwitch_center(int type){
		switch(type){
		case TYPE_ACTIVE:
			mSwitch_center.setBackgroundResource(R.drawable.player_btn_finish_active);
			break;
		case TYPE_UNUSE:
			mSwitch_center.setBackgroundResource(R.drawable.player_btn_finish_unuse);
			break;
		case TYPE_NORMAL:
			default:
				mSwitch_center.setBackgroundResource(R.drawable.player_btn_finish_normal);	
			break;	
		}
	}
	private void UpdatemSwitch_top(int type){
		switch(type){
		case TYPE_ACTIVE:
			mSwitch_top.setBackgroundResource(R.drawable.player_btn_continue_active);
			break;
		case TYPE_UNUSE:
			mSwitch_top.setBackgroundResource(R.drawable.player_btn_continue_unuse);
			break;
		case TYPE_NORMAL:
			default:
				mSwitch_top.setBackgroundResource(R.drawable.player_btn_continue_normal);	
			break;	
		}
	}
	private void UpdatemSwitch_left(int type){
		if(!mSwitch_left.isEnabled())type = TYPE_UNUSE;
		switch(type){
		case TYPE_ACTIVE:
			mSwitch_left.setBackgroundResource(R.drawable.player_btn_pre_active);
			break;
		case TYPE_UNUSE:
			mSwitch_left.setBackgroundResource(R.drawable.player_btn_pre_unuse);
			break;
		case TYPE_NORMAL:
			default:
				mSwitch_left.setBackgroundResource(R.drawable.player_btn_pre_normal);	
			break;	
		}
	}
	private void UpdatemSwitch_right(int type){
		if(!mSwitch_right.isEnabled())type = TYPE_UNUSE;
		switch(type){
		case TYPE_ACTIVE:
			mSwitch_right.setBackgroundResource(R.drawable.player_btn_next_active);
			break;
		case TYPE_UNUSE:
			mSwitch_right.setBackgroundResource(R.drawable.player_btn_next_unuse);
			break;
		case TYPE_NORMAL:
			default:
				mSwitch_right.setBackgroundResource(R.drawable.player_btn_next_normal);	
			break;	
		}
	}
	private void UpdatemSwitch_bottom(int type){
		switch(type){
		case TYPE_ACTIVE:
			if(JoyplusMediaPlayerActivity.mInfo.mCollection == 0){//un collection
				mSwitch_bottom.setBackgroundResource(R.drawable.player_btn_unfav_active);
			}else{
				mSwitch_bottom.setBackgroundResource(R.drawable.player_btn_fav_active);
			}
			break;
		case TYPE_UNUSE:
			if(JoyplusMediaPlayerActivity.mInfo.mCollection == 0){//un collection
				mSwitch_bottom.setBackgroundResource(R.drawable.player_btn_unfav_unuse);
			}else{
				mSwitch_bottom.setBackgroundResource(R.drawable.player_btn_fav_unuse);
			}
			break;
		case TYPE_NORMAL:
			default:
				if(JoyplusMediaPlayerActivity.mInfo.mCollection == 0){//un collection
					//mSwitch_bottom.setBackgroundResource(R.drawable.player_btn_unfav_normal);
					mSwitch_bottom.setBackgroundResource(R.drawable.player_btn_fav_normal);
				}else{
					//mSwitch_bottom.setBackgroundResource(R.drawable.player_btn_fav_normal);
					mSwitch_bottom.setBackgroundResource(R.drawable.player_btn_unfav_normal);
				}	
			break;	
		}
	}
	private void UpdatemPauseplay_button(int type){
		switch(type){
		case TYPE_ACTIVE:
			mPauseplay_button.setBackgroundResource(R.drawable.player_btn_play_play_active);
			break;
		case TYPE_NORMAL:
			default:
				mPauseplay_button.setBackgroundResource(R.drawable.player_btn_play_play_normal);	
			break;	
		}
	}
	@Override
	public void JoyplussetVisible(boolean visible, int layout) {
		// TODO Auto-generated method stub
		if(visible == false)StateOk = false;
		else{
			UpdateShowLayout();
			StateOk = true;
		}
	}
	@Override
	public int JoyplusgetLayout() {
		// TODO Auto-generated method stub
		return JoyplusMediaPlayerMiddleControl.LAYOUT_MINI;
	}
	@Override
	public boolean JoyplusonKeyLongPress(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}