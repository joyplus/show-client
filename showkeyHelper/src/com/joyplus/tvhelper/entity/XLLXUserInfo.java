package com.joyplus.tvhelper.entity;

public class XLLXUserInfo {

	public int autopay;
	public int daily;
	public String expiredate;
	public int growvalue;
	public int isvip;
	public int isyear;
	public int level;
	public String nickname;
	public String payname;
	public int paytype;
	public String usrname;

	public String toString() {
		return "XLLXUserInfo [autopay=" + this.autopay + ", usrname="
				+ this.usrname + ", growvalue=" + this.growvalue
				+ ", expiredate=" + this.expiredate + ", level=" + this.level
				+ ", isyear=" + this.isyear + ", daily=" + this.daily
				+ ", payname=" + this.payname + ", nickname=" + this.nickname
				+ ", isvip=" + this.isvip + ", paytype=" + this.paytype + "]";
	}

}
