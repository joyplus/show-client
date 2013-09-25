package com.joyplus.Sub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.joyplus.Sub.JoyplusSubInterface.SubContentType;
import com.joyplus.tvhelper.utils.Constant;

public class JoyplusSubServer {
	private static final String TAG = "JoyplusSubServer";

	private List<SubURI> SubUri = new ArrayList<SubURI>();
	
	private boolean    SubEnable = true;
	private JoyplusSub mSub;
    private Context mContext;
    private JoyplusSub getJoyplusSub(SubContentType type , SubURI uri){
    	if(type == SubContentType.SUB_ASS)return new ASSSub(uri);
    	else if(type == SubContentType.SUB_SCC)return new SCCSub(uri);
    	else if(type == SubContentType.SUB_SRT)return new SRTSub(uri);
    	else if(type == SubContentType.SUB_SSA)return new SSASub(uri);
    	else if(type == SubContentType.SUB_STL)return new STLSub(uri);
    	else return null;
    }
    public JoyplusSubServer(Context context){
    	mContext = context;
    }
	public void setSubUri(List<SubURI> subUri){
		 if(subUri==null || subUri.size()<=0)return;
		 SubUri = subUri;
		 CheckSubUriList();
	}
	public List<SubURI> getSubList(){
		return SubUri;
	}
    public boolean CheckSubAviable(){
    	if(mSub != null && mSub.getElements().size()>2)return true;
    	return false;
    }
    public JoyplusSub getJoyplusSub() throws Exception{
    	if(mSub != null)return mSub;
    	throw new Exception("JoyplusSub is null");
    }
	private void CheckSubUriList() {
		// TODO Auto-generated method stub
		Iterator<SubURI> it = SubUri.iterator();
		while(it.hasNext()){
			if(InstanceSub(it.next()))return;
			it.remove();
		}
		SubUri = new ArrayList<SubURI>();
		mSub   = null;
	}
	
	private boolean InstanceSub(SubURI uri){
		byte[] Subtitle = null;
		if(uri.SubType == SUBTYPE.NETWORK)
			Subtitle = getSubByte(uri.Uri);
		mSub = InstanceSub(uri,Subtitle);
		if(mSub != null){
			java.util.Collections.sort(mSub.elements, new SubTitleElementComparator());
			return true;
		}
		return false;
	}
	private JoyplusSub InstanceSub(SubURI uri,byte[] subtitle){
		JoyplusSub sub = null;
		if(uri.SubType == SUBTYPE.NETWORK && subtitle==null)return null;
		for(int i=1;i<=SubContentType.SUB_MAX.toInt();i++){
			sub = InstanceSub(JoyplusSub.getSubContentType(i),uri,subtitle);
			if(sub !=null)return sub;
		}
		return null;
	}
	private JoyplusSub InstanceSub(SubContentType type ,SubURI uri,byte[] subtitle){
		try{
			if(type.toInt()<=SubContentType.SUB_UNKNOW.toInt()
					||type.toInt()>SubContentType.SUB_MAX.toInt())return null;
			JoyplusSub sub = getJoyplusSub(type,uri);
			if(sub.getUri().SubType == SUBTYPE.NETWORK)
			      sub.parse(subtitle);
			else if(sub.getUri().SubType == SUBTYPE.LOCAL)
				  sub.parseLocal();
			if(sub.getElements().size()>2)return sub;
		}catch(ContentRestrictionException e){
		}
		return null;
	}
   private byte[] getSubByte(String url){		
		AjaxCallback<byte[]> cb = new AjaxCallback<byte[]>();
		cb.url(url).type(byte[].class);		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("app_key", Constant.APPKEY);
		cb.SetHeader(headers); 		
		(new AQuery(mContext)).sync(cb);
		byte[] subTitle = cb.getResult();
		return subTitle;
	}
   
    public void SwitchSub(int index){
    	if(index>=0 && index<SubUri.size()){
    		if(!InstanceSub(SubUri.get(index))){
    			SubUri.remove(index);
    			CheckSubUriList();
    		}
    	}
    }
    public boolean IsSubEnable(){
    	return SubEnable;
    }
    public void setSubEnable(boolean EN){
    	SubEnable = EN;
    }
    public int getCurrentSubIndex(){
    	if(mSub == null || !IsSubEnable())return -1;
    	return SubUri.indexOf(mSub.getUri());
    }
	public Element getElement(long time) {
		// TODO Auto-generated method stub
		if(mSub == null || !IsSubEnable()) return null;
		int start = 0;
		int end   = mSub.elements.size()-1;
		if(end<start || end==0)return null;
		if(time>mSub.elements.get(end).getStartTime().getTime())return null;
		while(start < end){			
			if(mSub.elements.get(getMiddle(start,end)).getStartTime().getTime()>time){
				end   = getMiddle(start,end);
			}else if(mSub.elements.get(getMiddle(start,end)).getStartTime().getTime()<time){
				start = getMiddle(start,end);
			}else if(mSub.elements.get(getMiddle(start,end)).getStartTime().getTime()==time){
				return mSub.elements.get(getMiddle(start,end));
			}
			if(start >end )return null;
			if(start == end ){
				if( mSub.elements.get(getMiddle(start,end)).getStartTime().getTime()<time 
						&& (getMiddle(start,end)+1)<mSub.elements.size()){	
					 return mSub.elements.get(getMiddle(start,end)+1);
				}else
					 return mSub.elements.get(getMiddle(start,end)); 
			}else if((end - start)==1){
				if(mSub.elements.get(end).getStartTime().getTime()<time){
					 if(end>=(mSub.elements.size()-1))end=mSub.elements.size()-2;
					 return mSub.elements.get(end+1);
				}else if(mSub.elements.get(start).getStartTime().getTime()<time)
					 return mSub.elements.get(end);
				else return mSub.elements.get(start);
			}
		}
		return null;
	}
	private int getMiddle(int index){
		if(index%2 != 0){
			index++;
		}
		if(index/2>=mSub.elements.size())return (mSub.elements.size()-1);
		return index/2;
	}
	private int getMiddle(int Start , int End){
		return getMiddle(Start+End);
	}
}
