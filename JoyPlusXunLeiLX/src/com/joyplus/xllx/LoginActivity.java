package com.joyplus.xllx;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.joyplus.app.MyApp;
import com.joyplus.entity.XLLXFileInfo;
import com.joyplus.entity.XLLXUserInfo;
import com.joyplus.utils.Utils;
import com.joyplus.utils.XunLeiLiXianUtil;

public class LoginActivity extends Activity {

	private static final String TAG = "LoginActivity";
	
//	private static final int DIALOG_WAITING = 0;

	private static final int LOGIN_ERROR = 2;
	private static final int LOGIN_SUCESS = 1;
	private static final int REFESH_USERINFO = 3;
	private static final int REFRESH_LIST = 4;
	private static final int START_LOGIN = 5;

	private View loginLayout, logoutLayout;
	private EditText userNameEdit,passwdEdit;
	
	private Button loginBt,logoutBt;
	
	private TextView nickNameTv,userIdTv,vipRankTv,outDateTv;

	private boolean isFirstLogin = true;
	
	private int pageIndex = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_main);

		initView();

		loginLayout.setVisibility(View.VISIBLE);
		logoutLayout.setVisibility(View.INVISIBLE);

//		if (XunLeiLiXianUtil.getCookie(getApplicationContext()) != null
//				&& !XunLeiLiXianUtil.getCookie(getApplicationContext()).equals("")) {
//
//			isFirstLogin = false;
//			userNameEdit.setText(XunLeiLiXianUtil.getUsrname(getApplicationContext()));
//			
//			handler.sendEmptyMessage(START_LOGIN);
//			MyApp.pool.execute(getUsrInfoRunnable);
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void initView() {

		loginLayout = findViewById(R.id.rl_login);
		logoutLayout = findViewById(R.id.rl_logout);
		
		userNameEdit = (EditText) findViewById(R.id.et_username);
		passwdEdit = (EditText) findViewById(R.id.et_passwd);
		
		userNameEdit.setText("13918413043@163.com");
		passwdEdit.setText("6105586");
		
		loginBt = (Button) findViewById(R.id.bt_login);
		logoutBt = (Button) findViewById(R.id.bt_logout);
		
		nickNameTv = (TextView) findViewById(R.id.tv_lx_logout_nickname_content);
		userIdTv = (TextView) findViewById(R.id.tv_lx_logout_userid_content);
		vipRankTv = (TextView) findViewById(R.id.tv_lx_logout_rank_content);
		outDateTv = (TextView) findViewById(R.id.tv_lx_logout_outofdate_content);
		
		loginBt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(TextUtils.isEmpty(userNameEdit.getText().toString())) {
					
					Utils.showToast(LoginActivity.this, "�������û���");
					
					return;
				}
				
				if(TextUtils.isEmpty(passwdEdit.getText().toString())) {
					
					Utils.showToast(LoginActivity.this, "����������");
					
					return;
				}
				
				MyApp.pool.execute(loginRunnable);
//				showDialog(DIALOG_WAITING);
			}
		});
		
		logoutBt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				XunLeiLiXianUtil.Logout(getApplicationContext());
				setLogin(false);
				pageIndex = 1;
				isFirstLogin = true;
			}
		});
	}
	
	private void setLogin(boolean isLogin) {
		
		if(!isLogin) {
			
			loginLayout.setVisibility(View.VISIBLE);
			logoutLayout.setVisibility(View.INVISIBLE);
		} else {
			
			logoutLayout.setVisibility(View.VISIBLE);
			loginLayout.setVisibility(View.INVISIBLE);
		}
	}
	
//	@Override
//	protected Dialog onCreateDialog(int id) {
//		// TODO Auto-generated method stub
//		
//		switch (id) {
//		case DIALOG_WAITING:
//			WaitingDialog dlg = new WaitingDialog(this);
//			dlg.show();
//			dlg.setOnCancelListener(new OnCancelListener() {
//
//				@Override
//				public void onCancel(DialogInterface dialog) {
//					// TODO Auto-generated method stub
//					finish();
//				}
//			});
//			dlg.setDialogWindowStyle();
//			return dlg;
//
//		default:
//			break;
//		}
//		return super.onCreateDialog(id);
//	}
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
//			super.handleMessage(msg);
			
			int what = msg.what;
			
			switch (what) {
			case LOGIN_SUCESS://��¼�ɹ�
				Utils.showToast(LoginActivity.this, "��½�ɹ�");
				MyApp.pool.execute(getUsrInfoRunnable);
				break;
			case REFESH_USERINFO://ˢ���û���Ϣ�ɹ�
				setLogin(true);//��ת���û���Ϣ����
				
				XLLXUserInfo xllxUserInfo = (XLLXUserInfo) msg.obj;
				if(xllxUserInfo != null) {
					
					nickNameTv.setText(xllxUserInfo.nickname);
					userIdTv.setText(xllxUserInfo.usrname);
					vipRankTv.setText("VIP" + xllxUserInfo.isvip + "");
					outDateTv.setText(xllxUserInfo.expiredate.replaceAll("-", "."));
				}
				
				MyApp.pool.execute(getVideoList);
				break;
			case REFRESH_LIST://ˢ���û���Ϣ�ɹ�
//				removeDialog(DIALOG_WAITING);
				break;
			case START_LOGIN://ֱ�ӽ����û�����
				MyApp.pool.execute(getUsrInfoRunnable);
				break;
			case LOGIN_ERROR:
				
				int loginErrorFlag = msg.arg1;
				switch (loginErrorFlag) {
				case 1:
					Utils.showToast(LoginActivity.this, "��ȡ��֤��ʧ��,�Ժ�����");
					break;
				case 2:
					Utils.showToast(LoginActivity.this, "�������");
					break;
				case 4:
				case 5:
					Utils.showToast(LoginActivity.this, "�˻�������");
					break;
				case 6:
					Utils.showToast(LoginActivity.this, "�˻�������");
					break;
				case 10:
					Utils.showToast(LoginActivity.this, "��ȡ�û���Ϣʧ��,�����Ի������µ�¼");
					break;
				case 11:
					Utils.showToast(LoginActivity.this, "��ȡ�б�ʧ��");
					break;
				default:
					Utils.showToast(LoginActivity.this, "���糬ʱ���Ժ�����");
					break;
				}
				break;

			default:
				break;
			}
		}
		
	};
	
	private Runnable loginRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			int loginFlag = XunLeiLiXianUtil.Login(LoginActivity.this,
					userNameEdit.getText().toString(), passwdEdit.getText().toString());
			
			if(loginFlag == 0) {
				
				handler.sendEmptyMessage(LOGIN_SUCESS);
			} else {
				
				Message message = handler.obtainMessage(LOGIN_ERROR, loginFlag,-1);
				handler.sendMessage(message);
			}
		}
	};
	
	private Runnable getUsrInfoRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			XLLXUserInfo xllxUserInfo = null;
			Log.d(TAG, "getUsrInfoRunnable--->");
			if(!isFirstLogin) {
				
				xllxUserInfo = XunLeiLiXianUtil.getUserInfoFromLocal(getApplicationContext());
				if(xllxUserInfo != null) {
					
					Message message = handler.obtainMessage(REFESH_USERINFO,xllxUserInfo);
					handler.sendMessage(message);
				} else {
					
					Message message = handler.obtainMessage(LOGIN_ERROR, 10,-1);
					handler.sendMessage(message);
				}
			} else {
				
				xllxUserInfo = XunLeiLiXianUtil.getUser(LoginActivity.this,
						XunLeiLiXianUtil.getCookieHeader(LoginActivity.this));
				
				if(xllxUserInfo != null) {
					
					Log.i(TAG, "getUsrInfoRunnable--->xllxUserInfo:"  + xllxUserInfo.toString());
					Message message = handler.obtainMessage(REFESH_USERINFO, xllxUserInfo);
					handler.sendMessage(message);
				} else {
					
					Message message = handler.obtainMessage(LOGIN_ERROR, 10,-1);
					handler.sendMessage(message);
				}
				
			}
		}
	};
	
	 private Runnable getVideoList = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.d(TAG, "getVideoList--->");
			 List<XLLXFileInfo> list = XunLeiLiXianUtil.
					 getVideoList(LoginActivity.this, 30, pageIndex);
			
			 if(list != null && list.size() > 0) {
				 
				 Message message = handler.obtainMessage(REFRESH_LIST, list);
				 handler.sendMessage(message);
				 
				 for(int i=0;i<list.size();i++) {
					 
					 XLLXFileInfo xllxFileInfo = list.get(i);
					 if(xllxFileInfo != null) {
						 
						 Log.d(TAG, "list xllxFileInfo--->" + xllxFileInfo);
					 }
				 }
			 } else {
				 
				 Message message = handler.obtainMessage(LOGIN_ERROR, 11, -1);
				 handler.sendMessage(message);
			 }
		}
	 };

}
