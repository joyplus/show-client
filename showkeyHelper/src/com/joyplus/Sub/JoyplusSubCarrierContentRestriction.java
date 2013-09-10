package com.joyplus.Sub;

import com.joyplus.Sub.JoyplusSubInterface.SubContentType;

public class JoyplusSubCarrierContentRestriction implements JoyplusSubContentRestriction{
	private final static int MAXSIZE = 500*1024;
	@Override
	public void checkSubSize(long SubSize, long increaseSize) throws ContentRestrictionException {
		// TODO Auto-generated method stub
		if(SubSize<0 || increaseSize<0){
			throw new ContentRestrictionException();
		}
		if((SubSize+increaseSize)>MAXSIZE){
			throw new ContentRestrictionException();
		}
	}
	@Override
	public void checkUri(SubContentType type, String uri)
			throws ContentRestrictionException {
		// TODO Auto-generated method stub
		if(type == null || uri==null || type==SubContentType.SUB_UNKNOW){
			throw new ContentRestrictionException();
		}
		if(type == SubContentType.SUB_SRT){
			CheckSRTUri(uri);
		}
	}
	private void CheckSRTUri(String uri) {
		// TODO Auto-generated method stub
		if(uri == null || "".equals(uri))throw new ContentRestrictionException();
		if(uri.contains("scid=" ))return;
		if(uri.length() == uri.indexOf(".srt") + 4)return;
		throw new ContentRestrictionException();
	}

	
}
