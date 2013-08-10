package org.blaznyoght.subtitles.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Collection {
	private File file;
	private String charset;
	
	private List<Element> elements = new ArrayList<Element>();

	public List<Element> getElements() {
		return elements;
	}
	
	public int getElementSize(){
		
		return elements.size();
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Element el : elements) {
			builder.append(el + "\n");
		}
		return builder.toString();
	}

	public void synchronize(final int startRank, final Time newStartTime,
			final int endRank, final Time newEndTime) {
		final Time oldStartTime = elements.get(startRank).getStartTime();
		final Time oldEndTime = elements.get(endRank).getEndTime();
		final long init = newStartTime.getTime() - oldStartTime.getTime();
		final double coef = (newStartTime.getTime() - oldStartTime.getTime())
				/ (newEndTime.getTime() - oldEndTime.getTime());
		for (int i = startRank; i <= endRank; ++i) {
			final Element e = elements.get(i);
			e.setStartTime(new Time(
					(long) (e.getStartTime().getTime() * coef + init)));
			e.setEndTime(new Time(
					(long) (e.getEndTime().getTime() * coef + init)));
		}
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	
}
