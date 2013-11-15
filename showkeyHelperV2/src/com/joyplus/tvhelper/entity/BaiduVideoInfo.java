package com.joyplus.tvhelper.entity;

public class BaiduVideoInfo {

	private long fs_id;
	private String path;
	private long size;
	private String pic_url;
	private String fileName;
	public long getFs_id() {
		return fs_id;
	}
	public void setFs_id(long fs_id) {
		this.fs_id = fs_id;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getPic_url() {
		return pic_url;
	}
	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
