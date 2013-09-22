package com.joyplus;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.TextView;

import com.joyplus.Sub.JoyplusSubManager;
import com.joyplus.manager.URLManager;
import com.joyplus.manager.URLManager.Quality;
import com.joyplus.mediaplayer.JoyplusMediaPlayerManager;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.Utils;


public class JoyplusMediaPlayerPreference extends AlertDialog{

	private boolean Debug = true;
	private String  TAG   = "JoyplusMediaPlayerPreference";
	
	private final static int MSG_BASE = 400;
	public  final static int MSG_QUALITY_CHANGE = MSG_BASE+1;
	public  final static int MSG_SUB_CHANGE     = MSG_BASE+2;
	
	private Context    mContext;
	private QUALITY    mQuality;
	private SUB        mSub;
	private Handler mHandler;
	public void setHandler(Handler handler){
		mHandler = handler;
	}
	public void setVisible(boolean visible){
		if(visible){
			show();
			if(mQuality != null)mQuality.setURLManager(JoyplusMediaPlayerManager.getInstance().getURLManager());
			if(mSub != null)    mSub.setSubManager(JoyplusMediaPlayerManager.getInstance().getSubManager());
		}
		else Dismiss();
	}
	
	public JoyplusMediaPlayerPreference(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;		
	}	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.video_choose_defination);
		initView();
	}
//    public void setURLManager(URLManager urlManager){
//    	if(mQuality == null) return;
//    	mQuality.setURLManager(urlManager);
//    }
	private void initView() {
		// TODO Auto-generated method stub		
		findViewById(R.id.btn_ok_def).setOnClickListener(new OKListener());
		findViewById(R.id.btn_cancle_def).setOnClickListener(new android.view.View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Dismiss();				
			}
		});
		mQuality   = new QUALITY();
		mSub       = new SUB();
	}
	private void Dismiss() {
		// TODO Auto-generated method stub
		JoyplusMediaPlayerPreference.this.dismiss();
	}	
	private class OKListener implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(mHandler==null) {
				Dismiss();
				return;
			}
			if(mQuality != null && mQuality.isChange()){
				 Message m = new Message();
				 m.what    = MSG_QUALITY_CHANGE;
				 m.obj     = mQuality.getCurrentQuality();
				 mHandler.sendMessage(m);		
			}else if(mSub != null && mSub.isChange()){
				 Message m = new Message();
				 m.what    = MSG_SUB_CHANGE;
				 m.arg1     = mSub.getCurrentIndex();
				 mHandler.sendMessage(m);
			}
			Dismiss();
		}		
	}
	private class QUALITY {		
		public  Gallery              gallery;
		private URLManager.Quality   mCurrentQuality;
		private ArrayList<String>    definationStrings = new ArrayList<String>();//清晰度选择
		public  QUALITY(){
			gallery = (Gallery) findViewById(R.id.gallery_def);
			gallery.requestFocus();
		};
        public void setURLManager(URLManager urlManager){
        	if(urlManager == null)return;
        	definationStrings = urlManager.getExitQualityList();
        	mCurrentQuality   = urlManager.getCurrentQuality();
        	gallery.setAdapter(new QuaSubAdapter(definationStrings));
        	gallery.setSelection(definationStrings.indexOf(URLManager.getQualityString(mCurrentQuality)));
        	gallery.requestFocus();
        }
        public boolean isChange(){
        	Log.i(TAG, "isChange-->gallery.getSelectedItemPosition():" + gallery.getSelectedItemPosition()
        			+ " definationStrings:" + definationStrings.toString()
        			+ " URLManager.getQualityString(mCurrentQuality):" + URLManager.getQualityString(mCurrentQuality));
        	return gallery.getSelectedItemPosition() != (definationStrings.indexOf(URLManager.getQualityString(mCurrentQuality)));
        }
        public Quality getCurrentQuality(){
        	return JoyplusMediaPlayerManager.getInstance().getURLManager().getQualityFromString(
        			     definationStrings.get(gallery.getSelectedItemPosition()));
        }
	}
	private class SUB {		
		private Gallery    gallery;
		private ArrayList<String> definationStrings = new ArrayList<String>();
		public SUB(){
			gallery = (Gallery) findViewById(R.id.gallery_zimu);
		};
        public void setSubManager(JoyplusSubManager subManager){
        	if(subManager == null)return;
        	definationStrings.clear();
        	Log.i(TAG, "SUB-setSubManager-size-->" + subManager.getSubList().size());
            if(subManager.CheckSubAviable()){
            	definationStrings.add(mContext.getResources().getString(R.string.meidaplayer_sub_string_closesub));
            	for(int i = 0;i<subManager.getSubList().size();i++){
            		definationStrings.add(mContext.getResources().getString(R.string.meidaplayer_sub_string_sub,(i+1)));
            	}
            	gallery.setAdapter(new QuaSubAdapter(definationStrings));
            	Log.i(TAG, "subManager.getCurrentSubIndex()+1--->" + (subManager.getCurrentSubIndex()+1));
            	if(subManager.IsSubEnable()){
            		gallery.setSelection(subManager.getCurrentSubIndex()+1);//for have add "sub close"
            	}else {
            		gallery.setSelection(0);//for have add "sub close"

            	}
            }else{
            	definationStrings.add(mContext.getResources().getString(R.string.meidaplayer_sub_string_nosub));
            	gallery.setAdapter(new QuaSubAdapter(definationStrings));
            }
        }	
        public boolean isChange(){
        	return gallery.getSelectedItemPosition() != (definationStrings.indexOf(JoyplusMediaPlayerManager.getInstance().getSubManager().getCurrentSubIndex()+1));
        }
        public int getCurrentIndex(){
        	return gallery.getSelectedItemPosition();
        }
	}
	
	class QuaSubAdapter extends BaseAdapter{
		private ArrayList<String> StringResource = new ArrayList<String>();
		public QuaSubAdapter(ArrayList<String> list){
			if(list == null)return;
			StringResource.clear();
			StringResource = list;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return StringResource.size();
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return CreateTextView(StringResource.get(position));
		}
		private TextView CreateTextView(String text){
			TextView tv = new TextView(mContext);
			tv.setBackgroundResource(R.drawable.bg_choose_defination_selector);
			tv.setTextColor(Color.WHITE);
			tv.setTextSize(25);
			tv.setText(text);
			Gallery.LayoutParams param = new Gallery.LayoutParams(Utils.getStandardValue(mContext,165), 
					Utils.getStandardValue(mContext,40));
			tv.setGravity(Gravity.CENTER);
			tv.setLayoutParams(param);
			return tv;
		}
	} 
	
	
	/**
	 * 
	 * @param list
	 * @param subtitleIndex -1 字幕不显示
	 */
//	public void setSubtitleGalleryAdapter(ArrayList<Integer> list,int subtitleIndex){
//		zimuStrings.clear();
//		if(list != null){
//			zimuStrings = list;
//		}
//		gallery.setAdapter(new DefinationAdapter(mContext, zimuStrings));
//		if(subtitleIndex == -1){
//			gallery.setSelection(0);
//		}else {
//			if(zimuStrings.size()==1&&zimuStrings.get(0)==-1){
//				gallery_zm.setSelection(0);
//			}else{
//				gallery_zm.setSelection(subtitleIndex+1);
//			}
//		}
//	}
	
//	class DefinationAdapter extends BaseAdapter{
//
//		List<Integer> list;
//		Context c;
//		
//		public DefinationAdapter(Context c, List<Integer> list){
//			this.c = c;
//			this.list = list;
//		}
//		
//		@Override
//		public int getCount() {
//			// TODO Auto-generated method stub
//			return list.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			// TODO Auto-generated method stub
//			return 0;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			// TODO Auto-generated method stub
//			TextView tv = new TextView(c);
//			tv.setBackgroundResource(R.drawable.bg_choose_defination_selector);
//			tv.setTextColor(Color.WHITE);
//			tv.setTextSize(25);
//			switch (list.get(position)) {
//			case Constant.DEFINATION_HD2:
//				tv.setText("超    清");
//				break;
//			case Constant.DEFINATION_MP4:
//				tv.setText("高    清");
//				break;
//			case Constant.DEFINATION_FLV:
//				tv.setText("标    清");
//				break;
//			default:
//				tv.setText("流    畅");
//				break;
//			}
//			Gallery.LayoutParams param = new Gallery.LayoutParams(UtilTools.getStandardValue(mContext,165), 
//					UtilTools.getStandardValue(mContext,40));
//			tv.setGravity(Gravity.CENTER);
//			tv.setLayoutParams(param);
//			return tv;
//		}
//
//		
//	}
//	
//	
//	
//	class ZimuAdapter extends BaseAdapter{
//
//		List<Integer> list;
//		Context c;
//		
//		public ZimuAdapter(Context c, List<Integer> list){
//			Log.i(TAG, "ZimuAdapter list--->" + list.size());
//			this.c = c;
//			this.list = list;
//		}
//		
//		@Override
//		public int getCount() {
//			// TODO Auto-generated method stub
//			Log.i(TAG, "ZimuAdapter getCount--->" + list.size());
//			return list.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			// TODO Auto-generated method stub
//			return 0;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			// TODO Auto-generated method stub
//			TextView tv = new TextView(c);
//			tv.setTextColor(Color.WHITE);
//			tv.setBackgroundResource(R.drawable.bg_choose_defination_selector);
//			tv.setTextSize(25);
//			Log.i(TAG, "ZimuAdapter position--->" + position);
//			if(position>=0&&position<list.size()){
//				switch (list.get(position)) {
//				case -1://无字幕
//					tv.setText("暂无字幕");
//					break;
//				case 0://字幕关
//					tv.setText("关");
//					break;
//					
//				default:
//					tv.setText("字幕" + list.get(position));
//					break;
////				case 1://字幕开
////					tv.setText("");
////					break;
//				}
//			}
//			Gallery.LayoutParams param = new Gallery.LayoutParams(UtilTools.getStandardValue(mContext,165),
//					UtilTools.getStandardValue(mContext,40));
//			tv.setGravity(Gravity.CENTER);
//			tv.setLayoutParams(param);
//			return tv;
//		}
//
//	}
}
