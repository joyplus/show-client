package org.cocos2dx.lib;

import java.lang.reflect.Field;

import android.content.Context;

public class ResourcesManager {
	private static final String TAG = 
			ResourcesManager.class.getName();
	
	private static ResourcesManager mResourcesManager = null;
	
	private static Class drawable = null;
	private static Class layout = null;
	private static Class id = null;
	private static Class anim = null;
	private static Class style = null;
	private static Class string = null;
	private static Class array = null;
	private Context mContext;
	
	public ResourcesManager(Context context){
		mContext = context.getApplicationContext();
		
		try {
			drawable = Class.forName(mContext.getPackageName() + ".R$drawable");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		try {
			layout = Class.forName(mContext.getPackageName() + ".R$layout");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			id = Class.forName(mContext.getPackageName() + ".R$id");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		try {
			anim = Class.forName(mContext.getPackageName() + ".R$anim");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		try {
			style = Class.forName(mContext.getPackageName() + ".R$style");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		try {
			string = Class.forName(mContext.getPackageName() + ".R$string");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		try {
			array = Class.forName(mContext.getPackageName() + ".R$array");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
	
	public static ResourcesManager newInstance(Context context){
		if(mResourcesManager == null){
			mResourcesManager = new ResourcesManager(context);
		}
		return mResourcesManager;
	}
	
	public int getDrawableID(String name){
		return getID4Class(drawable, name);
	}
	public int getLayoutID(String name){
		return getID4Class(layout, name);
	}
	public int getIdID(String name){
		return getID4Class(id, name);
	}
	public int getAnimID(String name){
		return getID4Class(anim, name);
	}
	public int getStyleID(String name){
		return getID4Class(style, name);
	}
	public int getStringID(String name){
		return getID4Class(string, name);
	}
	public int getArrayID(String name){
		return getID4Class(array, name);
	}
	
	private int getID4Class(Class<?> paramClass,String name){
	    if (paramClass == null){
	      throw new IllegalArgumentException("ResClass is not initialized.");
	    }
	    try{
	      Field localField = paramClass.getField(name);
	      return localField.getInt(name);
	    } catch (Exception localException){
	    	localException.printStackTrace();
	    }
	    return -1;
	}
}
