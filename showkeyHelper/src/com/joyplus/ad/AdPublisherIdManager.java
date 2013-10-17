package com.joyplus.ad;




import com.umeng.analytics.MobclickAgent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class AdPublisherIdManager {
         
	     private boolean Debug = true;
	     private String  TAG   = "Jas";
	     
	     private Context mContext;

	     //when publisherId can,t get from network and local XML, we should return default ID
	     private static final String DefaultID = "8f9bf339292d6f8216b91aa86e95d13e";
	     
	     public enum UMENGPARAMS{
	    	 LOADING_ADV  ("_LOADING_ADV_PUBLISHERID"),
	    	 MAIN_ADV     ("_MAIN_ADV_PUBLISHERID"),
	    	 PLAYER_ADV   ("_PLAYER_ADV_PUBLISHERID"),
	    	 BOOT_ADV     ("_BOOT_ADV_PUBLISHERID");
	    	 private String umengparam;
	    	 UMENGPARAMS(String param){
	    		 this.umengparam = param;
	    	 }
	    	 public String toString(){
	    		 return umengparam;
	    	 }
	     }
	     
	     public AdPublisherIdManager(Context context){
	    	  if(context==null)return;
	    	  mContext = context;	    	  
	     }
	     
	     /*define by Jas@20130725 for get publisherId from network.if get something it will save in XML file{@link AdDataManager}
	      * return : null    get nothing
	      *          String  new publisherId get from network*/
	     public String UpdatePublisherId(UMENGPARAMS param) {
			// TODO Auto-generated method stub
	    	  if(Debug)Log.d(TAG,"UpdatePublisherId() param="+param.toString());
	    	  MobclickAgent.onResume(mContext);
	  		  MobclickAgent.updateOnlineConfig(mContext);
	  		  String Channel = getUmengChannel();
	  		  if(Channel != null){
	  			   if(Debug)Log.d(TAG,"UpdatePublisherId() Channel="+Channel+" param="+param.toString());
	  			   String loadingAdvID = MobclickAgent.getConfigParams(mContext, Channel.trim() + param.toString().trim());
	  			   Log.d(TAG,"loadingAdvID = "+loadingAdvID);
	  			   if(loadingAdvID != null && !loadingAdvID.equals("")){
	  				   if(!setPublisherId(loadingAdvID)){
	  					    clearPublisherId();
	  				   }
	  				   return loadingAdvID;
	  			   }
	  		  }
	  		  return DefaultID;
		 }
	     
         private String getUmengChannel(){
     		try {
    			ApplicationInfo info=mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(),
    			        PackageManager.GET_META_DATA);
    			String umengChannel =info.metaData.getString("UMENG_CHANNEL");
    			Log.i(TAG, "UMENG_CHANNEL--->" + umengChannel);
    			if (umengChannel != null && !umengChannel.equals("")) {
    				return umengChannel;
    			}
    		} catch (NameNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
     		return null;
         }
		 
         public String getPublisherId(){
        	 if(Debug)Log.d(TAG,"getPublisherId()");
	    	 return AdDataManager.getPublisherID(mContext);
	     }
	     
	     public boolean setPublisherId(String id){
	    	 if(Debug)Log.d(TAG,"setPublisherId("+id+")");
	    	 return AdDataManager.setPublisherID(mContext, id);
	     }
	     
	     public boolean clearPublisherId(){
	    	 if(Debug)Log.d(TAG,"clearPublisherId()");
	    	 return setPublisherId("");
	     }
}
