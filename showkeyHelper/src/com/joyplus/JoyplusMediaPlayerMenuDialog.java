package com.joyplus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.joyplus.mediaplayer.JoyplusMediaPlayerManager;
import com.joyplus.mediaplayer.JoyplusMediaPlayerScreenManager;
import com.joyplus.sub.JoyplusSubManager;
import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.entity.URLS_INDEX;
import com.joyplus.tvhelper.utils.Constant;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.Utils;

public class JoyplusMediaPlayerMenuDialog extends AlertDialog implements OnItemClickListener {

	private static final String TAG = "JoyplusMediaPlayerMenuDialog";
	private JoyplusMediaPlayerActivity mContext;
	private static final int MAX = 3;
	private int MIN = 0;
	
//	private String[] list1 = {"1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111","222222222222222","33333333333333333","44444444444444","555555555555555555555555555555555555","222222222222222","33333333333333333","44444444444444","555555555555555555555555555555555555"};
//	private String[] list3 = {"超清","高清","标清","普清"};
//	private String[] list2 = {"字幕关","字幕1","字幕2","字幕3"};
//	private String[] list4 = {"全屏","4:3","自适应"};
	
	private List<String> list_juji;
	private List<String> list_zimu;
	private List<Integer> list_definition;
	private List<Integer> list_size;
	private Map<Integer, Integer> selectionPosions = new HashMap<Integer, Integer>();
	
	private ListView list;
	private MyAdapter adapter;
	private ImageView bg_image;
	private ImageView bg_title_selceted;
	private TextView title_xuanji;
	private TextView title_zimu;
	private TextView title_definition;
	private TextView title_size;
	private TextView selectedTitle;
	
	private int title_selecet_index = 0;
	
	private Handler mHandler = new Handler();
	
	public JoyplusMediaPlayerMenuDialog(JoyplusMediaPlayerActivity context) {
		super(context,R.style.Transparent);
		// TODO Auto-generated constructor stub
		mContext = context;		
	}	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		this.setContentView(R.layout.player_menu_dialog);
		findViews();
//		initView();
	}
	
	private void findViews(){
		list = (ListView) findViewById(R.id.list_choice);
		bg_image = (ImageView) findViewById(R.id.highlight_bg);
		bg_image.setVisibility(View.INVISIBLE);
		bg_title_selceted = (ImageView) findViewById(R.id.title_selected_background);
		title_xuanji = (TextView) findViewById(R.id.title_xuanji);
		title_zimu = (TextView) findViewById(R.id.title_zimu);
		title_definition = (TextView) findViewById(R.id.title_definition);
		title_size = (TextView) findViewById(R.id.title_size);
		list.setOnItemClickListener(this);
	}
	
	private void initView(){
		Log.d(TAG, "initView called------------------>");
		if(selectedTitle!=null){
			selectedTitle.setPadding(0, Utils.getStandardValue(mContext, 10), 0, Utils.getStandardValue(mContext, 10));
			selectedTitle.setTextSize(25);
		}
		MIN = list_juji!=null?0:1;
		title_selecet_index = MIN;
		updateTitleSelceted();
		if(MIN == 0){
			title_xuanji.setTextSize(35);
			title_xuanji.setPadding(0, 0, 0, 0);
			selectedTitle = title_xuanji;
		}else{
			title_zimu.setTextSize(35);
			title_zimu.setPadding(0, 0, 0, 0);
			selectedTitle = title_zimu;
			title_xuanji.setVisibility(View.GONE);
		}
		adapter = new MyAdapter();
		list.setAdapter(adapter);
		list.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.d(TAG, "top--------->" + (view.getTop()-Utils.getStandardValue(mContext, 10)));
				Log.d(TAG, "bottom----------------->"+(view.getBottom()+Utils.getStandardValue(mContext, 45)));
				if(position>=0){
					bg_image.setVisibility(View.VISIBLE);
					bg_image.layout(bg_image.getLeft(), view.getTop()-Utils.getStandardValue(mContext, 10), 
							bg_image.getRight(), view.getBottom()+Utils.getStandardValue(mContext, 45));
				}else{
					bg_image.setVisibility(View.INVISIBLE);
				}
				if(title_selecet_index == 3){
					if(list_size.get(position)!=mContext.getVideoSizeType()){
						mContext.changeVideoSize(list_size.get(position));
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
		list.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT&& event.getAction() == KeyEvent.ACTION_UP){
					title_selecet_index -= 1;
					if(title_selecet_index<MIN){
						title_selecet_index = MIN;
					}else{
						updateTitleSelceted();
						adapter = new MyAdapter();
						list.setAdapter(adapter);
						list.setSelection(selectionPosions.get(title_selecet_index));
					}
					return true;
				}else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT&&event.getAction() == KeyEvent.ACTION_UP){
					title_selecet_index += 1;
					if(title_selecet_index>MAX){
						title_selecet_index = MAX;
					}else{
						updateTitleSelceted();
						adapter = new MyAdapter();
						list.setAdapter(adapter);
						list.setSelection(selectionPosions.get(title_selecet_index));
					}
					return true;
				}else if(keyCode == KeyEvent.KEYCODE_MENU&&event.getAction() == KeyEvent.ACTION_UP){
					dismiss();
					return true;
				}
				return false;
			}
		});
		
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				list.setSelection(selectionPosions.get(title_selecet_index));
			}
		}, 150);
//		list.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//			
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				// TODO Auto-generated method stub
//				if(hasFocus){
//					list.setSelection(selectionPosions.get(title_selecet_index));
//					Log.d(TAG, "requst  hasFocus ------------->");
//				}
//			}
//		});
	}
	
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			int count = 0;
			switch (title_selecet_index) {
			case 0:
				count = list_juji.size();
				break;
			case 1:
				count = list_zimu.size();
				break;
			case 2:
				count = list_definition.size();
				break;
			case 3:
				count = list_size.size();
				break;
			}
			return count;
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
			TextView view;
			if(convertView == null){
				view = new TextView(mContext);
				LayoutParams layoutParams = new LayoutParams(parent.getWidth(), Utils.getStandardValue(mContext, 70));
				view.setLayoutParams(layoutParams);
				view.setGravity(Gravity.CENTER);
				view.setTextColor(Color.WHITE);
				view.setTextSize(25);
				view.setSingleLine(true);
				view.setEllipsize(TruncateAt.MARQUEE);
			}else{
				view = (TextView) convertView;
			}
			if(title_selecet_index == 0){
				view.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
				view.setPadding(Utils.getStandardValue(mContext, 35), 0, Utils.getStandardValue(mContext, 35), 0);
			}else{
				view.setGravity(Gravity.CENTER);
				view.setPadding(0, 0, 0, 0);
			}
			switch (title_selecet_index) {
			case 0:
				view.setText(list_juji.get(position));
				break;
			case 1:
				view.setText(list_zimu.get(position));
				break;
			case 2:
				switch (list_definition.get(position)) {
				case Constant.DEFINATION_HD2:
					view.setText("超    清");
					break;
				case Constant.DEFINATION_HD:
					view.setText("高    清");
					break;
				case Constant.DEFINATION_MP4:
					view.setText("标    清");
					break;
				case Constant.DEFINATION_FLV:
					view.setText("流    畅");
					break;
				}
				break;
			case 3:
				switch (list_size.get(position)) {
				case JoyplusMediaPlayerScreenManager.LINEARLAYOUT_PARAMS_FULL:
					view.setText("全    屏");
					break;
				case JoyplusMediaPlayerScreenManager.LINEARLAYOUT_PARAMS_16x9:
					view.setText("16 : 9");
					break;
				case JoyplusMediaPlayerScreenManager.LINEARLAYOUT_PARAMS_4x3:
					view.setText("4 : 3");
					break;
				case JoyplusMediaPlayerScreenManager.LINEARLAYOUT_PARAMS_ORIGINAL:
					view.setText("自 适 应");
					break;
				}
				break;
			}
			return view;
		}
	}
	
	private void updateTitleSelceted(){
		MarginLayoutParams p = (MarginLayoutParams) bg_title_selceted.getLayoutParams();
		p.setMargins(Utils.getStandardValue(mContext, 15)+(title_selecet_index-MIN)*title_zimu.getWidth(), p.topMargin, 
				p.rightMargin, p.bottomMargin);
		bg_title_selceted.setLayoutParams(p);
//		bg_title_selceted.layout(15+(title_selecet_index-MIN)*title_xuanji.getWidth(), 
//				bg_title_selceted.getTop(), 
//				15+(title_selecet_index-MIN)*title_xuanji.getWidth()+bg_title_selceted.getWidth(),
//				bg_title_selceted.getBottom());
		switch (title_selecet_index) {
		case 0:
			if(selectedTitle!=null){
				selectedTitle.setPadding(0, Utils.getStandardValue(mContext, 10), 0, Utils.getStandardValue(mContext, 10));
				selectedTitle.setTextSize(25);
			}
			title_xuanji.setPadding(0, 0, 0, 0);
			title_xuanji.setTextSize(35);
			selectedTitle = title_xuanji;
			break;
		case 1:
			if(selectedTitle!=null){
				selectedTitle.setPadding(0, Utils.getStandardValue(mContext, 10), 0, Utils.getStandardValue(mContext, 10));
				selectedTitle.setTextSize(25);
			}
			title_zimu.setPadding(0, 0, 0, 0);
			title_zimu.setTextSize(35);
			selectedTitle = title_zimu;
			break;
		case 2:
			if(selectedTitle!=null){
				selectedTitle.setPadding(0, Utils.getStandardValue(mContext, 10), 0, Utils.getStandardValue(mContext, 10));
				selectedTitle.setTextSize(25);
			}
			
			title_definition.setPadding(0, 0, 0, 0);
			title_definition.setTextSize(35);
			selectedTitle = title_definition;
			break;
		case 3:
			if(selectedTitle!=null){
				selectedTitle.setPadding(0, Utils.getStandardValue(mContext, 10), 0, Utils.getStandardValue(mContext, 10));
				selectedTitle.setTextSize(25);
			}
			title_size.setPadding(0, 0, 0, 0);
			title_size.setTextSize(35);
			selectedTitle = title_size;
			break;
		}
	}

	public void init() {
		// TODO Auto-generated method stub
		initLists();
//		if(findViewById(R.id.title_definition)!=null){
//			initView();
//		}
	}
	
	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		initView();
	}
	
	private void initLists(){
		
		selectionPosions.clear();
		
		list_juji = mContext.getEpisode();
		if(list_juji!=null){
			int prod_Sub_index = list_juji.indexOf(mContext.getCurrentProdSubName());
			if(prod_Sub_index<0){
				selectionPosions.put(0, 0);
			}else{
				selectionPosions.put(0, prod_Sub_index);
			}
		}else{
			selectionPosions.put(0, 0);
		}
		
		list_definition = new ArrayList<Integer>();
		list_zimu = new ArrayList<String>();
		//清晰度
		List<URLS_INDEX> playUrls = mContext.getPlayUrls();
		JoyplusSubManager subManager = (JoyplusSubManager) JoyplusMediaPlayerManager.getInstance().getSubManager();
		if(playUrls!=null){
			for(URLS_INDEX url_index_info:playUrls){
				if("hd2".equalsIgnoreCase(url_index_info.defination_from_server)&&!list_definition.contains(Constant.DEFINATION_HD2)){
					list_definition.add(Constant.DEFINATION_HD2);
				}else if("hd".equalsIgnoreCase(url_index_info.defination_from_server)&&!list_definition.contains(Constant.DEFINATION_HD)){
					list_definition.add(Constant.DEFINATION_HD);
				}else if("mp4".equalsIgnoreCase(url_index_info.defination_from_server)&&!list_definition.contains(Constant.DEFINATION_MP4)){
					list_definition.add(Constant.DEFINATION_MP4);
				}else if(!list_definition.contains(Constant.DEFINATION_FLV)){
					list_definition.add(Constant.DEFINATION_FLV);
				}
			}
		}
		if(list_definition.size()>1){
			Collections.sort(list_definition, new Comparator<Integer>(){

				@Override
				public int compare(Integer l, Integer r) {
					// TODO Auto-generated method stub.
					if(l>r){
						return -1;
					}else if(r<l){
						return 1;
					}else{ 
						return 0;
					}
				}
			});
		}
		int defanition_index = list_definition.indexOf(mContext.getCurrentDefination());
		if(defanition_index<0){
			defanition_index = 0;
		}
		selectionPosions.put(2 , defanition_index);
		//字幕
		if(subManager!=null){
			if(subManager.CheckSubAviable()){
				list_zimu.add(mContext.getResources().getString(R.string.meidaplayer_sub_string_closesub));
            	for(int i = 0;i<subManager.getSubList().size();i++){
            		list_zimu.add(mContext.getResources().getString(R.string.meidaplayer_sub_string_sub,(i+1)));
            	}
            	Log.i(TAG, "subManager.getCurrentSubIndex()+1--->" + (subManager.getCurrentSubIndex()+1));
            	if(subManager.IsSubEnable()){
            		selectionPosions.put(1, subManager.getCurrentSubIndex()+1);
            	}else {
            		selectionPosions.put(1, 0);
            	}
            }else{
            	list_zimu.add(mContext.getResources().getString(R.string.meidaplayer_sub_string_nosub));
            	selectionPosions.put(1, 0);
            }
		}
		list_size = new ArrayList<Integer>();
		list_size.add(JoyplusMediaPlayerScreenManager.LINEARLAYOUT_PARAMS_FULL);
		list_size.add(JoyplusMediaPlayerScreenManager.LINEARLAYOUT_PARAMS_16x9);
		list_size.add(JoyplusMediaPlayerScreenManager.LINEARLAYOUT_PARAMS_4x3);
//		list_size.add(JoyplusMediaPlayerScreenManager.LINEARLAYOUT_PARAMS_ORIGINAL);
		Log.d(TAG, "size type -- >" + mContext.getVideoSizeType());
		int index_size = list_size.indexOf(mContext.getVideoSizeType());
		if(index_size<0){
			index_size = 0;
		}
		selectionPosions.put(3 , index_size);
		Log.d(TAG, "0----->"+selectionPosions.get(0));
		Log.d(TAG, "1----->"+selectionPosions.get(1));
		Log.d(TAG, "2----->"+selectionPosions.get(2));
		Log.d(TAG, "3----->"+selectionPosions.get(3));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch (title_selecet_index) {
		case 0://切换剧集
			String newPord_sub_name = list_juji.get(position);
			if(!newPord_sub_name.equals(mContext.getCurrentProdSubName())){
				//通知改变剧集
				mContext.changeEpisode(position);
			}
			break;
		case 1://切换字幕
			JoyplusSubManager subManager = (JoyplusSubManager) JoyplusMediaPlayerManager.getInstance().getSubManager();
			if(subManager!=null&&subManager.CheckSubAviable()){
				if(position==0){
					if(subManager.IsSubEnable()){
						subManager.setSubEnable(false);
					}
				}else{
					if(subManager.getCurrentSubIndex()!=(position-1)){
						subManager.setSubEnable(true);
						final int zimu_index = position-1;
						new Thread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								((JoyplusSubManager)JoyplusMediaPlayerManager.getInstance().getSubManager()).SwitchSub(zimu_index);
							}
						}).start();
					}
				}
			}
			break;
		case 2://切换清晰度
			mContext.changeDefination(list_definition.get(position));
			break;
		case 3://切换画面比例
			break;
		default:
			break;
		}
		dismiss();
	}
}
