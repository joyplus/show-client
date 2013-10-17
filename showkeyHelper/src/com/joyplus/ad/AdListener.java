package com.joyplus.ad;

/*
 * Interface of AdBootScreenManager
 * Jas@20130711
 * 
 * */
public interface AdListener {

	// Interface of load advert file successed.
	public void adLoadSucceeded();

	// Interface of no Ad found.
	public void noAdFound();

	// interface of closed
	public void Closed();

}
