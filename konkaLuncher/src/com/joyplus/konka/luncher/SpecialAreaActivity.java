package com.joyplus.konka.luncher;

import com.joyplus.konka.utils.DensityUtil;
import com.joyplus.konka.utils.Log;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SpecialAreaActivity extends Activity implements OnFocusChangeListener {

	private ScaleAnimEffect animEffect;
	private ImageView whiteBorder;
	RelativeLayout layout;
	private Handler mHandler = new Handler();
	private AnimationSet animationSet;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_special_area);
		layout = (RelativeLayout) findViewById(R.id.layout_all);
		whiteBorder = (ImageView) findViewById(R.id.white_borad_spa);
		this.animEffect = new ScaleAnimEffect();
		for(int i=0; i<layout.getChildCount(); i++){
			View view = layout.getChildAt(i);
			view.setFocusable(true);
			view.setFocusableInTouchMode(true);
			view.setOnFocusChangeListener(this);
		}
		whiteBorder.setFocusable(false);
		whiteBorder.setFocusableInTouchMode(false);
		animationSet = new AnimationSet(true);
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		// TODO Auto-generated method stub
		if(hasFocus){
			showOnFocusAnimation(view);
		}else{
			showLooseFocusAnimation(view);
		}
	}

	private void showLooseFocusAnimation(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.spa_item_1_1){
			((ImageView)v).setImageResource(R.drawable.s_1_1);
		}
		float sdx = ((float)v.getMeasuredWidth()+DensityUtil.dip2px(this, 100))/v.getMeasuredWidth();
		float sdy = ((float)v.getMeasuredHeight()+DensityUtil.dip2px(this, 60))/v.getMeasuredHeight();
		this.animEffect.setAttributs(sdx, 1.0F, sdy, 1.0F, 100L);
		Animation localAnimation = this.animEffect.createAnimation();
		v.startAnimation(localAnimation);
	}

	private void showOnFocusAnimation(View v) {
		// TODO Auto-generated method stub
		v.bringToFront();
		if(v.getId() == R.id.spa_item_1_1){
			((ImageView)v).setImageResource(R.drawable.s_1_1_big);
		}
		float sdx = ((float)v.getMeasuredWidth()+DensityUtil.dip2px(this, 100))/v.getMeasuredWidth();
		float sdy = ((float)v.getMeasuredHeight()+DensityUtil.dip2px(this, 60))/v.getMeasuredHeight();
		this.animEffect.setAttributs(1.0F, sdx, 1.0F, sdy, 100L);
		Animation localAnimation = this.animEffect.createAnimation();
		v.startAnimation(localAnimation);
		int left = v.getLeft();
		int top = v.getTop();
		flyWhiteBorder(v.getMeasuredWidth()+DensityUtil.dip2px(this, 290), 
				v.getMeasuredHeight()+DensityUtil.dip2px(this, 264), 
				left-DensityUtil.dip2px(this, 145),
				top-DensityUtil.dip2px(this, 132));
	}
	
	private void flyWhiteBorder(final int width,final int height,final float paramFloat1,final float paramFloat2) {
		if ((this.whiteBorder != null)) {
			this.whiteBorder.setVisibility(View.VISIBLE);
			int mWidth = this.whiteBorder.getWidth();
			int mHeight = this.whiteBorder.getHeight();
			int mTop = this.whiteBorder.getTop();
			int mLeft = this.whiteBorder.getLeft();
			if (mWidth == 0 || mHeight == 0) {
				mWidth = 1;
				mHeight = 1;
			}
//			float sx = ((float)width)/mWidth;
//			float sy = ((float)height)/mHeight;
			float sx = (width == mWidth)?1:((float)width-DensityUtil.dip2px(this, 290))/(mWidth-DensityUtil.dip2px(this, 290));
			float sy = (height == mHeight)?1:((float)height-DensityUtil.dip2px(this, 264))/(mHeight-DensityUtil.dip2px(this, 264));
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
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					whiteBorder.clearAnimation();
					FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(width, height);
					layoutparams.leftMargin = (int)paramFloat1;
					layoutparams.topMargin = (int)paramFloat2;
					whiteBorder.setLayoutParams(layoutparams);
					whiteBorder.bringToFront();
				}
			});
		}
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if(animationSet!=null && !animationSet.hasEnded()){
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
}
