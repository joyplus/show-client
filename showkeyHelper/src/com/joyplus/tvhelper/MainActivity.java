package com.joyplus.tvhelper;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnHoverListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joyplus.tvhelper.faye.FayeService;
import com.joyplus.tvhelper.https.HttpUtils;
import com.joyplus.tvhelper.ui.MyScrollLayout;
import com.joyplus.tvhelper.ui.MyScrollLayout.OnViewChangeListener;
import com.joyplus.tvhelper.utils.Constant;
import com.joyplus.tvhelper.utils.Global;
import com.joyplus.tvhelper.utils.HttpTools;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.PreferencesUtils;
import com.joyplus.tvhelper.utils.Utils;
import com.joyplus.tvhelper.utils.XunLeiLiXianUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends Activity implements OnFocusChangeListener, OnHoverListener, OnKeyListener, OnClickListener {

	private static final String TAG = "MainActivity";
	
	private static final int MESSAGE_GETPINCODE_SUCCESS = 0;
	private static final int MESSAGE_GETPINCODE_FAILE = MESSAGE_GETPINCODE_SUCCESS+1;
	
	private ImageView image_showtui, image_xunlei, image_yuntui, image_zhibo, image_tuijian;
	
	private LinearLayout layout_showtui, layout_xunlei, layout_yuntui, layout_zhibo, layout_tuijian;
	
	private RelativeLayout layout_ceshu, layout_upan;
	
	private ImageView image_jiasu, image_upan, image_appguanli, image_ceshu, image_setting;
	
	private LinearLayout layout_jiasu, layout_appguanli, layout_setting;
	
	private TextView title_text_1, title_text_2;
	
	private View selectedLayout;
	
//	private FrameLayout relativeLayout;
	
	private MyScrollLayout layout;
	
	private TextView pincodeText;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_GETPINCODE_SUCCESS:
				displayPincode();
				int width = layout_yuntui.getWidth();
				int height = layout_yuntui.getHeight();
				image_showtui.layout(0, 0, width+ Utils.getStandardValue(MainActivity.this,40), height*2+Utils.getStandardValue(MainActivity.this,53));
				image_yuntui.layout(width+Utils.getStandardValue(MainActivity.this,13), 0, width*2+Utils.getStandardValue(MainActivity.this,53), height+Utils.getStandardValue(MainActivity.this,40));
				image_xunlei.layout(width+Utils.getStandardValue(MainActivity.this,13), height+Utils.getStandardValue(MainActivity.this,13), width*2+Utils.getStandardValue(MainActivity.this,53), height*2+Utils.getStandardValue(MainActivity.this,53));
				image_zhibo.layout(width*2+Utils.getStandardValue(MainActivity.this,26), 0, width*3+Utils.getStandardValue(MainActivity.this,66), height+Utils.getStandardValue(MainActivity.this,40));
				image_tuijian.layout(width*2+Utils.getStandardValue(MainActivity.this,26), height+Utils.getStandardValue(MainActivity.this,13), width*3+Utils.getStandardValue(MainActivity.this,66), height*2+Utils.getStandardValue(MainActivity.this,53));
				image_jiasu.layout(0, 0, width+Utils.getStandardValue(MainActivity.this,40), height*2+Utils.getStandardValue(MainActivity.this,53));
				image_upan.layout(width+Utils.getStandardValue(MainActivity.this,13), 0, width*2+Utils.getStandardValue(MainActivity.this,53), height+Utils.getStandardValue(MainActivity.this,40));
				image_appguanli.layout(width+Utils.getStandardValue(MainActivity.this,13), height+Utils.getStandardValue(MainActivity.this,13), width*2+Utils.getStandardValue(MainActivity.this,53), height*2+Utils.getStandardValue(MainActivity.this,53));
				image_ceshu.layout(width*2+Utils.getStandardValue(MainActivity.this,26), 0, width*3+Utils.getStandardValue(MainActivity.this,66), height+Utils.getStandardValue(MainActivity.this,40));
				image_setting.layout(width*2+Utils.getStandardValue(MainActivity.this,26), height+Utils.getStandardValue(MainActivity.this,13), width*3+Utils.getStandardValue(MainActivity.this,66), height*2+Utils.getStandardValue(MainActivity.this,53));
				image_showtui.setImageBitmap(layout_showtui.getDrawingCache());
				layout_showtui.requestFocus();
				startService(new Intent(MainActivity.this, FayeService.class));
				break;
			case MESSAGE_GETPINCODE_FAILE:
				Toast.makeText(MainActivity.this, "请求pinCode失败", 100).show();
				break;
			default:
				break;
			}
			
		};
	};
	
	private BroadcastReceiver reciver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			image_showtui.setImageBitmap(null);
			layout_showtui.setDrawingCacheEnabled(false);
			displayPincode();
			layout_showtui.setDrawingCacheEnabled(true);
			image_showtui.setImageBitmap(layout_showtui.getDrawingCache());
		}
		
	};
	
	private MyApp app;
	private Map<String, String> headers;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		MobclickAgent.onError(this);
		UmengUpdateAgent.update(this);
		
		app = (MyApp) getApplication();
		
		layout = (MyScrollLayout) findViewById(R.id.layout);
		findViews();
		if(PreferencesUtils.getPincode(this)==null){
			if(HttpUtils.isNetworkAvailable(this)){
				new Thread(new GetPinCodeTask()).start();
			}else {
				
				Utils.showToast(this, "检查网络设置");
			}
			
		}else{
			mHandler.sendEmptyMessageDelayed((MESSAGE_GETPINCODE_SUCCESS),200);
		}
		
		headers = new HashMap<String, String>();
		headers.put("app_key", Constant.APPKEY);
		app.setHeaders(headers);
		IntentFilter filter = new IntentFilter(Global.ACTION_PINCODE_REFRESH);
		registerReceiver(reciver, filter);
		
	}
	
	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
//		case KeyEvent.KEYCODE_DPAD_LEFT:
//			layout.showPre();
//			break;
//		case KeyEvent.KEYCODE_DPAD_RIGHT:
//			layout.showNext();
//			break;
		case KeyEvent.KEYCODE_BACK:
			if ((System.currentTimeMillis() - exitTime) > 2000) {
//				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();

				Toast toast = new Toast(this);
				View v = getLayoutInflater().inflate(R.layout.toast_textview, null);
				toast.setView(v);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void findViews(){
		
		pincodeText = (TextView) findViewById(R.id.pincodeText);
		
		image_showtui = (ImageView) findViewById(R.id.image_showtui);
		image_yuntui = (ImageView) findViewById(R.id.image_yuntui);
		image_xunlei = (ImageView) findViewById(R.id.image_xunlei);
		image_zhibo = (ImageView) findViewById(R.id.image_zhibo);
		image_tuijian = (ImageView) findViewById(R.id.image_tuijian);
		
		image_jiasu= (ImageView) findViewById(R.id.image_jiasu);
		image_upan = (ImageView) findViewById(R.id.image_upan);
		image_appguanli = (ImageView) findViewById(R.id.image_appguanli);
		image_ceshu = (ImageView) findViewById(R.id.image_ceshu);
		image_setting = (ImageView) findViewById(R.id.image_setting);
		
		layout_showtui = (LinearLayout) findViewById(R.id.showtui);
		layout_yuntui = (LinearLayout) findViewById(R.id.layout_yuntui);
		layout_xunlei = (LinearLayout) findViewById(R.id.layout_xunlei);
		layout_zhibo = (LinearLayout) findViewById(R.id.layout_zhibo);
		layout_tuijian = (LinearLayout) findViewById(R.id.layout_tuijian);
		
		layout_jiasu = (LinearLayout) findViewById(R.id.layout_jiashu);
		layout_upan = (RelativeLayout) findViewById(R.id.layout_upan);
		layout_appguanli = (LinearLayout) findViewById(R.id.layout_appguanli);
		layout_ceshu = (RelativeLayout) findViewById(R.id.layout_ceshu);
		layout_setting = (LinearLayout) findViewById(R.id.layout_setting);
		
		title_text_1 = (TextView) findViewById(R.id.title_1);
		title_text_2 = (TextView) findViewById(R.id.title_2);
		
		title_text_1.setOnClickListener(this);
		title_text_2.setOnClickListener(this);
//		relativeLayout = (FrameLayout) findViewById(R.id.relative_layout);
		
		layout_showtui.setOnFocusChangeListener(this);
		layout_yuntui.setOnFocusChangeListener(this);
		layout_xunlei.setOnFocusChangeListener(this);
		layout_zhibo.setOnFocusChangeListener(this);
		layout_tuijian.setOnFocusChangeListener(this);
		
		layout_jiasu.setOnFocusChangeListener(this);
		layout_upan.setOnFocusChangeListener(this);
		layout_appguanli.setOnFocusChangeListener(this);
		layout_ceshu.setOnFocusChangeListener(this);
		layout_setting.setOnFocusChangeListener(this);
		
		layout_showtui.setOnHoverListener(this);
		layout_yuntui.setOnHoverListener(this);
		layout_xunlei.setOnHoverListener(this);
		layout_zhibo.setOnHoverListener(this);
		layout_tuijian.setOnHoverListener(this);
		
		layout_jiasu.setOnHoverListener(this);
		layout_upan.setOnHoverListener(this);
		layout_appguanli.setOnHoverListener(this);
		layout_ceshu.setOnHoverListener(this);
		layout_setting.setOnHoverListener(this);
		
		layout_jiasu.setDrawingCacheEnabled(true);
		layout_upan.setDrawingCacheEnabled(true);
		layout_appguanli.setDrawingCacheEnabled(true);
		layout_ceshu.setDrawingCacheEnabled(true);
		layout_setting.setDrawingCacheEnabled(true);
		
		layout_showtui.setDrawingCacheEnabled(true);
		layout_yuntui.setDrawingCacheEnabled(true);
		layout_xunlei.setDrawingCacheEnabled(true);
		layout_zhibo.setDrawingCacheEnabled(true);
		layout_tuijian.setDrawingCacheEnabled(true);
		
		layout_showtui.setOnClickListener(this);
		layout_yuntui.setOnClickListener(this);
		layout_xunlei.setOnClickListener(this);
		layout_zhibo.setOnClickListener(this);
		layout_tuijian.setOnClickListener(this);
		layout_jiasu.setOnClickListener(this);
		layout_upan.setOnClickListener(this);
		layout_appguanli.setOnClickListener(this);
		layout_ceshu.setOnClickListener(this);
		layout_setting.setOnClickListener(this);
		
		layout_showtui.setTag(image_showtui);
		layout_yuntui.setTag(image_yuntui);
		layout_xunlei.setTag(image_xunlei);
		layout_zhibo.setTag(image_zhibo);
		layout_tuijian.setTag(image_tuijian);
		
		layout_jiasu.setTag(image_jiasu);
		layout_upan.setTag(image_upan);
		layout_appguanli.setTag(image_appguanli);
		layout_ceshu.setTag(image_ceshu);
		layout_setting.setTag(image_setting);
		
//		layout_jiasu.setOnKeyListener(this);
		
//		layout_tuijian.setOnKeyListener(this);
//		layout_zhibo.setOnKeyListener(this);
		
		layout.SetOnViewChangeListener(new OnViewChangeListener() {
			
			@Override
			public void OnViewChange(int index) {
				// TODO Auto-generated method stub
				switch (index) {
				case 0:
					title_text_1.setTextColor(getResources().getColor(R.color.main_title_selected));
					title_text_2.setTextColor(getResources().getColor(R.color.main_title_unselected));
					updateImageView(layout_tuijian);
					break;
				case 1:
					title_text_2.setTextColor(getResources().getColor(R.color.main_title_selected));
					title_text_1.setTextColor(getResources().getColor(R.color.main_title_unselected));
					updateImageView(layout_jiasu);
					break;
				}
			}
		});
	}
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if(hasFocus){
			updateImageView(v);
		}
	}

	@Override
	public boolean onHover(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_HOVER_ENTER){
			switch (v.getId()) {
			case R.id.showtui:
			case R.id.layout_yuntui:
			case R.id.layout_xunlei:
			case R.id.layout_zhibo:
			case R.id.layout_tuijian:
				if(layout.getSelected()==0){
					updateImageView(v);
					v.requestFocus();
				}
				break;
			case R.id.layout_jiashu:
			case R.id.layout_upan:
			case R.id.layout_appguanli:
			case R.id.layout_ceshu:
			case R.id.layout_setting:
				if(layout.getSelected()==1){
					updateImageView(v);
					v.requestFocus();
				}
				break;
			default:
				break;
			}
		}
		return true;
	}
	
	private void updateImageView(View v){
		if(v.equals(selectedLayout)){
			return;
		}
		ImageView imageView1 = (ImageView) v.getTag();
		ScaleAnimation animation_appear = new ScaleAnimation((imageView1.getWidth()-40f)/(imageView1.getWidth()), 
										1.0f, 
										(imageView1.getHeight()-40f)/(imageView1.getHeight()), 
										1.0f, 
										Animation.RELATIVE_TO_SELF, 
										0.5f, 
										Animation.RELATIVE_TO_SELF, 
										0.5f);
		animation_appear.setDuration(250);
		imageView1.setVisibility(View.VISIBLE);
		imageView1.startAnimation(animation_appear);
		imageView1.setImageBitmap(v.getDrawingCache());
		if(selectedLayout!=null){
			ImageView imageView2 = (ImageView) selectedLayout.getTag();
			ScaleAnimation animation_disappear = new ScaleAnimation(1.0f	, 
					(imageView2.getWidth()-40f)/(imageView2.getWidth()), 
					1.0f, 
					(imageView2.getHeight()-40f)/(imageView2.getHeight()), 
					Animation.RELATIVE_TO_SELF, 
					0.5f, 
					Animation.RELATIVE_TO_SELF, 
					0.5f);
			animation_disappear.setDuration(150);
			imageView2.startAnimation(animation_disappear);
			imageView2.setVisibility(View.INVISIBLE);
		}
		selectedLayout =  v;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout_zhibo:
		case R.id.layout_tuijian:
			if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT&&event.getAction() == KeyEvent.ACTION_DOWN){
				layout.showNext();
				layout_jiasu.requestFocus();
				return true;
			}
			break;
		case R.id.layout_jiashu:
			if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT&&event.getAction() == KeyEvent.ACTION_DOWN){
				layout.showPre();
				layout_tuijian.requestFocus();
				return true;
			}
		default:
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.showtui:
//			startActivity(new Intent(this, ManagePushApkActivity.class));
			break;
		case R.id.layout_yuntui:
			startActivity(new Intent(this, CloudDataDisplayActivity.class));
			break;
		case R.id.layout_xunlei:
			startActivity(new Intent(this, XunLeiLXActivity.class));
			break;
		case R.id.layout_zhibo:
//			startActivity(new Intent(this, TvLiveSrcUpdateActivity.class));
			startActivity(new Intent(this, ScanActivity.class));
			break;
		case R.id.layout_tuijian:
//			startActivity(new Intent(this, AppRecommendActivity.class));
			startActivity(new Intent(this, SettingActivity.class));
			break;
		case R.id.layout_jiashu:
//			startActivity(new Intent(this, ScanActivity.class));
			break;
		case R.id.layout_upan:
			Log.d(TAG, "敬请期待");
			break;
		case R.id.layout_appguanli:
//			startActivity(new Intent(this, ManageAppActivity.class));
			break;
		case R.id.layout_ceshu:
			Log.d(TAG, "敬请期待");
			break;
		case R.id.layout_setting:
//			startActivity(new Intent(this, SettingActivity.class));
			break;
		case R.id.title_1:
			layout.snapToScreen(0);
			break;
		case R.id.title_2:
			layout.snapToScreen(1);
			break;
		default:
			break;
		}
	}
	
	private void displayPincode(){
		String displayString = "";
		String pincode = PreferencesUtils.getPincode(MainActivity.this);
		if(pincode!=null){
			for(int i= 0; i<pincode.length(); i++){
				if(i==pincode.length()-1){
					displayString += pincode.substring(i);
				}else{
					displayString += (pincode.substring(i,i+1) + "  ");
				}
			}
		}
		Log.d(TAG, displayString);
		pincodeText.setText(displayString);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
//		displayPincode();
		super.onResume();
		
		MobclickAgent.onResume(this);
	}
	
	
	class GetPinCodeTask implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Map<String, String> params = new HashMap<String, String>();
			params.put("app_key", "ijoyplus_android_0001bj");
			params.put("mac_address", Utils.getMacAdd());
			params.put("client", new Build().MODEL);
			Log.d(TAG, "client = " + new Build().MODEL);
			String str = HttpTools.post(MainActivity.this, Constant.BASE_URL+"/generatePinCode", params);
			Log.d(TAG, str);
			try {
				JSONObject data = new JSONObject(str);
				String pincode = data.getString("pinCode");
				String channel = data.getString("channel");
				PreferencesUtils.setPincode(MainActivity.this, pincode);
				PreferencesUtils.setChannel(MainActivity.this, channel);
				PreferencesUtils.changeAcceptedStatue(MainActivity.this, false);
				mHandler.sendEmptyMessage(MESSAGE_GETPINCODE_SUCCESS);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
//				Toast.makeText(MainActivity.this, "请求pinCode失败", 100).show();
				mHandler.sendEmptyMessage(MESSAGE_GETPINCODE_FAILE);
				e.printStackTrace();
			}
			  
		}
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(reciver);
		XunLeiLiXianUtil.Logout(getApplicationContext());
	}
	
}
