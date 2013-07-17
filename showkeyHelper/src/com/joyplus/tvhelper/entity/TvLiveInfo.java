package com.joyplus.tvhelper.entity;

public class TvLiveInfo {
	
	private String iconUrl;
	private String name;
	private String packageName;
	private String tvSourceUrl;
	private String apkSourceUrl;
	private boolean isCommon;
	
	private int status;//0代表已经是最新的，1代表点击更新，2推荐下载

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getTvSourceUrl() {
		return tvSourceUrl;
	}

	public void setTvSourceUrl(String tvSourceUrl) {
		this.tvSourceUrl = tvSourceUrl;
	}

	public String getApkSourceUrl() {
		return apkSourceUrl;
	}

	public void setApkSourceUrl(String apkSourceUrl) {
		this.apkSourceUrl = apkSourceUrl;
	}

	public boolean isCommon() {
		return isCommon;
	}

	public void setCommon(boolean isCommon) {
		this.isCommon = isCommon;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	
	

}
