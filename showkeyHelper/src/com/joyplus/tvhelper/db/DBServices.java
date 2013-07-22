package com.joyplus.tvhelper.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.joyplus.network.filedownload.manager.DownloadManager;
import com.joyplus.tvhelper.entity.ApkInfo;
import com.joyplus.tvhelper.entity.PushedApkDownLoadInfo;
import com.joyplus.tvhelper.entity.PushedMovieDownLoadInfo;
import com.joyplus.tvhelper.utils.PackageUtils;
import com.joyplus.utils.Log;


public class DBServices {
	
	private static final String TAG = "DBServices";
	
	private static DBServices dao = null;
	private Context context;
	private DownloadManager dmg;

	private DBServices(Context context) {
		this.context = context;
		dmg = DownloadManager.getInstance(context);
	}

	public static DBServices getInstance(Context context) {
		if (dao == null) {
			dao = new DBServices(context);
		}
		return dao;
	}

	public SQLiteDatabase getConnection() {
		SQLiteDatabase sqliteDatabase = null;
		try {
			sqliteDatabase = new DBHelper(context).getWritableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sqliteDatabase;
	}
	
	
	public synchronized long insertApkInfo(PushedApkDownLoadInfo info){
		SQLiteDatabase db = getConnection();
		ContentValues values = new ContentValues();
		values.put(DBConstant.KEY_APK_INFO_NAME, info.getName());
		values.put(DBConstant.KEY_APK_INFO_PUSH_ID, info.getPush_id());
		values.put(DBConstant.KEY_APK_INFO_FILE_PATH, info.getFile_path());
		values.put(DBConstant.KEY_APK_INFO_DOWNLOAD_STATE, info.getDownload_state());
		values.put(DBConstant.KEY_APK_INFO_DOWNLOADUUID, info.getTast().getUUId());
		values.put(DBConstant.KEY_APK_INFO_ISUSER, info.getIsUser());
		values.put(DBConstant.KEY_SYN_C1, info.getIcon_url());
		long _id = db.insert(DBConstant.TABLE_APK_INFO, null, values);
		db.close();
        return _id;
	}
	
	public synchronized void updateApkInfo(PushedApkDownLoadInfo info){
        SQLiteDatabase db = getConnection();
        ContentValues values = new ContentValues();
		values.put(DBConstant.KEY_APK_INFO_NAME, info.getName());
		values.put(DBConstant.KEY_APK_INFO_PUSH_ID, info.getPush_id());
		values.put(DBConstant.KEY_APK_INFO_FILE_PATH, info.getFile_path());
		values.put(DBConstant.KEY_APK_INFO_DOWNLOAD_STATE, info.getDownload_state());
		values.put(DBConstant.KEY_APK_INFO_DOWNLOADUUID, info.getTast().getUUId());
		values.put(DBConstant.KEY_APK_INFO_ISUSER, info.getIsUser());
		values.put(DBConstant.KEY_SYN_C1, info.getIcon_url());
//
        int rows = db.update(DBConstant.TABLE_APK_INFO, values,
        		DBConstant.KEY_ID + " = ? ", new String[] {
        		info.get_id()+ ""
                });
        db.close();
        Log.d(TAG, rows + "--->update");
    }
	
	public synchronized void deleteApkInfo(PushedApkDownLoadInfo info) {
        SQLiteDatabase db = getConnection();
        int rows = db.delete(DBConstant.TABLE_APK_INFO,
        		DBConstant.KEY_ID + " = ? ", new String[] {
        		String.valueOf(info.get_id())
                });
        Log.i(TAG, rows + "rows deleted");
        db.close();
    }
	
	public synchronized ArrayList<PushedApkDownLoadInfo> queryUserApkDownLoadInfo() {
        SQLiteDatabase db = getConnection();
        Cursor cr = db.query(DBConstant.TABLE_APK_INFO, null,
        		DBConstant.KEY_APK_INFO_ISUSER + " = ? ", new String[] {
        		PushedApkDownLoadInfo.IS_USER + ""}, null, null, null);
        ArrayList<PushedApkDownLoadInfo> taskes = new ArrayList<PushedApkDownLoadInfo>();
        PushedApkDownLoadInfo info;
        while (cr.moveToNext()) {
        	
        	int _id = cr.getInt(cr.getColumnIndex(DBConstant.KEY_ID));
        	String name = cr.getString(cr.getColumnIndex(DBConstant.KEY_APK_INFO_NAME));
        	int push_id = cr.getInt(cr.getColumnIndex(DBConstant.KEY_APK_INFO_PUSH_ID));
        	String file_path = cr.getString(cr.getColumnIndex(DBConstant.KEY_APK_INFO_FILE_PATH));
        	int download_statue = cr.getInt(cr.getColumnIndex(DBConstant.KEY_APK_INFO_DOWNLOAD_STATE));
        	String download_uuid = cr.getString(cr.getColumnIndex(DBConstant.KEY_APK_INFO_DOWNLOADUUID));
        	String icon_url = cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C1));
        	
        	info = new PushedApkDownLoadInfo();
        	info.setIcon_url(icon_url);
        	info.set_id(_id);
        	info.setName(name);
        	info.setPush_id(push_id);
        	if(download_statue == PushedApkDownLoadInfo.STATUE_DOWNLOADING){
        		download_statue = PushedApkDownLoadInfo.STATUE_DOWNLOAD_PAUSE;
        	}
        	if(download_statue == PushedApkDownLoadInfo.STATUE_DOWNLOAD_COMPLETE||download_statue == PushedApkDownLoadInfo.STATUE_INSTALL_FAILE){
        		ApkInfo apkinfo = PackageUtils.getUnInstalledApkInfo(context, file_path);
        		if(apkinfo!=null){
        			info.setPackageName(apkinfo.getPackageName());
            		info.setIcon(apkinfo.getDrawble());
        		}
        	}
        	info.setDownload_state(download_statue);
        	info.setFile_path(file_path);
        	info.setIsUser(PushedApkDownLoadInfo.IS_USER);
        	info.setTast(dmg.findTaksByUUID(download_uuid));
        	
        	taskes.add(info);
        	
        }
        cr.close();
        db.close();
        return taskes;
    }
	
	public synchronized ArrayList<PushedApkDownLoadInfo> queryNotUserApkDownLoadInfo() {
        SQLiteDatabase db = getConnection();
        Cursor cr = db.query(DBConstant.TABLE_APK_INFO, null,
        		DBConstant.KEY_APK_INFO_ISUSER + " = ? ", new String[] {
        		PushedApkDownLoadInfo.IS_NOT_USER + ""}, null, null, null);
        ArrayList<PushedApkDownLoadInfo> taskes = new ArrayList<PushedApkDownLoadInfo>();
        PushedApkDownLoadInfo info;
        while (cr.moveToNext()) {
        	
        	int _id = cr.getInt(cr.getColumnIndex(DBConstant.KEY_ID));
        	String name = cr.getString(cr.getColumnIndex(DBConstant.KEY_APK_INFO_NAME));
        	int push_id = cr.getInt(cr.getColumnIndex(DBConstant.KEY_APK_INFO_PUSH_ID));
        	String file_path = cr.getString(cr.getColumnIndex(DBConstant.KEY_APK_INFO_FILE_PATH));
        	int download_statue = cr.getInt(cr.getColumnIndex(DBConstant.KEY_APK_INFO_DOWNLOAD_STATE));
        	String download_uuid = cr.getString(cr.getColumnIndex(DBConstant.KEY_APK_INFO_DOWNLOADUUID));
        	
        	
        	info = new PushedApkDownLoadInfo();
        	info.set_id(_id);
        	info.setName(name);
        	info.setPush_id(push_id);
        	info.setDownload_state(download_statue);
        	info.setFile_path(file_path);
        	info.setIsUser(PushedApkDownLoadInfo.IS_NOT_USER);
        	info.setTast(dmg.findTaksByUUID(download_uuid));
        	if(download_statue == PushedApkDownLoadInfo.STATUE_DOWNLOAD_COMPLETE||download_statue == PushedApkDownLoadInfo.STATUE_INSTALL_FAILE){
        		ApkInfo apkinfo = PackageUtils.getUnInstalledApkInfo(context, file_path);
        		if(info!=null){
        			info.setPackageName(apkinfo.getPackageName());
            		info.setIcon(apkinfo.getDrawble());
        		}
        	}
        	taskes.add(info);
        }
        cr.close();
        db.close();
        return taskes;
    }
	
	public synchronized long insertMovieDownLoadInfo(PushedMovieDownLoadInfo info){
		SQLiteDatabase db = getConnection();
		ContentValues values = new ContentValues();
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_NAME, info.getName());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_PUSH_ID, info.getPush_id());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_FILE_PATH, info.getFile_path());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOAD_STATE, info.getDownload_state());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_PUSH_URL, info.getPush_url());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOADUUID, info.getTast().getUUId());
		long _id = db.insert(DBConstant.TABLE_MOVIE_INFO, null, values);
		db.close();
        return _id;
	}
	
	public synchronized void deleteMovieDownLoadInfo(PushedMovieDownLoadInfo info) {
		SQLiteDatabase db = getConnection();
		int rows = db.delete(DBConstant.TABLE_MOVIE_INFO,
				DBConstant.KEY_ID + " = ? ", new String[] {
				String.valueOf(info.get_id())
		});
		Log.i(TAG, rows + "rows deleted");
		db.close();
	}
	
	public synchronized void updateMovieDownLoadInfo(PushedMovieDownLoadInfo info){
        SQLiteDatabase db = getConnection();
        ContentValues values = new ContentValues();
        values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_NAME, info.getName());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_PUSH_ID, info.getPush_id());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_FILE_PATH, info.getFile_path());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOAD_STATE, info.getDownload_state());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_PUSH_URL, info.getPush_url());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOADUUID, info.getTast().getUUId());
//
        int rows = db.update(DBConstant.TABLE_MOVIE_INFO, values,
        		DBConstant.KEY_ID + " = ? ", new String[] {
        		info.get_id()+ ""
                });
        db.close();
        Log.d(TAG, rows + "--->update");
    }
	
	
	public synchronized ArrayList<PushedMovieDownLoadInfo> queryMovieDownLoadInfos() {
        SQLiteDatabase db = getConnection();
        Cursor cr = db.query(DBConstant.TABLE_MOVIE_INFO, null,
        		DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOAD_STATE + " <? ", new String[] {
        		PushedMovieDownLoadInfo.STATUE_DOWNLOAD_COMPLETE + ""}, null, null, null);
        ArrayList<PushedMovieDownLoadInfo> taskes = new ArrayList<PushedMovieDownLoadInfo>();
        PushedMovieDownLoadInfo info;
        while (cr.moveToNext()) {
        	
        	int _id = cr.getInt(cr.getColumnIndex(DBConstant.KEY_ID));
        	String name = cr.getString(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_NAME));
        	int push_id = cr.getInt(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_PUSH_ID));
        	String file_path = cr.getString(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_FILE_PATH));
        	int download_statue = cr.getInt(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOAD_STATE));
        	String download_uuid = cr.getString(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOADUUID));
        	String push_url = cr.getString(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_PUSH_URL));
        	
        	info = new PushedMovieDownLoadInfo();
        	info.set_id(_id);
        	info.setName(name);
        	info.setPush_id(push_id);
        	download_statue = PushedApkDownLoadInfo.STATUE_DOWNLOAD_PAUSE;
        	info.setDownload_state(download_statue);
        	info.setFile_path(file_path);
        	info.setPush_url(push_url);
        	info.setTast(dmg.findTaksByUUID(download_uuid));
        	taskes.add(info);
        }
        cr.close();
        db.close();
        return taskes;
    }
	
	public synchronized ArrayList<PushedMovieDownLoadInfo> queryMovieDownLoadedInfos() {
        SQLiteDatabase db = getConnection();
        Cursor cr = db.query(DBConstant.TABLE_MOVIE_INFO, null,
        		DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOAD_STATE + " =? ", new String[] {
        		PushedMovieDownLoadInfo.STATUE_DOWNLOAD_COMPLETE + ""}, null, null, null);
        ArrayList<PushedMovieDownLoadInfo> taskes = new ArrayList<PushedMovieDownLoadInfo>();
        PushedMovieDownLoadInfo info;
        while (cr.moveToNext()) {
        	
        	int _id = cr.getInt(cr.getColumnIndex(DBConstant.KEY_ID));
        	String name = cr.getString(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_NAME));
        	int push_id = cr.getInt(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_PUSH_ID));
        	String file_path = cr.getString(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_FILE_PATH));
        	int download_statue = cr.getInt(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOAD_STATE));
        	String download_uuid = cr.getString(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOADUUID));
        	String push_url = cr.getString(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_PUSH_URL));
        	
        	info = new PushedMovieDownLoadInfo();
        	info.set_id(_id);
        	info.setName(name);
        	info.setPush_id(push_id);
        	info.setDownload_state(download_statue);
        	info.setFile_path(file_path);
        	info.setPush_url(push_url);
        	info.setTast(dmg.findTaksByUUID(download_uuid));
        	taskes.add(info);
        }
        cr.close();
        db.close();
        return taskes;
    }
	
//	public synchronized void saveApkInfo(PushedApkDownLoadInfo info){
//		if(isHasPushedApk(info)){
//			updatePushedApkInfo(info);
//		}else{
//			SQLiteDatabase database = getConnection();
//			try {
//				String sql = "insert into apk_info(push_id, name, url, fileSize, compeleteSize, download_state, file_path) values (?,?,?,?,?,?,?)";
//				Object[] bindArgs = { info.getPush_id(), info.getName(),
//						info.getUrl(), info.getFileSize(), info.getCompeleteSize(),
//						info.getDownload_state(),info.getFile_path()};
//				database.execSQL(sql, bindArgs);
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				if (null != database) {
//					database.close();
//				}
//			}
//		}
//	}
	
//	public synchronized boolean isHasPushedApk(PushedApkDownLoadInfo info){
//		SQLiteDatabase database = getConnection();
//		Cursor cursor = null;
//		int count = -1;
//		try {
//			String sql = "select count(*)  from apk_info where push_id = ?";
//			cursor = database.rawQuery(sql, new String[]{info.getPush_id()+""});
//			if (cursor.moveToFirst()) {
//				count = cursor.getInt(0);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//			if (null != cursor) {
//				cursor.close();
//			}
//		}
//		return  count != 0;
//	}
	
//	public synchronized void updatePushedApkInfo(PushedApkDownLoadInfo info){
//		if(!isHasPushedApk(info)){
//			saveApkInfo(info);
//		}else{
//			SQLiteDatabase database = getConnection();
//			Cursor cursor = null;
//			try {
//				String sql = "update apk_info set name=?, url=?, fileSize=?, compeleteSize=?, download_state=? , file_path =? where push_id=?";
//				Object[] bindArgs = {info.getName(), info.getUrl(), info.getFileSize(), info.getCompeleteSize(), info.getDownload_state(), info.getFile_path(), info.getPush_id() };
//				database.execSQL(sql, bindArgs);
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				if (null != database) {
//					database.close();
//				}
//				if (null != cursor) {
//					cursor.close();
//				}
//			}
//		}
//	}
//	public synchronized PushedApkDownLoadInfo GetPushedApkInfo(String push_id){
//		SQLiteDatabase database = getConnection();
//		Cursor cursor = null;
//		PushedApkDownLoadInfo info = null;
//		try {
//			String sql = "select * from apk_info where push_id=?";
//			cursor = database.rawQuery(sql, new String[]{push_id+""});
//			while (cursor.moveToNext()) {
//				info = new PushedApkDownLoadInfo();
//				info.setName(cursor.getString(1));
//				info.setPush_id(cursor.getInt(2));
//				info.setUrl(cursor.getString(3));
//				info.setFileSize(cursor.getInt(4));
//				info.setCompeleteSize(cursor.getInt(5));
//				info.setDownload_state(cursor.getInt(6));
//				info.setFile_path(cursor.getString(7));
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//			if (null != cursor) {
//				cursor.close();
//			}
//		}
//		return info;
//	}
	
//	public synchronized List<PushedApkDownLoadInfo> GetPushedApklist(List<PushedApkDownLoadInfo> list){
//		SQLiteDatabase database = getConnection();
//		Cursor cursor = null;
//		PushedApkDownLoadInfo info = null;
//		list.clear();
//		try {
//			String sql = "select * from apk_info where download_state>-1";
//			cursor = database.rawQuery(sql,null);
//			while (cursor.moveToNext()) {
//				info = new PushedApkDownLoadInfo();
//				info.setName(cursor.getString(1));
//				info.setPush_id(cursor.getInt(2));
//				info.setUrl(cursor.getString(3));
//				info.setFileSize(cursor.getInt(4));
//				info.setCompeleteSize(cursor.getInt(5));
//				info.setDownload_state(cursor.getInt(6));
//				info.setFile_path(cursor.getString(7));
//				if(info.getDownload_state()==3){
//					try{
//						ApkInfo apkInfo = PackageUtils.getUnInstalledApkInfo(context, info.getFile_path());
//						if(info!=null){
//							info.setIcon(apkInfo.getDrawble());
//						    info.setPackageName(apkInfo.getPackageName());
//						}
//					}catch (Exception e) {
//						// TODO: handle exception
//						e.printStackTrace();
//					}
//					
//				}
//				list.add(info);
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//			if (null != cursor) {
//				cursor.close();
//			}
//		}
//		return list;
//	}
//	
//	public synchronized void deleteDownLoadInfo(PushedApkDownLoadInfo info){
//		SQLiteDatabase database = getConnection();
//		try {
//			database.delete("apk_info", "push_id=?",
//					new String[] { info.getPush_id()+""});
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//		}
//	}

//	/**
//	 * 鏌ョ湅鏁版嵁搴撲腑鏄惁鏈夋暟鎹�	 */
//	public synchronized boolean isHasInfors(String prod_id, String my_index) {
//		SQLiteDatabase database = getConnection();
//		int count = -1;
//		Cursor cursor = null;
//		try {
//			String sql = "select count(*)  from download_info where prod_id=? and my_index=?";
//			cursor = database.rawQuery(sql, new String[] { prod_id, my_index });
//			if (cursor.moveToFirst()) {
//				count = cursor.getInt(0);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//			if (null != cursor) {
//				cursor.close();
//			}
//		}
//		return count == 0;
//	}
//
//	/**
//	 * 鏌ョ湅鏁版嵁搴撲腑鏄惁鏈夋鍦ㄤ笅杞界殑鏁版嵁
//	 */
//	public synchronized boolean isHasInforsDownloading(String download_state) {
//		SQLiteDatabase database = getConnection();
//		int count = -1;
//		Cursor cursor = null;
//		try {
//			String sql = "select count(*)  from download_info where download_state=?";
//			cursor = database.rawQuery(sql, new String[] { download_state });
//			if (cursor.moveToFirst()) {
//				count = cursor.getInt(0);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//			if (null != cursor) {
//				cursor.close();
//			}
//		}
//		return count == 0;
//	}
	
	/*
	 * 淇濆瓨缂撳瓨璁板綍
	 */
//	public synchronized void saveInfos(List<DownloadInfo> infos) {
//		SQLiteDatabase database = getConnection();
//		try {
//			for (DownloadInfo info : infos) {
//				String sql = "insert into download_info(compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state) values (?,?,?,?,?,?,?,?)";
//				Object[] bindArgs = { info.getCompeleteSize(),
//						info.getFileSize(), info.getProd_id(), info.getMy_index(),
//						info.getUrl(), info.getUrlposter(), info.getMy_name(),
//						info.getDownload_state()};
//				database.execSQL(sql, bindArgs);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//		}
//	}
	
	/*
	 * 鎻掑叆涓�潯璁板綍
	 */
//	public synchronized void InsertOneInfo(DownloadInfo info) {
//		SQLiteDatabase database = getConnection();
//		try {
//			String sql = "insert into download_info(compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state) values (?,?,?,?,?,?,?,?)";
//			Object[] bindArgs = { info.getCompeleteSize(), info.getFileSize(),
//					info.getProd_id(), info.getMy_index(), info.getUrl(),
//					info.getUrlposter(), info.getMy_name(), info.getDownload_state() };
//			database.execSQL(sql, bindArgs);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//		}
//	}
	
	/*
	 * 鑾峰彇鏌愪竴涓褰�杩斿洖涓�釜DownloadInfo list绫诲瀷
//	 */
//	public synchronized List<DownloadInfo> getInfos(String prod_id,
//			String my_index) {
//		List<DownloadInfo> list = new ArrayList<DownloadInfo>();
//		SQLiteDatabase database = getConnection();
//		Cursor cursor = null;
//		try {
//			String sql = "select compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state from download_info where prod_id=? and my_index=?";
//			cursor = database.rawQuery(sql, new String[] { prod_id, my_index });
//			while (cursor.moveToNext()) {
//				DownloadInfo info = new DownloadInfo(cursor.getInt(0),
//						cursor.getInt(1), cursor.getString(2),
//						cursor.getString(3), cursor.getString(4),
//						cursor.getString(5), cursor.getString(6),
//						cursor.getString(7));
//				list.add(info);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//			if (null != cursor) {
//				cursor.close();
//			}
//		}
//		return list;
//	}
//
//	public synchronized DownloadInfo getOneInfo(String prod_id, String my_index) {
//		DownloadInfo info = null;
//		SQLiteDatabase database = getConnection();
//		Cursor cursor = null;
//		try {
//			String sql = "select compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state from download_info where prod_id=? and my_index=?";
//			cursor = database.rawQuery(sql, new String[] { prod_id, my_index });
//			while (cursor.moveToNext()) {
//				info = new DownloadInfo(cursor.getInt(0), cursor.getInt(1),
//						cursor.getString(2), cursor.getString(3),
//						cursor.getString(4), cursor.getString(5),
//						cursor.getString(6), cursor.getString(7));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//			if (null != cursor) {
//				cursor.close();
//			}
//		}
//		return info;
//	}
//	
//	/*
//	 * 鑾峰彇鏌愪竴涓姸鎬佺殑鏌愪竴鏉¤褰�	 */
//	public synchronized DownloadInfo getOneStateInfo(String download_state) {
//		DownloadInfo info = null;
//		SQLiteDatabase database = getConnection();
//		Cursor cursor = null;
//		try {
//			String sql = "select compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state from download_info where download_state=?";
//			cursor = database.rawQuery(sql, new String[] { download_state });
//			while (cursor.moveToNext()) {
//				info = new DownloadInfo(cursor.getInt(0), cursor.getInt(1),
//						cursor.getString(2), cursor.getString(3),
//						cursor.getString(4), cursor.getString(5),
//						cursor.getString(6), cursor.getString(7));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//			if (null != cursor) {
//				cursor.close();
//			}
//		}
//		return info;
//	}
//
//	/*
//	 * 杩斿洖鏁版嵁搴撲腑鎵�湁鐨勬暟鎹�	 */
//	public synchronized List<DownloadInfo> getDownloadInfos() {
//		List<DownloadInfo> list = new ArrayList<DownloadInfo>();
//		SQLiteDatabase database = getConnection();
//		Cursor cursor = null;
//		try {
//			String sql = "select compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state from download_info";
//			cursor = database.rawQuery(sql, null);
//			while (cursor.moveToNext()) {
//				DownloadInfo info = new DownloadInfo(cursor.getInt(0),
//						cursor.getInt(1), cursor.getString(2),
//						cursor.getString(3), cursor.getString(4),
//						cursor.getString(5), cursor.getString(6),
//						cursor.getString(7));
//				list.add(info);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//			if (null != cursor) {
//				cursor.close();
//			}
//		}
//		return list;
//	}
//
//	// 鏍规嵁prod_id杩涜鍒嗙粍
//	public synchronized List<DownloadInfo> getDownloadInfosGroup() {
//		List<DownloadInfo> list = new ArrayList<DownloadInfo>();
//		SQLiteDatabase database = getConnection();
//		Cursor cursor = null;
//		try {
//			String sql = "select compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state from download_info group by prod_id";
//			cursor = database.rawQuery(sql, null);
//			while (cursor.moveToNext()) {
//				DownloadInfo info = new DownloadInfo(cursor.getInt(0),
//						cursor.getInt(1), cursor.getString(2),
//						cursor.getString(3), cursor.getString(4),
//						cursor.getString(5), cursor.getString(6),
//						cursor.getString(7));
//				list.add(info);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//			if (null != cursor) {
//				cursor.close();
//			}
//		}
//		return list;
//	}
//
//	// 鑾峰彇鏌愪竴涓猵rod_id鐨勬墍鏈夋暟鎹�閫氬父鐢ㄤ簬鐢佃鍓у拰鑺傜洰
//	public synchronized List<DownloadInfo> getInfosOfProd_id(String prod_id) {
//		List<DownloadInfo> list = new ArrayList<DownloadInfo>();
//		SQLiteDatabase database = getConnection();
//		Cursor cursor = null;
//		try {
//			String sql = "select compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state from download_info where prod_id=?";
//			cursor = database.rawQuery(sql, new String[] { prod_id });
//			while (cursor.moveToNext()) {
//				DownloadInfo info = new DownloadInfo(cursor.getInt(0),
//						cursor.getInt(1), cursor.getString(2),
//						cursor.getString(3), cursor.getString(4),
//						cursor.getString(5), cursor.getString(6),
//						cursor.getString(7));
//				list.add(info);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//			if (null != cursor) {
//				cursor.close();
//			}
//		}
//		return list;
//	}
//
//	/*
//	 * 鏇存柊鏌愪竴涓笅杞借褰曚笅杞戒簡澶氬皯
//	 */
//	public synchronized void updataInfos(int compeleteSize, String prod_id,
//			String my_index) {
//		SQLiteDatabase database = getConnection();
//		try {
//			String sql = "update download_info set compeleteSize=? where prod_id=? and my_index=?";
//			Object[] bindArgs = { compeleteSize, prod_id, my_index };
//			database.execSQL(sql, bindArgs);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//		}
//	}
//	
//	/*
//	 * 鏇存柊鏌愪竴鏉¤褰�	 */
//	public synchronized void updataInfos(DownloadInfo info) {
//		SQLiteDatabase database = getConnection();
//		try {
//			String sql = "update download_info set fileSize=? where prod_id=? and my_index=?";
//			Object[] bindArgs = { info.getFileSize(), info.getProd_id(), info.getMy_index() };
//			database.execSQL(sql, bindArgs);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//		}
//	}
//	/*
//	 * 鏇存柊鏌愪竴鏉′笅杞借褰曠殑鐘舵�
//	 */
//	public synchronized void updataInfoState(String download_state,
//			String prod_id, String my_index) {
//		SQLiteDatabase database = getConnection();
//		try {
//			String sql = "update download_info set download_state=? where prod_id=? and my_index=?";
//			Object[] bindArgs = { download_state, prod_id, my_index };
//			database.execSQL(sql, bindArgs);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//		}
//	}
//	/*
//	 * 涓嬭浇瀹屾垚鍚庢洿鏂板瓨鏀捐矾寰�	 */
//	public synchronized void updataInfofilePath(String localfile,
//			String prod_id, String my_index) {
//		SQLiteDatabase database = getConnection();
//		try {
//			String sql = "update download_info set localfile=? where prod_id=? and my_index=?";
//			Object[] bindArgs = { localfile, prod_id, my_index };
//			database.execSQL(sql, bindArgs);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//		}
//	}
//
//	/*
//	 * 鍒犻櫎鏌愪竴涓褰�	 */
//	public synchronized void delete(String prod_id, String my_index) {
//		SQLiteDatabase database = getConnection();
//		try {
//			database.delete("download_info", "prod_id=? and my_index=?",
//					new String[] { prod_id, my_index });
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//		}
//	}
//	
//	/*
//	 * 鍒犻櫎鏌愪釜闈炵數褰辩殑鎵�湁璁板綍
//	 */
//	public synchronized void delete(String prod_id) {
//		SQLiteDatabase database = getConnection();
//		try {
//			database.delete("download_info", "prod_id=?",
//					new String[] { prod_id});
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != database) {
//				database.close();
//			}
//		}
//	}
}