package com.joyplus.tvhelper.entity.service;

/**
 * 接口提供数据
 * @author Administrator
 *
 */
public class TvLiveView {
	
//	public int iconID;//图片Id
//	public String name;//应用名称
//	public int state = 0;//默认为 0  0：点击更新 1：已是最新版本 2：推荐下载
	
	public String iconUrl;//图片地址
	public String name;//名称
	public String packageName;//包名
	public String tvSourceUrl;//直播源下载地址
	public String apkSourceUrl;//APK资源下载地址
	public String isCommon;//是否是通用的视频APK，如果机器没有安装，应该推荐下载
	

}
