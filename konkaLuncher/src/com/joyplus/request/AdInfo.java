package com.joyplus.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AdInfo implements Serializable{
	
	private static final long serialVersionUID          = 6443573739926220979L;
	
    public final static OPENTYPE DEFAULT_OPENTYPE = OPENTYPE.ANDROID;
	public enum OPENTYPE{
		HTML5   (0),
		ANDROID (1);
		private int Type;
		OPENTYPE(int type){
			Type = type;
		}
		public String toString(){
			return String.valueOf(Type);
		}
		public int toInt(){
			return Type;
		}
	}
	
	public OPENTYPE mOPENTYPE  = DEFAULT_OPENTYPE;
	public String widgetPicUrl = "";
	public String creativeUrl  = "";
	public String trackingUrl  = "";
	
	public List<AdInfoItems> items = new ArrayList<AdInfoItems>();
	
	
	public String toString(){
		StringBuffer ap = new StringBuffer();
		ap.append("AdInfo{")
		  .append("mOPENTYPE="+(mOPENTYPE==null?"null":mOPENTYPE.toString()))
		  .append(",widgetPicUrl="+widgetPicUrl)
		  .append(",creativeUrl="+creativeUrl)
		  .append(",trackingUrl="+trackingUrl)
		  .append(","+ItemstoString())
		  .append("}");
		return ap.toString();
	}
	private String ItemstoString(){
		if(items==null || items.size()<=0)return "items==null";
		StringBuffer ap = new StringBuffer();
		ap.append("items{");
		for(AdInfoItems it :items){
			ap.append(it.toString());
		}
		ap.append("}");
		return ap.toString();
	}
}
