package com.joyplus;



import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joyplus.JoyplusMediaPlayerActivity.URLTYPE;
import com.joyplus.tvhelper.R;

public class JoyplusMediaPlayerMiddleControlLoading extends LinearLayout implements JoyplusMediaPlayerInterface{
	private Context mContext;
	private long mStartRX = 0;//
	private long rxByteslast = 0;
	public  long mLoadingPreparedPercent = -1;//
	
	private TextView info;
	private TextView lasttime;
	private TextView from;
	public JoyplusMediaPlayerMiddleControlLoading(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
	}
	public JoyplusMediaPlayerMiddleControlLoading(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context; 
	}
	protected void onFinishInflate() {
		info     = (TextView) this.findViewById(R.id.MiddleControlLoading_info);
		lasttime = (TextView) this.findViewById(R.id.MiddleControlLoading_lasttime);
		from     = (TextView) this.findViewById(R.id.MiddleControlLoading_from);
		StartTrafficStates();
	}
	private void UpdateInfo(int speed,int finished){
		if(speed<0)speed = 0;
		if(finished<-1)finished=0;
		else if(finished>95)finished = 95;
		if(finished >= 0)info.setText(mContext.getApplicationContext().getString(R.string.meidaplayer_loading_string,speed,finished));
		if(JoyplusMediaPlayerActivity.mInfo.mLastTime>0){
			lasttime.setVisibility(View.VISIBLE);
			lasttime.setText(mContext.getApplicationContext().getString(R.string.meidaplayer_loading_string_lasttime)+getTimeString(JoyplusMediaPlayerActivity.mInfo.mLastTime));
		}else lasttime.setVisibility(View.GONE);
		if(JoyplusMediaPlayerActivity.mInfo.mFrom != null && !"".equals(JoyplusMediaPlayerActivity.mInfo.mFrom)){
			from.setVisibility(View.VISIBLE);
			from.setText(JoyplusMediaPlayerActivity.mInfo.mFrom);
		}else from.setVisibility(View.GONE);
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
	private Handler mHandler = new Handler();
	private void StartTrafficStates(){
		mStartRX = TrafficStats.getTotalRxBytes();// ��ȡ�����ٶ�
		if (mStartRX == TrafficStats.UNSUPPORTED) {
			info.setText(mContext.getApplicationContext().getString(R.string.meidaplayer_loading_string_loading));
		} else {
			rxByteslast = 0;
			mLoadingPreparedPercent = 0;
			mHandler.removeCallbacks(UpdateTrafficStats);
			mHandler.postDelayed(UpdateTrafficStats, 500);
		}
	}
	private Runnable UpdateTrafficStats = new Runnable(){
		long beginTimeMillis, timeTakenMillis, m_bitrate;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			long rxBytes = TrafficStats.getTotalRxBytes() - mStartRX;
			timeTakenMillis = System.currentTimeMillis() - beginTimeMillis;
			beginTimeMillis = System.currentTimeMillis();
			if(timeTakenMillis!=0){
				m_bitrate = (rxBytes - rxByteslast) / timeTakenMillis;
				rxByteslast = rxBytes;
				mLoadingPreparedPercent+=m_bitrate;
				UpdateInfo((int) m_bitrate,(int)mLoadingPreparedPercent/100);
			}	
			mHandler.removeCallbacksAndMessages(null);
			mHandler.postDelayed(UpdateTrafficStats, 500);
		}
	};
	@Override
	public boolean JoyplusdispatchMessage(Message msg) {
		// TODO Auto-generated method stub
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
		if(visible){
			this.setVisibility(View.VISIBLE);
			if(JoyplusMediaPlayerActivity.mInfo.mType == URLTYPE.NETWORK){
				StartTrafficStates();
				UpdateInfo(-1,(int)mLoadingPreparedPercent);
			}
		}
		if(!visible){
			mHandler.removeCallbacksAndMessages(null);
			mLoadingPreparedPercent = 0;
		}
	}
	@Override
	public int JoyplusgetLayout() {
		// TODO Auto-generated method stub
		return JoyplusMediaPlayerMiddleControl.LAYOUT_LOADING;
	}
	@Override
	public boolean JoyplusonKeyLongPress(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}
