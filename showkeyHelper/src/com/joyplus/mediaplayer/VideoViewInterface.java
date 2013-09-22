package com.joyplus.mediaplayer;

public interface VideoViewInterface extends ViewInterface{
     
	   /*interface of event*/
	   static final int MIN_PLAYER_EVENT = 1;
	   static final int MAX_PLAYER_EVENT = 100;
	   /*define for change MediaPlayer decode type*/
	   public enum DecodeType{
		   Decode_HW   (1),
		   Decode_SW   (2);
		   private int TYPE;
		   DecodeType(int type){
			   TYPE = type;
		   }
		   public int toValue(){
			   return TYPE;
		   }
	   }
	  
	   /*define for MediaPlayer State*/
	   enum STATE{
		   MEDIA_STATE_UNKNOW      (0), //State unknow this will switch to IDLE
		   MEDIA_STATE_IDLE        (1), //State IDLE,means player can't use,pls init it first,it can be switch to setsource,idle
		   MEDIA_STATE_SETVIDEOURI (2), //State SetResource,means player nend to set resource,it can be switch to setvideouri,idle
		   MEDIA_STATE_INITED      (3), //State Inited,means player have been inited,it can be switch to loading. playing or idle
		   MEDIA_STATE_LOADING     (4), //State loading,means player is loading resource,it can be switch to loaded or inited.
		   MEDIA_STATE_LOADED      (5), //State loaded,means player has load over ,it can be switch to playing or inited.
		   MEDIA_STATE_PLAYING     (6), //State playing,means player has be playing,it can be switch to puse ,loading or inited
		   MEDIA_STATE_PUSE        (7), //State puse,means player has be pused,it can switch to playing,loading or inited.
		   MEDIA_STATE_FINISH      (8), //State finish,means player has be finish play,it can switch to idle
		   MEDIA_STATE_MAX         (8); //State MAX,it not a state only a flog use to jude
		   private int mState;
		   STATE(int state){
			   this.mState = state;
		   }
		   public int toInt(){
			   return mState;
		   }
	    }
	    
	    MediaInfo getMediaInfo();
	    void SetState(STATE state);
	    void SetVideoPaths(String video);
	    void SetVideoVisibility(boolean visible);
	    void StartVideo();
	    void StopVideo();
	    void PauseVideo();
	    void SeekVideo(int seekTo);
	    boolean IsPlaying();
	    void SetINFO(int info);
}
