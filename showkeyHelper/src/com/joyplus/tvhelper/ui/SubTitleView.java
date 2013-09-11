package com.joyplus.tvhelper.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.joyplus.Sub.Element;
import com.joyplus.Sub.JoyplusSubManager;
import com.joyplus.manager.JoyplusMediaPlayerManager;
import com.joyplus.tvhelper.VideoPlayerJPActivity;

public class SubTitleView extends TextView {
	private static final String TAG = "SubTitleView";
	
	private static final int SEEKBAR_REFRESH_TIME = 200;//refresh time
	private static final int SUBTITLE_DELAY_TIME_MAX = 500;
	
	private static final int MESSAGE_SUBTITLE_DISPLAY = 0;
	private static final int MESSAGE_SUBTITLE_HIDEN = MESSAGE_SUBTITLE_DISPLAY + 1;
	private static final int MESSAGE_SUBTITLE_START = MESSAGE_SUBTITLE_HIDEN + 1;
	private static final int MESSAGE_SUBTITLE_BEGAIN_SHOW =  MESSAGE_SUBTITLE_START + 1;
	private static final int MESSAGE_SUBTITLE_END_HIDEN = MESSAGE_SUBTITLE_BEGAIN_SHOW + 1;
	private static final int MESSAGE_SUBTITLE_SHOW_CACHE = MESSAGE_SUBTITLE_END_HIDEN + 1;
	private static final int MESSAGE_SUBTITLE_HIDEN_CACHE = MESSAGE_SUBTITLE_SHOW_CACHE + 1;
//	private static final int MESSAGE_SUBTITLE_BEGAIN_SHOW =  MESSAGE_SUBTITLE_HIDEN + 1;
//	private static final int MESSAGE_SUBTITLE_END_HIDEN = MESSAGE_SUBTITLE_BEGAIN_SHOW + 1;
//	private static final int MESSAGE_SUBTITLE_BEGAIN_CACHE = MESSAGE_SUBTITLE_END_HIDEN + 1;
//	private static final int MESSAGE_SUBTITLE_END_CACHE = MESSAGE_SUBTITLE_BEGAIN_CACHE + 1;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MESSAGE_SUBTITLE_DISPLAY:
				messageDisplay();
				break;
			case MESSAGE_SUBTITLE_HIDEN:
				setVisibility(INVISIBLE);
				break;
			case MESSAGE_SUBTITLE_START:
				startSubtitle();
				break;
			case MESSAGE_SUBTITLE_BEGAIN_SHOW:
				showStartSubtitle();
				break;
			case MESSAGE_SUBTITLE_END_HIDEN:
				startSubtitle();
				break;
			case MESSAGE_SUBTITLE_SHOW_CACHE:
				cacheShowSubtitle();
				break;
			case MESSAGE_SUBTITLE_HIDEN_CACHE:
				cacheHidenSubtitle();
				break;
			default:
				break;
			}
		}
	};
	
	private void cacheHidenSubtitle(){
		if(getTag() == null || !(getTag() instanceof Element)) return;
		Element currElement = (Element) getTag();
		long currTime = getCurrentTime();
		if(currTime >= currElement.getEndTime().getTime() + SUBTITLE_DELAY_TIME_MAX / 10){
			setTag("");
			mHandler.sendEmptyMessage(MESSAGE_SUBTITLE_START);
		}else {
			if(currElement.getEndTime().getTime() - currTime > SUBTITLE_DELAY_TIME_MAX){
				
				mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_HIDEN_CACHE, SUBTITLE_DELAY_TIME_MAX);
			}else {
				mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_END_HIDEN, currElement.getEndTime().getTime() - currTime);
			}
		}
	}
	
	private void cacheShowSubtitle(){
		if(getTag() == null || !(getTag() instanceof Element)) return;
		Element currElement = (Element) getTag();
		long currTime = getCurrentTime();
		if(currTime >= currElement.getStartTime().getTime() + SUBTITLE_DELAY_TIME_MAX / 10){
			mHandler.removeCallbacksAndMessages(null);
			mHandler.sendEmptyMessage(MESSAGE_SUBTITLE_START);
		}else {
			if(currElement.getStartTime().getTime() - currTime > SUBTITLE_DELAY_TIME_MAX){
				mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_SHOW_CACHE, SUBTITLE_DELAY_TIME_MAX);
			}else {
				mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_BEGAIN_SHOW, currElement.getStartTime().getTime() - currTime);
			}
		}
	}
	
	private void showStartSubtitle(){
		if(getTag() == null || !(getTag() instanceof Element)) return;
		Element elementShow = (Element) getTag();
		setText(elementShow.getText());
		long tempShowTime = getCurrentTime();
		if(elementShow.getEndTime().getTime() - tempShowTime > SUBTITLE_DELAY_TIME_MAX){
			
			mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_HIDEN_CACHE, SUBTITLE_DELAY_TIME_MAX);
		}else {
			mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_END_HIDEN, elementShow.getEndTime().getTime() - tempShowTime);
		}
	}
	
	private void startSubtitle(){
		long currentPosition = getCurrentTime();
		Element preElement = getElement(currentPosition);
		if(preElement != null){
			setTag(preElement);
			if(preElement.getStartTime().getTime() - currentPosition > SUBTITLE_DELAY_TIME_MAX){
				mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_SHOW_CACHE, SUBTITLE_DELAY_TIME_MAX);
			}else {
				mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_BEGAIN_SHOW, preElement.getStartTime().getTime() - currentPosition);
			}
		}
	}
	
	private void messageDisplay(){
		Log.i(TAG, "messageDisplay-->");
		if(getSubManager().CheckSubAviable()){
			setVisibility(VISIBLE);
			mHandler.sendEmptyMessage(MESSAGE_SUBTITLE_START);
		}
	}
	
	private VideoPlayerJPActivity   mActivity;
	public void Init(VideoPlayerJPActivity activity){
		if(activity == null)return ;
		mActivity = activity;
	}
	
	private long getCurrentTime(){
		if(mActivity == null) return 0;
		return mActivity.getPlayerCurrentPosition();
	}
	private JoyplusSubManager getSubManager(){
		return JoyplusMediaPlayerManager.getInstance().getSubManager();
	}
	private Element getElement(long time){
		return getSubManager().getElement(time);
	}
	
	public SubTitleView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView();
	}

	public SubTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView();
	}

	public SubTitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initView();
	}
	
	private void initView(){
		setVisibility(INVISIBLE);
		mHandler.removeCallbacksAndMessages(null);
	}
	
	public void displaySubtitle(){
		mHandler.removeCallbacksAndMessages(null);
		Log.i(TAG, "displaySubtitle--->" + getSubManager().CheckSubAviable());
		mHandler.sendEmptyMessage(MESSAGE_SUBTITLE_DISPLAY);
	}
	
	public void hiddenSubtitle(){		
		mHandler.removeCallbacksAndMessages(null);
		mHandler.sendEmptyMessage(MESSAGE_SUBTITLE_HIDEN);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		mHandler.removeCallbacksAndMessages(null);
		super.onDetachedFromWindow();
	}
	
}
