package com.joyplus.tvhelper.utils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.storage.StorageManager;
import android.util.DisplayMetrics;

import com.joyplus.tvhelper.entity.ApkInfo;

public class PackageUtils {
	
	public static final String TAG = "PackageUtils";
	
	
	public static ApplicationInfo getApplicationInfo(Context c, String apkPath){
		PackageManager pm = c.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
		if(info != null){ 
			 ApplicationInfo appInfo = info.applicationInfo;
			 return appInfo;
		}else{
			return null;
		}
	}
	
	public static PackageInfo getAppPackageInfo(Context c, String apkPath){
		PackageManager pm = c.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
		return info;
	}
	
	public static String getAppPackageName(Context c, String apkPath){
		PackageManager pm = c.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
		if(info != null){ 
			 return info.packageName;
		}else{
			return null;
		}
	}
	
	public static String getAppVersionName(Context c, String apkPath){
		PackageManager pm = c.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
		if(info != null){ 
			 return info.versionName;
		}else{
			return null;
		}
		
	}
	
	public static int getAppVersionCode(Context c, String apkPath){
		PackageManager pm = c.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
		if(info != null){ 
			 return info.versionCode;
		}else{
			return 0;
		}
		
	}
	
	public static Drawable getAppIcon(Context c, ApplicationInfo appInfo){
		PackageManager pm = c.getPackageManager();
		return pm.getApplicationIcon(appInfo);
	}
	
	public static String getAppName(Context c, ApplicationInfo appInfo){
		PackageManager pm = c.getPackageManager();
		return pm.getApplicationLabel(appInfo).toString();  
	}
	
	
//	public static boolean instatll(Context c, String pathFileApk, String packageName, IPackageInstallObserver observer) {
//	    int installFlags = 0;
//
//	    if (packageName != null)
//	      try {
//	        PackageInfo pi = c.getPackageManager().getPackageInfo(packageName, 
//	        		PackageManager.GET_UNINSTALLED_PACKAGES);
//	        if (pi != null) {
//	          installFlags |= 2;
//	          Log.i("PackInstaller", "Package already exsists, replace existing!!");
//	        }
//	      }
//	      catch (PackageManager.NameNotFoundException localNameNotFoundException)
//	      {
//	      }
//	    Uri mPackageURI = Uri.parse(pathFileApk);
//	    return installPackage(c, mPackageURI, observer, installFlags, packageName);
//	  }
//	
//	
//	private static boolean installPackage(Context c, Uri packageURI, IPackageInstallObserver observer, int flags, String installerPackageName) {
//	    try {
//	      Method method = c.getPackageManager().getClass().getMethod("installPackage", new Class[] { Uri.class, IPackageInstallObserver.class, Integer.TYPE, String.class });
//	      method.invoke(c.getPackageManager(), new Object[] { packageURI, observer, Integer.valueOf(flags), installerPackageName });
//	      return true;
//	    }
//	    catch (SecurityException e) {
//	      Log.e("PackInstaller", "No permission to invoke PackageManager.installPackage", e);
//	    } catch (NoSuchMethodException e) {
//	      Log.e("PackInstaller", "No such method: PackageManager.installPackage", e);
//	    } catch (IllegalArgumentException e) {
//	      Log.e("PackInstaller", "Illegal argument to invoke PackageManager.installPackage", e);
//	    } catch (IllegalAccessException e) {
//	      Log.e("PackInstaller", "Illegal access to invoke PackageManager.installPackage", e);
//	    } catch (InvocationTargetException e) {
//	      Log.e("PackInstaller", "Failed to invoke PackageManager.installPackage", e);
//	    }
//
//	    return false;
//	  }
	
	public static List<ApkInfo> getUsrInstalledApkInfos(Context context, List<ApkInfo> apps ){ 
		apps.clear();
		PackageManager pManager = context.getPackageManager();
		List<PackageInfo> paklist = pManager.getInstalledPackages(0); 
		for (int i = 0; i < paklist.size(); i++) { 
			PackageInfo pak = (PackageInfo) paklist.get(i); 
			//判断是否为非系统预装的应用程�?
			if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) { 
			// customs applications 
				ApkInfo apkInfo = new ApkInfo();
				apkInfo.setAppName(pManager.getApplicationLabel(pak.applicationInfo).toString());
				apkInfo.setPackageName(pak.packageName);
				apkInfo.setVision(pak.versionName);
				try {
					apkInfo.setDrawble(pManager.getApplicationIcon(pak.packageName));
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				apkInfo.setSize(size)
				apps.add(apkInfo); 
			} 
		} 
		return apps; 
	} 
	
	public static List<ApkInfo> getInstalledApkInfos(Context context) {

		List<ApkInfo> apps = new ArrayList<ApkInfo>();
		PackageManager pManager = context.getPackageManager();
		List<PackageInfo> paklist = pManager.getInstalledPackages(0);
		for (int i = 0; i < paklist.size(); i++) {
			PackageInfo pak = (PackageInfo) paklist.get(i);

			// customs applications
			ApkInfo apkInfo = new ApkInfo();
			apkInfo.setAppName(pManager.getApplicationLabel(pak.applicationInfo).toString());
			apkInfo.setPackageName(pak.packageName);
			apkInfo.setVision(pak.versionName);
			try {
				apkInfo.setDrawble(pManager.getApplicationIcon(pak.packageName));
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// apkInfo.setSize(size)
//			Log.d(TAG, "getInstalledApkInfos--->" + apkInfo.toString());
			apps.add(apkInfo);
		}
		return apps;
	}
	
	
	public static ApkInfo getUnInstalledApkInfo(Context c, String apkPath) {
		ApkInfo apkInfo;
		  String PATH_PackageParser = "android.content.pm.PackageParser";
		  String PATH_AssetManager = "android.content.res.AssetManager";
		  try {
			  apkInfo  = new ApkInfo();
			   // apk包的文件路径
			   // 这是�?��Package 解释�? 是隐藏的
			   // 构�?函数的参数只有一�? apk文件的路�?
			   // PackageParser packageParser = new PackageParser(apkPath);
			   Class pkgParserCls = Class.forName(PATH_PackageParser);
			   Class[] typeArgs = new Class[1];
			   typeArgs[0] = String.class;
			   Constructor pkgParserCt = pkgParserCls.getConstructor(typeArgs);
			   Object[] valueArgs = new Object[1];
			   valueArgs[0] = apkPath;
			   Object pkgParser = pkgParserCt.newInstance(valueArgs);
			   Log.d("ANDROID_LAB", "pkgParser:" + pkgParser.toString());
			   // 这个是与显示有关�? 里面涉及到一些像素显示等�? 我们使用默认的情�?
			   DisplayMetrics metrics = new DisplayMetrics();
			   metrics.setToDefaults();
			   // PackageParser.Package mPkgInfo = packageParser.parsePackage(new
			   // File(apkPath), apkPath,
			   // metrics, 0);
			   typeArgs = new Class[4];
			   typeArgs[0] = File.class;
			   typeArgs[1] = String.class;
			   typeArgs[2] = DisplayMetrics.class;
			   typeArgs[3] = Integer.TYPE;
			   Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod("parsePackage",
			     typeArgs);
			   valueArgs = new Object[4];
			   valueArgs[0] = new File(apkPath);
			   valueArgs[1] = apkPath;
			   valueArgs[2] = metrics;
			   valueArgs[3] = 0;
			   Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser, valueArgs);
			   // 应用程序信息�? 这个公开�? 不过有些函数, 变量没公�?
			   // ApplicationInfo info = mPkgInfo.applicationInfo;
			   Field appInfoFld = pkgParserPkg.getClass().getDeclaredField("applicationInfo");
			   ApplicationInfo info = (ApplicationInfo) appInfoFld.get(pkgParserPkg);
			   // uid 输出�?-1"，原因是未安装，系统未分配其Uid�?
			   Log.d("ANDROID_LAB", "pkg:" + info.packageName + " uid=" + info.uid);
			   apkInfo.setPackageName(info.packageName);
			   // Resources pRes = getResources();
			   // AssetManager assmgr = new AssetManager();
			   // assmgr.addAssetPath(apkPath);
			   // Resources res = new Resources(assmgr, pRes.getDisplayMetrics(),
			   // pRes.getConfiguration());
			   Class assetMagCls = Class.forName(PATH_AssetManager);
			   Constructor assetMagCt = assetMagCls.getConstructor((Class[]) null);
			   Object assetMag = assetMagCt.newInstance((Object[]) null);
			   typeArgs = new Class[1];
			   typeArgs[0] = String.class;
			   Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath",
			     typeArgs);
			   valueArgs = new Object[1];
			   valueArgs[0] = apkPath;
			   assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
			   Resources res = c.getResources();
			   typeArgs = new Class[3];
			   typeArgs[0] = assetMag.getClass();
			   typeArgs[1] = res.getDisplayMetrics().getClass();
			   typeArgs[2] = res.getConfiguration().getClass();
			   Constructor resCt = Resources.class.getConstructor(typeArgs);
			   valueArgs = new Object[3];
			   valueArgs[0] = assetMag;
			   valueArgs[1] = res.getDisplayMetrics();
			   valueArgs[2] = res.getConfiguration();
			   res = (Resources) resCt.newInstance(valueArgs);
			   CharSequence label = null;
			   if (info.labelRes != 0) {
			    label = res.getText(info.labelRes);
			   }
			   apkInfo.setAppName(label.toString());
			   // if (label == null) {
			   // label = (info.nonLocalizedLabel != null) ? info.nonLocalizedLabel
			   // : info.packageName;
			   // }
			   Log.d("ANDROID_LAB", "label=" + label);
			   // 这里就是读取�?��apk程序的图�?
			   if (info.icon != 0) {
			    Drawable icon = res.getDrawable(info.icon);
			    apkInfo.setDrawble(icon);
			   }
			   return apkInfo;
		  } catch (Exception e) {
			   e.printStackTrace();
			   return null;
		  }
		 }
	
	
	public static boolean isInstalled(Context c, String packageName,int versionCode) {
        List<PackageInfo> pakageinfos = c.getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo pi : pakageinfos) {
            String pi_packageName = pi.packageName;
//            int pi_versionCode = pi.versionCode;
            //如果这个包名在系统已经安装过的应用中存在
            if(packageName.endsWith(pi_packageName)&&versionCode==pi.versionCode){
            	return true;
//               if(versionCode==pi_versionCode){
//                   Log.i("test","已经安装，不用更新，可以卸载该应�?);
//                   return INSTALLED;
//               }else if(versionCode>pi_versionCode){
//                   Log.i("test","已经安装，有更新");  
//                   return INSTALLED_UPDATE;
//               }
            }
        }
        return false;
    }
	
	public static boolean isNeedInstalled(Context c, String packageName,int versionCode) {
        List<PackageInfo> pakageinfos = c.getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo pi : pakageinfos) {
            String pi_packageName = pi.packageName;
//            int pi_versionCode = pi.versionCode;
            //如果这个包名在系统已经安装过的应用中存在
            if(packageName.equalsIgnoreCase(pi_packageName)&&versionCode<=pi.versionCode){
            	return false;
//               if(versionCode==pi_versionCode){
//                   Log.i("test","已经安装，不用更新，可以卸载该应�?);
//                   return INSTALLED;
//               }else if(versionCode>pi_versionCode){
//                   Log.i("test","已经安装，有更新");  
//                   return INSTALLED_UPDATE;
//               }
            }
        }
        return true;
    }
	public static boolean isInstalled(Context c, String packageName) {
		List<PackageInfo> pakageinfos = c.getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for (PackageInfo pi : pakageinfos) {
			String pi_packageName = pi.packageName;
//            int pi_versionCode = pi.versionCode;
			//如果这个包名在系统已经安装过的应用中存在
			if(packageName.equalsIgnoreCase(pi_packageName)){
				return true;
//               if(versionCode==pi_versionCode){
//                   Log.i("test","已经安装，不用更新，可以卸载该应�?);
//                   return INSTALLED;
//               }else if(versionCode>pi_versionCode){
//                   Log.i("test","已经安装，有更新");  
//                   return INSTALLED_UPDATE;
//               }
			}
		}
		return false;
	}
	
	public static void getInstalledApkSize(Context c, String packageName, IPackageStatsObserver.Stub observer) throws Exception{
		PackageManager pm = c.getPackageManager();
		Method getPackageSizeInfo = pm.getClass().getMethod( "getPackageSizeInfo", String.class, IPackageStatsObserver.class);
		getPackageSizeInfo.invoke(pm, packageName,observer);
	}
	
	public static String fomartSize(long size){
		if(size>(1024*1024*1024)){
			double f = Double.valueOf(size)/(1024*1024*1024);
			DecimalFormat df=new DecimalFormat("#.##");
			return  df.format(f)+"GB";
		}else if(size>(1024*1024)){
			double f = Double.valueOf(size)/(1024*1024);
			DecimalFormat df=new DecimalFormat("#.##");
			return  df.format(f)+"MB";
		}else{
			double f = size/1024;
			return Math.round(f)+"KB";
		}
		
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static String getVolumePaths(Context c){
		StorageManager mStorageManager = (StorageManager)c.getSystemService(Context.STORAGE_SERVICE);
		try { 
            Method getPaths = mStorageManager.getClass().getMethod("getVolumePaths"); 
            String[] paths = (String[]) getPaths.invoke(mStorageManager);
            String path = "";
            for(int i =0; i <paths.length; i++){
            	path += paths[i];
            	path += "\t";
            	if(mStorageManager.isObbMounted(paths[i])){
            		path += "true";
            	}else{
            		path += "false";
            	}
            	path += "\n";
            }
            return path;
        } catch (Exception e) { 
            e.printStackTrace(); 
            return null;
        } 
	}
	
//	public static long getInstalledApkSize(Context c, String packageName) throws Exception{
//		PackageManager pm = c.getPackageManager();
//		Method getPackageSizeInfo = pm.getClass().getMethod( "getPackageSizeInfo", String.class, IPackageStatsObserver.class);
//		getPackageSizeInfo.invoke(pm, packageName,
//		    new IPackageStatsObserver.Stub() {
//
//				@Override
//				public void onGetStatsCompleted(PackageStats pStats,
//						boolean succeeded) throws RemoteException {
//					// TODO Auto-generated method stub
//					long applactionSize = pStats.codeSize;
//				}
//
//		    });
//	}

}
