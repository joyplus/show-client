package com.joyplus.Sub;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.joyplus.tvhelper.utils.Constant;
import com.joyplus.tvhelper.utils.Log;

public class JoyplusSubManager {
	

	   private boolean Debug = true;
	   private static final String  TAG   = "JoyplusSubManager";
	   
	   private Context mContext; 
	   
	   private JoyplusSubServer mSubServer;
	   
	   public JoyplusSubManager(Context context){
		     mContext   = context;
		     mSubServer = new JoyplusSubServer(mContext);
	   }
	   
	   /*Interface of init sub uri*/
	   public void setSubUri(SubURI string){
		   if(string == null || !mSubServer.CheckSubAviable())return;
		   List<SubURI> sub = new ArrayList<SubURI>();
		   sub.add(string);
		   setSubUri(sub);
	   }
	   public void setSubUri(List<SubURI> subUri){
		   Log.i(TAG, "setSubUri subUri size--->" + subUri.size());
		   if(subUri==null || subUri.size()<=0 ||mSubServer.CheckSubAviable())return;
		   mSubServer.setSubUri(subUri);
	   }
	   public int getCurrentSubIndex(){
		   return mSubServer.getCurrentSubIndex();
	   }
	   public List<SubURI> getSubList(){
		   return mSubServer.getSubList();
	   }
	   public boolean CheckSubAviable(){
		   return mSubServer.CheckSubAviable();
	   }
	   public boolean IsSubEnable(){
	    	return mSubServer.IsSubEnable();
	   }
	   public void setSubEnable(boolean EN){
	    	mSubServer.setSubEnable(EN);
	   }
	   public void SwitchSub(int index){
		   if(getSubList().size()<0 || index<0 || index>getSubList().size())return;
		   mSubServer.SwitchSub(index);
	   }
	   public Element getElement(long time){
		   return mSubServer.getElement(time);
	   }
	   
	   /*Interface of parser uri to get download sub uri*/
	public List<SubURI> getNetworkSubURI(String url, String MD5,
			Context context) {
		List<SubURI> list = new ArrayList<SubURI>();
		String subTitleUrl = Constant.SUBTITLE_PARSE_URL_URL + "?url="
				+ URLEncoder.encode(url) + "&md5_code=" + MD5;
		Log.i(TAG, "subTitleUrl-->" + subTitleUrl);
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(subTitleUrl).type(JSONObject.class);
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("app_key", Constant.APPKEY);
		cb.SetHeader(headers);
		(new AQuery(context)).sync(cb);
		JSONObject jo = cb.getResult();
		if (jo != null && jo.toString() != null && !"".equals(jo.toString())) {
			Log.i(TAG, "subtitles--->" + jo.toString());
			try {
				JSONObject subtitlesJsonObject = (JSONObject) new JSONTokener(
						jo.toString()).nextValue();
				if (subtitlesJsonObject.has("error")) {
					if (!subtitlesJsonObject.getBoolean("error")
							&& subtitlesJsonObject.has("subtitles")) {
						JSONArray subtitleContents = subtitlesJsonObject
								.getJSONArray("subtitles");
						if (subtitleContents != null
								&& subtitleContents.length() > 0) {
							for (int i = 0; i < subtitleContents.length(); i++) {
								String tempsubTitleUrl = subtitleContents.getString(i);
								SubURI subURI = new SubURI();
								subURI.SubType = SUBTYPE.NETWORK;
								subURI.Uri = tempsubTitleUrl;
								list.add(subURI);
							}
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.i(TAG, "list size--->" + list.size());
		return list;
	}
}
