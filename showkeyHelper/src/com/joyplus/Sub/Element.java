package com.joyplus.Sub;



public class Element {
	public static final String TIME_SEP = " --> ";
	private int rank;
	private JoyplusSubTime startTime;
	private JoyplusSubTime endTime;
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
	public JoyplusSubTime getStartTime() {
		return startTime;
	}

	/**
	 * @param time the startTime to set
	 */
	public void setStartTime(JoyplusSubTime time) {
		this.startTime = time;
	}

	/**
	 * @return the endTime
	 */
	public JoyplusSubTime getEndTime() {
		return endTime;
	}

	/**
	 * @param time the endTime to set
	 */
	public void setEndTime(JoyplusSubTime time) {
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
