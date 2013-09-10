package com.joyplus.tvhelper.ui;



import com.joyplus.Sub.Element;
import com.joyplus.Sub.JoyplusSubManager;
import com.joyplus.tvhelper.VideoPlayerJPActivity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class SubTitleView extends TextView {
	private static final String TAG = "SubTitleView";
	
	private static final int MESSAGE_SUBTITLE_BEGAIN_SHOW =  0;
	private static final int MESSAGE_SUBTITLE_END_HIDEN = MESSAGE_SUBTITLE_BEGAIN_SHOW + 1;
	
	private static final int SEEKBAR_REFRESH_TIME = 200;//refresh time
	private static final int SUBTITLE_DELAY_TIME_MAX = 1000;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MESSAGE_SUBTITLE_BEGAIN_SHOW:
				Element element_show = (Element) msg.obj;
				if(element_show != null){
					long currentPositionShow = getCurrentTime();
					Element preElement_show = getElement(currentPositionShow);					
					//在字幕的显示时间段内
					if(!element_show.getText().equals(getText())){
						if(element_show.getStartTime().getTime() < currentPositionShow + SEEKBAR_REFRESH_TIME/2
								&& element_show.getStartTime().getTime() > currentPositionShow - SEEKBAR_REFRESH_TIME/2){
							setText(element_show.getText());
							setTag(element_show.getEndTime().getTime());
						}
					}
					if(element_show.getEndTime().getTime() < currentPositionShow){
						setText("");
						setTag(-1L);
						mHandler.removeMessages(MESSAGE_SUBTITLE_END_HIDEN);
						if(preElement_show != null){
							Message messageHiden = mHandler.obtainMessage(MESSAGE_SUBTITLE_END_HIDEN, preElement_show);
							mHandler.sendMessageDelayed(messageHiden, preElement_show.getEndTime().getTime() - currentPositionShow);
						}
					}
					
					long tagEndTime = -1L;
					try {
						if(getTag() != null)
						tagEndTime = (Long) getTag();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(!element_show.getText().equals(getText()) && tagEndTime != -1
							&& tagEndTime < currentPositionShow){
						setText("");
						setTag(-1L);
					}
					if(preElement_show != null){
						Message messageShow = mHandler.obtainMessage(MESSAGE_SUBTITLE_BEGAIN_SHOW, preElement_show);
						if(preElement_show.getStartTime().getTime() - currentPositionShow > SUBTITLE_DELAY_TIME_MAX){
							mHandler.sendMessageDelayed(messageShow, SUBTITLE_DELAY_TIME_MAX);
						}else {
							mHandler.sendMessageDelayed(messageShow, preElement_show.getStartTime().getTime() - currentPositionShow);
						}
					}
				}
				break;
			case MESSAGE_SUBTITLE_END_HIDEN:
				Element element_end = (Element) msg.obj;
				if(element_end != null){
					long currentPositionShow = getCurrentTime();
					Element preElement_show = getElement(currentPositionShow);
					if(element_end.getEndTime().getTime() > currentPositionShow - SEEKBAR_REFRESH_TIME/2){
						setText("");
						setTag(-1L);
					}
					if(preElement_show != null){
						Message messageHiden = mHandler.obtainMessage(MESSAGE_SUBTITLE_END_HIDEN, preElement_show);
						mHandler.sendMessageDelayed(messageHiden, preElement_show.getEndTime().getTime() - currentPositionShow);
					}
				}
				break;
			default:
				break;
			}
		}
		
	};
	
	//private List<Element> subTitleList = new ArrayList<Element>();
	private VideoPlayerJPActivity   mActivity;
	private JoyplusSubManager mJoyplusSubManager;
	public void Init(VideoPlayerJPActivity activity){
		if(activity == null)return ;
		mActivity = activity;
	}
	
	private long getCurrentTime(){
		if(mActivity == null) return 0;
		return mActivity.getPlayerCurrentPosition();
	}
	public void setSubManager(JoyplusSubManager joyplusSubManager){
		mJoyplusSubManager = joyplusSubManager;
	}
	private Element getElement(long time){
		if(mJoyplusSubManager == null) return null;
		return mJoyplusSubManager.getElement(time);
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
		if(mJoyplusSubManager != null && mJoyplusSubManager.CheckSubAviable()){
			setVisibility(VISIBLE);
			long currentPosition = getCurrentTime();
			Element preElement = getElement(currentPosition);
			Log.i(TAG, "currentPosition--->" + currentPosition);
			if(preElement != null){
				Message messageShow = mHandler.obtainMessage(MESSAGE_SUBTITLE_BEGAIN_SHOW, preElement);
				Message messageHiden = mHandler.obtainMessage(MESSAGE_SUBTITLE_END_HIDEN, preElement);
				if(preElement.getStartTime().getTime() - currentPosition > SUBTITLE_DELAY_TIME_MAX){
					mHandler.sendMessageDelayed(messageShow, SUBTITLE_DELAY_TIME_MAX);
				}else {				
					mHandler.sendMessageDelayed(messageShow, preElement.getStartTime().getTime() - currentPosition);
				}
				mHandler.sendMessageDelayed(messageHiden, preElement.getEndTime().getTime() - currentPosition);
			}
		}
	}
	
	public void hiddenSubtitle(){		
		mHandler.removeCallbacksAndMessages(null);
		setVisibility(INVISIBLE);
	}
	
}
