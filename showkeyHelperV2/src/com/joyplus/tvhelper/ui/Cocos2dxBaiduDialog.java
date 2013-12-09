package com.joyplus.tvhelper.ui;

import org.cocos2dx.lib.Cocos2dxHelper;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.baidu.oauth.BaiduOAuth;
import com.baidu.oauth.BaiduOAuth.BaiduOAuthResponse;
import com.joyplus.tvhelper.MyApp;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.https.HttpUtils;
import com.joyplus.tvhelper.utils.HttpTools;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.PreferencesUtils;
import com.joyplus.tvhelper.utils.Utils;

public class Cocos2dxBaiduDialog extends Dialog {

	// ===========================================================
	// Fields
	// ===========================================================

	private ImageView mErWeima;
	private Button mLoginButton;
	
	private AQuery aq;

	private static final String API_Key = "VqMDPbqtjZcYRlYj2XGIxi7i";
	private static final String Secret_Key = "o3jncjlDfxMhiaUgFXAO6NdWcUHgzPYc";
	
	private static final int TIME_DELAY = 5000;
	
	private String device_code;
	private String user_code;

	private View rootView;
	
	private static final int MESSAGE_GET_ERWEIMA_SUCESS = 1;
	private static final int MESSAGE_GET_ACCESSTOKEN_SUCESS = 2;
	private static final int MESSAGE_GET_ACCESSTOKEN_FAILE = 3;
	private static final int MESSAGE_GET_ERWEIMA_FAILE = 4;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_GET_ERWEIMA_SUCESS:
				String url = (String)msg.obj;
				Log.d("BaiduDialog", "get erweima success ->" +url);
				aq.id(mErWeima).image(url);
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						verificationLogin();
					}
				}, TIME_DELAY);
				break;
			case MESSAGE_GET_ERWEIMA_FAILE:
				Utils.showToast(getContext(), "获取二维码失败",rootView);
//				Toast.makeText(getContext(), "获取二维码失败", 100).show();
				break;
			case MESSAGE_GET_ACCESSTOKEN_SUCESS:
				//通知登陆成功
				dismiss();
				mHandler.removeCallbacksAndMessages(null);
				Cocos2dxHelper.setBaiduLoginDialogResult(false);
				break;
			case MESSAGE_GET_ACCESSTOKEN_FAILE:
				verificationLogin();
				break;
			};
		}
	};
	

	// ===========================================================
	// Constructors
	// ===========================================================

	public Cocos2dxBaiduDialog(final Context pContext) {
		super(pContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
//		super(context, R.style.Theme_Translucent);
	}

	/* (non-Javadoc)
	 * @see android.app.Dialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
//		this.getWindow().setBackgroundDrawable(new ColorDrawable(0x80000000));
		
		this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		rootView  = this.getLayoutInflater().inflate(R.layout.dialog_baidu_login, null);
		aq = new AQuery(getOwnerActivity());
		mErWeima = (ImageView) rootView.findViewById(R.id.baidu_login_erweima);
		mLoginButton = (Button) rootView.findViewById(R.id.baidu_login_button);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		mLoginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				login();
			}
		});
		
		
		this.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				dismiss();
				mHandler.removeCallbacksAndMessages(null);
				Cocos2dxHelper.setBaiduLoginDialogResult(true);
			}
		});
		setContentView(rootView);
		PreferencesUtils.setBaiduAccessToken(getContext(), null);
		getErweima();
	}

	
	private void login(){
		BaiduOAuth oauthClient = new BaiduOAuth();
		oauthClient.startOAuth(getContext(), API_Key, new BaiduOAuth.OAuthListener() {
			@Override
			public void onException(String msg) {
//				Toast.makeText(getContext(), "Login failed " + msg, Toast.LENGTH_SHORT).show();
				Utils.showToast(getContext(), "登录失败",rootView);
			}
			@Override
			public void onComplete(BaiduOAuthResponse response) {
				if(null != response){
					String mbOauth = response.getAccessToken();
					Log.d("sss", mbOauth);
					PreferencesUtils.setBaiduAccessToken(getContext(), mbOauth);
					mHandler.sendEmptyMessage(MESSAGE_GET_ACCESSTOKEN_SUCESS);
				}
			}
			@Override
			public void onCancel() {
//				Toast.makeText(getContext(), "Login cancelled", Toast.LENGTH_SHORT).show();
				Utils.showToast(getContext(), "登录失败",rootView);
			}
		});
	}
	
	private void verificationLogin(){
		if(!this.isShowing())
		{
			return ;
		}
		MyApp.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String url = "https://openapi.baidu.com/oauth/2.0/token?grant_type=device_token&code="+
						 device_code + "&client_id=" +
						 API_Key + "&client_secret=" + Secret_Key;
//				String result = HttpTools.get(getContext(), url);
				String result = HttpUtils.getContent(url,null,null);
				if(result == null)
				{
					mHandler.sendEmptyMessageDelayed(MESSAGE_GET_ACCESSTOKEN_FAILE, TIME_DELAY);
					return ;
				}
				Log.d("BaiduDialog","result -- >" + result);
				try {
					JSONObject json = new JSONObject(result);
					
					if(!json.has("error")&&json.has("access_token"))
					{
						String access_token = json.getString("access_token");
						PreferencesUtils.setBaiduAccessToken(getContext(), access_token);
						mHandler.sendEmptyMessage(MESSAGE_GET_ACCESSTOKEN_SUCESS);
					}else{
						mHandler.sendEmptyMessageDelayed(MESSAGE_GET_ACCESSTOKEN_FAILE, TIME_DELAY);
					}
					
				} catch (JSONException e) {
					// TODO: handle exception
					mHandler.sendEmptyMessageDelayed(MESSAGE_GET_ACCESSTOKEN_FAILE, TIME_DELAY);
				}
			}
		});
	}
	
	private void getErweima(){
		MyApp.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String url = "https://openapi.baidu.com/oauth/2.0/device/code?client_id="
						 + API_Key +"&response_type=device_code&scope=basic,netdisk";
				String result = HttpTools.get(getContext(), url);
//				String result = HttpUtils.getContent(url,null,null);
				if(result == null)
				{
					mHandler.sendEmptyMessage(MESSAGE_GET_ERWEIMA_FAILE);
					return ;
				}
				try {
					JSONObject json = new JSONObject(result);
					device_code = json.getString("device_code");
					user_code = json.getString("user_code");
					String erwermaUlr = json.getString("qrcode_url");
					Message msg = mHandler.obtainMessage(MESSAGE_GET_ERWEIMA_SUCESS);
					msg.obj = erwermaUlr;
					mHandler.sendMessage(msg);
				} catch (JSONException e) {
					// TODO: handle exception
				}
			}
		});
	}
}
