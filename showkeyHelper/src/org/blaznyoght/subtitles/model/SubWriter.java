package org.blaznyoght.subtitles.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class SubWriter {
	private Collection collection;
	
	public SubWriter(Collection collection) {
		setCollection(collection);
		
	}
	public void write(Collection collection) {
		setCollection(collection);
		write();
	}
	
	public void write() {
		Collection collection = getCollection();
		String fileContent = collection.toString();
		Writer writer;
		try {
			writer = new OutputStreamWriter(
					new FileOutputStream(collection.getFile()), 
					collection.getCharset());
			writer.write(fileContent);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the collection
	 */
	public Collection getCollection() {
		return collection;
	}
	/**
	 * @param collection the collection to set
	 */
	public void setCollection(Collection collection) {
		this.collection = collection;
	}
}
