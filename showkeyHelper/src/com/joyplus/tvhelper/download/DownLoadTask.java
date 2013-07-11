package com.joyplus.tvhelper.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.joyplus.tvhelper.db.DBServices;
import com.joyplus.tvhelper.db.PushedApkDownLoadInfo;
import com.joyplus.tvhelper.faye.FayeService;

public class DownLoadTask implements Runnable{

		private String url;
		private boolean isStop = false;
		private int start;
		private Context context;
		private DBServices service;
		private PushedApkDownLoadInfo info;
		private Handler handler;
		private static final String TAG = "DownLoadTask";
		
		public void stopLoad(){
			isStop = true;
		}
		
		
		public DownLoadTask(Context c,PushedApkDownLoadInfo info, Handler handler) {
			super();
			this.url = info.getUrl();
			this.start = info.getCompeleteSize();
			this.context = c;
			this.handler = handler;
			this.info = info;
			service = DBServices.getInstance(context);
		}



		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.d(TAG, "down load tast thred id ********************************" + Thread.currentThread().getId());
			URL m;
			InputStream i = null;
			FileOutputStream out = null;
			
			String filename = getFilenameFromUrl(url);
			File dir = new File(Environment.getExternalStorageDirectory(), "showkey");
			if(!dir.exists()){
				dir.mkdirs();
			}
			File f = new File(dir, filename);
				Log.d(TAG, f.getAbsolutePath());
				info.setFile_path(f.getAbsolutePath());
			if(!f.exists()){
				try {
					f.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				info.setFileSize(0);
				info.setCompeleteSize(0);
				info.setDownload_state(0);
//				service.saveApkInfo(info);
			}
			start = info.getCompeleteSize();
			Message msg_file = new Message();
			msg_file.what = FayeService.MESSAGE_DOWNLOAD_CREAT_FILE_SUCCESS;
			msg_file.arg1 = info.getPush_id();
			 handler.sendMessage(msg_file);
			int retry = 0;

			do{
				try{
					String urlString = url + "?" + System.currentTimeMillis();
					m = new URL(Uri.encode(urlString,"UTF-8").replaceAll("%3A", ":").replaceAll("%2F", "/").replaceAll("%3F", "?"));
					HttpURLConnection connection = (HttpURLConnection) m.openConnection();
					connection.setDoInput(true);
					if(info.getFileSize()==0){
						connection.connect();
						i =  connection.getInputStream();
						info.setFileSize(connection.getContentLength());
						info.setDownload_state(1);
						Message msg = new Message();
						 msg.what = FayeService.MESSAGE_DOWNLOAD_GET_FILESIE_SUCCESS;
						 msg.arg1 = info.getPush_id();
						 handler.sendMessage(msg);
						service.updatePushedApkInfo(info);
						out = new FileOutputStream(f);
						byte[] b = new byte[4096];
					    int completeSize = 0;
					    int len = 0;
					    while ((len = i.read(b, 0, 4096)) != -1&&!isStop){
						   out.write(b, 0, len);
						   out.flush();
						   completeSize += len;
						   info.setCompeleteSize(completeSize);
						   service.updatePushedApkInfo(info);
						   Message msg1 = new Message();
						   msg1.what = FayeService.MESSAGE_DOWNLOAD_PROGRESS_CHANGED;
						   msg1.arg1 = info.getPush_id();
						   msg1.arg2 = completeSize;
						   handler.sendMessage(msg1);
						   service.updatePushedApkInfo(info);
//						   Message msg = new Message();
//						   msg.what = 0;
//						   msg.arg1 = (completeSize*100)/info.getFileSize();
//						   hanler.sendMessage(msg);
					    }
					    out.flush();
					    out.close();
					}else{
						connection.setRequestProperty("Range", "bytes=" + start + "-" + (info.getFileSize() - 1));
						connection.connect();
						i =  connection.getInputStream();
						RandomAccessFile randomAccessFile = new RandomAccessFile(f.getAbsolutePath(), "rwd");
						randomAccessFile.seek(start);
						info.setDownload_state(1);
						byte[] b = new byte[4096];
					    int completeSize = start;
					    int len = 0;
					    while ((len = i.read(b, 0, 4096)) != -1&&!isStop){
				    	   randomAccessFile.write(b, 0, len);
						   completeSize += len;
						   info.setCompeleteSize(completeSize);
						   service.updatePushedApkInfo(info);
						   Message msg = new Message();
						   msg.what = FayeService.MESSAGE_DOWNLOAD_PROGRESS_CHANGED;
						   msg.arg1 = info.getPush_id();
						   msg.arg2 = completeSize;
						   handler.sendMessage(msg);
					    }
					}
					Log.d(TAG, "retry--------->" + retry);
					retry = 5;
				    i.close();
				}catch (FileNotFoundException e) {
					// TODO: handle exception
					e.printStackTrace();
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					retry += 1;
					if(retry==5){
						Message msg = new Message();
						msg.what = FayeService.MESSAGE_DOWNLOAD_FAILE;
						msg.arg1 = info.getPush_id();
						handler.sendMessage(msg);
						return;
					}
				}catch (IOException e) {
					// TODO: handle exception
					e.printStackTrace();
					retry = 5;
					info.setDownload_state(0);
					service.updatePushedApkInfo(info);
					Message msg = new Message();
					msg.what = FayeService.MESSAGE_DOWNLOAD_FAILE;
					msg.arg1 = info.getPush_id();
					handler.sendMessage(msg);
				}
			}while(retry<5);
			if(info.getCompeleteSize()==info.getFileSize()){
		    	info.setDownload_state(3);
		    	service.updatePushedApkInfo(info);
		    	Message msg = new Message();
				msg.what = FayeService.MESSAGE_DOWNLOAD_COMPLETE;
				msg.arg1 = info.getPush_id();
				handler.sendMessage(msg);
//		    	service.updatePushedApkInfo(info);
//			    Log.d("TAG", info.getName()+"---------------download");
		    }else{
		    	
		    }
		}
	private String getFilenameFromUrl(String url){
		String [] strs = url.split("/");
		String filename = strs[strs.length - 1];
//		if(filename.contains(".")){
//			filename = filename.substring(0, filename.lastIndexOf("."));
//		}
		return filename;
	}
}