package com.joypuls.tvhelper.entity;

import android.graphics.drawable.Drawable;

public class ApkInfo {
	private String appName;
	private String packageName;
	private Drawable drawble;
	private long size;
	private boolean isInstalled;
	private String vision;
	private String filePath;
	
	private int versionCode;
	
	private int statue;
	
	public ApkInfo(){
		statue = 0;
	}
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public Drawable getDrawble() {
		return drawble;
	}
	public void setDrawble(Drawable drawble) {
		this.drawble = drawble;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public boolean isInstalled() {
		return isInstalled;
	}
	public void setInstalled(boolean isInstalled) {
		this.isInstalled = isInstalled;
	}
	public String getVision() {
		return vision;
	}
	public void setVision(String vision) {
		this.vision = vision;
	}
	public int getStatue() {
		return statue;
	}
	public void setStatue(int statue) {
		this.statue = statue;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
}
