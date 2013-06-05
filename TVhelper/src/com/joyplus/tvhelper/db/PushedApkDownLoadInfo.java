package com.joyplus.tvhelper.db;

import android.graphics.drawable.Drawable;

import com.joyplus.tvhelper.download.DownLoadTask;

public class PushedApkDownLoadInfo {
	private int push_id;
	private String name;
	private String url;
	private String file_path;
	private int fileSize;
	private int compeleteSize;
	private int download_state;
	private String packageName;
	private Drawable icon;
	
	private DownLoadTask tast;
	
	public int getPush_id() {
		return push_id;
	}
	public void setPush_id(int push_id) {
		this.push_id = push_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	public int getCompeleteSize() {
		return compeleteSize;
	}
	public void setCompeleteSize(int compeleteSize) {
		this.compeleteSize = compeleteSize;
	}
	public int getDownload_state() {
		return download_state;
	}
	public void setDownload_state(int download_state) {
		this.download_state = download_state;
	}
	public DownLoadTask getTast() {
		return tast;
	}
	public void setTast(DownLoadTask tast) {
		this.tast = tast;
	}
	public String getFile_path() {
		return file_path;
	}
	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
}
