package com.joyplus.tvhelper.ui;



import org.cocos2dx.lib.Cocos2dxHelper;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joyplus.mediaplayer.JoyplusMediaPlayerDataManager;
import com.joyplus.mediaplayer.VideoViewInterface.DecodeType;
import com.joyplus.tvhelper.MyApp;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.utils.Constant;
import com.joyplus.tvhelper.utils.Global;
import com.joyplus.tvhelper.utils.HttpTools;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.PreferencesUtils;
import com.joyplus.tvhelper.utils.Utils;


public class SettingDialog extends Dialog implements OnClickListener{

		private static final String TAG = "SettingActivity";
		private static final int STATUE_IMMEDIATELY_SHOW=0;
		private static final int STATUE_DECODE_MODE=STATUE_IMMEDIATELY_SHOW+1;
		private static final int STATUE_DEFAULT_DECREASE=STATUE_DECODE_MODE+1;
		private static final int STATUE_DEFAULT_RESOLUTION=STATUE_DEFAULT_DECREASE+1;
		private static final int STATUE_SIZE_DECREASE=STATUE_DEFAULT_RESOLUTION+1;
		private static final int STATUE_CANCEL_QQ=STATUE_SIZE_DECREASE+1;
		private static final int STATUE_SETTING_BACK=STATUE_CANCEL_QQ+1;
		
		
		public static final int FONT_SIZE_BIG = 42;
		public static final int FONT_SIZE_MIDDLE = 36;
		public static final int FONT_SIZE_SMALL = 30;
		
		private static final int MESSAGE_UNBAND_SUCCESS = 0;
		private static final int MESSAGE_UNBAND_FAILE = MESSAGE_UNBAND_SUCCESS+1;
		
		private LinearLayout mstatue_immediately_show,mstatue_decode_mode,mstatue_default_decrease,mstatue_default_resolution,mstatue_size_decrease,mstatue_setting_back,mstatue_cancel_qq;
		private int mstatue = 0;
		private int detail_decode_mode=0;
		
		private int detail_default_resolution;
		private int detail_size_decrease;
		private boolean detail_immediately_show;
		private boolean detail_default_decrease;
		
		private boolean detail_setting_back=false;
		private boolean detail_cancel_qq;
		
		private String tip_immediately_show,tip_decode_mode,tip_default_decrease,
				tip_default_resolution,tip_size_decrease,tip_setting_back,tip_cancel_qq;
		
		private TextView textview_immediately_show;
		private TextView textview_decode_mode;
		private TextView textview_default_decrease;
		private TextView textview_default_resolution;
		private TextView textview_size_decrease;
		
		private LinearLayout progress;
		
		private JoyplusMediaPlayerDataManager mJoyplusMediaPlayerDataManager;
		private TextView mstatue_textview,tip_textview,bing_qq_code;
		private Drawable bg_setting_choose;
		public SettingDialog(final Context pContext) {
			super(pContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		}
		
		
		private Handler mHandler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case MESSAGE_UNBAND_SUCCESS:
					detail_cancel_qq = true;
					progress.setVisibility(View.GONE);
					PreferencesUtils.setQQAvatare(getContext(), "");
					PreferencesUtils.setQQName(getContext(), "");
					updateUnbandQQdisplay();
					getContext().sendBroadcast(new Intent(Global.ACTION_UN_BAND_SUCCESS));
					break;
				case MESSAGE_UNBAND_FAILE:
					detail_cancel_qq = false;
					progress.setVisibility(View.GONE);
					updateUnbandQQdisplay();
					break;
				}
			};
		};
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_setting);
			
			initDate();
			
			mstatue_immediately_show=(LinearLayout)findViewById(R.id.immediately_show);
			mstatue_decode_mode=(LinearLayout)findViewById(R.id.decode_mode);
			mstatue_default_decrease=(LinearLayout)findViewById(R.id.default_decrease);
			mstatue_default_resolution=(LinearLayout)findViewById(R.id.default_resolution);
			mstatue_size_decrease=(LinearLayout)findViewById(R.id.size_decrease);
			mstatue_setting_back=(LinearLayout)findViewById(R.id.setting_back);
			mstatue_cancel_qq=(LinearLayout)findViewById(R.id.cancel_qq);
			progress = (LinearLayout)findViewById(R.id.layout_progress);
			
			textview_immediately_show = (TextView)findViewById(R.id.immediately_show_text);
			textview_decode_mode = (TextView)findViewById(R.id.decode_mode_text);
			textview_default_decrease = (TextView)findViewById(R.id.default_decrease_text);
			textview_default_resolution = (TextView)findViewById(R.id.default_resolution_text);
			textview_size_decrease = (TextView)findViewById(R.id.size_decrease_text);
			
			mstatue_immediately_show.setOnFocusChangeListener(itemFocusListener);
			mstatue_decode_mode.setOnFocusChangeListener(itemFocusListener);
			mstatue_default_decrease.setOnFocusChangeListener(itemFocusListener);
			mstatue_default_resolution.setOnFocusChangeListener(itemFocusListener);
			mstatue_size_decrease.setOnFocusChangeListener(itemFocusListener);
			mstatue_setting_back.setOnFocusChangeListener(itemFocusListener);
			mstatue_cancel_qq.setOnFocusChangeListener(itemFocusListener);
			
			tip_textview=(TextView)findViewById(R.id.all_center_tip_content_text);
	        
	        bg_setting_choose=this.getContext().getResources().getDrawable(R.drawable.setting_choose);
			bing_qq_code=(TextView)findViewById(R.id.cancel_qq_text);
	        
	        setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					Cocos2dxHelper.setSettingResult(true);
				}
			});
	        initView();
	        if("".equals(PreferencesUtils.getQQName(getContext()))){
				detail_cancel_qq = true;
			}else{
				detail_cancel_qq = false;
			}
	        updateUnbandQQdisplay();
		}
		
		private void initView(){
			switchDetailDecodemode();
			switchDetailDefaultDecrease();
			switchDetailImmediatelyShow();
			switchDetailSizeDecrease();
			switchDetailDefaultResolution();
		}
		
		private void initDate(){
			detail_default_resolution=PreferencesUtils.getDefualteDefination(getContext());
			detail_size_decrease=PreferencesUtils.getSubSize(getContext());
			detail_immediately_show=PreferencesUtils.getDefualtePlayChoice(getContext());
			detail_default_decrease=PreferencesUtils.getSubSwitch(getContext());
			
			tip_immediately_show=this.getContext().getResources().getString(R.string.setting_immediately_show_tip);
	        tip_decode_mode=this.getContext().getResources().getString(R.string.setting_decode_mode_tip);
	        tip_default_decrease=this.getContext().getResources().getString(R.string.setting_default_decrease_tip);
	        tip_default_resolution=this.getContext().getResources().getString(R.string.setting_default_resolution_tip);
	        tip_size_decrease=this.getContext().getResources().getString(R.string.setting_size_decrease_tip);
	        tip_setting_back=this.getContext().getResources().getString(R.string.setting_back_tip);
	        tip_cancel_qq=this.getContext().getResources().getString(R.string.setting_cancel_qq_tip);
			
	        Log.d(TAG,"inswitchDetailDecodemode--->"+detail_decode_mode);
			mJoyplusMediaPlayerDataManager = new JoyplusMediaPlayerDataManager(getContext());	               
			DecodeType type = mJoyplusMediaPlayerDataManager.getDecodeType();
			Log.d(TAG,"type 1 = "+type.name());
			if(type == DecodeType.Decode_HW){
				detail_decode_mode = 0;
			}else{
				detail_decode_mode = 1;
			}
		}
		
		
		
		private void updateUnbandQQdisplay() {
			// TODO Auto-generated method stub
			if(detail_cancel_qq){
				bing_qq_code.setTextColor(Color.parseColor("#505050"));
			}else{
				bing_qq_code.setTextColor(Color.parseColor("#FFFFFF"));			
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
					if(viewGroup != null && viewGroup.getChildCount() > 1){
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
							Log.d(TAG,"i::::"+i);
							View view = viewGroup.getChildAt(i);
							if(view instanceof TextView){
								mstatue_textview = (TextView) view;
							//	Log.d(TAG, "outonFocusChange--->TextView:"+view);
								if(mstatue_textview.getId()>0){
									mstatue_textview = (TextView) view;
							//		Log.d(TAG, "id"+mstatue_textview);
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
			case Constant.DEFINATION_FLV:
				textview_default_resolution.setText("流畅");
			    break;
			case Constant.DEFINATION_MP4:
				textview_default_resolution.setText("标清");
				break;
			case Constant.DEFINATION_HD:
				textview_default_resolution.setText("高清");
				break;
			case Constant.DEFINATION_HD2:
				textview_default_resolution.setText("超清");
				break;
			}
			PreferencesUtils.setDefualteDefination(getContext(), detail_default_resolution);
		}
		
		private void switchDetailImmediatelyShow(){
			Log.d(TAG,"inswitchImmediatelyShow--->"+detail_immediately_show);
			if(detail_immediately_show){
				textview_immediately_show.setText("开");
			}else{
				textview_immediately_show.setText("关");
			}
			PreferencesUtils.setDefualtePlayChoice(getContext(), detail_immediately_show);
//			switch(detail_immediately_show){
//			case 0:
//				mstatue_textview.setText("关");
//			    break;
//			case 1:
//				mstatue_textview.setText("开");
//				break;
//			}
		}
		
		
		private void switchDetailDecodemode(){
			DecodeType type = DecodeType.Decode_HW;
			
			switch(detail_decode_mode){
			case 0:
				textview_decode_mode.setText("硬解");
				type= DecodeType.Decode_HW;
			    break;
			case 1:
				textview_decode_mode.setText("软解");
				type= DecodeType.Decode_SW;
				break;
			}
			mJoyplusMediaPlayerDataManager.setDecodeType(type);
//			DecodeType type2 = mJoyplusMediaPlayerDataManager.getDecodeType();
//			Log.d(TAG,"type 2 = "+type.name());
//			switchMode(); 
		}
//		private void switchMode(){
//			
//			mJoyplusMediaPlayerDataManager = new JoyplusMediaPlayerDataManager(getContext());	               
//			DecodeType type = mJoyplusMediaPlayerDataManager.getDecodeType();
//			Log.d(TAG,"type 1 = "+type.name());
//			
//			if(type == DecodeType.Decode_HW)
//				type= DecodeType.Decode_SW;
//			else 
//				type= DecodeType.Decode_HW;			    
//			mJoyplusMediaPlayerDataManager.setDecodeType(type);
//		   
//			DecodeType type2 = mJoyplusMediaPlayerDataManager.getDecodeType();
//			Log.d(TAG,"type 2 = "+type.name());
//		}
//			

		
		
		private void switchDetailDefaultDecrease(){
			Log.d(TAG,"inswitchDetailDefaultDecrease--->"+detail_default_decrease);
			if(detail_default_decrease){
				textview_default_decrease.setText("开");
			}else{
				textview_default_decrease.setText("关");
			}
			PreferencesUtils.setSubSwitch(getContext(), detail_default_decrease);
//			switch(detail_default_decrease){
//			case 0:
//				mstatue_textview.setText("关");
//			    break;
//			case 1:
//				mstatue_textview.setText("开");
//				break;
//			
//			}
		}
		
		
		private void switchDetailSizeDecrease(){
			Log.d(TAG,"inswitchDetailSizeDecrease--->"+detail_size_decrease);
			switch(detail_size_decrease){
			case FONT_SIZE_SMALL:
				textview_size_decrease.setText("小");
			    break;
			case FONT_SIZE_MIDDLE:
				textview_size_decrease.setText("中");
				break;
			case FONT_SIZE_BIG:
				textview_size_decrease.setText("大");
				break;
			}
			PreferencesUtils.setSubSize(getContext(), detail_size_decrease);
		}
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			Log.d(TAG, "keyCode = " + keyCode + "mstatue = "+mstatue);
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				switch(mstatue){
				case STATUE_DEFAULT_DECREASE:
					Log.d(TAG, "字幕默认选择左");
					if(!detail_default_decrease){
						detail_default_decrease=true;
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
					if(!detail_immediately_show){
						detail_immediately_show=true;
					}
					switchDetailImmediatelyShow();
					break;
				case STATUE_DEFAULT_RESOLUTION:
//					if(detail_default_resolution<3 ){
//					detail_default_resolution=detail_default_resolution+1;
//					}
					switch (detail_default_resolution) {
					case Constant.DEFINATION_HD2:
						break;
					case Constant.DEFINATION_HD:
						detail_default_resolution = Constant.DEFINATION_HD2;
						break;
					case Constant.DEFINATION_MP4:
						detail_default_resolution = Constant.DEFINATION_HD;
						break;
					case Constant.DEFINATION_FLV:
						detail_default_resolution = Constant.DEFINATION_MP4;
						break;
					default:
						break;
					}
					Log.d(TAG, "默认清晰度选择左"+detail_default_resolution);
					switchDetailDefaultResolution();
					break;
				case STATUE_SIZE_DECREASE:
					switch (detail_size_decrease) {
					case FONT_SIZE_BIG:
						break;
					case FONT_SIZE_MIDDLE:
						detail_size_decrease = FONT_SIZE_BIG;
						break;
					case FONT_SIZE_SMALL:
						detail_size_decrease = FONT_SIZE_MIDDLE;
						break;
					default:
						break;
					}
//					if(detail_size_decrease<2){
//					detail_size_decrease=detail_size_decrease+1;
//					}
					Log.d(TAG, "字幕大小选择左");
					switchDetailSizeDecrease();
					break;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				switch (mstatue){
				case STATUE_DEFAULT_DECREASE:
					Log.d(TAG, "字幕默认选择右");
					if(detail_default_decrease){
						detail_default_decrease = false;
					}
//					if(detail_default_decrease>0){
//						detail_default_decrease=detail_default_decrease-1;
//					}
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
					if(detail_immediately_show){
						detail_immediately_show=false;
					}
					switchDetailImmediatelyShow();
					break;
				case STATUE_DEFAULT_RESOLUTION:
//					if(detail_default_resolution>0 ){
//					detail_default_resolution=detail_default_resolution-1;
//					}
					switch (detail_default_resolution) {
					case Constant.DEFINATION_HD2:
						detail_default_resolution = Constant.DEFINATION_HD;
						break;
					case Constant.DEFINATION_HD:
						detail_default_resolution = Constant.DEFINATION_MP4;
						break;
					case Constant.DEFINATION_MP4:
						detail_default_resolution = Constant.DEFINATION_FLV;
						break;
					case Constant.DEFINATION_FLV:
						break;
					default:
						break;
					}
					Log.d(TAG, "默认清晰度选择右"+detail_default_resolution);
					switchDetailDefaultResolution();
					break;
				case STATUE_SIZE_DECREASE:
//					if(detail_size_decrease>0){
//					detail_size_decrease=detail_size_decrease-1;
//					}
					switch (detail_size_decrease) {
					case FONT_SIZE_BIG:
						detail_size_decrease = FONT_SIZE_MIDDLE;
						break;
					case FONT_SIZE_MIDDLE:
						detail_size_decrease = FONT_SIZE_SMALL;
						break;
					case FONT_SIZE_SMALL:
						break;
					default:
						break;
					}
					Log.d(TAG, "字幕大小选择右");
					switchDetailSizeDecrease();
					break;
				
				}
				break;
			case KeyEvent.KEYCODE_ENTER:
			case KeyEvent.KEYCODE_DPAD_CENTER:
				switch (mstatue){
				case STATUE_SETTING_BACK:
					Log.d(TAG,"setting_back");
					detail_setting_back=true;
					setSettingBack();
					break;
				case STATUE_CANCEL_QQ:
					Log.d(TAG,"cancel_qq");
					if(!detail_cancel_qq){
						unBandQQ();
					}
					break;
				}
				break;
			case KeyEvent.KEYCODE_BACK:
			case KeyEvent.KEYCODE_ESCAPE:
				dismiss();
				Cocos2dxHelper.setSettingResult(true);
				break;
			default:
				break;
			}
			return super.onKeyUp(keyCode, event);
		}

		private void unBandQQ() {
			// TODO Auto-generated method stub
			progress.setVisibility(View.VISIBLE);
			MyApp.pool.execute(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String url = Constant.BASE_URL + "/device/unbind?pin="+PreferencesUtils.getPincode(getContext())+
							"&md="+Utils.getMacAdd(getContext());
					String str = HttpTools.get(getContext(), url);
					try{
						JSONObject obj = new JSONObject(str);
						if(obj.getBoolean("status")){
							mHandler.sendEmptyMessage(MESSAGE_UNBAND_SUCCESS);
						}else{
							mHandler.sendEmptyMessage(MESSAGE_UNBAND_FAILE);
						}
						
					}catch (Exception e) {
						// TODO: handle exception
						mHandler.sendEmptyMessage(MESSAGE_UNBAND_FAILE);
					}
				}
			});
		}


		private void setSettingBack() {
			// TODO Auto-generated method stub
			PreferencesUtils.removeDefualteDefination(getContext());
			PreferencesUtils.removeDefualtePlayChoice(getContext());
			PreferencesUtils.removeSubSize(getContext());
			PreferencesUtils.removeSubSwitch(getContext());
			mJoyplusMediaPlayerDataManager.setDecodeType(DecodeType.Decode_HW);
			initDate();
			initView();
		}


		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
		

	}

