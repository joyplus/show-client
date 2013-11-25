package com.joyplus.tvhelper.ui;

import org.cocos2dx.lib.Cocos2dxHelper;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.joyplus.tvhelper.MyApp;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.entity.XLLXUserInfo;
import com.joyplus.tvhelper.utils.MD5Util;
import com.joyplus.tvhelper.utils.Utils;
import com.joyplus.tvhelper.utils.XunLeiLiXianUtil;

public class Cocos2dxXunLeiLoginDialog extends Dialog {

	// ===========================================================
	// Fields
	// ===========================================================

	private EditText mAccountEditText;
	private EditText mPassWordEditText;
	private EditText mYanzhengMaEditText;
	private ImageView mYanzhengMa;
	private Button mLoginButton;
	
	private LinearLayout mProgressBar;
	private LinearLayout mLayout;
	private LinearLayout mYanZhengMaLoyout;


	private static final int LOGIN_ERROR = 2;
	private static final int LOGIN_SUCESS = 1;
	private static final int GET_USERINFO_SUCCESS = 3;
	private static final int VERIFY_CODE_SUCCESS = 6;
	private static final int VERIFY_CODE_FAIL = 7;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOGIN_SUCESS:
				//登陆成功
				Utils.showToast(getContext(), "登陆成功");
				saveUserAndPassWord();
				getUserInfo();
				break;
			case LOGIN_ERROR:
				//登陆失败
				int loginErrorFlag = msg.arg1;
				switch (loginErrorFlag) {
				case 1:
					if(mYanZhengMaLoyout.getVisibility() == View.VISIBLE){
						Utils.showToast(getContext(), "输入有误，请重新输入");
					}else {
						mYanZhengMaLoyout.setVisibility(View.VISIBLE);
						Utils.showToast(getContext(), "请手动输入验证码");
					}
//					}
//					
//					verifyLayout.setVisibility(View.VISIBLE);
					break;
				case 2:
					Utils.showToast(getContext(), "密码错误");
					clearPasswdRecord();
					break;
				case 4:
				case 5:
					Utils.showToast(getContext(), "账户不存在");
					break;
				case 6:
					Utils.showToast(getContext(), "账户被锁定");
					break;
				case 10:
					Utils.showToast(getContext(), "获取用户信息失败,请重试或者重新登录");
					break;
				case 11:
					Utils.showToast(getContext(), "获取列表失败");
					break;
				default:
					Utils.showToast(getContext(), "网络超时，稍后重试");
					break;
				}
				mProgressBar.setVisibility(View.GONE);
				mLayout.setVisibility(View.VISIBLE);
				if(mYanZhengMaLoyout.getVisibility() == View.VISIBLE){
					mYanzhengMaEditText.setText("");
					getVerifyBitmap();
				}
				break;
			case GET_USERINFO_SUCCESS:
				dismiss();
				Cocos2dxHelper.setXunLeiLoginDialogResult(false);
				break;
			case VERIFY_CODE_SUCCESS:
				Bitmap bitmap = (Bitmap) msg.obj;
				if(bitmap != null){
					mYanzhengMa.setBackgroundDrawable(new BitmapDrawable(getContext().getResources(),bitmap));
				}else{
					
					mYanzhengMa.setBackgroundDrawable(null);
					Utils.showToast(getContext(), "获取验证码图片失败,检查网络是否连接");
				}
				break;
			case VERIFY_CODE_FAIL:
				Utils.showToast(getContext(), "获取验证码图片失败,检查网络是否连接");
				break;
			}
		}
	};
	private void getVerifyBitmap() {
		// TODO Auto-generated method stub
		MyApp.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(!TextUtils.isEmpty(mAccountEditText.getText().toString())){
					Bitmap bitmap = XunLeiLiXianUtil.getVerifyCodeBitmap(getContext(),
							mAccountEditText.getText().toString());
					if(bitmap != null){
						Message message = mHandler.obtainMessage(VERIFY_CODE_SUCCESS, bitmap);
						mHandler.sendMessage(message);
						return;
					}
				}
				Message message = mHandler.obtainMessage(VERIFY_CODE_FAIL);
				mHandler.sendMessage(message);
			}
		});
	};

	// ===========================================================
	// Constructors
	// ===========================================================

	public Cocos2dxXunLeiLoginDialog(final Context pContext) {
		super(pContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
//		super(context, R.style.Theme_Translucent);
	}

	@Override
	protected void onCreate(final Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);

//		this.getWindow().setBackgroundDrawable(new ColorDrawable(0x80000000));
		this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		View	contetnView  = this.getLayoutInflater().inflate(R.layout.dialog_xunlei_login, null);
		
		mAccountEditText = (EditText)contetnView.findViewById(R.id.account_edit);
		mPassWordEditText = (EditText)contetnView.findViewById(R.id.passowrd_edit);
		mYanzhengMaEditText = (EditText)contetnView.findViewById(R.id.yanzhengma_edit);
		
		mProgressBar = (LinearLayout)contetnView.findViewById(R.id.layout_xunlei_progress);
		mYanZhengMaLoyout = (LinearLayout) contetnView.findViewById(R.id.layout_yamzhengma);
		mLayout = (LinearLayout) contetnView.findViewById(R.id.layout_xunlei_login);
		
		mYanzhengMa = (ImageView) contetnView.findViewById(R.id.yanzhengm_img);
		mLoginButton = (Button) contetnView.findViewById(R.id.xunlei_login);
		initView();
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
				Cocos2dxHelper.setXunLeiLoginDialogResult(true);
			}
		});
		
		setContentView(contetnView);
		XunLeiLiXianUtil.saveCookies(getContext(), null);
	}

	private void initView(){
		if (!TextUtils.isEmpty(XunLeiLiXianUtil.getLoginUserName(getContext()))) {
			mAccountEditText.setText(XunLeiLiXianUtil.getLoginUserName(getContext()));
		}

		if (!TextUtils.isEmpty(XunLeiLiXianUtil.getLoginUserPasswd(getContext()))) {
			mPassWordEditText.setText(XunLeiLiXianUtil.getLoginUserPasswd(getContext()));
		}
//		if (!TextUtils.isEmpty(XunLeiLiXianUtil.getCookie(getContext()))
//				&& !TextUtils.isEmpty(XunLeiLiXianUtil.getLoginUserPasswd(getContext()))) {//already login
//			isFirstLogin = false;
//			handler.sendEmptyMessage(START_LOGIN);
//			showDialog(DIALOG_WAITING);
//		}
		if(mYanZhengMaLoyout.getVisibility() == View.VISIBLE){
			mYanzhengMaEditText.setText("");
			getVerifyBitmap();
		}
	}
	
	private void login(){
		mProgressBar.setVisibility(View.VISIBLE);
		mLayout.setVisibility(View.INVISIBLE);
		MyApp.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int loginFlag = -10;
				String username = mAccountEditText.getText().toString();
				String passwd = mPassWordEditText.getText().toString();
				String verify = mYanzhengMaEditText.getText().toString();
				if (passwd != null&& passwd.equals(XunLeiLiXianUtil
					.getLoginUserPasswd(getContext()))) {
					loginFlag = XunLeiLiXianUtil.Login(getContext(),username, passwd, verify,true);
				} else {
					loginFlag = XunLeiLiXianUtil.Login(getContext(),username, passwd, verify);
				}

				if (loginFlag == 0) {
					mHandler.sendEmptyMessage(LOGIN_SUCESS);
				} else {
					Message message = mHandler.obtainMessage(LOGIN_ERROR, loginFlag,-1);
					mHandler.sendMessage(message);
				}
			}
		});
	}
	
	private void clearPasswdRecord(){
		mPassWordEditText.setText("");
		XunLeiLiXianUtil.saveLoginUserPasswd(getContext(), "");
	}
	
	private void saveUserAndPassWord(){
		if (!TextUtils.isEmpty(XunLeiLiXianUtil.getLoginUserName(getContext()))) {
			if (!XunLeiLiXianUtil.getLoginUserName(getContext()).equals(
					mAccountEditText.getText().toString())) {
				XunLeiLiXianUtil.saveLoginUserName(getContext(),
						mAccountEditText.getText().toString());
				XunLeiLiXianUtil.saveLoginUserPasswd(
						getContext(), 
						MD5Util.getMD5String(mPassWordEditText.getText().toString()));
			}else{
				if(!TextUtils.isEmpty(XunLeiLiXianUtil.getLoginUserPasswd(getContext()))&&XunLeiLiXianUtil.getLoginUserPasswd(getContext()).equals(
						mPassWordEditText.getText().toString())){
					XunLeiLiXianUtil.saveLoginUserPasswd(
							getContext(), 
							mPassWordEditText.getText().toString());
				}else{
					XunLeiLiXianUtil.saveLoginUserPasswd(
							getContext(), 
							MD5Util.getMD5String(mPassWordEditText.getText().toString()));
				}
			}
		} else {
			XunLeiLiXianUtil.saveLoginUserName(getContext(),
					mAccountEditText.getText().toString());
			XunLeiLiXianUtil.saveLoginUserPasswd(
					getContext(),
					MD5Util.getMD5String(mPassWordEditText.getText().toString()));
		}
	}
	
	private void getUserInfo(){
		MyApp.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
			XLLXUserInfo xllxUserInfo = XunLeiLiXianUtil.getUser(getContext(), 
						XunLeiLiXianUtil.getCookieHeader(getContext()));
				if (xllxUserInfo != null) {
					Message message = mHandler.obtainMessage(GET_USERINFO_SUCCESS,xllxUserInfo);
					mHandler.sendMessage(message);
				} else {
					Message message = mHandler.obtainMessage(LOGIN_ERROR, 10, -1);
					mHandler.sendMessage(message);
				}

			}
		});
	}
}
