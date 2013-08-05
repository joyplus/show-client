package com.joyplus.tvhelper.entity;

import java.io.Serializable;

public class VideoPlayUrl implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2475474330003142369L;
	
	public String playurl;
	public SharpnessEnum sharp;
	public boolean isCanDrag = true;

	public boolean equals(Object paramObject) {
		if ((paramObject instanceof VideoPlayUrl)) {
			VideoPlayUrl localVideoPlayUrl = (VideoPlayUrl) paramObject;
			if ((this.sharp.getIndex() == localVideoPlayUrl.sharp.getIndex())
					&& (this.playurl
							.equalsIgnoreCase(localVideoPlayUrl.playurl)))
				return true;
		}
		return false;
	}

	public String toString() {
		return "VideoPlayUrl [sharp=" + this.sharp + ", isCanDrag=" + isCanDrag +", playurl="
				+ this.playurl + "]";
	}
}
