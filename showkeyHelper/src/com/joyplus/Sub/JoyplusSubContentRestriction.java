package com.joyplus.Sub;

import com.joyplus.Sub.JoyplusSubInterface.SubContentType;

public interface JoyplusSubContentRestriction {
     
	void checkSubSize(long SubSize, long increaseSize) throws ContentRestrictionException;
	
	void checkUri(SubContentType type,String uri) throws ContentRestrictionException;
	
}
