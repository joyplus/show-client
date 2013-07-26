package com.joyplus.tvhelper.entity;

import java.io.File;
import java.util.List;

public class TvLiveInfo {
	public static final int UNKOWN = -1;
	public static final int NEWS = 0;
	public static final int UPDATE = 1;
	public static final int DOWNLOAD = 2;
	
	private String md5;
	private String app_name;//名称
	private String package_name;//包名
	private String icon_url;//图片地址
	private boolean is_specific_app;//是否是通用的视频APK，如果机器没有安装，应该推荐下载 1为特殊，0为普通
	private String[] file_urls;//直播源下载地址
	private String version;//版本号
	private String apk_url;//APK资源下载地址
	
	private int status;//0代表已经是最新的，1代表点击更新
	
	private boolean isInstall;

	private String[] fileNames;//下载的文件名
	
	private List<File> srcFileLists;//下载的临时文件
	
	public boolean isInstall() {
		return isInstall;
	}

	public void setInstall(boolean isInstall) {
		this.isInstall = isInstall;
	}
	
	public List<File> getSrcFileLists() {
		return srcFileLists;
	}

	public void setSrcFileLists(List<File> srcFileLists) {
		this.srcFileLists = srcFileLists;
	}

	public List<File> getDstFileLists() {
		return dstFileLists;
	}

	public void setDstFileLists(List<File> dstFileLists) {
		this.dstFileLists = dstFileLists;
	}

	private List<File> dstFileLists;//有效目录文件


	public String[] getFileNames() {
		return fileNames;
	}

	public void setFileNames(String[] fileNames) {
		this.fileNames = fileNames;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getApp_name() {
		return app_name;
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}

	public String getPackage_name() {
		return package_name;
	}

	public void setPackage_name(String package_name) {
		this.package_name = package_name;
	}

	public String getIcon_url() {
		return icon_url;
	}

	public void setIcon_url(String icon_url) {
		this.icon_url = icon_url;
	}

	public boolean isIs_specific_app() {
		return is_specific_app;
	}

	public void setIs_specific_app(boolean is_specific_app) {
		this.is_specific_app = is_specific_app;
	}

	public String[] getFile_urls() {
		return file_urls;
	}

	public void setFile_urls(String[] file_urls) {
		this.file_urls = file_urls;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getApk_url() {
		return apk_url;
	}

	public void setApk_url(String apk_url) {
		this.apk_url = apk_url;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "app_name:" + app_name+ " package_name:" + package_name;
	}
}
