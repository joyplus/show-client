package com.joyplus.tvhelper.entity;

import java.io.Serializable;
import java.util.Arrays;

public class XLLXFileInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2396021059024511861L;
	
	public XLLXFileInfo[] btFiles;
	public String createTime;
	public String duration;
	public String file_name;
	public String filesize;
	public String gcid;
	public boolean isDir = false;
	public String src_url;
	public String userid;
	
	public int playflag;
	public int recordeNum;

	public String toString() {
		return "XLLXFileInfo [file_name=" + this.file_name + ", src_url="
				+ this.src_url + ", userid=" + this.userid + ", gcid="
				+ this.gcid + ", filesize=" + this.filesize + ", duration="
				+ this.duration + ", createTime=" + this.createTime
				+ ", isDir=" + this.isDir + ", btFiles="
				+ Arrays.toString(this.btFiles) + "]";
	}
}
