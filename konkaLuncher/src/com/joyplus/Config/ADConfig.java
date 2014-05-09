package com.joyplus.Config;

public final class ADConfig {

	public static final boolean DEBUG = true;
	
	public static final String BD_ID = "9a51d0c16fa83008eba3001aa892b901";
	public static final String BD_PATH_SDCARD = "/mnt/sdcard/Joyplus_video"; 
	public static final String BD_PATH_CACHE = "/data/misc/konka/advert/Jas/"+BD_ID+"/";
	public static final String BD_PATH = DEBUG?BD_PATH_SDCARD:BD_PATH_CACHE;
	public static final String html5BaseUrl = "http://download.joyplus.tv/app/item.html?s=" + BD_ID;
	public static final String BaseUrl      = "http://advapi.joyplus.tv/advapi/v1/topic/get?s="+ BD_ID;
	
}
