package com.joyplus.Sub;

import java.io.InputStream;

public interface JoyplusSubInterface {
	   /*Interface of sub type*/      
       public enum SubContentType{
    	   SUB_UNKNOW (0),
    	   SUB_SRT    (1),
    	   SUB_ASS    (2),
    	   SUB_SSA    (3),
    	   SUB_STL    (4),
    	   SUB_SCC    (5),
    	   SUB_MAX    (5);
    	   private int Type;
    	   SubContentType(int type){
    		   Type = type;
    	   }
    	   public int toInt(){
    		   return Type;
    	   }
       }
       
       public void parse(byte[] sub);
       
       public void parseLocal();
       
}
