/****************************************************************************
Copyright (c) 2010-2013 cocos2d-x.org

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 ****************************************************************************/
package org.cocos2dx.lib;

import java.net.URLEncoder;

import org.cocos2dx.lib.Cocos2dxHelper.Cocos2dxHelperListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joyplus.JoyplusMediaPlayerActivity;
import com.joyplus.tvhelper.MyApp;
import com.joyplus.tvhelper.PlayBaiduActivity;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.db.DBServices;
import com.joyplus.tvhelper.entity.BaiduVideoInfo;
import com.joyplus.tvhelper.entity.CurrentPlayDetailData;
import com.joyplus.tvhelper.entity.MoviePlayHistoryInfo;
import com.joyplus.tvhelper.entity.XLLXFileInfo;
import com.joyplus.tvhelper.faye.FayeService;
import com.joyplus.tvhelper.https.HttpUtils;
import com.joyplus.tvhelper.utils.Constant;
import com.joyplus.tvhelper.utils.Global;
import com.joyplus.tvhelper.utils.HttpTools;
import com.joyplus.tvhelper.utils.LevelMore;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.PreferencesUtils;
import com.joyplus.tvhelper.utils.Utils;
import com.umeng.analytics.MobclickAgent;

public abstract class Cocos2dxActivity extends Activity implements Cocos2dxHelperListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String TAG = Cocos2dxActivity.class.getSimpleName();

	// ===========================================================
	// Fields
	// ===========================================================
	
	private Cocos2dxGLSurfaceView mGLSurfaceView;
	
	private ImageView icon_net_statue;
	private TextView text_net_statue;
	private LevelMore navigationBar;
	
	private String umeng_channel;
	private Cocos2dxHandler mHandler;
	private static Context sContext = null;
	private View rootView;
	private static MyApp app;
	
	public static Context getContext() {
		return sContext;
	}
	
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(Global.ACTION_BAND_SUCCESS.equals(action)){
				updateQQ();
			}else if(Global.ACTION_UN_BAND_SUCCESS.equals(action)){
				Cocos2dxHelper.updateQQdisplay();
			}else if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())){
				
			}else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
				checkNetStatue();
			}
		}
	};
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sContext = this;
    	this.mHandler = new Cocos2dxHandler(this);

    	this.init();

		Cocos2dxHelper.init(this, this);
		app = (MyApp)getApplication();
		
		IntentFilter filter = new IntentFilter(Global.ACTION_BAND_SUCCESS);
		filter.addAction(Global.ACTION_UN_BAND_SUCCESS);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, filter);
		
		ApplicationInfo info = null;
		try {
			info = this.getPackageManager().getApplicationInfo(getPackageName(),
			        PackageManager.GET_META_DATA);
			umeng_channel = info.metaData.getString("UMENG_CHANNEL");
			Log.d(TAG, "key--->" + "URL"+ umeng_channel);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(umeng_channel==null||umeng_channel.length()==0){
			umeng_channel = "j001";
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onResume() {
		super.onResume();

		Cocos2dxHelper.onResume();
		this.mGLSurfaceView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

		Cocos2dxHelper.onPause();
		this.mGLSurfaceView.onPause();
	}

	@Override
	public void showDialog(final String pTitle, final String pMessage) {
		Message msg = new Message();
		msg.what = Cocos2dxHandler.HANDLER_SHOW_DIALOG;
		msg.obj = new Cocos2dxHandler.DialogMessage(pTitle, pMessage);
		this.mHandler.sendMessage(msg);
	}

	@Override
	public void showEditTextDialog(final String pTitle, final String pContent, final int pInputMode, final int pInputFlag, final int pReturnType, final int pMaxLength) { 
		Message msg = new Message();
		msg.what = Cocos2dxHandler.HANDLER_SHOW_EDITBOX_DIALOG;
		msg.obj = new Cocos2dxHandler.EditBoxMessage(pTitle, pContent, pInputMode, pInputFlag, pReturnType, pMaxLength);
		this.mHandler.sendMessage(msg);
	}
	
	@Override
	public void generatePincode() {
		// TODO Auto-generated method stub
		if(!HttpUtils.isNetworkAvailable(this)){
			mHandler.sendEmptyMessage(Cocos2dxHandler.HANDLER_NET_NOT_CONNECT);
			return;
		}
		MyApp.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
//				Map<String, String> params = new HashMap<String, String>();
//				params.put("md", Utils.getMacAdd(Cocos2dxActivity.this));
//				params.put("c", new Build().MODEL);
//				Log.d(TAG, "client = " + new Build().MODEL);
//				String str = HttpTools.post(Cocos2dxActivity.this, Constant.BASE_URL+"/make_pin", params);
				String str = HttpTools.get(Cocos2dxActivity.this, Constant.BASE_URL+"/make_pin?md="+
						Utils.getMacAdd(Cocos2dxActivity.this)+"&c="+URLEncoder.encode(new Build().MODEL));
				Log.d(TAG, str);
				try {
					JSONObject data = new JSONObject(str);
					String pincode = data.getString("pinCode");
					String channel = data.getString("channel");
					String token = data.getString("token");
					String erweima_url = data.getString("pre_url");
					PreferencesUtils.setPincode(Cocos2dxActivity.this, pincode);
					PreferencesUtils.setChannel(Cocos2dxActivity.this, channel);
					PreferencesUtils.setPincodeMd5(Cocos2dxActivity.this, null);
					PreferencesUtils.setToken(sContext, token);
					PreferencesUtils.setErweima_url(sContext, erweima_url);
					mHandler.sendEmptyMessage(Cocos2dxHandler.MESSAGE_GETPINCODE_SUCCESS);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mHandler.sendEmptyMessage(Cocos2dxHandler.MESSAGE_GETPINCODE_FAILE);
				}
			}
		});
	}
	
	@Override
	public void showAnimationToast(final String msg) {
		// TODO Auto-generated method stub
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Utils.showToast(sContext, msg, rootView);
			}
		});
	}
	
	@Override
	public void showTitle(final int index, final String msg) {
		// TODO Auto-generated method stub
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				switch (index) {
				case 1:
					navigationBar.getFristLevel(msg);
					break;
				case 2:
					navigationBar.getSecondLevel(msg);
					break;
				}
			}
		});
	}
	
	@Override
	public void hideTitle(final int index) {
		// TODO Auto-generated method stub
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				switch (index) {
				case 1:
					navigationBar.dismissFristLevel();
					break;
				case 2:
					navigationBar.dismissSecondLevel();
					break;
				}
			}
		});
	}
	
	@Override
	public void playVideo(String str) {
		// TODO Auto-generated method stub
		Log.d(TAG, "str" + str);
		try{
			DBServices dbServices = DBServices.getInstance(sContext);
			JSONObject obj = new JSONObject(str);
			int type = obj.getInt("type");
			JSONObject date = obj.getJSONObject("date");
			CurrentPlayDetailData playDate;
			switch (type) {
			case 0://推送历史
				int _id = date.getInt("_id");
				int isDir = date.getInt("isDir");
				String sub_name = null;
				MoviePlayHistoryInfo playInfo = dbServices.queryMoviePlayHistoryById(_id);
				if(playInfo==null){
					com.joyplus.tvhelper.utils.Log.e(TAG, "play_info is null");
					return;
				}
				playDate = new CurrentPlayDetailData();
				if(isDir == 1){
					sub_name = date.getString("sub_name");
					if(sub_name==null){
						com.joyplus.tvhelper.utils.Log.e(TAG, "sub_name is null");
						return ;
					}
					playDate.prod_type = JoyplusMediaPlayerActivity.TYPE_PUSH_BT_EPISODE;
					playDate.prod_sub_name = sub_name;
				}else{
					playDate.prod_type = JoyplusMediaPlayerActivity.TYPE_PUSH;
				}
				playDate.prod_name = playInfo.getName();
				playDate.obj = playInfo;
				Log.d(TAG, "prod_type" + playDate.prod_type);
				if((playInfo.getDuration()-playInfo.getPlayback_time())<=Constant.END_TIME){
					playDate.prod_time = 0;
				}else{
					playDate.prod_time = Math.round(playInfo.getPlayback_time()*1000);
				}
				Log.d(TAG, "prod_time" + playDate.prod_time);
				playDate.prod_qua = playInfo.getDefination();
				playDate.isOnline = false;
				app.setmCurrentPlayDetailData(playDate);
				app.set_ReturnProgramView(null);
				startActivity(Utils.getIntent(getContext()));
				break;
			case 1://推送历史——百度
				int _id_baidu = date.getInt("_id");
				MoviePlayHistoryInfo play_info_baidu = dbServices.queryMoviePlayHistoryById(_id_baidu);
				play_info_baidu.setCreat_time(System.currentTimeMillis());
				dbServices.updateMoviePlayHistory(play_info_baidu);
				Intent intent_baidu = new Intent(this,PlayBaiduActivity.class);
				intent_baidu.putExtra("url", play_info_baidu.getRecivedDonwLoadUrls());
				intent_baidu.putExtra("name", play_info_baidu.getName());
				intent_baidu.putExtra("push_url", play_info_baidu.getPush_url());
				intent_baidu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent_baidu);
				break;
			case 2://迅雷离线
				XLLXFileInfo xunleiInfo = new XLLXFileInfo();
				xunleiInfo.createTime = date.getString("createTime");
				xunleiInfo.duration = date.getString("duration");
				xunleiInfo.file_name = date.getString("file_name");
				xunleiInfo.filesize = date.getString("filesize");
				xunleiInfo.gcid = date.getString("gcid");
				xunleiInfo.src_url = date.getString("src_url");
				xunleiInfo.userid = date.getString("userid");
				xunleiInfo.isDir = (date.getInt("isDir")==1)?true:false;
				String sub_name_xunlei = null;
				if(xunleiInfo.isDir){
					sub_name_xunlei = date.getString("sub_name");
					JSONArray array = date.getJSONArray("item_list");
					XLLXFileInfo [] btfiles = new XLLXFileInfo[array.length()];
					for(int i=0; i<array.length(); i++){
						JSONObject item =  array.getJSONObject(i);
						XLLXFileInfo info_item = new XLLXFileInfo();
						info_item.createTime = item.getString("createTime");
						info_item.duration = item.getString("duration");
						info_item.file_name = item.getString("file_name");
						info_item.filesize = item.getString("filesize");
						info_item.gcid = item.getString("gcid");
						info_item.src_url = item.getString("src_url");
						info_item.userid = item.getString("userid");
						btfiles[i] = info_item;
					}
					xunleiInfo.btFiles = btfiles;
					CurrentPlayDetailData currentPlayDetailData = new CurrentPlayDetailData();
					currentPlayDetailData.prod_url = xunleiInfo.src_url;
					currentPlayDetailData.prod_type = JoyplusMediaPlayerActivity.TYPE_XUNLEI_BT_EPISODE;
					currentPlayDetailData.prod_name = xunleiInfo.file_name;
					currentPlayDetailData.prod_sub_name = sub_name_xunlei;
					currentPlayDetailData.prod_qua = PreferencesUtils.getDefualteDefination(sContext);
					currentPlayDetailData.obj = xunleiInfo.btFiles;
					app.setmCurrentPlayDetailData(currentPlayDetailData);
					startActivity(Utils.getIntent(getContext()));
				}else{
					CurrentPlayDetailData currentPlayDetailData = new CurrentPlayDetailData();
					currentPlayDetailData.prod_url = xunleiInfo.src_url;
					currentPlayDetailData.prod_type = JoyplusMediaPlayerActivity.TYPE_XUNLEI;
					currentPlayDetailData.prod_name = xunleiInfo.file_name;
					currentPlayDetailData.prod_qua = PreferencesUtils.getDefualteDefination(sContext);
					currentPlayDetailData.obj = xunleiInfo;
					app.setmCurrentPlayDetailData(currentPlayDetailData);
					startActivity(Utils.getIntent(getContext()));
				}
				
				break;
			case 3://百度云
				BaiduVideoInfo info = new BaiduVideoInfo();
				info.setFileName(date.getString("filename"));
				info.setFs_id(date.getLong("fs_id"));
				info.setPath(date.getString("path"));
				playDate = new CurrentPlayDetailData();
				playDate.obj = info;
				playDate.prod_name = info.getFileName();
				playDate.prod_type = JoyplusMediaPlayerActivity.TYPE_BAIDU;
				playDate.prod_qua = PreferencesUtils.getDefualteDefination(sContext);
				playDate.obj = info;
				app.setmCurrentPlayDetailData(playDate);
				startActivity(Utils.getIntent(getContext()));
				break;
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public void showXunLeiLoginDialog() { 
		Message msg = new Message();
		msg.what = Cocos2dxHandler.HANDLER_SHOW_XUNLEI_lOGGIN_DIALOG;
		this.mHandler.sendMessage(msg);
	}
	
	@Override
	public void startService() {
		// TODO Auto-generated method stub
		startService(new Intent(this,FayeService.class));
	}
	
	@Override
	public void startSetting() {
		// TODO Auto-generated method stub
		
		Message msg = new Message();
		msg.what = Cocos2dxHandler.HANDLER_SHOW_SETTING_DIALOG;
		this.mHandler.sendMessage(msg);
	}
	
	@Override
	public void updateQQ() {
		// TODO Auto-generated method stub
		MyApp.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				String url = Constant.BASE_URL + "/account/getuserinfo?pin="+
				PreferencesUtils.getPincode(sContext)+"&md="+Utils.getMacAdd(sContext);
				
				String str = HttpTools.get(sContext, url);
				Log.d(TAG, "updateQQ-->result-->"+str);
				try{
					
					JSONObject obj = new JSONObject(str);
					boolean statue = obj.getBoolean("status");
					if(statue){
						String nickname = obj.getString("nickname");
						String avatare = obj.getString("figureurl");
						PreferencesUtils.setQQName(sContext, nickname);
						PreferencesUtils.setQQAvatare(sContext, avatare);
					}else{
						//失败了、、
					}
					
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
				Message msg = new Message();
				msg.what = Cocos2dxHandler.HANDLER_UPDATE_QQ;
				mHandler.sendMessage(msg);
			}
		});
	}
	
	@Override
	public String getDisplayWebUrl() {
		// TODO Auto-generated method stub
			String online_base_url = MobclickAgent.getConfigParams(this, "URL"+ umeng_channel);
		Log.d(TAG, "online_base_url----->" + online_base_url);
		if(online_base_url!=null&&online_base_url.length()>0){
			PreferencesUtils.setWebUrl(this, online_base_url);
		}else{
			online_base_url = "tt.showkey.tv";
		}
		return online_base_url;
	}
	
	@Override
	public void showBaiduDailog() {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.what = Cocos2dxHandler.HANDLER_SHOW_BAIDU_lOGGIN_DIALOG;
		this.mHandler.sendMessage(msg);
	}

	@Override
	public void runOnGLThread(final Runnable pRunnable) {
		this.mGLSurfaceView.queueEvent(pRunnable);
	}

	// ===========================================================
	// Methods
	// ===========================================================
	public void init() {
		
    	// FrameLayout
        ViewGroup.LayoutParams framelayout_params =
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                       ViewGroup.LayoutParams.FILL_PARENT);
//        FrameLayout framelayout = new FrameLayout(this);
//        framelayout.setLayoutParams(framelayout_params);
//
//        // Cocos2dxEditText layout
//        ViewGroup.LayoutParams edittext_layout_params =
//            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
//                                       ViewGroup.LayoutParams.WRAP_CONTENT);
//        ViewGroup.LayoutParams back_params =
//        		new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
//        				ViewGroup.LayoutParams.FILL_PARENT);
//        ViewGroup.LayoutParams label_params =
//        		new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
//        				ViewGroup.LayoutParams.WRAP_CONTENT);
//        Cocos2dxEditText edittext = new Cocos2dxEditText(this);
//        ImageView back = new ImageView(this);
//        back.setScaleType(ScaleType.FIT_XY);
//        back.setBackgroundResource(R.drawable.back);
//        back.setLayoutParams(back_params);
//        edittext.setLayoutParams(edittext_layout_params);
//
//        TextView tv = new TextView(this);
//        tv.setText("V2内测版");
//        tv.setGravity(Gravity.RIGHT);
//        tv.setTextSize(20);
//        tv.setPadding(0	, Utils.getStandardValue(this, 50), Utils.getStandardValue(this, 40), 0);
//        // ...add to FrameLayout
//        framelayout.addView(edittext);
//        framelayout.addView(back);
//        framelayout.addView(tv);
//        // Cocos2dxGLSurfaceView
        
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        RelativeLayout framelayout = (RelativeLayout)rootView.findViewById(R.id.main_fram);
        text_net_statue = (TextView) rootView.findViewById(R.id.ssid_text);
        icon_net_statue = (ImageView) rootView.findViewById(R.id.net_statue);
        this.mGLSurfaceView = this.onCreateView();
        // ...add to FrameLayout
        framelayout.addView(this.mGLSurfaceView);
        // Switch to supported OpenGL (ARGB888) mode on emulator
        //if (isAndroidEmulator())
        this.mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.mGLSurfaceView.setCocos2dxRenderer(new Cocos2dxRenderer());
        
        navigationBar = new LevelMore(this);
        Point size = new Point(1270, 800);
        getWindowManager().getDefaultDisplay().getSize(size);
        int leftMargin = (160*size.x)/1920;
        int topMargin = ((1080-880)*size.y)/1080;
        RelativeLayout.LayoutParams navigationBar_params =
		new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
        Log.d(TAG, "left = " + leftMargin +" and top = " + topMargin);
		navigationBar_params.setMargins(leftMargin, topMargin, 0, 0);
        framelayout.addView(navigationBar,navigationBar_params);
        // Set framelayout as the content view
		setContentView(rootView);
		checkNetStatue();
	}
	
	public View getRootView(){
		return rootView;
	}
	
    private void checkNetStatue() {
		// TODO Auto-generated method stub
        ConnectivityManager cm= (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo info = cm.getActiveNetworkInfo();  
         if (info != null && info.isAvailable() && info.getState() == NetworkInfo.State.CONNECTED){  
//             if(ConnectivityManager.TYPE_MOBILE==info.getType()){  //3G网络 
//            	 icon_net_statue.setImageResource(R.drawable.icon_mobile);
//            	 text_net_statue.setText("");
//             }else 
        	 if(ConnectivityManager.TYPE_WIFI==info.getType()){  //wifi  
	        	 
	        	 WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	        	 WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	        	 if(wifiInfo!=null){
	        		 text_net_statue.setText(wifiInfo.getSSID());
	        		 int level = wifiInfo.getRssi();
	        		 Log.d(TAG, "wifi leve ----->"+ level);
	        		 if(level>-50){
	        			 icon_net_statue.setImageResource(R.drawable.icon_wifi_3);
	        		 }else if(level>-75){
	        			 icon_net_statue.setImageResource(R.drawable.icon_wifi_2);
	        		 }else{
	        			 icon_net_statue.setImageResource(R.drawable.icon_wifi_1);
	        		 }
	        	 }else{
	        		 text_net_statue.setText("");
	        		 icon_net_statue.setImageResource(R.drawable.icon_wifi_1);
	        	 }
             }else if(ConnectivityManager.TYPE_ETHERNET==info.getType()){  //有线网络 
            	 icon_net_statue.setImageResource(R.drawable.icon_ethernet);
            	 text_net_statue.setText("已连接");
             }else{ //未知
            	 icon_net_statue.setImageDrawable(null);
            	 text_net_statue.setText("");
             }
        }else if(info ==null){  //未连接
        	icon_net_statue.setImageResource(R.drawable.icon_disconect);
       	 	text_net_statue.setText(R.string.main_net_statue_not_connect);
        }  
              
	}

	public Cocos2dxGLSurfaceView onCreateView() {
    	return new Cocos2dxGLSurfaceView(this);
    }
	
   @Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	   Log.d(TAG, "onKeyDown" + keyCode);
	   if(!mGLSurfaceView.hasFocus()){
		   this.mGLSurfaceView.requestFocus();
		   return super.onKeyDown(keyCode, event);
	   }
	   return true;
//	   return this.mGLSurfaceView.onKeyDown(keyCode, event);
//	return super.onKeyDown(keyCode, event);
}
   
   @Override
protected void onDestroy() {
	// TODO Auto-generated method stub
	 unregisterReceiver(mReceiver);
	super.onDestroy();
}
   
   
   
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
