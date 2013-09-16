package com.joyplus.tvhelper.utils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HttpTools {

	private static HttpClient mHttpClient;

	public static final String NETWORK_NOT_AVAILABLE = "network_not_available";
	public static final String SERVER_ERR = "server_err";
	public static final String TIME_OUT = "time_out";

	public final static int NETWORK_STATUS_NOT_AVAILABLE = 0;
	public final static int NETWORK_STATUS_IS_WIFI = 1;
	public final static int NETWORK_STATUS_IS_GPRS = 2;
	private static Header[] headers = new BasicHeader[11]; 
	
//	static { 
//        headers[0] = new BasicHeader("Appkey", ""); 
//        headers[1] = new BasicHeader("Udid", ""); 
//        headers[2] = new BasicHeader("Os", ""); 
//        headers[3] = new BasicHeader("Osversion", ""); 
//        headers[4] = new BasicHeader("Appversion", ""); 
//        headers[5] = new BasicHeader("Sourceid", ""); 
//        headers[6] = new BasicHeader("Ver", ""); 
//        headers[7] = new BasicHeader("Userid", ""); 
//        headers[8] = new BasicHeader("Usersession", ""); 
//        headers[9] = new BasicHeader("Unique", ""); 
//        headers[10] = new BasicHeader("Cookie", ""); 
// 
//    } 
	
	private HttpTools() {
	}
	
	private static int getNetworkStatus(Context context){
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if(info != null){
			if(cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()){
				return NETWORK_STATUS_IS_WIFI;
			}else if(cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()){
				return NETWORK_STATUS_IS_GPRS; // 2G/2.5G/3G
			}
		}
		return NETWORK_STATUS_NOT_AVAILABLE;
	}

	private static synchronized HttpClient getHttpClient() {
		if (null == mHttpClient) {
			HttpParams params = new BasicHttpParams();

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setHttpElementCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setUseExpectContinue(params, true);

			ConnManagerParams.setTimeout(params, 1000);
			HttpConnectionParams.setConnectionTimeout(params, 30000);
			HttpConnectionParams.setSoTimeout(params, 30000);

			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));

			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
					params, schReg);
			mHttpClient = new DefaultHttpClient(conMgr, params);
		}
		return mHttpClient;
	}

	public static String post(Context c, String url, Map<String, String> parmas) {
//		if(getNetworkStatus(c) == NETWORK_STATUS_NOT_AVAILABLE){
//			return NETWORK_NOT_AVAILABLE;
//		}
		String result = null;
		HttpPost reqeust = new HttpPost(url);
		HttpResponse res;
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();   
		if(parmas != null){   
			Set<String> keys = parmas.keySet();   
			for(Iterator<String> i = keys.iterator(); i.hasNext();) {   
				String key = (String)i.next();   
				pairs.add(new BasicNameValuePair(key, parmas.get(key)));   
			}   
		} 
		try {
//			Map<String, String> headers = new HashMap<String, String>();
//			headers.put("User-Agent",
//					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
//			PackageInfo pInfo;
//			try {
//				pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//				headers.put("version", pInfo.versionName);
//			} catch (NameNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		
//			headers.put("app_key", Constant.APPKEY);
//			headers.put("client","android");
			UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(pairs, "utf-8");
//			reqeust.setHeader("User-Agent",
//					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
//			reqeust.setHeaders(headers);
			reqeust.setHeader("app_key", Constant.APPKEY);
			String umeng_channel = null;
			ApplicationInfo info = null;
			try {
				info = c.getPackageManager().getApplicationInfo(c.getPackageName(),
				        PackageManager.GET_META_DATA);
				umeng_channel = info.metaData.getString("UMENG_CHANNEL");
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(umeng_channel==null){
				umeng_channel = "j001";
			}
			reqeust.setHeader("app_channel", umeng_channel);
			reqeust.setEntity(p_entity);
			res = getHttpClient().execute(reqeust);
			if (res != null
					&& res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity resEntity = res.getEntity();
				result = (resEntity == null) ? null : EntityUtils.toString(
						resEntity, "UTF-8");
			} else if (res != null
					&& res.getStatusLine().getStatusCode() / 100 == 3) {

			} else if (res != null
					&& String.valueOf(res.getStatusLine().getStatusCode())
							.startsWith("4")) {

			} else if (res != null
					&& String.valueOf(res.getStatusLine().getStatusCode())
							.startsWith("5")) {

			}
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			return TIME_OUT;
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			return TIME_OUT;
		} catch (IOException e1) {
			e1.printStackTrace();
			return SERVER_ERR;
		}
		if (result != null) {
			return result;
		} else {
			return SERVER_ERR;
		}
	}
	
	
	public static String get(Context c, String url) {
//		if(getNetworkStatus(c) == NETWORK_STATUS_NOT_AVAILABLE){
//			return NETWORK_NOT_AVAILABLE;
//		}
		String result = null;
		HttpResponse res;
		HttpGet reqeust = new HttpGet(url);
		reqeust.setHeader("app_key", Constant.APPKEY);
		String umeng_channel = null;
		ApplicationInfo info = null;
		try {
			info = c.getPackageManager().getApplicationInfo(c.getPackageName(),
			        PackageManager.GET_META_DATA);
			umeng_channel = info.metaData.getString("UMENG_CHANNEL");
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(umeng_channel==null){
			umeng_channel = "j001";
		}
		reqeust.setHeader("app_channel", umeng_channel);
		try {
			res = getHttpClient().execute(reqeust);
			if (res != null
					&& res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity resEntity = res.getEntity();
				result = (resEntity == null) ? null : EntityUtils.toString(
						resEntity, "UTF-8");
			} else if (res != null
					&& res.getStatusLine().getStatusCode() / 100 == 3) {

			} else if (res != null
					&& String.valueOf(res.getStatusLine().getStatusCode())
							.startsWith("4")) {

			} else if (res != null
					&& String.valueOf(res.getStatusLine().getStatusCode())
							.startsWith("5")) {

			}
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			return TIME_OUT;
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			return TIME_OUT;
		} catch (IOException e1) {
			e1.printStackTrace();
			return SERVER_ERR;
		}
		if (result != null) {
			return result;
		} else {
			return SERVER_ERR;
		}
	}
	
	public static boolean isNetConenct(){
		HttpGet reqeust = new HttpGet("http://www.baidu.com/");
		HttpResponse res;
		try{
			res = getHttpClient().execute(reqeust);
			if (res != null&& res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return true;
			}else{
				return false;
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
}
