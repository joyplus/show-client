package org.blaznyoght.subtitles.model;

import java.util.Comparator;

public class SubTitleElementComparator implements Comparator<Element> {

	@Override
	public int compare(Element lhs, Element rhs) {
		// TODO Auto-generated method stub
		long lhsStartTime = lhs.getStartTime().getTime();
		long rhsStartTime = rhs.getStartTime().getTime();
		
		if(lhsStartTime - rhsStartTime > 0 ){
			return 1;
		}else if(lhsStartTime - rhsStartTime < 0 ){
			return -1;
		}
		
		return 0;
	}

}
