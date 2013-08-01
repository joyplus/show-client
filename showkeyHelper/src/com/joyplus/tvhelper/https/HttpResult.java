package com.joyplus.tvhelper.https;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.util.EntityUtils;

import com.joyplus.tvhelper.utils.Log;

import android.text.TextUtils;

public class HttpResult {

	private static final String TAG = "HttpResult";

	private Cookie[] cookies;
	private Header[] headers;
	private byte[] response;
	private int statuCode = -1;

	public HttpResult(HttpResponse paramHttpResponse) {

		new HttpResult(paramHttpResponse, null);
	}

	public HttpResult(HttpResponse paramHttpResponse,
			CookieStore paramCookieStore) {

		if (paramCookieStore != null) {

			this.cookies = ((Cookie[]) paramCookieStore.getCookies().toArray(
					new Cookie[0]));
		}

		if (paramHttpResponse != null) {

			this.headers = paramHttpResponse.getAllHeaders();
			this.statuCode = paramHttpResponse.getStatusLine().getStatusCode();

			// System.out.println(this.statuCode);
			Log.i(TAG, "HttpResult--->" + statuCode);
		}

		try {

			this.response = EntityUtils.toByteArray(paramHttpResponse
					.getEntity());

		} catch (IOException e) {

			e.printStackTrace();

		}
	}

	public Cookie getCookie(String name) {

		if ((this.cookies == null) || (this.cookies.length == 0)) {

			return null;
		}
		for (int i = 0; cookies.length > 0; i++) {

			if (cookies[i] != null && cookies[i].getName() != null
					&& cookies[i].getName().equalsIgnoreCase(name)) {

				return cookies[i];
			}
		}

		return null;
	}

	public Cookie[] getCookies() {

		return this.cookies;
	}

	public Header getHeader(String name) {

		if ((this.headers == null) || (this.headers.length == 0)) {

			return null;
		}

		for (int i = 0; i < headers.length; i++) {

			if (headers[i] != null && headers[i].getName() != null
					&& headers[i].getName().equalsIgnoreCase(name)) {

				return headers[i];
			}
		}

		return null;
	}

	public Header[] getHeaders() {

		return this.headers;
	}

	public String getHtml() {

		return getText("UTF-8");
	}

	public String getHtml(String paramString) {

		return getText(paramString);
	}

	public byte[] getResponse() {
		
		if (this.response == null || this.response.length < 0) {

			return null;
		}

		byte[] arrayOfByte = Arrays.copyOf(this.response, this.response.length);

		return arrayOfByte;
	}

	public int getStatuCode() {
		
		return this.statuCode;
	}

	public String getText(String paramString) {
		
		if (this.response == null) {
			
			return null;
		}
		
		if(TextUtils.isEmpty(paramString)) {
			
			paramString = "utf-8";
		}
		
		try {
			
			String str = new String(this.response, paramString);
			return str;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			Log.e(TAG,e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}

	public String toString() {
		return "HttpResult [cookies=" + Arrays.toString(this.cookies)
				+ ", headers=" + Arrays.toString(this.headers) + ", response="
				+ getText("utf-8") + ", statuCode=" + this.statuCode + "]";
	}

}
