package com.joyplus.tvhelper;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.joyplus.tvhelper.faye.FayeService;
import com.joyplus.tvhelper.https.HttpUtils;
import com.joyplus.tvhelper.utils.Constant;
import com.joyplus.tvhelper.utils.Global;
import com.joyplus.tvhelper.utils.HttpTools;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.PreferencesUtils;
import com.joyplus.tvhelper.utils.Utils;
import com.umeng.analytics.MobclickAgent;

public class SettingActivity extends Activity implements OnClickListener{
	
	private static final int MESSAGE_GETPINCODE_SUCCESS = 0;
	private static final int MESSAGE_GETPINCODE_FAILE = MESSAGE_GETPINCODE_SUCCESS+1;
	private static final String TAG = "SettingActivity";
	private Button btn_setting;
	private Button btn_help;
	private Button btn_about;
	
	private LinearLayout  layout_setting;
	private ScrollView  layout_help;
	private ScrollView  layout_about;
	private WebView webView;
	
	private TextView seletedView;
	private TextView pincodeText;
	private TextView versionName;
	
	private LinearLayout layout_refresh,layout_deleteApk, layout_confirm;
	
	private ImageView switch_delete, switch_confirm;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MESSAGE_GETPINCODE_SUCCESS:
				displayPincode();
				sendBroadcast(new Intent(Global.ACTION_PINCODE_REFRESH));
				startService(new Intent(SettingActivity.this, FayeService.class));
				removeDialog(0);
				break;
			case MESSAGE_GETPINCODE_FAILE:
				Toast.makeText(SettingActivity.this, "请求pinCode失败", 100).show();
				removeDialog(0);
				break;
			default:
				break;
			}
		}
	};
	
	private boolean isdelete = false;
	private boolean isconfirm = true;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		btn_setting= (Button) findViewById(R.id.btn_setting);
		btn_help= (Button) findViewById(R.id.btn_help);
		btn_about= (Button) findViewById(R.id.btn_about);
		pincodeText = (TextView) findViewById(R.id.pincode_text);
		versionName = (TextView) findViewById(R.id.versionCode);
		
		btn_setting.setOnClickListener(this);
		btn_help.setOnClickListener(this);
		btn_about.setOnClickListener(this);
		
		layout_refresh = (LinearLayout) findViewById(R.id.layout_refressPin);
		layout_deleteApk = (LinearLayout) findViewById(R.id.layout_deleteApk);
		layout_confirm = (LinearLayout) findViewById(R.id.layout_confirm);
		
		layout_refresh.setOnClickListener(this);
		layout_deleteApk.setOnClickListener(this);
		layout_confirm.setOnClickListener(this);
		
		layout_setting = (LinearLayout) findViewById(R.id.layout_setting);
		layout_help = (ScrollView) findViewById(R.id.layout_help);
		layout_about = (ScrollView) findViewById(R.id.layout_about);
		webView = (WebView) findViewById(R.id.webView);
		
		switch_delete = (ImageView) findViewById(R.id.switch_deleteApk);
		switch_confirm = (ImageView) findViewById(R.id.switch_confirm);
		
		isconfirm = PreferencesUtils.isneedConfirm(this);
		isdelete = PreferencesUtils.isautodelete(this);
		
		updateSwitch();
		displayPincode();
		String versionName_str = null;
		try {
			versionName_str = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(versionName_str != null){
			versionName.setText("版本号:"+versionName_str);
		}else{
			versionName.setVisibility(View.INVISIBLE);
		}
			
		
		seletedView = btn_setting; 
		btn_setting.setBackgroundResource(R.drawable.highlight);
		btn_setting.setTextColor(Color.BLACK);
		
		webView.getSettings().setJavaScriptEnabled(false);
		webView.getSettings().setEnableSmoothTransition(true);
		webView.setBackgroundColor(Color.TRANSPARENT);
		webView.setWebViewClient(new WebViewClient()
		   {
		          @Override
		          public boolean shouldOverrideUrlLoading(WebView view, String url)
		          {
		 
		            view.loadUrl(url); // 在当前的webview中跳转到新的url
		 
		            return true;
		          }
		    });
//		webView.loadUrl("http://www.joyplus.tv/faq-tv?"+System.currentTimeMillis());
		webView.loadUrl(Constant.URL_FAQ +"?"+System.currentTimeMillis());
		layout_refresh.requestFocus();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == seletedView){
			return;
		}
		switch (v.getId()) {
		case R.id.btn_setting:
			seletedView.setBackgroundResource(R.drawable.bg_title_setting_selector);
			seletedView.setTextColor(getResources().getColorStateList(R.color.setting_title_selector));
			
			btn_setting.setBackgroundResource(R.drawable.highlight);
			btn_setting.setTextColor(Color.BLACK);
			seletedView = btn_setting;
			
			layout_setting.setVisibility(View.VISIBLE);
			layout_setting.requestFocus();
			layout_help.setVisibility(View.GONE);
			layout_about.setVisibility(View.GONE);
			
			break;
		case R.id.btn_help:
			seletedView.setBackgroundResource(R.drawable.bg_title_setting_selector);
			seletedView.setTextColor(getResources().getColorStateList(R.color.setting_title_selector));
			
			btn_help.setBackgroundResource(R.drawable.highlight);
			btn_help.setTextColor(Color.BLACK);
			seletedView = btn_help;
			layout_setting.setVisibility(View.GONE);
			layout_help.setVisibility(View.VISIBLE);
			layout_help.requestFocus();
			layout_about.setVisibility(View.GONE);
			break;
		case R.id.btn_about:
			seletedView.setBackgroundResource(R.drawable.bg_title_setting_selector);
			seletedView.setTextColor(getResources().getColorStateList(R.color.setting_title_selector));
			
			btn_about.setBackgroundResource(R.drawable.highlight);
			btn_about.setTextColor(Color.BLACK);
			seletedView = btn_about;
			layout_setting.setVisibility(View.GONE);
			layout_help.setVisibility(View.GONE);
			layout_about.setVisibility(View.VISIBLE);
			layout_about.requestFocus();
			break;
		case R.id.layout_refressPin:
			if(HttpUtils.isNetworkAvailable(this)){
				
				showDialog(0);
//				sendBroadcast(new Intent(Global.ACTION_PINCODE_REFRESH));
				new Thread(new GetPinCodeTask()).start();
			}else {
				
				Utils.showToast(this, "检查网络设置");
			}
			
			break;
		case R.id.layout_deleteApk:
			isdelete = !isdelete;
			PreferencesUtils.setIsautodelete(this, isdelete);
			updateSwitch();
			break;
		case R.id.layout_confirm:
			isconfirm = !isconfirm;
			PreferencesUtils.setIsneedConfirm(this, isconfirm);
			updateSwitch();
			break;
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case 0:
			ProgressDialog d = ProgressDialog.show(this, null, "正在重新生成pincode···");
			return d;

		default:
			return super.onCreateDialog(id);
		}
		
	}
	
	private void displayPincode(){
		String displayString = "";
		String pincode = PreferencesUtils.getPincode(SettingActivity.this);
		if(pincode!=null){
			for(int i= 0; i<pincode.length(); i++){
				if(i==pincode.length()-1){
					displayString += pincode.substring(i);
				}else{
					displayString += (pincode.substring(i,i+1) + "  ");
					Log.d(TAG, displayString);
				}
			}
		}
		Log.d(TAG, displayString);
		pincodeText.setText(displayString);
	}
	
	private void updateSwitch(){
		if(isdelete){
			switch_delete.setImageResource(R.drawable.swith_on);
		}else{
			switch_delete.setImageResource(R.drawable.swith_off);
		}
		if(isconfirm){
			switch_confirm.setImageResource(R.drawable.swith_on);
		}else{
			switch_confirm.setImageResource(R.drawable.swith_off);
		}
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
//			Log.d(TAG, "client = " + new Build().MODEL);
			String str = HttpTools.post(SettingActivity.this, Constant.BASE_URL+"/generatePinCode", params);
			Log.d(TAG, str);
			try {
				JSONObject data = new JSONObject(str);
				String pincode = data.getString("pinCode");
				String channel = data.getString("channel");
				PreferencesUtils.setPincode(SettingActivity.this, pincode);
				PreferencesUtils.setChannel(SettingActivity.this, channel);
				PreferencesUtils.changeAcceptedStatue(SettingActivity.this, false);
				PreferencesUtils.setPincodeMd5(SettingActivity.this, null);
				mHandler.sendEmptyMessage(MESSAGE_GETPINCODE_SUCCESS);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mHandler.sendEmptyMessage(MESSAGE_GETPINCODE_FAILE);
			}
			  
		}
	}
}
