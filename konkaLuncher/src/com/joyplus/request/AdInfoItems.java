package com.joyplus.request;

import java.io.Serializable;

public class AdInfoItems implements Serializable{
	
    private static final long serialVersionUID          = 6443573739926220979L;

	public int     id;
	
	public String  name;
	
	public String  description;
	
	public String  uri;
	
	public String column;
	
	public String zone;
	
	public String  picUrl;
	
	public String  createTime;
	
	public String toString(){
		StringBuffer ap = new StringBuffer();
		ap.append("AdInfoItems{")
		  .append("id="+id)
		  .append(",name="+name)
		  .append(",description="+description)
		  .append(",uri="+uri)
		  .append(",column="+column)
		  .append(",zone="+zone)
		  .append(",picUrl="+picUrl)
		  .append(",createTime="+createTime)
		  .append("}");
		return ap.toString();
	}
}
