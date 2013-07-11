package com.joyplus.tvhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.joyplus.tvhelper.utils.Global;

public class DialogActivity extends Activity implements OnClickListener {

	private Button btn_cancle;
	private Button btn_ok;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);
		btn_cancle = (Button) findViewById(R.id.btn_canle);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		
		btn_ok.setOnClickListener(this);
		btn_cancle.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_ok:
			sendBroadcast(new Intent(Global.ACTION_CONFIRM_ACCEPT));
			break;
		case R.id.btn_canle:
			sendBroadcast(new Intent(Global.ACTION_CONFIRM_REFUSE));
			break;
		}
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			sendBroadcast(new Intent(Global.ACTION_CONFIRM_REFUSE));
		}
		return super.onKeyDown(keyCode, event);
	}
}
