package com.joyplus.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AdInfo implements Serializable{
	
	private static final long serialVersionUID          = 6443573739926220979L;
	
    public final static OPENTYPE DEFAULT_OPENTYPE = OPENTYPE.ANDROID;
	public enum OPENTYPE{
		HTML5   ("002"),
		ANDROID ("001");
		private String Type;
		OPENTYPE(String type){
            Type = type;
		}
		public String toString(){
			return Type;
		}
	}
    public static  OPENTYPE GetOPENTYPE(String type){
        if(type == null || "".equals(type))return DEFAULT_OPENTYPE;
        if(OPENTYPE.ANDROID.toString().equals(type))return OPENTYPE.ANDROID;
        else if(OPENTYPE.HTML5.toString().equals(type))return OPENTYPE.HTML5;
        else return  DEFAULT_OPENTYPE;
    }
	
	public OPENTYPE mOPENTYPE  = DEFAULT_OPENTYPE;
	public String widgetPicUrl = "";
	public String creativeUrl  = "";
	public String trackingUrl  = "";
	public String trackingUrlMiaozhen = "";
    public String trackingUrlIresearch= "";
    public String trackingUrlAdMaster = "";
    public String trackingUrlNielsen  = "";




	public List<AdInfoItems> items = new ArrayList<AdInfoItems>();
	
	
	public String toString(){
		StringBuffer ap = new StringBuffer();
		ap.append("AdInfo{")
		  .append("mOPENTYPE="+(mOPENTYPE==null?"null":mOPENTYPE.toString()))
		  .append(",widgetPicUrl="+widgetPicUrl)
		  .append(",creativeUrl="+creativeUrl)
		  .append(",trackingUrl="+trackingUrl)
          .append(",trackingUrlMiaozhen="+trackingUrlMiaozhen)
          .append(",trackingUrlIresearch="+trackingUrlIresearch)
          .append(",trackingUrlAdMaster="+trackingUrlAdMaster)
          .append(",trackingUrlNielsen="+trackingUrlNielsen)
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
