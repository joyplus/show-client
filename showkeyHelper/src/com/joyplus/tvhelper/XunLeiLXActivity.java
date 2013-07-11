package com.joyplus.tvhelper;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.joyplus.tvhelper.adapter.PlayExpandListAdapter;
import com.joyplus.tvhelper.entity.CurrentPlayDetailData;
import com.joyplus.tvhelper.entity.XLLXFileInfo;
import com.joyplus.tvhelper.entity.XLLXUserInfo;
import com.joyplus.tvhelper.ui.WaitingDialog;
import com.joyplus.tvhelper.utils.Utils;
import com.joyplus.tvhelper.utils.XunLeiLiXianUtil;

public class XunLeiLXActivity extends Activity {

	private static final String TAG = "XunLeiLXActivity";
	
	private static final int DIALOG_WAITING = 0;

	private static final int LOGIN_ERROR = 2;
	private static final int LOGIN_SUCESS = 1;
	private static final int REFESH_USERINFO = 3;
	private static final int REFRESH_LIST = 4;
	private static final int START_LOGIN = 5;

	private View loginLayout, logoutLayout;
	private View userNameLayout,passwdLayout;
	private EditText userNameEdit,passwdEdit;
	private Button loginBt,logoutBt;
	private TextView nickNameTv,userIdTv,vipRankTv,outDateTv;
	private ExpandableListView playerListView;
	
	private PlayExpandListAdapter playExpandListAdapter;
	
	private MyApp app;

	private ArrayList<XLLXFileInfo> playerList = new ArrayList<XLLXFileInfo>();
	
	private int expandFlag =-1;
	private int pageIndex = 1;
	private boolean isFirstLogin = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xunlei_login_main);
		
		app = (MyApp) getApplication();

		initView();

		loginLayout.setVisibility(View.VISIBLE);
		logoutLayout.setVisibility(View.INVISIBLE);
		
		

		if (XunLeiLiXianUtil.getCookie(getApplicationContext()) != null
				&& !XunLeiLiXianUtil.getCookie(getApplicationContext()).equals("")) {

			isFirstLogin = false;
			userNameEdit.setText(XunLeiLiXianUtil.getUsrname(getApplicationContext()));
			
			handler.sendEmptyMessage(START_LOGIN);
			MyApp.pool.execute(getUsrInfoRunnable);
			showDialog(DIALOG_WAITING);
		}
	}
	
	private void initView() {

		loginLayout = findViewById(R.id.rl_login);
		logoutLayout = findViewById(R.id.rl_logout);
		
		userNameEdit = (EditText) findViewById(R.id.et_username);
		passwdEdit = (EditText) findViewById(R.id.et_passwd);
		
		userNameLayout = findViewById(R.id.rl_username);
		passwdLayout = findViewById(R.id.rl_passwd);
		
		userNameEdit.setText("13918413043@163.com");
		passwdEdit.setText("6105586");
		
		loginBt = (Button) findViewById(R.id.bt_login);
		logoutBt = (Button) findViewById(R.id.bt_logout);
		
		nickNameTv = (TextView) findViewById(R.id.tv_lx_logout_nickname_content);
		userIdTv = (TextView) findViewById(R.id.tv_lx_logout_userid_content);
		vipRankTv = (TextView) findViewById(R.id.tv_lx_logout_rank_content);
		outDateTv = (TextView) findViewById(R.id.tv_lx_logout_outofdate_content);
		
		playerListView = (ExpandableListView) findViewById(R.id.lv_movie);
		playerListView.setGroupIndicator(null);
		
		addViewListener();
		
	}
	
	private void addViewListener() {
		
		userNameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus) {
					
					userNameLayout.setBackgroundResource(R.drawable.edit_focused);
				} else {
					
					userNameLayout.setBackgroundDrawable(null);
				}
			}
		});
		
		passwdEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus) {
					
					passwdLayout.setBackgroundResource(R.drawable.edit_focused);
				} else {
					
					passwdLayout.setBackgroundDrawable(null);
				}
			}
		});
		
		loginBt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(TextUtils.isEmpty(userNameEdit.getText().toString())) {
					
					Utils.showToast(XunLeiLXActivity.this, "请输入用户名");
					
					return;
				}
				
				if(TextUtils.isEmpty(passwdEdit.getText().toString())) {
					
					Utils.showToast(XunLeiLXActivity.this, "请输入密码");
					
					return;
				}
				
				MyApp.pool.execute(loginRunnable);
				showDialog(DIALOG_WAITING);
			}
		});
		
		logoutBt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				reset2Login();
			}
		});
		
		playerListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					final int groupPosition, long id) {
				// TODO Auto-generated method stub
				
				if(playerList.size() > groupPosition) {
					
					final XLLXFileInfo xllxFileInfo = playerList.get(groupPosition);
					if(xllxFileInfo != null) {
						
						if(!xllxFileInfo.isDir) {
							
							Log.i(TAG, "onItemClick--->xllxFileInfo:" + xllxFileInfo.toString());
							if(xllxFileInfo.file_name != null && !xllxFileInfo.file_name.equals("")) {
								
								//如果url不为空，直接传给播放器
								CurrentPlayDetailData currentPlayDetailData = new CurrentPlayDetailData();
								currentPlayDetailData.prod_url = xllxFileInfo.src_url;
								currentPlayDetailData.prod_type = -10;
								currentPlayDetailData.prod_name = xllxFileInfo.file_name;
								
								if(xllxFileInfo.duration != null && !xllxFileInfo.duration.equals("")
										&& !xllxFileInfo.duration.equals("0")) {
									
									long tempDuration = 0l;
									try {
										tempDuration = Long.valueOf(xllxFileInfo.duration);
									} catch (NumberFormatException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									if(tempDuration > 0) {
										
//										currentPlayDetailData.prod_time = tempDuration;
									}
								}
								currentPlayDetailData.obj = xllxFileInfo;
								app.setmCurrentPlayDetailData(currentPlayDetailData);
								
//								if(currentPlayDetailData.prod_src != null )
								startActivity(new Intent(XunLeiLXActivity.this, VideoPlayerJPActivity.class));
							}
						} else {
							
							if (xllxFileInfo.btFiles == null) {
								
					              new Thread(new Runnable()
					              {
					                public void run()
					                {
					                  if (XunLeiLiXianUtil.getSubFile(XunLeiLXActivity.this, xllxFileInfo) != null)
					                    handler.post(new Runnable()
					                    {
					                      public void run(){
					                    	  
					                        playExpandListAdapter.notifyDataSetChanged();
					                        playerListView.expandGroup(groupPosition);
					                        expandFlag = groupPosition;
					                      }
					                    });
					                }
					              }).start();
							}
						}
					}
				}
				
				if(expandFlag == groupPosition) {
					
					playerListView.collapseGroup(expandFlag);
					expandFlag = -1;
				} else {
					
					playerListView.collapseGroup(groupPosition);
					expandFlag= groupPosition;
				}
				return false;
			}
		});
		
		playerListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				
				if(playerList.size() > groupPosition && playerList.get(groupPosition) != null) {
					
					if(playerList.get(groupPosition).btFiles != null 
							&& playerList.get(groupPosition).btFiles.length > childPosition) {
						
						if(playerList.get(groupPosition).btFiles[childPosition] != null
								&& !playerList.get(groupPosition).btFiles[childPosition].isDir) {
							
							XLLXFileInfo xllxFileInfo = playerList.get(groupPosition).btFiles[childPosition];
							
							if(xllxFileInfo.file_name != null && !xllxFileInfo.file_name.equals("")) {
								
								//如果url不为空，直接传给播放器
								CurrentPlayDetailData currentPlayDetailData = new CurrentPlayDetailData();
								currentPlayDetailData.prod_url = xllxFileInfo.src_url;
								currentPlayDetailData.prod_type = -10;
								currentPlayDetailData.prod_name = xllxFileInfo.file_name;
								
								if(xllxFileInfo.duration != null && !xllxFileInfo.duration.equals("")
										&& !xllxFileInfo.duration.equals("0")) {
									
									long tempDuration = 0l;
									try {
										tempDuration = Long.valueOf(xllxFileInfo.duration);
									} catch (NumberFormatException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									if(tempDuration > 0) {
										
//										currentPlayDetailData.prod_time = tempDuration;
									}
								}
								currentPlayDetailData.obj = xllxFileInfo;
								app.setmCurrentPlayDetailData(currentPlayDetailData);
								
//								if(currentPlayDetailData.prod_src != null )
								startActivity(new Intent(XunLeiLXActivity.this, VideoPlayerJPActivity.class));
							}
						}
					}
				}
				
				return false;
			}
		});
		
		playerListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
//		playerListView.setOn
	}
	
	private void setLogin(boolean isLogin) {
		
		if(!isLogin) {
			
			loginLayout.setVisibility(View.VISIBLE);
			logoutLayout.setVisibility(View.INVISIBLE);
			
			passwdEdit.setText("");
		} else {
			
			logoutLayout.setVisibility(View.VISIBLE);
			loginLayout.setVisibility(View.INVISIBLE);
		}
	}
	
	private void reset2Login() {
		
		XunLeiLiXianUtil.Logout(getApplicationContext());
		setLogin(false);
		
		playerList.clear();
		playExpandListAdapter.notifyDataSetChanged();
		
		pageIndex = 1;
		isFirstLogin = true;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		
		switch (id) {
		case DIALOG_WAITING:
			WaitingDialog dlg = new WaitingDialog(this);
			dlg.show();
			dlg.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			dlg.setDialogWindowStyle();
			return dlg;

		default:
			break;
		}
		return super.onCreateDialog(id);
	}
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
//			super.handleMessage(msg);
			
			int what = msg.what;
			
			switch (what) {
			case LOGIN_SUCESS://登录成功
				
				Utils.showToast(XunLeiLXActivity.this, "登陆成功");
				MyApp.pool.execute(getUsrInfoRunnable);
				break;
			case REFESH_USERINFO://刷新用户信息成功
				setLogin(true);//跳转到用户信息界面
				
				isFirstLogin = false;
				
				XLLXUserInfo xllxUserInfo = (XLLXUserInfo) msg.obj;
				if(xllxUserInfo != null) {
					
					nickNameTv.setText(xllxUserInfo.nickname);
					userIdTv.setText(xllxUserInfo.usrname);
					vipRankTv.setText("VIP" + xllxUserInfo.level + "");
					outDateTv.setText(xllxUserInfo.expiredate.replaceAll("-", "."));
				}
				
				MyApp.pool.execute(getVideoList);
				removeDialog(DIALOG_WAITING);
				break;
			case REFRESH_LIST://刷新用户信息成功
//				removeDialog(DIALOG_WAITING);
				
				ArrayList<XLLXFileInfo> list =  (ArrayList<XLLXFileInfo>) msg.obj;

				if(list != null && list.size() >0) {
					playerList = list;
					playExpandListAdapter = new PlayExpandListAdapter(XunLeiLXActivity.this,playerList );
					playerListView.setAdapter(playExpandListAdapter);
				}
				break;
			case START_LOGIN://直接进入用户界面
				MyApp.pool.execute(getUsrInfoRunnable);
				break;
			case LOGIN_ERROR:
				
				int loginErrorFlag = msg.arg1;
				switch (loginErrorFlag) {
				case 1:
					Utils.showToast(XunLeiLXActivity.this, "获取验证码失败,稍后重试");
					break;
				case 2:
					Utils.showToast(XunLeiLXActivity.this, "密码错误");
					break;
				case 4:
				case 5:
					Utils.showToast(XunLeiLXActivity.this, "账户不存在");
					break;
				case 6:
					Utils.showToast(XunLeiLXActivity.this, "账户被锁定");
					break;
				case 10:
					Utils.showToast(XunLeiLXActivity.this, "获取用户信息失败,请重试或者重新登录");
					break;
				case 11:
					Utils.showToast(XunLeiLXActivity.this, "获取列表失败");
					break;
				default:
					Utils.showToast(XunLeiLXActivity.this, "网络超时，稍后重试");
					break;
				}
				
				//清空数据重新获取数据
				reset2Login();
				removeDialog(DIALOG_WAITING);
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
			
			int loginFlag = XunLeiLiXianUtil.Login(XunLeiLXActivity.this,
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
				
				xllxUserInfo = XunLeiLiXianUtil.getUser(XunLeiLXActivity.this,
						XunLeiLiXianUtil.getCookieHeader(XunLeiLXActivity.this));
				
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
			 ArrayList<XLLXFileInfo> list = XunLeiLiXianUtil.
					 getVideoList(XunLeiLXActivity.this, 30, pageIndex);
			
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
