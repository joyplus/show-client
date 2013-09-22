package com.joyplus.mediaplayer;

import java.util.ArrayList;
import java.util.List;

import com.joyplus.tvhelper.utils.Log;

public class JoyplusMediaPlayerStateTrack {

	private boolean Debug = false;
	private String  TAG   = "JoyplusMediaPlayerStateTrack";
	
	
	private List<JoyplusMediaPlayerListener> mListenerList=new ArrayList<JoyplusMediaPlayerListener>();
	
	public void registerListener(JoyplusMediaPlayerListener listener){
		if(listener != null){
			synchronized(mListenerList){
			   mListenerList.add(listener);
			}
		}
	}
	public void unregisterListener(JoyplusMediaPlayerListener listener){
		if(listener != null){
			synchronized(mListenerList){
				mListenerList.remove(listener);
			}
		}
	}
	public void unregisterAllListener(){
		synchronized(mListenerList){
			mListenerList = new ArrayList<JoyplusMediaPlayerListener>();
		}
	}
	
	public void notifyMediaInfo(final MediaInfo info){
		if(Debug)Log.d(TAG,"notifyMediaInfo("+info.toString()+")");
		synchronized(mListenerList){
		   new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					for(JoyplusMediaPlayerListener listener:mListenerList){
						 listener.MediaInfo(info);
					}
				} 
		   }.run();
		}
	}
	public void notifyMediaCompletion(){
		if(Debug)Log.d(TAG,"notifyMediaCompletion()");
		synchronized(mListenerList){
			   new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						for(JoyplusMediaPlayerListener listener:mListenerList){
							 listener.MediaCompletion();
						}
					} 
			   }.run();
		}
	}
	public void notifyMediaError(){
		if(Debug)Log.d(TAG,"notifyMediaError()");
		synchronized(mListenerList){
			   new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						for(JoyplusMediaPlayerListener listener:mListenerList){
							 listener.ErrorInfo();
						}
					} 
			   }.run();
		}
	}
	public void NoProcessCommend(final String commend){
		if(Debug)Log.d(TAG,"NoProcessCommend("+commend+")");
		synchronized(mListenerList){
			   new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						for(JoyplusMediaPlayerListener listener:mListenerList){
							 listener.NoProcess(commend);
						}
					} 
			   }.run();
	   }
	}
}
