package com.joyplus.tvhelper.utils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;



import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.AndroidHttpClient;
import android.widget.Toast;

public class Utils {
	
	private static final String TAG = "Utils";
	
	public static void showToast(Context context,String str) {
		
		Toast.makeText(context, str, Toast.LENGTH_LONG).show();
	}
	
	public static String formatDuration(long duration) {
		duration = duration / 1000;
		int h = (int) duration / 3600;
		int m = (int) (duration - h * 3600) / 60;
		int s = (int) duration - (h * 3600 + m * 60);
		String durationValue;
		// if (h == 0) {
		// durationValue = String.format("%1$02d:%2$02d", m, s);
		// } else {
		durationValue = String.format("%1$02d:%2$02d:%3$02d", h, m, s);
		// }
		return durationValue;
	}
	
	public static void recycleBitmap(Bitmap bitmap) {
		
		if(bitmap != null) {
			
			if(!bitmap.isRecycled()) {
				
				bitmap.recycle();
			}
			
			bitmap = null;
		}
	}
	
	//把字节换算成M
	public static String byte2Mbyte(String byteStr) {
		
		if(byteStr != null && !byteStr.equals("")
				&&!byteStr.equals("null")) {
			
			long fileSize = -1l;
			try {
				fileSize = Long.valueOf(byteStr);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(fileSize > 0) {
				
				float tempLong = fileSize/(1024 * 1.0f);
				if(tempLong < 1024) {
					
					return String.format("%.2f", tempLong) + "KB";
				} else {
					
					tempLong = tempLong/(1024 * 1.0f);
					
					if(tempLong < 1024) {
						
						return String.format("%.2f", tempLong) + "M";
					} else {
						
						float tempFloat = tempLong/(1024 * 1.0f);
						
						return String.format("%.2f", tempFloat) + "G";
					}
				}
			}
		}
		
		return "未知";
	}
	
	public static String setFileSize(long paramLong) {
		DecimalFormat localDecimalFormat = new DecimalFormat("###.##");
		float f = (float) paramLong / 1048576.0F;
		
		if (f < 1.0D)
			return localDecimalFormat.format(new Float(
					(float) paramLong / 1024.0F).doubleValue()) + "KB";
		
		if ((f >= 1.0D) && (f < 1024.0D))
			return localDecimalFormat.format(new Float(f).doubleValue()) + "M";
		
		return localDecimalFormat.format(new Float(f / 1024.0F).doubleValue())
				+ "G";
	}
	
	public  static boolean isSame4Str(String str1, String str2){
		if(str1==null||str2==null){
			return false;
		}
		if(str1.equalsIgnoreCase(str2)){
			return true;
		}else{
			if(str1.trim().equalsIgnoreCase(str2.trim())){
				return true;
			}else{
				if(str1.length()>=str2.length()){
					if(str1.startsWith(str2)){
						return true;
					}else{
						return false;
					}
				}else{
					if(str2.startsWith(str1)){
						return true;
					}else{
						return false;
					}
				}
			}
		}
	}
	
	
public static InetAddress getLocalIpAddress(){
		
		try{
			 for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				 NetworkInterface intf = en.nextElement();  
	                for (Enumeration<InetAddress> enumIpAddr = intf  
	                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {  
	                    InetAddress inetAddress = enumIpAddr.nextElement();  
	                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {  
	                    	return inetAddress;  
	                    }  
	                }  
			 }
		}catch (SocketException e) {
			// TODO: handle exception
			Log.e("TAG","WifiPreference IpAddress---error-" + e.toString());
		}
		
		return null; 
	}
  
  public static String getMacAdd(){
	  String  str = "";
	  try {
		  byte[] b = null;
		  b = NetworkInterface.getByInetAddress(getLocalIpAddress()).getHardwareAddress();
		  for(int i =0; i<b.length; i++){
			  if(i!=0){
				  str += ":";
			  }
			  str += Integer.toHexString(0xFF & b[i]);
		  }
//		  str = new String(b);
	} catch (SocketException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
	  return str;
  }
  
	public static String getFilenameFromUrl(String url){
		
		String [] strs = url.split("/");
		String filename = strs[strs.length - 1];
		return filename;
	}
	
	public static long getTotalSize4File(String fileName){
		
		File dir = new File(fileName);
		if(dir.exists() && dir.isDirectory()){
			
			File[] files = dir.listFiles();
			long filesSize = 0;
			for(int k=0;k<files.length;k++){
				
				filesSize= files[k].length() + filesSize;
			}
			
			return filesSize;
		}
		
		return dir.length();
	}
	
	/**\
	 * 不要放在主线程里面
	 * @return
	 */
	public static String getRedirectUrl(String url){
		String urlStr = null;
//		while(urlStr == null) {
			
		List<String> list = new ArrayList<String>();
		
		try {
			urlRedirect(url,list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//超时异常
		}
		if(list.size() > 0) {
			 urlStr = list.get(list.size() -1);
		}
//		}
		return urlStr;
	}
	
	private static void urlRedirect(String urlStr,List<String> list) {
		
		// 模拟火狐ios发用请求 使用userAgent
		AndroidHttpClient mAndroidHttpClient = AndroidHttpClient
				.newInstance(Constant.USER_AGENT_IOS);

		HttpParams httpParams = mAndroidHttpClient.getParams();
		// 连接时间最长5秒，可以更改
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000 * 1);
		
		URL url;
		try {
			url = new URL(urlStr);
//			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(),null);
//			HttpGet mHttpGet = new HttpGet(uri);
			HttpGet mHttpGet = new HttpGet(url.toURI());
			HttpResponse response = mAndroidHttpClient.execute(mHttpGet);
			StatusLine statusLine = response.getStatusLine();
			
			int status = statusLine.getStatusCode();
			Log.i(TAG, "HTTP STATUS : " + status);
			
			if (status == HttpStatus.SC_OK) {
				Log.i(TAG, "HttpStatus.SC_OK--->" + urlStr);
				// 正确的话直接返回，不进行下面的步骤
				mAndroidHttpClient.close();
				list.add(urlStr);
				
				return;//后面不执行
			} else {
				
				Log.i(TAG, "NOT HttpStatus.SC_OK--->" + urlStr);
				
				if (status == HttpStatus.SC_MOVED_PERMANENTLY || // 网址被永久移除
						status == HttpStatus.SC_MOVED_TEMPORARILY || // 网址暂时性移除
						status == HttpStatus.SC_SEE_OTHER || // 重新定位资源
						status == HttpStatus.SC_TEMPORARY_REDIRECT) {// 暂时定向
					
					Header header = response.getFirstHeader("Location");// 拿到重新定位后的header
					
					if(header != null) {
						
						String location = header.getValue();// 从header重新取出信息
						Log.i(TAG, "Location: " + location);
						if(location != null && !location.equals("")) {
							
							urlRedirect(location, list);
							
							mAndroidHttpClient.close();// 关闭此次连接
							return;//后面不执行
						}
					}
					
					list.add(null);
					mAndroidHttpClient.close();
					
					return;

				} else {//地址真的不存在
					
					mAndroidHttpClient.close();
					list.add(null);
					
					return;//后面不执行
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	

}
