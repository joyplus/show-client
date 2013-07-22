package com.joyplus.tvhelper.utils;

public class Global {
	
	/** 测试环境 **/
//	public static String serverUrl = "http://otatest.joyplus.tv";
	public static String serverUrl = "http://tt.yue001.com:8080";
	 
	public static String app_key = "ijoyplus_android_0001bj";
	
	
	/**
	 * 确认绑定
	 */
	public static final String ACTION_CONFIRM_ACCEPT = "action_confirm_accept";
	
	/**
	 * 拒绝绑定
	 */
	public static final String ACTION_CONFIRM_REFUSE = "action_confirm_refuse";
	
	/**
	 * 推送apk的请求
	 */
	public static final String ACTION_DOWNLOAD_RECIVED = "action_download_recived"; 
	
	/**
	 * 下载进度更新
	 */
	public static final String ACTION_DOWNLOAD_PROGRESS = "action_download_progress";
	
	/**
	 * 文件大小获取成功
	 */
	public static final String ACTION_DOWNL_GETSIZE_SUCESS = "action_downl_getsize_sucess";
	/**
	 * 安装成功
	 */
	public static final String ACTION_DOWNL_INSTALL_SUCESS = "action_downl_install_sucess";
	/**
	 * 安装失败
	 */
	public static final String ACTION_DOWNL_INSTALL_FAILE = "action_downl_install_faile";
	/**
	 * 下载暂停
	 */
	public static final String ACTION_DOWNLOAD_PAUSE = "action_download_pause";
	/**
	 * 下载开始
	 */
	public static final String ACTION_DOWNLOAD_START = "action_download_start";
	/**
	 * APK下载继续
	 */
	public static final String ACTION_APK_DOWNLOAD_CONTINUE = "action_download_continue";
	/**
	 * APK下载完成
	 */
	public static final String ACTION_APK_DOWNLOAD_COMPLETE = "action_download_complete";
	/**
	 * APK下载完成
	 */
	public static final String ACTION_APK_DOWNLOAD_FAILE = "action_download_faile";
	/**
	 * APK删除
	 */
	public static final String ACTION_APK_DELETE_DOWNLOAD = "action_delete_download";
	/**
	 * MOVIE下载继续
	 */
	public static final String ACTION_MOVIE_DOWNLOAD_CONTINUE = "action_movie_download_continue";
	/**
	 * MOVIE下载完成
	 */
	public static final String ACTION_MOVIE_DOWNLOAD_COMPLETE = "action_movie_download_complete";
	/**
	 * MOVIE下载完成
	 */
	public static final String ACTION_MOVIE_DOWNLOAD_FAILE = "action_movie_download_faile";
	/**
	 * MOVIE删除
	 */
	public static final String ACTION_MOVIE_DELETE_DOWNLOAD = "action_movie_delete_download";
	/**
	 * 刷新pincode
	 */
	public static final String ACTION_PINCODE_REFRESH = "action_pincode_refresh";
	
}
