package com.joyplus.mediaplayer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;

import com.joyplus.mediaplayer.VideoViewInterface.STATE;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.utils.Log;

public class JoyplusVideoView extends LinearLayout{

	private boolean Debug = false;
	private String  TAG   = "JoyplusVideoView";
	
	private Context mContext;
	
	private VideoViewInterface   mView;
	private JoyplusPlayerMonitor mMonitor;
	private MediaInfo            mTragetMediaInfo;
	private MediaInfo            mPreMediaInfo;
	
	public JoyplusVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		hideView();
	}
    
	public JoyplusVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		hideView();
	}

	public VideoViewInterface getVideoView(){
		if(Debug)Log.d(TAG,"getVideoView() mView is null ="+(mView == null));
		if(mView != null)
			return mView;
		else 
			return null;
	}
	/*Interface of MediaPlayer handler*/
	private Handler mServerHandler;
	private Handler MediaPlayerhandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case JoyplusPlayerMonitor.MSG_STATEUPDATE:
				mPreMediaInfo = (MediaInfo)msg.obj;
				if(Debug)Log.d(TAG,"MediaPlayer Info="+mPreMediaInfo.toString());
				if(mPreMediaInfo.getState()==STATE.MEDIA_STATE_UNKNOW){
					if(JoyplusMediaPlayerManager.getInstance().getSwitchEn()){
						mTragetMediaInfo = mPreMediaInfo;
					}
				}
				break;
			}
			if(mServerHandler != null){
				Message m = Message.obtain(mServerHandler, msg.what,null);
				if(JoyplusPlayerMonitor.MSG_STATEUPDATE == msg.what)m.obj=mPreMediaInfo;
				mServerHandler.sendMessage(m);
			}
		}
    };
	
	
	public boolean Update(){
		return Update(JoyplusMediaPlayerManager.getInstance().getCurrentType().PlayerType);
	}
	public boolean Update(int MediaPlayerType){
		if(Debug)Log.d(TAG,"Update("+JoyplusMediaPlayerManager.getPlayerTypeName(MediaPlayerType)+")");
		hideView();
		mView = null;
		if(!JoyplusMediaPlayerManager.isTypeAvailable(MediaPlayerType)){
			Log.i(TAG,"Update() unavailable type "+MediaPlayerType);
			return false;
		}
		JoyplusPlayerConfig config= JoyplusMediaPlayerManager.getInstance().getJoyplusPlayerConfig(MediaPlayerType);
	    if(config == null || !config.EN){
	    	Log.i(TAG,"JoyplusPlayerConfig("+MediaPlayerType+") unusable");
	    	return false;	
	    }
	    if(config.TYPE == JoyplusMediaPlayerManager.TYPE_VITAMIO && !JoyplusMediaPlayerManager.getInstance().getVitamioEn()){
	    	Log.i(TAG,"Vitamio unsupport now !!");
	    	return false;
	    }
		mView = CreateView(MediaPlayerType);
		return true;
	}
    public void hideView(){
    	if(Debug)Log.d(TAG,"hideView()");
    	if(mView!=null){
    		((ViewInterface)mView).setVisibility(false);
    		((View)mView).setVisibility(View.GONE);
    	}
    	if(mMonitor != null){
			mMonitor.stopMonitor();
			mMonitor=null;
		}
    	mServerHandler = null;
    }
    private View getStubView(int stubId,int viewId){
    	if(Debug)Log.d(TAG,"getStubView("+stubId+" , "+viewId+")");
    	View view = findViewById(viewId);
    	if(view == null){
    		ViewStub stub = (ViewStub)findViewById(stubId);
    		view = stub.inflate();
    	}
    	return view;
    }
    private VideoViewInterface CreateView(int type){
    	if(Debug)Log.d(TAG,"CreateView()"+JoyplusMediaPlayerManager.getPlayerTypeName(type));
    	if(type == JoyplusMediaPlayerManager.TYPE_MEDIAPLAYER){
    		return CreateVideoView(
    				R.id.system_mediaplayer_videoview_stub,
    				R.id.system_mediaplayer_videoview);
    	}else if(type == JoyplusMediaPlayerManager.TYPE_VITAMIO){
    		return CreateVideoView(
    				R.id.system_vitamioplayer_videoview_stub,
    				R.id.system_vitamioplayer_videoview);
    	}else{
    		throw new IllegalArgumentException();
    	}
    }
    private VideoViewInterface CreateVideoView(int stubId,int viewId){
    	if(Debug)Log.d(TAG,"CreateVideoView("+stubId+" , "+viewId+")");
    	LinearLayout view = (LinearLayout) getStubView(stubId,viewId);
    	view.setVisibility(View.VISIBLE);
    	if(view != null){
    		//view.setLayoutParams(JoyplusMediaPlayerScreenManager.getInstance().getParams());
    		mMonitor = new JoyplusPlayerMonitor(mContext,(VideoViewInterface)view);
    		mMonitor.startMonitor(MediaPlayerhandler);
    		mServerHandler = JoyplusMediaPlayerManager.getInstance().getmediaPlayerHandler();
    	}
    	return (VideoViewInterface)view;
    }
    public boolean setLayoutParams(LinearLayout.LayoutParams params){
    	if(mView!=null && params!=null){
    		((LinearLayout)mView).setLayoutParams(params);
    		return true;
    	}
    	return false;
    }
}
