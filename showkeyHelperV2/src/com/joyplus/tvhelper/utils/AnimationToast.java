package com.joyplus.tvhelper.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.joyplus.tvhelper.utils.AnimationToast;
import com.joyplus.tvhelper.R;

public class AnimationToast {
	static final String TAG = "AnimationToast";    
    public static final int LENGTH_SHORT = 0;  // 显示时间长短
    public static final int LENGTH_LONG = 1;  
    private TextView toasttext;
    final Context mContext;  
    int mDuration;  
    PopupWindow mPopToast;  
    View mParent;  
    int width = 380;//toast 宽  
    int height =120;//toast 高  
	static AnimationToast cacheresult;
    public AnimationToast(Context context)  
    {  
        mContext = context;  
    }  
      
    public void show()  
    {  
        mPopToast.showAtLocation(mParent, Gravity.TOP|Gravity.RIGHT,0, 35);  
          
        //LONG��5000ms    SHORT��300ms  
        //显示时间等于动画时间加上不变时间
        long duration = mDuration== LENGTH_LONG ? 3000 : 1000;  
          
        mParent.postDelayed(new Runnable()  
        {  
            @Override  
            public void run()  
            {  
                cancel();  
            }  
        }, duration);  
    }  
      
    public void cancel()  
    {  
        mPopToast.dismiss();  
    }  
      
    /** 
     * 设置toast 宽高 动画 背景 
     */  
    public void setView(CharSequence text)   
    {  
    	
    	LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.popup_window, null);
        toasttext=(TextView)v.findViewById(R.id.toast_text);
        if(text.length()>30){
        	toasttext.setTextSize(20);
        }else if(text.length()>14){
        	toasttext.setTextSize(22);
        }

        toasttext.setText(text);
        mPopToast = new PopupWindow(v, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
        mPopToast.setAnimationStyle(R.style.AnimationToast);  
        mPopToast.setFocusable(false);  
        mPopToast.setOutsideTouchable(true);
         
    }  
  
    /** 
     *获取视图
     */  
    public View getView()   
    {  
        return mPopToast.getContentView();  
    }  
      
    public void setParent(View parent)  
    {  
        mParent = parent;  
    }  
      
    public View getParent()  
    {  
        return mParent;  
    } 
    public void setDuration(int duration)   
    {  
        mDuration = duration;  
    }  
    public int getDuration()   
    {  
        return mDuration;  
    }  
      
    public void setWidth(int w)  
    {  
        width = w;  
    }  
      
    public void setHeight(int h)  
    {  
        height =h;  
    } 
    public static AnimationToast makeText(Context context, CharSequence text, int duration, View parent)   
    {  
    	Log.d(TAG,"makeText"+text+"duration"+duration+"parent"+parent);
    	if(cacheresult!=null){
    		cacheresult.cancel();
    	}
    	AnimationToast result = new AnimationToast(context);  
    	cacheresult=result;
//        TextView tv = new TextView(context);
//        tv.setTextSize(25);
//        tv.setTextColor(Color.WHITE);  
//        tv.setGravity(Gravity.CENTER);  
//        tv.setText(text);  
          
        result.setView(text);  
        result.setParent(parent);  
        result.setDuration(duration);  
        
        return result;  
    }  
    public static AnimationToast makeText(Context context, int resId, int duration, View parent)  
                                throws Resources.NotFoundException {  
        return makeText(context, context.getResources().getText(resId), duration, parent);  
    }  

}
