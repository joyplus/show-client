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

import org.cocos2dx.lib.Cocos2dxHelper.Cocos2dxHelperListener;
import org.json.JSONObject;

import com.joyplus.JoyplusMediaPlayerActivity;
import com.joyplus.adkey.Util;
import com.joyplus.tvhelper.MyApp;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.entity.BaiduVideoInfo;
import com.joyplus.tvhelper.entity.CurrentPlayDetailData;
import com.joyplus.tvhelper.faye.FayeService;
import com.joyplus.tvhelper.utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

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
	public void playVideo(String str) {
		// TODO Auto-generated method stub
		try{
			JSONObject obj = new JSONObject(str);
			int type = obj.getInt("type");
			JSONObject date = obj.getJSONObject("date");
			switch (type) {
			case 0://推送历史
				
				break;
			case 1://推送历史——百度
				
				break;
			case 2://迅雷离线
				
				break;
			case 3://百度云
				BaiduVideoInfo info = new BaiduVideoInfo();
				info.setFileName(date.getString("filename"));
				info.setFs_id(date.getLong("fs_id"));
				info.setPath(date.getString("path"));
				CurrentPlayDetailData playDate = new CurrentPlayDetailData();
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
