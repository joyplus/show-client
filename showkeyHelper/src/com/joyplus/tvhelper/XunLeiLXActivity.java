package com.joyplus.tvhelper;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
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
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.MD5Util;
import com.joyplus.tvhelper.utils.Utils;
import com.joyplus.tvhelper.utils.XunLeiLiXianUtil;
import com.umeng.analytics.MobclickAgent;

public class XunLeiLXActivity extends Activity {

	private static final String TAG = "XunLeiLXActivity";

	private static final int DIALOG_WAITING = 0;

	private static final int LOGIN_ERROR = 2;
	private static final int LOGIN_SUCESS = 1;
	private static final int REFESH_USERINFO = 3;
	private static final int REFRESH_LIST = 4;
	private static final int START_LOGIN = 5;
	private static final int VERIFY_CODE_SUCCESS = 6;
	private static final int VERIFY_CODE_FAIL = 7;

	private View loginLayout, logoutLayout,verifyEtLayout,verifyBtLayout,verifyLayout;
	private View userNameLayout, passwdLayout;
	private EditText userNameEdit, passwdEdit,verifyEdit;
	private Button loginBt, logoutBt;
	private TextView nickNameTv, userIdTv, vipRankTv, outDateTv;
	private ExpandableListView playerListView;
	private Button returnBt,refreshBt,verifyBt;

	private PlayExpandListAdapter playerExpandListAdapter;

	private MyApp app;

	private ArrayList<XLLXFileInfo> playerList = new ArrayList<XLLXFileInfo>();

	private int expandFlag = -1;
	private int pageIndex = 1;
	private boolean isCanCache = false;
	private boolean isFirstLogin = true;
	private boolean isTempRefresh = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xunlei_login_main);

		app = (MyApp) getApplication();

		initView();

		loginLayout.setVisibility(View.VISIBLE);
		logoutLayout.setVisibility(View.INVISIBLE);

		if (XunLeiLiXianUtil.getCookie(getApplicationContext()) != null
				&& !XunLeiLiXianUtil.getCookie(getApplicationContext()).equals(
						"")) {

			isFirstLogin = false;
			// userNameEdit.setText(XunLeiLiXianUtil.getUsrname(getApplicationContext()));

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

		loginBt = (Button) findViewById(R.id.bt_login);
		logoutBt = (Button) findViewById(R.id.bt_logout);

		nickNameTv = (TextView) findViewById(R.id.tv_lx_logout_nickname_content);
		userIdTv = (TextView) findViewById(R.id.tv_lx_logout_userid_content);
		vipRankTv = (TextView) findViewById(R.id.tv_lx_logout_rank_content);
		outDateTv = (TextView) findViewById(R.id.tv_lx_logout_outofdate_content);

		returnBt = (Button) findViewById(R.id.bt_back);
		refreshBt = (Button) findViewById(R.id.bt_refresh_list);
		
		verifyEdit = (EditText) findViewById(R.id.et_verify_code);
		verifyBt = (Button) findViewById(R.id.bt_verify_code);
		
		verifyEtLayout = findViewById(R.id.ll_et_verify_code);
		verifyBtLayout = findViewById(R.id.ll_bt_verify_code);
		
		verifyLayout = findViewById(R.id.ll_verify_code);

		playerListView = (ExpandableListView) findViewById(R.id.lv_movie);
		playerListView.setGroupIndicator(null);

		if (XunLeiLiXianUtil.getLoginUserName(getApplicationContext()) != null
				&& !XunLeiLiXianUtil.getLoginUserName(getApplicationContext())
						.equals("")) {

			userNameEdit.setText(XunLeiLiXianUtil
					.getLoginUserName(getApplicationContext()));
		}

		if (XunLeiLiXianUtil.getLoginUserPasswd(getApplicationContext()) != null
				&& !XunLeiLiXianUtil
						.getLoginUserPasswd(getApplicationContext()).equals("")) {

			passwdEdit.setText(XunLeiLiXianUtil
					.getLoginUserPasswd(getApplicationContext()));
		}

		addViewListener();

	}

	private void addViewListener() {
		
		verifyBt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(userNameEdit.getText().toString() != null 
						&& !"".equals(userNameEdit.getText().toString())){
					MyApp.pool.execute(getVerifyBitmap);
				}
			}
		});
		
		refreshBt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 pageIndex = 1;
				 playerList.clear();

				 if (playerExpandListAdapter != null) {

					 playerExpandListAdapter.notifyDataSetChanged();
				 }
				 isTempRefresh = true;
				 showDialog(DIALOG_WAITING);
				 MyApp.pool.execute(getVideoList);
			}
		});

		returnBt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				finish();
			}
		});

		userNameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {

					userNameLayout
							.setBackgroundResource(R.drawable.edit_focused);
				} else {

					userNameLayout.setBackgroundDrawable(null);
				}
			}
		});

		passwdEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {

					passwdLayout.setBackgroundResource(R.drawable.edit_focused);
				} else {

					passwdLayout.setBackgroundDrawable(null);
				}
			}
		});
		
		verifyEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {

					verifyEtLayout
							.setBackgroundResource(R.drawable.edit_focused);
				} else {

					verifyEtLayout.setBackgroundDrawable(null);
				}
			}
		});
		
		verifyBt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					
					verifyBtLayout
					.setBackgroundResource(R.drawable.edit_focused);
				} else {
					
					verifyBtLayout.setBackgroundDrawable(null);
				}
			}
		});

		loginBt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (TextUtils.isEmpty(userNameEdit.getText().toString())) {

					Utils.showToast(XunLeiLXActivity.this, "请输入用户名");

					return;
				}

				if (TextUtils.isEmpty(passwdEdit.getText().toString())) {

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

				reset2Login(-1);
				verifyLayout.setVisibility(View.INVISIBLE);
			}
		});

		playerListView
				.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

					@Override
					public boolean onGroupClick(ExpandableListView parent,
							View v, final int groupPosition, long id) {
						// TODO Auto-generated method stub

						if (playerList.size() > groupPosition) {

							final XLLXFileInfo xllxFileInfo = playerList
									.get(groupPosition);
							if (xllxFileInfo != null) {

								if (!xllxFileInfo.isDir) {

									Log.i(TAG, "onItemClick--->xllxFileInfo:"
											+ xllxFileInfo.toString());
									if (xllxFileInfo.file_name != null
											&& !xllxFileInfo.file_name
													.equals("")) {

										// 如果url不为空，直接传给播放器
										CurrentPlayDetailData currentPlayDetailData = new CurrentPlayDetailData();
										currentPlayDetailData.prod_url = xllxFileInfo.src_url;
										currentPlayDetailData.prod_type = -10;
										currentPlayDetailData.prod_name = xllxFileInfo.file_name;

										if (xllxFileInfo.duration != null
												&& !xllxFileInfo.duration
														.equals("")
												&& !xllxFileInfo.duration
														.equals("0")) {

											long tempDuration = 0l;
											try {
												tempDuration = Long
														.valueOf(xllxFileInfo.duration);
											} catch (NumberFormatException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}

											if (tempDuration > 0) {

												// currentPlayDetailData.prod_time
												// = tempDuration;
											}
										}
										currentPlayDetailData.obj = xllxFileInfo;
										app.setmCurrentPlayDetailData(currentPlayDetailData);

										// if(currentPlayDetailData.prod_src !=
										// null )
										startActivity(Utils.getIntent(XunLeiLXActivity.this));
									}
								} else {

									if (xllxFileInfo.btFiles == null) {

										new Thread(new Runnable() {

											public void run() {
												if (XunLeiLiXianUtil
														.getSubFile(
																XunLeiLXActivity.this,
																xllxFileInfo) != null)

													handler.post(new Runnable() {
														public void run() {

															Log.i(TAG,
																	"handler.post(new Runnable()--->groupPosition:"
																			+ groupPosition);
															playerExpandListAdapter
																	.notifyDataSetChanged();
															playerListView
																	.expandGroup(groupPosition);
															expandFlag = groupPosition;
														}
													});
											}
										}).start();
									} else {

										Log.i(TAG,
												"!= null handler.post(new Runnable()--->groupPosition:"
														+ groupPosition
														+ " expandFlag--->"
														+ expandFlag
														+ "  btFiles--->"
														+ xllxFileInfo.btFiles.length);

										if (expandFlag == groupPosition) {

											playerListView
													.collapseGroup(expandFlag);
											expandFlag = -1;
										} else {
											playerListView
													.expandGroup(groupPosition);
											expandFlag = groupPosition;
										}
									}
								}
							}
						}
						return true;
					}
				});

		playerListView
				.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

					@Override
					public void onGroupExpand(int groupPosition) {
						// TODO Auto-generated method stub

						Log.i(TAG, "onGroupExpand--->" + groupPosition);
						XLLXFileInfo xllxFileInfo = playerList
								.get(groupPosition);
						if (xllxFileInfo != null
								&& xllxFileInfo.btFiles != null)
							Log.i(TAG, "  btFiles--->"
									+ xllxFileInfo.btFiles.length);
						for (int i = 0; i < playerList.size(); i++) {

							if (i != groupPosition) {

								playerListView.collapseGroup(i);
							}
						}

						expandFlag = groupPosition;
					}
				});

		playerListView
				.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

					@Override
					public void onGroupCollapse(int groupPosition) {
						// TODO Auto-generated method stub

						if (playerListView.isGroupExpanded(groupPosition)) {

							playerListView.collapseGroup(groupPosition);
						}
					}
				});

		// playerListView.setOnFocusChangeListener(new
		// View.OnFocusChangeListener() {
		//
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// // TODO Auto-generated method stub
		//
		// if(!hasFocus){
		//
		// // playerListView.collapseGroup(expandFlag);
		// for (int i = 0; i < playerList.size(); i++) {
		//
		// playerListView.collapseGroup(i);
		// }
		// expandFlag = -1;
		// }
		// }
		// });

		logoutBt.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_LEFT:

					if (expandFlag != -1)
						Log.i(TAG,
								"KEYCODE_DPAD_LEFT"
										+ playerListView
												.isGroupExpanded(expandFlag));

					for (int i = 0; i < playerList.size(); i++) {

						playerListView.collapseGroup(i);
					}
					expandFlag = -1;
					break;

				default:
					break;
				}
				return false;
			}
		});

		playerListView
				.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

					@Override
					public boolean onChildClick(ExpandableListView parent,
							View v, int groupPosition, int childPosition,
							long id) {
						// TODO Auto-generated method stub

						if (playerList.size() > groupPosition
								&& playerList.get(groupPosition) != null) {

							if (playerList.get(groupPosition).btFiles != null
									&& playerList.get(groupPosition).btFiles.length > childPosition) {

								if (playerList.get(groupPosition).btFiles[childPosition] != null
										&& !playerList.get(groupPosition).btFiles[childPosition].isDir) {
									Log.i(TAG,
											"setOnChildClickListener groupPosition:"
													+ groupPosition
													+ " childPosition:"
													+ childPosition
													+ " length:"
													+ playerList
															.get(groupPosition).btFiles.length
													+ " id:" + id);
									XLLXFileInfo xllxFileInfo = playerList
											.get(groupPosition).btFiles[childPosition];

									if (xllxFileInfo.file_name != null
											&& !xllxFileInfo.file_name
													.equals("")) {

										// 如果url不为空，直接传给播放器
										CurrentPlayDetailData currentPlayDetailData = new CurrentPlayDetailData();
										currentPlayDetailData.prod_url = xllxFileInfo.src_url;
										currentPlayDetailData.prod_type = -10;
										currentPlayDetailData.prod_name = xllxFileInfo.file_name;

										if (xllxFileInfo.duration != null
												&& !xllxFileInfo.duration
														.equals("")
												&& !xllxFileInfo.duration
														.equals("0")) {

											long tempDuration = 0l;
											try {
												tempDuration = Long
														.valueOf(xllxFileInfo.duration);
											} catch (NumberFormatException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}

											if (tempDuration > 0) {

												// currentPlayDetailData.prod_time
												// = tempDuration;
											}
										}
										currentPlayDetailData.obj = xllxFileInfo;
										app.setmCurrentPlayDetailData(currentPlayDetailData);

										// if(currentPlayDetailData.prod_src !=
										// null )
										startActivity(Utils.getIntent(XunLeiLXActivity.this));
									}
								}
							}
						}

						return false;
					}
				});

		playerListView
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						Log.i(TAG, "onItemSelected--->" + position + " id:"
								+ id);

						if (isCanCache) {

							int lastVisiblePosition = playerListView
									.getLastVisiblePosition();
							int totalSize = playerList.size();
							if (expandFlag != -1
									&& playerList.get(expandFlag) != null
									&& playerList.get(expandFlag).btFiles != null
									&& playerList.get(expandFlag).btFiles.length > 0) {

								totalSize = playerList.size()
										+ playerList.get(expandFlag).btFiles.length;
							}

							if (totalSize - lastVisiblePosition < XunLeiLiXianUtil.CACHE_NUM) {

								pageIndex++;

								new Thread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										final ArrayList<XLLXFileInfo> list = XunLeiLiXianUtil
												.getVideoList(
														getApplicationContext(),
														XunLeiLiXianUtil.CACHE_NUM,
														pageIndex);
										handler.post(new Runnable() {

											@Override
											public void run() {
												// TODO Auto-generated method
												// stub
												refreshListView(list);
											}
										});
									}
								}).start();
							}
						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub

					}
				});

		playerListView.setOnScrollListener(new AbsListView.OnScrollListener() {

			int lastVisiblePosition;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					Log.i(TAG, "playerListView--->SCROLL_STATE_IDLE"
							+ " tempfirstVisibleItem--->" + lastVisiblePosition);

					// 缓存
					if (playerExpandListAdapter != null) {

						if (playerExpandListAdapter.getFiles() != null) {

							if (isCanCache) {

								int totalSize = playerList.size();
								if (expandFlag != -1
										&& playerList.get(expandFlag) != null
										&& playerList.get(expandFlag).btFiles != null
										&& playerList.get(expandFlag).btFiles.length > 0) {

									totalSize = playerList.size()
											+ playerList.get(expandFlag).btFiles.length;
								}

								if (totalSize - lastVisiblePosition < XunLeiLiXianUtil.CACHE_NUM) {

									pageIndex++;

									new Thread(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											final ArrayList<XLLXFileInfo> list = XunLeiLiXianUtil
													.getVideoList(
															getApplicationContext(),
															XunLeiLiXianUtil.CACHE_NUM,
															pageIndex);
											handler.post(new Runnable() {

												@Override
												public void run() {
													// TODO Auto-generated
													// method stub
													refreshListView(list);
												}
											});
										}
									}).start();

								}
							}
						}
					}

					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					Log.i(TAG, "playGv--->SCROLL_STATE_TOUCH_SCROLL");

					break;
				case OnScrollListener.SCROLL_STATE_FLING:
					Log.i(TAG, "playGv--->SCROLL_STATE_FLING");

					// isDragGridView = false;
					break;
				default:
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				lastVisiblePosition = firstVisibleItem + visibleItemCount;
			}
		});
	}

	private void refreshListView(ArrayList<XLLXFileInfo> list) {

		if (list != null && list.size() > 0) {

			if (list.size() >= XunLeiLiXianUtil.CACHE_NUM) {

				isCanCache = true;
			} else {

				isCanCache = false;
			}

			playerList.addAll(list);
			playerExpandListAdapter.setFiles(playerList);
			playerExpandListAdapter.notifyDataSetChanged();

		}
	}

	private void setLogin(boolean isLogin) {

		if (!isLogin) {

			loginLayout.setVisibility(View.VISIBLE);
			logoutLayout.setVisibility(View.INVISIBLE);

		} else {

			logoutLayout.setVisibility(View.VISIBLE);
			loginLayout.setVisibility(View.INVISIBLE);
		}
	}

	private void reset2Login(int loginErrorFlag) {

		XunLeiLiXianUtil.Logout(getApplicationContext());
		setLogin(false);

		playerList.clear();

		if (playerExpandListAdapter != null) {

			playerExpandListAdapter.notifyDataSetChanged();
		}

		pageIndex = 1;
		isFirstLogin = true;
		refreshBt.setVisibility(View.INVISIBLE);

		if(loginErrorFlag == 10 || loginErrorFlag == 11) return;

		passwdEdit.setText("");
		XunLeiLiXianUtil.saveLoginUserPasswd(getApplicationContext(), "");
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

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);

			int what = msg.what;

			switch (what) {
			case LOGIN_SUCESS:// 登录成功

				Utils.showToast(XunLeiLXActivity.this, "登陆成功");
				MyApp.pool.execute(getUsrInfoRunnable);

				if (XunLeiLiXianUtil.getLoginUserName(getApplicationContext()) != null
						&& !XunLeiLiXianUtil.getLoginUserName(
								getApplicationContext()).equals("")) {

					if (!XunLeiLiXianUtil.getLoginUserName(
							getApplicationContext()).equals(
							userNameEdit.getText().toString())) {
						XunLeiLiXianUtil.saveLoginUserName(
								getApplicationContext(), userNameEdit.getText()
										.toString());
						XunLeiLiXianUtil.saveLoginUserPasswd(
								getApplicationContext(), MD5Util
										.getMD5String(passwdEdit.getText()
												.toString()));
					}
				} else {

					XunLeiLiXianUtil.saveLoginUserName(getApplicationContext(),
							userNameEdit.getText().toString());
					XunLeiLiXianUtil.saveLoginUserPasswd(
							getApplicationContext(), MD5Util
									.getMD5String(passwdEdit.getText()
											.toString()));
				}

				break;
			case REFESH_USERINFO:// 刷新用户信息成功
				setLogin(true);// 跳转到用户信息界面

				isFirstLogin = false;
				
				refreshBt.setVisibility(View.VISIBLE);

				XLLXUserInfo xllxUserInfo = (XLLXUserInfo) msg.obj;
				if (xllxUserInfo != null) {

					nickNameTv.setText(xllxUserInfo.nickname);
					userIdTv.setText(xllxUserInfo.usrname);
					vipRankTv.setText("VIP" + xllxUserInfo.level + "");
					outDateTv.setText(xllxUserInfo.expiredate.replaceAll("-",
							"."));
				}

				MyApp.pool.execute(getVideoList);
				removeDialog(DIALOG_WAITING);
				break;
			case REFRESH_LIST:// 刷新用户信息成功
				// removeDialog(DIALOG_WAITING);
				ArrayList<XLLXFileInfo> list = (ArrayList<XLLXFileInfo>) msg.obj;

				if (list != null && list.size() > 0) {
					playerList = list;
					playerExpandListAdapter = new PlayExpandListAdapter(
							XunLeiLXActivity.this, playerList);
					playerListView.setAdapter(playerExpandListAdapter);
				}
				
				if(isTempRefresh){
					
					removeDialog(DIALOG_WAITING);
					isTempRefresh = false;
				}
				break;
			case START_LOGIN:// 直接进入用户界面
				MyApp.pool.execute(getUsrInfoRunnable);
				break;
			case LOGIN_ERROR:

				int loginErrorFlag = msg.arg1;
				switch (loginErrorFlag) {
				case 1:
					Utils.showToast(XunLeiLXActivity.this, "自动获取验证码失败");
					verifyLayout.setVisibility(View.VISIBLE);
					if(userNameEdit.getText().toString() != null 
							&& !"".equals(userNameEdit.getText().toString())){
						MyApp.pool.execute(getVerifyBitmap);
					}
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

				// 清空数据重新获取数据
				reset2Login(msg.arg1);
				removeDialog(DIALOG_WAITING);
				break;
			case VERIFY_CODE_SUCCESS:
				Bitmap bitmap = (Bitmap) msg.obj;
				if(bitmap != null){
					verifyBt.setBackgroundDrawable(new BitmapDrawable(getResources(),bitmap));
				}else{
					
					verifyBt.setBackgroundDrawable(null);
					Utils.showToast(XunLeiLXActivity.this, "获取验证码图片失败,检查网络是否连接");
				}
				
				break;
			case VERIFY_CODE_FAIL:
				Utils.showToast(XunLeiLXActivity.this, "获取验证码图片失败,检查网络是否连接");
			default:
				break;
			}
		}

	};

	private Runnable loginRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			int loginFlag = -10;

			if (passwdEdit.getText().toString() != null
					&& passwdEdit.getText().toString()
							.equals(XunLeiLiXianUtil
									.getLoginUserPasswd(getApplicationContext()))) {

				loginFlag = XunLeiLiXianUtil.Login(XunLeiLXActivity.this,
						userNameEdit.getText().toString(), passwdEdit.getText()
								.toString(), verifyEdit.getText().toString());
			} else {

				loginFlag = XunLeiLiXianUtil.Login(XunLeiLXActivity.this,
						userNameEdit.getText().toString(), passwdEdit.getText()
								.toString(), verifyEdit.getText().toString());
			}

			if (loginFlag == 0) {

				handler.sendEmptyMessage(LOGIN_SUCESS);
			} else {

				Message message = handler.obtainMessage(LOGIN_ERROR, loginFlag,
						-1);
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
			if (!isFirstLogin) {

				xllxUserInfo = XunLeiLiXianUtil
						.getUserInfoFromLocal(getApplicationContext());
				if (xllxUserInfo != null) {

					Message message = handler.obtainMessage(REFESH_USERINFO,
							xllxUserInfo);
					handler.sendMessage(message);
				} else {

					Message message = handler
							.obtainMessage(LOGIN_ERROR, 10, -1);
					handler.sendMessage(message);
				}
			} else {

				xllxUserInfo = XunLeiLiXianUtil
						.getUser(XunLeiLXActivity.this, XunLeiLiXianUtil
								.getCookieHeader(XunLeiLXActivity.this));

				if (xllxUserInfo != null) {

					Log.i(TAG, "getUsrInfoRunnable--->xllxUserInfo:"
							+ xllxUserInfo.toString());
					Message message = handler.obtainMessage(REFESH_USERINFO,
							xllxUserInfo);
					handler.sendMessage(message);
				} else {

					Message message = handler
							.obtainMessage(LOGIN_ERROR, 10, -1);
					handler.sendMessage(message);
				}

			}
		}
	};

	private Runnable getVideoList = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.d(TAG, "getVideoList--->");
			ArrayList<XLLXFileInfo> list = XunLeiLiXianUtil.getVideoList(
					XunLeiLXActivity.this, XunLeiLiXianUtil.CACHE_NUM,
					pageIndex);

			if (list != null && list.size() > 0) {

				Message message = handler.obtainMessage(REFRESH_LIST, list);
				handler.sendMessage(message);

				if (list.size() >= XunLeiLiXianUtil.CACHE_NUM) {

					isCanCache = true;
				} else {

					isCanCache = false;
				}
			} else {

				Message message = handler.obtainMessage(LOGIN_ERROR, 11, -1);
				handler.sendMessage(message);
			}
		}
	};
	
	private Runnable getVerifyBitmap = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(userNameEdit.getText().toString() != null 
					&& !"".equals(userNameEdit.getText().toString())){
				Bitmap bitmap = XunLeiLiXianUtil.getVerifyCodeBitmap(getApplicationContext(),
						userNameEdit.getText().toString());
				if(bitmap != null){
					
					Message message = handler.obtainMessage(VERIFY_CODE_SUCCESS, bitmap);
					handler.sendMessage(message);
					return;
				}
			}
			Message message = handler.obtainMessage(VERIFY_CODE_FAIL);
			handler.sendMessage(message);
		}
	};

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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		XunLeiLiXianUtil.saveVerifyCookies(getApplicationContext(), "");
		super.onDestroy();
	}

}
