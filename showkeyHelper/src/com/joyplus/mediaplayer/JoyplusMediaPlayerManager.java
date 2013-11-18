package com.joyplus.mediaplayer;

import io.vov.vitamio.LibsChecker;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.joyplus.control.SubManager;
import com.joyplus.manager.URLManager;
import com.joyplus.mediaplayer.JoyplusMediaPlayerServer.PlayerState;
import com.joyplus.mediaplayer.VideoViewInterface.DecodeType;


public class JoyplusMediaPlayerManager {

	    private boolean Debug = true;
	    private String  TAG   = "MediaPlayerManager";
	    
	    private Context mContext;
	    /*Interface of control videoview*/
	    private JoyplusMediaPlayerServer        mServer;
	    /*Interface of videoview preference*/
	    private JoyplusMediaPlayerDataManager   mDataManager;
	    /*Interface of sub manager*/
	    public final static boolean SubanagerUseNEW = true;
	    private SubManager            mSubManager;
	    /*Interface of Url Manager*/
	    private URLManager                      mURLManager;
	    	    
	    /*a type of media player which unknow*/
	    public final static int TYPE_UNKNOW       = -1;
	    /*a type of media player it use Android default mediaplayer*/
	    public final static int TYPE_MEDIAPLAYER  = 0;
	    /*a type of media player it use Vitamio*/
	    public final static int TYPE_VITAMIO      = 1;
	    /*a type of media player it use Baidu */
	    public final static int TYPE_BAIDU        = 2;
	    
	    public final static int TYPE_MAX          = TYPE_BAIDU;
	    
	    public static String getPlayerTypeName(int type){
	    	 switch(type){
	    	 case TYPE_MEDIAPLAYER:
	    		 return "MEDIAPLAYER";
	    	 case TYPE_VITAMIO:
	    		 return "VITAMIO";
	    	 case TYPE_BAIDU:
	    		 return "BAIDU";
	    	 default :
	    		 return Integer.toString(type);
	    	 }
	    }
	    
		public static boolean isTypeAvailable(int type){
			return (type>TYPE_UNKNOW && type<=TYPE_MAX);
		}
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
	    	mDataManager   = new JoyplusMediaPlayerDataManager(context);
	    	mServer        = new JoyplusMediaPlayerServer(context);
	    	InitURLAndSub();
	    	if(mServer.getJoyplusPlayerConfig(TYPE_VITAMIO).EN)
	    		setVitamioEn(LibsChecker.checkVitamioLibs((Activity)context));
	    	else setVitamioEn(false);
	    	if(mServer.getJoyplusPlayerConfig(TYPE_BAIDU).EN){
//	    		BCyberPlayerFactory.init(mContext);
//	    		BEngineManager mgr = BCyberPlayerFactory.createEngineManager();
//				mgr.initCyberPlayerEngine(mDataManager.getBaiduAK(), mDataManager.getBaiduSK());
	    		setBaiduEn(true);
	    	}else setBaiduEn(false);
	    }
	    /*Interface of SubManager and Urlmanager*/
	    public SubManager getSubManager(){	    	
	    	return mSubManager; 	    	
	    }
	    
	    public URLManager getURLManager(){
	    	return mURLManager;
	    }
	    public JoyplusMediaPlayerDataManager getDataManager(){
	    	return mDataManager;
	    }
	    private void InitURLAndSub(){
	    	ResetURLAndSub();
	    }
	    public void ResetURLAndSub(){
	    	if(SubanagerUseNEW){
	    		mSubManager    = new com.joyplus.sub.JoyplusSubManager(mContext);
	    	}else{
	    		mSubManager    = new com.joyplus.sub_old_1.JoyplusSubManager(mContext);
	    	}	    	
	    	mURLManager    = new URLManager();
	    }
	    /*Interface of type config*/
	    public JoyplusPlayerConfig getJoyplusPlayerConfig(int Type){
	    	try {
				return mServer.getJoyplusPlayerConfig(Type);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
	    }
	    /*Interface of set decode type*/
	    public boolean setDecodeType(DecodeType type){
	    	 return mDataManager.setDecodeType(type);
	    }
	    /*Interface of get decode type*/
	    public DecodeType getDecodeType(){
	    	return mDataManager.getDecodeType();
	    }
	    /*Interface of Switch Internal flog*/
	    public boolean setSwitchEn(boolean en){
	    	return mDataManager.setSwitchEnable(en);
	    }
	    public boolean getSwitchEn(){
	    	return mDataManager.getSwitchEnable();
	    }
	    /*Interface of Vitamio support flog*/
	    public boolean setVitamioEn(boolean en){
	    	return mDataManager.setVitamioEnable(en);
	    }
	    public boolean getVitamioEn(){
	    	return mDataManager.getVitamioEnable();
	    }
	    /*Interface of Baidu support flog*/
	    public boolean setBaiduEn(boolean en){
	    	return mDataManager.setBaiduEnable(en);
	    }
	    public boolean getBaiduEn(){
	    	return mDataManager.getBaiduEnable();
	    }
	    /*Interface of check decode name*/
	    public String getDecodeName(DecodeType type){
	    	return mDataManager.getDecodeType(type);
	    }
	    /*Interface of mediaplayer */
	    public PlayerState getCurrentType(){
	    	return mServer.getCurrentType();
	    }
	    public PlayerState getNextType(){
	    	PlayerState current = getCurrentType();
	    	if(current.PlayerType != JoyplusMediaPlayerManager.TYPE_UNKNOW && isTypeAvailable(current.PlayerType)){
	    		try {
	    			return mServer.getNextType(current.PlayerType);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	return current;
	    }
	    public boolean IshaveNextType(){
	    	if(!isTypeAvailable(getCurrentType().PlayerType))return false;
	        mServer.SwitchPlayer();
	    	return false;
	    }
		/*Interface of MediaPlayer Listener*/
	    public Handler getmediaPlayerHandler(){
	    	return mServer.getMediaPlayerHandler();
	    }
		public void registerListener(JoyplusMediaPlayerListener listener){
			if(listener == null)return;
			mServer.registerListener(listener);
		}
		public void unregisterListener(JoyplusMediaPlayerListener listener){
			if(listener == null)return;
			mServer.unregisterListener(listener);
		}
		public void unregisterAllListener(){
			mServer.unregisterAllListener();
		}
		/*Interface of switch player*/
		public boolean SwitchPlayer(){
			return mServer.SwitchPlayer();
		}
		public void InitPlayer(){
			mServer.InitPlayer();
		}
	    
}
