package com.joyplus.Sub;

public class SubURI {
      
	public String  Uri;
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return Uri;
	}
	public void setUrl(String url) {
		this.Uri = url;
	}
	
	public SUBTYPE SubType;
	
//	public enum SUBTYPE{
//		  UNKNOW , NETWORK , LOCAL
//    }
}
