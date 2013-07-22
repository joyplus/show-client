package com.joyplus.tvhelper.entity;

import com.joyplus.network.filedownload.model.DownloadTask;

import android.graphics.drawable.Drawable;

public class PushedApkDownLoadInfo {
	
	
	public static final int STATUE_WAITING_DOWNLOAD 	= 0;
	public static final int STATUE_DOWNLOAD_PAUSE 		= STATUE_WAITING_DOWNLOAD + 1;
	public static final int STATUE_DOWNLOAD_PAUSEING 	= STATUE_DOWNLOAD_PAUSE + 1;
	public static final int STATUE_DOWNLOADING 			= STATUE_DOWNLOAD_PAUSEING + 1;
	public static final int STATUE_DOWNLOAD_COMPLETE 	= STATUE_DOWNLOADING + 1;
	public static final int STATUE_INSTALL_FAILE 		= STATUE_DOWNLOAD_COMPLETE + 1;
	
	public static final int IS_USER = 1;
	public static final int IS_NOT_USER = 0;
	
	public static final int EDITE_STATUE_NOMAL 			= 0;
	public static final int EDITE_STATUE_EDIT 			= EDITE_STATUE_NOMAL + 1;
	public static final int EDITE_STATUE_SELETED 		= EDITE_STATUE_EDIT + 1;
	
	private int _id;
	private int push_id;
	private String name;
	private String file_path;
	private int download_state;
	private int edite_state = 0;
	
	private String packageName;
	private Drawable icon;
	private String icon_url;
	public String getIcon_url() {
		return icon_url;
	}

	public void setIcon_url(String icon_url) {
		this.icon_url = icon_url;
	}

	private int isUser;
	
	private DownloadTask tast;

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

	public String getFile_path() {
		return file_path;
	}

	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}

	public int getDownload_state() {
		return download_state;
	}

	public void setDownload_state(int download_state) {
		this.download_state = download_state;
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

	public DownloadTask getTast() {
		return tast;
	}

	public void setTast(DownloadTask tast) {
		this.tast = tast;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getIsUser() {
		return isUser;
	}

	public void setIsUser(int isUser) {
		this.isUser = isUser;
	}

	public int getEdite_state() {
		return edite_state;
	}

	public void setEdite_state(int edite_state) {
		this.edite_state = edite_state;
	}

}
