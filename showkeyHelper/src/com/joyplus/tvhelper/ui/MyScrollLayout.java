package com.joyplus.tvhelper.ui;

import com.joyplus.tvhelper.utils.Log;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.Scroller;

public class MyScrollLayout extends ViewGroup{  
  
    private static final String TAG = "ScrollLayout";        
    private VelocityTracker mVelocityTracker;           // �����ж�˦������      
    private static final int SNAP_VELOCITY = 600;          
    private Scroller  mScroller;                        // ����������      
    private int mCurScreen;                               
    private int mDefaultScreen = 0;                            
    private float mLastMotionX;         
      
    private OnViewChangeListener mOnViewChangeListener;    
    public MyScrollLayout(Context context) {  
        super(context);  
        init(context);  
    }     
    public MyScrollLayout(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        init(context);  
    }     
    public MyScrollLayout(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);      
        init(context);  
    }     
    private void init(Context context)  
    {  
        mCurScreen = mDefaultScreen;                          
        mScroller = new Scroller(context);        
    }  
  
    @Override  
    protected void onLayout(boolean changed, int l, int t, int r, int b) {  
        // TODO Auto-generated method stub        
         if (changed) {      
                int childLeft = 0;      
                final int childCount = getChildCount();                       
                for (int i=0; i<childCount; i++) {      
                    final View childView = getChildAt(i);      
                    if (childView.getVisibility() != View.GONE) {      
                        final int childWidth = childView.getMeasuredWidth();
                        Log.d(TAG, "childLeft------------------->" + childLeft);
                        childView.layout(childLeft, 0,       
                                childLeft+childWidth, childView.getMeasuredHeight());      
                        childLeft += childWidth;      
                    }      
                }      
            } 
         
         updateAlpha();
    }  
  
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        // TODO Auto-generated method stub  
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);         
//        final int width = MeasureSpec.getSize(widthMeasureSpec);         
//        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);                      
//        final int count = getChildCount();         
//        for (int i = 0; i < count; i++) {     
//            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);         
//        }                 
//        Log.d(TAG, "width------------------------->" + width);
//        scrollTo(mCurScreen * width, 0);     
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
		int measureHeigth = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(measureWidth, measureHeigth);
		// TODO Auto-generated method stub
		for(int i= 0;i<getChildCount();i++){
			View v = getChildAt(i);
			int widthSpec = 0;
			int heightSpec = 0;
			LayoutParams params = v.getLayoutParams();
			if(params.width > 0){
				widthSpec = MeasureSpec.makeMeasureSpec(params.width, MeasureSpec.EXACTLY);
			}else if (params.width == -1) {
				widthSpec = MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.EXACTLY);
			} else if (params.width == -2) {
				widthSpec = MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.AT_MOST);
			}
			
			if(params.height > 0){
				heightSpec = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
			}else if (params.height == -1) {
				heightSpec = MeasureSpec.makeMeasureSpec(measureHeigth, MeasureSpec.EXACTLY);
			} else if (params.height == -2) {
				heightSpec = MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.AT_MOST);
			}
			v.measure(widthSpec, heightSpec);
		}
    }  
    
    public int getSelected(){
    	return mCurScreen;
    }
  
     public void snapToDestination() {      
//            final int screenWidth = getWidth();     
            final int left = getChildLeft(mCurScreen);
            final int width = getChildAt(mCurScreen).getMeasuredWidth();
            Log.d(TAG, "x----->" + getScrollX());
            Log.d(TAG, "left----->" + left);
            Log.d(TAG, "width----->" + width);
            int destScreen;
            if(getScrollX()>left){
            	destScreen = mCurScreen + ((getScrollX() - left + width/2)/width);
            }else{
            	destScreen = mCurScreen - ((left - getScrollX() + width/2)/width);
            }
            snapToScreen(destScreen);      
     }    
      
     public void snapToScreen(int whichScreen) {          
            // get the valid layout page      
//            whichScreen = Math.max(0, Math.min(whichScreen, getChildCount()-1));      
            whichScreen = 0;
            if (getScrollX() != (getChildLeft(whichScreen))) {      
                final int delta = getChildLeft(whichScreen)-getScrollX();      
//                        mScroller.startScroll(getScrollX(), 0,       
//                        delta, 0, Math.abs(delta)*2);                 
                        mScroller.startScroll(getScrollX(), 0,       
                        		delta, 0, Math.min(Math.abs(delta)*2, 500));                 
                mCurScreen = whichScreen;      
                invalidate();       // Redraw the layout                      
                if (mOnViewChangeListener != null)  
                {  
                    mOnViewChangeListener.OnViewChange(mCurScreen);  
                }  
            } 
            
            updateAlpha();
        }     
     
    private void updateAlpha(){
    	for(int i=0; i<getChildCount(); i++){
    		Log.d("ssss", "i***********>" + i);
        	if(i==mCurScreen){
        		getChildAt(i).setAlpha(1.0F);
        	}else{
        		getChildAt(i).setAlpha(0.2F);
        		Log.d("ssss", "i------>" + i);
        	}
        }
    }
     
    public void showPre(){
    	if(mCurScreen>0){
    		snapToScreen(mCurScreen-1);
    	}
    }
    
    public void showNext(){
    	if(mCurScreen<getChildCount()-1){
    		snapToScreen(mCurScreen+1);
    	}
    }
     
    @Override  
    public void computeScroll() {  
        // TODO Auto-generated method stub  
        if (mScroller.computeScrollOffset()) {      
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());    
            postInvalidate();      
        }     
    }  
  
    @Override  
    public boolean onTouchEvent(MotionEvent event) {  
        // TODO Auto-generated method stub                            
            final int action = event.getAction();      
            final float x = event.getX();      
            final float y = event.getY();                     
            switch (action) {      
            case MotionEvent.ACTION_DOWN:                 
                  Log.i("", "onTouchEvent  ACTION_DOWN");                   
                if (mVelocityTracker == null) {      
                        mVelocityTracker = VelocityTracker.obtain();      
                        mVelocityTracker.addMovement(event);   
                }              
                if (!mScroller.isFinished()){      
                    mScroller.abortAnimation();      
                }                  
                mLastMotionX = x;                
                break;                        
            case MotionEvent.ACTION_MOVE:    
               int deltaX = (int)(mLastMotionX - x);                 
               if (IsCanMove(deltaX)){  
                 if (mVelocityTracker != null){  
                        mVelocityTracker.addMovement(event);   
                 }     
                mLastMotionX = x;       
                scrollBy(deltaX, 0);      
               }         
               break;                         
            case MotionEvent.ACTION_UP:                       
                int velocityX = 0;  
                if (mVelocityTracker != null){  
                    mVelocityTracker.addMovement(event);   
                    mVelocityTracker.computeCurrentVelocity(1000);    
                    velocityX = (int) mVelocityTracker.getXVelocity();  
                }                                     
                if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {               
                    Log.e(TAG, "snap left");      
                    snapToScreen(mCurScreen - 1);         
                } else if (velocityX < -SNAP_VELOCITY         
                        && mCurScreen < getChildCount() - 1) {             
                    Log.e(TAG, "snap right");      
                    snapToScreen(mCurScreen + 1);         
                } else {         
                    snapToDestination();         
                }                                     
                if (mVelocityTracker != null) {         
                    mVelocityTracker.recycle();         
                    mVelocityTracker = null;         
                }         
                break;        
            }                     
            return true;      
    }  
  
    private boolean IsCanMove(int deltaX)  
    {  
        if (getScrollX() <= 0 && deltaX < 0 ){  
            return false;  
        }     
        
        if  (getScrollX() >=  getChildLeft(getChildCount()-1) && deltaX > 0){  
            return false;  
        }         
        return true;  
    }  
      
    public void SetOnViewChangeListener(OnViewChangeListener listener)  
    {  
        mOnViewChangeListener = listener;  
    }  
    
    private int getChildLeft(int index){
    	int x = 0;
    	for(int i=0; i<index; i++){
    		x += getChildAt(i).getMeasuredWidth();
    	}
    	
    	if(index>0){
    		if(index==getChildCount()-1){
    			return x-(getWidth()-getChildAt(index).getMeasuredWidth());
    		}else{
    			return x-(getWidth()-getChildAt(index).getMeasuredWidth())/2;
    		}
    	}else{
    		return x;
    	}
    }
    
    public interface OnViewChangeListener {  
        public void OnViewChange(int view);  
    } 
}  