package com.joyplus.Sub;

import java.util.ArrayList;
import java.util.List;

import com.joyplus.Sub.JoyplusSubInterface.SubContentType;

public abstract class JoyplusSub implements JoyplusSubInterface{
    
	protected String           mTag;
	protected SubContentType   mContentType;
	
	private SubURI     mUri;
	public  SubURI     getUri(){
		return mUri;
	}
	protected List<Element> elements = new ArrayList<Element>();

	public List<Element> getElements() {
		return elements;
	}
	public static SubContentType getSubContentType(int type){
 	   switch(type){
 	   case 1: return SubContentType.SUB_SRT;
 	   case 2: return SubContentType.SUB_ASS;
 	   case 3: return SubContentType.SUB_SSA;
 	   case 4: return SubContentType.SUB_STL;
 	   case 5: return SubContentType.SUB_SCC;
 	   }
 	   return null;
    }
	public JoyplusSub(SubURI uri){
		mUri = uri;
		mContentType = SubContentType.SUB_UNKNOW;
	}
	
}
