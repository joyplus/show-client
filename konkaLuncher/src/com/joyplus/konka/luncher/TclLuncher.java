package com.joyplus.konka.luncher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher.ViewFactory;

import com.joyplus.Config.ADConfig;
import com.joyplus.adkey.widget.SerializeManager;
import com.joyplus.konka.utils.DensityUtil;
import com.joyplus.konka.utils.Log;
import com.joyplus.konka_jas.joyplus.konka.ADRequest;
import com.joyplus.konka_jas.joyplus.konka.KonkaConfig;
import com.joyplus.request.AdInfo;

public class TclLuncher extends Fragment implements ViewFactory, OnClickListener, OnFocusChangeListener, OnKeyListener {

	private static final String TAG =  TclLuncher.class.getSimpleName();
	private ImageSwitcher mSwitcher;
//	private int[] arrayPictures = {R.drawable.p1,R.drawable.p2,R.drawable.p3,R.drawable.p4};
	private List<Drawable> pictures = new ArrayList<Drawable>();
	private int pictureIndex = -1;
	private Handler mHandler;
	private static final int MESSAGE_NEXT = 100;
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
	private AnimationSet animationSet;
	private PageController mPageController;
	
	public View  onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		return inflater.inflate(R.layout.layout_tcl, null);
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
//		setContentView(R.layout.layout_tcl);
		mSwitcher = (ImageSwitcher) getView().findViewById(R.id.switcher_tcl);
		layout = (FrameLayout) getView().findViewById(R.id.fram_items_tcl);
		whiteBorder = (ImageView) getView().findViewById(R.id.white_borad_tcl);
		bangdan = (ImageView) getView().findViewById(R.id.image_bangdan_tcl);
//		bangdan_layout = (RelativeLayout) findViewById(R.id.layout_bangdan);
		mSwitcher.setFactory(this);
		mSwitcher.setOnClickListener(this);
		animation_in = AnimationUtils.loadAnimation(this.getActivity(), R.anim.slide_in_right);
		animation_out = AnimationUtils.loadAnimation(this.getActivity(), R.anim.slide_out_left);
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
			view.setOnFocusChangeListener(TclLuncher.this);
			view.setOnClickListener(this);
			view.setOnKeyListener(this);
		}
		whiteBorder.setFocusable(false);
		whiteBorder.setFocusableInTouchMode(false);
//		FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(DensityUtil.dip2px(this, 391), DensityUtil.dip2px(this, 258));
//		layoutparams.leftMargin = DensityUtil.dip2px(this, 13);
//		layoutparams.topMargin = DensityUtil.dip2px(this, 6);
//		whiteBorder.setLayoutParams(layoutparams);
		File f = new File(ADConfig.BD_PATH + "/ADFILE");
		if(f.exists()){
			Drawable d = Drawable.createFromPath(f.getAbsolutePath());
			if(d != null){
				bangdan.setImageDrawable(d);
			}
		}
//		UmengUpdate.update(this);
		
		mHandler.sendEmptyMessageDelayed(MESSAGE_NEXT, DELAY_TIME);
		mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_BANGDAN, DELAY_TIME_BANGDAN+DELAY_TIME/2);
		mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PICTURE, INITPICTURE_TIME/3);
	}
	
	public void requsetFouces(boolean isLeftSideFocus){
		if(isLeftSideFocus){
			getView().findViewById(R.id.layout_switcher_tcl).requestFocus();
		}else{
//			getView().findViewById(R.id.layout_switcher).requestFocus();
		}
		
	}
	
	public void setPageController(PageController controller){
		this.mPageController = controller;
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
		
		Animation animation = AnimationUtils.loadAnimation(this.getActivity(), android.R.anim.fade_out);
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
				Animation animation_show = AnimationUtils.loadAnimation(TclLuncher.this.getActivity(), android.R.anim.fade_in);
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
		ImageView imageView = new ImageView(this.getActivity());  
        imageView.setImageResource(R.drawable.p1);  
        imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,FrameLayout.LayoutParams.FILL_PARENT));
        imageView.setScaleType(ScaleType.FIT_XY);
        return imageView;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mHandler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		try{
			Intent intent = null;
			Log.d(TAG, "click ------------- ");
			switch (view.getId()) {
			case R.id.layout_bangdan_tcl:// bangdan
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
			case  R.id.item_spa_tcl: //tv but now use as car special area
				intent = new Intent(this.getActivity(), SpecialAreaActivity.class);
				break;
			case R.id.item_live_tcl: //1080p but now use us live tv
				intent = new Intent();
				intent.setClassName("tv.wan8.weisp", "tv.huan.epg.live.ui.HuanPlayerActivity");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
			showOnFocusAnimation(view);
		}else{
//			if(view.getId() != R.id.switcher){
				showLooseFocusAnimation(view);
//			}
		}
	}

	private void showOnFocusAnimation(final View v){
		v.bringToFront();
		float sdx = ((float)v.getWidth()+DensityUtil.dip2px(this.getActivity(), 10))/v.getWidth();
		float sdy = ((float)v.getHeight()+DensityUtil.dip2px(this.getActivity(), 10))/v.getHeight();
		this.animEffect.setAttributs(1.0F, sdx, 1.0F, sdy, 100L);
		Animation localAnimation = this.animEffect.createAnimation();
		int left = v.getLeft();
		int top = v.getTop();
		int width = v.getWidth();
		int height = v.getHeight();
//		if(v.getId() == R.id.layout_bangdan){
//			FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(width+DensityUtil.dip2px(MainActivity.this, 14),
//					height+DensityUtil.dip2px(MainActivity.this, 14));
//			layoutparams.leftMargin = left-DensityUtil.dip2px(MainActivity.this, 7);
//			layoutparams.topMargin = top-DensityUtil.dip2px(MainActivity.this, 7);
//			v.setLayoutParams(layoutparams);
//			v.layout(layoutparams.leftMargin, layoutparams.topMargin, 
//					layoutparams.leftMargin + v.getWidth()+DensityUtil.dip2px(MainActivity.this, 14), 
//					layoutparams.topMargin + v.getHeight()+DensityUtil.dip2px(MainActivity.this, 14));
//			
//		}else{
////			v.startAnimation(localAnimation);
//		}
		flyWhiteBorder(v.getMeasuredWidth()+DensityUtil.dip2px(this.getActivity(), 100), 
				v.getMeasuredHeight()+DensityUtil.dip2px(this.getActivity(), 100), 
				left-DensityUtil.dip2px(this.getActivity(), 50),
				top-DensityUtil.dip2px(this.getActivity(), 50));
	}
	
	private void showLooseFocusAnimation(final View v){
		this.whiteBorder.setVisibility(View.INVISIBLE);
//		float sdx = (v.getMeasuredWidth()+DensityUtil.dip2px(this, 10))/v.getMeasuredWidth();
//		float sdy = (v.getMeasuredHeight()+DensityUtil.dip2px(this, 10))/v.getMeasuredHeight();
//		this.animEffect.setAttributs(sdx, 1.0F, sdy, 1.0F, 100L);
//		Animation localAnimation = this.animEffect.createAnimation();
//		if(v.getId() == R.id.layout_bangdan){
//			FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(v.getWidth()-DensityUtil.dip2px(MainActivity.this, 14),
//					v.getHeight()-DensityUtil.dip2px(MainActivity.this, 14));
//			layoutparams.leftMargin = v.getLeft()+DensityUtil.dip2px(MainActivity.this, 7);
//			layoutparams.topMargin = v.getTop()+DensityUtil.dip2px(MainActivity.this, 7);
//			v.setLayoutParams(layoutparams);
//			v.layout(layoutparams.leftMargin, layoutparams.topMargin, 
//					layoutparams.leftMargin + v.getWidth()-DensityUtil.dip2px(MainActivity.this, 14), 
//					layoutparams.topMargin + v.getHeight()-DensityUtil.dip2px(MainActivity.this, 14));
//		}else{
//			v.startAnimation(localAnimation);
//		}
	}
	
	/**
	 * 白色焦点框飞动、移动、变大
	 * 
	 * */
	private void flyWhiteBorder(final int width,final int height,final float paramFloat1,final float paramFloat2) {
		if ((this.whiteBorder != null)) {
			this.whiteBorder.setVisibility(View.VISIBLE);
			int mWidth = this.whiteBorder.getWidth();
			int mHeight = this.whiteBorder.getHeight();
			int mTop = this.whiteBorder.getTop();
			int mLeft = this.whiteBorder.getLeft();
			if (mWidth == 0 || mHeight == 0) {
				mWidth = 100;
				mHeight = 100;
			}
			Log.d(TAG, "mWidth = " + mWidth + ", mHeight = " + mHeight + ", mTop = " + mTop + ", mLeft = " +mLeft);
			Log.d(TAG, "width = " + width + ", height = " + height + ", paramFloat1 = " + paramFloat1 + ", paramFloat2 = " +paramFloat2);
			
			float sx = (width == mWidth)?1:((float)width-DensityUtil.dip2px(this.getActivity(), 100))/(mWidth-DensityUtil.dip2px(this.getActivity(), 100));
			float sy = (height == mHeight)?1:((float)height-DensityUtil.dip2px(this.getActivity(), 100))/(mHeight-DensityUtil.dip2px(this.getActivity(), 100));
			animationSet = new AnimationSet(true);
			ScaleAnimation scaleAnimation = new ScaleAnimation(1,  sx, 1 , sy, 1, 0.5F, 1, 0.5F);
//			scaleAnimation.setDuration(1500L);
			
			TranslateAnimation translateAnimation = new TranslateAnimation(0, (paramFloat1+width/2-mLeft-mWidth/2), 0, (paramFloat2 + height/2-mTop - mHeight/2));
//			translateAnimation.setDuration(1500L);
			animationSet.addAnimation(scaleAnimation);
			animationSet.addAnimation(translateAnimation);
			animationSet.setDuration(150L);
			whiteBorder.startAnimation(animationSet);
			animationSet.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
					
//					FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(width, height);
//					layoutparams.leftMargin = (int)paramFloat1;
//					layoutparams.topMargin = (int)paramFloat2;
//					whiteBorder.setLayoutParams(layoutparams);
//					whiteBorder.bringToFront();
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					Log.d(TAG, "----------onAnimationEnd------------");
					whiteBorder.clearAnimation();
					FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(width, height);
					layoutparams.leftMargin = (int)paramFloat1;
					layoutparams.topMargin = (int)paramFloat2;
					whiteBorder.setLayoutParams(layoutparams);
					whiteBorder.bringToFront();
				}
			});
//			ViewPropertyAnimator localViewPropertyAnimator = this.whiteBorder.animate();
//			localViewPropertyAnimator.setDuration(100L);
//			localViewPropertyAnimator.scaleX((float) (width + DensityUtil.dip2px(this, 14)) / (float) width);
//			localViewPropertyAnimator.scaleY((float) (height + DensityUtil.dip2px(this, 14)) / (float) height);
//			localViewPropertyAnimator.x(paramFloat1);
//			localViewPropertyAnimator.y(paramFloat2);
//			localViewPropertyAnimator.start();
		}
	}
	
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if(animationSet!=null && !animationSet.hasEnded()){
			return true;
		}
		return false;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout_switcher_tcl:
			if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT&&event.getAction() == KeyEvent.ACTION_DOWN){
				if(mPageController!=null){
					mPageController.showPage(PageController.PAGE_HAIER, false);
					return true;
				}
				return false;
			}else{
				return false;
			}
		default:
			return false;
		}
	}
}
