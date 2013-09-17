package com.joyplus.tvhelper.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.joyplus.Sub.SubURI;

public class MoviePlayHistoryInfo {

	
	public static final int PLAY_TYPE_HIDE 	= -1;
	public static final int PLAY_TYPE_ONLINE = 0;
	public static final int PLAY_TYPE_LOCAL = PLAY_TYPE_ONLINE + 1;
	public static final int PLAY_TYPE_BAIDU = PLAY_TYPE_LOCAL + 1;
	public static final int PLAY_TYPE_BT_EPISODES = PLAY_TYPE_BAIDU + 1;
	
	public static final int EDITE_STATUE_NOMAL 			= 0;
	public static final int EDITE_STATUE_EDIT 			= EDITE_STATUE_NOMAL + 1;
	public static final int EDITE_STATUE_SELETED 		= EDITE_STATUE_EDIT + 1;
	
	private int id;
	private String name;
	private int play_type;
	private String push_url;
//	private String download_url;
	private String local_url;
	private int duration;
	private int playback_time;
	private int push_id;
	private String recivedDonwLoadUrls;
	private int defination;
	private long creat_time;
	private List<BTEpisode> btEpisodes = new ArrayList<BTEpisode>();
	private List<SubURI> subList;
	private String time_token;
	
	private int edite_state = 0;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPush_url() {
		return push_url;
	}
	public void setPush_url(String push_url) {
		this.push_url = push_url;
	}
	public String getLocal_url() {
		return local_url;
	}
	public void setLocal_url(String local_url) {
		this.local_url = local_url;
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
	public int getPush_id() {
		return push_id;
	}
	public void setPush_id(int push_id) {
		this.push_id = push_id;
	}
	public int getPlay_type() {
		return play_type;
	}
	public void setPlay_type(int play_type) {
		this.play_type = play_type;
	}
	public int getEdite_state() {
		return edite_state;
	}
	public void setEdite_state(int edite_state) {
		this.edite_state = edite_state;
	}
//	public String getDownload_url() {
//		return download_url;
//	}
//	public void setDownload_url(String download_url) {
//		this.download_url = download_url;
//	}
	public String getRecivedDonwLoadUrls() {
		return recivedDonwLoadUrls;
	}
	public void setRecivedDonwLoadUrls(String recivedDonwLoadUrls) {
		this.recivedDonwLoadUrls = recivedDonwLoadUrls;
	}
	public int getDefination() {
		return defination;
	}
	public void setDefination(int defination) {
		this.defination = defination;
	}
	public long getCreat_time() {
		return creat_time;
	}
	public void setCreat_time(long creat_time) {
		this.creat_time = creat_time;
	}
	public String getTime_token() {
		return time_token;
	}
	public void setTime_token(String time_token) {
		this.time_token = time_token;
	}
	
	public List<BTEpisode> getBtEpisodes() {
		return btEpisodes;
	}
	public void setBtEpisodes(List<BTEpisode> btEpisodes) {
		if(play_type!=PLAY_TYPE_BT_EPISODES){
			return ;
		}
		this.btEpisodes = btEpisodes;
	}
	
	public String getBtEpisodesString(){
		if(play_type != PLAY_TYPE_BT_EPISODES){
			return "";
		}
		JSONArray array = new JSONArray();
		for(BTEpisode b : btEpisodes){
			array.put(b.toJSONOSObject());
		}
		return array.toString();
	}
	
	public void setBtEpisodes(String data){
		if(play_type != PLAY_TYPE_BT_EPISODES){
			return ;
		}
		btEpisodes.clear();
		try {
			JSONArray array = new JSONArray(data);
			for(int i=0; i<array.length(); i++){
				btEpisodes.add(new BTEpisode(array.getJSONObject(i)));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public List<SubURI> getSubList() {
		return subList;
	}
	public void setSubList(List<SubURI> subList) {
		this.subList = subList;
	}
}
