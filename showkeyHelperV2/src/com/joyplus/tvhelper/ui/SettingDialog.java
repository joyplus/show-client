package com.joyplus.tvhelper.ui;



import com.joyplus.mediaplayer.JoyplusMediaPlayerDataManager;
import com.joyplus.mediaplayer.VideoViewInterface.DecodeType;
import com.joyplus.tvhelper.utils.Log;

import android.R.drawable;
import android.app.Activity;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



import org.cocos2dx.lib.Cocos2dxHelper;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.baidu.oauth.BaiduOAuth;
import com.baidu.oauth.BaiduOAuth.BaiduOAuthResponse;
import com.joyplus.tvhelper.MyApp;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.SettingActivity;
import com.joyplus.tvhelper.helper.HttpClientHelper;
import com.joyplus.tvhelper.https.HttpUtils;
import com.joyplus.tvhelper.utils.HttpTools;
import com.joyplus.tvhelper.utils.PreferencesUtils;


public class SettingDialog extends Dialog implements OnClickListener{

		private static final String TAG = "SettingActivity";
		private static final int STATUE_IMMEDIATELY_SHOW=0;
		private static final int STATUE_DECODE_MODE=STATUE_IMMEDIATELY_SHOW+1;
		private static final int STATUE_DEFAULT_DECREASE=STATUE_DECODE_MODE+1;
		private static final int STATUE_DEFAULT_RESOLUTION=STATUE_DEFAULT_DECREASE+1;
		private static final int STATUE_SIZE_DECREASE=STATUE_DEFAULT_RESOLUTION+1;
		private static final int STATUE_CANCEL_QQ=STATUE_SIZE_DECREASE+1;
		private static final int STATUE_SETTING_BACK=STATUE_CANCEL_QQ+1;
		
		
		
		private LinearLayout mstatue_immediately_show,mstatue_decode_mode,mstatue_default_decrease,mstatue_default_resolution,mstatue_size_decrease,mstatue_setting_back,mstatue_cancel_qq;
		private int mstatue = 0;
		private int detail_default_resolution=1;
		private int detail_size_decrease=1;
		private int detail_immediately_show=0;
		private int detail_decode_mode=0;
		private int detail_default_decrease=1;
		private boolean detail_setting_back=false;
		private boolean detail_cancel_qq=false;
		
		
		private JoyplusMediaPlayerDataManager mJoyplusMediaPlayerDataManager;
		private TextView mstatue_textview,tip_textview,bing_qq_code;
		private String tip_immediately_show,tip_decode_mode,tip_default_decrease,tip_default_resolution,tip_size_decrease,tip_setting_back,tip_cancel_qq;
		private Drawable bg_setting_choose;
		public SettingDialog(final Context pContext) {
			super(pContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		}
		
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_setting);
			

			mstatue_immediately_show=(LinearLayout)findViewById(R.id.immediately_show);
			mstatue_decode_mode=(LinearLayout)findViewById(R.id.decode_mode);
			mstatue_default_decrease=(LinearLayout)findViewById(R.id.default_decrease);
			mstatue_default_resolution=(LinearLayout)findViewById(R.id.default_resolution);
			mstatue_size_decrease=(LinearLayout)findViewById(R.id.size_decrease);
			mstatue_setting_back=(LinearLayout)findViewById(R.id.setting_back);
			mstatue_cancel_qq=(LinearLayout)findViewById(R.id.cancel_qq);
			
			mstatue_immediately_show.setOnFocusChangeListener(itemFocusListener);
			mstatue_decode_mode.setOnFocusChangeListener(itemFocusListener);
			mstatue_default_decrease.setOnFocusChangeListener(itemFocusListener);
			mstatue_default_resolution.setOnFocusChangeListener(itemFocusListener);
			mstatue_size_decrease.setOnFocusChangeListener(itemFocusListener);
			mstatue_setting_back.setOnFocusChangeListener(itemFocusListener);
			mstatue_cancel_qq.setOnFocusChangeListener(itemFocusListener);
			
			tip_textview=(TextView)findViewById(R.id.all_center_tip_content_text);
			tip_immediately_show=this.getContext().getResources().getString(R.string.setting_immediately_show_tip);
	        tip_decode_mode=this.getContext().getResources().getString(R.string.setting_decode_mode_tip);
	        tip_default_decrease=this.getContext().getResources().getString(R.string.setting_default_decrease_tip);
	        tip_default_resolution=this.getContext().getResources().getString(R.string.setting_default_resolution_tip);
	        tip_size_decrease=this.getContext().getResources().getString(R.string.setting_size_decrease_tip);
	        tip_setting_back=this.getContext().getResources().getString(R.string.setting_back_tip);
	        tip_cancel_qq=this.getContext().getResources().getString(R.string.setting_cancel_qq_tip);
	        
	        bg_setting_choose=this.getContext().getResources().getDrawable(R.drawable.setting_choose);
			bing_qq_code=(TextView)findViewById(R.id.cancel_qq_text);
	        
	        
	        
	        bingQqCode();
		}
		
		
		
		
		private void bingQqCode() {
			// TODO Auto-generated method stub
			if(detail_cancel_qq){
            	bing_qq_code.setTextColor(Color.parseColor("#FFFFFF"));			
			}else{
				bing_qq_code.setTextColor(Color.parseColor("#000000"));
			}
		}




		private View.OnFocusChangeListener itemFocusListener = new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					setStatue(v);
					//Log.d(TAG, "hasonFocusChange--->mstatue:"+mstatue+" id="+v.getId());
					ViewGroup viewGroup = (ViewGroup) v; 	
					switchTip(v);
					//Log.d(TAG, "hasonFocusChange--->TextView:"+viewGroup);
					if(viewGroup != null && viewGroup.getChildCount() > 0){
						for(int i=0;i<viewGroup.getChildCount();i++){
							
							View view = viewGroup.getChildAt(i);
							if(view instanceof TextView){
								
								mstatue_textview = (TextView) view;
								Log.d(TAG, "hasonFocusChange--->TextView:"+view);

								if(mstatue_textview.getId()>0){
									mstatue_textview = (TextView) view;
									Log.d(TAG, "id"+mstatue_textview);
									
									mstatue_textview.setBackgroundDrawable(bg_setting_choose);
								    
								};
							}
						}
					}
				}else {
					
					ViewGroup viewGroup = (ViewGroup) v;
					if(viewGroup != null && viewGroup.getChildCount() > 0){
						for(int i=0;i<viewGroup.getChildCount();i++){
							
							View view = viewGroup.getChildAt(i);
							if(view instanceof TextView){
								mstatue_textview = (TextView) view;
								Log.d(TAG, "outonFocusChange--->TextView:"+view);
								if(mstatue_textview.getId()>0){
									mstatue_textview = (TextView) view;
									Log.d(TAG, "id"+mstatue_textview);
									mstatue_textview.setBackgroundDrawable(null);
								   
								};
							}
						}
					}
				}

			}
		};
		
		
		
		private void switchTip(View v){
			Log.d(TAG,"switchTip="+v);
			if(v.equals(mstatue_decode_mode)){
				tip_textview.setText(tip_decode_mode);			
			}else if(v.equals(mstatue_default_decrease)){
	        	tip_textview.setText(tip_default_decrease);
	        }else if(v.equals(mstatue_immediately_show)){
				tip_textview.setText(tip_immediately_show);
			}else if(v.equals(mstatue_default_resolution)){
				tip_textview.setText(tip_default_resolution);
			}else if(v.equals(mstatue_size_decrease)){
				tip_textview.setText(tip_size_decrease);
			}else if (v.equals(mstatue_setting_back)){
				tip_textview.setText(tip_setting_back);
			}else if (v.equals(mstatue_cancel_qq)){
				tip_textview.setText(tip_cancel_qq);
			}
		}
		
		private void setStatue(View v){
			    if(v.equals(mstatue_decode_mode)){
		           mstatue=STATUE_DECODE_MODE;			
				}else  if(v.equals(mstatue_default_decrease)){
		        	mstatue=STATUE_DEFAULT_DECREASE;
		        }else if(v.equals(mstatue_immediately_show)){
					mstatue = STATUE_IMMEDIATELY_SHOW;
				}else if(v.equals(mstatue_default_resolution)){
					mstatue=STATUE_DEFAULT_RESOLUTION;
				}else if(v.equals(mstatue_size_decrease)){
					mstatue=STATUE_SIZE_DECREASE;
				}else if(v.equals(mstatue_setting_back)){
					mstatue=STATUE_SETTING_BACK;
				}else if(v.equals(mstatue_cancel_qq)){
					mstatue=STATUE_CANCEL_QQ;
				}
		}
		
		private void switchDetailDefaultResolution(){
			Log.d(TAG,"inswitchDetailDefaultResolution--->"+detail_default_resolution);
			switch(detail_default_resolution){
			case 0:
				mstatue_textview.setText("流畅");
			    break;
			case 1:
				mstatue_textview.setText("标清");
				break;
			case 2:
				mstatue_textview.setText("高清");
				break;
			case 3:
				mstatue_textview.setText("超清");
				break;
			
			}
		}
		
		private void switchDetailImmediatelyShow(){
			Log.d(TAG,"inswitchImmediatelyShow--->"+detail_immediately_show);
			switch(detail_immediately_show){
			case 0:
				mstatue_textview.setText("关");
			    break;
			case 1:
				mstatue_textview.setText("开");
				break;
			}
		}
		
		
		private void switchDetailDecodemode(){
			Log.d(TAG,"inswitchDetailDecodemode--->"+detail_decode_mode);
			switch(detail_decode_mode){
			case 0:
				mstatue_textview.setText("硬解");
			    break;
			case 1:
				mstatue_textview.setText("软解");
				break;
				
			}
			switchMode();
		}
		private void switchMode(){
//			mJoyplusMediaPlayerDataManager = new JoyplusMediaPlayerDataManager(SettingActivity.this);
//	                    Log.d(TAG, "mJoyplusMediaPlayerDataManager--->"+mJoyplusMediaPlayerDataManager);
//						DecodeType type = mJoyplusMediaPlayerDataManager.getDecodeType();
//				if(type == DecodeType.Decode_HW)type= DecodeType.Decode_SW;
//				else type= DecodeType.Decode_HW;
//				mJoyplusMediaPlayerDataManager.setDecodeType(type);
		}
			

		
		
		private void switchDetailDefaultDecrease(){
			Log.d(TAG,"inswitchDetailDefaultDecrease--->"+detail_default_decrease);
			switch(detail_default_decrease){
			case 0:
				mstatue_textview.setText("关");
			    break;
			case 1:
				mstatue_textview.setText("开");
				break;
			
			}
		}
		
		
		private void switchDetailSizeDecrease(){
			Log.d(TAG,"inswitchDetailSizeDecrease--->"+detail_size_decrease);
			switch(detail_size_decrease){
			case 0:
				mstatue_textview.setText("小");
			    break;
			case 1:
				mstatue_textview.setText("中");
				break;
			case 2:
				mstatue_textview.setText("大");
				break;
			}
		}
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				switch(mstatue){
				case STATUE_DEFAULT_DECREASE:
					Log.d(TAG, "字幕默认选择左");
					if(detail_default_decrease<1){
						detail_default_decrease=detail_default_decrease+1;
					}
					switchDetailDefaultDecrease();
					break;
				case STATUE_DECODE_MODE:
					Log.d(TAG, "播放解码选择左");
					if(detail_decode_mode<1){
						detail_decode_mode=detail_decode_mode+1;
					}
					switchDetailDecodemode();
					break;
				case STATUE_IMMEDIATELY_SHOW:
					Log.d(TAG,"推送立即显示选择左");
					if(detail_immediately_show<1){
						detail_immediately_show=detail_immediately_show+1;
					}
					switchDetailImmediatelyShow();
					break;
				case STATUE_DEFAULT_RESOLUTION:
					if(detail_default_resolution<3 ){
					detail_default_resolution=detail_default_resolution+1;
					}
					Log.d(TAG, "默认清晰度选择左"+detail_default_resolution);
					switchDetailDefaultResolution();
					break;
				case STATUE_SIZE_DECREASE:
					if(detail_size_decrease<2){
					detail_size_decrease=detail_size_decrease+1;
					}
					Log.d(TAG, "字幕大小选择左");
					switchDetailSizeDecrease();
					break;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				switch (mstatue){
				case STATUE_DEFAULT_DECREASE:
					Log.d(TAG, "字幕默认选择右");
					if(detail_default_decrease>0){
						detail_default_decrease=detail_default_decrease-1;
					}
					switchDetailDefaultDecrease();
					break;
				case STATUE_DECODE_MODE:
					Log.d(TAG, "播放解码选择右");
					if(detail_decode_mode>0){
						detail_decode_mode=detail_decode_mode-1;
					}
					switchDetailDecodemode();
					break;
				case STATUE_IMMEDIATELY_SHOW:
					Log.d(TAG,"推送立即显示选择右");
					if(detail_immediately_show>0){
						detail_immediately_show=detail_immediately_show-1;
					}
					switchDetailImmediatelyShow();
					break;
				case STATUE_DEFAULT_RESOLUTION:
					if(detail_default_resolution>0 ){
					detail_default_resolution=detail_default_resolution-1;
					}
					Log.d(TAG, "默认清晰度选择右"+detail_default_resolution);
					switchDetailDefaultResolution();
					break;
				case STATUE_SIZE_DECREASE:
					if(detail_size_decrease>0){
					detail_size_decrease=detail_size_decrease-1;
					}
					Log.d(TAG, "字幕大小选择右");
					switchDetailSizeDecrease();
					break;
				
				}
				break;
			case KeyEvent.KEYCODE_ENTER:
				switch (mstatue){
				case STATUE_SETTING_BACK:
					Log.d(TAG,"setting_back");
					detail_setting_back=true;
					break;
				case STATUE_CANCEL_QQ:
					Log.d(TAG,"cancel_qq");
					detail_cancel_qq=true;
					break;
				}
				break;
			case KeyEvent.KEYCODE_BACK:
				dismiss();
			case KeyEvent.KEYCODE_ESCAPE:
				dismiss();
				break;
			default:
				break;
			}
			return super.onKeyUp(keyCode, event);
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
		

	}

