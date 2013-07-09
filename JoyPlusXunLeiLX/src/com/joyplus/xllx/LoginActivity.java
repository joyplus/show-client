package com.joyplus.xllx;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.joyplus.adapter.PlayListAdapter;
import com.joyplus.app.MyApp;
import com.joyplus.entity.CurrentPlayDetailData;
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
	
	private ListView playerListView;

	private boolean isFirstLogin = true;
	
	private int pageIndex = 1;
	
	private List<XLLXFileInfo> playerList = new ArrayList<XLLXFileInfo>();
	
	private MyApp app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_main);
		
		app = (MyApp) getApplication();

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
		
		playerListView = (ListView) findViewById(R.id.lv_movie);
		
		loginBt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(TextUtils.isEmpty(userNameEdit.getText().toString())) {
					
					Utils.showToast(LoginActivity.this, "请输入用户名");
					
					return;
				}
				
				if(TextUtils.isEmpty(passwdEdit.getText().toString())) {
					
					Utils.showToast(LoginActivity.this, "请输入密码");
					
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
		
		playerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.i(TAG, "onItemClick--->");
				
				if(playerList != null && playerList.size() > 0) {
					
					if(playerList.get(position) != null) {
						
						XLLXFileInfo xllxFileInfo = playerList.get(position);
						Log.i(TAG, "onItemClick--->xllxFileInfo:" + xllxFileInfo.toString());
						if(xllxFileInfo.src_url != null) {
							
							//如果url不为空，直接传给播放器
							CurrentPlayDetailData currentPlayDetailData = new CurrentPlayDetailData();
							currentPlayDetailData.prod_src = xllxFileInfo.src_url;
							currentPlayDetailData.prod_type = -1;
							
							if(xllxFileInfo.file_name != null && !xllxFileInfo.file_name.equals("")) {
								
								currentPlayDetailData.prod_name = xllxFileInfo.file_name;
							}
							
							if(xllxFileInfo.duration != null && !xllxFileInfo.file_name.equals("")
									&& !xllxFileInfo.file_name.equals("0")) {
								
								long tempDuration = 0l;
								try {
									tempDuration = Long.valueOf(xllxFileInfo.duration);
								} catch (NumberFormatException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								if(tempDuration > 0) {
									
									currentPlayDetailData.prod_time = tempDuration;
								}
							}
							
							app.setmCurrentPlayDetailData(currentPlayDetailData);
							startActivity(new Intent(LoginActivity.this, VideoPlayerJPActivity.class));
						}
					}
				}
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
			case LOGIN_SUCESS://登录成功
				Utils.showToast(LoginActivity.this, "登陆成功");
				MyApp.pool.execute(getUsrInfoRunnable);
				break;
			case REFESH_USERINFO://刷新用户信息成功
				setLogin(true);//跳转到用户信息界面
				
				XLLXUserInfo xllxUserInfo = (XLLXUserInfo) msg.obj;
				if(xllxUserInfo != null) {
					
					nickNameTv.setText(xllxUserInfo.nickname);
					userIdTv.setText(xllxUserInfo.usrname);
					vipRankTv.setText("VIP" + xllxUserInfo.isvip + "");
					outDateTv.setText(xllxUserInfo.expiredate.replaceAll("-", "."));
				}
				
				MyApp.pool.execute(getVideoList);
				break;
			case REFRESH_LIST://刷新用户信息成功
//				removeDialog(DIALOG_WAITING);
				
				List<XLLXFileInfo> list =  (List<XLLXFileInfo>) msg.obj;

				if(list != null && list.size() >0) {
					playerList = list;
					playerListView.setAdapter(new PlayListAdapter(LoginActivity.this,playerList ));
				}
				break;
			case START_LOGIN://直接进入用户界面
				MyApp.pool.execute(getUsrInfoRunnable);
				break;
			case LOGIN_ERROR:
				
				int loginErrorFlag = msg.arg1;
				switch (loginErrorFlag) {
				case 1:
					Utils.showToast(LoginActivity.this, "获取验证码失败,稍后重试");
					break;
				case 2:
					Utils.showToast(LoginActivity.this, "密码错误");
					break;
				case 4:
				case 5:
					Utils.showToast(LoginActivity.this, "账户不存在");
					break;
				case 6:
					Utils.showToast(LoginActivity.this, "账户被锁定");
					break;
				case 10:
					Utils.showToast(LoginActivity.this, "获取用户信息失败,请重试或者重新登录");
					break;
				case 11:
					Utils.showToast(LoginActivity.this, "获取列表失败");
					break;
				default:
					Utils.showToast(LoginActivity.this, "网络超时，稍后重试");
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
