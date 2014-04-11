package com.joyplus.konka.update;

import java.io.File;
import java.net.URLDecoder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;

import com.joyplus.konka.luncher.R;
import com.joyplus.konka.utils.Log;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class UmengUpdate {

	private static final boolean UPDATE = true;
	
	private static final String TAG = UmengUpdate.class.getSimpleName();
	
	private static boolean isDialogShow = false;
	
	public static void update(final Activity context){
		
		if(!UPDATE){
			return;
		}
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setOnDownloadListener(null);
		UmengUpdateAgent.update(context);
		UmengUpdateAgent.setUpdateAutoPopup(false);

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
	            	final File f = new File(context.getCacheDir(), DownLoadUpdateApkThread.NAME_APK_DOWNLOADED);
	            	if(f.exists()){
	            		PackageInfo info = Utils.getAppPackageInfo(context, f.getAbsolutePath());
	            		if(info != null&&info.versionName!=null&&info.versionName.equals(updateInfo.version)&&!isDialogShow){
	            			AlertDialog.Builder builder = new Builder(context);
	            			  builder.setMessage(updateInfo.updateLog);

	            			  builder.setTitle(context.getString(R.string.update_discover_newVersion,updateInfo.version));

	            			  builder.setPositiveButton(context.getString(R.string.update_oK), new DialogInterface.OnClickListener() {

		            			   @Override
		            			   public void onClick(DialogInterface dialog, int which) {
		            				   dialog.dismiss();
		            				   isDialogShow = false;
		            				   try {
		            						Uri packageURI =Uri.parse("file://"+f.getAbsolutePath());
		            						Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE, packageURI);
		            						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            						context.startActivity(intent);
		            					} catch (Exception e) {
		            						// TODO: handle exception
		            						e.printStackTrace();
		            					}
		            			   }
	            			  });

	            			  builder.setNegativeButton(context.getString(R.string.update_cancel), new DialogInterface.OnClickListener() {

		            			   @Override
		            			   public void onClick(DialogInterface dialog, int which) {
		            			    dialog.dismiss();
		            			    isDialogShow = false;
		            			   }
	            			  });
	            			  builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
								
								@Override
								public void onCancel(DialogInterface dialog) {
									// TODO Auto-generated method stub
									dialog.dismiss();
		            			    isDialogShow = false;
								}
							});
	            			  builder.create().show();
	            			  isDialogShow = true;
	            		}else{
	            			new Thread(new DownLoadUpdateApkThread(context, URLDecoder.decode(updateInfo.path))).start();
	            		}
	            	}else{
	            		new Thread(new DownLoadUpdateApkThread(context, URLDecoder.decode(updateInfo.path))).start();
	            	}
//	                UmengUpdateAgent.showUpdateDialog(MainActivity.this, updateInfo);
	                break;
	            case 1: // has no update
	                break;
//	            case 2: // none wifi
//	                break;
	            case 3: // time out
	                break;
	            }
	        }
		});
	}
}
