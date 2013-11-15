package com.joyplus.tvhelper.entity;

import org.json.JSONObject;

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
	
	public String toJsonString(){
		try{
			JSONObject json = new JSONObject();
			json.put("autopay", autopay);
			json.put("daily", daily);
			json.put("growvalue", growvalue);
			json.put("isvip", isvip);
			json.put("isyear", isyear);
			json.put("level", level);
			json.put("nickname", nickname);
			json.put("payname", payname);
			json.put("paytype", paytype);
			json.put("usrname", usrname);
			return json.toString();
			
		}catch (Exception e) {
			// TODO: handle exception
			return "";
		}
	}

}
