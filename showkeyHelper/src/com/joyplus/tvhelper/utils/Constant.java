package com.joyplus.tvhelper.utils;

import android.os.Environment;

public class Constant {
	
	public static boolean isJoyPlus = true;//是否是JoyPlus本身应用，还是其他应用
	
	 //正式环境
	 public static final boolean TestEnv = false;
	 public static String APPKEY = "ijoyplus_android_0001bj";
	 public static String BASE_URL = "http://tt.showkey.tv";//视频直播源
	 public static final String URL_FAQ = "http://tt.showkey.tv/helptv.html";//视频直播源
	//测试环境控制
//	public static final boolean TestEnv = true;
//	 public static String APPKEY = "ijoyplus_android_0001bj";
//	 public static final String BASE_URL = "http://tt.yue001.com:8080";//视频直播源
//	 public static final String URL_FAQ = "http://tt.yue001.com:8080/helptv.html";//视频直播源
	 
	//刘洋环境
//	 public static String APPKEY = "ijoyplus_android_0001bj";
//	 public static String BASE_URL = "http://172.16.31.64:8030";//视频直播源
//	 public static final String URL_FAQ = "http://172.16.31.64:8030/helptv.html";//视频直播源
	 
	public static final String SUBTITLE_PARSE_URL_URL = BASE_URL + "/joyplus/subtitle/";
	
	 public static final String DES_KEY = "ilovejoy";
	 
	public static final String VIDEOPLAYERCMD = "com.joyplus.tv.videoservicecommand";

	
	
	public static final String[] video_dont_support_extensions = { ".m3u",".m3u8"};//不支持的格式
	public static final String[] video_dont_download_sign = {"&start=","&end="};//不支持head 断点续传功能
	public static final String[] video_index = { "wangpan", "le_tv_fee",
			"letv", "fengxing", "qiyi", "youku", "sinahd", "sohu", "56", "qq","pptv", "m1905" };//来源
	public static final String BAIDU_WANGPAN = "baidu_wangpan";
	/*
	 * "type": flv,3gp：标清 (普清就是标清) ,"mp4", mp4:高清，hd2：超清
	 */
//	public static final String[] quality_index = { "hd2", "mp4", "flv", "3gp" }; // 播放器用

	public static final String[] player_quality_index = { "hd2", "mp4", "3gp","flv" };//格式
	
	//模拟firefox发送请求
	public static final String USER_AGENT_IOS = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7";
	public static final String USER_AGENT_ANDROID = "Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
	public static final String USER_AGENT_FIRFOX = "	Mozilla/5.0 (Windows NT 6.1; rv:19.0) Gecko/20100101 Firefox/19.0";
	
	//图片路径
//	public static String PATH = Environment.getExternalStorageDirectory()
//			+ "/joy/image_cache/";
	//用此目录用户清除程序时，可以删掉缓存信息
	public static String PATH = Environment.getExternalStorageDirectory()
	+ "/Android/data/com.joyplus.tvhelper/image_cache";
	public static String PATH_BIG_IMAGE = Environment.getExternalStorageDirectory()
	+ "/Android/data/com.joyplus.tvhelper/bg_image_cache";
	public static String PATH_HEAD = Environment.getExternalStorageDirectory()
			+ "/joy/admin/";
	public static String TV_LIVING_FILE_PATH = Environment.getExternalStorageDirectory()
	+ "/Android/data/com.joyplus.tvhelper/tv_living_file_temp";

	
	public static final int DEFINATION_HD2 = 8; 
	public static final int DEFINATION_HD = 7; 
	public static final int DEFINATION_MP4 = 6; 
	public static final int DEFINATION_FLV = 5; 
}
