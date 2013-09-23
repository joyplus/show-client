package com.joyplus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore.Video;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.Sub.JoyplusSubManager;
import com.joyplus.Sub.SUBTYPE;
import com.joyplus.Sub.SubURI;
import com.joyplus.manager.URLManager;
import com.joyplus.manager.URLManager.Quality;
import com.joyplus.mediaplayer.JoyplusMediaPlayerListener;
import com.joyplus.mediaplayer.JoyplusMediaPlayerManager;
import com.joyplus.mediaplayer.JoyplusMediaPlayerScreenManager;
import com.joyplus.mediaplayer.MediaInfo;
import com.joyplus.mediaplayer.VideoViewInterface;
import com.joyplus.mediaplayer.VideoViewInterface.STATE;
import com.joyplus.tvhelper.MyApp;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.WebViewActivity;
import com.joyplus.tvhelper.db.DBServices;
import com.joyplus.tvhelper.entity.BTEpisode;
import com.joyplus.tvhelper.entity.CurrentPlayDetailData;
import com.joyplus.tvhelper.entity.MoviePlayHistoryInfo;
import com.joyplus.tvhelper.entity.URLS_INDEX;
import com.joyplus.tvhelper.entity.VideoPlayUrl;
import com.joyplus.tvhelper.entity.XLLXFileInfo;
import com.joyplus.tvhelper.entity.service.ReturnProgramView;
import com.joyplus.tvhelper.utils.Constant;
import com.joyplus.tvhelper.utils.DefinationComparatorIndex;
import com.joyplus.tvhelper.utils.DesUtils;
import com.joyplus.tvhelper.utils.HttpTools;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.PreferencesUtils;
import com.joyplus.tvhelper.utils.Utils;
import com.joyplus.tvhelper.utils.XunLeiLiXianUtil;
import com.umeng.analytics.MobclickAgent;


public class JoyplusMediaPlayerActivity extends Activity implements JoyplusMediaPlayerListener{
	
	private boolean Debug   = true;
	
	private String  TAG     = "JoyplusMediaPlayerActivity";
	/*videoview layout      msg 100-199  level 3*/
	public JoyplusMediaPlayerVideoView     mVideoView;
	/*middle control layout msg 200-299  level 2*/
	public JoyplusMediaPlayerMiddleControl mMiddleControl;
	/*Top and bottom layout msg 300-399  level 1*/   
	public JoyplusMediaPlayerBar           mTopBottomController;
	//private Handler mHandler = new Handler(){};
	public JoyplusMediaPlayerScreenManager mScreenManager;
	//Setting msg 400-450  
	public JoyplusMediaPlayerPreference    mPreference;
	/*SubTitle TextView Control   level 4*/
	private SubTitleView                   mSubTitleView;	
	/*ad */
	private JoyplusMediaPlayerAd           mAd;
	
	public static final int   DELAY_SHOWVIEW        = 10*1000; //10s
	/*msg 0-99*/
	public static final int   MSG_MEDIAINFO         = 0;
	public static final int   MSG_UPDATEPLAYERINFO  = 1;
	public static final int   MSG_REQUSETPLAYERINFO = 2;
	
	public static final int   MSG_REQUESTPLAY       = 3;
	public static final int   MSG_REQUESTPAUSE      = 4;
	public static final int   MSG_REQUESTFORWARD    = 5;
	public static final int   MSG_REQUESTBACKWARD   = 6;
	
	public static final int   MSG_UPDATECURRENTINFO = 7;
	
	//use to ad
	public static final int   MSG_FROM_AD           = 8;//play commend come from
	public static final int   MSG_FROM_SELF         = 9;//play commend come from
	public static final int   MSG_AD_START          = 10;
	public static final int   MSG_AD_FINISH         = 11;
	public static final int   MSG_PLAYER_PLAY       = 12;
	public static final int   MSG_PLAYER_WAIT       = 13;
	public Handler PlayerHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case MSG_PLAYER_PLAY:
				String path    = (String) msg.obj;
				int   seekto  = msg.arg1;
				if(!(path==null || "".equals(path))){
					if(mAd.isPlaying()){
						PlayerHandler.removeMessages(MSG_PLAYER_WAIT);
						Message m = new Message();
						m.what   = MSG_PLAYER_WAIT;
						m.obj    = path;
						m.arg1 = (int) seekto;
						PlayerHandler.sendMessage(m);
					}else{
						mVideoView.getPlayer().SetVideoPaths(path);
						if(lastTime>0){
							mVideoView.getPlayer().SeekVideo(seekto);
						}
					}
					
				}
				break;
			case MSG_PLAYER_WAIT:
				if(mAd.isPlaying()){
					PlayerHandler.removeMessages(MSG_PLAYER_WAIT);
					Message m = new Message();
					m.what   = MSG_PLAYER_WAIT;
					m.obj    = msg.obj;
					m.arg1   = msg.arg1;
					PlayerHandler.sendMessageDelayed(m,500);
				}else{
					PlayerHandler.removeMessages(MSG_PLAYER_PLAY);
					Message m = new Message();
					m.what   = MSG_PLAYER_PLAY;
					m.obj    = msg.obj;
					m.arg1   = msg.arg1;
					PlayerHandler.sendMessage(m);
				}
				break;
			}
		}		
	};
	
    private void PlayerPlay(String path,int seek){
    	if(!(path==null || "".equals(path))){
	    	PlayerHandler.removeMessages(MSG_PLAYER_PLAY);
			Message m = new Message();
			m.what = MSG_PLAYER_PLAY;
			m.obj  = path;
			m.arg1 = seek;
			PlayerHandler.sendMessage(m);
    	}
    }
	enum URLTYPE{
		UNKNOW (0), NETWORK (1), LOCAL (2);
		private int type;
		URLTYPE(int Type){
			type = Type;
		}
		public int toInt(){
			return type;
		}
	}
	
	public  static CurrentPlayerInfo mInfo;
	public  static boolean StateOk = false;//flog of player in loading or others
	public  static Animation mAlphaDispear ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.joyplusvideoview);
		new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				InitResource();
				initFromIntent(getIntent());
			}			
		}.run();		
	}
	private class JoyplusFinish implements Runnable{
		@Override 
        public void run() {
			finish();
			JoyplusMediaPlayerManager.getInstance().unregisterListener(JoyplusMediaPlayerActivity.this);
			mTopBottomController.JoyplussetVisible(false, 0);
        	mMiddleControl.JoyplussetVisible(false, 0);
        	mVideoView.JoyplussetVisible(false, 0);
        }
	};
	private void finishActivity(){
		new JoyplusFinish().run();
	}
	private Handler MiniHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case JoyplusMediaPlayerMiddleControlMini.MSG_PAUSEPLAY:
				mVideoView.getPlayer().PauseVideo();
				return;
			case JoyplusMediaPlayerMiddleControlMini.MSG_KEYDOWN_CENTER:
				finishActivity();
				break;
			case JoyplusMediaPlayerMiddleControlMini.MSG_KEYDOWN_LEFT:
				if(mInfo.mType == URLTYPE.NETWORK && mInfo.getHavePre()){
					InitUI();
					JoyplusMediaPlayerManager.getInstance().ResetURLAndSub();
					if (mProd_type == 3) 
					    mEpisodeIndex +=1;
					else
						mEpisodeIndex -=1;
					mHandler.sendEmptyMessage(MESSAGE_RETURN_DATE_OK);
				}
				break;
			case JoyplusMediaPlayerMiddleControlMini.MSG_KEYDOWN_TOP:
				if(mVideoView.getPlayer() != null){
					mVideoView.getPlayer().StartVideo();
				}
				mMiddleControl.JoyplussetVisible(false, JoyplusMediaPlayerMiddleControl.LAYOUT_MINI);
				break;
			case JoyplusMediaPlayerMiddleControlMini.MSG_KEYDOWN_RIGHT:
				if(mInfo.mType == URLTYPE.NETWORK && mInfo.getHaveNext()){
					InitUI();
					JoyplusMediaPlayerManager.getInstance().ResetURLAndSub();
					if (mProd_type == 3)
						mEpisodeIndex -= 1;
					else
						mEpisodeIndex += 1;
					mHandler.sendEmptyMessage(MESSAGE_RETURN_DATE_OK);
				}
				break;
			case JoyplusMediaPlayerMiddleControlMini.MSG_KEYDOWN_BOTTOM:
				if(mInfo.mCollection!=0)
				    mInfo.setCollection(false);
				else 
					mInfo.setCollection(true);
				if(mVideoView.getPlayer() != null){
					mVideoView.getPlayer().StartVideo();
				}
				mMiddleControl.JoyplussetVisible(false, JoyplusMediaPlayerMiddleControl.LAYOUT_MINI);
				break;
			case JoyplusMediaPlayerMiddleControlMini.MSG_KEYDOWN_PAUSEPLAY:
				if(mVideoView.getPlayer() != null){
					mVideoView.getPlayer().StartVideo();
				}
				mMiddleControl.JoyplussetVisible(false, JoyplusMediaPlayerMiddleControl.LAYOUT_MINI);
				mTopBottomController.JoyplussetVisible(true, 0);//Controller should be hide next.
				break;
			}			
		}
	};
	private Handler PreferenceHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "handleMessage msg.what-->" + msg.what);
			switch(msg.what){
			case JoyplusMediaPlayerPreference.MSG_QUALITY_CHANGE:
				if(msg.obj == null) return;
				Quality currQuality= (Quality) msg.obj;
				if(currQuality != null){
					InitUI();
					Log.i(TAG, "handleMessage currQuality-->" + currQuality.toInt());
					mURLManager.setDefaultQuality(currQuality);
					mHandler.sendEmptyMessage(MESSAGE_URLS_READY);
				}
				break;
			case JoyplusMediaPlayerPreference.MSG_SUB_CHANGE:
				final int currIndex = msg.arg1;
				mSubTitleView.hiddenSubtitle();
				if(currIndex == 0) {
					mJoyplusSubManager.setSubEnable(false);
					return;
				}
				new Thread(new Runnable() {					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mJoyplusSubManager.setSubEnable(true);
						mJoyplusSubManager.SwitchSub(currIndex -1);
						mSubTitleView.displaySubtitle();
					}
				}).start();				
				break;
			}
		}
	};
	
    @Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		mHandler.removeCallbacksAndMessages(null);
		m_ReturnProgramView = null;
		initFromIntent(intent);
	}

	private void InitResource() {
		// TODO Auto-generated method stub
    	mInfo  = new CurrentPlayerInfo();
    	JoyplusMediaPlayerMiddleControlMini.setHandler(MiniHandler);
    	try {
			JoyplusMediaPlayerManager.Init(JoyplusMediaPlayerActivity.this);
			JoyplusMediaPlayerManager.getInstance().registerListener(JoyplusMediaPlayerActivity.this);
			mScreenManager = new JoyplusMediaPlayerScreenManager(JoyplusMediaPlayerActivity.this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	mVideoView           = new JoyplusMediaPlayerVideoView(this);
    	mMiddleControl       = (JoyplusMediaPlayerMiddleControl) this.findViewById(R.id.JoyplusMediaPlayerMiddleControl);
    	mTopBottomController = new JoyplusMediaPlayerBar(this);
    	registerReceiver(mReceiver, new IntentFilter(Constant.VIDEOPLAYERCMD));
    	mAlphaDispear        = AnimationUtils.loadAnimation(this, R.anim.alpha_disappear);
    	mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mPreference          = new JoyplusMediaPlayerPreference(JoyplusMediaPlayerActivity.this);
		    	mPreference.setHandler(PreferenceHandler);
			}
		});
    	
    	ResetURLAndSub();
    	mSubTitleView        = (SubTitleView) findViewById(R.id.tv_subtitle);
    	mSubTitleView.Init(this);
    	//add ad 
    	mAd = new JoyplusMediaPlayerAd(this);
	}
	
	private void ResetURLAndSub(){
		JoyplusMediaPlayerManager.getInstance().ResetURLAndSub();
    	mURLManager = JoyplusMediaPlayerManager.getInstance().getURLManager();
    	mJoyplusSubManager = JoyplusMediaPlayerManager.getInstance().getSubManager();
	}
	
	public void InitUI(){
		StateOk = false;
		mVideoView.Init();
    	mTopBottomController.Init();
    	mMiddleControl.Init();
    	CurrentPlayerInfo Info  = new CurrentPlayerInfo(mInfo);
    	mInfo = null;
    	mInfo = new CurrentPlayerInfo();
    	mInfo.mType = Info.mType;
    	mInfo.NotifyPlayerInfo(); 
    	if(mVideoView.getPlayer() == null){
    		finishActivity();
    		return;
    	}
    	mAd.startAD();
	}

    private void initFromIntent(Intent intent){
    	Log.i(TAG, "initFromIntent--->");
    	InitUI();
    	ResetURLAndSub();
    	if(intent != null && intent.getData() != null){
    		mInfo.mType = URLTYPE.LOCAL;
    		CreateLocal(intent.getData());
    	}else{
    		mInfo.mType = URLTYPE.NETWORK;
    		Create();
    	}
    }
    
    /*Interface of get the videoview of which used*/
    public VideoViewInterface getPlayer(){
    	return mVideoView.getPlayer();
    }
	private boolean JoyplusdispatchMessage(Message msg){
		//if(Debug)Log.d(TAG,"JoyplusdispatchMessage()");
		if(!mVideoView.JoyplusdispatchMessage(msg))
			if(!mMiddleControl.JoyplusdispatchMessage(msg))
				if(!mTopBottomController.JoyplusdispatchMessage(msg))
				       return false;
		return true;
	}
	
	private boolean JoyplusonKeyLongPress(int keyCode, KeyEvent event){
		if(Debug)Log.d(TAG,"JoyplusonKeyLongPress() "+keyCode);
		if(!mMiddleControl.JoyplusonKeyLongPress(keyCode, event))
		    if(!mTopBottomController.JoyplusonKeyLongPress(keyCode, event))
			   return false;
		return true;
	}

	private boolean JoyplusonKeyDown(int keyCode, KeyEvent event){
		if(Debug)Log.d(TAG,"JoyplusonKeyDown() "+keyCode);
		if(!mMiddleControl.JoyplusonKeyDown(keyCode, event))
		    if(!mTopBottomController.JoyplusonKeyDown(keyCode, event))
			   return false;
		return true;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub	
		if(!event.isTracking())event.startTracking();//now we can listener to omKeyLongPress	
		return true;
	}
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(!StateOk ){
			return true;
		}
		if(JoyplusonKeyLongPress(keyCode,event))return true;
		switch(keyCode){
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			 Message msg = new Message();
			 msg.what    = JoyplusMediaPlayerBar.MSG_SHOWANDKEYLONGPRESS;
			 msg.obj     = keyCode;
		     mTopBottomController.JoyplusdispatchMessage(msg);
		     break;
		}
		return super.onKeyLongPress(keyCode, event);
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(!StateOk){
			//when loading ,we only can finish it.
			if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == 111)finishActivity();
			return true;
		}
		if(JoyplusonKeyDown(keyCode,event))return true;
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
		case 111:
			if(mProd_type == 1 || mInfo.mType == URLTYPE.LOCAL){//movie
				finishActivity();return true; 
			}
			JoyplusMediaPlayerMiddleControlMini.setLayout(JoyplusMediaPlayerMiddleControlMini.LAYOUT_SWITCH);
			mMiddleControl.JoyplussetVisible(true, JoyplusMediaPlayerMiddleControl.LAYOUT_MINI);
			return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			Message msg = new Message();
			msg.what    = JoyplusMediaPlayerBar.MSG_SHOWANDKEYDOWN;
			msg.obj     = keyCode;
		    mTopBottomController.JoyplusdispatchMessage(msg);
			return true;
		case KeyEvent.KEYCODE_DPAD_UP:
		case KeyEvent.KEYCODE_DPAD_DOWN:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_VOLUME_MUTE:
			mMiddleControl.JoyplussetVisible(true, JoyplusMediaPlayerMiddleControl.LAYOUT_AUDIO);
			if(mMiddleControl.JoyplusonKeyDown(keyCode, event))return true;			
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			JoyplusMediaPlayerMiddleControlMini.setLayout(JoyplusMediaPlayerMiddleControlMini.LAYOUT_PAUSEPLAY);
			mMiddleControl.JoyplussetVisible(true, JoyplusMediaPlayerMiddleControl.LAYOUT_MINI);
			RequestMediaPlayerBarShowandHold();
			break;
		case KeyEvent.KEYCODE_MENU:
			if(mProd_type < 0) return true;
			if(mPreference.isShowing()){
				mPreference.setVisible(false);
				return true;
			}
			mPreference.setVisible(true);
			break;
		}
		return super.onKeyUp(keyCode, event);
	}
	private void RequestMediaPlayerBarShowandHold(){
		Message m = new Message();
		m.what    = JoyplusMediaPlayerBar.MSG_SHOWANDHOLD;
		mTopBottomController.JoyplusdispatchMessage(m);
	}
	private Handler VIDEOPLAYERCMD_Handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case MSG_REQUESTPLAY:
				break;
			case MSG_REQUESTPAUSE:
				break;
			case MSG_REQUESTFORWARD:
				break;
			case MSG_REQUESTBACKWARD:
				finishActivity();
				break;
			}
		}
	};
	
	@Override
	public void MediaInfo(MediaInfo info) {
		// TODO Auto-generated method stub
		if((info.getState().toInt()>=STATE.MEDIA_STATE_INITED.toInt()&&!StateOk)){ 
			mVideoView.getPlayer().StartVideo();
			if(mVideoView.hasMediaInfoChange()){
				mMiddleControl.JoyplussetVisible(false, 0);
				mTopBottomController.JoyplussetVisible(true, 0);
				if(mJoyplusSubManager.CheckSubAviable())mSubTitleView.displaySubtitle();
				StateOk = true;//now we can dispatch keydown or others operation
			}
		}
		mInfo.mState = info.CreateMediaInfo().getState();
		//if(mInfo.mState == STATE.MEDIA_STATE_FINISH)MediaCompletion();
		Message m = new Message();
		m.what    = MSG_MEDIAINFO;
		m.obj     = info;
		JoyplusdispatchMessage(m);
	}
	@Override
	public void MediaCompletion() {
		// TODO Auto-generated method stub
		if(mInfo.mType == URLTYPE.NETWORK){
//			autoPlayNext();			
		}else{//local media should be exit
			finishActivity();
		}
	}
	@Override
	public void ErrorInfo() {
		// TODO Auto-generated method stub
		Log.e(TAG,"+++++++++++++++++ERROR+++++++++++++++");
		mHandler.sendEmptyMessage(MESSAGE_URL_NEXT);
//		finishActivity();
	}
	@Override
	public void NoProcess(String commend) {
		// TODO Auto-generated method stub
		Log.i(TAG,"Commend ("+commend+") is no disptch !!!");
	} 
	
	
	public void SaveCurrentRecord(){
		if (mVideoView.CurrentMediaInfo != null && mInfo.mType == URLTYPE.NETWORK) {
			MediaInfo info       = mVideoView.CurrentMediaInfo.CreateMediaInfo();
			long duration        = info.getTotleTime();
			long curretnPosition = info.getCurrentTime();
			Log.d(TAG, "duration ->" + duration);
			Log.d(TAG, "curretnPosition ->" + curretnPosition);
			if(duration<=curretnPosition || curretnPosition<=0)return;
			if(duration-curretnPosition<10*1000){
				saveToDB(duration / 1000, (duration / 1000) -10);
			}else{
				saveToDB(duration / 1000, curretnPosition / 1000);
			}
		}
	}
	/* follow was use to handle the local resource
	 * 
	 * */
	private void CreateLocal( Uri uri){
		String scheme = uri.getScheme();
        if (scheme.equals("content")) {
			try {
				initFromContentUri(uri);
				PlayerPlay(uri.toString(),0);
				//mVideoView.getPlayer().SetVideoPaths(uri.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				onCreateDialog(0);//the uri was bad we can finish this.
				e.printStackTrace();
			}
        } else if (uri.getScheme().equals("file")) {
        	mInfo.mPlayerName = getMediaName(uri);
        	PlayerPlay(uri.toString(),0);
        	//mVideoView.getPlayer().SetVideoPaths(uri.toString());
        }
        mInfo.NotifyPlayerInfo();
	}
	private String getMediaName(Uri uri){
		String extension = uri.getPath();
		if(!TextUtils.isEmpty(extension)){
			int dotPos = extension.lastIndexOf('.');
            if (0 <= dotPos) {
                extension = extension.substring(dotPos + 1);
            }
		}
		return extension;
	}
	private String getMediaType(String string){
		String extension = MimeTypeMap.getFileExtensionFromUrl(string);
        if (TextUtils.isEmpty(extension)) {
            // getMimeTypeFromExtension() doesn't handle spaces in filenames nor can it handle
            // urlEncoded strings. Let's try one last time at finding the extension.
            int dotPos = string.lastIndexOf('.');
            if (0 <= dotPos) {
                extension = string.substring(dotPos + 1);
            }
        }
        return extension;
	}
	 private void initFromContentUri(Uri uri) throws Exception {
	        ContentResolver cr = this.getContentResolver();
	        Cursor c = cr.query(uri, new String[] {Video.Media.DATA}, null, null, null);
	        if (c != null) {
	            try {
	                if (c.moveToFirst()) {
	                    String path;
	                    try {
	                        // Local videos will have a data column
	                        path = c.getString(0);
	                    } catch (IllegalArgumentException e) {
	                        // For non-local videos, the path is the uri
	                        path = uri.toString();
	                    }
	                    mInfo.mPlayerName = path.substring(path.lastIndexOf('/') + 1);
	                } else {
	                    throw new Exception("Nothing found: " + uri);
	                }
	            } finally {
	                c.close();
	            }
	        } else {
	            throw new Exception("Bad URI: " + uri);
	        }
	    }

	/*
	 * this class was use to interface the player param before (VideoPlayerJPActivity)
	 * 
	 */
	public class CurrentPlayerInfo  implements Parcelable{
		public String  mPlayerUri  = "";             //the uri which was use to play
		public String  mPlayerName = "";             //the name of media
		public String  mQua        = "Unknow";       //the qua of madia 720P/1080P/Unknow
		public URLTYPE mType       = URLTYPE.UNKNOW; //the flog of this resource from
		public int     mCollection = 0 ;             //false =0 else true
		public STATE   mState      = STATE.MEDIA_STATE_UNKNOW;//current state
	    
		public int     mLastTime   = 0 ;
		public String  mFrom       = "";
		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}
		public CurrentPlayerInfo(){
		}
		public CurrentPlayerInfo(CurrentPlayerInfo info){
			if(info != null){
				mPlayerUri  = info.mPlayerUri;
				mPlayerName = info.mPlayerName;
				mQua        = info.mQua;
				mType       = info.mType;
				mCollection = info.mCollection;
				mState      = info.mState;
				mLastTime   = info.mLastTime;
				mFrom       = info.mFrom;
			}
		}
		public CurrentPlayerInfo CreateInfo(){
			return new CurrentPlayerInfo(this);
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			// TODO Auto-generated method stub
			dest.writeString(mPlayerUri);
			dest.writeString(mPlayerName);
			dest.writeString(mQua);
			dest.writeInt(mType.toInt());
			dest.writeInt(mCollection);
			dest.writeInt(mState.toInt());
			dest.writeInt(mLastTime);
			dest.writeString(mFrom);
		}
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			StringBuffer sb = new StringBuffer();
	        sb.append("CurrentPlayerInfo{ mPlayerUri: ").append(mPlayerUri).
	           append(", mPlayerName: ").append(mPlayerName).
	           append(", mQua: ").append(mQua).
	           append(", mType: ").append(mType.toInt()).
	           append(", mCollection: ").append((mCollection==0)?false:true).
	           append(", mLastTime: ").append(mLastTime).
	           append(", mFrom: ").append(mFrom).
	           append("} ");
	        return sb.toString();
		}
		public boolean getHaveNext(){
			if(m_ReturnProgramView ==null )return false;
			if (mProd_type == 3){
				if (mEpisodeIndex > 0&&m_ReturnProgramView.show.episodes[mEpisodeIndex-1].down_urls!=null) {
					return true;
				}
			}else if(mProd_type == 2 || mProd_type == 131){
				if (mEpisodeIndex < (m_ReturnProgramView.tv.episodes.length - 1)&&m_ReturnProgramView.tv.episodes[mEpisodeIndex+1].down_urls!=null) {
					return true;
				}
			}
			return false;
		}
		public boolean getHavePre(){
			if (mProd_type == 3){
				if (mEpisodeIndex < (m_ReturnProgramView.show.episodes.length - 1)&&m_ReturnProgramView.show.episodes[mEpisodeIndex+1].down_urls!=null) {
					return true;
				}
			}else if(mProd_type == 2 || mProd_type == 131){
				if (mEpisodeIndex > 0&&m_ReturnProgramView.tv.episodes[mEpisodeIndex-1].down_urls!=null) {
					return true;
				}
			}
			return false;
		}
		public void setCollection(boolean collection){
//			String url = "";
//			if(collection)
//				url = Constant.BASE_URL + "program/favority";
//			else
//				url = Constant.BASE_URL + "program/unfavority";
//			Map<String, Object> params = new HashMap<String, Object>();
//			params.put("prod_id", mProd_id);
//			AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
//			cb.SetHeader(app.getHeaders());
//			if(collection)
//			    cb.params(params).url(url).type(JSONObject.class).weakHandler(JoyplusMediaPlayerActivity.this, "favorityResult");
//			else 
//				cb.params(params).url(url).type(JSONObject.class).weakHandler(JoyplusMediaPlayerActivity.this, "unfavorityResult");
//			aq.ajax(url,JSONObject.class,cb);
		}
		private void NotifyPlayerInfo(){
			Message m = new Message();
			m.what    = MSG_UPDATEPLAYERINFO;
			m.obj     = new CurrentPlayerInfo(this);
			JoyplusdispatchMessage(m);
		}
	}
	
	
	
	/***********************************************************
	 * follow was copy from VideoPlayerJPActivity
	 * it don't want to change 
	 * ************************************************************/
	private static final int MESSAGE_RETURN_DATE_OK = 0;
	private static final int MESSAGE_URLS_READY = MESSAGE_RETURN_DATE_OK + 1;
	private static final int MESSAGE_PALY_URL_OK = MESSAGE_URLS_READY + 1;
	private static final int MESSAGE_URL_NEXT = MESSAGE_PALY_URL_OK + 1;
	
	public static final int TYPE_XUNLEI = -10;
	public static final int TYPE_PUSH = TYPE_XUNLEI -1;
	public static final int TYPE_PUSH_BT_EPISODE = TYPE_PUSH -2;
	
	private String mProd_id;
	private String mProd_name;
	private int    mProd_type;
	private String mProd_src;// 来源
	
	private String url_temp; //首次url备份
	private int    mDefination = 0; // 清晰度 6为尝鲜，7为普清，8为高清
	private String mProd_sub_name = null;
	private int    mEpisodeIndex = -1; // 当前集数对应的index
	private long   lastTime = 0;
	
	private List<URLS_INDEX> playUrls_hd2 = new ArrayList<URLS_INDEX>();//超清
	private List<URLS_INDEX> playUrls_hd = new ArrayList<URLS_INDEX>();//高清
	private List<URLS_INDEX> playUrls_mp4 = new ArrayList<URLS_INDEX>();//标清
	private List<URLS_INDEX> playUrls_flv = new ArrayList<URLS_INDEX>();//流畅
	
	private MoviePlayHistoryInfo play_info;
	private int isRequset = 0;
	private long lastPlayTime = -1; 
	private int loadingCount;

	private boolean isOnline;
	
	/**
	 * 网络数据
	 */
	private int               currentPlayIndex;
	private String            currentPlayUrl;
	private ReturnProgramView m_ReturnProgramView = null;
	private List<URLS_INDEX>  playUrls = new ArrayList<URLS_INDEX>();
	private AQuery            aq;
	private MyApp               app;
	private boolean           isOnlyExistFengXing = false;
	private boolean           isOnlyExistLetv = false;
    private String            sourceFromUrl = null;//当前集的原始播放地址
    
    private URLManager mURLManager = null;
	private JoyplusSubManager mJoyplusSubManager = null;
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Constant.VIDEOPLAYERCMD)) {
				int mCMD = intent.getIntExtra("cmd", 0);
				Log.d(TAG, "onReceive------>" + mCMD);
				String mContent = intent.getStringExtra("content");
				String mProd_url = intent.getStringExtra("prod_url");
				if (!mProd_url.equalsIgnoreCase(url_temp)){
					Log.d(TAG, "mProd_url != url_temp");
					return ;
				}
				/*
				 * “403”：视频推送后，手机发送播放指令。 “405”：视频推送后，手机发送暂停指令。
				 * “407”：视频推送后，手机发送快进指令。 “409”：视频推送后，手机发送后退指令。
				 */
				switch (mCMD) {
				case 403:
					VIDEOPLAYERCMD_Handler.sendEmptyMessage(MSG_REQUESTPLAY);
					break;
				case 405:
					VIDEOPLAYERCMD_Handler.sendEmptyMessage(MSG_REQUESTPAUSE);
					break;
				case 407:
					VIDEOPLAYERCMD_Handler.sendEmptyMessage(MSG_REQUESTFORWARD);
				case 409:
					VIDEOPLAYERCMD_Handler.sendEmptyMessage(MSG_REQUESTBACKWARD);
					break;
				}
			} 
		}
	};

	
	private void Create() {
		// TODO Auto-generated method stub
		aq = new AQuery(this);
		app = (MyApp) getApplication();
		m_ReturnProgramView = app.get_ReturnProgramView();
		initVedioDate();
	}
	
	private void initVedioDate() {
		// 点击某部影片播放时，会全局设置CurrentPlayData
		CurrentPlayDetailData playDate = app.getmCurrentPlayDetailData();
		if (playDate == null) {// 如果不设置就不播放
			//change by Jas@20130816
			finishActivity();
			return;
		}
		
		// 初始化基本播放数据
		mProd_id = playDate.prod_id;
		mProd_type = playDate.prod_type;
		mProd_name = playDate.prod_name;
		mProd_sub_name = playDate.prod_sub_name;
		currentPlayUrl = playDate.prod_url;
		url_temp = playDate.prod_url;
		mDefination = playDate.prod_qua;
		lastTime = (int) playDate.prod_time;
		mProd_src = playDate.prod_src;
		isOnline = playDate.isOnline;
		Log.d(TAG, "type---->" + mProd_type);
		if(mProd_type == TYPE_PUSH ||mProd_type == TYPE_PUSH_BT_EPISODE){
			play_info = (MoviePlayHistoryInfo) playDate.obj;
		}
		
		if(mDefination == 0){
			mDefination = 8;
		}
		
		// 更新播放来源和上次播放时间
		updateSourceAndTime();
		updateName();
		
		switch (mProd_type) {
		case TYPE_XUNLEI://迅雷 传递-10
			
			//取list
			MyApp.pool.execute(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					XLLXFileInfo xllxFileInfo = (XLLXFileInfo) app.getmCurrentPlayDetailData().obj;
					if(xllxFileInfo != null && !xllxFileInfo.isDir) {
						
						ArrayList<VideoPlayUrl> list = 
								XunLeiLiXianUtil.getLXPlayUrl(JoyplusMediaPlayerActivity.this, xllxFileInfo);
						//get subtitle
//						subTitleUrlList = XunLeiLiXianUtil.getSubtitleList(VideoPlayerJPActivity.this,xllxFileInfo);
						mJoyplusSubManager.setSubUri(XunLeiLiXianUtil.
								getSubtitleList(JoyplusMediaPlayerActivity.this,xllxFileInfo));
						mSubTitleView.displaySubtitle();
//						currentSubtitleIndex = 0;
//						initSubTitleCollection();
						
						if(list != null && list.size() > 0) {
							
							if(playUrls != null && playUrls.size() > 0) {
								
								playUrls.clear();
							}
							for(int i=0;i<list.size();i++) {
								
								VideoPlayUrl videoPlayUrl = list.get(i);
								Log.i(TAG, "VideoPlayUrl--->" + videoPlayUrl.toString());
								if(videoPlayUrl != null && videoPlayUrl.playurl != null) {
									
									URLS_INDEX url = new URLS_INDEX();
									url.url = videoPlayUrl.playurl;
									url.source_from = "XUNLEI";
									if(videoPlayUrl.isCanDrag){// can drag hd2 hd mp4 
										
										if(videoPlayUrl.sharp != null) {
											
											int index = videoPlayUrl.sharp.getIndex();
											switch (index) {
											case 0:
												url.defination_from_server ="mp4";
												break;
											case 2:
												url.defination_from_server ="hd";
												break;
											case 3:
												url.defination_from_server ="hd2";
												break;

											default:
												break;
											}
										}
									}else {//can't drag flv
										url.defination_from_server ="flv";
//										playUrls_flv.a
									}
								
									playUrls.add(url);
								}
							}
						}
					}
					initFourList();
					sortPushUrls(mDefination);
					mHandler.sendEmptyMessage(MESSAGE_URLS_READY);
				}
			});
			break;
//		case TYPE_LOCAL: //本地
//			if(currentPlayUrl!=null){
//				mHandler.sendEmptyMessage(MESSAGE_PALY_URL_OK);
//			}else{
//				finish();
//			}
//			break;
		case TYPE_PUSH: //推送
			MyApp.pool.execute(new getPlayList());
			break;
		case TYPE_PUSH_BT_EPISODE:// BT聚集
			MyApp.pool.execute(new getEpisodePlayUrls());
			break;
		default:
			if (currentPlayUrl != null && URLUtil.isNetworkUrl(currentPlayUrl)) {
				mHandler.sendEmptyMessage(MESSAGE_PALY_URL_OK);
			}else{
				if (app.get_ReturnProgramView() != null) {// 如果不为空，获取服务器返回的详细数据

					m_ReturnProgramView = app.get_ReturnProgramView();
					mHandler.sendEmptyMessage(MESSAGE_RETURN_DATE_OK);
				} else {// 如果为空，就重新获取
					getProgramViewDetailServiceData();
				}
			}
			break;
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MESSAGE_RETURN_DATE_OK:
				new Thread(new PrepareTask()).start();
				break;
//			case MESSAGE_NO_NETCONNECT:
//				mVideoView.pause();
//				mDateLoadingLayout.setVisibility(View.GONE);
//				mHandler.removeCallbacksAndMessages(null);
//				showDialog(2);
//				break;
			case MESSAGE_URLS_READY:// url 准备好了
				if(playUrls.size()<=0){
					if(mProd_type==TYPE_PUSH){
						if(isRequset==2){
							if(URLUtil.isNetworkUrl(URLDecoder.decode(play_info.getPush_url()))){
								Intent intent_web = new Intent(JoyplusMediaPlayerActivity.this, WebViewActivity.class);
								intent_web.putExtra("url", URLDecoder.decode(play_info.getPush_url()));
								startActivity( intent_web);
							}else{
								if(!isFinishing()){
									showDialog(0);
								}
							}
						}else{
							//失效了 接着搞
							new Thread(new RequestNewUrl()).start();
						}
					}else if(mProd_type == TYPE_XUNLEI){
						
						if(!isFinishing()){
							showDialog(0);
						}
					}else if(mProd_type == TYPE_PUSH_BT_EPISODE){
						if(isRequset>1){
							if(!isFinishing()){
								showDialog(0);
							}
						}else{
							new Thread(new RequestNewUrl()).start();
						}
					}
					return;
				}
				
				//字幕获取
				if((mProd_type == TYPE_PUSH || mProd_type == TYPE_PUSH_BT_EPISODE) && 
						!mJoyplusSubManager.CheckSubAviable()){
					MyApp.pool.execute(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (play_info != null
									&& play_info.getPush_url() != null
									&& !play_info.getPush_url().equals("")) {
								if (play_info.getSubList() != null) {
									mJoyplusSubManager.setSubUri(play_info.getSubList());
									mSubTitleView.displaySubtitle();
								} else {
									String subTitleUrl = Constant.BASE_URL
											+ "/joyplus/subtitle/?url="
											+ URLEncoder.encode(play_info.getPush_url())
											+ "&md5_code=" + getUmengMd5();
									// subTitleUrlList =
									// XunLeiLiXianUtil.getSubtitle4Push(subTitleUrl,
									// Constant.APPKEY);
									mJoyplusSubManager.setSubUri(XunLeiLiXianUtil
													.getSubtitle4Push(subTitleUrl,
															Constant.APPKEY));
									mSubTitleView.displaySubtitle();
									// currentSubtitleIndex = 0;
									// initSubTitleCollection();
								}
							}
						}
					});
				}
				
				currentPlayIndex = 0;
				currentPlayUrl = playUrls.get(currentPlayIndex).url;
				Log.d(TAG,"MESSAGE_URLS_READY--->currentPlayUrl:" + currentPlayUrl);
				mProd_src = playUrls.get(currentPlayIndex).source_from;
				if (currentPlayUrl != null
						&& URLUtil.isNetworkUrl(currentPlayUrl)) {
					// 地址跳转相关。。。
					new Thread(new UrlRedirectTask()).start();
//					mHandler.sendEmptyMessage(MESSAGE_PALY_URL_OK);
					// 要根据不同的节目做相应的处理。这里仅仅是为了验证上下集
				}else {
					
					mHandler.sendEmptyMessage(MESSAGE_URL_NEXT);
				}
				break;
			case MESSAGE_URL_NEXT:
				if (playUrls.size() <= 0) {
					if (app.get_ReturnProgramView() != null) {
						m_ReturnProgramView = app.get_ReturnProgramView();
						mHandler.sendEmptyMessage(MESSAGE_RETURN_DATE_OK);
					} else {
						if (mProd_type > 0 && !"-1".equals(mProd_id)
								&& mProd_id != null) {
							getProgramViewDetailServiceData();
						}
						else if(mProd_type==TYPE_PUSH){
							if(isRequset==1){
								if(URLUtil.isNetworkUrl(URLDecoder.decode(play_info.getPush_url()))){
									Intent intent_web = new Intent(JoyplusMediaPlayerActivity.this, WebViewActivity.class);
									intent_web.putExtra("url", URLDecoder.decode(play_info.getPush_url()));
									startActivity( intent_web);
								}else{
									if(!isFinishing()){
										showDialog(0);
									}
								}
								
							}else{
								//失效了 接着搞
								new Thread(new RequestNewUrl()).start();
							}
						}else if(mProd_type == TYPE_PUSH_BT_EPISODE){
							if(isRequset>1){
								if(!isFinishing()){
									showDialog(0);
								}
							}else{
								new Thread(new RequestNewUrl()).start();
							}
						}
					}
				} else {
					if (currentPlayIndex < playUrls.size() - 1) {
						currentPlayIndex += 1;
						currentPlayUrl = playUrls.get(currentPlayIndex).url;
						mProd_src = playUrls.get(currentPlayIndex).source_from;
						if (currentPlayUrl != null
								&& URLUtil.isNetworkUrl(currentPlayUrl)) {
							// 地址跳转相关。。。
							Log.d(TAG, "currentPlayUrl" + currentPlayUrl);
							Log.d(TAG, "mProd_src" + mProd_src);
							new Thread(new UrlRedirectTask()).start();
//							mHandler.sendEmptyMessage(MESSAGE_PALY_URL_OK);
						}
					} else {
						// 所有的片源都不能播放
						Log.e(TAG, "no url can play!");
						if(mProd_type==TYPE_PUSH){
							if(isRequset==1){
								if(URLUtil.isNetworkUrl(URLDecoder.decode(play_info.getPush_url()))){
									Intent intent_web = new Intent(JoyplusMediaPlayerActivity.this, WebViewActivity.class);
									intent_web.putExtra("url", URLDecoder.decode(play_info.getPush_url()));
									startActivity( intent_web);
								}else{
									if(!isFinishing()){
										showDialog(0);
									}
								}
							}else{
								//失效了 接着搞
								new Thread(new RequestNewUrl()).start();
							}
						}else if(mProd_type == TYPE_PUSH_BT_EPISODE){
							if(isRequset>1){
								if(!isFinishing()){
									showDialog(0);
								}
							}else{
								new Thread(new RequestNewUrl()).start();
							}
						}
					}
				}
//				if(JoyplusMediaPlayerManager.getInstance().IshaveNextType()){
//					mURLManager.resetCurURLS_INDEX();
//					InitUI();
//					mHandler.sendEmptyMessage(MESSAGE_URLS_READY);
//				}else {
//					noUrlCanPlay();
//				}
				break;
			case MESSAGE_PALY_URL_OK:
				updateName();
				updateSourceAndTime();
				//add by Jas@20130816 for player
				mInfo.mPlayerUri = currentPlayUrl;
				PlayerPlay(mInfo.mPlayerUri , (int)lastTime);
				break;
			default:
				break;
			}
		}
	};
	
	private String defintionToType(int defintion){		
		String sourceQua = "";		
		switch (defintion) {
		case 8:
			sourceQua = "hd2";
			break;
		case 7:
			sourceQua = "mp4";
			break;
		case 6:
			sourceQua = "flv";
			break;
		}		
		return sourceQua;
	}
	
	private void noUrlCanPlay(){		
		if(!JoyplusMediaPlayerActivity.this.isFinishing()){			
			//所有url不能播放，向服务器传递-1
			showDialog(0);
//			saveToDB(-1, 0);
		}
	}

	private void updateName() {
		switch (mProd_type) {
		case -1:
		case 1:
			//mVideoNameText.setText(mProd_name);
			mInfo.mPlayerName = mProd_name;
			break;
		case 2:
		case 131:
			//mVideoNameText.setText(mProd_name + " 第" + mProd_sub_name + "集");
			mInfo.mPlayerName = mProd_name+" 第" + mProd_sub_name + "集";
			break;
		case 3:
			//mVideoNameText.setText(mProd_name + " " + mProd_sub_name);
			mInfo.mPlayerName = mProd_name+" "+ mProd_sub_name;
			break;
		case TYPE_PUSH:
		case TYPE_PUSH_BT_EPISODE:
		case TYPE_XUNLEI:
			mInfo.mPlayerName = mProd_name;
			break;
		default:
			mInfo.mPlayerName = "UnKnow ";
			break;
		}
		mInfo.NotifyPlayerInfo();
	}
		
	private void autoPlayNext(){
		switch (mProd_type) {
		case 1:
			finishActivity();
			break;
		case 2:
		case 131:
			if(mEpisodeIndex<m_ReturnProgramView.tv.episodes.length-1){
				playNext();
			}else{
				setResult2Xiangqing();//返回集数和是否收藏
				finishActivity();
			}
			break;
		case 3:
			if(mEpisodeIndex>0){
				playNext();
			}else{
				setResult2Xiangqing();//返回集数和是否收藏
				finishActivity();
			}
			break;
		default:
			finishActivity();
			break;
		}
	}
	private void playNext(){
		InitUI();
		JoyplusMediaPlayerManager.getInstance().ResetURLAndSub();
		lastTime = 0;
		if (mProd_type == 3) {
			mEpisodeIndex -= 1;
		} else {
			mEpisodeIndex += 1;
		}
		mHandler.sendEmptyMessage(MESSAGE_RETURN_DATE_OK);
	}

	private void updateSourceAndTime() {
		Log.d(TAG, " ---- sre = " + mProd_src);
		if (mProd_src == null || mProd_src.length() == 1
				|| "null".equals(mProd_src)) {
			//mResourceTextView.setText("");
			//add by Jas
			mInfo.mFrom = "";
		} else {
			String strSrc = "";
			if (mProd_src.equalsIgnoreCase("wangpan")) {
				strSrc = "PPTV";
			} else if (mProd_src.equalsIgnoreCase("le_tv_fee")) {
				strSrc = "乐  视";
			} else if (mProd_src.equalsIgnoreCase("letv")) {
				strSrc = "乐  视";
			} else if (mProd_src.equalsIgnoreCase("fengxing")) {
				strSrc = "风  行";
			} else if (mProd_src.equalsIgnoreCase("qiyi")) {
				strSrc = "爱  奇  艺";
			} else if (mProd_src.equalsIgnoreCase("youku")) {
				strSrc = "优  酷";
			} else if (mProd_src.equalsIgnoreCase("sinahd")) {
				strSrc = "新  浪  视  频";
			} else if (mProd_src.equalsIgnoreCase("sohu")) {
				strSrc = "搜  狐  视  频";
			} else if (mProd_src.equalsIgnoreCase("qq")) {
				strSrc = "腾  讯  视  频";
			} else if (mProd_src.equalsIgnoreCase("pptv")) {
				strSrc = "PPTV";
			} else if (mProd_src.equalsIgnoreCase("m1905")) {
				strSrc = "电  影  网";
			} else if (mProd_src.equalsIgnoreCase("p2p")) {
				strSrc = "P 2 P";
			}else {
				strSrc = "PPTV";
			}
			//add by Jas
			mInfo.mFrom = strSrc;
			//mResourceTextView.setText(strSrc);
		}
		//add by Jas
		mInfo.mLastTime = (int) lastTime;
		//end add by Jas
//		if(mURLManager.getCurURLS_INDEX() != null){
//			Log.d(TAG, "type---->" + mURLManager.getCurURLS_INDEX().defination_from_server);
//			//mDefinationIcon.setVisibility(View.VISIBLE);
//			if(Constant.player_quality_index[0].equalsIgnoreCase(mURLManager.getCurURLS_INDEX().defination_from_server)){
//				//mDefinationIcon.setImageResource(R.drawable.player_1080p);
//				mInfo.mQua = "1080p";
//			}else if(Constant.player_quality_index[1].equalsIgnoreCase(mURLManager.getCurURLS_INDEX().defination_from_server)){
//				//mDefinationIcon.setImageResource(R.drawable.player_720p);
//				mInfo.mQua = "720p";
//			}else{
//				//mDefinationIcon.setVisibility(View.INVISIBLE);
//				mInfo.mQua = "Unknow";
//			}
//		}
		if(playUrls.size()>0&&currentPlayIndex<=playUrls.size()-1){
			if(mProd_type == TYPE_PUSH||mProd_type == TYPE_XUNLEI||mProd_type == TYPE_PUSH_BT_EPISODE){
				if("hd2".equalsIgnoreCase(playUrls.get(currentPlayIndex).defination_from_server)){
					mInfo.mQua = "hd2";
					mDefination = 8;
				}else if("hd".equalsIgnoreCase(playUrls.get(currentPlayIndex).defination_from_server)){
					mInfo.mQua = "hd";
					mDefination = 7;
				}else if("mp4".equalsIgnoreCase(playUrls.get(currentPlayIndex).defination_from_server)){
					mInfo.mQua = "mp4";
					mDefination = 6;
				}else{
//					mDefinationIcon.setVisibility(View.INVISIBLE);
					mInfo.mQua = "flv";
					mDefination = 5;
				}
			}
		}
		mInfo.NotifyPlayerInfo();
	}

	/**
	 * 把m_ReturnProgramView中数据转化成基本数据
	 * 
	 * @author Administrator
	 * 
	 */
	class PrepareTask implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			playUrls.clear();
			switch (mProd_type) {
			case 1:
				
				if(m_ReturnProgramView.movie != null) {
					
					mProd_name = m_ReturnProgramView.movie.name;
					
					if(m_ReturnProgramView.movie.episodes != null 
							&& m_ReturnProgramView.movie.episodes.length > 0 
							&& m_ReturnProgramView.movie.episodes[0].down_urls != null) {
							
							if(m_ReturnProgramView.movie.episodes[0].video_urls != null
									&& m_ReturnProgramView.movie.episodes[0].video_urls.length ==1){
								if(m_ReturnProgramView.movie.episodes[0].video_urls[0]!= null){
									
									if(m_ReturnProgramView.movie.episodes[0].video_urls[0].source != null){
										
										if(m_ReturnProgramView.movie.episodes[0].video_urls[0].source.
												equals(Constant.video_index[2])||
												m_ReturnProgramView.movie.episodes[0].video_urls[0].source.
												equals(Constant.video_index[3])){
											
											isOnlyExistLetv = true;
										}else if(m_ReturnProgramView.movie.episodes[0].video_urls[0].source.
												equals(Constant.video_index[4])){
											
											isOnlyExistFengXing = true;
										}
									}
									
									if(isOnlyExistFengXing || isOnlyExistLetv){
										
										sourceFromUrl = m_ReturnProgramView.movie.episodes[0].video_urls[0].url;
									}
								}
							}
						
						for (int i = 0; i < m_ReturnProgramView.movie.episodes[0].down_urls.length; i++) {
							
							if(m_ReturnProgramView.movie.episodes[0].down_urls[i] != null) {
								
								String souces = m_ReturnProgramView.movie.episodes[0].down_urls[i].source;
								
								if(m_ReturnProgramView.movie.episodes[0].down_urls[i].urls != null) {
									
									for (int j = 0; j < m_ReturnProgramView.movie.episodes[0].down_urls[i].urls.length; j++) {
										
										if(m_ReturnProgramView.movie.episodes[0].down_urls[i].urls[j] != null) {
											
											URLS_INDEX url = new URLS_INDEX();
											url.source_from = souces;
											url.defination_from_server = m_ReturnProgramView.movie.episodes[0].down_urls[i].urls[j].type;
											url.url = m_ReturnProgramView.movie.episodes[0].down_urls[i].urls[j].url;
											playUrls.add(url);
										}

									}
								}
							}
						}
					}

				}
				
				break;
			case 2:
			case 131:
				
				if(m_ReturnProgramView.tv != null) {
					
					mProd_name = m_ReturnProgramView.tv.name;
					
					if(m_ReturnProgramView.tv.episodes != null) {
						
						if (mEpisodeIndex == -1) {
							for (int i = 0; i < m_ReturnProgramView.tv.episodes.length; i++) {
								if (m_ReturnProgramView.tv.episodes[i] != null 
										&& mProd_sub_name
										.equals(m_ReturnProgramView.tv.episodes[i].name)) {
									mEpisodeIndex = i;
									if(m_ReturnProgramView.tv.episodes[i].down_urls == null){
										mHandler.sendEmptyMessage(MESSAGE_URLS_READY);
										return; 
									}
									
									if(m_ReturnProgramView.tv.episodes[i].video_urls != null
											&& m_ReturnProgramView.tv.episodes[i].video_urls.length ==1){
										if(m_ReturnProgramView.tv.episodes[i].video_urls[0]!= null){
											
											if(m_ReturnProgramView.tv.episodes[i].video_urls[0].source != null){
												
												if(m_ReturnProgramView.tv.episodes[i].video_urls[0].source.
														equals(Constant.video_index[2])||
														m_ReturnProgramView.tv.episodes[i].video_urls[0].source.
														equals(Constant.video_index[3])){
													
													isOnlyExistLetv = true;
												}else if(m_ReturnProgramView.tv.episodes[i].video_urls[0].source.
														equals(Constant.video_index[4])){
													
													isOnlyExistFengXing = true;
												}
											}
											
											if(isOnlyExistFengXing || isOnlyExistLetv){
												
												sourceFromUrl = m_ReturnProgramView.tv.episodes[i].video_urls[0].url;
											}
										}
									}
									
									for (int j = 0; j < m_ReturnProgramView.tv.episodes[i].down_urls.length; j++) {
										
										if(m_ReturnProgramView.tv.episodes[i].down_urls[j] != null) {
											
											String souces = m_ReturnProgramView.tv.episodes[i].down_urls[j].source;
											
											if(m_ReturnProgramView.tv.episodes[i].down_urls[j].urls != null) {
												
												for (int k = 0; k < m_ReturnProgramView.tv.episodes[i].down_urls[j].urls.length; k++) {
													
													if(m_ReturnProgramView.tv.episodes[i].down_urls[j].urls[k] != null) {
														
														URLS_INDEX url = new URLS_INDEX();
														url.source_from = souces;
														url.defination_from_server = m_ReturnProgramView.tv.episodes[i].down_urls[j].urls[k].type;
														url.url = m_ReturnProgramView.tv.episodes[i].down_urls[j].urls[k].url;
														playUrls.add(url);
													}
												}
											}
										}

									}
								}
							}
						} else {
							
							if(m_ReturnProgramView.tv.episodes.length > mEpisodeIndex
									&& m_ReturnProgramView.tv.episodes[mEpisodeIndex] != null) {
								
								if(m_ReturnProgramView.tv.episodes[mEpisodeIndex].video_urls != null
										&& m_ReturnProgramView.tv.episodes[mEpisodeIndex].video_urls.length ==1){
									if(m_ReturnProgramView.tv.episodes[mEpisodeIndex].video_urls[0]!= null){
										
										if(m_ReturnProgramView.tv.episodes[mEpisodeIndex].video_urls[0].source != null){
											
											if(m_ReturnProgramView.tv.episodes[mEpisodeIndex].video_urls[0].source.
													equals(Constant.video_index[2])||
													m_ReturnProgramView.tv.episodes[mEpisodeIndex].video_urls[0].source.
													equals(Constant.video_index[3])){
												
												isOnlyExistLetv = true;
											}else if(m_ReturnProgramView.tv.episodes[mEpisodeIndex].video_urls[0].source.
													equals(Constant.video_index[4])){
												
												isOnlyExistFengXing = true;
											}
										}
										
										if(isOnlyExistFengXing || isOnlyExistLetv){
											
											sourceFromUrl = m_ReturnProgramView.tv.episodes[mEpisodeIndex].video_urls[0].url;
										}
									}
								}
								
								if(m_ReturnProgramView.tv.episodes[mEpisodeIndex].down_urls != null) {
									
									for (int j = 0; j < m_ReturnProgramView.tv.episodes[mEpisodeIndex].down_urls.length; j++) {
										
										if(m_ReturnProgramView.tv.episodes[mEpisodeIndex].down_urls[j] != null) {
											
											String souces = m_ReturnProgramView.tv.episodes[mEpisodeIndex].down_urls[j].source;
											
											if( m_ReturnProgramView.tv.episodes[mEpisodeIndex].down_urls[j].urls != null) {
												
												for (int k = 0; k < m_ReturnProgramView.tv.episodes[mEpisodeIndex].down_urls[j].urls.length; k++) {
													
													if(m_ReturnProgramView.tv.episodes[mEpisodeIndex].down_urls[j].urls[k] != null) {
														
														URLS_INDEX url = new URLS_INDEX();
														url.source_from = souces;
														url.defination_from_server = m_ReturnProgramView.tv.episodes[mEpisodeIndex].down_urls[j].urls[k].type;
														url.url = m_ReturnProgramView.tv.episodes[mEpisodeIndex].down_urls[j].urls[k].url;
														playUrls.add(url);
													}
												}
											}
										}
									}
								}
								mProd_sub_name = m_ReturnProgramView.tv.episodes[mEpisodeIndex].name;
							}
						}
					}
				}
				
				break;
			case 3:
				
				if(m_ReturnProgramView.show != null) {
					
					mProd_name = m_ReturnProgramView.show.name;
					
					if(m_ReturnProgramView.show.episodes != null) {
						
						if (mEpisodeIndex == -1) {
							for (int i = 0; i < m_ReturnProgramView.show.episodes.length; i++) {
								
								if(m_ReturnProgramView.show.episodes[i] != null) {
									
									if (Utils.isSame4Str(mProd_sub_name, m_ReturnProgramView.show.episodes[i].name)) {
										mEpisodeIndex = i;
										mProd_sub_name = m_ReturnProgramView.show.episodes[i].name;
										if(m_ReturnProgramView.show.episodes[i].down_urls==null){
											mHandler.sendEmptyMessage(MESSAGE_URLS_READY);
											return ;
										}
										
										if(m_ReturnProgramView.show.episodes[mEpisodeIndex].video_urls != null
												&& m_ReturnProgramView.show.episodes[mEpisodeIndex].video_urls.length ==1){
											if(m_ReturnProgramView.show.episodes[mEpisodeIndex].video_urls[0]!= null){
												
												if(m_ReturnProgramView.show.episodes[mEpisodeIndex].video_urls[0].source != null){
													
													if(m_ReturnProgramView.show.episodes[mEpisodeIndex].video_urls[0].source.
															equals(Constant.video_index[2])||
															m_ReturnProgramView.show.episodes[mEpisodeIndex].video_urls[0].source.
															equals(Constant.video_index[3])){
														
														isOnlyExistLetv = true;
													}else if(m_ReturnProgramView.show.episodes[mEpisodeIndex].video_urls[0].source.
															equals(Constant.video_index[4])){
														
														isOnlyExistFengXing = true;
													}
												}
												
												if(isOnlyExistFengXing || isOnlyExistLetv){
													
													sourceFromUrl = m_ReturnProgramView.show.episodes[mEpisodeIndex].video_urls[0].url;
												}
											}
										}
										
										for (int j = 0; j < m_ReturnProgramView.show.episodes[i].down_urls.length; j++) {
											
											
											if(m_ReturnProgramView.show.episodes[i].down_urls[j] != null) {
												
												String souces = m_ReturnProgramView.show.episodes[i].down_urls[j].source;
												
												if(m_ReturnProgramView.show.episodes[i].down_urls[j].urls != null) {
													
													for (int k = 0; k < m_ReturnProgramView.show.episodes[i].down_urls[j].urls.length; k++) {
														
														if(m_ReturnProgramView.show.episodes[i].down_urls[j].urls[k] != null) {
															
															URLS_INDEX url = new URLS_INDEX();
															url.source_from = souces;
															url.defination_from_server = m_ReturnProgramView.show.episodes[i].down_urls[j].urls[k].type;
															url.url = m_ReturnProgramView.show.episodes[i].down_urls[j].urls[k].url;
															playUrls.add(url);
														}
													}
												}
											}
										}
									}
								}
							}
						} else {
							
							if(m_ReturnProgramView.show.episodes.length > mEpisodeIndex ) {
								
								if(m_ReturnProgramView.show.episodes[mEpisodeIndex] != null 
										&& m_ReturnProgramView.show.episodes[mEpisodeIndex].down_urls != null) {
									
									if(m_ReturnProgramView.show.episodes[mEpisodeIndex].video_urls != null
											&& m_ReturnProgramView.show.episodes[mEpisodeIndex].video_urls.length ==1){
										if(m_ReturnProgramView.show.episodes[mEpisodeIndex].video_urls[0]!= null){
											
											if(m_ReturnProgramView.show.episodes[mEpisodeIndex].video_urls[0].source != null){
												
												if(m_ReturnProgramView.show.episodes[mEpisodeIndex].video_urls[0].source.
														equals(Constant.video_index[1])||
														m_ReturnProgramView.show.episodes[mEpisodeIndex].video_urls[0].source.
														equals(Constant.video_index[2])){
													
													isOnlyExistLetv = true;
												}else if(m_ReturnProgramView.show.episodes[mEpisodeIndex].video_urls[0].source.
														equals(Constant.video_index[3])){
													
													isOnlyExistFengXing = true;
												}
											}
											
											if(isOnlyExistFengXing || isOnlyExistLetv){
												
												sourceFromUrl = m_ReturnProgramView.show.episodes[mEpisodeIndex].video_urls[0].url;
											}
										}
									}
									
									for (int j = 0; j < m_ReturnProgramView.show.episodes[mEpisodeIndex].down_urls.length; j++) {
										
										if(m_ReturnProgramView.show.episodes[mEpisodeIndex].down_urls[j] != null) {
											
											String souces = m_ReturnProgramView.show.episodes[mEpisodeIndex].down_urls[j].source;
											
											if(m_ReturnProgramView.show.episodes[mEpisodeIndex].down_urls[j].urls != null) {
												
												for (int k = 0; k < m_ReturnProgramView.show.episodes[mEpisodeIndex].down_urls[j].urls.length; k++) {
													
													if(m_ReturnProgramView.show.episodes[mEpisodeIndex].down_urls[j].urls[k] != null) {
														
														URLS_INDEX url = new URLS_INDEX();
														url.source_from = souces;
														url.defination_from_server = m_ReturnProgramView.show.episodes[mEpisodeIndex].down_urls[j].urls[k].type;
														url.url = m_ReturnProgramView.show.episodes[mEpisodeIndex].down_urls[j].urls[k].url;
														playUrls.add(url);
													}
												}
											}
										}

									}
								}
								mProd_sub_name = m_ReturnProgramView.show.episodes[mEpisodeIndex].name;
							}
						}
					}
				}
				break;
			}
			
			boolean hasChaoqing = false,hasGaoqing = false,hasPuqing = false;
			
			for(int i=0;i<playUrls.size();i++){
				
				URLS_INDEX url_index = playUrls.get(i);
				
				if(url_index.defination_from_server.equals(Constant.player_quality_index[0])){
					
					hasChaoqing = true;
				}else if(url_index.defination_from_server.equals(Constant.player_quality_index[1])){
					
					hasGaoqing = true;
				}else {
					
					hasPuqing = true;
				}
			}
			
			if(hasChaoqing){
				
				maxQuality = 8;
			}else {
				
				if(hasGaoqing){
					
					maxQuality = 7;
				}else {
					
					if(hasPuqing){
						
						maxQuality = 6;
					}
				}
			}

			sequenceList();
			// url list 准备完成
			mHandler.sendEmptyMessage(MESSAGE_URLS_READY);
		}
	}
	
	private void getMD5_P2P(){
//		UtilTools.setP2PMD5(getApplicationContext(), "");
//
//		if("".equals(UtilTools.getP2PMD5(getApplicationContext()))){
//			
//			String md5 = null;
//			if(Constant.TestEnv){
//				md5 = MobclickAgent.getConfigParams(this, "TEST_P2P_TV_MD5");
//			}else {
//				md5 = MobclickAgent.getConfigParams(this, "P2P_TV_MD5");
//			}
//			
////			Log.i(TAG, "md5--->" + md5);
//			if(md5 != null && !"".equals(md5)){
//				
//				UtilTools.setP2PMD5(getApplicationContext(), md5);
//			}
//		}
	}
	
	private String getUmengMd5(){
		
		String md5 = "";
		if (PreferencesUtils.getPincodeMd5(this) == null ||
				"".equals(PreferencesUtils.getPincodeMd5(this))) {
			if (Constant.TestEnv) {
				md5 = MobclickAgent.getConfigParams(this, "TEST_P2P_TV_MD5");
			} else {
				md5 = MobclickAgent.getConfigParams(this, "P2P_TV_MD5");
			}

		}else{
			md5 = PreferencesUtils.getPincodeMd5(this);
		}
		Log.i(TAG, "md5--->" + md5);
		return md5;
	}
	
	private void getDownloadUrl_P2P(){
		
		List<URLS_INDEX> tempP2PList = new ArrayList<URLS_INDEX>();
		List<URLS_INDEX> tempExP2PList = new ArrayList<URLS_INDEX>();
		
		for (int i = 0; i < playUrls.size(); i++) {
			Log.i(TAG, "url_index-->" + playUrls.get(i).toString());

			if ("p2p".equalsIgnoreCase(playUrls.get(i).source_from)) {
				tempP2PList.add(playUrls.get(i));
			}else {
				
				tempExP2PList.add(playUrls.get(i));
			}
		}
		for(int i=0;i< tempP2PList.size();i++){
			
			if("p2p".equalsIgnoreCase(tempP2PList.get(i).source_from)){
				
				String p2pUrl = tempP2PList.get(i).url;
				Log.i(TAG, "p2pUrl--->" + p2pUrl);
				if(p2pUrl != null && !p2pUrl.equals("")){//取P2P资源
					
//					if(!"".equals(UtilTools.getP2PMD5(getApplicationContext()))){
//						
//						String p2pStr = URLUtils.getXunLeiUrlURL(Constant.P2P_PARSE_URL_URL,p2pUrl,
//								UtilTools.getP2PMD5(getApplicationContext()));
//						Log.i(TAG, "p2pStr-->" + p2pStr);
//						AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();           
//						cb.url(p2pStr).type(JSONObject.class);             
//						Map<String, String> headers = new HashMap<String, String>();
//						headers.put("app_key", Constant.APPKEY);
//						cb.SetHeader(headers);        
//						aq.sync(cb);
////						AjaxStatus status = cb.getStatus();
//						JSONObject jo = cb.getResult();
//						if(jo != null && jo.has("error")){
//							Log.i(TAG, "jo-->" + jo.toString());
//							
//							try {
//								if(!jo.getBoolean("error")){
//									String downloadUrl = jo.getString("downurl");
//									if(downloadUrl != null && !"".equals(downloadUrl)){
//										String data = DesUtils.decode(Constant.DES_KEY, downloadUrl);
//										Log.i(TAG, "data code-->" + data);
//										String[] urls = data.split("\\{mType\\}");
//										for(String str : urls){
//											URLS_INDEX url_index_info = new URLS_INDEX();
//											String[] p = str.split("\\{m\\}");
//											if(p.length<2){
//												continue;
//											}
//											url_index_info.source_from="p2p";
//											url_index_info.defination_from_server = p[0];
//											if("hd".equals(p[0])){
//												url_index_info.defination_from_server="mp4";
//											}else if("hd2".equals(p[0])){
//												url_index_info.defination_from_server="hd2";
//											}else {
//												url_index_info.defination_from_server="flv";
//											}
//											url_index_info.url = p[1];
//											tempExP2PList.add(url_index_info);
//										}
//									}
//								}
//							} catch (JSONException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//						//获取字幕
//						if(!mJoyplusSubManager.CheckSubAviable()){
//							mJoyplusSubManager.setSubUri(mJoyplusSubManager.
//									getNetworkSubURI(p2pUrl, UtilTools.getP2PMD5(getApplicationContext()),getApplicationContext()));
//						}
//					}
				}
			}
		}
		
		playUrls.clear();
		playUrls = tempExP2PList;
	}
	
	private void getDownloadUrl_BaiduWP(){
		
		for (int i = 0; i < playUrls.size(); i++) {
			URLS_INDEX url_index = playUrls.get(i);
			if (url_index.source_from
					.equalsIgnoreCase(Constant.BAIDU_WANGPAN)) {
				Document doc = null;
				try {
					doc = Jsoup.connect(url_index.url).timeout(10000).get();
					// doc = Jsoup.connect(htmlStr).timeout(10000).get();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (doc != null) {
					Element e = doc.getElementById("fileDownload");
					if (e != null) {
						Log.d(TAG, "url = " + e.attr("href"));
						if (e.attr("href") != null
								&& e.attr("href").length() > 0) {
							url_index.url = e.attr("href");
						}
					}
				}
			}
		}
	}
	
	private int maxQuality = -1;//最高清晰度
	
	private void sequenceList(){		
		Log.d(TAG, "playUrls size--->" + playUrls.size() + " maxQuality--->" + maxQuality);	
		
		/**获取MD5值**/
		getMD5_P2P();
		
		/**获取P2P的真正播放地址 **/
		getDownloadUrl_P2P();
		
		/**获取百度网盘的真正播放地址 **/
		getDownloadUrl_BaiduWP();
		
		mURLManager.setDefaultQuality(mDefination);
		mURLManager.setURLS(playUrls);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	protected void onPause() {		
		Log.i(TAG, "onPause--->");		
		// TODO Auto-generated method stub
		MobclickAgent.onPause(this);
		SaveCurrentRecord();
		super.onPause();
	}
    
	@Override
	protected void onStop() {
		Log.i(TAG, "onStop--->");
		if(!isFinishing()){
//			finish();
			finishActivity();
		}
		super.onStop();
	}

	public void saveToDB(long duration, long playBackTime) {
		
		Log.d(TAG, "mProd_type---------------->" + mProd_type);
		Log.d(TAG, "mEpisodeIndex---------------->" + mEpisodeIndex);
		DBServices services = DBServices.getInstance(this);
		if(mProd_type == TYPE_PUSH_BT_EPISODE){
			if(duration>0){
				play_info.getBtEpisodes().get(mEpisodeIndex).setDuration((int) duration);
			}
			if(playBackTime>0){
				play_info.getBtEpisodes().get(mEpisodeIndex).setPlayback_time((int) playBackTime);
			}
			play_info.getBtEpisodes().get(mEpisodeIndex).setDefination(mDefination);
			play_info.setCreat_time(System.currentTimeMillis());
			services.updateMoviePlayHistory(play_info);
		}else{
			play_info.setDuration((int) duration);
			play_info.setPlayback_time((int) playBackTime);
			if(mProd_type == TYPE_PUSH){
				play_info.setDefination(mDefination);
//				play_info.setDownload_url(currentPlayUrl);
			}
			play_info.setCreat_time(System.currentTimeMillis());
			services.updateMoviePlayHistory(play_info);
		}
		
//		String url = Constant.BASE_URL + "program/play";
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("app_key", Constant.APPKEY);// required string
//		params.put("prod_id", mProd_id);
//		params.put("prod_name", mProd_name);// required
//		params.put("prod_subname", mProd_sub_name);
//		params.put("prod_type", mProd_type);// required int 视频类别
//		params.put("play_type", "1");
//		params.put("playback_time", playBackTime);// _time required int
//		params.put("duration", duration);// required int 视频时长， 单位：秒
//		params.put("video_url", currentPlayUrl);// required
//		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
//		cb.SetHeader(app.getHeaders());
//		cb.params(params).url(url).type(JSONObject.class)
//				.weakHandler(this, "CallProgramPlayResult");		
//		aq.ajax(cb);
		// DB操作，把存储到服务器的数据保存到数据库
//		TvDatabaseHelper helper = TvDatabaseHelper
//				.newTvDatabaseHelper(getApplicationContext());
//		SQLiteDatabase database = helper.getWritableDatabase();// 获取写db
//		String selection = UserShouCang.USER_ID + "=? and "
//				+ UserHistory.PRO_ID + "=?";// 通过用户id，找到相应信息
//		String[] selectionArgs = {
//				UtilTools.getCurrentUserId(getApplicationContext()), mProd_id };
//		database.delete(TvDatabaseHelper.HISTORY_TABLE_NAME, selection,
//				selectionArgs);
//		HotItemInfo info = new HotItemInfo();
//		info.prod_type = mProd_type + "";
//		info.prod_name = mProd_name;
//		info.prod_subname = mProd_sub_name;
//		info.prod_id = mProd_id;
//		info.play_type = "1";
//		info.playback_time = playBackTime + "";
//		info.video_url = currentPlayUrl;
//		info.duration = duration + "";
//		DBUtils.insertHotItemInfo2DB_History(getApplicationContext(), info,
//				UtilTools.getCurrentUserId(getApplicationContext()), database);
//		helper.closeDatabase();		
		//发送更新最新记录广播
//		app.set_ReturnProgramView(m_ReturnProgramView);
//		Intent historyIntent  = new Intent(UtilTools.ACTION_PLAY_END_HISTORY);
//		historyIntent.putExtra("prod_id", mProd_id);
//		historyIntent.putExtra("prod_sub_name", mProd_sub_name);
//		historyIntent.putExtra("prod_type", mProd_type);
//		historyIntent.putExtra("time", playBackTime);
//		sendBroadcast(historyIntent);
//		
//		Intent mainIntent  = new Intent(UtilTools.ACTION_PLAY_END_MAIN);
//		mainIntent.putExtra("prod_id", mProd_id);
//		mainIntent.putExtra("prod_sub_name", mProd_sub_name);
//		mainIntent.putExtra("prod_type", mProd_type);
//		mainIntent.putExtra("time", playBackTime);
//		sendBroadcast(mainIntent);
	}
	
	private void setResult2Xiangqing() {		
//		Intent dataIntent = getIntent();
//		dataIntent.putExtra("prod_subname", mProd_sub_name);		
//		if(isShoucang) {			
//			setResult(JieMianConstant.SHOUCANG_ADD,dataIntent);
//		} else {			
//			setResult(JieMianConstant.SHOUCANG_CANCEL,dataIntent);
//		}
	}

	public void CallProgramPlayResult(String url, JSONObject json,
			AjaxStatus status) {
		if (json != null) {
			Log.d(TAG, json.toString());
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onDestroy--->");		
		unregisterReceiver(mReceiver);	
		super.onDestroy();
	}

	private void getIsShoucangData() {
		String url = Constant.BASE_URL + "program/is_favority";
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		Map<String, String> params = new HashMap<String, String>();
		params.put("prod_id", mProd_id);
		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "initIsShoucangData");
		cb.SetHeader(app.getHeaders());
		aq.ajax(cb);
	}

	public void initIsShoucangData(String url, JSONObject json,
			AjaxStatus status) {
		if (status.getCode() == AjaxStatus.NETWORK_ERROR || json == null) {
			Utils.showToast(aq.getContext(),getResources().getString(R.string.networknotwork));
			return;
		}
		if (json == null || json.equals(""))return;
		Log.d(TAG, "data = " + json.toString());
		String flag = json.toString();
		if (!flag.equals("")) {
			if (flag.contains("true")) {
				mInfo.mCollection = 1;
			} else {
				mInfo.mCollection = 0;
			}
		} else {
			mInfo.mCollection = 1;
		}
	}

	public void favorityResult(String url, JSONObject json,AjaxStatus status) {
		if (json != null) {
			try {
				// woof is "00000",now "20024",by yyc
				if (json.getString("res_code").trim().equalsIgnoreCase("00000")) {
					Utils.showToast(this, "收藏成功!");					
					mInfo.mCollection = 1;
				} else {					
					mInfo.mCollection = 1;
					Utils.showToast(this, "已收藏!");
				}					
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// ajax error, show error code
			if (status.getCode() == AjaxStatus.NETWORK_ERROR)
				Utils.showToast(aq.getContext(),getResources().getString(R.string.networknotwork));
		}

	}

	public void unfavorityResult(String url, JSONObject json, AjaxStatus status) {
		if (json != null) {
			try {
				if (json.getString("res_code").trim().equalsIgnoreCase("00000")) {
					Utils.showToast(this, "取消收藏成功!");
					mInfo.mCollection = 0;
				} else {					
					Utils.showToast(this, "取消收藏失败!");
					mInfo.mCollection = 1;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// ajax error, show error code
			if (status.getCode() == AjaxStatus.NETWORK_ERROR)
				Utils.showToast(this,getResources().getString(R.string.networknotwork));
		}
	}
	
	private void getProgramViewDetailServiceData() {
		// TODO Auto-generated method stub
		String url = Constant.BASE_URL + "program/view" + "?prod_id="+ mProd_id;
		getServiceData(url, "initMovieDate");
	}

	public void initMovieDate(String url, JSONObject json, AjaxStatus status) {
		if (status.getCode() == AjaxStatus.NETWORK_ERROR || json == null) {
			Utils.showToast(aq.getContext(),getResources().getString(R.string.networknotwork));
			return;
		}
		if (json == null || json.equals(""))
			return;
		Log.d(TAG, "data = " + json.toString());
		ObjectMapper mapper = new ObjectMapper();
		try {
			m_ReturnProgramView = null;
			m_ReturnProgramView = mapper.readValue(json.toString(),ReturnProgramView.class);
			// 检测URL
			mHandler.sendEmptyMessage(MESSAGE_RETURN_DATE_OK);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void getServiceData(String url, String interfaceName) {
		// TODO Auto-generated method stub
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, interfaceName);
		cb.SetHeader(app.getHeaders());
		Log.d(TAG, url);
		Log.d(TAG, "header appkey" + app.getHeaders().get("app_key"));
		aq.ajax(cb);
	}
	
	private void getFenxingNetServiceData(String url){
		
		getParseServiceData(url, "initFenxingNetServiceData");
	}
	
	public void initFenxingNetServiceData(String url, JSONObject json, AjaxStatus status) {

//		if (status.getCode() == AjaxStatus.NETWORK_ERROR || json == null) {
//			Utils.showToast(aq.getContext(),
//					getResources().getString(R.string.networknotwork));
//			mHandler.sendEmptyMessage(MESSAGE_PALY_URL_OK);
//			return;
//		}
//
//		if (json == null || json.equals("")){
//			mHandler.sendEmptyMessage(MESSAGE_PALY_URL_OK);
//			return;
//		}
//
//		Log.d(TAG, "initFenxingNetServiceData = " + json.toString());
//		getFengxingSecondServiceData(Constant.FENGXING_REGET_SECOND_URL, json);
		
	}
	
	private void getFengxingSecondServiceData(String url,JSONObject json){
		
		getParseServiceData(url,json, "initFengxingSecondServiceData");
	}
	
	protected void getParseServiceData(String url,JSONObject json, String interfaceName) {
		// TODO Auto-generated method stub

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, interfaceName);
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			HttpEntity entity = new StringEntity(json.toString());
			params.put(AQuery.POST_ENTITY, entity);

			cb.params(params);
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Content-Type", "application/json");
			headers.put("app_key", Constant.APPKEY);
			cb.SetHeader(headers);
			aq.ajax(cb);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
;
	}
	
	public void initFengxingSecondServiceData(String url, JSONObject json, AjaxStatus status) {

//		if (status.getCode() == AjaxStatus.NETWORK_ERROR) {
//			Utils.showToast(aq.getContext(),
//					getResources().getString(R.string.networknotwork));
//			mHandler.sendEmptyMessage(MESSAGE_PALY_URL_OK);
//			return;
//		}
//		
//		if (json == null || json.equals("")){
//			Log.d(TAG, "initFengxingSecondServiceData = ");
//			mHandler.sendEmptyMessage(MESSAGE_PALY_URL_OK);
//			return;
//		}
//
//
//		Log.d(TAG, "initFengxingSecondServiceData = " + json.toString());
//		ObjectMapper mapper = new ObjectMapper();
//		try {
//			ReturnFengxingSecondView returnFengxingSecondView = mapper.readValue(json.toString(),
//					ReturnFengxingSecondView.class);
//			
//			if(returnFengxingSecondView != null && returnFengxingSecondView.error!= null
//					&& returnFengxingSecondView.error.equals("false")){
//				
//				if(returnFengxingSecondView.urls != null && returnFengxingSecondView.urls.length > 0){
//					
//					String type = defintionToType(mDefination);
//					if(playUrls != null){
//						
//						playUrls.clear();
//					}
//					for(int i=0;i<returnFengxingSecondView.urls.length;i++){
//						
//						URLS_INDEX urls_INDEX = new URLS_INDEX();
//						urls_INDEX.source_from = "fengxing";
//						urls_INDEX.defination_from_server = type;
//						urls_INDEX.url = returnFengxingSecondView.urls[i];
//						Log.i(TAG, "urls_INDEX--->" + urls_INDEX.toString());
//						playUrls.add(urls_INDEX);
//					}
//				}
//			}
//		} catch (JsonParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//			
//			mHandler.sendEmptyMessage(MESSAGE_PALY_URL_OK);
	}
	
	protected void getParseServiceData(String url, String interfaceName) {
		// TODO Auto-generated method stub

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, interfaceName);

		if(!"initFenxingNetServiceData".equals(interfaceName)){
			
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("app_key", Constant.APPKEY);
			cb.SetHeader(headers);
		}
		aq.ajax(cb);
	}
	
	private void getLetvParseServiceData(String url){
		
		getParseServiceData(url, "initLetvParseServiceData");
	}
	
	public void initLetvParseServiceData(String url, JSONObject json, AjaxStatus status) {

//		if (status.getCode() == AjaxStatus.NETWORK_ERROR || json == null) {
//			Utils.showToast(aq.getContext(),
//					getResources().getString(R.string.networknotwork));
//			noUrlCanPlay();
//			return;
//		}
//
//		if (json == null || json.equals("")){
//			
//			noUrlCanPlay();
//			return;
//		}
//
//
//		Log.d(TAG, "initLetvParseServiceData = " + json.toString());
//		ObjectMapper mapper = new ObjectMapper();
//		try {
//			ReturnReGetVideoView reGetVideoView = mapper.readValue(json.toString(),
//					ReturnReGetVideoView.class);
//			
//			
//			if(reGetVideoView != null && "false".equals(reGetVideoView.error)
//					&&reGetVideoView.down_urls != null
//					&& reGetVideoView.down_urls.urls != null
//					&& reGetVideoView.down_urls.urls.length > 0){
//				if(playUrls != null){
//					
//					playUrls.clear();
//				}
//				for(int i=0;i<reGetVideoView.down_urls.urls.length ;i++){
//					
//					if(reGetVideoView.down_urls.urls != null){
//						
//						URLS_INDEX urls_INDEX = new URLS_INDEX(); 
//						urls_INDEX.source_from = reGetVideoView.down_urls.source;
//						urls_INDEX.defination_from_server = reGetVideoView.down_urls.urls[i].type;
//						urls_INDEX.url = reGetVideoView.down_urls.urls[i].url;
//						Log.i(TAG, "urls_INDEX--->" + urls_INDEX.toString());
//						playUrls.add(urls_INDEX);
//					}
//				}
//				
//				if(maxQuality != -1 && isOnlyExistLetv){
//					
//					if(maxQuality == mDefination){
//						
//						mDefination = 8;
//					}
//				}
//				
//				sequenceList();
//				
//				for(int i=0;i<playUrls.size();i++){
//					
//					Log.i(TAG, "playUrls--->" + playUrls.get(i).defination_from_server);
//				}
//				// url list 准备完成
//				mHandler.sendEmptyMessage(MESSAGE_URLS_READY);
//				return;
//				
//			}
//		} catch (JsonParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		noUrlCanPlay();
	}
	
	private void getFengxingParseServiceData(String url){
		getParseServiceData(url, "initFengxingParseServiceData");
	}
	
	public void initFengxingParseServiceData(String url, JSONObject json, AjaxStatus status) {
		if (status.getCode() == AjaxStatus.NETWORK_ERROR || json == null) {
			Utils.showToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			mHandler.sendEmptyMessage(MESSAGE_PALY_URL_OK);
			return;
		}
		if (json == null || json.equals("")){
			mHandler.sendEmptyMessage(MESSAGE_PALY_URL_OK);
			return;
		}
		Log.d(TAG, "initFengxingParseServiceData= " + json.toString());		
		ObjectMapper mapper = new ObjectMapper();
//		try {
//			ReturnFirstFengxingUrlView returnFirstFengxingUrlView = mapper.readValue(json.toString(),
//					ReturnFirstFengxingUrlView.class);			
//			if(returnFirstFengxingUrlView != null){
//				if(returnFirstFengxingUrlView.error != null && 
//						returnFirstFengxingUrlView.error.equals("false")){
//					String sourceQua = defintionToType(mDefination);
//					if(returnFirstFengxingUrlView.video_infos != null &&
//							returnFirstFengxingUrlView.video_infos.length > 0){						
//						for(int i=0;i<returnFirstFengxingUrlView.video_infos.length;i++){							
//							if(returnFirstFengxingUrlView.video_infos[i]!= null 
//									&& returnFirstFengxingUrlView.video_infos[i].type != null){								
//								if(sourceQua.equals(returnFirstFengxingUrlView.video_infos[i].type)){									
//									String tempUrl = returnFirstFengxingUrlView.video_infos[i].request_url;
//									Log.i(TAG, "tempUrl--->" + tempUrl);
//									getFenxingNetServiceData(tempUrl);
//									return;
//								}
//							}							
//						}
//					}
//				}				
//			}
//		} catch (JsonParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		mHandler.sendEmptyMessage(MESSAGE_PALY_URL_OK);
	}
	
	/**
	 * 地址跳转
	 */	
	class  UrlRedirectTask implements Runnable{
		@Override
		public void run() {			
			// TODO Auto-generated method stub			
			Log.i(TAG, "UrlRedirectTask-->" + currentPlayUrl);			
			if(currentPlayUrl != null && !currentPlayUrl.equals("")) {				
				if(currentPlayUrl.indexOf(("{now_date}")) != -1) {					
					currentPlayUrl = currentPlayUrl.replace("{now_date}", System.currentTimeMillis()/1000 + "");
				}
			}			
			String str = Utils.getRedirectUrl(currentPlayUrl);			
			if(str!=null){
				currentPlayUrl = str;
				mHandler.sendEmptyMessage(MESSAGE_PALY_URL_OK);
			}else{
				mHandler.sendEmptyMessage(MESSAGE_URL_NEXT);
			}
		}		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		 Dialog alertDialog = new AlertDialog.Builder(this). 
	                setTitle("提示"). 
	                setMessage("该视频无法播放"). 
	                setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							finishActivity();
						}

					}).
	                create();
		 	alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					finishActivity();
				}
			});
	        alertDialog.show(); 
		return super.onCreateDialog(id);
	}
	
	class getEpisodePlayUrls implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			for(int i=0 ; i<play_info.getBtEpisodes().size(); i++){
				BTEpisode e = play_info.getBtEpisodes().get(i);
				if(mProd_sub_name!=null&&mProd_sub_name.equals(e.getName())){
					mEpisodeIndex = i;
				}
			}
			Log.d(TAG, "mEpisodeIndex---------------->" + mEpisodeIndex);
			String url = Constant.BASE_URL + "/joyplus/produrl?id=" + play_info.getPush_id()
					+ "&name=" + URLEncoder.encode(mProd_sub_name) 
					+ "&md5_code=" + getUmengMd5()
					+ "&mac_address=" + Utils.getMacAdd(JoyplusMediaPlayerActivity.this);
			String date_str = HttpTools.get(JoyplusMediaPlayerActivity.this, url);
			Log.d(TAG, date_str);
			try{
				JSONObject data = new JSONObject(date_str);
				boolean haserror = data.getBoolean("error");
				if(!haserror){
					String downurls = data.getString("downurl");
					List<SubURI> subList = null;
					if(data.has("subtitle")){
						Log.d(TAG, data.get("subtitle").toString());
						if(!"".equals(data.get("subtitle").toString())){
							JSONArray array_sub = data.getJSONArray("subtitle");
							subList = new ArrayList<SubURI>();
							for(int i = 0; i< array_sub.length() ; i++){
								JSONObject subObj = array_sub.getJSONObject(i);
								SubURI subInfo = new SubURI();
								subInfo.setName(subObj.getString("name"));
								subInfo.setUrl(subObj.getString("url"));
								subInfo.SubType = SUBTYPE.NETWORK;
								subList.add(subInfo);
							}
						}
						
					}
					play_info.setSubList(subList);
					String donwLoad_url_data = DesUtils.decode(Constant.DES_KEY, downurls);
					Log.d(TAG, "getPlayList--->data:" + donwLoad_url_data);
					String[] urls = donwLoad_url_data.split("\\{mType\\}");
//					List<URLS_INDEX> list = new ArrayList<URLS_INDEX>();
					playUrls.clear();
//					playUrls_flv.clear();
//					playUrls_hd.clear();
//					playUrls_hd2.clear();
//					playUrls_mp4.clear();
					for(String str : urls){
						URLS_INDEX url_index_info = new URLS_INDEX();
						String[] p = str.split("\\{m\\}");
						if(p.length<2){
							continue;
						}
						url_index_info.defination_from_server = p[0];
						url_index_info.url = p[1];
//						if("hd2".equalsIgnoreCase(p[0])){
//							playUrls_hd2.add(url_index_info);
//						}else if("hd".equalsIgnoreCase(p[0])){
//							playUrls_hd.add(url_index_info);
//						}else if("mp4".equalsIgnoreCase(p[0])){
//							playUrls_mp4.add(url_index_info);
//						}else{
//							playUrls_flv.add(url_index_info);;
//						}
						playUrls.add(url_index_info);
					}
					initFourList();
					sortPushUrls(mDefination);
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
			mHandler.sendEmptyMessage(MESSAGE_URLS_READY);
		}
		
	}
	
	class getPlayList implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				Log.d(TAG, "getPlayList----> start");
				if(play_info!=null){
					String data = DesUtils.decode(Constant.DES_KEY, play_info.getRecivedDonwLoadUrls());
					Log.d(TAG, "getPlayList--->data:" + data);
					String[] urls = data.split("\\{mType\\}");
//					List<URLS_INDEX> list = new ArrayList<URLS_INDEX>();
					playUrls.clear();
//					playUrls_flv.clear();
//					playUrls_hd.clear();
//					playUrls_hd2.clear();
//					playUrls_mp4.clear();
					for(String str : urls){
						URLS_INDEX url_index_info = new URLS_INDEX();
						String[] p = str.split("\\{m\\}");
						if(p.length<2){
							continue;
						}
						url_index_info.defination_from_server = p[0];
						url_index_info.url = p[1];
//						if("hd2".equalsIgnoreCase(p[0])){
//							playUrls_hd2.add(url_index_info);
//						}else if("hd".equalsIgnoreCase(p[0])){
//							playUrls_hd.add(url_index_info);
//						}else if("mp4".equalsIgnoreCase(p[0])){
//							playUrls_mp4.add(url_index_info);
//						}else{
//							playUrls_flv.add(url_index_info);;
//						}
						playUrls.add(url_index_info);
					}
					initFourList();
					sortPushUrls(mDefination);
					mHandler.sendEmptyMessage(MESSAGE_URLS_READY);
				}else{
					Log.d(TAG, "play_info----> = null");
					mHandler.sendEmptyMessage(MESSAGE_URLS_READY);
				}
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				mHandler.sendEmptyMessage(MESSAGE_URLS_READY);
			}
		}
		
	}
	
	private void initFourList(){
		playUrls_flv.clear();
		playUrls_hd.clear();
		playUrls_hd2.clear();
		playUrls_mp4.clear();
		for(URLS_INDEX url_index_info:playUrls){
			if("hd2".equalsIgnoreCase(url_index_info.defination_from_server)){
				playUrls_hd2.add(url_index_info);
			}else if("hd".equalsIgnoreCase(url_index_info.defination_from_server)){
				playUrls_hd.add(url_index_info);
			}else if("mp4".equalsIgnoreCase(url_index_info.defination_from_server)){
				playUrls_mp4.add(url_index_info);
			}else{
				playUrls_flv.add(url_index_info);;
			}
		}
	}
	
	private void sortPushUrls(int defination){
		
		for(URLS_INDEX url_index_info:playUrls){
			switch (defination) {
			case Constant.DEFINATION_HD2:
				if("hd2".equalsIgnoreCase(url_index_info.defination_from_server)){
					url_index_info.defination = 0;
				}else if("hd".equalsIgnoreCase(url_index_info.defination_from_server)){
					url_index_info.defination = 1;
				}else if("mp4".equalsIgnoreCase(url_index_info.defination_from_server)){
					url_index_info.defination = 2;
				}else if("flv".equalsIgnoreCase(url_index_info.defination_from_server)){
					url_index_info.defination = 3;
				}else{
					url_index_info.defination = 4;
				}
				break;
			case Constant.DEFINATION_HD:
				if("hd2".equalsIgnoreCase(url_index_info.defination_from_server)){
					url_index_info.defination = 1;
				}else if("hd".equalsIgnoreCase(url_index_info.defination_from_server)){
					url_index_info.defination = 0;
				}else if("mp4".equalsIgnoreCase(url_index_info.defination_from_server)){
					url_index_info.defination = 2;
				}else if("flv".equalsIgnoreCase(url_index_info.defination_from_server)){
					url_index_info.defination = 3;
				}else{
					url_index_info.defination = 4;
				}
				break;
			case Constant.DEFINATION_MP4:
				if("hd2".equalsIgnoreCase(url_index_info.defination_from_server)){
					url_index_info.defination = 1;
				}else if("hd".equalsIgnoreCase(url_index_info.defination_from_server)){
					url_index_info.defination = 2;
				}else if("mp4".equalsIgnoreCase(url_index_info.defination_from_server)){
					url_index_info.defination = 0;
				}else if("flv".equalsIgnoreCase(url_index_info.defination_from_server)){
					url_index_info.defination = 3;
				}else{
					url_index_info.defination = 4;
				}
				break;
			case Constant.DEFINATION_FLV:
				if("hd2".equalsIgnoreCase(url_index_info.defination_from_server)){
					url_index_info.defination = 1;
				}else if("hd".equalsIgnoreCase(url_index_info.defination_from_server)){
					url_index_info.defination = 2;
				}else if("mp4".equalsIgnoreCase(url_index_info.defination_from_server)){
					url_index_info.defination = 3;
				}else if("flv".equalsIgnoreCase(url_index_info.defination_from_server)){
					url_index_info.defination = 0;
				}else{
					url_index_info.defination = 4;
				}
				break;
			default:
				break;
			}
		}
		if(playUrls.size()>1){
			Collections.sort(playUrls, new DefinationComparatorIndex());
		}
	}
	
	class RequestNewUrl implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			isRequset +=1;	
			playUrls.clear();
			//updateXunleiurl
			String url = null;
			String url_param = play_info.getPush_url();
			if(url_param.contains(":")){
				url_param = URLEncoder.encode(url_param);
			}
			if(mProd_type==TYPE_PUSH_BT_EPISODE){
				if(isOnline){
					url = Constant.BASE_URL + "/updateJoyplusUrl/retry?url=" + url_param
							+ "&id=" + play_info.getPush_id()
							+ "&md5_code=" + getUmengMd5()
							+ "&name=" + URLEncoder.encode(mProd_sub_name);
				}else{
					if(isRequset==1){
						url = Constant.BASE_URL + "/updateJoyplusUrl?url=" + url_param
								+ "&id=" + play_info.getPush_id()
								+ "&md5_code=" + getUmengMd5()
								+ "&name=" + URLEncoder.encode(mProd_sub_name);
					}else{
						url = Constant.BASE_URL + "/updateJoyplusUrl/retry?url=" + url_param
								+ "&id=" + play_info.getPush_id()
								+ "&md5_code=" + getUmengMd5()
								+ "&name=" + URLEncoder.encode(mProd_sub_name);
					}
				}
			}else{
				if(isOnline){
					url = Constant.BASE_URL + "/updateJoyplusUrl/retry?url=" + url_param
							+ "&id=" + play_info.getPush_id()
							+ "&md5_code=" + getUmengMd5();
				}else{
					if(isRequset==1){
						url = Constant.BASE_URL + "/updateJoyplusUrl?url=" + url_param
								+ "&id=" + play_info.getPush_id()
								+ "&md5_code=" + getUmengMd5();
					}else{
						url = Constant.BASE_URL + "/updateJoyplusUrl/retry?url=" + url_param
								+ "&id=" + play_info.getPush_id()
								+ "&md5_code=" + getUmengMd5();
					}
				}
				
			}
			Log.d(TAG, "url--->" + url);
			String response = HttpTools.get(JoyplusMediaPlayerActivity.this, url);
			Log.d(TAG, "response--->" + response);
			try {
				JSONObject data = new JSONObject(response);
				String reciveData = data.getString("downurl");
				if(play_info!=null){
					play_info.setRecivedDonwLoadUrls(reciveData);
				}
				String downLoadurls = DesUtils.decode(Constant.DES_KEY, reciveData);
				Log.d(TAG, "downLoadurls--->" + downLoadurls);
				List<SubURI> subList = null;
				if(data.has("subtitle")){
					Log.d(TAG, data.get("subtitle").toString());
					if(!"".equals(data.get("subtitle").toString())){
						JSONArray array_sub = data.getJSONArray("subtitle");
						subList = new ArrayList<SubURI>();
						for(int i = 0; i< array_sub.length() ; i++){
							JSONObject subObj = array_sub.getJSONObject(i);
							SubURI subInfo = new SubURI();
							subInfo.setName(subObj.getString("name"));
							subInfo.setUrl(subObj.getString("url"));
							subInfo.SubType = SUBTYPE.NETWORK;
							subList.add(subInfo);
						}
					}
					
				}
				play_info.setSubList(subList);
				String[] urls = downLoadurls.split("\\{mType\\}");
//				List<URLS_INDEX> list = new ArrayList<URLS_INDEX>();
				
//				playUrls_flv.clear();
//				playUrls_hd.clear();
//				playUrls_hd2.clear();
//				playUrls_mp4.clear();
				for(String str : urls){
					URLS_INDEX url_index_info = new URLS_INDEX();
					String[] p = str.split("\\{m\\}");
					if(p.length<2){
						continue;
					}
					url_index_info.defination_from_server = p[0];
					url_index_info.url = p[1];
//					if("hd2".equalsIgnoreCase(p[0])){
//						playUrls_hd2.add(url_index_info);
//					}else if("hd".equalsIgnoreCase(p[0])){
//						playUrls_hd.add(url_index_info);
//					}else if("mp4".equalsIgnoreCase(p[0])){
//						playUrls_mp4.add(url_index_info);
//					}else{
//						playUrls_flv.add(url_index_info);;
//					}
					playUrls.add(url_index_info);
				}
				initFourList();
				sortPushUrls(play_info.getDefination());
				mHandler.sendEmptyMessage(MESSAGE_URLS_READY);
//				currentPlayUrl = Utils.getUrl(downLoadurls);
//				currentPlayUrl = Utils.getRedirectUrl(currentPlayUrl);
//				mHandler.sendEmptyMessage(MESSAGE_PALY_URL_OK);
			} catch (Exception e) {
				// TODO: handle exception
//				mHandler.sendEmptyMessage(MESSAGE_URL_NEXT);
				e.printStackTrace();
				mHandler.sendEmptyMessage(MESSAGE_URLS_READY);
			}
		}
	}
}
