package com.joyplus.tvhelper.db;

public class DBConstant {
	
	public static final String TABLE_APK_INFO								= "apk_download_info";
	public static final String TABLE_MOVIE_INFO								= "movie_download_info";
	public static final String TABLE_PLAY_INFO								= "movie_play_history";
//	public static final String TABLE_BAIDU_PLAY_INFO						= "movie_baidu_play_history";

	public static final String KEY_ID 										= "_id";
	
	public static final String KEY_APK_INFO_NAME 							= "name";
	public static final String KEY_APK_INFO_PUSH_ID 						= "push_id";
	public static final String KEY_APK_INFO_DOWNLOAD_STATE 					= "download_state";
	public static final String KEY_APK_INFO_FILE_PATH 						= "file_path";
	public static final String KEY_APK_INFO_DOWNLOADUUID 					= "downloaduuid";
	public static final String KEY_APK_INFO_ISUSER 							= "isuser";
	
	
	public static final String KEY_MOVIE_DOWNLOAD_INFO_NAME 				= "name";
	public static final String KEY_MOVIE_DOWNLOAD_INFO_PUSH_ID 				= "push_id";
	public static final String KEY_MOVIE_DOWNLOAD_INFO_DOWNLOAD_STATE 		= "download_state";
	public static final String KEY_MOVIE_DOWNLOAD_INFO_PUSH_URL 			= "push_url";
	public static final String KEY_MOVIE_DOWNLOAD_INFO_FILE_PATH 			= "file_path";
	public static final String KEY_MOVIE_DOWNLOAD_INFO_DOWNLOADUUID 		= "downloaduuid";
	
	
	public static final String KEY_PLAY_INFO_NAME 							= "name";
	public static final String KEY_PLAY_INFO_TYPE 							= "type";
	public static final String KEY_PLAY_INFO_PUSH_ID 						= "push_id";
	public static final String KEY_PLAY_INFO_PUSH_URL 						= "push_url";
	public static final String KEY_PLAY_INFO_PLAY_BACK_TIME 				= "play_back_time";
	public static final String KEY_PLAY_INFO_FILE_PATH 						= "file_path";//以前存储本地播放路径，一直没用 现在记录影片缩略图
	public static final String KEY_PLAY_INFO_DOWNLOADUUID 					= "downloaduuid";
	
	
	
	public static final String KEY_SYN1 									= "syn1";
	public static final String KEY_SYN2 									= "syn2";
	public static final String KEY_SYN3 									= "syn3";
	public static final String KEY_SYN4 									= "syn4";
	
	public static final String KEY_SYN_C1 									= "syn_c1";
	public static final String KEY_SYN_C2 									= "syn_c2";
	public static final String KEY_SYN_C3 									= "syn_c3";
	public static final String KEY_SYN_C4 									= "syn_c4";
}
