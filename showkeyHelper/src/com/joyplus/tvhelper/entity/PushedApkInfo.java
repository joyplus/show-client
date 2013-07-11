package com.joyplus.tvhelper.entity;

import android.graphics.drawable.Drawable;


public class PushedApkInfo {
	private int push_id;
	private String appName;
	private long size;
	private int progress;
	private Drawable icon;
	private int statue;//0：等待下载 .1：正在下载，2：暂停。 3：安装。 4.安装失败
	private int edite_statue; // 0：正常  1：编辑未选中2：选中
	
	public PushedApkInfo(){
		statue = 0;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getStatue() {
		return statue;
	}

	public void setStatue(int statue) {
		this.statue = statue;
	}

	public int getPush_id() {
		return push_id;
	}

	public void setPush_id(int push_id) {
		this.push_id = push_id;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public int getEdite_statue() {
		return edite_statue;
	}

	public void setEdite_statue(int edite_statue) {
		this.edite_statue = edite_statue;
	}
}
