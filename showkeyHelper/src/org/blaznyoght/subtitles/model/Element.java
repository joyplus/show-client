package org.blaznyoght.subtitles.model;



public class Element {
	public static final String TIME_SEP = " --> ";
	private int rank;
	private Time startTime;
	private Time endTime;
	private String text;
	
	public Element() {}
	
	public Element(Element e){
		
		this.rank = e.rank;
		this.startTime = e.startTime;
		this.endTime = e.endTime;
		this.text = e.text;
	}
	
	
	/**
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}


	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}


	/**
	 * @return the startTime
	 */
	public Time getStartTime() {
		return startTime;
	}

	/**
	 * @param time the startTime to set
	 */
	public void setStartTime(Time time) {
		this.startTime = time;
	}

	/**
	 * @return the endTime
	 */
	public Time getEndTime() {
		return endTime;
	}

	/**
	 * @param time the endTime to set
	 */
	public void setEndTime(Time time) {
		this.endTime = time;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}


	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}


	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.valueOf(rank)+"\n");
		builder.append(startTime + TIME_SEP + endTime +"\n");
		builder.append(text);
		return builder.toString();
	}
}
