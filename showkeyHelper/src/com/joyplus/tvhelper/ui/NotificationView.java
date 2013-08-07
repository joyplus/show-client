package com.joyplus.tvhelper.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joyplus.tvhelper.R;

public class NotificationView extends RelativeLayout{
	
	private TextView textView1;
	private TextView textView2;
	
	private TextView currentTextView;
	
	private Animation out;
	private Animation in;

	public NotificationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,  
                R.styleable.NotificationView);
		init(a);
	}

	public NotificationView(Context context) {
		super(context);
		init(null);
	}
	
	
	public void init(TypedArray a){
		LayoutParams layout = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		textView1 = new TextView(getContext());
		textView1.setLayoutParams(layout);
		textView1.setVisibility(View.VISIBLE);
		currentTextView = textView1;
		textView2 = new TextView(getContext());
		textView2.setLayoutParams(layout);
		textView2.setVisibility(View.INVISIBLE);
		if(a!=null){
			textView1.setText(a.getString(R.styleable.NotificationView_text));
			textView1.setTextColor(a.getColor(R.styleable.NotificationView_textColor, Color.GRAY));
			textView1.setTextSize(a.getDimension(R.styleable.NotificationView_textSize, 20));
			textView2.setTextColor(a.getColor(R.styleable.NotificationView_textColor, Color.GRAY));
			textView2.setTextSize(a.getDimension(R.styleable.NotificationView_textSize, 20));
		}else{
			textView1.setTextColor(Color.GRAY);
			textView1.setTextSize(20);
			textView2.setTextColor(Color.GRAY);
			textView2.setTextSize(20);
		}
		textView1.setGravity(Gravity.LEFT);
		textView2.setGravity(Gravity.LEFT);
		this.addView(textView1);
		this.addView(textView2);
		out = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_out);
		in = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_in);
	}
	
	public void setText(String str){
		if(textView1.getVisibility()==View.VISIBLE){
			textView2.setText(str);
			textView1.setVisibility(View.INVISIBLE);
			textView2.setVisibility(View.VISIBLE);
			currentTextView = textView2;
			textView1.startAnimation(out);
			textView2.startAnimation(in);
		}else{
			textView1.setText(str);
			textView2.setVisibility(View.INVISIBLE);
			textView1.setVisibility(View.VISIBLE);
			currentTextView = textView1;
			textView2.startAnimation(out);
			textView1.startAnimation(in);
		}
	}
	
	public String getText(){
		return currentTextView.getText().toString();
	}
}
