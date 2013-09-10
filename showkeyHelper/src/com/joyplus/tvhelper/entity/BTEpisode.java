package com.joyplus.tvhelper.entity;

import org.json.JSONObject;

public class BTEpisode {

	
	private String name;
	private int duration;
	private int playback_time;
	private int defination;
	public int getDefination() {
		return defination;
	}
	public void setDefination(int defination) {
		this.defination = defination;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getPlayback_time() {
		return playback_time;
	}
	public void setPlayback_time(int playback_time) {
		this.playback_time = playback_time;
	}
	
	
	public JSONObject toJSONOSObject(){
		JSONObject obj = new JSONObject();
		try {
			obj.put("name", name);
			obj.put("duration", duration);
			obj.put("playback_time", playback_time);
			obj.put("defination", defination);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}
	
	public BTEpisode(){
		
	}
	
	public BTEpisode(JSONObject data) {
		super();
		try{
			if(data.has("name")){
				this.name = data.getString("name");
			}
			if(data.has("duration")){
				this.duration = data.getInt("duration");
			}
			if(data.has("playback_time")){
				this.playback_time = data.getInt("playback_time");
			}
			if(data.has("defination")){
				this.defination = data.getInt("defination");
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
