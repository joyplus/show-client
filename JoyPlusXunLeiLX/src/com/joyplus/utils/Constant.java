package com.joyplus.utils;

import android.os.Environment;

public class Constant {
	
	public static boolean isJoyPlus = true;//�Ƿ���JoyPlus����Ӧ�ã���������Ӧ��
	
//	public static final String APPKEY_TOP = "ijoyplus_android_0001";//��ʽ
//	// ��ʽ����
//	public static final String BASE_URL_TOP = "http://api.joyplus.tv/joyplus-service/index.php/";//��ʽ
//	public static boolean TestEnv = false;
//	public static  String BASE_URL = "http://api.joyplus.tv/joyplus-service/index.php/";
//	public static String DEFAULT_APPKEY = "ijoyplus_android_0001";
//	public static String APPKEY = "ijoyplus_android_0001";
//	public static final String FAYESERVERURL = "http://comet.joyplus.tv:8080/bindtv";
//	public static final String FAYESERVERURL_CHECKBAND = "http://comet.joyplus.tv:8080/api/check_binding";
	
//	
	
	/*
	 * test: �µĲ��Ի����� ���Ի�����
	 * 
	 * 1��service: apitest.joyplus.tv/joyplus-service/index.php
	 * 
	 * app_key:
	 * 
	 * Android:ijoyplusandroid0001bj
	 * 
	 * IOS: ijoyplusios001bj
	 * 
	 * 
	 * 2��cms cms-test.yue001.com/manager/index.php
	 */
	
//	//���Ի�������
	public static final String APPKEY_TOP = "ijoyplus_android_0001bj";//����
	public static final String BASE_URL_TOP = "http://apitest.yue001.com/joyplus-service/index.php/";//����
	public static boolean TestEnv = true;
////	
////	//���Ի���
	 public static  String BASE_URL =
	 "http://apitest.yue001.com/joyplus-service/index.php/";
	 public static String DEFAULT_APPKEY = "ijoyplus_android_0001bj";
	 public static String APPKEY = "ijoyplus_android_0001bj";

	 public static final String FAYESERVERURL =
	 "http://comettest.joyplus.tv:8000/bindtv";//����
	 public static final String FAYESERVERURL_CHECKBAND =
	 "http://comettest.joyplus.tv:8000/api/check_binding";//����

	
	// ���ǵ�sina�˺�
	public static String SINA_CONSUMER_KEY = "1490285522";
	public static String SINA_CONSUMER_SECRET = "f9ebc3ca95991b6dfce2c1608687e92b";
	public static String TECENTAPPID = "100317415";
	public static String SINA_REDIRECTURL = "https://api.weibo.com/oauth2/default.html";
	
	
	// Test Env parse
	public static String Parse_AppId = "FtAzML5ln4zKkcL28zc9XR6kSlSGwXLdnsQ2WESB";
	public static String Parse_ClientKey = "YzMYsyKNV7ibjZMfIDSGoV5zxsylV4evtO8x64tl";

	// Production Env
	// public static String Parse_AppId =
	// "UBgv7IjGR8i6AN0nS4diS48oQTk6YErFi3LrjK4P";
	// public static String Parse_ClientKey =
	// "Y2lKxqco7mN3qBmZ05S8jxSP8nhN92hSN4OHDZR8";

	// faye ��أ���ά��ɨ�裩
	public static final String FAYECHANNEL_TV_BASE = "/screencast/CHANNEL_TV_";
	public static final String FAYECHANNEL_TV_HEAD = "/screencast/";
	public static final String FAYECHANNEL_MOBILE_BASE = "/screencast/CHANNEL_MOBILE_";
	public static final String CHANNELHEADER = "joy";

	
	
	public static final String VIDEOPLAYERCMD = "com.joyplus.tv.videoservicecommand";

	
	
	public static final String[] video_dont_support_extensions = { ".m3u",".m3u8" };//��֧�ֵĸ�ʽ
	public static final String[] video_index = { "wangpan", "le_tv_fee",
			"letv", "fengxing", "qiyi", "youku", "sinahd", "sohu", "56", "qq","pptv", "m1905" };//��Դ
	public static final String BAIDU_WANGPAN = "baidu_wangpan";
	/*
	 * "type": flv,3gp������ (������Ǳ���) ,"mp4", mp4:���壬hd2������
	 */
//	public static final String[] quality_index = { "hd2", "mp4", "flv", "3gp" }; // ��������

	public static final String[] player_quality_index = { "hd2", "mp4", "3gp","flv" };//��ʽ
	
	//ģ��firefox��������
	public static final String USER_AGENT_IOS = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7";
	public static final String USER_AGENT_ANDROID = "Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
	public static final String USER_AGENT_FIRFOX = "	Mozilla/5.0 (Windows NT 6.1; rv:19.0) Gecko/20100101 Firefox/19.0";
	
	//ͼƬ·��
//	public static String PATH = Environment.getExternalStorageDirectory()
//			+ "/joy/image_cache/";
	//�ô�Ŀ¼�û��������ʱ������ɾ��������Ϣ
	public static String PATH = Environment.getExternalStorageDirectory()
	+ "/Android/data/com.joyplus.tv/image_cache";
	public static String PATH_BIG_IMAGE = Environment.getExternalStorageDirectory()
	+ "/Android/data/com.joyplus.tv/bg_image_cache";
	public static String PATH_HEAD = Environment.getExternalStorageDirectory()
			+ "/joy/admin/";

}
