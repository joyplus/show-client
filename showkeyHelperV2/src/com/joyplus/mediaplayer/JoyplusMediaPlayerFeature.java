package com.joyplus.mediaplayer;

import android.content.Context;

public class JoyplusMediaPlayerFeature {

	  /*feature: when player report complement,but it not really.
	   *       eg: when report complements,but current time is not small then totle time
	   *           (eg: 00:12:23 -- 01:12:23).Then we report MediaErrorComplement.
	   *@see : {JoyplusMediaPlayerListener#MediaErrorCompletion(MediaInfo info);}*/
	  public final static boolean FEATURE_REPLAY_ERRORURL = true;
	  
	  
	  
	 
	  
}
