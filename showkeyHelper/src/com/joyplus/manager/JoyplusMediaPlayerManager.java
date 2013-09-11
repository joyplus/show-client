package com.joyplus.manager;

import android.app.Activity;
import android.content.Context;

import com.joyplus.Sub.JoyplusSubManager;

public class JoyplusMediaPlayerManager {

	private static final String TAG = "JoyplusMediaPlayerManager";
	
    private Context mContext;
    /*Interface of sub manager*/
    private JoyplusSubManager               mSubManager;
    /*Interface of Url Manager*/
    private URLManager                      mURLManager;
    
    private static JoyplusMediaPlayerManager mMediaPlayerManager;
    public  static JoyplusMediaPlayerManager getInstance(){
    	return mMediaPlayerManager;
    }
    
    public static void Init(Activity context) throws Exception{
    	mMediaPlayerManager = new JoyplusMediaPlayerManager(context);
    }
    		
    public JoyplusMediaPlayerManager(Context context) throws Exception{
    	if(! (context instanceof Activity))throw new Exception("use it in Activity");
    	mContext       = context;
    	InitURLAndSub();
    }
    
    /*Interface of SubManager and Urlmanager*/
    public JoyplusSubManager getSubManager(){
    	return mSubManager;
    }
    public URLManager getURLManager(){
    	return mURLManager;
    }
    private void InitURLAndSub(){
    	ResetURLAndSub();
    }
    public void ResetURLAndSub(){
    	mSubManager    = new JoyplusSubManager(mContext);
//    	mURLManager    = new URLManager();
    }
}
