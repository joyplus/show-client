package com.joyplus.Sub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class LocalSubParser {
	
	 
	 public TimedTextObject ParserFile(File subfile){
		    JoyplusSubContentRestrictionFactory.getContentRestriction().checkSubSize(0, subfile.length());
            TimedTextObject tto;		
			TimedTextFileFormat ttff;	
			//To test the correct implementation of the SRT parser and writer.
			if(subfile.getName().contains(".ass"))ttff = new FormatASS();
			else if(subfile.getName().contains(".srt"))ttff = new FormatSRT();
			else if(subfile.getName().contains(".scc"))ttff = new FormatSCC();
			else if(subfile.getName().contains(".ssa"))ttff = new FormatSCC();
			else if(subfile.getName().contains(".STL")
					||subfile.getName().contains(".stl") )ttff = new FormatSTL();
			else if(subfile.getName().contains(".xml"))ttff = new FormatTTML();
			else {
				return null;
			}
			InputStream is;
			try {
				is = new FileInputStream(subfile);
				
				tto = ttff.parseFile(subfile.getName(), is);
				return tto;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FatalParsingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
 }
}
