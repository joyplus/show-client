package com.joyplus.helper;

import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

import com.joyplus.https.HttpResult;

public class HttpClientHelper {

	private static String TAG = "HttpClientHelper";

	private static final int CONNECT_TIMEOUT = 5000;
	private static final int SO_TIMEOUT = 30000;

	private static DefaultHttpClient client = null;

	public static HttpResult get(String paramString) {

		return get(paramString, null, null, null, 30000);
	}

	public static HttpResult get(String paramString, Header[] paramArrayOfHeader) {

		return get(paramString, paramArrayOfHeader, null, null, SO_TIMEOUT);
	}

	public static HttpResult get(String paramString,
			Header[] paramArrayOfHeader,
			NameValuePair[] paramArrayOfNameValuePair) {

		return get(paramString, paramArrayOfHeader, paramArrayOfNameValuePair,
				null, SO_TIMEOUT);
	}

	public static HttpResult get(String paramString,
			NameValuePair[] paramArrayOfNameValuePair) {

		return get(paramString, null, paramArrayOfNameValuePair, null, SO_TIMEOUT);
	}

	public static HttpResult get(String paramString,
			Header[] paramArrayOfHeader,
			NameValuePair[] paramArrayOfNameValuePair,
			Cookie[] paramArrayOfCookie, int paramInt) {

		client = initHttpClient();
		client.getParams().setIntParameter("http.socket.timeout", paramInt);

		HttpGet localHttpGet = new HttpGet();
		client.setRedirectHandler(new DefaultRedirectHandler());

		StringBuilder localStringBuilder = null;
		HttpResult localHttpResult = null;
		
		try {

			if (paramArrayOfNameValuePair != null) {

				if (paramArrayOfNameValuePair.length > 0) {

					localStringBuilder = new StringBuilder("?");

					for (int i = 0; i < paramArrayOfNameValuePair.length; i++) {

						localStringBuilder.append("&");
						Object[] arrayOfObject = new Object[2];
						arrayOfObject[0] = paramArrayOfNameValuePair[i]
								.getName();
						arrayOfObject[1] = paramArrayOfNameValuePair[i]
								.getValue();
						localStringBuilder.append(String.format("%s=%s",
								arrayOfObject));
					}

					paramString = paramString + localStringBuilder.toString();
				}
			}
			
			Log.d(TAG, "get url=" + paramString);
			localHttpGet.setURI(new URI(paramString));

			if ((paramArrayOfHeader != null)
					&& (paramArrayOfHeader.length > 0)) {

				localHttpGet.setHeaders(paramArrayOfHeader);
			}

			if ((paramArrayOfCookie == null)
					|| (paramArrayOfCookie.length <= 0)) {

				client.getCookieStore().clear();
			} else {

				BasicCookieStore localBasicCookieStore = new BasicCookieStore();
				localBasicCookieStore.addCookies(paramArrayOfCookie);
				client.setCookieStore(localBasicCookieStore);
			}

			localHttpResult = new HttpResult(client.execute(localHttpGet),
					client.getCookieStore());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			localHttpGet.abort();
		}

		return localHttpResult;
	}

	public static String getResultRedirecUrl(String paramString,
			Header[] paramArrayOfHeader,
			NameValuePair[] paramArrayOfNameValuePair) {

		client.setRedirectHandler(new DynamicRedirectHandler());
		HttpGet localHttpGet = new HttpGet();

		try {

			localHttpGet.setURI(new URI(paramString));
			localHttpGet.setHeaders(paramArrayOfHeader);

			Header localHeader = client.execute(localHttpGet).getLastHeader(
					"Location");

			if (localHeader != null) {

				String str = getResultRedirecUrl(localHeader.getValue(),
						paramArrayOfHeader, paramArrayOfNameValuePair);
				paramString = str;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		return paramString;
	}

	public static DefaultHttpClient initHttpClient() {

		if (client == null) {

			BasicHttpParams localBasicHttpParams = new BasicHttpParams();
			HttpProtocolParams.setHttpElementCharset(localBasicHttpParams,
					"UTF-8");
			HttpProtocolParams.setVersion(localBasicHttpParams,
					HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(localBasicHttpParams, "UTF-8");
			HttpProtocolParams.setUseExpectContinue(localBasicHttpParams, true);
			HttpProtocolParams
					.setUserAgent(
							localBasicHttpParams,
							"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
			ConnManagerParams.setTimeout(localBasicHttpParams, 1000L);
			HttpConnectionParams.setConnectionTimeout(localBasicHttpParams,
					5000);
			HttpConnectionParams.setSoTimeout(localBasicHttpParams, 30000);

			SchemeRegistry localSchemeRegistry = new SchemeRegistry();
			localSchemeRegistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			localSchemeRegistry.register(new Scheme("https", SSLSocketFactory
					.getSocketFactory(), 443));

			client = new DefaultHttpClient(new ThreadSafeClientConnManager(
					localBasicHttpParams, localSchemeRegistry),
					localBasicHttpParams);
		}
		return client;
	}

	public static NameValuePair[] mapToPairs(Map<String, String> paramMap) {

		Set<String> localSet = paramMap.keySet();
		NameValuePair[] arrayOfNameValuePair = null;

		if ((localSet == null) || (localSet.size() <= 0)) {

			return null;
		}

		String[] arrayOfString = localSet.toArray(new String[0]);
		for (int i = 0; i < arrayOfString.length; i++) {

			arrayOfNameValuePair[i] = new BasicNameValuePair(arrayOfString[i],
					paramMap.get(arrayOfString[i]));
		}

		return arrayOfNameValuePair;

	}

	public static HttpResult post(String paramString,
			Header[] paramArrayOfHeader) {
		
		return post(paramString, paramArrayOfHeader, null, null, 30000);
	}

	public static HttpResult post(String paramString,
			Header[] paramArrayOfHeader,
			NameValuePair[] paramArrayOfNameValuePair) {
		
		return post(paramString, paramArrayOfHeader, paramArrayOfNameValuePair,
				null, 30000);
	}

	public static HttpResult post(String paramString,
			Header[] paramArrayOfHeader,
			NameValuePair[] paramArrayOfNameValuePair,
			Cookie[] paramArrayOfCookie) {
		
		return post(paramString, paramArrayOfHeader, paramArrayOfNameValuePair,
				paramArrayOfCookie, 30000);
	}

	public static HttpResult post(String paramString,
			NameValuePair[] paramArrayOfNameValuePair) {
		
		return post(paramString, null, paramArrayOfNameValuePair, null, 30000);
	}

	public static HttpResult post(String paramString,
			Header[] paramArrayOfHeader,
			NameValuePair[] paramArrayOfNameValuePair,
			Cookie[] paramArrayOfCookie, int paramInt) {
		
		Log.d(TAG, " post url=" + paramString);
		
		client = initHttpClient();
		client.getParams().setIntParameter("http.socket.timeout", paramInt);
		
		HttpPost localHttpPost = new HttpPost(paramString);
		
		HttpResult localHttpResult = null; 
		
		if (paramArrayOfNameValuePair != null) {
			
			try {
				if(paramArrayOfNameValuePair.length > 0) {
					
					ArrayList<NameValuePair> localArrayList = new ArrayList<NameValuePair>();
					
					for(int i=0;i<paramArrayOfNameValuePair.length;i++) {
						
						localArrayList.add(paramArrayOfNameValuePair[i]);
					}
					
					localHttpPost.setEntity(new UrlEncodedFormEntity(
							localArrayList, "UTF-8"));
				}
				
				if ((paramArrayOfHeader != null)
						&& (paramArrayOfHeader.length > 0)) {
					
					localHttpPost.setHeaders(paramArrayOfHeader);
				}
				
				if ((paramArrayOfCookie == null)
						|| (paramArrayOfCookie.length <= 0)) {
					
					client.getCookieStore().clear();
				} else {
					
					BasicCookieStore localBasicCookieStore = new BasicCookieStore();
					localBasicCookieStore.addCookies(paramArrayOfCookie);
					client.setCookieStore(localBasicCookieStore);
				}
				
				localHttpResult = new HttpResult(client.execute(localHttpPost),
						client.getCookieStore());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
			} finally {
				
				localHttpPost.abort();
			}
			
		}
		
		return localHttpResult;
	}

	static class DynamicRedirectHandler extends DefaultRedirectHandler {

		public boolean isRedirectRequested(HttpResponse paramHttpResponse,
				HttpContext paramHttpContext) {

			return false;
		}
	}

}
