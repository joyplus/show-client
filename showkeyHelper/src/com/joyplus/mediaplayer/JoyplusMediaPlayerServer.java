package com.joyplus.mediaplayer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.joyplus.mediaplayer.VideoViewInterface.DecodeType;
import com.joyplus.mediaplayer.VideoViewInterface.STATE;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.utils.Log;


public class JoyplusMediaPlayerServer {
	
	private boolean Debug = true;
	private String  TAG   = "JoyplusMediaPlayerServer";
	
	private final static int NO_FIND = -1;
	
	//private Context mContext;
	
	private JoyplusPlayerConfig[] mPlayerConfigs;//it depend on priority_HW which depend in "JoyplusMediaPlayerConfig.XML" ,
	//private VideoViewInterface    mPlayers[];//it depend on priority_HW which depend in "JoyplusMediaPlayerConfig.XML" ,
	private int   HWPlayer = 0;     //it depend on priority_HW which depend in "JoyplusMediaPlayerConfig.XML" ,
	private int   SWPlayer = 0;     //it depend on priority_HW which depend in "JoyplusMediaPlayerConfig.XML" ,
	private int[] priorityHWList;   //it depend on priority_HW which depend in "JoyplusMediaPlayerConfig.XML" ,reverse order
	private int[] prioritySWList;   //it depend on priority_SW which depend in "JoyplusMediaPlayerConfig.XML" ,reverse order

	/*MediaPlayer tracker，it use to report*/
    private JoyplusMediaPlayerStateTrack mStateTracker;
    private PlayerState mCurrentState;
    
	/*client of JoyplusMediaPlayerServer,bc no C/S,so do it in the same thread*/
	public JoyplusMediaPlayerServer(Context context){
		 // this.mContext  = context;
		  mStateTracker  = new JoyplusMediaPlayerStateTrack();
		  mPlayerConfigs = new JoyplusPlayerConfig[JoyplusMediaPlayerManager.TYPE_MAX+1];
		  //mPlayers       = new VideoViewInterface[JoyplusMediaPlayerManager.TYPE_MAX+1];
		  String[] ConfigStrings = context.getResources().getStringArray(R.array.mediaplayerconfig);
		  if(ConfigStrings==null || "".equals(ConfigStrings))throw new IllegalArgumentException();
		  for(String config : ConfigStrings){
			  JoyplusPlayerConfig n = new JoyplusPlayerConfig(config);
			 
			  if(!JoyplusMediaPlayerManager.isTypeAvailable(n.TYPE)){
				  Log.i(TAG,"JoyplusMediaPlayerServer - ignoring attempt to define type");
				  continue;
			  }
			  if(mPlayerConfigs[n.TYPE]!=null){
				  Log.i(TAG,"JoyplusMediaPlayerServer - ignoring attempt to redefine type");
				  continue;
			  }
			  mPlayerConfigs[n.TYPE] = n;
			  if(!n.EN){
				  Log.i(TAG,"JoyplusMediaPlayerServer - ignoring EN to define type");
				  continue;
			  }
			  if(n.DECODE_HW)HWPlayer++;
			  if(n.DECODE_SW)SWPlayer++;
		  }
		  InitprioritySWList();
		  InitpriorityHWList();
	}
	/*Interface of mediaplayer config*/
	public JoyplusPlayerConfig getJoyplusPlayerConfig(int Type) throws Exception{
		if(!JoyplusMediaPlayerManager.isTypeAvailable(Type))throw new Exception("unAvailableType"+Type);
		Log.i(TAG, "getJoyplusPlayerConfig--->" + mPlayerConfigs[Type]);
		return mPlayerConfigs[Type];
	}
	/*interface of current player,it will be use we init or resume*/
	public PlayerState getCurrentType(){
		if(mCurrentState == null)mCurrentState = new PlayerState();
		return mCurrentState;
	}
	/*get next player type,it depend on the priority
	 * when CurrentDecodeType is default decode type,we allow to search the others decode type
	 * eg: CurrentDecodeType is HW. default type is HW. when we search,frist company priorityHWList. 
	 *     if no lower priority find,now, we allow to search prioritySWList.
	 *     CurrentDecodeType is HW. default type is SW. when we search,frist company priorityHWList.
	 *     if no lower priority find,now ,we return no find.
	 * First  Priority : DecodeType
	 * Second Priority : priority
	 * 
	 * Return: PlayerState*/
	public PlayerState getNextType(int Type) throws Exception{
		if(!JoyplusMediaPlayerManager.isTypeAvailable(Type))throw new Exception("unAvailableType"+Type);
		DecodeType DefaultDecodeType = JoyplusMediaPlayerManager.getInstance().getDecodeType();
		return getNextType(DefaultDecodeType,Type,true);
	}
	/*define by Jas@20130730 for get lower priority in list priorityHWList,prioritySWList*/
	private PlayerState getNextType(DecodeType type,int currentType,boolean Allow){
		PlayerState state = new PlayerState();
		if(DecodeType.Decode_HW == type){
			state.DecodeType = DecodeType.Decode_HW;
			state.PlayerType = getNextType(priorityHWList,currentType);
		}else if(DecodeType.Decode_SW == type){
			state.DecodeType = DecodeType.Decode_SW;
			state.PlayerType = getNextType(prioritySWList,currentType);
		}
		if(state.PlayerType <= NO_FIND){
			if(Allow){
				state = getNextType(type,currentType,false);
			}
		}
		return state;
	}
	/*define by Jas@20130730 for get lower priority in list like priorityHWList,prioritySWList*/
	private int getNextType(int[] list,int currentType){
		if(list == null || list.length<1)return NO_FIND;
		for(int m : list){
			if(currentType <= m)continue;
			return m;
		}
		return NO_FIND;
	}
	/*init SW priority ,reverse order*/
	private void InitprioritySWList(){
		if(SWPlayer<=0)return;
		prioritySWList = new int[SWPlayer];
		int insertionPoint = SWPlayer-1;
        int currentLowest = 0;
        int nextLowest = 0;
        while (insertionPoint > -1) {
            for (JoyplusPlayerConfig na : mPlayerConfigs) {
                if (na == null || !na.DECODE_SW || !na.EN)    continue;
                if (na.PRIORITY_SW < currentLowest) continue;
                if (na.PRIORITY_SW > currentLowest) {
                    if (na.PRIORITY_SW < nextLowest || nextLowest == 0) {
                        nextLowest = na.PRIORITY_SW;
                    }
                    continue;
                }
                prioritySWList[insertionPoint--] = na.TYPE;
            }
            currentLowest = nextLowest;
            nextLowest = 0;
        }
        if(Debug){
        	for(int SW :prioritySWList){
        		Log.d(TAG,"prioritySWList mPlayerConfigs["+SW+"]"+mPlayerConfigs[SW].toString());
        	}
        }
	}
	
	/*init HW priority ,reverse order*/
	private void InitpriorityHWList(){
		if(HWPlayer<=0)return;
		priorityHWList = new int[HWPlayer];
	    int insertionPoint = HWPlayer-1;
        int currentLowest = 0;
        int nextLowest = 0;
        while (insertionPoint > -1) {
            for (JoyplusPlayerConfig na : mPlayerConfigs) {
                if (na == null || !na.DECODE_HW || !na.EN)    continue;
                if (na.PRIORITY_HW < currentLowest) continue;
                if (na.PRIORITY_HW > currentLowest) {
                    if (na.PRIORITY_HW < nextLowest || nextLowest == 0) {
                        nextLowest = na.PRIORITY_HW;
                    }
                    continue;
                }
                priorityHWList[insertionPoint--] = na.TYPE;
            }
            currentLowest = nextLowest;
            nextLowest = 0;
        }
        if(Debug){
        	for(int HW :priorityHWList){
        		Log.d(TAG,"priorityHWList mPlayerConfigs["+HW+"]"+mPlayerConfigs[HW].toString());
        	}
        }
	}
	
    public class PlayerState{
    	/*MediaPlayer TYPE
    	 * @link{#JoyplusMediaPlayerManager}
    	 * see@# JoyplusMediaPlayerManager TYPE_UNKNOW
    	 * see@# JoyplusMediaPlayerManager TYPE_MEDIAPLAYER
    	 * see@# JoyplusMediaPlayerManager TYPE_VITAMIO
    	 * */
    	public int        PlayerType ;
    	/*MediaPlayer DecodeType
    	 * @link{# JoyplusPlayer}
    	 * see@# JoyplusPlayer DecodeType*/
    	public DecodeType DecodeType ;
    	/*Save player current state
    	 * it will use to restore */
    	public MediaInfo  Info;
    	public PlayerState(){    
    		DecodeType = JoyplusMediaPlayerManager.getInstance().getDecodeType();
    		if(DecodeType == DecodeType.Decode_HW){
    			if(priorityHWList != null && priorityHWList.length>0){
    				PlayerType = priorityHWList[0];
    			}else if(prioritySWList != null && prioritySWList.length>0){
    				DecodeType = DecodeType.Decode_SW;
    				PlayerType = prioritySWList[0];
    			}else{
    				PlayerType = JoyplusMediaPlayerManager.TYPE_UNKNOW;
    			}
    		}else if(DecodeType == DecodeType.Decode_SW){
    			if(prioritySWList != null && prioritySWList.length>0){
    				PlayerType = prioritySWList[0];
    			}else if(priorityHWList != null && priorityHWList.length>0){
    				DecodeType = DecodeType.Decode_HW;
    				PlayerType = priorityHWList[0];
    			}else{
    				PlayerType = JoyplusMediaPlayerManager.TYPE_UNKNOW;
    			}
    		}
    		if(JoyplusMediaPlayerManager.isTypeAvailable(PlayerType)){
    			Info = new MediaInfo();//Info will report by MediaPlayer monitor
    		}
    		
    	}
    	public PlayerState(PlayerState state){
    		if(state != null){
    			this.PlayerType = state.PlayerType;
    			this.DecodeType = state.DecodeType;
    			this.Info       = state.Info.CreateMediaInfo();
    		}
    	}
    	public String toString(){
    		StringBuffer sb = new StringBuffer();
            sb.append("PlayerState{ PlayerType: ").append(JoyplusMediaPlayerManager.getPlayerTypeName(PlayerType)).
               append(", DecodeType:").append(JoyplusMediaPlayerManager.getInstance().getDecodeName(DecodeType)).
               append(", MediaInfo:").append(Info.toString());
            return sb.toString();
    	}
    }
    
    /*Interface of switch player*/
    public boolean SwitchPlayer(){
    	try {
    		if(JoyplusMediaPlayerManager.isTypeAvailable(mCurrentState.PlayerType)){
				PlayerState nextPlayer = getNextType(mCurrentState.PlayerType);
				if(NO_FIND != nextPlayer.PlayerType && JoyplusMediaPlayerManager.isTypeAvailable(nextPlayer.PlayerType)){
					mCurrentState = new PlayerState(nextPlayer);
					return true;
				}
    		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	InitPlayer();
    	return false;
    }
    public void InitPlayer(){
    	mCurrentState = new PlayerState();
    }
    /*Interface of Listener*/
	public void registerListener(JoyplusMediaPlayerListener listener){
		if(listener == null)return;
		mStateTracker.registerListener(listener);
	}
	public void unregisterListener(JoyplusMediaPlayerListener listener){
		if(listener == null)return;
		mStateTracker.unregisterListener(listener);
	}
	public void unregisterAllListener(){
		mStateTracker.unregisterAllListener();
	}
	/*this handler was use to get info for MediaPlayerMonitor*/
	public Handler getMediaPlayerHandler(){
		return mMediaPlayerHandler;
	}
	private Handler mMediaPlayerHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case JoyplusPlayerMonitor.MSG_STATEUPDATE:
				MediaInfo info = ((MediaInfo) msg.obj).CreateMediaInfo();
				if(info != null){
					//mCurrentState.Info = info;
					mStateTracker.notifyMediaInfo(info);
					if(info.getState() == STATE.MEDIA_STATE_UNKNOW ){
						//SwitchPlayer();
						mStateTracker.notifyMediaError();
					}else if(info.getState() == STATE.MEDIA_STATE_FINISH){
						mStateTracker.notifyMediaCompletion();
					}
				}
				break;
			case JoyplusPlayerMonitor.MSG_NOPROCESSCOMMEND:
				mStateTracker.NoProcessCommend((String)msg.obj);
				break;
			}
		}
		
	};
	private void notityNoProcess(String commend){
		if(Debug)Log.d(TAG,"notityNoProcess("+commend+")");
 		Message m = Message.obtain(mMediaPlayerHandler, JoyplusPlayerMonitor.MSG_NOPROCESSCOMMEND,"MSG_NOPROCESSCOMMEND");
		m.obj     = commend;
		mMediaPlayerHandler.removeCallbacksAndMessages("MSG_NOPROCESSCOMMEND");
		mMediaPlayerHandler.sendMessage(m);
 	 }
		
}
