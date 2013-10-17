package com.joyplus.mediaplayer;

public interface JoyplusMediaPlayerListener {

	/*send MediaPlayer info current
	 * @link{# MediaInfo}*/
	public void MediaInfo(MediaInfo info);
	
	/*send MediaPlayer Complements*/
	public void MediaCompletion();
	
	/*send MediaPlayer error*/
	public void ErrorInfo();
    
	/*receiver error commend*/
	public void NoProcess(String commend);
}
