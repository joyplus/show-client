package org.blaznyoght.subtitles.model;

import java.util.Date;
import java.util.Scanner;
import java.util.regex.MatchResult;

public class Time {
	private long time;
	
	public Time(long time) {
		this.time = time;
	}
	
	public Time(long h, long m, long s, long ms) {
		setTime(getTimeAsLong(h, m, s, ms));
	}
	
	public Time(String time) {
		setTime(stringTimeToLong(time));
	}

	public Time(Date value) {
		this.time = value.getTime();
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}

	private String longToTime(long time) {
		return String.format(
				"%02d:%02d:%02d,%03d", 
				(time / 3600000),
				(time / 60000) % 60,
				(time / 1000) % 60,
				time % 1000);
	}
	
	private long stringTimeToLong(String time) {
		Scanner scanner = new Scanner(time);
		scanner.findInLine("(\\d+):(\\d+):(\\d+),(\\d+)");
		MatchResult result = scanner.match();
		int h = Integer.parseInt(result.group(0));
		int m = Integer.parseInt(result.group(1));
		int s = Integer.parseInt(result.group(2));
		int ms = Integer.parseInt(result.group(3));
		scanner.close();
		return getTimeAsLong(h, m, s, ms);
	}

	public long getTimeAsLong(long h, long m, long s, long ms) {
		return (h*3600000)+(m*60000)+(s*1000)+ms;
	}
	
	public String toString() {
		return longToTime(getTime());
	}
}
