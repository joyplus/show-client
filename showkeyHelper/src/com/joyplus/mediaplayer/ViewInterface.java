package com.joyplus.mediaplayer;

public interface ViewInterface {

	  /*Get the width of the view object*/
	 int getWidth();
	 
	 /*Get the heigtht of the view object*/
	 int getHeight();
	 
	 /*Reset the view (for next presentation)*/
	 void reset();
	 
	 /*set the visibitity of the view object*/
	 void setVisibility(boolean visible);
	 
	 /*get videoview*/
	 VideoViewInterface getVideoViewInterface();
	 
	 /*get target MediaInfo*/
	 MediaInfo getTargetMediaInfo();
}
