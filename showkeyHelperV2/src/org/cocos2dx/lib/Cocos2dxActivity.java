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

import java.util.HashMap;
import java.util.Map;

import org.cocos2dx.lib.Cocos2dxHelper.Cocos2dxHelperListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.joyplus.JoyplusMediaPlayerActivity;
import com.joyplus.tvhelper.MyApp;
import com.joyplus.tvhelper.PlayBaiduActivity;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.SettingActivity;
import com.joyplus.tvhelper.db.DBServices;
import com.joyplus.tvhelper.entity.BaiduVideoInfo;
import com.joyplus.tvhelper.entity.CurrentPlayDetailData;
import com.joyplus.tvhelper.entity.MoviePlayHistoryInfo;
import com.joyplus.tvhelper.entity.XLLXFileInfo;
import com.joyplus.tvhelper.faye.FayeService;
import com.joyplus.tvhelper.https.HttpUtils;
import com.joyplus.tvhelper.utils.Constant;
import com.joyplus.tvhelper.utils.HttpTools;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.PreferencesUtils;
import com.joyplus.tvhelper.utils.Utils;

public abstract class Cocos2dxActivity extends Activity implements Cocos2dxHelperListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String TAG = Cocos2dxActivity.class.getSimpleName();

	// ===========================================================
	// Fields
	// ===========================================================
	
	private Cocos2dxGLSurfaceView mGLSurfaceView;
	private Cocos2dxHandler mHandler;
	private static Context sContext = null;
	
	private static MyApp app;
	
	public static Context getContext() {
		return sContext;
	}
	
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
			Utils.showToast(this, "检查网络设置");
			mHandler.sendEmptyMessage(Cocos2dxHandler.MESSAGE_GETPINCODE_FAILE);
			return;
		}
		MyApp.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Map<String, String> params = new HashMap<String, String>();
				params.put("app_key", Constant.APPKEY);
				params.put("mac_address", Utils.getMacAdd(Cocos2dxActivity.this));
				params.put("client", new Build().MODEL);
//				Log.d(TAG, "client = " + new Build().MODEL);
				String str = HttpTools.post(Cocos2dxActivity.this, Constant.BASE_URL+"/generatePinCode", params);
				Log.d(TAG, str);
				try {
					JSONObject data = new JSONObject(str);
					String pincode = data.getString("pinCode");
					String channel = data.getString("channel");
					PreferencesUtils.setPincode(Cocos2dxActivity.this, pincode);
					PreferencesUtils.setChannel(Cocos2dxActivity.this, channel);
					PreferencesUtils.setPincodeMd5(Cocos2dxActivity.this, null);
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
					currentPlayDetailData.obj = xunleiInfo.btFiles;
					app.setmCurrentPlayDetailData(currentPlayDetailData);
					startActivity(Utils.getIntent(getContext()));
				}else{
					CurrentPlayDetailData currentPlayDetailData = new CurrentPlayDetailData();
					currentPlayDetailData.prod_url = xunleiInfo.src_url;
					currentPlayDetailData.prod_type = JoyplusMediaPlayerActivity.TYPE_XUNLEI;
					currentPlayDetailData.prod_name = xunleiInfo.file_name;

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
        FrameLayout framelayout = new FrameLayout(this);
        framelayout.setLayoutParams(framelayout_params);

        // Cocos2dxEditText layout
        ViewGroup.LayoutParams edittext_layout_params =
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                       ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewGroup.LayoutParams back_params =
        		new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
        				ViewGroup.LayoutParams.FILL_PARENT);
        Cocos2dxEditText edittext = new Cocos2dxEditText(this);
        ImageView back = new ImageView(this);
        back.setScaleType(ScaleType.FIT_XY);
        back.setBackgroundResource(R.drawable.back);
        back.setLayoutParams(back_params);
        edittext.setLayoutParams(edittext_layout_params);

        // ...add to FrameLayout
        framelayout.addView(edittext);
        framelayout.addView(back);
        // Cocos2dxGLSurfaceView
        this.mGLSurfaceView = this.onCreateView();

        // ...add to FrameLayout
        framelayout.addView(this.mGLSurfaceView);

        // Switch to supported OpenGL (ARGB888) mode on emulator
        //if (isAndroidEmulator())
        this.mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.mGLSurfaceView.setCocos2dxRenderer(new Cocos2dxRenderer());
        this.mGLSurfaceView.setCocos2dxEditText(edittext);

        
        // Set framelayout as the content view
		setContentView(framelayout);
	}
	
    public Cocos2dxGLSurfaceView onCreateView() {
    	return new Cocos2dxGLSurfaceView(this);
    }

   private final static boolean isAndroidEmulator() {
      String model = Build.MODEL;
      Log.d(TAG, "model=" + model);
      String product = Build.PRODUCT;
      Log.d(TAG, "product=" + product);
      boolean isEmulator = false;
      if (product != null) {
         isEmulator = product.equals("sdk") || product.contains("_sdk") || product.contains("sdk_");
      }
      Log.d(TAG, "isEmulator=" + isEmulator);
      return isEmulator;
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
   
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
