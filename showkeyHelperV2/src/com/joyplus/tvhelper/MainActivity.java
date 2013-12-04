/****************************************************************************
Copyright (c) 2010-2012 cocos2d-x.org

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/
package com.joyplus.tvhelper;

import java.io.File;
import java.net.URLDecoder;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxHelper;

import com.joyplus.tvhelper.utils.DownLoadUpdateApkThread;
import com.joyplus.tvhelper.utils.Log;
import com.joyplus.tvhelper.utils.PackageUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;

public class MainActivity extends Cocos2dxActivity{
	
	private static final String TAG = "MainActivity";

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MobclickAgent.onError(this);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.update(this);
		MobclickAgent.setDebugMode(false);
		MobclickAgent.updateOnlineConfig(this);
		MobclickAgent.updateOnlineConfig(this);
		
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
	        @Override
	        public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
	            switch (updateStatus) {
	            case 0: // has update
	            case 2:
	            	Log.d(TAG, "hasUpdate---->" + updateInfo.hasUpdate);
	            	Log.d(TAG, "path ------>" + updateInfo.path);
	            	Log.d(TAG, "log---->" + updateInfo.updateLog);
	            	Log.d(TAG, "version---->" + updateInfo.version);
	            	final File f = new File(Cocos2dxHelper.getCocos2dxWritablePath(), DownLoadUpdateApkThread.NAME_APK_DOWNLOADED);
	            	if(f.exists()){
	            		PackageInfo info = PackageUtils.getAppPackageInfo(MainActivity.this, f.getAbsolutePath());
	            		if(info!=null){
	            			Log.d(TAG, "info.versionName = " + info.versionName);
	            		}
	            		if(info != null&&info.versionName!=null&&info.versionName.equals(updateInfo.version)){
	            			//Toast.makeText(MainActivity.this, "可以更新啦", Toast.LENGTH_SHORT).show();
	            			AlertDialog.Builder builder = new Builder(MainActivity.this);
	            			  builder.setMessage(updateInfo.updateLog);

	            			  builder.setTitle("发现新版本:" + updateInfo.version);

	            			  builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

		            			   @Override
		            			   public void onClick(DialogInterface dialog, int which) {
		            				   dialog.dismiss();
		            				   try {
		            						Uri packageURI =Uri.parse("file://"+f.getAbsolutePath());
		            						Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE, packageURI);
		            						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            						startActivity(intent);
		            					} catch (Exception e) {
		            						// TODO: handle exception
		            						e.printStackTrace();
		            					}
		            			   }
	            			  });

	            			  builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

		            			   @Override
		            			   public void onClick(DialogInterface dialog, int which) {
		            			    dialog.dismiss();
		            			   }
	            			  });

	            			  builder.create().show();
	            		}else{
	            			new Thread(new DownLoadUpdateApkThread(MainActivity.this, URLDecoder.decode(updateInfo.path))).start();
	            		}
	            	}else{
	            		new Thread(new DownLoadUpdateApkThread(MainActivity.this, URLDecoder.decode(updateInfo.path))).start();
	            	}
//	                UmengUpdateAgent.showUpdateDialog(MainActivity.this, updateInfo);
	                break;
	            case 1: // has no update
	                //Toast.makeText(MainActivity.this, "没有更新", Toast.LENGTH_SHORT).show();
	                break;
//	            case 2: // none wifi
	                //Toast.makeText(MainActivity.this, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
//	                break;
	            case 3: // time out
	                //Toast.makeText(MainActivity.this, "超时", Toast.LENGTH_SHORT).show();
	                break;
	            }
	        }
	});
	}
	
    static {
         System.loadLibrary("helper");
    }
}
