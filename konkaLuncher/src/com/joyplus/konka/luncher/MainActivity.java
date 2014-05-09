package com.joyplus.konka.luncher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher.ViewFactory;

import com.joyplus.Config.ADConfig;
import com.joyplus.adkey.widget.SerializeManager;
import com.joyplus.konka.update.UmengUpdate;
import com.joyplus.konka.utils.DensityUtil;
import com.joyplus.konka.utils.Log;
import com.joyplus.konka_jas.joyplus.konka.ADRequest;
import com.joyplus.request.AdInfo;
import com.joyplus.tvhelper.ui.MyScrollLayout;

public class MainActivity extends Activity implements ViewFactory, OnClickListener, OnFocusChangeListener , PageController, OnKeyListener{

	private static final String TAG =  MainActivity.class.getSimpleName();
	private ImageSwitcher mSwitcher;
//	private int[] arrayPictures = {R.drawable.p1,R.drawable.p2,R.drawable.p3,R.drawable.p4};
	private List<Drawable> pictures = new ArrayList<Drawable>();
	private int pictureIndex = -1;
	private Handler mHandler;
	private static final int MESSAGE_NEXT = 0;
	private static final int MESSAGE_UPDATE_PICTURE = MESSAGE_NEXT + 1;
	private static final int MESSAGE_UPDATE_BANGDAN = MESSAGE_UPDATE_PICTURE + 1;
	private static final int DELAY_TIME = 5*1000;
	private static final int DELAY_TIME_BANGDAN = 30*1000;
	private static final int INITPICTURE_TIME = 60*1000;
	/**
	 * 盛辉下载banner的存储路径
	 */
//	private static final String IMAGE_PATH = "/mnt/sdcard/Jas_1001"; //banner 
//	private static final String IMAGE_PATH_DEBUG = "/mnt/sdcard/Jas"; //banner_debug
	/**
	 * 盛辉下载bangdan的存储路径
	 */
//	private static final String BD_PATH = "/mnt/sdcard/Joyplus_video"; //bangdan
//	private static final String ID = "9a51d0c16fa83008eba3001aa892b901";
//	public static final String html5BaseUrl = "http://download.joyplus.tv/app/item.html?s="+ID;
//	public static final String BaseUrl      = "http://advapi.joyplus.tv/advapi/v1/topic/get?s="+ID;
	private Animation animation_in;
	private Animation animation_out;
	private ScaleAnimEffect animEffect;
	private FrameLayout layout;
	private ImageView whiteBorder;
	private ImageView bangdan;
	private MyScrollLayout myScrollLayout;
	private SkyworthLuncher skyFrament;
	private HaierLuncher haierFrament;
	private TclLuncher tclFrament;
//	private RelativeLayout bangdan_layout;
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent arg1) {
			// TODO Auto-generated method stub
			ConnectivityManager manager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);  
//	        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
	        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
	        NetworkInfo ethernetInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);  
//	        NetworkInfo activeInfo = manager.getActiveNetworkInfo();  
	        if(wifiInfo.isConnected() || ethernetInfo.isConnected()){  
	        	UmengUpdate.update(MainActivity.this);
	        }
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
		layout = (FrameLayout) findViewById(R.id.fram_items);
		whiteBorder = (ImageView) findViewById(R.id.white_borad);
		bangdan = (ImageView) findViewById(R.id.image_bangdan);
		myScrollLayout = (MyScrollLayout) findViewById(R.id.content_layout);
		skyFrament = (SkyworthLuncher) getFragmentManager().findFragmentById(R.id.view_skyworth);
		haierFrament = (HaierLuncher) getFragmentManager().findFragmentById(R.id.view_haier);
		tclFrament = (TclLuncher) getFragmentManager().findFragmentById(R.id.view_tcl);
		haierFrament.setPageController(this);
		skyFrament.setPageController(this);
		tclFrament.setPageController(this);
//		bangdan_layout = (RelativeLayout) findViewById(R.id.layout_bangdan);
		mSwitcher.setFactory(this);
//		mSwitcher.setOnClickListener(this);
		animation_in = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
		animation_out = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case MESSAGE_NEXT:
					showNext();
					break;
				case MESSAGE_UPDATE_PICTURE:
					updatePictures();
					break;
				case MESSAGE_UPDATE_BANGDAN:
					updateBangdan();
					break;
				default:
					break;
				}
			}
		};
		pictureIndex = -1;
		initPicturesDrawble();
		this.animEffect = new ScaleAnimEffect();
		for(int i=0; i<layout.getChildCount(); i++){
			View view = layout.getChildAt(i);
			view.setFocusable(true);
			view.setFocusableInTouchMode(true);
			view.setOnFocusChangeListener(MainActivity.this);
			view.setOnKeyListener(this);
			view.setOnClickListener(this);
		}
		whiteBorder.setFocusable(false);
		whiteBorder.setFocusableInTouchMode(false);
		File f = new File(ADConfig.BD_PATH + "/ADFILE");
		if(f.exists()){
			Drawable d = Drawable.createFromPath(f.getAbsolutePath());
			if(d != null){
				bangdan.setImageDrawable(d);
			}else{
				bangdan.setImageResource(R.drawable.item_bangdan);
			}
		}else{
			bangdan.setImageResource(R.drawable.item_bangdan);
		}
		
//		File dir = new File(ADRequest.getPicturesDrawble(1));
		List<Drawable> bgs = ADRequest.getPicturesDrawble(1);
		if(bgs.size()>0){
			findViewById(R.id.bg_konka).setBackgroundDrawable(bgs.get(0)); 
			layout.setAlpha(0.8f);
		}
		UmengUpdate.update(this);
		
		IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(mReceiver, intentFilter);
		mHandler.sendEmptyMessageDelayed(MESSAGE_NEXT, DELAY_TIME);
		mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_BANGDAN, DELAY_TIME_BANGDAN+DELAY_TIME/2);
		mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PICTURE, INITPICTURE_TIME/3);
	}
	
//	@Override
//	protected void onStart() {
//		// TODO Auto-generated method stub
//		super.onStart();
//		mHandler.sendEmptyMessageDelayed(MESSAGE_NEXT, DELAY_TIME);
//		mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_BANGDAN, DELAY_TIME_BANGDAN+DELAY_TIME/2);
//	}
	
//	@Override
//	protected void onStop() {
//		// TODO Auto-generated method stub
//		super.onStop();
//		mHandler.removeCallbacksAndMessages(null);
//	}
	
	
	private void updateBangdan(){
		
		Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
		animation.setDuration(100);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				File f = new File(ADConfig.BD_PATH + "/ADFILE");
				if(f.exists()){
					Log.d(TAG, "file exists");
					Drawable d = Drawable.createFromPath(f.getAbsolutePath());
					if(d != null){
						Log.d(TAG, "Drawable exists");
						bangdan.setImageDrawable(d);
					}else{
						bangdan.setImageResource(R.drawable.item_bangdan);
					}
				}else{
					bangdan.setImageResource(R.drawable.item_bangdan);
				}
				Animation animation_show = AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in);
				animation_show.setDuration(400);
				bangdan.startAnimation(animation_show);
			}
		});
		bangdan.startAnimation(animation);
		mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_BANGDAN, DELAY_TIME_BANGDAN);
	}
	
	private void updatePictures(){
		initPicturesDrawble();
		mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PICTURE, INITPICTURE_TIME);
	}
	
	private void initPicturesDrawble(){
		this.pictures = ADRequest.getPicturesDrawble(0);
	}
	
	private void showNext(){
		if(pictures.size()>0){
			if(pictures.size() == 1){
				if(pictureIndex == -1){
					pictureIndex = pictureIndex >= (pictures.size() - 1) ? 0 : (pictureIndex + 1);
					// 设置图片切换的动画  
					mSwitcher.setInAnimation(animation_in);  
					mSwitcher.setOutAnimation(animation_out);  
			        // 设置当前要看的图片  
					mSwitcher.setImageDrawable(pictures.get(pictureIndex)); 
				}
			}else{
				pictureIndex = pictureIndex >= (pictures.size() - 1) ? 0 : (pictureIndex + 1);
				// 设置图片切换的动画  
				mSwitcher.setInAnimation(animation_in);  
				mSwitcher.setOutAnimation(animation_out);  
		        // 设置当前要看的图片  
				mSwitcher.setImageDrawable(pictures.get(pictureIndex));  
			}
		}else if(pictureIndex>=0){
			pictureIndex = -1;
			mSwitcher.setInAnimation(animation_in);  
			mSwitcher.setOutAnimation(animation_out);  
	        // 设置当前要看的图片  
			mSwitcher.setImageResource(R.drawable.p1);
		}
		Log.d(TAG, "-------------showNext------------=" + pictureIndex);
		mHandler.sendEmptyMessageDelayed(MESSAGE_NEXT, DELAY_TIME);
	}

	@Override
	public View makeView() {
		// TODO Auto-generated method stub
		Log.d(TAG, "-------------make view------------");
		ImageView imageView = new ImageView(this);  
        imageView.setImageResource(R.drawable.p1);  
        imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,FrameLayout.LayoutParams.FILL_PARENT));
        imageView.setScaleType(ScaleType.FIT_XY);
        return imageView;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		mHandler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		try{
			Intent intent = null;
			switch (view.getId()) {
			case R.id.layout_switcher:// banner
				break;
			case R.id.layout_bangdan:// bangdan
				AdInfo info  =  (AdInfo) new SerializeManager().readSerializableData(ADConfig.BD_PATH+"/ad");
				if(info!=null){
					JSONObject json = new JSONObject();
					intent = new Intent("com.joyplus.ad.test.view");
					if(info.mOPENTYPE == null || info.mOPENTYPE==AdInfo.OPENTYPE.ANDROID){
						json.put("type", 2);
						json.put("url", ADConfig.BaseUrl);
					}else{
						json.put("type", 0);
						json.put("url", ADConfig.html5BaseUrl);
					}
					intent.putExtra("data", json.toString());
				}
				break;
			case R.id.item_cloud_controller:// yunzhikong but now use us live tv
				intent = new Intent();
				intent.setClassName("tv.wan8.weisp", "tv.huan.epg.live.ui.HuanPlayerActivity");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				break;
			case R.id.item_beston: //baishitong
				break;
			case R.id.item_football://yingchao
				break;
			case R.id.item_basketball://NBA
				break;
			case R.id.item_appstore:// app store but now use as  car special area
				intent = new Intent(this, SpecialAreaActivity.class);
				break;
			case R.id.item_app_helper: // yingyong zhushou
				break;
			case R.id.item_class_room:// kuaikan xue tang
				break;
			case R.id.item_setting: //setting
				intent = new Intent(Settings.ACTION_SETTINGS);
				break;
			case R.id.item_youku: // youku
				intent = new Intent();
				intent.setClassName("com.youku.tv", "com.youku.tv.WelcomeActivity");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				break;
			case R.id.item_letv: // letv
				intent = new Intent();
				intent.setClassName("com.skyworth.onlinemovie.letv.csapp", "com.letv.tv.activity.WelcomeActivity");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				break;
			case R.id.item_broadcast: // duoping
				break;
			case R.id.item_manager: // guanjia
				intent = new Intent();
				intent.setClassName("com.qihoo360.mobilesafe_tv", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				break;
			case R.id.item_game: // game center
				break;
			case R.id.item_browser: // browser
				intent = new Intent();        
				intent.setAction("android.intent.action.VIEW");    
				Uri content_url = Uri.parse("http://www.joyplus.tv/");   
				intent.setData(content_url);  
				break;
			case R.id.item_more_app: // more app
				intent = new Intent(this, InstalledApplication.class);
				break;
			default:
				break;
			}
			if(intent != null){
				startActivity(intent);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		// TODO Auto-generated method stub
		if(hasFocus){
			if(view.getId() != R.id.layout_switcher){
				showOnFocusAnimation(view);
			}else{
				flyWhiteBorder(view.getMeasuredWidth()-DensityUtil.dip2px(this, 8), view.getMeasuredHeight()-DensityUtil.dip2px(this, 8), 
						view.getLeft()+DensityUtil.dip2px(this, 4), view.getTop()+DensityUtil.dip2px(this, 4));
			}
		}else{
			if(view.getId() != R.id.layout_switcher){
				showLooseFocusAnimation(view);
			}
		}
	}

	private void showOnFocusAnimation(final View v){
//		this.whiteBorder.setVisibility(View.VISIBLE);
		v.bringToFront();
		float sdx = ((float)v.getWidth()+DensityUtil.dip2px(this, 10))/v.getWidth();
		float sdy = ((float)v.getHeight()+DensityUtil.dip2px(this, 10))/v.getHeight();
		this.animEffect.setAttributs(1.0F, sdx, 1.0F, sdy, 100L);
		Animation localAnimation = this.animEffect.createAnimation();
		if(v.getId() == R.id.layout_bangdan){
			int left = v.getLeft();
			int top = v.getTop();
			int width = v.getWidth();
			int height = v.getHeight();
			FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(width+DensityUtil.dip2px(MainActivity.this, 14),
					height+DensityUtil.dip2px(MainActivity.this, 14));
			layoutparams.leftMargin = left-DensityUtil.dip2px(MainActivity.this, 7);
			layoutparams.topMargin = top-DensityUtil.dip2px(MainActivity.this, 7);
			v.setLayoutParams(layoutparams);
			v.layout(layoutparams.leftMargin, layoutparams.topMargin, 
					layoutparams.leftMargin + v.getWidth()+DensityUtil.dip2px(MainActivity.this, 14), 
					layoutparams.topMargin + v.getHeight()+DensityUtil.dip2px(MainActivity.this, 14));
			flyWhiteBorder(v.getMeasuredWidth(), v.getMeasuredHeight(), left, top);
		}else{
			v.startAnimation(localAnimation);
			flyWhiteBorder(v.getMeasuredWidth(), v.getMeasuredHeight(), v.getLeft(), v.getTop());
		}
	}
	
	private void showLooseFocusAnimation(final View v){
//		whiteBorder.setVisibility(View.GONE);
		float sdx = (v.getMeasuredWidth()+DensityUtil.dip2px(this, 10))/v.getMeasuredWidth();
		float sdy = (v.getMeasuredHeight()+DensityUtil.dip2px(this, 10))/v.getMeasuredHeight();
		this.animEffect.setAttributs(sdx, 1.0F, sdy, 1.0F, 100L);
		Animation localAnimation = this.animEffect.createAnimation();
		if(v.getId() == R.id.layout_bangdan){
			FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(v.getWidth()-DensityUtil.dip2px(MainActivity.this, 14),
					v.getHeight()-DensityUtil.dip2px(MainActivity.this, 14));
			layoutparams.leftMargin = v.getLeft()+DensityUtil.dip2px(MainActivity.this, 7);
			layoutparams.topMargin = v.getTop()+DensityUtil.dip2px(MainActivity.this, 7);
			v.setLayoutParams(layoutparams);
			v.layout(layoutparams.leftMargin, layoutparams.topMargin, 
					layoutparams.leftMargin + v.getWidth()-DensityUtil.dip2px(MainActivity.this, 14), 
					layoutparams.topMargin + v.getHeight()-DensityUtil.dip2px(MainActivity.this, 14));
		}else{
			v.startAnimation(localAnimation);
		}
	}
	
	/**
	 * 白色焦点框飞动、移动、变大
	 * 
	 * */
	private void flyWhiteBorder(int width, int height, float paramFloat1, float paramFloat2) {
		if ((this.whiteBorder != null)) {
//			this.whiteBorder.setVisibility(View.VISIBLE);
			whiteBorder.layout((int)paramFloat1, (int)paramFloat2, (int)paramFloat1+width, (int)paramFloat2+height);
			FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(width, height);
			layoutparams.leftMargin = (int)paramFloat1;
			layoutparams.topMargin = (int)paramFloat2;
			whiteBorder.setLayoutParams(layoutparams);
			whiteBorder.bringToFront();
			ViewPropertyAnimator localViewPropertyAnimator = this.whiteBorder.animate();
			localViewPropertyAnimator.setDuration(100L);
			localViewPropertyAnimator.scaleX((float) (width + DensityUtil.dip2px(this, 14)) / (float) width);
			localViewPropertyAnimator.scaleY((float) (height + DensityUtil.dip2px(this, 14)) / (float) height);
			localViewPropertyAnimator.x(paramFloat1);
			localViewPropertyAnimator.y(paramFloat2);
			localViewPropertyAnimator.start();
		}
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if(myScrollLayout.getSelected()== 1 && skyFrament.dispatchKeyEvent(event)){
			return true;
		}
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_ESCAPE) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void showPage(int page, boolean isLeftSideFocus){
		// TODO Auto-generated method stub
		switch (page) {
		case PageController.PAGE_KONKA:
			showKonkaPage(isLeftSideFocus);
			break;
		case PageController.PAGE_SKYWORTH:
			showSkyworthPage(isLeftSideFocus);
			break;
		case PageController.PAGE_HAIER:
			showHaierPage(isLeftSideFocus);
			break;
		case PageController.PAGE_TCL:
			showTCL(isLeftSideFocus);
			break;
		}
	}
	
	private void showSkyworthPage(boolean isLeftSideFocus) {
		// TODO Auto-generated method stub
		myScrollLayout.snapToScreen(1);
		skyFrament.requsetFouces(isLeftSideFocus);
	}

	private void showKonkaPage(boolean isLeftSideFocus) {
		// TODO Auto-generated method stub
		myScrollLayout.snapToScreen(0);
		findViewById(R.id.item_more_app).requestFocus();
	}
	private void showHaierPage(boolean isLeftSideFocus) {
		// TODO Auto-generated method stub
		myScrollLayout.snapToScreen(2);
		haierFrament.requsetFouces(isLeftSideFocus);
	}
	
	private void showTCL(boolean isLeftSideFocus) {
		// TODO Auto-generated method stub
		myScrollLayout.snapToScreen(3);
		tclFrament.requsetFouces(isLeftSideFocus);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.item_setting:
		case R.id.item_manager:
		case R.id.item_more_app:
			if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT&&event.getAction() == KeyEvent.ACTION_DOWN){
				showSkyworthPage(true);
				return true;
			}else{
				return false;
			}
		default:
			return false;
		}
	}

}
