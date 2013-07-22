package com.joyplus.tvhelper.entity;

public class AppRecommendInfo {
	
	private String md5;
	private String app_name;
	private String package_name;
	private String icon_url;
	private String apk_size;
	private String version;
	private String apk_url;
	
	private boolean isInstalled;
	
	private int iconSrcId;
	

	public int getIconSrcId() {
		return iconSrcId;
	}

	public void setIconSrcId(int iconSrcId) {
		this.iconSrcId = iconSrcId;
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

	public String getApk_size() {
		return apk_size;
	}

	public void setApk_size(String apk_size) {
		this.apk_size = apk_size;
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

	public boolean isInstalled() {
		return isInstalled;
	}

	public void setInstalled(boolean isInstalled) {
		this.isInstalled = isInstalled;
	}

}
