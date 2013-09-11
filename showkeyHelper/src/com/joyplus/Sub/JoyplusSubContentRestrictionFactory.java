package com.joyplus.Sub;

public class JoyplusSubContentRestrictionFactory {
	  private static JoyplusSubContentRestriction sContentRestriction;

	    private JoyplusSubContentRestrictionFactory() {
	    }

	    public static JoyplusSubContentRestriction getContentRestriction() {
	        if (null == sContentRestriction) {
	            sContentRestriction = new JoyplusSubCarrierContentRestriction();
	        }
	        return sContentRestriction;
	    }
}
