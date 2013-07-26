package com.joyplus.tvhelper.entity.service;

/**
 * 接口提供数据
 * @author Administrator
 *
 */
public class TvLiveViews {
	
	public Resources[] resources;
	
	public static class Resources {
		
//		public int iconID;//图片Id
//		public String name;//应用名称
//		public int state = 0;//默认为 0  0：点击更新 1：已是最新版本 2：推荐下载
		public String md5;
		public String app_name;//名称
		public String package_name;//包名
		public String icon_url;//图片地址
		public String is_specific_app;//是否是通用的视频APK，如果机器没有安装，应该推荐下载 1为特殊，0为普通
		public FILE_URL[] file_urls;//直播源下载地址
		public String version_name;//版本号
		public String version_code;//版本号
		public String apk_url;//APK资源下载地址
	}
	
	public static class FILE_URL{
		
		public String file_url;
	}
}
