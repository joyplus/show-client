package com.joyplus.tvhelper.utils;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.joyplus.tvhelper.R;
import com.joyplus.tvhelper.entity.URLS_INDEX;

public class Utils {
	
	private static final String TAG = "Utils";
	
	public static void showToast(Context context,String str) {
		
		Toast toast = new Toast(context);
		View v = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
				inflate(R.layout.toast_textview, null);
		TextView tv = (TextView) v.findViewById(R.id.message);
		tv.setText(str);
		toast.setView(v);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
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
  
  public static String getMacAdd(Context c){
	  
	  String mac_add = getMac();
	  
	  if(mac_add == null){
		  mac_add = PreferencesUtils.getMac(c);
		  if(mac_add==null||mac_add.length()==0){
			  mac_add = "";
			  Random r = new Random();
			  for(int i=0; i<6; i++){
				  int num = r.nextInt(16*16);
				  if(i!=0){
					  mac_add += ":";
				  }
				  mac_add += Integer.toHexString(num);
			  }
			  PreferencesUtils.setMac(c, mac_add);
		  }
	  }
	  Log.d(TAG, "mac --->" + mac_add);
	  return mac_add;
  }
  
  private static String getMac(){
	  String mac = "";
		  try {
			  byte[] b = null;
			  b = NetworkInterface.getByInetAddress(getLocalIpAddress()).getHardwareAddress();
			  for(int i =0; i<b.length; i++){
				  if(i!=0){
					  mac += ":";
				  }
				  mac += Integer.toHexString(0xFF & b[i]);
			  }
//			  str = new String(b);
		  } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
		  
	 if(mac.length() == 0){
		 mac = null;
	 }
	 return mac;
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
		
		

	public static long getTotalSize4ListFiles(List<File> list){
		
		
		if(list != null && list.size() > 0){
			
			long filesSize = 0;
			for(File file:list){
				
				filesSize += file.length();
			}
			
			return filesSize;
		}
		return 0;
	}
	
	public static long getTotalSize4FileNames(String[] fileNames){
		
		if(fileNames != null && fileNames.length > 0){
			
			long fileSizes = 0;
			for(int i=0;i<fileNames.length;i++){
				
				String fileName = fileNames[i];
				if(fileName != null && !fileName.equals("")){
					
					fileSizes += getTotalSize4File(fileName);
				}
			}
			return fileSizes;
		}
		return 0;
	}
	
	public static List<File> getListFile4FileNames(File dir,String[] fileNames){
		
		List<File> list = new ArrayList<File>();
		if(fileNames != null && fileNames.length > 0){
			
			for(int i=0;i<fileNames.length;i++){
				
				String fileName = fileNames[i];
				if(fileName != null && !fileName.equals("")){
					
					File file = new File(dir,fileName);
					list.add(file);
				}
			}
		}
		
		return list;
	}
	
	public static void copyFile(File srcFile, File dstFile) {
		FileChannel src = null, dest = null;
		try {
			src = new FileInputStream(srcFile).getChannel();
			dest = new FileOutputStream(dstFile).getChannel();
			dest.transferFrom(src, 0, src.size());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if (src != null) {
					src.close();
				}
				if(dest != null){
					
					dest.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static String getFileNameforUrl(String url){
		String [] urls = url.split("\\?");
		url = urls[0];
		String [] strs = url.split("/");
		String filename = strs[strs.length - 1];
//		if(filename.contains(".")){
//			filename = filename.substring(0, filename.lastIndexOf("."));
//		}
		return System.currentTimeMillis() + Uri.decode(filename);
	}
	
	public static String getDisPlayFileNameforUrl(String url){
		String [] urls = url.split("\\?");
		url = urls[0];
		String [] strs = url.split("/");
		String filename = strs[strs.length - 1];
//		if(filename.contains(".")){
//			filename = filename.substring(0, filename.lastIndexOf("."));
//		}
		return filename;
	}
	
	public static String getUrl(String push_urls) throws Exception{
		push_urls = DesUtils.decode(Constant.DES_KEY, push_urls);
		Log.d(TAG, push_urls);
		String[] urls = push_urls.split("\\{mType\\}");
		List<URLS_INDEX> list = new ArrayList<URLS_INDEX>();
		for(String str : urls){
			URLS_INDEX url_index_info = new URLS_INDEX();
			String[] p = str.split("\\{m\\}");
			if(p.length<2){
				continue;
			}
			if("hd2".equalsIgnoreCase(p[0])){
				url_index_info.defination = 0;
			}else if("hd".equalsIgnoreCase(p[0])){
				url_index_info.defination = 1;
			}else if("mp4".equalsIgnoreCase(p[0])){
				url_index_info.defination = 2;
			}else{
				url_index_info.defination = 3;
			}
			url_index_info.url = p[1];
			list.add(url_index_info);
		}
		if(list.size()>1){
			Collections.sort(list, new DefinationComparatorIndex());
		}
		if(list.size()<=0){
			return  null;
		}else{
			return list.get(0).url;
		}
	}
	
	
	/**
	 * 安装安全支付服务，安装assets文件夹下的apk
	 * 
	 * @param context
	 *            上下文环境
	 * @param fileName
	 *            apk名称
	 * @param path
	 *            安装路径
	 * @return
	 */
	public static boolean retrieveApkFromAssets(Context context, String fileName) {
		boolean bRet = false;
		File cacheDir = context.getCacheDir();
		String path = cacheDir.getAbsolutePath() + "/temp.apk";
		File file = new File(path);
		try {
//			InputStream is = context.getAssets().open(fileName);
			Log.d(TAG, path);
			InputStream is = context.getAssets().open(fileName);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);

			byte[] temp = new byte[4096];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}
			fos.flush();
			fos.close();
			is.close();

			bRet = true;
			Log.d(TAG, "file move done");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		chmod("777", path);

		//
		// install the apk.
		// 安装安全支付服务APK
		try{
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.parse("file://" + path),
					"application/vnd.android.package-archive");
			Log.d(TAG, "file://" + path);
			context.startActivity(intent);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Toast.makeText(context, "您的设备暂不支持安装应用", Toast.LENGTH_LONG).show();
			((Activity)context).finish();
//			Intent intent = new Intent("com.tcl.packageinstaller.service.PackageInstallerService");
//			intent.putExtra("uri", Uri.fromFile(file).toString());
//			Log.d(TAG, Uri.fromFile(file).toString());
//
//			
////			Uri packageURI =Uri.parse("file://"+path);
//			
//			context.startService(intent);
		}
		return bRet;
	}
	
	/**
	 * 获取权限
	 * 
	 * @param permission
	 *            权限
	 * @param path
	 *            路径
	 */
	public static void chmod(String permission, String path) {
		try {
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    public static boolean isUTF_8(byte[] file){
        if(file.length<3)
            return false;
//        if((file[0]&0xFF)==0xEF && 
//                (file[1]&0xFF)==0xBB &&
//                (file[2]&0xFF)==0xBF)
        
        if (file[0] == -17 && file[1] == -69 && file[2] == -65) 
            return true;
        return false;
    }
    
    public static String getCharset(byte[] subTitle,int length){
    	
    	if(subTitle != null){
    		
    		if(subTitle.length < length){
    			
    			length = subTitle.length;
    		}
    		
    		ByteArrayInputStream in = new ByteArrayInputStream(subTitle);
    		
    		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
//    		detector.add(new ParsingDetector(false));
    		detector.add(JChardetFacade.getInstance());
    		try {
    			Charset charset = detector.detectCodepage(in, length);
    			
    			return charset!= null ? charset.name() : "";
    		} catch (IllegalArgumentException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
		
		return "";
    }
    
    public static String getBaiduName(String url){
    	String[] str = url.split("\\|");
		String name = null;
		if(str.length>=3){
			name = str[2];
		}
		return name;
    }
    
	public static int getStandardValue(Context context,int value){
		float standardDp = context.getResources().getDimension(R.dimen.standard_1_dp);
		return standardDp == 0 ? value:(int)(value * standardDp);
	}
}
