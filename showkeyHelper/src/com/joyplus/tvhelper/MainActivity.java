package com.joyplus.tvhelper;

import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
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

import com.joyplus.tvhelper.faye.FayeService;
import com.joyplus.tvhelper.https.HttpUtils;
import com.joyplus.tvhelper.ui.MyScrollLayout;
import com.joyplus.tvhelper.ui.MyScrollLayout.OnViewChangeListener;
import com.joyplus.tvhelper.ui.NotificationView;
import com.joyplus.tvhelper.utils.Constant;
import com.joyplus.tvhelper.utils.Global;
import com.joyplus.tvhelper.utils.HttpTools;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.PackageUtils;
import com.joyplus.tvhelper.utils.PreferencesUtils;
import com.joyplus.tvhelper.utils.Utils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class MainActivity extends Activity implements OnFocusChangeListener, OnHoverListener, OnKeyListener, OnClickListener {

	private static final String TAG = "MainActivity";
	
	public static boolean isConnect = false;
	
	private static final int MESSAGE_GETPINCODE_SUCCESS = 0;
	private static final int MESSAGE_GETPINCODE_FAILE = MESSAGE_GETPINCODE_SUCCESS+1;
	
	private ImageView image_1_1, image_1_3, image_1_2, image_1_4, image_1_5;
	
	private LinearLayout layout_1_1, layout_1_3, layout_1_2, layout_1_4, layout_1_5;
	
	private RelativeLayout layout_3_4;
	
	private ImageView image_3_1, image_3_2, image_3_3, image_3_4, image_3_5;
	
	private LinearLayout layout_3_1, layout_3_3, layout_3_5, layout_3_2;
	
	private TextView title_text_1, title_text_2;
	
	private View selectedLayout;
	
	private View layout_page_3;
	private LinearLayout layout_title;
	
	private TextView web_url_textview;
	
//	private FrameLayout relativeLayout;
	
	private MyScrollLayout layout;
	
	private TextView pincodeText;
	
	private String umeng_channel;
	
	private NotificationView connectStatueText;
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(Global.ACTION_CONNECT_SUCCESS.equals(action)){
				connectStatueText.setText("已连接");
				isConnect = true;
				Intent intent_local = new Intent(Global.ACTION_CONNECT_SUCCESS_MAIN);
				sendBroadcast(intent_local);
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						connectStatueText.setText("");
					}
				}, 2000);
			}else if(Global.ACTION_DISCONNECT_SERVER.equals(action)){
				if(!"正在连接服务器···".equals(connectStatueText.getText())){
					connectStatueText.setText("正在连接服务器···");
					mHandler.removeCallbacksAndMessages(null);
				}
				Intent intent_local = new Intent(Global.ACTION_DISCONNECT_SERVER_MAIN);
				sendBroadcast(intent_local);
				isConnect = false;
			}if(Global.ACTION_PINCODE_REFRESH.equals(action)){
				image_1_1.setImageBitmap(null);
				layout_1_1.setDrawingCacheEnabled(false);
				displayPincode();
				layout_1_1.setDrawingCacheEnabled(true);
				image_1_1.setImageBitmap(layout_1_1.getDrawingCache());
			}
		}
		
	};
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_GETPINCODE_SUCCESS:
				displayPincode();
				reSetImages();
				startService(new Intent(MainActivity.this, FayeService.class));
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String online_base_url = MobclickAgent.getConfigParams(MainActivity.this, "URL"+ umeng_channel);
						Log.d(TAG, "online_base_url----->" + online_base_url);
						if(online_base_url!=null&&online_base_url.length()>0){
							web_url_textview.setText(online_base_url);
							PreferencesUtils.setWebUrl(MainActivity.this, online_base_url);
						}else{
							web_url_textview.setText("tt.showkey.tv");
						}
					}
				}, 1000);
				break;
			case MESSAGE_GETPINCODE_FAILE:
//				Toast.makeText(MainActivity.this, "请求pinCode失败", 100).show();
				Utils.showToast(MainActivity.this,"请求pinCode失败" );
				reSetImages();
				break;
			default:
				break;
			}
			
		};
	};
	
	private void reSetImages(){
		int width = layout_1_1.getWidth();
		int height = (layout_1_1.getHeight()-Utils.getStandardValue(MainActivity.this,13))/2;
		image_1_1.layout(0, 0, width+ Utils.getStandardValue(MainActivity.this,40), height*2+Utils.getStandardValue(MainActivity.this,53));
		image_1_2.layout(width+Utils.getStandardValue(MainActivity.this,13), 0, width*2+Utils.getStandardValue(MainActivity.this,53), height*2+Utils.getStandardValue(MainActivity.this,53));
//		image_1_2.layout(width+Utils.getStandardValue(MainActivity.this,13), 0, width*2+Utils.getStandardValue(MainActivity.this,53), height+Utils.getStandardValue(MainActivity.this,40));
//		image_1_3.layout(width+Utils.getStandardValue(MainActivity.this,13), height+Utils.getStandardValue(MainActivity.this,13), width*2+Utils.getStandardValue(MainActivity.this,53), height*2+Utils.getStandardValue(MainActivity.this,53));
		image_1_4.layout(width*2+Utils.getStandardValue(MainActivity.this,26), 0, width*3+Utils.getStandardValue(MainActivity.this,66), height+Utils.getStandardValue(MainActivity.this,40));
		if(Constant.isSimple){
			image_1_5.layout(width*2+Utils.getStandardValue(MainActivity.this,26), 0, width*3+Utils.getStandardValue(MainActivity.this,66), height*2+Utils.getStandardValue(MainActivity.this,53));
		}else{
			image_1_5.layout(width*2+Utils.getStandardValue(MainActivity.this,26), height+Utils.getStandardValue(MainActivity.this,13), width*3+Utils.getStandardValue(MainActivity.this,66), height*2+Utils.getStandardValue(MainActivity.this,53));
		}
		image_3_1.layout(0, 0, width+Utils.getStandardValue(MainActivity.this,40), height*2+Utils.getStandardValue(MainActivity.this,53));
		image_3_2.layout(width+Utils.getStandardValue(MainActivity.this,13), 0, width*2+Utils.getStandardValue(MainActivity.this,53), height+Utils.getStandardValue(MainActivity.this,40));
		image_3_3.layout(width+Utils.getStandardValue(MainActivity.this,13), height+Utils.getStandardValue(MainActivity.this,13), width*2+Utils.getStandardValue(MainActivity.this,53), height*2+Utils.getStandardValue(MainActivity.this,53));
		image_3_4.layout(width*2+Utils.getStandardValue(MainActivity.this,26), 0, width*3+Utils.getStandardValue(MainActivity.this,66), height+Utils.getStandardValue(MainActivity.this,40));
		image_3_5.layout(width*2+Utils.getStandardValue(MainActivity.this,26), height+Utils.getStandardValue(MainActivity.this,13), width*3+Utils.getStandardValue(MainActivity.this,66), height*2+Utils.getStandardValue(MainActivity.this,53));
		image_1_1.setImageBitmap(layout_1_1.getDrawingCache());
		layout_1_1.requestFocus();
	}
	
	private MyApp app;
	private Map<String, String> headers;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(Constant.isSimple){
			setContentView(R.layout.activity_main_simple);
		}else{
			setContentView(R.layout.activity_main);
		}
		
		MobclickAgent.onError(this);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.update(this);
		MobclickAgent.setDebugMode(false);
		;
		MobclickAgent.updateOnlineConfig(this);
		
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
	        @Override
	        public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
	            switch (updateStatus) {
	            case 0: // has update
	            case 2:
	            	Log.d(TAG, "hasUpdate---->" + updateInfo.hasUpdate);
	            	Log.d(TAG, "path ------>" + updateInfo.path);
	            	Log.d(TAG, "log---->" + updateInfo.updateLog);
	            	Log.d(TAG, "version---->" + updateInfo.version);
	            	final File f = new File(getCacheDir(), DownLoadUpdateApkThread.NAME_APK_DOWNLOADED);
	            	if(f.exists()){
	            		PackageInfo info = PackageUtils.getAppPackageInfo(MainActivity.this, f.getAbsolutePath());
	            		if(info != null&&info.versionName!=null&&info.versionName.equals(updateInfo.version)){
	            			//Toast.makeText(MainActivity.this, "可以更新啦", Toast.LENGTH_SHORT).show();
	            			AlertDialog.Builder builder = new Builder(MainActivity.this);
	            			  builder.setMessage(updateInfo.updateLog);

	            			  builder.setTitle("发现新版本:" + updateInfo.version);

	            			  builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

		            			   @Override
		            			   public void onClick(DialogInterface dialog, int which) {
		            				   dialog.dismiss();
		            				   try {
		            						Uri packageURI =Uri.parse("file://"+f.getAbsolutePath());
		            						Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE, packageURI);
		            						startActivity(intent);
		            					} catch (Exception e) {
		            						// TODO: handle exception
		            						e.printStackTrace();
		            					}
		            			   }
	            			  });

	            			  builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

		            			   @Override
		            			   public void onClick(DialogInterface dialog, int which) {
		            			    dialog.dismiss();
		            			   }
	            			  });

	            			  builder.create().show();
	            		}else{
	            			new Thread(new DownLoadUpdateApkThread(MainActivity.this, URLDecoder.decode(updateInfo.path))).start();
	            		}
	            	}else{
	            		new Thread(new DownLoadUpdateApkThread(MainActivity.this, URLDecoder.decode(updateInfo.path))).start();
	            	}
//	                UmengUpdateAgent.showUpdateDialog(MainActivity.this, updateInfo);
	                break;
	            case 1: // has no update
	                //Toast.makeText(MainActivity.this, "没有更新", Toast.LENGTH_SHORT).show();
	                break;
//	            case 2: // none wifi
	                //Toast.makeText(MainActivity.this, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
//	                break;
	            case 3: // time out
	                //Toast.makeText(MainActivity.this, "超时", Toast.LENGTH_SHORT).show();
	                break;
	            }
	        }
	});
		
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
		
		ApplicationInfo info = null;
		try {
			info = this.getPackageManager().getApplicationInfo(getPackageName(),
			        PackageManager.GET_META_DATA);
			umeng_channel = info.metaData.getString("UMENG_CHANNEL");
			Log.d(TAG, "key--->" + "URL"+ umeng_channel);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(umeng_channel==null||umeng_channel.length()==0){
			umeng_channel = "j001";
		}
		
		headers = new HashMap<String, String>();
		headers.put("app_key", Constant.APPKEY);
		headers.put("app_channel", umeng_channel);
		app.setHeaders(headers);
		IntentFilter filter = new IntentFilter(Global.ACTION_PINCODE_REFRESH);
		filter.addAction(Global.ACTION_CONNECT_SUCCESS);
		filter.addAction(Global.ACTION_DISCONNECT_SERVER);
		registerReceiver(mReceiver, filter);
		
		if("j001".equals(umeng_channel)){
			web_url_textview.setText("tt.showkey.tv");
		}else{
			web_url_textview.setText(PreferencesUtils.getWebUrl(this));
		}
		if(Utils.getVersionCode(this)>PreferencesUtils.getGuidLastVersion(this)&&Constant.isNeedGuid){
			startActivity(new Intent(this, GuideActivity.class));
		}
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

//				Toast toast = new Toast(this);
//				View v = getLayoutInflater().inflate(R.layout.toast_textview, null);
//				toast.setView(v);
//				toast.setDuration(Toast.LENGTH_SHORT);
//				toast.setGravity(Gravity.CENTER, 0, 0);
//				toast.show();
				Utils.showToast(this, "再按一次退出程序");
				exitTime = System.currentTimeMillis();
			} else {
				finish();
//				android.os.Process.killProcess(android.os.Process.myPid());
//				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void findViews(){
		
		pincodeText = (TextView) findViewById(R.id.pincodeText);
		
		connectStatueText = (NotificationView) findViewById(R.id.statue_connect);
		web_url_textview = (TextView) findViewById(R.id.web_url_text);
		
		image_1_1 = (ImageView) findViewById(R.id.image_1_1);
		image_1_2 = (ImageView) findViewById(R.id.image_1_2);
		image_1_3 = (ImageView) findViewById(R.id.image_1_3);
		image_1_4 = (ImageView) findViewById(R.id.image_1_4);
		image_1_5 = (ImageView) findViewById(R.id.image_1_5);
		
		image_3_1= (ImageView) findViewById(R.id.image_3_1);
		image_3_2 = (ImageView) findViewById(R.id.image_3_2);
		image_3_3 = (ImageView) findViewById(R.id.image_3_3);
		image_3_4 = (ImageView) findViewById(R.id.image_3_4);
		image_3_5 = (ImageView) findViewById(R.id.image_3_5);
		
		layout_1_1 = (LinearLayout) findViewById(R.id.layout_1_1);
		layout_1_2 = (LinearLayout) findViewById(R.id.layout_1_2);
		layout_1_3 = (LinearLayout) findViewById(R.id.layout_1_3);
		layout_1_4 = (LinearLayout) findViewById(R.id.layout_1_4);
		layout_1_5 = (LinearLayout) findViewById(R.id.layout_1_5);
		
		layout_3_1 = (LinearLayout) findViewById(R.id.layout_3_1);
		layout_3_2 = (LinearLayout) findViewById(R.id.layout_3_2);
		layout_3_3 = (LinearLayout) findViewById(R.id.layout_3_3);
		layout_3_4 = (RelativeLayout) findViewById(R.id.layout_3_4);
		layout_3_5 = (LinearLayout) findViewById(R.id.layout_3_5);
		
		title_text_1 = (TextView) findViewById(R.id.title_1);
		title_text_2 = (TextView) findViewById(R.id.title_2);
		
		title_text_1.setOnClickListener(this);
		title_text_2.setOnClickListener(this);
//		relativeLayout = (FrameLayout) findViewById(R.id.relative_layout);
		
		layout_1_1.setOnFocusChangeListener(this);
		layout_1_2.setOnFocusChangeListener(this);
		layout_1_3.setOnFocusChangeListener(this);
		layout_1_4.setOnFocusChangeListener(this);
		layout_1_5.setOnFocusChangeListener(this);
		
		layout_3_1.setOnFocusChangeListener(this);
		layout_3_2.setOnFocusChangeListener(this);
		layout_3_3.setOnFocusChangeListener(this);
		layout_3_4.setOnFocusChangeListener(this);
		layout_3_5.setOnFocusChangeListener(this);
		
		layout_1_1.setOnHoverListener(this);
		layout_1_2.setOnHoverListener(this);
		layout_1_3.setOnHoverListener(this);
		layout_1_4.setOnHoverListener(this);
		layout_1_5.setOnHoverListener(this);
		
		layout_3_1.setOnHoverListener(this);
		layout_3_2.setOnHoverListener(this);
		layout_3_3.setOnHoverListener(this);
		layout_3_4.setOnHoverListener(this);
		layout_3_5.setOnHoverListener(this);
		
		layout_3_1.setDrawingCacheEnabled(true);
		layout_3_2.setDrawingCacheEnabled(true);
		layout_3_3.setDrawingCacheEnabled(true);
		layout_3_4.setDrawingCacheEnabled(true);
		layout_3_5.setDrawingCacheEnabled(true);
		
		layout_1_1.setDrawingCacheEnabled(true);
		layout_1_2.setDrawingCacheEnabled(true);
		layout_1_3.setDrawingCacheEnabled(true);
		layout_1_4.setDrawingCacheEnabled(true);
		layout_1_5.setDrawingCacheEnabled(true);
		
		layout_1_1.setOnClickListener(this);
		layout_1_2.setOnClickListener(this);
		layout_1_3.setOnClickListener(this);
		layout_1_4.setOnClickListener(this);
		layout_1_5.setOnClickListener(this);
		layout_3_1.setOnClickListener(this);
		layout_3_2.setOnClickListener(this);
		layout_3_3.setOnClickListener(this);
		layout_3_4.setOnClickListener(this);
		layout_3_5.setOnClickListener(this);
		
		layout_1_1.setTag(image_1_1);
		layout_1_2.setTag(image_1_2);
		layout_1_3.setTag(image_1_3);
		layout_1_4.setTag(image_1_4);
		layout_1_5.setTag(image_1_5);
		
		layout_3_1.setTag(image_3_1);
		layout_3_2.setTag(image_3_2);
		layout_3_3.setTag(image_3_3);
		layout_3_4.setTag(image_3_4);
		layout_3_5.setTag(image_3_5);
		
		layout_3_1.setOnKeyListener(this);
		
		layout_1_5.setOnKeyListener(this);
		layout_1_4.setOnKeyListener(this);
		
		layout_page_3 = findViewById(R.id.layout_page_3);
		layout_title = (LinearLayout) findViewById(R.id.layout_title);
		
		layout.SetOnViewChangeListener(new OnViewChangeListener() {
			
			@Override
			public void OnViewChange(int index) {
				// TODO Auto-generated method stub
				switch (index) {
				case 0:
					title_text_1.setTextColor(getResources().getColor(R.color.main_title_selected));
					title_text_2.setTextColor(getResources().getColor(R.color.main_title_unselected));
					updateImageView(layout_1_5);
					break;
				case 1:
					title_text_2.setTextColor(getResources().getColor(R.color.main_title_selected));
					title_text_1.setTextColor(getResources().getColor(R.color.main_title_unselected));
					updateImageView(layout_3_1);
					break;
				}
			}
		});
		if(Constant.isSimple){
			layout_page_3.setVisibility(View.GONE);
			layout_title.setVisibility(View.INVISIBLE);
			findViewById(R.id.layout_1_4).setVisibility(View.GONE);
			findViewById(R.id.layout_divider_3).setVisibility(View.GONE);
		}
//		web_url_textview.setText(Constant.BASE_URL.replace("http://", "").replace("https://", ""));
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
			case R.id.layout_1_1:
			case R.id.layout_1_2:
			case R.id.layout_1_3:
			case R.id.layout_1_4:
			case R.id.layout_1_5:
				if(layout.getSelected()==0){
					updateImageView(v);
					v.requestFocus();
				}
				break;
			case R.id.layout_3_1:
			case R.id.layout_3_2:
			case R.id.layout_3_3:
			case R.id.layout_3_4:
			case R.id.layout_3_5:
				if(!Constant.isSimple){
					if(layout.getSelected()==1){
						updateImageView(v);
						v.requestFocus();
					}
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
		case R.id.layout_1_4:
		case R.id.layout_1_5:
			if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT&&event.getAction() == KeyEvent.ACTION_DOWN){
				if (!Constant.isSimple) {
					layout.showNext();
					layout_3_1.requestFocus();
				}
				return true;
			}
			break;
		case R.id.layout_3_1:
			if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT&&event.getAction() == KeyEvent.ACTION_DOWN){
				if (!Constant.isSimple) {
					layout.showPre();
					layout_1_5.requestFocus();
				}
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
		case R.id.layout_1_1:
//			startActivity(new Intent(this, ManagePushApkActivity.class));
			break;
		case R.id.layout_1_2:
			startActivity(new Intent(this, CloudDataDisplayActivity.class));
			break;
		case R.id.layout_1_3:
//			startActivity(new Intent(this, XunLeiLXActivity.class));
			break;
		case R.id.layout_1_4:
			startActivity(new Intent(this, XunLeiLXActivity.class));
//			startActivity(new Intent(this, TvLiveSrcUpdateActivity.class));
			break;
		case R.id.layout_1_5:
//			startActivity(new Intent(this, AppRecommendActivity.class));
			startActivity(new Intent(this, SettingActivity.class));
			break;
		case R.id.layout_3_1:
			startActivity(new Intent(this, ScanActivity.class));
			break;
		case R.id.layout_3_2:
//			Log.d(TAG, "敬请期待");
			startActivity(new Intent(this, ManageAppActivity.class));
			break;
		case R.id.layout_3_3:
//			startActivity(new Intent(this, ManageAppActivity.class));
			startActivity(new Intent(this, AppRecommendActivity.class));
			break;
		case R.id.layout_3_4:
			Log.d(TAG, "敬请期待");
			break;
		case R.id.layout_3_5:
//			startActivity(new Intent(this, SettingActivity.class));
			startActivity(new Intent(this, TvLiveSrcUpdateActivity.class));
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
		if(web_url_textview.getText()==null||"".equals(web_url_textview.getText())){
			if(PreferencesUtils.getWebUrl(this)==null||PreferencesUtils.getWebUrl(this).length()==0){
				String online_base_url = MobclickAgent.getConfigParams(MainActivity.this, "URL"+ umeng_channel);
				Log.d(TAG, "online_base_url----->" + online_base_url);
				if(online_base_url!=null&&online_base_url.length()>0){
					web_url_textview.setText(online_base_url);
					PreferencesUtils.setWebUrl(MainActivity.this, online_base_url);
				}else{
					web_url_textview.setText("tt.showkey.tv");
				}
			}else{
				web_url_textview.setText(PreferencesUtils.getWebUrl(this));
			}
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
//		displayPincode();
		super.onResume();
		
		MobclickAgent.onResume(this);
	}
	
	
	class GetPinCodeTask implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Map<String, String> params = new HashMap<String, String>();
			params.put("app_key", Constant.APPKEY);
			params.put("mac_address", Utils.getMacAdd(MainActivity.this));
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
		unregisterReceiver(mReceiver);
//		XunLeiLiXianUtil.Logout(getApplicationContext());
	}
	
}
