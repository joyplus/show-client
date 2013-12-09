package com.joyplus.tvhelper.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joyplus.tvhelper.R;

public class LevelMore extends RelativeLayout{

	private static final String TAG = "test";
	private Context mContext;
	private TextView oneTextView,twoTextView,homeTextView,fristPoint,secondPoint;
	private String get_level_string,set_level_string;
	
	
	
	public LevelMore(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mContext  = context;
		InitResource();
	}

	public LevelMore(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext  = context;
		InitResource(); 
	}

	public LevelMore(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext  = context;
		
		InitResource();
		
	}

	private void InitResource() {
		// TODO Auto-generated method stub
		//LayoutInflater inflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		//LayoutInflater.from(c).inflate(R.layout.item_layout_gallery, null);
        View v = inflater.inflate(R.layout.levelmore_layout, null);
        this.addView(v,new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        oneTextView = (TextView) v.findViewById(R.id.level_one);
        twoTextView = (TextView) v.findViewById(R.id.level_two);
        homeTextView = (TextView) v.findViewById(R.id.level_home);
        fristPoint = (TextView) v.findViewById(R.id.frist_level_point);
        secondPoint = (TextView) v.findViewById(R.id.second_level_point);
        Log.d(TAG,"INIT");
   }

	public void getFristLevel(String getlevel){
		get_level_string=getlevel;
		AnimationSet animationSet = new AnimationSet(true);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
		alphaAnimation.setDuration(250);
		animationSet.addAnimation(alphaAnimation);
		Animation moreAnimationRight = new TranslateAnimation(Animation.RELATIVE_TO_SELF,-2f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f);
		moreAnimationRight.setDuration(300);
		animationSet.addAnimation(moreAnimationRight);
		homeTextView.startAnimation(animationSet);
		homeTextView.setVisibility(View.VISIBLE);
		fristPoint.startAnimation(animationSet);
		fristPoint.setVisibility(View.VISIBLE);
		oneTextView.startAnimation(animationSet);
		oneTextView.setVisibility(View.VISIBLE);
		oneTextView.setText(get_level_string);
		Log.d(TAG,"getFristLevel------->");
		
	}
	

	public void getSecondLevel(String getlevel){
		get_level_string=getlevel;
		AnimationSet animationSet = new AnimationSet(true);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
		alphaAnimation.setDuration(250);
		animationSet.addAnimation(alphaAnimation);
		Animation moreAnimationRight = new TranslateAnimation(Animation.RELATIVE_TO_SELF,-2f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f);
		moreAnimationRight.setDuration(500);
		animationSet.addAnimation(moreAnimationRight);
		twoTextView.startAnimation(animationSet);
		secondPoint.startAnimation(animationSet);
		secondPoint.setVisibility(View.VISIBLE);
		twoTextView.setVisibility(View.VISIBLE);
		twoTextView.setText(get_level_string);
		Log.d(TAG,"getSecondLevel------->");
	}
    public void dismissFristLevel(){
    	AnimationSet lanimationSet = new AnimationSet(true);
		AlphaAnimation lalphaAnimation = new AlphaAnimation(1,0);
		lalphaAnimation.setDuration(500);
		lanimationSet.addAnimation(lalphaAnimation);
		Animation lessAnimationRight = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-10f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f);
		lessAnimationRight.setDuration(1500);
		lanimationSet.addAnimation(lessAnimationRight);
		lanimationSet.setFillAfter(true);
		homeTextView.startAnimation(lanimationSet);
		homeTextView.setVisibility(View.VISIBLE);
		fristPoint.startAnimation(lanimationSet);
		fristPoint.setVisibility(View.VISIBLE);
		oneTextView.startAnimation(lanimationSet);
		oneTextView.setVisibility(View.VISIBLE);
		Log.d(TAG,"dismissFristLevel------->");
	}
	public void dismissSecondLevel(){
		AnimationSet lanimationSet = new AnimationSet(true);
		AlphaAnimation lalphaAnimation = new AlphaAnimation(1,0);
		lalphaAnimation.setDuration(500);
		lanimationSet.addAnimation(lalphaAnimation);
		Animation lessAnimationRight = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-10f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f);
		lessAnimationRight.setDuration(1500);
		lanimationSet.addAnimation(lessAnimationRight);
		lanimationSet.setFillAfter(true);
		secondPoint.startAnimation(lanimationSet);
		secondPoint.setVisibility(View.VISIBLE);
		twoTextView.startAnimation(lanimationSet);
		twoTextView.setVisibility(View.VISIBLE);
		Log.d(TAG,"dismissSecondLevel------->");
	}
}


