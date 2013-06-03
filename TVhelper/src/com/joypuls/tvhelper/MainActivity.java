package com.joypuls.tvhelper;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnHoverListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joypuls.tvhelper.faye.FayeService;
import com.joypuls.tvhelper.ui.NotificationView;
import com.joypuls.tvhelper.utils.Global;
import com.joypuls.tvhelper.utils.HttpTools;
import com.joypuls.tvhelper.utils.PreferencesUtils;


public class MainActivity extends Activity implements OnHoverListener, OnFocusChangeListener, OnClickListener {
	
	private static String TAG = "MainActivity";
//	private static final String serverUrl = "http://otatest.joyplus.tv/generatePinCode";
	
	private static final int MESSAGE_GETPINCODE_SUCCESS = 0;
	
	private LinearLayout layout1, layout2, layout3;
	private RelativeLayout contentLayout_1,  contentLayout_2, contentLayout_3;
	private ImageView bigImage1, bigImage2, bigImage3;
	private NotificationView notificationTextView;
	private TextView pincodeText;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MESSAGE_GETPINCODE_SUCCESS:
				displayPincode();
				startService(new Intent(MainActivity.this, FayeService.class));
				this.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						contentLayout_2.setDrawingCacheEnabled(true);
						bigImage2.setImageBitmap(DrawShadowImg(contentLayout_2.getDrawingCache()));
						contentLayout_2.setDrawingCacheEnabled(false);
					}
				},100);
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bigImage1 = (ImageView) findViewById(R.id.test1);
		bigImage2 = (ImageView) findViewById(R.id.test2);
		bigImage3 = (ImageView) findViewById(R.id.test3);
		layout1 = (LinearLayout) findViewById(R.id.main_layout1);
		layout2 = (LinearLayout) findViewById(R.id.main_layout2);
		layout3 = (LinearLayout) findViewById(R.id.main_layout3);
		contentLayout_1 = (RelativeLayout) findViewById(R.id.main_content_1);
		contentLayout_2 = (RelativeLayout) findViewById(R.id.main_content_2);
		contentLayout_3 = (RelativeLayout) findViewById(R.id.main_content_3);
		notificationTextView = (NotificationView) findViewById(R.id.notification);
		pincodeText = (TextView) findViewById(R.id.pincodeText);
		layout1.setOnHoverListener(this);
		layout2.setOnHoverListener(this);
		layout3.setOnHoverListener(this);
		layout1.setOnFocusChangeListener(this);
		layout2.setOnFocusChangeListener(this);
		layout3.setOnFocusChangeListener(this);
		layout1.setOnClickListener(this);
		layout2.setOnClickListener(this);
		layout3.setOnClickListener(this);
		contentLayout_1.setDrawingCacheEnabled(true);
		contentLayout_2.setDrawingCacheEnabled(true);
		contentLayout_3.setDrawingCacheEnabled(true);
		Log.d(TAG, "main thred id ********************************" + Thread.currentThread().getId());
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int height = layout1.getMeasuredHeight();
				int width = layout1.getMeasuredWidth();
				RelativeLayout.LayoutParams layoutParam1 = new RelativeLayout.LayoutParams(width+40,height);
				RelativeLayout.LayoutParams layoutParam2 = new RelativeLayout.LayoutParams(width+40,height);
				RelativeLayout.LayoutParams layoutParam3 = new RelativeLayout.LayoutParams(width+40,height);
				bigImage1.setLayoutParams(layoutParam1);
				bigImage2.setLayoutParams(layoutParam2);
				bigImage3.setLayoutParams(layoutParam3);
				bigImage1.setScaleType(ScaleType.FIT_XY);
				bigImage2.setScaleType(ScaleType.FIT_XY);
				bigImage3.setScaleType(ScaleType.FIT_XY);
				MarginLayoutParams margin1 = (MarginLayoutParams) bigImage2.getLayoutParams();
				margin1.setMargins(width, margin1.topMargin, margin1.rightMargin, margin1.bottomMargin);
				MarginLayoutParams margin2 = (MarginLayoutParams) bigImage3.getLayoutParams();
				margin2.setMargins(2*width, margin2.topMargin, margin2.rightMargin, margin2.bottomMargin);
				
				bigImage1.requestLayout();
				
				layout1.setFocusable(true);
				layout1.setFocusableInTouchMode(true);
				layout2.setFocusable(true);
				layout2.setFocusableInTouchMode(true);
				layout3.setFocusable(true);
				layout3.setFocusableInTouchMode(true);
				
				layout2.requestFocus();
			}
		},200);
		if(PreferencesUtils.getPincode(this)==null){
			new Thread(new GetPinCodeTask()).start();
		}else{
			startService(new Intent(MainActivity.this, FayeService.class));
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		displayPincode();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onHover(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_HOVER_EXIT:
//			switch (v.getId()) {
//			case R.id.main_layout1:
//				bigImage1.setVisibility(View.INVISIBLE);
//				break;
//			case R.id.main_layout2:
//				bigImage2.setVisibility(View.INVISIBLE);
//				break;
//			case R.id.main_layout3:
//				bigImage3.setVisibility(View.INVISIBLE);
//				break;
//			}
			break;

		case MotionEvent.ACTION_HOVER_ENTER:
			switch (v.getId()) {
			case R.id.main_layout1:
//				bigImage1.setVisibility(View.VISIBLE);
//				bigImage1.setImageBitmap(DrawShadowImg(contentLayout_1.getDrawingCache()));
				layout1.requestFocus();
				break;
			case R.id.main_layout2:
//				bigImage2.setVisibility(View.VISIBLE);
//				bigImage2.setImageBitmap(DrawShadowImg(contentLayout_2.getDrawingCache()));
				layout2.requestFocus();
				break;
			case R.id.main_layout3:
//				bigImage3.setVisibility(View.VISIBLE);
//				bigImage3.setImageBitmap(DrawShadowImg(contentLayout_3.getDrawingCache()));
				layout3.requestFocus();
				break;
			}
			break;
		}
		return false;
	}
	
	public Bitmap DrawShadowImg(Bitmap bitmap){
		BlurMaskFilter blurFilter = new BlurMaskFilter(10, BlurMaskFilter.Blur.OUTER);
		Paint shadowPaint = new Paint();
		shadowPaint.setMaskFilter(blurFilter);
	
		int[] offsetXY = new int[2];
		Bitmap shadowImage = bitmap.extractAlpha(shadowPaint, offsetXY);
		Bitmap shadow = shadowImage.copy(Bitmap.Config.ARGB_8888, true);
		Canvas c = new Canvas(shadow);
		c.drawBitmap(bitmap, -offsetXY[0], -offsetXY[1], null);
		return shadow;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if(hasFocus){
			switch (v.getId()) {
			case R.id.main_layout1:
				bigImage1.setVisibility(View.VISIBLE);
				bigImage1.setImageBitmap(DrawShadowImg(contentLayout_1.getDrawingCache()));
				break;
			case R.id.main_layout2:
				bigImage2.setVisibility(View.VISIBLE);
				contentLayout_2.setDrawingCacheEnabled(true);
				bigImage2.setImageBitmap(DrawShadowImg(contentLayout_2.getDrawingCache()));
				contentLayout_2.setDrawingCacheEnabled(false);
				break;
			case R.id.main_layout3:
				bigImage3.setVisibility(View.VISIBLE);
				bigImage3.setImageBitmap(DrawShadowImg(contentLayout_3.getDrawingCache()));
				break;
			}
		}else{
			switch (v.getId()) {
			case R.id.main_layout1:
				bigImage1.setVisibility(View.INVISIBLE);
				break;
			case R.id.main_layout2:
				bigImage2.setVisibility(View.INVISIBLE);
				break;
			case R.id.main_layout3:
				bigImage3.setVisibility(View.INVISIBLE);
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.main_layout1:
//			notificationTextView.setText("�������");
			startActivity(new Intent(MainActivity.this, ManageAppActivity.class));
			break;
		case R.id.main_layout2:
			startActivity(new Intent(MainActivity.this, ManagePushApkActivity.class));
			break;
		case R.id.main_layout3:
//			notificationTextView.setText("个人设置");
			startActivity(new Intent(MainActivity.this, SettingActivity.class));
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
					Log.d(TAG, displayString);
				}
			}
		}
		Log.d(TAG, displayString);
		pincodeText.setText(displayString);
	}
	

	class GetPinCodeTask implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Map<String, String> params = new HashMap<String, String>();
			params.put("app_key", "ijoyplus_android_0001bj");
			params.put("mac_address", FayeService.getMacAdd(MainActivity.this));
			params.put("client", new Build().MODEL);
			Log.d(TAG, "client = " + new Build().MODEL);
			String str = HttpTools.post(MainActivity.this, Global.serverUrl+"/generatePinCode", params);
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
				Toast.makeText(MainActivity.this, "请求pinCode失败", 100).show();
				e.printStackTrace();
			}
			  
		}
		
	}
}
