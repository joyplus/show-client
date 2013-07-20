package com.joyplus.tvhelper.ui;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.AdapterView;

public class GridSwitcherView extends ViewPager implements AdapterView.OnItemSelectedListener{
	
	public static final int ROW_1 = 1;
	public static final int ROW_2 = 2;
	
	public static final int NUM_CLOUMNS_4 = 4;
	public static final int NUM_CLOUMNS_5 = 5;
	
	private Context context;
	private ArrayList<CustomGridView> gridViewList = new ArrayList<CustomGridView>();
	
	private int pageSizes;
	private int row;
	private int numCloumns = NUM_CLOUMNS_4;
	
	private BaseAdapter gridAdapter;

	public GridSwitcherView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		this.context = context;
		setVisibility(View.GONE);
		
		row = ROW_2;
	}

	public CustomGridView getCustomGridView(int index) {
		
		if(gridViewList != null && gridViewList.size() > index) {
			
			return gridViewList.get(index);
		}
		
		return null;
	}
	
	/**
	 * 1 or 2
	 * @param row
	 */
	public void setRows(int row){
		
		this.row = row;
	}
	
	public int getRows() {
		
		return row;
	}
	
	public void initGridSwitcherView(int viewSizes,int numCloumns){
		
		this.numCloumns = numCloumns;
		
		setVisibility(View.VISIBLE);
		if(viewSizes > 0) {
			
			if(viewSizes % (numCloumns * row) == 0) {
				
				pageSizes = viewSizes / (numCloumns * row);
			} else {
				
				pageSizes = viewSizes / (numCloumns * row) + 1;
			}
			
			removeAllViewsInLayout();
			
			if(gridViewList != null) {
				
				gridViewList.clear();
			}
			
			for(int i=0;i<pageSizes;i++) {
				
				CustomGridView customGridView = new CustomGridView(context);
				customGridView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
				customGridView.setOnItemSelectedListener(this);
				customGridView.setHorizontalSpacing(20);
				customGridView.setVerticalSpacing(20);
				customGridView.setNumColumns(numCloumns);
				customGridView.setGravity(Gravity.CENTER);
				gridViewList.add(customGridView);
			}
			
			setCurrentItem(0);
			setAdapter(pagerAdapter);
			setOnPageChangeListener(pageChangeListener);
		}
	}
	
	public int getNumCloumns() {
		
		return numCloumns;
	}
	
	public void setGridAdapter(BaseAdapter adapter) {
		
		gridAdapter = adapter;
		for(int i=0;i<pageSizes;i++) {
			
			gridViewList.get(i).setAdapter(adapter);
		}
		
	}
	
	public BaseAdapter getGridAdapter(){
		
		return gridAdapter;
	}
	
	private PagerAdapter pagerAdapter = new PagerAdapter() {
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
//			return super.instantiateItem(container, position);
			container.addView(gridViewList.get(position));
			
			return gridViewList.get(position);
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
//			super.destroyItem(container, position, object);
			container.removeView(gridViewList.get(position));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return gridViewList.size();
		}
	};
	
	private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}
	};

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
	
	public void setOnFocusChangeListener(AdapterView.OnItemClickListener itemClickListener) {
		
		for(int i=0;i<pageSizes;i++) {
			
			gridViewList.get(i).setOnItemClickListener(itemClickListener);
		}
	}
	
	public void setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener){
		
		Iterator<CustomGridView> iterator = gridViewList.iterator();
		while(iterator.hasNext()) {
			
			iterator.next().setOnItemClickListener(itemClickListener);
		}
	}

}
