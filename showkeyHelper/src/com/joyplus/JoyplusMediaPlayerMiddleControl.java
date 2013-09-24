package com.joyplus;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;

import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.utils.Log;

public class JoyplusMediaPlayerMiddleControl extends LinearLayout implements JoyplusMediaPlayerInterface{
     

	private boolean Debug = true;
	private String  TAG   = "JoyplusMediaPlayerMiddleControl";

	private JoyplusMediaPlayerInterface   mView;//Current display layout;
	//private Context  mContext;
    
	private final static int MSG_BASE  = 200;
	public  final static int MSG_HIDEVIEW               = MSG_BASE+1;
	public  final static int MSG_SHOWVIEW               = MSG_BASE+2;
	public  final static int MSG_REQUESTSHOWVIEW        = MSG_BASE+3;
	public  final static int MSG_REQUESTHIDEVIEW        = MSG_BASE+4;
	public  final static int MSG_REQUESTHOLDSHOWVIEW    = MSG_BASE+5;
	public  final static int LAYOUT_CONTROL_MIDDLE      = MSG_BASE+6;
	//layout of loading
	public  final static int LAYOUT_LOADING             = MSG_BASE+30;
	public  final static int LAYOUT_LOADING_UPDATEINFO  = MSG_BASE+31;
	//layout of audio 
	public  final static int LAYOUT_AUDIO               = MSG_BASE+50;
	//layout of minicontrol
	public  final static int LAYOUT_MINI                = MSG_BASE+70;
	public void Init(){
		update(LAYOUT_LOADING);
		mHandler.removeCallbacksAndMessages(null);
	}
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case MSG_HIDEVIEW:
				hideView();
				break;
			case MSG_SHOWVIEW:
				update((Integer) msg.obj);				
				if(mView != null && mView.JoyplusgetLayout()==LAYOUT_AUDIO){
					setVisible(false,JoyplusMediaPlayerMiddleControlAudio.SHOWTIME,0);
				}
				break;
            case MSG_REQUESTSHOWVIEW:
            	if(mView != null && ((View)mView).getVisibility()!=View.VISIBLE){
            		setVisible(true,0,mView.JoyplusgetLayout());
            	}
            	mHandler.removeCallbacksAndMessages(null);
            	if(mView != null && mView.JoyplusgetLayout()==LAYOUT_AUDIO){
					setVisible(false,JoyplusMediaPlayerMiddleControlAudio.SHOWTIME,0);
				}
				break;
            case MSG_REQUESTHOLDSHOWVIEW:
            	if(mView != null){
            		if(((View)mView).getVisibility() != View.VISIBLE)
            		setVisible(true,0,mView.JoyplusgetLayout());
            		mHandler.removeCallbacksAndMessages(null);
            	}
            	break;
            case MSG_REQUESTHIDEVIEW:
            	setVisible(false,0,mView.JoyplusgetLayout());
            	break;
			}
		}
	};
	private void setVisible(boolean visible,int delay,int layout){
		Message m ;
		mHandler.removeCallbacksAndMessages(null);
		if(visible)
			m=Message.obtain(mHandler,MSG_SHOWVIEW,"MSG_SHOWVIDE");
		else
			m=Message.obtain(mHandler,MSG_HIDEVIEW,"MSG_HIDEVIDE");
		m.obj = layout;
		mHandler.sendMessageDelayed(m,delay);
	}
	public JoyplusMediaPlayerMiddleControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//mContext = context;
		hideView();
	}
	public JoyplusMediaPlayerMiddleControl(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		//mContext = context;
		hideView();
	}
	public void hideView(){
		if(mView!=null){
			mView.JoyplussetVisible(false, 0);
    		((View)mView).setVisibility(View.GONE);
    		((View)mView).startAnimation(JoyplusMediaPlayerActivity.mAlphaDispear);
    	}
		mView = null;
	}
	public boolean update(int layout){
		hideView();
		mView = CreateView(layout);
		mView.JoyplussetVisible(true, mView.JoyplusgetLayout());
		return true;
	}
	private View getStubView(int stubId,int viewId){
    	if(Debug)Log.d(TAG,"getStubView("+stubId+" , "+viewId+")");
    	View view = findViewById(viewId);
    	if(view == null){
    		ViewStub stub = (ViewStub)findViewById(stubId);
    		view          = stub.inflate();
    	}
    	return view;
    }
    private JoyplusMediaPlayerInterface CreateView(int layout){
    	if(Debug)Log.d(TAG,"CreateView("+layout+")");
    	if(layout == LAYOUT_LOADING){
    		return CreateLayoutView(
    				R.id.joyplusvideoview_loading_stub,
    				R.id.joyplusvideoview_loading);
    	}else if(layout == LAYOUT_AUDIO){
    		return CreateLayoutView(
    				R.id.joyplusvideoview_audio_stub,
    				R.id.joyplusvideoview_audio);
    	}else if(layout == LAYOUT_MINI){
    		return CreateLayoutView(
    				R.id.joyplusvideoview_mini_stub,
    				R.id.joyplusvideoview_mini);
    	}else{
    		throw new IllegalArgumentException();
    	}
    }
    private JoyplusMediaPlayerInterface CreateLayoutView(int stubId,int viewId){
    	if(Debug)Log.d(TAG,"CreateLayoutView("+stubId+" , "+viewId+")");
    	LinearLayout view = (LinearLayout) getStubView(stubId,viewId);
    	view.setVisibility(View.VISIBLE);
    	return (JoyplusMediaPlayerInterface)view;
    }
	@Override
	public boolean JoyplusdispatchMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean JoyplusonKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(mView!=null && (((View)mView).getVisibility() == View.VISIBLE)){
			 if(Debug)Log.d("KeyCode","Control JoyplusonKeyDown() keyCode="+keyCode);
			 if( mView.JoyplusonKeyDown(keyCode, event)){
				 mHandler.sendEmptyMessage(MSG_REQUESTSHOWVIEW);
				 return true;
			 }
		}
		return false;
	}
	@Override
	public void JoyplussetVisible(boolean visible,int layout) {
		// TODO Auto-generated method stub
		setVisible(visible,0,layout);
	}
	@Override
	public int JoyplusgetLayout() {
		// TODO Auto-generated method stub
		return LAYOUT_CONTROL_MIDDLE;
	}
	@Override
	public boolean JoyplusonKeyLongPress(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}