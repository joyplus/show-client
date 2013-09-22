package com.joyplus.mediaplayer;


import com.joyplus.mediaplayer.VideoViewInterface.STATE;

import android.os.Parcel;
import android.os.Parcelable;

/*define by Jas@20130723 for Load MediaPlayer current info
 * */
public class MediaInfo implements Parcelable{
    
	/*add by Jas@20130723 for MediaPlayer cueernt state
	 * @see #Player.State*/
	private STATE mState;
	public void setState(STATE mediaStateSetvideouri){
		this.mState = mediaStateSetvideouri;
	}
	public STATE getState(){
		return this.mState;
	}
	/*add by Jas@20130723 for current time*/
	private long CurrentTime;
	public void setCurrentTime(long l){
		this.CurrentTime = l;
	}
	public long getCurrentTime(){
		return this.CurrentTime;
	}
	/*add by Jas@20130723 for current totle time*/
	private long TotleTime;
	public void setTotleTime(long l){
		this.TotleTime = l;
	}
	public long getTotleTime(){
		return this.TotleTime;
	}
	/*add by Jas for get current Type*/
	private int TYPE;
	public void setType(int type){
		this.TYPE = type;
	}
	public int getType(){
	    return this.TYPE;	
	}
	/*add by Jas for restore url*/
	private String Path;
	
	public void setPath(String path){
		this.Path = path;
	}
	public String getPath(){
		return this.Path;
	}
	/*add by Jas for VideoView info*/
	private int INFO;
	public void setINFO(int info){
		this.INFO = info;
	}
	public int getINFO(){
	    return this.INFO;	
	} 
	public MediaInfo(){
		this.mState      = STATE.MEDIA_STATE_IDLE;
		this.CurrentTime = 0;
		this.TotleTime   = 0;
		this.TYPE        = JoyplusMediaPlayerManager.TYPE_UNKNOW;
		this.Path        = null;
		this.INFO        = 0;
	}
	public MediaInfo(MediaInfo info){
		if(info != null){
			this.mState      = info.mState;
			this.CurrentTime = info.CurrentTime;
			this.TotleTime   = info.TotleTime;
			this.TYPE        = info.TYPE;
			this.Path        = info.Path;
			this.INFO        = info.INFO;
		}
	}
	public MediaInfo CreateMediaInfo(){
		return new MediaInfo(this);
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeInt(this.mState.toInt());
		arg0.writeLong(CurrentTime);
		arg0.writeLong(TotleTime);
		arg0.writeString(Path);
		arg0.writeInt(INFO);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
        sb.append("MediaInfo{ STATE: ").append(mState.toInt()).
           append(", CurrentTime: ").append(CurrentTime).
           append(", TotleTime: ").append(TotleTime ).
           append(", Path: ").append(Path).
           append(", INFO: ").append(INFO).
           append("} ");
        return sb.toString();
	}

}
