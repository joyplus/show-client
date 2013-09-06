package org.blaznyoght.subtitles.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

public class Parser {
	private static final String SEP = "\r?\n";
	private static final String RANK_REGEX = "(\\d+)";
	private static final String TIME_REGEX = "(\\d{1,2}):(\\d{1,2}):(\\d{1,2})[,.](\\d{1,3})";
	private static final String TEXT_REGEX = "(.*?)^" + SEP;
	private static final String SRT_REGEX = RANK_REGEX + SEP + TIME_REGEX
			+ " --> " + TIME_REGEX + SEP + TEXT_REGEX;

	private String charset = "GBK";

	private final Collection collection = new Collection();
	private final Pattern pattern = Pattern.compile(SRT_REGEX,
			Pattern.MULTILINE | Pattern.DOTALL);

	public Parser() {
//		parse(f);
	}

//	public Parser(InputStream is) {
//		parse(is);
//	}
//
//	public Parser(String input) {
//		parse(input);
//	}

	public void parse(File f) {
		try {
			InputStream is;
			nsICharsetDetectionObserver observer = new nsICharsetDetectionObserver() {
				@Override
				public void Notify(String charset) {
					setCharset(charset);
				}
			};
			collection.setFile(f);
			nsDetector detector = new nsDetector();
			detector.Init(observer);
			is = new FileInputStream(f);
			int len = 0;
			byte[] buf = new byte[512];
			while ((len = is.read(buf, 0, buf.length)) > 0) {
				detector.DoIt(buf, len, false);
			}
			detector.DataEnd();
			is.close();
			is = new FileInputStream(f);
			parse(is);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void parse(InputStream is) {
		int len = 0;
		final char[] buffer = new char[512];
		final Writer writer = new StringWriter();
		try {
			InputStreamReader isr = new InputStreamReader(is, getCharset());
			final BufferedReader br = new BufferedReader(isr);
			while ((len = br.read(buffer)) > 0) {
				writer.write(buffer, 0, len);
			}
		} catch (Exception e) {
		} finally {
			try {
				is.close();
			} catch (Exception e) {
			}
		}
		parse(writer.toString());

	}

	public void parse(String input) {
		Matcher m = pattern.matcher(input);
		int match = 0;
		while (m.find()) {
			++match;
			Element e = getElement(m);
			if(collection.getElementSize() >= 1 ){
				if(e.getStartTime().getTime() !=
					collection.getElements().get(collection.getElementSize()-1)
					.getStartTime().getTime()){
					collection.getElements().add(e);
				}
			}else {
				collection.getElements().add(e);
			}
		}
		collection.setCharset(getCharset());
		java.util.Collections.sort(collection.getElements(), new SubTitleElementComparator());
	}

	public Collection getCollection() {
		return collection;
	}

	public Element getElement(Matcher matcher) {
		final Element e = new Element();
		e.setRank(Integer.parseInt(matcher.group(1)));
		{
			final long h = Long.parseLong(matcher.group(2));
			final long m = Long.parseLong(matcher.group(3));
			final long s = Long.parseLong(matcher.group(4));
			final long ms = Long.parseLong(matcher.group(5));
			e.setStartTime(new Time(h, m, s, ms));
		}
		{
			final long h = Long.parseLong(matcher.group(6));
			final long m = Long.parseLong(matcher.group(7));
			final long s = Long.parseLong(matcher.group(8));
			final long ms = Long.parseLong(matcher.group(9));
			e.setEndTime(new Time(h, m, s, ms));
		}
		e.setText(matcher.group(10).replaceAll("<.*>", "").trim().replaceAll("\\\\N", "\n"));
		return e;
	}

	public void updateCollection(Collection subtitles) {
		synchronized (subtitles) {
			// subtitles.getElements().clear();
			subtitles.getElements().addAll(getCollection().getElements());
		}
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @param charset
	 *            the charset to set
	 */
	public void setCharset(String charset) {
		System.out.println(charset);
		this.charset = charset;
	}
}
