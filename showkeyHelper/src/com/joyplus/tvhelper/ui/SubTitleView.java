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
	private static final int MESSAGE_SUBTITLE_BEGAIN_SHOW =  MESSAGE_SUBTITLE_HIDEN + 1;
	private static final int MESSAGE_SUBTITLE_END_HIDEN = MESSAGE_SUBTITLE_BEGAIN_SHOW + 1;
	private static final int MESSAGE_SUBTITLE_BEGAIN_CACHE = MESSAGE_SUBTITLE_END_HIDEN + 1;
	private static final int MESSAGE_SUBTITLE_END_CACHE = MESSAGE_SUBTITLE_BEGAIN_CACHE + 1;
	
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
			case MESSAGE_SUBTITLE_BEGAIN_CACHE:
				messageBegainCache();
				break;
			case MESSAGE_SUBTITLE_BEGAIN_SHOW:
				messageBegainShow();
				break;
			case MESSAGE_SUBTITLE_END_CACHE:
				messageEndCache();
				break;
			case MESSAGE_SUBTITLE_END_HIDEN:
				messageEndHiden();
				break;
			default:
				break;
			}
		}
	};
	
	private void messageBegainShow(){
		Log.i(TAG, "messageBegainShow-->");
		if(getTag() == null ||!(getTag() instanceof Element)) return;
		Element element_show = (Element) getTag();
		long currentPositionShow = getCurrentTime();
		Element preElement_show = getElement(currentPositionShow);					
		//在字幕的显示时间段内
		if(!element_show.getText().equals(getText())){
			if(element_show.getStartTime().getTime() < currentPositionShow + SEEKBAR_REFRESH_TIME/2
					&& element_show.getStartTime().getTime() > currentPositionShow - SEEKBAR_REFRESH_TIME/2){
				setText(element_show.getText());
				setTag(element_show);
			}
		}
		Log.i(TAG, "preElement_show--->" + preElement_show);
		if(preElement_show != null){
			if(preElement_show.getStartTime().getTime() - currentPositionShow > SUBTITLE_DELAY_TIME_MAX){
				mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_BEGAIN_CACHE, SUBTITLE_DELAY_TIME_MAX);
			} else {				
				mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_BEGAIN_SHOW, preElement_show.getStartTime().getTime() - currentPositionShow);
			}
		}
		
	}
	
	private void messageEndHiden(){
		Log.i(TAG, "messageEndHiden-->");
		if(getTag() == null ||!(getTag() instanceof Element)) return;
		Element elementEH = (Element) getTag();
		long currentPositionEH = getCurrentTime();
		Element preElementEH = getElement(currentPositionEH);	
		if(elementEH.getEndTime().getTime() > currentPositionEH - SEEKBAR_REFRESH_TIME/2){
			setText("");
		}
		Log.i(TAG, "preElementEH--->" + preElementEH);
		if(preElementEH != null){
			if(preElementEH.getEndTime().getTime() - currentPositionEH > SUBTITLE_DELAY_TIME_MAX){
				mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_END_CACHE, SUBTITLE_DELAY_TIME_MAX);
			}else {
				mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_END_HIDEN, preElementEH.getEndTime().getTime() - currentPositionEH);
			}
		}
	}
	
	private void messageEndCache(){
		Log.i(TAG, "messageEndCache-->");
		if(getTag() == null ||!(getTag() instanceof Element)) return;
		long currPositionEC = getCurrentTime();
		Element preElementEC = getElement(currPositionEC);
		if(preElementEC == null) return;
		Element elementEC = (Element) getTag();
		if(elementEC.getEndTime().getTime() == 
				preElementEC.getEndTime().getTime()){
			if(elementEC.getEndTime().getTime() - currPositionEC > SUBTITLE_DELAY_TIME_MAX){
				mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_END_CACHE, SUBTITLE_DELAY_TIME_MAX);
			}else {
				mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_END_HIDEN, elementEC.getEndTime().getTime() - currPositionEC);
			}
		}else{
			mHandler.removeMessages(MESSAGE_SUBTITLE_END_HIDEN);
			mHandler.removeMessages(MESSAGE_SUBTITLE_END_CACHE);
		}
	}
	
	private void messageBegainCache(){
		Log.i(TAG, "messageBegainCache-->");
		if(getTag() == null ||!(getTag() instanceof Element)) return;
		long currPositionBC = getCurrentTime();
		Element preElementBC = getElement(currPositionBC);
		if(preElementBC == null) return;
		Element elementBC = (Element) getTag();
		if(elementBC.getStartTime().getTime() != 
				preElementBC.getStartTime().getTime()){
			elementBC = preElementBC;
			setTag(elementBC);
			mHandler.removeMessages(MESSAGE_SUBTITLE_BEGAIN_SHOW);
			mHandler.removeMessages(MESSAGE_SUBTITLE_END_HIDEN);
			mHandler.removeMessages(MESSAGE_SUBTITLE_END_CACHE);
			mHandler.removeMessages(MESSAGE_SUBTITLE_BEGAIN_CACHE);
		}
		if(elementBC.getStartTime().getTime() - currPositionBC > SUBTITLE_DELAY_TIME_MAX){
			mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_BEGAIN_CACHE, SUBTITLE_DELAY_TIME_MAX);
		} else {
			mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_BEGAIN_SHOW, elementBC.getStartTime().getTime() - currPositionBC);
		}
	}
	
	private void messageDisplay(){
		Log.i(TAG, "messageDisplay-->");
		if(getSubManager().CheckSubAviable()){
			long currentPosition = getCurrentTime();
			setVisibility(VISIBLE);
			Element preElement = getElement(currentPosition);
			if(preElement != null){
				setTag(preElement);
				if(preElement.getStartTime().getTime() - currentPosition > SUBTITLE_DELAY_TIME_MAX){
					mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_BEGAIN_CACHE, SUBTITLE_DELAY_TIME_MAX);
				} else {				
					mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_BEGAIN_SHOW, preElement.getStartTime().getTime() - currentPosition);
				}
				if(preElement.getEndTime().getTime() - currentPosition > SUBTITLE_DELAY_TIME_MAX) {
					mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_END_CACHE, SUBTITLE_DELAY_TIME_MAX);
				} else{
					mHandler.sendEmptyMessageDelayed(MESSAGE_SUBTITLE_END_HIDEN, preElement.getEndTime().getTime() - currentPosition);
				}
			}
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
