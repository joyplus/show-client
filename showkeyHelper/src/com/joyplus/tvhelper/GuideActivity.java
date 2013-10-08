package com.joyplus.tvhelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.joyplus.tvhelper.ui.GuideScrollLayout;
import com.joyplus.tvhelper.utils.PreferencesUtils;
import com.joyplus.tvhelper.utils.Utils;

public class GuideActivity extends Activity implements OnGestureListener {

	private GuideScrollLayout layout;
	private ImageView guide_page_1;
	private ImageView guide_page_2;
	private ImageView guide_page_3;
	private ImageView guide_page_4;
	private GestureDetector detector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		PreferencesUtils.setGuidLastVersion(this, Utils.getVersionCode(this));
		layout = (GuideScrollLayout) findViewById(R.id.layout);
		guide_page_1 = (ImageView) findViewById(R.id.guide_page_1);
		guide_page_2 = (ImageView) findViewById(R.id.guide_page_2);
		guide_page_3 = (ImageView) findViewById(R.id.guide_page_3);
		guide_page_4 = (ImageView) findViewById(R.id.guide_page_4);
		try{
			guide_page_1.setBackgroundResource(R.drawable.guide_page_1);
			guide_page_2.setBackgroundResource(R.drawable.guide_page_2);
			guide_page_3.setBackgroundResource(R.drawable.guide_page_3);
			guide_page_4.setBackgroundResource(R.drawable.guide_page_4);
		}catch (Exception e) {
			// TODO: handle exception
			finish();
		}
		detector = new GestureDetector(this,this); 
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if(layout.getSelected()>0){
				layout.showPre();
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if(layout.getSelected()>=layout.getChildCount()-1){
				finish();
			}else{
				layout.showNext();
			}
			break;
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			if(layout.getSelected()==layout.getChildCount()-1){
				finish();
			}
			break;
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_ESCAPE:
			return true;
		default:
			break;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return detector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		if(layout.getSelected()==layout.getChildCount()-1){
			finish();
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		if(e1.getX()<e2.getX()){
			if(layout.getSelected()>0){
				layout.showPre();
			}
		}else{
			if(layout.getSelected()>=layout.getChildCount()-1){
				finish();
			}else{
				layout.showNext();
			}
		}
		return false;
	}
}
