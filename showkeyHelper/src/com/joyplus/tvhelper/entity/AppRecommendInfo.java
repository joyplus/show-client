package com.joyplus.tvhelper.entity;

public class AppRecommendInfo {
	
	private String appName;
	private String iconUrl;
	private int iconSrcId;
	private String pakageName;

	private String size;
	private String downloadUrl;
	
	private boolean isInstalled;
	
	public String getPakageName() {
		return pakageName;
	}

	public void setPakageName(String pakageName) {
		this.pakageName = pakageName;
	}
	
	public int getIconSrcId() {
		return iconSrcId;
	}

	public void setIconSrcId(int iconSrcId) {
		this.iconSrcId = iconSrcId;
	}
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public boolean isInstalled() {
		return isInstalled;
	}

	public void setInstalled(boolean isInstalled) {
		this.isInstalled = isInstalled;
	}

}
