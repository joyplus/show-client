package com.joyplus.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.joyplus.tvhelper.entity.URLS_INDEX;
import com.joyplus.tvhelper.utils.BangDanConstant;
import com.joyplus.tvhelper.utils.Constant;
import com.joyplus.tvhelper.utils.DefinationComparatorIndex;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.SouceComparatorIndex1;

public class URLManager {
	private static final String TAG = "URLManager";
	
	public enum Quality{
		HD2  (8),//超清
		MP4  (7), //高清
		FLV  (6);//普清
		private int Type =8;
		Quality(int type){
			Type = type;
		}
		public int toInt(){
			return Type;
		}
	}
	
	private URLManagerServer mServer;
	
	private final static Quality DEFAULTQUALITY  = Quality.HD2;
    
	public URLManager(){
		mServer = new URLManagerServer();
	}
	
	public  URLS_INDEX getCurURLS_INDEX(){
		return mServer.mURUrls_INDEX;
	}
	public URLS_INDEX getNextURLS(){
		return mServer.getNextURLS();
	}
	
	/*Interface of server*/
	public void setDefaultQuality(int defaultQuality){
		setDefaultQuality(getQuality(defaultQuality));
	}
    public void setDefaultQuality(Quality qua){
    	mServer.setDefaultQuality(qua);
	}
    
    public void setURLS(URLS_INDEX list){
    	if(list == null)return;
    	List<URLS_INDEX> lists = new ArrayList<URLS_INDEX>();
    	lists.add(list);
    	setURLS(lists);
    }
    public void setURLS(List<URLS_INDEX> list){
    	if(list == null)return;
    	mServer.setURLs(list);
    }
 
    public void resetCurURLS_INDEX(){
    	mServer.resetCurURLS_INDEX();
    }
    public Quality getDefaultQuality(){
    	return mServer.getDefaultQuality();
    }
	/*Interface */
	public Quality getCurrentQuality(){
		if(mServer.mURUrls_INDEX == null)return DEFAULTQUALITY;
		return getQuality(mServer.mURUrls_INDEX.defination_from_server.trim());
	}
	public boolean isHave_HD2(){
		return mServer.isHave_HD2();
	}
	public boolean isHave_MP4(){
		return mServer.isHave_MP4();
	}
	public boolean isHave_FLV(){
		return mServer.isHave_FLV();
	}
	/*Interface for String show*/
    public ArrayList<String> getExitQualityList(){
    	ArrayList<String> definationStrings = new ArrayList<String>();
     	if(isHave_HD2())definationStrings.add("超    清");
    	if(isHave_MP4())definationStrings.add("高    清");
    	if(isHave_FLV())definationStrings.add("标    清");
    	return definationStrings;
    }
    public static String getQualityString(Quality quality){
    	switch(quality.toInt()){
    	case 8:return "超    清";
    	case 7:return "高    清";
    	case 6:return "标    清";
    	}
		return getQualityString(DEFAULTQUALITY);
    }
    public static Quality getQualityFromString(String quality){    	
    	if(quality.equals("超    清"))return Quality.HD2;
    	else if(quality.equals("高    清"))return Quality.MP4;
    	else if(quality.equals("标    清"))return Quality.FLV;
		return getQualityFromString(getQualityString(DEFAULTQUALITY));
    }
    /*Interface of quality tranface*/
   public static Quality getQuality(String string){    	
    	if (string.trim().equalsIgnoreCase("mp4")) {
			return Quality.MP4;
		} else if (string.trim().equalsIgnoreCase("hd2")) {
			return Quality.HD2;
		}else if (string.trim().equalsIgnoreCase("flv")) {
			return Quality.FLV;
		}else if (string.trim().equalsIgnoreCase("3gp")) {
			return Quality.FLV;
		}    	
    	return DEFAULTQUALITY;
    }
    public static String getQuality(Quality qua){
    	switch(qua.toInt()){
    	case 6:return "flv";
    	case 7:return "mp4";
    	case 8:return "hd2";
    	}
		return getQuality(DEFAULTQUALITY);
    }
    public static Quality getQuality(int type){    	
    	switch(type){
    	case 6:return Quality.FLV;
    	case 7:return Quality.MP4;
    	case 8:return Quality.HD2;
    	} 	
    	return DEFAULTQUALITY;
    }
	private class URLManagerServer{
		
		private List<URLS_INDEX> mList = new ArrayList<URLS_INDEX>();
		
		private List<URLS_INDEX> mList_HD2 = new ArrayList<URLS_INDEX>();
		private List<URLS_INDEX> mList_MP4 = new ArrayList<URLS_INDEX>();
		private List<URLS_INDEX> mList_FLV = new ArrayList<URLS_INDEX>();
		
		private URLS_INDEX mURUrls_INDEX;
		private Quality mDefaultQuality = null;
		
		public URLS_INDEX getNextURLS() {
			// TODO Auto-generated method stub	
			if(mURUrls_INDEX == null || mList.size()<=0)return null;
			Iterator<URLS_INDEX> it = mList.iterator();
			while(it.hasNext()){
				URLS_INDEX index = it.next();
				if(index.url.equals(mURUrls_INDEX.url)){
					 if(it.hasNext()){
						 mURUrls_INDEX=it.next();
						 Log.i(TAG, "getNextURLS--->" + mURUrls_INDEX.toString());
						 return mURUrls_INDEX;
					 }
					 break;
				}
			}
			mURUrls_INDEX = null;
			return null;
		}
		
		public void resetCurURLS_INDEX(){			
			if(mList.size()<=0){
				mURUrls_INDEX= null;
				return;
			}
			mURUrls_INDEX = mList.get(0);	
		}
		public void setDefaultQuality(Quality qua){
			mDefaultQuality = qua;
			ListURLS_INDEX();
		}
		public Quality getDefaultQuality(){
			return mDefaultQuality;
		}

		public void setURLs(List<URLS_INDEX> list){
			mList = list;
			if(mDefaultQuality == null) mDefaultQuality = DEFAULTQUALITY;
			InitResource();
		}
		
		public URLManagerServer(){
			
		}

		private void InitResource(){
			mList_HD2 = new ArrayList<URLS_INDEX>();
			mList_MP4 = new ArrayList<URLS_INDEX>();
			mList_FLV = new ArrayList<URLS_INDEX>();
			ListURLS_INDEX();
		}
		private void ListURLS_INDEX() {
			// TODO Auto-generated method stub
			if(mList.size()<=0)return;     
			
        	for(URLS_INDEX url_index :mList){
        		initResource_list_from(url_index);
        		initResource_list_qua(url_index); 
        		initResource_list_primary(url_index);
        	}        	
    		if (mList.size() > 1) {
    			Collections.sort(mList, new SouceComparatorIndex1());
    			Collections.sort(mList, new DefinationComparatorIndex());
    		}  
    		mURUrls_INDEX = mList.get(0);
		}
        
        private void initResource_list_from(URLS_INDEX url_index){
        	if (url_index.source_from.trim().equalsIgnoreCase(
					Constant.video_index[0])) {
				url_index.souces = 0;
			} else if (url_index.source_from.trim().equalsIgnoreCase(
					Constant.video_index[1])) {
				url_index.souces = 1;
				
			} else if (url_index.source_from.trim().equalsIgnoreCase(
					Constant.video_index[2])) {//le_tv_fee
				url_index.souces = 2;
				
			} else if (url_index.source_from.trim().equalsIgnoreCase(
					Constant.video_index[3])) {//letv
				url_index.souces = 3;

			} else if (url_index.source_from.trim().equalsIgnoreCase(
					Constant.video_index[4])) {//fengxing
				url_index.souces = 4;
			} else if (url_index.source_from.trim().equalsIgnoreCase(
					Constant.video_index[5])) {
				url_index.souces = 5;
			} else if (url_index.source_from.trim().equalsIgnoreCase(
					Constant.video_index[6])) {
				url_index.souces = 6;
			} else if (url_index.source_from.trim().equalsIgnoreCase(
					Constant.video_index[7])) {
				url_index.souces = 7;
			} else if (url_index.source_from.trim().equalsIgnoreCase(
					Constant.video_index[8])) {
				url_index.souces = 8;
			} else if (url_index.source_from.trim().equalsIgnoreCase(
					Constant.video_index[9])) {
				url_index.souces = 9;
			} else if (url_index.source_from.trim().equalsIgnoreCase(
					Constant.video_index[10])) {
				url_index.souces = 10;
			} else if (url_index.source_from.trim().equalsIgnoreCase(
					Constant.video_index[11])) {
				url_index.souces = 11;
			} else if (url_index.source_from.trim().equalsIgnoreCase(
					Constant.video_index[12])) {
				url_index.souces = 12;
			} else {
				url_index.souces = 13;
			}
        }
        private void initResource_list_qua(URLS_INDEX url_index){
        	switch (mDefaultQuality.toInt()) {
			case BangDanConstant.GAOQING:// 高清
				if (url_index.defination_from_server.trim()
						.equalsIgnoreCase(Constant.player_quality_index[1])) {
					url_index.defination = 1;
				} else if (url_index.defination_from_server.trim()
						.equalsIgnoreCase(Constant.player_quality_index[0])) {
					url_index.defination = 2;
				} else if (url_index.defination_from_server.trim()
						.equalsIgnoreCase(Constant.player_quality_index[2])) {
					url_index.defination = 3;
				} else if (url_index.defination_from_server.trim()
						.equalsIgnoreCase(Constant.player_quality_index[3])) {
					url_index.defination = 4;
				} else {
					url_index.defination = 5;
				}
				break;
			case BangDanConstant.CHAOQING:// 超清
				if (url_index.defination_from_server.trim()
						.equalsIgnoreCase(Constant.player_quality_index[0])) {
					url_index.defination = 1;
				} else if (url_index.defination_from_server.trim()
						.equalsIgnoreCase(Constant.player_quality_index[1])) {
					url_index.defination = 2;
				} else if (url_index.defination_from_server.trim()
						.equalsIgnoreCase(Constant.player_quality_index[2])) {
					url_index.defination = 3;
				} else if (url_index.defination_from_server.trim()
						.equalsIgnoreCase(Constant.player_quality_index[3])) {
					url_index.defination = 4;
				} else {
					url_index.defination = 5;
				}
				break;
			case BangDanConstant.CHANGXIAN:// 标清
				if (url_index.defination_from_server.trim()
						.equalsIgnoreCase(Constant.player_quality_index[2])) {
					url_index.defination = 1;
				} else if (url_index.defination_from_server.trim()
						.equalsIgnoreCase(Constant.player_quality_index[3])) {
					url_index.defination = 2;
				} else if (url_index.defination_from_server.trim()
						.equalsIgnoreCase(Constant.player_quality_index[1])) {
					url_index.defination = 3;
				} else if (url_index.defination_from_server.trim()
						.equalsIgnoreCase(Constant.player_quality_index[0])) {
					url_index.defination = 4;
				} else {
					url_index.defination = 5;
				}
				break;
			}
        }
        private void initResource_list_primary(URLS_INDEX url_index){
			if ("hd2".equalsIgnoreCase(url_index.defination_from_server)) {
				mList_HD2.add(url_index);
			} else if ("mp4".equalsIgnoreCase(url_index.defination_from_server)) {
				mList_MP4.add(url_index);
			} else if ("flv".equalsIgnoreCase(url_index.defination_from_server)) {
				mList_FLV.add(url_index);
			}
        }
        
		public boolean haveNext(){
			if(mURUrls_INDEX == null || mList.size()<=0)return false;
			Iterator<URLS_INDEX> it = mList.iterator();
			while(it.hasNext()){
				URLS_INDEX index = it.next();
				if(index.url.equals(mURUrls_INDEX.url)){
					return it.hasNext();
				}
			}
			return false;
		}
		
		public boolean isHave_HD2() {
			// TODO Auto-generated method stub
			return mList_HD2.size()>0;
		}
		public boolean isHave_MP4() {
			// TODO Auto-generated method stub
			return mList_MP4.size()>0;
		}
		public boolean isHave_FLV() {
			// TODO Auto-generated method stub
			return mList_FLV.size()>0;
		}
	}
}
